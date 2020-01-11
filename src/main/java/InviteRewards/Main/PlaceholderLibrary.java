package InviteRewards.Main;

import VertXCommons.Storage.PlayerData;
import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.PlaceholderHook;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class PlaceholderLibrary {

    public PlaceholderLibrary() {
        registerPlaceholders();
    }

    public static PlayerData getPlayerData(Player player) {
        return new PlayerData(player.getUniqueId(), player.getName());
    }

    public String getYesNo(boolean trueOrFalse) {
        return (trueOrFalse ? "" + ChatColor.GREEN + "yes" : "" + ChatColor.RED + "no") + ChatColor.RESET;
    }

    private void registerPlaceholders() {
        PlaceholderAPI.registerPlaceholderHook(InviteRewards.getPlugin(), new PlaceholderHook() {
            @Override
            public String onPlaceholderRequest(Player player, String s) {
                VertXPlayer vertXPlayer = InviteRewards.getDataHandler().getPlayer(getPlayerData(player));
                switch (s) {
                    case "locked":
                        return getYesNo(vertXPlayer.isLocked());
                    case "satisfied":
                        return getYesNo(vertXPlayer.isSatisfied());
                    case "given":
                        return getYesNo(vertXPlayer.isGiven());
                    case "invited_count":
                        int count = vertXPlayer.getInvitedPlayers().size();
                        return (count > 0 ? ChatColor.RED + "" + count : ChatColor.GREEN + "" + count) + ChatColor.RESET;
                    case "brief_stats":
                        return PlaceholderAPI.setPlaceholders(player, "locked: %inviterewards_locked%, completed: %inviterewards_satisfied%, invited: %inviterewards_invited_count%");

                }
                return null;
            }
        });
    }

}
