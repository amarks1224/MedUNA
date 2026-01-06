/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cr.ac.una.meduna.util;

import cr.ac.una.meduna.App;
import cr.ac.una.meduna.controller.Controller;
import io.github.palexdev.materialfx.css.themes.MFXThemeManager;
import io.github.palexdev.materialfx.css.themes.Themes;
import java.io.IOException;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.logging.Level;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class FlowController {

    private static FlowController INSTANCE = null;
    private static Stage mainStage;
    private static ResourceBundle idioma;
    private static HashMap<String, FXMLLoader> loaders = new HashMap<>();
    private static Controller logInController;

    private FlowController() {
    }

    private static void createInstance() {
        if (INSTANCE == null) {
            synchronized (FlowController.class) {
                if (INSTANCE == null) {
                    INSTANCE = new FlowController();
                }
            }
        }
    }

    public static FlowController getInstance() {
        if (INSTANCE == null) {
            createInstance();
        }
        return INSTANCE;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }

    public void InitializeFlow(Stage stage, ResourceBundle idioma) {
        getInstance();
        this.mainStage = stage;
        this.idioma = idioma;
    }

    private FXMLLoader getLoader(String name) {
        FXMLLoader loader = loaders.get(name);
        if (loader == null) {
            synchronized (FlowController.class) {
                if (loader == null) {
                    try {
                        loader = new FXMLLoader(App.class.getResource("view/" + name + ".fxml"), this.idioma);
                        loader.load();
                        loaders.put(name, loader);
                    } catch (Exception ex) {
                        loader = null;
                        java.util.logging.Logger.getLogger(FlowController.class.getName()).log(Level.SEVERE, "Creando loader [" + name + "].", ex);
                    }
                }
            }
        }
        if (!name.equals("PrincipalView")) {
            this.logInController = loader.getController();
        }
        return loader;
    }

    public void goMain(String viewName) {
        try {
            this.mainStage.setScene(new Scene(FXMLLoader.load(App.class.getResource("view/" + viewName + ".fxml"), this.idioma)));
            MFXThemeManager.addOn(this.mainStage.getScene(), Themes.DEFAULT, Themes.LEGACY);
            this.mainStage.show();
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(FlowController.class.getName()).log(Level.SEVERE, "Error inicializando la vista base.", ex);
        }
    }

    public void goView(String viewName) {
        goView(viewName, "Center", null);
    }

    public void goView(String viewName, String accion) {
        goView(viewName, "Center", accion);
    }

    public void goView(String viewName, String location, String accion) {
        BorderPane borderPane;
        FXMLLoader loader = getLoader(viewName);
        Controller controller = loader.getController();
        controller.setAccion(accion);
        controller.initialize();
        Stage stage = controller.getStage();
        if (stage == null) {
            stage = this.mainStage;
            controller.setStage(stage);
        }
        switch (location) {
            case "Center":
                borderPane = (BorderPane) stage.getScene().getRoot();
                borderPane.setCenter(loader.getRoot());
                break;
            case "Top":
                borderPane = (BorderPane) stage.getScene().getRoot();
                borderPane.setTop(loader.getRoot());
                break;
            case "Bottom":
                borderPane = (BorderPane) stage.getScene().getRoot();
                borderPane.setBottom(loader.getRoot());
                break;
            case "Right":
                borderPane = (BorderPane) stage.getScene().getRoot();
                borderPane.setRight(loader.getRoot());
                break;
            case "Left":
                borderPane = (BorderPane) stage.getScene().getRoot();
                borderPane.setLeft(loader.getRoot());
                break;
            default:
                break;
        }
    }

    /**
     * Limpia una sección específica del BorderPane principal.
     *
     * @param location Ubicación a limpiar ("Center", "Top", "Bottom", "Left",
     * "Right")
     * @author Stiward Araya C.
     */
    public void clearView(String location) {
        BorderPane borderPane = (BorderPane) this.mainStage.getScene().getRoot();

        switch (location) {
            case "Center":
                borderPane.setCenter(null);
                break;
            case "Top":
                borderPane.setTop(null);
                break;
            case "Bottom":
                borderPane.setBottom(null);
                break;
            case "Right":
                borderPane.setRight(null);
                break;
            case "Left":
                borderPane.setLeft(null);
                break;
            default:
                break;
        }
    }

    /**
     * Limpia una sección del BorderPane con animación.
     *
     * @param location Ubicación a limpiar
     * @param onFinished Acción a ejecutar después de limpiar
     * @author Stiward Araya C.
     */
    public void clearViewWithAnimation(String location, Runnable onFinished) {
        BorderPane borderPane = (BorderPane) this.mainStage.getScene().getRoot();
        Node nodeToRemove = null;

        switch (location) {
            case "Center":
                nodeToRemove = borderPane.getCenter();
                break;
            case "Top":
                nodeToRemove = borderPane.getTop();
                break;
            case "Bottom":
                nodeToRemove = borderPane.getBottom();
                break;
            case "Right":
                nodeToRemove = borderPane.getRight();
                break;
            case "Left":
                nodeToRemove = borderPane.getLeft();
                break;
        }

        if (nodeToRemove != null) {
            Node finalNode = nodeToRemove;
            String finalLocation = location;
            UIAnimator.slideOutLeft(nodeToRemove, () -> {
                clearView(finalLocation);
                if (onFinished != null) {
                    onFinished.run();
                }
            });
        } else if (onFinished != null) {
            onFinished.run();
        }
    }

    public void goViewInStage(String viewName, Stage stage) {
        FXMLLoader loader = getLoader(viewName);
        Controller controller = loader.getController();
        controller.setStage(stage);
        stage.getScene().setRoot(loader.getRoot());
        MFXThemeManager.addOn(stage.getScene(), Themes.DEFAULT, Themes.LEGACY);

    }

    public void goViewInWindow(String viewName) {
        FXMLLoader loader = getLoader(viewName);
        Controller controller = loader.getController();
        controller.initialize();
        Stage stage = new Stage();
       // stage.getIcons().add(new Image(App.class.getResourceAsStream("/cr/ac/una/restuna/resources/images/LogoRestUNA.png")));
        stage.setTitle(controller.getNombreVista());
        stage.setOnHidden((WindowEvent event) -> {
            controller.getStage().getScene().setRoot(new Pane());
            controller.setStage(null);
        });
        controller.setStage(stage);
        Parent root = loader.getRoot();
        Scene scene = new Scene(root);
        MFXThemeManager.addOn(scene, Themes.DEFAULT, Themes.LEGACY);
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }

    public void goLogInWindowModal(Boolean resizable) {
        goViewInWindowModal("LogInView", this.logInController.getStage(), resizable);

    }

    public void goViewInWindowModal(String viewName, Stage parentStage, Boolean resizable) {
        FXMLLoader loader = getLoader(viewName);
        Controller controller = loader.getController();
        controller.initialize();
        Stage stage = new Stage();
   //     stage.getIcons().add(new Image(App.class.getResourceAsStream("/cr/ac/una/restuna/resources/images/LogoRestUNA.png")));
        stage.setTitle(controller.getNombreVista());
        stage.setResizable(resizable);
        stage.setOnHidden((WindowEvent event) -> {
            controller.getStage().getScene().setRoot(new Pane());
            controller.setStage(null);
        });
        controller.setStage(stage);
        Parent root = loader.getRoot();
        Scene scene = new Scene(root);
        MFXThemeManager.addOn(scene, Themes.DEFAULT, Themes.LEGACY);
        stage.setScene(scene);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(parentStage);
        stage.centerOnScreen();
        stage.showAndWait();

    }

    public Controller getController(String viewName) {
        return getLoader(viewName).getController();
    }

    public void limpiarLoader(String view) {
        this.loaders.remove(view);
    }

    public static void setIdioma(ResourceBundle idioma) {
        FlowController.idioma = idioma;
    }

    public void initialize() {
        this.loaders.clear();
    }

    public void salir() {
        this.mainStage.close();
    }

}
