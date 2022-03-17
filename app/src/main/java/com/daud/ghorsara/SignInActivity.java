package com.daud.ghorsara;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.text.MessagePattern;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.util.HashMap;

public class SignInActivity extends AppCompatActivity {
    private TextView signInTv,createAcTv;
    private TextInputEditText emailEt,passwordEt;
    private MaterialButton Btn;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private ProgressBar progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        //Initialization
        initial();

        //Shared preference get logged in or not value
        String LogInStatus = sharedPreferences.getString("LogInStatus","");

        if (LogInStatus.equals("true")){
            Intent intent = new Intent(SignInActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        }else{

             ////////////////////
            // SignIn OnClick //

            Btn.setOnClickListener(view -> {
                String emailIn = emailEt.getText().toString();
                String passwordIn = passwordEt.getText().toString();
                //Chek Edit text Empty or Not
                if (emailIn.isEmpty()||passwordIn.isEmpty()){
                    Toast.makeText(this, "Empty Value", Toast.LENGTH_SHORT).show();

                } else {
                    //checking the validity of the email
                    if(!android.util.Patterns.EMAIL_ADDRESS.matcher(emailIn).matches()) {
                        emailEt.setError("Enter a valid email address");
                        emailEt.requestFocus();
                        return;
                    }
                    //checking the validity of the password
                    if(passwordIn.length() < 6) {
                        passwordEt.setError("Enter at least 6 (six) character");
                        passwordEt.requestFocus();
                        return;
                    }

                    /*HereMyCode*/
                    progress.setVisibility(View.VISIBLE);
                    firebaseAuth.signInWithEmailAndPassword(emailIn,passwordIn).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                progress.setVisibility(View.GONE);
                                //Alert Dialog for asking save pass or not for shared preference tag to auto login
                                AlertDialog.Builder builder = new AlertDialog.Builder(SignInActivity.this);
                                builder.setTitle("Login With One Tap !");
                                builder.setMessage("Do You Want To Save Your Password ?");
                                builder.setNegativeButton("No", (dialogInterface, i) -> {

                                    Intent intent = new Intent(SignInActivity.this,MainActivity.class);
                                    ///////
                                    startActivity(intent);
                                    finish();
                                });

                                builder.setPositiveButton("Yes", (dialogInterface, i) -> {
                                    editor.putString("LogInStatus","true");
                                    editor.commit();
                                    Toast.makeText(SignInActivity.this, "Password Saved", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(SignInActivity.this,MainActivity.class);
                                    ///////
                                    startActivity(intent);
                                    finish();
                                });
                                builder.show();
                            } else{
                                progress.setVisibility(View.GONE);
                                //Login failure code
                                Toast.makeText(SignInActivity.this,"Error: "+task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            });
        }

        /////////////////////
        // SignUp OnClick //

        createAcTv.setOnClickListener(view -> {
            startActivity(new Intent(SignInActivity.this, SignUpActivity.class));
        });
    }

    /////////////////
    // Initialize //

    private void initial() {
        signInTv = findViewById(R.id.signInTv);
        createAcTv = findViewById(R.id.createAcTv);
        emailEt = findViewById(R.id.emailEt);
        passwordEt = findViewById(R.id.passwordEt);
        Btn = findViewById(R.id.signInBtn);
        sharedPreferences = getSharedPreferences("MyShared",MODE_PRIVATE);
        editor = sharedPreferences.edit();
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        progress = findViewById(R.id.progress);
    }
}