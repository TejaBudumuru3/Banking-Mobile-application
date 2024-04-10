
package com.example.project1;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class contactus extends AppCompatActivity {

    private RecyclerView chatsRV;
    private ImageButton sendMsgIB;
    private EditText userMsgEdt;
    private final String USER_KEY = "user";
    private final String BOT_KEY = "bot";
    int n;
    private ArrayList<MessageModal> messageModalArrayList;
    private MessageRVAdapter messageRVAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contactus);
        chatsRV = findViewById(R.id.idRVChats);
        sendMsgIB = findViewById(R.id.idIBSend);
        userMsgEdt = findViewById(R.id.idEdtMessage);
        messageModalArrayList = new ArrayList<>();
        messageModalArrayList.add(new MessageModal("Hi,Here are some things I can do for you", BOT_KEY));
        messageModalArrayList.add(new MessageModal("Enter Related Number"+'\n'+"1-Bank Balance"+'\t'+"2-Transactions"+'\n'+"3-Apply ATM Card"+'\t'+"4-Transfer Funds"+'\n'+"5-Block ATM Card", BOT_KEY));
        sendMsgIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userMsgEdt.getText().toString().isEmpty()) {
                    Toast.makeText(contactus.this, "Please enter your message..", Toast.LENGTH_SHORT).show();
                    return;
                }
                sendMessage(userMsgEdt.getText().toString());
                userMsgEdt.setText("");
            }
        });
        messageRVAdapter = new MessageRVAdapter(messageModalArrayList, this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(contactus.this, RecyclerView.VERTICAL, false);
        chatsRV.setLayoutManager(linearLayoutManager);
        chatsRV.setAdapter(messageRVAdapter);
    }

    private void sendMessage(String userMsg) {
        messageModalArrayList.add(new MessageModal(userMsg, USER_KEY));
        messageRVAdapter.notifyDataSetChanged();
        n=Integer.parseInt(userMsgEdt.getText().toString());
        switch (n) {
            case 1:
                messageModalArrayList.add(new MessageModal("Click on the 'A/C SUMMARY' button.You will be able to see your account details and balance.", BOT_KEY));
                break;
            case 2:
                messageModalArrayList.add(new MessageModal("Click on the 'A/C SUMMARY' button,under email click on 'MINI STATEMENT'.You will be able to see your transactions.", BOT_KEY));
                break;
            case 3:
                messageModalArrayList.add(new MessageModal("Click on the 'E-SERVICES' button,You can give your details and click 'APPLY ATM CARD'.", BOT_KEY));
                break;
            case 4:
                messageModalArrayList.add(new MessageModal("Click on the 'PAYMENTS & TRANSFERS' button.You can transfer funds.", BOT_KEY));
                break;
            case 5:
                messageModalArrayList.add(new MessageModal("Click on the 'E-SERVICES' button,click on 'BLOCK YOUR ATM CARD'.You can give your details and click 'PROCEED'", BOT_KEY));
                break;
            default:
                messageModalArrayList.add(new MessageModal("Sorry,I can't Understand", BOT_KEY));
                messageModalArrayList.add(new MessageModal("I am only do this things.", BOT_KEY));
                messageModalArrayList.add(new MessageModal("Enter Related Number"+'\n'+"1-Bank Balance"+'\t'+"2-Transactions"+'\n'+"3-Apply ATM Card"+'\t'+"4-Transfer Funds"+'\n'+"5-Block ATM Card", BOT_KEY));
                break;
        }

    }

}