package com.ibrahim.async;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

import model.Flower;

/**
 * Created by ibra on 3/23/16.
 */
public class FlowerAdapter extends ArrayAdapter<Flower> {
    private Context context;
    private List<Flower> flowerList;

    public FlowerAdapter(Context context, int resource, List<Flower> object) {
        super(context, resource, object);
        this.context = context;
        this.flowerList = object;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.item_flower, parent, false);
        Flower flower = flowerList.get(position);
        TextView tv = (TextView) v.findViewById(R.id.textView1);
        tv.setText(flower.getName());
        if (flower.getBitmap() != null) {
            ImageView imageView = (ImageView) v.findViewById(R.id.imageView1);
            imageView.setImageBitmap(flower.getBitmap());
        }
        else {
            FlowerAndView container =new FlowerAndView();
            container.flower=flower;
            container.view=v;
            ImageLoader loader =new ImageLoader();
            loader.execute(container);
        }
        return v;
    }

    class FlowerAndView {
        public Flower flower;
        public View view;
        public Bitmap bitmap;
    }

    private class ImageLoader extends AsyncTask<FlowerAndView, Void, FlowerAndView> {

        @Override
        protected FlowerAndView doInBackground(FlowerAndView... params) {
            FlowerAndView container = params[0];
            Flower flower = container.flower;

            try {
                String imageURL = MainActivity.PHOTOS_BASE_URL + flower.getPhoto();
                InputStream in = (InputStream) new URL(imageURL).getContent();
                Bitmap bitmap = BitmapFactory.decodeStream(in);
                flower.setBitmap(bitmap);
                in.close();
                container.bitmap = bitmap;
                return container;
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;

        }

        @Override
        protected void onPostExecute(FlowerAndView result) {
            ImageView imageView = (ImageView) result.view.findViewById(R.id.imageView1);
            imageView.setImageBitmap(result.bitmap);
        }
    }
}
