package InviteRewards.Listeners;

import InviteRewards.CustomEvents.AwardedEvent;
import InviteRewards.CustomEvents.MetRequirementsEvent;
import InviteRewards.Main.Main;
import InviteRewards.Main.VertXPlayer;
import InviteRewards.Storage.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class MetRequirementsListener implements Listener {

    @EventHandler
    public void onSatisfied(MetRequirementsEvent event) {

        Main.info("Satisfied Event");

        PlayerData invitedData = event.getInvitedData();
        PlayerData inviterData = event.getInviterData();

        VertXPlayer invitedPlayer = Main.getDataHandler().getPlayer(invitedData);

        invitedPlayer.msg("You have met the invite reward program requirements");

        if (invitedPlayer.isLocked())
            Bukkit.getPluginManager().callEvent(new AwardedEvent(inviterData, invitedData));
        else
            invitedPlayer.msg("Please lock in your invite selection for your inviter to receive an award");

    }

}
