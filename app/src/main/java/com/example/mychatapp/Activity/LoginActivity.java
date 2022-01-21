package com.example.mychatapp.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mychatapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    TextView txt_signup;
    EditText login_email, login_password;
    TextView signin_btn;
    FirebaseAuth firebaseAuth;
    //String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();

        txt_signup = findViewById(R.id.txt_signup);
        signin_btn = findViewById(R.id.signin_btn);
        login_email = findViewById(R.id.login_email);
        login_password = findViewById(R.id.login_password);

        signin_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = login_email.getText().toString();
                String password = login_password.getText().toString();

                if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password)){
                    Toast.makeText(LoginActivity.this, "An error occured", Toast.LENGTH_SHORT).show();
                }/*else if(email.matches(emailPattern)){
                    login_email.setError("email type is wrong");
                    Toast.makeText(LoginActivity.this, "email type is wrong!", Toast.LENGTH_SHORT).show();
                }*/else if(password.length() > 6){
                    login_password.setError("Invalid password");
                    Toast.makeText(LoginActivity.this, "Please enter valid password", Toast.LENGTH_SHORT).show();
                }else{
                    firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                            }else{
                                Toast.makeText(LoginActivity.this,"Error in login", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        txt_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
            }
        });
    }
}