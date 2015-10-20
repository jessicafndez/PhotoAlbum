package com.muno.photoalbum.BaseActivities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.muno.photoalbum.DirectoryManagement.CheckDirectories;
import com.muno.photoalbum.R;

import java.io.File;

/**
 * Created by InX on 16/10/2015.
 */
public class RenameDirectory extends AppCompatActivity implements View.OnClickListener {

    //Components
    private TextView txtRenameDirName;
    private EditText eTxtNewName;
    private ImageButton btnAccept, btnCancel;


    //Variables
    String oldDirName;
    File filePath;
    File oldDirNameFile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rename_directory_layout);

        txtRenameDirName = (TextView) findViewById(R.id.txtRenameDirName);
        eTxtNewName = (EditText) findViewById(R.id.eTextNewName);
        btnAccept = (ImageButton) findViewById(R.id.acceptBtn);
        btnCancel = (ImageButton) findViewById(R.id.cancelBtn);

        btnAccept.setOnClickListener(this);
        btnCancel.setOnClickListener(this);

        oldDirName = getIntent().getStringExtra("OldDirectoryName");
        txtRenameDirName.setText(oldDirName);

        filePath = new File(Environment.getExternalStorageDirectory() + "/PhotoAlbumDirectory");
    }

    public boolean renameDirectory (File newDir) {
        oldDirNameFile = new File(filePath.getPath() + "/" + oldDirName);
        CheckDirectories checkDirectories = new CheckDirectories(this, filePath);

        if (checkDirectories.checkIfDirectoryNameExist(newDir.getPath())) {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(getApplicationContext());
            Resources res = getResources();
            builder1.setMessage(res.getString(R.string.directory_name_exist));
            builder1.setCancelable(true);
            builder1.setPositiveButton(res.getString(R.string.ok),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            return false;
        }
        else {
            return true;
        }
    }

    @Override
    public void onClick(View v) {
        if (v == btnCancel) {
            //Dont change nothing, just back to MainActivity
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }

        if (v == btnAccept) {
            String newDirName = eTxtNewName.getText().toString();
            File newDirNameFile = new File(filePath.getPath() + "/" + newDirName);
            renameDirectory(newDirNameFile);

            if (renameDirectory(newDirNameFile)) {
                oldDirNameFile.renameTo(newDirNameFile);

                //Exit
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }
}
