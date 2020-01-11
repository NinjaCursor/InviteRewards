package InviteRewards.Main;

import InviteRewards.CustomEvents.LockedInEvent;
import InviteRewards.CustomEvents.MetRequirementsEvent;
import InviteRewards.Storage.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class VertXPlayer {


    public static class VertXPlayerLoader {

        private PlayerData selfPlayer, inviterPlayer;
        private ArrayList<PlayerData> invitedPlayers;
        private boolean locked, given, satisfied;

        public VertXPlayerLoader(PlayerData selfPlayer) {
            this.selfPlayer = selfPlayer;
            this.inviterPlayer = null;
            this.invitedPlayers = new ArrayList<>();
            this.locked = false;
            this.given = false;
            this.satisfied = false;
        }

        public boolean isSatisfied() {
            return satisfied;
        }

        public void setSatisfied(boolean satisfied) {
            this.satisfied = satisfied;
        }

        public void setLocked(boolean locked) {
            this.locked = locked;
        }

        public void setGiven(boolean given) {
            this.given = given;
        }

        public boolean getGiven() {
            return this.given;
        }

        public boolean getLocked() {
            return this.locked;
        }

        public void setInviter(PlayerData inviter) {
            this.inviterPlayer = inviter;
        }

        public void addInvited(PlayerData invitedPlayer) {
            invitedPlayers.add(invitedPlayer);
        }

        public PlayerData getSelfPlayer() {
            return selfPlayer;
        }

        public PlayerData getInviterPlayer() {
            return inviterPlayer;
        }

        public ArrayList<PlayerData> getInvitedPlayers() {
            return invitedPlayers;
        }
    }

    private String getYesNo(boolean trueOrFalse) {
        return ((trueOrFalse) ? ChatColor.BLUE + "YES" : ChatColor.RED + "NO") + ChatColor.GRAY;
    }

    public String[] getStats() {
        List<String> statFormatted = new ArrayList<>();

        statFormatted.add("----------- Invite Stats ----------");
        statFormatted.add("Player: " + selfPlayer.getUsername());
        if (inviterPlayer != null)
            statFormatted.add("Selected Inviter: " + inviterPlayer.getUsername());
        else
            statFormatted.add("Has not selected inviter.");
        statFormatted.add("Has Invited: " + invitedPlayers.size());

        statFormatted.add("Locked: " + getYesNo(locked));
        statFormatted.add("Met Requirements: " + getYesNo(satisfied));


        for (VertXPlayer vertXPlayer : invitedPlayers) {
            String nameColor = ((vertXPlayer.isSatisfied()) ? ChatColor.GREEN + "" : ChatColor.RED + "");
            String suffixString = ((vertXPlayer.isLocked()) ? ChatColor.GRAY + "Locked" : ChatColor.RED + "Not Locked");

            statFormatted.add(" - " + nameColor + vertXPlayer.getSelfPlayer().getUsername() + " " + suffixString);
        }

        String[] arr = statFormatted.toArray(new String[0]);
        return arr;
    }

    public void error(String message) {
        Main.messageError(selfPlayer, message);
    }

    private void databaseError() {
        error("A database issue occurred, so your command could not be executed");
        error("Please contact the server admin about this problem");
    }

    public class PlayerCommander extends CommandUtility {

        private VertXPlayer player;

        public PlayerCommander(VertXPlayer player) {
            this.player = player;
        }

        public void setInviter(PlayerData inviter) {

            if (isGiven()) {
                error("The reward has already been given to " + Main.formatName(inviterPlayer));
                return;
            }

            if (isLocked()) {
                error("You have already locked in your choice");
                return;
            }

            if (inviter.getUUID() == selfPlayer.getUUID()) {
                error("You cannot invite yourself");
                return;
            }

            Main.getDataHandler().setInvited(inviter, selfPlayer).thenAccept((success) -> {
                if (success) {
                    //setInvited for inviter
                    inviterPlayer = inviter;

                    msg("You have selected " + inviter.getUsername() + " to receive your invite reward");

                    //change inviterPlayer (PlayerData)
                    Main.getDataHandler().getPlayer(inviter).getCommander().setInvited(player);

                } else {
                    //todo: message player saying an error occurred
                    Bukkit.getLogger().info("It fails");
                }
            });
        }

        public void setInvited(VertXPlayer invitedPlayer) {
            invitedPlayers.add(invitedPlayer);
        }

        public void setConfirmed() {

            if (inviterPlayer == null) {
                error("You need to select a player first");
                return;
            }

            if (isLocked()) {
                error("You have already locked in your choice");
                return;
            }

            Main.getDataHandler().setConfirmed(selfPlayer).thenAcceptAsync((success) -> {
                if (success) {
                    Main.runSync(new Runnable() {
                        @Override
                        public void run() {
                            Bukkit.getPluginManager().callEvent(new LockedInEvent(inviterPlayer, selfPlayer));
                        }
                    });
                    locked = true;
                } else {
                    databaseError();
                }

            });
        }

        public void setGiven() {
            Main.getDataHandler().setGiven(selfPlayer).thenAccept((success) -> {
                if (success) {
                    given = true;
                } else {
                    databaseError();
                }
            });
        }

        public void setSatisfied() {

            Main.getDataHandler().setSatisfied(selfPlayer).thenAccept((success) -> {
                if (success) {
                    satisfied = true;
                    Main.runSync(new Runnable() {
                        @Override
                        public void run() {
                            Bukkit.getPluginManager().callEvent(new MetRequirementsEvent(inviterPlayer, selfPlayer));
                        }
                    });

                } else {
                    databaseError();
                }
            });

        }

    }

    private PlayerData selfPlayer, inviterPlayer;
    Set<VertXPlayer> invitedPlayers;
    private PlayerCommander commander;
    private boolean given, locked, satisfied;

    public VertXPlayer(PlayerData selfPlayer) {
        given = false;
        locked = false;
        satisfied = false;
        this.selfPlayer = selfPlayer;
        this.inviterPlayer = null;
        invitedPlayers = new HashSet<>();
        commander = new PlayerCommander(this);
    }

    public VertXPlayer(final PlayerData selfPlayer, final PlayerData inviterPlayer, final ArrayList<VertXPlayer> invitedPlayers, boolean locked, boolean given, boolean satisfied) {
        this.commander = new PlayerCommander(this);
        this.selfPlayer = selfPlayer;
        this.inviterPlayer = inviterPlayer;
        this.invitedPlayers = new HashSet<>(invitedPlayers);
        this.locked = locked;
        this.given = given;
        this.satisfied = satisfied;
    }

    public void showRequirements()  {
        msg("Invite Reward Requirements");
        msg(" - " + Main.minTotalTime + "m on the server");
    }

    public void msg(String message) {
        Main.msg(selfPlayer, message);
    }

    public PlayerData getSelfPlayer() {
        return selfPlayer;
    }

    public PlayerData getInviterPlayer() {
        return inviterPlayer;
    }

    public Set<VertXPlayer> getInvitedPlayers() {
        return invitedPlayers;
    }

    public PlayerCommander getCommander() {
        return commander;
    }

    public boolean isGiven() {
        return given;
    }

    public boolean isLocked() {
        return locked;
    }

    public boolean isSatisfied() {
        return satisfied;
    }

    public void copyPlayer(VertXPlayer vertXPlayer) {
        selfPlayer = vertXPlayer.getSelfPlayer();
        inviterPlayer = vertXPlayer.getInviterPlayer();
        invitedPlayers = vertXPlayer.getInvitedPlayers();
        locked = vertXPlayer.isLocked();
        given = vertXPlayer.isGiven();
        satisfied = vertXPlayer.isSatisfied();
    }
}