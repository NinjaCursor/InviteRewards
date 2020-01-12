package InviteRewards.CustomEvents;

import VertXCommons.Storage.PlayerData;

public class EventPackage {

    private PlayerData inviterData, invitedData;

    public EventPackage(PlayerData inviterData, PlayerData invitedData) {
        this.inviterData = inviterData;
        this.invitedData = invitedData;
    }

    public PlayerData getInviterData() {
        return inviterData;
    }

    public PlayerData getInvitedData() {
        return invitedData;
    }
}
