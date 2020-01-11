package InviteRewards.UsernameConverter;

import InviteRewards.Main.Main;
import InviteRewards.Storage.PlayerData;
import org.json.JSONObject;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class UsernameConverter {

    //https://stackoverflow.com/questions/4308554/simplest-way-to-read-json-from-a-url-in-java
    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    //https://stackoverflow.com/questions/4308554/simplest-way-to-read-json-from-a-url-in-java
    private static JSONObject readJsonFromUrl(String url) throws IOException {
        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            JSONObject json = new JSONObject(jsonText);
            return json;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            is.close();
        }
        return null;
    }

    private static UUID getUUIDFromString(String uuidString) {
        return UUID.fromString(uuidString.replaceFirst("(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)", "$1-$2-$3-$4-$5" ));
    }


    public static CompletableFuture<PlayerData> getPlayerData(String username) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            String url = "https://api.mojang.com/users/profiles/minecraft/"+username;
                try {
                    JSONObject UUIDObject = readJsonFromUrl(url);
                    String uuidString = UUIDObject.get("id").toString();
                    UUID uuid = getUUIDFromString(uuidString);
                    Main.info("Marker SUCCESS in UsernameConverter");
                    return new PlayerData(uuid, username);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Main.info("Mission Failed");
                return null;
        });
    }

    public static CompletableFuture<PlayerData> getPlayerData(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            String uuidString = uuid.toString();
            String url = "https://api.mojang.com/user/profiles/" + uuidString.replace("-", "") + "/names";
            try {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                JSONObject UUIDObject = readJsonFromUrl(url);
                String username = UUIDObject.get("name").toString();
                return new PlayerData(uuid, username);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        });
    }

}
