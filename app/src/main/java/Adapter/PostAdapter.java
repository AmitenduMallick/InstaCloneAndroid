package Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instaclone.CommentActivity;
import com.example.instaclone.FollowersActivity;
import com.example.instaclone.ProfileViewingActivity;
import com.example.instaclone.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import Fragments.ProfileFragment;
import Fragments.ProfileViewFragment;
import Model.Post;
import Model.User;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
    private Context mContext;
    private List<Post> mPosts;
    private FirebaseUser firebaseUser;

    public PostAdapter(Context mContext, List<Post> mPosts) {
        this.mContext = mContext;
        this.mPosts = mPosts;
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.post_item,parent,false);
        return new PostAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        final Post post=mPosts.get(position);
        Picasso.get().load(post.getImageUrl()).into(holder.postImage);
        holder.description.setText(post.getDescription());

        FirebaseDatabase.getInstance().getReference().child("Users").child(post.getPublisher()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user=dataSnapshot.getValue(User.class);
                if(user.getImageUrl().equals("default")){
                    holder.profileImage.setImageResource(R.mipmap.ic_launcher);
                }
                else {
                    Picasso.get().load(user.getImageUrl()).into(holder.profileImage);
                }
                holder.usernameHome.setText(user.getUsername());
                holder.author.setText(user.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        isLiked(post.getPostId(),holder.like);
        numberOfLikes(post.getPostId(),holder.noOfLikes);
        getComments(post.getPostId(),holder.noOfComments);
        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.like.getTag().equals("like")){
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getPostId()).child(firebaseUser.getUid()).setValue(true);
                }else{
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getPostId()).child(firebaseUser.getUid()).removeValue();
                }
            }
        });
        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mContext, CommentActivity.class);
                intent.putExtra("postId",post.getPostId());
                intent.putExtra("authorId",post.getPublisher());
                mContext.startActivity(intent);
            }
        });
        holder.noOfComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mContext, CommentActivity.class);
                intent.putExtra("postId",post.getPostId());
                intent.putExtra("authorId",post.getPublisher());
                mContext.startActivity(intent);

            }
        });
        holder.profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.getSharedPreferences("Profile",Context.MODE_PRIVATE).edit().putString("profileID",post.getPublisher()).apply();
                //((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new ProfileViewFragment()).commit();
                mContext.startActivity(new Intent(mContext,ProfileViewingActivity.class));




            }
        });
        holder.usernameHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.getSharedPreferences("Profile",Context.MODE_PRIVATE).edit().putString("profileID",post.getPublisher()).apply();
                //((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new ProfileViewFragment()).commit();
                //mContext.startActivity(new Intent(mContext.getApplicationContext(), ProfileViewActivity.class));\
                mContext.startActivity(new Intent(mContext,ProfileViewingActivity.class));


            }
        });

        holder.noOfLikes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mContext, FollowersActivity.class);
                intent.putExtra("id",post.getPublisher());
                intent.putExtra("title","likes");
                mContext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView profileImage;
        public TextView usernameHome;
        public ImageView like;
        public ImageView comment;
        public ImageView postImage;
        public TextView noOfLikes;
        public TextView author;
        public TextView noOfComments;
        public TextView description;
        public Activity contexting;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImage=itemView.findViewById(R.id.profile_image);
            usernameHome=itemView.findViewById(R.id.username_home);
            like=itemView.findViewById(R.id.like);
            comment=itemView.findViewById(R.id.comment);
            postImage=itemView.findViewById(R.id.post_image);
            noOfLikes=itemView.findViewById(R.id.no_of_likes);
            author=itemView.findViewById(R.id.author);
            noOfComments=itemView.findViewById(R.id.no_of_comments);
            description=itemView.findViewById(R.id.descriptions);

        }
    }
    private void isLiked(String postId, final ImageView imageView){
        FirebaseDatabase.getInstance().getReference().child("Likes").child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(firebaseUser.getUid()).exists()){
                    imageView.setImageResource(R.drawable.ic_liked);
                    imageView.setTag("liked");

                }else{
                    imageView.setImageResource(R.drawable.ic_likes);
                    imageView.setTag("like");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void numberOfLikes(String postId, final TextView textView){
        FirebaseDatabase.getInstance().getReference().child("Likes").child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                textView.setText(snapshot.getChildrenCount()+" likes");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void getComments(String postId, final TextView textView){
        FirebaseDatabase.getInstance().getReference().child("Comments").child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                textView.setText("View all "+snapshot.getChildrenCount()+" comments");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
