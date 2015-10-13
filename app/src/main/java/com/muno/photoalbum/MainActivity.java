package com.muno.photoalbum;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import com.muno.photoalbum.DirectoryManagement.NewDirectory;

import java.io.File;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageButton delBtn, addBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Create or Load Initial App Directori
        File appDir = new File(Environment.getExternalStorageDirectory() + "/PhotoAlbumDirectory");
        if(!appDir.exists()) {
            appDir.mkdirs();
            Log.d("trolo", "dir. created");
        }else {
            Log.d("trolo", "dir. already exists");
        }

        //Our Buttons
        delBtn = (ImageButton)findViewById(R.id.delBtn);
        addBtn = (ImageButton)findViewById(R.id.addBtn);
        delBtn.setOnClickListener(this);
        addBtn.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if (v == addBtn){
            Intent intent = new Intent(this, NewDirectory.class);
            startActivity(intent);
        }
    }
}
