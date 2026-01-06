package cr.ac.una.meduna.util;

import javafx.scene.Node;

/**
 * Utilidad para animar campos con efecto shake y resaltar errores visuales.
 *
 * @author Angie Marks
 * @author Juan Calderon
 */
public class Shake {

    private static final String ERROR_STYLE = "-fx-border-color: red";

    public Shake() {
    }

    public void error(Node node) {
        node.setStyle(ERROR_STYLE);
        UIAnimator.shake(node, () -> {
            node.setStyle("");
        });
    }

    public void error(Node... nodes) {
        for (Node node : nodes) {
            error(node);
        }
    }
}
