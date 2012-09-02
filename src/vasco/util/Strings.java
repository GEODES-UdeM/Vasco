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

package vasco.util;

import iceberg.analysis.types.Types;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import vasco.model.Element;

public final class Strings {
    public static final int NONE = 0;
    public static final int SHORT_CLASS_NAME = 0x01;
    public static final int SHORT_ARG_NAMES  = 0x02;

    private static final String BUNDLE_NAME = "Strings"; //$NON-NLS-1$

    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

    private Strings() {
    }

    public static String get(String key) {
        try {
            return RESOURCE_BUNDLE.getString(key);
        } catch (MissingResourceException e) {
            return '!' + key + '!';
        }
    }

    public static String getElementName(Element element, int options) {
        String name = element.getName();
        String type, methodName, methodDescriptor;
        int dotPosition = name.lastIndexOf('.');
        if (dotPosition >= 0) {
            type = name.substring(0, dotPosition);
        } else {
            type = null;
        }

        int parenthesisPosition = name.indexOf('(');
        if (parenthesisPosition >= 0) {
            methodName = name.substring(dotPosition + 1, parenthesisPosition);
            methodDescriptor = name.substring(parenthesisPosition);
            if ((options & SHORT_ARG_NAMES) != 0) {
                methodDescriptor = Types.getShortSignature(methodDescriptor);
            }
        } else {
            methodName = name.substring(dotPosition + 1);
            methodDescriptor = ""; //$NON-NLS-1$
        }

        if ((options & SHORT_CLASS_NAME) != 0) {
            type = Types.shortJavaName(type);
        }

        StringBuilder sb = new StringBuilder();
        if (type != null) {
            sb.append(type);
            sb.append('.');
        }
        sb.append(methodName);
        sb.append(methodDescriptor);
        return sb.toString();
    }
}
