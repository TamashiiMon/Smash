package de.terrocraft.smesh.managers;

import de.terrocraft.smesh.Smash;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class WorldManager {
    public static final String smashWorldName = "Smash-world";
    public static List<Player> playersineditmode = new CopyOnWriteArrayList<>();
    public static boolean iseditmodeactive = false;
    public static String mapnameofworldineditmode = null;
    private final File worldsFolder;
    private final File serverWorldFolder;

    public WorldManager(File pluginFolder) {
        this.worldsFolder = new File(pluginFolder, "worlds");
        this.serverWorldFolder = Bukkit.getWorldContainer();
    }

    public void editmapmode(Player player, String mapName) {
        if (!player.hasPermission("smesh.editmapmode")) {
            player.sendMessage(new ChatManager(Smash.getInstance()).permission);
            return;
        }
        if (playersineditmode.contains(player)) {
            player.sendMessage(new ChatManager(Smash.getInstance()).prefix + "Du bist bereits im Edit Mode.");
            return;
        }
        if (!doesMapExist(mapName)) {
            player.sendMessage(ChatColor.RED + "World '" + mapName + "' does not exist.");
            return;
        }

        if (!loadMap(mapName)) {
            player.sendMessage(ChatColor.RED + "Failed to load the map '" + mapName + "'.");
            return;
        }

        player.sendMessage(ChatColor.GREEN + "The map '" + mapName + "' is loading. You will be teleported in 5 seconds.");

        new BukkitRunnable() {
            @Override
            public void run() {
                World world = Bukkit.getWorld(smashWorldName);
                if (world == null) {
                    player.sendMessage(ChatColor.RED + "The world '" + smashWorldName + "' could not be found.");
                    return;
                }
                Location spawnLocation = new Location(world, 0, 85, 0);
                player.setAllowFlight(true);
                player.setFlying(true);
                player.setGameMode(GameMode.CREATIVE);
                player.teleport(spawnLocation);

                playersineditmode.add(player);
                iseditmodeactive = true;
                mapnameofworldineditmode = mapName;
                player.sendMessage(ChatColor.GREEN + "You have been teleported to the map '" + mapName + "'.");
            }
        }.runTaskLater(Smash.getInstance(), 100L); // 100L = 5 Sekunden
    }

    public void saveMap(Player player) {
        if (!player.hasPermission("smesh.editmapmode")) {
            player.sendMessage(new ChatManager(Smash.getInstance()).permission);
            return;
        }
        if (!playersineditmode.contains(player)) {
            player.sendMessage(new ChatManager(Smash.getInstance()).prefix + "Du bist nicht der Spieler im Edit Mode.");
            return;
        }
        if (!doesMapExist(mapnameofworldineditmode)) {
            player.sendMessage(ChatColor.RED + "World '" + mapnameofworldineditmode + "' does not exist! ERROR");
            return;
        }

        File targetWorldFolder = new File(worldsFolder, mapnameofworldineditmode);
        File sourceWorldFolder = new File(serverWorldFolder, smashWorldName);

        try {
            copyWorld(sourceWorldFolder.toPath(), targetWorldFolder.toPath());
        } catch (IOException e) {
            player.sendMessage(ChatColor.RED + "Fehler beim Speichern der Welt: " + e.getMessage());
            return;
        }

        Location loc = new Location(Bukkit.getWorld("world"), 0, 101, 0);
        player.teleport(loc);
        player.setGameMode(GameMode.ADVENTURE);
        player.setAllowFlight(false);

        deleteWorld();
        iseditmodeactive = false;
        mapnameofworldineditmode = null;
        playersineditmode.remove(player);
        player.sendMessage(ChatColor.GREEN + "Die Welt wurde erfolgreich gespeichert.");
    }

    public boolean loadMap(String mapName) {
        File sourceWorldFolder = new File(worldsFolder, mapName);
        File targetWorldFolder = new File(serverWorldFolder, smashWorldName);

        if (!sourceWorldFolder.exists() || !sourceWorldFolder.isDirectory()) {
            Bukkit.getLogger().severe("Die Map '" + mapName + "' existiert nicht im Ordner " + worldsFolder.getPath());
            return false;
        }

        if (targetWorldFolder.exists()) {
            deleteWorld();
        }

        try {
            copyWorld(sourceWorldFolder.toPath(), targetWorldFolder.toPath());
        } catch (IOException e) {
            Bukkit.getLogger().severe("Fehler beim Kopieren der Map: " + e.getMessage());
            return false;
        }

        if (isuidExist(mapName)) {
            deleteuid(mapName);
        }

        World world = Bukkit.createWorld(new WorldCreator(smashWorldName).generator("VoidGen"));
        if (world == null) {
            Bukkit.getLogger().severe("Fehler beim Laden der Welt '" + smashWorldName + "'.");
            return false;
        }

        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
        world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
        world.setGameRule(GameRule.DO_ENTITY_DROPS, false);
        world.setGameRule(GameRule.TNT_EXPLOSION_DROP_DECAY, false);
        world.setGameRule(GameRule.DO_FIRE_TICK, false);
        world.setGameRule(GameRule.DO_MOB_SPAWNING, false);
        return true;
    }

    public boolean doesMapExist(String mapName) {
        File mapFolder = new File(worldsFolder, mapName);
        return mapFolder.exists() && mapFolder.isDirectory();
    }

    public boolean isuidExist(String mapName) {
        File mapFolder = new File(worldsFolder, mapName);
        File uid = new File(mapFolder, "uid.dat" );
        return  uid.isFile() && uid.exists();
    }

    public List<String> getMapNames() {
        if (!worldsFolder.exists() || !worldsFolder.isDirectory()) {
            return Collections.emptyList();
        }

        File[] files = worldsFolder.listFiles();
        if (files == null) {
            return Collections.emptyList();
        }

        List<String> folderNames = new ArrayList<>();
        for (File file : files) {
            if (file.isDirectory()) {
                folderNames.add(file.getName());
            }
        }
        return folderNames;
    }

    public boolean deleteWorld() {
        File targetWorldFolder = new File(serverWorldFolder, smashWorldName);
        World world = Bukkit.getWorld(smashWorldName);

        if (world != null) {
            Bukkit.unloadWorld(world, false);
        }

        try {
            deleteFolder(targetWorldFolder);
            return true;
        } catch (IOException e) {
            Bukkit.getLogger().severe("Fehler beim Löschen der Welt: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteuid(String mapName) {
        File mapFolder = new File(worldsFolder, mapName);
        File uid = new File(mapFolder, "uid.dat" );

        try {
            deleteFolder(uid);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public void copyWorld(Path source, Path target) throws IOException {
        Files.walk(source).forEach(path -> {
            Path relativePath = source.relativize(path);
            Path targetPath = target.resolve(relativePath);

            try {
                if (Files.isDirectory(path)) {
                    if (!Files.exists(targetPath)) {
                        Files.createDirectories(targetPath);
                    }
                } else {
                    // Falls die Datei bereits existiert, wird sie überschrieben
                    Files.copy(path, targetPath, StandardCopyOption.REPLACE_EXISTING);
                }
            } catch (IOException e) {
                throw new RuntimeException("Fehler beim Kopieren der Datei: " + path.toString(), e);
            }
        });
    }

    private void deleteFolder(File folder) throws IOException {
        if (!folder.exists()) return;

        Files.walk(folder.toPath())
                .sorted((a, b) -> b.compareTo(a))
                .forEach(path -> {
                    try {
                        Files.delete(path);
                    } catch (IOException e) {
                        Bukkit.getLogger().severe("Fehler beim Löschen der Datei: " + path.toString());
                        throw new RuntimeException(e);
                    }
                });
    }
}
