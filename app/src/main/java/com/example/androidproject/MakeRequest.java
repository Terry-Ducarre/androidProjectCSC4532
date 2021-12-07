package com.example.androidproject;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.net.*;

class MakeRequest extends AsyncTask<String, Void, JSONObject> {
    JSONObject result;
    Activity myActivity;

    public MakeRequest(Activity activity){
        this.myActivity=activity;
    }
    protected JSONObject doInBackground(String... strings) {
        System.out.println("doInBackground running");
        Log.i("JFL","background task running");
        URL url = null;
        try {
            Log.i("URL",myActivity.getString(R.string.api_url));
            Log.i("KEY",myActivity.getString(R.string.api_key));
            url = new URL(myActivity.getString(R.string.api_url));//https://coinmarketcap.com/api/documentation/v1/
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestProperty("X-CMC_PRO_API_KEY", myActivity.getString(R.string.api_key));
            urlConnection.setRequestProperty("Accepts", "application/json");
            //String urlParameters = "start=1&limit=5000&convert=USD";

            try {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                StringBuilder sb = new StringBuilder();

                String line = null;
                try {
                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                } catch (IOException e) {
                    Log.e(TAG, "IOException", e);
                } finally {
                    try {
                        in.close();
                    } catch (IOException e) {
                        Log.e(TAG, "IOException", e);
                    }
                }
                String resString=sb.toString();
                Log.i("JFL", resString);
                result=new JSONObject(resString);
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                urlConnection.disconnect();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.i("RES",result.toString());
        try {
            Log.i("RES", String.valueOf(result.getJSONArray("data").length()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
    @Override
    protected void onPostExecute(JSONObject res) {
        Log.i("TEA","testo");
        Log.i("JFL","fin de postexec");

        myActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Switch switch1 = (Switch) myActivity.findViewById(R.id.switch1);
                switch1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean checked = ((Switch) v).isChecked();
                        if (checked){
                            TextView displayText=(TextView)myActivity.findViewById(R.id.displayText);
                            JSONObject res = null;
                            try {
                                res= (JSONObject) result.getJSONArray("data").get(0);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            displayText.setText(res.toString());
                        }
                        else{
                            TextView displayText=(TextView)myActivity.findViewById(R.id.displayText);
                            displayText.setText("Nothing to display");
                        }
                    }
                });
            }
        });
    }
}