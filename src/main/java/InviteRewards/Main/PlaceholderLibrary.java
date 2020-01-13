package InviteRewards.Main;

import VertXCommons.Storage.PlayerData;
import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.java.JavaPlugin;

public class PlaceholderLibrary extends PlaceholderExpansion {

    private JavaPlugin plugin;

    /**
     * Since we register the expansion inside our own plugin, we
     * can simply use this method here to get an instance of our
     * plugin.
     *
     * @param plugin The instance of our plugin.
     */
    public PlaceholderLibrary(JavaPlugin plugin) {
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
    public boolean canRegister() {
        return true;
    }

    /**
     * The name of the person who created this expansion should go here.
     * <br>For convienience do we return the author from the plugin.yml
     *
     * @return The name of the author as a String.
     */
    @Override
    public String getAuthor() {
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
    public String getIdentifier() {
        return "invplugin";
    }

    /**
     * This is the version of the expansion.
     * <br>You don't have to use numbers, since it is set as a String.
     * <p>
     * For convienience do we return the version from the plugin.yml
     *
     * @return The version as a String.
     */
    @Override
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }

    public static PlayerData getPlayerData(OfflinePlayer player) {
        return new PlayerData(player.getUniqueId(), player.getName());
    }

    public String getCount(int count) {
        return ((count > 0) ? "" + ChatColor.GREEN + count : "" + ChatColor.RED + count) + InviteRewards.getChat().getDefaultColor();
    }

    public String getYesNo(boolean trueOrFalse) {
        return (trueOrFalse ? "" + ChatColor.GREEN + "yes" : "" + ChatColor.RED + "no") + InviteRewards.getChat().getDefaultColor();
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {
        VertXPlayer vertXPlayer = InviteRewards.getDataHandler().getPlayer(getPlayerData(player));
        String returnString = "";
        switch (params) {
            case "locked":
                returnString += getYesNo(vertXPlayer.isLocked());
                break;
            case "satisfied":
                returnString += getYesNo(vertXPlayer.isSatisfied());
                break;
            case "given":
                returnString += getYesNo(vertXPlayer.isGiven());
                break;
            case "invited_count":
                int count = vertXPlayer.getInvitedPlayers().size();
                returnString += (count < 1 ? ChatColor.RED + "" + count : ChatColor.GREEN + "" + count);
                break;
            case "brief_stats":
                returnString += PlaceholderAPI.setPlaceholders(player, "locked: %invplugin_locked%, completed: %invplugin_satisfied%, invited: %invplugin_invited_count%");
                break;
            case "self":
                returnString += vertXPlayer.getSelfPlayer().getName();
                break;
            case "inviter":
                if (vertXPlayer.getInviterPlayer() != null) {
                    returnString += vertXPlayer.getInviterPlayer().getName();
                } else {
                    returnString += "none selected";
                }
                break;
            case "progress":
                if (vertXPlayer.isSatisfied()) {
                    returnString += "100%";
                } else {
                    if (vertXPlayer.getProgress() >= 0)
                        returnString += vertXPlayer.getProgress() + "%";
                    else
                        returnString += vertXPlayer.isSatisfied() ? "100%" : "not completed";
                }
                break;
            case "invite_selection_command":
                returnString += InviteRewards.getInvitedByCommandName();
                break;
            case "invite_confirm_command":
                returnString += InviteRewards.getInvitedConfirmCommandName();
                break;
            case "invite_stats_command":
                returnString += InviteRewards.getInviteStatsCommandName();
                break;
            case "time_required":
                returnString += InviteRewards.minTotalTime + "m";
                break;
            case "most_recent_invited_locked":
                returnString += vertXPlayer.getMostRecent().getLocked().getName();
                break;
            case "most_recent_invited_completed":
                returnString += vertXPlayer.getMostRecent().getCompleted().getName();
                break;
            case "most_recent_invited_selected":
                returnString += vertXPlayer.getMostRecent().getSelected().getName();
                break;
            case "most_recent_invited_rewarded":
                returnString += vertXPlayer.getMostRecent().getAward().getName();
                break;
            case "minutes_left":
                returnString += "" + Math.ceil(InviteRewards.minTotalTime-(vertXPlayer.getTimePlayed()/(60.0*1000.0)));

        }

        returnString += InviteRewards.getChat().getDefaultColor();
        return returnString;

    }

}
