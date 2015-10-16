package com.muno.photoalbum.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.etsy.android.grid.util.DynamicHeightImageView;
import com.muno.photoalbum.ImagesManagement.ImagesExif;
import com.muno.photoalbum.R;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by InX on 14/10/2015.
 */
public class AlbumLayoutAdapter extends BaseAdapter{

    public Context context;
    private LayoutInflater mInflater;

    //Variables
    private ArrayList<Bitmap> imageBitmap;
    private ArrayList<String> imageString;
    private boolean[] selectedImages;


    public AlbumLayoutAdapter(Context c, ArrayList<Bitmap> iList, ArrayList<String> iString) {
        this.context = c;
        this.imageBitmap = iList;
        this.imageString = iString;

        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return this.imageBitmap.size();
    }

    @Override
    public Object getItem(int position) {
        return this.imageBitmap.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(
                    R.layout.album_image_single_layout, null);
            holder.imageView = (DynamicHeightImageView) convertView.findViewById(R.id.imageDinamic);

            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.imageView.setId(position);

        holder.imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        holder.imageView.setPadding(8, 8, 8, 8);

        //int rotation = getRotationAngle(s);

        //Rotate ImageView

        String imageFileName = imageString.get(position);

        int rotation = new ImagesExif().getRotationAngle(imageFileName);

        holder.imageView.setImageBitmap(imageBitmap.get(position));
        holder.imageView.setRotation(rotation);

        holder.id = position;

        return convertView;
    }

    class ViewHolder  {
        DynamicHeightImageView imageView;
        int id;
    }

}
