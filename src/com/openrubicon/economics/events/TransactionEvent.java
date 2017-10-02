package com.openrubicon.economics.events;

import org.bukkit.entity.Player;
import com.openrubicon.economics.classes.Transaction;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class TransactionEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    Transaction t;

    public TransactionEvent(Transaction transaction) {
        this.t = transaction;
    }

    public Transaction getTransaction(){
        return t;
    }

    public HandlerList getHandlers(){
        return handlers;
    }

    public static HandlerList getHandlerList(){
        return handlers;
    }

}
