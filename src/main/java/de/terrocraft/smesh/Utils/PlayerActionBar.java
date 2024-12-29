package de.terrocraft.smesh.Utils;

import com.google.common.base.Strings;
import de.terrocraft.smesh.Gamestates;
import de.terrocraft.smesh.Smash;
import de.terrocraft.smesh.listeners.DamageListener;
import de.terrocraft.smesh.managers.ChatManager;
import de.terrocraft.smesh.managers.MachMakeManager;
import fr.mrmicky.fastboard.FastBoard;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import java.util.*;

public class PlayerActionBar {
    private static Scoreboard scoreboard;
    private static double knockbackPercentage;
    public static final Map<UUID, FastBoard> boards = new HashMap<>();

    public static void start() {
        // Initialize the scoreboard when the plugin starts
        initializeScoreboard();

        new BukkitRunnable() {
            @Override
            public void run() {
                if (Smash.getInstance().getGamestate().equals(Gamestates.INGAME)) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        if (MachMakeManager.BypassPlayers.contains(player)) return;
                        sendPercentageToPlayer(player);
                    }

                    MachMakeManager.MachMakeUpdateEvent();
                }
                MachMakeManager.triggerautostartcheck();
                if (!DamageListener.KnockbackPercentage.isEmpty()) {

                    if (Smash.getInstance().getGamestate() == Gamestates.ENDGAME) {
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            if (MachMakeManager.BypassPlayers.contains(player)) return;
                            MachMakeManager.EndGameEvent(player);
                        }
                        Smash.getInstance().setGamestate(Gamestates.LOBBY);
                    }
                }
                for (FastBoard board : boards.values()) {
                    updateBoard(board);
                }
            }
        }.runTaskTimer(Smash.getInstance(), 3L, 5L);
    }

    private static void initializeScoreboard() {
        // Get the scoreboard manager and create a new scoreboard
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        scoreboard = manager.getNewScoreboard();
    }

    private static void sendPercentageToPlayer(Player player) {
        if (DamageListener.KnockbackPercentage.get(player) == null || DamageListener.KnockbackPercentage.get(player) == 0) {
            knockbackPercentage = 0;

        } else {
            knockbackPercentage = DamageListener.KnockbackPercentage.get(player);
        }

        String color;
        if (knockbackPercentage > 150) {
            color = "§4";
        } else if (knockbackPercentage > 100) {
            color = "§c";
        } else if (knockbackPercentage > 50) {
            color = "§e";
        } else {
            color = "§a";
        }

        String knockbackMessage = color + knockbackPercentage + "%";
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(knockbackMessage));

        updatePlayerNametag(player, knockbackPercentage, color);
    }

    private static void updatePlayerNametag(Player player, double knockbackPercentage, String color) {
        Team team = scoreboard.getTeam(player.getName());
        if (team == null) {
            team = scoreboard.registerNewTeam(player.getName());
        }

        // Set the prefix to display the percentage above the player's head
        team.setPrefix(color + knockbackPercentage + "% ");

        // Add the player to the team
        team.addEntry(player.getName());

        // Set the global scoreboard for all players
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            onlinePlayer.setScoreboard(scoreboard);
        }
    }

    private static void updateBoard(FastBoard board) {
        if (Smash.getInstance().getGamestate().equals(Gamestates.INGAME) || Smash.getInstance().getGamestate().equals(Gamestates.ENDGAME)) {
            List<String> lines = new ArrayList<>();
            lines.add("Spiel ist an laufen");

            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.getHealth() <= 6) {
                    int fullHearts = (int) (player.getHealth() / 2);
                    String hearts = "§c\u2754".repeat(fullHearts);
                    lines.add(ChatManager.hex("#e06153" + player.getName() + ": " + hearts));
                }

                if (!boards.containsKey(player.getUniqueId())) {
                    FastBoard playerbord = new FastBoard(player);
                    board.updateTitle(ChatColor.RED + "Smash");
                    PlayerActionBar.boards.put(player.getUniqueId(), playerbord);
                }
            }

            if (lines.size() == 1) {
                // Keine Spieler mit niedriger Gesundheit gefunden
                lines.add("Keine kritischen Spieler");
            }

            board.updateLines(lines);
        } else {
            board.updateLines(
                    "Kein spiel an laufen",
                    "test2",
                    "test2"
            );
        }
    }
}
