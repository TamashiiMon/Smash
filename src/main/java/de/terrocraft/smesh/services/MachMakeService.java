package de.terrocraft.smesh.services;

import de.terrocraft.smesh.Smash;
import de.terrocraft.smesh.countdowns.PreGameTimer;
import de.terrocraft.smesh.enums.Gamestates;
import de.terrocraft.smesh.listeners.DamageListener;
import de.terrocraft.smesh.utils.CoinManager;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class MachMakeService {
    public static HashMap<Player, Integer> playerDeaths = new HashMap<>();

    public static int lives = 3;

    public static List<Player> playersInRound = new ArrayList<>();

    public static List<Player> bypassPlayers = new ArrayList<>();

    public static void EndGameEvent(Player player) {
        try {
            if (player == null || player.getWorld() == null) {
                Bukkit.getLogger().warning("EndGameEvent: Player or player's world is null.");
                return;
            }
            if (MachMakeService.bypassPlayers.contains(player)) return;

            Smash.getInstance().setGamestate(Gamestates.LOBBY);

            Location loc = new Location(Bukkit.getWorld("world"), 0, 101, 0);
            player.teleport(loc);

            player.setGameMode(GameMode.ADVENTURE);
            player.setAllowFlight(true);
            player.setMaxHealth(20);
            player.setHealth(20);
            player.setFoodLevel(20);

            DamageListener.KnockbackPercentage.remove(player);
            if (playerDeaths.containsKey(player)) playersInRound.remove(player);

            playerDeaths.remove(player);

            PlayerService playerService = new PlayerService(Smash.getInstance());
            playerService.handle(player);

            Smash.getInstance().spectating.remove(player);
            Smash.getInstance().vanished.remove(player);

        } catch (Exception e) {
            Bukkit.getLogger().severe("An error occurred in EndGameEvent for player: " + (player != null ? player.getName() : "null"));
            e.printStackTrace();
        }
    }


    public static void GameStartEvent(Player player) {

        Location loc = new Location(Bukkit.getWorld(WorldService.smashWorldName), 0, 85, 0);

        if (MachMakeService.bypassPlayers.contains(player)) return;

        PlayerService.teleportToRandomBlock(player, loc, 25);

        player.setGameMode(GameMode.ADVENTURE);
        player.setMaxHealth(MachMakeService.lives * 2);
        player.setHealth(MachMakeService.lives * 2);
        player.setFoodLevel(6);
        player.setAllowFlight(true);
        MachMakeService.playerDeaths.put(player, 0);
        playersInRound.add(player);
    }

    public static void PlayerDeathEvent(Player player) {
        try {

            if (MachMakeService.bypassPlayers.contains(player)) return;

            player.setMaxHealth(20);
            player.setHealth(20);
            player.setFoodLevel(20);
            player.setAllowFlight(true);
            player.setExp(0);
            player.setTotalExperience(0);

            player.setGameMode(GameMode.SPECTATOR);

            Bukkit.broadcastMessage(new ChatService(Smash.getInstance()).prefix + ChatService.hex("#e06153" + player.getName() + " #df3f2dis dead."));

            playerDeaths.remove(player);

            playersInRound.remove(player);

            Smash.getInstance().spectating.add(player);
            Smash.getInstance().vanished.add(player);

            DamageListener.KnockbackPercentage.remove(player);

            World world = Bukkit.getWorld(WorldService.smashWorldName);
            if (world != null) {
                Location loc = new Location(world, 0, 0, 0);
                player.teleport(loc);
            } else {
                Bukkit.getLogger().severe("World 'world' not found for teleportation!");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void MachMakeUpdateEvent() {
        if (playersInRound.size() == 1) {
            Player winner = playersInRound.get(0);

            Smash.getInstance().setGamestate(Gamestates.ENDGAME);

            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                EndGameEvent(onlinePlayer);
            }

            CoinManager.addWins(winner.getUniqueId(), 1);
            CoinManager.addCoins(winner.getUniqueId(), 5);

            Bukkit.broadcastMessage(new ChatService(Smash.getInstance()).prefix + ChatService.hex("#df3f2Der Spieler #e06153" + winner.getName() + " #df3f2hat gewonnen!"));
        }

    }

    private static final boolean AutostartEnabled = Smash.config.getBoolean("AutoGameStart");
    private static final int minplayersforautostart = Smash.config.getInt("AutoGameStartMinPlayers");
    private static final WorldService worldService = new WorldService(Smash.getInstance().getDataFolder());

    public static void triggerautostartcheck() {
        // Prüfen, ob der Autostart aktiviert ist
        if (!AutostartEnabled) {
            return;
        }

        // Prüfen, ob die Mindestspieleranzahl erreicht ist
        int onlinePlayers = Bukkit.getOnlinePlayers().size();
        if (onlinePlayers < minplayersforautostart) {
            return;
        }

        // Kartenliste abrufen
        List<String> mapNames = worldService.getMapNames();
        if (mapNames.isEmpty()) {
            return;
        }

        Random rand = new Random();
        String randomMapName = mapNames.get(rand.nextInt(mapNames.size()));

        if (!worldService.doesMapExist(randomMapName)) {
            System.out.println("Karte existiert nicht: " + randomMapName);
            return;
        }

        Gamestates currentState = Smash.getInstance().getGamestate();
        if (currentState.equals(Gamestates.INGAME) || currentState.equals(Gamestates.PREGAME) || currentState.equals(Gamestates.ENDGAME) || currentState.equals(Gamestates.Countdown)) {
            return;
        }

        new PreGameTimer(Smash.getInstance(), randomMapName).startCountdown();
    }


}
