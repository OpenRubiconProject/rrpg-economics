package com.openrubicon.economics;

import com.openrubicon.core.RRPGCore;
import org.bukkit.plugin.java.JavaPlugin;
import com.openrubicon.core.interfaces.iModule;

public class RRPGEconomics extends JavaPlugin implements iModule {

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
