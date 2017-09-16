package com.openrubicon.economics;

import org.bukkit.plugin.java.JavaPlugin;
import com.openrubicon.core.iModule;

public class RRPGEconomics extends JavaPlugin implements iModule {

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
