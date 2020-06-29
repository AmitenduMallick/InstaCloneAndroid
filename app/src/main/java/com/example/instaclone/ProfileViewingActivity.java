package com.example.instaclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import Adapter.PhotoAdapter;
import Model.Post;
import Model.User;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileViewingActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PhotoAdapter photoAdapter;
    private List<Post> myposts;
    private CircleImageView profilerImage;
    private TextView profilerUsername;
    private TextView profilerPosts;
    private TextView profilerFollowers;
    private TextView profilerFollowing;
    private ImageView profilerMypics;
    private TextView profilerFullname;
    private TextView profilerBio;
    private Button profiler_edit;
    private FirebaseUser firebaseUser;
    String profileId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_viewing);
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        String data=getSharedPreferences("Profile", Context.MODE_PRIVATE).getString("profileID","none");
        if(data.equals("none")){
            profileId=firebaseUser.getUid();
        }else {
            profileId = data;
        }

        profilerImage=findViewById(R.id.profiler_viewing_image);
        profilerUsername=findViewById(R.id.profiler_viewing_username);
        profilerPosts=findViewById(R.id.profiler_viewing_posts);
        profilerFollowers=findViewById(R.id.profiler_viewing_followers);
        profilerFollowing=findViewById(R.id.profiler_viewing_following);
        profilerMypics=findViewById(R.id.profiler_viewing_my_pictures);
        profilerFullname=findViewById(R.id.profiler_viewing_fullname);
        profiler_edit=findViewById(R.id.profiler_viewing_edit);
        profilerBio=findViewById(R.id.profiler_viewing_bio);
        recyclerView=findViewById(R.id.profiler_viewing_recycler_view_pictures);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(),3));
        myposts=new ArrayList<>();
        photoAdapter=new PhotoAdapter(getApplicationContext(),myposts);
        recyclerView.setAdapter(photoAdapter);


        if(profileId.equals(firebaseUser.getUid())){
            profiler_edit.setText("Edit profile");
        }else{
            checkFollowingStatus();

        }

        userInfo();
        getFollowersandFollowingCount();
        getPostCount();
        getPhotos();
        profiler_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String btnText=profiler_edit.getText().toString();
                if(btnText.equals("Edit profile")){
                    startActivity(new Intent(getApplicationContext(),EditProfileActivity.class));
                }else{
                    if(btnText.equals("follow")){
                        FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid()).child("following").child(profileId).setValue(true);
                        FirebaseDatabase.getInstance().getReference().child("Follow").child(profileId).child("followers").child(firebaseUser.getUid()).setValue(true);
                    }else{
                        FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid()).child("following").child(profileId).removeValue();
                        FirebaseDatabase.getInstance().getReference().child("Follow").child(profileId).child("followers").child(firebaseUser.getUid()).removeValue();

                    }
                }
            }
        });
        //getContext().getSharedPreferences("PROFILE",Context.MODE_PRIVATE).edit().remove("profileId").commit();
        profilerFollowers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(), FollowersActivity.class);
                intent.putExtra("id",profileId);
                intent.putExtra("title","followers");
                startActivity(intent);
            }
        });
        profilerFollowing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(), FollowersActivity.class);
                intent.putExtra("id",profileId);
                intent.putExtra("title","followings");
                startActivity(intent);
            }
        });


    }

    private void getPhotos() {
        FirebaseDatabase.getInstance().getReference().child("Posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                myposts.clear();
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Post post=snapshot.getValue(Post.class);

                    if(post.getPublisher().equals(profileId)){
                        myposts.add(post);
                    }
                }
                Collections.reverse(myposts);
                photoAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getPostCount() {
        FirebaseDatabase.getInstance().getReference().child("Posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int counter=0;
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Post post=snapshot.getValue(Post.class);
                    if(post.getPublisher().equals(profileId))
                        counter++;
                }
                profilerPosts.setText(String.valueOf(counter));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getFollowersandFollowingCount() {
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("Follow").child(profileId);
        ref.child("followers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                profilerFollowers.setText(snapshot.getChildrenCount()+"");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        ref.child("following").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                profilerFollowing.setText(""+snapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void userInfo() {
        FirebaseDatabase.getInstance().getReference().child("Users").child(profileId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user=dataSnapshot.getValue(User.class);
                if(user.getImageUrl().equals("default"))
                    profilerImage.setImageResource(R.mipmap.ic_launcher);
                else{
                    Picasso.get().load(user.getImageUrl()).into(profilerImage);
                }
                profilerUsername.setText(user.getUsername());
                profilerFullname.setText(user.getUsername());
                profilerBio.setText(user.getBio());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void checkFollowingStatus(){
        FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid()).child("following").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(profileId).exists()){
                    profiler_edit.setText("following");
                }else{
                    profiler_edit.setText("follow");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}