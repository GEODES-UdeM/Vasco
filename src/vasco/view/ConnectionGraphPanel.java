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

import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JPanel;

/**
 * PanelGraphConnection représente un panel surlequel s'affiche le graphe de
 * connexion de la methode souhaitée
 * 
 * @author fleur
 */
public class ConnectionGraphPanel extends JPanel implements MouseWheelListener {

    private static final long serialVersionUID = 1L;
    private Image image;
    private double height;
    private double width;

    /**
     * Constructeur de la classe PanelGraphConnection
     * 
     * @param image
     *            image du graphe de connexion
     */
    public ConnectionGraphPanel(Image image) {
        addMouseWheelListener(this);
        this.image = image;
        height = image.getHeight(null);
        width = image.getWidth(null);
    }

    @Override
    public void paint(Graphics g) {
        g.clearRect(0, 0, 1000, 1000);
        g.drawImage(image, 0, 0, (int) width, (int) height, null);
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        int notches = e.getWheelRotation();
        if (notches < 0) {
            this.height = this.height * 0.8;
            this.width = this.width * 0.8;
            repaint();
        } else {
            this.height = this.height * 1.2;
            this.width = this.width * 1.2;
            repaint();
        }
    }

    /**
     * Accesseur de la variable img
     * 
     * @return la valeur de la variable img : image du graphe de connexion
     */
    public Image getImage() {
        return image;
    }

    /**
     * Modifieur de la variable img
     * 
     * @param image
     *            nouvelle image de la variable img
     */
    public void setImage(Image image) {
        this.image = image;
    }
}
