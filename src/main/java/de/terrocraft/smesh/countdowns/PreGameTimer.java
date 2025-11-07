package de.terrocraft.smesh.countdowns;

import de.terrocraft.smesh.Smash;
import de.terrocraft.smesh.enums.Gamestates;
import de.terrocraft.smesh.listeners.DamageListener;
import de.terrocraft.smesh.services.ChatService;
import de.terrocraft.smesh.services.MachMakeService;
import de.terrocraft.smesh.services.WorldService;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class PreGameTimer {

    private final Smash smash;
    private final String worldName;

    public PreGameTimer(Smash main, String worldName) {
        this.smash = main;
        this.worldName = worldName;
    }

    public void startCountdown() {
        WorldService worldService = new WorldService(Smash.getInstance().getDataFolder());
        worldService.loadMap(worldName);
        Smash.getInstance().setGamestate(Gamestates.Countdown);
        new BukkitRunnable() {

            int number = 10;

            @Override
            public void run() {
                if (number > 0) {
                    if (number != 1) {
                        Bukkit.broadcastMessage(new ChatService(smash).prefix + ChatService.hex("#df3f2dGame starting in #e06153§l" + number + " #df3f2dseconds."));
                    }

                    if (number == 1) {
                        Bukkit.broadcastMessage(new ChatService(smash).prefix + ChatService.hex("#df3f2dGame starting in #e06153§l1 #df3f2dsecond."));
                    }

                    if (number == 5) {
                        Smash.getInstance().setGamestate(Gamestates.PREGAME);
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            if (MachMakeService.bypassPlayers.contains(player)) return;
                            MachMakeService.GameStartEvent(player);
                        }
                    }

                    number--;
                } else {
                    Bukkit.broadcastMessage(new ChatService(smash).prefix + ChatService.hex("#df3f2dThe game has now started!"));
                    DamageListener.KnockbackPercentage.clear();
                    smash.setGamestate(Gamestates.INGAME);
                    cancel();
                }
            }
        }.runTaskTimer(smash, 20L, 20L);
    }

}
