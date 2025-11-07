package de.terrocraft.smesh;

import de.terrocraft.smesh.commands.*;
import de.terrocraft.smesh.enums.Gamestates;
import de.terrocraft.smesh.listeners.*;
import de.terrocraft.smesh.services.MachMakeService;
import de.terrocraft.smesh.services.WorldService;
import de.terrocraft.smesh.utils.PlayerActionBar;
import de.terrocraft.smesh.utils.config.ConfigUtil;
import de.terrocraft.smesh.utils.config.SConfig;
import fr.mrmicky.fastboard.FastBoard;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;


public final class Smash extends JavaPlugin {
    private static Smash instance;
    public static SConfig config;
    public ArrayList<Player> alive = new ArrayList<>();
    public ArrayList<Player> spectating = new ArrayList<>();
    public ArrayList<Player> vanished = new ArrayList<>();
    private Gamestates gamestates;

    @Override
    public void onEnable() {
        instance = this;

        config = ConfigUtil.getConfig("config", this);

        if (!config.getFile().isFile() && !config.getFile().exists()) {
            config.setDefault("prefix", "\uE000 ");
            config.setDefault("game.AutoGameStart", true);
            config.setDefault("game.MinPlayers", 4);
            config.setDefault("database.address", "localhost");
            config.setDefault("database.port", 3306);
            config.setDefault("database.database", "smash");
            config.setDefault("database.username", "root");
            config.setDefault("database.password", "smash123!");
            config.save();
        }

        setGamestate(Gamestates.LOBBY);

        PlayerActionBar.start();
        registerCommands();
        registerEvents();

        for (Player p : Bukkit.getOnlinePlayers()) {
            FastBoard board = new FastBoard(p);
            board.updateTitle(ChatColor.RED + "Smash");
            PlayerActionBar.boards.put(p.getUniqueId(), board);
        }


    }

    public void setGamestate(Gamestates gamestate) {
        this.gamestates = gamestate;
        if (gamestates == Gamestates.ENDGAME) {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                MachMakeService.EndGameEvent(onlinePlayer);
            }
            WorldService worldService = new WorldService(getDataFolder());
            worldService.deleteWorld();
        }
    }

    @Override
    public void onDisable() {
        WorldService worldService = new WorldService(getDataFolder());
        worldService.deleteWorld();

    }

    private void registerCommands() {
        getCommand("start").setExecutor(new StartCommand(this));
        getCommand("smashstop").setExecutor(new SmashStopCommand(this));
        getCommand("bypass").setExecutor(new BypassCommand());
        getCommand("coins").setExecutor(new CoinsCommand());
        getCommand("wins").setExecutor(new WinsCommand());
    }

    private void registerEvents() {
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new PlayerListener(this), this);
        pm.registerEvents(new BlockListener(this), this);
        pm.registerEvents(new DamageListener(this), this);
        pm.registerEvents(new TNTListener(), this);
        pm.registerEvents(new MovementListener(), this);
    }

    public static Smash getInstance() {
        return instance;
    }

    public Gamestates getGamestate() {
        return gamestates;
    }
}