package com.muno.photoalbum.DirectoryManagement;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.muno.photoalbum.Adapters.GridViewImageAdapter;
import com.muno.photoalbum.ImagesManagement.ImagesSizes;
import com.muno.photoalbum.MainActivity;
import com.muno.photoalbum.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by InX on 09/10/2015.
 */
public class NewDirectory extends AppCompatActivity implements View.OnClickListener {

    //Components
    private ImageButton btnAccept, btnCancel;
    private EditText eTxtDirectoryName;

    //Variables
    private File actualDirectory;
    private File newDataFile;
    private File[] actualDirectoryFilesArray;
    private ArrayList<Bitmap> imagesArray;
    File appDir = new File(Environment.getExternalStorageDirectory() + "/PhotoAlbumDirectory");
   // File appData = new File(Environment.getExternalStorageDirectory() + "/PhotoAlbum/data");

    //Classes
    private GridViewImageAdapter gridViewImageAdapter;
    private ImagesSizes imagesSizes;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_directory_layout);

        //Components
        btnAccept = (ImageButton) findViewById(R.id.acceptBtn);
        btnCancel = (ImageButton) findViewById(R.id.cancelBtn);
        eTxtDirectoryName = (EditText) findViewById(R.id.eTextDirecotyName);
        btnAccept.setOnClickListener(this);
        btnCancel.setOnClickListener(this);

        //Default Directory
        setActualDirectory(Environment.getExternalStorageDirectory() + "/DCIM/Camera");
    }

    void setActualDirectory(String dirName) {
        actualDirectory = new File(dirName);

        actualDirectoryFilesArray = actualDirectory.listFiles();

        TextView directoryText = (TextView) findViewById(R.id.textDefaultDirectory);
        directoryText.setText(actualDirectory.getAbsolutePath().toString());

        //Call load method
        ImagesLoad(actualDirectory);
    }

    File getActualDirecoty() {
        return actualDirectory;
    }

    //Method to call and enumerate files of directory passing as value
    void ImagesLoad(File fileDir) {
        //Set array images Selected
        //isSelected = new boolean[actualDirectory.listFiles().length];

        new ImageLoaderAsyncTask(fileDir).execute();
    }


    public class ImageLoaderAsyncTask extends AsyncTask<Void, Void, ArrayList<Bitmap>> {
        //Asynk Method to Load Images of directory
        private File myDirectory;

        private ArrayList<Bitmap> imagesArray = new ArrayList<>();

        private ProgressDialog progressDialog;

        public ImageLoaderAsyncTask(File f) {
            this.myDirectory = f;

            Log.d("trolo:", "ImageLoaderAsynk: " + f.getPath().toString());
        }

        @Override
        protected void onPreExecute() {
            //ProgressDialog starting Methods
            progressDialog = new ProgressDialog(NewDirectory.this);
            Resources res = getResources();
            progressDialog.setMessage(res.getString(R.string.loading_directory_string));
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }

        @Override
        protected ArrayList<Bitmap> doInBackground(Void... params) {
            for (File f : myDirectory.listFiles()) {
                String filePath = f.getPath();
                Log.d("trolo", "FilePath: " + filePath);


                Bitmap bitmap = decodeSampledBitmapFromUri(f.getPath(), 200, 200);
                imagesArray.add(bitmap);
            }
            return imagesArray;
        }

        @Override
        protected void onPostExecute(ArrayList<Bitmap> imagesList) {
            final GridView gridview = (GridView) findViewById(R.id.gridViewSelect);
            gridViewImageAdapter = new GridViewImageAdapter(NewDirectory.this, imagesArray);

            gridview.setAdapter(gridViewImageAdapter);

            progressDialog.dismiss();
        }

        public Bitmap decodeSampledBitmapFromUri(String path, int reqWidth, int reqHeight) {
            Bitmap bm = null;
            final BitmapFactory.Options options = new BitmapFactory.Options();

            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, options);
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

            options.inJustDecodeBounds = false;

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


    @Override
    public void onClick (View v){
        if (v == btnAccept) {
            Log.d("trolo", "Saved Images List:");

            createNewDirectory(eTxtDirectoryName.getText().toString());
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
                directoryName = eTxtDirectoryName.getText().toString();
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
        boolean[] s = gridViewImageAdapter.getSelectedImages();

        File[] file = actualDirectory.listFiles();

        String saveFilesString = "";

        for (int i=0; i<s.length; i++) {
            Log.d("trolo", "Ssaved: "+s[i]);
            if (s[i] == true) {
                Log.d("trolo", "2- Saved: " + file[i].getPath());
                saveFilesString += file[i].getPath().toString() + "\n";
            }
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
}
