package InviteRewards.Commands;

import InviteRewards.Main.InviteRewards;
import VertXCommons.Storage.PlayerData;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class InvitedConfirmCommand extends CommandAsset {
    public InvitedConfirmCommand(String commandName, String permission) {
        super(commandName, permission, AllowableUserType.PLAYER, 0);
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        InviteRewards.getDataHandler().getPlayer(new PlayerData(player.getUniqueId(), player.getPlayerListName())).getCommander().setConfirmed();
        return true;
    }
}
