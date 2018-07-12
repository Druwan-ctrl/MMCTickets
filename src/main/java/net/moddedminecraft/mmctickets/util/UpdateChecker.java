package net.moddedminecraft.mmctickets.util;

import com.google.gson.Gson;
import net.moddedminecraft.mmctickets.Main;
import net.moddedminecraft.mmctickets.config.Config;
import net.moddedminecraft.mmctickets.config.Messages;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;


public class UpdateChecker {
    Main plugin;
    private String currentVersion = "0";
    private String recommendedVersion = "0";

    public UpdateChecker(Main plugin, String version) {
        this.plugin = plugin;
        this.currentVersion = version;
    }

    private static String readUrl(String urlString) throws Exception {
        BufferedReader reader = null;
        try {
            URL url = new URL(urlString);
            reader = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuffer buffer = new StringBuffer();
            int read;
            char[] chars = new char[1024];
            while ((read = reader.read(chars)) != -1)
                buffer.append(chars, 0, read);

            return buffer.toString();
        } finally {
            if (reader != null)
                reader.close();
        }
    }

    static class Answer
    {
        Recommended recommended;
    }

    static class Recommended {
        String name;
    }


    public void main() throws Exception {
        Gson gson = new Gson();
        String json = readUrl("https://ore.spongepowered.org/api/v1/projects/mmctickets");

        Answer answer = gson.fromJson(json, Answer.class);

        recommendedVersion = answer.recommended.name;
    }

    public void startUpdateCheck() {
        if (Config.checkForUpdate) {
            try {
                main();
            } catch (Exception e) {
                plugin.getLogger().info("Update check failed. URL Invalid.");
            }

            String[] recSplit = recommendedVersion.split("[.]", 3);
            int recMajor = Integer.parseInt(recSplit[0]);
            int recMinor = Integer.parseInt(recSplit[1]);
            int recPatch = Integer.parseInt(recSplit[2]);

            String[] curSplit = currentVersion.split("[.]", 3);
            int curMajor = Integer.parseInt(curSplit[0]);
            int curMinor = Integer.parseInt(curSplit[1]);
            int curPatch = Integer.parseInt(curSplit[2]);

            if (recPatch > curPatch && recMinor >= curMinor && recMajor >= curMajor) {
                plugin.getLogger().info(Messages.getPluginOutdated(recommendedVersion).toPlain());
            }
        }
    }

    public void startUpdateCheckPlayer(Player player) {
        if (Config.checkForUpdate) {
            try {
                main();
            } catch (Exception e) {
                e.printStackTrace();
            }

            String[] recSplit = recommendedVersion.split("[.]", 3);
            int recMajor = Integer.parseInt(recSplit[0]);
            int recMinor = Integer.parseInt(recSplit[1]);
            int recPatch = Integer.parseInt(recSplit[2]);

            String[] curSplit = currentVersion.split("[.]", 3);
            int curMajor = Integer.parseInt(curSplit[0]);
            int curMinor = Integer.parseInt(curSplit[1]);
            int curPatch = Integer.parseInt(curSplit[2]);

            if (recPatch > curPatch && recMinor >= curMinor && recMajor >= curMajor) {
                String text1 = Messages.getChatprefix().toPlain();
                String text2 = Messages.getPluginOutdated(recommendedVersion).toPlain();
                player.sendMessage(Text.of(text1 + text2));
            }
        }
    }
}
