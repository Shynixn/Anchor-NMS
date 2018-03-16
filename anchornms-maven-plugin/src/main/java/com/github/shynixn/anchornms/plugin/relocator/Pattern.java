package com.github.shynixn.anchornms.plugin.relocator;

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
public class Pattern {
    private static final int CLASS_LENGTH = 6;

    private final String pattern;
    private final String newPattern;
    private final String patternPath;
    private final String newPatternPath;

    public Pattern(String pattern, String newPattern) {
        this.pattern = pattern.replace('/', '.');
        this.patternPath = pattern.replace('.', '/');

        this.newPattern = newPattern.replace('/', '.');
        this.newPatternPath = newPattern.replace('.', '/');
    }

    /**
     * Relocates the path
     * @param path
     * @return
     */
    public String relocatePath(final String path) {
        return path.replaceFirst(this.patternPath, this.newPatternPath);
    }

    public String relocateClass(final String clazz) {
        return clazz.replaceFirst(this.pattern, this.newPattern);
    }

    public boolean isPathRelocateAble(String path) {
        String tempPath = path;
        if (tempPath.endsWith(".class")) {
            tempPath = tempPath.substring(0, tempPath.length() - CLASS_LENGTH);
        }

        return tempPath.startsWith(this.patternPath) || tempPath.startsWith("/" + this.patternPath);
    }

    public boolean isClassRelocateAble(final String clazz) {
        return clazz.indexOf('/') < 0 && this.isPathRelocateAble(clazz.replace('.', '/'));
    }
}
