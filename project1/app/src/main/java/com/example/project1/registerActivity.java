package com.example.project1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class registerActivity extends AppCompatActivity {

    final String emailPattern = "[a-z0-9]+@[a-z]+\\.[a-z]{2,3}";
    final String panPattern ="[A-Z]{5}[0-9]{4}[A-Z]{1}";
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        TextView Login,AccNumber;
        Button nextBtn,generate;
        EditText username,firstname,lastname,email,password,conpassword,aadhaar,amount,address,pan;
        ProgressBar RegProgress;
        FirebaseAuth Auth;
        FirebaseFirestore store;

        String TAG = "MSG";



        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        nextBtn = findViewById(R.id.registerNextBtn);
        Login = findViewById(R.id.loginTV);
        username = findViewById(R.id.inputUserName);
        firstname = findViewById(R.id.inputFname);
        lastname = findViewById(R.id.inputLname);
        email = findViewById(R.id.inputRegisterEmail);
        password = findViewById(R.id.inputRegisterPass);
        conpassword = findViewById(R.id.inputRegisterConfirm);
        RegProgress = findViewById(R.id.regProgress);


        //Bank Details

        AccNumber = findViewById(R.id.accNumber);
        aadhaar = findViewById(R.id.aadhaar);
        amount  = findViewById(R.id.amt);
        address = findViewById(R.id.address);
        pan = findViewById(R.id.pan);
        generate = findViewById(R.id.generate);

       //FireBase

        Auth = FirebaseAuth.getInstance();
        store = FirebaseFirestore.getInstance();



            Login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent =new Intent(registerActivity.this,Homeactivity.class);
                    startActivity(intent);
                }
            });

        generate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long randomNum = (long) random(100000000L,999999999L);
                AccNumber.setText(String.valueOf(randomNum));
            }
        });


        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Firstname = firstname.getText().toString();
                String Lastname = lastname.getText().toString();
                String Username = username.getText().toString();
                String Email = email.getText().toString();
                String Password = password.getText().toString();
                String RePassword = conpassword.getText().toString();
                String Amt = amount.getText().toString();
                String Aadhaar = aadhaar.getText().toString();
                String Address = address.getText().toString();
                String Pan = pan.getText().toString();
                String Acc = AccNumber.getText().toString();



                 if(Firstname.isEmpty())
                    firstname.setError("Enter Valid Details");
                else if(Lastname.isEmpty())
                    lastname.setError("Enter Valid Details");
                else if (Username.isEmpty())
                    username.setError("Enter Valid Details");
              else if(Email.isEmpty() ||  !Email.matches(emailPattern))
                  email.setError("invalid email format");
              else if ( Password.length()<8 || Password.isEmpty())
                  password.setError("Password Should be long atleast 8 Characters");
              else if ( !RePassword.equals(Password)|| RePassword.isEmpty() )
                  conpassword.setError("Password not matched");
               else if(Aadhaar.length() != 12)
                {
                    aadhaar.setError("Enter valid number");
                }
               else if(Address.isEmpty())
                     address.setError("Enter Valid Details");
                 else if (!Pan.matches(panPattern) || Pan.isEmpty())
                     pan.setError("Invalid PAN ID !!");
                 else if (Acc.isEmpty())
                     AccNumber.setError("Invalid PAN ID !!");
                 else if ( Amt.length()<4 || Amt.isEmpty())
                 {
                     amount.setError("Insufficient funds !!");
                 }

              else {
                     RegProgress.setVisibility(View.VISIBLE);
                  //registering account
                   Auth.createUserWithEmailAndPassword(Email,Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                       @Override
                       public void onComplete(@NonNull Task<AuthResult> task) {
                           if(task.isSuccessful())
                           {
                               Toast.makeText(registerActivity.this, "registered", Toast.LENGTH_SHORT).show();
                               userId = Auth.getCurrentUser().getUid();
                             DocumentReference documentReference = store.collection("users").document(userId);
                               Map<String,Object> user = new HashMap<>();
                               user.put("Uname",Username);
                               user.put("Fname",Firstname);
                               user.put("Lname",Lastname);
                               user.put("mail",Email);
                               user.put("Amount",Amt);
                               user.put("aadhaar",Aadhaar);
                               user.put("address",Address);
                               user.put("pan",Pan);
                               user.put("Account Number",Acc);
                               user.put("DebitCard", false);
                               user.put("CVV", "");
                               user.put("CardNo", "");

                               documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {

                                   @Override
                                   public void onSuccess(Void unused) {
                                       Log.d(TAG,"user stored"+ userId);
                                   }
                               });
                               Intent loginIntent = new Intent(registerActivity.this,Homeactivity.class);
                               startActivity(loginIntent);
                               RegProgress.setVisibility(View.GONE);
                           }
                           else
                           {
                               Toast.makeText(registerActivity.this, "Error !!"+"  "+task.getException(), Toast.LENGTH_SHORT).show();
                               RegProgress.setVisibility(View.GONE);
                           }
                       }
                   });


              }





            }
        });

        }
    private long random(long min, long max){
        return min + (long) (Math.random() *((max-min) +1));
    }

}
