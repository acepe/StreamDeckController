package de.acepe.streamdeck;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import purejavahidapi.HidDevice;
import purejavahidapi.HidDeviceInfo;
import purejavahidapi.PureJavaHidApi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

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

    public static final int VENDOR_ID = 0x0fd9;
    public static final int PRODUCT_ID = 0x0060;

    private final List<StreamDeck> streamDecks = new ArrayList<>(0);

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
        streamDecks.addAll(openStreamDeckDevices().stream().map(StreamDeck::new).collect(toList()));
    }

    private List<HidDevice> openStreamDeckDevices() {
        List<HidDeviceInfo> foundStreamDeckDevices = findStreamDeckDevices();

        try {
            LOG.info("Connected Stream Decks:");
            List<HidDevice> openedDecks = new ArrayList<>(foundStreamDeckDevices.size());
            for (HidDeviceInfo hidDeviceinfo : foundStreamDeckDevices) {
                LOG.info("  Manufacurer: " + hidDeviceinfo.getManufacturerString());
                LOG.info("  Product:     " + hidDeviceinfo.getProductString());
                LOG.info("  Device-Id:   " + hidDeviceinfo.getDeviceId());
                LOG.info("  Serial-No:   " + hidDeviceinfo.getSerialNumberString());
                LOG.info("  Path:        " + hidDeviceinfo.getPath());
                LOG.info("");
                openedDecks.add(PureJavaHidApi.openDevice(hidDeviceinfo));
            }
            return openedDecks;
        } catch (IOException e) {
            LOG.error("Could not enumerate USB-devices", e);
            return new ArrayList<>(0);
        }
    }

    private List<HidDeviceInfo> findStreamDeckDevices() {
        LOG.info("Scanning for devices");
        List<HidDeviceInfo> foundStreamDeckDevices = new ArrayList<>(0);

        List<HidDeviceInfo> devList = PureJavaHidApi.enumerateDevices();
        for (HidDeviceInfo info : devList) {
            LOG.debug("Vendor-ID: " + info.getVendorId() + ", Product-ID: " + info.getProductId());
            if (info.getVendorId() == VENDOR_ID && info.getProductId() == PRODUCT_ID) {
                LOG.info("Found ESD [" + info.getVendorId() + ":" + info.getProductId() + "]");
                foundStreamDeckDevices.add(info);
            }
        }
        return foundStreamDeckDevices;
    }

    public StreamDeck getStreamDeck() {
        return getStreamDeck(0);
    }

    public StreamDeck getStreamDeck(int id) {
        if (id < 0 || id >= getStreamDeckSize()) {
            return null;
        }
        return streamDecks.get(id);
    }

    public int getStreamDeckSize() {
        return streamDecks.size();
    }
}
 