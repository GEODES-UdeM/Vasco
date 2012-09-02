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

package vasco;

import java.util.ArrayList;
import java.util.List;

import vasco.model.State;
import vasco.util.Strings;

public class ContextManager {
    public static final int CURRENT = -1;

    private List<State> states = new ArrayList<State>(4);
    private int currentStateIndex = -1;

    private List<ContextListener> listeners = new ArrayList<ContextListener>(1);

    public ContextManager() {
        // empty
    }

    public int count() {
        return currentStateIndex + 1;
    }

    private void restoreState(int index, boolean forceUpdate) {
        if (index == currentStateIndex) {
            if (forceUpdate) notifyStateActivated();
            return;
        } else if (index >= 0 && index < currentStateIndex) {
            // Release memory
            for (int i = index + 1; i <= currentStateIndex; i++) {
                states.set(i, null);
            }
            currentStateIndex = index;
            getCurrentState().setActiveMethod(null);

            notifyStateActivated();
        } else {
            // oops
            throw new IllegalArgumentException(Strings.get("ContextManager.notAnOldState") + ": " + index); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }

    public void restoreState(State oldState) {
        restoreState(states.indexOf(oldState), false);
    }

    public void restoreState(State oldState, boolean forceUpdate) {
        restoreState(states.indexOf(oldState), forceUpdate);
    }

    public void reset() {
        restoreState(0, false);
    }

    public void reset(boolean forceUpdate) {
        restoreState(0, forceUpdate);
    }

    public State getState(int index) {
        if (index < 0) {
            // index is relative to the current one
            return states.get(currentStateIndex + index + 1);
        } else {
            return states.get(index);
        }
    }

    public void addListener(ContextListener listener) {
        this.listeners.add(listener);
    }

    public void removeListener(ContextListener listener) {
        this.listeners.remove(listener);
    }

    private void notifyStateActivated() {
        State state = states.get(currentStateIndex);
        for (ContextListener listener: listeners) {
            listener.onStateActivated(this, state);
        }
    }

    public State getCurrentState() {
        if (currentStateIndex >= 0) {
            return states.get(currentStateIndex);
        } else {
            return null;
        }
    }

    public void invalidateAll() {
        for (State state: states) {
            if (state != null) {
                state.getLayout().setStale();
            }
        }
    }

    public void enterState(State state) {
        if (currentStateIndex >= 0) {
            getCurrentState().setActiveMethod(state.getLayout().getRoot());
        }

        if (currentStateIndex < states.size() - 1) {
            states.set(currentStateIndex + 1, state);
        } else {
            states.add(state);
        }
        currentStateIndex++;
        notifyStateActivated();
    }
}
