package InviteRewards.CustomEvents;

import VertXCommons.Storage.PlayerData;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class AwardedEvent extends Event implements Cancellable {

    private boolean isCancelled;
    private EventPackage eventPackage;
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public EventPackage getEventPackage() {
        return this.eventPackage;
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

    public AwardedEvent(EventPackage eventPackage) {
        this.eventPackage = eventPackage;
    }

}
