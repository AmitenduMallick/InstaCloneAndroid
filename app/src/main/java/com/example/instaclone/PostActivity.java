package com.example.instaclone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

public class PostActivity extends AppCompatActivity {
    String imageUrl;
    Uri imageuri;
    ImageView close;
    ImageView imageAdded;
    TextView post;
    EditText description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        close=findViewById(R.id.close);
        imageAdded=findViewById(R.id.image_added);
        post=findViewById(R.id.post);
        description=findViewById(R.id.description);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PostActivity.this,MainActivity.class));
                finish();
            }
        });
        CropImage.activity().start(PostActivity.this);
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upload();
            }
        });

    }
    public void upload(){
        final ProgressDialog pd=new ProgressDialog(this);
        pd.setMessage("Uploading");
        pd.show();
        if(imageuri!=null){
            final StorageReference filepath= FirebaseStorage.getInstance().getReference("Posts").child(System.currentTimeMillis()+"."+getFileExtension(imageuri));
            StorageTask uploadTask=filepath.putFile(imageuri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if(!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return filepath.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    Uri downloadUri=task.getResult();
                    imageUrl=downloadUri.toString();
                    DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Posts");
                    String postId=ref.push().getKey();
                    HashMap<String,Object> map=new HashMap<>();
                    map.put("postId",postId);
                    map.put("ImageUrl",imageUrl);
                    map.put("description",description.getText().toString());
                    map.put("publisher", FirebaseAuth.getInstance().getCurrentUser().getUid());
                    ref.child(postId).setValue(map);
                    pd.dismiss();
                    startActivity(new Intent(PostActivity.this,MainActivity.class));
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(PostActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            Toast.makeText(PostActivity.this, "No image was selected", Toast.LENGTH_SHORT).show();
        }
    }

    private String getFileExtension(Uri uri) {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(this.getContentResolver().getType(uri));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE&&resultCode==RESULT_OK){
            CropImage.ActivityResult result=CropImage.getActivityResult(data);
            imageuri=result.getUri();
            imageAdded.setImageURI(imageuri);
        }else{
            Toast.makeText(PostActivity.this, "Try again", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(PostActivity.this,MainActivity.class));
            finish();
        }
    }
}