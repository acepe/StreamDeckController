package de.acepe.streamdeck.backend.config;

import com.google.gson.annotations.Expose;
import de.acepe.streamdeck.backend.DeckManager;
import de.acepe.streamdeck.device.IStreamDeck;
import de.acepe.streamdeck.device.event.KeyEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;

import static de.acepe.streamdeck.device.event.KeyEvent.Type.RELEASED;
import static java.util.UUID.randomUUID;

public class Page {
    @Expose
    private final List<DeckButton> buttons = new ArrayList<>(15);

    @Expose
    private String id = randomUUID().toString();
    @Expose
    private String title;

    private DeckManager deckManager;

    public Page(String title) {
        this.title = title;
        IntStream.rangeClosed(0, 14).forEach(i -> buttons.add(new DeckButton()));
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void addButton(DeckButton deckButton) {
        buttons.add(deckButton);
    }

    public List<DeckButton> getButtons() {
        return buttons;
    }

    public DeckButton getButton(int index) {
        return buttons.get(index);
    }

    public void addButton(int index, DeckButton button) {
        if (index < 0 || index > 14) {
            throw new IllegalArgumentException("Index must be between 0 and 14");
        }
        buttons.set(index, button);
        button.setIndex(index);
    }

    public void removeButton(int index) {
        DeckButton removed = buttons.remove(index);
        removed.setIndex(-1);
    }

    public void update() {
        forAllButtonsDo(this::updateButton);
    }

    public void syncButtonImages() {
        forAllButtonsDo(this::syncButtonImage);
    }

    private void syncButtonImage(int index) {
        DeckButton deckButton = buttons.get(index);
        deckButton.setImage(deckButton.getImage());
    }

    public void updateButton(int index) {
        deckManager.getDeck().setKeyBitmap(index, buttons.get(index).getImageRaw());
        deckManager.updateButtonUi(index);
    }

    public void fireActionFromUI(int index, IStreamDeck deck) {
        DeckButton deckButton = buttons.get(index);
        if (deckButton == null) {
            return;
        }
        deckButton.onButtonReleased(new KeyEvent(deck, index, RELEASED));
    }

    private void forAllButtonsDo(IntConsumer callable) {
        IntStream.rangeClosed(0, 14).filter(i -> getButton(i) != null).forEach(callable);
    }

    public void bindDeckManager(DeckManager deckManager) {
        this.deckManager = deckManager;
        IntStream.rangeClosed(0, 14).mapToObj(buttons::get).forEach(b -> b.bindDeckManager(deckManager));
    }

    public void unbindDeckManager() {
        deckManager = null;
        IntStream.rangeClosed(0, 14).mapToObj(buttons::get).forEach(DeckButton::unbindDeckManager);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}