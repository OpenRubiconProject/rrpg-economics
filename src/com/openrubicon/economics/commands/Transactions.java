package com.openrubicon.economics.commands;

import com.openrubicon.core.api.command.Command;
import com.openrubicon.core.api.interactables.Player;
import com.openrubicon.core.api.interactables.enums.InteractableType;
import com.openrubicon.core.api.interactables.interfaces.Interactable;
import com.openrubicon.economics.RRPGEconomics;
import org.bukkit.ChatColor;

import java.util.ArrayList;

import static java.lang.Integer.parseInt;

/**
 * Created by Quinn on 10/21/2017.
 */
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
    public void handle(Interactable interactable, String[] strings) {
        //Args:
        //[0] optional page number

        if(strings.length <= 1){
            if (strings.length == 1 && parseInt(strings[0]) > 0){
                RRPGEconomics.economy.getAccount(((Player)interactable).getPlayer()).displayTransactions(parseInt(strings[0]));
            } else {
                RRPGEconomics.economy.getAccount(((Player)interactable).getPlayer()).displayTransactions(0);
            }
        } else {
            //Possibly check number of arguments and tell them they need more or less(?)
            ((Player)interactable).getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&',"&2Incorrect syntax for /money history"));
        }


    }
}
