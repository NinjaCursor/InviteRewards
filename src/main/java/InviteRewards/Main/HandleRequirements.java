package InviteRewards.Main;

import InviteRewards.Storage.PlayerData;
import VertXTimeManagement.Main.TimeManagement;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class HandleRequirements {

    private static ConcurrentHashMap<UUID, Integer> playerTaskIDs = new ConcurrentHashMap<>();

    public static void end(VertXPlayer vertXPlayer) {
        UUID uuid = vertXPlayer.getSelfPlayer().getUUID();
        if (playerTaskIDs.containsKey(uuid)) {
            Bukkit.getScheduler().cancelTask(playerTaskIDs.get(uuid));
            playerTaskIDs.remove(uuid);
        }
    }

    public static void handle(VertXPlayer vertXPlayer) {

        boolean wasInvited;

        if (vertXPlayer.getInviterPlayer() != null)
            wasInvited = true;
        else
            wasInvited = false;

        TimeManagement.getBasicInfo(vertXPlayer.getSelfPlayer().getUUID()).thenAccept((dataContainer) -> {

            long minutesPlayed = dataContainer.getTotalTime() / (1000 * 60);
            long minutesLeft = Main.minTotalTime-minutesPlayed;

            //check if they really did not satisify requirements
            if (minutesPlayed < Main.minTotalTime) {

                if (wasInvited) {
                    Main.msg(vertXPlayer.getSelfPlayer(), "For " + vertXPlayer.getInviterPlayer().getUsername() + " to receive your invite reward");
                    Main.msg(vertXPlayer.getSelfPlayer(), "play for " + minutesLeft + " more minute(s)");
                }

                int taskToken = Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(), new Runnable() {
                    @Override
                    public void run() {
                        Main.getDataHandler().getPlayer(vertXPlayer.getSelfPlayer()).getCommander().setSatisfied();
                        playerTaskIDs.remove(vertXPlayer.getSelfPlayer());
                    }
                }, minutesLeft*60*20);

                playerTaskIDs.put(vertXPlayer.getSelfPlayer().getUUID(), taskToken);

            } else {
                vertXPlayer.getCommander().setSatisfied();
                Bukkit.getLogger().info("An error occurred where a vertXPlayer was marked as not having satisfied the requirements even though they had. DANGEROUS ERROR");
            }
        });
    }

}
