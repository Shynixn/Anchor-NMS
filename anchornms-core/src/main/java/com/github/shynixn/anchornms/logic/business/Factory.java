package com.github.shynixn.anchornms.logic.business;

import com.github.shynixn.anchornms.logic.business.api.PluginServiceProvider;
import com.github.shynixn.anchornms.logic.business.service.ActionSetupService;
import org.slf4j.Logger;

import java.io.File;

/**
 * Created by Shynixn 2018.
 * <p>
 * Version 1.2
 * <p>
 * MIT License
 * <p>
 * Copyright (c) 2018 by Shynixn
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
public class Factory {

    /**
     * Creates a new service which is capable of generating mcp libraries and obfuscating jar files.
     *
     * @param workingDirectory workingDirectory. For Maven this would be the target folder, for Gradle build folder.
     * @param logger           logger implementing slf4j
     * @return service
     */
    public static PluginServiceProvider createPluginServiceProvider(File sourceDirectory, File workingDirectory, Logger logger) {
        return new ActionSetupService(sourceDirectory, workingDirectory, logger);
    }
}
