package Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instaclone.PostDetailViewingActivity;
import com.example.instaclone.ProfileViewingActivity;
import com.example.instaclone.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import Fragments.PostDetailFragment;
import Model.Post;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.ViewHolder> {
    public Context mContext;
    public List<Post> mposts;

    public PhotoAdapter(Context mContext, List<Post> mposts) {
        this.mContext = mContext;
        this.mposts = mposts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(mContext).inflate(R.layout.pictures_item,parent,false);
        return new PhotoAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Post post=mposts.get(position);
        Picasso.get().load(post.getImageUrl()).placeholder(R.mipmap.ic_launcher).into(holder.profilerImage);

        holder.profilerImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit().putString("postId",post.getPostId()).apply();
                //((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new PostDetailFragment()).commit();
                //mContext.startActivity(new Intent(mContext,PostDetailViewingActivity.class));
                Intent intent=new Intent(mContext,PostDetailViewingActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);




            }
        });

    }

    @Override
    public int getItemCount() {
        return mposts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView profilerImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profilerImage=itemView.findViewById(R.id.profiler_posts);
        }
    }
}
