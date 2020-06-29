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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    EditText loginMailEditText;
    EditText loginPasswordEditText;
    Button loginScreenButton;
    FirebaseAuth auth;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginMailEditText=findViewById(R.id.loginMailEditText);
        loginPasswordEditText=findViewById(R.id.loginPasswordEditText);
        loginScreenButton=findViewById(R.id.loginScreenButton);
        auth=FirebaseAuth.getInstance();
        pd=new ProgressDialog(this);
        pd.setMessage("Logging in");

        loginScreenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mail=loginMailEditText.getText().toString();
                String password=loginPasswordEditText.getText().toString();
                signInUser(mail,password);
            }
        });
    }
    public void signInUser(String mail,String password){
        pd.show();
        auth.signInWithEmailAndPassword(mail,password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    pd.dismiss();
                    Toast.makeText(LoginActivity.this,"Login in Successful",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this,MainActivity.class));
                    finish();
                }else{
                    pd.dismiss();
                    Toast.makeText(LoginActivity.this,"Wrong Credentials!!",Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
    }


}