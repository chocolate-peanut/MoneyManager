package com.example.moneymanager;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.DialogInterface;
import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private EditText email;
    private EditText password;
    private TextView forgotTextLink;
    private Button btnSignIn, goRegister;
    private FirebaseAuth firebase;
    int attempt = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebase = FirebaseAuth.getInstance(); //point to rootNode
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        btnSignIn = (Button) findViewById(R.id.btnSignIn);
        goRegister = (Button) findViewById(R.id.goRegister);
        forgotTextLink = (TextView) findViewById(R.id.nEtForgot);
        //existing users don't have to sign in again
        if (firebase.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), HomePage.class));
        }

        //Sign In Activity
        //@Override
        //public void onClick (View v){
        String em = email.getText().toString().trim();
        String pw = password.getText().toString().trim();

        //forgotTextLink = findViewById(R.id.nEtForgot);
        forgotTextLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText resetMail = new EditText(v.getContext());

                final AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(v.getContext());
                passwordResetDialog.setTitle("Reset Password?");
                passwordResetDialog.setMessage("Enter your Email to Received Reset Link.");
                passwordResetDialog.setView(resetMail);

                passwordResetDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String mail = resetMail.getText().toString().trim();
                        firebase.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(MainActivity.this, "Reset Link Sent To your Email", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MainActivity.this, "Error! Reset Link is not Sent" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
                ((AlertDialog.Builder) passwordResetDialog).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                passwordResetDialog.create().show();
            }
        });
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String em = email.getText().toString().trim();
                String pw = password.getText().toString().trim();

                //input validation
                if(TextUtils.isEmpty(em)) {
                    email.setError("Please enter Email");
                    return;
                }
                if(TextUtils.isEmpty(pw)) {
                    password.setError("Please enter Password");
                    return;
                }

                firebase.signInWithEmailAndPassword(em,pw).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(),"Sign In Successful", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), HomePage.class);
                            startActivity(intent);
                        }else if(attempt == 3){
                            Button btn_login = findViewById(R.id.btnSignIn);
                            btn_login.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(MainActivity.this,changePw.class);
                                    startActivity(intent);
                                }
                            });
                        }
                        else {
                            Toast.makeText(getApplicationContext(),"Sign In Failed", Toast.LENGTH_SHORT).show();
                            attempt++;
                        }
                    }
                });
            }
        });


        goRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Register.class));
            }
        });
    }
}