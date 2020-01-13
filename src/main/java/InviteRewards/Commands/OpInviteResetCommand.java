package InviteRewards.Commands;

import InviteRewards.Main.InviteRewards;
import InviteRewards.UsernameConverter.UsernameConverter;
import VertXCommons.Commands.AllowableUserType;
import VertXCommons.Commands.CommandAsset;
import org.bukkit.command.CommandSender;

public class OpInviteResetCommand extends CommandAsset {
    public OpInviteResetCommand(String commandName, String permission) {
        super(commandName, permission, AllowableUserType.ANY, 1);
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        String playerName = args[0];
        UsernameConverter.getPlayerData(playerName).thenAccept((playerData) -> {
            if (playerData != null) {
                InviteRewards.getDataHandler().forceReset(playerData).thenAccept((success) -> {
                   InviteRewards.runSync(new Runnable() {
                       @Override
                       public void run() {
                           if (success)
                               sender.sendMessage("The specified player has been reset in the database and in game");
                           else
                               sender.sendMessage("A database issue caused the player to not be reset. No changes were made");
                       }
                   });
                });
            } else {
                sender.sendMessage("The specified player could not be found");
            }
        });
        return true;
    }
}
