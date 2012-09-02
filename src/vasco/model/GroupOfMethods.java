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

import vasco.util.Strings;

import elude.graphs.cg.EType;

/**
 * La classe GroupOfMethods représente un groupe de méthodes
 * 
 * @author fleur
 */
public class GroupOfMethods extends AggregateElement {
    /**
     * Constructeur de la classe GroupOfMethods
     * 
     * @param methods
     *            : liste des méthodes du groupe
     */
    public GroupOfMethods(Collection<Method> methods) {
        super(methods);
    }

    /**
     * La fonction ajouteMethod permet d'ajouter une méthode a la liste des
     * méthodes du groupe
     * 
     * @param m
     *            la méthode a ajouter
     */
    public void addMethod(Method m) {
        this.elements.add(m);

    }

    @Override
    public String getName() {
        return Strings.get("info.group.prefix") + " " + this.elements.size() + " " + Strings.get("info.group.suffix"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
    }

    @Override
    public Map<EType, Integer> getAllocatedTypes() {
        final Map<EType, Integer> types = new HashMap<EType, Integer>();
        this.forEach(new Procedure() {
            @Override
            public void apply(Method e) {
                merge(e.getAllocatedTypes(), types);
            }
        });
        return types;
    }

    @Override
    public Map<EType, Integer> getCapturedTypes() {
        final Map<EType, Integer> types = new HashMap<EType, Integer>();
        this.forEach(new Procedure() {
            @Override
            public void apply(Method e) {
                merge(e.getCapturedTypes(), types);
            }
        });
        return types;
    }

    @Override
    public boolean in(Collection<?> c) {
        if (c.isEmpty())
            return false; // optimization
        final List<Method> allMethods = new ArrayList<Method>();
        this.forEach(new Procedure() {
            @Override
            public void apply(Method e) {
                allMethods.add(e);
            }
        });

        for (Method m : allMethods) {
            if (c.contains(m))
                return true;
        }
        return false;
    }
}
