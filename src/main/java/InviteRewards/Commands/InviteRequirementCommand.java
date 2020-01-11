package InviteRewards.Commands;

import InviteRewards.Main.Main;
import InviteRewards.Main.VertXPlayer;
import InviteRewards.Storage.DatabaseFunctions;
import InviteRewards.Storage.PlayerData;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class InviteRequirementCommand extends CommandAsset {

    public InviteRequirementCommand(String commandName, String permission) {
        super(commandName, permission, AllowableUserType.PLAYER, 0);
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        VertXPlayer vertXPlayer = Main.getDataHandler().getPlayer(new PlayerData(player.getUniqueId(), player.getDisplayName()));
        vertXPlayer.showRequirements();
        return true;
    }
}
