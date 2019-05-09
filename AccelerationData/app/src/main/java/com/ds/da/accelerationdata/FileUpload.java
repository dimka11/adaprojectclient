package com.ds.da.accelerationdata;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FileUpload extends AppCompatActivity {

    Button btnUpload, btnPickFile;
    String mediaPath;
    ProgressDialog progressDialog;
    TextView str1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_upload);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading...");

        btnUpload = (Button) findViewById(R.id.buttonUpload);
        btnPickFile = (Button) findViewById(R.id.buttonPick);
        str1 = (TextView) findViewById(R.id.textViewFileName);

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadFile();
            }
        });

        btnPickFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("*/*");
                startActivityForResult(intent, 0);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // When an file is picked
            if (requestCode == 0 && resultCode == RESULT_OK && null != data) {

                // Get the Image from data
                Uri selectedFile = data.getData();
                String[] filePathColumn = {MediaStore.Files.FileColumns.DATA};

                Cursor cursor = getContentResolver().query(selectedFile, filePathColumn, null, null, null);
                assert cursor != null;
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                mediaPath = cursor.getString(columnIndex);
                str1.setText(mediaPath);
                cursor.close();

            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
        }
    }


            // Uploading Image/Video
            private void uploadFile () {
                progressDialog.show();

                // Map is used to multipart the file using okhttp3.RequestBody
                File file = new File(mediaPath);

                // Parsing any Media type file
                String createFormDataNameParameter = GetSharedPreferences.invoke("serverUploadCreateFormDataName"); // file is default
                RequestBody requestBody = RequestBody.create(MediaType.parse("*/*"), file);
                MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData(createFormDataNameParameter, file.getName(), requestBody);
                RequestBody filename = RequestBody.create(MediaType.parse("text/plain"), file.getName());

                ApiConfig getResponse = AppConfig.getRetrofit().create(ApiConfig.class);

                String uploadEndPoint = GetSharedPreferences.invoke("serverUploadPath");

                Call<ServerResponse> call = getResponse.uploadFile(uploadEndPoint, fileToUpload, filename);
                call.enqueue(new Callback<ServerResponse>() {
                    @Override
                    public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                        ServerResponse serverResponse = response.body();
                        if (serverResponse != null) {
                            if (serverResponse.getSuccess()) {
                                Toast.makeText(getApplicationContext(), serverResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), serverResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            assert serverResponse != null;
                            Log.v("Response", serverResponse.toString());
                        }
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onFailure(Call<ServerResponse> call, Throwable t) {
                        Log.v("Response", "onFailure");
                    }
                });
            }
        }
