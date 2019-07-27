package com.stirante.RuneChanger.gui.controllers;

import com.stirante.RuneChanger.gui.Constants;
import com.stirante.RuneChanger.gui.Settings;
import com.stirante.RuneChanger.util.LangHelper;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

@Slf4j
public class MainController implements Initializable {

    @FXML
    private BorderPane border_pane;

    private Settings settings;
    private double xOffset = 0;
    private double yOffset = 0;

    public void init(Settings settings) {
        this.settings = settings;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        log.info("Main Controller initializing");
        makeStageDrageable();
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setResources(LangHelper.getLang());
            fxmlLoader.setLocation(getClass().getResource("/fxml/ContentArea.fxml"));
            Parent contentArea = fxmlLoader.load();
            border_pane.setCenter(contentArea);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void makeStageDrageable() {
        border_pane.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            }
        });
        border_pane.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Settings.mainStage.setX(event.getScreenX() - xOffset);
                Settings.mainStage.setY(event.getScreenY() - yOffset);
            }
        });

    }
}
