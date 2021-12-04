package com.upt.cti.smartwallet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
    private List<Payment> payments = new ArrayList<>();

    // ui
    TextView tStatus;
    Button bPrevious;
    FloatingActionButton fabAdd;
    ListView listPayments;
    PaymentAdapter adapter;

    // Firebase authentication
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private static final int REQ_SIGNIN = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //
        tStatus = findViewById(R.id.tStatus);
        bPrevious = findViewById(R.id.bPrevious);
        fabAdd = findViewById(R.id.fabAdd);

        listPayments = findViewById(R.id.listPayments);
        adapter = new PaymentAdapter(this, R.layout.payment_item, payments);
        listPayments.setAdapter(adapter);

        // setup authentication
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    TextView tLoginDetail = findViewById(R.id.tLoginDetail);
                    TextView tUser = findViewById(R.id.tUser);
                    tLoginDetail.setText("Firebase ID: " + user.getUid());
                    tUser.setText("Email: " + user.getEmail());

                    initPaymentsList();

                    AppState.get().setUserId(user.getUid());
                    attachDBListener(user.getUid());
                } else {
                    startActivityForResult(new Intent(getApplicationContext(),
                            SignupActivity.class), REQ_SIGNIN);
                }
            }
        };
    }

    private void attachDBListener(String uid) {
        // setup firebase database
        final FirebaseDatabase database = FirebaseDatabase.getInstance("https://smart-wallet-f28ef-default-rtdb.europe-west1.firebasedatabase.app/");
        DatabaseReference databaseReference = database.getReference();
        AppState.get().setDatabaseReference(databaseReference);

        databaseReference.child("wallet").child(uid).addChildEventListener(new ChildEventListener() {
            //...
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
                try {
                    Payment payment = dataSnapshot.getValue(Payment.class);

                    if (payment != null) {
                        payment.timestamp = dataSnapshot.getKey();
//                        AppState.get().updateLocalBackup(getApplicationContext(), payment, true);
                        System.out.println(payment.toString());

                        if (!payments.contains(payment)){
                            payments.add(payment);
                        }

                        adapter.notifyDataSetChanged();
                        listPayments.deferNotifyDataSetChanged();
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
//                        AppState.get().updateLocalBackup(getApplicationContext(), payment, true);
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
//                        AppState.get().updateLocalBackup(getApplicationContext(), payment, false);
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

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    public void clicked(View view){
        switch(view.getId()){
            case R.id.fabAdd:
                AppState.get().setCurrentPayment(null);
                startActivity(new Intent(this, AddPaymentActivity.class));
                break;
            case R.id.bSignOut:
                payments = new ArrayList<>();
                mAuth.signOut();
                break;
        }
    }

    private void initPaymentsList() {
        payments = new ArrayList<>();
        adapter = new PaymentAdapter(this, R.layout.payment_item, payments);
        listPayments.setAdapter(adapter);
    }
}