package Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instaclone.MainActivity;
import com.example.instaclone.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import Fragments.ProfileViewFragment;
import Model.User;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder>{
    Context mContext;
    List<User> mUsers;
    boolean isFragment;
    FirebaseUser firebaseUser;

    public UserAdapter(Context mContext, List<User> mUsers, boolean isFragment) {
        this.mContext = mContext;
        this.mUsers = mUsers;
        this.isFragment = isFragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.user_item,parent,false);
        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        final User user=mUsers.get(position);
        holder.btnfollow.setVisibility(View.VISIBLE);
        holder.Username.setText(user.getUsername());
        Picasso.get().load(user.getImageUrl()).placeholder(R.mipmap.ic_launcher).into(holder.imageprofile);
        isFollowed(user.getId(),holder.btnfollow);
        if(user.getId().equals(firebaseUser.getUid()))
            holder.btnfollow.setVisibility(View.GONE);
        holder.btnfollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.btnfollow.getText().toString().equals("follow")){
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid()).child("following").child(user.getId()).setValue(true);
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(user.getId()).child("followers").child(firebaseUser.getUid()).setValue(true);
                }else {
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid()).child("following").child(user.getId()).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(user.getId()).child("followers").child(firebaseUser.getUid()).removeValue();

                }
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isFragment){
                    mContext.getSharedPreferences("Profile",Context.MODE_PRIVATE).edit().putString("profileID",user.getId()).apply();
                    ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new ProfileViewFragment()).commit();
                }else{
                    Intent intent=new Intent(mContext, MainActivity.class);
                    intent.putExtra("publisherID",user.getId());
                    mContext.startActivity(intent);
                }
            }
        });

    }

    private void isFollowed(final String id, final Button btnfollow) {
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid()).child("following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(id).exists())
                    btnfollow.setText("following");
                else
                    btnfollow.setText("follow");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public CircleImageView imageprofile;
        TextView Username;
        Button btnfollow;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageprofile= itemView.findViewById(R.id.image_profile);
            Username= itemView.findViewById(R.id.username);
            btnfollow= itemView.findViewById(R.id.btn_follow);
        }
    }
}
