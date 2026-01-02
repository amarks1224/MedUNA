/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package cr.ac.una.meduna.controller;

import io.github.palexdev.materialfx.controls.MFXButton;
import cr.ac.una.meduna.util.FlowController;
import cr.ac.una.meduna.util.UIAnimator;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.input.TouchEvent;
import javafx.scene.layout.AnchorPane;

/**
 * FXML Controller class
 *
 * @author Usuario
 */
public class InicioController extends Controller implements Initializable {

    @FXML
    private AnchorPane root;
    @FXML
    private Label lblTitulo;
    @FXML
    private Label lblSubtitulo;
    @FXML
    private Hyperlink linkAcercaDe;
    @FXML
    private MFXButton btnIngresar;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        iniciarVentana();
    }    
    
     @Override
    public void initialize() {
        // TODO
    }    

    @FXML
    private void onActionLinkAcercaDe(ActionEvent event) {
    }

    @FXML
    private void onTouchPressedLinkRecuperacion(TouchEvent event) {
    }

    @FXML
    private void onActionBtnIngresar(ActionEvent event) {
    }

    @FXML
    private void onTouchPressedBtnIngresar(TouchEvent event) {
    }
    
    private void iniciarVentana() {
        // Animación de entrada de la ventana
        UIAnimator.slideInLeft(root);

        // Configuración del botón
        btnIngresar.setOnAction(e -> abrirMenuPrincipal());
    }

    private void abrirMenuPrincipal() {
        // Animación de salida y carga del fondo/base permanente
        UIAnimator.slideOutRight(root, () -> {
            FlowController.getInstance().goView("MenuView", "Left", "");
        });
    }
}
