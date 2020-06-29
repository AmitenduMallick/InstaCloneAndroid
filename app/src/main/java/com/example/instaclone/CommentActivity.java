package com.example.instaclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Adapter.CommentAdapter;
import Fragments.NotificationFragment;
import Model.Comment;
import Model.User;

public class CommentActivity extends AppCompatActivity {

    RecyclerView commentRecyclerView;
    CommentAdapter commentAdapter;
    List<Comment> commentList;
    EditText addComment;
    ImageView commentImageProfile;
    TextView commentPost;
    String postId;
    String authorId;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        Toolbar toolbar=findViewById(R.id.comment_toolbar);
        Intent intent=getIntent();
        postId=intent.getStringExtra("postId");
        authorId=intent.getStringExtra("authorId");
        commentRecyclerView=findViewById(R.id.comment_recyclerView);
        commentRecyclerView.setHasFixedSize(true);
        commentRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        commentList=new ArrayList<>();
        commentAdapter=new CommentAdapter(this,commentList,postId);
        commentRecyclerView.setAdapter(commentAdapter);

        getSupportActionBar().setTitle("Comments");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        addComment=findViewById(R.id.addCommment);
        commentImageProfile=findViewById(R.id.comment_image_profile);
        commentPost=findViewById(R.id.post_comment);


        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        getUserImage();
        commentPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(addComment.getText().toString())){
                    Toast.makeText(CommentActivity.this, "No comment added!!", Toast.LENGTH_SHORT).show();
                }else{
                    putComment();
                }
            }
        });
        getComments();
    }

    private void getComments() {
        FirebaseDatabase.getInstance().getReference().child("Comments").child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                commentList.clear();
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Comment comment=snapshot.getValue(Comment.class);
                    commentList.add(comment);
                }
                commentAdapter.notifyDataSetChanged();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void putComment() {
        HashMap<String, Object> map=new HashMap<>();
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("Comments").child(postId);
        String id=ref.push().getKey();
        map.put("id",id);
        map.put("comment",addComment.getText().toString());
        map.put("publisher",firebaseUser.getUid());
        addComment.setText("");
        ref.child(id).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(CommentActivity.this, "Comment added!!", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void getUserImage() {

        FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user=snapshot.getValue(User.class);
                if(user.getImageUrl().equals("default"))
                    commentImageProfile.setImageResource(R.mipmap.ic_launcher);
                else{
                    Picasso.get().load(user.getImageUrl()).into(commentImageProfile);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}