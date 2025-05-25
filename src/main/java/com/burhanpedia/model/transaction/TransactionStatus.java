package com.burhanpedia.model.transaction;

import java.util.Date;

public class TransactionStatus {
    private Date timestamp;
    private String status;
    
    public TransactionStatus(Date timestamp, String status) {
        this.timestamp = timestamp;
        this.status = status;
    }
    
    public Date getTimestamp() {
        return timestamp;
    }
    
    public String getStatus() {
        return status;
    }
}