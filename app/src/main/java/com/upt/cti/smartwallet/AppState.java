package com.upt.cti.smartwallet;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.upt.cti.smartwallet.model.Payment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class AppState implements Serializable {
    private static AppState singletonObject;

    public static synchronized AppState get() {
        if (singletonObject == null) {
            singletonObject = new AppState();
        }
        return singletonObject;
    }

    // reference to Firebase used for reading and writing data
    private DatabaseReference databaseReference;
    // current payment to be edited or deleted
    private Payment currentPayment;
    // current user
    private String userId;

    public DatabaseReference getDatabaseReference() {
        return databaseReference;
    }

    public void setDatabaseReference(DatabaseReference databaseReference) {
        this.databaseReference = databaseReference;
    }

    public void setCurrentPayment(Payment currentPayment) {
        this.currentPayment = currentPayment;
    }

    public Payment getCurrentPayment() {
        return currentPayment;
    }

    public static String getCurrentTimeDate() {
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date now = new Date();
        return sdfDate.format(now);
    }

    public void updateLocalBackup(Context context, Payment payment, boolean toAdd) {
        String fileName = payment.timestamp;

        try {
            if (toAdd) {
                // save to file
                FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
                ObjectOutputStream os = new ObjectOutputStream(fos);
                os.writeObject(payment.copy());
                os.close();
                fos.close();
            } else {
                context.deleteFile(fileName);
            }
        } catch (Exception e) { // IOException
            Toast.makeText(context, "Cannot access local data.", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean hasLocalStorage(Context context) {
        return Objects.requireNonNull(context.getFilesDir().listFiles()).length > 0; // context.getFilesDir().listFiles().length > 0
    }

    public List<Payment> loadFromLocalBackup(Context context) {
        try {
            List<Payment> payments = new ArrayList<>();

            for (File file : Objects.requireNonNull(context.getFilesDir().listFiles())) { // context.getFilesDir().listFiles()
//                if (/*only own files*/)
                {
                    // ...
                    FileInputStream fis = context.openFileInput(file.getName());
                    ObjectInputStream is = new ObjectInputStream(fis);
                    Payment payment = (Payment) is.readObject();
                    payments.add(payment);
                    is.close();
                    fis.close();
                }
            }

            return payments;
        } catch (Exception e) {
            Toast.makeText(context, "Cannot access local data.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        return null;
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void clearUserId() {
        this.userId = null;
        this.currentPayment = null;
        FirebaseAuth.getInstance().signOut();
    }
}
