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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import elude.graphs.cg.EType;

public abstract class AggregateElement implements Element, Iterable<Element> {
    protected final Collection<Element> elements;
    private int id = -1;

    /**
     * Creates a new aggregate element from the specified elements.
     *
     * @param elements
     *            The selected elements.
     */
    @SuppressWarnings("unchecked")
    public AggregateElement(Collection<? extends Element> elements) {
        this.elements = (Collection<Element>) elements;
    }

    @Override
    public int getID() {
        return this.id;
    }

    @Override
    public void setID(int id) {
        this.id = id;
    }

    @Override
    public int getNumberOfChildren() {
        int sum = 0;
        for (Element e : this) {
            sum += e.getNumberOfChildren();
        }
        return sum;
    }

    @Override
    public int getNumberOfAllocations() {
        int sum = 0;
        for (Element e : this) {
            sum += e.getNumberOfAllocations();
        }
        return sum;
    }

    @Override
    public int getTotalAllocations() {
        int sum = 0;
        for (Element e : this) {
            sum += e.getTotalAllocations();
        }
        return sum;
    }

    @Override
    public int getTotalCaptures() {
        int sum = 0;
        for (Element e : this) {
            sum += e.getTotalCaptures();
        }
        return sum;
    }

    @Override
    public int getNumberOfCaptures() {
        int sum = 0;
        for (Element e : this) {
            sum += e.getNumberOfCaptures();
        }
        return sum;
    }

    @Override
    public int getNumberOfTypes() {
        Set<EType> types = new HashSet<EType>();
        for (Element e : this) {
            types.addAll(e.getAllocatedTypes().keySet());
        }
        return types.size();
    }

    @Override
    public int getTotalTypes() {
        final Set<EType> types = new HashSet<EType>();
        this.forEach(new Procedure() {
            @Override
            public void apply(Method e) {
                types.addAll(e.getAllocatedTypes().keySet());
            }
        });
        return types.size();
    }

    @Override
    public boolean isEliminated() {
        return false;
    }

    public static void merge(Map<EType, Integer> what, Map<EType, Integer> into) {
        for (Map.Entry<EType, Integer> entry : what.entrySet()) {
            Integer current = into.get(entry.getKey());
            int v = (current != null ? current.intValue() : 0);
            into.put(entry.getKey(), v + entry.getValue());
        }
    }

    @Override
    public Map<EType, Integer> getAllocatedTypes() {
        final Map<EType, Integer> types = new HashMap<EType, Integer>();
        for (Element e : this) {
            merge(e.getAllocatedTypes(), types);
        }
        return types;
    }

    @Override
    public Map<EType, Integer> getCapturedTypes() {
        final Map<EType, Integer> types = new HashMap<EType, Integer>();
        for (Element e : this) {
            merge(e.getCapturedTypes(), types);
        }
        return types;
    }

    @Override
    public boolean in(Collection<?> c) {
        if (c.isEmpty())
            return false; // optimization

        for (Element e : this) {
            if (e.in(c)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((elements == null) ? 0 : elements.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj;
    }

    @Override
    public Iterator<Element> iterator() {
        return this.elements.iterator();
    }

    protected interface Procedure {
        public void apply(Method e);
    }

    protected void forEach(Procedure p) {
        forEach(p, this);
    }

    private static void forEach(Procedure p, Element root) {
        if (root instanceof AggregateElement) {
            AggregateElement aggregate = (AggregateElement) root;
            for (Element e : aggregate) {
                forEach(p, e);
            }
        } else {
            Method m = (Method) root;
            p.apply(m);
            for (Method child : m.getChildren()) {
                forEach(p, child);
            }
        }
    }
}
