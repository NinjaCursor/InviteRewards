package InviteRewards.Listeners;

import InviteRewards.CustomEvents.AwardedEvent;
import InviteRewards.CustomEvents.MetRequirementsEvent;
import InviteRewards.Main.InviteRewards;
import InviteRewards.Main.VertXPlayer;
import VertXCommons.Storage.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class MetRequirementsListener implements Listener {

    @EventHandler
    public void onSatisfied(MetRequirementsEvent event) {

        PlayerData invitedData = event.getInvitedData();
        PlayerData inviterData = event.getInviterData();

        VertXPlayer invitedPlayer = InviteRewards.getDataHandler().getPlayer(invitedData);

        invitedPlayer.msg("" + ChatColor.RESET + ChatColor.GREEN + ChatColor.BOLD + "CONGRATULATIONS!");
        invitedPlayer.msg("" + ChatColor.AQUA + "You completed the Invite Rewards Program time requirements!");

        if (invitedPlayer.isLocked())
            Bukkit.getPluginManager().callEvent(new AwardedEvent(inviterData, invitedData));
        else {
            invitedPlayer.error("You still need to select the player who invited you!");
        }


    }

}
