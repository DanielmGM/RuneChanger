package com.stirante.RuneChanger.gui.controllers;

import com.jfoenix.controls.JFXButton;
import com.stirante.RuneChanger.gui.Constants;
import com.stirante.RuneChanger.gui.Settings;
import com.stirante.RuneChanger.util.LangHelper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Duration;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

@Slf4j
public class ContentAreaController implements Initializable {

    @FXML
    private JFXButton minimizeButton;

    @FXML
    private JFXButton shutdownButton;

    boolean flag = true;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        log.info("Content Area Controller initializing");
    }

    @FXML
    void handleMenuBarButtonPressed(ActionEvent event) {
        JFXButton target = (JFXButton) event.getTarget();
        if (target.equals(minimizeButton)) {
            Settings.mainStage.hide();
        }
        else if (target.equals(shutdownButton)) {
            log.warn("User requested program shutdown");
            System.exit(0);
        }
    }

    @FXML
    private void open_sidebar(ActionEvent event) throws IOException {
        BorderPane border_pane = (BorderPane) ((Node) event.getSource()).getScene().getRoot();
        if (flag == true) {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setResources(LangHelper.getLang());
            fxmlLoader.setLocation(getClass().getResource("/fxml/Sidebar.fxml"));
            Parent sidebar = fxmlLoader.load();
            border_pane.setLeft(sidebar);
            BorderPane.setAlignment(sidebar, Pos.CENTER_LEFT);
            flag = false;
        }
        else {
            border_pane.setLeft(null);
            flag = true;
        }
    }
}
