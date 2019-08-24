package de.acepe.streamdeck.backend.config;

import com.google.gson.annotations.Expose;
import de.acepe.streamdeck.backend.config.behaviours.ButtonBehaviour;
import de.acepe.streamdeck.backend.DeckManager;
import de.acepe.streamdeck.device.event.KeyEvent;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static de.acepe.streamdeck.util.IconHelper.convertImage;
import static de.acepe.streamdeck.util.IconHelper.imageFromText;

public class DeckButton {
    @Expose
    private final List<ButtonBehaviour> behaviours = new ArrayList<>(0);
    @Expose
    private String text;
    @Expose
    private int index;
    private byte[] imageRaw;
    @Expose
    private Image image;

    public DeckButton() {
    }

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
        if (image != null) {
            imageRaw = convertImage(image);
        }else{
            imageRaw=null;
        }
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        setText(text, null);
    }

    public void setText(String text, Integer fontSize) {
        this.text = text;
        BufferedImage fromText = imageFromText(text, fontSize);
        setImage(SwingFXUtils.toFXImage(fromText, null));
    }

    public final void addBehaviour(ButtonBehaviour... buttonBehaviour) {
        Arrays.stream(buttonBehaviour).forEach(t -> {
                    t.bindButton(this);
                    behaviours.add(t);
                }
        );
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

    public void setIndex(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public void bindDeckManager(DeckManager deckManager) {
        behaviours.forEach(b -> b.bindDeckManager(deckManager));
    }

    public void unbindDeckManager() {
        behaviours.forEach(ButtonBehaviour::unbindDeckManager);
    }
}
