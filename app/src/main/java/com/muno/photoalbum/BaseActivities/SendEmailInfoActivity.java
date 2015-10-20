package com.muno.photoalbum.BaseActivities;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.muno.photoalbum.R;

import org.w3c.dom.ls.LSResourceResolver;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by InX on 19/10/2015.
 */
public class SendEmailInfoActivity extends AppCompatActivity implements View.OnClickListener {

    //Components
    TextView txtEmailInfo, txtFilesInfo;
    ImageButton btnAccept, btnCancel;

    //Variables
    private String emailString;
    private ArrayList<File> filesToSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.email_send_info_layout);

        txtEmailInfo = (TextView) findViewById(R.id.txtEmailConfirms);
        txtFilesInfo = (TextView) findViewById(R.id.txtFilesCompressString);
        btnAccept = (ImageButton) findViewById(R.id.acceptBtn);
        btnCancel = (ImageButton) findViewById(R.id.cancelBtn);

        btnAccept.setOnClickListener(this);
        btnCancel.setOnClickListener(this);

        emailString = getIntent().getStringExtra("EmailString");
        String fileDirectory = getIntent().getStringExtra("FileDirectory");

        filesToSend = new ArrayList<>();

        FillInfo(emailString, fileDirectory);
    }

    public void FillInfo (String emailS, String fileS) {
        Resources res = getResources();


        File fDirectory = new File (fileS);

        for (File f: fDirectory.listFiles()) {
            String fName = f.getName();
            if (fName.endsWith(".zip")) {
                filesToSend.add(f);
            }
        }


        String messageFiles = res.getString(R.string.with_files) + "\n ";
        for (File f: filesToSend) {
            double fileSizeInKB = (double)f.length() / 1024;
            // Convert the KB to MegaBytes (1 MB = 1024 KBytes)
            double  fileSizeInMB = fileSizeInKB / 1024;
            String result = String.format("%.2f", fileSizeInMB);
            String fSize = Long.toString(f.length());
            messageFiles += " - " + f.getName() + " " + res.getString(R.string.size) + ". " + result +"MB \n";
        }

        String messageFinal = res.getString(R.string.sending_email_to) + " " + emailS;
        Log.d("trolo", "Email Sending: "+messageFinal);
        txtEmailInfo.setText(messageFinal);
        txtFilesInfo.setText(messageFiles);
    }

    public void SendingPreparing (ArrayList<File> fArray, String eMail) {
        for (int i=0; i<fArray.size(); i++) {
            SendEmail(fArray.get(i), eMail, "part "+(i+1));
        }
    }

    public void SendEmail(File f, String eRecipient, String partNum) {

        Resources res = getResources();
        String subjectEmail = res.getString(R.string.email_subject) + partNum;
        String subjectBody = res.getString(R.string.email_body);

        Log.d("trolo", "To: "+eRecipient);
        Log.d("trolo", "Email Sub: "+subjectEmail);
        Log.d("trolo", "Email Body: "+subjectBody);
        Log.d("trolo", "File: "+ f.getPath());

        String filelocation = f.getPath();

        Uri URI = Uri.fromFile(f);

        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL, new String[]{eRecipient});
        i.putExtra(Intent.EXTRA_SUBJECT, subjectEmail);
        i.putExtra(Intent.EXTRA_TEXT   , subjectBody);
        i.putExtra(Intent.EXTRA_STREAM, URI);
        try {
            startActivity(Intent.createChooser(i, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(SendEmailInfoActivity.this, res.getString(R.string.no_client_installed), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {
        if (v == btnAccept) {
            SendingPreparing(filesToSend, emailString);
        }
    }
}
