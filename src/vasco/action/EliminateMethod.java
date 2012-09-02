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

import java.util.ArrayList;

import vasco.model.CGModel;
import vasco.model.Method;
import vasco.view.Canvas;

/**
 * La classe EliminMethod est une action qui permet d'éliminer toutes les
 * invocations d'une méthode
 *
 * @author fleur
 */
public class EliminateMethod implements Action {
    private Canvas canvas;
    private ArrayList<Method> allInvocations;

    /**
     * Constructeur de la classe EliminMethod
     *
     * @param canvas
     *            canvas dans lequel on va modifier la visualisation sans les
     *            invocations de la methode
     * @param allInvocations
     *            liste de toutes les invocations de la méthode
     */
    public EliminateMethod(Canvas canvas, ArrayList<Method> allInvocations) {
        this.canvas = canvas;
        this.allInvocations = allInvocations;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void execute() {
        for (Method method : allInvocations) {
            method.setEliminated(true);
            ArrayList<Method> children = new ArrayList<Method>();
            children = (ArrayList<Method>) method.getChildren().clone();
            while (!children.isEmpty()) {
                children.get(0).setEliminated(true);
                children.addAll(children.get(0).getChildren());
                children.remove(children.get(0));
            }
        }

        CGModel cgm = this.canvas.getCGModel();

        cgm.updateMetrics();
        this.canvas.getLayout().update();
        canvas.getScene().getContextManager().invalidateAll();

        int maxValue = cgm.calculateMaxValue(this.canvas.getScene().getColorMetric());
        this.canvas.getScene().getColorMetric().setMaxValue(maxValue);
        this.canvas.getScene().setMaxColorValue(maxValue);
        canvas.layoutChanged();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void inverse() {
        for (Method method : allInvocations) {
            method.setEliminated(false);
            ArrayList<Method> children = new ArrayList<Method>();
            children = (ArrayList<Method>) method.getChildren().clone();
            while (!children.isEmpty()) {
                children.get(0).setEliminated(false);
                children.addAll(children.get(0).getChildren());
                children.remove(children.get(0));
            }
        }

        CGModel cgm = this.canvas.getCGModel();

        cgm.updateMetrics();
        this.canvas.getLayout().layout();
        this.canvas.reset();

        int maxValue = cgm.calculateMaxValue(this.canvas.getScene().getColorMetric());
        // System.out.println("val max= " + maxValue);
        this.canvas.getScene().getColorMetric().setMaxValue(maxValue);
        this.canvas.getScene().setMaxColorValue(maxValue);
        canvas.layoutChanged();
    }
}
