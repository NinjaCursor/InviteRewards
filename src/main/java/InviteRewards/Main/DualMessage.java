package InviteRewards.Main;

import VertXCommons.Storage.PlayerData;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class DualMessage {

    private ConfigMessage inviterMessages, invitedMessages;

    public DualMessage(String path) {
        invitedMessages = new ConfigMessage(path + ".invited");
        inviterMessages = new ConfigMessage(path + ".inviter");
    }

    public void sendMessages(PlayerData invitedData, PlayerData inviterData) {
        inviterMessages.sendMessage(inviterData);
        invitedMessages.sendMessage(invitedData);
    }

}
