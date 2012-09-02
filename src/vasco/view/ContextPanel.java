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
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;

import vasco.Scene;
import vasco.model.State;
import vasco.render.Camera;
import vasco.render.OffscreenRenderer;
import vasco.render.Viewport;

/**
 * Panel Context représente le panel contextuel il se trouve sur le côté
 * gauche de l'outil
 *
 * @author fleur
 */
public class ContextPanel extends JPanel {
    private static final long serialVersionUID = 1550076477786101717L;

    private static final int PADDING = 5;

    private Scene scene;
    private State state;

    public ContextPanel(Scene scene, int numPanel) {
        this.scene = scene;
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (state != null) {
                    ContextPanel.this.scene.getContextManager().restoreState(state);
                }
            }
        });
    }

    private Image updateImage() {
        Viewport viewport = new Viewport() {
            @Override
            public int getWidth() {
                return ContextPanel.this.getWidth();
            }

            @Override
            public int getHeight() {
                return ContextPanel.this.getHeight() - PADDING;
            }
        };
        Camera camera = new Camera(viewport);
        camera.zoomToFit(state.getLayout());

        Image image = OffscreenRenderer.render(state, camera);
        state.setImage(image);
        return image;
    }

    @Override
    public void paint(Graphics g) {
        if (state != null) {
            Image image = state.getImage();
            if (image == null) image = updateImage();
            g.drawImage(image, 0, 0, null);
        } else {
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, getWidth(), getHeight() - PADDING);
        }
    }

    /**
     * Accesseur de la variable state
     *
     * @return la valeur de la variable state
     */
    public State getState() {
        return state;
    }

    /**
     * Modifieur de la variable state
     *
     * @param state
     *            nouvelle valeur de la variable state
     */
    public void setState(State state) {
        this.state = state;
        repaint();
    }
}