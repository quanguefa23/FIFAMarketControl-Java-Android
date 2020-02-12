package com.nhq.fifamarketcontrol;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AddContractActivity extends AppCompatActivity {

    RadioGroup buyOrSellRG;
    EditText nameET;
    EditText positionET;
    EditText ovrET;
    EditText priceET;
    EditText dateET;

    Button okBT;
    Button cancelBT;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contract);

        buyOrSellRG = findViewById(R.id.buyOrSell);
        nameET = findViewById(R.id.name);
        positionET = findViewById(R.id.position);
        ovrET = findViewById(R.id.ovr);
        priceET = findViewById(R.id.price);
        dateET = findViewById(R.id.date);
        okBT = findViewById(R.id.ok);
        cancelBT = findViewById(R.id.cancel);

        dateET.setKeyListener(null);
        dateET.requestFocus();

        dateET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogInputDate();
            }
        });
        
        okBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int isBuy = buyOrSellRG.getCheckedRadioButtonId() == R.id.isBuy ? 1 : 0;

                String name = nameET.getText().toString();
                if (name.equals("")) {
                    Toast.makeText(AddContractActivity.this, "Bạn chưa nhập tên cầu thủ",
                            Toast.LENGTH_LONG).show();
                    return;
                }

                String position = positionET.getText().toString();
                if (position.equals("")) {
                    Toast.makeText(AddContractActivity.this, "Bạn chưa nhập vị trí",
                            Toast.LENGTH_LONG).show();
                    return;
                }
                if (!isValidPosition(position)) {
                    Toast.makeText(AddContractActivity.this, "Vị trí không hợp lệ",
                            Toast.LENGTH_LONG).show();
                    return;
                }

                int ovr;
                try {
                    ovr = Integer.parseInt(ovrET.getText().toString());
                }
                catch (Exception e) {
                    Toast.makeText(AddContractActivity.this, "Bạn chưa nhập chỉ số",
                            Toast.LENGTH_LONG).show();
                    return;
                }
                if (ovr > 150 || ovr < 40) {
                    Toast.makeText(AddContractActivity.this, "Chỉ số không hợp lệ",
                            Toast.LENGTH_LONG).show();
                    return;
                }

                int price;
                try {
                    price = Integer.parseInt(priceET.getText().toString());
                }
                catch (Exception e) {
                    Toast.makeText(AddContractActivity.this, "Bạn chưa nhập giá",
                            Toast.LENGTH_LONG).show();
                    return;
                }
                if (price < 0) {
                    Toast.makeText(AddContractActivity.this, "Giá không hợp lệ",
                            Toast.LENGTH_LONG).show();
                    return;
                }

                String date = dateET.getText().toString();
                if (date.equals("")) {
                    Toast.makeText(AddContractActivity.this, "Bạn chưa nhập ngày",
                            Toast.LENGTH_LONG).show();
                    return;
                }

                Intent intent = new Intent();
                intent.putExtra("isBuy", isBuy);
                intent.putExtra("name", name);
                intent.putExtra("position", position);
                intent.putExtra("ovr", ovr);
                intent.putExtra("price", price);
                intent.putExtra("date", date);

                setResult(RESULT_OK, intent);
                finish();
                overridePendingTransition(R.anim.anim_in1, R.anim.anim_out1);
            }
        });

        cancelBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private boolean isValidPosition(String position) {
        String[] validPositions = {"GK", "RWB", "RB", "CB", "LB", "LWB",
                "CDM", "RM", "CM", "LM", "CAM",
                "RF", "CF", "LF", "RW", "LW", "ST"};
        position = position.toUpperCase();
        for (String p : validPositions) {
            if (position.equals(p))
                return true;
        }
        return false;
    }

    private void showDialogInputDate() {
        final Calendar calendar = Calendar.getInstance();
        int date = calendar.get(Calendar.DATE);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

        DatePickerDialog dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                calendar.set(i, i1, i2);
                SimpleDateFormat formatDate = new SimpleDateFormat("dd-MM-yyyy");
                dateET.setText(formatDate.format(calendar.getTime()));
            }
        }, year, month, date);

        dialog.show();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        setResult(RESULT_CANCELED, intent);
        finish();
        overridePendingTransition(R.anim.anim_in1, R.anim.anim_out1);
    }
}
