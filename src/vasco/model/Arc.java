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

package vasco.model;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

/**
 * La classe Arc représente un arc qui permet de visualiser une méthode
 *
 * @author fleur
 */
public class Arc {

    public static final float NORM = 0.1f;
    // les draw arc seront définis par le point en haut a droite, le point du
    // centre, leur angle, et leur epaisseur et la methode qui lui est associé
    // point en haut a droite
    private double x;
    private double y;

    // coordonnées polaires
    private double polarAngle;
    private double polarRadius;

    // point du centre
    private double xC;
    private double yC;

    // angle
    private double angle;

    // epaisseur
    private double thickness;

    // methode associée
    private Method method;

    /**
     * Constructeur de la classe Arc, initialise le point du centre du cercle a
     * 0,0 et l'epaisseur a 1 et les autres variables avec les parametres
     *
     * @param polarAngle
     *            coordonnées polaires de l'arc
     * @param polarRadius
     *            coordonnées polaires de l'arc
     * @param angle
     *            angle que fait l'arc
     */
    public Arc(double polarAngle, double polarRadius, double angle) {
        this.polarAngle = polarAngle;
        this.polarRadius = polarRadius;
        this.angle = angle;
        this.thickness = 1;
        this.xC = 0;
        this.yC = 0;
    }

    /**
     * La méthode drawArc sert a dessiner l'arc
     *
     * @param gl
     */
    public void drawArc(GL2 gl) {
        gl.glLoadIdentity();
        gl.glBegin(GL.GL_TRIANGLE_STRIP);
        double startAngle = this.polarAngle;
        double currentAngle = 0;
        double r = this.polarRadius;
        while (currentAngle <= this.angle) {

            double x1 = (Math.cos(startAngle) * (r + this.thickness - 0.05) * NORM);
            double y1 = (Math.sin(startAngle) * (r + this.thickness - 0.05) * NORM);
            double x2 = (Math.cos(startAngle) * r * NORM);
            double y2 = (Math.sin(startAngle) * r * NORM);

            gl.glVertex2d(x1, y1);
            gl.glVertex2d(x2, y2);

            startAngle = (startAngle + 0.005);
            currentAngle = (currentAngle + 0.005);
            if (startAngle > 2 * Math.PI) {
                startAngle = 0;
            }
        }

        gl.glEnd();

        gl.glColor3f(1, 1, 1);
        gl.glBegin(GL.GL_LINES);

        startAngle = this.polarAngle;

        double x1 = (Math.cos(startAngle) * (r + this.thickness - 0.05) * NORM);
        double y1 = (Math.sin(startAngle) * (r + this.thickness - 0.05) * NORM);
        double x2 = (Math.cos(startAngle) * r * NORM);
        double y2 = (Math.sin(startAngle) * r * NORM);

        gl.glVertex2d(x1, y1);
        gl.glVertex2d(x2, y2);

        gl.glEnd();
    }

    /**
     * Accesseur de la variable x
     *
     * @return la valeur de la variable x
     */
    public double getX() {
        return x;
    }

    /**
     * Modifieur de la variable x
     *
     * @param x
     *            la nouvelle valeur de la variable x
     */
    public void setX(double x) {
        this.x = x;
    }

    /**
     * Accesseur de la variable y
     *
     * @return la valeur de la variable y
     */
    public double getY() {
        return y;
    }

    /**
     * Modifieur de la variable y
     *
     * @param y
     *            la nouvelle valeur de la variable y
     */
    public void setY(double y) {
        this.y = y;
    }

    /**
     * Accesseur de la variable xC
     *
     * @return la valeur de la variable xC
     */
    public double getXC() {
        return xC;
    }

    /**
     * Modifieur de la variable xC
     *
     * @param xC
     *            la nouvelle valeur de la variable xC
     */
    public void setXC(double xC) {
        this.xC = xC;
    }

    /**
     * Accesseur de la variable yC
     *
     * @return la valeur de la variable yC
     */
    public double getYC() {
        return yC;
    }

    /**
     * Modifieur de la variable yC
     *
     * @param yC
     *            la nouvelle valeur de la variable yC
     */
    public void setYC(double yC) {
        this.yC = yC;
    }

    /**
     * Accesseur de la variable angle
     *
     * @return la valeur de la variable angle
     */
    public double getAngle() {
        return angle;
    }

    /**
     * Modifieur de la variable angle
     *
     * @param angle
     *            la nouvelle valeur de la variable angle
     */
    public void setAngle(double angle) {
        this.angle = angle;
    }

    /**
     * Accesseur de la variable anglePolaire
     *
     * @return la valeur de la variable anglePolaire
     */
    public double getAnglePolaire() {
        return polarAngle;
    }

    /**
     * Modifieur de la variable anglePolaire
     *
     * @param polarAngle
     *            la nouvelle valeur de la variable anglePolaire
     */
    public void setPolarAngle(double polarAngle) {
        this.polarAngle = polarAngle;
    }

    /**
     * Accesseur de la variable rayonPolaire
     *
     * @return la valeur de la variable rayonPolaire
     */
    public double getPolarRadius() {
        return polarRadius;
    }

    /**
     * Modifieur de la variable rayonPolaire
     *
     * @param polarRadius
     *            la nouvelle valeur de la variable rayonPolaire
     */
    public void setPolarRadius(double polarRadius) {
        this.polarRadius = polarRadius;
    }

    /**
     * Accesseur de la variable method
     *
     * @return la valeur de la variable method
     */
    public Method getMethod() {
        return method;
    }

    /**
     * Modifieur de la variable method
     *
     * @param method
     *            la nouvelle valeur de la variable method
     */
    public void setMethod(Method method) {
        this.method = method;
    }

    /**
     * Modifieur de la variable epaisseur
     *
     * @param thickness
     *            la nouvelle valeur de la variable epaisseur
     */
    public void setThickness(double thickness) {
        this.thickness = thickness;
    }
}
