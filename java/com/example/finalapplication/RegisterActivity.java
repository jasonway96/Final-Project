package com.example.finalapplication;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.finalapplication.LoginActivity;
import com.example.finalapplication.R;
import com.example.finalapplication.RequestHandler;
import com.example.finalapplication.User;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    EditText edEmail,edPass,edPhone,edName;
    Button btnReg;
    ImageView imgprofile;
    TextView tvlogin;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initView();
        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUserInput();
            }
        });
        tvlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        imgprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogTakePicture();
            }
        });
    }

    private void dialogTakePicture() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Do You Wish To Take a Picture?");

        alertDialogBuilder
                .setMessage("")
                .setCancelable(false)
                .setPositiveButton("YES",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                            startActivityForResult(takePictureIntent, 1);
                        }
                    }
                })
                .setNegativeButton("NO",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1 && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imageBitmap = ThumbnailUtils.extractThumbnail(imageBitmap,400,500);
            imgprofile.setImageBitmap(imageBitmap);
            imgprofile.buildDrawingCache();
            ContextWrapper cw = new ContextWrapper(this);
            File pictureFileDir = cw.getDir("basic", Context.MODE_PRIVATE);
            if (!pictureFileDir.exists()) {
                pictureFileDir.mkdir();
            }
            Log.e("FILE NAME", "" + pictureFileDir.toString());
            if (!pictureFileDir.exists() && !pictureFileDir.mkdirs()) {
                return;
            }
            FileOutputStream outStream = null;
            String photoFile = "profile.jpg";
            File outFile = new File(pictureFileDir, photoFile);
            try {
                outStream = new FileOutputStream(outFile);
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
                outStream.flush();
                outStream.close();
                //hasimage = true;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void registerUserInput() {
        String Email,Pass,Phone,Name;
        Email = edEmail.getText().toString();
        Pass = edPass.getText().toString();
        Phone = edPhone.getText().toString();
        Name = edName.getText().toString();
        //error checking here
        user = new User(Name,Phone,Email,Pass);
        //insertData();
        registerUserDialog();
    }

    private void insertData(){
        class RegisterUser extends AsyncTask<Void, Void, String> {

            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(RegisterActivity.this,
                        "Registration", "Thanks for register !", false, false);
            }

            @Override
            protected String doInBackground(Void... voids) {
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("Email", user.Email);
                hashMap.put("Name", user.Name);
                hashMap.put("Password", user.Password);
                hashMap.put("Phone", user.Phone);
                RequestHandler rh = new RequestHandler();
                String s = rh.sendPostRequest
                        ("http://socstudents.net/yourwaystechnology/insert_registration.php", hashMap);
                return s;
            }

            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                if (s.equalsIgnoreCase("success")) {
                    Toast.makeText(RegisterActivity.this, "Registration Success", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    RegisterActivity.this.finish();
                    startActivity(intent);
                } else if (s.equalsIgnoreCase("nodata")) {
                    Toast.makeText(RegisterActivity.this, "Please fill in data first", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(RegisterActivity.this, "Registration Failed", Toast.LENGTH_SHORT).show();
                }
            }

        }
        RegisterUser registerUser = new RegisterUser();
        registerUser.execute();
    }

    private void registerUserDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(this.getResources().getString(R.string.registerfor)+" "+user.Name+"?");

        alertDialogBuilder
                .setMessage(this.getResources().getString(R.string.registerdialognew))
                .setCancelable(false)
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        //Toast.makeText(getActivity(), "DELETE "+jobid, Toast.LENGTH_SHORT).show();
                        new Encode_image().execute(getDir(),user.Phone+".jpg");
                        Toast.makeText(RegisterActivity.this, getResources().getString(R.string.registrationprocess), Toast.LENGTH_SHORT).show();


                    }
                })


                .setNegativeButton("NO",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public String getDir(){
        ContextWrapper cw = new ContextWrapper(this);
        File pictureFileDir = cw.getDir("basic", Context.MODE_PRIVATE);
        if (!pictureFileDir.exists()) {
            pictureFileDir.mkdir();
        }
        Log.d("GETDIR",pictureFileDir.getAbsolutePath());
        return pictureFileDir.getAbsolutePath()+"/profile.jpg";
    }





    private void initView() {
        edEmail = findViewById(R.id.txtEmail);
        edPass = findViewById(R.id.txtpassword);
        edPhone =findViewById(R.id.txtphone);
        edName = findViewById(R.id.txtname);
        btnReg = findViewById(R.id.btn_register);
        tvlogin = findViewById(R.id.tvregister);
        imgprofile = findViewById(R.id.imageView2);
    }

    public class Encode_image extends AsyncTask<String,String,Void> {
        private String encoded_string, image_name;
        Bitmap bitmap;

        @Override
        protected Void doInBackground(String... args) {
            String filname = args[0];
            image_name = args[1];
            bitmap = BitmapFactory.decodeFile(filname);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream);
            byte[] array = stream.toByteArray();
            encoded_string = Base64.encodeToString(array, 0);
            return null;
        }


        @Override
        protected void onPostExecute(Void avoid) {
            makeRequest(encoded_string, image_name);
        }


        private void makeRequest(final String encoded_string, final String image_name) {
            class UploadAll extends AsyncTask<Void, Void, String> {
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                }

                @Override
                protected String doInBackground(Void... params) {
                    HashMap<String, String> map = new HashMap<>();
                    map.put("encoded_string", encoded_string);
                    map.put("image_name", image_name);
                    RequestHandler rh = new RequestHandler();//request server connection
                    String s = rh.sendPostRequest("http://socstudents.net/yourwaystechnology/userImage.php", map);
                    return s;
                }

                @Override
                protected void onPostExecute(String s) {
                    super.onPostExecute(s);
                    Toast.makeText(RegisterActivity.this,s,Toast.LENGTH_SHORT).show();
                    if (s.equalsIgnoreCase("Success")) {
                        // Toast.makeText(RegisterActivity.this, "Success Upload Image", Toast.LENGTH_SHORT).show();
                        insertData();

                    }else{
                        Toast.makeText(RegisterActivity.this, "Failed Registration", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            UploadAll uploadall = new UploadAll();
            uploadall.execute();
        }
    }

}