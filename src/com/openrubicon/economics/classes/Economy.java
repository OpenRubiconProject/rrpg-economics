package com.openrubicon.economics.classes;

import com.Acrobot.ChestShop.Events.TransactionEvent;
import com.openrubicon.core.api.database.interfaces.PostDatabaseLoad;
import com.openrubicon.core.api.events.Event;
import com.openrubicon.core.api.vault.economy.EconomyResponse;
import com.openrubicon.economics.RRPGEconomics;
import com.openrubicon.economics.database.models.AccountModel;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;

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
        //Register Economy Events
        new EconomyServerListener();
        new TransactionEventListener();

        //Load database entries into the hash map on load.
        loadHashMap();
    }

    //Economy Listener to create accounts for players when joining the server.
    public class EconomyServerListener implements Listener {

        @EventHandler
        public void onPlayerJoin(PlayerJoinEvent event){

            //When a player joins the server, if they don't have an account, make them an account
            OfflinePlayer player = event.getPlayer();
            if(!hasAccount(player)){
                createPlayerAccount(player);
            }
        }

        public EconomyServerListener(){
            Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
        }
    }

    //Chest Shop Event listener to add Transactions when a chest shop event occurs.
    public class TransactionEventListener extends Event implements Listener{


        @EventHandler
        public void onTransactionEvent(com.Acrobot.ChestShop.Events.TransactionEvent e){
            Sign s = e.getSign();
            Double amount = e.getPrice();
            String clientReason = "selling";
            String ownerReason = "buying";
            if(e.getTransactionType() == TransactionEvent.TransactionType.BUY){
                amount = amount * -1;
                clientReason = "buying";
                ownerReason = "selling";
            }

            //Append the quantity and the item name to the reason of the transaction
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

    /**
     * Creates a player account for the OfflinePlayer specified.
     * @param player    OfflinePlayer to create the account for
     * @return          If the account was successfully created or not
     */
    @Override
    public boolean createPlayerAccount(OfflinePlayer player) {
        //If the player has never played before but the player is now online, make them an account
        if (player.isOnline()){
            String uuid = player.getUniqueId().toString();
            //If their uuid is not in the database, check the hashmap.
            if(!existsInDatabase(player)) {
                //If their account does not exist in the hast map, we will create a new account for them
                if(!accountHash.containsKey(player)) {
                    //Create a new account for them
                    PlayerAccount account = new PlayerAccount(player, 0.0);
                    AccountModel am = new AccountModel(account);

                    //Attempt to save to database
                    if(am.insertInto()){
                        accountHash.put(player, account);
                        return true;
                    } else {
                        return false;
                    }
                }
                //If the account exists in the hash map but not in the database, nothing needs to be done
                //Will regularly updateWithAccount accounts.
                return true;
            } else {
                //If the player exists in the database, the player has an account.
                //Get the account and add it to the hash map.
                PlayerAccount p = new PlayerAccount(getFromDatabase(player));
                accountHash.put(player, p);
                return true;
            }
        }
        //If the player is offline or the player has not player on the server, we shouldn't be creating an account for them
        return false;
    }

    /**
     * Withdraws a specified amount of money from a player's account.
     * @param player    The account to withdraw money from
     * @param amount    The amount of money to withdraw
     * @return          If the Economy successfully performed the withdraw.
     */
    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, double amount) {
        //Check if the player has an account
        if(hasAccount(player)){
            //Check if the account has enough money
            if(has(player, amount)){

                //Withdraw from the account
                accountHash.get(player).alterAccount(-1 * amount);

                Double divisor = Math.pow(10, RRPGEconomics.economy.fractionalDigits());
                Double d = new Double((Math.round(accountHash.get(player).getBalance() * divisor) / divisor));

                //updateWithAccount the database
                AccountModel am = getFromDatabase(player);
                am.setBal(accountHash.get(player).getBalance());
                if(am.updateModel()){
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

    /**
     * Deposits a specified amount of money into a player's account
     * @param player    The account to deposit money into
     * @param amount    The amount of money to deposit
     * @return          If the Economy successfully performed the deposit
     */
    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, double amount) {
        //Check if the player has an account
        if(hasAccount(player)){
            //Deposit to the account
            accountHash.get(player).alterAccount(1 * amount);

            Double divisor = Math.pow(10, RRPGEconomics.economy.fractionalDigits());
            Double d = new Double((Math.round(accountHash.get(player).getBalance() * divisor) / divisor));

            //updateWithAccount the database
            AccountModel am = getFromDatabase(player);
            am.setBal(accountHash.get(player).getBalance());
            if(am.updateModel()){
                return EconomyResponse.Success();
            } else {
                //Undo if the database cannot update
                accountHash.get(player).alterAccount(-1 * amount);
                EconomyResponse.Failure("SQL Error");
            }
        }
        return EconomyResponse.Failure(player.getName() + " does not have an account.");
    }

    /**
     * Returns a list of the banks that exist
     * @return  A list of strings of the bank names
     */
    @Override
    public List<String> getBanks() {
        return null;
    }

    /**
     * Gets the balance of the player's account
     * @param player    The desired player's balance
     * @return          The balance of the player's account
     */
    @Override
    public double getBalance(OfflinePlayer player) {
        //Message for debugging.
//        Bukkit.getLogger().info(accountHash.size() + " entires in the hash map.");
//        accountHash.forEach((key, value) ->
//        {
//            Bukkit.getLogger().info("uuid : " + key + " Account : " + value);
//        });

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

    /**
     * Checks if the specified player has enough money in their account
     * @param player    The desired player's account
     * @param amount    The amount of money to check their account for
     * @return          If the player has more than the stated amount of money
     */
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

    /**
     * Checks if the economy is enabled
     * @return  If the economy is enabled
     */
    @Override
    public boolean isEnabled() {
        return true;
    }

    /**
     * Gets the name of the economy
     * @return  The name of the economy
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Checks if the economy supports banks
     * @return  If the economy supports banks
     */
    @Override
    public boolean hasBankSupport() {
        return false;
    }

    /**
     * Determines the number of digits in the currency for the economy
     * @return  The number of digits for the currency
     */
    @Override
    public int fractionalDigits() {
        return 2;
    }

    /**
     * The format of the currency when displaying to the player
     * @param v     The amount of money that is being displayed
     * @return      The format of the currency
     */
    @Override
    public String format(double v) {
        return v + "r";
    }

    /**
     * Returns the plural name of the currency
     * @return  The plural name of the currency
     */
    @Override
    public String currencyNamePlural() {
        return "Rubies";
    }

    /**
     * Returns the singular name of the currency
     * @return  The singular name of the currency
     */
    @Override
    public String currencyNameSingular() {
        return "Ruby";
    }

    /**
     * Chcks if the specified player has an account
     * @param offlinePlayer The player to check for an account
     * @return              If the player has an account
     */
    @Override
    public boolean hasAccount(OfflinePlayer offlinePlayer) {
        return accountHash.containsKey(offlinePlayer);
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

    /**
     * Checks if the specified player has an account in the database
     * @param p     The player to check for an account in the database
     * @return      If the player has an account in the database
     */
    private boolean existsInDatabase(OfflinePlayer p){
        AccountModel am = new AccountModel();
        am.setName(p.getName());
        am.setUuid(p.getUniqueId().toString());
        return am.existsInDb();
    }

    /**
     * Obtains a player's account from the database
     * @param p     The player's account to retreieve from the database/
     * @return      The AccountModel of the player's database entry
     */
    private AccountModel getFromDatabase(OfflinePlayer p){
        AccountModel am = new AccountModel();
        am.setName(p.getName());
        am.setUuid(p.getUniqueId().toString());
        am = am.getAccount();
        return am;
    }

    /**
     * Obtains the PlayerAccount for the specified player
     * @param p     The player's account to retrieve
     * @return      The PlayerAccount of the specified player
     */
    public PlayerAccount getAccount(OfflinePlayer p){
        if(accountHash.containsKey(p))
            return accountHash.get(p);
        else return null;
    }


}
