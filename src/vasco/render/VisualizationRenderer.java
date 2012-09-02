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

package vasco.render;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.nio.IntBuffer;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLException;

import vasco.Preferences;
import vasco.Scene;
import vasco.model.Element;
import vasco.model.GroupOfMethods;
import vasco.model.Method;
import vasco.util.Colors;
import vasco.view.Canvas;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.util.awt.Screenshot;

public class VisualizationRenderer extends SunburstRenderer implements GLEventListener {
    public static enum DisplayMode {
        NORMAL,
        SCREENSHOT,
        SELECTION
    }

    private Canvas canvas;
    private DisplayMode mode = DisplayMode.NORMAL;

    public VisualizationRenderer(Canvas canvas, Camera camera) {
        super(camera);
        this.canvas = canvas;
    }

    public DisplayMode getMode() {
        return mode;
    }

    public void setMode(DisplayMode mode) {
        this.mode = mode;
    }

    @Override
    protected SunburstLayout getLayout() {
        return canvas.getLayout();
    }

    /**
     * la fonction display est une fonction d'openGL qui permet de dessiner le
     * drawable
     */
    @Override
    public void display(GLAutoDrawable drawable) {
        SunburstLayout layout = canvas.getLayout();

        if (layout != null && layout.isStale()) {
            layout.update();
        }

        if (this.mode == DisplayMode.SCREENSHOT) {
            try {
                GregorianCalendar date = new GregorianCalendar();
                String filename = "screenshot_" //$NON-NLS-1$
                        + date.get(Calendar.DAY_OF_MONTH) + "_" //$NON-NLS-1$
                        + date.get(Calendar.MONTH) + "_" //$NON-NLS-1$
                        + date.get(Calendar.YEAR) + "_" //$NON-NLS-1$
                        + date.get(Calendar.HOUR_OF_DAY) + ":" //$NON-NLS-1$
                        + date.get(Calendar.MINUTE) + ":" //$NON-NLS-1$
                        + date.get(Calendar.SECOND) + ".jpg"; //$NON-NLS-1$
                Screenshot.writeToFile(new File(filename), canvas.getWidth(), canvas.getHeight());
            } catch (GLException e) {
                e.printStackTrace(); // FIXME
            } catch (IOException e) {
                e.printStackTrace(); // FIXME
            }
            this.mode = DisplayMode.NORMAL;
        }

        if (this.mode == DisplayMode.SELECTION) {
            select(drawable.getGL().getGL2());
            this.mode = DisplayMode.NORMAL;
            return;
        }

        super.display(drawable);
    }

    @Override
    public void drawElementArc(GL2 gl, SunburstLayout layout, Element e) {
        if (mode == DisplayMode.SELECTION) {
            gl.glPushName(e.getID());
            pickingEntities.put(e.getID(), e);
        }

        super.drawElementArc(gl, layout, e);

        if (mode == DisplayMode.SELECTION) {
            gl.glPopName();
        }
    }

    @Override
    protected Color getElementColor(SunburstLayout layout, Element e) {
        if (!layout.isArcVisible(e)) {
            return Color.BLACK;
        }

        final Scene scene = canvas.getScene();
        final Preferences preferences = scene.getPreferences();

        if (e.in(scene.getHighlightedElements())) {
            return preferences.getCaptureColor();
        } else {
            if (e.getClass() == GroupOfMethods.class) {
                return preferences.getGroupColor();
            } else if (e.getClass() == Method.class && ((Method) e).isSelected()) {
                return preferences.getSelectionColor();
            } else {
                float value = scene.getColorMetric().getValue(e);

                float ratio = Math.min(1f, value / scene.getMaxColorValue());
                Color lowColor = scene.getPreferences().getMinColor(scene.getColorMetric());
                Color highColor = scene.getPreferences().getMaxColor(scene.getColorMetric());
                Color color = Colors.blend(lowColor, highColor, ratio);

                if (e == scene.getElementUnderCursor()) {
                    color = Colors.blend(color, preferences.getHoverColor(), .5f);
                }

                return color;
            }
        }
    }

    /*
     * picking
     */
    private int pickX = 0;
    private int pickY = 0;
    private Element pickedElement;
    protected static Map<Integer, Element> pickingEntities = new HashMap<Integer, Element>();

    public void pick(int x, int y) {
        pickX = x;
        pickY = y;
        mode = DisplayMode.SELECTION;
    }

    public Element getPickedElement() {
        return pickedElement;
    }

    /**
     * La fonction select permet de savoir quel element est sous les
     * coordonnées pickX, pickY
     *
     * @param gl
     */
    private void select(GL2 gl) {
        int buffsize = 512;
        int[] viewPort = new int[4];
        IntBuffer selectBuffer = Buffers.newDirectIntBuffer(buffsize);
        int hits = 0;
        gl.glGetIntegerv(GL2.GL_VIEWPORT, viewPort, 0);
        gl.glSelectBuffer(buffsize, selectBuffer);
        gl.glRenderMode(GL2.GL_SELECT);
        gl.glInitNames();
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glPushMatrix();
        gl.glLoadIdentity();
        glu.gluPickMatrix(pickX, viewPort[3] - pickY, 1.0f, 1.0f, viewPort, 0);

        float w, h, L, H;
        w = canvas.getWidth();
        h = canvas.getHeight();
        if (w <= h) {
            H = camera.getDistance() * h / w;
            L = camera.getDistance();
        } else {
            H = camera.getDistance();
            L = camera.getDistance() * w / h;
        }

        glu.gluOrtho2D(-L / 2 + camera.getLeft() + camera.getRight(),
                L / 2 + camera.getLeft() + camera.getRight(),
                -H / 2 + camera.getTop() + camera.getBottom(),
                H / 2 + camera.getTop() + camera.getBottom());

        gl.glMatrixMode(GL2.GL_MODELVIEW);

        draw(gl);


        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glPopMatrix();
        gl.glFlush();
        hits = gl.glRenderMode(GL2.GL_RENDER);

        if (hits > 0) {
            int id = selectBuffer.get(3);
            this.pickedElement = pickingEntities.get(id);
        } else {
            this.pickedElement = null;
        }
    }
}
