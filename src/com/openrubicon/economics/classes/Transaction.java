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
     * Creates a new transaction
     * @param name Name of player that is incurring the transaction
     * @param amount    The amount of the transaction
     * @param reason    The reason the transaction occurred
     * @param date      The date the transaction took place
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

    /**
     * Creates a transaction when interacting with another player
     * @param name  The first player of the transaction
     * @param from  The second player of the transaction
     * @param amount    The amount that the first player's account has changed.
     * @param reason    The reason for the transaction
     * @param date      The date of the transaction
     */
    public Transaction(OfflinePlayer name, OfflinePlayer from, double amount, String reason, Date date){
        if(amount < 0) {
            decreasedPlayer = name;
            increasedPlayer = from;
        } else {
            decreasedPlayer = from;
            increasedPlayer = name;
        }
        transactionAmount = amount;
        comment = reason;
        Timestamp = date;

        Event event = new TransactionEvent(this);
        Bukkit.getPluginManager().callEvent(event);
    }

    /**
     * Creates a transaction based off of a TransactionModel created from the database
     * @param t     The transaction model to create the transaction based off
     */
    public Transaction(TransactionModel t){
        //The onTransactionEvent will not be called when loading from a transaction model.
        //This is because the transaction already exists if you are loading it from a model.

        this.decreasedPlayer = Bukkit.getOfflinePlayer(t.getDecreasedUuid());
        this.increasedPlayer = Bukkit.getOfflinePlayer(t.getIncreasedUuid());
        this.transactionAmount = t.getAmount();
        this.comment = t.getComment();
        this.Timestamp = t.getTimestamp();
    }

    /**
     * Gets the player who was decreased by the transaction
     * @return  Player account who decreased balance
     */
    public OfflinePlayer getDecreasedPlayer(){
        return decreasedPlayer;
    }

    /**
     * Gets the player who increased balance by the transaction
     * @return  The palyer who increased balance
     */
    public OfflinePlayer getIncreasedPlayer(){ return increasedPlayer;}

    /**
     * Gets the amount of the transaction
     * @return  The amount of the transaction
     */
    public double getAmount(){
        return transactionAmount;
    }

    /**
     * Gets the date of the transaction
     * @return  The date of the transaction
     */
    public Date getTimestamp(){
        return Timestamp;
    }

    /**
     * Gets the reason for the transaction
     * @return  The reason for the trasnaction
     */
    public String getReason(){
        return comment;
    }

    /**
     * Displays the text of a single transaction to the player
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
