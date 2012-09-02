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

package vasco.view;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Map.Entry;

import javax.swing.JPanel;

import vasco.model.Method;

import elude.graphs.cg.EType;

public class VerticalAllocPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private Method method;

    public VerticalAllocPanel(Method method) {
        this.method = method;
        this.setBackground(Color.white);
    }

    public VerticalAllocPanel() {
        this.setBackground(Color.white);
    }

    @Override
    public void paint(Graphics g) {

        if (method != null && method.getNumberOfAllocations() > 0) {
            float value = 0.90f;
            float hue = 0.71f;
            double yCoord = 5;
            for (Entry<EType, Integer> entry : (method.getAllocatedTypes()).entrySet()) {

                String type = (entry.getKey()).getJavaName();
                Integer count = entry.getValue();
                double height = count * 500 / method.getNumberOfAllocations();

                g.setColor(Color.getHSBColor(hue, 0.1f, value));
                value += 0.05f;

                if (value > 1) {
                    value = 0.90f;
                }

                g.fillRect(5, (int) yCoord, 50, (int) height);
                g.setColor(Color.BLACK);
                // Font font1 = new Font("TimesRoman", Font.PLAIN, 13);
                g.drawString(type + "(" + count + ")", 75, (int) (yCoord + 15)); //$NON-NLS-1$ //$NON-NLS-2$
                yCoord += height;
            }
        }
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }
}
