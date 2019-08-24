package de.acepe.streamdeck.backend.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import de.acepe.streamdeck.backend.config.behaviours.*;
import de.acepe.streamdeck.backend.config.json.ImageAdapter;
import de.acepe.streamdeck.backend.config.json.RuntimeTypeAdapterFactory;
import javafx.scene.image.Image;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Comparator.comparing;

public class Persistence {
    private static final Logger LOG = LoggerFactory.getLogger(Persistence.class);

    private static final Page EMPTY_PAGE = new Page("empty");
    private static final String APP_DIR = "deck";
    private static final String PROFILES_DIR = "profiles";
    private static final String SETTINGS_FILE = "settings.json";

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

    public Persistence() {
        createAppDir();
    }

    public Profile createDefaultProfile() {
        Profile defaultProfile = new Profile();
        defaultProfile.setName("Default");
        defaultProfile.addPage(EMPTY_PAGE);
        return defaultProfile;
    }

    public List<Profile> loadAllProfiles() {
        Path profilesDir = getProfilesDir();

        try (Stream<Path> files = Files.walk(profilesDir)) {
            return files.filter(Files::isRegularFile)
                        .map(p -> p.getFileName().toString())
                        .map(this::loadProfile)
                        .sorted(comparing(Profile::getName))
                        .collect(Collectors.toList());
        } catch (IOException e) {
            LOG.error("Couldn't read profiles from {}", profilesDir, e);
        }
        return new ArrayList<>(0);
    }

    public Profile loadProfile(String profileName) {
        File file = getProfileFile(profileName).toFile();
        if (!file.exists()) {
            LOG.error("Profile file {} does not exist", file);
            return createDefaultProfile();
        }

        try (Reader jsonReader = new InputStreamReader(new FileInputStream(file), UTF_8)) {
            Profile profile = gson.fromJson(jsonReader, Profile.class);
            profile.syncPages();
            return profile;
        } catch (IOException | JsonSyntaxException | JsonIOException e) {
            LOG.error("Couldn't read page file: {}", file, e);
            return createDefaultProfile();
        }
    }

    public void saveProfile(Profile profile) {
        String pageJson = gson.toJson(profile);

        Path pageFile = getProfileFile(profile.getName());
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(pageFile.toFile()), UTF_8)) {
            LOG.info("Writing page to file {}", pageFile);
            writer.write(pageJson);
        } catch (Exception e) {
            LOG.error("Couldn't write page to file {}", pageFile);
        }
    }

    public void saveSettings(Settings settings) {
        String settingsJson = gson.toJson(settings);

        Path settingsFile = getSettingsFile();
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(settingsFile.toFile()), UTF_8)) {
            LOG.info("Writing settings to file {}", settingsFile);
            writer.write(settingsJson);
        } catch (Exception e) {
            LOG.error("Couldn't write settings to file {}", settingsFile);
        }
    }

    public Settings loadSettings() {
        File file = getSettingsFile().toFile();
        if (!file.exists()) {
            LOG.error("Settings file {} does not exist", file);
            return new Settings();
        }

        try (Reader jsonReader = new InputStreamReader(new FileInputStream(file), UTF_8)) {
            return gson.fromJson(jsonReader, Settings.class);
        } catch (IOException | JsonSyntaxException | JsonIOException e) {
            LOG.error("Couldn't read page file: {}", file, e);
            return new Settings();
        }
    }

    private Path getSettingsFile() {
        return getAppDir().resolve(SETTINGS_FILE);
    }

    private Path getProfileFile(String profileName) {
        return getProfilesDir().resolve(profileName);
    }

    private Path getProfilesDir() {
        return getAppDir().resolve(PROFILES_DIR);
    }

    private Path getAppDir() {
        return Paths.get(System.getProperty("user.home"), APP_DIR);
    }

    private void createAppDir() {
        File appDir = getAppDir().toFile();
        if (!appDir.exists()) {
            LOG.debug("creating app directory {}", appDir);
            if (!appDir.mkdir()) {
                LOG.error("Couldn't create app directory: {}", appDir);
            }
        }
    }

}
