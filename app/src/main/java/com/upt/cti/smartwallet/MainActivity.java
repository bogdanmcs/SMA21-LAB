package com.upt.cti.smartwallet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.upt.cti.smartwallet.model.MonthlyExpenses;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private TextView tStatus;
    private EditText eIncome, eExpenses;
    private Spinner sSearch;
    // firebase
    private DatabaseReference databaseReference;

    // ...
    private String currentMonth;
    private ValueEventListener databaseListener;
    private SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ...
        tStatus = findViewById(R.id.tStatus);
        eIncome = findViewById(R.id.eIncome);
        eExpenses = findViewById(R.id.eExpenses);
        sSearch = findViewById(R.id.spinner);

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://smart-wallet-f28ef-default-rtdb.europe-west1.firebasedatabase.app/");
        databaseReference = database.getReference();

        // spinner
        final List<MonthlyExpenses> monthlyExpenses = new ArrayList<>();
        final List<String> monthNames = new ArrayList<>();

        final ArrayAdapter<String> sAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, monthNames);
        sAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sSearch.setAdapter(sAdapter);

//        sSearch.setOnItemSelectedListener(this);

        databaseReference.child("calendar").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot monthSnapshot : dataSnapshot.getChildren()) {
                    try {
                        MonthlyExpenses me = monthSnapshot.getValue(MonthlyExpenses.class);

                        if (me != null) {
                            me.month = monthSnapshot.getKey();

                            if (!monthNames.contains(me.month)){
                                monthlyExpenses.add(me);
                                monthNames.add(me.month);
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                // notify the spinner that data may have changed
                sAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        sSearch.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
                currentMonth = parent.getItemAtPosition(pos).toString();
                createNewDBListener();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    public void clicked(View view) {
        switch (view.getId()) {
            case R.id.bUpdate:
                if (databaseReference != null && currentMonth != null) {
                    updateDB();
                }
                else
                {
                    Toast.makeText(this, "db reference/current month is null", Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }
    private void updateDB() {
        String eIncomeText = eIncome.getText().toString();
        String eExpensesText = eExpenses.getText().toString();

        // texts in eIncome and eExpenses are not empty
        if (!eIncomeText.isEmpty() && !eExpensesText.isEmpty()) {
            try {
                // parse data to double
                double uIncome = Double.parseDouble(eIncomeText);
                double uExpenses = Double.parseDouble(eExpensesText);
                // get current month name from spinner
                String currentMonth = sSearch.getSelectedItem().toString();

                // call update children on database reference
                databaseReference.child("calendar").child(currentMonth).child("income").setValue(uIncome);
                databaseReference.child("calendar").child(currentMonth).child("expenses").setValue(uExpenses);
                Toast.makeText(this, "Update successful", Toast.LENGTH_SHORT).show();

            } catch (Exception e) {
                Toast.makeText(this, "Income and expenses must be in numeric format", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Income and expenses fields cannot be empty!", Toast.LENGTH_SHORT).show();
        }
    }

    private void createNewDBListener() {
        // remove previous databaseListener
        if (databaseReference != null && currentMonth != null && databaseListener != null)
            databaseReference.child("calendar").child(currentMonth).removeEventListener(databaseListener);

        databaseListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                MonthlyExpenses monthlyExpense = dataSnapshot.getValue(MonthlyExpenses.class);
                // explicit mapping of month name from entry key
                if (monthlyExpense != null) {
                    monthlyExpense.month = dataSnapshot.getKey();

                    if (currentMonth.equals(monthlyExpense.month)) {
                        eIncome.setText(String.valueOf(monthlyExpense.getIncome()));
                        eExpenses.setText(String.valueOf(monthlyExpense.getExpenses()));
                        tStatus.setText("Found entry for " + currentMonth);
                    }
                }
                else
                {
                    tStatus.setText("No entry found for " + currentMonth);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        };

        // set new databaseListener
        databaseReference.child("calendar").child(currentMonth).addValueEventListener(databaseListener);
    }
}