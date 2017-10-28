package com.openrubicon.economics.commands;

import com.openrubicon.core.api.command.Command;
import com.openrubicon.core.api.interactables.Player;
import com.openrubicon.core.api.interactables.enums.InteractableType;
import com.openrubicon.core.api.interactables.interfaces.Interactable;
import com.openrubicon.economics.RRPGEconomics;
import com.openrubicon.economics.classes.Economy;
import com.openrubicon.economics.classes.Transaction;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Quinn on 10/21/2017.
 */
public class Pay extends Command {
    @Override
    public String getCommandFormat() {
        return "pay $ $";
    }

    @Override
    public ArrayList<InteractableType> getAllowedSenderTypes() {
        ArrayList<InteractableType> senders = new ArrayList<>();
        senders.add(InteractableType.PLAYER);
        return senders;
    }

    @Override
    public void handle(Interactable interactable, String[] strings) {

        //Args:
        //[0] targetPlayer
        //[1] amount

        if(strings.length != 2)
        {

            ((Player)interactable).getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&',"&2Incorrect syntax for /money pay"));
            return;
        }

        if(Bukkit.getPlayer(strings[0]) == null)
        {
            ((Player)interactable).getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&',"&2That player does not exist!"));
            return;
        }

        if (!RRPGEconomics.economy.hasAccount(((Player)interactable).getPlayer()) || !RRPGEconomics.economy.hasAccount(Bukkit.getPlayer(strings[0])))
        {
            ((Player)interactable).getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&',"&2That player does not exist!"));
            return;
        }

        if (Double.parseDouble(strings[1]) <= 0)
        {
            ((Player)interactable).getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&',"&2Invalid payment amount."));
            return;
        }

        Double amount = Double.parseDouble(strings[1]);

        if (!RRPGEconomics.economy.has(((Player)interactable).getPlayer(), amount))
        {
            ((Player)interactable).getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&',"&2You do not have enough money!"));
            return;
        }

        RRPGEconomics.economy.withdrawPlayer(((Player)interactable).getPlayer(), amount);
        RRPGEconomics.economy.depositPlayer(Bukkit.getPlayer(strings[0]), amount);
        ((Player)interactable).getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&',"&2Paid " + strings[0] + " " + amount + RRPGEconomics.economy.currencyNamePlural()));
        if (Bukkit.getPlayer(strings[0]).isOnline()){
            Bukkit.getPlayer(strings[0]).sendMessage(ChatColor.translateAlternateColorCodes('&',"&2Recieved " + amount + RRPGEconomics.economy.currencyNamePlural() + " from " + ((Player)interactable).getPlayer().getName()));
        }
        //ADD TO TRANSACTION HISTORY HERE


        Transaction t = new Transaction(((Player)interactable).getPlayer(), Bukkit.getPlayer(strings[0]), amount, "payment", new Date());
        RRPGEconomics.economy.getAccount(((Player)interactable).getPlayer()).addTransaction(t);
        RRPGEconomics.economy.getAccount(Bukkit.getPlayer(strings[0])).addTransaction(t);
        return;
    }
}
