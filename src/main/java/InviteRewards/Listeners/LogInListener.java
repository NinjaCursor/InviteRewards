package InviteRewards.Listeners;

import InviteRewards.Main.ConfigMessage;
import InviteRewards.Main.HandleRequirements;
import InviteRewards.Main.InviteRewards;
import InviteRewards.Main.VertXPlayer;
import VertXCommons.Storage.PlayerData;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class LogInListener implements Listener {

    private HashMap<UUID, Integer> playerTaskIDs;
    private ConfigMessage newPersonMessage, oldNotInvitedMessage, oldInvitedNotLockedButCompletedMessage, oldNotCompletedMessage;

    public LogInListener() {
        playerTaskIDs = new HashMap<>();
        newPersonMessage = new ConfigMessage("login-message.new");
        oldNotInvitedMessage = new ConfigMessage("login-message.old.not-invited");
        oldInvitedNotLockedButCompletedMessage = new ConfigMessage("login-message.old.invited.not-locked-but-completed");
        oldNotCompletedMessage = new ConfigMessage("login-message.old.invited.not-completed");
    }

    @EventHandler
    public void onLogout(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        PlayerData playerData = new PlayerData(uuid, player.getDisplayName());
        VertXPlayer vertXPlayer = InviteRewards.getDataHandler().getPlayer(playerData);
        HandleRequirements.end(vertXPlayer);
    }

    @EventHandler
    public void onLogin(PlayerJoinEvent event) {

        Player player = event.getPlayer();

        UUID uuid = player.getUniqueId();
        PlayerData playerData = new PlayerData(uuid, player.getDisplayName());

        VertXPlayer vertXPlayer = InviteRewards.getDataHandler().getPlayer(playerData);

        if (!vertXPlayer.isSatisfied()) {
            HandleRequirements.handle(vertXPlayer);
        }

        if (!player.hasPlayedBefore()) {
            newPersonMessage.sendMessage(playerData);
        } else {
            if (vertXPlayer.getInviterPlayer() == null) {
                oldNotInvitedMessage.sendMessage(playerData);
            } else {
                if (vertXPlayer.isSatisfied() && !vertXPlayer.isLocked())
                    oldInvitedNotLockedButCompletedMessage.sendMessage(playerData);
                else
                    oldNotCompletedMessage.sendMessage(playerData);
            }
        }

    }
 }
