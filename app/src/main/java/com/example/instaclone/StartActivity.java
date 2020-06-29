package com.example.instaclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class StartActivity extends AppCompatActivity {
    EditText registerUsernameEditText;
    EditText registerMailEditText;
    EditText registerPasswordEditText;
    Button registerButton;
    Button loginButton;
    FirebaseAuth auth;
    ProgressDialog pd;
    DatabaseReference mRootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        registerUsernameEditText=findViewById(R.id.registerUsernameEditText);
        registerMailEditText=findViewById(R.id.registerMailEditText);
        registerPasswordEditText=findViewById(R.id.registerPasswordEditText);
        registerButton=findViewById(R.id.registerButton);
        loginButton=findViewById(R.id.loginButton);
        auth=FirebaseAuth.getInstance();
        mRootRef= FirebaseDatabase.getInstance().getReference();
        pd=new ProgressDialog(this);
        if(auth.getCurrentUser()!=null){
            startActivity(new Intent(StartActivity.this,MainActivity.class));
            finish();
        }
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mail=registerMailEditText.getText().toString();
                String password=registerPasswordEditText.getText().toString();
                String username=registerUsernameEditText.getText().toString();
                registerUser(mail,password,username);

            }
        });
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StartActivity.this,LoginActivity.class));
            }
        });
    }
    public void registerUser(final String mail, String password, final String username){
        pd.setMessage("Please wait");
        pd.show();
        auth.createUserWithEmailAndPassword(mail, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                HashMap<String,Object> map=new HashMap<>();
                map.put("Username",username);
                map.put("Email",mail);
                map.put("id",auth.getCurrentUser().getUid());
                map.put("bio","");
                map.put("ImageUrl","default");
                mRootRef.child("Users").child(auth.getCurrentUser().getUid()).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(StartActivity.this,"Update your profile for a better experience!",Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(StartActivity.this,MainActivity.class));
                            finish();
                        }
                    }
                });


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(StartActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}