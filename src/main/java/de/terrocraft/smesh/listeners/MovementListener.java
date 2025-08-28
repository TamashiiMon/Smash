package de.terrocraft.smesh.listeners;

import de.terrocraft.smesh.Gamestates;
import de.terrocraft.smesh.Smash;
import de.terrocraft.smesh.managers.ChatManager;
import de.terrocraft.smesh.managers.MachMakeManager;
import de.terrocraft.smesh.managers.PlayerManager;
import de.terrocraft.smesh.managers.WorldManager;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.scheduler.BukkitRunnable;
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

    private final Set<Player> smashedPlayers = new HashSet<>();

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

        if (Smash.getInstance().getGamestate() == Gamestates.PREGAME) {
            if (event.getFrom().getX() != event.getTo().getX() ||
                    event.getFrom().getY() != event.getTo().getY() ||
                    event.getFrom().getZ() != event.getTo().getZ()) {

                event.setTo(event.getFrom());
            }
            return;
        }

        Location lobbyloc = new Location(Bukkit.getWorld("world"), 0.5, 101, 0.5);

        if (player.getLocation().getBlockY() < 70) {
            World world = Bukkit.getWorld(WorldManager.smashWorldName);
            if (lobbyloc == null) {
                Bukkit.getLogger().severe("Ingame or lobby location is not set in the configuration.");
                return;
            }
            smashedPlayers.remove(player);
            Location loc = new Location(world, 0, 85, 0);
            if (Smash.getInstance().getGamestate() == Gamestates.INGAME) {
                if (MachMakeManager.PlayersInRound.contains(player)) {

                    if (world == null) {
                        Bukkit.getLogger().severe("World 'world' not found!");
                        return;
                    }
                    PlayerManager.teleportToRandomBlock(player, loc, 25);

                    player.sendMessage(ChatManager.hex("#df3f2dDu bist runter gefallen!"));

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
            } else if (Smash.getInstance().getGamestate() == Gamestates.LOBBY || Smash.getInstance().getGamestate() == Gamestates.PREGAME || Smash.getInstance().getGamestate() == Gamestates.ENDGAME) {
                player.teleport(lobbyloc);
            }

            if (Smash.getInstance().getGamestate() == Gamestates.LOBBY) {
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
            // Setze die Geschwindigkeit wie zuvor
            Vector previousVelocity = lastVelocity.get(player);
            if (previousVelocity != null) {
                Vector newVelocity = previousVelocity.clone().setY(-fallSpeedMultiplier);
                cooldowns.put(player, cooldownTime);
                newVelocity.setX(previousVelocity.getX() * 0.5);
                newVelocity.setZ(previousVelocity.getZ() * 0.5);
                player.setVelocity(newVelocity);

                // Smash-Effekt: Luftstoß um den Spieler
                Location location = player.getLocation();
                World world = player.getWorld();
                int airParticleCount = 30; // Anzahl der Partikel pro Tick
                double airRadius = 1.5;   // Radius des Effekts

                for (int i = 0; i < airParticleCount; i++) {
                    double angle = Math.random() * 2 * Math.PI; // Zufälliger Winkel
                    double xOffset = Math.cos(angle) * airRadius;
                    double zOffset = Math.sin(angle) * airRadius;
                    double yOffset = Math.random() * 0.5 - 0.25; // Leichte Höhenvariation

                    Location particleLocation = location.clone().add(xOffset, yOffset, zOffset);
                    world.spawnParticle(Particle.CLOUD, particleLocation, 0, 0.1, 0.1, 0.1, 0.05);
                }

                smashedPlayers.add(player);
            }
        }

        if (smashedPlayers.contains(player) && player.isOnGround()) {
            Location location = player.getLocation();
            World world = player.getWorld();

            world.playSound(location, Sound.ENTITY_BREEZE_SHOOT, 0.25f, 1f);

            double knockbackStrength = 1.3;
            double knockbackRadius = 3;
            for (Player nearbyPlayer : world.getPlayers()) {
                if (nearbyPlayer.equals(player)) continue;
                if (nearbyPlayer.getLocation().distance(location) <= knockbackRadius) {
                    Vector knockback = nearbyPlayer.getLocation().toVector().subtract(location.toVector()).normalize();
                    knockback.multiply(knockbackStrength);
                    nearbyPlayer.setVelocity(knockback);
                }
            }

            smashedPlayers.remove(player);
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
