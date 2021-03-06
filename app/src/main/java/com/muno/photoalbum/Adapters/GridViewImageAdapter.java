package com.muno.photoalbum.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.muno.photoalbum.ImagesManagement.PhotosSelected;
import com.muno.photoalbum.R;

import java.util.ArrayList;

/**
 * Created by InX on 09/10/2015.
 */
public class GridViewImageAdapter extends BaseAdapter{

    //Components
    private Context context;
    private LayoutInflater mInflater;

    //Variables
    private ArrayList<Bitmap> imageList;
    private boolean[] selectedImages;
    private PhotosSelected photosSelected;

    public GridViewImageAdapter(Context c, ArrayList<Bitmap> iList, PhotosSelected pS) {
        this.context = c;
        this.imageList = iList;
        this.photosSelected = pS;

        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        selectedImages = new boolean[imageList.size()];
        for(int i=0; i< pS.getObjectSize(); i++) {
          //  Log.d("trolo", "View Selecteds: "+pS.getSingleSelected(i));
            selectedImages[i] = pS.getSingleSelected(i);
        }

        //photosSelected = new PhotosSelected(selectedImages);

        Log.d("trolo","Created Here GRIDVIEW CLASS");
    }


    @Override
    public int getCount() {
        return this.imageList.size();
    }

    @Override
    public Object getItem(int position) {
        return this.imageList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(
                    R.layout.single_image_gridview_layout, null);
            holder.imageView = (ImageView) convertView.findViewById(R.id.imageGridView);
            holder.checkBox = (CheckBox) convertView.findViewById(R.id.checkBoxGridView);

            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.checkBox.setId(position);
        holder.imageView.setId(position);

        holder.imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        holder.imageView.setPadding(8, 8, 8, 8);


        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageView im = (ImageView) v;
                int id = im.getId();

                if (selectedImages[id]) {
                    holder.checkBox.setChecked(false);
                    selectedImages[id] = false;
                } else {
                    holder.checkBox.setChecked(true);
                    selectedImages[id] = true;
                }

                //Save results
                setSelectedImages(id, selectedImages[id]);
            }
        });

        holder.imageView.setImageBitmap(imageList.get(position));
        holder.checkBox .setChecked(selectedImages[position]);
        holder.id = position;

        return convertView;
    }

    public void setSelectedImages(int i, boolean state) {
        Log.d("trolo", "New selected: "+i+";"+selectedImages[i]);
        selectedImages[i] =  state;
        photosSelected.setNewPageSelected(selectedImages);
    }

    public boolean[] getSelectedImages() {
        return  selectedImages;
    }

    class ViewHolder {
        ImageView imageView;
        CheckBox checkBox;
        int id;
    }
}
