package InviteRewards.Listeners;

import InviteRewards.CustomEvents.AwardedEvent;
import InviteRewards.CustomEvents.LockedInEvent;
import InviteRewards.Main.DualMessage;
import InviteRewards.Main.InviteRewards;
import InviteRewards.Main.VertXPlayer;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class LockedInListener extends DualMessage implements Listener {

    public LockedInListener() {
        super("locked-message");
    }

    @EventHandler
    public void onLockedIn(LockedInEvent event) {

        sendMessages(event.getEventPackage());

        VertXPlayer invitedPlayer = InviteRewards.getDataHandler().getPlayer(event.getEventPackage().getInvitedData());
        if (invitedPlayer.isSatisfied())
            Bukkit.getPluginManager().callEvent(new AwardedEvent(event.getEventPackage()));

        VertXPlayer inviter = InviteRewards.getDataHandler().getPlayer(event.getEventPackage().getInviterData());
        inviter.getMostRecent().setLocked(event.getEventPackage().getInvitedData());

    }

}
