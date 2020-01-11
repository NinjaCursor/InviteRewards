package InviteRewards.CustomEvents;

import VertXCommons.Storage.PlayerData;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class LockedInEvent extends Event implements Cancellable {

    private boolean isCancelled;
    private static final HandlerList handlers = new HandlerList();

    private PlayerData inviterData, invitedData;

    public LockedInEvent(PlayerData inviterData, PlayerData invitedData) {
        this.inviterData = inviterData;
        this.invitedData = invitedData;
    }

    public PlayerData getInviterData() {
        return inviterData;
    }

    public PlayerData getInvitedData() {
        return invitedData;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return this.isCancelled;
    }

    @Override
    public void setCancelled(boolean arg0) {
        this.isCancelled = arg0;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }


}
