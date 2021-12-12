package com.example.androidproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {
    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        this.saveButton=(Button)findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            SettingsActivity.this.doSave(v);
            }
        });

        String[] cryptoList=getResources().getStringArray(R.array.symbol_list);
        TableLayout settingsTable = (TableLayout)findViewById(R.id.settingsTable);
        //settingsTable.removeAllViews();//useless because it is not saving the rows
        //Log.i("COUNT", String.valueOf(settingsTable.getChildCount()));
        settingsTable.setStretchAllColumns(true);
        settingsTable.bringToFront();
        SharedPreferences sharedPreferences= this.getSharedPreferences("appSettings", Context.MODE_PRIVATE);
        for(int i = 0; i < cryptoList.length; i++){
            TableRow tr =  new TableRow(this);
            TextView c1 = new TextView(this);
            c1.setText(cryptoList[i]);
            EditText c2 = new EditText(this);
            c2.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            c2.setText(String.valueOf(sharedPreferences.getFloat(cryptoList[i]+"NB",0)));

            CheckBox c3 = new CheckBox(this);
            c3.setChecked(sharedPreferences.getBoolean(cryptoList[i]+"CB",true));
            //c3.setText("evolution");
            tr.addView(c1);
            tr.addView(c2);
            tr.addView(c3);
            settingsTable.addView(tr);
        }
    }

    public void doSave(View v) {
        SharedPreferences sharedPreferences= this.getSharedPreferences("appSettings", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();

        TableLayout table = (TableLayout)findViewById(R.id.settingsTable);
        for(int i = 0, j = table.getChildCount(); i < j; i++) {
            View view = table.getChildAt(i);
            TableRow row = (TableRow) view;
            TextView txt=(TextView) row.getChildAt(0);

            EditText nb=(EditText)row.getChildAt(1);
            editor.putFloat((String) txt.getText()+"NB", Float.parseFloat(nb.getText().toString()));

            CheckBox cb=(CheckBox) row.getChildAt(2);
            editor.putBoolean((String) txt.getText()+"CB",cb.isChecked());
            //Log.i("AAA", txt.getText() + String.valueOf(nb.getText()) + String.valueOf(cb.isChecked()));
        }
        // Save.
        editor.apply();

        Toast.makeText(this,"Settings saved!",Toast.LENGTH_LONG).show();
    }
}