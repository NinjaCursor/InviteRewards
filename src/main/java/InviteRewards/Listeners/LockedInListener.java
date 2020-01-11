package InviteRewards.Listeners;

import InviteRewards.CustomEvents.AwardedEvent;
import InviteRewards.CustomEvents.LockedInEvent;
import InviteRewards.Main.DualMessage;
import InviteRewards.Main.InviteRewards;
import InviteRewards.Main.VertXPlayer;
import VertXCommons.Storage.PlayerData;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class LockedInListener extends DualMessage implements Listener {

    public LockedInListener() {
        super("locked-message");
    }

    @EventHandler
    public void onLockedIn(LockedInEvent event) {

        PlayerData invitedData = event.getInvitedData();
        PlayerData inviterData = event.getInviterData();

        sendMessages(invitedData, inviterData);

        VertXPlayer invitedPlayer = InviteRewards.getDataHandler().getPlayer(invitedData);
        if (invitedPlayer.isSatisfied())
            Bukkit.getPluginManager().callEvent(new AwardedEvent(inviterData, invitedData));

    }

}
