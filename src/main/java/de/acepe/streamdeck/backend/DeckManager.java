package de.acepe.streamdeck.backend;

import de.acepe.streamdeck.device.IStreamDeck;
import de.acepe.streamdeck.device.event.KeyEvent;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static de.acepe.streamdeck.device.event.KeyEvent.Type.PRESSED;
import static de.acepe.streamdeck.device.event.KeyEvent.Type.RELEASED;

public class DeckManager {

    private final List<DeckButton> buttons = new ArrayList<>();
    private final IStreamDeck deck;

    @Inject
    DeckManager(IStreamDeck deck) {
        this.deck = deck;

        deck.addKeyListener(this::onDeckEventsReceived);

        IntStream.rangeClosed(0, 14).forEach(i -> buttons.add(null));
    }

    private void onDeckEventsReceived(KeyEvent keyEvent) {
        DeckButton deckButton = buttons.get(keyEvent.getKeyId());
        if (deckButton != null) {
            KeyEvent.Type type = keyEvent.getType();
            if (type == PRESSED) {
                deckButton.onButtonPressed(keyEvent);
            } else if (type == RELEASED) {
                deckButton.onButtonReleased(keyEvent);
            }
        }
    }

    public List<DeckButton> getButtons() {
        return buttons;
    }

    public DeckButton getButton(int i) {
        return buttons.get(i);
    }

    public void addButton(int index, DeckButton button) {
        if (index < 0 || index > 14) {
            throw new IllegalArgumentException("Index must be between 0 and 14");
        }
        buttons.set(index, button);
    }

    public void updateDeck() {
        deck.reset();
        IntStream.rangeClosed(0, 14).filter(i -> buttons.get(i) != null).forEach(this::updateButton);
    }

    private void updateButton(int index) {
        deck.setKeyBitmap(index, buttons.get(index).getImageRaw());
    }

    public void fireActionFromUI(int index) {
        DeckButton deckButton = buttons.get(index);
        if (deckButton == null) {
            return;
        }
        deckButton.onButtonReleased(new KeyEvent(deck, index, RELEASED));
    }
}
