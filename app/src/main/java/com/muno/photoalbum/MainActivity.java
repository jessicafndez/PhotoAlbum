package com.muno.photoalbum;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.muno.photoalbum.Adapters.ListDirectoriesAdapter;
import com.muno.photoalbum.DirectoryManagement.NewDirectory;
import com.muno.photoalbum.DirectoryManagement.OpenDirectory;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //Components
    private ImageButton delBtn, addBtn;
    private ListView listView;

    //Variables
    File appDir;
    ArrayList<String> fileNamesStringArray;
    Resources res;

    //Classes
    ListDirectoriesAdapter listDirectoriesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Create or Load Initial App Directori
        appDir = new File(Environment.getExternalStorageDirectory() + "/PhotoAlbumDirectory");
       // dataDir = new File(Environment.getExternalStorageDirectory() + "/PhotoAlbum/"+"data");
        if(!appDir.exists()) {
            appDir.mkdirs();
            //dataDir.mkdirs();
            Log.d("trolo", "dir. created");
        }else {
            Log.d("trolo", "dir. already exists");
        }
        //ONCE
        //dataDir.delete();
        //dataDir.mkdirs();

        res = getResources();

        listView = (ListView) findViewById(R.id.listViewMenu);
        registerForContextMenu(listView);

        fileNamesStringArray = new ArrayList<>();

        Log.d("trolo", "Principal Directory Size: " + appDir.listFiles().length);

        if (appDir.listFiles().length == 0) {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(getApplicationContext());
            builder1.setMessage(res.getString(R.string.no_albums_found));
            builder1.setCancelable(true);
            builder1.setPositiveButton(res.getString(R.string.ok),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
        }
        else {
            LoadDirectoriesList();
        }
    }


    public void LoadDirectoriesList() {
        Log.d("trolo", "Loading Directories.....");

        ArrayList<HashMap<String, String>> myHashArray =  new ArrayList<>();
        for (File f : appDir.listFiles()) {


            Date lastModDate = new Date(f.lastModified());

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");

            String lastModification = simpleDateFormat.format(lastModDate);

            Log.d("trolo", "Directories:: " + f.getName() + "--> " + lastModification);


            HashMap<String, String> newHash = new HashMap<>();
            newHash.put("DirName",f.getName());
            newHash.put("DirDate", lastModification);

            myHashArray.add(newHash);

            fileNamesStringArray.add(f.getName());
        }



        listDirectoriesAdapter = new ListDirectoriesAdapter(MainActivity.this, fileNamesStringArray, myHashArray);
        listView.setAdapter(listDirectoriesAdapter);

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("trolo", "Long click Here...");

                //ImageView imageView = (ImageView) v.findViewById(R.id.imageView1);
               // CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkBoxDelete);
              //  checkBox.setVisibility(View.VISIBLE);
                return false;
            }
        });

        //Simple click
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("trolo", "Open File: " + listView.getAdapter().getItem(position));
                Intent intent = new Intent(getApplicationContext(), OpenDirectory.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ;
                intent.putExtra("FileName", listView.getAdapter().getItem(position).toString());
                startActivity(intent);
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
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId()==R.id.listViewMenu) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_list_directory, menu);
        }
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

        if (id == R.id.action_add_new) {
            AddNewDirectory();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        String pressDir = fileNamesStringArray.get(((AdapterView.AdapterContextMenuInfo) item.getMenuInfo()).position);
        switch(item.getItemId()) {
            case R.id.action_delete:
                // add stuff here
                DeleteDirectory(pressDir);
                return true;
            case R.id.action_rename:
                // edit stuff here

                return true;
            case R.id.action_compress_send:
                // remove stuff here
                return true;
            case R.id.action_details:
                // remove stuff here
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    public void AddNewDirectory() {
        Intent intent = new Intent(this, NewDirectory.class);
        startActivity(intent);
    }

    /*
    Context Item Selected Actions
     */
    //Delete Directory
    public void DeleteDirectory(String dName) {
        final String directoryName = appDir.getPath() + "/" + dName;
        Log.d("trolo", "We r going to delete: "+directoryName);

        String messageString = res.getString(R.string.delete_directory_msg) + ": \n"+ dName +"\n\n"
                + res.getString(R.string.are_u_sure);

        //Need to Show Message to User
        AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
        builder1.setMessage(messageString);
        builder1.setCancelable(true);
        builder1.setPositiveButton(res.getString(R.string.yes),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //Go to delete
                        Log.d("trolo", "Deleting, sorry...");
                        File fDirectory = new File(directoryName);
                        fDirectory.delete();

                        //Reload Activity
                        finish();
                        startActivity(getIntent());
                    }
                });
        builder1.setNegativeButton(res.getString(R.string.no),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        builder1.show();

    }

    @Override
    public void onClick(View v) {

    }
}
