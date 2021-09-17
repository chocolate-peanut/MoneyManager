package com.example.moneymanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class changePw extends AppCompatActivity {

    private FirebaseAuth firebase;
    private Button nBtnSend,btnBack;
    private EditText Useremail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pw);
        Useremail = findViewById(R.id.re_email);
        nBtnSend = findViewById(R.id.btnsend);
        firebase = FirebaseAuth.getInstance();
        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), HomePage.class);
                startActivity(intent);
            }
        });

        nBtnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebase.sendPasswordResetEmail(Useremail.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(changePw.this, "Reset Link Sent To your Email", Toast.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(changePw.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
    }}