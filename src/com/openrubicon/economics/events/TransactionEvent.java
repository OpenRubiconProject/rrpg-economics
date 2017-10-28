package com.openrubicon.economics.events;

import com.openrubicon.core.api.events.Event;
import com.openrubicon.economics.database.models.TransactionModel;
import org.bukkit.entity.Player;
import com.openrubicon.economics.classes.Transaction;
import org.bukkit.event.HandlerList;

public class TransactionEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    Transaction t;

    public TransactionEvent(Transaction transaction) {
        this.t = transaction;
        TransactionModel tmodel = new TransactionModel(t);
        tmodel.insertInto();
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
