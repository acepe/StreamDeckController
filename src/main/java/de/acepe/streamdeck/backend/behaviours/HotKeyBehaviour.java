package de.acepe.streamdeck.backend.behaviours;

import de.acepe.streamdeck.device.event.KeyEvent;

import javax.inject.Inject;
import java.awt.*;

public class HotKeyBehaviour extends BasicButtonBehaviour {

    private final Robot robot;

    private Integer keycode;
    private Integer modifier1;
    private Integer modifier2;

    @Inject
    public HotKeyBehaviour(Robot robot) {
        this.robot = robot;
    }

    public void setKey(Integer keycode) {
        this.keycode = keycode;
    }

    public void setModifier1(Integer keycode) {
        this.modifier1 = keycode;
    }

    public void setModifier2(Integer keycode) {
        this.modifier2 = keycode;
    }

    @Override
    public void onButtonPressed(KeyEvent event) {

    }

    @SuppressWarnings("MethodWithMoreThanThreeNegations")
    @Override
    public void onButtonReleased(KeyEvent event) {
        if (modifier1 != null) {
            robot.keyPress(modifier1);
        }
        if (modifier2 != null) {
            robot.keyPress(modifier2);
        }
        if (keycode != null) {
            robot.keyPress(keycode);
            robot.keyRelease(keycode);
        }
        if (modifier1 != null) {
            robot.keyRelease(modifier1);
        }
        if (modifier2 != null) {
            robot.keyRelease(modifier2);
        }
    }

    public Integer getKeycode() {
        return keycode;
    }

    public Integer getModifier1() {
        return modifier1;
    }

    public Integer getModifier2() {
        return modifier2;
    }
}
