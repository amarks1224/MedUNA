package cr.ac.una.meduna.controller;

import cr.ac.una.meduna.util.FlowController;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.BorderPane;

public class PrincipalController extends Controller implements Initializable  {

    @FXML
    private BorderPane root;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
       Platform.runLater(() -> {
            FlowController.getInstance().goView("InicioView");
        });
    }
    
    @Override
    public void initialize() {
        super.initialize();
       
       
    }
   
}

    

   
