package InviteRewards.Listeners;

import InviteRewards.CustomEvents.InviteSelectionEvent;
import InviteRewards.Main.DualMessage;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class InviteSelectionListener extends DualMessage implements Listener {

    public InviteSelectionListener() {
        super("selected-message");
    }

    @EventHandler
    public void onInviteSelection(InviteSelectionEvent event) {
        sendMessages(event.getEventPackage());
    }

}
