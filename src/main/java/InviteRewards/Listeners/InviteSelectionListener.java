package InviteRewards.Listeners;

import InviteRewards.CustomEvents.InviteSelectionEvent;
import InviteRewards.Main.DualMessage;
import InviteRewards.Main.InviteRewards;
import InviteRewards.Main.VertXPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class InviteSelectionListener extends DualMessage implements Listener {

    public InviteSelectionListener() {
        super("selected-message");
    }

    @EventHandler
    public void onInviteSelection(InviteSelectionEvent event) {
        sendMessages(event.getEventPackage());

        VertXPlayer inviter = InviteRewards.getDataHandler().getPlayer(event.getEventPackage().getInviterData());
        inviter.getMostRecent().setSelected(event.getEventPackage().getInvitedData());
    }

}
