package cr.ac.una.meduna.util;

import javafx.animation.*;
import javafx.scene.Node;
import javafx.util.Duration;

public class UIAnimator {

    private static final Duration NORMAL = Duration.millis(300);

    // ANIMACIONES ------------------------------------------------------
    public static void fadeIn(Node node, Duration duration, Runnable onFinished) {
        FadeTransition fade = new FadeTransition(duration, node);
        fade.setFromValue(0.0);
        fade.setToValue(1.0);
        if (onFinished != null) {
            fade.setOnFinished(e -> onFinished.run());
        }
        fade.play();
    }

    public static void fadeOut(Node node, Duration duration, Runnable onFinished) {
        FadeTransition fade = new FadeTransition(duration, node);
        fade.setFromValue(1.0);
        fade.setToValue(0.0);
        if (onFinished != null) {
            fade.setOnFinished(e -> onFinished.run());
        }
        fade.play();
    }

    public static void slideInLeft(Node node, double distance, Duration duration, Runnable onFinished) {
        TranslateTransition slide = new TranslateTransition(duration, node);
        slide.setFromX(-distance);
        slide.setToX(0);

        FadeTransition fade = new FadeTransition(duration, node);
        fade.setFromValue(0.0);
        fade.setToValue(1.0);

        ParallelTransition parallel = new ParallelTransition(slide, fade);
        if (onFinished != null) {
            parallel.setOnFinished(e -> onFinished.run());
        }
        parallel.play();
    }

    public static void slideInRight(Node node, double distance, Duration duration, Runnable onFinished) {
        TranslateTransition slide = new TranslateTransition(duration, node);
        slide.setFromX(distance);
        slide.setToX(0);

        FadeTransition fade = new FadeTransition(duration, node);
        fade.setFromValue(0.0);
        fade.setToValue(1.0);

        ParallelTransition parallel = new ParallelTransition(slide, fade);
        if (onFinished != null) {
            parallel.setOnFinished(e -> onFinished.run());
        }
        parallel.play();
    }

    public static void slideOutLeft(Node node, double distance, Duration duration, Runnable onFinished) {
        TranslateTransition slide = new TranslateTransition(duration, node);
        slide.setFromX(0);
        slide.setToX(-distance);

        FadeTransition fade = new FadeTransition(duration, node);
        fade.setFromValue(1.0);
        fade.setToValue(0.0);

        ParallelTransition parallel = new ParallelTransition(slide, fade);
        if (onFinished != null) {
            parallel.setOnFinished(e -> onFinished.run());
        }
        parallel.play();
    }

    public static void slideOutRight(Node node, double distance, Duration duration, Runnable onFinished) {
        TranslateTransition slide = new TranslateTransition(duration, node);
        slide.setFromX(0);
        slide.setToX(distance);

        FadeTransition fade = new FadeTransition(duration, node);
        fade.setFromValue(1.0);
        fade.setToValue(0.0);

        ParallelTransition parallel = new ParallelTransition(slide, fade);
        if (onFinished != null) {
            parallel.setOnFinished(e -> onFinished.run());
        }
        parallel.play();
    }

    public static void rotate(Node node, double angle, Duration duration, Runnable onFinished) {
        RotateTransition rotate = new RotateTransition(duration, node);
        rotate.setByAngle(angle);
        if (onFinished != null) {
            rotate.setOnFinished(e -> onFinished.run());
        }
        rotate.play();
    }

    public static void shake(Node node, Runnable onFinished) {
        TranslateTransition shake = new TranslateTransition(Duration.millis(100), node);
        shake.setFromX(0);
        shake.setByX(10);
        shake.setCycleCount(6);
        shake.setAutoReverse(true);
        shake.setOnFinished(e -> {
            node.setTranslateX(0);
            if (onFinished != null) {
                onFinished.run();
            }
        });
        shake.play();
    }

    // SOBRECARGAS ------------------------------------------------------
    public static void fadeIn(Node node, Duration duration) {
        fadeIn(node, duration, null);
    }

    public static void fadeIn(Node node) {
        fadeIn(node, NORMAL, null);
    }

    public static void fadeIn(Node node, Runnable onFinished) {
        fadeIn(node, NORMAL, onFinished);
    }

    public static void fadeOut(Node node, Duration duration) {
        fadeOut(node, duration, null);
    }

    public static void fadeOut(Node node) {
        fadeOut(node, NORMAL, null);
    }

    public static void fadeOut(Node node, Runnable onFinished) {
        fadeOut(node, NORMAL, onFinished);
    }

    public static void slideInLeft(Node node, double distance, Duration duration) {
        slideInLeft(node, distance, duration, null);
    }

    public static void slideInLeft(Node node) {
        slideInLeft(node, 100, NORMAL, null);
    }

    public static void slideInLeft(Node node, Runnable onFinished) {
        slideInLeft(node, 100, NORMAL, onFinished);
    }

    public static void slideInRight(Node node, double distance, Duration duration) {
        slideInRight(node, distance, duration, null);
    }

    public static void slideInRight(Node node) {
        slideInRight(node, 100, NORMAL, null);
    }

    public static void slideInRight(Node node, Runnable onFinished) {
        slideInRight(node, 100, NORMAL, onFinished);
    }

    public static void slideOutLeft(Node node, double distance, Duration duration) {
        slideOutLeft(node, distance, duration, null);
    }

    public static void slideOutLeft(Node node) {
        slideOutLeft(node, 100, NORMAL, null);
    }

    public static void slideOutLeft(Node node, Runnable onFinished) {
        slideOutLeft(node, 100, NORMAL, onFinished);
    }

    public static void slideOutRight(Node node, double distance, Duration duration) {
        slideOutRight(node, distance, duration, null);
    }

    public static void slideOutRight(Node node) {
        slideOutRight(node, 100, NORMAL, null);
    }

    public static void slideOutRight(Node node, Runnable onFinished) {
        slideOutRight(node, 100, NORMAL, onFinished);
    }

    public static void rotate(Node node, double angle, Duration duration) {
        rotate(node, angle, duration, null);
    }

    public static void rotate360(Node node) {
        rotate(node, 360, NORMAL, null);
    }

    public static void shake(Node node) {
        shake(node, null);
    }

    // CREATE -------------------------------------------------------
    public static FadeTransition createFadeTransition(Node node, Duration duration, Runnable onFinished) {
        FadeTransition fade = new FadeTransition(duration, node);
        if (onFinished != null) {
            fade.setOnFinished(e -> onFinished.run());
        }
        return fade;
    }

    // No instance
    private UIAnimator() {
        throw new UnsupportedOperationException("Esta es una clase de utilidad y no puede ser instanciada");
    }
}
