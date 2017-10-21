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
public class Take extends Command {
    @Override
    public String getCommandFormat() {
        return "take $ $";
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
        //[0] take
        //[1] targetPlayer
        //[2] amount
        org.bukkit.OfflinePlayer thePlayer = ((Player)interactable).getPlayer();
        
        if (thePlayer.getPlayer().getPlayer().hasPermission("economics.admin.give")) {
            if (strings.length == 3) {
                //check if the player they are trying to send money to exists.
                if (Bukkit.getPlayer(strings[0]) != null) {
                    if (RRPGEconomics.economy.hasAccount(Bukkit.getPlayer(strings[0]))) {
                        if (Double.parseDouble(strings[1]) > 0) {
                            Double amount = Double.parseDouble(strings[1]);
                            if (RRPGEconomics.economy.has(Bukkit.getPlayer(strings[0]), amount)) {
                                RRPGEconomics.economy.withdrawPlayer(Bukkit.getPlayer(strings[0]), amount);
                                thePlayer.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&2Siphoned " + strings[0] + " " + amount + RRPGEconomics.economy.currencyNamePlural()));
                                if (Bukkit.getPlayer(strings[0]).isOnline()) {
                                    Bukkit.getPlayer(strings[0]).sendMessage(ChatColor.translateAlternateColorCodes('&', "&2You were siphoned " + amount + RRPGEconomics.economy.currencyNamePlural()));
                                }

                                //ADD TO TRANSACTION HISTORY HERE
                                RRPGEconomics.economy.getAccount(Bukkit.getPlayer(strings[0])).addTransaction(new Transaction(Bukkit.getPlayer(strings[0]), thePlayer.getPlayer(), amount, "taken", new Date()));
                            } else {
                                thePlayer.getPlayer().sendMessage(strings[0] + " does not have that much money!");
                            }
                        } else {
                            thePlayer.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&2Invalid amount!"));
                        }
                    } else {
                        thePlayer.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&2That player does not exist!"));
                    }
                } else {
                    thePlayer.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&2That player does not exist!"));
                }
            } else {
                //Possibly check number of arguments and tell them they need more or less(?)
                thePlayer.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&2Incorrect syntax for /money take"));
            }
        }
        return;
    }
}
