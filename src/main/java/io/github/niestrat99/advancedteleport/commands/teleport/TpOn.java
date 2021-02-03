package io.github.niestrat99.advancedteleport.commands.teleport;

import io.github.niestrat99.advancedteleport.commands.ATCommand;
import io.github.niestrat99.advancedteleport.config.CustomMessages;
import io.github.niestrat99.advancedteleport.config.NewConfig;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class TpOn implements ATCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player)sender;
            UUID uuid = player.getUniqueId();
            if (NewConfig.getInstance().USE_BASIC_TELEPORT_FEATURES.get()) {
                if (sender.hasPermission("at.member.on")) {
                    if (TpOff.getTpOff().contains(uuid)) {
                        TpOff.getTpOff().remove(uuid);
                        sender.sendMessage(CustomMessages.getString("Info.tpOn"));
                    } else {
                        sender.sendMessage(CustomMessages.getString("Error.alreadyOn"));
                    }
                }
            } else {
                sender.sendMessage(CustomMessages.getString("Error.featureDisabled"));
            }
        } else {
            sender.sendMessage(CustomMessages.getString("Error.notAPlayer"));
        }
        return true;
    }
}
