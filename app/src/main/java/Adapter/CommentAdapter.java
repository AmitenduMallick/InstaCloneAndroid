package Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instaclone.MainActivity;
import com.example.instaclone.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import Model.Comment;
import Model.User;
import de.hdodenhof.circleimageview.CircleImageView;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
    Context mContext;
    List<Comment> mComments;
    FirebaseUser fuser;
    String postId;

    public CommentAdapter(Context mContext, List<Comment> mComments,String postId) {
        this.mContext = mContext;
        this.mComments = mComments;
        this.postId=postId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.comment_item,parent,false);
        return new CommentAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        fuser= FirebaseAuth.getInstance().getCurrentUser();
        final Comment comment=mComments.get(position);
        holder.commented.setText(comment.getComment());
        FirebaseDatabase.getInstance().getReference().child("Users").child(comment.getPublisher()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user=snapshot.getValue(User.class);
                holder.commentedUsername.setText(user.getUsername());
                if(user.getImageUrl().equals("default"))
                    holder.commentedImageProfile.setImageResource(R.mipmap.ic_launcher);
                else{
                    Picasso.get().load(user.getImageUrl()).into(holder.commentedImageProfile);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        holder.commentedUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mContext, MainActivity.class);
                intent.putExtra("publisherID",comment.getPublisher());
                mContext.startActivity(intent);
            }
        });
        holder.commentedImageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mContext, MainActivity.class);
                intent.putExtra("publisherID",comment.getPublisher());
                mContext.startActivity(intent);
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(comment.getPublisher().endsWith(fuser.getUid())){
                    AlertDialog ad=new AlertDialog.Builder(mContext).create();
                    ad.setTitle("Do you want to delete this comment");
                    ad.setButton(AlertDialog.BUTTON_NEGATIVE, "No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    ad.setButton(AlertDialog.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(final DialogInterface dialog, int which) {
                            FirebaseDatabase.getInstance().getReference().child("Comments").child(postId).child(comment.getId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(mContext, "Comment deleted successfully!!", Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                    }
                                }
                            });

                        }
                    });
                    ad.show();
                }
                return true;
            }
        });

    }

    @Override
    public int getItemCount() {
        return mComments.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        CircleImageView commentedImageProfile;
        TextView commentedUsername;
        TextView commented;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            commentedImageProfile=itemView.findViewById(R.id.commented_image_profile);
            commentedUsername=itemView.findViewById(R.id.commented_username);
            commented=itemView.findViewById(R.id.commented);
        }
    }
}
