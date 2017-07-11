package com.theCreatorLabs.SharePic;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.creativityapps.gmailbackgroundlibrary.BackgroundMail;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private Button btnCapture;
    String timeStamp;
    EditText etSenderId, etRecipientId, etPassword;
    String strSenderId, strRecipientId, strPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnCapture = (Button) findViewById(R.id.btn_capture);
        etRecipientId = (EditText) findViewById(R.id.et_recipientid);
        etSenderId = (EditText) findViewById(R.id.et_senderid);
        etPassword = (EditText) findViewById(R.id.et_senderpass);
        int MyVersion = Build.VERSION.SDK_INT;
        if (MyVersion > Build.VERSION_CODES.LOLLIPOP_MR1) {
            if (!checkIfAlreadyhavePermission()) {
                requestForSpecificPermission();
            }
        }

        btnCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                strSenderId = etSenderId.getText().toString().trim();
                strRecipientId = etRecipientId.getText().toString().trim();
                strPassword = etPassword.getText().toString().trim();
                if (!(strSenderId.isEmpty())) {
                    if (!(strRecipientId.isEmpty())) {
                        if (!(strPassword.isEmpty())) {
                            Intent imageIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                            timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                            File imagesFolder = new File(Environment.getExternalStorageDirectory(), "MyImages");
                            imagesFolder.mkdirs();
                            File image = new File(imagesFolder, "IMG_" + timeStamp + ".jpg");
                            Uri uriSavedImage = Uri.fromFile(image);
                            imageIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);
                            startActivityForResult(imageIntent, 1);
                        } else {
                            Toast.makeText(getApplicationContext(), "Enter all the fields", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Enter all the fields", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Enter all the fields", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                BackgroundMail.newBuilder(this)
                        .withUsername(strSenderId)
                        .withPassword(strPassword)
                        .withMailto(strRecipientId)
                        .withType(BackgroundMail.TYPE_PLAIN)
                        .withSubject("this is the subject")
                        .withBody("this is the body")
                        .withAttachments(Environment.getExternalStorageDirectory().getPath() + "/MyImages/" + "IMG_" + timeStamp + ".jpg")
                        .withOnSuccessCallback(new BackgroundMail.OnSuccessCallback() {
                            @Override
                            public void onSuccess() {
                                Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_LONG).show();
                            }
                        })
                        .withOnFailCallback(new BackgroundMail.OnFailCallback() {
                            @Override
                            public void onFail() {
                                Toast.makeText(getApplicationContext(), "Failed.. Allow Less Secure Apps : ON", Toast.LENGTH_LONG).show();
                            }
                        })
                        .send();
            }
        }
    }

    private boolean checkIfAlreadyhavePermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestForSpecificPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.GET_ACCOUNTS,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
        }, 101);
    }
}
