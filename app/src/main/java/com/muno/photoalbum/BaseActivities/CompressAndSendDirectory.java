package com.muno.photoalbum.BaseActivities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.muno.photoalbum.R;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by InX on 19/10/2015.
 */
public class CompressAndSendDirectory extends AppCompatActivity implements View.OnClickListener {

    //Components
    TextView txtDirectoryName, txtCompressMessage, txtCompressMessage2;
    ImageButton btnCancel, btnAccept;
    EditText eTextEmail;

    //Variables
    File newTempFolder;
    ArrayList<String> fileNameStringArray;
    String directoryName;

    File[] filesToSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.compress_and_send_layout);

        txtDirectoryName = (TextView) findViewById(R.id.txtCompressDirName);
        txtCompressMessage = (TextView) findViewById(R.id.txtCompressingMessage);
        txtCompressMessage2 = (TextView) findViewById(R.id.txtCompressingMessage2);
        btnCancel = (ImageButton) findViewById(R.id.cancelBtn);
        btnAccept = (ImageButton) findViewById(R.id.acceptBtn);
        eTextEmail = (EditText) findViewById(R.id.eTextDestinationMail);

        //buttons actions
        btnAccept.setOnClickListener(this);

        //First get Directory Name to compress
        String tempFileFolder = getIntent().getStringExtra("TempFileFolder");
        directoryName = getIntent().getStringExtra("DirectoryName");
        
        
        //Hide Keyboard on Enter



        //Create a temp Folder to allow all photo files
        newTempFolder = new File(tempFileFolder);
        File directoryNameFolder = new File(directoryName);

        //Put Directory name to textview here
        txtDirectoryName.setText(directoryNameFolder.getName());

        Log.d("trolo", "Compress Received: ");
        Log.d("trolo", "Temp File: "+tempFileFolder);
        Log.d("trolo", "Dire Name: "+directoryName);

        //if exist delete
        if (newTempFolder.listFiles().length>0){
            for (File f: newTempFolder.listFiles()){
                f.delete();
            }
        }

        //First get .data.txt of directry name
        fileNameStringArray =  LoadPhotoFolderString(directoryNameFolder);

        new FolderLoaderAsyncTask(fileNameStringArray, directoryNameFolder, newTempFolder).execute();
    }

    public ArrayList<String> LoadPhotoFolderString(File f) {
        File datFile = new File(f.getPath()+"/data.txt");

        ArrayList<String> tempArray = new ArrayList<>();

        Log.d("trolo", "Reading data.txt OpenPhotoFolder--");
        Log.d("trolo", "Show Me files here: ");

        try {
            BufferedReader br = new BufferedReader(new FileReader(datFile));
            String line;

            while ((line = br.readLine()) != null) {
                Log.d("trolo", "Reading: " + line + " ----");
                tempArray.add(line);
            }
            br.close();
        }
        catch (IOException e) {
            //You'll need to add proper error handling here
        }
        return tempArray;
    }

    public class FolderLoaderAsyncTask extends AsyncTask<Void, Void, ArrayList<File>> {

        private ProgressDialog progressDialog;
        private ArrayList<String> stringArrayList;
        private File tempFolder;
        private File directoryPrincipal;

        int numFolders;

        public FolderLoaderAsyncTask(ArrayList<String> sArray, File tPrincipal, File tFolder) {
            this.stringArrayList = sArray;
            this.tempFolder = tFolder;
            this.directoryPrincipal = tPrincipal;
        }

        @Override
        protected void onPreExecute() {
            //ProgressDialog starting Methods
            progressDialog = new ProgressDialog(CompressAndSendDirectory.this);
            Resources res = getResources();
            progressDialog.setMessage(res.getString(R.string.loading_directory_files));
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }

        @Override
        protected ArrayList<File> doInBackground(Void... params) {
            ArrayList<Bitmap> arrayImages = new ArrayList<>();

            Log.d("trolo", "Number of photos: " + stringArrayList.size());
            float albumFloat = stringArrayList.size() / 3;
            numFolders = (int) Math.ceil(albumFloat);
            Log.d("trolo", "Number of albums: "+numFolders);
            for(int i=1; i<=albumFloat; i++) {
                String folder = tempFolder + "/" + directoryPrincipal.getName() + "_part" + (i);
                File folderFile = new File(folder);
                folderFile.mkdirs();
            }

            for (File f: tempFolder.listFiles()) {
                Log.d("trolo", "Folders----: "+f.getAbsolutePath().toString());
            }

            final BitmapFactory.Options options = new BitmapFactory.Options();

            File[] folderArray = tempFolder.listFiles();
            int count = 0;

            for (int i=0; i<albumFloat; i++) {
                for (int j=i+count; j<(count + 3); j++) {
                    String folder = tempFolder + "/" + directoryPrincipal.getName() + "_part" + (i+1);
                    Log.d("trolo", "LoadBitmapFile: " + stringArrayList.get(j));
                    Bitmap bitmap = BitmapFactory.decodeFile(stringArrayList.get(j), options);

                    Log.d("trolo", "Image Size: " + bitmap.getWidth() + " x " + bitmap.getHeight());

                    arrayImages.add(bitmap);

                    File tFile = new File(stringArrayList.get(j));
                    String nString = tFile.getName();
                    File f = new File(folder + "/" + nString);

                    Log.d("trolo", "Create F: " + f.getPath());
                    try {
                        f.createNewFile();

                        //Convert bitmap to byte array
                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/ , bos);

                        byte[] bitmapdata = bos.toByteArray();

                        //write the bytes in file
                        FileOutputStream fos = new FileOutputStream(f);
                        fos.write(bitmapdata);
                        fos.flush();
                        fos.close();

                    } catch (IOException e) {

                    }
                }
                if (count<(numFolders-1)) {
                    count++;
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<File> imagesBitmap) {

            //How to get number of files in directory
            Log.d("trolo", "Number of files: "+tempFolder.listFiles().length);

            //Maximus 9 photos -> 3 folder of 3 photos each one
            filesToSend = new File[tempFolder.listFiles().length];

            for (File f: tempFolder.listFiles()) {
                zipFolder(f.getPath(), f.getPath() + ".zip");
            }

            //Show textView Messages
            Resources res = getResources();
            String message1 = res.getString(R.string.numero_photos_abum) + " " + stringArrayList.size() +
                    " " + res.getString(R.string.photos) + "\n";
            String message2 =  res.getString(R.string.in) + "  " +
                    numFolders + " " + res.getString(R.string.emails);

            txtCompressMessage.setText(message1);
            txtCompressMessage2.setText(message2);

            progressDialog.dismiss();
        }

        void zipFolder(String inputFolderPath, String outZipPath) {
            try {
                FileOutputStream fos = new FileOutputStream(outZipPath);
                ZipOutputStream zos = new ZipOutputStream(fos);
                File srcFile = new File(inputFolderPath);
                File[] files = srcFile.listFiles();
                Log.d("trolo", "Zip directory: " + srcFile.getName());
                for (int i = 0; i < files.length; i++) {
                    Log.d("trolo", "Adding file: " + files[i].getName());
                    byte[] buffer = new byte[1024];
                    FileInputStream fis = new FileInputStream(files[i]);
                    zos.putNextEntry(new ZipEntry(files[i].getName()));
                    int length;
                    while ((length = fis.read(buffer)) > 0) {
                        zos.write(buffer, 0, length);
                    }
                    zos.closeEntry();
                    fis.close();
                }
                zos.close();
            } catch (IOException ioe) {
                Log.e("", ioe.getMessage());
            }
        }
    }

    public void chechFiles (File f) {
        File[] folder = f.listFiles();
        for (File myF: folder) {
            Log.d("trolo", "File: "+myF.getName());
        }
    }

    public boolean CheckEmailForm(String s) {
        if (s.contains("@") && s.contains(".")) {
            return true;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        if (v == btnCancel) {

        }
        if (v == btnAccept) {
            //Check is email is correct
            if (CheckEmailForm(eTextEmail.getText().toString())) {
                //Open new Activity with intro data
                Intent intent = new Intent(this, SendEmailInfoActivity.class);
                intent.putExtra("EmailString", eTextEmail.getText().toString());
                intent.putExtra("FileDirectory", newTempFolder.getPath());
                startActivity(intent);
                finish();
            }
            else {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(getApplicationContext());
                Resources res = getResources();
                builder1.setMessage(res.getString(R.string.message_wrong_email));
                builder1.setCancelable(true);
                builder1.setPositiveButton(res.getString(R.string.ok),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
            }
        }
    }
}
