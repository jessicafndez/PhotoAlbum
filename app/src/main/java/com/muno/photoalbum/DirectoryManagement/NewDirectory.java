package com.muno.photoalbum.DirectoryManagement;

import android.app.ProgressDialog;
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
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.muno.photoalbum.Adapters.GridViewImageAdapter;
import com.muno.photoalbum.R;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by InX on 09/10/2015.
 */
public class NewDirectory extends AppCompatActivity implements View.OnClickListener {

    //Components
    private ImageButton btnAccept, btnCancel;

    //Variables
    private File actualDirectory;
    private File[] actualDirectoryFilesArray;
    private ArrayList<Bitmap> imagesArray;

    //Classes
    private GridViewImageAdapter gridViewImageAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_directory_layout);

        //Components
        btnAccept = (ImageButton) findViewById(R.id.acceptBtn);
        btnCancel = (ImageButton) findViewById(R.id.cancelBtn);
        btnAccept.setOnClickListener(this);
        btnCancel.setOnClickListener(this);

        //Default Directory
        setActualDirectory(Environment.getExternalStorageDirectory() + "/DCIM/Camera");
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

    void setActualDirectory(String dirName) {
        actualDirectory = new File(dirName);

        actualDirectoryFilesArray = actualDirectory.listFiles();

        TextView directoryText = (TextView) findViewById(R.id.textDefaultDirectory);
        directoryText.setText(actualDirectory.getAbsolutePath().toString());

        //Call next method
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
           // isSelected = new boolean[myDirectory.listFiles().length];

            Log.d("trolo:", "ImageLoaderAsynk: "+f.getPath().toString());
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

            // gridViewAdapterV2.notifyDataSetChanged();

            gridview.setAdapter(gridViewImageAdapter);


/*
            gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                   // File[] files = myDirectory.listFiles();
                    //Log.d("trolo", "You have Selected: " + position + "; Name: " + files[position].getPath());

                   // ImageView imageView = (ImageView) v.findViewById(R.id.imageView1);
                   // imageView.setBackgroundColor(Color.BLUE);

                   // setItemsSelected(position, v);

                    v.setBackgroundResource(R.drawable.photo_border_selected);
                }
            });
            */

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


        //Method to enable disable image border in select images
        public void setItemsSelected(int pos, View v) {
            // if (isSelected[pos] == false) {
            //    isSelected[pos] = true;
            // }
            //else if (isSelected[pos] == true) {
            //    isSelected[pos] = false;
            // }

            //for (int j=0; j<isSelected.length; j++) {
            //    Log.d("trolo", "Show: "+isSelected[j]);
            // }

        /*
        for (int i=0; i<isSelected.length; i++) {
            if (isSelected[i] == true) {
                v.setBackgroundResource(R.drawable.photo_border_selected);
            }
            else {
                v.setBackgroundResource(R.drawable.photo_border_nonselected);
            }
        }
        */
        }

    }


        @Override
        public void onClick (View v){
            if (v == btnAccept) {
                Log.d("trolo", "Saved Images List:");
                boolean[] s = gridViewImageAdapter.getSelectedImages();

                File[] f = actualDirectory.listFiles();

                for (int i=0; i<s.length; i++) {
                    Log.d("trolo", "Ssaved: "+s[i]);
                    if (s[i] == true) {
                        Log.d("trolo", "2- Saved: " + f[i].getPath());
                    }
                }
            }
        }

    //Set Default Directory Name
    public void setDefaultDirectoryName(){
        //I already checked it exist in MainActivity.java
        //This is out principal App Directory
        File appDir = new File(Environment.getExternalStorageDirectory() + "/PhotoAlbumDirectory");
        File[] allFiles = appDir.listFiles();
        if (allFiles.length == 0) {
            //Means Directory is empty so...
            Log.d("trolo", "App Directori is Empty!!!");
        }
        for (File f: allFiles) {
            if (f.isDirectory()) {
                Log.d("trolo", "App Directories: "+f.getPath());
            }
        }
    }

}
