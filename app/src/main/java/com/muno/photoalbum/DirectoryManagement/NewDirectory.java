package com.muno.photoalbum.DirectoryManagement;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.etsy.android.grid.StaggeredGridView;
import com.muno.photoalbum.Adapters.AlbumAdapter;
import com.muno.photoalbum.Adapters.AlbumLayoutAdapter;
import com.muno.photoalbum.Adapters.GridViewImageAdapter;
import com.muno.photoalbum.ImagesManagement.ImagesSizes;
import com.muno.photoalbum.BaseActivities.MainActivity;
import com.muno.photoalbum.ImagesManagement.PhotosSelected;
import com.muno.photoalbum.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by InX on 09/10/2015.
 */
public class NewDirectory extends AppCompatActivity implements View.OnClickListener {

    //Components
    private ImageButton btnAddMore, btnTrash;
    private EditText eTxtDirectoryName;
    private TextView txtAlbumSize;
    StaggeredGridView mGridView;

    //Variables
    private File actualDirectory;
    private File newDataFile;
    private File[] actualDirectoryFilesArray;
    private ArrayList<Bitmap> imagesArray;
    File appDir = new File(Environment.getExternalStorageDirectory() + "/PhotoAlbumDirectory");
    private boolean[] selectedToTrash;
    private PhotosSelected photosSelectedTrash;
    ArrayList<String> photosSaved;

    //Classes
    private GridViewImageAdapter gridViewImageAdapter;
    private ImagesSizes imagesSizes;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_directory_layout);

        //Components
        eTxtDirectoryName = (EditText) findViewById(R.id.eTextDirecotyName);
        btnTrash = (ImageButton) findViewById(R.id.trashBtn);
        btnAddMore = (ImageButton) findViewById(R.id.addMoreBtn);
        txtAlbumSize = (TextView) findViewById(R.id.txtSizeInfo);

        btnAddMore.setOnClickListener(this);
        btnTrash.setOnClickListener(this);

        mGridView  = (StaggeredGridView) findViewById(R.id.gridViewSelect);
       // registerForContextMenu(mGridView);


        //saved Photos
        photosSaved = getIntent().getStringArrayListExtra("PhotoSavedArray");
        for (String s: photosSaved) {
            Log.d("trolo", "New Direcotry Saved: "+s);
        }

        photosSelectedTrash = new PhotosSelected(new boolean[photosSaved.size()],
                photosSaved.size());

        Resources res = getResources();
        String folderSize = res.getString(R.string.directory_size)
                + ": " + photosSaved.size();
        txtAlbumSize.setText(folderSize);
        if(photosSaved.size()<10) {
            txtAlbumSize.setTextColor(Color.parseColor("#009999"));
        }
        else {
            txtAlbumSize.setTextColor(Color.parseColor("#fd1d2d"));
        }

        new ImageLoaderAsyncTask(photosSaved).execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_directory_add_more, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_accept_btn) {
            createNewDirectory(eTxtDirectoryName.getText().toString());
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



    public class ImageLoaderAsyncTask extends AsyncTask<Void, Void, ArrayList<Bitmap>> {
        private ProgressDialog progressDialog;
        private List<String> photoList = new ArrayList<>();

        private ImageLoaderAsyncTask(List<String> pList) {
            this.photoList = pList;
        }

        @Override
        protected void onPreExecute() {
            //ProgressDialog starting Methods */
            progressDialog = new ProgressDialog(NewDirectory.this);
            Resources res = getResources();
            progressDialog.setMessage(res.getString(R.string.loading_directory_string));
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }

        @Override
        protected ArrayList<Bitmap> doInBackground(Void... params) {
            ArrayList<Bitmap> myBitmap = new ArrayList<>();

            Log.d("mysize", "Loading Files: " + myBitmap.size());
            //We r going to load only 1 page in each AsyncTask
            for(String s: photoList) {
                Bitmap bitmap = decodeSampledBitmapFromUri(s, 200, 200);
                myBitmap.add(bitmap);
            }
            return myBitmap;
        }
        @Override
        protected void onPostExecute(ArrayList<Bitmap> imagesList) {
            //AlbumLayoutAdapter gridViewImageAdapter = new AlbumLayoutAdapter(NewDirectory.this, imagesList);

            //mGridView.setAdapter(gridViewImageAdapter);
             gridViewImageAdapter = new GridViewImageAdapter(NewDirectory.this, imagesList,
                    photosSelectedTrash);
            StaggeredGridView mGridView  = (StaggeredGridView) findViewById(R.id.gridViewSelect);
            mGridView.setAdapter(gridViewImageAdapter);

            progressDialog.dismiss();
        }

        public Bitmap decodeSampledBitmapFromUri(String path, int reqWidth, int reqHeight) {
            Bitmap bm = null;
            final BitmapFactory.Options options = new BitmapFactory.Options();

            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, options);
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

            options.inJustDecodeBounds = false;
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            options.inDither = true;

            bm = BitmapFactory.decodeFile(path, options);

            return bm;
        }
        public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
            final int height = options.outHeight;
            final int width = options.outWidth;
            int inSampleSize = 1;

            if (height > reqHeight || width > reqWidth) {
                if (width > height) {
                    inSampleSize = Math.round((float) height / (float) reqHeight);
                } else {
                    inSampleSize = Math.round((float) width / (float) reqWidth);
                }
            }
            return inSampleSize;
        }
    }


    /*
    Method to check if directory name is available
     */
    public void createNewDirectory(String dName) {
        String directoryName = "";

        //No directoy name already inserted!!
        if (dName.equalsIgnoreCase("")) {
            Log.d("trolo", "Go to Default Dir Name");
            directoryName = createDefaultDirectoryName();
            createNewDirectory(new File(directoryName));

        } else  {
            if (!checkNameAvailable(dName)){
                Log.d("trolo", "Directory Name is: " + dName);
                eTxtDirectoryName.setText("");
            }

            //name is available
            else {
                directoryName = appDir.getPath() +"/" + eTxtDirectoryName.getText().toString();
                Log.d("trolo", "DirectoryNAme: "+directoryName);
                createNewDirectory(new File(directoryName));
            }
        }
    }

    public boolean checkNameAvailable (String s) {
        Resources res = getResources();
        if (checkIfDirectoryNameExist(s)) {
            Toast.makeText(getApplicationContext(), res.getString(R.string.file_already_exists), Toast.LENGTH_LONG).show();
            return  false;
        }
        return true;
    }

    /*
    Set Default Directory Name if directoryName textbox is empty
     */
    public String createDefaultDirectoryName(){
        //I already checked it exist in MainActivity.java
        //This is out principal App Directory
        File[] allFiles = appDir.listFiles();

        String defaultNameString = "My Photo Album ";

        String newDirectoryName = "";

        Log.d("trolo", "App DIRECT PATH: "+appDir.getPath());

        //Means appDir is Empty -> No directories YET
        if (allFiles.length == 0) {
            //Means Directory is empty so...
            Log.d("trolo", "App Directori is Empty!!!");
            newDirectoryName = appDir.getPath() + "/" + defaultNameString + "1";
        }

        //App Base directory contain some directory
        else {
            //Show me all directories
            for (File f: allFiles) {
                Log.d("trolo", "Direc: "+f.getPath());
            }

            //Starting with
            int num = 1;
            newDirectoryName = appDir.getPath() + "/"+ defaultNameString + Integer.toString(num);

            //If exist, continue
            while(checkIfDirectoryNameExist(newDirectoryName)) {
                num++;
                newDirectoryName = appDir.getPath() + "/" + defaultNameString + Integer.toString(num);
            }
        }
        return newDirectoryName;
    }

    public boolean checkIfDirectoryNameExist(String n) {
        for (File f: appDir.listFiles()) {
            if (f.getPath().equalsIgnoreCase(n)) {
                return true;
            }
        }
        return false;
    }

    public void createNewDirectory(File f) {
        Toast.makeText(this, "Saving...", Toast.LENGTH_LONG).show();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.d("trolo", "-----FORCE SLEEP  2 OFF-----");

        Log.d("trolo", "Creating directory...: "+f.getPath());
        f.mkdirs();

        //Save data here
       // boolean[] s = gridViewImageAdapter.getSelectedImages();

//        File[] file = actualDirectory.listFiles();

        /*
        String saveFilesString = "";

        for (int i=0; i<s.length; i++) {
            Log.d("trolo", "Ssaved: "+s[i]);
            if (s[i] == true) {
                Log.d("trolo", "2- Saved: " + file[i].getPath());
                saveFilesString += file[i].getPath().toString() + "\n";
            }
        }
        */

        String saveFilesString = "";
        for(int i=0; i<photosSaved.size(); i++) {
            saveFilesString += photosSaved.get(i) + "\n";
            Log.d("trolo","Saving....."+photosSaved.get(i));
        }


        File newDataFile = new File(f.getPath() + "/data.txt");
        Log.d("trolo", "Data File: " + newDataFile.getPath());

        writeDataFile(newDataFile, saveFilesString);


        //Go to MainActivity
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        this.finish();
    }

    public void writeDataFile(File f, String dataString) {
        try {
            FileWriter writer = new FileWriter(f);
            writer.append(dataString);
            writer.flush();
            writer.close();

        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readDataFile(File f) {
        StringBuilder text = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(f));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
        }
        catch (IOException e) {
            //You'll need to add proper error handling here
        }

        Log.d("trolo", "In Data FIle----");
        Log.d("trolo", text.toString());
    }

    @Override
    public void onClick (View v){
        if (v == btnAddMore) {
          //  Log.d("trolo", "Saved Images List:");

         //   createNewDirectory(eTxtDirectoryName.getText().toString());
        }

        if(v == btnTrash) {
            //First check is something is selected
            //selected for trash
            int count = 0;
            for(int i=0; i<photosSelectedTrash.getObjectSize(); i++){
                if(photosSelectedTrash.getSingleSelected(i)== true) {
                    count++;
                }
            }
            Log.d("trolo", "Trash objects: "+count);
            Resources res = getResources();

            if (count == 0){
                AlertDialog.Builder builder1 = new AlertDialog.Builder(NewDirectory.this);
                builder1.setMessage(res.getString(R.string.no_photos_to_trash));
                builder1.setCancelable(true);
                builder1.setPositiveButton(res.getString(R.string.ok),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //  AddNewDirectory();
                                dialog.cancel();
                            }
                        });
                builder1.show();
            }
            else {
                String message = res.getString(R.string.you_going_to_select) + " " + count + " "
                        + res.getString(R.string.photos);
                AlertDialog.Builder builder1 = new AlertDialog.Builder(NewDirectory.this);
                builder1.setMessage(message);
                builder1.setCancelable(true);
                final int c = count;
                builder1.setPositiveButton(res.getString(R.string.ok),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //  delete selected photos & reload activity
                                DeleteAndReloadPhotos(c);
                                dialog.cancel();
                            }
                        });
                builder1.setNegativeButton(res.getString(R.string.cancel),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //  delete selected photos & reload activity
                                dialog.cancel();
                            }
                        });
                builder1.show();
            }
        }
    }
    /*****************************************************
    * To Delete seleted photos and reload activity
     *****************************************************/
    public void DeleteAndReloadPhotos(int nDelete) {
       // int newSize = photosSelectedTrash.getObjectSize() - nDelete;
        ArrayList<String> newPhotoSaved = new ArrayList<>();
        for(int i=0; i<photosSelectedTrash.getObjectSize(); i++) {
            if(photosSelectedTrash.getSingleSelected(i) == false) {
                //if not selected, then add in new array
                newPhotoSaved.add(photosSaved.get(i));
            }
        }

        Intent intent = getIntent();
        intent.putStringArrayListExtra("PhotoSavedArray", newPhotoSaved);
        startActivity(intent);
        finish();
    }
}
