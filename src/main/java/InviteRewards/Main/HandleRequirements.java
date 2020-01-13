package InviteRewards.Main;

import VertXTimeManagement.Main.TimeManagement;
import org.bukkit.Bukkit;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class HandleRequirements {

    private static ConcurrentHashMap<UUID, Integer> playerTaskIDs = new ConcurrentHashMap<>();

    public static void end(VertXPlayer vertXPlayer) {
        UUID uuid = vertXPlayer.getSelfPlayer().getUuid();
        if (playerTaskIDs.containsKey(uuid)) {
            Bukkit.getScheduler().cancelTask(playerTaskIDs.get(uuid));
            playerTaskIDs.remove(uuid);
        }
    }

    public static void handle(VertXPlayer vertXPlayer, Runnable runnable) {

        boolean wasInvited;

        if (vertXPlayer.getInviterPlayer() != null)
            wasInvited = true;
        else
            wasInvited = false;

        TimeManagement.getBasicInfo(vertXPlayer.getSelfPlayer().getUuid()).thenAccept((dataContainer) -> {

            long minutesPlayed = dataContainer.getTotalTime() / (1000 * 60);
            long minutesLeft = InviteRewards.minTotalTime-minutesPlayed;

            //check if they really did not satisify requirements
            if (minutesPlayed < InviteRewards.minTotalTime) {


                int taskToken = Bukkit.getScheduler().scheduleSyncDelayedTask(InviteRewards.getPlugin(), new Runnable() {
                    @Override
                    public void run() {
                        Bukkit.getLogger().info("MARKER MARKER MAKER MARKER");
                        InviteRewards.getDataHandler().getPlayer(vertXPlayer.getSelfPlayer()).getCommander().setSatisfied();
                        playerTaskIDs.remove(vertXPlayer.getSelfPlayer());
                    }
                }, minutesLeft*60*20);


                runnable.run();

                playerTaskIDs.put(vertXPlayer.getSelfPlayer().getUuid(), taskToken);

            } else {
                vertXPlayer.getCommander().setSatisfied();
                Bukkit.getLogger().info("An error occurred where a vertXPlayer was marked as not having satisfied the requirements even though they had. DANGEROUS ERROR");
            }
        });
    }

}
