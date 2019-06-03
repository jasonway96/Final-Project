package com.example.finalapplication;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {
    TextView tvRegister, tvForgot;
    EditText edEmail, edPassword;
    Button btnLogin;
    SharedPreferences sharedPreferences;
    CheckBox cbRemember;
    Dialog myDialogWindow;
    String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        edEmail = findViewById(R.id.editTextEmail);
        edPassword = findViewById(R.id.editTextPassword);
        btnLogin = findViewById(R.id.buttonLogin);
        tvRegister = findViewById(R.id.tvRegister);
        tvForgot = findViewById(R.id.textView2);
        cbRemember = findViewById(R.id.checkBox);
        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = edEmail.getText().toString();
                String pass = edPassword.getText().toString();

                loginUser(email, pass);
            }
        });
        cbRemember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cbRemember.isChecked()) {
                    String email = edEmail.getText().toString();
                    String pass = edPassword.getText().toString();
                    savePref(email, pass);
                }

            }
        });
        loadPref();
    }

    private void savePref(String e, String p) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("Email", e);
        editor.putString("Password", p);

        editor.commit();
        Toast.makeText(this, "Preferences has been saved", Toast.LENGTH_SHORT).show();
    }

    private void loadPref() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String premail = sharedPreferences.getString("Email", "");
        String prpass = sharedPreferences.getString("Password", "");
        if (premail.length() > 0) {
            cbRemember.setChecked(true);
            edEmail.setText(premail);
            edPassword.setText(prpass);
        }
    }

    private void loginUser(final String email, final String pass) {
        class LoginUser extends AsyncTask<Void, Void, String> {
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(LoginActivity.this, "Login user", "You are going into main page", false, false);
            }

            @Override
            protected String doInBackground(Void... voids) {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("Email", email);
                hashMap.put("Password", pass);
                RequestHandler rh = new RequestHandler();
                String s = rh.sendPostRequest("http://socstudents.net/yourwaystechnology/login.php", hashMap);
                return s;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                if (s.length()>0) {
                    //Toast.makeText(LoginActivity.this, s, Toast.LENGTH_LONG).show();
                    String[] val = s.split(",");
                    Bundle bundle = new Bundle();
                    bundle.putString("Email",email);
                    bundle.putString("Name",val[0]);
                    bundle.putString("Phone",val[1]);
                    Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                } else {
                    Toast.makeText(LoginActivity.this, "Please check your email or password", Toast.LENGTH_LONG).show();
                }
            }
        }
        LoginUser loginUser = new LoginUser();
        loginUser.execute();
    }
}