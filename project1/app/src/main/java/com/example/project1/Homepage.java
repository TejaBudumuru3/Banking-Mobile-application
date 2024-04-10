package com.example.project1;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class Homepage extends AppCompatActivity {
    TextView username;
    Button profile,transfers,payments,eservices,contact;
    String userId;
    FirebaseAuth Auth;
    FirebaseFirestore store;
    private ProgressDialog progress;
    int count=0,countOut=0;
    private boolean flag;

    @Override
    public void onBackPressed() {

        count++;
        if(count!=2)
            Toast.makeText(this, "Press Again to exit", Toast.LENGTH_SHORT).show();
        else if(count==2)
        finishAffinity();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        username = findViewById(R.id.displayUser);
        Auth = FirebaseAuth.getInstance();
        store = FirebaseFirestore.getInstance();
       ButterKnife.bind(this);

        profile = findViewById(R.id.acc_summary);
        transfers = findViewById(R.id.fund_transfers);
        payments = findViewById(R.id.bill_payments);
        eservices = findViewById(R.id.eservices);
        contact = findViewById(R.id.chatbot);


//profile
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent profile = new Intent(Homepage.this,AcSummary.class);
                startActivity(profile);

            }
        });

 //Transfers

        transfers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent transfer = new Intent(Homepage.this,FundsTransfers.class);
                startActivity(transfer);
            }
        });
  //bill Payments
        payments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            startActivity(new Intent(Homepage.this,billPayments.class));
            }
        });

  //E-services
        eservices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progress = new ProgressDialog(view.getContext());
                progress.setMessage("please wait...");
                progress.setProgress(0);
                progress.show();
                Thread t = new Thread() {
                    @Override
                    public void run() {
                        try {
                            sleep(500);
                            progress.dismiss();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                };
                t.start();
                userId = Auth.getCurrentUser().getUid();
                DocumentReference doc = store.collection("users").document(userId);
                doc.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                        boolean Debit = value.getBoolean("DebitCard");

                        if (Debit)
                            startActivity(new Intent(Homepage.this,ATM.class));
                        else {
                            startActivity(new Intent(Homepage.this, EServices.class));
                            return;
                        }
                    }
                });
//                if(flag)
//                    startActivity(new Intent(Homepage.this,ATM.class));
//                else
//                startActivity(new Intent(Homepage.this,EServices.class));
//
            }
        });

  //Contact us
        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Homepage.this, contactus.class));
            }
        });


        userId = Auth.getCurrentUser().getUid();

            DocumentReference documentReference = store.collection("users").document(userId);
            documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                   if (Auth.getCurrentUser()!=null)
                    username.setText(documentSnapshot.getString("Uname"));
                }
            });
        }



    // ------------------------------------logout-----------------------------------
    @OnClick(R.id.logout)

    void logout()
    {

        countOut++;
        if(countOut!=2)
            Toast.makeText(this, "Press Again to confirm!!", Toast.LENGTH_SHORT).show();
        else if(countOut==2) {
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
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Auth.signOut();
                    Toast.makeText(Homepage.this, " logged out ", Toast.LENGTH_SHORT).show();
                    finishAffinity();
                }
            },2000);



    }
}
}