package com.openrubicon.economics.classes;

import com.Acrobot.ChestShop.Events.TransactionEvent;
import com.openrubicon.core.api.database.interfaces.PostDatabaseLoad;
import com.openrubicon.core.api.events.Event;
import com.openrubicon.core.api.vault.economy.EconomyResponse;
import com.openrubicon.economics.RRPGEconomics;
import com.openrubicon.economics.database.models.AccountModel;
import com.openrubicon.economics.database.models.TransactionModel;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static com.openrubicon.core.RRPGCore.plugin;

public class Economy extends com.openrubicon.core.api.vault.economy.Economy implements PostDatabaseLoad {

    private HashMap<OfflinePlayer, PlayerAccount> accountHash = new HashMap<OfflinePlayer, PlayerAccount>();
    private final String name = "Rubies";

    public Economy(Plugin plugin) {
        super(plugin);
    }

    @Override
    public void run() {
        //Create an economy listener to register economy event handlers.
        new EconomyServerListener();
        //new TransactionEventListener();

        //Load database entries into the table on restarts
        loadHashMap();

    }

    public class EconomyServerListener implements Listener {

        @EventHandler
        public void onPlayerJoin(PlayerJoinEvent event){
            OfflinePlayer player = event.getPlayer();
            //When a player joins the server, if they don't have an account, make them an account
            if(!hasAccount(player)){
                createPlayerAccount(player);
            }
        }

        public EconomyServerListener(){
            Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
        }
    }

    public class TransactionEventListener extends Event implements Listener{

        @EventHandler
        public void onTransactionEvent(com.Acrobot.ChestShop.Events.TransactionEvent e){
            //ADD TO TRANSACTION HISTORY HERE

            Sign s = e.getSign();
            Double amount = e.getPrice();
            String clientReason = "selling";
            String ownerReason = "buying";
            if(e.getTransactionType() == TransactionEvent.TransactionType.BUY){
                amount = amount * -1;
                clientReason = "buying";
                ownerReason = "selling";
            }
            clientReason += " " + e.getSign().getLine(1) + " " + e.getSign().getLine(3);
            ownerReason += " " + e.getSign().getLine(1) + " " + e.getSign().getLine(3);

            Double divisor = Math.pow(10, RRPGEconomics.economy.fractionalDigits());
            Double d = new Double((Math.round(amount * divisor) / divisor));

            //Fires the event handler for a new transaction event
            Transaction t = new Transaction(e.getClient(), e.getOwner(), d, clientReason, new Date());

            accountHash.get(e.getClient().getUniqueId().toString()).addTransaction(t);
            accountHash.get(e.getOwner().getUniqueId().toString()).addTransaction(t);
            return;
        }

        public TransactionEventListener(){
            Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
        }
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer player) {
        //If the player has never played before but the player is now online, make them an account
        if (player.hasPlayedBefore() && player.isOnline()){
            String uuid = player.getUniqueId().toString();
            //If their uuid is not in the database, check the hashmap.
            if(!existsInDatabase(player)) {
                //If their account does not exist in the hast map, we will create a new account for them
                if(!accountHash.containsKey(player)) {
                    //Create a new account for them
                    PlayerAccount account = new PlayerAccount(player, 0.0);

                    //Attempt to save to database
                    if(new AccountModel(account).insertInto()){
                        accountHash.put(player, account);
                        return true;
                    } else {
                        return false;
                    }
                }
                //If the account exists in the hash map but not in the database, nothing needs to be done
                //Will regularly update accounts.
                return true;
            }
            //If the player exists in the database, the player has an account
            return true;
        }
        //If the player is offline or the player has not player on the server, we shouldn't be creating an account for them
        return false;
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, double amount) {

        if(hasAccount(player)){
            if(has(player, amount)){
                //Withdraw from the account
                accountHash.get(player).alterAccount(-1 * amount);

                Double divisor = Math.pow(10, RRPGEconomics.economy.fractionalDigits());
                Double d = new Double((Math.round(accountHash.get(player).getBalance() * divisor) / divisor));

                //update the database
                if(new AccountModel(accountHash.get(player)).insertInto()){
                    return EconomyResponse.Success();
                } else {
                    return EconomyResponse.Failure("SQL error");
                }

            } else {
                return EconomyResponse.Failure(player.getName() + " does not have enough money.");
            }
        }
        return EconomyResponse.Failure(player.getName() + " does not have an account.");
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, double amount) {

        String uuid = player.getUniqueId().toString();
        if(hasAccount(player)){
            //Deposit to the account
            accountHash.get(player).alterAccount(1 * amount);

            Double divisor = Math.pow(10, RRPGEconomics.economy.fractionalDigits());
            Double d = new Double((Math.round(accountHash.get(player).getBalance() * divisor) / divisor));

            //update the database
            if(new AccountModel(accountHash.get(player)).updateModel()){
                return EconomyResponse.Success();
            } else {
                //Undo if the database cannot update
                accountHash.get(player).alterAccount(-1 * amount);
            }

        }
        return EconomyResponse.Failure(player.getName() + " does not have an account.");
    }

    @Override
    public List<String> getBanks() {
        return null;
    }

    @Override
    public double getBalance(OfflinePlayer player) {
        Bukkit.getLogger().info(accountHash.size() + " entires in the hash map.");
        accountHash.forEach((key, value) -> {
            Bukkit.getLogger().info("uuid : " + key + " Account : " + value);
        });

        //Check if the user has played on the server.
        if (player.hasPlayedBefore()){
            //If the player has played on the server, check if they exist in the hash map
            String uuid = player.getUniqueId().toString();
            if(accountHash.containsKey(uuid)){
                //Return the balance
                return accountHash.get(uuid).getBalance();
            } else {
                //If the player has played on the server but doesn't have an account, we will create an account.
                createPlayerAccount(player);

                Double divisor = Math.pow(10, RRPGEconomics.economy.fractionalDigits());
                Double d = new Double((Math.round(accountHash.get(uuid).getBalance() * divisor) / divisor));

                return d;
            }
        }
        return 0;
    }

    @Override
    public boolean has(OfflinePlayer player, double amount) {
        if (player.hasPlayedBefore()) {
            if (accountHash.containsKey(player)) {
                PlayerAccount account = accountHash.get(player);
                if (account.getBalance() >= amount) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean hasBankSupport() {
        return false;
    }

    @Override
    public int fractionalDigits() {
        return 2;
    }

    @Override
    public String format(double v) {
        return v + "r";
    }

    @Override
    public String currencyNamePlural() {
        return "Rubies";
    }

    @Override
    public String currencyNameSingular() {
        return "Ruby";
    }

    @Override
    public boolean hasAccount(OfflinePlayer offlinePlayer) {
        return false;
    }

    private void loadHashMap() {
        ArrayList<AccountModel> results = (ArrayList<AccountModel>) new AccountModel().selectAll().executeFetch(AccountModel.class);
        if(results != null) {
            for(AccountModel am : results)
            {
                OfflinePlayer p = Bukkit.getOfflinePlayer(am.getName());
                accountHash.put(p, new PlayerAccount(am));
            }
        }
    }

    private boolean existsInDatabase(OfflinePlayer p){
        AccountModel am = new AccountModel();
        am.setName(p.getName());
        am.setUuid(p.getUniqueId().toString());
        return am.existsInDb();
    }

    private AccountModel getFromDatabase(OfflinePlayer p){

        AccountModel am = new AccountModel();
        am.setName(p.getName());
        am.setUuid(p.getUniqueId().toString());
        am.getAccount();
        return am;
    }

    public PlayerAccount getAccount(OfflinePlayer p){
        if(accountHash.containsKey(p))
            return accountHash.get(p);
        else return null;
    }


}
