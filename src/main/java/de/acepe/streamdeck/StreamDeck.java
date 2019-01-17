package de.acepe.streamdeck;

import purejavahidapi.HidDevice;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StreamDeck {
    private static final byte DEFAULT_BRIGHTNESS = 99;

    /**
     * Reset command
     */
    private final static byte[] RESET_DATA = new byte[]{0x0B, 0x63, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

    /**
     * Brightness command
     */
    private final static byte[] BRIGHTNES_DATA = new byte[]{0x05, 0x55, (byte) 0xAA, (byte) 0xD1, 0x01, 0x63, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};

    /**
     * Brightness command for this instance.
     */
    private final byte[] brightness = Arrays.copyOf(BRIGHTNES_DATA, BRIGHTNES_DATA.length);

    private final HidDevice hidDevice;
    private final ExecutorService dispatcher;

    public StreamDeck(HidDevice hidDevice) {
        this.hidDevice = hidDevice;
        this.brightness[5] = DEFAULT_BRIGHTNESS;

        dispatcher = Executors.newFixedThreadPool(1, r -> new Thread(r, "ESD-Dispatch-Thread"));
    }

    /**
     * Queues a task to reset the stream deck.
     */
    public void reset() {
        dispatcher.submit(this::_reset);
        //TODO: update Icons
    }

    /**
     * Sets the desired brightness from 0 - 100 % and queues the change.
     *
     * @param brightness
     */
    public void setBrightness(int brightness) {
        brightness = brightness > 99 ? 99 : brightness < 0 ? 0 : brightness;
        this.brightness[5] = (byte) brightness;
        dispatcher.submit(this::_updateBrightnes);
    }

    /**
     * Sends reset-command to ESD
     */
    private void _reset() {
        hidDevice.setFeatureReport(RESET_DATA, RESET_DATA.length);
    }

    /**
     * Sends brightness-command to ESD
     */
    private void _updateBrightnes() {
        hidDevice.setFeatureReport(this.brightness, this.brightness.length);
    }
}
