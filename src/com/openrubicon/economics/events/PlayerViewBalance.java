package com.openrubicon.economics.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import com.openrubicon.economics.classes.PlayerAccount;

import java.util.Date;

/**
 * Created by Quinn on 10/1/2017.
 */
public class PlayerViewBalance extends Event {

    private static final HandlerList handlers = new HandlerList();
    PlayerAccount account;
    Date time = new Date();

    public PlayerViewBalance(PlayerAccount pa) {
        this.account = pa;
    }

    public Date getDate(){return time;}

    public PlayerAccount getPlayerAccount(){return account;}

    public HandlerList getHandlers(){
        return handlers;
    }

    public static HandlerList getHandlerList(){
        return handlers;
    }


}
