package de.acepe.streamdeck;

import de.acepe.streamdeck.event.KeyEvent;
import de.acepe.streamdeck.event.KeyListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import purejavahidapi.HidDevice;
import purejavahidapi.InputReportListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static de.acepe.streamdeck.event.KeyEvent.Type;

public class StreamDeck implements InputReportListener {
    private static final Logger LOG = LoggerFactory.getLogger(StreamDeck.class);

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
    private static final int KEY_DOWN_VALUE = 0x01;

    /**
     * Brightness command for this instance.
     */
    private final byte[] brightness = Arrays.copyOf(BRIGHTNES_DATA, BRIGHTNES_DATA.length);

    /**
     * current values if a key on a certain index is pressed or not
     */
    private final boolean[] keysPressed = new boolean[15];

    /**
     * Registered Listeners to the {@link KeyEvent}s created by the ESD
     */
    private final List<KeyListener> keyListeners = new ArrayList<>(0);

    private final HidDevice hidDevice;
    private final ExecutorService commandDispatcher;
    private final ExecutorService eventDispatcher;

    public StreamDeck(HidDevice hidDevice) {
        this.hidDevice = hidDevice;
        this.brightness[5] = DEFAULT_BRIGHTNESS;

        commandDispatcher = Executors.newFixedThreadPool(1, r -> new Thread(r, "ESD-Command-Dispatch-Thread"));
        eventDispatcher = Executors.newFixedThreadPool(1, r -> new Thread(r, "ESD-Event-Dispatch-Thread"));

        hidDevice.setInputReportListener(this);
    }

    @Override
    public void onInputReport(HidDevice source, byte reportID, byte[] reportData, int reportLength) {
        if (reportID != 1) {
            return;
        }

        for (int i = 0; i < 15 && i < reportLength; i++) {
            boolean keyPressed = reportData[i] == KEY_DOWN_VALUE;
            if (keysPressed[i] != keyPressed) {
                fireKeyChangedEvent(i, keyPressed);
                keysPressed[i] = keyPressed;
            }
        }
    }

    private void fireKeyChangedEvent(int keyIndex, boolean pressed) {
        eventDispatcher.submit(() -> notifyKeyListeners(keyIndex, pressed));
    }

    private void notifyKeyListeners(int keyIndex, boolean pressed) {
        Type eventType = pressed ? Type.PRESSED : Type.RELEASED;
        keyListeners.forEach(listener -> listener.onKeyEvent(new KeyEvent(this, keyIndex, eventType)));
    }

    /**
     * Queues a task to reset the stream deck.
     */
    public void reset() {
        commandDispatcher.submit(this::_reset);
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
        commandDispatcher.submit(this::_updateBrightnes);
    }

    /**
     * Adds a KeyListener to the ESD. WHenever an event is generated, the Listener will be informed.
     *
     * @param listener Listener to be added
     */
    public void addKeyListener(KeyListener listener) {
        if (listener == null) {
            return;
        }
        keyListeners.add(listener);
    }

    /**
     * Removes a KeyListener from the ESD.
     *
     * @param listener Listener to be removed
     */
    public void removeKeyListener(KeyListener listener) {
        if (listener == null) {
            return;
        }
        keyListeners.remove(listener);
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
