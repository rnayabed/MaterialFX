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
import io.github.palexdev.materialfx.skins.MFXCheckboxSkin;
import javafx.css.*;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Skin;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This is the implementation of a checkbox following Google's material design guidelines in JavaFX.
 * <p>
 * Extends {@code CheckBox}, redefines the style class to "mfx-checkbox" for usage in CSS and
 * includes a {@code RippleGenerator}(in the Skin) to generate ripple effect on click.
 */
public class MFXCheckbox extends CheckBox {
    //================================================================================
    // Properties
    //================================================================================
    private static final StyleablePropertyFactory<MFXCheckbox> FACTORY = new StyleablePropertyFactory<>(CheckBox.getClassCssMetaData());
    private final String STYLE_CLASS = "mfx-checkbox";
    private final String STYLESHEET = MFXResourcesLoader.load("css/MFXCheckBox.css");

    //================================================================================
    // Constructors
    //================================================================================
    public MFXCheckbox() {
        setText("CheckBox");
        initialize();
    }

    public MFXCheckbox(String text) {
        super(text);
        initialize();
    }

    //================================================================================
    // Methods
    //================================================================================
    private void initialize() {
        getStyleClass().add(STYLE_CLASS);
    }

    //================================================================================
    // Stylesheet properties
    //================================================================================

    /**
     * Specifies the color of the box when it's checked.
     *
     * @see Color
     */
    private final StyleableObjectProperty<Paint> checkedColor = new SimpleStyleableObjectProperty<>(
            StyleableProperties.CHECKED_COLOR,
            this,
            "checkedColor",
            Color.rgb(15, 157, 88)
    );

    /**
     * Specifies the color of the box when it's unchecked.
     *
     * @see Color
     */
    private final StyleableObjectProperty<Paint> uncheckedColor = new SimpleStyleableObjectProperty<>(
            StyleableProperties.UNCHECKED_COLOR,
            this,
            "uncheckedColor",
            Color.rgb(90, 90, 90)
    );

    /**
     * Specifies the shape of the mark from a predefined set.
     *
     * @see javafx.scene.shape.SVGPath
     */
    private final StyleableStringProperty markType = new SimpleStyleableStringProperty(
            StyleableProperties.MARK_TYPE,
            this,
            "markType",
            "mfx-modena-mark"
    );

    /**
     * Specifies the size of the mark.
     */
    private final StyleableDoubleProperty markSize = new SimpleStyleableDoubleProperty(
            StyleableProperties.MARK_SIZE,
            this,
            "markSize",
            12.0
    );

    public Paint getCheckedColor() {
        return checkedColor.get();
    }

    public StyleableObjectProperty<Paint> checkedColorProperty() {
        return checkedColor;
    }

    public void setCheckedColor(Paint checkedColor) {
        this.checkedColor.set(checkedColor);
    }

    public Paint getUncheckedColor() {
        return uncheckedColor.get();
    }

    public StyleableObjectProperty<Paint> uncheckedColorProperty() {
        return uncheckedColor;
    }

    public void setUncheckedColor(Paint uncheckedColor) {
        this.uncheckedColor.set(uncheckedColor);
    }

    public String getMarkType() {
        return markType.get();
    }

    public StyleableStringProperty markTypeProperty() {
        return markType;
    }

    public void setMarkType(String markType) {
        this.markType.set(markType);
    }

    public double getMarkSize() {
        return markSize.get();
    }

    public StyleableDoubleProperty markSizeProperty() {
        return markSize;
    }

    public void setMarkSize(double markSize) {
        this.markSize.set(markSize);
    }

    //================================================================================
    // CssMetaData
    //================================================================================
    private static class StyleableProperties {
        private static final List<CssMetaData<? extends Styleable, ?>> cssMetaDataList;

        private static final CssMetaData<MFXCheckbox, Paint> CHECKED_COLOR =
                FACTORY.createPaintCssMetaData(
                        "-mfx-checked-color",
                        MFXCheckbox::checkedColorProperty,
                        Color.rgb(15, 157, 88)
                );

        private static final CssMetaData<MFXCheckbox, Paint> UNCHECKED_COLOR =
                FACTORY.createPaintCssMetaData(
                        "-mfx-unchecked-color",
                        MFXCheckbox::uncheckedColorProperty,
                        Color.rgb(90, 90, 90)
                );

        private static final CssMetaData<MFXCheckbox, String> MARK_TYPE =
                FACTORY.createStringCssMetaData(
                        "-mfx-mark-type",
                        MFXCheckbox::markTypeProperty,
                        "mfx-modena-mark"
                );

        private static final CssMetaData<MFXCheckbox, Number> MARK_SIZE =
                FACTORY.createSizeCssMetaData(
                        "-mfx-mark-size",
                        MFXCheckbox::markSizeProperty,
                        12
                );

        static {
            List<CssMetaData<? extends Styleable, ?>> ckbCssMetaData = new ArrayList<>(CheckBox.getClassCssMetaData());
            Collections.addAll(ckbCssMetaData, CHECKED_COLOR, UNCHECKED_COLOR, MARK_TYPE, MARK_SIZE);
            cssMetaDataList = Collections.unmodifiableList(ckbCssMetaData);
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
        return new MFXCheckboxSkin(this);
    }

    @Override
    public String getUserAgentStylesheet() {
        return STYLESHEET;
    }

    @Override
    public List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
        return MFXCheckbox.getControlCssMetaDataList();
    }
}
