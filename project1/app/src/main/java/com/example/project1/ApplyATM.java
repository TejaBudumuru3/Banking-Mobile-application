package com.example.project1;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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


public class ApplyATM extends AppCompatActivity {
    EditText aadhaar, PanNum;
    Button Atm;
    String aadhaarNumber, PanNumber, fPan, fAadhaar, userId;
    FirebaseAuth Auth;
    FirebaseFirestore store;
    static boolean flag = false;
    ProgressDialog progress;
    final String panPattern = "[A-Z]{5}[0-9]{4}[A-Z]{1}";

    @Override
    public void onBackPressed() {
        startActivity(new Intent(ApplyATM.this,Homepage.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_atm);

        aadhaar = findViewById(R.id.aadhaarApply);
        PanNum = findViewById(R.id.panApply);
        Atm = findViewById(R.id.ATMbtn);
        Auth = FirebaseAuth.getInstance();
        store = FirebaseFirestore.getInstance();

        Atm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                aadhaarNumber = aadhaar.getText().toString();
                PanNumber = PanNum.getText().toString();

                if (aadhaarNumber.length() != 12)
                    aadhaar.setError("AADHAAR number not matched with our records !!");
                else if (PanNumber.isEmpty())
                    PanNum.setError("PAN number should not empty !!");
                else if (!PanNumber.matches(panPattern))
                    PanNum.setError("PAN number not matched with our records !!");
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
                            fPan = value.getString("pan");

                            if (aadhaarNumber.matches(fAadhaar) && PanNumber.matches(fPan))
                                flag = true;
                            else {
                                AlertDialog.Builder transfers = new AlertDialog.Builder(view.getContext());
                                transfers.setTitle("ERROR !!");
                                transfers.setMessage("AADHAAR number / PAN number not matched with our records !!");
                                transfers.create().show();
                                Toast.makeText(ApplyATM.this, "AADHAAR number / PAN number not matched with our records !!", Toast.LENGTH_SHORT).show();

                                flag = false;
                                return;
                            }
                        }
                    }
                    });
                    if (flag) {

                        AlertDialog.Builder transfers = new AlertDialog.Builder(view.getContext());
                        transfers.setTitle("Applying for ATM card");
                        transfers.setMessage("AADHAAR number & PAN number is verified !!");
                        transfers.create().show();
                        Toast.makeText(ApplyATM.this, "AADHAAR number & PAN number is verified !!", Toast.LENGTH_SHORT).show();
                        updateData(flag);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                startActivity(new Intent(ApplyATM.this, generatingATM.class));
                            }
                        }, 1000);
                        aadhaar.setText("");
                        PanNum.setText("");
                        return;
                    }
                }
            }
        });

    }

    public void updateData(boolean Pflag) {
        DocumentReference documentReference = store.collection("users").document(userId);
        documentReference.update("DebitCard", true).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(ApplyATM.this, "Debit card updated successful", Toast.LENGTH_SHORT).show();
            }
        });
        return;
    }
}