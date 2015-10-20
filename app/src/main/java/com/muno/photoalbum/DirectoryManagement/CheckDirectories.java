package com.muno.photoalbum.DirectoryManagement;

import android.app.Activity;
import android.content.res.Resources;
import android.widget.Toast;

import com.muno.photoalbum.R;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by InX on 16/10/2015.
 */
public class CheckDirectories {

    //Variables & Classes
    private Activity activity;
    private File pathFile;
    private File[] filesDirectoryList;


    public CheckDirectories(Activity act, File pFile) {
        this.activity = act;
        this.pathFile = pFile;

        filesDirectoryList = pathFile.listFiles();
    }

    public boolean checkNameAvailable (String s) {
        Resources res = activity.getResources();
        if (checkIfDirectoryNameExist(s)) {
            Toast.makeText(activity.getApplicationContext(), res.getString(R.string.file_already_exists), Toast.LENGTH_LONG).show();
            return  false;
        }
        return true;
    }

    public boolean checkIfDirectoryNameExist(String n) {
        for (File f: filesDirectoryList) {
            if (f.getPath().equalsIgnoreCase(n)) {
                return true;
            }
        }
        return false;
    }
}
