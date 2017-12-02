package com.openrubicon.economics.commands;

import com.openrubicon.core.api.command.Command;
import com.openrubicon.core.api.interactables.Player;
import com.openrubicon.core.api.interactables.enums.InteractableType;
import com.openrubicon.core.api.interactables.interfaces.Interactable;
import com.openrubicon.core.api.utility.DynamicPrimitive;
import com.openrubicon.economics.RRPGEconomics;
import org.bukkit.ChatColor;

import java.util.ArrayList;

import static java.lang.Integer.parseInt;

public class Transactions extends Command {
    @Override
    public String getCommandFormat() {
        return "transactions";
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
        //[0] optional page number

        if(args.size() <= 1){
            if (args.size() == 1 && args.get(0).getInt() > 0){
                RRPGEconomics.economy.getAccount(((Player)interactable).getPlayer()).displayTransactions(args.get(0).getInt());
            } else {
                RRPGEconomics.economy.getAccount(((Player)interactable).getPlayer()).displayTransactions(0);
            }
        } else {
            //Possibly check number of arguments and tell them they need more or less(?)
            ((Player)interactable).getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&',"&2Incorrect syntax for /money history"));
        }
    }
}
