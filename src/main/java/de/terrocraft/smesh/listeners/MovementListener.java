package de.terrocraft.smesh.listeners;

import de.terrocraft.smesh.Gamestates;
import de.terrocraft.smesh.Smash;
import de.terrocraft.smesh.managers.MachMakeManager;
import de.terrocraft.smesh.managers.PlayerManager;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MovementListener implements Listener {
    public static Set<Player> jumpedPlayers = new HashSet<>();
    public static Map<Player, Long> cooldowns = new HashMap<>();
    public static Map<Player, Vector> lastVelocity = new HashMap<>();
    private double verticalJumpPower = 1.3;
    private double horizontalJumpPower = 1;
    private double fallSpeedMultiplier = 2.5;
    private long cooldownTime = 2000;

    @EventHandler
    public void setFly(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        player.setAllowFlight(true);
        player.setFlying(false);
    }

    @EventHandler
    public void move(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (MachMakeManager.BypassPlayers.contains(player)) return;

        Location lobbyloc = Smash.config.getLocation("lobby-location");

        if (player.getLocation().getBlockY() < 70) {
            World world = Bukkit.getWorld("world");
            if (world == null) {
                Bukkit.getLogger().severe("World 'world' not found!");
                return;
            }
            if (lobbyloc == null) {
                Bukkit.getLogger().severe("Ingame or lobby location is not set in the configuration.");
                return;
            }
            Location loc = new Location(world, 109, 117, -376);
            if (Smash.getInstance().getGamestate() == Gamestates.INGAME) {
                if (MachMakeManager.PlayersInRound.contains(player)) {

                    PlayerManager.teleportToRandomBlock(player, loc, 25);

                    player.sendMessage("Du bist runter gefallen!");

                    MachMakeManager.PlayerDeaths.put(player, MachMakeManager.PlayerDeaths.getOrDefault(player, 0) + 1);

                    int remainingHealth = Math.max(1, MachMakeManager.Lives * 2 - MachMakeManager.PlayerDeaths.get(player) * 2);
                    player.setMaxHealth(remainingHealth);
                    player.setHealth(remainingHealth);

                    if (MachMakeManager.PlayerDeaths.getOrDefault(player, 0) == MachMakeManager.Lives) {
                        MachMakeManager.PlayerDeathEvent(player);
                    }
                } else {
                    player.teleport(loc);
                    MachMakeManager.PlayerDeaths.remove(player);
                }
            } else {
                player.teleport(lobbyloc);
            }

            jumpedPlayers.remove(player);
            cooldowns.remove(player);
            lastVelocity.remove(player);
            DamageListener.KnockbackPercentage.remove(player);
            player.setAllowFlight(true);
        }

        Location locUnderPlayer = player.getLocation().subtract(0, 1, 0);
        if (locUnderPlayer.getBlock().getType() != Material.AIR) {
            if (jumpedPlayers.contains(player)) {
                jumpedPlayers.remove(player);
                cooldowns.remove(player);
                lastVelocity.remove(player);
                player.setAllowFlight(true);
            }
        }

        if (player.isSneaking() && !player.isOnGround() && jumpedPlayers.contains(player)) {
            Vector previousVelocity = lastVelocity.get(player);
            if (previousVelocity != null) {
                Vector newVelocity = previousVelocity.clone().setY(-fallSpeedMultiplier);
                cooldowns.put(player, cooldownTime);
                newVelocity.setX(previousVelocity.getX() * 0.5);
                newVelocity.setZ(previousVelocity.getZ() * 0.5);
                player.setVelocity(newVelocity);
            }
        }
    }


    @EventHandler
    public void setVelocity(PlayerToggleFlightEvent event) {
        Player player = event.getPlayer();
        if (MachMakeManager.BypassPlayers.contains(player)) return;

        if (player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR) {
            return;
        }


        if (cooldowns.containsKey(player)) {
            long lastJump = cooldowns.get(player);
            if (System.currentTimeMillis() - lastJump < cooldownTime) {
                event.setCancelled(true);
                return;
            }
        }

        event.setCancelled(true);

        if (jumpedPlayers.contains(player)) {
            event.setCancelled(true);
        } else {
            player.setAllowFlight(false);
            player.setFlying(false);

            lastVelocity.put(player, player.getVelocity());

            player.setVelocity(player.getLocation().getDirection().multiply(horizontalJumpPower).setY(verticalJumpPower));

            player.playSound(player, Sound.ENTITY_BREEZE_JUMP, 1, 1);
            player.setAllowFlight(false);
            jumpedPlayers.add(player);
            cooldowns.put(player, System.currentTimeMillis());
        }
    }
}
