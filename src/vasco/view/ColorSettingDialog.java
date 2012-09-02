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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import vasco.Scene;
import vasco.metrics.Metric;
import vasco.util.Strings;

/**
 * La classe FenetreParamCoul représente la fenetre de parametrage des couleurs
 * de la visualisation
 *
 * @author fleur
 */
public class ColorSettingDialog extends JFrame {

    private static final long serialVersionUID = 1L;
    private Canvas canvas;
    private ColorSettingPanel colorSettingPanel;
    private Scene scene;

    /**
     * Constructeur de la classe FenetreParamCoul
     *
     * @param scene
     * @param canvas
     */
    public ColorSettingDialog(Scene scene, Canvas canvas) {
        super();
        this.scene = scene;

        this.canvas = canvas;
        this.setPreferredSize(new Dimension(500, 300));

        colorSettingPanel = new ColorSettingPanel(scene, canvas);
        this.add(colorSettingPanel);

        JPanel okPanel = new JPanel();
        okPanel.setLayout(new BoxLayout(okPanel, BoxLayout.PAGE_AXIS));

        JButton okButton = new JButton(Strings.get("general.OK")); //$NON-NLS-1$
        okButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                final Scene scene = ColorSettingDialog.this.scene;
                Metric colorMetric = scene.getColorMetric();
                scene.getPreferences().setMinColor(colorMetric, colorSettingPanel.getMinColor());
                scene.getPreferences().setMaxColor(colorMetric, colorSettingPanel.getMaxColor());
                ColorSettingDialog.this.canvas.display();  // FIXME: move to listener
                ColorSettingDialog.this.dispose();
            }
        });

        okPanel.add(okButton);

        this.add(okPanel, BorderLayout.SOUTH);
        this.pack();
    }
}
