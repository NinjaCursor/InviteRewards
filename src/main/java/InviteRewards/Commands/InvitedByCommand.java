package InviteRewards.Commands;

import InviteRewards.Main.InviteRewards;
import InviteRewards.Main.VertXPlayer;
import InviteRewards.UsernameConverter.UsernameConverter;
import VertXCommons.Commands.AllowableUserType;
import VertXCommons.Commands.CommandAsset;
import VertXCommons.Storage.PlayerData;
import me.clip.placeholderapi.PlaceholderAPI;
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
            InviteRewards.runSync(new Runnable() {
                @Override
                public void run() {
                    VertXPlayer invitedPlayer = InviteRewards.getDataHandler().getPlayer(new PlayerData(player.getUniqueId(), player.getPlayerListName()));
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
