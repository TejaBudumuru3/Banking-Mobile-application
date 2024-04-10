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
import android.widget.ProgressBar;
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

public class BankTransfers extends AppCompatActivity {

    EditText Bankname,BaccNum,BAmt;
    Button Transferbtn;
    ProgressBar bar;
    final String ifscPattern = "^[A-Z]{4}0[A-Z0-9]{6}$";
    String balance;
    boolean flag = false;
    FirebaseAuth Auth;
    FirebaseFirestore store;
    String userId;
    String bal;
    String TAG="msg";
    private ProgressDialog progress;

    @Override
    public void onBackPressed() {
        startActivity(new Intent(BankTransfers.this,Homepage.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_transfers);

        Bankname = findViewById(R.id.Tname);
        BaccNum = findViewById(R.id.TAccountno);
        BAmt = findViewById(R.id.Tamount);
        Transferbtn = findViewById(R.id.bankTransfer);
        Auth = FirebaseAuth.getInstance();
        store = FirebaseFirestore.getInstance();

        Transferbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Name = Bankname.getText().toString();
                String account = BaccNum.getText().toString();
               String Amount = BAmt.getText().toString();


                if (Name.isEmpty())
                    Bankname.setError("Enter Valid Details");
                else if (account.isEmpty())
                    BaccNum.setError("Enter Valid Details");
                else if (Amount.isEmpty())
                    BAmt.setError("Enter Valid Amount");
                else if (account.length()!=10)
                    BaccNum.setError("invalid Account number");
                else if (Amount.length()<=2)
                    BAmt.setError("Minimum Transaction amount 100₹");
                else {

                   userId = Auth.getCurrentUser().getUid();
                    DocumentReference doc = store.collection("users").document(userId);
                    doc.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                            if (Auth.getCurrentUser() != null) {

                            balance = value.getString("Amount");


                            if ((Long.parseLong(Amount)) < (Long.parseLong(balance))) {

                                flag = true;

                            } else {
                                progress = new ProgressDialog(view.getContext());
                                progress.setMessage("please wait...");
                                progress.setProgress(0);
                                progress.show();
                                Thread t = new Thread() {
                                    @Override
                                    public void run() {
                                        try {
                                            sleep(5000);
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
                                        AlertDialog.Builder transfers = new AlertDialog.Builder(view.getContext());
                                        transfers.setTitle("Funds transferring");
                                        transfers.setMessage("Transaction Unsuccessful");
                                        transfers.create().show();
                                    }
                                },1000);
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        startActivity(new Intent(BankTransfers.this,Homepage.class));
                                    }
                                },3000);
                                Toast.makeText(BankTransfers.this, "Transaction unsuccessful", Toast.LENGTH_SHORT).show();

                            }
                            return;
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
                                    sleep(5000);
                                    progress.dismiss();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        };
                        t.start();

                        String remaining = transaction(Amount, balance);
                        updateData(remaining);
                        String Time = timeStamp();
                        Log.d(TAG, "onCreate: "+Time);
                        miniStatement(remaining,Amount,Time);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                AlertDialog.Builder transfers = new AlertDialog.Builder(view.getContext());
                                transfers.setTitle("Funds transferring");
                                transfers.setMessage("Transaction of "+Amount+" for "+account+"("+Name+")"+"  Successful");
                                transfers.create().show();
                            }
                        },1000);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                startActivity(new Intent(BankTransfers.this,Homepage.class));
                            }
                        },4000);
                        Toast.makeText(BankTransfers.this, "Transaction successful", Toast.LENGTH_SHORT).show();

                        flag = false;
                        Bankname.setText("");
                        BaccNum.setText("");
                        BAmt.setText("");
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
                Toast.makeText(BankTransfers.this, "Amount updated", Toast.LENGTH_SHORT).show();
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