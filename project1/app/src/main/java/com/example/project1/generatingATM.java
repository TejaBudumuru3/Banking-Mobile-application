package com.example.project1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.ThreadLocalRandom;

public class generatingATM extends AppCompatActivity {
    Button generateATM;
    ProgressDialog progress;
    FirebaseFirestore store;
    FirebaseAuth Auth;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generating_atm);

        generateATM = findViewById(R.id.generatingATM);
        store = FirebaseFirestore.getInstance();
        Auth = FirebaseAuth.getInstance();

        generateATM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progress = new ProgressDialog(view.getContext());
                progress.setMessage("generating...");
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

                //generating Card no:

                long smallest = 1000_0000_0000_0000L;
                long biggest =  9999_9999_9999_9999L;
                long random = ThreadLocalRandom.current().nextLong(smallest, biggest+1);
                String cardNo = String.valueOf(random);
                updateCard(cardNo);
                //generating CVV:

                int big = 999;
                int small  = 99;
                int randomCVV = ThreadLocalRandom.current().nextInt(small, big+1);
               String cvv = String.valueOf(randomCVV);
                updateCVV(cvv);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(generatingATM.this,Homepage.class));
                    }
                },2000);
                AlertDialog.Builder transfers = new AlertDialog.Builder(view.getContext());
                transfers.setTitle("Generating.......");
                transfers.setMessage("Generated Successfully");
                transfers.create().show();
                startActivity(new Intent(generatingATM.this,ATM.class));
            }
        });

    }

    public void updateCVV(String PCvv) {
        userId = Auth.getCurrentUser().getUid();
        DocumentReference documentReference = store.collection("users").document(userId);
        documentReference.update("CVV", PCvv).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(generatingATM.this, "CVV updated successful", Toast.LENGTH_SHORT).show();
            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(generatingATM.this, "CVV updated unsuccessful "+e, Toast.LENGTH_SHORT).show();
            }
        });
        return;
    }

    public void updateCard(String PCardNo) {
        userId = Auth.getCurrentUser().getUid();
        DocumentReference documentReference = store.collection("users").document(userId);
        documentReference.update("CardNo", PCardNo).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(generatingATM.this, "Card no updated successful", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(generatingATM.this, "Card no updated unsuccessful "+e, Toast.LENGTH_SHORT).show();
            }
        });
        return;
    }

}