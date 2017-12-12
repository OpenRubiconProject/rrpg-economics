package com.openrubicon.economics.database.models;

import com.openrubicon.core.api.database.DatabaseModel;
import com.openrubicon.core.api.database.interfaces.DatabaseMigration;
import com.openrubicon.economics.classes.PlayerAccount;
import com.openrubicon.economics.database.migrations.CreateAccount;
import com.openrubicon.economics.database.migrations.UpdateAccountsAddDates;

import java.util.Date;
import java.util.HashMap;

/**
 * Created by Quinn on 10/1/2017.
 */
public class AccountModel extends DatabaseModel<AccountModel> {

    private int id;
    private String uuid;
    private String name;
    private double bal;
    private Date created_at;
    private Date updated_at;
    private Date deleted_at;

    private String tablename = "rubicon_economics_accounts";
    private int version = 2;


    public AccountModel() {
    }

    public AccountModel(PlayerAccount pa){
        this.uuid = pa.getUser().getUniqueId().toString();
        this.name = pa.getUser().getName();
        this.bal = pa.getBalance();
        this.created_at = new Date();
        this.updated_at = new Date();
    }

    public double getBal() {
        return bal;
    }

    public void setBal(double bal) {
        this.bal = bal;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    /**
     * Inserts the current account model into the datqabase as a new entry
     * @return  true
     */
    public boolean insertInto(){
        this.insert("uuid, name, bal, created_at, updated_at", ":uuid, :name, :bal, :created_at, :updated_at").executeInsert();
        return true;
    }

    /**
     * Updates the current account model in the database
     * @return true
     */
    public boolean updateModel() {
        this.update().set("uuid, name, bal, created_at, updated_at", ":uuid, :name, :bal, :created_at, :updated_at").executeUpdate();
        return true;
    }

    /**
     * Gets the account from the database using the name specified
     * @return true
     */
    public boolean getAccount(){
        this.select("*").where("uuid = :uuid, name = :name").executeFetch(this.getClass());
        return true;
    }

    /**
     * Checks if the account exists in the database
     * @return if the account exists in the database
     */
    public boolean existsInDb(){
        if(this.count("id").where("uuid = :uuid, name = :name").executeCount() == 1)
            return true;
        return false;
    }

    @Override
    public HashMap<Integer, DatabaseMigration> getMigrations() {
        HashMap<Integer, DatabaseMigration> migrations = new HashMap<>();
        migrations.put(1, new CreateAccount());
        migrations.put(2, new UpdateAccountsAddDates());
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
