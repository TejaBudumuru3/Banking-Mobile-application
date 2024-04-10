package com.example.project1;

public class MiniStatements {
    String remainingBalance;
    String debitedBalance;
    String Time;

    public MiniStatements()
    {

    }

    public MiniStatements(String remainingBalance,String debitedBalance,String Time)
    {
        this.remainingBalance = remainingBalance;
        this.debitedBalance = debitedBalance;
        this.Time = Time;
    }



    public String getRemainingBalance() {
        return remainingBalance;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public void setRemainingBalance(String remainingBalance) {
        this.remainingBalance = remainingBalance;
    }

    public String getDebitedBalance() {
        return debitedBalance;
    }

    public void setDebitedBalance(String debitedBalance) {
        this.debitedBalance = debitedBalance;
    }
}
