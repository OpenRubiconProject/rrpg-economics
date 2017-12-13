package com.openrubicon.economics.classes;

import com.google.common.collect.EvictingQueue;
import com.openrubicon.economics.database.models.AccountModel;
import com.openrubicon.economics.database.models.TransactionModel;
import com.openrubicon.economics.events.PlayerViewHistory;
import com.openrubicon.economics.RRPGEconomics;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.Event;

import java.util.ArrayList;
import java.util.Queue;

/**
 * Created by Quinn on 5/13/2017.
 */
public class PlayerAccount{

    OfflinePlayer user;
    private String worldName;
    private int resultsPerPage = 10;
    private int capacity = 100;
    private Queue<Transaction> transactionHistory = EvictingQueue.create(capacity);
    private double balance;

    public PlayerAccount(){
    }

    public PlayerAccount(AccountModel m){
        this.user = Bukkit.getOfflinePlayer(m.getName());
        this.balance = m.getBal();
    }

    public PlayerAccount(OfflinePlayer p){
        this.user = p;
        balance = 0;
    }

    public PlayerAccount(OfflinePlayer p, Double balance){
        this.user = p;
        this.balance = balance;
    }

    /**
     * Add or remove from a player's balance
     * @param change    The amount to change the player's balance by
     */
    public void alterAccount(double change){
        balance += change;
    }

    /**
     * Returns the balance of the account
     * @return  The balance of the account
     */
    public double getBalance(){
        return balance;
    }

    /**
     * Returns the player who ows the account
     * @return  The owner of the account
     */
    public OfflinePlayer getUser(){
        return user;
    }

    /**
     * Shows a page of the player's transaction history
     * @param page  The desired page to show
     */
    public void displayTransactions(int page){

        //Throw an event.
        Event event = new PlayerViewHistory(this, page);
        Bukkit.getPluginManager().callEvent(event);

        if(transactionHistory.size() == 0) {
            //get the 100 most recent transactions for the player if not loaded.
            populateTransactions();
        }

        if (transactionHistory.size() == 0){
            Bukkit.getPlayer(user.getName()).sendMessage("Page 0 of 0");
            Bukkit.getPlayer(user.getName()).sendMessage("No recent transactions.");
            return;
        } else {
            double totalpages = Math.ceil(new Double((transactionHistory.size() / 10.0f)));
            if (page > totalpages){
                return;
            }
            Bukkit.getPlayer(user.getName()).sendMessage("Page " + (page + 1) + " of " + (int)totalpages);
        }

        //set the iterator to loop through the transactions
        int i=0;
        int start = page * resultsPerPage;
        int stop = start + resultsPerPage + 1;

        Bukkit.broadcastMessage("start:" + start);
        Bukkit.broadcastMessage("stop:" + stop);

        int count = start + 1;

        //Stack<TransactionModel> output = new Stack<TransactionModel>();
        for (Transaction history : transactionHistory) {
            if (i >= start && i <= stop && i <= 100) {
                Bukkit.getPlayer(user.getName()).sendMessage((count) + ". " + history.showTransaction(user));
                count++;
                //output.add(history);
            }
            i++;
        }
    }

    /**
     * Gets the balance conforming to the economy's decimal places
     * @return  The balance of the account with the economy's decimal places.
     */
    public Double getRoundedBalance(){
        Double divisor = Math.pow(10, RRPGEconomics.economy.fractionalDigits());
        return new Double((Math.round(balance * divisor) / divisor));
    }

    /**
     * Adds a transaction to the player's transaction history
     * @param transaction   The transaction to add to the player's history
     */
    public void addTransaction(Transaction transaction){
        transactionHistory.add(transaction);
    }

    /**
     * Loads the 20 most recent transactions by the player into the transaction queue
     */
    public void populateTransactions(){

        //Check the database for the transaction history of this account.
        ArrayList<TransactionModel> r = new TransactionModel().getAccountTransactions(user);

        for(int i=(r.size() - 1); (i > r.size() - (capacity - 1)) && (i > 0); i--){
            Transaction t = new Transaction(r.get(i));
            transactionHistory.add(t);
        }
    }
}
