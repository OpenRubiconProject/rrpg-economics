package com.openrubicon.economics.commands;

import com.openrubicon.core.api.command.Command;
import com.openrubicon.core.api.interactables.Player;
import com.openrubicon.core.api.interactables.enums.InteractableType;
import com.openrubicon.core.api.interactables.interfaces.Interactable;
import com.openrubicon.economics.RRPGEconomics;
import org.bukkit.ChatColor;

import java.util.ArrayList;

/**
 * Created by Quinn on 10/21/2017.
 */
public class Bal extends Command {
    @Override
    public String getCommandFormat() {
        return "bal";
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
        //[0] Optional Player Name

        if (strings.length == 1){
            if (((Player)interactable).getPlayer() != null){
                ((Player)interactable).getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&2" + strings[0] + "'s balance is " + RRPGEconomics.economy.getBalance(((Player)interactable).getPlayer()) + " " + RRPGEconomics.economy.currencyNamePlural()));
                return;
            } else {
                ((Player)interactable).sendMessage(ChatColor.translateAlternateColorCodes('&',"&2Player not found."));
                return;
            }
        } else {
            ((Player)interactable).sendMessage(ChatColor.translateAlternateColorCodes('&', "&2 Your balance is " + RRPGEconomics.economy.getBalance(((Player)interactable).getPlayer()) + RRPGEconomics.economy.currencyNamePlural()));
            return;
        }
    }
}
