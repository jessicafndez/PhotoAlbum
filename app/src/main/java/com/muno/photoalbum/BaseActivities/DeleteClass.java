package com.muno.photoalbum.BaseActivities;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import java.io.File;

/**
 * Created by InX on 20/10/2015.
 */
public class DeleteClass {

    Activity activity;

    public DeleteClass(File f, Activity a) {
        this.activity = a;
        DeleteRecursive(f);
    }

    void DeleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                DeleteRecursive(child);

        fileOrDirectory.delete();

        if (fileOrDirectory.exists()) {
            Log.d("trolo", "File exists");
        }
        else {
            Log.d("trolo", "File Not exists");
        }

        //Come back To Main Activity
        Intent intent = new Intent (activity, MainActivity.class);
        activity.startActivity(intent);
    }
}
