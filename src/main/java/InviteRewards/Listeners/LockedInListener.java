package InviteRewards.Listeners;

import InviteRewards.CustomEvents.AwardedEvent;
import InviteRewards.CustomEvents.LockedInEvent;
import InviteRewards.Main.Main;
import InviteRewards.Main.VertXPlayer;
import InviteRewards.Storage.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class LockedInListener implements Listener {

    @EventHandler
    public void onLockedIn(LockedInEvent event) {
        Bukkit.getLogger().info("LockedIn Event");

        PlayerData invitedData = event.getInvitedData();
        PlayerData inviterData = event.getInviterData();

        VertXPlayer invitedPlayer = Main.getDataHandler().getPlayer(invitedData);

        if (invitedPlayer.isSatisfied())
            Bukkit.getPluginManager().callEvent(new AwardedEvent(inviterData, invitedData));

        if (invitedPlayer == null)
            Main.info("invitedPlayer is null");

        if (inviterData == null) {
            Main.info("inviterData is null");
        }


        invitedPlayer.msg("You have locked in " + Main.formatName(inviterData) + " to receive your invite reward");
        invitedPlayer.msg("This cannot be undone");
    }

}
