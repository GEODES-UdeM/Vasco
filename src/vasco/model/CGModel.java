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

package vasco.model;

import java.awt.Image;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import javax.imageio.ImageIO;

import vasco.metrics.Metric;
import vasco.metrics.RegionChurn;

/**
 * La classe CGModel represente le modele du context global
 *
 * @author fleur
 */
public class CGModel implements Iterable<Method> {
    private String filename;
    private ArrayList<Method> methods;
    private Method root;
    private JarFile jar;

    public CGModel() {
        this.methods = new ArrayList<Method>();
    }

    public CGModel(JarFile jar) {
        this();
        setJar(jar);
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    /**
     * la fonction addNode ajoute une méthode node a la liste des methodes
     *
     * @param node
     */
    public void addNode(Method node) {
        if (node.getID() >= 0) {
            throw new IllegalArgumentException();
        }

        node.setID(this.methods.size());
        this.methods.add(node);
    }

    /**
     * La fonction addEdge ajoute un lien entre deux methodes
     *
     * @param parent
     *            premiere methode
     * @param child
     *            deuxieme methode
     */
    public void addEdge(Method parent, Method child) {
        parent.getChildren().add(child);
        child.setParent(parent);
    }

    /**
     * Modifieur de la variable root
     *
     * @param root
     *            la nouvelle valeur de la variable root
     */
    public void setRoot(Method root) {
        this.root = root;
    }

    /**
     * Accesseur de la variable listMethods
     *
     * @return la liste des méthodes
     */
    public ArrayList<Method> getMethods() {
        return this.methods;
    }

    /**
     * Accesseur de la variable root
     *
     * @return la valeur de la variable root
     */
    public Method getRoot() {
        return this.root;
    }

    /**
     * Modifieur de la variable listMethods
     *
     * @param methodsList
     *            nouvelle liste des methodes
     */
    public void setMethodsList(ArrayList<Method> methodsList) {
        this.methods = methodsList;
    }

    @Override
    public Iterator<Method> iterator() {
        return this.methods.iterator();
    }

    /**
     * La méthode hasConnectionGraphs permet de savoir s'il y a des graphes de
     * connexion dans un jar
     *
     * @return retourne vrai s'il y a des graphes de connexion
     */
    public boolean hasConnectionGraphs() {
        return this.jar != null;
    }

    private ZipEntry getConnectionGraphEntry(Method m) {
        return jar.getEntry("rcg/" + (m.getID() - 1) + ".png");  //$NON-NLS-1$ //$NON-NLS-2$
    }

    public Image getConnectionGraph(Method m) {
        if (hasConnectionGraphs()) {
            ZipEntry img = getConnectionGraphEntry(m);
            if (img != null) {
                try {
                    InputStream imgStream = jar.getInputStream(img);
                    return ImageIO.read(imgStream);
                } catch (IOException e) {
                    // fallthrough
                }
            }
        }

        return null;
    }

    public boolean hasConnectionGraph(Method method) {
        if (hasConnectionGraphs()) {
            return getConnectionGraphEntry(method) != null;
        }

        return false;
    }

    public void setJar(JarFile jar) {
        this.jar = jar;
        if (jar != null) this.setFilename(jar.getName());
    }

    public void updateMetrics() {
        updateMetrics(getRoot());
    }

    public void updateMetrics(Method root) {
        computeAllocations(root);
        computeTotalCaptures(root);
        computeTotalTypes(root);
    }

    /**
     * la fonction computeCaptsTot permet de calculer le total des captures
     * d'une methode et de tous ses enfants
     *
     * @param method
     *            methode a partir de laquelle on calcule le total
     * @return le total
     */
    private int computeTotalCaptures(Method method) {
        int total = method.getNumberOfCaptures();
        for (Method child : method.getChildren()) {
            if (!child.isEliminated()) {
                total += computeTotalCaptures(child);
            }
        }

        method.setTotalCaptures(total);
        return total;
    }

    /**
     * la fonction computeAllocs permet de calculer le total des allocations
     * d'une methode et de tous ses enfants
     *
     * @param method
     *            methode a partir de laquelle on calcule le total
     * @return le total
     */
    private int computeAllocations(Method method) {
        int total = method.getNumberOfAllocations();
        for (Method child : method.getChildren()) {
            if (!child.isEliminated()) {
                total += computeAllocations(child);
            }
        }

        method.setTotalAllocations(total);
        return total;
    }

    /**
     * la fonction computeNbTypesTot permet de calculer le total des types
     * d'allocations differents d'une methode et de tous ses enfants
     *
     * @param method
     *            methode a partir de laquelle on calcule le total
     * @return le total
     */
    private int computeTotalTypes(Method method) {
        int total = method.getNumberOfTypes();
        for (Method child : method.getChildren()) {
            if (!child.isEliminated()) {
                total += computeTotalTypes(child);
            }
        }

        method.setTotalTypes(total);
        return total;
    }

    /**
     * La fonction calculValMax permet de calculer la valeur maximum d'une
     * métrique dans une visualisation
     *
     * @param metric
     *            métrique
     * @return la valeur max
     */
    public int calculateMaxValue(Metric metric) {
        int maxValue = 0;
        ArrayList<Method> methods = getMethods();

        for (int i = 0; i < methods.size(); i++) {
            if (!methods.get(i).isEliminated() && metric.getValue(methods.get(i)) > maxValue) {
                maxValue = metric.getValue(methods.get(i));
                methods.get(i);
            }
        }

        if (metric.getClass() == RegionChurn.class && maxValue > 100) {
            // FIXME: remove class-specific code
            maxValue = 100;
        }
        return maxValue;
    }
}
