package InviteRewards.Main;

import VertXCommons.Storage.PlayerData;

import java.util.ArrayList;
import java.util.List;

public class ConfigMessage {
    private List<String> messages;

    public ConfigMessage(String path) {
        messages = (ArrayList<String>) InviteRewards.getPlugin().getConfig().get(path);
    }

    public void sendMessage(PlayerData playerData) {
        for (String message : messages) {
            InviteRewards.msg(playerData, message);
        }
    }
}
