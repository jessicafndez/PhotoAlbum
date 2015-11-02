package com.muno.photoalbum.DirectoryManagement;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.etsy.android.grid.StaggeredGridView;
import com.muno.photoalbum.BaseActivities.MainActivity;
import com.muno.photoalbum.R;

import java.io.File;

/**
 * Created by InX on 02/11/2015.
 */
public class OpenDirectory extends AppCompatActivity {

    //Components
    TextView txtDirectoryName;
    StaggeredGridView gridView;

    //Variables
    private File dataFile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.open_directory_layout);

        String directoryName = getIntent().getStringExtra("directoryName");

        Log.d("trolo", "In open directory: "+ directoryName);

        txtDirectoryName = (TextView) findViewById(R.id.txtDirectoryFileName);
        gridView = (StaggeredGridView) findViewById(R.id.grid_view_open);

        txtDirectoryName.setText(directoryName);
    }

    /***
    * Override Back Button
     */
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}
