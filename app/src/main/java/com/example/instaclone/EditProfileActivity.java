package com.example.instaclone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.net.URI;
import java.util.HashMap;

import Model.User;
import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity {

    private ImageView editProfileClose;
    private TextView editProfileSave;
    private CircleImageView editProfileImage;
    private TextView editProfileChangePhoto;
    private MaterialEditText editProfileUsername;
    private MaterialEditText editProfileBio;
    private FirebaseUser firebaseUser;
    private Uri mImageUri;
    private StorageTask uploadTask;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);


        editProfileClose=findViewById(R.id.edit_profile_close);
        editProfileSave=findViewById(R.id.edit_profile_save);
        editProfileImage=findViewById(R.id.edit_profile_image);
        editProfileChangePhoto=findViewById(R.id.edit_profile_changePhoto);
        editProfileUsername=findViewById(R.id.edit_profile_username);
        editProfileBio=findViewById(R.id.edit_profile_bio);
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        storageReference= FirebaseStorage.getInstance().getReference().child("Uploads");
        FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user=snapshot.getValue(User.class);
                editProfileUsername.setText(user.getUsername());
                editProfileBio.setText(user.getBio());
                if(user.getImageUrl().equals("default")){
                    editProfileImage.setImageResource(R.mipmap.ic_launcher);
                }else {
                    Picasso.get().load(user.getImageUrl()).into(editProfileImage);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        editProfileClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        editProfileChangePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity().setCropShape(CropImageView.CropShape.OVAL).start(EditProfileActivity.this);
            }
        });
        editProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity().setCropShape(CropImageView.CropShape.OVAL).start(EditProfileActivity.this);
            }
        });
        editProfileSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile();
                finish();
            }
        });
    }

    private void updateProfile() {
        HashMap<String,Object> map=new HashMap<>();
        map.put("Username",editProfileUsername.getText().toString());
        map.put("bio",editProfileBio.getText().toString());

        FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser.getUid()).updateChildren(map);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE&&resultCode==RESULT_OK){
            CropImage.ActivityResult result=CropImage.getActivityResult(data);
            mImageUri=result.getUri();
            uploadImage();
        }else{
            Toast.makeText(this, "Something went wrong!!", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadImage() {
        final ProgressDialog pd=new ProgressDialog(this);
        pd.setMessage("Uploading");
        pd.show();
        if(mImageUri!=null){
            final StorageReference fileRef=storageReference.child(System.currentTimeMillis()+".jpeg");
            uploadTask=fileRef.putFile(mImageUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if(!task.isSuccessful()){
                        throw task.getException();
                    }
                    return fileRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()){
                        Uri downloadUri=task.getResult();
                        String url=downloadUri.toString();
                        FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser.getUid()).child("ImageUrl").setValue(url);
                        pd.dismiss();
                    }else{
                        Toast.makeText(EditProfileActivity.this, "Upload failed", Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }else{
            Toast.makeText(this, "No Image Selected", Toast.LENGTH_SHORT).show();
        }
    }
}