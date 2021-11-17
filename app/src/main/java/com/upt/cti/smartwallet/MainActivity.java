package com.upt.cti.smartwallet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

        databaseReference.child("wallet").addChildEventListener(new ChildEventListener() {
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
        /* Listen for changes to the items in a list. This event fired any time a child
        node is modified, including any modifications to descendants of the child node.
        The DataSnapshot passed to the event listener contains the updated data for the child.
        */
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
        /* Listen for items being removed from a list. The DataSnapshot passed to the event
         callback contains the data for the removed child. */
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
        /*
        * */
    }
}