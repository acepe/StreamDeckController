package de.acepe.streamdeck;

import com.google.inject.Guice;
import com.google.inject.Injector;
import de.acepe.streamdeck.app.AppModule;
import de.acepe.streamdeck.app.ScreenManager;
import de.acepe.streamdeck.app.Screens;
import de.acepe.streamdeck.backend.DeckManager;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

public class StreamDeckApp extends Application {

    private static final Logger LOG = LoggerFactory.getLogger(StreamDeckApp.class);

    @Inject
    private ScreenManager screenManager;
    @Inject
    private DeckManager deckManager;

    private Injector injector;

    @Override
    public void start(Stage primaryStage) {
        System.setProperty("prism.lcdtext", "false");
        System.setProperty("https.protocols", "TLSv1.2");

        injector = Guice.createInjector(new AppModule(this, this::getInjector));
        injector.injectMembers(this);

        screenManager.loadScreen(Screens.SETTINGS);
        screenManager.loadScreen(Screens.MAIN);

        BorderPane root = new BorderPane();
        root.setCenter(screenManager);

        Screens startScreen = Screens.MAIN;
        Scene scene = new Scene(root, startScreen.getWidth(), startScreen.getHeight());
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());

        primaryStage.setScene(scene);
        primaryStage.setX(1160);
        primaryStage.show();
        primaryStage.setIconified(true);
        primaryStage.setIconified(false);

        primaryStage.setOnCloseRequest((event -> screenManager.closeStages()));

        screenManager.setScreen(startScreen);
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        deckManager.stop();
    }

    public static void main(String[] args) {
        launch(args);
    }

    Injector getInjector() {
        return injector;
    }
}
