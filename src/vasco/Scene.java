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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import vasco.metrics.Allocations;
import vasco.metrics.Captures;
import vasco.metrics.Churn;
import vasco.metrics.Metric;
import vasco.metrics.NumberOfTypes;
import vasco.metrics.RegionChurn;
import vasco.model.CGModel;
import vasco.model.Element;
import vasco.util.CCTReader;
import vasco.util.Strings;

public class Scene {
    private List<Metric> colorMetrics;
    private List<Metric> angleMetrics;
    private Preferences prefs;

    private CGModel model;
    private Metric angleMetric;
    private Metric colorMetric;
    private int maxColorValue;
    private UndoManager undoManager = new UndoManager();
    private ContextManager contextManager = new ContextManager();

    private Element elementUnderCursor;
    private Collection<? extends Element> highlightedElements = Collections.emptyList();

    private List<SceneListener> listeners = new ArrayList<SceneListener>(3);

    public Scene() {
        setupMetrics();

        this.prefs = new Preferences(this);
    }

    public Scene(Preferences prefs) {
        setupMetrics();
    }

    public void addListener(SceneListener listener) {
        listeners.add(listener);
    }

    public void removeListener(SceneListener listener) {
        listeners.remove(listener);
    }

    public CGModel getModel() {
        return model;
    }

    public void setModel(CGModel model) {
        this.model = model;
        int maxValue = model.calculateMaxValue(colorMetric);
        colorMetric.setMaxValue(maxValue);
        setMaxColorValue(maxValue);
        setupMetrics();

        for (SceneListener listener: listeners) {
            listener.onModelChanged(this, model);
        }
    }

    public Metric getAngleMetric() {
        return angleMetric;
    }

    public void setAngleMetric(Metric angleMetric) {
        if (this.angleMetric != angleMetric) {
            this.angleMetric = angleMetric;

            for (SceneListener listener: listeners) {
                listener.onAngleMetricChanged(this, angleMetric);
            }
        }
    }

    public Metric getColorMetric() {
        return colorMetric;
    }

    public void setColorMetric(Metric colorMetric) {
        if (this.colorMetric != colorMetric) {
            this.colorMetric = colorMetric;

            for (SceneListener listener: listeners) {
                listener.onColorMetricChanged(this, colorMetric);
            }

            setMaxColorValue(colorMetric.getMaxValue());
        }
    }

    /*default*/ void notifyMetricColorsChanged(Metric metric) {
        for (SceneListener listener: listeners) {
            listener.onMetricColorsModified(this, metric);
        }
    }

    public int getMaxColorValue() {
        return maxColorValue;
    }

    public void setMaxColorValue(int maxColorValue) {
        this.maxColorValue = maxColorValue;

        for (SceneListener listener: listeners) {
            listener.onMaxColorValueChanged(this, maxColorValue);
        }
    }

    public UndoManager getUndoManager() {
        return undoManager;
    }

    public Element getElementUnderCursor() {
        return elementUnderCursor;
    }

    public void setElementUnderCursor(Element element) {
        this.elementUnderCursor = element;
    }

    public Collection<? extends Element> getHighlightedElements() {
        return highlightedElements;
    }

    public void setHighlightedElements(Collection<? extends Element> highlightedElements) {
        this.highlightedElements = highlightedElements;
    }

    public void clearHighlightedElements() {
        this.highlightedElements = Collections.emptyList();
    }

    public ContextManager getContextManager() {
        return contextManager;
    }

    public Preferences getPreferences() {
        return this.prefs;
    }

    public void addColorMetric(Metric metric) {
        if (colorMetrics.isEmpty()) colorMetric = metric;
        this.colorMetrics.add(metric);
    }

    public void addAngleMetric(Metric metric) {
        if (angleMetrics.isEmpty()) angleMetric = metric;
        this.angleMetrics.add(metric);
    }

    public List<Metric> getColorMetrics() {
        return colorMetrics;
    }

    public List<Metric> getAngleMetrics() {
        return angleMetrics;
    }

    private void setupMetrics() {
        this.angleMetrics = new ArrayList<Metric>();
        this.colorMetrics = new ArrayList<Metric>();
        final CGModel model = this.model;

        if (model == null || model.getRoot().getTotalAllocations() > 0) {
            Metric alloc = new Allocations();
            addColorMetric(alloc);
            addAngleMetric(alloc);

            Metric type = new NumberOfTypes();
            addColorMetric(type);
            addAngleMetric(type);
        }

        if (model == null || model.getRoot().getTotalCaptures() > 0) {
            Metric capture = new Captures();
            addColorMetric(capture);
            addAngleMetric(capture);

            Metric churn = new RegionChurn();
            addColorMetric(churn);

            Metric allocCapt = new Churn();
            addColorMetric(allocCapt);
        }
    }

    public void loadModelFromFile(File file) {
        try {
            if (isBundle(file)) {
                setModel(loadBundleFromFile(file));
            } else {
                setModel(loadCCTFromFile(file));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private CGModel loadCCTFromFile(File file) throws IOException {
        CCTReader reader = new CCTReader();
        return reader.readFile(file);
    }

    private CGModel loadBundleFromFile(File file) throws IOException {
        JarFile jar = new JarFile(file);

        ZipEntry cctEntry = jar.getEntry("callgraph.acct"); //$NON-NLS-1$
        if (cctEntry == null) {
            Main.fatalError(Strings.get("Main.invalidBundle")); //$NON-NLS-1$
        }

        InputStream stream = jar.getInputStream(cctEntry);
        CCTReader reader = new CCTReader();
        CGModel cgm = reader.readFile(stream);
        cgm.setJar(jar);
        return cgm;
    }

    private static boolean isBundle(File inputFile) {
        return inputFile.getName().endsWith(".viz"); //$NON-NLS-1$
    }
}
