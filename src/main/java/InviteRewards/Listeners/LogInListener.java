package InviteRewards.Listeners;

import InviteRewards.Main.HandleRequirements;
import InviteRewards.Main.Main;
import InviteRewards.Main.VertXPlayer;
import InviteRewards.Storage.PlayerData;
import VertXTimeManagement.Main.TimeManagement;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.UUID;

public class LogInListener implements Listener {

    private HashMap<UUID, Integer> playerTaskIDs;

    public LogInListener() {
        playerTaskIDs = new HashMap<>();
    }

    @EventHandler
    public void onLogout(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        PlayerData playerData = new PlayerData(uuid, player.getDisplayName());
        VertXPlayer vertXPlayer = Main.getDataHandler().getPlayer(playerData);
        HandleRequirements.end(vertXPlayer);
    }

    @EventHandler
    public void onLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        PlayerData playerData = new PlayerData(uuid, player.getDisplayName());
        VertXPlayer vertXPlayer = Main.getDataHandler().getPlayer(playerData);

        Main.info("aasdfas;ldkfja;lksdfja;skldfj;alkjdsf");
        Main.info("You suck");
        boolean wasInvited = false;
        if (vertXPlayer.getInviterPlayer() != null)
            wasInvited = true;

        if (!vertXPlayer.isSatisfied()) {
            HandleRequirements.handle(vertXPlayer);
        }

    }
 }
