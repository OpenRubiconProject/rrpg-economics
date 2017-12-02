package com.openrubicon.economics.commands;

import com.openrubicon.core.api.command.Command;
import com.openrubicon.core.api.interactables.Player;
import com.openrubicon.core.api.interactables.enums.InteractableType;
import com.openrubicon.core.api.interactables.interfaces.Interactable;
import com.openrubicon.core.api.utility.DynamicPrimitive;
import com.openrubicon.economics.RRPGEconomics;
import org.bukkit.ChatColor;

import java.util.ArrayList;

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
    public void handle(Interactable interactable, ArrayList<DynamicPrimitive> args) {

        //Args:
        //[0] Optional Player Name

        if (args.size() == 1){
            if (((Player)interactable).getPlayer() != null){
                ((Player)interactable).getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&2" + args.get(0).getString() + "'s balance is " + RRPGEconomics.economy.getBalance(((Player)interactable).getPlayer()) + " " + RRPGEconomics.economy.currencyNamePlural()));
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
