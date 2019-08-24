package de.acepe.streamdeck.backend.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.acepe.streamdeck.backend.config.behaviours.*;
import de.acepe.streamdeck.backend.config.json.ImageAdapter;
import de.acepe.streamdeck.backend.config.json.RuntimeTypeAdapterFactory;
import javafx.scene.image.Image;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

import static java.nio.charset.StandardCharsets.UTF_8;

public class Persistence {
    private static final Logger LOG = LoggerFactory.getLogger(Persistence.class);
    private static final Page EMPTY_PAGE = new Page("empty");
    private static final String APP_DIR = "deck";

    private final RuntimeTypeAdapterFactory<ButtonBehaviour> typeFactory = RuntimeTypeAdapterFactory
            .of(ButtonBehaviour.class, "type")
            .registerSubtype(ExecuteProgrammBehaviour.class)
            .registerSubtype(HotKeyBehaviour.class)
            .registerSubtype(OpenLocationBehaviour.class)
            .registerSubtype(ShowPageBehaviour.class)
            .registerSubtype(SleepBehaviour.class)
            .registerSubtype(StreamDeckToggleBrightnessBehavior.class);

    private final Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation()
                                               .setPrettyPrinting()
                                               .registerTypeAdapterFactory(typeFactory)
                                               .registerTypeAdapter(Image.class, new ImageAdapter())
                                               .create();

    public Page getEmptyPage() {
        return EMPTY_PAGE;
    }

    public Page loadPage(String uuid) {
        File file = new File(getPageFile(uuid));
        if (!file.exists()) {
            return EMPTY_PAGE;
        }

        try (Reader jsonReader = new InputStreamReader(new FileInputStream(file), UTF_8)) {
            Page page = gson.fromJson(jsonReader, Page.class);
            page.syncButtonImages();
            return page;
        } catch (IOException e) {
            LOG.error("Couldn't read data file: {}", file, e);
            return EMPTY_PAGE;
        }
    }

    public void savePage(Page page) {
        String settingsJson = gson.toJson(page);
        createAppDir();

        String pageFile = getPageFile(page.getId());
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(pageFile), UTF_8)) {
            LOG.info("Writing page to file {}", pageFile);
            writer.write(settingsJson);
        } catch (Exception e) {
            LOG.error("Couldn't write page to file {}", pageFile);
        }
    }

    private String getPageFile(String id) {
        return getAppDir() + File.separator + id;
    }

    private String getAppDir() {
        return System.getProperty("user.home") + File.separator + APP_DIR;
    }

    private void createAppDir() {
        String path = getAppDir();
        File appDir = new File(path);
        if (!appDir.exists()) {
            LOG.debug("creating app directory {}", path);
            if (!appDir.mkdir()) {
                LOG.error("Couldn't create app directory: {}", path);
            }
        }
    }
}
