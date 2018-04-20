package com.github.shynixn.anchornms.logic.business.service;

import com.github.shynixn.anchornms.logic.business.mcp.Version;
import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.URL;

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
public class GradleService implements AutoCloseable {
    private final File buildFolder;
    private final Logger log;

    public GradleService(File buildFolder, Logger log) {
        this.buildFolder = buildFolder;
        this.log = log;
    }

    /**
     * Generates a build gradle file for the given nms version.
     *
     * @param version version
     * @throws IOException exception
     */
    public void generateBuildGradleFor(Version version, String accessTransformer) throws IOException {
        if (version == null) {
            throw new IllegalArgumentException("Version cannot be null!");
        }

        final String transform;
        if (accessTransformer == null) {
            transform = "";
        } else {
            transform = "manifest.attributes('FMLAT': '" + accessTransformer + "')";
        }

        final String resource = "build.gradle.txt";

        final File buildGradleTargetFile = new File(this.buildFolder, "build.gradle");
        final URL buildGradleFile = Thread.currentThread().getContextClassLoader().getResource(resource);
        FileUtils.copyURLToFile(buildGradleFile, buildGradleTargetFile);

        String content = FileUtils.readFileToString(buildGradleTargetFile, "UTF-8");
        content = content.replace("<SNAPSHOTMAPPING>", version.getSnapshotVersion())
                .replace("<VERSION>", version.getVersion())
                .replace("<FORGEGRADLEVERSION>", version.getForgeGradleVersion())
                .replace("<ACCESSTRANSFORMER>", transform)
                .replace("<PACKAGEVERSION>", version.getPackageVersion());

        FileUtils.write(buildGradleTargetFile, content, "UTF-8");
        this.log.info("Updated build.gradle.");
    }

    /**
     * Executes a new gradle command.
     *
     * @param command command
     * @throws InterruptedException exception
     * @throws ZipException         exception
     * @throws IOException          exception
     */
    public void executeCommand(String command) throws InterruptedException, ZipException, IOException {
        if (command == null) {
            throw new IllegalArgumentException("Commandcannot be null!");
        }

        this.executeGradleCommand(this.buildFolder, command);
    }

    private void setupProjectGradle() throws IOException, ZipException, InterruptedException {
        this.executeGradleCommand(this.buildFolder, "wrapper", true);
    }

    private void executeGradleCommand(File commandFolder, String command) throws IOException, InterruptedException, ZipException {
        this.executeGradleCommand(commandFolder, command, false);
    }

    private void executeGradleCommand(File commandFolder, String command, boolean recursive) throws IOException, InterruptedException, ZipException {
        final String winCommand = "gradle.bat";
        final String otherCommand = "gradle";

        try {
            this.executeCommand(commandFolder, otherCommand, command);
        } catch (final IOException ex) {
            try {
                this.executeCommand(commandFolder, winCommand, command);
            } catch (final IOException e) {
                if (!recursive) {
                    this.setupProjectGradle();
                    this.executeGradleCommand(commandFolder, command, true);
                } else {
                    this.log.error(ex.getMessage(), ex);
                    this.log.error(e.getMessage(), e);
                    throw new RuntimeException("Failed to execute gradle command.", e);
                }
            }
        }
    }

    private void executeCommand(File folder, String... params) throws InterruptedException, IOException {
        final ProcessBuilder builder = new ProcessBuilder(params);
        builder.inheritIO();
        builder.directory(folder);
        final Process p = builder.start();
        p.waitFor();
    }

    /**
     * Closes this resource, relinquishing any underlying resources.
     * This method is invoked automatically on objects managed by the
     * {@code try}-with-resources statement.
     * However, implementers of this interface are strongly encouraged
     * to make their {@code close} methods idempotent.
     *
     * @throws Exception if this resource cannot be closed
     */
    @Override
    public void close() throws Exception {
        this.executeGradleCommand(this.buildFolder, "--stop");
    }
}
