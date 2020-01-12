package InviteRewards.Main;

import InviteRewards.CustomEvents.LockedInEvent;
import InviteRewards.CustomEvents.MetRequirementsEvent;
import VertXCommons.Storage.PlayerData;
import VertXTimeManagement.Main.TimeManagement;
import VertXTimeManagement.Storage.PublicDataContainer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

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
        return ((trueOrFalse) ? ChatColor.GREEN + "yes" : ChatColor.RED + "no") + ChatColor.GRAY;
    }

    public String[] getStats() {
        List<String> statFormatted = new ArrayList<>();

        statFormatted.add("----------[ " + ChatColor.AQUA + ChatColor.BOLD + "Invite Stats" + ChatColor.GRAY + " ]---------");
        statFormatted.add("Player: " + InviteRewards.formatName(selfPlayer));
        if (inviterPlayer != null)
            statFormatted.add("Inviter: " + InviteRewards.formatName(inviterPlayer));
        else
            statFormatted.add("Inviter: " + ChatColor.RED + "none selected");

        statFormatted.add("Locked: " + getYesNo(locked));
        statFormatted.add("Completed: " + getYesNo(satisfied));

        if (invitedPlayers.size() == 0)
            statFormatted.add("Invited: " + ChatColor.RED + "none");
        else
            statFormatted.add("Invited: ");


        for (VertXPlayer vertXPlayer : invitedPlayers) {
            String satisfiedNotification = ((vertXPlayer.isSatisfied()) ? ChatColor.GREEN + "completed" : ChatColor.RED + "not completed");
            String suffixString = ((vertXPlayer.isLocked()) ? ChatColor.GRAY + "locked" : ChatColor.RED + "not locked");

            statFormatted.add(" - " + InviteRewards.formatName(vertXPlayer.getSelfPlayer()) + " " + suffixString + ChatColor.RESET + " | " + satisfiedNotification);
        }

        String[] arr = statFormatted.toArray(new String[0]);
        return arr;
    }

    public void error(String message) {
        InviteRewards.messageError(selfPlayer, message);
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

            if (isExecutingDependentCommand()) {
                error(getDependencyMessage());
                return;
            }

            if (isGiven()) {
                error("The reward has already been given to " + InviteRewards.formatName(inviterPlayer));
                return;
            }

            if (isLocked()) {
                error("You have already locked in your choice");
                return;
            }

            if (inviter.getUuid().equals(selfPlayer.getUuid())) {
                error("You cannot invite yourself");
                return;
            }
            setRunning();
            InviteRewards.getDataHandler().setInvited(inviter, selfPlayer).thenAccept((success) -> {
                if (success) {

                    //setInvited for inviter
                    if (inviterPlayer != null) {
                        InviteRewards.getDataHandler().getPlayer(inviterPlayer).getCommander().removeInvited(player);
                    }

                    inviterPlayer = inviter;

                    msg("You have selected " + InviteRewards.formatName(inviter) + " to receive your invite reward");
                    msg("Type " + ChatColor.WHITE + "/inviteconfirm" + ChatColor.GRAY + " to confirm your selection");

                    //change inviterPlayer (PlayerData)
                    InviteRewards.getDataHandler().getPlayer(inviter).getCommander().setInvited(player);


                } else {
                    databaseError();
                }
                setFinished();
            }).exceptionally((exception) -> {
                setFinished();
                InviteRewards.info(exception.getMessage());
                return null;
            });
        }

        public void setInvited(VertXPlayer invitedPlayer) {
            invitedPlayers.add(invitedPlayer);
        }

        public void removeInvited(VertXPlayer invitedPlayer) {
            invitedPlayers.remove(invitedPlayer);
        }

        public void setConfirmed() {

            if (isExecutingDependentCommand()) {
                error(getDependencyMessage());
                return;
            }

            if (inviterPlayer == null) {
                error("You need to select a player first");
                return;
            }

            if (isLocked()) {
                error("You have already locked in your choice");
                return;
            }

            setRunning();
            InviteRewards.getDataHandler().setConfirmed(selfPlayer).thenAcceptAsync((success) -> {
                if (success) {
                    InviteRewards.runSync(new Runnable() {
                        @Override
                        public void run() {
                            Bukkit.getPluginManager().callEvent(new LockedInEvent(inviterPlayer, selfPlayer));
                        }
                    });

                    locked = true;
                } else {
                    databaseError();
                }
                setFinished();
            }).exceptionally((exception) -> {
                setFinished();
                InviteRewards.info(exception.getMessage());
                return null;
            });
        }

        public void setGiven() {
            InviteRewards.getDataHandler().setGiven(selfPlayer).thenAccept((success) -> {
                if (success) {
                    given = true;
                } else {
                    databaseError();
                }
            });
        }

        public void setSatisfied() {

            InviteRewards.getDataHandler().setSatisfied(selfPlayer).thenAccept((success) -> {
                if (success) {
                    satisfied = true;
                    InviteRewards.runSync(new Runnable() {
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

    public void msg(String message) {
        InviteRewards.msg(selfPlayer, message);
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

    public CompletableFuture<Long> getTimePlayed() {
        return TimeManagement.getBasicInfo(selfPlayer.getUuid()).thenApply((data) -> {
            return data.getTotalTime();
        });
    }

    public CompletableFuture<Long> getTimeLeft() {
        return getTimePlayed().thenApply((totalTimePlayed) -> {
           return (long) (InviteRewards.minTotalTime - (1.0*totalTimePlayed)/(60.0*1000.0));
        });
    }

    public CompletableFuture<Integer> getProgress() {
        return getTimePlayed().thenApply((totalTimePlayed) -> {
           double percentage = (totalTimePlayed*1.0) / (InviteRewards.minTotalTime *1000*60);
           return (int) percentage;
        });
    }
}
