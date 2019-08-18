/*
package de.acepe.streamdeck.backend.behaviours;

import de.acepe.streamdeck.backend.ButtonBehaviour;
import de.acepe.streamdeck.backend.DeckButton;
import de.acepe.streamdeck.backend.config.Page;
import de.acepe.streamdeck.device.event.KeyEvent;

import java.util.Timer;
import java.util.TimerTask;

public class AnimatedBehaviour implements ButtonBehaviour {

    private DeckButton deckButton;
    private Timer timer;
    private int currentFrame;
    private int period = 100;
    private Page page;

    public void bindPage(Page page) {
        this.page = page;
    }

    @Override
    public void bindButton(DeckButton deckButton) {
        this.deckButton = deckButton;
        currentFrame = 0;
        restartTimer();
    }

    private void startTimer() {
        timer = new Timer("ESD-Button-Animator[Button-" + deckButton.getIndex() + "]");
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                nextFrame();
            }
        }, 0, period);
    }

    private void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer.purge();
            timer = null;
        }
    }

    private void restartTimer() {
        stopTimer();
        startTimer();
    }

    private void nextFrame() {
        if (currentFrame == 10) {
            currentFrame = 0;
        }
        deckButton.setText(String.valueOf(currentFrame));
        currentFrame++;
        page.updateButton(deckButton.getIndex());
    }

    @Override
    public void unbindButton(DeckButton deckButton) {
        stopTimer();
    }

    @Override
    public void onButtonPressed(KeyEvent event) {

    }

    @Override
    public void onButtonReleased(KeyEvent event) {
        if (timer == null) {
            startTimer();
        } else {
            stopTimer();
        }
    }

    public void setPeriod(int period) {
        this.period = period;
        if (deckButton != null) {
            restartTimer();
        }
    }

    public int getPeriod() {
        return period;
    }

}
*/
