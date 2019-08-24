package de.acepe.streamdeck.ui;

import de.acepe.streamdeck.app.ControlledScreen;
import de.acepe.streamdeck.app.ScreenManager;
import de.acepe.streamdeck.app.Screens;
import de.acepe.streamdeck.backend.DeckManager;
import de.acepe.streamdeck.backend.config.ExampleProfileFactory;
import de.acepe.streamdeck.backend.config.Profile;
import de.acepe.streamdeck.util.OneWayStringConverter;
import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.image.ImageView;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.stream.IntStream;

public class MainViewController implements ControlledScreen {

    private final ObservableList<Profile> profilesList = FXCollections.observableArrayList();
    private final ArrayList<Button> buttons = new ArrayList<>(15);
    private final ScreenManager screenManager;
    private final DeckManager deckManager;

    @FXML
    private Button button0;
    @FXML
    private Button button1;
    @FXML
    private Button button2;
    @FXML
    private Button button3;
    @FXML
    private Button button4;
    @FXML
    private Button button5;
    @FXML
    private Button button6;
    @FXML
    private Button button7;
    @FXML
    private Button button8;
    @FXML
    private Button button9;
    @FXML
    private Button button10;
    @FXML
    private Button button11;
    @FXML
    private Button button12;
    @FXML
    private Button button13;
    @FXML
    private Button button14;
    @FXML
    private ComboBox<Profile> profilesComboBox;

    @FXML
    private Button settingsButton;

    @Inject
    public MainViewController(ScreenManager screenManager,
                              DeckManager deckManager,
                              ExampleProfileFactory exampleProfileFactory) {
        this.screenManager = screenManager;
        this.deckManager = deckManager;

//        deckManager.addProfile(examplePageFactory.createProfile1());
//        deckManager.saveProfiles();

//        Page page2 = examplePageFactory.configurePage2();
//        persistence.savePage(page2);
//
//        Page page1 = examplePageFactory.configurePage1();
//        persistence.savePage(page1);
    }

    @FXML
    private void initialize() {
        GlyphsDude.setIcon(settingsButton, FontAwesomeIcon.COG, "1.5em");
        buttons.add(button0);
        buttons.add(button1);
        buttons.add(button2);
        buttons.add(button3);
        buttons.add(button4);
        buttons.add(button5);
        buttons.add(button6);
        buttons.add(button7);
        buttons.add(button8);
        buttons.add(button9);
        buttons.add(button10);
        buttons.add(button11);
        buttons.add(button12);
        buttons.add(button13);
        buttons.add(button14);

        profilesComboBox.setConverter(new OneWayStringConverter<>(Profile::getName));
        profilesComboBox.itemsProperty().set(profilesList);
        profilesComboBox.getSelectionModel().selectedItemProperty()
                        .addListener((obs, ov, nv) -> deckManager.setCurrentProfile(nv));
        configureButtons();
        deckManager.bindUpdateUICallback(this::updateButtonUI);

    }

    private void configureButtons() {
        IntStream.rangeClosed(0, 14).forEach(i -> {
            Button button = buttons.get(i);
            button.setGraphic(new ImageView());
            button.setOnAction(event -> deckManager.fireActionFromUI(i));
            updateButtonUI(i);
        });
    }

    private void updateButtonUI(int index) {
        Button button = buttons.get(index);
        ((ImageView) button.getGraphic()).setImage(deckManager.getButtonImage(index));

        //TODO: only on Profile-Change, extra callback
        if (profilesComboBox.getSelectionModel().getSelectedItem() != deckManager.getCurrentProfile()) {
            profilesList.setAll(deckManager.getProfiles());
            profilesComboBox.getSelectionModel().select(deckManager.getCurrentProfile());
        }
    }

    @FXML
    void onSettingsPerformed() {
        screenManager.setScreen(Screens.SETTINGS, ScreenManager.Direction.LEFT);
    }

}
