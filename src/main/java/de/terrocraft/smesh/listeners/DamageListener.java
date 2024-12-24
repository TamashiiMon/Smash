package de.terrocraft.smesh.listeners;

import de.terrocraft.smesh.Gamestates;
import de.terrocraft.smesh.Smash;
import de.terrocraft.smesh.managers.MachMakeManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;

public class DamageListener implements Listener {

    public static final HashMap<Player, Double> KnockbackPercentage = new HashMap<>();
    private final Smash smash;
    private final long cooldownTime = 500;
    private final Map<Player, Long> cooldowns = new HashMap<>();

    public DamageListener(Smash smash) {
        this.smash = smash;
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getEntity();
        if (MachMakeManager.BypassPlayers.contains(player)) return;

        if (event.getCause() == EntityDamageEvent.DamageCause.FALL ||
                event.getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION ||
                event.getCause() == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION) {
            event.setCancelled(true);
            return;
        }

        if (smash.getGamestate() == Gamestates.LOBBY || smash.getGamestate() == Gamestates.PREGAME) {
            event.setCancelled(true);
            return;
        }

        double damage = (int) event.getDamage();
        event.setDamage(0);

        double currentKnockback = KnockbackPercentage.getOrDefault(player, 0.0);



        if (damage < 3) {
            KnockbackPercentage.put(player, currentKnockback + 1.5);
        } else {
            KnockbackPercentage.put(player, currentKnockback + 3.5);
        }
    }



    @EventHandler
    public void onHit(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        Player victim = (Player) event.getEntity();
        if (MachMakeManager.BypassPlayers.contains(victim)) return;

        Player attacker = null;
        if (event.getDamager() instanceof Player) {
            attacker = (Player) event.getDamager();
        }

        if (cooldowns.containsKey(victim)) {
            long lastHit = cooldowns.get(victim);
            if (System.currentTimeMillis() - lastHit < cooldownTime) {
                event.setCancelled(true);
                return;
            }
        }

        double knockbackPercentage = KnockbackPercentage.getOrDefault(victim, 0.0);
        double knockbackStrength = calculateKnockbackStrength(knockbackPercentage);

        Vector knockbackVector;
        if (attacker != null) {
            knockbackVector = victim.getLocation().toVector().subtract(attacker.getLocation().toVector()).normalize();
        } else {
            knockbackVector = new Vector(1, 0, 0).normalize();
        }

        knockbackVector.setY(0);

        knockbackVector.multiply(knockbackStrength);

        victim.setVelocity(knockbackVector);

        cooldowns.put(victim, System.currentTimeMillis());
        if (attacker != null) {
            cooldowns.put(attacker, System.currentTimeMillis());
        }
    }

    private double calculateKnockbackStrength(double percentage) {
        // Flüssiger Anstieg des Knockbacks mit einer exponentiellen Skalierung
        double baseKnockback = 2.0; // Minimaler Knockback
        double scalingFactor = 0.1; // Stärke des Anstiegs
        return baseKnockback + (Math.pow(percentage * scalingFactor, 1.5));
    }

}
