package InviteRewards.Listeners;

import InviteRewards.CustomEvents.AwardedEvent;
import InviteRewards.CustomEvents.LockedInEvent;
import InviteRewards.Main.InviteRewards;
import InviteRewards.Main.VertXPlayer;
import VertXCommons.Storage.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class LockedInListener implements Listener {

    @EventHandler
    public void onLockedIn(LockedInEvent event) {

        PlayerData invitedData = event.getInvitedData();
        PlayerData inviterData = event.getInviterData();

        VertXPlayer invitedPlayer = InviteRewards.getDataHandler().getPlayer(invitedData);

        if (invitedPlayer.isSatisfied())
            Bukkit.getPluginManager().callEvent(new AwardedEvent(inviterData, invitedData));

        if (invitedPlayer == null)
            InviteRewards.info("invitedPlayer is null");

        if (inviterData == null) {
            InviteRewards.info("inviterData is null");
        }


        invitedPlayer.msg("You have locked in " + InviteRewards.formatName(inviterData) + " to receive your invite reward");
        invitedPlayer.msg("This cannot be undone");
    }

}
