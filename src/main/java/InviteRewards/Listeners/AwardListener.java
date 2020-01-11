package InviteRewards.Listeners;

import InviteRewards.CustomEvents.AwardedEvent;
import InviteRewards.Main.Main;
import InviteRewards.Main.VertXPlayer;
import InviteRewards.Storage.PlayerData;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class AwardListener implements Listener {

    @EventHandler
    public void onAward(AwardedEvent event) {

        PlayerData invitedData = event.getInvitedData();
        PlayerData inviterData = event.getInviterData();

        VertXPlayer invitedPlayer = Main.getDataHandler().getPlayer(invitedData);
        VertXPlayer inviterPlayer = Main.getDataHandler().getPlayer(inviterData);

        invitedPlayer.msg(Main.formatName(inviterData) + " will receive an award");

        inviterPlayer.msg(Main.formatName(invitedData) + " completed the invite reward program");

    }

}
