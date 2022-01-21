package com.example.mychatapp.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mychatapp.R;
import com.example.mychatapp.Adapter.UserAdapter;
import com.example.mychatapp.Model.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    RecyclerView mainUserRecyclerView;
    UserAdapter adapter;
    FirebaseDatabase firebaseDatabase;
    ArrayList<Users> usersArrayList;
    ImageView imgLogout;
    ImageView imgSettings;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase= FirebaseDatabase.getInstance();
        usersArrayList = new ArrayList<>();
        DatabaseReference reference = firebaseDatabase.getReference().child("user");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Users users = dataSnapshot.getValue(Users.class);
                    usersArrayList.add(users);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        imgLogout = findViewById(R.id.img_logOut);
        imgSettings = findViewById(R.id.im_Settings);
        mainUserRecyclerView = findViewById(R.id.mainUserRecyclerView);
        mainUserRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new UserAdapter(HomeActivity.this, usersArrayList);
        mainUserRecyclerView.setAdapter(adapter);

        imgLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Dialog dialog = new Dialog(HomeActivity.this, R.style.Dialog);
                dialog.setContentView(R.layout.dialog_layout);

                TextView yesBtn, noBtn;
                yesBtn = dialog.findViewById(R.id.yesBtn);
                noBtn = dialog.findViewById(R.id.noBtn);

                yesBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(HomeActivity.this, RegisterActivity.class));
                    }
                });

                noBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });

        if(firebaseAuth.getCurrentUser() == null){
            startActivity(new Intent(HomeActivity.this, RegisterActivity.class));
        }

        imgSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, SettingsActivity.class));
            }
        });

    }
}