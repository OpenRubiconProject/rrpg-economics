package com.openrubicon.economics.classes;

import com.Acrobot.ChestShop.Events.TransactionEvent;
import com.openrubicon.core.api.database.interfaces.PostDatabaseLoad;
import com.openrubicon.core.api.events.Event;
import com.openrubicon.core.api.vault.economy.EconomyResponse;
import com.openrubicon.economics.RRPGEconomics;
import com.openrubicon.economics.database.models.AccountModel;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.openrubicon.core.RRPGCore.plugin;

public class Economy extends com.openrubicon.core.api.vault.economy.Economy implements PostDatabaseLoad {

    public Economy(Plugin plugin) {
        super(plugin);
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public boolean hasBankSupport() {
        return false;
    }

    @Override
    public int fractionalDigits() {
        return 0;
    }

    @Override
    public String format(double v) {
        return null;
    }

    @Override
    public String currencyNamePlural() {
        return null;
    }

    @Override
    public String currencyNameSingular() {
        return null;
    }

    @Override
    public List<String> getBanks() {
        return null;
    }

    @Override
    public void run() {

    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer offlinePlayer) {
        return false;
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer offlinePlayer, double v) {
        return null;
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer offlinePlayer, double v) {
        return null;
    }

    @Override
    public double getBalance(OfflinePlayer offlinePlayer) {
        return 0;
    }

    @Override
    public boolean has(OfflinePlayer offlinePlayer, double v) {
        return false;
    }

    @Override
    public boolean hasAccount(OfflinePlayer offlinePlayer) {
        return false;
    }
}
