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
            if(p.getUniqueId().equals(playerData.getUuid()))
                p.sendMessage(ChatColor.GRAY + message);
    }

    public static void messageError(PlayerData playerData, String message) {
        msg(playerData, "" + ChatColor.RED + message);
    }

    public static String formatName(PlayerData playerData) {
        return "" + ChatColor.WHITE + ChatColor.stripColor(playerData.getName()) + ChatColor.RESET + ChatColor.GRAY;
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

    public static PlayerData getPlayerData(Player player) {
        return new PlayerData(player.getUniqueId(), player.getName());
    }

    private void registerPlaceholders() {
        PlaceholderAPI.registerPlaceholderHook("inviterewards", new PlaceholderHook() {
            @Override
            public String onPlaceholderRequest(Player player, String s) {
                VertXPlayer vertXPlayer = getDataHandler().getPlayer(getPlayerData(player));
                switch (s) {
                    case "locked":
                        return vertXPlayer.isLocked() ? "" + ChatColor.GREEN + "yes" : "" + ChatColor.RED + "no";
                        break;
                    case "satisfied":
                        return vertXPlayer.isSatisfied() ? "" + ChatColor.GREEN + "yes" : "" + ChatColor.RED + "no";
                        break;
                    case "given":
                        return vertXPlayer.isGiven() ? "" + ChatColor.GREEN + "yes" : "" + ChatColor.RED + "no";
                        break;
                    case "invited_count":
                        int count = vertXPlayer.getInvitedPlayers().size();
                        return count > 0 ? ChatColor.RED + "" + count : ChatColor.GREEN + "" + count;
                        break;
                    case "brief_stats":
                        return PlaceholderAPI.setPlaceholders(player, "locked: %locked%, completed: %satisfied%, invited: %invited_count%");
                        break;

                }
            }
        });
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
