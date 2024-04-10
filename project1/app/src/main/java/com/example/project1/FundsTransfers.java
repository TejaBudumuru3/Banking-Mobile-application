package com.example.project1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class FundsTransfers extends AppCompatActivity {
    Button  otherBank,quickTransfer;

    @Override
    public void onBackPressed() {
        startActivity(new Intent(FundsTransfers.this,Homepage.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_funds_transfers);
        otherBank = findViewById(R.id.otherTranfer);
        quickTransfer = findViewById(R.id.quickTranfer);




        otherBank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i1 =new Intent(FundsTransfers.this,otherBank.class);
                startActivity(i1);
            }
        });

        quickTransfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i =new Intent(FundsTransfers.this,BankTransfers.class);
                startActivity(i);
            }
        });

    }
}