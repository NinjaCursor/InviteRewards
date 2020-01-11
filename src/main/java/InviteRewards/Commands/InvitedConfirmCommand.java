package InviteRewards.Commands;

import InviteRewards.Main.Main;
import InviteRewards.Storage.PlayerData;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class InvitedConfirmCommand extends CommandAsset {
    public InvitedConfirmCommand(String commandName, String permission) {
        super(commandName, permission, AllowableUserType.PLAYER, 0);
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        Main.getDataHandler().getPlayer(new PlayerData(player.getUniqueId(), player.getDisplayName())).getCommander().setConfirmed();
        return true;
    }
}
