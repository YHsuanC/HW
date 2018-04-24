package com.example.gj94g.myhw2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Ckbxad ckbxad;
    private LinearLayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.rv01);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        ckbxad = new Ckbxad();
        recyclerView.setAdapter(ckbxad);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menulayout, menu);
        return super.onCreateOptionsMenu(menu);
    }


    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId()== R.id.app_bar_search){
            Toast toast = Toast.makeText(this,"",Toast.LENGTH_SHORT);
            String selected_text = "";
            for (int i = 0; i < ckbxad.checked_list.length; i++){
                if (ckbxad.checked_list[i])
                    selected_text += i + 1 + ", ";
            }
            toast.setText("Your selections:" + selected_text);
            toast.show();
        }
        return super.onOptionsItemSelected(item);
    }
}


