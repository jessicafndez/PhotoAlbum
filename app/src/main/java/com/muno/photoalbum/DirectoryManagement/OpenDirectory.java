package com.muno.photoalbum.DirectoryManagement;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.media.ExifInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

import com.etsy.android.grid.StaggeredGridView;
import com.muno.photoalbum.Adapters.AlbumLayoutAdapter;
import com.muno.photoalbum.Adapters.GridViewImageAdapter;
import com.muno.photoalbum.BaseActivities.CompressAndSendDirectory;
import com.muno.photoalbum.BaseActivities.DeleteClass;
import com.muno.photoalbum.BaseActivities.MainActivity;
import com.muno.photoalbum.BaseActivities.RenameDirectory;
import com.muno.photoalbum.ImagesManagement.ImagesExif;
import com.muno.photoalbum.ImagesManagement.ImagesSizes;
import com.muno.photoalbum.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by InX on 14/10/2015.
 */
public class OpenDirectory extends AppCompatActivity {

    //Variables
    String directoryName = "";
    File directoryNameFile, appDir, dataFile;
    ArrayList<String> arrayDirectoryFiles;

    //Components
    TextView txtDirectoryName;
    EditText editDirectoryName;
    private Resources res;

    private StaggeredGridView mGridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.open_directory_layout);

        directoryName += getIntent().getExtras().getString("FileName");
        appDir = new File(Environment.getExternalStorageDirectory()+"/PhotoAlbumDirectory");

        directoryNameFile = new File(appDir.getPath() + "/"+directoryName);

        Log.d("trolo", "Open Directory: "+directoryNameFile.getPath());


        dataFile = new File(directoryNameFile.getPath()+"/data.txt");

        //Load Components
        editDirectoryName = (EditText) findViewById(R.id.eTxtDirectoryName);

        editDirectoryName.setText(directoryNameFile.getName());

       // arrayDirectoryFiles = loadDataFile(dataFile);

        //loadBitmapFiles(arrayDirectoryFiles);

        res = getResources();

        Log.d("trolo", "Reading: "+dataFile.getPath());

        ImagesLoad(dataFile);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.open_folder_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_rename) {
            RenameDirectory(directoryName);
            return true;
        }

        if (id == R.id.action_compress_send) {
            CompressAndSend(directoryName);
            return true;
        }

        if (id == R.id.action_delete) {
            DeleteDirectory(directoryName);
            return true;
        }

        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
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
        AlertDialog.Builder builder1 = new AlertDialog.Builder(OpenDirectory.this);
        builder1.setMessage(messageString);
        builder1.setCancelable(true);
        builder1.setPositiveButton(res.getString(R.string.yes),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //Go to delete
                        Log.d("trolo", "Deleting, sorry..."+ directoryName);
                        File fDirectory = new File(directoryName);

                        new DeleteClass(fDirectory, OpenDirectory.this);

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
        intent.putExtra("TempFileFolder", directoryNameFile.getPath());
        startActivity(intent);
    }

    void ImagesLoad(File dataFile) {
        new ImageLoaderAsyncTask(loadDataFile(dataFile)).execute();
    }

    public ArrayList<String> loadDataFile(File f) {
        StringBuilder text = new StringBuilder();

        Log.d("trolo", "Load Data File: "+f.getPath());

        ArrayList<String> arrayList = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(f));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                Log.d("trolo", "Reading: "+line +" ----");
                arrayList.add(line);
                text.append('\n');
            }
            br.close();
        }
        catch (IOException e) {
            //You'll need to add proper error handling here
        }

        Log.d("trolo", "Open Directory: In Data FIle----");
        Log.d("trolo", text.toString());

        return arrayList;
    }

    public ArrayList<Bitmap> loadBitmapFiles(ArrayList<String> arrayFiles) {
        ArrayList<Bitmap> arrayImages = new ArrayList<>();

        final BitmapFactory.Options options = new BitmapFactory.Options();

        for (String s: arrayFiles) {
            Log.d("trolo", "LoadBitmapFile: " + s);
            Bitmap bitmap = BitmapFactory.decodeFile(s, options);

            Log.d("trolo", "Image Size: "+bitmap.getWidth()+" x "+bitmap.getHeight());

            arrayImages.add(bitmap);
        }

        return arrayImages;
    }


    public class ImageLoaderAsyncTask extends AsyncTask<Void, Void, ArrayList<Bitmap>> {
        //Asynk Method to Load Images of directory
        private File myDirectory;

        private ArrayList<Bitmap> imagesArray = new ArrayList<>();
        private ArrayList<String> imageList = new ArrayList<>();

        private ProgressDialog progressDialog;

        public ImageLoaderAsyncTask(ArrayList<String> iList) {
            imageList = iList;
        }

        @Override
        protected void onPreExecute() {
            //ProgressDialog starting Methods
            progressDialog = new ProgressDialog(OpenDirectory.this);
            Resources res = getResources();
            progressDialog.setMessage(res.getString(R.string.loading_directory_string));
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }

        @Override
        protected ArrayList<Bitmap> doInBackground(Void... params) {

            for (String s: imageList) {
                Log.d("trolo", "FilePath: " + s);

                String myOrientation = new ImagesExif().getImageOrientation(s);

                Log.d("trolo", "My orientation: "+myOrientation);

                Bitmap bitmap = null;
                if (myOrientation.equalsIgnoreCase("portrait")) {
                    bitmap = new ImagesSizes().decodeSampledBitmapFromUri(s, 120, 120);
                }
                else {
                    bitmap = new ImagesSizes().decodeSampledBitmapFromUri(s, 300, 150);
                }
                imagesArray.add(bitmap);
            }
            return imagesArray;
        }

        @Override
        protected void onPostExecute(ArrayList<Bitmap> imagesBitmap) {
            mGridView = (StaggeredGridView) findViewById(R.id.grid_view);
            AlbumLayoutAdapter albumLayoutAdapter = new AlbumLayoutAdapter(OpenDirectory.this, imagesBitmap, imageList);
            mGridView.setAdapter(albumLayoutAdapter);

            progressDialog.dismiss();
        }
    }
}
