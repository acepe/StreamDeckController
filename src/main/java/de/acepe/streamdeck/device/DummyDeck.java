package de.acepe.streamdeck.device;

import de.acepe.streamdeck.device.event.KeyListener;

public class DummyDeck implements IStreamDeck {

    @Override
    public int getNumberOfKeys() {
        return IStreamDeck.KEY_COUNT;
    }

    @Override
    public void setBrightness(int percent) {

    }

    @Override
    public int getBrightness() {
        return 0;
    }

    @Override
    public void reset() {

    }

    @Override
    public void setLogo() {

    }

    @Override
    public void dispose() {

    }

    @Override
    public void setKeyBitmap(int id, byte[] bmdata) {

    }

    @Override
    public void addKeyListener(KeyListener listener) {

    }

    @Override
    public void removeKeyListener(KeyListener listener) {

    }

    @Override
    public String getDeviceName() {
        return null;
    }

    @Override
    public String getDeviceId() {
        return null;
    }

    @Override
    public String getSerialnumber() {
        return "-1";
    }

    @Override
    public void open() {

    }
}
