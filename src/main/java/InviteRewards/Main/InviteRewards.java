package InviteRewards.Main;

import InviteRewards.Commands.*;
import InviteRewards.Listeners.*;
import InviteRewards.Storage.DatabaseFunctions;
import VertXCommons.Storage.PlayerData;
import VertXCommons.Storage.SQLPool;
import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.PlaceholderHook;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;


import java.io.File;

public class InviteRewards extends JavaPlugin {

    private static JavaPlugin plugin;
    private static DatabaseFunctions dataHandler;
    public static int minLogins, minTotalTime, minLoginTime;
    private static InvitedByCommand invitedByCommand;
    private static InvitedConfirmCommand invitedConfirmCommand;
    private static InviteStatsCommand inviteStatsCommand;
    private static ChatHandler chatHandler;

    public static String getInvitedByCommandName() {
        return invitedByCommand.getCommandName();
    }

    public static String getInvitedConfirmCommandName() {
        return invitedConfirmCommand.getCommandName();
    }

    public static String getInviteStatsCommandName() {
        return inviteStatsCommand.getCommandName();
    }

    public static DatabaseFunctions getDataHandler() {
        return dataHandler;
    }

    public static JavaPlugin getPlugin() {
        return plugin;
    }

    public static void info(String message) {
        Bukkit.getLogger().info("[VertX.InviteRewards] INFO >> " + message);
    }

    public static ChatHandler getChat() {
        return chatHandler;
    }

    public static void setPlugin(JavaPlugin otherPlugin) {
        plugin = otherPlugin;
    }

    @Override
    public void onEnable() {
        plugin = this;
        createConfig();

        Bukkit.getServer().getPluginManager().registerEvents(new AwardListener(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new LockedInListener(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new LogInListener(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new MetRequirementsListener(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new InviteSelectionListener(), this);

        dataHandler = new DatabaseFunctions();
        if (!dataHandler.setup()) {
            info("Could not load database");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        minLogins = getConfig().getInt("logins");
        minLoginTime = getConfig().getInt("login-min");
        minTotalTime = getConfig().getInt("totaltime");
        chatHandler = new ChatHandler(getConfig().getString("default-color"));

        invitedByCommand = new InvitedByCommand("invitedby" , "");
        invitedConfirmCommand = new InvitedConfirmCommand("inviteconfirm", "");
        inviteStatsCommand = new InviteStatsCommand("invitestats", "");

        getCommand("invitedby").setExecutor(invitedByCommand);
        getCommand("inviteconfirm").setExecutor(invitedConfirmCommand);
        getCommand("invitestats").setExecutor(inviteStatsCommand);
        getCommand("opinvitereset").setExecutor(new OpInviteResetCommand("opinvitereset", "invite.admin"));

        PlaceholderLibrary library = new PlaceholderLibrary(this);
        PlaceholderAPI.registerExpansion(library);
    }

    public static void runSync(Runnable runnable) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(InviteRewards.getPlugin(), runnable);
    }

    private void createConfig() {
        getLogger().info("Checking config.yml");
        try {
            if (!getDataFolder().exists()) {
                getDataFolder().mkdirs();
            }
            File file = new File(getDataFolder(), "config.yml");
            if (!file.exists()) {
                getLogger().info("config.yml not found, creating!");
                saveDefaultConfig();
            } else {
                getLogger().info("config.yml found, loading!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onDisable() {
        SQLPool.close();
    }

}
