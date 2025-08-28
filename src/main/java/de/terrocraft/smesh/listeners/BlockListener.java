package de.terrocraft.smesh.listeners;

import com.nexomc.nexo.api.events.custom_block.NexoBlockBreakEvent;
import com.nexomc.nexo.api.events.furniture.NexoFurnitureBreakEvent;
import de.terrocraft.smesh.Smash;
import de.terrocraft.smesh.managers.ChatManager;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class BlockListener implements Listener {

    private Smash smash;

    public BlockListener(Smash main) {
        this.smash = main;
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Player p = event.getPlayer();
        if (!p.hasPermission("Build")) {
            event.setCancelled(true);
            p.sendMessage(new ChatManager(smash).prefix + "You cannot break!");
        }
    }

    @EventHandler
    public void onNexoFurnitureBreak(NexoFurnitureBreakEvent event) {
        Player p = event.getPlayer();
        if (!p.hasPermission("Build")) {
            event.setCancelled(true);
            p.sendMessage(new ChatManager(smash).prefix + "You cannot break!");
        }
    }

    @EventHandler
    public void onNexoBreak(NexoBlockBreakEvent event) {
        Player p = event.getPlayer();
        if (!p.hasPermission("Build")) {
            event.setCancelled(true);
            p.sendMessage(new ChatManager(smash).prefix + "You cannot break!");
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        Player p = event.getPlayer();
        if (!p.hasPermission("Build")) {
            event.setCancelled(true);
            p.sendMessage(new ChatManager(smash).prefix + "You cannot build!");
        }
    }

}
