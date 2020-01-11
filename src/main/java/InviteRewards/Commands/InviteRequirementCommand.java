package InviteRewards.Commands;

import InviteRewards.Main.InviteRewards;
import InviteRewards.Main.VertXPlayer;
import VertXCommons.Storage.PlayerData;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class InviteRequirementCommand extends CommandAsset {

    public InviteRequirementCommand(String commandName, String permission) {
        super(commandName, permission, AllowableUserType.PLAYER, 0);
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        VertXPlayer vertXPlayer = InviteRewards.getDataHandler().getPlayer(new PlayerData(player.getUniqueId(), player.getPlayerListName()));
        vertXPlayer.showRequirements();
        return true;
    }
}
