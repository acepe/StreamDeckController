package de.acepe.streamdeck.app;

public enum Screens {
    MAIN("ui/main_view.fxml", "Controller", 570, 460),
    SETTINGS("ui/settings.fxml", "Einstellungen", 570, 460);

    private final String resource;
    private final String title;
    private final int width;
    private final int height;

    Screens(String resource, String title, int width, int height) {
        this.resource = resource;
        this.title = title;
        this.width = width;
        this.height = height;
    }

    public String getResource() {
        return resource;
    }

    public String getTitle() {
        return title;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
