package InviteRewards.Main;

import VertXCommons.Storage.PlayerData;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ChatHandler {

    private String defaultColor;

    public ChatHandler(String defaultColor) {
        this.defaultColor = defaultColor;
    }

    public void msg(PlayerData playerData, String message) {

        for(Player p : Bukkit.getServer().getOnlinePlayers()) {

            if(p.getUniqueId().equals(playerData.getUuid())) {
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', defaultColor+PlaceholderAPI.setPlaceholders(p, message.replace("&r", defaultColor))));
            }

        }

    }

    public String getDefaultColor() {
        return defaultColor;
    }

    public void messageError(PlayerData playerData, String message) {
        msg(playerData, "" + ChatColor.RED + message);
    }

}
