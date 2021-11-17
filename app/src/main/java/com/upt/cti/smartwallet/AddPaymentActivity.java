package com.upt.cti.smartwallet;

import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.upt.cti.smartwallet.model.Payment;
import com.upt.cti.smartwallet.ui.PaymentType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class AddPaymentActivity extends AppCompatActivity {
    private EditText eName;
    private EditText eCost;
    private TextView tTimestamp;
    private Button bUpdate;
    private Button bDelete;
    private Spinner sType;

    private Payment payment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_payment);

        //
        setTitle("Add or edit payment");

        // ui
        eName = (EditText) findViewById(R.id.eName);
        eCost = (EditText) findViewById(R.id.eCost);
        sType = (Spinner) findViewById(R.id.sType);
        tTimestamp = (TextView) findViewById(R.id.tTimestamp);

        // spinner adapter
        String[] types = PaymentType.getTypes();
        final ArrayAdapter<String> sAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, types);
        sAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sType.setAdapter(sAdapter);

        // initialize UI if editing
        payment = AppState.get().getCurrentPayment();
        if (payment != null) {
            eName.setText(payment.getName());
            eCost.setText(String.valueOf(payment.getCost()));
            tTimestamp.setText("Time of payment: " + payment.timestamp);
            try {
                sType.setSelection(Arrays.asList(types).indexOf(payment.getType()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            tTimestamp.setText("");
        }
    }

    public void clicked(View view) {
        switch(view.getId()){
            case R.id.bUpdate:
                if (payment != null)
                    save(payment.timestamp);
                else
                    save(AppState.getCurrentTimeDate());
                break;
            case R.id.bDelete:
                if (payment != null)
                    delete(payment.timestamp);
                else
                    Toast.makeText(this, "Payment does not exist", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void save(String timestamp){
        Payment payment = new Payment(
                Double.parseDouble(eCost.getText().toString()),
                eName.getText().toString(),
                sType.getSelectedItem().toString()
        );

        Map<String, Object> map = new HashMap<>();
        map.put("cost", payment.getCost());
        map.put("name", payment.getName());
        map.put("type", payment.getType());

        System.out.println("timestamp: " + timestamp);
        System.out.println("name: " + payment.getName());
        System.out.println("cost: " + payment.getCost());
        System.out.println("type: " + payment.getType());

        AppState.get().getDatabaseReference().child("wallet").child(timestamp).updateChildren(map);
        finish();
    }

    private void delete(String timestamp){
        AppState.get().getDatabaseReference().child("wallet").child(timestamp).removeValue();
        finish();
    }
}
