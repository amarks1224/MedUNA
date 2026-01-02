package cr.ac.una.meduna;

import cr.ac.una.meduna.util.FlowController;
import javafx.application.Application;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage stage) {
        FlowController.getInstance().InitializeFlow(stage, null);
        FlowController.getInstance().goMain("PrincipalView");
    }

    public static void main(String[] args) {
        launch();
    }
}
