package de.acepe.streamdeck;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class TestDisplayController {
    private static final Logger LOG = LoggerFactory.getLogger(TestDisplayController.class);

    public static void main(String[] args) throws IOException, InterruptedException {

        StreamDeck sd = StreamDeckDevices.getInstance().getStreamDeck();
        sd.reset();
        sd.setBrightness((int) (Math.random() * 100 - 1));

        sd.addKeyListener(event -> LOG.info("Key Changed: {} - {}" , event.getKeyId(), event.getType()));

        while (true) {
            Thread.sleep(1);
        }
    }
}
