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

package io.github.palexdev.materialfx.utils;

import io.github.palexdev.materialfx.controls.base.AbstractMFXFlowlessListView;
import io.github.palexdev.materialfx.controls.factories.MFXAnimationFactory;
import io.github.palexdev.materialfx.controls.flowless.VirtualFlow;
import javafx.animation.Animation;
import javafx.animation.Animation.Status;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.control.Control;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.util.Duration;
import org.reactfx.value.Var;

import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Utility class for ScrollPanes and MFXFlowlessListViews.
 */
public class ScrollUtils {

    public enum ScrollDirection {
        UP(-1), RIGHT(-1), DOWN(1), LEFT(1);

        final int intDirection;

        ScrollDirection(int intDirection) {
            this.intDirection = intDirection;
        }
    }

    private ScrollUtils() {
    }

    /**
     * Determines if the given ScrollEvent comes from a trackpad.
     * <p></p>
     * Although this method works in most cases, it is not very accurate.
     * Since in JavaFX there's no way to tell if a ScrollEvent comes from a trackpad or a mouse
     * we use this trick: I noticed that a mouse scroll has a delta of 32 (don't know if it changes depending on the device or OS)
     * and trackpad scrolls have a way smaller delta. So depending on the scroll direction we check if the delta is lesser than 10
     * (trackpad event) or greater(mouse event).
     *
     * @see ScrollEvent#getDeltaX()
     * @see ScrollEvent#getDeltaY()
     */
    public static boolean isTrackPad(ScrollEvent event, ScrollDirection scrollDirection) {
        switch (scrollDirection) {
            case UP:
            case DOWN:
                return Math.abs(event.getDeltaY()) < 10;
            case LEFT:
            case RIGHT:
                return Math.abs(event.getDeltaX()) < 10;
            default:
                return false;
        }
    }

    /**
     * Determines the scroll direction of the given ScrollEvent.
     * <p></p>
     * Although this method works fine, it is not very accurate.
     * In JavaFX there's no concept of scroll direction, if you try to scroll with a trackpad
     * you'll notice that you can scroll in both directions at the same time, both deltaX and deltaY won't be 0.
     * <p></p>
     * For this method to work we assume that this behavior is not possible.
     * <p></p>
     * If deltaY is 0 we return LEFT or RIGHT depending on deltaX (respectively if lesser or greater than 0).
     * <p>
     * Else we return DOWN or UP depending on deltaY (respectively if lesser or greater than 0).
     *
     * @see ScrollEvent#getDeltaX()
     * @see ScrollEvent#getDeltaY()
     */
    public static ScrollDirection determineScrollDirection(ScrollEvent event) {
        double deltaX = event.getDeltaX();
        double deltaY = event.getDeltaY();

        if (deltaY == 0.0) {
            return deltaX < 0 ? ScrollDirection.LEFT : ScrollDirection.RIGHT;
        } else {
            return deltaY < 0 ? ScrollDirection.DOWN : ScrollDirection.UP;
        }
    }

    //================================================================================
    // ScrollPanes
    //================================================================================

    /**
     * Adds a smooth scrolling effect to the given scroll pane,
     * calls {@link #addSmoothScrolling(ScrollPane, double)} with a
     * default speed value of 1.
     */
    public static void addSmoothScrolling(ScrollPane scrollPane) {
        addSmoothScrolling(scrollPane, 1);
    }

    /**
     * Adds a smooth scrolling effect to the given scroll pane with the given scroll speed.
     * Calls {@link #addSmoothScrolling(ScrollPane, double, double)}
     * with a default trackPadAdjustment of 7.
     */
    public static void addSmoothScrolling(ScrollPane scrollPane, double speed) {
        addSmoothScrolling(scrollPane, speed, 7);
    }

    /**
     * Adds a smooth scrolling effect to the given scroll pane with the given
     * scroll speed and the given trackPadAdjustment.
     * <p></p>
     * The trackPadAdjustment is a value used to slow down the scrolling if a trackpad is used.
     * This is kind of a workaround and it's not perfect, but at least it's way better than before.
     * The default value is 7, tested up to 10, further values can cause scrolling misbehavior.
     */
    public static void addSmoothScrolling(ScrollPane scrollPane, double speed, double trackPadAdjustment) {
        smoothScroll(scrollPane, speed, trackPadAdjustment);
    }

    private static void smoothScroll(ScrollPane scrollPane, double speed, double trackPadAdjustment) {
        final double[] frictions = {0.99, 0.1, 0.05, 0.04, 0.03, 0.02, 0.01, 0.04, 0.01, 0.008, 0.008, 0.008, 0.008, 0.0006, 0.0005, 0.00003, 0.00001};
        final double[] derivatives = new double[frictions.length];
        AtomicReference<Double> atomicSpeed = new AtomicReference<>(speed);

        Timeline timeline = new Timeline();
        AtomicReference<ScrollDirection> scrollDirection = new AtomicReference<>();
        final EventHandler<MouseEvent> mouseHandler = event -> timeline.stop();
        final EventHandler<ScrollEvent> scrollHandler = event -> {
            if (event.getEventType() == ScrollEvent.SCROLL) {
                scrollDirection.set(determineScrollDirection(event));
                if (isTrackPad(event, scrollDirection.get())) {
                    atomicSpeed.set(speed / trackPadAdjustment);
                } else {
                    atomicSpeed.set(speed);
                }
                derivatives[0] += scrollDirection.get().intDirection * atomicSpeed.get();
                if (timeline.getStatus() == Status.STOPPED) {
                    timeline.play();
                }
                event.consume();
            }
        };
        if (scrollPane.getContent().getParent() != null) {
            scrollPane.getContent().getParent().addEventHandler(MouseEvent.MOUSE_PRESSED, mouseHandler);
            scrollPane.getContent().getParent().addEventHandler(ScrollEvent.ANY, scrollHandler);
        }
        scrollPane.getContent().parentProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue != null) {
                oldValue.removeEventHandler(MouseEvent.MOUSE_PRESSED, mouseHandler);
                oldValue.removeEventHandler(ScrollEvent.ANY, scrollHandler);
            }
            if (newValue != null) {
                newValue.addEventHandler(MouseEvent.MOUSE_PRESSED, mouseHandler);
                newValue.addEventHandler(ScrollEvent.ANY, scrollHandler);
            }
        });

        timeline.getKeyFrames().add(new KeyFrame(Duration.millis(3), event -> {
            for (int i = 0; i < derivatives.length; i++) {
                derivatives[i] *= frictions[i];
            }
            for (int i = 1; i < derivatives.length; i++) {
                derivatives[i] += derivatives[i - 1];
            }

            double dy = derivatives[derivatives.length - 1];
            Function<Bounds, Double> sizeFunction = (scrollDirection.get() == ScrollDirection.UP || scrollDirection.get() == ScrollDirection.DOWN) ? Bounds::getHeight : Bounds::getWidth;
            double size = sizeFunction.apply(scrollPane.getContent().getLayoutBounds());
            double value;
            switch (scrollDirection.get()) {
                case LEFT:
                case RIGHT:
                    value = Math.min(Math.max(scrollPane.hvalueProperty().get() + dy / size, 0), 1);
                    scrollPane.hvalueProperty().set(value);
                    break;
                case UP:
                case DOWN:
                    value = Math.min(Math.max(scrollPane.vvalueProperty().get() + dy / size, 0), 1);
                    scrollPane.vvalueProperty().set(value);
                    break;
            }

            if (Math.abs(dy) < 0.001) {
                timeline.stop();
            }
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
    }

    /**
     * Adds a fade in and out effect to the given scroll pane's scroll bars,
     * calls {@link #animateScrollBars(ScrollPane, double)} with a
     * default fadeSpeedMillis value of 500.
     */
    public static void animateScrollBars(ScrollPane scrollPane) {
        animateScrollBars(scrollPane, 500);
    }

    /**
     * Adds a fade in and out effect to the given scroll pane's scroll bars,
     * calls {@link #animateScrollBars(ScrollPane, double, double)} with a
     * default fadeSpeedMillis value of 500 and a default hideAfterMillis value of 800.
     */
    public static void animateScrollBars(ScrollPane scrollPane, double fadeSpeedMillis) {
        animateScrollBars(scrollPane, fadeSpeedMillis, 800);
    }

    /**
     * Adds a fade in and out effect to the given scroll pane's scroll bars
     * with the given fadeSpeedMillis and hideAfterMillis values.
     *
     * @see NodeUtils#waitForSkin(Control, Runnable, boolean, boolean)
     */
    public static void animateScrollBars(ScrollPane scrollPane, double fadeSpeedMillis, double hideAfterMillis) {
        NodeUtils.waitForSkin(scrollPane, () -> {
            Set<ScrollBar> scrollBars = scrollPane.lookupAll(".scroll-bar").stream()
                    .filter(node -> node instanceof ScrollBar)
                    .map(node -> (ScrollBar) node)
                    .collect(Collectors.toSet());
            scrollBars.forEach(scrollBar -> {
                scrollBar.setOpacity(0.0);
                scrollPane.hoverProperty().addListener((observable, oldValue, newValue) -> {
                    if (!scrollBar.isVisible()) {
                        return;
                    }

                    if (newValue) {
                        MFXAnimationFactory.FADE_IN.build(scrollBar, fadeSpeedMillis).play();
                    } else {
                        AnimationUtils.PauseBuilder.build()
                                .setDuration(hideAfterMillis)
                                .setOnFinished(event -> MFXAnimationFactory.FADE_OUT.build(scrollBar, fadeSpeedMillis).play())
                                .getAnimation()
                                .play();
                    }
                });
            });
        }, true, true);
    }

    //================================================================================
    // ListViews
    //================================================================================

    /**
     * Adds a smooth scrolling effect to the given scroll pane,
     * calls {@link #addSmoothScrolling(ScrollPane, double)} with a
     * default speed value of 1.
     */
    public static void addSmoothScrolling(AbstractMFXFlowlessListView<?, ?, ?> listView) {
        addSmoothScrolling(listView, 1);
    }

    /**
     * Adds a smooth scrolling effect to the given scroll pane with the given scroll speed.
     * Calls {@link #addSmoothScrolling(ScrollPane, double, double)}
     * with a default trackPadAdjustment of 7.
     */
    public static void addSmoothScrolling(AbstractMFXFlowlessListView<?, ?, ?> listView, double speed) {
        addSmoothScrolling(listView, speed, 7);
    }

    /**
     * Adds a smooth scrolling effect to the given scroll pane with the given
     * scroll speed and the given trackPadAdjustment.
     * <p></p>
     * The trackPadAdjustment is a value used to slow down the scrolling if a trackpad is used.
     * This is kind of a workaround and it's not perfect, but at least it's way better than before.
     * The default value is 7, tested up to 10, further values can cause scrolling misbehavior.
     */
    public static void addSmoothScrolling(AbstractMFXFlowlessListView<?, ?, ?> listView, double speed, double trackPadAdjustment) {
        NodeUtils.waitForSkin(listView, () -> {
            VirtualFlow<?, ?> flow = (VirtualFlow<?, ?>) listView.lookup(".virtual-flow");
            smoothScroll(flow, speed, trackPadAdjustment);
        }, true, true);
    }

    private static void smoothScroll(VirtualFlow<?, ?> flow, double speed, double trackPadAdjustment) {
        final double[] frictions = {0.99, 0.1, 0.05, 0.04, 0.03, 0.02, 0.01, 0.04, 0.01, 0.008, 0.008, 0.008, 0.008, 0.0006, 0.0005, 0.00003, 0.00001};
        final double[] derivatives = new double[frictions.length];
        AtomicReference<Double> atomicSpeed = new AtomicReference<>(speed);

        Timeline timeline = new Timeline();
        AtomicReference<ScrollDirection> scrollDirection = new AtomicReference<>();
        final EventHandler<MouseEvent> mouseHandler = event -> timeline.stop();
        final EventHandler<ScrollEvent> scrollHandler = event -> {
            if (event.getEventType() == ScrollEvent.SCROLL) {
                scrollDirection.set(determineScrollDirection(event));
                if (isTrackPad(event, scrollDirection.get())) {
                    atomicSpeed.set(speed / trackPadAdjustment);
                } else {
                    atomicSpeed.set(speed);
                }
                derivatives[0] += scrollDirection.get().intDirection * atomicSpeed.get();
                if (timeline.getStatus() == Animation.Status.STOPPED) {
                    timeline.play();
                }
                event.consume();
            }
        };

        if (flow.getParent() != null) {
            flow.getParent().addEventFilter(MouseEvent.MOUSE_PRESSED, mouseHandler);
        }
        flow.parentProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue != null) {
                oldValue.removeEventFilter(MouseEvent.MOUSE_PRESSED, mouseHandler);
            }
            if (newValue != null) {
                newValue.addEventFilter(MouseEvent.MOUSE_PRESSED, mouseHandler);
            }
        });
        flow.addEventFilter(MouseEvent.MOUSE_PRESSED, mouseHandler);
        flow.addEventFilter(ScrollEvent.ANY, scrollHandler);

        timeline.getKeyFrames().add(new KeyFrame(Duration.millis(3), (event) -> {
            for (int i = 0; i < derivatives.length; i++) {
                derivatives[i] *= frictions[i];
            }
            for (int i = 1; i < derivatives.length; i++) {
                derivatives[i] += derivatives[i - 1];
            }

            double dy = derivatives[derivatives.length - 1];
            Var<Double> scrollVar = (scrollDirection.get() == ScrollDirection.UP || scrollDirection.get() == ScrollDirection.DOWN) ? flow.estimatedScrollYProperty() : flow.estimatedScrollXProperty();
            scrollVar.setValue(scrollVar.getValue() + dy);

            if (Math.abs(dy) < 0.001) {
                timeline.stop();
            }
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
    }
}
