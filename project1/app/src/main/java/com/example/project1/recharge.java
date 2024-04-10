package com.example.project1;

import static android.content.Intent.ACTION_VIEW;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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

public class recharge extends AppCompatActivity {
    Spinner spinner;
    Button recharge;
    FloatingActionButton contacts;
    EditText phNumber,plans;
    String balance;
    private static final int RESULT_PICK_CONTACT =1;
    boolean flag = false;
    String PHnumber;
    String operator;
    String Plan;
    FirebaseAuth Auth;
    FirebaseFirestore store;
    String userId;
    String bal;
    String TAG="msg";
    private ProgressDialog progress;

    @Override
    public void onBackPressed() {
        startActivity(new Intent(recharge.this,Homepage.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge);

        recharge = findViewById(R.id.rechargebtn);
        phNumber = findViewById(R.id.phNum);
        plans = findViewById(R.id.plan);
        spinner = findViewById(R.id.spinner);
        Auth = FirebaseAuth.getInstance();
        store = FirebaseFirestore.getInstance();
        contacts = findViewById(R.id.MycontactList);



        contacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent (Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                startActivityForResult(in,RESULT_PICK_CONTACT);
            }
        });

//        recharge.setOnClickListener(new View.OnClickListener() {
//        });
    }

    public void searchPlans(View view) {
      startActivity(new Intent(ACTION_VIEW,Uri.parse("https://gadgets360.com/mobile-recharge-plans")));
    }



    protected void onActivityResult(int requestCode, int resultCode,  Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case RESULT_PICK_CONTACT:
                    contactPicked(data);
                    break;
            }
        } else {
            Toast.makeText(this, "Failed To pick contact", Toast.LENGTH_SHORT).show();
        }
    }

    private void contactPicked(Intent data) {
        Cursor cursor = null;

        try {
            String phoneNo = null;
            Uri uri = data.getData ();
            cursor = getContentResolver ().query (uri, null, null,null,null);
            cursor.moveToFirst ();
            int phoneIndex = cursor.getColumnIndex (ContactsContract.CommonDataKinds.Phone.NUMBER);

            phoneNo = cursor.getString (phoneIndex);

            phNumber.setText (phoneNo);


        } catch (Exception e) {
            e.printStackTrace ();
        }
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
                Toast.makeText(recharge.this, "Transaction successful", Toast.LENGTH_SHORT).show();
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

    public void rechargeFunction(View view) {

            PHnumber = phNumber.getText().toString();
            Plan = plans.getText().toString();

            if(PHnumber.isEmpty())
                phNumber.setError("Number should not empty");
            else if (Plan.isEmpty())
                plans.setError("Invalid Plan");
            else if (PHnumber.length()<10)
                phNumber.setError("Invalid Number");
            else if (Plan.length()<2)
                plans.setError("Invalid Plan");
            else
            {

                userId = Auth.getCurrentUser().getUid();
                DocumentReference doc = store.collection("users").document(userId);
                doc.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (Auth.getCurrentUser() != null) {

                            balance = value.getString("Amount");


                            if ((Long.parseLong(Plan)) < (Long.parseLong(balance))) {
                                flag = true;
                            }
                            else {
                                progress = new ProgressDialog(view.getContext());
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
                                operator = spinner.getSelectedItem().toString();
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        startActivity(new Intent(recharge.this,Homepage.class));
                                    }
                                },2000);
                                AlertDialog.Builder transfers = new AlertDialog.Builder(view.getContext());
                                transfers.setTitle("RECHARGE PAYMENT");
                                transfers.setMessage("payment of \n" + Plan + " for " + PHnumber + " " + operator + " is unsuccessful");
                                transfers.create().show();
                                Toast.makeText(recharge.this, "Transaction unsuccessful due to insufficient funds", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(recharge.this,billPayments.class));
                                flag = false;
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
                                sleep(4000);
                                progress.dismiss();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    t.start();
                    operator = spinner.getSelectedItem().toString();
                    String remaining = transaction(Plan, balance);
                    updateData(remaining);
                    String Time = timeStamp();
                    Log.d(TAG, "onCreate: "+Time);
                    miniStatement(remaining,Plan,Time);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            AlertDialog.Builder transfers = new AlertDialog.Builder(view.getContext());
                            transfers.setTitle("RECHARGE PAYMENT");
                            transfers.setMessage("payment of \n"+Plan+" for "+PHnumber+" "+operator+" is successful");
                            transfers.create().show();
                        }
                    },2000);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startActivity(new Intent(recharge.this,Homepage.class));
                        }
                    },3000);
                    flag = false;

                    phNumber.setText("");
                    plans.setText("");
                    return;
                }
            }
        }

    }