/*
* Copyright (C) 2016 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.example.android.todolist.data;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.example.android.todolist.data.TaskContentProvider;
import com.example.android.todolist.data.TaskContract;
import com.example.android.todolist.data.TaskDbHelper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

@RunWith(AndroidJUnit4.class)
public class TestTaskContentProvider {

    private final Context mContext = InstrumentationRegistry.getTargetContext();

    /**
     * Because we annotate this method with the @Before annotation, this method will be called
     * before every single method with an @Test annotation. We want to start each test clean, so we
     * delete all entries in the tasks directory to do so.
     */
    @Before
    public void setUp() {
        TaskDbHelper dbHelper = new TaskDbHelper(mContext);
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        database.delete(TaskContract.TaskEntry.TABLE_NAME, null, null);
    }

    /**
     * This test checks to make sure that the content provider is registered correctly in the
     * AndroidManifest file. If it fails, you should check the AndroidManifest to see if you've
     * added a <provider/> tag and that you've properly specified the android:authorities attribute.
     */
    @Test
    public void testProviderRegistry() {

        String packageName = mContext.getPackageName();
        String taskProviderClassName = TaskContentProvider.class.getName();
        ComponentName componentName = new ComponentName(packageName, taskProviderClassName);

        try {

            PackageManager pm = mContext.getPackageManager();

            ProviderInfo providerInfo = pm.getProviderInfo(componentName, 0);
            String actualAuthority = providerInfo.authority;
            String expectedAuthority = packageName;

            String incorrectAuthority =
                    "Error: TaskContentProvider registered with authority: " + actualAuthority +
                            " instead of expected authority: " + expectedAuthority;
            assertEquals(incorrectAuthority,
                    actualAuthority,
                    expectedAuthority);

        } catch (PackageManager.NameNotFoundException e) {
            String providerNotRegisteredAtAll =
                    "Error: TaskContentProvider not registered at " + mContext.getPackageName();

            fail(providerNotRegisteredAtAll);
        }
    }

    private static final Uri TEST_TASKS = TaskContract.TaskEntry.CONTENT_URI;
    private static final Uri TEST_TASK_WITH_ID = TEST_TASKS.buildUpon().appendPath("1").build();


    /**
     * This function tests that the UriMatcher returns the correct integer value for
     * each of the Uri types that the ContentProvider can handle. Uncomment this when you are
     * ready to test your UriMatcher.
     */
    @Test
    public void testUriMatcher() {

        UriMatcher testMatcher = TaskContentProvider.buildUriMatcher();

        String tasksUriDoesNotMatch = "Error: The TASKS URI was matched incorrectly.";
        int actualTasksMatchCode = testMatcher.match(TEST_TASKS);
        int expectedTasksMatchCode = TaskContentProvider.TASKS;
        assertEquals(tasksUriDoesNotMatch,
                actualTasksMatchCode,
                expectedTasksMatchCode);

        String taskWithIdDoesNotMatch =
                "Error: The TASK_WITH_ID URI was matched incorrectly.";
        int actualTaskWithIdCode = testMatcher.match(TEST_TASK_WITH_ID);
        int expectedTaskWithIdCode = TaskContentProvider.TASK_WITH_ID;
        assertEquals(taskWithIdDoesNotMatch,
                actualTaskWithIdCode,
                expectedTaskWithIdCode);
    }

    /**
     * Tests inserting a single row of data via a ContentResolver
     */
    @Test
    public void testInsert() {

        ContentValues testTaskValues = new ContentValues();
        testTaskValues.put(TaskContract.TaskEntry.COLUMN_DESCRIPTION, "Test description");
        testTaskValues.put(TaskContract.TaskEntry.COLUMN_PRIORITY, 1);

        TestUtilities.TestContentObserver taskObserver = TestUtilities.getTestContentObserver();

        ContentResolver contentResolver = mContext.getContentResolver();

        contentResolver.registerContentObserver(

                TaskContract.TaskEntry.CONTENT_URI,

                true,
                 taskObserver);


        Uri uri = contentResolver.insert(TaskContract.TaskEntry.CONTENT_URI, testTaskValues);


        Uri expectedUri = ContentUris.withAppendedId(TaskContract.TaskEntry.CONTENT_URI, 1);

        String insertProviderFailed = "Unable to insert item through Provider";
        assertEquals(insertProviderFailed, uri, expectedUri);

        taskObserver.waitForNotificationOrFail();

        contentResolver.unregisterContentObserver(taskObserver);
    }

    /**
     * Inserts data, then tests if a query for the tasks directory returns that data as a Cursor
     */
    @Test
    public void testQuery() {

        TaskDbHelper dbHelper = new TaskDbHelper(mContext);
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        ContentValues testTaskValues = new ContentValues();
        testTaskValues.put(TaskContract.TaskEntry.COLUMN_DESCRIPTION, "Test description");
        testTaskValues.put(TaskContract.TaskEntry.COLUMN_PRIORITY, 1);

        long taskRowId = database.insert(

                TaskContract.TaskEntry.TABLE_NAME,
                null,

                testTaskValues);

        String insertFailed = "Unable to insert directly into the database";
        assertTrue(insertFailed, taskRowId != -1);

        database.close();

        Cursor taskCursor = mContext.getContentResolver().query(
                TaskContract.TaskEntry.CONTENT_URI,

                null,

                null,

                null,

                null);


        String queryFailed = "Query failed to return a valid Cursor";
        assertTrue(queryFailed, taskCursor != null);

        /* We are done with the cursor, close it now. */
        taskCursor.close();
    }

    /**
     * Tests deleting a single row of data via a ContentResolver
     */
    @Test
    public void testDelete() {

        TaskDbHelper helper = new TaskDbHelper(InstrumentationRegistry.getTargetContext());
        SQLiteDatabase database = helper.getWritableDatabase();

        ContentValues testTaskValues = new ContentValues();
        testTaskValues.put(TaskContract.TaskEntry.COLUMN_DESCRIPTION, "Test description");
        testTaskValues.put(TaskContract.TaskEntry.COLUMN_PRIORITY, 1);

        long taskRowId = database.insert(

                TaskContract.TaskEntry.TABLE_NAME,
                null,

                testTaskValues);

        database.close();

        String insertFailed = "Unable to insert into the database";
        assertTrue(insertFailed, taskRowId != -1);

        TestUtilities.TestContentObserver taskObserver = TestUtilities.getTestContentObserver();

        ContentResolver contentResolver = mContext.getContentResolver();

        contentResolver.registerContentObserver(

                TaskContract.TaskEntry.CONTENT_URI,

                true,

                taskObserver);


        Uri uriToDelete = TaskContract.TaskEntry.CONTENT_URI.buildUpon().appendPath("1").build();
        int tasksDeleted = contentResolver.delete(uriToDelete, null, null);

        String deleteFailed = "Unable to delete item in the database";
        assertTrue(deleteFailed, tasksDeleted != 0);

        taskObserver.waitForNotificationOrFail();

        contentResolver.unregisterContentObserver(taskObserver);
    }

}
