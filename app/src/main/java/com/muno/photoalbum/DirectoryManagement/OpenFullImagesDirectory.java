package com.muno.photoalbum.DirectoryManagement;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.etsy.android.grid.StaggeredGridView;
import com.muno.photoalbum.Adapters.GridViewImageAdapter;
import com.muno.photoalbum.ImagesManagement.PhotosSelected;
import com.muno.photoalbum.R;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by InX on 21/10/2015.
 */
public class OpenFullImagesDirectory extends AppCompatActivity implements View.OnClickListener{

    //Components
    TextView txtPagesCount;
    Button btnRight, btnLeft;
  //  ImageButton btnOk;


    //Variables
    private int pagesCounter, maxPages;
    private ArrayList<String> allFiles;
    private String albumDirectory;


    private List<PhotosSelected> listSaveds = new ArrayList<>();
    private PhotosSelected savedXPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.open_album_full);

        txtPagesCount = (TextView) findViewById(R.id.txtPagesCount);
        btnLeft = (Button) findViewById(R.id.btnLeft);
        btnRight = (Button) findViewById(R.id.btnRight);
        btnLeft.setOnClickListener(this);
        btnRight.setOnClickListener(this);

        albumDirectory = getIntent().getStringExtra("AlbumDirectory");

        Log.d("trolo", "Arrived album: "+albumDirectory);

        pagesCounter = 0;

        allFiles = PhotoLoadFromString(albumDirectory);

        //check if number of photos is less than 100
        int mP = (int) Math.ceil(allFiles.size()/100.0);

        Log.d("trolo", "Number of files: "+allFiles.size());
        setPagesMax(mP);

        setPageNum(pagesCounter);

        int maxXPage;
        if (allFiles.size() > 100) {
            maxXPage = 100;
        }
        else {
            maxXPage = allFiles.size();
        }

        Log.d("trolo", "Number of Pages: " + mP);

        //We know de dimension of first asynctask to...
        PhotosSelected photosSelected = new PhotosSelected(new boolean[maxXPage], maxXPage);

        //pass First array
        new AlbumLoaderAsyncTask(0, 0, maxXPage, photosSelected).execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_select_directory, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_camera_folder) {
            //erase array
            listSaveds.clear();
            String defaultDirectory = Environment.getExternalStorageDirectory() + "/DCIM/Camera";
            ChangeDirectory(defaultDirectory);
            return true;
        }

        if (id == R.id.action_whatsapp_folder) {
            //erase array
            listSaveds.clear();
            String defaultDirectory = Environment.getExternalStorageDirectory() + "/WhatsApp/Media/WhatsApp Images";
            ChangeDirectory(defaultDirectory);
            return true;
        }
        //Need to create a directory view
        if (id == R.id.action_accept_btn) {
            //On click accept
            //Check if something in this page has been selected
            SavedCheckedsByPage(getPageNum(), savedXPage);

            ArrayList<String> savedPhotosArray = SavedPhotos();

            //Max 10 photos per album
            Log.d("trolo", "Total saved: "+savedPhotosArray.size());

            Intent intent = new Intent(this, NewDirectory.class);
            intent.putStringArrayListExtra("PhotoSavedArray", savedPhotosArray);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void ChangeDirectory (String directoryName) {
        Intent intent  = getIntent();
        intent.putExtra("AlbumDirectory", directoryName);
        startActivity(intent);
        finish();
    }

    public void setPageNum(int pNum) {
        this.pagesCounter = pNum;
    }
    public int getPageNum() {
        return this.pagesCounter;
    }
    public void setPagesMax(int pMax) {
        this.maxPages = pMax;
    }
    public int getPageMax() {
        return this.maxPages;
    }

    public ArrayList<String> PhotoLoadFromString(String folder) {
        ArrayList<String> arrayList =  new ArrayList<>();

        File f = new File(folder);
        for (File myFile : f.listFiles()) {
            if (fileIsImage(myFile)) {
                arrayList.add(myFile.getPath());
            }
        }
        return arrayList;
    }

    public boolean fileIsImage(File file) {
        String[] okFileExtensions =  new String[] {"jpg", "png", "gif","jpeg"};
        for (String extension : okFileExtensions) {
            if (file.getName().toLowerCase().endsWith(extension)){
                return true;
            }
        }
        return false;
    }


    public class AlbumLoaderAsyncTask extends AsyncTask<Void, Void, ArrayList<Bitmap>> {
        private int pageLoad, maxLoad, minLoad;
        private ProgressDialog progressDialog;
        private int pageNum;
        private PhotosSelected photosSelected;

        //Need to pass selectImages
        public AlbumLoaderAsyncTask(int pNum, int minPage, int maxPage, PhotosSelected pS) {
            this.minLoad = minPage;
            this.maxLoad = maxPage;
            this.pageLoad = pNum;
            this.pageNum = pNum;
            this.photosSelected = pS;

            Log.d("trolo", "Loading Page: " + pageLoad + "; Files ->" + minLoad + "-" + maxLoad);
        }

        @Override
        protected void onPreExecute() {
            //ProgressDialog starting Methods
            progressDialog = new ProgressDialog(OpenFullImagesDirectory.this);
            Resources res = getResources();
            progressDialog.setMessage(res.getString(R.string.loading_directory_string));
            progressDialog.setCanceledOnTouchOutside(false);

            progressDialog.show();

            savedXPage = null;

          //  freeMemory(imagesArray);
        }

        @Override
        protected ArrayList<Bitmap> doInBackground(Void... params) {

            ArrayList<Bitmap> myBitmap = new ArrayList<>();

            Log.d("mysize", "Loading Files: "+myBitmap.size());

            //We r going to load only 1 page in each AsyncTask
            for(int i=minLoad; i<maxLoad; i++) {
                Bitmap bitmap = decodeSampledBitmapFromUri(allFiles.get(i), 200, 200);
                myBitmap.add(bitmap);
            }
            return myBitmap;
        }

        @Override
        protected void onPostExecute(ArrayList<Bitmap> imagesList) {
            String pageText = "Page " + (getPageNum()+1) + " of " + getPageMax();
            txtPagesCount.setText(pageText);

            GridViewImageAdapter gridViewImageAdapter = new GridViewImageAdapter(OpenFullImagesDirectory.this, imagesList, photosSelected);
            StaggeredGridView mGridView  = (StaggeredGridView) findViewById(R.id.grid_view_new_photos);
            mGridView.setAdapter(gridViewImageAdapter);

            //need to manage onLongClicks in images: Delte & Properties

            savedXPage = photosSelected;

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

    @Override
    public void onClick(View v) {
        if (v == btnRight) {
            if ((getPageNum()+1)<getPageMax()) {

                //Saved Actual selecteds
                SavedCheckedsByPage(getPageNum(), savedXPage);

                //Increment number page
                setPageNum(getPageNum()+1);

                Log.d("trolo", "Right: " + getPageMax() + "; Num:" + getPageNum());

                int photosXPage;

                //If we actualy are in the max number of pages availables
                if ((getPageNum()+1) == getPageMax()) {
                    photosXPage = allFiles.size()-(getPageNum()*100);
                    PhotosSelected pS = GetCheckedsByPageUp(getPageNum(), photosXPage);
                    new AlbumLoaderAsyncTask(getPageNum(), getPageNum()*100, allFiles.size(), pS).execute();
                }
                else {
                    photosXPage = (getPageNum()*100)-((getPageNum()-1)*100);
                    PhotosSelected pS = GetCheckedsByPageUp(getPageNum(), photosXPage);
                    new AlbumLoaderAsyncTask(getPageNum(), getPageNum()*100, (getPageNum()+1)*100, pS).execute();
                }
            }
        }

        if (v == btnLeft) {
            if (getPageNum()>0) {
                //Already not incrementing pageNum here
                SavedCheckedsByPage(getPageNum(), savedXPage);
                setPageNum(getPageNum() - 1);
                Log.d("trolo", "Max: " + getPageMax() + "; Num:" + getPageNum());

                PhotosSelected pS = GetCheckedsByPageDown(getPageNum());

                new AlbumLoaderAsyncTask(getPageNum(), getPageNum()*100, (getPageNum()+1)*100,
                        pS).execute();
            }
        }
    }

    //pNum include -1, for the array
    public PhotosSelected GetCheckedsByPageUp(int pNum, int pageSize){
        if (listSaveds.size() > pNum) {
            Log.d("trolo", "GetChekeds: ");
            for(int i=0; i<listSaveds.get(pNum).getObjectSize(); i++){
                Log.d("trolo", "In List: "+listSaveds.get(pNum).getSingleSelected(i));
            }
            return listSaveds.get(pNum);
        }
        else {
            return new PhotosSelected(new boolean[pageSize], pageSize);
        }
    }
    public PhotosSelected GetCheckedsByPageDown(int pNum) {
        return listSaveds.get(pNum);
    }
    public void SavedCheckedsByPage(int pNum, PhotosSelected pSelect){
        Log.d("trolo", "List Saveds Size: "+listSaveds.size()+"; pag: "+pNum);
        if (listSaveds.size() > pNum) {
            //Its mean update
            listSaveds.set(pNum, pSelect);
        }
        else {
            listSaveds.add(pSelect);
        }
    }

    private ArrayList<String> SavedPhotos() {
        Log.d("trolo", "Going to Save...");
        //Count how many photos to load
        int totalSaved = 0;
        ArrayList<String> actualPhotosSaved = new ArrayList<>();
        File folderName = new File(albumDirectory);

        //Only images files, not others
        List<File> listFiles = new ArrayList<>();
        for(File f: folderName.listFiles()) {
            if (fileIsImage(f)) {
                listFiles.add((f));
            }
        }

        int pageNum=0;
        for(PhotosSelected p: listSaveds) {
            Log.d("trolo", "Object Size: "+p.getObjectSize());
            for (int i=0; i<p.getObjectSize(); i++) {
                Log.d("trolo", "Final List: "+p.getSingleSelected(i)+"; "+i);
                if (p.getSingleSelected(i)) {
                    totalSaved++;

                    actualPhotosSaved.add(listFiles.get(i+(pageNum*100)).getPath());
                }
            }
            pageNum++;
        }

        Log.d("trolo", "Total Saved; "+totalSaved);

        //Need to get file name of saved photos
        for(String s: actualPhotosSaved) {
            Log.d("trolo", "Photo: " + s);
        }
        return  actualPhotosSaved;
    }
}
