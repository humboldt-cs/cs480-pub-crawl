package com.example.pubcrawl;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class RegisterActivity extends AppCompatActivity
{
    public static final String TAG = "RegisterActivity";
    private EditText etUser;
    private EditText etPass;
    private EditText etEmail;
    private EditText etName;
    private Button btnReg;

    @Override
    protected void onCreate( Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        if (ParseUser.getCurrentUser() != null) {
            goMainActivity();
        }

        etUser = findViewById(R.id.etUser);
        etPass = findViewById(R.id.etPass);
        etEmail = findViewById(R.id.etEmail);
        btnReg = findViewById(R.id.btnReg);
        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick register button");

                //Todo: Make user able to set Nickname from the register screen

                ParseUser user = new ParseUser();
                user.setUsername(etUser.getText().toString());
                user.setPassword(etPass.getText().toString());
                user.setEmail(etEmail.getText().toString());
                user.signUpInBackground(new SignUpCallback() {
                    @Override
                    public void done(ParseException e)
                    {
                        if(e != null)
                        {
                            //Register Unsuccessful
                            Log.e(TAG, "Issue with login",e);
                            Toast.makeText(RegisterActivity.this, "Issue with Register", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        goMainActivity();
                        Toast.makeText(RegisterActivity.this, "Success!", Toast.LENGTH_SHORT).show();

                    }
                });
            }
        });
    }

    private void goMainActivity()
    {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }
}
