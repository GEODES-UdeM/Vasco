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

import static javax.media.opengl.GL.GL_NICEST;
import static javax.media.opengl.GL2ES1.GL_PERSPECTIVE_CORRECTION_HINT;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_SMOOTH;

import java.awt.Color;
import java.util.ArrayList;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;

import vasco.model.Arc;
import vasco.model.Element;
import vasco.model.Method;

public abstract class SunburstRenderer implements GLEventListener {
    protected Camera camera;
    protected GLU glu;

    public SunburstRenderer(Camera camera) {
        this.camera = camera;
    }

    /**
     * la fonction init est une fonction d'openGL qui permet d'initialiser le
     * drawable (zone de dessin)
     */
    @Override
    public void init(GLAutoDrawable drawable) {
        init(drawable.getGL().getGL2());
    }

    protected void init(final GL2 gl) {
        // Enable z- (depth) buffer for hidden surface removal.
        gl.glEnable(GL.GL_DEPTH_TEST);
        gl.glDepthFunc(GL.GL_LEQUAL);

        // Enable smooth shading.
        gl.glShadeModel(GL_SMOOTH);

        // Define "clear" color.
        gl.glClearColor(1f, 1f, 1f, 1f);

        // We want a nice perspective.
        gl.glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);

        // Create GLU.
        glu = new GLU();

        Viewport viewport = camera.getViewport();
        gl.glViewport(0, 0, viewport.getWidth(), viewport.getHeight());
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        display(drawable.getGL().getGL2());
    }

    protected void display(final GL2 gl) {
        // Clear screen.
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

        if (getLayout() != null) {
            this.camera.update(gl, glu);
            draw(gl);
        }

        gl.glFlush();
    }

    /**
     * fonction qui permet de dessiner la visualisation
     *
     * @param gl
     */
    public void draw(final GL2 gl) {
        final SunburstLayout layout = getLayout();
        if (layout != null) {
            layout.dfs(new SunburstLayout.CGVisitor() {
                @Override
                public void visit(Element e) {
                    drawElementArc(gl, layout, e);
                }
            });
        }
    }

    protected abstract SunburstLayout getLayout();

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        final GL2 gl = drawable.getGL().getGL2();
        reshape(gl, x, y, width, height);
    }

    protected void reshape(final GL2 gl, int x, int y, int width, int height) {
        gl.glViewport(0, 0, width, height);
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
        dispose(drawable.getGL().getGL2());
    }

    protected void dispose(GL2 gl2) {
        // empty
    }

    /**
     * la fonction drawElementArc permet de dessiner l'element e sous forme d'un
     * arc en lui attribuant une couleur
     *
     * @param gl
     * @param layout
     * @param e
     *            element a dessiner sous forme d'arc
     */
    public void drawElementArc(GL2 gl, SunburstLayout layout, Element e) {
        Color color = getElementColor(layout, e);

        gl.glColor3f(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f);

        Arc arc = layout.getArc(e);

        if (arc != null && !e.isEliminated()) {
            if (!layout.isArcVisible(e) && e.getClass() == Method.class) {
                arc.setThickness(getSubtreeHeight((Method) e));
                arc.drawArc(gl);
                arc.setThickness(1);
            } else {
                arc.drawArc(gl);
            }
        }
    }

    protected Color getElementColor(SunburstLayout layout, Element e) {
        return Color.BLACK;
    }

    /**
     * la fonction sizeSubset sert a connaitre la profondeur d'un sous arbre
     *
     * @param method
     *            methode a partir de laquelle commence le sous arbre
     * @return la profondeur du sous arbre
     */
    public int getSubtreeHeight(Method method) {
        // TODO: rewrite this
        int maxLevel = 0;
        ArrayList<Method> list = new ArrayList<Method>();
        list.add(method);
        while (!list.isEmpty()) {
            int tempLevel = list.get(0).getLevel();
            if (maxLevel < tempLevel) {
                maxLevel = tempLevel;
            }
            list.addAll(list.get(0).getChildren());
            list.remove(0);
        }

        int size = maxLevel - method.getLevel() + 1;
        return size;
    }
}