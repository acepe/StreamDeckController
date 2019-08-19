package de.acepe.streamdeck;

import com.google.inject.Guice;
import com.google.inject.Injector;
import de.acepe.streamdeck.app.AppModule;
import de.acepe.streamdeck.device.IStreamDeck;
import de.acepe.streamdeck.device.event.KeyEvent;
import javafx.application.Application;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

import static de.acepe.streamdeck.util.IconHelper.*;

public class TestDisplayController {

    private static final Logger LOG = LoggerFactory.getLogger(TestDisplayController.class);
    private static Injector injector;

    public static void main(String[] args) throws IOException, InterruptedException, AWTException {
        Random rnd = new Random();
        Robot robot = new Robot();

        BufferedImage img = ImageIO.read(TestDisplayController.class.getResource("icon1.png"));

        injector = Guice.createInjector(new AppModule(new Application() {
            @Override
            public void start(Stage primaryStage) throws Exception {

            }
        }, TestDisplayController::getInjector));
        IStreamDeck sd = injector.getInstance(IStreamDeck.class);
        sd.reset();
        sd.setBrightness(rnd.nextInt(99));

        sd.setKeyBitmap(1, convertImage(img));

        sd.addKeyListener(event -> {
            LOG.info("Key Changed: {} - {}", event.getKeyId(), event.getType());

            if (event.getType() == KeyEvent.Type.RELEASED) {
                if (event.getKeyId() == 0) {
                    sd.reset();
                }
                if (event.getKeyId() == 1) {
                    sd.setKeyBitmap(1, convertImage(img));
                }
                if (event.getKeyId() == 2) {
                    sd.setBrightness(rnd.nextInt(99));
                }
                if (event.getKeyId() == 3) {
                    sd.setKeyBitmap(3, createColourBitmap(
                            (byte) rnd.nextInt(255),
                            (byte) rnd.nextInt(255),
                            (byte) rnd.nextInt(255))
                    );
                }
                if (event.getKeyId() == 4) {
                    sd.setKeyBitmap(4, convertImage(createDefaultImage()));
                }

                if (event.getKeyId() == 5) {
                    // Simulate a key press
                    robot.keyPress(java.awt.event.KeyEvent.VK_CONTROL);
                    robot.keyPress(java.awt.event.KeyEvent.VK_F1);
                    robot.keyRelease(java.awt.event.KeyEvent.VK_F1);
                    robot.keyRelease(java.awt.event.KeyEvent.VK_CONTROL);
                }
            }
        });

        while (true) {
            Thread.sleep(1);
        }
    }

    public static Injector getInjector() {
        return injector;
    }
}
