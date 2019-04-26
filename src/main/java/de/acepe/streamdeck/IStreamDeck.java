package de.acepe.streamdeck;

import de.acepe.streamdeck.event.KeyListener;

/**
 * An interface to a stream deck.
 */
public interface IStreamDeck {
    /**
     * Returns the number of keys. There is only one Stream Deck released currently, but implemented in case future Stream Decks are released.
     *
     * @return
     */
    int getNumberOfKeys();

    /**
     * Sets the brightness of the Elgato's LCD display.
     *
     * @param percent The percent.
     */
    void setBrightness(int percent);

    /**
     * Instructs the Elgato Stream Deck to display the Elgato logo.
     */
    void reset();

    /**
     * Closes device and instructs it to go back to the logo. Not strictly necessary, but nice to have!
     */
    void dispose();

    /**
     * Sets a byte array to a key. Inputted data _MUST_ be 72x72.
     *
     * @param id     The ID of the key
     * @param bmdata A byte array containing the bitmap. _MUST_ be 72x72.
     */
    void setKeyBitmap(int id, byte[] bmdata);

    /**
     * Use this to register a key listener. Listener task doesn't listen unless at least one listener is registered.
     *
     * @param listener
     */
    void addKeyListener(KeyListener listener);

    /**
     * Use this to remove a previously registered key listener
     *
     * @param listener A listener you want to unregister
     */
    void removeKeyListener(KeyListener listener);
}
