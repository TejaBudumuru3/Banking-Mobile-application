package com.example.project1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class AcSummary extends AppCompatActivity {
    TextView account,mail,name,ifsc,bal,address;
    String userId;
    FirebaseAuth Auth;
    FirebaseFirestore store;
    Button ministatement;
    @Override
    public void onBackPressed() {
        startActivity(new Intent(AcSummary.this,Homepage.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ac_summary);
        account = (TextView) findViewById(R.id.Baccno);
        mail = (TextView) findViewById(R.id.mail);
        name = (TextView) findViewById(R.id.Bname);
        ifsc = (TextView) findViewById(R.id.Bifsc);
        bal = (TextView) findViewById(R.id.bal);
        address = (TextView) findViewById(R.id.address);
        Auth = FirebaseAuth.getInstance();
        store  = FirebaseFirestore.getInstance();
        ministatement = findViewById(R.id.miniStatement);

        ministatement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AcSummary.this,miniStatement.class));
            }
        });


        userId = Auth.getCurrentUser().getUid();

        DocumentReference documentReference = store.collection("users").document(userId);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (Auth.getCurrentUser() != null) {name.setText(documentSnapshot.getString("Fname") + documentSnapshot.getString("Lname"));
                account.setText(documentSnapshot.getString("Account Number"));
                mail.setText(documentSnapshot.getString("mail"));
                bal.setText(documentSnapshot.getString("Amount"));
                address.setText(documentSnapshot.getString("address"));
            }
        }
        });


    }
}