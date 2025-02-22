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

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Little utils class to convert a throwable stack trace to a String.
 */
public class ExceptionUtils {
    private static final StringWriter sw = new StringWriter();

    private ExceptionUtils() {}

    /**
     * Converts the given exception stack trace to a String
     * by using a {@link StringWriter} and a {@link PrintWriter}.
     */
    public static String getStackTraceString(Throwable ex) {
        sw.flush();
        ex.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }
}
