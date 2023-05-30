package com.alihaydar.socialmedia.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.alihaydar.socialmedia.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityMainBinding.inflate(getLayoutInflater());
        View view=binding.getRoot();
        setContentView(view);
        auth=FirebaseAuth.getInstance();
        currentUserCheck();
    }

    private void currentUserCheck() {
        FirebaseUser user=auth.getCurrentUser();
        if (user!=null){
            intentToFeed();
        }
    }

    private void intentToFeed() {
        Intent intent=new Intent(MainActivity.this, FeedActivity.class);
        startActivity(intent);
        finish();
    }

    public void login(View view){
        String email=binding.emailId.getText().toString();
        String password=binding.passwordId.getText().toString();

        if (email.matches("") || password.matches("")){
            Toast.makeText(MainActivity.this,"You have to enter email and password",Toast.LENGTH_LONG).show();
        }else {
            auth.signInWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                intentToFeed();


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MainActivity.this,e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                }
            });
        }
    }
    public void signUp(View view){
        Intent intentToSignUp= new Intent(MainActivity.this, SignUpActivity.class);
        startActivity(intentToSignUp);
    }
}