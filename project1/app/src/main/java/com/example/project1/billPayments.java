package com.example.project1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class billPayments extends AppCompatActivity {
    Button recharge,dth,eleBill;

    @Override
    public void onBackPressed() {
        startActivity(new Intent(billPayments.this,Homepage.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_payments);
        recharge = findViewById(R.id.recharge);
        dth = findViewById(R.id.DTH);
        eleBill = findViewById(R.id.electricityBill);

        recharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(billPayments.this, recharge.class));
            }
        });

        dth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(billPayments.this, Dth.class));
            }
        });

        eleBill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(billPayments.this,electricity.class));
            }
        });
    }
}