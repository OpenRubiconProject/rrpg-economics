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
    private int capacity = 20;
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

        //get the most recent transactions for the player.
        populateTransactions();

        ArrayList<TransactionModel> results = new TransactionModel().getAccountTransactions(user);
        int totalTransactions = results.size();

        if (transactionHistory.size() == 0){
            Bukkit.getPlayer(user.getName()).sendMessage("Page 0 of 0");
            Bukkit.getPlayer(user.getName()).sendMessage("No recent transactions.");
            return;
        } else {
            int totalpages = (int)Math.ceil(new Double((totalTransactions / 10)));
            if (page > totalpages){
                return;
            }
            Bukkit.getPlayer(user.getName()).sendMessage("Page " + page + " of " + totalpages);
        }

        //If the transactions required are not in the queue, load them from the database)
        if((transactionHistory.size() == capacity) && (page > 2)){
            loadTransactions(results, page, totalTransactions);
            return;
        }

        //set the iterator to loop through the transactions
        int i=0;
        int stop = capacity - (resultsPerPage * (page - 1));
        int start = stop - (resultsPerPage - 1);

        int count= ((resultsPerPage * page) - 1);

        //Stack<TransactionModel> output = new Stack<TransactionModel>();
        for (Transaction history : transactionHistory) {
            if (i >= start && i <= stop) {
                Bukkit.getPlayer(user.getName()).sendMessage((count) + ". " + history.showTransaction(user));
                count--;
                //output.add(history);
            }
            i++;
        }
    }

    /**
     * A helper function for the displayTransactions function. If the player is requesting a list of transactions that are not in
     * the queue, this function will load the required transactions from the database.
     * @param results   The transaction results to load
     * @param page      The page number requested
     * @param totalresults  The total number of transactions that the player has
     */
    private void loadTransactions(ArrayList<TransactionModel> results, int page, int totalresults){

        //set the iterator to loop through the transactions
        int stop = totalresults - (resultsPerPage * (page - 1));
        int start = stop - (resultsPerPage - 1);
        //int i= ((resultsPerPage * page) - 1);

        for(int i=stop; i>start; i--){
            Transaction t = new Transaction(results.get(i));
            Bukkit.getPlayer(user.getName()).sendMessage((i)+". " + t.showTransaction(user));
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

        for(int i=r.size(); i>r.size() - 20; i--){
            Transaction t = new Transaction(r.get(i));
            transactionHistory.add(t);
        }
    }
}
