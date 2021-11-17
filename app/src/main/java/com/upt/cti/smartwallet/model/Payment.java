package com.upt.cti.smartwallet.model;

import androidx.annotation.NonNull;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Payment {

    public String timestamp;
    private double cost;
    private String name;
    private String type;

    public Payment() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Payment(double cost, String name, String type) {
        this.cost = cost;
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public double getCost() {
        return cost;
    }

    public String getType() {
        return type;
    }

    @NonNull
    public String toString(){
        return "( " + timestamp + ", " + cost + ", " + name + ", " + type + " )";
    }

    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Payment))
            return false;
        Payment other = (Payment)o;

        return this.timestamp.equals(other.timestamp) &&
                this.type.equals(other.type) &&
                this.name.equals(other.name) &&
                this.cost == other.cost;
    }
}
