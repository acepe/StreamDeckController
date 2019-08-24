package de.acepe.streamdeck.backend.config;

import de.acepe.streamdeck.backend.DeckManager;
import de.acepe.streamdeck.backend.config.behaviours.*;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.stream.IntStream;

import static java.awt.event.KeyEvent.*;

public class ExampleProfileFactory {
    public static final String PAGE_1_ID = "65f069ef-fd1e-4897-acbd-b610d29ab1cc";
    public static final String PAGE_2_ID = "0bcd4517-5036-4d64-965d-ebc8d1c7f605";

    private final DeckManager deckManager;
    private final Provider<StreamDeckToggleBrightnessBehavior> toggleBrightness;
    private final Provider<HotKeyBehaviour> hotkey;
    private final Provider<ShowPageBehaviour> showPage;
    private final Provider<ExecuteProgrammBehaviour> executeProgramm;
    private final Provider<OpenLocationBehaviour> openLocation;
    private final Provider<SleepBehaviour> sleepBehaviour;

    @Inject
    public ExampleProfileFactory(DeckManager deckManager,
                                 Provider<StreamDeckToggleBrightnessBehavior> toggleBrightness,
                                 Provider<HotKeyBehaviour> hotkey,
                                 Provider<ShowPageBehaviour> showPage,
                                 Provider<ExecuteProgrammBehaviour> executeProgramm,
                                 Provider<OpenLocationBehaviour> openLocation,
                                 Provider<SleepBehaviour> sleepBehaviour) {
        this.deckManager = deckManager;
        this.toggleBrightness = toggleBrightness;
        this.hotkey = hotkey;
        this.showPage = showPage;
        this.executeProgramm = executeProgramm;
        this.openLocation = openLocation;
        this.sleepBehaviour = sleepBehaviour;
    }

    public Profile createProfile1() {
        Profile profile = new Profile();
        profile.setName("Profile 1");
        profile.addPage(configurePage1());
        profile.addPage(configurePage2());
        return profile;
    }

    private Page configurePage2() {
        Page page = new Page("Page 2");
        page.setId(PAGE_2_ID);
        deckManager.registerPage(page);

        ShowPageBehaviour showPageBehaviour = showPage.get();
        showPageBehaviour.setPageId(PAGE_1_ID);

        DeckButton showPage1Button = new DeckButton();
        showPage1Button.setText("Page 1", 20);
        showPage1Button.addBehaviour(showPageBehaviour);
        page.addButton(4, showPage1Button);
        return page;
    }

    private Page configurePage1() {
        Page page = new Page("Page 1");
        page.setId(PAGE_1_ID);
        deckManager.registerPage(page);

        //        IntStream.rangeClosed(0, 10).forEach(i -> page1.addButton(i, new DeckButton(String.valueOf(i))));
        IntStream.rangeClosed(11, 14).forEach(i -> page.addButton(i, new DeckButton("D" + (15 - i))));

        ShowPageBehaviour showPageBehaviour = showPage.get();
        showPageBehaviour.setPageId(PAGE_2_ID);

        DeckButton showPage2Button = page.getButton(4);
        showPage2Button.setText("Page 2", 20);
        showPage2Button.addBehaviour(showPageBehaviour);

        DeckButton brightnessButton = page.getButton(0);
        brightnessButton.setText("Br");
        brightnessButton.addBehaviour(toggleBrightness.get());

        HotKeyBehaviour vdesk1 = hotkey.get();
        vdesk1.setKey(VK_F1);
        vdesk1.setModifier1(VK_CONTROL);

        HotKeyBehaviour vdesk2 = hotkey.get();
        vdesk2.setKey(VK_F2);
        vdesk2.setModifier1(VK_CONTROL);

        HotKeyBehaviour vdesk3 = hotkey.get();
        vdesk3.setKey(VK_F3);
        vdesk3.setModifier1(VK_CONTROL);

        HotKeyBehaviour vdesk4 = hotkey.get();
        vdesk4.setKey(VK_F4);
        vdesk4.setModifier1(VK_CONTROL);

        page.getButton(14).addBehaviour(vdesk1);
        page.getButton(13).addBehaviour(vdesk2);
        page.getButton(12).addBehaviour(vdesk3);
        page.getButton(11).addBehaviour(vdesk4);

        OpenLocationBehaviour openGoogle = openLocation.get();
        openGoogle.setUri("https://google.de");
        DeckButton openGoogleButton = page.getButton(9);
        openGoogleButton.setText("Google", 20);
        openGoogleButton.addBehaviour(openGoogle);

        OpenLocationBehaviour openHome = openLocation.get();
        openHome.setFile(System.getProperty("user.home"));
        DeckButton openHomeButton = page.getButton(7);
        openHomeButton.setText("⌂", 60);
        openHomeButton.addBehaviour(openHome);

        ExecuteProgrammBehaviour startChrome = executeProgramm.get();
        startChrome.setProgramm("/usr/bin/google-chrome");
        startChrome.setArguments("--incognito");
        DeckButton chromeButton = page.getButton(8);
        chromeButton.setText("Chrome", 20);
        chromeButton.addBehaviour(startChrome);

        SleepBehaviour sleep = sleepBehaviour.get();
        sleep.setDurationInMillis(2000);
        HotKeyBehaviour vdesk1comp = hotkey.get();
        vdesk1comp.setKey(VK_F1);
        vdesk1comp.setModifier1(VK_CONTROL);
        OpenLocationBehaviour openHomeComp = openLocation.get();
        openHomeComp.setFile(System.getProperty("user.home"));
        DeckButton compoundButton = page.getButton(10);
        compoundButton.setText("Multi", 30);
        compoundButton.addBehaviour(vdesk1comp, sleep, openHomeComp);

        return page;
    }

}
