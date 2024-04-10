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

public class electricity extends AppCompatActivity {
    Button pay;
    EditText serviceNum,serviceBoard,billAmount;
    FirebaseAuth Auth;
    FirebaseFirestore store;
    String Board,ServiceNumber,Bill;
    String userId;
    String bal;
    String TAG="msg";
    boolean flag;
    String balance;
    private ProgressDialog progress;

    @Override
    public void onBackPressed() {
        startActivity(new Intent(electricity.this,Homepage.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_electricity);

        pay = findViewById(R.id.electricPayment);
        serviceBoard = findViewById(R.id.serviceBoard);
        serviceNum = findViewById(R.id.serviceNumber);
        billAmount = findViewById(R.id.BillAmount);
        Auth = FirebaseAuth.getInstance();
        store = FirebaseFirestore.getInstance();

        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Board = serviceBoard.getText().toString();
                 ServiceNumber = serviceNum.getText().toString();
                 Bill = billAmount.getText().toString();
                if(Board.isEmpty())
                    serviceBoard.setError("Invalid Board");
                else if (ServiceNumber.isEmpty())
                    serviceNum.setError("Invalid Number");
                else if (ServiceNumber.length()!=16)
                    serviceNum.setError("Invalid Number");
                else if (Bill.length()<1)
                    billAmount.setError("Invalid Amount");
                else{
                    userId = Auth.getCurrentUser().getUid();
                    DocumentReference doc = store.collection("users").document(userId);
                    doc.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                            if (Auth.getCurrentUser() != null) {

                            balance = value.getString("Amount");


                            if ((Long.parseLong(Bill)) < (Long.parseLong(balance))) {

                                flag = true;

                                return;

                            } else {
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        startActivity(new Intent(electricity.this,Homepage.class));
                                    }
                                },2000);
                                AlertDialog.Builder transfers = new AlertDialog.Builder(view.getContext());
                                transfers.setTitle("ELECTRICITY BILL PAYMENT");
                                transfers.setMessage("payment of \n" + Bill + " for " + ServiceNumber + " is unsuccessful");
                                transfers.create().show();
                                Toast.makeText(electricity.this, "Transaction unsuccessful due to insufficient funds", Toast.LENGTH_SHORT).show();

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
                        String remaining = transaction(Bill, balance);
                        updateData(remaining);
                        String Time = timeStamp();
                        Log.d(TAG, "onCreate: "+Time);
                        miniStatement(remaining,Bill,Time);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                AlertDialog.Builder transfers = new AlertDialog.Builder(view.getContext());
                                transfers.setTitle("RECHARGE PAYMENT");
                                transfers.setMessage("payment of \n"+Bill+" for "+ServiceNumber+" is Successful");
                                transfers.create().show();
                            }
                        },1000);
                       new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                startActivity(new Intent(electricity.this,Homepage.class));
                            }
                        },3000);

                        flag = false;
                        serviceBoard.setText("");
                        billAmount.setText("");
                        serviceNum.setText("");

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
                Toast.makeText(electricity.this, "Transaction successful", Toast.LENGTH_SHORT).show();
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