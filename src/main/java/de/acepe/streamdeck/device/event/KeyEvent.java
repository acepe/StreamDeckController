package de.acepe.streamdeck.device.event;

import de.acepe.streamdeck.device.IStreamDeck;

import java.util.EventObject;

/**
 * Event that represents an event triggered by interacting with the stream deck.
 *
 * <br><br>
 * <p>
 * MIT License<br>
 * <br>
 * Copyright (c) 2017 Roland von Werden<br>
 * <br>
 * Permission is hereby granted, free of charge, to any person obtaining a copy<br>
 * of this software and associated documentation files (the "Software"), to deal<br>
 * in the Software without restriction, including without limitation the rights<br>
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell<br>
 * copies of the Software, and to permit persons to whom the Software is<br>
 * furnished to do so, subject to the following conditions:<br>
 * <br>
 * The above copyright notice and this permission notice shall be included in all<br>
 * copies or substantial portions of the Software.<br>
 * <br>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR<br>
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,<br>
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE<br>
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER<br>
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,<br>
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE<br>
 * SOFTWARE.<br>
 *
 * @author Roland von Werden, acepe
 * @version 0.1
 */
public class KeyEvent extends EventObject {

    /**
     * Id of the key
     */
    private final int keyId;

    /**
     * Type of the event
     */
    private final Type type;

    public KeyEvent(IStreamDeck source, int keyId, Type type) {
        super(source);
        this.keyId = keyId;
        this.type = type;
    }

    /**
     * Returns the Stream Deck for which the event was created
     */
    @Override
    public IStreamDeck getSource() {
        return (IStreamDeck) super.getSource();
    }

    /**
     * Returns the key for which the event was created
     *
     * @return Key id between 0 and 14
     */
    public int getKeyId() {
        return keyId;
    }

    /**
     * Returns the type of the event
     *
     * @return Type of the Event
     */
    public Type getType() {
        return type;
    }

    /**
     * Type of the Key Event
     *
     * @author Roland von Werden
     */
    public enum Type {
        PRESSED, RELEASED, ON_DISPLAY, OFF_DISPLAY, OPEN_FOLDER, CLOSE_FOLDER
    }
}
