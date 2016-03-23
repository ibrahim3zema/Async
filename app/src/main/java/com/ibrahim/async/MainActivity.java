package com.ibrahim.async;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import model.Flower;
import parsers.FlowerJSONParser;
import parsers.FlowerXMLParser;

public class MainActivity extends AppCompatActivity {
    public static final String PHOTOS_BASE_URL =
            "http://services.hanselandpetal.com/photos/";
    TextView output;
    ProgressBar pb;
    List<MyTask> tasks;
    List<Flower> flowerList;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView) findViewById(R.id.list);
//		Initialize the TextView for vertical scrolling
        // output = (TextView) findViewById(R.id.textView);
        //output.setMovementMethod(new ScrollingMovementMethod());

        pb = (ProgressBar) findViewById(R.id.progressBar);
        pb.setVisibility(View.INVISIBLE);

        tasks = new ArrayList<>();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_do_task) {
            if (isOnline()) {
                requestData("http://services.hanselandpetal.com/feeds/flowers.json");
            } else {
                Toast.makeText(this, "Network isn't available", Toast.LENGTH_LONG).show();
            }
        }
        return false;
    }

    private void requestData(String uri) {
        MyTask task = new MyTask();
        task.execute(uri);
    }

    protected void updateDisplay() {
        FlowerAdapter adapter = new FlowerAdapter(this, R.layout.item_flower, flowerList);
        listView.setAdapter(adapter);
    }

    protected boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }

    private class MyTask extends AsyncTask<String, String, List<Flower>> {

        @Override
        protected void onPreExecute() {
//			updateDisplay("Starting task");

            if (tasks.size() == 0) {
                pb.setVisibility(View.VISIBLE);
            }
            tasks.add(this);
        }

        @Override
        protected List<Flower> doInBackground(String... params) {

            String content = HttpManager.getData(params[0]);
            flowerList = FlowerJSONParser.ParseFeed(content);
           /* for (Flower flower : flowerList) {
                try {
                    String imageURL=PHOTOS_BASE_URL+flower.getPhoto();
                    InputStream in = (InputStream) new URL(imageURL).getContent();
                    Bitmap bitmap = BitmapFactory.decodeStream(in);
                    flower.setBitmap(bitmap);
                    in.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }*/
            return flowerList;
        }

        @Override
        protected void onPostExecute(List<Flower> flowers) {
            // flowerList = FlowerJSONParser.ParseFeed(result);
            //  flowerList = FlowerXMLParser.parseFeed(result);
            updateDisplay();
            tasks.remove(this);
            if (tasks.size() == 0) {
                pb.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        protected void onProgressUpdate(String... values) {
//			updateDisplay(values[0]);
        }

    }

}