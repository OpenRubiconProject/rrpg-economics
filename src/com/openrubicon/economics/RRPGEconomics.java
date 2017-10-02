package com.openrubicon.economics;

import com.openrubicon.core.RRPGCore;
import com.openrubicon.core.api.database.interfaces.DatabaseModel;
import com.openrubicon.core.api.database.interfaces.PostDatabaseLoad;
import com.openrubicon.economics.classes.Economy;
import com.openrubicon.economics.database.models.AccountModel;
import com.openrubicon.economics.database.models.TransactionModel;
import com.openrubicon.core.api.command.Command;
import com.openrubicon.core.api.database.interfaces.DatabaseModel;
import org.bukkit.plugin.java.JavaPlugin;
import com.openrubicon.core.interfaces.Module;

import java.util.ArrayList;

public class RRPGEconomics extends JavaPlugin implements Module {

    public static Economy economy;

    @Override
    public void onEnable(){
        economy = new Economy(this);
    }

    @Override
    public ArrayList<DatabaseModel> getDatabaseModels() {
        ArrayList<DatabaseModel> models = new ArrayList<>();
        models.add(new AccountModel());
        models.add(new TransactionModel());
        return models;
    }

    @Override
    public ArrayList<Command> getCommands() {
        return new ArrayList<>();
    }

    @Override
    public ArrayList<PostDatabaseLoad> getPostDatabaseLoads() {
        ArrayList<PostDatabaseLoad> loads = new ArrayList<>();
        loads.add(economy);
        return loads;
    }

    @Override
    public void onLoad()
    {
        RRPGCore.modules.addModule(this);
    }

    @Override
    public String getKey() {
        return "rrpg-economics";
    }

    @Override
    public String getOverview() {
        return "The Economy of RRPG";
    }

    @Override
    public String getConfiguration() {
        return this.getDataFolder().getAbsolutePath();
    }
}
