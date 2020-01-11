package InviteRewards.Commands;

import InviteRewards.Main.Main;
import InviteRewards.Storage.PlayerData;
import InviteRewards.UsernameConverter.UsernameConverter;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class InviteStatsCommand extends CommandAsset {

    public InviteStatsCommand(String commandName, String permission) {
        super(commandName, permission, AllowableUserType.PLAYER, 1);
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        String playerName = args[0];
        UsernameConverter.getPlayerData(playerName).thenAccept((playerData) -> {

            if(playerData == null) {
                Main.runSync(new Runnable() {
                    @Override
                    public void run() {
                        sender.sendMessage("Could not find given player");
                    }
                });
                return;
            }

            String[] playerInviteData = Main.getDataHandler().getPlayer(playerData).getStats();
            Main.runSync(new Runnable() {
                @Override
                public void run() {
                    Player player = (Player) sender;
                    PlayerData viewer = new PlayerData(player.getUniqueId(), player.getDisplayName());
                    for (String message : playerInviteData) {
                        Main.msg(viewer, message);
                    }

                }
            });
        });

        return true;
    }
}
