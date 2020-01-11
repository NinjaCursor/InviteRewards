package InviteRewards.Commands;

import InviteRewards.Main.InviteRewards;
import InviteRewards.UsernameConverter.UsernameConverter;
import VertXCommons.Storage.PlayerData;
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
                InviteRewards.runSync(new Runnable() {
                    @Override
                    public void run() {
                        sender.sendMessage("Could not find given player");
                    }
                });
                return;
            }

            String[] playerInviteData = InviteRewards.getDataHandler().getPlayer(playerData).getStats();
            InviteRewards.runSync(new Runnable() {
                @Override
                public void run() {
                    Player player = (Player) sender;
                    PlayerData viewer = new PlayerData(player.getUniqueId(), player.getPlayerListName());
                    for (String message : playerInviteData) {
                        InviteRewards.msg(viewer, message);
                    }

                }
            });
        });

        return true;
    }
}
