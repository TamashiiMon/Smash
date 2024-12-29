package de.terrocraft.smesh;

import de.terrocraft.smesh.Utils.ConfigUtil;
import de.terrocraft.smesh.Utils.PlayerActionBar;
import de.terrocraft.smesh.Utils.SConfig;
import de.terrocraft.smesh.commands.BypassCommand;
import de.terrocraft.smesh.commands.SmashStopCommand;
import de.terrocraft.smesh.commands.StartCommand;
import de.terrocraft.smesh.listeners.*;
import de.terrocraft.smesh.managers.MachMakeManager;
import de.terrocraft.smesh.managers.WorldManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;


public final class Smash extends JavaPlugin {
    private static Smash instance;
    public static SConfig config;
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
            WorldManager worldManager = new WorldManager(getDataFolder());
            worldManager.deleteWorld();
        }
    }


    @Override
    public void onEnable() {
        instance = this;

        config = ConfigUtil.getConfig("config");

        if (!config.getFile().isFile() && !config.getFile().exists()) {
            config.setDefault("AutoGameStart", true);
            config.setDefault("AutoGameStartMinPlayers", 4);
            config.save();
        }

        setGamestate(Gamestates.LOBBY);

        PlayerActionBar.start();
        registerCommands();
        registerEvents();


    }

    @Override
    public void onDisable() {
        WorldManager worldManager = new WorldManager(getDataFolder());
        worldManager.deleteWorld();

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