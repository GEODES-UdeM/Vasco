/* _________________________________________________________________________
 *
 *             Vasco : A Visual Churn Exploration Tool
 *
 *
 *  This file is part of the Vasco project.
 *
 *  Vasco is distributed at:
 *      http://github.com/GEODES-UdeM/Vasco
 *
 *
 *  Copyright (c) 2012, Universite de Montreal
 *  All rights reserved.
 *
 *  This software is licensed under the following license (Modified BSD
 *  License):
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are
 *  met:
 *    * Redistributions of source code must retain the above copyright
 *      notice, this list of conditions and the following disclaimer.
 *    * Redistributions in binary form must reproduce the above copyright
 *      notice, this list of conditions and the following disclaimer in the
 *      documentation and/or other materials provided with the distribution.
 *    * Neither the name of the Universite de Montreal nor the names of its
 *      contributors may be used to endorse or promote products derived
 *      from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 *  IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 *  TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 *  PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL UNIVERSITE DE
 *  MONTREAL BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 *  EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 *  PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 *  PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 *  LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *  NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * _________________________________________________________________________
 */

package vasco.util;

import java.awt.Color;

public class Properties extends java.util.Properties {
    private static final long serialVersionUID = 4906636582905574740L;

    public Properties() {
        super();
    }

    public Properties(java.util.Properties defaults) {
        super(defaults);
    }

    public boolean getBooleanProperty(String name) {
        return getBooleanProperty(name, false);
    }

    public boolean getBooleanProperty(String name, boolean defaultValue) {
        String val = getProperty(name, String.valueOf(defaultValue));
        return val.equalsIgnoreCase("true"); //$NON-NLS-1$
    }

    public Object setBooleanProperty(String name, boolean value) {
        return setProperty(name, String.valueOf(value));
    }

    private static String toHex(Color color) {
        return "#" + Integer.toHexString((color.getRGB() & 0x00FFFFFF) | 0x1000000).substring(1); //$NON-NLS-1$
    }

    public Color getColorProperty(String name) {
        return getColorProperty(name, Color.BLACK);
    }

    public Color getColorProperty(String name, String defaultColor) {
        String val = getProperty(name, defaultColor);
        return Color.decode(val);
    }

    public Color getColorProperty(String name, Color defaultColor) {
        return getColorProperty(name, toHex(defaultColor));
    }

    public Object setColorProperty(String name, Color color) {
        return setColorProperty(name, toHex(color));
    }

    public Object setColorProperty(String name, String color) {
        return setProperty(name, color);
    }
}
