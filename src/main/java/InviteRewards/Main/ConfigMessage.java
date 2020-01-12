package InviteRewards.Main;

import VertXCommons.Storage.PlayerData;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ConfigMessage {
    private List<String> messages;

    public ConfigMessage(String path) {
        messages = (ArrayList<String>) InviteRewards.getPlugin().getConfig().get(path);
    }

    public void sendMessage(PlayerData receiverData, PlayerData placeholderPlayer) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(placeholderPlayer.getUuid());
        for (String message : messages) {
            InviteRewards.msg(receiverData, PlaceholderAPI.setPlaceholders(player, message));
        }
    }

    public void sendMessage(PlayerData receiverData) {
        for (String message : messages) {
            InviteRewards.msg(receiverData, message);
        }
    }
}
