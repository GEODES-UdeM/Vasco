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
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import vasco.CanvasListener;
import vasco.ContextListener;
import vasco.ContextManager;
import vasco.Scene;
import vasco.SceneAdapter;
import vasco.action.ChangeAngleMetric;
import vasco.action.ChangeColorMetric;
import vasco.action.ChangeMaxColorValue;
import vasco.action.OpenFile;
import vasco.metrics.Metric;
import vasco.model.CGModel;
import vasco.model.Element;
import vasco.model.Method;
import vasco.model.State;
import vasco.render.Camera;
import vasco.render.CameraListener;
import vasco.util.Search;
import vasco.util.Strings;

/**
 * La classe Fenetre représente la fenetre principale
 *
 * @author fleur
 */
public class MainWindow extends JFrame {
    private static final long serialVersionUID = -7708979280354710183L;

    private JMenuBar menuBar;
    private JMenuItem openItem;
    private JMenuItem quitItem;
    private JMenuItem resetItem;
    private JMenuItem zoomInItem;
    private JMenuItem zoomOutItem;
    private JMenuItem screenshotItem;
    private JButton undo;
    private JButton redo;
    private JComboBox boxM1; // colorMetricDropdown. M1 = metric1 = colorMetric
    private JComboBox boxM2; // M2 = angle metric
    private JLabel textBoxM1;
    private JLabel textBoxM2;
    private JLabel textValMaxM1;
    private JTextField maxColorMetricTextField;
    private JTextField searchTextField;
    private JButton closeSearch;

    private Canvas canvas;

    private Scene scene;

    private InfoPanel infoPanel;
    private HorizontalAllocPanel allocPanel;
    private ArrayList<ContextPanel> contextPanels;


    private static String basename(String pathname) {
        File f = new File(pathname);
        return f.getAbsoluteFile().getName();
    }

    public MainWindow(Scene scene) {
        this.scene = scene;

        setTitle(Strings.get("MainWindow.title")); //$NON-NLS-1$ //$NON-NLS-2$
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 730, 480);
        setMinimumSize(new Dimension(100, 100));
        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[] { 0, 0, 0 };
        gridBagLayout.rowHeights = new int[] { 0, 0, 0 };
        gridBagLayout.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
        gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 1.0, Double.MIN_VALUE };
        getContentPane().setLayout(gridBagLayout);

        createCanvas();
        createInfoPanel();
        createAllocPanel();
        createContextPanels();
        createMenuBar();

        canvas.addListener(new CanvasListener() {
            @Override
            public void onElementInspected(Canvas canvas, Element element) {
                infoPanel.update(element);
                infoPanel.repaint();

                allocPanel.setElement(element);
                allocPanel.repaint();
            }

            @Override
            public void onLayoutChanged(Canvas canvas) {
                this.onElementInspected(canvas, null);
            }
        });

        scene.addListener(new SceneAdapter() {
            @Override
            public void onModelChanged(Scene scene, CGModel model) {
                String filename = model.getFilename();
                String title = Strings.get("MainWindow.title"); //$NON-NLS-1$
                if (filename != null) {
                    title += " - " + basename(filename); //$NON-NLS-1$
                }

                setTitle(title);
            }
        });

        this.pack();
        this.setVisible(true);
        canvas.requestFocus();
    }

    private void createCanvas() {
        this.canvas = new Canvas(800, 500, scene);
        this.canvas.setPreferredSize(new Dimension(1000, 600));
        this.canvas.setMinimumSize(new Dimension(1000, 600));
        GridBagConstraints gbc_canvasPanel = new GridBagConstraints();
        gbc_canvasPanel.fill = GridBagConstraints.BOTH;
        gbc_canvasPanel.gridx = 1;
        gbc_canvasPanel.gridy = 2;
        getContentPane().add(canvas, gbc_canvasPanel);
    }

    private void createInfoPanel() {
        this.infoPanel = new InfoPanel(scene, canvas);
        this.infoPanel.setPreferredSize(new Dimension(1000, 80));
        this.infoPanel.setMinimumSize(new Dimension(1000, 80));
        this.infoPanel.setMaximumSize(new Dimension(1000, 80));
        GridBagConstraints gbc_infoPanel = new GridBagConstraints();
        gbc_infoPanel.insets = new Insets(0, 0, 5, 0);
        gbc_infoPanel.anchor = GridBagConstraints.NORTH;
        gbc_infoPanel.fill = GridBagConstraints.HORIZONTAL;
        gbc_infoPanel.gridx = 1;
        gbc_infoPanel.gridy = 0;
        getContentPane().add(this.infoPanel, gbc_infoPanel);

        JLabel infoLabel = new JLabel("infoLabel"); //$NON-NLS-1$
        this.infoPanel.add(infoLabel);
    }

    private void createAllocPanel() {
        this.allocPanel = new HorizontalAllocPanel(scene);
        this.allocPanel.setPreferredSize(new Dimension(1000, 50));
        this.allocPanel.setMinimumSize(new Dimension(1000, 50));
        this.allocPanel.setMaximumSize(new Dimension(1000, 50));
        GridBagConstraints gbc_infoAlloc = new GridBagConstraints();
        gbc_infoAlloc.insets = new Insets(0, 0, 5, 0);
        gbc_infoAlloc.anchor = GridBagConstraints.NORTH;
        gbc_infoAlloc.fill = GridBagConstraints.HORIZONTAL;
        gbc_infoAlloc.gridx = 1;
        gbc_infoAlloc.gridy = 1;
        getContentPane().add(this.allocPanel, gbc_infoAlloc);
    }

    private void createContextPanels() {
        JPanel contextPanel = new JPanel();
        GridBagConstraints gbc_contextPanel = new GridBagConstraints();
        gbc_contextPanel.gridheight = 3;
        gbc_contextPanel.insets = new Insets(0, 0, 5, 5);
        gbc_contextPanel.fill = GridBagConstraints.BOTH;
        gbc_contextPanel.gridx = 0;
        gbc_contextPanel.gridy = 0;
        getContentPane().add(contextPanel, gbc_contextPanel);
        contextPanel.setLayout(new BoxLayout(contextPanel, BoxLayout.Y_AXIS));

        final int NUMBER_OF_CONTEXTS = 4;

        this.contextPanels = new ArrayList<ContextPanel>(NUMBER_OF_CONTEXTS);
        for (int i = 0; i < NUMBER_OF_CONTEXTS; i++) {
            ContextPanel panel = new ContextPanel(scene, i);
            this.contextPanels.add(panel);
            panel.setPreferredSize(new Dimension(175, 175));
            panel.setMinimumSize(new Dimension(175, 175));
            contextPanel.add(panel);
        }

        scene.getContextManager().addListener(new ContextListener() {
            @Override
            public void onStateActivated(ContextManager manager, State state) {
                int firstState = Math.max(0, manager.count() - 1 - contextPanels.size());
                for (int i = 0; i < contextPanels.size(); i++) {
                    ContextPanel panel = contextPanels.get(i);

                    int stateIndex = firstState + i;
                    if (stateIndex < manager.count() - 1) {
                        panel.setState(manager.getState(stateIndex));
                    } else {
                        panel.setState(null);
                    }
                }
            }
        });
    }

    /**
     * La fonction creationBarreMenue permet de creer la barre de menu
     */
    public void createMenuBar() {

        menuBar = new JMenuBar();
        setJMenuBar(menuBar);
        MenuListener menuListener = new MenuListener(canvas, this);

        // menu FICHIER
        JMenu fileMenu = new JMenu(Strings.get("menu.file")); //$NON-NLS-1$
        menuBar.add(fileMenu, BorderLayout.LINE_START);
        menuBar.setBorderPainted(true);

        // item OUVRIR
        openItem = new JMenuItem(Strings.get("menu.file.open")); //$NON-NLS-1$
        openItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));
        openItem.addActionListener(menuListener);
        fileMenu.add(openItem);
        fileMenu.add(new JSeparator());

        // item RETOUR AU DEBUT
        resetItem = new JMenuItem(Strings.get("menu.file.reset")); //$NON-NLS-1$
        resetItem.addActionListener(menuListener);
        fileMenu.add(resetItem);

        // item ZOOM AVANT
        zoomInItem = new JMenuItem(Strings.get("menu.file.zoomIn")); //$NON-NLS-1$
        zoomInItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_MASK));
        zoomInItem.addActionListener(menuListener);
        fileMenu.add(zoomInItem);

        canvas.getCamera().addListener(new CameraListener() {
            @Override
            public void onCameraChanged(Camera camera) {
                zoomInItem.setEnabled(camera.canZoomIn());
            }
        });

        // item ZOOM ARRIERE
        zoomOutItem = new JMenuItem(Strings.get("menu.file.zoomOut")); //$NON-NLS-1$
        zoomOutItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_MASK));
        zoomOutItem.addActionListener(menuListener);
        fileMenu.add(zoomOutItem);

        // item screenshot
        screenshotItem = new JMenuItem(Strings.get("menu.file.screenshot")); //$NON-NLS-1$
        screenshotItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                MainWindow.this.canvas.setScreenshot();
                MainWindow.this.canvas.display();
            }
        });
        screenshotItem.setEnabled(false);
        fileMenu.add(screenshotItem);
        fileMenu.addSeparator();

        // item QUITTER
        quitItem = new JMenuItem(Strings.get("menu.file.quit")); //$NON-NLS-1$
        quitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_MASK));
        quitItem.addActionListener(menuListener);
        fileMenu.add(quitItem);

        // Menu 'Options'
        JMenu optionsMenu = new JMenu(Strings.get("menu.options")); //$NON-NLS-1$
        menuBar.add(optionsMenu, BorderLayout.CENTER);
        menuBar.setBorderPainted(true);

        // item 'Use short names'
        final JMenuItem useShortNamesItem = new JCheckBoxMenuItem(Strings.get("menu.options.useShortNames")); //$NON-NLS-1$
        useShortNamesItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                scene.getPreferences().useShortNames(useShortNamesItem.isSelected());
                allocPanel.repaint();
            }
        });
        useShortNamesItem.setSelected(scene.getPreferences().useShortNames());
        optionsMenu.add(useShortNamesItem);

        final JMenuItem colorSettingItem = new JMenuItem(Strings.get("menu.options.paramCoul")); //$NON-NLS-1$
        colorSettingItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                ColorSettingDialog f = new ColorSettingDialog(scene);
                f.setVisible(true);
            }
        });
        optionsMenu.add(colorSettingItem);

        // BOUTON PRECEDENT
        undo = new JButton(new ImageIcon(ClassLoader.getSystemClassLoader().getResource("images/Undo-icon.png"))); //$NON-NLS-1$
        undo.setPreferredSize(new Dimension(50, 25));
        undo.addActionListener(menuListener);
        menuBar.add(undo, BorderLayout.CENTER);

        redo = new JButton(new ImageIcon(ClassLoader.getSystemClassLoader().getResource("images/Redo-icon.png"))); //$NON-NLS-1$
        redo.setPreferredSize(new Dimension(50, 25));
        redo.addActionListener(menuListener);
        menuBar.add(redo, BorderLayout.CENTER);

        textBoxM1 = new JLabel(Strings.get("toolbar.colour") + " : "); //$NON-NLS-1$ //$NON-NLS-2$
        menuBar.add(textBoxM1, BorderLayout.CENTER);
        boxM1 = new JComboBox(scene.getColorMetrics().toArray());
        boxM1.setSelectedItem(this.scene.getColorMetric());
        Dimension dimBoxM = new Dimension(125, 20);
        boxM1.setPreferredSize(dimBoxM);
        boxM1.setMinimumSize(dimBoxM);
        boxM1.setMaximumSize(dimBoxM);
        boxM1.setEnabled(false);
        boxM1.addActionListener(menuListener);
        menuBar.add(boxM1, BorderLayout.CENTER);

        textValMaxM1 = new JLabel(Strings.get("toolbar.maxValue") + " : "); //$NON-NLS-1$ //$NON-NLS-2$
        menuBar.add(textValMaxM1, BorderLayout.CENTER);

        this.scene.setMaxColorValue(this.scene.getColorMetric().getMaxValue());
        maxColorMetricTextField = new JTextField("" + this.scene.getMaxColorValue()); //$NON-NLS-1$
        Dimension dimTextField = new Dimension(50, 20);
        maxColorMetricTextField.setPreferredSize(dimTextField);
        maxColorMetricTextField.setMinimumSize(dimTextField);
        maxColorMetricTextField.setMaximumSize(dimTextField);
        maxColorMetricTextField.setEnabled(false);
        maxColorMetricTextField.addActionListener(menuListener);
        menuBar.add(maxColorMetricTextField, BorderLayout.CENTER);

        textBoxM2 = new JLabel(Strings.get("toolbar.angle") + " : "); //$NON-NLS-1$ //$NON-NLS-2$
        menuBar.add(textBoxM2, BorderLayout.CENTER);

        boxM2 = new JComboBox(this.scene.getAngleMetrics().toArray());
        boxM2.setSelectedItem(this.scene.getAngleMetric());

        boxM2.setPreferredSize(dimBoxM);
        boxM2.setMinimumSize(dimBoxM);
        boxM2.setMaximumSize(dimBoxM);
        boxM2.setEnabled(false);
        boxM2.addActionListener(menuListener);
        menuBar.add(boxM2, BorderLayout.CENTER);

        searchTextField = new JTextField(Strings.get("toolbar.search")); //$NON-NLS-1$
        Dimension dimTextSearch = new Dimension(200, 20);
        searchTextField.setPreferredSize(dimTextSearch);
        searchTextField.setMinimumSize(dimTextSearch);
        searchTextField.setMaximumSize(dimTextSearch);
        searchTextField.addActionListener(menuListener);
        searchTextField.setEnabled(false);
        menuBar.add(searchTextField, BorderLayout.CENTER);

        closeSearch = new JButton(new ImageIcon(ClassLoader.getSystemClassLoader().getResource("images/clear_search.jpg"))); //$NON-NLS-1$
        closeSearch.setPreferredSize(new Dimension(20, 20));
        closeSearch.setMinimumSize(new Dimension(20, 20));
        closeSearch.setMaximumSize(new Dimension(20, 20));
        closeSearch.addActionListener(menuListener);
        closeSearch.setEnabled(false);
        menuBar.add(closeSearch, BorderLayout.LINE_END);

        scene.addListener(new SceneAdapter() {
            @Override
            public void onMaxColorValueChanged(Scene scene, int value) {
                maxColorMetricTextField.setText(String.valueOf(value));
            }

            @Override
            public void onModelChanged(Scene scene, CGModel model) {
                boxM1.setModel(new DefaultComboBoxModel(scene.getColorMetrics().toArray()));
                boxM2.setModel(new DefaultComboBoxModel(scene.getAngleMetrics().toArray()));
                boxM1.setSelectedItem(scene.getColorMetric());
                boxM2.setSelectedItem(scene.getAngleMetric());

                boolean enable = (model != null);
                screenshotItem.setEnabled(enable);
                boxM1.setEnabled(enable);
                boxM2.setEnabled(enable);
                maxColorMetricTextField.setEnabled(enable);
                searchTextField.setEnabled(enable);
                closeSearch.setEnabled(enable);
            }

        });
    }

    private class MenuListener implements ActionListener {
        private Canvas canvas;
        private MainWindow window;

        public MenuListener(Canvas canvas, MainWindow fenetre) {
            this.canvas = canvas;
            this.window = fenetre;
        }

        @Override
        public void actionPerformed(ActionEvent event) {
            Object source = event.getSource();
            if (source == openItem) {
                OpenFile action = new OpenFile(scene);
                action.run();
            } else if (source == resetItem) {
                this.canvas.reset();
            } else if (source == zoomInItem) {
                this.canvas.zoomIn();
            } else if (source == zoomOutItem) {
                this.canvas.zoomOut();
            } else if (source == quitItem) {
                window.dispose();
            } else if (source == undo) {
                scene.getUndoManager().undo();
            } else if (source == redo) {
                scene.getUndoManager().redo();
            } else if (source == boxM1) {
                ChangeColorMetric cmc = new ChangeColorMetric(canvas, MainWindow.this.scene.getColorMetric(), (Metric) boxM1.getSelectedItem());
                scene.getUndoManager().perform(cmc);
            } else if (source == boxM2) {
                ChangeAngleMetric cma = new ChangeAngleMetric(canvas, MainWindow.this.scene.getAngleMetric(), (Metric) boxM2.getSelectedItem());
                scene.getUndoManager().perform(cma);
            }

            if (source == maxColorMetricTextField) {
                int oldValue = scene.getMaxColorValue();
                int newValue = Integer.parseInt(maxColorMetricTextField.getText());
                if (newValue > scene.getAngleMetric().getMaxValue()) {
                    newValue = scene.getAngleMetric().getMaxValue();
                }
                ChangeMaxColorValue cvmc = new ChangeMaxColorValue(canvas.getScene(), oldValue, newValue);
                scene.getUndoManager().perform(cvmc);
            } else if (source == searchTextField) {
                String stringSearch = searchTextField.getText();
                if (!stringSearch.isEmpty()) {
                    Search search = new Search(stringSearch, this.canvas.getCGModel().getMethods());
                    ArrayList<Method> methods = search.searchInList();
                    if (methods.isEmpty()) {
                        searchTextField.setBackground(Color.red);
                    } else {
                        searchTextField.setBackground(Color.WHITE);
                    }
                    this.canvas.display();
                }
            } else if (source == closeSearch) {
                ArrayList<Method> methods = canvas.getCGModel().getMethods();
                for (int i = 0; i < methods.size(); i++) {
                    methods.get(i).setSelected(false);
                }
                this.canvas.display();
            }
        }
    }

    public Canvas getCanvas() {
        return this.canvas;
    }
}
