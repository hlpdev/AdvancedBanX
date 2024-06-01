package net.hnt8.advancedban.manager;

import net.hnt8.advancedban.MethodInterface;
import net.hnt8.advancedban.Universal;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;


/**
 * The Update Manager used to keep config files up to date and migrate them seamlessly to the newest version.
 */
public class UpdateManager {

    private static UpdateManager instance = null;

    /**
     * Get the update manager.
     *
     * @return the update manager instance
     */
    public static synchronized UpdateManager get() {
        return instance == null ? instance = new UpdateManager() : instance;
    }

    /**
     * Initially checks which configuration options from the newest version are missing and tries to add them
     * without altering any old configuration settings.
     */
    public void setup() {
        MethodInterface mi = Universal.get().getMethods();

        if (mi.isUnitTesting()) {
        }
    }

    private void addMessage(String search, String insert, int indexOffset) {
        try {
            File file = new File(Universal.get().getMethods().getDataFolder(), "Messages.yml");
            List<String> lines = FileUtils.readLines(file, "UTF8");
            int index = lines.indexOf(search);
            lines.add(index + indexOffset, insert);
            FileUtils.writeLines(file, "UTF8", lines);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
