package com.alihaydar.socialmedia.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.alihaydar.socialmedia.R;
import com.alihaydar.socialmedia.adapter.PostAdepter;
import com.alihaydar.socialmedia.databinding.ActivityFeedBinding;
import com.alihaydar.socialmedia.model.Post;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class FeedActivity extends AppCompatActivity {
    private ActivityFeedBinding binding;
    FirebaseAuth auth;
    FirebaseFirestore firestore;
    ArrayList<Post>arrayList;
    PostAdepter postAdepter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityFeedBinding.inflate(getLayoutInflater());
        View view=binding.getRoot();
        setContentView(view);
        auth=FirebaseAuth.getInstance();
        firestore=FirebaseFirestore.getInstance();
        arrayList=new ArrayList<>();
        getData();
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        postAdepter=new PostAdepter(arrayList);
        binding.recyclerView.setAdapter(postAdepter);
    }

    private void getData() {
        firestore.collection("Posts").orderBy("post_date", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error!=null){
                    Toast.makeText(FeedActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }else {
                    if (value!=null){
                        for(DocumentSnapshot documentSnapshot:value.getDocuments()){
                            Map<String,Object>data=documentSnapshot.getData();
                            String comment=(String) data.get("post_comment");
                            String image=(String) data.get("post_image");
                            String userPhoto=(String) data.get("post_photo");
                            String userName=(String) data.get("post_username");
                            Date dataDate = documentSnapshot.getDate("post_date", DocumentSnapshot.ServerTimestampBehavior.ESTIMATE);
                            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy  HH:mm", Locale.getDefault());
                            String date = sdf.format(dataDate);
                            Post post=new Post(comment,image,userPhoto,userName,date);
                            arrayList.add(post);

                        }
                        postAdepter.notifyDataSetChanged();
                    }
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.option_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.add_post){
            Intent intentToUpload=new Intent(FeedActivity.this, UploadActivity.class);
            startActivity(intentToUpload);
        } else if (item.getItemId()==R.id.settings_page) {


        }else {
            Intent intentToMain=new Intent(FeedActivity.this, MainActivity.class);
            startActivity(intentToMain);
            finish();
            auth.signOut();
        }

        return super.onOptionsItemSelected(item);
    }
}