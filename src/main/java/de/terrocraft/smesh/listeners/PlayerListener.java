package de.terrocraft.smesh.listeners;

import de.terrocraft.smesh.Smash;
import de.terrocraft.smesh.enums.Gamestates;
import de.terrocraft.smesh.services.ChatService;
import de.terrocraft.smesh.services.MachMakeService;
import de.terrocraft.smesh.services.PlayerService;
import de.terrocraft.smesh.utils.CoinManager;
import de.terrocraft.smesh.utils.PlayerActionBar;
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

    private final Smash smash;

    public PlayerListener(Smash main) {
        this.smash = main;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        event.setJoinMessage(new ChatService(smash).prefix + ChatService.hex("#df3f2dEin #e06153" + p.getName() + " #df3f2dist auf dem Server gehüpft!"));


        if (event.getPlayer().getName().equals("NeonSprout")) {
            p.setDisplayName("Zero_Two");
        }
        FastBoard board = new FastBoard(p);
        board.updateTitle(ChatColor.RED + "Smash");
        PlayerActionBar.boards.put(p.getUniqueId(), board);

        CoinManager.doesPlayerExist(p.getUniqueId()).thenAccept(exists -> {
            if (!exists) {
                CoinManager.savePlayerData(p.getUniqueId(), 0, 0);
            }
        });

        new PlayerService(smash).handle(p);
    }


    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player p = event.getPlayer();
        if (Smash.getInstance().getGamestate().equals(Gamestates.INGAME)) {
            MachMakeService.playersInRound.remove(p);
            MachMakeService.playerDeaths.remove(p);
            Bukkit.broadcastMessage(new ChatService(smash).prefix + ChatService.hex("#df3f2dDer spieler #e06153" + p.getName() + " #df3f2dhat den server verlassen und ist somit raus!"));
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
        if (MachMakeService.bypassPlayers.contains(p)) return;
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

        event.setCancelled(true);

        Bukkit.getScheduler().runTask(Smash.getInstance(), () -> {
            if (event.getPlayer().hasPermission("smash.hex.chat")) {
                Bukkit.broadcastMessage(ChatService.hex("<" + event.getPlayer().getDisplayName() + ">" + " " + event.getMessage()));
            } else {
                Bukkit.broadcastMessage(ChatService.hex("<" + event.getPlayer().getDisplayName() + ">") + " " + event.getMessage());
            }

            if (message.contains("darling")) {
                if (!event.getPlayer().getName().equals("NeonSprout")) {
                    Bukkit.getScheduler().runTaskLater(Smash.getInstance(), () -> {
                        Bukkit.broadcastMessage("<Zero_Two> Machst du mir etwa nach?");
                    }, 50);
                }
            }
        });
    }
}
