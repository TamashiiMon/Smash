package de.terrocraft.smesh.managers;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class ItemSpawnerManager {
    private static List<Location> map3Locations = new ArrayList<>();
    private static List<ItemStack> map3ItemStacks = new ArrayList<>();

    private static int ticks = 0;
    public static void ItemSpawnerManagerUpdater(){
        ticks++;
        if(ticks > 100) {
            ticks = 0;
            spawnItemInRandomLocation();
            Bukkit.broadcastMessage("Ein item ist gespawnt...");
        }
    }

    private static void spawnItemInRandomLocation(){
        if (WorldManager.smashWorldName.equals("map3")){
        if (!map3Locations.isEmpty()) {
            Random random = new Random();

            int randomIndex = random.nextInt(map3Locations.size());

            Location randomLocation = map3Locations.get(randomIndex);

            ItemStack itemToSpawn = map3ItemStacks.get(randomIndex);

            randomLocation.getWorld().dropItem(randomLocation, itemToSpawn);


            }
        }
    }
}
