package de.terrocraft.smesh.countdowns;

import de.terrocraft.smesh.Gamestates;
import de.terrocraft.smesh.Smash;
import de.terrocraft.smesh.listeners.DamageListener;
import de.terrocraft.smesh.managers.ChatManager;
import de.terrocraft.smesh.managers.MachMakeManager;
import de.terrocraft.smesh.managers.WorldManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HexFormat;

public class PreGameTimer {

    private Smash smash;
    private String worldName;

    public PreGameTimer(Smash main, String worldName) {
        this.smash = main;
        this.worldName = worldName;
    }

    public void startCountdown() {
        WorldManager worldManager = new WorldManager(Smash.getInstance().getDataFolder());
        worldManager.loadMap(worldName);
        Smash.getInstance().setGamestate(Gamestates.Countdown);
        new BukkitRunnable() {

            int number = 10;

            @Override
            public void run() {
                if (number > 0) {
                    if (number != 1) {
                        Bukkit.broadcastMessage(new ChatManager(smash).prefix + ChatManager.hex("#df3f2dGame starting in #e06153§l" + number + " #df3f2dseconds."));
                    }

                    if (number == 1) {
                        Bukkit.broadcastMessage(new ChatManager(smash).prefix + ChatManager.hex("#df3f2dGame starting in #e06153§l1 #df3f2dsecond."));
                    }

                    if (number == 5) {
                        Smash.getInstance().setGamestate(Gamestates.PREGAME);
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            if (MachMakeManager.BypassPlayers.contains(player)) return;
                            MachMakeManager.GameStartEvent(player);
                        }
                    }

                    number--;
                } else {
                    Bukkit.broadcastMessage(new ChatManager(smash).prefix + ChatManager.hex("#df3f2dThe game has now started!"));
                    DamageListener.KnockbackPercentage.clear();
                    smash.setGamestate(Gamestates.INGAME);
                    cancel();
                }
            }
        }.runTaskTimer(smash, 20L, 20L);
    }

}
