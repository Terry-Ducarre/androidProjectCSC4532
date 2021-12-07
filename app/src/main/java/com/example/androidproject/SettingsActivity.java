package com.example.androidproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {
    private CheckBox btcBox;
    private CheckBox ethBox;
    private CheckBox chzBox;

    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        this.btcBox=(CheckBox)findViewById(R.id.btcBox);
        this.ethBox=(CheckBox)findViewById(R.id.ethBox);
        this.chzBox=(CheckBox)findViewById(R.id.chzBox);

        //btcBox.setChecked(true);

        this.saveButton=(Button)findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            SettingsActivity.this.doSave(v);
            }
        });

        this.loadSettings();
    }

    public void doSave(View v) {
        SharedPreferences sharedPreferences= this.getSharedPreferences("appSettings", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean("btc",this.btcBox.isChecked());
        editor.putBoolean("eth",this.ethBox.isChecked());
        editor.putBoolean("chz",this.chzBox.isChecked());

        // Save.
        editor.apply();

        Toast.makeText(this,"Settings saved!",Toast.LENGTH_LONG).show();
    }


    private void loadSettings() {
        SharedPreferences sharedPreferences= this.getSharedPreferences("appSettings", Context.MODE_PRIVATE);

        if(sharedPreferences!= null) {

            boolean btcChecked= sharedPreferences.getBoolean("btc",true);
            boolean ethChecked= sharedPreferences.getBoolean("eth",true);
            boolean chzChecked= sharedPreferences.getBoolean("chz",true);

            this.btcBox.setChecked(btcChecked);
            this.ethBox.setChecked(ethChecked);
            this.chzBox.setChecked(chzChecked);

        } else {
            this.btcBox.setChecked(true);
            this.ethBox.setChecked(true);
            this.chzBox.setChecked(true);
            Toast.makeText(this,"Use the default app settings",Toast.LENGTH_LONG).show();
        }
    }
}