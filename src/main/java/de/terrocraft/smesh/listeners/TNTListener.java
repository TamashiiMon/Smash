package de.terrocraft.smesh.listeners;

import de.terrocraft.smesh.Smash;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.util.Vector;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TNTListener implements Listener {
    private static final Pattern integerTagPattern = Pattern.compile("R=\\((\\d+)\\)");

    public static int getIntegerFromSpecificTag(String tag) {
        Matcher matcher = integerTagPattern.matcher(tag);

        if (matcher.find()) {
            int extractedInteger = Integer.parseInt(matcher.group(1));
            return extractedInteger;
        }
        return -1;
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {

        Entity entity = event.getEntity();
        if (entity instanceof TNTPrimed) {

            TNTPrimed tnt = (TNTPrimed) entity;

            for (String scoreboardTag : tnt.getScoreboardTags()) {
                int explosionPower = getIntegerFromSpecificTag(scoreboardTag);
                if (explosionPower > -1) {
                    tnt.getWorld().spawnParticle(Particle.EXPLOSION, tnt.getLocation(), 1);

                    for (Entity nearbyEntity : tnt.getNearbyEntities(explosionPower * 2, explosionPower * 2, explosionPower * 2)) {
                        if (nearbyEntity instanceof LivingEntity) {
                            LivingEntity livingEntity = (LivingEntity) nearbyEntity;

                            double distance = livingEntity.getLocation().distance(tnt.getLocation());

                            double damage = Math.max(0, (explosionPower * 1.5) - (distance / 2));
                            damage = Math.min(damage, 3);

                            if (distance < 1.0) {
                                Vector knockback = livingEntity.getLocation().toVector().subtract(tnt.getLocation().toVector()).normalize();
                                knockback.multiply(0.3);  // Rückstoß stark reduzieren
                                livingEntity.setVelocity(knockback);  // Knockback anwenden
                            }

                            // Verhindere den Standard-Explosionsschaden, indem du ihn auf 0 setzt
                            livingEntity.setNoDamageTicks(0); // Damit wird sofortiger Schaden möglich, wenn der Standard-Schaden ignoriert wird.
                            livingEntity.damage(damage); // Benutzerdefinierten Schaden anwenden
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onBlockDrop(BlockDropItemEvent event) {
        event.setCancelled(true);
    }
}
