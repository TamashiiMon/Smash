package de.terrocraft.smesh.managers;

import de.terrocraft.smesh.Gamestates;
import de.terrocraft.smesh.Smash;
import org.bukkit.*;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Random;


public class PlayerManager {
    private static Smash main;

    public PlayerManager(Smash main) {
        this.main = main;
    }

    public void handle(Player player) {
        if (main.getGamestate() == Gamestates.LOBBY) {
            if (MachMakeManager.BypassPlayers.contains(player)) return;
            main.alive.remove(player);
            main.spectating.remove(player);
            main.alive.add(player);
            player.setExp(0);
            player.setTotalExperience(0);
            player.setMaxHealth(20);
            player.setHealth(20);
            player.setFoodLevel(20);
            player.setGameMode(GameMode.ADVENTURE);
            player.setAllowFlight(false);
            player.sendMessage(new ChatManager(main).prefix + "Welcome to the MiniGame!");
            Bukkit.broadcastMessage(new ChatManager(main).prefix + player.getDisplayName() + " has joined the minigame.");
        } else if (main.getGamestate() == Gamestates.INGAME || main.getGamestate() == Gamestates.ENDGAME || main.getGamestate() == Gamestates.PREGAME) {
            if (MachMakeManager.BypassPlayers.contains(player)) return;
            main.alive.remove(player);
            main.spectating.remove(player);
            main.spectating.add(player);
            Bukkit.broadcastMessage(new ChatManager(main).prefix + player.getDisplayName() + " has joined as a spectator.");
        }
    }

    public static void teleportToRandomBlock(Player player, Location center, int radius) {
        if (player == null || center == null || center.getWorld() == null) {
            Bukkit.getLogger().warning("Ungültige Parameter für teleportToRandomBlock: Spieler, Center oder Welt ist null.");
            return;
        }

        if (MachMakeManager.BypassPlayers.contains(player)) return;

        Random random = new Random();
        World world = center.getWorld();

        int maxAttempts = 100;
        for (int attempts = 0; attempts < maxAttempts; attempts++) {
            int randomX = center.getBlockX() + random.nextInt(radius * 2 + 1) - radius;
            int randomZ = center.getBlockZ() + random.nextInt(radius * 2 + 1) - radius;

            int highestY = world.getHighestBlockYAt(randomX, randomZ);

            Material blockType = world.getBlockAt(randomX, highestY - 1, randomZ).getType();

            if (blockType.isSolid()) {
                Location targetLocation = new Location(world, randomX + 0.5, highestY + 1, randomZ + 0.5);

                if (canTeleportToLocation(targetLocation)) {
                    player.teleport(targetLocation);
                    Bukkit.getLogger().info("Spieler " + player.getName() + " wurde erfolgreich zu einer zufälligen Position teleportiert.");
                    return;
                }
            }
        }

        Bukkit.getLogger().warning("Es konnte keine gültige Position für Spieler " + player.getName() + " gefunden werden.");
    }

    private static boolean canTeleportToLocation(Location location) {
        if (location == null || location.getWorld() == null) {
            return false;
        }

        World world = location.getWorld();

        Material targetBlock = world.getBlockAt(location).getType();
        Material blockAbove = world.getBlockAt(location.clone().add(0, 1, 0)).getType();

        return targetBlock == Material.AIR && blockAbove == Material.AIR;
    }

}
