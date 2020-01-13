package InviteRewards.Main;

import InviteRewards.CustomEvents.EventPackage;
import InviteRewards.CustomEvents.InviteSelectionEvent;
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

    public void error(String message) {
        InviteRewards.getChat().messageError(selfPlayer, message);
    }

    private void databaseError() {
        error("A database issue occurred, so your command could not be executed");
        error("Please contact the server admin about this problem");
    }

    public class PlaceholderData {
        private PlayerData locked, completed, selected;

        public PlaceholderData() {
            this.locked = this.completed = this.selected = null;
        }

        public PlayerData getLocked() {
            return locked;
        }

        public void setLocked(PlayerData locked) {
            this.locked = locked;
        }

        public PlayerData getCompleted() {
            return completed;
        }

        public void setCompleted(PlayerData completed) {
            this.completed = completed;
        }

        public PlayerData getSelected() {
            return selected;
        }

        public void setSelected(PlayerData selected) {
            this.selected = selected;
        }
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
                error("The reward has already been given to " + inviterPlayer.getName());
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
                InviteRewards.runSync(new Runnable() {
                    @Override
                    public void run() {
                        if (success) {


                            //setInvited for inviter
                            if (inviterPlayer != null) {
                                InviteRewards.getDataHandler().getPlayer(inviterPlayer).getCommander().removeInvited(player);
                            }

                            inviterPlayer = inviter;

                            //change inviterPlayer (PlayerData)
                            InviteRewards.getDataHandler().getPlayer(inviter).getCommander().setInvited(player);
                            Bukkit.getPluginManager().callEvent(new InviteSelectionEvent(new EventPackage(inviter, selfPlayer)));
                            setFinished();

                        } else {
                            databaseError();
                        }
                    }
                });


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
                            Bukkit.getPluginManager().callEvent(new LockedInEvent(new EventPackage(inviterPlayer, selfPlayer)));
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
                            Bukkit.getPluginManager().callEvent(new MetRequirementsEvent(new EventPackage(inviterPlayer, selfPlayer)));
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
    private PlaceholderData mostRecentData;
    private long timePlayed;

    private void setup(PlayerData selfPlayer) {
        mostRecentData = new PlaceholderData();
        this.selfPlayer = selfPlayer;
        timePlayed = -1;
        TimeManagement.getBasicInfo(selfPlayer.getUuid()).thenAccept(data -> {
            timePlayed = data.getTotalTime();
        });
    }

    public VertXPlayer(PlayerData selfPlayer) {
        setup(selfPlayer);
        given = false;
        locked = false;
        satisfied = false;
        this.inviterPlayer = null;
        invitedPlayers = new HashSet<>();
        commander = new PlayerCommander(this);
    }

    public VertXPlayer(final PlayerData selfPlayer, final PlayerData inviterPlayer, final ArrayList<VertXPlayer> invitedPlayers, boolean locked, boolean given, boolean satisfied) {
        setup(selfPlayer);
        this.commander = new PlayerCommander(this);
        this.inviterPlayer = inviterPlayer;
        this.invitedPlayers = new HashSet<>(invitedPlayers);
        this.locked = locked;
        this.given = given;
        this.satisfied = satisfied;
    }

    public PlaceholderData getMostRecent() {
        return mostRecentData;
    }

    public void msg(String message) {
        InviteRewards.getChat().msg(selfPlayer, message);
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

    public long getTimePlayed() {
        return timePlayed;
    }

    public int getProgress() {
        PublicDataContainer data = TimeManagement.getLoginHandler().getDataImmediate(selfPlayer.getUuid());

        if (data == null)
            return -1;

        double percentage = (data.getTotalTime()*1.0) / (InviteRewards.minTotalTime *1000*60);
        if (percentage > 1)
            return 100;
        return (int) (percentage*100);
    }
}
