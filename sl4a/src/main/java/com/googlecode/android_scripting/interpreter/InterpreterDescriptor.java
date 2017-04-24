/*
 * Copyright (C) 2010 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.googlecode.android_scripting.interpreter;

import android.content.Context;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * Provides interpreter-specific info for execution/installation/removal purposes.
 *
 * @author Alexey Reznichenko (alexey.reznichenko@gmail.com)
 */
public interface InterpreterDescriptor {

    /**
     * Returns unique name of the interpreter.
     */
    String getName();

    /**
     * Returns display name of the interpreter.
     */
    String getNiceName();

    /**
     * Returns supported script-file extension.
     */
    String getExtension();

    /**
     * Returns interpreter version number.
     */
    int getVersion();

    /**
     * Returns the binary as a File object. Context is the InterpreterProvider's {@link Context} and
     * is provided to find the interpreter installation directory.
     */
    File getBinary(Context context);

    /**
     * Returns execution parameters in case when script name is not provided (when interpreter is
     * started in a shell mode);
     */
    String getInteractiveCommand(Context context);

    /**
     * Returns command line arguments to execute a with a given script (format string with one
     * argument).
     */
    String getScriptCommand(Context context);

    /**
     * Returns an array of command line arguments required to execute the interpreter (it's essential
     * that the order in the array is consistent with order of arguments in the command line).
     */
    List<String> getArguments(Context context);

    /**
     * Should return a map of environment variables names and their values (or null if interpreter
     * does not require any environment variables).
     */
    Map<String, String> getEnvironmentVariables(Context context);

    /**
     * Returns true if interpreter has an archive.
     */
    boolean hasInterpreterArchive();

    /**
     * Returns true if interpreter has an extras archive.
     */
    boolean hasExtrasArchive();

    /**
     * Returns true if interpreter comes with a scripts archive.
     */
    boolean hasScriptsArchive();

    /**
     * Returns file name of the interpreter archive.
     */
    String getInterpreterArchiveName();

    /**
     * Returns file name of the extras archive.
     */
    String getExtrasArchiveName();

    /**
     * Returns file name of the scripts archive.
     */
    String getScriptsArchiveName();

    /**
     * Returns URL location of the interpreter archive.
     */
    String getInterpreterArchiveUrl();

    /**
     * Returns URL location of the scripts archive.
     */
    String getScriptsArchiveUrl();

    /**
     * Returns URL location of the extras archive.
     */
    String getExtrasArchiveUrl();

    /**
     * Returns true if interpreter can be executed in interactive mode.
     */
    boolean hasInteractiveMode();
}