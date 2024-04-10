package com.example.project1;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.concurrent.ThreadLocalRandom;

public class ATM extends AppCompatActivity {
    TextView cardno, cardname, cvv;
    ProgressDialog progress;
    Button block;
    FirebaseFirestore store;
    FirebaseAuth Auth;
    String userId;
    String name;


    @Override
    public void onBackPressed() {
        startActivity(new Intent(ATM.this,Homepage.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atm);
        cardname = findViewById(R.id.cardHolderName);
        cardno = findViewById(R.id.cardNumber);
        cvv = findViewById(R.id.CVV);
        store = FirebaseFirestore.getInstance();
        Auth = FirebaseAuth.getInstance();
        block = findViewById(R.id.blockATMbtn);

        progress = new ProgressDialog(this);
        progress.setMessage("please wait...");
        progress.setProgress(0);
        progress.show();
        Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    sleep(1000);
                    progress.dismiss();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        t.start();
        if (Auth.getCurrentUser() != null) {
            userId = Auth.getCurrentUser().getUid();

            DocumentReference documentReference = store.collection("users").document(userId);
            documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                    cardname.setText(new StringBuilder().append(documentSnapshot.getString("Fname")).append(" ").append(documentSnapshot.getString("Lname")).toString());
                    cardno.setText(documentSnapshot.getString("CardNo"));
                    cvv.setText(documentSnapshot.getString("CVV"));


                }
            });
        }


        block.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ATM.this,BlockATM.class));
            }
        });

    }


}