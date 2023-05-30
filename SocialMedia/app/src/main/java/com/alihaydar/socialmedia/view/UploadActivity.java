package com.alihaydar.socialmedia.view;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import com.alihaydar.socialmedia.R;
import com.alihaydar.socialmedia.databinding.ActivityUploadBinding;
import com.alihaydar.socialmedia.model.LoadingDialog;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UploadActivity extends AppCompatActivity {
    private ActivityUploadBinding binding;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    private FirebaseStorage firebaseStorage;
    private FirebaseUser user;
    private StorageReference storageReference;
    ActivityResultLauncher<Intent>activityResultLauncher;
    ActivityResultLauncher<String>permissionLauncher;
    Uri uri;
    LoadingDialog dialog;
    Map<String,Object>userDatas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityUploadBinding.inflate(getLayoutInflater());
        View view=binding.getRoot();
        setContentView(view);
        dialog=new LoadingDialog(UploadActivity.this);
        uri=null;
        auth=FirebaseAuth.getInstance();
        firestore=FirebaseFirestore.getInstance();
        firebaseStorage=FirebaseStorage.getInstance();
        user=auth.getCurrentUser();
        storageReference=firebaseStorage.getReference();
        userDatas=new HashMap<>();
        getUserData();

        registerLauncher();
    }

    private void getUserData() {
        firestore.collection("Datas").document(user.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
            String username=(String) documentSnapshot.get("user_username");
            String email=user.getEmail();
            String photo=(String) documentSnapshot.get("user_photo");
            userDatas.put("post_username",username);
            userDatas.put("post_email",email);
            userDatas.put("post_photo",photo);

            binding.postUserName.setText("@"+username);

            if (photo==null){
                binding.userPphoto.setImageResource(R.drawable.defaultpp);
            }else {
                Picasso.get().load(photo).into(binding.userPphoto);
            }


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {


            }
        });
    }

    private void registerLauncher() {

        activityResultLauncher=registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode()==RESULT_OK){
                    Intent intentGetData=result.getData();
                    if (intentGetData!=null){
                       uri =intentGetData.getData();
                       binding.imagePost.setImageURI(uri);
                    }

                }
            }
        });


        permissionLauncher=registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                if(result){
                    Intent intentToGallery=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    activityResultLauncher.launch(intentToGallery);
                }else {
                    Toast.makeText(UploadActivity.this,"You have to give permission",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void postFunc(View view){
        if(uri==null && binding.editTextComment.getText().toString().matches("")){
            Toast.makeText(UploadActivity.this,"Please select an image and write comment",Toast.LENGTH_LONG).show();

        }else {
            dialog.startLoadingDialog();
            setFirebaseStorage();

        }

    }

    private void setFirebaseStorage() {
        String comment=binding.editTextComment.getText().toString();
        userDatas.put("post_comment",comment);
        if (uri!=null){
            UUID uuid=UUID.randomUUID();
            String imageFolder="images/"+uuid+".jpg";
            storageReference.child(imageFolder).putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    //download url
                    StorageReference storageReference1=firebaseStorage.getReference(imageFolder);
                    storageReference1.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String url=uri.toString();
                            userDatas.put("post_image",url);
                            userDatas.put("post_date", FieldValue.serverTimestamp());
                            firebaseFireStore();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(UploadActivity.this,e.getLocalizedMessage(),Toast.LENGTH_LONG).show();

                        }
                    });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(UploadActivity.this,e.getLocalizedMessage(),Toast.LENGTH_LONG).show();

                }
            });
        }else {
            userDatas.put("post_image",null);
            userDatas.put("post_date", FieldValue.serverTimestamp());
            firebaseFireStore();
        }
    }

    private void firebaseFireStore() {
        firestore.collection("Posts").add(userDatas).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                String postId = documentReference.getId();
                userDatas.put("post_id",postId);
                documentReference.set(userDatas).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        dialog.dismissDialog();
                        Intent intentToFeed = new Intent(UploadActivity.this,FeedActivity.class);
                        intentToFeed.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intentToFeed);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UploadActivity.this,e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void addPhoto(View view){
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.TIRAMISU){
            if(ContextCompat.checkSelfPermission(UploadActivity.this, Manifest.permission.READ_MEDIA_IMAGES)!= PackageManager.PERMISSION_GRANTED){
                if(ActivityCompat.shouldShowRequestPermissionRationale(UploadActivity.this,Manifest.permission.READ_MEDIA_IMAGES)){
                    Snackbar.make(view,"Need permission enter your gallery and select a photo",Snackbar.LENGTH_INDEFINITE).setAction("Need Permission", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //permission
                            permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
                        }
                    }).show();
                }else {
                    //permission
                    permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
                }
            }else {
                Intent intentToGallery=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                activityResultLauncher.launch(intentToGallery);

            }
        }else {
            if(ContextCompat.checkSelfPermission(UploadActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                if(ActivityCompat.shouldShowRequestPermissionRationale(UploadActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE)){
                    Snackbar.make(view,"Need permission enter your gallery and select a photo",Snackbar.LENGTH_INDEFINITE).setAction("Need Permission", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //permission
                            permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                        }
                    }).show();
                }else {
                    //permission
                    permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                }
            }else {
                Intent intentToGallery=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                activityResultLauncher.launch(intentToGallery);

            }

        }
    }
}