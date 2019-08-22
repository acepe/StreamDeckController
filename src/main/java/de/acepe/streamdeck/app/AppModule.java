package de.acepe.streamdeck.app;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Names;
import de.acepe.streamdeck.backend.DeckManager;
import de.acepe.streamdeck.device.StreamDeckDevices;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;

import java.awt.*;
import java.util.function.Supplier;

public class AppModule extends AbstractModule {

    private static final String APP_TITLE = "Stream Deck";
    private final Application application;
    private final Supplier<Injector> injectorSupplier;

    public AppModule(Application application, Supplier<Injector> injectorSupplier) {
        this.application = application;
        this.injectorSupplier = injectorSupplier;
    }

    @Override
    protected void configure() {
        bind(Application.class).toInstance(application);
        bind(String.class).annotatedWith(Names.named("APP_TITLE")).toInstance(APP_TITLE);

        bind(ScreenManager.class).in(Singleton.class);
        bind(StreamDeckDevices.class).in(Singleton.class);
        bind(DeckManager.class).in(Singleton.class);

        bind(Robot.class).in(Singleton.class);

//        FactoryModuleBuilder builder = new FactoryModuleBuilder();
//        install(builder.build(OnDemandStreamFactory.class));
    }

    @Provides
    public FXMLLoader createFXMLLoader() {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setControllerFactory(injectorSupplier.get()::getInstance);
        return fxmlLoader;
    }

}
