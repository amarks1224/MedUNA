/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cr.ac.una.meduna.util;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;

/**
 *
 * @author ccarranza
 */
public final class BindingUtils {

    static ChangeListener<Toggle> changeListener = (ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) -> {
    };

    private BindingUtils() {
    }

    public static <T> void bindToggleGroupToProperty(final ToggleGroup toggleGroup, final ObjectProperty<T> property) {
        // Check all toggles for required user data
        toggleGroup.getToggles().forEach(toggle -> {
            if (toggle.getUserData() == null) {
                throw new IllegalArgumentException("The ToggleGroup contains at least one Toggle without user data!");
            }
        });
        // Select initial toggle for current property state
        for (Toggle toggle : toggleGroup.getToggles()) {
            if (property.getValue() != null && property.getValue().equals(toggle.getUserData())) {
                toggleGroup.selectToggle(toggle);
                break;
            }
        }
        // Update property value on toggle selection changes
        changeListener = (ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) -> {
            property.setValue((T) newValue.getUserData());
        };
        toggleGroup.selectedToggleProperty().addListener(changeListener);
    }

    public static <T> void unbindToggleGroupToProperty(final ToggleGroup toggleGroup, final ObjectProperty<T> property) {
        toggleGroup.selectedToggleProperty().removeListener(changeListener);
    }
    
    public static void bindToggleGroupToProperty(
        final ToggleGroup toggleGroup,
        final StringProperty property) {

    // Selección inicial
    for (Toggle toggle : toggleGroup.getToggles()) {
        if (toggle instanceof RadioButton rb &&
            rb.getText().equals(property.get())) {
            toggleGroup.selectToggle(toggle);
            break;
        }
    }

    changeListener = (obs, oldToggle, newToggle) -> {
        if (newToggle != null) {
            property.set(((RadioButton) newToggle).getText());
        }
    };

    toggleGroup.selectedToggleProperty().addListener(changeListener);
}

public static void unbindToggleGroupToProperty(
        final ToggleGroup toggleGroup,
        final StringProperty property) {
    toggleGroup.selectedToggleProperty().removeListener(changeListener);
}

}
