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

/*
 *  Vasco - A Visual Churn Exploration Tool
 *  Copyright (C) 2012  Fleur Duseau (duseaufl@iro.umontreal.ca)
 *  Copyright (C) 2012  Bruno Dufour (dufour@iro.umontreal.ca)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package vasco;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import vasco.metrics.Metric;
import vasco.util.Properties;
import vasco.util.Strings;

public class Preferences {
    public static final boolean DEBUG = false;

    private static final File prefsFile = new File(System.getProperty("user.home",  "."), ".vasco.prefs"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

    private boolean useShortNames;

    private Color captureColor;
    private Color highlightColor;
    private Color selectionColor;
    private Color groupColor;
    private Color hoverColor;
    private Color contextHighlightColor;
    private Color contextBaseColor;

    private Map<Class<? extends Metric>, Color> minColors = new HashMap<Class<? extends Metric>, Color>();
    private Map<Class<? extends Metric>, Color> maxColors = new HashMap<Class<? extends Metric>, Color>();

    private static final Properties DEFAULTS = new Properties();

    static {
        DEFAULTS.setBooleanProperty("useShortNames", true); //$NON-NLS-1$

        DEFAULTS.setColorProperty("captureColor", "#EF00FF"); //$NON-NLS-1$ //$NON-NLS-2$
        DEFAULTS.setColorProperty("highlightColor", "#FB7507"); //$NON-NLS-1$ //$NON-NLS-2$
        DEFAULTS.setColorProperty("selectionColor", "#A5F1FF"); //$NON-NLS-1$ //$NON-NLS-2$
        DEFAULTS.setColorProperty("groupColor", "#D9D9D9"); //$NON-NLS-1$ //$NON-NLS-2$
        DEFAULTS.setColorProperty("hoverColor", "#EEEEED"); //$NON-NLS-1$ //$NON-NLS-2$
        DEFAULTS.setColorProperty("contextHighlightColor", "#646464"); //$NON-NLS-1$ //$NON-NLS-2$
        DEFAULTS.setColorProperty("contextBaseColor", "#e1e1e1"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    private Scene scene;

    public Preferences(Scene scene) {
        this.scene = scene;

        load();
    }

    private void load() {
        if (prefsFile.exists()) {
            loadFromFile(prefsFile);
        } else {
            loadDefaults();
        }
    }

    public void save() {
        writeToFile(prefsFile);
    }

    public boolean useShortNames() {
        return this.useShortNames;
    }

    public void useShortNames(boolean doIt) {
        this.useShortNames = doIt;
    }

    public Color getCaptureColor() {
        return captureColor;
    }

    public void setCaptureColor(Color captureColor) {
        this.captureColor = captureColor;
    }

    public Color getHighlightColor() {
        return highlightColor;
    }

    public void setHighlightColor(Color highlightColor) {
        this.highlightColor = highlightColor;
    }

    public Color getSelectionColor() {
        return selectionColor;
    }

    public void setSelectionColor(Color selectionColor) {
        this.selectionColor = selectionColor;
    }

    public Color getGroupColor() {
        return groupColor;
    }

    public void setGroupColor(Color groupColor) {
        this.groupColor = groupColor;
    }

    public Color getHoverColor() {
        return hoverColor;
    }

    public void setHoverColor(Color hoverColor) {
        this.hoverColor = hoverColor;
    }

    public Color getContextHighlightColor() {
        return contextHighlightColor;
    }

    public void setContextHighlightColor(Color contextHighlightColor) {
        this.contextHighlightColor = contextHighlightColor;
    }

    public Color getContextBaseColor() {
        return contextBaseColor;
    }

    public void setContextBaseColor(Color contextBaseColor) {
        this.contextBaseColor = contextBaseColor;
    }

    public Color getMinColor(Metric metric) {
        return minColors.get(metric.getClass());
    }

    public Color getMaxColor(Metric metric) {
        return maxColors.get(metric.getClass());
    }

    public void setMinColor(Metric metric, Color color) {
        minColors.put(metric.getClass(), color);
    }

    public void setMaxColor(Metric metric, Color color) {
        maxColors.put(metric.getClass(), color);
    }

    public void loadFromFile(String filename) {
        loadFromFile(new File(filename));
    }

    public void loadFromFile(File file) {
        Properties props = new Properties(DEFAULTS);
        try {
            props.load(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            // FIXME: ignore?
        } catch (IOException e) {
            Main.fatalError(Strings.get("Preferences.failedToRead")); //$NON-NLS-1$
        }

        loadFromProperties(props);
    }

    public void loadDefaults() {
        loadFromProperties(DEFAULTS);
    }

    private void loadFromProperties(Properties props) {
        loadOptions(props);
        loadColors(props);
        loadMetricSettings(props);
    }

    private void loadOptions(Properties props) {
        useShortNames = props.getBooleanProperty("useShortNames"); //$NON-NLS-1$
    }

    private void loadColors(Properties props) {
        captureColor = props.getColorProperty("captureColor"); //$NON-NLS-1$
        highlightColor = props.getColorProperty("highlightColor"); //$NON-NLS-1$
        selectionColor = props.getColorProperty("selectionColor"); //$NON-NLS-1$
        groupColor = props.getColorProperty("groupColor"); //$NON-NLS-1$
        hoverColor = props.getColorProperty("hoverColor"); //$NON-NLS-1$
        contextHighlightColor = props.getColorProperty("contextHighlightColor"); //$NON-NLS-1$
        contextBaseColor = props.getColorProperty("contextBaseColor"); //$NON-NLS-1$
    }


    private void loadMetricSettings(Properties props) {
        for (Metric m: scene.getColorMetrics()) {
            String key = "metric." + m.getClass().getSimpleName(); //$NON-NLS-1$

            setMinColor(m, props.getColorProperty(key + ".min", m.getDefaultMinColor())); //$NON-NLS-1$
            setMaxColor(m, props.getColorProperty(key + ".max", m.getDefaultMaxColor())); //$NON-NLS-1$
        }
    }

    public void writeToFile(String filename) {
        writeToFile(new File(filename));
    }

    public void writeToFile(File file) {
        Properties props = new Properties();

        saveOptions(props);
        saveColors(props);
        saveMetricSettings(props);

        try {
            props.store(new FileOutputStream(file), Strings.get("Preferences.fileHeader")); //$NON-NLS-1$
        } catch (IOException e) {
            // FIXME: don't kill the app with unsaved prefs
            Main.fatalError(Strings.get("Preferences.failedToWrite")); //$NON-NLS-1$
        }
    }

    private void saveOptions(Properties props) {
        props.setBooleanProperty("useShortNames", useShortNames); //$NON-NLS-1$
    }

    private void saveColors(Properties props) {
        props.setColorProperty("captureColor", captureColor); //$NON-NLS-1$
        props.setColorProperty("highlightColor", highlightColor); //$NON-NLS-1$
        props.setColorProperty("selectionColor", selectionColor); //$NON-NLS-1$
        props.setColorProperty("groupColor", groupColor); //$NON-NLS-1$
        props.setColorProperty("hoverColor", hoverColor); //$NON-NLS-1$
        props.setColorProperty("contextHighlightColor", contextHighlightColor); //$NON-NLS-1$
        props.setColorProperty("contextBaseColor", contextBaseColor); //$NON-NLS-1$
    }

    private void saveMetricSettings(Properties props) {
        for (Metric m: scene.getColorMetrics()) {
            String key = "metric." + m.getClass().getSimpleName(); //$NON-NLS-1$

            props.setColorProperty(key + ".min", getMinColor(m)); //$NON-NLS-1$
            props.setColorProperty(key + ".max", getMaxColor(m)); //$NON-NLS-1$
        }
    }
}
