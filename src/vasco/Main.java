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

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import vasco.view.MainWindow;

/**
 * La classe Main est la classe principale
 */
public class Main implements Runnable {
    private Scene scene;
    private File inputFile;

    private Main() {
        this(null);
    }

    public Main(File inputFile) {
        this.inputFile = inputFile;
    }

    @Override
    public void run() {
//        if (inputFile == null) {
//            inputFile = askForInputFile();
//            if (inputFile == null) return;
//        }
//
//        if (!inputFile.exists()) {
//            fatalError(Strings.get("Main.fileNotFound") + ": " + inputFile); //$NON-NLS-1$ //$NON-NLS-2$
//            return;
//        }

        scene = new Scene();

//        try {
//            if (isBundle(inputFile)) {
//                loadBundle();
//            } else {
//                loadCCT();
//            }
//        } catch (IOException e) {
//            fatalError(Strings.get("Main.failedToLoadInputFile")); //$NON-NLS-1$
//        }

        openMainWindow();
        if (inputFile != null) {
            scene.loadModelFromFile(inputFile);
        }
    }

    private void openMainWindow() {
        MainWindow window = new MainWindow(scene);
        window.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // Catch cases where the window is closed via the 'close' button
                windowClosed(e);
            }

            @Override
            public synchronized void windowClosed(WindowEvent e) {
                // Catch cases where the window is disposed programmatically
                scene.getPreferences().save();
                System.exit(0);
            }
        });
    }

    public static void main(String[] args) {
        Main main;
        if (args.length == 1) {
            main = new Main(new File(args[0]));
        } else {
            main = new Main();
        }
        main.run();
    }

    public static void fatalError(String message) {
        System.err.println(message);
        System.exit(-1);
    }
}
