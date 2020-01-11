package InviteRewards.Listeners;

import InviteRewards.CustomEvents.AwardedEvent;
import InviteRewards.Main.InviteRewards;
import InviteRewards.Main.VertXPlayer;
import VertXCommons.Storage.PlayerData;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class AwardListener implements Listener {

    @EventHandler
    public void onAward(AwardedEvent event) {

        PlayerData invitedData = event.getInvitedData();
        PlayerData inviterData = event.getInviterData();

        VertXPlayer invitedPlayer = InviteRewards.getDataHandler().getPlayer(invitedData);
        VertXPlayer inviterPlayer = InviteRewards.getDataHandler().getPlayer(inviterData);

        invitedPlayer.msg(InviteRewards.formatName(inviterData) + " will now receive a reward!");

        inviterPlayer.msg(ChatColor.GREEN + "" + ChatColor.BOLD + "CONGRATULATIONS!");
        inviterPlayer.msg(ChatColor.AQUA + "You invited " + InviteRewards.formatName(invitedData) + " and you deserve a reward!");

    }

}
