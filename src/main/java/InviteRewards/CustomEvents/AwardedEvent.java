package InviteRewards.CustomEvents;

import InviteRewards.Storage.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class AwardedEvent extends Event implements Cancellable {

    private boolean isCancelled;
    private PlayerData inviterData, invitedData;
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public PlayerData getInviterData() {
        return inviterData;
    }

    public PlayerData getInvitedData() {
        return invitedData;
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
    public AwardedEvent(PlayerData inviterData, PlayerData invitedData) {
        this.inviterData = inviterData;
        this.invitedData = invitedData;
    }

}
