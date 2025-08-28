package de.terrocraft.smesh.commands;

import de.terrocraft.smesh.Gamestates;
import de.terrocraft.smesh.Smash;
import de.terrocraft.smesh.managers.ChatManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class SmashStopCommand implements CommandExecutor {
    private final Smash smash;

    public SmashStopCommand(Smash smash) {
        this.smash = smash;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender Sender, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {
        if (smash.getGamestate().equals(Gamestates.LOBBY) || smash.getGamestate().equals(Gamestates.ENDGAME)) {
            Sender.sendMessage(new ChatManager(smash).prefix + ChatManager.hex("#df3f2dDas spiel leuft nicht oder wird gerade schon beended."));
            return true;
        }
        smash.setGamestate(Gamestates.ENDGAME);
        return false;
    }
}
