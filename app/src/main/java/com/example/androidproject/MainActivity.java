package com.example.androidproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
double btcPrice=0;
JSONObject res;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //to access a string-array in ressources
        //String[] test=getResources().getStringArray(R.array.symbol_list);
        MakeRequest initReq=new MakeRequest(MainActivity.this);
        initReq.execute();
        //refresh the list and displays a toast
        Button refreshButton=(Button) findViewById(R.id.refreshButton);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("JFL","onclick button pressed");
                Toast toast= Toast.makeText(getApplicationContext(),"Refreshing list of crypto",Toast.LENGTH_SHORT);
                toast.show();
                MakeRequest req=new MakeRequest(MainActivity.this);
                req.execute();
            }
        });
        //go to settings page
        Button settingsButton=(Button) findViewById(R.id.settingsButton);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });

    }
}