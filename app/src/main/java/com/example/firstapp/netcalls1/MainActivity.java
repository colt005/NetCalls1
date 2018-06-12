package com.example.firstapp.netcalls1;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.firstapp.netcalls1.models.MovieModel;

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
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    private TextView tvdata;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ListView lvMovies = (ListView) findViewById(R.id.lvmovies);

        new JSONtask().execute("https://jsonparsingdemo-cec5b.firebaseapp.com/jsonData/moviesData.txt");
    }


    public class JSONtask extends AsyncTask< String, String, List<MovieModel> >
    {




        @Override
        protected List<MovieModel> doInBackground(String... urls) {
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

                List<MovieModel> moviemodellist = new ArrayList<>();

                for(int i=0;i<parentaray.length();i++){
                    JSONObject finalobject = parentaray.getJSONObject(i);
                    MovieModel movieModel = new MovieModel();
                    movieModel.setMovie(finalobject.getString("movie"));
                    movieModel.setYear(finalobject.getInt("year"));
                    movieModel.setRating((float)finalobject.getDouble("rating"));
                    movieModel.setDirector(finalobject.getString("director"));

                    movieModel.setDuration(finalobject.getString("duration"));
                    movieModel.setTagline(finalobject.getString("tagline"));
                    movieModel.setImage(finalobject.getString("image"));
                    movieModel.setStory(finalobject.getString("story"));

                    List<MovieModel.cast> castlist = new ArrayList<>();
                    for (int j=0;j<finalobject.getJSONArray("cast").length();j++){
                        MovieModel.cast Cast = new MovieModel.cast();
                        Cast.setName(finalobject.getJSONArray("cast").getJSONObject(j).getString("name"));
                        castlist.add(Cast);
                    }
                        movieModel.setCastlist(castlist);
                    //adding final object to the list
                        moviemodellist.add(movieModel);

                    //finalBuffureddata.append(moviename + " - " +year+ "\n");

                }

                return moviemodellist;

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
        protected void onPostExecute(List<MovieModel> result) {
            super.onPostExecute(result);
           //TODO
        }
    }
}