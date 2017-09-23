package com.openrubicon.economics;

import com.openrubicon.core.RRPGCore;
import com.openrubicon.core.database.interfaces.DatabaseModel;
import org.bukkit.plugin.java.JavaPlugin;
import com.openrubicon.core.interfaces.Module;

import java.util.ArrayList;

public class RRPGEconomics extends JavaPlugin implements Module {

    @Override
    public ArrayList<DatabaseModel> getDatabaseModels() {
        ArrayList<DatabaseModel> models = new ArrayList<>();

        return models;
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
