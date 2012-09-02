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

package vasco.metrics;

import java.awt.Color;
import java.util.Collection;

import vasco.model.Element;
import vasco.model.Method;
import vasco.util.Strings;

import elude.graphs.cg.attrs.Allocation;
import elude.graphs.cg.attrs.CapturedBy;

/**
 * This class represents the number of allocations of an element that are
 * eventually captured.
 */
public class Churn extends AbstractMetric {
    public Churn() {
        super(new Color(1, 1, 0.5f), new Color(0, 0.37f, 0));
    }

    @Override
    public String getName() {
        return Strings.get("metrics.allocCapt.name"); //$NON-NLS-1$
    }

    @Override
    public int getValue(Element element) {
        int numOfAllocationsCaptured = 0;
        Method method = (Method) element;

        Collection<Allocation> allocs = method.getAttributes(Allocation.class);
        for (Allocation alloc : allocs) {
            Collection<CapturedBy> captures = alloc.getAttributes(CapturedBy.class);
            if (!captures.isEmpty()) {
                numOfAllocationsCaptured += alloc.getCount();
            }
        }

        return numOfAllocationsCaptured;
    }

    @Override
    public int getTotalValue(Element element) {
        return 0;
    }
}
