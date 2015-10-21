package com.muno.photoalbum.DirectoryManagement;

import android.app.ProgressDialog;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.etsy.android.grid.StaggeredGridView;
import com.muno.photoalbum.Adapters.AlbumLayoutAdapter;
import com.muno.photoalbum.Adapters.GridViewImageAdapter;
import com.muno.photoalbum.R;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by InX on 21/10/2015.
 */
public class OpenFullImagesDirectory extends AppCompatActivity implements View.OnClickListener{

    //Components
    TextView txtPagesCount;
    Button btnRight, btnLeft;


    //Variables
    private String txtPagesCountString;
    private int pagesCounter, maxPages;
    private ArrayList<String> allFiles;
    private ArrayList<Bitmap> imagesArray;

    //Classes
    private AlbumLoaderAsyncTask albumAsyncTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.open_album_full);

        txtPagesCount = (TextView) findViewById(R.id.txtPagesCount);
        btnLeft = (Button) findViewById(R.id.btnLeft);
        btnRight = (Button) findViewById(R.id.btnRight);
        btnLeft.setOnClickListener(this);
        btnRight.setOnClickListener(this);

        imagesArray = new ArrayList<>();

        String albumDirectory = getIntent().getStringExtra("AlbumDirectory");

        Log.d("trolo", "Arrived album: "+albumDirectory);

        pagesCounter = 1;

        allFiles = PhotoLoadFromString(albumDirectory);

        int mP = (int) Math.ceil(allFiles.size()/100.0);

        setPagesMax(mP);

        setPageNum(pagesCounter);

        new AlbumLoaderAsyncTask(1, 1, 100).execute();
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
            arrayList.add(myFile.getPath());
        }


        return arrayList;
    }

    public class AlbumLoaderAsyncTask extends AsyncTask<Void, Void, ArrayList<Bitmap>> {
        private int pageLoad, maxLoad, minLoad;
        private ProgressDialog progressDialog;

        public AlbumLoaderAsyncTask(int pNum, int minPage, int maxPage) {
            this.minLoad = minPage;
            this.maxLoad = maxPage;
            this.pageLoad = pNum;

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

          //  freeMemory(imagesArray);
        }

        @Override
        protected ArrayList<Bitmap> doInBackground(Void... params) {

            ArrayList<Bitmap> myBitmap = new ArrayList<>();

            Log.d("mysize", "Loading Files: "+myBitmap.size());

            //First count how many files directory have --> maxPages

            //We r going to load only 1 page in each AsyncTask
            for(int i=minLoad; i<maxLoad; i++) {
                Log.d("trolo", "Loading Files: "+allFiles.get(i) + "-->"+i);

                Bitmap bitmap = decodeSampledBitmapFromUri(allFiles.get(i), 200, 200);
                myBitmap.add(bitmap);
            }
            return myBitmap;
        }

        @Override
        protected void onPostExecute(ArrayList<Bitmap> imagesList) {
            String pageText = "Page " + getPageNum() + " of " + getPageMax();
            txtPagesCount.setText(pageText);

            GridViewImageAdapter gridViewImageAdapter = new GridViewImageAdapter(OpenFullImagesDirectory.this, imagesList);
            StaggeredGridView mGridView  = (StaggeredGridView) findViewById(R.id.grid_view_new_photos);
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


    @Override
    public void onClick(View v) {
        if (v == btnRight) {
            if (getPageNum()<getPageMax()) {
                setPageNum(getPageNum() + 1);
                Log.d("trolo", "Max: " + getPageMax() + "; Num:" + getPageNum());
                if (getPageNum() == getPageMax()) {
                    new AlbumLoaderAsyncTask(getPageNum(), (getPageNum()-1)*100, allFiles.size()).execute();
                }
                else {
                    new AlbumLoaderAsyncTask(getPageNum(), (getPageNum()-1)*100, getPageNum()*100).execute();
                }
            }
        }

        if (v == btnLeft) {
            if (getPageNum()>1) {
                setPageNum(getPageNum() - 1);
                Log.d("trolo", "Max: " + getPageMax() + "; Num:" + getPageNum());

                if (getPageNum() == 1) {
                    new AlbumLoaderAsyncTask(getPageNum(), getPageNum()*100, getPageNum()*100).execute();
                }
                else {
                    new AlbumLoaderAsyncTask(getPageNum(), (getPageNum()-1)*100, getPageNum()*100).execute();
                }
            }
        }
    }

}
