package com.example.firstapp.netcalls1;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.V;

public class MainActivity extends AppCompatActivity {


    private TextView tvdata;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btnhit = (Button) (findViewById(R.id.btnhit));
        tvdata = (TextView) (findViewById(R.id.tvJSONItem));

        btnhit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new JSONtask().execute("https://jsonparsingdemo-cec5b.firebaseapp.com/jsonData/moviesDemoList.txt");


            }
        });


    }


    public class JSONtask extends AsyncTask<String, String, String>
    {




        @Override
        protected String doInBackground(String... urls) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            try {

                URL url;
                url = new URL(urls[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));
                String line = " ";
                StringBuffer buffer = new StringBuffer();

                while ((line = reader.readLine()) != null) {

                    buffer.append(line);

                }
                String finalJSON = buffer.toString();
                JSONObject parentobject = new JSONObject(finalJSON);
                JSONArray parentaray = parentobject.getJSONArray("movies");

                StringBuffer finalBuffureddata = new StringBuffer();
                for(int i=0;i<parentaray.length();i++){

                    JSONObject finalobject = parentaray.getJSONObject(i);
                    String moviename = finalobject.getString("movie");
                    int year = finalobject.getInt("year");
                    finalBuffureddata.append(moviename + " - " +year+ "\n");

                }

                return finalBuffureddata.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            tvdata.setText(result);
        }
    }
}