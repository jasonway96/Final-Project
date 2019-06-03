package com.example.finalapplication;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {
    String userid, name, phone, location, latitude, longitude;
    Spinner sploc;
    TextView tvphone,tvlocation;
    EditText tvuserid, tvname, edoldpass, ednewpass;
    CircleImageView imgprofile;
    Button btnUpdate;
    ImageButton btnloc;
    Dialog myDialogMap;
    String slatitude, slongitude;
    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        imgprofile = findViewById(R.id.imageView4);
        tvuserid = findViewById(R.id.txtemail);
        tvname = findViewById(R.id.txtUsername);
        tvphone = findViewById(R.id.txtphone);
        edoldpass = findViewById(R.id.txtoldpassword);
        ednewpass = findViewById(R.id.txtnewpassword);
        btnUpdate = findViewById(R.id.button5);
        userid = bundle.getString("Email");//email
        name = bundle.getString("Name");  //full name
        Log.e("MICKY",(bundle.getString("Phone")));
        phone = bundle.getString("Phone"); //phone
        tvphone.setText(phone);
        tvuserid.setText(userid);
        String image_url = "http://socstudents.net/yourwaystechnology/userprofile/" + phone + ".jpg";
        Picasso.with(this).load(image_url)
                .resize(400, 400).into(imgprofile);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        loadUserProfile();
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newemail = tvuserid.getText().toString();
                String newname = tvname.getText().toString();
                String oldpass = edoldpass.getText().toString();
                String newpass = ednewpass.getText().toString();
                dialogUpdate(newemail, newname, oldpass, newpass);

            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("Email", userid);
                bundle.putString("Name", name);
                bundle.putString("Phone", phone);
                intent.putExtras(bundle);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    void loadUserProfile() {
        class LoadUserProfile extends AsyncTask<Void, Void, String> {

            @Override
            protected String doInBackground(Void... voids) {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("Phone", phone);
                RequestHandler rh = new RequestHandler();
                String s = rh.sendPostRequest("http://socstudents.net/yourwaystechnology/load_user.php", hashMap);
                return s;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                //Toast.makeText(ProfileActivity.this, s, Toast.LENGTH_SHORT).show();
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray restarray = jsonObject.getJSONArray("User");
                    JSONObject c = restarray.getJSONObject(0);
                    name = c.getString("Name");
                    userid = c.getString("Email");

                } catch (JSONException e) {

                }

                tvuserid.setText(userid);
                tvname.setText(name);
            }
        }
        LoadUserProfile loadUserProfile = new LoadUserProfile();
        loadUserProfile.execute();
    }

    void updateProfile(final String newemail, final String newname,final String oldpass, final String newpass) {
        class UpdateProfile extends AsyncTask<Void, Void, String> {

            @Override
            protected String doInBackground(Void... voids) {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("Email", newemail);
                hashMap.put("Name", newname);
                hashMap.put("Phone", phone);
                hashMap.put("Opassword", oldpass);
                hashMap.put("Npassword", newpass);
                RequestHandler rh = new RequestHandler();
                String s = rh.sendPostRequest("http://socstudents.net/yourwaystechnology/update_profile.php", hashMap);
                return s;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                Log.e("POP",s);
                if (s.equalsIgnoreCase("success")) {
                    Toast.makeText(ProfileActivity.this, "Success", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("Email", userid);
                    bundle.putString("Name", name);
                    bundle.putString("Phone", phone);
                    intent.putExtras(bundle);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(intent);
                } else {
                    Toast.makeText(ProfileActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                }
            }
        }
        UpdateProfile updateProfile = new UpdateProfile();
        updateProfile.execute();
    }


    public boolean checkLocationPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)) {
                    ActivityCompat.requestPermissions(ProfileActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            99);

                } else {
                    // No explanation needed, we can request the permission.
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            99);
                }
                return false;
            } else {
                return true;
            }
        }
        return true;
    }


    private void dialogUpdate(final String newemail, final String newname, final String oldpass, final String newpass) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Profile");

        alertDialogBuilder
                .setMessage("Update this profile")
                .setCancelable(false)
                .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        updateProfile(newemail, newname,oldpass, newpass);
                    }
                })
                .setNegativeButton("No",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}