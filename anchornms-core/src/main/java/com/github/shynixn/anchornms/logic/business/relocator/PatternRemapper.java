package com.github.shynixn.anchornms.logic.business.relocator;

import org.objectweb.asm.commons.Remapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

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
public class PatternRemapper extends Remapper {
    private final java.util.regex.Pattern classPattern = java.util.regex.Pattern.compile("(\\[*)?L(.+);");

    private final List<Pattern> patterns = new ArrayList<>();

    public PatternRemapper(Map<String, String> patternMap) {
        for (final String key : patternMap.keySet()) {
            this.patterns.add(new Pattern(key, patternMap.get(key)));
        }
    }

    /**
     * Checks if patterns are available.
     *
     * @return available
     */
    public boolean hasPatterns() {
        return !this.patterns.isEmpty();
    }

    /**
     * Maps the given object value.
     *
     * @param object object
     * @return object
     */
    @Override
    public Object mapValue(final Object object) {
        if (object instanceof String) {
            String name = (String) object;
            String value = name;

            String prefix = "";
            String suffix = "";

            final Matcher m = this.classPattern.matcher(name);
            if (m.matches()) {
                prefix = m.group(1) + "L";
                suffix = ";";
                name = m.group(2);
            }

            for (final Pattern r : this.patterns) {
                if (r.isClassRelocateAble(name)) {
                    value = prefix + r.relocateClass(name) + suffix;
                    break;
                } else if (r.isPathRelocateAble(name)) {
                    value = prefix + r.relocatePath(name) + suffix;
                    break;
                }
            }

            return value;
        }

        return super.mapValue(object);
    }

    /**
     * Maps the object value with the given name.
     *
     * @param name name
     * @return data
     */
    @Override
    public String map(String name) {
        String value = name;
        String prefix = "";
        String suffix = "";

        final Matcher m = this.classPattern.matcher(name);
        if (m.matches()) {
            prefix = m.group(1) + "L";
            suffix = ";";
            name = m.group(2);
        }

        for (final Pattern r : this.patterns) {
            if (r.isPathRelocateAble(name)) {
                value = prefix + r.relocatePath(name) + suffix;
                break;
            }
        }

        return value;
    }
}
