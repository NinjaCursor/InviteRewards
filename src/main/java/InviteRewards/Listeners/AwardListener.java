package InviteRewards.Listeners;

import InviteRewards.CustomEvents.AwardedEvent;
import InviteRewards.Main.DualMessage;
import InviteRewards.Main.InviteRewards;
import InviteRewards.Main.VertXPlayer;
import VertXCommons.Storage.PlayerData;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class AwardListener extends DualMessage implements Listener {

    public AwardListener() {
        super("award-message");
    }

    @EventHandler
    public void onAward(AwardedEvent event) {
        sendMessages(event.getEventPackage());

        //set placeholder data
        VertXPlayer inviter = InviteRewards.getDataHandler().getPlayer(event.getEventPackage().getInviterData());
        inviter.getMostRecent().setAward(event.getEventPackage().getInvitedData());
    }

}
