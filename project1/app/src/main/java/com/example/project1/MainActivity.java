package com.example.project1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity{
    private static int SPLASH_TIME_OUT = 1000;
    ImageView img,logo;
    Context context;
    Button retryBTn;
    FirebaseAuth Auth;
    @Override
    public void onBackPressed() {
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Auth = FirebaseAuth.getInstance();
        img=findViewById(R.id.img);
        logo = findViewById(R.id.imageView2);
        retryBTn = findViewById(R.id.retryBtn);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isConnected()) {
                    img.setVisibility(View.VISIBLE);
                    retryBTn.setVisibility(View.VISIBLE);
                    logo.setVisibility(View.GONE);
                    Toast.makeText(MainActivity.this, "NO INTERNET TRY AGAIN BY CLOSING", Toast.LENGTH_SHORT).show();
                } else if(isConnected()) {
                    Toast.makeText(MainActivity.this, "INTERNET ACCESS", Toast.LENGTH_SHORT).show();
                    if (Auth.getCurrentUser() != null) {
                        Intent homeIntent = new Intent(MainActivity.this, Homepage.class);
                        startActivity(homeIntent);
                    }
                    else {
                        Intent homeIntent = new Intent(MainActivity.this, Homeactivity.class);
                        startActivity(homeIntent);
                    }
                }
            }
        }, SPLASH_TIME_OUT);
    }
    public boolean isConnected()
    {
        ConnectivityManager connectivityManager= (ConnectivityManager) getApplicationContext().getSystemService(context.CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo()!=null && connectivityManager.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    public void retry(View view) {
        startActivity(new Intent(MainActivity.this,MainActivity.class));
    }
}