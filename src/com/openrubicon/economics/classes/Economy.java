package com.openrubicon.economics.classes;

import com.Acrobot.ChestShop.Events.TransactionEvent;
import com.openrubicon.core.api.database.interfaces.PostDatabaseLoad;
import com.openrubicon.economics.RRPGEconomics;
import com.openrubicon.economics.classes.PlayerAccount;
import com.openrubicon.economics.classes.Transaction;
import com.openrubicon.economics.database.models.AccountModel;
import com.openrubicon.economics.database.models.TransactionModel;
import net.milkbowl.vault.economy.AbstractEconomy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

public class Economy extends AbstractEconomy implements PostDatabaseLoad
{

    private static final Logger log = Logger.getLogger("Minecraft");
    private final String name = "Economics Economy";
    private Plugin plugin = null;
    private HashMap<OfflinePlayer, PlayerAccount> accountHash = new HashMap<OfflinePlayer, PlayerAccount>();

    public Economy(Plugin plugin) {
        //log.info(String.format("[Economy] %s found: %s", name, this.isEnabled() ? "Loaded" : "Waiting"));
        plugin.getLogger().info("Created: " + name);
        this.plugin = plugin;
        //Create an economy listener to register economy event handlers.
        new EconomyServerListener();
        new TransactionEventListener();
    }

    public HashMap<OfflinePlayer, PlayerAccount> getHashMap(){
        return this.accountHash;
    }

    @Override
    public void run() {
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

    public class TransactionEventListener implements Listener{
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

            Transaction t = new Transaction(e.getClient(), e.getOwner(), d, clientReason, new Date());

            accountHash.get((OfflinePlayer)e.getClient()).addTransaction(t);
            accountHash.get(e.getOwner()).addTransaction(t);
            //Make a new database model for the transaction and insert it into the database.
            new TransactionModel(t).insertInto();
            return;
        }

        public TransactionEventListener(){
            Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
        }
    }

    @Override
    public boolean hasAccount(OfflinePlayer player) {
        if (player.hasPlayedBefore()) {
            if (accountHash.containsKey(player)) {
                return true;
            } else {
                return createPlayerAccount(player);
            }
        }
        return false;
    }

    @Override
    public double getBalance(OfflinePlayer player){
        Bukkit.getLogger().info(accountHash.size() + " entires in the hash map.");
        accountHash.forEach((key, value) -> {
            Bukkit.getLogger().info("uuid : " + key + " AccountModel : " + value);
        });

        //Check if the user has played on the server.
        if (player.hasPlayedBefore()){
            //If the player has played on the server, check if they exist in the hash map
            if(accountHash.containsKey(player)){
                //Return the balance
                return accountHash.get(player).getBalance();
            } else {
                //If the player has played on the server but doesn't have an account, we will create an account.
                createPlayerAccount(player);

                Double divisor = Math.pow(10, RRPGEconomics.economy.fractionalDigits());
                Double d = new Double((Math.round(accountHash.get(player).getBalance() * divisor) / divisor));

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
    public EconomyResponse withdrawPlayer(OfflinePlayer player, double amount) {
        if(hasAccount(player)){
            if(has(player, amount)){
                //Withdraw from the account
                accountHash.get(player).alterAccount(-1 * amount);

                Double divisor = Math.pow(10, RRPGEconomics.economy.fractionalDigits());
                Double d = new Double((Math.round(accountHash.get(player).getBalance() * divisor) / divisor));

                //update the database
                if(new AccountModel(accountHash.get(player)).insertInto()){
                    return new EconomyResponse(amount, 0, EconomyResponse.ResponseType.SUCCESS, "");
                } else {
                    return new EconomyResponse(amount, 0, EconomyResponse.ResponseType.FAILURE, "SQL error");
                }

            } else {
                return new EconomyResponse(amount, 0, EconomyResponse.ResponseType.FAILURE, player.getName() + " does not have enough money.");
            }
        }
        return new EconomyResponse(amount, 0, EconomyResponse.ResponseType.FAILURE, player.getName() + " does not have an account.");
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
                return new EconomyResponse(amount, accountHash.get(player).getBalance(), EconomyResponse.ResponseType.SUCCESS, "");
            } else {
                //Undo if the database cannot update
                accountHash.get(player).alterAccount(-1 * amount);
            }

        }
        return new EconomyResponse(amount, 0, EconomyResponse.ResponseType.FAILURE, player.getName() + " does not have an account.");
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

    /**
     * Creates an account for a player in the specified world.
     * @param player
     * @param worldName
     * @return
     */
    @Override
    public boolean createPlayerAccount(OfflinePlayer player, String worldName) {
        return createPlayerAccount(player);
    }

    public EconomyResponse createBank(String name, String player) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.SUCCESS, "");
    }

    public EconomyResponse deleteBank(String name) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.SUCCESS, "");
    }

    public EconomyResponse bankHas(String name, double amount) {
        return new EconomyResponse(amount, 0, EconomyResponse.ResponseType.SUCCESS, "");
    }

    public EconomyResponse bankWithdraw(String name, double amount) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.SUCCESS, "");
    }

    public EconomyResponse bankDeposit(String name, double amount) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.SUCCESS, "");
    }

    public EconomyResponse isBankOwner(String name, String playerName) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.SUCCESS, "");
    }

    public EconomyResponse isBankMember(String name, String playerName) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.SUCCESS, "");
    }

    public EconomyResponse bankBalance(String name) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.SUCCESS, "");
    }

    public List<String> getBanks() {
        return null;
    }

    /**
     * DONT USE THIS FUNCTION
     * @param s
     * @return
     */
    @Override
    public double getBalance(String s) {
        return 0;
    }

    /**
     * DONT USE THIS FUNCTION
     * @param player
     * @return
     */
    @Override
    public boolean hasAccount(String player)
    {
        return false;
    }

    /**
     * DONT USE THIS FUNCTION
     * @param player
     * @param worldName
     * @return
     */
    @Override
    public boolean hasAccount(String player, String worldName)
    {
        return false;
    }

    /**
     * DONT USE THIS FUNCTION
     * @param s
     * @param s1
     * @return
     */
    @Override
    public boolean createPlayerAccount(String s, String s1) {
        return false;
    }

    /**
     * DON'T USE THIS FUNCTION
     * @param s
     * @param v
     * @return
     */
    @Override
    public EconomyResponse depositPlayer(String s, double v) {
        return null;
    }

    /**
     * FUNCTION SIGNATURE FOR VAULT INHERITANCE ONLY
     * @param s
     * @param s1
     * @param v
     * @return
     */
    @Override
    public EconomyResponse withdrawPlayer(String s, String s1, double v) {
        return null;
    }
    /**
     * FUNCTION SIGNATURE FOR VAULT INHERITANCE ONLY
     * @param s
     * @param v
     * @return
     */
    @Override
    public EconomyResponse withdrawPlayer(String s, double v) {
        return null;
    }

    /**
     * FUNCTION SIGNATURE FOR VAULT INHERITANCE ONLY
     * @param s
     * @param s1
     * @param v
     * @return
     */
    @Override
    public boolean has(String s, String s1, double v) {
        return false;
    }

    /**
     * FUNCTION SIGNATURE FOR VAULT INHERITANCE ONLY
     * @param s
     * @param v
     * @return
     */
    @Override
    public boolean has(String s, double v) {
        return false;
    }

    /**
     * DO NOT USE THIS FUNCTION
     * @param s
     * @param s1
     * @return
     */
    @Override
    public double getBalance(String s, String s1) {
        return 0;
    }

    /**
     * DO NOT USE
     * @param s
     * @param s1
     * @param v
     * @return
     */
    @Override
    public EconomyResponse depositPlayer(String s, String s1, double v) {
        return null;
    }

    public boolean isEnabled() {
        return true;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean createPlayerAccount(String s) {
        return false;
    }

    public boolean hasBankSupport() {
        return false;
    }

    @Override
    public int fractionalDigits() {
        return 2;
    }

    @Override
    public String format(double v) {
        return (v + "r");
    }

    @Override
    public String currencyNamePlural() {
        return " Rubies";
    }

    @Override
    public String currencyNameSingular() {
        return " Ruby";
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, String worldName, double amount) {
        return this.depositPlayer(player, amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, String worldName, double amount) {
        return this.withdrawPlayer(player, amount);
    }

    @Override
    public boolean hasAccount(OfflinePlayer player, String worldName) {
        return this.hasAccount(player);
    }

    /**
     * Get balance of an offline player
     * @param player
     * @param world
     * @return
     */
    @Override
    public double getBalance(OfflinePlayer player, String world) {
        return this.getBalance(player);
    }

    @Override
    public boolean has(OfflinePlayer player, String worldName, double amount) {
        return this.has(player, amount);
    }

    public void getTransactions(OfflinePlayer player, int page){
        if (player.hasPlayedBefore()) {
            String uuid = player.getUniqueId().toString();
            if (accountHash.containsKey(player)) {
                PlayerAccount account = accountHash.get(player);
                account.displayTransactions(player, page);
            }
        }
        return;
    }
}
