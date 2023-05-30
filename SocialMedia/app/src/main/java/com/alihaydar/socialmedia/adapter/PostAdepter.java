package com.alihaydar.socialmedia.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alihaydar.socialmedia.R;
import com.alihaydar.socialmedia.databinding.RowPostBinding;
import com.alihaydar.socialmedia.model.Post;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PostAdepter extends RecyclerView.Adapter<PostAdepter.PostHolder> {
    ArrayList<Post>arrayList;

    public PostAdepter(ArrayList<Post> arrayList) {
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public PostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RowPostBinding rowPostBinding=RowPostBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);

        return new PostHolder(rowPostBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull PostHolder holder, int position) {
        holder.binding.nameUsernameText.setText("@"+arrayList.get(position).userName);
        holder.binding.userCommentText.setText(arrayList.get(position).comment);
        holder.binding.postDateText.setText(arrayList.get(position).date.toString());
        Picasso.get().load(arrayList.get(position).image).into(holder.binding.userPostImage);
        if (arrayList.get(position).userPhoto != null) {
            Picasso.get().load(arrayList.get(position).userPhoto).into(holder.binding.userPhoto);
        } else {
            holder.binding.userPhoto.setImageResource(R.drawable.defaultpp);
        }


    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class PostHolder extends RecyclerView.ViewHolder{
        private RowPostBinding binding;
        public PostHolder(RowPostBinding rowPostBinding) {
            super(rowPostBinding.getRoot());
            this.binding=rowPostBinding;
        }
    }



}
