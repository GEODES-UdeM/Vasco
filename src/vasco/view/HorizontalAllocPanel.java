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

import iceberg.analysis.types.Types;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;

import javax.swing.JPanel;

import vasco.Scene;
import vasco.metrics.Captures;
import vasco.model.Element;
import vasco.util.Strings;
import elude.graphs.cg.EType;

public class HorizontalAllocPanel extends JPanel {
    private static final int PADDING = 10;

    private static final long serialVersionUID = 1L;

    private Scene scene;
    private Element element;

    public HorizontalAllocPanel(Scene scene) {
        this.scene = scene;
    }

    @Override
    public void paint(Graphics g) {
        final Map<EType, Integer> types = getTypes();
        if (!types.isEmpty()) {
            float value = 0.90f;
            float hue;
            if (scene.getColorMetric() instanceof Captures) {
                hue = 0.28f;
            } else {
                hue = 0.71f;
            }
            ArrayList<EType> typeList = new ArrayList<EType>(types.keySet());
            Collections.sort(typeList, new Comparator<EType>() {
                @Override
                public int compare(EType o1, EType o2) {
                    return types.get(o2) - types.get(o1);
                }
            });
            int total = 0;
            for (EType t : typeList) {
                total += types.get(t);
            }
            int seen = 0;
            double l = 0;
            for (EType t : typeList) {
                boolean finish = false;
                String type = t.getJavaName();
                if (this.scene.getPreferences().useShortNames()) {
                    type = Types.shortJavaName(type);
                }
                Integer count = types.get(t);
                seen += count;
                double width = count * this.getSize().getWidth() / total;
                if (width < 40) {
                    width = this.getSize().getWidth() - l; // All remaining
                                                           // space
                    count += (total - seen);
                    finish = true;
                    type = Strings.get("info.otherTypes"); //$NON-NLS-1$
                }

                g.setColor(Color.getHSBColor(hue, 0.1f, value));
                value += 0.05f;
                if (value > 1) {
                    value = 0.90f;
                }
                g.fillRect((int) l, 0, (int) width, 50);
                g.setColor(Color.BLACK);
                g.drawRect((int) l, 0, (int) width, 50);
                int lineHeight = g.getFontMetrics().getHeight();
                g.drawString(String.valueOf(count), (int) l + PADDING, lineHeight + 5);
                g.drawString(type, (int) l + PADDING, 2 * lineHeight + 5);
                if (finish) {
                    break;
                }
                l += width;
            }
        } else {
            g.clearRect(0, 0, (int) this.getSize().getWidth(), (int) this.getSize().getHeight());
        }
    }

    private Map<EType, Integer> getTypes() {
        if (element == null)
            return Collections.emptyMap();

        if (scene.getColorMetric() instanceof Captures) {
            return element.getCapturedTypes();
        } else {
            return element.getAllocatedTypes();
        }
    }

    public Element getElement() {
        return element;
    }

    public void setElement(Element element) {
        this.element = element;
    }
}
