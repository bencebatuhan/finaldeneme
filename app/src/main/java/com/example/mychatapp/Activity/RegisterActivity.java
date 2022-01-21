package com.example.mychatapp.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mychatapp.R;
import com.example.mychatapp.Model.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegisterActivity extends AppCompatActivity {

    TextView txt_signin, btn_signup;
    CircleImageView profile_image;
    EditText reg_name, reg_email, reg_password, reg_cPassword;
    FirebaseAuth firebaseAuth;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    Uri imageUri;
    FirebaseDatabase firebaseDatabase;
    FirebaseStorage firebaseStorage;
    String imageURI;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();

        txt_signin = findViewById(R.id.txt_signin);
        profile_image = findViewById(R.id.profile_image);
        reg_email = findViewById(R.id.reg_email);
        reg_email = findViewById(R.id.reg_email);
        reg_name = findViewById(R.id.reg_name);
        reg_password = findViewById(R.id.reg_password);
        reg_cPassword = findViewById(R.id.reg_cPassword);
        btn_signup = findViewById(R.id.btn_signup);


        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();
                String name = reg_name.getText().toString();
                String email = reg_email.getText().toString();
                String password = reg_password.getText().toString();
                String cPassword = reg_cPassword.getText().toString();
                String status = "Hey there I am using This App";


                if(TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(cPassword)){

                    progressDialog.dismiss();
                    Toast.makeText(RegisterActivity.this, "Please enter valid data", Toast.LENGTH_SHORT).show();

                }else if(!email.matches(emailPattern)){

                    progressDialog.dismiss();
                    reg_email.setError("Please enter valid email");
                    Toast.makeText(RegisterActivity.this, "Please enter valid email", Toast.LENGTH_SHORT).show();

                }else if(!password.equals(cPassword)){

                    progressDialog.dismiss();
                    Toast.makeText(RegisterActivity.this, "Password does not match", Toast.LENGTH_SHORT).show();

                }else{

                    firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if(task.isSuccessful()){

                                DatabaseReference databaseReference = firebaseDatabase.getReference().child("user").child(firebaseAuth.getUid());
                                StorageReference storageReference = firebaseStorage.getReference().child("upload").child(firebaseAuth.getUid());

                                if(imageUri != null){
                                    storageReference.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                            if(task.isSuccessful()){
                                                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                       imageURI =  uri.toString();
                                                       Users users = new Users(firebaseAuth.getUid(), name, email, imageURI, status);
                                                       databaseReference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                           @Override
                                                           public void onComplete(@NonNull Task<Void> task) {
                                                                if(task.isSuccessful()){
                                                                    progressDialog.dismiss();
                                                                    startActivity(new Intent(RegisterActivity.this, HomeActivity.class));
                                                                }else{
                                                                    Toast.makeText(RegisterActivity.this, "Error on creating a new user", Toast.LENGTH_SHORT).show();
                                                                }
                                                           }
                                                       });
                                                    }
                                                });
                                            }
                                        }
                                    });
                                }else{
                                    String status = "Hey there I am using This App";
                                    imageURI =  "https://firebasestorage.googleapis.com/v0/b/mychatapp-95db8.appspot.com/o/profile.png?alt=media&token=17b17dd4-a00f-4dd0-8a00-c0071a5c1aad";
                                    Users users = new Users(firebaseAuth.getUid(), name, email, imageURI, status);
                                    databaseReference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                startActivity(new Intent(RegisterActivity.this, HomeActivity.class));
                                            }else{
                                                Toast.makeText(RegisterActivity.this, "Error on creating a new user", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }

                            }else{
                                progressDialog.dismiss();
                                Toast.makeText(RegisterActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 10);
            }
        });


        txt_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 10){
            if(data != null){
                imageUri = data.getData();
                profile_image.setImageURI(imageUri);
            }
        }
    }
}