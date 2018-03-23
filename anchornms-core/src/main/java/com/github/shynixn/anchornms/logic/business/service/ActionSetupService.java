package com.github.shynixn.anchornms.logic.business.service;

import com.github.shynixn.anchornms.logic.business.api.PluginServiceProvider;
import com.github.shynixn.anchornms.logic.business.mcp.Version;
import com.github.shynixn.anchornms.logic.business.relocator.JarRelocator;
import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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
public class ActionSetupService implements PluginServiceProvider {

    private static final String DEV_FOLDER = "nms-tools";
    private static final String TO_BE_OBFUSCATED_JAR = "to-be-obfuscated.jar";

    private final Logger log;

    private final File userHomer = new File(System.getProperty("user.home"));
    private final File devTools;

    private final GradleService gradleService;

    /**
     * Initializes a new setup service with the targetFolder and logger.
     *
     * @param targetFolder targetFolder
     * @param log          logger
     */
    public ActionSetupService(File targetFolder, Logger log) {
        super();
        if (targetFolder == null) {
            throw new IllegalArgumentException("Folder cannot be null!");
        }

        if (log == null) {
            throw new IllegalArgumentException("Logger cannot be null!");
        }

        this.log = log;
        this.devTools = new File(targetFolder, DEV_FOLDER);
        if (!this.devTools.exists()) {
            this.devTools.mkdir();
        }
        this.gradleService = new GradleService(this.devTools, log);
    }

    /**
     * Generates a new library in the lib folder of the project for the given version.
     *
     * @param version version
     */
    public void generateLibrary(Version version) {
        if (version == null) {
            throw new IllegalArgumentException("Version cannot be null!");
        }

        this.log.info("Checking library " + version.getVersion() + " ...");

        final File targetLibraryFile = new File(this.devTools, "mcp-" + version.getVersion() + ".jar");
        final File temporaryLibraryFile = new File(this.devTools, "tmp-minecraftserver.jar");
        if (targetLibraryFile.exists()) {
            this.log.info("Finished checking library " + version.getVersion() + ". It does already exist.");
            return;
        }

        this.log.info("Generating library " + version.getVersion() + " via ForgeGradle...");

        try {
            this.gradleService.generateBuildGradleFor(version);
            this.gradleService.executeCommand("setupDecompWorkspace");

            final File minecraftServerFile = new File(this.userHomer, version.getGradleInstallPath());
            FileUtils.copyFile(minecraftServerFile, temporaryLibraryFile);
        } catch (IOException | InterruptedException | ZipException e) {
            throw new RuntimeException(e);
        }

        final JarRelocator jarRelocator = JarRelocator.from(temporaryLibraryFile, targetLibraryFile, this.log);
        final Map<String, String> pattern = new HashMap<>();
        pattern.put("net.minecraft", "net.minecraft.anchor." + version.getPackageVersion());
        jarRelocator.relocate(pattern);

        this.log.info("Generated " + targetLibraryFile.getName() + " in folder.");
        this.log.info("Finished checking library " + version.getVersion() + ". New library was created.");
    }

    /**
     * Returns the gradle service.
     *
     * @return service
     */
    public GradleService getGradleService() {
        return this.gradleService;
    }

    /**
     * Obfuscates the given jarFile for the given minecraft version-
     *
     * @param versions      version
     * @param inputJarFile  input
     * @param outputJarFile output
     */
    public void obfuscateJar(File inputJarFile, File outputJarFile, Version... versions) {
        if (inputJarFile == null || outputJarFile == null) {
            throw new IllegalArgumentException("Define an input and an outputJar!");
        }

        if (versions.length == 0) {
            throw new IllegalArgumentException("One version has to be atleast defined!");
        }

        if (!inputJarFile.exists()) {
            throw new IllegalArgumentException("Input file does not exist!");
        }

        this.log.info("Obfuscating jar " + inputJarFile.getName() + " ...");

        final File tobeObfuscated = new File(this.devTools, TO_BE_OBFUSCATED_JAR);
        File finalObfuscatedJar;
        try {
            FileUtils.copyFile(inputJarFile, tobeObfuscated);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }

        for (final Version version : versions) {

            this.log.info("Relocating " + version.getVersion() + " ...");
            final Map<String, String> pattern = new HashMap<>();
            pattern.put("net.minecraft.anchor." + version.getPackageVersion(), "net.minecraft");

            try {
                final JarRelocator jarRelocator = JarRelocator.from(tobeObfuscated, this.log);
                jarRelocator.relocate(pattern);
                this.log.info("Successfully relocated " + version.getVersion() + ".");
            } catch (final Exception e) {
                this.log.info("Relocating and obfuscating skipped.");
                return;
            }

            try {
                this.log.info("Obfuscating " + version.getVersion() + " via ForgeGradle...");
                this.gradleService.generateBuildGradleFor(version);
                this.gradleService.executeCommand("build");
            } catch (IOException | InterruptedException | ZipException e) {
                throw new RuntimeException(e);
            }

            this.log.info("Finished ForgeGradle process.");

            finalObfuscatedJar = new File(this.devTools, "build/libs/" + this.devTools.getName() + ".jar");
            if (!finalObfuscatedJar.exists()) {
                throw new RuntimeException("Obfuscated jar was not generated!");
            }

            try {
                FileUtils.copyFile(finalObfuscatedJar, tobeObfuscated);
                FileUtils.deleteQuietly(finalObfuscatedJar);
            } catch (final IOException e) {
                throw new RuntimeException(e);
            }
        }

        this.log.info("Setting output jar " + outputJarFile.getName() + "...");
        try {
            FileUtils.copyFile(tobeObfuscated, outputJarFile);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }

        this.log.info("File " + outputJarFile.getName() + " was created.");
        this.log.info("Finished obfuscating jar " + inputJarFile.getName() + ".");
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
        this.gradleService.close();
    }
}
