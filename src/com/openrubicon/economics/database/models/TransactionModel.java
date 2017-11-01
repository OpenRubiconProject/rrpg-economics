package com.openrubicon.economics.database.models;

import com.openrubicon.core.api.database.DatabaseModel;
import com.openrubicon.core.api.database.interfaces.DatabaseMigration;
import com.openrubicon.economics.classes.Transaction;
import com.openrubicon.economics.database.migrations.CreateTransaction;
import org.bukkit.OfflinePlayer;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by Quinn on 10/1/2017.
 */
public class TransactionModel extends DatabaseModel<TransactionModel> {

    private int id;
    private String decreasedUuid;
    private String increasedUuid;
    private double amount;
    private String comment;
    private Date timestamp;

    private String tablename = "rubicon_economics_transactions";
    private int version = 1;

    public TransactionModel(Transaction t){
        decreasedUuid = t.getDecreasedPlayer().getUniqueId().toString();
        increasedUuid = t.getIncreasedPlayer().getUniqueId().toString();
        amount = t.getAmount();
        comment = t.getReason();
        timestamp = t.getTimestamp();
    }

    public TransactionModel(){
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDecreasedUuid() {
        return decreasedUuid;
    }

    public void setDecreasedUuid(String decreasedUuid) {
        this.decreasedUuid = decreasedUuid;
    }

    public String getIncreasedUuid() {
        return increasedUuid;
    }

    public void setIncreasedUuid(String increasedUuid) {
        this.increasedUuid = increasedUuid;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getTablename() {
        return tablename;
    }

    public void setTablename(String tablename) {
        this.tablename = tablename;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    /**
     * Inserts a new transaction into the database table
     * @return true
     */
    public boolean insertInto(){
        this.timestamp = new Date();
        this.insert("decreasedUuid, increasedUuid, amount, comment, timestamp", ":decreasedUuid, :increasedUuid, :amount, :comment, :timestamp").executeInsert();
        return true;
    }

    /**
     * Gets an arraylist of all transactions that the specified account has participated in.
     * @param p     The player to get transactions for
     * @return      ArrayList of transactionModels where player p was involved
     */
    public ArrayList<TransactionModel> getAccountTransactions(OfflinePlayer p){
        this.setDecreasedUuid(p.getUniqueId().toString());
        return (ArrayList<TransactionModel>) this.select("*").where("decreased_uuid = :decreased_uuid OR increased_uuid = :decreased_uuid").executeFetch(TransactionModel.class);
    }

    @Override
    public HashMap<Integer, DatabaseMigration> getMigrations() {
        HashMap<Integer, DatabaseMigration> migrations = new HashMap<>();
        migrations.put(1, new CreateTransaction());
        return migrations;
    }

    @Override
    public String getTableName() {
        return tablename;
    }

    @Override
    public int getVersion() {
        return version;
    }
}
