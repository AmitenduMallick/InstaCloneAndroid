package Fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.instaclone.R;
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

public class ProfileViewFragment extends Fragment {

    private RecyclerView recyclerView;
    private PhotoAdapter photoAdapter;
    private List<Post> myposts;
    private CircleImageView profilerViewImage;
    private TextView profilerViewUsername;
    private TextView profilerViewPosts;
    private TextView profilerViewFollowers;
    private TextView profilerViewFollowing;
    private ImageView profilerViewMypics;
    private TextView profilerViewFullname;
    private TextView profilerViewBio;
    private Button profilerViewedit;
    private FirebaseUser firebaseUser;
    String profileId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_profile_view, container, false);
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        String data=getContext().getSharedPreferences("Profile", Context.MODE_PRIVATE).getString("profileID","none");
        if(data.equals("none")){
            profileId=firebaseUser.getUid();
        }else {
            profileId = data;
        }
        profilerViewImage=view.findViewById(R.id.profiler_view_image);
        profilerViewUsername=view.findViewById(R.id.profiler_view_username);
        profilerViewPosts=view.findViewById(R.id.profiler_view_posts);
        profilerViewFollowers=view.findViewById(R.id.profiler_view_followers);
        profilerViewFollowing=view.findViewById(R.id.profiler_view_following);
        profilerViewMypics=view.findViewById(R.id.profiler_view_my_pictures);
        profilerViewFullname=view.findViewById(R.id.profiler_view_fullname);
        profilerViewedit=view.findViewById(R.id.profiler_view_edit);
        profilerViewBio=view.findViewById(R.id.profiler_view_bio);
        recyclerView=view.findViewById(R.id.profiler_view_recycler_view_pictures);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),3));
        myposts=new ArrayList<>();
        photoAdapter=new PhotoAdapter(getContext(),myposts);
        recyclerView.setAdapter(photoAdapter);
        if(profileId.equals(firebaseUser.getUid())){
            profilerViewedit.setText("Edit profile");
        }else{
            checkFollowingStatus();

        }

        userInfo();
        getFollowersandFollowingCount();
        getPostCount();
        getPhotos();
        profilerViewedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String btnText=profilerViewedit.getText().toString();
                if(btnText.equals("Edit profile")){
                    //go to edit profile
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

        return view;
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
                profilerViewPosts.setText(String.valueOf(counter));
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
                profilerViewFollowers.setText(snapshot.getChildrenCount()+"");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        ref.child("following").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                profilerViewFollowing.setText(""+snapshot.getChildrenCount());
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
                    profilerViewImage.setImageResource(R.mipmap.ic_launcher);
                else{
                    Picasso.get().load(user.getImageUrl()).into(profilerViewImage);
                }
                profilerViewUsername.setText(user.getUsername());
                profilerViewFullname.setText(user.getUsername());
                profilerViewBio.setText(user.getBio());

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
                    profilerViewedit.setText("following");
                }else{
                    profilerViewedit.setText("follow");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}