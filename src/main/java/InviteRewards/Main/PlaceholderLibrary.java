package InviteRewards.Main;

import VertXCommons.Storage.PlayerData;
import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.ExecutionException;

public class PlaceholderLibrary extends PlaceholderExpansion {

    private JavaPlugin plugin;

    /**
     * Since we register the expansion inside our own plugin, we
     * can simply use this method here to get an instance of our
     * plugin.
     *
     * @param plugin
     *        The instance of our plugin.
     */
    public PlaceholderLibrary(JavaPlugin plugin){
        this.plugin = plugin;
    }

    /**
     * Because this is an internal class,
     * you must override this method to let PlaceholderAPI know to not unregister your expansion class when
     * PlaceholderAPI is reloaded
     *
     * @return true to persist through reloads
     */
    @Override
    public boolean persist() {
        return true;
    }

    /**
     * Because this is a internal class, this check is not needed
     * and we can simply return {@code true}
     *
     * @return Always true since it's an internal class.
     */
    @Override
    public boolean canRegister(){
        return true;
    }

    /**
     * The name of the person who created this expansion should go here.
     * <br>For convienience do we return the author from the plugin.yml
     *
     * @return The name of the author as a String.
     */
    @Override
    public String getAuthor(){
        return plugin.getDescription().getAuthors().toString();
    }

    /**
     * The placeholder identifier should go here.
     * <br>This is what tells PlaceholderAPI to call our onRequest
     * method to obtain a value if a placeholder starts with our
     * identifier.
     * <br>This must be unique and can not contain % or _
     *
     * @return The identifier in {@code %<identifier>_<value>%} as String.
     */
    @Override
    public String getIdentifier(){
        return "invplugin";
    }

    /**
     * This is the version of the expansion.
     * <br>You don't have to use numbers, since it is set as a String.
     *
     * For convienience do we return the version from the plugin.yml
     *
     * @return The version as a String.
     */
    @Override
    public String getVersion(){
        return plugin.getDescription().getVersion();
    }

    public static PlayerData getPlayerData(OfflinePlayer player) {
        return new PlayerData(player.getUniqueId(), player.getName());
    }

    public String getCount(int count) {
        return ((count > 0) ? "" + ChatColor.GREEN + count : "" + ChatColor.RED + count) + ChatColor.GRAY;
    }

    public String getYesNo(boolean trueOrFalse) {
        return (trueOrFalse ? "" + ChatColor.GREEN + "yes" : "" + ChatColor.RED + "no") + ChatColor.GRAY;
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {
        VertXPlayer vertXPlayer = InviteRewards.getDataHandler().getPlayer(getPlayerData(player));
        switch (params) {
            case "locked":
                return getYesNo(vertXPlayer.isLocked());
            case "satisfied":
                return getYesNo(vertXPlayer.isSatisfied());
            case "given":
                return getYesNo(vertXPlayer.isGiven());
            case "invited_count":
                int count = vertXPlayer.getInvitedPlayers().size();
                return (count < 1 ? ChatColor.RED + "" + count : ChatColor.GREEN + "" + count) + ChatColor.GRAY;
            case "brief_stats":
                return PlaceholderAPI.setPlaceholders(player, "locked: %invplugin_locked%, completed: %invplugin_satisfied%, invited: %invplugin_invited_count%");
            case "self":
                return vertXPlayer.getSelfPlayer().getName();
            case "inviter":
                if (vertXPlayer.getInviterPlayer() != null) {
                    return vertXPlayer.getInviterPlayer().getName();
                }
                return "none selected";
            case "progress":
                if (vertXPlayer.getProgress() >= 0)
                    return vertXPlayer.getProgress() + "%";
                else
                    return vertXPlayer.isSatisfied() ? "100%" : "not completed";
            case "invite_selection_command":
                return InviteRewards.getInvitedByCommandName();
            case "invite_confirm_command":
                return InviteRewards.getInvitedConfirmCommandName();
            case "invite_stats_command":
                return InviteRewards.getInviteStatsCommandName();
            case "time_required":
                return InviteRewards.minTotalTime + "m";
            case "most_recent_invited_locked":
                return vertXPlayer.getMostRecent().getLocked().getName();
            case "most_recent_invited_completed":
                return vertXPlayer.getMostRecent().getCompleted().getName();
            case "most_recent_invited_selected":
                return vertXPlayer.getMostRecent().getSelected().getName();

        }
        return null;
    }

}
