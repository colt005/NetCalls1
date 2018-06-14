package com.example.firstapp.netcalls1;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
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
    private ListView lvMovies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lvMovies = (ListView) findViewById(R.id.lvmovies);
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
            MovieAdapter adapter = new MovieAdapter(getApplicationContext(), R.layout.row, result);
            lvMovies.setAdapter(adapter);
           //TODO
        }
    }

    public class MovieAdapter extends ArrayAdapter{

        private List<MovieModel> movieModelList;
        private int resource;
        private LayoutInflater inflater;
        public MovieAdapter(@NonNull Context context, int resource, @NonNull List<MovieModel> objects) {
            super(context, resource, objects);
            movieModelList = objects;
            this.resource = resource;
            inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            if(convertView == null) {
                convertView = inflater.inflate(resource, null);
            }
            ImageView ivMovieIcon;
            TextView tvMovie;
            TextView tvTagline;
            TextView tvYear;
            TextView tvDuration;
            TextView tvDirector;

            RatingBar rbMovieRating;
            TextView tvCast;
            TextView tvStory;

            ivMovieIcon = (ImageView)convertView.findViewById(R.id.ivicon);
            tvMovie = (TextView)convertView.findViewById(R.id.tvmovie);
            tvTagline = (TextView)convertView.findViewById(R.id.tvTagline);
            tvYear = (TextView)convertView.findViewById(R.id.tvYear);
            tvDuration = (TextView)convertView.findViewById(R.id.tvDuration);
            tvDirector = (TextView)convertView.findViewById(R.id.tvDirector);
            rbMovieRating = (RatingBar) convertView.findViewById(R.id.rbMovie);
            tvCast = (TextView)convertView.findViewById(R.id.tvCast);
            tvStory = (TextView)convertView.findViewById(R.id.tvStory);

            tvMovie.setText(movieModelList.get(position).getMovie());
            tvTagline.setText("Year : "+movieModelList.get(position).getTagline());
            tvYear.setText(movieModelList.get(position).getYear());
            tvDuration.setText(movieModelList.get(position).getDuration());
            tvDirector.setText(movieModelList.get(position).getDirector());
            rbMovieRating.setRating(movieModelList.get(position).getRating()/2);


            StringBuffer stringBuffer = new StringBuffer();
            for(MovieModel.cast casts : movieModelList.get(position).getCastlist())
            {
                stringBuffer.append(casts.getName() + ",");

            }
            tvCast.setText(stringBuffer);
            tvStory.setText(movieModelList.get(position).getStory());


            return convertView;
        }
    }

}