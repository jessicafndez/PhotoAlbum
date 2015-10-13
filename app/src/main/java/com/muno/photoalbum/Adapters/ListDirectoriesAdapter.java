package com.muno.photoalbum.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.muno.photoalbum.R;

import java.util.ArrayList;

/**
 * Created by InX on 13/10/2015.
 */
public class ListDirectoriesAdapter extends BaseAdapter {

    //Initial Variables
    private Context context;
    private ArrayList<String> directoriesList;
    private LayoutInflater mInflater;

    public ListDirectoriesAdapter(Context c, ArrayList<String> dList) {
        this.context = c;
        this.directoriesList = dList;

        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return this.directoriesList.size();
    }

    @Override
    public Object getItem(int position) {
        return this.directoriesList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(
                    R.layout.directories_list_layout, null);
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.imageDirectories);
            viewHolder.textView = (TextView) convertView.findViewById(R.id.txtDirectoryName);

            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.imageView.setId(position);
        viewHolder.textView.setId(position);

        viewHolder.imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        viewHolder.imageView.setPadding(8, 8, 8, 8);

        viewHolder.textView.setText(directoriesList.get(position));

        return convertView;
    }

    class ViewHolder {
        ImageView imageView;
        TextView textView;
        CheckBox checkBox;
        int id;
    }
}
