package com.nhq.fifamarketcontrol;

import android.content.Intent;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.TextView;

public class EditContractActivity extends AddContractActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView title = findViewById(R.id.title);
        title.setText("SỬA HỢP ĐỒNG");

        Intent intent = getIntent();
        int isBuy = intent.getIntExtra("isBuy", 1);
        String name = intent.getStringExtra("name");
        String position = intent.getStringExtra("position");
        int ovr = intent.getIntExtra("ovr", 1);
        int price = intent.getIntExtra("price", 1);
        String date = intent.getStringExtra("date");

        if (isBuy == 1) {
            RadioButton buy = findViewById(R.id.isBuy);
            buy.setChecked(true);
        }
        else {
            RadioButton sell = findViewById(R.id.isSell);
            sell.setChecked(true);
        }

        nameET.setText(name);
        positionET.setText(position);
        ovrET.setText(ovr + "");
        priceET.setText(price + "");
        dateET.setText(date);
    }

}
