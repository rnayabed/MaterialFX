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

import io.github.palexdev.materialfx.MFXResourcesLoader;
import io.github.palexdev.materialfx.skins.MFXToggleButtonSkin;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.css.*;
import javafx.scene.Node;
import javafx.scene.control.Skin;
import javafx.scene.control.ToggleButton;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This is the implementation of a toggle button following Google's material design guidelines in JavaFX.
 * <p>
 * Extends {@code ToggleButton}, redefines the style class to "mfx-toggle-button" for usage in CSS and
 * includes a {@code RippleGenerator}(in the Skin) to generate ripple effect when toggled/un-toggled.
 */
public class MFXToggleButton extends ToggleButton {
    //================================================================================
    // Properties
    //================================================================================
    private static final StyleablePropertyFactory<MFXToggleButton> FACTORY = new StyleablePropertyFactory<>(ToggleButton.getClassCssMetaData());
    private final String STYLE_CLASS = "mfx-toggle-button";
    private final String STYLESHEET = MFXResourcesLoader.load("css/MFXToggleButton.css");

    //================================================================================
    // Constructors
    //================================================================================
    public MFXToggleButton() {
        setText("ToggleButton");
        initialize();
    }

    public MFXToggleButton(String text) {
        super(text);
        initialize();
    }

    public MFXToggleButton(String text, Node graphic) {
        super(text, graphic);
        initialize();
    }

    //================================================================================
    // Methods
    //================================================================================
    private void initialize() {
        this.getStyleClass().add(STYLE_CLASS);

        toggleColor.addListener((observable, oldValue, newValue) -> {
            if (isAutomaticColorAdjustment()) {
                Color color = ((Color) newValue).desaturate().desaturate().brighter();
                toggleLineColor.set(color);
            }
        });
    }

    //================================================================================
    // Styleable Properties
    //================================================================================

    /**
     * Specifies the color of the "circle" when toggled.
     *
     * @see Color
     */
    private final StyleableObjectProperty<Paint> toggleColor = new SimpleStyleableObjectProperty<>(
            StyleableProperties.TOGGLE_COLOR,
            this,
            "toggleColor",
            Color.rgb(0, 150, 136)
    );

    /**
     * Specifies the color of the "circle" when un-toggled.
     *
     * @see Color
     */
    private final StyleableObjectProperty<Paint> unToggleColor = new SimpleStyleableObjectProperty<>(
            StyleableProperties.UNTOGGLE_COLOR,
            this,
            "unToggleColor",
            Color.rgb(250, 250, 250)
    );

    /**
     * Specifies the color of the "line" when toggled.
     *
     * @see Color
     */
    private final StyleableObjectProperty<Paint> toggleLineColor = new SimpleStyleableObjectProperty<>(
            StyleableProperties.TOGGLE_LINE_COLOR,
            this,
            "toggleLineColor",
            Color.rgb(119, 194, 187)
    );

    /**
     * Specifies the color of the line when un-toggled.
     *
     * @see Color
     */
    private final StyleableObjectProperty<Paint> unToggleLineColor = new SimpleStyleableObjectProperty<>(
            StyleableProperties.UNTOGGLE_LINE_COLOR,
            this,
            "unToggleLineColor",
            Color.rgb(153, 153, 153)
    );

    /**
     * Specifies the size of the toggle button.
     * <p>
     * NOTE: Optimal values are from 8 upwards.
     */
    private final StyleableDoubleProperty size = new SimpleStyleableDoubleProperty(
            StyleableProperties.SIZE,
            this,
            "size",
            10.0
    );

    /**
     * When this is set to true and toggle color is changed the un-toggle color is automatically adjusted.
     * <p>
     * NOTE: This works only if changing toggle color, if un-toggle color is changed the toggle color won't be automatically adjusted.
     * You can see this behavior in the demo.
     */
    private final BooleanProperty automaticColorAdjustment = new SimpleBooleanProperty(false);

    public Paint getToggleColor() {
        return toggleColor.get();
    }

    public StyleableObjectProperty<Paint> toggleColorProperty() {
        return toggleColor;
    }

    public void setToggleColor(Paint toggleColor) {
        this.toggleColor.set(toggleColor);
    }

    public Paint getUnToggleColor() {
        return unToggleColor.get();
    }

    public StyleableObjectProperty<Paint> unToggleColorProperty() {
        return unToggleColor;
    }

    public void setUnToggleColor(Paint unToggleColor) {
        this.unToggleColor.set(unToggleColor);
    }

    public Paint getToggleLineColor() {
        return toggleLineColor.get();
    }

    public StyleableObjectProperty<Paint> toggleLineColorProperty() {
        return toggleLineColor;
    }

    public void setToggleLineColor(Paint toggleLineColor) {
        this.toggleLineColor.set(toggleLineColor);
    }

    public Paint getUnToggleLineColor() {
        return unToggleLineColor.get();
    }

    public StyleableObjectProperty<Paint> unToggleLineColorProperty() {
        return unToggleLineColor;
    }

    public void setUnToggleLineColor(Paint unToggleLineColor) {
        this.unToggleLineColor.set(unToggleLineColor);
    }

    public double getSize() {
        return size.get();
    }

    public StyleableDoubleProperty sizeProperty() {
        return size;
    }

    public void setSize(double size) {
        this.size.set(size);
    }

    public boolean isAutomaticColorAdjustment() {
        return automaticColorAdjustment.get();
    }

    public BooleanProperty automaticColorAdjustmentProperty() {
        return automaticColorAdjustment;
    }

    public void setAutomaticColorAdjustment(boolean automaticColorAdjustment) {
        this.automaticColorAdjustment.set(automaticColorAdjustment);
    }

    //================================================================================
    // CssMetaData
    //================================================================================
    private static class StyleableProperties {
        private static final List<CssMetaData<? extends Styleable, ?>> cssMetaDataList;

        private static final CssMetaData<MFXToggleButton, Paint> TOGGLE_COLOR =
                FACTORY.createPaintCssMetaData(
                        "-mfx-toggle-color",
                        MFXToggleButton::toggleColorProperty,
                        Color.rgb(0, 150, 136)
                );

        private static final CssMetaData<MFXToggleButton, Paint> UNTOGGLE_COLOR =
                FACTORY.createPaintCssMetaData(
                        "-mfx-untoggle-color",
                        MFXToggleButton::unToggleColorProperty,
                        Color.rgb(250, 250, 250)
                );

        private static final CssMetaData<MFXToggleButton, Paint> TOGGLE_LINE_COLOR =
                FACTORY.createPaintCssMetaData(
                        "-mfx-toggle-line-color",
                        MFXToggleButton::toggleLineColorProperty,
                        Color.rgb(119, 194, 187)
                );

        private static final CssMetaData<MFXToggleButton, Paint> UNTOGGLE_LINE_COLOR =
                FACTORY.createPaintCssMetaData(
                        "-mfx-untoggle-line-color",
                        MFXToggleButton::unToggleLineColorProperty,
                        Color.rgb(153, 153, 153)
                );

        private static final CssMetaData<MFXToggleButton, Number> SIZE =
                FACTORY.createSizeCssMetaData(
                        "-mfx-size",
                        MFXToggleButton::sizeProperty,
                        10.0
                );

        static {
            List<CssMetaData<? extends Styleable, ?>> tobCssMetaData = new ArrayList<>(ToggleButton.getClassCssMetaData());
            Collections.addAll(tobCssMetaData, TOGGLE_COLOR, UNTOGGLE_COLOR, TOGGLE_LINE_COLOR, UNTOGGLE_LINE_COLOR, SIZE);
            cssMetaDataList = Collections.unmodifiableList(tobCssMetaData);
        }
    }

    public static List<CssMetaData<? extends Styleable, ?>> getControlCssMetaDataList() {
        return StyleableProperties.cssMetaDataList;
    }

    //================================================================================
    // Override Methods
    //================================================================================
    @Override
    protected Skin<?> createDefaultSkin() {
        return new MFXToggleButtonSkin(this);
    }

    @Override
    public String getUserAgentStylesheet() {
        return STYLESHEET;
    }

    @Override
    public List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
        return MFXToggleButton.getControlCssMetaDataList();
    }
}
