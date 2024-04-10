package com.example.project1;
import androidx.annotation.NonNull;
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

public class otherBank<bal> extends AppCompatActivity {
    EditText name,accNum,CaccNum,Ifsc,Amt,BnkName;
    Button Transfer;
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
        startActivity(new Intent(otherBank.this,Homepage.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_bank);

        name = findViewById(R.id.Oname);
        BnkName = findViewById(R.id.Bankname);
        accNum = findViewById(R.id.OAccountno);
        CaccNum = findViewById(R.id.Oconfirm);
        Ifsc = findViewById(R.id.Oifsc);
        Amt = findViewById(R.id.Oamount);
        Transfer = findViewById(R.id.btnTransfer);
        bar = findViewById(R.id.Oprogress);
        Auth = FirebaseAuth.getInstance();
        store = FirebaseFirestore.getInstance();






        Transfer.setOnClickListener(view -> {
            String Name = name.getText().toString();
            String account = accNum.getText().toString();
            String Confirm = CaccNum.getText().toString();
            String IFSC = Ifsc.getText().toString();
            String Amount = Amt.getText().toString();
            String bank = BnkName.getText().toString();

            if (Name.isEmpty())
                name.setError("Enter Valid Details");
            else if (account.isEmpty())
                accNum.setError("Enter Valid Details");
            else if (Confirm.isEmpty())
                CaccNum.setError("Enter Valid Details");
            else if (Amount.isEmpty())
                Amt.setError("Enter Valid Amount");
            else if (bank.isEmpty())
                BnkName.setError("invalid Bank");
            else if (account.length()<9)
                accNum.setError("invalid Account number");
            else if (Confirm.length()<9)
                CaccNum.setError("invalid Account number");
            else if (!Confirm.matches(account))
                CaccNum.setError("Account Number not matches !!");
            else if (IFSC.isEmpty() || !IFSC.matches(ifscPattern))
                Ifsc.setError("invalid IFSC code");
            else if (Amount.length()<=2)
                Amt.setError("Minimum Transaction amount 100₹");
            else
            {


                userId = Auth.getCurrentUser().getUid();
                    DocumentReference doc = store.collection("users").document(userId);
                    doc.addSnapshotListener(this,new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                            if (Auth.getCurrentUser() != null) {

                                balance = value.getString("Amount");


                            if ((Integer.parseInt(Amount)) < (Integer.parseInt(balance))) {

                                flag = true;

                                return;

                            } else {
                                progress=new ProgressDialog(view.getContext());
                                progress.setMessage("please wait...");
                                progress.setProgress(05);
                                progress.show();
                                Thread t = new Thread()
                                {
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
                                bar.setVisibility(View.VISIBLE);
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        AlertDialog.Builder transfers = new AlertDialog.Builder(view.getContext());
                                        transfers.setTitle("Funds transferring");
                                        transfers.setMessage("Unsuccessful due to insufficient funds");
                                        transfers.create().show();
                                    }
                                },1000);
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        startActivity(new Intent(otherBank.this,Homepage.class));
                                    }
                                },3000);
                               Toast.makeText(otherBank.this, "Transaction unsuccessful", Toast.LENGTH_SHORT).show();
                                bar.setVisibility(View.INVISIBLE);
                                return;

                            }

                        }
                    }
                    });

                    if(flag)
                    {
                        progress=new ProgressDialog(view.getContext());
                        progress.setMessage("please wait...");
                        progress.setProgress(05);
                        progress.show();
                        Thread t = new Thread()
                        {
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
                        String remaining = transaction(Amount,balance);
                        updateData(remaining);
                        String Time = timeStamp();
                        Log.d(TAG, "onCreate: "+Time);
                        miniStatement(remaining,Amount,Time);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                AlertDialog.Builder transfers = new AlertDialog.Builder(view.getContext());
                                transfers.setTitle("Funds transferring");
                                transfers.setMessage("Transaction of "+Amount +" amount for "+account+" Successful");
                                transfers.create().show();
                                Toast.makeText(otherBank.this, "Transaction successful", Toast.LENGTH_SHORT).show();
                            }
                        },1000);

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                startActivity(new Intent(otherBank.this,Homepage.class));
                            }
                        },3000);

                        flag = false;
                        name.setText("");
                        accNum.setText("");
                        CaccNum.setText("");
                        Ifsc.setText("");
                        BnkName.setText("");
                        Amt.setText("");
                        return;
                    }
                        }
        });

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


    public String transaction(String PAmount, String Pbal)
    {
        int enteredAmt = Integer.parseInt(PAmount);
        int totalBal = Integer.parseInt(Pbal);
        int remainingBal = totalBal - enteredAmt;
        bal = String.valueOf(remainingBal);
        Log.i(TAG, "onClick: "+bal);
        return bal;
    }

public void updateData(String Premaining) {
    DocumentReference documentReference = store.collection("users").document(userId);
    documentReference.update("Amount", Premaining).addOnSuccessListener(new OnSuccessListener<Void>() {
        @Override
        public void onSuccess(Void unused) {
            Toast.makeText(otherBank.this, "Amount Updated", Toast.LENGTH_SHORT).show();
        }
    });
    return;
}
public String timeStamp()
{
    SimpleDateFormat simpleDateFormat  = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    return simpleDateFormat.format(new Date());
}

}