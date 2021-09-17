package com.example.moneymanager;

import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.google.android.gms.safetynet.SafetyNet;
import com.google.android.gms.safetynet.SafetyNetApi;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import java.util.Random;

public class Register extends AppCompatActivity {
    EditText fullName, nEmail,passWord, passWord2;
    Button signUp, backLogin, autoGeneratePass;
    TextView passWordStr;
    View root;
    FirebaseAuth firebase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        fullName = findViewById(R.id.editTextTextPersonName);
        nEmail = findViewById(R.id.email);
        passWord = findViewById(R.id.password);
        passWord2 = findViewById(R.id.password2);
        signUp = findViewById(R.id.btnSignIn);
        backLogin = findViewById(R.id.backToSignIn);
        passWordStr = findViewById(R.id.passStr);
        autoGeneratePass = findViewById(R.id.autoGenerate);
        //root = findViewById(R.id.root);
        firebase = FirebaseAuth.getInstance();

        if(firebase.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), HomePage.class));
            finish();
        }
        passWord.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                calPasswordStrength(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        signUp.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String name = fullName.getText().toString().trim();
                String email = nEmail.getText().toString().trim();
                String password = passWord.getText().toString().trim();
                String password2 = passWord2.getText().toString().trim();
                String passwordStrength = passWordStr.getText().toString();

                if (TextUtils.isEmpty(name)){
                    fullName.setError("Your Full Name is Required.");
                    return;
                }
                if (TextUtils.isEmpty(email)){
                    nEmail.setError("Email is Required.");
                    return;
                }
                if (TextUtils.isEmpty(password)){
                    passWord.setError("Password is Required.");
                    return;
                }
                if (TextUtils.isEmpty(password2)){
                    passWord2.setError("Confirmed Password is Required.");
                    return;
                }

                if (passwordStrength.equals("Strong") == false && passwordStrength.equals("Very Strong") == false){
                    passWord.setError("Please enter password that has strong or very strong password strength. " +
                            "You can include at least one number, one uppercase letter and one lowercase letter in the password in order " +
                            "to get the strong password.");
                    return;
                }
                if (password.length() < 8){
                    passWord.setError("Password must be >= 8 characters");
                    return;
                }
                if (!password.equals(password2)){
                    passWord2.setError("Passwords did not match!");
                    return;
                }

                firebase.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            ProcessRegister();
                            Toast.makeText(getApplicationContext(),"New Account is Created Successfully ", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(getApplicationContext(),"New Account is Not Created " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }

        });

        backLogin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });

        autoGeneratePass.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                int length = 4;
                passWord.setText(GetPassword(length));
            }
        });

    }
    private void calPasswordStrength(String str) {
        // Now, we need to define a PasswordStrength enum
        // with a calculate static method returning the password strength
        CalcPassStr passwordStrength = CalcPassStr.calculate(str);
        passWordStr.setText(passwordStrength.msg);
        //root.setBackgroundColor(passwordStrength.color);
    }
    public String GetPassword(int length){
        char[] chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
        char[] chars2 = "abcdefghijklmnopqrstuvwxyz".toCharArray();
        char[] chars3 = "0123456789".toCharArray();
        StringBuilder stringBuilder = new StringBuilder();
        Random rand = new Random();
        for (int i =0; i < length; i++){
            char c = chars[rand.nextInt(chars.length)];
            stringBuilder.append(c);
            c = chars2[rand.nextInt(chars2.length)];
            stringBuilder.append(c);
            c = chars3[rand.nextInt(chars3.length)];
            stringBuilder.append(c);
        }
        return stringBuilder.toString();
    }
    public void ShowHidePass(@NonNull View view) {

        if(view.getId()==R.id.showPass){
            if(passWord.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())){
                ((ImageView)(view)).setImageResource(R.drawable.ic_viewpass);
                //Show Password
                passWord.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            }
            else{
                ((ImageView)(view)).setImageResource(R.drawable.ic_viewpass_off);
                //Hide Password
                passWord.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        }
    }

    private void ProcessRegister(){
        SafetyNet.getClient(Register.this).verifyWithRecaptcha("6Le31SobAAAAACWnckgnGkJsQsNe9ZBTAMweTQY5")
                .addOnSuccessListener(new OnSuccessListener<SafetyNetApi.RecaptchaTokenResponse>() {
                    @Override
                    public void onSuccess(SafetyNetApi.RecaptchaTokenResponse recaptchaTokenResponse) {
                        String captchaToken = recaptchaTokenResponse.getTokenResult();

                        if (captchaToken != null) {
                            if (!captchaToken.isEmpty()) {
                                processRegisterStep(captchaToken, passWord.getText().toString(), passWord2.getText().toString());
                            }
                            else{
                                Toast.makeText(Register.this, "Invalid Captcha Response", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Register.this, "Fail to load Captcha", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void processRegisterStep(String captchaToken, String s, String toString) {
        Log.d("CAPTCHA TOKEN", ""+captchaToken);
    }

}