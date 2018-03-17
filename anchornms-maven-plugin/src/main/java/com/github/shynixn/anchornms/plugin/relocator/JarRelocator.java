package com.github.shynixn.anchornms.plugin.relocator;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.codehaus.plexus.util.IOUtil;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.commons.RemappingClassAdapter;

import java.io.*;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipException;

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
public class JarRelocator {

    private final File inputFile;
    private final File outputFile;

    private final boolean temp;
    private final Log log;

    /**
     * Initializes a new relocator with the given parameters.
     *
     * @param inputFile  inputFile
     * @param outputFile outputFile
     * @param temp       temp
     * @param log        log
     */
    public JarRelocator(File inputFile, File outputFile, boolean temp, Log log) {
        if (inputFile == null) {
            throw new IllegalArgumentException("InputFile cannot be null!");
        }

        if (outputFile == null) {
            throw new IllegalArgumentException("OuputFile cannot be null!");
        }

        if (log == null) {
            throw new IllegalArgumentException("Log cannot be null!");
        }

        this.inputFile = inputFile;
        this.log = log;
        this.outputFile = outputFile;
        this.temp = temp;
    }

    public void relocate(Map<String, String> patternMap) throws MojoFailureException {
        if (patternMap == null) {
            throw new IllegalArgumentException("PatternMap cannot be null!");
        }

        if (this.outputFile.exists()) {
            FileUtils.deleteQuietly(this.outputFile);
        }

        try (JarOutputStream jarOutputStream = new JarOutputStream(new BufferedOutputStream(new FileOutputStream(this.outputFile)))) {
            final Set<String> resources = new HashSet<>();

            final PatternRemapper remapper = new PatternRemapper(patternMap);

            try (final JarFile jarFile = new JarFile(this.inputFile)) {
                for (final Enumeration<JarEntry> j = jarFile.entries(); j.hasMoreElements(); ) {
                    final JarEntry entry = j.nextElement();
                    final String name = entry.getName();

                    if (!entry.isDirectory()) {
                        final String mappedName = remapper.map(name);
                        this.remapItem(jarOutputStream, resources, remapper, jarFile, entry, name, mappedName);
                    }
                }
            }

        } catch (final IOException e) {
            this.log.error(e);
            throw new MojoFailureException(e.getMessage(), e);
        }

        if (this.temp) {
            try {
                this.replaceTempFile();
            } catch (final IOException e) {
                throw new MojoFailureException(e.getMessage(), e);
            }
        }
    }

    private void remapItem(JarOutputStream jarOutputStream, Set<String> resources, PatternRemapper remapper, JarFile jarFile, JarEntry entry, String name, String mappedName) throws IOException {
        try (final InputStream is = jarFile.getInputStream(entry)) {
            final int idx = mappedName.lastIndexOf('/');
            if (idx != -1) {
                final String dir = mappedName.substring(0, idx);
                if (!resources.contains(dir)) {
                    this.addDirectory(resources, jarOutputStream, dir);
                }
            }

            if (name.endsWith(".class")) {
                this.addRemappedClass(remapper, jarOutputStream, name, is);
            } else {
                if (!resources.contains(mappedName)) {
                    this.addResource(resources, jarOutputStream, mappedName, is);
                }
            }
        }
    }

    private void replaceTempFile() throws IOException {
        FileUtils.deleteQuietly(this.inputFile);
        FileUtils.copyFile(this.outputFile, this.inputFile);
    }

    private void addResource(final Set<String> resources, final JarOutputStream jos, final String name, final InputStream is) throws IOException {
        jos.putNextEntry(new JarEntry(name));
        IOUtil.copy(is, jos);
        resources.add(name);
    }

    private void addDirectory(final Set<String> resources, final JarOutputStream jos, final String name) throws IOException {
        if (name.lastIndexOf('/') > 0) {
            final String parent = name.substring(0, name.lastIndexOf('/'));
            if (!resources.contains(parent)) {
                this.addDirectory(resources, jos, parent);
            }
        }

        final JarEntry entry = new JarEntry(name + "/");
        jos.putNextEntry(entry);
        resources.add(name);
    }

    private void addRemappedClass(final PatternRemapper remapper, final JarOutputStream jos, final String name, final InputStream is) throws IOException {
        if (!remapper.hasPatterns()) {
            try {
                jos.putNextEntry(new JarEntry(name));
                IOUtil.copy(is, jos);
            } catch (final ZipException e) {
                this.log.error(e);
            }
        }

        final ClassReader classReader = new ClassReader(is);
        final ClassWriter classWriter = new ClassWriter(0);
        final ClassVisitor classVisitor = new RemappingClassAdapter(classWriter, remapper);

        try {
            classReader.accept(classVisitor, ClassReader.EXPAND_FRAMES);
        } catch (final Throwable e) {
            throw new RuntimeException(e);
        }

        final byte[] renamedClass = classWriter.toByteArray();
        final String mappedName = remapper.map(name.substring(0, name.indexOf('.')));

        try {
            jos.putNextEntry(new JarEntry(mappedName + ".class"));
            IOUtil.copy(renamedClass, jos);
        } catch (final ZipException e) {
            this.log.error(e);
        }
    }

    public static JarRelocator from(File jarFile, Log log) {
        return new JarRelocator(jarFile, new File(jarFile.getParent(), jarFile.getName() + "-relocate-temp.jar"), true, log);
    }

    public static JarRelocator from(File inputJarFile, File outPutJarFile, Log log) {
        return new JarRelocator(inputJarFile, outPutJarFile, false, log);
    }
}
