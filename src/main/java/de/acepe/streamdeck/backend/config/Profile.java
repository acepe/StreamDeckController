package de.acepe.streamdeck.backend.config;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

public class Profile {

    @Expose
    private final List<Page> pages = new ArrayList<>(1);
    @Expose
    private String name;
    @Expose
    private String startPage;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addPage(Page page) {
        if (pages.isEmpty()) {
            setStartPage(page);
        }
        pages.add(page);
    }

    public void removePage(Page page) {
        pages.remove(page);
    }

    public List<Page> getPages() {
        return pages;
    }

    public String getStartPage() {
        return startPage;
    }

    public void setStartPage(String startPageId) {
        this.startPage = startPageId;
    }

    public void setStartPage(Page startPage) {
        this.startPage = startPage.getId();
    }

    public void syncPages() {
        pages.forEach(Page::syncButtonImages);
    }
}
