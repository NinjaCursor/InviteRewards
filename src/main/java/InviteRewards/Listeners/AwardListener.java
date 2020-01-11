package InviteRewards.Listeners;

import InviteRewards.CustomEvents.AwardedEvent;
import InviteRewards.Main.InviteRewards;
import InviteRewards.Main.VertXPlayer;
import VertXCommons.Storage.PlayerData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class AwardListener implements Listener {

    @EventHandler
    public void onAward(AwardedEvent event) {

        PlayerData invitedData = event.getInvitedData();
        PlayerData inviterData = event.getInviterData();

        VertXPlayer invitedPlayer = InviteRewards.getDataHandler().getPlayer(invitedData);
        VertXPlayer inviterPlayer = InviteRewards.getDataHandler().getPlayer(inviterData);

        invitedPlayer.msg(InviteRewards.formatName(inviterData) + " will receive an award");

        inviterPlayer.msg("You invited " + InviteRewards.formatName(invitedData) + " and you deserve an award!");

    }

}
