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

import java.util.ArrayList;
import java.util.Date;

public class Pay extends Command {
    @Override
    public String getCommandFormat() {
        return "pay $s $n";
    }

    @Override
    public ArrayList<InteractableType> getAllowedSenderTypes() {
        ArrayList<InteractableType> senders = new ArrayList<>();
        senders.add(InteractableType.PLAYER);
        return senders;
    }

    @Override
    public void handle(Interactable interactable, ArrayList<DynamicPrimitive> args) {

        //Args:
        //[0] targetPlayer
        //[1] amount

        if(args.size() != 2)
        {

            ((Player)interactable).getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&',"&2Incorrect syntax for /money pay"));
            return;
        }

        if(Bukkit.getPlayer(args.get(0).getString()) == null)
        {
            ((Player)interactable).getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&',"&2Player is not online!"));
            return;
        }

        if (!RRPGEconomics.economy.hasAccount(((Player)interactable).getPlayer()) || !RRPGEconomics.economy.hasAccount(Bukkit.getPlayer(args.get(0).getString())))
        {
            ((Player)interactable).getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&',"&2That player does not exist!"));
            return;
        }

        if (args.get(1).getDouble() <= 0)
        {
            ((Player)interactable).getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&',"&2Invalid payment amount."));
            return;
        }

        Double amount = args.get(1).getDouble();

        if (!RRPGEconomics.economy.has(((Player)interactable).getPlayer(), amount))
        {
            ((Player)interactable).getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&',"&2You do not have enough money!"));
            return;
        }

        RRPGEconomics.economy.withdrawPlayer(((Player)interactable).getPlayer(), amount);
        RRPGEconomics.economy.depositPlayer(Bukkit.getPlayer(args.get(0).getString()), amount);
        ((Player)interactable).getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&',"&2Paid " + args.get(0) + " " + amount + RRPGEconomics.economy.currencyNamePlural()));
        if (Bukkit.getPlayer(args.get(0).getString()).isOnline()){
            Bukkit.getPlayer(args.get(0).getString()).sendMessage(ChatColor.translateAlternateColorCodes('&',"&2Recieved " + amount + RRPGEconomics.economy.currencyNamePlural() + " from " + ((Player)interactable).getPlayer().getName()));
        }
        //ADD TO TRANSACTION HISTORY HERE


        Transaction t = new Transaction(((Player)interactable).getPlayer(), Bukkit.getPlayer(args.get(0).getString()), amount, "payment", new Date());
        RRPGEconomics.economy.getAccount(((Player)interactable).getPlayer()).addTransaction(t);
        RRPGEconomics.economy.getAccount(Bukkit.getPlayer(args.get(0).getString())).addTransaction(t);
        return;
    }
}
