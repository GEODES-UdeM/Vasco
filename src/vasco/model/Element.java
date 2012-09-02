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
import java.util.Map;

import elude.graphs.cg.EType;

/**
 * L'interface Element représente un élément
 *
 * @author fleur
 */
public interface Element {
    /**
     * Chaque élément a son numero
     *
     * @return le numéro de l'élément
     */
    public abstract int getID();

    /**
     * Modifieur de la variable num
     *
     * @param id
     *            la nouvelle valeur de la variable num
     */
    public abstract void setID(int id);

    /**
     * Chaque élément a un nom
     *
     * @return le nom de l'élément
     */
    public abstract String getName();

    /**
     * Chaque élément connait son nombre d'enfants
     *
     * @return le nombre d'enfants de l'élément
     */
    public abstract int getNumberOfChildren();

    /**
     * Chaque élément connait son nombre d'allocations Pour les groupes de
     * méthodes : le nombre d'allocations est la somme de toutes les
     * allocations des méthodes du groupe
     *
     * @return le nombre d'allocation
     */
    public abstract int getNumberOfAllocations();

    /**
     * Chaque élément connait son nombre d'allocations total Pour les groupes
     * de méthodes : le nombre d'allocations total est la somme de toutes les
     * allocations total des méthodes du groupe
     *
     * @return le nombre d'allocation total
     */
    public abstract int getTotalAllocations();

    /**
     * Chaque élément connait son nombre de captures Pour les groupes de
     * méthodes : le nombre de captures est la somme de toutes les captures des
     * méthodes du groupe
     *
     * @return le nombre des captures
     */
    public abstract int getTotalCaptures();

    /**
     * Chaque élément connait son nombre de captures total Pour les groupes de
     * méthodes : le nombre de captures total est la somme de toutes les
     * captures total des méthodes du groupe
     *
     * @return le nombre des captures total
     */
    public abstract int getNumberOfCaptures();

    /**
     * Chaque élément connait son nombre de types d'allocations Pour les
     * groupes de méthodes : le nombre de types d'allocations est la somme de
     * tous les types d'allocations des méthodes du groupe
     *
     * @return le nombre de types d'allocations
     */
    public abstract int getNumberOfTypes();

    /**
     * Chaque élément connait son nombre de types d'allocations total Pour les
     * groupes de méthodes : le nombre de types d'allocations total est la
     * somme de tous les types d'allocations total des méthodes du groupe
     *
     * @return le nombre de types d'allocations total
     */
    public abstract int getTotalTypes();

    /**
     * Chaque élément sait s'il est éliminé ou non
     *
     * @return la valeur booléenne pour savoir si l'élément est éliminé ou
     *         non
     */
    public abstract boolean isEliminated();

    public boolean in(Collection<?> c);

    public Map<EType, Integer> getAllocatedTypes();

    public Map<EType, Integer> getCapturedTypes();
}
