package de.terrocraft.smesh.listeners;

import de.terrocraft.smesh.Gamestates;
import de.terrocraft.smesh.Smash;
import de.terrocraft.smesh.managers.ChatManager;
import de.terrocraft.smesh.managers.MachMakeManager;
import de.terrocraft.smesh.managers.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
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
        if (p.getUniqueId().equals("bd6e02a7a569464585526349639aa799")) {
            event.setJoinMessage(new ChatManager(smash).prefix + new ChatManager(smash).format("&4!Achtung! Ein Controller (Arien) ist ge joint, bringt euch in sicherheit!"));
            new PlayerManager(smash).handle(p);
            return;
        }
        event.setJoinMessage(new ChatManager(smash).prefix + new ChatManager(smash).format("§aEin §e" + p.getName() + " §aist auf dem Server gehüpft!"));
        new PlayerManager(smash).handle(p);
    }


    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player p = event.getPlayer();
        if (Smash.getInstance().getGamestate().equals(Gamestates.INGAME)) {
            MachMakeManager.PlayersInRound.remove(p);
            MachMakeManager.PlayerDeaths.remove(p);
            Bukkit.broadcastMessage(new ChatManager(smash).prefix + new ChatManager(smash).format("&4&lDer spieler &6" + p.getName() + " &ahat den server verlassen und ist somit raus!"));
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

        if (MachMakeManager.BypassPlayers.contains(player)) return;

        if (event.getNewGameMode() == GameMode.SURVIVAL) {
            MovementListener.jumpedPlayers.remove(player);
            MovementListener.cooldowns.remove(player);
            MovementListener.lastVelocity.remove(player);
        }

        player.setAllowFlight(true);
    }
}
