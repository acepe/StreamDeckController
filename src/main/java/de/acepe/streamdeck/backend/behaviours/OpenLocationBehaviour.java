package de.acepe.streamdeck.backend.behaviours;

import de.acepe.streamdeck.device.event.KeyEvent;
import de.acepe.streamdeck.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

public class OpenLocationBehaviour extends BasicButtonBehaviour {
    private static final Logger LOG = LoggerFactory.getLogger(OpenLocationBehaviour.class);

    private URI uri;
    private File file;

    public void setUri(URI uri) {
        this.uri = uri;
    }

    public void setUri(String uriAsString) {
        try {
            setUri(new URI(uriAsString));
        } catch (URISyntaxException e) {
            LOG.error("URI {} is not valid", uriAsString, e);
        }
    }

    public void setFile(String file) {
        setFile(new File(file));
    }

    public void setFile(File file) {
        this.file = file;
    }

    @Override
    public void onButtonPressed(KeyEvent event) {

    }

    @Override
    public void onButtonReleased(KeyEvent event) {
        if (uri != null) {
            FileUtil.doOpen(uri);
        }
        if (file != null) {
            FileUtil.doOpen(file);
        }
    }

    public URI getUri() {
        return uri;
    }

    public File getFile() {
        return file;
    }

}
