package InviteRewards.Commands;

import InviteRewards.Main.ConfigMessage;
import InviteRewards.Main.InviteRewards;
import InviteRewards.Main.VertXPlayer;
import InviteRewards.UsernameConverter.UsernameConverter;
import VertXCommons.Storage.PlayerData;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.Set;

public class InviteStatsCommand extends CommandAsset {

    private ConfigMessage statsMessage, individualStatsMessage;

    public InviteStatsCommand(String commandName, String permission) {
        super(commandName, permission, AllowableUserType.PLAYER, 1);
        statsMessage = new ConfigMessage("invite-stats");
        individualStatsMessage = new ConfigMessage("invite-list-structure");
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

            InviteRewards.runSync(new Runnable() {
                @Override
                public void run() {
                    Player player = (Player) sender;
                    PlayerData viewer = new PlayerData(player.getUniqueId(), player.getPlayerListName());

                    VertXPlayer vertXPlayer = InviteRewards.getDataHandler().getPlayer(viewer);
                    Set<VertXPlayer> invitedPlayers = vertXPlayer.getInvitedPlayers();

                    statsMessage.sendMessage(viewer, playerData);

                    for (VertXPlayer invited : invitedPlayers) {
                        individualStatsMessage.sendMessage(viewer, invited.getSelfPlayer());
                    }
                }
            });
        });

        return true;
    }
}
