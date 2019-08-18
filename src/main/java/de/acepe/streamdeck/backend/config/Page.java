package de.acepe.streamdeck.backend.config;

import de.acepe.streamdeck.backend.DeckButton;
import de.acepe.streamdeck.device.IStreamDeck;
import de.acepe.streamdeck.device.event.KeyEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;

import static de.acepe.streamdeck.device.event.KeyEvent.Type.RELEASED;

public class Page {

    private final List<DeckButton> buttons = new ArrayList<>(15);

    private String title;

    private Consumer<Integer> updateCallback;
    private IStreamDeck deck;

    public Page(String title) {
        this.title = title;
        IntStream.rangeClosed(0, 14).forEach(i -> buttons.add(null));
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

    public void updateButton(int index) {
        deck.setKeyBitmap(index, buttons.get(index).getImageRaw());
        if (updateCallback != null) {
            updateCallback.accept(index);
        }
    }

    public void fireActionFromUI(int index, IStreamDeck deck) {
        DeckButton deckButton = buttons.get(index);
        if (deckButton == null) {
            return;
        }
        deckButton.onButtonReleased(new KeyEvent(deck, index, RELEASED));
    }

    public void unbindDeckManager() {
        forAllButtonsDo(index -> buttons.get(index).clearBehaviours());
        deck = null;
        updateCallback = null;

    }

    private void forAllButtonsDo(IntConsumer callable) {
        IntStream.rangeClosed(0, 14).filter(i -> getButton(i) != null).forEach(callable);
    }

    public void bindDeckManager(IStreamDeck deck, Consumer<Integer> updateCallback) {
        this.deck = deck;
        this.updateCallback = updateCallback;
    }

}