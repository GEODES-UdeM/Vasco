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

package vasco.render;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import vasco.Scene;
import vasco.metrics.Metric;
import vasco.model.Arc;
import vasco.model.Element;
import vasco.model.GroupOfMethods;
import vasco.model.Method;

public class SunburstLayout {
    private static final double MIN_RADIANS = 0.05;
    private Map<Element, MethodData> metadata;
    private Method root;
    private Scene scene;
    private int maxLevel;

    private int nextGroupID;

    private boolean stale;

    public SunburstLayout(Scene scene, Method root) {
        this.scene = scene;
        this.nextGroupID = this.scene.getModel().getMethods().size();
        this.root = root;
        this.update();
    }

    public Scene getScene() {
        return scene;
    }

    public MethodData getData(Element e) {
        return this.metadata.get(e);
    }

    public MethodData getOrMakeData(Element e) {
        MethodData data = this.getData(e);
        if (data == null) {
            data = new MethodData(e);
            this.metadata.put(e, data);
        }
        return data;
    }

    public void update() {
        this.metadata = new HashMap<Element, SunburstLayout.MethodData>();
        this.layout();
        this.stale = false;
    }

    public void layout() {
        this.dfs(new CGVisitor() {

            @Override
            public void visit(Element e) {
                getOrMakeData(e).arc = null;
            }
        }, this.root);
        this.root.setLevel(0);
        this.computeArcs(this.root, 2 * Math.PI, new PolarCoord(0, 0), 0);

    }

    private void computeArcs(Method root, double degrees, PolarCoord start, int level) {
        if (!root.isEliminated()) {
            MethodData parentData = this.getOrMakeData(root);
            parentData.reset();
            parentData.arc = new Arc(start.angle, level, degrees);

            if (degrees < MIN_RADIANS) {
                parentData.children = Collections.emptyList();
                return;
            }

            final Metric metric = this.scene.getAngleMetric();

            int totalForParent = metric.getTotalValue(root);

            if (totalForParent == 0)
                return;

            root.setLevel(level);
            if (this.maxLevel < level) {
                this.maxLevel = level;
            }

            // Compute total degrees available for children
            int availForChildren = totalForParent - metric.getValue(root);

            double degAvailForChildren = degrees * availForChildren / totalForParent;

            // Compute total metric value for visible children
            int totalForChildren = 0;
            int numberOfClosedArcs = 0;
            for (Method child : root.getChildren()) {
                MethodData data = this.getOrMakeData(child);
                if (!data.isVisible) {
                    numberOfClosedArcs++;
                    continue;
                }
                if (child.isEliminated()) {
                    continue;
                }
                totalForChildren += metric.getTotalValue(child);

            }

            degAvailForChildren -= numberOfClosedArcs * MIN_RADIANS;

            // Compute degrees per child, proportionally
            PolarCoord currentStart = start;

            List<Method> group = new ArrayList<Method>();
            double totalAngle = 0;
            for (Method child : root.getChildren()) {
                MethodData data = this.getOrMakeData(child);
                double degForChild = degAvailForChildren * metric.getTotalValue(child) / totalForChildren;
                if (child.isEliminated()) {
                    continue;
                }
                if (!data.isVisible) {
                    data.arc = new Arc(currentStart.angle, level + 1, MIN_RADIANS);
                    // this.computeArcs(child, MIN_DEGREES, currentStart, level
                    // + 1);

                    currentStart = currentStart.rotateBy(MIN_RADIANS);

                    continue;
                }

                if (totalForChildren == 0) {
                    degForChild = 0;
                }

                if (degForChild < MIN_RADIANS) {
                    group.add(child);
                    totalAngle += degForChild;
                } else {
                    data.arc = new Arc(currentStart.angle, level, degForChild);

                    this.computeArcs(child, degForChild, currentStart, level + 1);
                    currentStart = currentStart.rotateBy(degForChild);
                }
            }

            if (!group.isEmpty() && totalAngle > 0) {
                GroupOfMethods newGroup = new GroupOfMethods(group);
                newGroup.setID(nextGroupID);
                nextGroupID++;

                MethodData groupData = this.getOrMakeData(newGroup);
                groupData.arc = new Arc(currentStart.angle, level + 1, totalAngle);
                parentData.children.add(newGroup);
            }
        }
    }

    public void dfs(CGVisitor v) {
        this.dfs(v, this.root);
    }

    private void dfs(CGVisitor v, Element root) {
        v.visit(root);
        List<? extends Element> children = this.getOrMakeData(root).children;
        for (Element e : children) {
            this.dfs(v, e);
        }
    }

    private class PolarCoord {
        public final double angle;
        public final double dist;

        public PolarCoord(double angle, double dist) {
            this.angle = angle;
            this.dist = dist;
        }

        public PolarCoord rotateBy(double angle) {
            return new PolarCoord(this.angle + angle, dist);
        }
    }

    private class MethodData {
        private Element element;
        public boolean isVisible = true;
        public Arc arc;
        public List<Element> children;

        public MethodData(Element e) {
            this.element = e;
            this.reset();
        }

        public void reset() {
            if (this.element instanceof Method) {
                Method m = (Method) this.element;
                this.children = new ArrayList<Element>(m.getChildren());
            } else {
                this.children = Collections.emptyList();
            }
        }

    }

    public Arc getArc(Element element) {
        MethodData data = this.getData(element);
        if (data != null) {
            return data.arc;
        }

        return null;
    }

    public void setArcVisible(Element element, boolean isVisible) {
        MethodData data = this.getData(element);
        if (data != null && data.isVisible != isVisible) {
            data.isVisible = isVisible;
            layout();
        }
    }

    public boolean isArcVisible(Element element) {
        MethodData data = this.getData(element);
        if (data != null) {
            return data.isVisible;
        }
        return true;
    }

    public static interface CGVisitor {
        public void visit(Element e);

    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public Method getRoot() {
        return root;
    }

    public void setRoot(Method root) {
        this.root = root;
    }

    public boolean isStale() {
        return this.stale;
    }

    public void setStale() {
        this.stale = true;
    }
}
