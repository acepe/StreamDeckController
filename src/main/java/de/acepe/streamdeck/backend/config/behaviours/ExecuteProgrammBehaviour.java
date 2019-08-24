package de.acepe.streamdeck.backend.config.behaviours;

import com.google.gson.annotations.Expose;
import de.acepe.streamdeck.device.event.KeyEvent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ExecuteProgrammBehaviour extends BasicButtonBehaviour {

    @Expose
    private String pathToProgramm;
    @Expose
    private String[] args = new String[0];

    public void setProgramm(String pathToProgramm) {
        this.pathToProgramm = pathToProgramm;
    }

    public void setArguments(String... args) {
        this.args = args;
    }

    @Override
    public void onButtonPressed(KeyEvent event) {

    }

    @Override
    public void onButtonReleased(KeyEvent event) {
        List<String> command = new ArrayList<>(args.length + 1);
        command.add(pathToProgramm);
        command.addAll(Arrays.asList(args));
        try {
            new ProcessBuilder(command).start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
