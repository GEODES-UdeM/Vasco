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

package vasco.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;

import javax.swing.JPanel;

import vasco.Scene;
import vasco.SceneAdapter;
import vasco.metrics.Metric;
import vasco.model.Element;
import vasco.render.SunburstLayout;
import vasco.util.Strings;

/**
 * PanelInfo représente le panel affichant les informations sur les méthodes
 *
 * @author fleur
 */
public class InfoPanel extends JPanel implements MouseListener, MouseMotionListener {

    private static final long serialVersionUID = 1L;
    private Scene scene;
    private Canvas canvas;
    private String activeMethodName = ""; //$NON-NLS-1$
    private String name = Strings.get("info.noCCTLoaded"); //$NON-NLS-1$
    private String children = ""; //$NON-NLS-1$
    private String numberOfAlloc = ""; //$NON-NLS-1$
    private String numberOfCapture = ""; //$NON-NLS-1$
    private String numberOfTypes = ""; //$NON-NLS-1$

    // coordonnées du rectangle pour l'info sur les couleurs + la largeur de ce
    // rect
    private int x = 700;
    private int y = 45;
    private int rectWidth = 250;
    private int rectHeight = 20;
    private int maxSelectedValue;
    private int maxValue; // true max

    public InfoPanel(Scene scene, Canvas canvas) {
        this.scene = scene;
        this.canvas = canvas;

        addMouseListener(this);
        addMouseMotionListener(this);

        scene.addListener(new SceneAdapter() {
            @Override
            public void onMaxColorValueChanged(Scene scene, int value) {
                repaint();
            }
        });
    }

    @Override
    public void paint(Graphics g) {
        g.clearRect(0, 0, 1500, 200);

        g.setColor(Color.BLACK);

        Font font2 = new Font("TimesRoman", Font.PLAIN, 15); //$NON-NLS-1$
        g.setFont(font2);
        g.drawString(name, 1, 25);

//        g.fillRect(x, y, rectWidth, rectHeight);

        g.drawString(children, 1, 48);
        g.drawString(numberOfAlloc, 250, 48);
        g.drawString(numberOfCapture, 1, 70);
        g.drawString(numberOfTypes, 250, 70);

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setPaint(Color.gray);

        Metric colorMetric = scene.getColorMetric();
        Color minColor = scene.getPreferences().getMinColor(colorMetric);

        Color maxColor = scene.getPreferences().getMaxColor(colorMetric);

        this.maxSelectedValue = this.canvas.getScene().getMaxColorValue();
        this.maxValue = colorMetric.getMaxValue();

        if (this.maxSelectedValue >= 0 && this.maxValue > 0) {
            int width = maxSelectedValue * 250 / maxValue;
            GradientPaint gradient = new GradientPaint(x, y, minColor, x + width, y, maxColor);

            if (maxSelectedValue > 0) {
                g2.setPaint(gradient);
            } else {
                g2.setPaint(maxColor);
            }
            g2.fill(new Rectangle2D.Double(x, y, rectWidth, rectHeight));

            g2.setPaint(Color.black);
            g2.drawLine(x, y - 3, x, y + rectHeight + 3);
            g2.drawLine(x + width, y - 3, x + width, y + rectHeight + 3);
            g2.drawLine(x + rectWidth, y - 3, x + rectWidth, y + rectHeight + 3);
            g2.setFont(new Font("TimesRoman", Font.PLAIN, 10)); //$NON-NLS-1$
            g2.drawString("0", x - 5, 77); //$NON-NLS-1$
            g2.drawString("" + maxSelectedValue, x + width - 2, 40); //$NON-NLS-1$
            g2.drawString("" + maxValue, x + rectWidth, 77); //$NON-NLS-1$
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        mouseDragged(e);
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    /**
     * Modifieur de la variable setValMaxReelle
     *
     * @param maxValue
     *            nouvelle valeur de la variable setValMaxReelle
     */
    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
    }

    @Override
    public void mouseDragged(MouseEvent mouseEvent) {
        int xEvent = mouseEvent.getX();
        int yEvent = mouseEvent.getY();
        if (xEvent <= this.x + rectWidth && xEvent >= this.x && yEvent <= this.y + rectHeight && yEvent >= this.y) {
            int newVal = (xEvent - this.x) * this.maxValue / rectWidth;
            this.canvas.getScene().setMaxColorValue(newVal);
        } else if (yEvent <= this.y + rectHeight && yEvent >= this.y & xEvent > this.x + rectWidth && xEvent < this.x + rectWidth + 20) {
            this.canvas.getScene().setMaxColorValue(this.maxValue);
        }
    }

    @Override
    public void mouseMoved(MouseEvent arg0) {

    }

    /**
     * Modifieur de la variable nom
     *
     * @param name
     *            nouvelle valeur de la variable nom
     */
    public void setNom(String name) {
        this.name = name;
    }

    /**
     * Modifieur de la variable enfants
     *
     * @param children
     *            nouvelle valeur de la variable enfants
     */
    public void setChildren(String children) {
        this.children = children;
    }

    /**
     * Modifieur de la variable nbAlloc
     *
     * @param numberOfAlloc
     *            nouvelle valeur de la variable nbAlloc
     */
    public void setNumberOfAlloc(String numberOfAlloc) {
        this.numberOfAlloc = numberOfAlloc;
    }

    /**
     * Modifieur de la variable nbCapt
     *
     * @param numberOfCapture
     *            nouvelle valeur de la variable nbCapt
     */
    public void setNumberOfCapture(String numberOfCapture) {
        this.numberOfCapture = numberOfCapture;
    }

    /**
     * Accesseur de la variable nbTypes
     *
     * @return la valeur de la variable nbTypes
     */
    public String getNumberOfTypes() {
        return numberOfTypes;
    }

    /**
     * Modifieur de la variable nbTypes
     *
     * @param numberOfTypes
     *            nouvelle valeur de la variable nbTypes
     */
    public void setNumberOfTypes(String numberOfTypes) {
        this.numberOfTypes = numberOfTypes;
    }

    public void update(Element element) {
        final SunburstLayout layout = canvas.getLayout();
        if (element != null) {
            this.activeMethodName = formatMethodName(element);
            if (layout.isArcVisible(element)) {
                this.children = Strings.get("info.nbChildren") + " : " + element.getNumberOfChildren(); //$NON-NLS-1$ //$NON-NLS-2$
                this.numberOfAlloc = Strings.get("info.nbAlloc") + " : " + formatRatio(element.getNumberOfAllocations(), element.getTotalAllocations(), layout.getRoot().getTotalAllocations()); //$NON-NLS-1$ //$NON-NLS-2$
                this.numberOfCapture = Strings.get("info.nbCapture") + " : " + formatRatio(element.getNumberOfCaptures(), element.getTotalCaptures(), layout.getRoot().getTotalCaptures()); //$NON-NLS-1$ //$NON-NLS-2$
                this.numberOfTypes = Strings.get("info.nbTypes") + " : " + formatRatio(element.getNumberOfTypes(), element.getTotalTypes(), layout.getRoot().getTotalTypes()); //$NON-NLS-1$ //$NON-NLS-2$
            } else {
                this.children = ""; //$NON-NLS-1$
                this.numberOfAlloc = ""; //$NON-NLS-1$
                this.numberOfCapture = ""; //$NON-NLS-1$
                this.numberOfTypes = ""; //$NON-NLS-1$
            }
        } else {
            this.activeMethodName = Strings.get("info.metricTotal"); //$NON-NLS-1$
            this.children = Strings.get("info.nbChildren") + " : "; //$NON-NLS-1$ //$NON-NLS-2$
            this.numberOfAlloc = Strings.get("info.nbAlloc") + " : " + layout.getRoot().getTotalAllocations(); //$NON-NLS-1$ //$NON-NLS-2$
            this.numberOfCapture = Strings.get("info.nbCapture") + " : " + layout.getRoot().getTotalCaptures(); //$NON-NLS-1$ //$NON-NLS-2$
            this.numberOfTypes = Strings.get("info.nbTypes") + " : " + layout.getRoot().getTotalTypes(); //$NON-NLS-1$ //$NON-NLS-2$
        }

        this.setNom(activeMethodName);
        this.setChildren(children);
        this.setNumberOfAlloc(numberOfAlloc);
        this.setNumberOfCapture(numberOfCapture);
        this.setNumberOfTypes(numberOfTypes);
        this.repaint();
    }

    private String formatMethodName(Element element) {
        int formatOptions = Strings.SHORT_ARG_NAMES;
        if (scene.getPreferences().useShortNames()) {
            formatOptions |= Strings.SHORT_CLASS_NAME;
        }
        return Strings.getElementName(element, formatOptions);
    }

    private static final DecimalFormat percent = new DecimalFormat("0.0%"); //$NON-NLS-1$

    private static String formatRatio(int quantity, int total, int toplevel) {
        if (toplevel > 0) {
            return quantity + "/" + total + "/" + percent.format(total / (double) toplevel); //$NON-NLS-1$ //$NON-NLS-2$
        } else {
            return Strings.get("info.notAvail");
        }
    }
}
