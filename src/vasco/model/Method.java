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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import elude.graphs.cg.AttributeContainer;
import elude.graphs.cg.EAttribute;
import elude.graphs.cg.EType;
import elude.graphs.cg.attrs.Allocation;
import elude.graphs.cg.attrs.Capture;

/**
 * La classe Method représente une méthode
 *
 * @author fleur
 */
public class Method extends AttributeContainer implements Element {
    private int id = -1;
    private final String name;
    private Method parent;
    private ArrayList<Method> children;

    private int numberOfAllocations;
    private int totalAllocations;
    private int numberOfCaptures;
    private int totalCaptures;
    private int totalTypes;

    private int level;

    private ArrayList<Method> capturingMethods;
    private ArrayList<Method> allocatingMethods;

    private Map<EType, Integer> allocatedTypes;

    private boolean eliminated = false;
    private boolean isSelected = false;

    /**
     * Constructeur de la classe Method
     *
     * @param name
     *            nom de la méthode
     */
    public Method(String name) {
        this.name = name;
        this.parent = null;
        this.children = new ArrayList<Method>();
        this.capturingMethods = new ArrayList<Method>();
        this.allocatingMethods = new ArrayList<Method>();
        this.allocatedTypes = new HashMap<EType, Integer>();
    }

    @Override
    public void addAttribute(EAttribute attribute) {
        if (attribute instanceof Allocation) {
            Allocation alloc = (Allocation) attribute;
            this.setNumberOfAllocations(this.getNumberOfAllocations() + alloc.getCount());
        }
        if (attribute instanceof Capture) {
            Capture capt = (Capture) attribute;
            this.setNumberOfCaptures(this.getNumberOfCaptures() + capt.getCount());

        }
        super.addAttribute(attribute);
    }

    /**
     * Accesseur de la variable capturingMethods
     *
     * @return la valeur de la variable capturingMethods
     */
    public ArrayList<Method> getCapturingMethods() {
        return this.capturingMethods;
    }

    /**
     * La méthode addCapturingMethod permet d'ajouter une méthode a la liste
     * capturingMethod
     *
     * @param m
     *            la méthode a ajouter
     */
    public void addCapturingMethod(Method m) {
        if (!this.capturingMethods.contains(m)) {
            this.capturingMethods.add(m);
        }
    }

    /**
     * getAllocatingMethods
     *
     * @return allocatingMethods
     */
    public List<Method> getAllocatingMethods() {
        return this.allocatingMethods;
    }

    /**
     * addAllocatingMethod
     *
     * @param m
     */
    public void addAllocatingMethod(Method m) {
        if (!this.allocatingMethods.contains(m)) {
            this.allocatingMethods.add(m);
        }
    }

    @Override
    public int getNumberOfCaptures() {
        return this.numberOfCaptures;
    }

    public void setNumberOfCaptures(int numberOfCaptures) {
        this.numberOfCaptures = numberOfCaptures;
    }

    @Override
    public int getTotalCaptures() {
        return this.totalCaptures;
    }

    public void setTotalCaptures(int totalCaptures) {
        this.totalCaptures = totalCaptures;
    }

    @Override
    public int getID() {
        return id;
    }

    @Override
    public void setID(int num) {
        this.id = num;
    }

    @Override
    public String getName() {
        return name;
    }

    /**
     * Accesseur de la variable parent
     *
     * @return la valeur de la variable parent
     */
    public Method getParent() {
        return parent;
    }

    /**
     * Modifieur de la variable parent
     *
     * @param parent
     *            la nouvelle valeur de la variable parent
     */
    public void setParent(Method parent) {
        this.parent = parent;
    }

    /**
     * Accesseur de la variable children
     *
     * @return la valeur de la variable children
     */
    public ArrayList<Method> getChildren() {
        return children;
    }

    /**
     * Modifieur de la variable children
     *
     * @param children
     *            la nouvelle valeur de la variable children
     */
    public void setChildren(ArrayList<Method> children) {
        this.children = children;
    }

    @Override
    public int getNumberOfAllocations() {
        return numberOfAllocations;
    }

    public void setNumberOfAllocations(int numberOfAllocations) {
        this.numberOfAllocations = numberOfAllocations;
    }

    @Override
    public int getTotalAllocations() {
        return totalAllocations;
    }

    public void setTotalAllocations(int totalAllocations) {
        this.totalAllocations = totalAllocations;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;

        Method other = (Method) obj;

        if (id != other.id)
            return false;

        return true;
    }

    /**
     * Accesseur de la variable allocTypes
     *
     * @return la valeur de la variable allocTypes
     */
    @Override
    public Map<EType, Integer> getAllocatedTypes() {
        return allocatedTypes;
    }

    /**
     * Modifieur de la variable allocTypes
     *
     * @param allocatedTypes
     *            la nouvelle valeur de la variable allocTypes
     */
    public void setAllocatedTypes(Map<EType, Integer> allocatedTypes) {
        this.allocatedTypes = allocatedTypes;
    }

    @Override
    public Map<EType, Integer> getCapturedTypes() {
        Map<EType, Integer> types = new HashMap<EType, Integer>();
        for (Capture capt : this.getAttributes(Capture.class)) {
            EType type = capt.getType();
            if (types.containsKey(type)) {
                types.put(type, types.get(type) + capt.getCount());
            } else {
                types.put(type, capt.getCount());
            }
        }
        return types;
    }

    /**
     * Accesseur de la variable niveau
     *
     * @return la valeur de la variable niveau
     */
    public int getLevel() {
        return level;
    }

    /**
     * Modifieur de la variable niveau
     *
     * @param level
     *            nouvelle valeur de la variable niveau
     */
    public void setLevel(int level) {
        this.level = level;
    }

    @Override
    public int getNumberOfTypes() {
        return allocatedTypes.size();
    }

    @Override
    public int getTotalTypes() {
        return this.totalTypes;
    }

    public void setTotalTypes(int totalTypes) {
        this.totalTypes = totalTypes;
    }

    @Override
    public int getNumberOfChildren() {

        return children.size();
    }

    @Override
    public boolean isEliminated() {
        return eliminated;
    }

    public void setEliminated(boolean eliminated) {
        this.eliminated = eliminated;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        this.isSelected = selected;
    }

    @Override
    public boolean in(Collection<?> c) {
        return c.contains(this);
    }
}