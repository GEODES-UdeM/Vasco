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

import vasco.model.Method;
import vasco.render.SunburstLayout;
import vasco.view.Canvas;

/**
 * La classe CloseMethod est une action qui permet de fermer un sous arbre de la
 * visualisation
 *
 * @author fleur
 */
public class CloseMethod implements Action {
    private Method method;
    private SunburstLayout layout;
    private Canvas canvas;

    /**
     * Constructeur de la classe CloseMethod
     *
     * @param method
     *            méthode qui est la racine du sous arbre que l'on va fermer
     * @param layout
     *            treeVisu que l'on va changer pour ne plus faire apparaitre le
     *            sous arbre
     * @param canvas
     *            canvas dans lequel la visualisation va changer
     */
    public CloseMethod(Method method, SunburstLayout layout, Canvas canvas) {
        this.method = method;
        this.layout = layout;
        this.canvas = canvas;
    }

    @Override
    public void execute() {
        this.layout.setArcVisible(this.method, false);
        canvas.layoutChanged();
    }

    @Override
    public void inverse() {
        OpenMethod om = new OpenMethod(method, layout, canvas);
        om.execute();
    }
}
