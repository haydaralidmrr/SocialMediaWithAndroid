package com.alihaydar.socialmedia.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.alihaydar.socialmedia.databinding.ActivitySignUpBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {
    private ActivitySignUpBinding binding;
    FirebaseAuth auth;
    FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivitySignUpBinding.inflate(getLayoutInflater());
        View view=binding.getRoot();
        setContentView(view);
        auth=FirebaseAuth.getInstance();
        db=FirebaseFirestore.getInstance();
    }

    public void signup(View view){
        String userName=binding.userName.getText().toString();
        String email=binding.email.getText().toString();
        String password=binding.password.getText().toString();

        if (email.matches("")|| userName.matches("")|| password.matches("")){
            Toast.makeText(SignUpActivity.this,"You have to enter all gaps",Toast.LENGTH_LONG).show();
        }else {
            auth.createUserWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    setFirebaseStore(userName,email,password);



                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(SignUpActivity.this,e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                }
            });
        }

    }

    private void setFirebaseStore(String userName, String email, String password) {
        Map<String,Object>userDatas=new HashMap<>();
        userDatas.put("user_email",email);
        userDatas.put("user_username",userName);
        userDatas.put("user_password",password);
        userDatas.put("user_id",FirebaseAuth.getInstance().getUid());
        userDatas.put("user_photo",null);
        userDatas.put("user_birth",null);
        userDatas.put("user_bio",null);
        userDatas.put("user_phone",null);

        db.collection("Datas").document(FirebaseAuth.getInstance().getUid()).set(userDatas)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Intent intent=new Intent(SignUpActivity.this, FeedActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SignUpActivity.this,e.getLocalizedMessage(),Toast.LENGTH_LONG).show();

                    }
                });
    }
}