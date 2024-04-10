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

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class BlockATM extends AppCompatActivity {
    Button blockBtn;
    EditText aadhaarNumber,AccountNumber;
    FirebaseAuth Auth;
    FirebaseFirestore store;
    String userId,fAccountNumber,fAadhaar;
    ProgressDialog progress;
    boolean flag = false;
    private String TAG = "MSG";

    @Override
    public void onBackPressed() {
        startActivity(new Intent(BlockATM.this,Homepage.class));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_block_atm);
        blockBtn = findViewById(R.id.BlockProceedBtn);
        aadhaarNumber  = findViewById(R.id.BlockAadhaar);
        AccountNumber = findViewById(R.id.BlockAccNum);
        store = FirebaseFirestore.getInstance();
        Auth = FirebaseAuth.getInstance();


        blockBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String AccNumber= AccountNumber.getText().toString();
                String AAdhaar = aadhaarNumber.getText().toString();

                if (AccNumber.isEmpty())
                    AccountNumber.setError("Account Number is mandatory");
                else if (AAdhaar.isEmpty())
                    aadhaarNumber.setError("Aadhaar Number is mandatory");
                else if (AccNumber.length()!=9)
                    AccountNumber.setError("Invalid Account number");
                else if (AAdhaar.length()!=12)
                    aadhaarNumber.setError("Invalid Aadhaar");
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

                    userId = Auth.getCurrentUser().getUid();
                    DocumentReference doc = store.collection("users").document(userId);
                    doc.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                            if (Auth.getCurrentUser() != null) {

                            fAadhaar = value.getString("aadhaar");
                            fAccountNumber = value.getString("Account Number");

                            Log.d(TAG, "onEvent:" + fAadhaar + " " + fAccountNumber);

                            if (!AccNumber.matches(fAccountNumber))
                                AccountNumber.setError("Account Number is not in our records");
                            else if (!AAdhaar.matches(fAadhaar))
                                aadhaarNumber.setError("AADHAAR Number is not in our records");
                            else {
                                flag = true;
                                return;
                            }
                        }
                    }
                    });
                    if(flag)
                    {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                startActivity(new Intent(BlockATM.this,Homepage.class));
                            }
                        },2000);

                        AlertDialog.Builder transfers = new AlertDialog.Builder(view.getContext());
                        transfers.setTitle("Blocking");
                        transfers.setMessage("ATM card successfully Blocked !!");
                        transfers.create().show();
                        updateData();
                    }

                        }
            }
        });
    }
    public void updateData() {
        DocumentReference documentReference = store.collection("users").document(userId);
        documentReference.update("DebitCard", false);
        documentReference.update("CardNo","");
        documentReference.update("CVV","").addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(BlockATM.this, "Debit card blocked !!", Toast.LENGTH_SHORT).show();
            }
        });
        return;
    }
}