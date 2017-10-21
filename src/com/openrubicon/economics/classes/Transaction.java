package com.openrubicon.economics.classes;


import com.openrubicon.core.RRPGCore;
import com.openrubicon.economics.database.models.TransactionModel;
import com.openrubicon.economics.events.TransactionEvent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.Event;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static java.lang.Math.abs;

/**
 * Created by Quinn on 5/28/2017.
 */
public class Transaction {

    private OfflinePlayer decreasedPlayer;
    private OfflinePlayer increasedPlayer;
    private double transactionAmount;
    private String comment;
    private Date Timestamp;

    /**
     *
     * @param name Name of player recieving
     * @param amount
     * @param reason
     * @param date
     */
    public Transaction(OfflinePlayer name, double amount, String reason, Date date)
    {
        decreasedPlayer = name;
        transactionAmount = amount;
        comment = reason;
        Timestamp = date;

        Event event = new TransactionEvent(this);
        Bukkit.getPluginManager().callEvent(event);
    }

    public Transaction(OfflinePlayer name, OfflinePlayer from, double amount, String reason, Date date){
        decreasedPlayer = name;
        increasedPlayer = from;
        transactionAmount = amount;
        comment = reason;
        Timestamp = date;

        Event event = new TransactionEvent(this);
        Bukkit.getPluginManager().callEvent(event);
    }

    public Transaction(TransactionModel t){
        //The onTransactionEvent will not be called when loading from a transaction model.
        //This is because the transaction already exists if you are loading it from a model.

        this.decreasedPlayer = Bukkit.getOfflinePlayer(t.getDecreasedUuid());
        this.increasedPlayer = Bukkit.getOfflinePlayer(t.getIncreasedUuid());
        this.transactionAmount = t.getAmount();
        this.comment = t.getComment();
        this.Timestamp = t.getTimestamp();
    }

    public OfflinePlayer getDecreasedPlayer(){
        return decreasedPlayer;
    }

    public OfflinePlayer getIncreasedPlayer(){ return increasedPlayer;}

    public double getAmount(){
        return transactionAmount;
    }

    public Date getTimestamp(){
        return Timestamp;
    }

    public String getReason(){
        return comment;
    }

    /**
     *
     * @param thePlayer to send the transaction information to.
     */
    public String showTransaction(OfflinePlayer thePlayer){
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

        String reportDate = df.format(Timestamp);

        if (thePlayer.equals(decreasedPlayer)) {
            return "Sent " + abs(transactionAmount) + " to " + decreasedPlayer.getName() + " for " + comment + " on " + reportDate;
        } else {
            return "Recieved " + abs(transactionAmount) + " from " + increasedPlayer.getName() + " for " + comment + " on " + reportDate;
        }
    }
}
