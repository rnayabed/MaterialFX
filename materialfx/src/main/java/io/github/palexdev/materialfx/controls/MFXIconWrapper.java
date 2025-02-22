/*
 * Copyright (C) 2021 Parisi Alessandro
 * This file is part of MaterialFX (https://github.com/palexdev/MaterialFX).
 *
 * MaterialFX is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MaterialFX is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with MaterialFX.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.palexdev.materialfx.controls;

import io.github.palexdev.materialfx.effects.ripple.MFXCircleRippleGenerator;
import io.github.palexdev.materialfx.effects.ripple.RipplePosition;
import io.github.palexdev.materialfx.effects.ripple.base.IRippleGenerator;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;

import java.util.function.Function;

/**
 * Convenience class for creating icons wrapped in a StackPane.
 * <p>
 * The size is equal and fixed both for height and width.
 */
public class MFXIconWrapper extends StackPane {
    //================================================================================
    // Properties
    //================================================================================
    private final String STYLE_CLASS = "mfx-icon-wrapper";

    private final ObjectProperty<Node> icon = new SimpleObjectProperty<>();
    private final DoubleProperty size = new SimpleDoubleProperty();
    private final MFXCircleRippleGenerator rippleGenerator = new MFXCircleRippleGenerator(this);

    //================================================================================
    // Constructors
    //================================================================================
    public MFXIconWrapper() {
        initialize();
    }

    public MFXIconWrapper(Node icon, double size) {
        initialize();

        setIcon(icon);
        setSize(size);
    }

    //================================================================================
    // Methods
    //================================================================================

    /**
     * Adds a ripple generator to the icon. It is an optional.
     */
    public MFXIconWrapper addRippleGenerator() {
        if (!getChildren().contains(rippleGenerator)) {
            super.getChildren().add(0, rippleGenerator);
        }

        return this;
    }

    /**
     * Adds the ripple generator to the icon by calling {@link #addRippleGenerator()}, sets its position function
     * to use the mouse event x and y coordinates, and adds the event filter to the icon to generate the ripples.
     *
     * @see IRippleGenerator
     * @see MFXCircleRippleGenerator
     */
    public MFXIconWrapper defaultRippleGeneratorBehavior() {
        addRippleGenerator();
        rippleGenerator.setRipplePositionFunction(event -> new RipplePosition(event.getX(), event.getY()));
        addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                rippleGenerator.generateRipple(event);
            }
        });
        return this;
    }

    /**
     * Adds the ripple generator to the icon by calling {@link #addRippleGenerator()}, sets its position function
     * to the given function, and adds the event filter to the icon to generate the ripples.
     *
     * @see IRippleGenerator
     * @see MFXCircleRippleGenerator
     */
    public MFXIconWrapper rippleGeneratorBehavior(Function<MouseEvent, RipplePosition> positionFunction) {
        addRippleGenerator();
        rippleGenerator.setRipplePositionFunction(positionFunction);
        addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                rippleGenerator.generateRipple(event);
            }
        });
        return this;
    }

    private void initialize() {
        getStyleClass().add(STYLE_CLASS);

        setMinSize(USE_PREF_SIZE, USE_PREF_SIZE);
        setMaxSize(USE_PREF_SIZE, USE_PREF_SIZE);

        icon.addListener((observable, oldValue, newValue) -> {
            super.getChildren().remove(oldValue);
            manageIcon(newValue);
        });
        size.addListener((observable, oldValue, newValue) -> setPrefSize(newValue.doubleValue(), newValue.doubleValue()));
    }

    /**
     * This method handles the positioning of the icon in the children list.
     */
    private void manageIcon(Node icon) {
        if (icon == null) {
            return;
        }

        ObservableList<Node> children = super.getChildren();

        if (children.isEmpty()) {
            children.add(icon);
            return;
        }

        if (children.contains(rippleGenerator)) {
            if (children.size() == 1) {
                children.add(icon);
            } else {
                children.set(1, icon);
            }
        }
    }

    /**
     * @return the RippleGenerator instance.
     */
    public MFXCircleRippleGenerator getRippleGenerator() {
        return rippleGenerator;
    }

    public Node getIcon() {
        return icon.get();
    }

    /**
     * Contains the reference to the icon.
     */
    public ObjectProperty<Node> iconProperty() {
        return icon;
    }

    public void setIcon(Node icon) {
        this.icon.set(icon);
    }

    /**
     * Removes the icon node.
     */
    public void removeIcon() {
        setIcon(null);
    }

    public double getSize() {
        return size.get();
    }

    /**
     * Specifies the size of the container.
     */
    public DoubleProperty sizeProperty() {
        return size;
    }

    public void setSize(double size) {
        this.size.set(size);
    }

    //================================================================================
    // Override Methods
    //================================================================================

    /**
     * @return an unmodifiable list of the StackPane children
     */
    @Override
    public ObservableList<Node> getChildren() {
        return FXCollections.unmodifiableObservableList(super.getChildren());
    }
}
