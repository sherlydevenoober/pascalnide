/*
 *  Copyright 2017 Tran Le Duy
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.duy.pascal.interperter.builtin_libraries.file.exceptions

import java.io.File

/**
 * Reported by the following functions : Close, Read, Write, Seek, EOf, FilePos, FileSize, Flush,
 * BlockRead, and BlockWrite if the file is not open.
 *
 *
 * Created by Duy on 07-Apr-17.
 */
class FileNotOpenException : FileException {
    constructor(filePath: String) : super(filePath) {}

    constructor(filePath: File) : super(filePath) {
    }

    override val message: String?
        get() = "file not open " + filePath;
}
