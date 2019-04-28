package de.acepe.streamdeck.backend.behaviours;

import de.acepe.streamdeck.device.event.KeyEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SleepBehaviour extends BasicButtonBehaviour {
    private static final Logger LOG = LoggerFactory.getLogger(SleepBehaviour.class);

    private long millis;

    public void setDurationInMillis(long millis) {
        this.millis = millis;
    }

    @Override
    public void onButtonPressed(KeyEvent event) {

    }

    @Override
    public void onButtonReleased(KeyEvent event) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            LOG.warn("Sleep interrupted", e);
        }
    }

    public long getMillis() {
        return millis;
    }
}
