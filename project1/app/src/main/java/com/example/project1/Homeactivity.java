package com.example.project1;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


import android.content.DialogInterface;
import android.content.Intent;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class Homeactivity extends AppCompatActivity {

TextView signup,Forgot;
ProgressBar progressBar;
EditText email,password;
Button login;
FirebaseAuth fAuth;
int count=0;

    @Override
    public void onBackPressed() {
        count++;
        if(count!=2)
            Toast.makeText(this, "Press Again to exit", Toast.LENGTH_SHORT).show();
        else finishAffinity();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_homeactivity);
        signup = findViewById(R.id.signupTv);
        email = findViewById(R.id.inputEmail);
        progressBar  = findViewById(R.id.progressBar);
        password = findViewById(R.id.inputPass);
        login = findViewById(R.id.loginBtn);
        Forgot = findViewById(R.id.ForgotPass);
        fAuth = FirebaseAuth.getInstance();


    Forgot.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            EditText reset = new EditText(view.getContext());
            AlertDialog.Builder password = new AlertDialog.Builder(view.getContext());
            password.setTitle("Forgot Password");
            password.setMessage("Enter mail Id to send link..");
            password.setView(reset);

            password.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                String mail = reset.getText().toString();
                fAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(Homeactivity.this, "Reset Link sent to the "+mail, Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Homeactivity.this, "Failed to sent to the "+mail, Toast.LENGTH_SHORT).show();

                    }
                });
                }

            }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            password.create().show();

        }
    });



        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String Email = email.getText().toString();
                String Pass = password.getText().toString();

                if (Email.isEmpty() || Pass.isEmpty()) {
                    email.setError("required!!");
                    password.setError("required!!");
                }
                else
                {
                    progressBar.setVisibility(View.VISIBLE);
                    //Authenticating......
                    fAuth.signInWithEmailAndPassword(Email,Pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful())
                            {
                                Toast.makeText(Homeactivity.this, "Login succesfull", Toast.LENGTH_SHORT).show();

                                Intent loginIntent = new Intent(Homeactivity.this,Homepage.class);
                                startActivity(loginIntent);
                                progressBar.setVisibility(View.GONE);
                                email.setText("");
                                password.setText("");

                            }
                            else
                            {
                                Toast.makeText(Homeactivity.this, "Error !!"+" "+task.getException(), Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    });
                }

            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //
                Intent intent = new Intent(Homeactivity.this, registerActivity.class);
                startActivity(intent);
            }
        });

    }



    }


