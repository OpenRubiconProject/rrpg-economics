package com.openrubicon.economics.commands;

import com.openrubicon.core.api.command.Command;
import com.openrubicon.core.api.interactables.Player;
import com.openrubicon.core.api.interactables.enums.InteractableType;
import com.openrubicon.core.api.interactables.interfaces.Interactable;
import com.openrubicon.core.api.utility.DynamicPrimitive;
import com.openrubicon.economics.RRPGEconomics;
import com.openrubicon.economics.classes.Transaction;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;

import java.util.ArrayList;
import java.util.Date;

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
    public void handle(Interactable interactable, ArrayList<DynamicPrimitive> args) {

        //Args:
        //[0] targetPlayer
        //[1] amount

        org.bukkit.OfflinePlayer thePlayer = ((Player)interactable).getPlayer();

        //Note this command gives out free money.
        if (thePlayer.getPlayer().hasPermission("economics.admin.give")) {
            if (args.size() == 2) {
                //check if the player they are trying to send money to exists.
                if (Bukkit.getPlayer(args.get(0).getString()) != null) {
                    if (RRPGEconomics.economy.hasAccount(Bukkit.getPlayer(args.get(0).getString()))) {
                        if (args.get(1).getDouble() > 0) {
                            Double amount = args.get(1).getDouble();
                            RRPGEconomics.economy.depositPlayer(Bukkit.getPlayer(args.get(0).getString()), amount);
                            thePlayer.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&2Gave " + args.get(0) + " " + amount + RRPGEconomics.economy.currencyNamePlural()));
                            if (Bukkit.getPlayer(args.get(0).getString()).isOnline()) {
                                Bukkit.getPlayer(args.get(0).getString()).sendMessage(ChatColor.translateAlternateColorCodes('&', "&2You were given " + amount + RRPGEconomics.economy.currencyNamePlural()));
                            }

                            //ADD TO TRANSACTION HISTORY HERE
                            RRPGEconomics.economy.getAccount(Bukkit.getPlayer(args.get(0).getString())).addTransaction(new Transaction((OfflinePlayer)thePlayer, Bukkit.getPlayer(args.get(0).getString()), amount, "given", new Date()));

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
