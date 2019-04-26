package de.acepe.streamdeck;

import org.hid4java.HidDevice;
import org.hid4java.HidManager;
import org.hid4java.HidServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * <br><br>
 * <p>
 * MIT License
 * <p>
 * Copyright (c) 2017 Roland von Werden
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 * @author Roland von Werden, acepe
 * @version 0.1
 */
public final class StreamDeckDevices {

    private static final Logger LOG = LoggerFactory.getLogger(StreamDeckDevices.class);

    private static final Integer VENDOR_ID = 0x0FD9;
    private static final Integer PRODUCT_ID = 0x0060;
    public static final String SERIAL_NUMBER = null;

    private final List<IStreamDeck> streamDecks = new ArrayList<>(0);

    private static StreamDeckDevices instance;

    public static StreamDeckDevices getInstance() {
        if (instance == null) {
            instance = new StreamDeckDevices();
        }
        return instance;
    }

    private StreamDeckDevices() {
        initDecks();
    }

    private void initDecks() {
        streamDecks.clear();
        streamDecks.addAll(getAllStreamDecks());
    }

//    /**
//     * Returns the first Stream Deck found. Will be ready to use as will already have been prepared!
//     *
//     * @return
//     */
//    public static IStreamDeck getFirstStreamDeck() {
//        HidServices hidServices = HidManager.getHidServices();
//        hidServices.start();
//
//        HidDevice hidDevice = hidServices.getHidDevice(VENDOR_ID, PRODUCT_ID, SERIAL_NUMBER);
//        return hidDevice == null ? null : new StreamDeck(hidDevice);
//    }

    private ArrayList<IStreamDeck> getAllStreamDecks() {
        LOG.info("Scanning for devices");

        HidServices hidServices = HidManager.getHidServices();
        hidServices.start();

        ArrayList<IStreamDeck> ret = new ArrayList<>();
        for (HidDevice device : hidServices.getAttachedHidDevices()) {
            LOG.debug("Vendor-ID: " + device.getVendorId() + ", Product-ID: " + device.getProductId());
            if (device.getVendorId() == VENDOR_ID && device.getProductId() == PRODUCT_ID) {
                LOG.info("  Manufacurer: " + device.getManufacturer());
                LOG.info("  Product:     " + device.getProduct());
                LOG.info("  Device-Id:   " + device.getId());
                LOG.info("  Serial-No:   " + device.getSerialNumber());
                LOG.info("  Path:        " + device.getPath());
                LOG.info("");
                ret.add(new StreamDeck(device));
            }
        }
        return ret;
    }

    public IStreamDeck getStreamDeck() {
        return getStreamDeck(0);
    }

    public IStreamDeck getStreamDeck(int id) {
        if (id < 0 || id >= getStreamDeckSize()) {
            return null;
        }
        return streamDecks.get(id);
    }

    public int getStreamDeckSize() {
        return streamDecks.size();
    }
}
 