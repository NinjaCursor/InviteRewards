package InviteRewards.Listeners;

import InviteRewards.Commands.InvitedConfirmCommand;
import InviteRewards.CustomEvents.AwardedEvent;
import InviteRewards.CustomEvents.MetRequirementsEvent;
import InviteRewards.Main.DualMessage;
import InviteRewards.Main.InviteRewards;
import InviteRewards.Main.VertXPlayer;
import VertXCommons.Storage.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

public class MetRequirementsListener extends DualMessage implements Listener {

    public MetRequirementsListener() {
        super("completed-message");
    }

    @EventHandler
    public void onSatisfied(MetRequirementsEvent event) {

        PlayerData invitedData = event.getInvitedData();
        PlayerData inviterData = event.getInviterData();

        sendMessages(invitedData, inviterData);

        VertXPlayer invitedPlayer = InviteRewards.getDataHandler().getPlayer(invitedData);

        if (invitedPlayer.isLocked())
            Bukkit.getPluginManager().callEvent(new AwardedEvent(inviterData, invitedData));
        else {
            invitedPlayer.error("You still need to select the player who invited you!");
        }


    }

}
