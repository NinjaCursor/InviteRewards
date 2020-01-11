package InviteRewards.Main;

import InviteRewards.Commands.InviteStatsCommand;
import VertXCommons.Storage.PlayerData;
import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.PlaceholderHook;
import me.clip.placeholderapi.external.EZPlaceholderHook;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class PlaceholderLibrary extends EZPlaceholderHook {

    public PlaceholderLibrary() {
        super(InviteRewards.getPlugin(), "invplugin");
    }

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
                return (count < 1 ? ChatColor.RED + "" + count : ChatColor.GREEN + "" + count) + ChatColor.RESET;
            case "brief_stats":
                return PlaceholderAPI.setPlaceholders(player, "locked: %invplugin_locked%, completed: %invplugin_satisfied%, invited: %invplugin _invited_count%");
            case "self":
                return vertXPlayer.getSelfPlayer().getName();
            case "inviter":
                if (vertXPlayer.getInviterPlayer() != null) {
                    return vertXPlayer.getInviterPlayer().getName();
                }
                return "none selected";
            case "progress":
                if (vertXPlayer.isSatisfied()) {
                    return ChatColor.GREEN + "completed" + ChatColor.RESET;
                }
                return vertXPlayer.getProgress() + " / " + InviteRewards.minTotalTime;
            case "invited_count":
                return getCount(vertXPlayer.getInvitedPlayers().size());
            case "invite_selection_command":
                return InviteRewards.getInvitedByCommandName();
            case "invite_confirm_command":
                return InviteRewards.getInvitedConfirmCommandName();
            case "invite_stats_command":
                return InviteRewards.getInviteStatsCommandName();
            case "time_left":
                if (vertXPlayer.isSatisfied())
                    return "0";
                else
                    return vertXPlayer.timeLeft() + "";
            case "time_required":
                return InviteRewards.minTotalTime + "m";


        }
        return null;
    }

    public static PlayerData getPlayerData(Player player) {
        return new PlayerData(player.getUniqueId(), player.getName());
    }

    public String getCount(int count) {
        return ((count > 0) ? "" + ChatColor.GREEN + count : "" + ChatColor.RED + count) + ChatColor.RESET;
    }

    public String getYesNo(boolean trueOrFalse) {
        return (trueOrFalse ? "" + ChatColor.GREEN + "yes" : "" + ChatColor.RED + "no") + ChatColor.RESET;
    }

}
