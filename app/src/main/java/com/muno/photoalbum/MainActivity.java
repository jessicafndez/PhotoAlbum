package com.muno.photoalbum;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ListView;

import com.muno.photoalbum.Adapters.ListDirectoriesAdapter;
import com.muno.photoalbum.DirectoryManagement.NewDirectory;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //Components
    private ImageButton delBtn, addBtn;
    private ListView listView;

    //Variables
    File appDir;
    ArrayList<String> fileNamesStringArray;

    //Classes
    ListDirectoriesAdapter listDirectoriesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Create or Load Initial App Directori
        appDir = new File(Environment.getExternalStorageDirectory() + "/PhotoAlbumDirectory");
        if(!appDir.exists()) {
            appDir.mkdirs();
            Log.d("trolo", "dir. created");
        }else {
            Log.d("trolo", "dir. already exists");
        }


        //Our Components
        delBtn = (ImageButton)findViewById(R.id.delBtn);
        addBtn = (ImageButton)findViewById(R.id.addBtn);
        delBtn.setOnClickListener(this);
        addBtn.setOnClickListener(this);
        listView = (ListView) findViewById(R.id.listViewMenu);

        fileNamesStringArray = new ArrayList<>();

        Log.d("trolo", "Principal Directory Size: " + appDir.listFiles().length);

        if (appDir.listFiles().length != 0)
            LoadDirectoriesList();
    }


    public void LoadDirectoriesList() {
        Log.d("trolo", "Loading Directories.....");
        for (File f : appDir.listFiles()) {
            Log.d("trolo", "Directories:: " + f.getName());
            fileNamesStringArray.add(f.getName());
        }

        listDirectoriesAdapter = new ListDirectoriesAdapter(MainActivity.this, fileNamesStringArray);
        listView.setAdapter(listDirectoriesAdapter);

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("trolo", "Long click Here...");

                //ImageView imageView = (ImageView) v.findViewById(R.id.imageView1);
                CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkBoxDelete);
                checkBox.setVisibility(View.VISIBLE);
                return false;
            }
        });
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
