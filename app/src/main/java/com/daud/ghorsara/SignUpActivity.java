package com.daud.ghorsara;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity {
    private TextInputEditText nameEtdSup,emailEtdSup,passwordEtdSup,phoneEtSup ;
    private Button signUpnBtn;
    private TextView signInTv;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private ProgressBar progress;
    private String userImage = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        //Initialization
        initial();

        ////////////////////
        // SignUp OnClick //

        signUpnBtn.setOnClickListener(view2 -> {
            String nameInd = nameEtdSup.getText().toString();
            String phoneInd = phoneEtSup.getText().toString();
            String emailInd = emailEtdSup.getText().toString();
            String passwordInd = passwordEtdSup.getText().toString();

            //Chek Empty of Edittext
            if (nameInd.isEmpty() || phoneInd.isEmpty() || emailInd.isEmpty() || passwordInd.isEmpty()){
                Toast.makeText(SignUpActivity.this, "Invalid Value", Toast.LENGTH_SHORT).show();

            }else{
                //checking the validity of the email
                if(!android.util.Patterns.EMAIL_ADDRESS.matcher(emailInd).matches()) {
                    emailEtdSup.setError("Enter a valid email address");
                    emailEtdSup.requestFocus();
                    return;
                }
                //checking the validity of the password
                if(passwordInd.length() < 6) {
                    passwordEtdSup.setError("Enter at least 6 (six) character");
                    passwordEtdSup.requestFocus();
                    return;
                }
                /*HereMyCode*/
                // Firebase Auth
                progress.setVisibility(View.VISIBLE);
                firebaseAuth.createUserWithEmailAndPassword(emailInd,passwordInd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){

                            // For RealTime Database
                            String userId = firebaseAuth.getCurrentUser().getUid();
                            DatabaseReference dataRef = databaseReference.child("UserInfo").child(userId);
                            // For Add Data Into UserId Child
                            HashMap<String,Object> userIdData = new HashMap<>();
                            userIdData.put("UserName",nameInd);
                            userIdData.put("UserPhone",phoneInd);
                            userIdData.put("UserEmail",emailInd);
                            userIdData.put("UserImage",userImage);
                            userIdData.put("UserId",userId);

                            dataRef.setValue(userIdData).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        progress.setVisibility(View.GONE);
                                        Toast.makeText(SignUpActivity.this,"Data Added SuccessFully",Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(SignUpActivity.this,SignInActivity.class));
                                        finish();
                                    }else{
                                        progress.setVisibility(View.GONE);
                                        Toast.makeText(SignUpActivity.this,""+task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }else{
                            progress.setVisibility(View.GONE);
                            ////////if firebaseAuth Failure
                            if (task.getException() instanceof FirebaseAuthUserCollisionException){
                                emailEtdSup.setError("Email Already InUse");
                                emailEtdSup.requestFocus();
                                return;
                            }else{
                                emailEtdSup.setError("Error: "+task.getException().getMessage());
                                return;
                            }
                        }
                    }
                });
            }
        });

        //////////////////////////////
        //Already SignInTv OnClick //

        signInTv.setOnClickListener(view -> {
            startActivity(new Intent(SignUpActivity.this,SignInActivity.class));
            finish();
        });
    }

    //Finnish Activity On BackPressed
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
    /////////////////
    // Initialize //

    private void initial() {
        nameEtdSup = findViewById(R.id.nameEtSup);
        emailEtdSup = findViewById(R.id.emailEtSup);
        passwordEtdSup = findViewById(R.id.passwordEtSup);
        phoneEtSup = findViewById(R.id.phoneEtSup);
        signUpnBtn = findViewById(R.id.signUpBtn);
        signInTv = findViewById(R.id.signInTv);
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        sharedPreferences = getSharedPreferences("MyShared",MODE_PRIVATE);
        editor = sharedPreferences.edit();
        progress = findViewById(R.id.progress);
    }
}