package de.acepe.streamdeck.device;

import de.acepe.streamdeck.device.event.KeyListener;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class StreamDeck implements IStreamDeck {

    private final List<KeyListener> keyListeners = new ArrayList<>(0);

    private final StreamDeckDevices streamDeckDevices;
    private final DummyDeck dummyDeck;

    @Inject
    public StreamDeck(StreamDeckDevices streamDeckDevices, DummyDeck dummyDeck) {
        this.streamDeckDevices = streamDeckDevices;
        this.dummyDeck = dummyDeck;
    }

    @Override
    public int getNumberOfKeys() {
        return IStreamDeck.KEY_COUNT;
    }

    @Override
    public void setBrightness(int percent) {
        IStreamDeck streamDeck = streamDeckDevices.getStreamDeck();
        if (streamDeck == null) {
            dummyDeck.setBrightness(percent);
        } else {
            streamDeck.setBrightness(percent);
        }
    }

    @Override
    public int getBrightness() {
        IStreamDeck streamDeck = streamDeckDevices.getStreamDeck();
        return streamDeck == null ? dummyDeck.getBrightness() : streamDeck.getBrightness();
    }

    @Override
    public void reset() {
        IStreamDeck streamDeck = streamDeckDevices.getStreamDeck();
        if (streamDeck == null) {
            dummyDeck.reset();
        } else {
            streamDeck.reset();
        }
    }

    @Override
    public void setLogo() {
        IStreamDeck streamDeck = streamDeckDevices.getStreamDeck();
        if (streamDeck == null) {
            dummyDeck.setLogo();
        } else {
            streamDeck.setLogo();
        }
    }

    @Override
    public void dispose() {
        IStreamDeck streamDeck = streamDeckDevices.getStreamDeck();
        if (streamDeck == null) {
            dummyDeck.dispose();
        } else {
            streamDeck.dispose();
        }
    }

    @Override
    public void setKeyBitmap(int id, byte[] bmdata) {
        IStreamDeck streamDeck = streamDeckDevices.getStreamDeck();
        if (streamDeck == null) {
            dummyDeck.setKeyBitmap(id, bmdata);
        } else {
            streamDeck.setKeyBitmap(id, bmdata);
        }
    }

    @Override
    public void addKeyListener(KeyListener listener) {
        if (listener == null) {
            return;
        }
        synchronized (keyListeners) {
            keyListeners.add(listener);
        }
        IStreamDeck streamDeck = streamDeckDevices.getStreamDeck();
        if (streamDeck == null) {
            dummyDeck.addKeyListener(listener);
        } else {
            streamDeck.addKeyListener(listener);
        }
    }

    @Override
    public void removeKeyListener(KeyListener listener) {
        if (listener == null) {
            return;
        }
        synchronized (keyListeners) {
            keyListeners.remove(listener);
        }

        IStreamDeck streamDeck = streamDeckDevices.getStreamDeck();
        if (streamDeck == null) {
            dummyDeck.removeKeyListener(listener);
        } else {
            streamDeck.removeKeyListener(listener);
        }
    }

    public void reconnect() {
        streamDeckDevices.discoverDevices();
        IStreamDeck streamDeck = streamDeckDevices.getStreamDeck();
        if (streamDeck != null) {
            keyListeners.forEach(streamDeck::addKeyListener);
        }
    }

}
