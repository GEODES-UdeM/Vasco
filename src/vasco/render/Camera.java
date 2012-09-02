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

import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import vasco.model.Arc;

public class Camera {
    private static final float PAN_STEP = 0.1f;
    private static final float ZOOM_STEP = 0.5f;
    private static final float MIN_DISTANCE = 0.1f;
    private Viewport viewport;
    private float distance = 2;
    private float left = 0;
    private float right = 0;
    private float top = 0;
    private float bottom = 0;

    private List<CameraListener> listeners = new ArrayList<CameraListener>(3);

    public Camera(Viewport viewport) {
        this.viewport = viewport;
    }

    public void addListener(CameraListener listener) {
        this.listeners.add(listener);
    }

    public void removeListener(CameraListener listener) {
        this.listeners.remove(listener);
    }

    private void notifyListeners() {
        for (CameraListener listener: listeners) {
            listener.onCameraChanged(this);
        }
    }

    public void reset() {
        distance = 2;
        left = 0;
        right = 0;
        top = 0;
        bottom = 0;

        notifyListeners();
    }

    public float getDistance() {
        return distance;
    }

    public float getLeft() {
        return left;
    }

    public float getRight() {
        return right;
    }

    public float getTop() {
        return top;
    }

    public float getBottom() {
        return bottom;
    }

    public boolean canZoomIn() {
        return (this.distance - MIN_DISTANCE) > 0.0001f; // Use a tolerance to make this more intuitive
    }

    public void zoomIn() {
        this.distance = this.distance - ZOOM_STEP;
        if (distance < MIN_DISTANCE)
            this.distance = MIN_DISTANCE;
        notifyListeners();
    }

    public void zoomOut() {
        this.distance = this.distance + ZOOM_STEP;
        notifyListeners();
    }

    public void moveUp() {
        this.moveUp(PAN_STEP);
    }

    public void moveUp(double amount) {
        this.top += amount;
        notifyListeners();
    }

    public void moveRight() {
        moveRight(PAN_STEP);
    }

    public void moveRight(float amount) {
        this.right += amount;
        notifyListeners();
    }

    public void moveLeft() {
        this.moveLeft(PAN_STEP);
    }

    public void moveLeft(double amount) {
        this.left -= amount;
        notifyListeners();
    }

    public void moveDown() {
        this.moveDown(PAN_STEP);
    }

    public void moveDown(float amount) {
        this.bottom -= amount;
        notifyListeners();
    }

    public void zoomToFit(SunburstLayout layout) {
        this.distance = (layout.getMaxLevel() + 1) * Arc.NORM * 2;
        notifyListeners();
    }

    public Viewport getViewport() {
        return viewport;
    }

    public void update(GL2 gl, GLU glu) {
        // Change to projection matrix.
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();

        float w, h, L, H;
        w = viewport.getWidth();
        h = viewport.getHeight();
        if (w <= h) {
            H = distance * h / w;
            L = distance;
        } else {
            H = distance;
            L = distance * w / h;
        }

        glu.gluOrtho2D((-L / 2) + left + right, (L / 2) + left + right, (-H / 2) + top + bottom, (H / 2) + top + bottom);

        // Change back to model view matrix.
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();
    }
}
