package com.openrubicon.economics.classes;

import com.google.common.collect.EvictingQueue;
import com.openrubicon.economics.classes.Transaction;
import com.openrubicon.economics.database.models.AccountModel;
import com.openrubicon.economics.database.models.TransactionModel;
import com.openrubicon.economics.events.PlayerViewHistory;
import com.openrubicon.economics.RRPGEconomics;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.Event;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
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

    public void alterAccount(double change){
        balance += change;
    }

    public double getBalance(){
        return balance;
    }

    public OfflinePlayer getUser(){
        return user;
    }

    public void displayTransactions(OfflinePlayer thePlayer, int page){

        //Throw and event.
        Event event = new PlayerViewHistory(this, page);
        Bukkit.getPluginManager().callEvent(event);

        //get the most recent transactions for the player.
        populateTransactions(thePlayer);

        ArrayList<TransactionModel> results = new TransactionModel().getAccountTransactions(thePlayer);
        int totalTransactions = results.size();

        if (transactionHistory.size() == 0){
            Bukkit.getPlayer(thePlayer.getName()).sendMessage("Page 0 of 0");
            Bukkit.getPlayer(thePlayer.getName()).sendMessage("No recent transactions.");
            return;
        } else {
            int totalpages = (int)Math.ceil(new Double((totalTransactions / 10)));
            if (page > totalpages){
                return;
            }
            Bukkit.getPlayer(thePlayer.getName()).sendMessage("Page " + page + " of " + totalpages);
        }

        //If the transactions required are not in the queue, load them from the database)
        if((transactionHistory.size() == capacity) && (page > 2)){
            loadTransactions(thePlayer, results, page, totalTransactions);
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
                Bukkit.getPlayer(thePlayer.getName()).sendMessage((count) + ". " + history.showTransaction(thePlayer));
                count--;
                //output.add(history);
            }
            i++;
        }
    }

    public void loadTransactions(OfflinePlayer thePlayer, ArrayList<TransactionModel> results, int page, int totalresults){

        //set the iterator to loop through the transactions
        int stop = totalresults - (resultsPerPage * (page - 1));
        int start = stop - (resultsPerPage - 1);
        //int i= ((resultsPerPage * page) - 1);

        for(int i=stop; i>start; i--){
            Transaction t = new Transaction(results.get(i));
            Bukkit.getPlayer(thePlayer.getName()).sendMessage((i)+". " + t.showTransaction(thePlayer));
        }
    }

    public Double getRoundedBalance(){
        Double divisor = Math.pow(10, RRPGEconomics.economy.fractionalDigits());
        return new Double((Math.round(balance * divisor) / divisor));
    }

    public void addTransaction(Transaction transaction){
        transactionHistory.add(transaction);
    }

    public void populateTransactions(OfflinePlayer thePlayer){

        //Check the database for the transaction history of this account.
        ArrayList<TransactionModel> r = new TransactionModel().getAccountTransactions(thePlayer);

        for(int i=r.size(); i>r.size() - 20; i--){
            Transaction t = new Transaction(r.get(i));
            transactionHistory.add(t);
        }
    }
}
