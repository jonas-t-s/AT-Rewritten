package io.github.niestrat99.advancedteleport.commands.teleport;

import io.github.niestrat99.advancedteleport.CoreClass;
import io.github.niestrat99.advancedteleport.commands.ATCommand;
import io.github.niestrat99.advancedteleport.config.CustomMessages;
import io.github.niestrat99.advancedteleport.config.NewConfig;
import io.github.niestrat99.advancedteleport.managers.CooldownManager;
import io.github.niestrat99.advancedteleport.managers.MovementManager;
import io.github.niestrat99.advancedteleport.utilities.ConditionChecker;
import io.github.niestrat99.advancedteleport.payments.PaymentManager;
import io.github.niestrat99.advancedteleport.utilities.TPRequest;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class TpaHere implements ATCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (sender instanceof Player){
            if (NewConfig.getInstance().USE_BASIC_TELEPORT_FEATURES.get()) {
                if (sender.hasPermission("at.member.here")) {
                    Player player = (Player) sender;
                    UUID playerUuid = player.getUniqueId();
                    int cooldown = CooldownManager.secondsLeftOnCooldown("tpahere", player);
                    if (cooldown > 0) {
                        sender.sendMessage(CustomMessages.getString("Error.onCooldown").replaceAll("\\{time}", String.valueOf(cooldown)));
                        return true;
                    }
                    if (MovementManager.getMovement().containsKey(playerUuid)) {
                        player.sendMessage(CustomMessages.getString("Error.onCountdown"));
                        return true;
                    }
                    if (args.length > 0) {
                        Player target = Bukkit.getPlayer(args[0]);
                        String result = ConditionChecker.canTeleport(player, target, "tpahere");
                        if (result.isEmpty()) {
                            if (PaymentManager.getInstance().canPay("tpahere", player)) {
                                int requestLifetime = NewConfig.getInstance().REQUEST_LIFETIME.get();
                                sender.sendMessage(CustomMessages.getString("Info.requestSent")
                                        .replaceAll("\\{player}", target.getName())
                                        .replaceAll("\\{lifetime}", String.valueOf(requestLifetime)));

                                CoreClass.playSound("tpahere", "sent", player);

                                target.sendMessage(CustomMessages.getString("Info.tpaRequestHere")
                                        .replaceAll("\\{player}", sender.getName())
                                        .replaceAll("\\{lifetime}", String.valueOf(requestLifetime)));

                                CoreClass.playSound("tpahere", "received", target);

                                BukkitRunnable run = new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        sender.sendMessage(CustomMessages.getString("Error.requestExpired").replaceAll("\\{player}", target.getName()));
                                        TPRequest.removeRequest(TPRequest.getRequestByReqAndResponder(target, player));
                                    }
                                };
                                run.runTaskLater(CoreClass.getInstance(), requestLifetime * 20); // 60 seconds
                                TPRequest request = new TPRequest(player, target, run, TPRequest.TeleportType.TPAHERE); // Creates a new teleport request.
                                TPRequest.addRequest(request);
                                // If the cooldown is to be applied after request or accept (they are the same in the case of /spawn), apply it now
                                if(NewConfig.getInstance().APPLY_COOLDOWN_AFTER.get().equalsIgnoreCase("request")) {
                                    CooldownManager.addToCooldown("tpahere", player);
                                }
                                return true;
                            }
                        } else {
                            sender.sendMessage(result);
                            return true;
                        }
                    } else {
                        sender.sendMessage(CustomMessages.getString("Error.noPlayerInput"));
                        return true;
                    }

                }
            }
        } else {
        sender.sendMessage(CustomMessages.getString("Error.notAPlayer"));
    }
        return true;
    }
}
