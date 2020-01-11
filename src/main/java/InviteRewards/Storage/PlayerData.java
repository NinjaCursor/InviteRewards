package InviteRewards.Storage;

import java.util.UUID;

public class PlayerData {

    private UUID uuid;
    private String username;

    public PlayerData(final UUID uuid, final String username) {
        this.uuid = uuid;
        this.username = username;
    }

    public UUID getUUID() {
        return uuid;
    }

    public String getUsername() {
        return username;
    }
}
