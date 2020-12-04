package com.zebra.rfid.demo.pslsdksample.modals;

public class TransactionTypes {
    public String getTransaction_id() {
        return transaction_id;
    }

    public void setTransaction_id(String transaction_id) {
        this.transaction_id = transaction_id;
    }

    public String getTransaction_description() {
        return transaction_description;
    }

    public void setTransaction_description(String transaction_description) {
        this.transaction_description = transaction_description;
    }

    public String getTransaction_location_type() {
        return transaction_location_type;
    }

    public void setTransaction_location_type(String transaction_location_type) {
        this.transaction_location_type = transaction_location_type;
    }

    public String getTransaction_modified_date() {
        return transaction_modified_date;
    }

    public void setTransaction_modified_date(String transaction_modified_date) {
        this.transaction_modified_date = transaction_modified_date;
    }

    public String getTransaction_is_active() {
        return transaction_is_active;
    }

    public void setTransaction_is_active(String transaction_is_active) {
        this.transaction_is_active = transaction_is_active;
    }

    String transaction_id,transaction_description,transaction_location_type,transaction_modified_date,transaction_is_active;


}
