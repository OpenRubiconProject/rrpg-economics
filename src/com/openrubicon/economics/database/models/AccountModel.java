package com.openrubicon.economics.database.models;

import com.openrubicon.core.api.database.DatabaseModel;
import com.openrubicon.core.api.database.interfaces.DatabaseMigration;
import com.openrubicon.economics.classes.PlayerAccount;
import com.openrubicon.economics.database.migrations.CreateAccount;
import com.openrubicon.economics.database.migrations.UpdateAccountsAddDates;
import org.apache.commons.lang.ObjectUtils;
import org.bukkit.Bukkit;

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

    public Date getCreated_at(){return this.created_at;}

    public void setCreated_at(Date date){
        this.created_at = date;
    }

    public Date getUpdated_at(){return this.updated_at;}

    public void setUpdated_at(Date date){
        this.updated_at = date;
    }

    public Date getDeleted_at(){return this.deleted_at;}

    public void setDeleted_at(Date date){
        this.deleted_at = date;
    }


    /**
     * Inserts the current account model into the datqabase as a new entry
     * @return  true
     */
    public boolean insertInto(){
        this.insert("uuid, name, bal", ":uuid, :name, :bal").executeInsert();
        return true;
    }

    /**
     * Updates the current account model in the database
     * @return true
     */
    public boolean updateModel() {
        this.update().set("uuid", ":uuid").set("name", ":name").set("bal", ":bal").executeUpdate();
        return true;
    }

    /**
     * Gets the account from the database using the name specified
     * @return true
     */
    public AccountModel getAccount(){
        return this.selectAll().where("uuid", ":uuid").where("name", ":name").executeFetchFirst(AccountModel.class);
    }

    /**
     * Checks if the account exists in the database
     * @return if the account exists in the database
     */
    public boolean existsInDb(){
        if(this.count("id").where("uuid = :uuid and name = :name").executeCount() == 1)
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

    /**
     * Update an existing database model using a player account.
     * @param playerAccount The player account
     */
    public boolean updateWithAccount(PlayerAccount playerAccount){

        if(playerAccount.getUser().getName().equals(this.name) && playerAccount.getUser().getUniqueId().toString().equals(this.uuid)){
            //set the new balance
            this.bal = playerAccount.getBalance();
            return this.updateModel();
        } else {
            return false;
        }
    }
}
