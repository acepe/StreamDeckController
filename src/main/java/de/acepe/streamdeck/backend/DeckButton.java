package de.acepe.streamdeck.backend;

import de.acepe.streamdeck.device.event.KeyEvent;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import static de.acepe.streamdeck.util.IconHelper.convertImage;
import static de.acepe.streamdeck.util.IconHelper.imageFromText;

public class DeckButton {

    private final List<ButtonBehaviour> behaviours = new ArrayList<>(0);

    private byte[] imageRaw;
    private Image image;
    private String text;

    public DeckButton(String text) {
        setText(text);
    }

    public DeckButton(Image image) {
        setImage(image);
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
        imageRaw = convertImage(image);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
        BufferedImage fromText = imageFromText(text);
        setImage(SwingFXUtils.toFXImage(fromText, null));
    }

    public void addBehaviour(ButtonBehaviour buttonBehaviour) {
        buttonBehaviour.bindButton(this);
        behaviours.add(buttonBehaviour);
    }

    public void removeBehaviour(ButtonBehaviour buttonBehaviour) {
        buttonBehaviour.unbindButton(this);
        behaviours.remove(buttonBehaviour);
    }

    public void clearBehaviours() {
        behaviours.forEach(b -> b.unbindButton(this));
        behaviours.clear();
    }

    public void onButtonPressed(KeyEvent event) {
        behaviours.forEach(b -> b.onButtonPressed(event));
    }

    public void onButtonReleased(KeyEvent event) {
        behaviours.forEach(b -> b.onButtonReleased(event));
    }

    public byte[] getImageRaw() {
        return imageRaw;
    }
}
