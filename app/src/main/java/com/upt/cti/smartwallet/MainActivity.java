package com.upt.cti.smartwallet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.upt.cti.smartwallet.model.Payment;
import com.upt.cti.smartwallet.ui.PaymentAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    // firebase
    private DatabaseReference databaseReference;
    private List<Payment> payments = new ArrayList<>();

    // ui
    TextView tStatus;
    Button bPrevious;
    Button bNext;
    FloatingActionButton fabAdd;
    ListView listPayments;
    PaymentAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //
        tStatus = findViewById(R.id.tStatus);
        bPrevious = findViewById(R.id.bPrevious);
        bNext = findViewById(R.id.bNext);
        fabAdd = findViewById(R.id.fabAdd);

        listPayments = findViewById(R.id.listPayments);
        adapter = new PaymentAdapter(this, R.layout.payment_item, payments);
        listPayments.setAdapter(adapter);

        // setup firebase
        final FirebaseDatabase database = FirebaseDatabase.getInstance("https://smart-wallet-f28ef-default-rtdb.europe-west1.firebasedatabase.app/");
        databaseReference = database.getReference();

        AppState.get().setDatabaseReference(databaseReference);

        AppState.get().getDatabaseReference().child("wallet").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
                try {
                    Payment payment = dataSnapshot.getValue(Payment.class);

                    if (payment != null) {
                        payment.timestamp = dataSnapshot.getKey();

                        System.out.println(payment.toString());

                        if (!payments.contains(payment)){
                            payments.add(payment);
                        }

                        adapter.notifyDataSetChanged();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String s) {
                try {
                    Payment payment = dataSnapshot.getValue(Payment.class);

                    if (payment != null) {
                        payment.timestamp = dataSnapshot.getKey();

                        for (Payment p: payments) {
                            if (p.timestamp.equals(payment.timestamp)) {
                                payments.set(payments.indexOf(p), payment);
                                break;
                            }
                        }

                        adapter.notifyDataSetChanged();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                try {
                    Payment payment = dataSnapshot.getValue(Payment.class);

                    if (payment != null) {
                        payment.timestamp = dataSnapshot.getKey();

                        for (Payment p: payments) {
                            if (p.equals(payment)) {
                                payments.remove(p);
                                break;
                            }
                        }

                        adapter.notifyDataSetChanged();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String s) {
        /* Listen for changes to the order of items in an ordered list. This event is
        triggered whenever the onChildChanged() callback is triggered by an update that
        causes reordering of the child. It is used with data that is ordered with
        orderByChild or orderByValue. */
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void clicked(View view){
        switch(view.getId()){
            case R.id.fabAdd:
                AppState.get().setCurrentPayment(null);
                startActivity(new Intent(this, AddPaymentActivity.class));
                break;
        }
    }
}