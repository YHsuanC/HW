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

package com.example.android.todolist;

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.android.todolist.data.TaskContract;


public class AddTaskActivity extends AppCompatActivity {

    private int number = 1;

    public String people;
    Spinner spinner;





    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        spinner = (Spinner)findViewById(R.id.people_spinner);
        ArrayAdapter<CharSequence> peopleList = ArrayAdapter.createFromResource(AddTaskActivity.this,
                R.array.people,
                android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(peopleList);

        readData();
    }


    /**
     * onClickAddTask is called when the "ADD" button is clicked.
     * It retrieves user input and inserts that new task data into the underlying database.
     */
    public void onClickAddTask(View view) {

        String input = ((EditText) findViewById(R.id.editTextTaskDescription)).getText().toString();
        String cellphone = ((EditText) findViewById(R.id.editTextTaskCellphone)).getText().toString();
        if (input.length() == 0||cellphone.length() == 0) {
            return;
        }

        people = spinner.getSelectedItem().toString();

        ContentValues contentValues = new ContentValues();

        contentValues.put(TaskContract.TaskEntry.COLUMN_DESCRIPTION, input);
        contentValues.put(TaskContract.TaskEntry.COLUMN_PRIORITY, number);
        contentValues.put(TaskContract.TaskEntry.COLUMN_CELLPHONE, cellphone);
        contentValues.put(TaskContract.TaskEntry.COLUMN_PEOPLE, people);

        Uri uri = getContentResolver().insert(TaskContract.TaskEntry.CONTENT_URI, contentValues);

        if(uri != null) {
            Toast.makeText(getBaseContext(), "You've joined the queue .", Toast.LENGTH_LONG).show();
        }
        number+=1;
        if (number > 99){
            number = 1;
        }
        saveData();

        finish();

    }


    /**
     * onPrioritySelected is called whenever a priority button is clicked.
     * It changes the value of mPriority based on the selected button.
     */
    private SharedPreferences settings;
    private static final String data = "Data";
    private static final String order = "Order";
    public void readData(){
        settings = getSharedPreferences(data,0);
        number = Integer.valueOf(settings.getString(order, "1"));
    }
    public void saveData(){
        settings = getSharedPreferences(data,0);
        settings.edit()
                .putString(order, Integer.toString(number))
                .commit();
    }
}
