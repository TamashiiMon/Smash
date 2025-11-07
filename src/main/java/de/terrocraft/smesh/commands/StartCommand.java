package de.terrocraft.smesh.commands;

import de.terrocraft.smesh.Smash;
import de.terrocraft.smesh.countdowns.PreGameTimer;
import de.terrocraft.smesh.enums.Gamestates;
import de.terrocraft.smesh.services.ChatService;
import de.terrocraft.smesh.services.WorldService;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;

public class StartCommand implements CommandExecutor, TabCompleter {

    private final Smash smash;

    public StartCommand(Smash main) {
        this.smash = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        Player p = (Player) sender;

        if (!p.hasPermission("smash.start")) {
            p.sendMessage(new ChatService(smash).permission);
            return true;
        }
        if (cmd.getName().equalsIgnoreCase("start")) {
            if (args.length == 0) {
                p.sendMessage(new ChatService(smash).prefix + "Usage: /start <world-name>");
                return true;
            }
            if (WorldService.isEditModeActive) {
                p.sendMessage(new ChatService(smash).prefix + "Der edit mode ist aktiviert, du kanst kein spiel starten.");
                return false;
            }
            if (args.length == 1) {
                if (args[0].equals("random")) {
                    Random rand = new Random();
                    WorldService worldService = new WorldService(Smash.getInstance().getDataFolder());
                    String randomMapName = worldService.getMapNames().get(rand.nextInt(worldService.getMapNames().size()));
                    if (!new WorldService(Smash.getInstance().getDataFolder()).doesMapExist(randomMapName)) {
                        p.sendMessage(ChatColor.DARK_PURPLE + "Fehler! Random world system broken!");
                        return false;
                    }
                    if (!(Bukkit.getOnlinePlayers().size() > 1)) {
                        p.sendMessage(new ChatService(smash).prefix + ChatService.hex("#df3f2dEs müssen mindestens #e06153§l2 #df3f2dpersonen online sein!"));
                        return false;
                    }
                    new PreGameTimer(smash, randomMapName).startCountdown();
                    return true;
                }
                if (!new WorldService(Smash.getInstance().getDataFolder()).doesMapExist(args[0])) {
                    p.sendMessage(ChatColor.DARK_PURPLE + "World '" + args[0] + "' does not exist.");
                    return false;
                }

                if (Smash.getInstance().getGamestate().equals(Gamestates.INGAME) || Smash.getInstance().getGamestate().equals(Gamestates.PREGAME) || Smash.getInstance().getGamestate().equals(Gamestates.ENDGAME) || Smash.getInstance().getGamestate().equals(Gamestates.Countdown)) {
                    p.sendMessage(new ChatService(smash).prefix + ChatService.hex("#df3f2dEin spiel ist noch am laufen!"));
                    return false;
                }
                if (!(Bukkit.getOnlinePlayers().size() > 1)) {
                    p.sendMessage(new ChatService(smash).prefix + ChatService.hex("#df3f2dEs müssen mindestens #e061532 #df3f2dpersonen online sein!"));
                    return true;
                }
                new PreGameTimer(smash, args[0]).startCountdown();
                p.sendMessage(new ChatService(smash).prefix + ChatService.hex("#df3f2dYou have started the game."));
                return true;
            }
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(CommandSender sender, Command cmd, String s, String[] args) {
        if (args.length == 1) {
            WorldService worldService = new WorldService(Smash.getInstance().getDataFolder());
            List<String> mapnames = worldService.getMapNames();
            mapnames.addFirst("random");
            return mapnames;
        }

        return List.of();
    }
}