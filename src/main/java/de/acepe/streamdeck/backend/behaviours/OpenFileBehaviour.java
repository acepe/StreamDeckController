package de.acepe.streamdeck.backend.behaviours;

import de.acepe.streamdeck.device.event.KeyEvent;
import de.acepe.streamdeck.util.FileUtil;

import java.io.File;
import java.net.URI;

public class OpenFileBehaviour extends BasicButtonBehaviour {

    private URI uri;
    private File file;

    public void setUri(URI uri) {
        this.uri = uri;
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
