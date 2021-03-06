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

package vasco.action;

import vasco.metrics.Metric;
import vasco.view.Canvas;

/**
 * La classe ChangeMetriqueAngle représente une action qui permet de changer la
 * métrique qui est representée par l'angle des arcs
 *
 * @author fleur
 */
public class ChangeAngleMetric implements Action {
    private Canvas canvas;
    private Metric initialMetric;
    private Metric finalMetric;

    /**
     * Constructeur de la classe ChangeMetriqueAngle
     *
     * @param canvas
     *            canvas dans lequel va changer la taille de tous les arcs
     * @param initialMetric
     *            métrique initiale
     * @param finalMetric
     *            nouvelle métrique mise en place
     */
    public ChangeAngleMetric(Canvas canvas, Metric initialMetric, Metric finalMetric) {
        this.canvas = canvas;
        this.initialMetric = initialMetric;
        this.finalMetric = finalMetric;
    }

    @Override
    public void execute() {
        this.canvas.getScene().setAngleMetric(finalMetric);
        this.canvas.getLayout().layout();
        this.canvas.zoomToFit();
    }

    @Override
    public void inverse() {
        this.canvas.getScene().setAngleMetric(initialMetric);
        this.canvas.getLayout().layout();
        this.canvas.display();
    }
}
