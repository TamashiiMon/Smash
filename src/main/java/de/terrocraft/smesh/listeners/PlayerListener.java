package de.terrocraft.smesh.listeners;

import de.terrocraft.smesh.Gamestates;
import de.terrocraft.smesh.Smash;
import de.terrocraft.smesh.Utils.PlayerActionBar;
import de.terrocraft.smesh.managers.ChatManager;
import de.terrocraft.smesh.managers.MachMakeManager;
import de.terrocraft.smesh.managers.PlayerManager;
import fr.mrmicky.fastboard.FastBoard;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {

    private Smash smash;

    public PlayerListener(Smash main) {
        this.smash = main;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        event.setJoinMessage(new ChatManager(smash).prefix + ChatManager.hex("#df3f2dEin #e06153" + p.getName() + " #df3f2dist auf dem Server gehüpft!"));


        if (event.getPlayer().getUniqueId().toString().equals("bfd59ae0c9054f3b903a3ce8510d6b71")) {
            p.setDisplayName("Zero_Two");
        }
        FastBoard board = new FastBoard(p);
        board.updateTitle(ChatColor.RED + "Smash");
        PlayerActionBar.boards.put(p.getUniqueId(), board);

        new PlayerManager(smash).handle(p);
    }


    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player p = event.getPlayer();
        if (Smash.getInstance().getGamestate().equals(Gamestates.INGAME)) {
            MachMakeManager.PlayersInRound.remove(p);
            MachMakeManager.PlayerDeaths.remove(p);
            Bukkit.broadcastMessage(new ChatManager(smash).prefix + ChatManager.hex("#df3f2dDer spieler #e06153" + p.getName() + " #df3f2dhat den server verlassen und ist somit raus!"));
        }


        FastBoard board = PlayerActionBar.boards.remove(p.getUniqueId());

        if (board != null) {
            board.delete();
        }
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        Player p = (Player) event.getEntity();
        if (MachMakeManager.BypassPlayers.contains(p)) return;
        if (smash.getGamestate() == Gamestates.LOBBY || smash.getGamestate() == Gamestates.PREGAME || smash.getGamestate() == Gamestates.ENDGAME) {
            p.setFoodLevel(20);
            event.setCancelled(true);
        } else if (smash.getGamestate() == Gamestates.INGAME) {
            p.setFoodLevel(6);
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onGameModeChange(PlayerGameModeChangeEvent event) {
        Player player = event.getPlayer();

        if (event.getNewGameMode() == GameMode.SURVIVAL) {
            MovementListener.jumpedPlayers.remove(player);
            MovementListener.cooldowns.remove(player);
            MovementListener.lastVelocity.remove(player);
            player.setAllowFlight(true);
        }

        player.setAllowFlight(true);
    }

    @EventHandler
    public void onChatMessage(AsyncPlayerChatEvent event) {
        String message = event.getMessage().toLowerCase();

        // Verhindert, dass die Nachricht sofort im Chat angezeigt wird
        event.setCancelled(true);

        // Nachricht des Spielers sofort senden
        Bukkit.getScheduler().runTask(Smash.getInstance(), () -> {
            if (event.getPlayer().hasPermission("smash.hex.chat")) {
                Bukkit.broadcastMessage(ChatManager.hex("<" + event.getPlayer().getDisplayName() + ">" + " " + event.getMessage()));
            } else {
                Bukkit.broadcastMessage(ChatManager.hex("<" + event.getPlayer().getDisplayName() + ">") + " " + event.getMessage());
            }

            if (message.contains("darling")) {
                Bukkit.getScheduler().runTaskLater(Smash.getInstance(), () -> {
                    Bukkit.broadcastMessage("<Zero_Two> Machst du mir etwa nach?");
                }, 50);
            }
        });
    }
}
