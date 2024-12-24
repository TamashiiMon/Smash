package de.terrocraft.smesh.countdowns;

import de.terrocraft.smesh.Gamestates;
import de.terrocraft.smesh.Smash;
import de.terrocraft.smesh.listeners.DamageListener;
import de.terrocraft.smesh.managers.ChatManager;
import de.terrocraft.smesh.managers.MachMakeManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class PreGameTimer {

    private Smash smash;

    public PreGameTimer(Smash main) {
        this.smash = main;
    }

    public void startCountdown() {
        Smash.getInstance().setGamestate(Gamestates.PREGAME);
        new BukkitRunnable() {

            int number = 10;

            @Override
            public void run() {
                if (number > 0) {
                    if (number == 10) {
                        Bukkit.broadcastMessage(new ChatManager(smash).prefix + "§aGame starting in 10 seconds.");
                    }
                    if (number == 5) {
                        Bukkit.broadcastMessage(new ChatManager(smash).prefix + "§aGame starting in 5 seconds.");
                    }
                    if (number == 4) {
                        Bukkit.broadcastMessage(new ChatManager(smash).prefix + "§aGame starting in 4 seconds.");
                    }
                    if (number == 3) {
                        Bukkit.broadcastMessage(new ChatManager(smash).prefix + "§aGame starting in 3 seconds.");
                    }
                    if (number == 2) {
                        Bukkit.broadcastMessage(new ChatManager(smash).prefix + "§aGame starting in 2 seconds.");
                    }
                    if (number == 1) {
                        Bukkit.broadcastMessage(new ChatManager(smash).prefix + "§aGame starting in 1 second.");
                    }
                    number--;
                } else {
                    Bukkit.broadcastMessage(new ChatManager(smash).prefix + "§a§lThe game has now started!");
                    DamageListener.KnockbackPercentage.clear();
                    smash.setGamestate(Gamestates.INGAME);

                    for (Player player : Bukkit.getOnlinePlayers()) {
                        if (MachMakeManager.BypassPlayers.contains(player)) return;
                        MachMakeManager.GameStartEvent(player);
                    }
                    cancel();
                }
            }
        }.runTaskTimer(smash, 20L, 20L);
    }

}
