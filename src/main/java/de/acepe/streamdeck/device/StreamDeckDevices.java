package de.acepe.streamdeck.device;

import org.hid4java.HidDevice;
import org.hid4java.HidManager;
import org.hid4java.HidServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Consumer;

import static java.util.concurrent.Executors.newScheduledThreadPool;
import static java.util.concurrent.TimeUnit.SECONDS;

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

    private final List<Consumer<IStreamDeck>> onDeckDiscoveredCallbacks = new LinkedList<>();
    private final ScheduledExecutorService watchdogThreadPool;

    private IStreamDeck streamDeck = new DummyDeck();

    @Inject
    private StreamDeckDevices() {
        watchdogThreadPool = newScheduledThreadPool(0, r -> new Thread(r, "ESD-Watchdog-Thread"));
        watchdogThreadPool.scheduleAtFixedRate(this::discoverAndConnectDevices, 0, 1, SECONDS);
    }

    private void discoverAndConnectDevices() {
        try {

        IStreamDeck foundDeck = discoverConnectedStreamDeck();
        if (!Objects.equals(foundDeck, streamDeck)) {
            if (streamDeck != null) {
                streamDeck.dispose();
            }
            streamDeck = foundDeck;
            streamDeck.open();
            onDeckDiscoveredCallbacks.forEach(onDeckDiscoveredCallback -> onDeckDiscoveredCallback.accept(streamDeck));
        }
        }catch (Exception e){
            LOG.error("Caught Exception in Watchdog-Thread", e);
        }
    }

    private IStreamDeck discoverConnectedStreamDeck() {
        LOG.info("Scanning for devices");

        HidServices hidServices = HidManager.getHidServices();
        hidServices.start();

        ArrayList<IStreamDeck> ret = new ArrayList<>();
        for (HidDevice device : hidServices.getAttachedHidDevices()) {
            LOG.debug("Vendor-ID: " + device.getVendorId() + ", Product-ID: " + device.getProductId());
            if (device.getVendorId() == VENDOR_ID && device.getProductId() == PRODUCT_ID) {
                LOG.debug("  Manufacurer: " + device.getManufacturer());
                LOG.debug("  Product:     " + device.getProduct());
                LOG.debug("  Device-Id:   " + device.getId());
                LOG.debug("  Serial-No:   " + device.getSerialNumber());
                LOG.debug("  Path:        " + device.getPath());
                LOG.debug("");
                ret.add(new ClassicStreamDeck(device));
            }
        }
        if (ret.isEmpty()) {
            LOG.info("No Stream Deck found…");
            return new DummyDeck();
        }
        //for now we only support one Deck, so we use the first we found
        IStreamDeck deck = ret.get(0);
        LOG.info("Found Stream Deck with Serial-No: " + deck.getSerialnumber());
        return deck;
    }

    public IStreamDeck getStreamDeck() {
        return streamDeck;
    }

    public void stop() {
        watchdogThreadPool.shutdown();
        if (streamDeck != null) {
            streamDeck.setLogo();
            streamDeck.dispose();
        }
    }

    public void registerDecksDiscoveredCallback(Consumer<IStreamDeck> onDeckDiscoveredCallback) {
        onDeckDiscoveredCallbacks.add(onDeckDiscoveredCallback);
    }
}
 