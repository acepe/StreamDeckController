package de.acepe.streamdeck;

import java.io.IOException;

public class TestDisplayController {
    public static void main(String[] args) throws IOException, InterruptedException {

        StreamDeck sd = StreamDeckDevices.getInstance().getStreamDeck();
        sd.reset();
        sd.setBrightness((int) (Math.random()*100-1));

        Thread.sleep(2000);
        System.exit(0);
    }
}
