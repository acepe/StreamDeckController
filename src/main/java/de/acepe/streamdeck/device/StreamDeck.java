package de.acepe.streamdeck.device;

import de.acepe.streamdeck.device.event.KeyEvent;
import de.acepe.streamdeck.device.event.KeyListener;
import org.hid4java.HidDevice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static de.acepe.streamdeck.device.event.KeyEvent.Type;

public class StreamDeck implements IStreamDeck {
    private static final Logger LOG = LoggerFactory.getLogger(StreamDeck.class);

    private static final byte DEFAULT_BRIGHTNESS = 99;

    private static final byte[] RESET_DATA = new byte[]{0x0B, 0x63, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    private static final byte[] BRIGHTNES_DATA = new byte[]{0x55, (byte) 0xaa, (byte) 0xd1, 0x01, 0x64, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};

    private static final int KEY_DOWN_VALUE = 0x01;

    private static final int PAGE_PACKET_SIZE = 8191;
    /**
     * Header for Page 1 of the image command
     */
    private static final byte[] PAGE_1_HEADER2 = new byte[]{
            0x02, 0x01, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x42, 0x4d, (byte) 0xf6, 0x3c, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x36, 0x00, 0x00, 0x00, 0x28, 0x00,
            0x00, 0x00, 0x48, 0x00, 0x00, 0x00, 0x48, 0x00,
            0x00, 0x00, 0x01, 0x00, 0x18, 0x00, 0x00, 0x00,
            0x00, 0x00, (byte) 0xc0, 0x3c, 0x00, 0x00, (byte) 0xc4, 0x0e,
            0x00, 0x00, (byte) 0xc4, 0x0e, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00};

    /**
     * Header for Page 2 of the image command
     */
    private static final byte[] PAGE_2_HEADER2 = new byte[]{
            0x02, 0x01, 0x02, 0x00, 0x01, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};

    private final Object disposeLock = new Object();

    /**
     * Brightness command for this instance.
     */
    private final byte[] currentBrightness = Arrays.copyOf(BRIGHTNES_DATA, BRIGHTNES_DATA.length);

    /**
     * current values if a key on a certain index is pressed or not
     */
    private final boolean[] keysPressed = new boolean[15];

    /**
     * Pixels(times 3 to get the amount of bytes) of an icon that can be sent with page 1 of the image command
     */
    public final static int NUM_FIRST_PAGE_PIXELS = 2583;

    /**
     * Pixels(times 3 to get the amount of bytes) of an icon that can be sent with page 2 of the image command
     */
    public final static int NUM_SECOND_PAGE_PIXELS = 2601;

    /**
     * Registered Listeners to the {@link KeyEvent}s created by the ESD
     */
    private final List<KeyListener> keyListeners = new ArrayList<>(0);
    private final ExecutorService commandDispatcher;
    private final ExecutorService eventDispatcher;

    private HidDevice hidDevice;
    private boolean isListening;
    private Thread keyListenTask;
    private boolean disposed;

    public StreamDeck(HidDevice hidDevice) {
        this.hidDevice = hidDevice;
        this.currentBrightness[4] = DEFAULT_BRIGHTNESS;

        commandDispatcher = Executors.newFixedThreadPool(1, r -> new Thread(r, "ESD-Command-Dispatch-Thread"));
        eventDispatcher = Executors.newFixedThreadPool(1, r -> new Thread(r, "ESD-Event-Dispatch-Thread"));

        if (!hidDevice.isOpen()) {
            hidDevice.open();
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
    }

    /**
     * Queues a task to set the logo.
     */
    public void setLogo() {
        commandDispatcher.submit(this::_setLogo);
    }

    /**
     * Returns the number of keys. There is only one Stream Deck released currently, but implemented in case future Stream Decks are released.
     *
     * @return the number of keys
     */
    @Override
    public int getNumberOfKeys() {
        return KEY_COUNT;
    }

    /**
     * Sets the desired brightness from 0 - 99 and queues the change.
     *
     * @param brightness between 0 - 99
     */
    @Override
    public void setBrightness(int brightness) {
        brightness = brightness > 99 ? 99 : brightness < 0 ? 0 : brightness;
        this.currentBrightness[4] = (byte) brightness;
        LOG.debug("Brightness set to {}", brightness);
        commandDispatcher.submit(this::_updateBrightnes);
    }

    public int getBrightness() {
        return this.currentBrightness[4];
    }

    /**
     * Closes device and instructs it to go back to the logo. Not strictly necessary, but nice to have!
     */
    @Override
    public void dispose() {
        synchronized (disposeLock) {
            if (disposed) {
                return;
            }
            disposed = true;
        }

        if (hidDevice == null) {
            return;
        }
        reset();

        stopKeyListenerThread();
        commandDispatcher.shutdownNow();
        eventDispatcher.shutdownNow();

        hidDevice.close();
        hidDevice = null;
    }

    /**
     * Creates a Job to send the give icon to the ESD to be displayed on the given keyIndex
     *
     * @param keyIndex Index of ESD (0..14)
     * @param imgData  Image in BGR format to be displayed
     */
    @Override
    public void setKeyBitmap(int keyIndex, byte[] imgData) {
        eventDispatcher.submit(() -> {
            try {
                _drawImage(keyIndex, imgData);
            } catch (Exception e) {
                LOG.error("Exception in {}", Thread.currentThread().getName(), e);
            }
        });
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
        synchronized (keyListeners) {
            keyListeners.add(listener);
            if (keyListenTask == null) {
                isListening = true;
                keyListenTask = new Thread(this::keyListener);
                keyListenTask.setName("ESD-Key-Listener-Thread");
                keyListenTask.setDaemon(true);
                keyListenTask.start();
            }
        }
    }

    private void keyListener() {
        synchronized (keyListeners) {
            while (isListening) {
                byte[] data = new byte[16];
                hidDevice.read(data, 1000);

                if (data[0] != 0) {
                    for (int i = 0; i < 15; i++) {
                        boolean keyPressed = data[i + 1] == KEY_DOWN_VALUE;
                        if (keysPressed[i] != keyPressed) {
                            fireKeyChangedEvent(i, keyPressed);
                            keysPressed[i] = keyPressed;
                        }
                    }
                }
            }
        }
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
        synchronized (keyListeners) {
            keyListeners.remove(listener);
            if (keyListeners.isEmpty() && keyListenTask != null) {
                stopKeyListenerThread();
            }
        }
    }

    private void stopKeyListenerThread() {
        isListening = false;
        keyListenTask = null;
    }

    /**
     * Sends reset-command to ESD
     */
    private void _reset() {
        LOG.debug("Resetting");
        hidDevice.sendFeatureReport(RESET_DATA, (byte) RESET_DATA.length);
    }

    private void _setLogo() {
        LOG.debug("Setting logo");
        hidDevice.sendFeatureReport(new byte[]{0x63}, (byte) 0x0B);
    }

    /**
     * Sends brightness-command to ESD
     */
    private void _updateBrightnes() {
        LOG.debug("Updating brightness");
        hidDevice.sendFeatureReport(currentBrightness, (byte) 0x05);
    }

    private void _drawImage(int keyIndex, byte[] imgData) {
        LOG.debug("Drawing image for index: " + keyIndex);

        byte[] page1 = generatePage1(keyIndex, imgData);
        byte[] page2 = generatePage2(keyIndex, imgData);
        byte repID1 = page1[0];
        byte repID2 = page2[0];

        page1 = Arrays.copyOfRange(page1, 1, page1.length);
        page2 = Arrays.copyOfRange(page2, 1, page2.length);

        hidDevice.write(page1, page1.length, repID1);
        hidDevice.write(page2, page2.length, repID2);
    }

    private static byte[] generatePage1(int keyId, byte[] imgData) {
        byte[] p1 = new byte[PAGE_PACKET_SIZE];
        System.arraycopy(PAGE_1_HEADER2, 0, p1, 0, PAGE_1_HEADER2.length);

        if (imgData != null) {
            System.arraycopy(imgData, 0, p1, PAGE_1_HEADER2.length, NUM_FIRST_PAGE_PIXELS * KEY_PIXEL_DEPTH);
        }

        p1[5] = (byte) (keyId + 1);
        return p1;
    }

    private static byte[] generatePage2(int keyId, byte[] imgData) {
        byte[] p2 = new byte[PAGE_PACKET_SIZE];
        System.arraycopy(PAGE_2_HEADER2, 0, p2, 0, PAGE_2_HEADER2.length);

        if (imgData != null) {
            System.arraycopy(imgData, NUM_FIRST_PAGE_PIXELS * KEY_PIXEL_DEPTH, p2, PAGE_2_HEADER2.length, NUM_SECOND_PAGE_PIXELS * KEY_PIXEL_DEPTH);
        }

        p2[5] = (byte) (keyId + 1);
        return p2;
    }

}
