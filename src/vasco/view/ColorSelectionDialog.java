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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import vasco.Scene;
import vasco.util.Strings;

/**
 * La classe FenetreChoixCoul représente la fenêtre qui permet le choix de
 * couleur
 *
 * @author fleur
 */
public class ColorSelectionDialog extends JFrame {

    private static final long serialVersionUID = 1L;
    private ColorSelectionPanel colorSelectionPanel;
    private Color selectedColor;
    private ColorSettingPanel colorSettingPanel;
    private int colorID;

    /**
     * Constructeur de la classe FenetreChoixCoul
     *
     * @param scene
     * @param colorSettingPanel
     * @param colorID
     */
    public ColorSelectionDialog(Scene scene, ColorSettingPanel colorSettingPanel, int colorID) {
        super();
        this.colorSettingPanel = colorSettingPanel;
        this.colorID = colorID;

        this.setPreferredSize(new Dimension(500, 500));

        if (colorID == 1) {
            colorSelectionPanel = new ColorSelectionPanel(colorSettingPanel.getMinColor());
        } else if (colorID == 2) {
            colorSelectionPanel = new ColorSelectionPanel(colorSettingPanel.getMaxColor());
        }
        this.add(colorSelectionPanel, BorderLayout.CENTER);

        JPanel okPanel = new JPanel();
        JButton okButton = new JButton(Strings.get("general.OK")); //$NON-NLS-1$
        okButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                selectedColor = colorSelectionPanel.getSelectedColor();
                if (ColorSelectionDialog.this.colorID == 1 && selectedColor != null) {
                    ColorSelectionDialog.this.colorSettingPanel.setMinColor(selectedColor);
                    ColorSelectionDialog.this.colorSettingPanel.repaint();
                } else if (ColorSelectionDialog.this.colorID == 2 && selectedColor != null) {
                    ColorSelectionDialog.this.colorSettingPanel.setMaxColor(selectedColor);
                    ColorSelectionDialog.this.colorSettingPanel.repaint();

                }
                ColorSelectionDialog.this.dispose();
            }
        });

        okPanel.add(okButton);
        this.add(okPanel, BorderLayout.SOUTH);
        this.pack();
    }

    /**
     * Accesseur de la variale couleurChoisie
     *
     * @return la valeur de la couleur choisie
     */
    public Color getselectedColor() {
        return selectedColor;
    }
}
