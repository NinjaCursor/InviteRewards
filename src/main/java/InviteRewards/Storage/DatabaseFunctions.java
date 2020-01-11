package InviteRewards.Storage;

import InviteRewards.Main.InviteRewards;
import InviteRewards.Main.VertXPlayer;
import VertXCommons.Storage.*;
import org.bukkit.Bukkit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

public class DatabaseFunctions {

    private DatabaseTable invitedToInviterTable;
    private DatabaseTable.ColumnWrapper invitedUUIDColumn, invitedUsernameColumn, inviterUUIDColumn, inviterUsernameColumn, lockedColumn, givenColumn, satisfiedRequirementsColumn;
    private LinkedBlockingQueue<SequentialRunnable> databaseCommandQueue;
    private BlockingQueueHandler commandQueueHandler;
    private ConcurrentHashMap<UUID, VertXPlayer> players;
    private HashSet<UUID> playersWhoNeedReward;
    private Thread thread;


    public boolean setup() {
        invitedUUIDColumn = new DatabaseTable.ColumnWrapper("Invited_UUID", "VARCHAR(36) NOT NULL", " PRIMARY KEY");
        invitedUsernameColumn = new DatabaseTable.ColumnWrapper("Invited_Username", "VARCHAR(50) NOT NULL", "");
        inviterUUIDColumn = new DatabaseTable.ColumnWrapper("Inviter_UUID", "VARCHAR(36) NOT NULL", "");
        inviterUsernameColumn = new DatabaseTable.ColumnWrapper("Inviter_Username", "VARCHAR(50) NOT NULL", "");
        lockedColumn = new DatabaseTable.ColumnWrapper("Locked", "BOOLEAN NOT NULL", "DEFAULT FALSE");
        givenColumn = new DatabaseTable.ColumnWrapper("Given", "BOOLEAN NOT NULL", "DEFAULT FALSE");
        satisfiedRequirementsColumn = new DatabaseTable.ColumnWrapper("MetInviteRequirements", "BOOLEAN NOT NULL", "DEFAULT FALSE");
        invitedToInviterTable = new DatabaseTable("VertX_Invited_Inviter", invitedUUIDColumn, invitedUsernameColumn, inviterUUIDColumn, inviterUsernameColumn, lockedColumn, givenColumn, satisfiedRequirementsColumn);

        if (!invitedToInviterTable.create()) {
            Bukkit.getLogger().info("Could not create table");
            return false;
        }

        if (!loadData()) {
            Bukkit.getLogger().info("Could not load data");
            return false;
        }

        databaseCommandQueue = new LinkedBlockingQueue<>();
        commandQueueHandler = new BlockingQueueHandler(databaseCommandQueue);
        thread = new Thread(commandQueueHandler);
        thread.start();

        return true;
    }

    public boolean loadData() {
        players = new ConcurrentHashMap<UUID, VertXPlayer>();
        playersWhoNeedReward = new HashSet<>();

        HashMap<UUID, VertXPlayer.VertXPlayerLoader> playersLoading = new HashMap<UUID, VertXPlayer.VertXPlayerLoader>();

        try (Connection connection = SQLPool.getConnection()) {
            String sql = String.format("SELECT * FROM %s", invitedToInviterTable.getName());
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet results = statement.executeQuery();
            while (results.next()) {
                try {
                    String invitedUsername = results.getString(invitedUsernameColumn.getName());
                    String invitedUUIDString = results.getString(invitedUUIDColumn.getName());
                    UUID invitedUUID = UUID.fromString(invitedUUIDString);

                    String inviterUsername = results.getString(inviterUsernameColumn.getName());
                    String inviterUUIDString = results.getString(inviterUUIDColumn.getName());
                    UUID inviterUUID = UUID.fromString(inviterUUIDString);

                    boolean locked = results.getBoolean(lockedColumn.getName());
                    boolean given = results.getBoolean(lockedColumn.getName());
                    boolean satisfied = results.getBoolean(satisfiedRequirementsColumn.getName());

                    PlayerData invitedPlayerData = new PlayerData(invitedUUID, invitedUsername);
                    PlayerData inviterPlayerData = new PlayerData(inviterUUID, inviterUsername);

                    //set invited player object to have inviter
                    VertXPlayer.VertXPlayerLoader invitedLoader = new VertXPlayer.VertXPlayerLoader(invitedPlayerData);
                    if (playersLoading.containsKey(invitedUUID))
                        invitedLoader = playersLoading.get(invitedUUID);
                    invitedLoader.setInviter(inviterPlayerData);

                    invitedLoader.setGiven(given);
                    invitedLoader.setLocked(locked);
                    invitedLoader.setSatisfied(satisfied);

                    //add invited player to collection of people inviter invited
                    VertXPlayer.VertXPlayerLoader inviterLoader = new VertXPlayer.VertXPlayerLoader(inviterPlayerData);
                    if (playersLoading.containsKey(inviterUUID))
                        inviterLoader = playersLoading.get(inviterUUID);

                    inviterLoader.addInvited(invitedPlayerData);


                    players.put(invitedUUID, new VertXPlayer(invitedPlayerData));
                    players.put(inviterUUID, new VertXPlayer(inviterPlayerData));

                    playersLoading.put(invitedUUID, invitedLoader);
                    playersLoading.put(inviterUUID, inviterLoader);

                } catch (Exception e) {
                    Bukkit.getLogger().info(e.toString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (VertXPlayer.VertXPlayerLoader loader : playersLoading.values()) {

            ArrayList<VertXPlayer> vertXPlayers = new ArrayList<>();
            for (PlayerData playerData : loader.getInvitedPlayers()) {
                vertXPlayers.add(players.get(playerData.getUuid()));
            }

            VertXPlayer vertXPlayer = new VertXPlayer(loader.getSelfPlayer(), loader.getInviterPlayer(), vertXPlayers, loader.getLocked(), loader.getGiven(), loader.isSatisfied());

            VertXPlayer original = players.get(loader.getSelfPlayer().getUuid());

            if (original != null)
                original.copyPlayer(vertXPlayer);
            else
                players.put(loader.getSelfPlayer().getUuid(), vertXPlayer);

        }

        return true;
    }

    private CompletableFuture<Boolean> addToQueue(SequentialRunnable runnable) {
        databaseCommandQueue.add(runnable);
        return runnable.getCompletableFuture();
    }

    private boolean sendCommand(ThrowingConsumer<Connection> function) {
        try (Connection connection = SQLPool.getConnection()) {
            try {
                connection.setAutoCommit(false);
                function.acceptThrows(connection);
                connection.commit();
                InviteRewards.info("Successful database transaction");
                return true;
            } catch (Exception e) {
                InviteRewards.info(e.getMessage());
                connection.rollback();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        InviteRewards.info("Unsuccessful database transaction");
        return false;
    }

    public CompletableFuture<Boolean> forceReset(PlayerData invited) {
        SequentialRunnable runnable = new SequentialRunnable() {
            @Override
            public RunnableResult run() {
                boolean success = sendCommand((connection) -> {
                    String sql = String.format("DELETE FROM %1$s WHERE %2$s=?", invitedToInviterTable.getName(), invitedUUIDColumn.getName());
                    InviteRewards.info(sql);
                    PreparedStatement statement = connection.prepareStatement(sql);
                    statement.setString(1, invited.getUuid().toString());
                    statement.execute();
                });

                return new RunnableResult(success, true);
            }
        };
        return addToQueue(runnable).thenApply((success) -> {

            InviteRewards.runSync(new Runnable() {
                @Override
                public void run() {
                    if (success) {

                        if (players.containsKey(invited.getUuid())) {
                            PlayerData playerData = players.get(invited.getUuid()).getInviterPlayer();
                            if (playerData != null) {
                                players.get(playerData.getUuid()).getCommander().removeInvited(players.get(invited.getUuid()));
                            }
                        }

                        if (players.containsKey(invited.getUuid()))
                            players.get(invited.getUuid()).copyPlayer(new VertXPlayer(invited));
                        else
                            players.put(invited.getUuid(), new VertXPlayer(invited));


                    }
                }
            });
            return success;
        });
    }

    public CompletableFuture<Boolean> setInvited(PlayerData inviter, PlayerData invited) {
        SequentialRunnable runnable = new SequentialRunnable() {
            @Override
            public RunnableResult run() {

                boolean success = sendCommand((connection) -> {
                    String sql = String.format("INSERT INTO %1$s (%2$s, %3$s, %4$s, %5$s) VALUES(?, ?, ?, ?) ON DUPLICATE KEY UPDATE %4$s=?, %5$s=?", invitedToInviterTable.getName(), invitedUUIDColumn.getName(), invitedUsernameColumn.getName(), inviterUUIDColumn.getName(), inviterUsernameColumn.getName());
                    InviteRewards.info(sql);
                    PreparedStatement statement = connection.prepareStatement(sql);
                    statement.setString(1, invited.getUuid().toString());
                    statement.setString(2, invited.getName());
                    statement.setString(3, inviter.getUuid().toString());
                    statement.setString(4, inviter.getName());

                    statement.setString(5, inviter.getUuid().toString());
                    statement.setString(6, inviter.getName());
                    statement.execute();
                });

                return new RunnableResult(success, true);
            }
        };
        return addToQueue(runnable);
    }

    public CompletableFuture<Boolean> setConfirmed(PlayerData invited) {
        SequentialRunnable runnable = new SequentialRunnable() {
            @Override
            public RunnableResult run() {

                boolean success = sendCommand((connection) -> {
                    String sql = String.format("UPDATE %1$s SET %2$s=? WHERE %3$s=?", invitedToInviterTable.getName(), lockedColumn.getName(), invitedUUIDColumn.getName());
                    PreparedStatement statement = connection.prepareStatement(sql);
                    statement.setBoolean(1, true);
                    statement.setString(2, invited.getUuid().toString());
                    statement.execute();
                });

                return new RunnableResult(success, true);
            }
        };
        return addToQueue(runnable);
    }

    public CompletableFuture<Boolean> setGiven(PlayerData invited) {
        SequentialRunnable runnable = new SequentialRunnable() {
            @Override
            public RunnableResult run() {

                boolean success = sendCommand((connection) -> {
                    String sql = String.format("UPDATE %1$s SET %2$s=? WHERE %3$s=?", invitedToInviterTable.getName(), givenColumn.getName(), invitedUUIDColumn.getName());
                    PreparedStatement statement = connection.prepareStatement(sql);
                    statement.setBoolean(1, true);
                    statement.setString(2, invited.getUuid().toString());
                    statement.execute();
                });

                return new RunnableResult(success, true);

            }
        };
        return addToQueue(runnable);
    }

    public CompletableFuture<Boolean> setSatisfied(PlayerData invited) {
        SequentialRunnable runnable = new SequentialRunnable() {
            @Override
            public RunnableResult run() {

                boolean success = sendCommand((connection) -> {
                    String sql = String.format("UPDATE %1$s SET %2$s=? WHERE %3$s=?", invitedToInviterTable.getName(), satisfiedRequirementsColumn.getName(), invitedUUIDColumn.getName());
                    PreparedStatement statement = connection.prepareStatement(sql);
                    statement.setBoolean(1, true);
                    statement.setString(2, invited.getUuid().toString());
                    statement.execute();
                });

                return new RunnableResult(success, true);

            }
        };
        return addToQueue(runnable);
    }

    public VertXPlayer getPlayer(PlayerData playerData) {
        if (!players.containsKey(playerData.getUuid())) {
            VertXPlayer newPlayer = new VertXPlayer(playerData);
            players.put(playerData.getUuid(), newPlayer);
        }
        return players.get(playerData.getUuid());
    }

}
