package de.terrocraft.smesh;

import de.terrocraft.smesh.Utils.ConfigUtil;
import de.terrocraft.smesh.Utils.PlayerActionBar;
import de.terrocraft.smesh.Utils.SConfig;
import de.terrocraft.smesh.commands.BypassCommand;
import de.terrocraft.smesh.commands.StartCommand;
import de.terrocraft.smesh.commands.SmashStopCommand;
import de.terrocraft.smesh.listeners.*;
import de.terrocraft.smesh.managers.MachMakeManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;


public final class Smash extends JavaPlugin {
    public static SConfig config;
    private static Smash instance;
    public ArrayList<Player> alive = new ArrayList<>();
    public ArrayList<Player> spectating = new ArrayList<>();
    public ArrayList<Player> vanished = new ArrayList<>();
    private Gamestates gamestates;

    public static Smash getInstance() {
        return instance;
    }

    public Gamestates getGamestate() {
        return gamestates;
    }

    public void setGamestate(Gamestates gamestate) {
        this.gamestates = gamestate;
        if (gamestates == Gamestates.ENDGAME) {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                MachMakeManager.EndGameEvent(onlinePlayer);
            }
        }
    }

    @Override
    public void onEnable() {
        instance = this;

        config = ConfigUtil.getConfig("config");

        Location defaultlobbyloc = new Location(Bukkit.getWorld("world"), 0, 0, 0);

        if (!config.getFile().isFile()) {
            config.setDefault("prefix", "&5&lSmash&7:&r ");
            config.setDefault("noperm", "&cYou do not have permission to access this command.");
            config.setDefault("lobby-location", defaultlobbyloc);
            config.save();
        }

        PlayerActionBar.start();
        registerCommands();
        registerEvents();
        setGamestate(Gamestates.LOBBY);


    }

    @Override
    public void onDisable() {

    }

    private void registerCommands() {
        getCommand("start").setExecutor(new StartCommand(this));
        getCommand("smashstop").setExecutor(new SmashStopCommand(this));
        getCommand("bypass").setExecutor(new BypassCommand());
    }

    private void registerEvents() {
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new PlayerListener(this), this);
        pm.registerEvents(new BuildListener(this), this);
        pm.registerEvents(new DamageListener(this), this);
        pm.registerEvents(new TNTListener(), this);
        pm.registerEvents(new MovementListener(), this);
    }


}