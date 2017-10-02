package com.openrubicon.economics.events;

import com.openrubicon.economics.classes.PlayerAccount;
import com.openrubicon.economics.classes.Transaction;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.Date;

/**
 * Created by Quinn on 7/5/2017.
 */
public class PlayerViewHistory extends Event {

    private static final HandlerList handlers = new HandlerList();
    int page;
    PlayerAccount pa;
    Date time = new Date();

    public PlayerViewHistory(PlayerAccount account, int pg) {
        this.page = pg;
        this.pa = account;
    }

    public int getPage(){
        return page;
    }

    public PlayerAccount getPlayerAccount(){return pa;}

    public HandlerList getHandlers(){
        return handlers;
    }

    public static HandlerList getHandlerList(){
        return handlers;
    }


}
