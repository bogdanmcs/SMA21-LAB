package com.upt.cti.smartwallet;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.upt.cti.smartwallet.model.MonthlyExpenses;

public class MainActivity extends AppCompatActivity {
    private TextView tStatus;
    private EditText eSearch, eIncome, eExpenses;
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
        tStatus = (TextView) findViewById(R.id.tStatus);
        eSearch = (EditText) findViewById(R.id.eSearch);
        eIncome = (EditText) findViewById(R.id.eIncome);
        eExpenses = (EditText) findViewById(R.id.eExpenses);

        sharedPreferences = getSharedPreferences("prefs", Context.MODE_PRIVATE);
        eSearch.setText(sharedPreferences.getString("CURRENT_MONTH", null));

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://smart-wallet-f28ef-default-rtdb.europe-west1.firebasedatabase.app/");
        databaseReference = database.getReference();
    }

    public void clicked(View view) {
        switch (view.getId()) {
            case R.id.bSearch:
                if (!eSearch.getText().toString().isEmpty()) {
                    // save text to lower case (all our months are stored online in lower case)
                    currentMonth = eSearch.getText().toString().toLowerCase();
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("CURRENT_MONTH", currentMonth);
                    editor.apply();

                    tStatus.setText("Searching ...");
                    createNewDBListener();
                } else {
                    Toast.makeText(this, "Search field may not be empty", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.bUpdate:
                String eIncomeText = eIncome.getText().toString();
                String eExpensesText = eExpenses.getText().toString();

                // pre-conditions
                if (databaseReference != null && currentMonth != null) {
                    if (!eIncomeText.isEmpty() && !eExpensesText.isEmpty()) {
                        try {
                            int uIncome = Integer.parseInt(eIncomeText);
                            int uExpenses = Integer.parseInt(eExpensesText);
                            // write to db
                            databaseReference.child("calendar").child(currentMonth).child("income").setValue(uIncome);
                            databaseReference.child("calendar").child(currentMonth).child("expenses").setValue(uExpenses);
                            Toast.makeText(this, "Update successful", Toast.LENGTH_SHORT).show();

                        }catch(NumberFormatException e) {
                            Toast.makeText(this, "Income/Expenses fields must be numerical", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else
                    {
                        Toast.makeText(this, "Income/Expenses fields must be filled", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(this, "db reference/current month is null", Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }

    private void createNewDBListener() {
        // remove previous databaseListener
        if (databaseReference != null && currentMonth != null && databaseListener != null)
            databaseReference.child("calendar").child(currentMonth).removeEventListener(databaseListener);

        databaseListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                MonthlyExpenses monthlyExpense = dataSnapshot.getValue(MonthlyExpenses.class);
                // explicit mapping of month name from entry key
                monthlyExpense.month = dataSnapshot.getKey();

                eIncome.setText(String.valueOf(monthlyExpense.getIncome()));
                eExpenses.setText(String.valueOf(monthlyExpense.getExpenses()));
                tStatus.setText("Found entry for " + currentMonth);
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        };

        // set new databaseListener
        databaseReference.child("calendar").child(currentMonth).addValueEventListener(databaseListener);
    }
}