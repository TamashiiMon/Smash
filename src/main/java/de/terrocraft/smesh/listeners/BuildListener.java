package de.terrocraft.smesh.listeners;

import de.terrocraft.smesh.Smash;
import de.terrocraft.smesh.managers.ChatManager;
import de.terrocraft.smesh.managers.MachMakeManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class BuildListener implements Listener {

    private Smash smash;

    public BuildListener(Smash main) {
        this.smash = main;
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Player p = event.getPlayer();
        if (MachMakeManager.BypassPlayers.contains(p)) return;
        if (!p.hasPermission("Build")) {
            event.setCancelled(true);
            p.sendMessage(new ChatManager(smash).prefix + "You cannot build before the game!");
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        Player p = event.getPlayer();
        if (MachMakeManager.BypassPlayers.contains(p)) return;
        if (!p.hasPermission("Build")) {
            event.setCancelled(true);
            p.sendMessage(new ChatManager(smash).prefix + "You cannot build before the game!");
        }
    }
}
