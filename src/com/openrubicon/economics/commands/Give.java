package com.openrubicon.economics.commands;

import com.openrubicon.core.api.command.Command;
import com.openrubicon.core.api.interactables.Player;
import com.openrubicon.core.api.interactables.enums.InteractableType;
import com.openrubicon.core.api.interactables.interfaces.Interactable;
import com.openrubicon.economics.RRPGEconomics;
import com.openrubicon.economics.classes.Transaction;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Quinn on 10/21/2017.
 */
public class Give extends Command {
    @Override
    public String getCommandFormat() {
        return "give $ $";
    }

    @Override
    public ArrayList<InteractableType> getAllowedSenderTypes() {
        ArrayList<InteractableType> interactableTypes = new ArrayList<>();
        interactableTypes.add(InteractableType.PLAYER);
        return interactableTypes;
    }

    @Override
    public void handle(Interactable interactable, String[] strings) {

        //Args:
        //[0] targetPlayer
        //[1] amount

        org.bukkit.OfflinePlayer thePlayer = ((Player)interactable).getPlayer();

        //Note this command gives out free money.
        if (thePlayer.getPlayer().hasPermission("economics.admin.give")) {
            if (strings.length == 2) {
                //check if the player they are trying to send money to exists.
                if (Bukkit.getPlayer(strings[0]) != null) {
                    if (RRPGEconomics.economy.hasAccount(Bukkit.getPlayer(strings[0]))) {
                        if (Double.parseDouble(strings[1]) > 0) {
                            Double amount = Double.parseDouble(strings[1]);
                            RRPGEconomics.economy.depositPlayer(Bukkit.getPlayer(strings[0]), amount);
                            thePlayer.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&2Gave " + strings[0] + " " + amount + RRPGEconomics.economy.currencyNamePlural()));
                            if (Bukkit.getPlayer(strings[0]).isOnline()) {
                                Bukkit.getPlayer(strings[0]).sendMessage(ChatColor.translateAlternateColorCodes('&', "&2You were given " + amount + RRPGEconomics.economy.currencyNamePlural()));
                            }

                            //ADD TO TRANSACTION HISTORY HERE
                            RRPGEconomics.economy.getAccount(Bukkit.getPlayer(strings[0])).addTransaction(new Transaction((OfflinePlayer)thePlayer, Bukkit.getPlayer(strings[0]), amount, "given", new Date()));

                        } else {
                            thePlayer.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&2Invalid payment amount."));
                        }
                    } else {
                        thePlayer.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&2That player does not exist!"));
                    }
                } else {
                    thePlayer.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&2That player does not exist!"));
                }
            } else {
                //Possibly check number of arguments and tell them they need more or less(?)
                thePlayer.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&2Incorrect syntax for /money give"));
            }
        }
        return;
    }
}
