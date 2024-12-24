package de.terrocraft.smesh.managers;

import de.terrocraft.smesh.Gamestates;
import de.terrocraft.smesh.Smash;
import de.terrocraft.smesh.listeners.DamageListener;
import jdk.javadoc.internal.doclets.toolkit.taglets.IndexTaglet;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import sun.jvm.hotspot.utilities.IntArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MachMakeManager {
    public static HashMap<Player, Integer> PlayerDeaths = new HashMap<>();

    public static int Lives = 3;

    public static List<Player> PlayersInRound  = new ArrayList<>();

    public static List<Player> BypassPlayers = new ArrayList<>();

    public static void EndGameEvent(Player player) {
        try {
            if (player == null || player.getWorld() == null) {
                Bukkit.getLogger().warning("EndGameEvent: Player or player's world is null.");
                return;
            }
            if (MachMakeManager.BypassPlayers.contains(player)) return;

            Smash.getInstance().setGamestate(Gamestates.LOBBY);

            Location loc = Smash.config.getLocation("lobby-location");
            player.teleport(loc);

            player.setGameMode(GameMode.ADVENTURE);
            player.setAllowFlight(true);
            player.setMaxHealth(20);
            player.setHealth(20);
            player.setFoodLevel(20);

            DamageListener.KnockbackPercentage.remove(player);
            if (PlayerDeaths.containsKey(player)) PlayersInRound.remove(player);

            PlayerDeaths.remove(player);

            PlayerManager playerManager = new PlayerManager(Smash.getInstance());
            playerManager.handle(player);

            Smash.getInstance().spectating.remove(player);
            Smash.getInstance().vanished.remove(player);

        } catch (Exception e) {
            Bukkit.getLogger().severe("An error occurred in EndGameEvent for player: " + (player != null ? player.getName() : "null"));
            e.printStackTrace();
        }
    }


    public static void GameStartEvent(Player player) {

        Location loc = new Location(Bukkit.getWorld("world"), 109, 107, -376);

        if (MachMakeManager.BypassPlayers.contains(player)) return;

        PlayerManager.teleportToRandomBlock(player, loc, 25);

        player.setGameMode(GameMode.ADVENTURE);
        player.setMaxHealth(MachMakeManager.Lives * 2);
        player.setHealth(MachMakeManager.Lives * 2);
        player.setFoodLevel(6);
        MachMakeManager.PlayerDeaths.put(player, 0);
        PlayersInRound.add(player);
    }

    public static void PlayerDeathEvent(Player player) {
        try {

            if (MachMakeManager.BypassPlayers.contains(player)) return;

            player.setMaxHealth(20);
            player.setHealth(20);
            player.setFoodLevel(20);
            player.setAllowFlight(true);
            player.setExp(0);
            player.setTotalExperience(0);

            player.setGameMode(GameMode.SPECTATOR);

            Bukkit.broadcastMessage(new ChatManager(Smash.getInstance()).prefix + player.getName() + " is dead.");

            if (PlayerDeaths.containsKey(player)) {
                PlayerDeaths.remove(player);
            }

            if (PlayersInRound.contains(player)) {
                PlayersInRound.remove(player);
            }

            Smash.getInstance().spectating.add(player);
            Smash.getInstance().vanished.add(player);

            if (DamageListener.KnockbackPercentage.containsKey(player)) {
                DamageListener.KnockbackPercentage.remove(player);
            }

            World world = Bukkit.getWorld("world");
            if (world != null) {
                Location loc = new Location(world, 109, 107, -376);
                player.teleport(loc);
            } else {
                Bukkit.getLogger().severe("World 'world' not found for teleportation!");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public static void MachMakeUpdateEvent() {
        if (PlayersInRound.size() == 1) {
            Player winner = PlayersInRound.get(0);

            Smash.getInstance().setGamestate(Gamestates.ENDGAME);

            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                EndGameEvent(onlinePlayer);
            }

            Bukkit.broadcastMessage(new ChatManager(Smash.getInstance()).prefix + new ChatManager(Smash.getInstance()).format("&a&lDer Spieler &6" + winner.getName() + " hat gewonnen!"));
        }


    }

}
