package com.example.project1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;
import java.util.Objects;

public class miniStatement extends AppCompatActivity {
    TextView displayTime,displayDebit,displayBalance;
    String TAG ="MSG";
    FirebaseFirestore store = FirebaseFirestore.getInstance();
    FirebaseAuth Auth = FirebaseAuth.getInstance();
    String userId = Auth.getCurrentUser().getUid();

    CollectionReference collectionReference = store.collection("users").document(userId).collection("mini statement");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mini_statement);
        displayTime = findViewById(R.id.displayTime);
        displayDebit = findViewById(R.id.displayDebit);
        displayBalance = findViewById(R.id.displayBalance);

      collectionReference.orderBy("time", Query.Direction.DESCENDING).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
          @Override
          public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
              String dataTime ="";
              String dataDebit ="";
              String dataBal ="";
              List<DocumentSnapshot> snapshotList = queryDocumentSnapshots.getDocuments();
              for(DocumentSnapshot snapshot : snapshotList){
                  String balance = Objects.requireNonNull(snapshot.getString("remainingBalance")).toString();
                  String debited = Objects.requireNonNull(snapshot.getString("debitedBalance")).toString();
                  String time = Objects.requireNonNull(snapshot.getString("time")).toString();
                  dataTime +=""+time+"\n";
                  dataDebit +=""+debited+"\n";
                  dataBal +=""+balance+"\n";
              }
              displayTime.setText(dataTime);
              displayDebit.setText(dataDebit);
              displayBalance.setText(dataBal);
          }
      });


    }
}