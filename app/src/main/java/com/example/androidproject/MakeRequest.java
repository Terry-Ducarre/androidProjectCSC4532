package com.example.androidproject;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.text.InputType;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

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
    JSONObject result=new JSONObject();
    Activity myActivity;
    Double wallet;
    Double change;

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
            String[] symbol_list=myActivity.getResources().getStringArray(R.array.symbol_list);
            String URLparam="";
            for(int i = 0, j = symbol_list.length-1; i < j; i++){
                URLparam+=symbol_list[i]+",";
            }
            URLparam+=symbol_list[symbol_list.length-1];
            //url = new URL(myActivity.getString(R.string.api_url)+"?start=1&limit=7&convert=USD");//https://coinmarketcap.com/api/documentation/v1/
            url=new URL("https://pro-api.coinmarketcap.com/v1/cryptocurrency/quotes/latest?convert=USD&symbol="+URLparam);
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
                //Log.i("JFL", resString);
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
        return null;
    }
    @Override
    protected void onPostExecute(JSONObject res) {
        Log.i("JFL","starting onpostexec");
        myActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String[] cryptoList=myActivity.getResources().getStringArray(R.array.symbol_list);
                TableLayout cryptoTable=(TableLayout) myActivity.findViewById(R.id.cryptoTable);
                cryptoTable.removeAllViews();//To avoid duplicating rows
                //Setting up the header row
                TableRow htr =  new TableRow(myActivity);
                TextView hc1 = new TextView(myActivity);
                TextView hc2 = new TextView(myActivity);
                TextView hc3 = new TextView(myActivity);
                hc1.setText("Name");
                hc2.setText("Price");
                hc3.setText("24h Change");
                hc1.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                hc2.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                hc3.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                htr.addView(hc1);
                htr.addView(hc2);
                htr.addView(hc3);
                cryptoTable.addView(htr);

                wallet=Double.valueOf(0);
                change=Double.valueOf(0);
                SharedPreferences sharedPreferences= myActivity.getSharedPreferences("appSettings", Context.MODE_PRIVATE);
                for(int i = 0; i < cryptoList.length; i++){
                    if (sharedPreferences.getBoolean(cryptoList[i]+"CB",true)){
                        TableRow tr =  new TableRow(myActivity);
                        TextView c1 = new TextView(myActivity);
                        c1.setText(cryptoList[i]);

                        TextView c2 = new TextView(myActivity);
                        TextView c3 = new TextView(myActivity);

                        try {
                            Double variation = result.getJSONObject("data").getJSONObject(cryptoList[i]).getJSONObject("quote").getJSONObject("USD").getDouble("percent_change_24h");
                            Double price=result.getJSONObject("data").getJSONObject(cryptoList[i]).getJSONObject("quote").getJSONObject("USD").getDouble("price");

                            wallet+=price*sharedPreferences.getFloat(cryptoList[i]+"NB",0);
                            change+=sharedPreferences.getFloat(cryptoList[i]+"NB",0)*price*(variation/100)/(1+(variation/100));
                            c2.setText(String.format("%.6g",price)+"$");

                            if (variation>0){
                                c3.setText("+"+String.format("%.3g",variation)+"%");
                            }
                            else{
                                c3.setText(String.format("%.3g",variation)+"%");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        //c3.setText("evolution");
                        tr.addView(c1);
                        tr.addView(c2);
                        tr.addView(c3);
                        cryptoTable.addView(tr);
                    }
                    else{
                        Log.i("REJECT","The cryptocurrency "+cryptoList[i]+" was not checked in the settings");
                    }
                }
                ImageView ima=(ImageView) myActivity.findViewById(R.id.stonksOrNot);
                TextView walletandchangeText=(TextView) myActivity.findViewById(R.id.walletAndChangeText);
                String textToDisplay="";
                textToDisplay+=String.format("%.6g",wallet)+"$";
                if (change>0){
                    textToDisplay+="  +"+String.format("%.6g",change)+"$";
                    ima.setImageResource(R.drawable.stonks);
                }
                else{
                    textToDisplay+="  "+String.format("%.6g",change)+"$";
                    ima.setImageResource(R.drawable.notstonks);
                }
                walletandchangeText.setText(textToDisplay);



                double BTCPrice=1;
                try {
                    BTCPrice=result.getJSONObject("data").getJSONObject("BTC").getJSONObject("quote").getJSONObject("USD").getDouble("price");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Toast toast= Toast.makeText(myActivity.getApplicationContext(),"List updated",Toast.LENGTH_SHORT);
                toast.show();

                }
        });
    }
}