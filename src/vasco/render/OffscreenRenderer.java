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

package vasco.render;

import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.media.nativewindow.AbstractGraphicsDevice;
import javax.media.opengl.GL2;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLContext;
import javax.media.opengl.GLDrawable;
import javax.media.opengl.GLDrawableFactory;
import javax.media.opengl.GLProfile;

import vasco.Scene;
import vasco.model.Method;
import vasco.model.State;

import com.jogamp.opengl.FBObject;
import com.jogamp.opengl.FBObject.Attachment;
import com.jogamp.opengl.util.awt.Screenshot;

public class OffscreenRenderer {
    private Viewport viewport;
    private FBObject fbo;
    private ContextRenderer renderer;

    public OffscreenRenderer(State state, Camera camera) {
        this(state.getLayout(), state.getLayout().getScene(), camera, state.getActiveMethod());
    }

    public OffscreenRenderer(SunburstLayout layout, Scene scene, Camera camera, Method highlightedMethod) {
        this.viewport = camera.getViewport();
        renderer = new ContextRenderer(scene, layout, highlightedMethod, camera);

//        if (factory.canCreateGLPbuffer(GLProfile.getDefaultDevice())) {
//            buffer = factory.createGLPbuffer(GLProfile.getDefaultDevice(),
//                            capabilities, null, width, height, null);
//            buffer.addGLEventListener(new ContextRenderer(scene, layout, highlightedMethod, camera));
//        } else {
//            System.err.println("PBuffers not supported");
//        }
    }

    public Image render() {
        final int width = viewport.getWidth();
        final int height = viewport.getHeight();

        AbstractGraphicsDevice device = GLProfile.getDefaultDevice();
        GLProfile glProfile = GLProfile.getDefault(device);
        GLDrawableFactory factory = GLDrawableFactory.getFactory(glProfile);

        GLCapabilities capabilities = new GLCapabilities(glProfile);

        // For antialiasing
        capabilities.setSampleBuffers(true);
        capabilities.setNumSamples(4);

        capabilities.setDoubleBuffered(false);

        capabilities.setFBO(true);

        GLDrawable drawable = factory.createOffscreenDrawable(device,
                capabilities,
                null, width, height);
        GLContext context = drawable.createContext(null);
        drawable.setRealized(true);
        context.makeCurrent();
        GL2 gl = context.getGL().getGL2();
//        gl = new DebugGL2(gl);

        fbo = new FBObject();
        fbo.reset(gl, width, height);
        fbo.attachTexture2D(gl, 0, true);
        fbo.attachRenderbuffer(gl, Attachment.Type.DEPTH, 32);

        renderer.init(gl);
        renderer.display(gl);

        BufferedImage image = Screenshot.readToBufferedImage(viewport.getWidth(), viewport.getHeight());

        fbo.unbind(gl);
        fbo.destroy(gl);
        context.release();

        drawable.setRealized(false);

        return image;
    }

    public static Image render(State state, Camera camera) {
        return new OffscreenRenderer(state, camera).render();
    }
}
