package com.example.project1;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.rxjava3.annotations.NonNull;

public class Dth extends AppCompatActivity {
    Button payBill;
    Spinner sp;
    EditText subscriber,PaymentAmt;
    FirebaseAuth Auth;
    FirebaseFirestore store;
    String subscriberId,BillAmt;
    ProgressDialog progress;
    String userId,balance;
    String bal;
    String TAG="msg";
    String serviceProvider;
    boolean flag = false;

    @Override
    public void onBackPressed() {
        startActivity(new Intent(Dth.this,Homepage.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dth);

        payBill = findViewById(R.id.proceedToPay);
        sp = findViewById(R.id.DTHspinner);
        subscriber = findViewById(R.id.subscriberID);
        PaymentAmt = findViewById(R.id.DTHbill);
        Auth = FirebaseAuth.getInstance();
        store = FirebaseFirestore.getInstance();


        payBill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                subscriberId = subscriber.getText().toString();
                BillAmt = PaymentAmt.getText().toString();

                if (subscriberId.isEmpty())
                    subscriber.setError("Invalid ID / Number");
                else if (BillAmt.isEmpty())
                    PaymentAmt.setError("Invalid Amount");
                else if (subscriberId.length() != 10)
                    subscriber.setError("Invalid ID / Number");
                else if (BillAmt.length() < 3)
                    PaymentAmt.setError("Invalid Amount");
                else {
                    userId = Auth.getCurrentUser().getUid();
                    DocumentReference doc = store.collection("users").document(userId);
                    doc.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                            if (Auth.getCurrentUser() != null) {

                            balance = value.getString("Amount");


                            if ((Long.parseLong(BillAmt)) < (Long.parseLong(balance))) {

                                flag = true;

                                return;

                            } else {
                                serviceProvider = sp.getSelectedItem().toString();
                                AlertDialog.Builder transfers = new AlertDialog.Builder(view.getContext());
                                transfers.setTitle("DTH PAYMENT");
                                transfers.setMessage("payment of \n" + BillAmt + " for " + subscriberId + " " + serviceProvider + " is unsuccessful");
                                transfers.create().show();
                                Toast.makeText(Dth.this, "Transaction unsuccessful due to insufficient funds", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(Dth.this,billPayments.class));

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        startActivity(new Intent(Dth.this,Homepage.class));
                                    }
                                },2000);

                                flag = false;
                                return;
                            }
                        }
                        }
                    });
                    if (flag) {
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


                        serviceProvider = sp.getSelectedItem().toString();
                        String remaining = Dth.this.transaction(BillAmt, balance);
                        Dth.this.updateData(remaining);
                        String Time = timeStamp();
                        Log.d(TAG, "onCreate: "+Time);
                        miniStatement(remaining,BillAmt,Time);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                AlertDialog.Builder transfers = new AlertDialog.Builder(view.getContext());
                                transfers.setTitle("DTH PAYMENT");
                                transfers.setMessage("payment of \n" + BillAmt + " for " + subscriberId + " " + serviceProvider + " is Successful");
                                transfers.create().show();
                            }
                        },1000);

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                startActivity(new Intent(Dth.this,Homepage.class));
                            }
                        },3000);
                        flag = false;
                        subscriber.setText("");
                        PaymentAmt.setText("");
                        return;
                    }
                }
            }
        });
    }
    public String transaction(String PAmount, String Pbal)
    {
        long enteredAmt = Long.parseLong(PAmount);
        long totalBal = Long.parseLong(Pbal);
        long remainingBal = totalBal - enteredAmt;
        bal = String.valueOf(remainingBal);
        Log.i(TAG, "onClick: "+bal);
        return bal;
    }

    public void updateData(String Premaining) {
        DocumentReference documentReference = store.collection("users").document(userId);
        documentReference.update("Amount", Premaining).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(Dth.this, "Transaction successful", Toast.LENGTH_SHORT).show();
            }
        });
        return;
    }


    private void miniStatement(String Pbal,String Pdebited,String Ptime) {
        userId = Auth.getCurrentUser().getUid();
        CollectionReference collectionReference = store.collection("users").document(userId).collection("mini statement");
        Map<String,Object> statements = new HashMap<>();
        statements.put("debitedBalance",Pdebited);
        statements.put("remainingBalance",Pbal);
        statements.put("time",Ptime);
        collectionReference.add(statements).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Log.d(TAG, "onSuccess: mini statement created");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: "+e);
            }
        });
    }

    public String timeStamp()
    {
        SimpleDateFormat simpleDateFormat  = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return simpleDateFormat.format(new Date());
    }

}