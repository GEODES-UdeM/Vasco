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

import static java.awt.event.MouseEvent.BUTTON1;
import static java.awt.event.MouseEvent.BUTTON3;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import vasco.CanvasListener;
import vasco.ContextListener;
import vasco.ContextManager;
import vasco.Scene;
import vasco.SceneListener;
import vasco.action.Action;
import vasco.action.ChangeRoot;
import vasco.action.CloseMethod;
import vasco.action.EliminateInvocation;
import vasco.action.EliminateMethod;
import vasco.action.OpenMethod;
import vasco.metrics.Metric;
import vasco.model.CGModel;
import vasco.model.Element;
import vasco.model.Method;
import vasco.model.Selection;
import vasco.model.State;
import vasco.render.Camera;
import vasco.render.SunburstLayout;
import vasco.render.Viewport;
import vasco.render.VisualizationRenderer;
import vasco.util.Search;
import vasco.util.Strings;

/**
 * La classe Canvas represente le canvas sur lequel la visualisation sera
 * dessin√©e
 *
 * @author fleur
 */
public class Canvas extends GLCanvas implements Viewport, KeyListener, MouseListener, MouseMotionListener, MouseWheelListener {
    private static final long serialVersionUID = 1L;

    private Scene scene;
    private SunburstLayout layout;
    private Camera camera;
    private List<CanvasListener> listeners = new ArrayList<CanvasListener>(3);
    private VisualizationRenderer renderer;

    /*
     * Methode selectionn√©e (par n'importe quel evenement souris)
     */
    private Element selectedMethod;

    private boolean popupMenuIsVisible = false;

    private static GLCapabilities createCapabilities() {
        GLProfile glProfile = GLProfile.getDefault(GLProfile.getDefaultDevice());
        GLCapabilities capabilities = new GLCapabilities(glProfile);

        // Antialiasing
        capabilities.setSampleBuffers(true);
        capabilities.setNumSamples(4);

        return capabilities;
    }

    public Canvas(int width, int height, Scene scene) {
        this(width, height, scene, createCapabilities());
    }

    public Canvas(int width, int height, Scene scene, GLCapabilities capabilities) {
        super(capabilities);

        this.scene = scene;
        this.camera = new Camera(this);
        this.renderer = new VisualizationRenderer(this, camera);

        addGLEventListener(renderer);
        addKeyListener(this);
        addMouseListener(this);
        addMouseMotionListener(this);
        addMouseWheelListener(this);

        scene.getContextManager().addListener(new ContextListener() {
            @Override
            public void onStateActivated(ContextManager manager, State state) {
                Canvas.this.layout = state.getLayout();
                Canvas.this.zoomToFit();
            }
        });

        scene.addListener(new SceneListener() {
            @Override
            public void onMaxColorValueChanged(Scene scene, int value) {
                display();
            }

            @Override
            public void onColorMetricChanged(Scene scene, Metric colorMetric) {
                display();
            }

            @Override
            public void onMetricColorsModified(Scene scene, Metric metric) {
                if (metric == scene.getColorMetric()) display();
            }

            @Override
            public void onAngleMetricChanged(Scene scene, Metric angleMetric) {
                if (layout != null) {
                    layout.layout();
                    display();
                }
            }

            @Override
            public void onModelChanged(Scene scene, CGModel model) {
                camera.reset();
                layout = new SunburstLayout(scene, model.getRoot());
                scene.getContextManager().enterState(new State(layout));

                for (CanvasListener listener : listeners) {
                    listener.onLayoutChanged(Canvas.this);
                }
            }
        });
    }

    public Camera getCamera() {
        return camera;
    }

    public Scene getScene() {
        return scene;
    }

    public void addListener(CanvasListener listener) {
        listeners.add(listener);
    }

    public void removeListener(CanvasListener listener) {
        listeners.remove(listener);
    }

    private void notifyInspectedElement(Element element) {
        for (CanvasListener listener : listeners) {
            listener.onElementInspected(this, element);
        }
    }

    public void layoutChanged() {
        display();

        for (CanvasListener listener : listeners) {
            listener.onLayoutChanged(this);
        }
    }

    @Override
    public void display() {
        if (deltaX != 0 || deltaY != 0) {
            this.camera.moveLeft(deltaX / 30);
            this.camera.moveUp(deltaY / 30);
            deltaX = 0;
            deltaY = 0;
            // setCamera(gl,glu,(this.distance), this.g, this.d, this.haut,
            // this.bas);
        }

        super.display();
    }

    /**
     * La fonction pick permet de savoir sur quel element on a cliqué avec la
     * souris
     *
     * @param mouseX
     *            coordonnées de la souris
     * @param mouseY
     *            coordonnées de la souris
     * @return l'element sur lequel on a cliqué
     */
    public Element pick(int mouseX, int mouseY) {
        renderer.pick(mouseX, mouseY);
        this.display();
        return renderer.getPickedElement();
    }

    /**
     * la fonction zoomAvant permet de mette true dans la variable booleenne
     * zoomAvant Ce qui permettra de faire un zoom avant au moment de l'appel de
     * display()
     */
    public void zoomIn() {
        this.camera.zoomIn();
        display();
    }

    /**
     * la fonction zoomArriere permet de mette true dans la variable booleenne
     * zoomArriere Ce qui permettra de faire un zoom arriere au moment de
     * l'appel de display()
     */
    public void zoomOut() {
        this.camera.zoomOut();
        display();
    }

    public void zoomToFit() {
        this.camera.zoomToFit(this.layout);
        display();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_UP) {
            this.camera.moveUp();
            display();
        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            this.camera.moveRight();
            display();
        } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            this.camera.moveLeft();
            display();
        } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            this.camera.moveDown();
            display();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void mouseClicked(MouseEvent me) {
        popupMenuIsVisible = false;
        Element select = pick(me.getX(), me.getY());

        if (select == null) {
            clearSelection();
            this.display();
        } else if (me.getButton() == BUTTON1) {
            scene.clearHighlightedElements();
            if (select != null && select instanceof Method) {
                if (layout.isArcVisible(select)) {
                    if (me.isShiftDown()) {
                        ((Method) select).setSelected(true);
                        display();
                    } else {
                        this.selectedMethod = select;
                        if (select != layout.getRoot()) {
                            ChangeRoot cr = new ChangeRoot((Method) select, this);
                            scene.getUndoManager().perform(cr);
                        }
                    }
                } else {
                    Method method = (Method) select;
                    OpenMethod openMethod = new OpenMethod(method, this.layout, this);
                    scene.getUndoManager().perform(openMethod);
                }
            } else if (scene.getHighlightedElements().size() > 0) {
                scene.clearHighlightedElements();
                display();
            }
        } else if (me.getButton() == BUTTON3) {
            if (select != null && select.getClass() == Method.class) {
                popupMenuIsVisible = true;
                selectedMethod = select;
                showPopup(me, select);
            }
        }
    }

    private void clearSelection() {
        scene.clearHighlightedElements();
        CGModel cgm = scene.getModel();
        if (cgm != null) {
            for (Method method : cgm.getMethods()) {
                if (method.isSelected()) {
                    method.setSelected(false);
                }
            }
        }
    }

    private void showPopup(MouseEvent me, Element select) {
        JPopupMenu popupMenu = new JPopupMenu();

        JMenuItem selectInvocation = new JMenuItem(Strings.get("popupMenu.selectInvoc")); //$NON-NLS-1$
        SelectInvocationListener selectInvocListener = new SelectInvocationListener(select);
        selectInvocation.addActionListener(selectInvocListener);

        JMenuItem search = new JMenuItem(Strings.get("popupMenu.search")); //$NON-NLS-1$
        SearchListener searchListener = new SearchListener();
        search.addActionListener(searchListener);

        JMenuItem removeInvocation = new JMenuItem(Strings.get("popupMenu.eliminInvoc")); //$NON-NLS-1$
        RemoveInvocationListener removeInvocationListener = new RemoveInvocationListener();
        removeInvocation.addActionListener(removeInvocationListener);

        JMenuItem removeMethod = new JMenuItem(Strings.get("popupMenu.eliminMethod")); //$NON-NLS-1$
        RemoveMethodListener removeMethodListener = new RemoveMethodListener();
        removeMethod.addActionListener(removeMethodListener);

        JMenuItem capturedBy = new JMenuItem(Strings.get("popupMenu.capturingBy")); //$NON-NLS-1$
        CapturingByListener capturingByListener = new CapturingByListener(select);
        capturedBy.addActionListener(capturingByListener);
        capturedBy.setEnabled(((Method) select).getCapturingMethods().size() > 0);

        JMenuItem allocatedBy = new JMenuItem(Strings.get("popupMenu.allocatedBy")); //$NON-NLS-1$
        AllocatedByListener allocatedByListener = new AllocatedByListener(select);
        allocatedBy.addActionListener(allocatedByListener);
        allocatedBy.setEnabled(((Method) select).getAllocatingMethods().size() > 0);

        JMenuItem openCloseMethod;
        if (layout.isArcVisible(select)) {
            openCloseMethod = new JMenuItem(Strings.get("popupMenu.closeMethod")); //$NON-NLS-1$
        } else {
            openCloseMethod = new JMenuItem(Strings.get("popupMenu.openMethod")); //$NON-NLS-1$
        }
        OpenCloseMethodListener openCloseMethodListener = new OpenCloseMethodListener(select);
        openCloseMethod.addActionListener(openCloseMethodListener);

        JMenuItem showAllocations = new JMenuItem(Strings.get("popupMenu.infoAllocVert")); //$NON-NLS-1$
        ShowAllocationsListener showAllocationsListener = new ShowAllocationsListener(select);
        showAllocations.addActionListener(showAllocationsListener);
        showAllocations.setEnabled(select.getAllocatedTypes().size() > 0);

        JMenuItem showConnectionGraph = new JMenuItem(Strings.get("popupMenu.afficheGrapheConnex")); //$NON-NLS-1$
        ShowConnectionGraphListener showConnectionGraphListener = new ShowConnectionGraphListener(select);
        showConnectionGraph.addActionListener(showConnectionGraphListener);
        showConnectionGraph.setEnabled(scene.getModel().hasConnectionGraph((Method) select));

        popupMenu.add(selectInvocation);
        popupMenu.add(search);
        popupMenu.addSeparator();
        popupMenu.add(removeInvocation);
        popupMenu.add(removeMethod);
        popupMenu.addSeparator();
        popupMenu.add(showAllocations);
        popupMenu.add(showConnectionGraph);
        popupMenu.add(capturedBy);
        popupMenu.add(allocatedBy);
        popupMenu.addSeparator();
        popupMenu.add(openCloseMethod);

        popupMenu.show(me.getComponent(), me.getX(), me.getY());
    }

    private class SelectInvocationListener implements ActionListener {
        private Element select;

        public SelectInvocationListener(Element select) {
            this.select = select;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            popupMenuIsVisible = false;

            if (select != null && select.getClass() == Method.class) {
                ((Method) select).setSelected(true);
                display();
            }
        }
    }

    private class SearchListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            Method method = (Method) selectedMethod;
            popupMenuIsVisible = false;
            Search search = new Search(method.getName(), scene.getModel().getMethods());
            search.searchInList(); // WARNING: this call has side effects
            display();
        }
    }

    private class RemoveInvocationListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            Method method = (Method) selectedMethod;
            popupMenuIsVisible = false;
            EliminateInvocation eliminateInvocation = new EliminateInvocation(Canvas.this, method);
            scene.getUndoManager().perform(eliminateInvocation);
        }

    }

    private class RemoveMethodListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            Method m = (Method) selectedMethod;
            popupMenuIsVisible = false;
            Search search = new Search(m.getName(), scene.getModel().getMethods());
            ArrayList<Method> methods = search.searchInList();
            if (!methods.isEmpty()) {
                EliminateMethod eliminateMethod = new EliminateMethod(Canvas.this, methods);
                scene.getUndoManager().perform(eliminateMethod);
            }
        }
    }

    private class CapturingByListener implements ActionListener {
        private Element select;

        public CapturingByListener(Element select) {
            this.select = select;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            popupMenuIsVisible = false;
            if ((Method) select != null && ((Method) select).getCapturingMethods().size() > 0) {
                Canvas.this.selectedMethod = select;
                scene.setHighlightedElements(((Method) select).getCapturingMethods());
            } else {
                Canvas.this.selectedMethod = null;
            }
            Canvas.this.display();
        }
    }

    private class AllocatedByListener implements ActionListener {
        private Element select;

        public AllocatedByListener(Element select) {
            this.select = select;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            popupMenuIsVisible = false;
            if ((Method) select != null && ((Method) select).getAllocatingMethods().size() > 0) {
                Canvas.this.selectedMethod = select;
                scene.setHighlightedElements(((Method) select).getAllocatingMethods());
            } else {
                Canvas.this.selectedMethod = null;
            }
            Canvas.this.display();
        }
    }

    private class OpenCloseMethodListener implements ActionListener {
        private Element select;

        public OpenCloseMethodListener(Element select) {
            this.select = select;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            popupMenuIsVisible = false;
            Canvas.this.selectedMethod = select;
            if (select != null) {
                Method method = (Method) select;

                Action action;
                if (layout.isArcVisible(method)) {
                    action = new CloseMethod(method, Canvas.this.layout, Canvas.this);
                } else {
                    action = new OpenMethod(method, Canvas.this.layout, Canvas.this);
                }

                scene.getUndoManager().perform(action);
            }
        }
    }

    private class ShowAllocationsListener implements ActionListener {
        private Element select;

        public ShowAllocationsListener(Element select) {
            this.select = select;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            popupMenuIsVisible = false;
            if (select != null) {
                String name = Strings.getElementName(select, Strings.SHORT_ARG_NAMES | Strings.SHORT_CLASS_NAME);
                JFrame f = new JFrame(Strings.get("allocPanel.title") + " " + name); //$NON-NLS-1$ //$NON-NLS-2$
                f.setPreferredSize(new Dimension(800, 500));
                VerticalAllocPanel infoAlloc = new VerticalAllocPanel((Method) select);

                f.add(infoAlloc);
                f.pack();
                f.setVisible(true);

                display();
            }
        }
    }

    private class ShowConnectionGraphListener implements ActionListener {
        private Element select;

        public ShowConnectionGraphListener(Element select) {
            this.select = select;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            popupMenuIsVisible = false;
            if (scene.getModel().hasConnectionGraphs() && select != null) {
                new ConnectionGraphFrame(scene.getModel(), (Method) select);
            }
        }
    }

    @Override
    public void mouseEntered(MouseEvent arg0) {

    }

    @Override
    public void mouseExited(MouseEvent arg0) {

    }

    private Point origin;
    private double deltaX;
    private double deltaY;

    @Override
    public void mousePressed(MouseEvent e) {
        origin = e.getPoint();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        origin = null;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        Point p = e.getPoint();
        deltaX = p.getX() - origin.getX();
        deltaY = p.getY() - origin.getY();
        origin = p;
        display();
    }

    private Element lastMethod = null;

    @Override
    public void mouseMoved(MouseEvent me) {
        if (!popupMenuIsVisible) {
            Element selectedElement = pick(me.getX(), me.getY());
            scene.setElementUnderCursor(selectedElement);
            if (selectedElement == lastMethod)
                return;
            lastMethod = selectedElement;

            // FIXME: remove class-specific behavior
            if (selectedElement != null && (selectedElement instanceof Method) && ((Method) selectedElement).isSelected()) {
                ArrayList<Method> methods = new ArrayList<Method>();
                for (Method method : scene.getModel().getMethods()) {
                    if (method.isSelected()) {
                        methods.add(method);
                    }
                }
                notifyInspectedElement(new Selection(methods));
            } else if (selectedElement != null) {
                notifyInspectedElement(selectedElement);
            } else {
                notifyInspectedElement(null);
            }
            display();
        }
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        int notches = e.getWheelRotation();
        if (notches < 0) {
            zoomOut();
        } else {
            zoomIn();
        }
    }

    /**
     * La fonction retourAuDeb permet de revenir avec les parametres de la
     * visualisation du debut
     */
    public void reset() {
        camera.reset();
        camera.zoomToFit(layout);
        scene.getContextManager().reset(true);
    }

    public SunburstLayout getLayout() {
        return this.layout;
    }

    public CGModel getCGModel() {
        return scene.getModel();
    }

    public void setScreenshot() {
        renderer.setMode(VisualizationRenderer.DisplayMode.SCREENSHOT); // FIXME: temporary, very ugly
    }
}
