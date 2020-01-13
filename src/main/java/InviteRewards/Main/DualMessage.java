package InviteRewards.Main;

import InviteRewards.CustomEvents.EventPackage;

public class DualMessage {

    private ConfigMessage inviterMessages, invitedMessages;

    public DualMessage(String path) {
        invitedMessages = new ConfigMessage(path + ".invited");
        inviterMessages = new ConfigMessage(path + ".inviter");
    }

    public void sendMessages(EventPackage eventPackage) {
        invitedMessages.sendMessage(eventPackage.getInvitedData());
        inviterMessages.sendMessage(eventPackage.getInviterData());
    }

}
