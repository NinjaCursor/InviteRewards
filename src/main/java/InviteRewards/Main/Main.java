package InviteRewards.Main;

import InviteRewards.Commands.*;
import InviteRewards.CustomEvents.AwardedEvent;
import InviteRewards.CustomEvents.LockedInEvent;
import InviteRewards.Listeners.*;
import InviteRewards.Storage.DatabaseFunctions;
import InviteRewards.Storage.PlayerData;
import InviteRewards.Storage.SQLPool;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;


import java.io.File;

public class Main extends JavaPlugin {

    private static JavaPlugin plugin;
    private static DatabaseFunctions dataHandler;
    public static int minLogins, minTotalTime, minLoginTime;

    public static DatabaseFunctions getDataHandler() {
        return dataHandler;
    }

    public static JavaPlugin getPlugin() {
        return plugin;
    }

    public static void info(String message) {
        Bukkit.getLogger().info("[VertX.InviteRewards] INFO >> " + message);
    }

    public static void msg(PlayerData playerData, String message) {
        for(Player p : Bukkit.getServer().getOnlinePlayers())
            if(p.getUniqueId().equals(playerData.getUUID()))
                p.sendMessage(ChatColor.GRAY + message);
    }

    public static void messageError(PlayerData playerData, String message) {
        msg(playerData, "" + ChatColor.RED + message);
    }

    public static String formatName(PlayerData playerData) {
        return "" + ChatColor.WHITE + ChatColor.stripColor(playerData.getUsername()) + ChatColor.RESET + ChatColor.GRAY;
    }

    @Override
    public void onEnable() {
        plugin = this;
        createConfig();


        Bukkit.getServer().getPluginManager().registerEvents(new AwardListener(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new LockedInListener(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new LogInListener(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new MetRequirementsListener(), this);

        dataHandler = new DatabaseFunctions();
        if (!dataHandler.setup()) {
            info("Could not load database");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        minLogins = getConfig().getInt("logins");
        minLoginTime = getConfig().getInt("login-min");
        minTotalTime = getConfig().getInt("totaltime");

        getCommand("invitedby").setExecutor(new InvitedByCommand("invitedby" , ""));
        getCommand("inviteconfirm").setExecutor(new InvitedConfirmCommand("inviteconfirm", ""));
        getCommand("invitestats").setExecutor(new InviteStatsCommand("invitestats", ""));
        getCommand("inviterequirements").setExecutor(new InviteRequirementCommand("inviterequirements", ""));
        getCommand("opinvitereset").setExecutor(new OpInviteResetCommand("opinvitereset", "invite.admin"));

    }

    public static void runSync(Runnable runnable) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(), runnable);
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
