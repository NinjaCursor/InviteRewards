package InviteRewards.Commands;

import InviteRewards.Main.CommandUtility;
import InviteRewards.Main.Main;
import InviteRewards.Main.VertXPlayer;
import InviteRewards.Storage.PlayerData;
import InviteRewards.UsernameConverter.UsernameConverter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class InvitedByCommand extends CommandAsset {

    public InvitedByCommand(String commandName, String permission) {
        super(commandName, permission, AllowableUserType.PLAYER, 1);
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        String inviterUsername = args[0];
        UsernameConverter.getPlayerData(inviterUsername).thenAccept((inviterPlayerData) -> {
            Main.runSync(new Runnable() {
                @Override
                public void run() {
                    VertXPlayer invitedPlayer = Main.getDataHandler().getPlayer(new PlayerData(player.getUniqueId(), player.getPlayerListName()));
                    if (inviterPlayerData == null) {
                        invitedPlayer.error("Could not find player \"" + inviterUsername + "\"");
                        return;
                    }

                    try {
                        invitedPlayer.getCommander().setInviter(inviterPlayerData);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            return;
        });
        return true;
    }
}
