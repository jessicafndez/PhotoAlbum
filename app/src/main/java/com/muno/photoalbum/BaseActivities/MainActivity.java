package com.muno.photoalbum.BaseActivities;

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
import android.widget.ImageButton;
import android.widget.ListView;

import com.muno.photoalbum.Adapters.ListDirectoriesAdapter;
import com.muno.photoalbum.DirectoryManagement.OpenDirectory;
import com.muno.photoalbum.DirectoryManagement.OpenFullImagesDirectory;
import com.muno.photoalbum.R;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity  {

    //Components
    private ImageButton delBtn, addBtn;
    private ListView listView;

    //Variables
    File appDir, tempAppFolder;
    ArrayList<String> fileNamesStringArray;
    Resources res;

    //Classes
    ListDirectoriesAdapter listDirectoriesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Create or Load Initial App Directori
        //Need to create Temp folder to compress and send
        appDir = new File(Environment.getExternalStorageDirectory() + "/PhotoAlbumDirectory");
        tempAppFolder = new File(Environment.getExternalStorageDirectory() + "/TempFileFolder");
       // dataDir = new File(Environment.getExternalStorageDirectory() + "/PhotoAlbum/"+"data");
        if(!appDir.exists() ||!tempAppFolder.exists()) {
            appDir.mkdirs();
            tempAppFolder.mkdirs();
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

        //Register for ContextMenu
        registerForContextMenu(listView);


        //DUDE DUDEEEEEEE, we got dis !!!!
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int arg2, long arg3) {
                Intent intent = new Intent(MainActivity.this, OpenDirectory.class);
                File[] f = appDir.listFiles();
               // Log.d("trolo", "Opening..."+f[arg2].getPath());
                intent.putExtra("directoryName", f[arg2].getPath());
                startActivity(intent);
                finish();
            }
        });

        //On clivk Open plis
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

        if (id == R.id.action_add_new) {

            AddNewDirectory();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId()==R.id.listViewMenu) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.open_folder_menu, menu);
        }
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)
                item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.action_rename:
              //  editNote(info.id);
                //PositionToString((int) info.id);
                RenameDirectory(PositionToString((int) info.id));
                return true;
            case R.id.action_compress_send:
                //deleteNote(info.id);
                CompressAndSend(PositionToString((int) info.id));
                return true;
            case R.id.action_delete:
                //DeleteDirectory();
                DeleteDirectory(PositionToString((int) info.id));
                return true;
            case R.id.action_details:
                //deleteNote(info.id);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    public String PositionToString(int p) {
        File[] f = appDir.listFiles();
        Log.d("trolo",f[p].getPath());
        return f[p].getPath();
    }

    /********************************************
     * ** Context Item Selected Actions
     * ****************************************/
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
                        Log.d("trolo", "Deleting, sorry..."+ directoryName);
                        File fDirectory = new File(directoryName);

                        new DeleteClass(fDirectory, MainActivity.this);

                        finish();
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

    public void RenameDirectory(String dName) {
        //File oldName
        String directoryNameOld = appDir.getPath() + "/" + dName;
        Intent intent = new Intent(this, RenameDirectory.class);
        intent.putExtra("OldDirectoryName", directoryNameOld);
        startActivity(intent);
    }

    public void CompressAndSend(String dName) {
        String directoryName = appDir.getPath() + "/" + dName;
        Intent intent = new Intent(this, CompressAndSendDirectory.class);
        intent.putExtra("DirectoryName", directoryName);
   //     intent.putExtra("TempFileFolder", directoryNameFile.getPath());
        startActivity(intent);
    }


    public void AddNewDirectory() {
        final String defaultDirectory = Environment.getExternalStorageDirectory() + "/DCIM/Camera";

        Log.d("trolo", "Add New Direcotry");
        //Need to inform than maximus album size has to be 10!!

        AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
        Resources res = getResources();
        builder1.setMessage(res.getString(R.string.free_version_max));
        builder1.setCancelable(true);
        builder1.setPositiveButton(res.getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        LaunchIntent(defaultDirectory);
                        dialog.cancel();
                    }
                });
        builder1.show();
    }

    public void LaunchIntent(String dDirectory) {
        Intent intent = new Intent(this, OpenFullImagesDirectory.class);
        intent.putExtra("AlbumDirectory", dDirectory);
        startActivity(intent);
    }
}
