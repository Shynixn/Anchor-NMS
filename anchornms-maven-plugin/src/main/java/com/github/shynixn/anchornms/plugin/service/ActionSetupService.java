package com.github.shynixn.anchornms.plugin.service;

import com.github.shynixn.anchornms.plugin.Version;
import com.github.shynixn.anchornms.plugin.relocator.JarRelocator;
import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
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
public class ActionSetupService implements AutoCloseable {

    private static final String DEV_FOLDER = "nms-tools";
    private static final String TO_BE_OBFUSCATED_JAR = "to-be-obfuscated.jar";

    private final Log log;

    private final File userHomer = new File(System.getProperty("user.home"));
    private final File devTools;

    private final GradleService gradleService;

    /**
     * Initializes a new setup service with the targetFolder and logger.
     *
     * @param targetFolder targetFolder
     * @param log          logger
     */
    public ActionSetupService(File targetFolder, Log log) {
        super();
        this.log = log;
        this.devTools = new File(targetFolder, DEV_FOLDER);
        if (!this.devTools.exists()) {
            this.devTools.mkdir();
        }
        this.gradleService = new GradleService(this.devTools, log);
    }

    /**
     * Generates a new sponge library in the lib folder of the project for the given version.
     *
     * @param version version
     * @throws IOException          exception
     * @throws ZipException         exception
     * @throws InterruptedException exception
     * @throws MojoFailureException exception
     */
    public void generateSpongeLibrary(Version version) throws IOException, ZipException, InterruptedException, MojoFailureException {
        this.log.info("Checking library " + version.getVersion() + " ...");

        final File targetLibraryFile = new File(this.devTools, "mcp-" + version.getVersion() + ".jar");
        final File temporaryLibraryFile = new File(this.devTools, "tmp-minecraftserver.jar");
        if (targetLibraryFile.exists()) {
            this.log.info("Finished checking library " + version.getVersion() + ". It does already exist.");
            return;
        }

        this.log.info("Generating library " + version.getVersion() + " via ForgeGradle...");

        this.gradleService.generateBuildGradleFor(version);
        this.gradleService.executeCommand("setupDecompWorkspace");

        final File minecraftServerFile = new File(this.userHomer, version.getGradleInstallPath());
        FileUtils.copyFile(minecraftServerFile, temporaryLibraryFile);

        final JarRelocator jarRelocator = JarRelocator.from(temporaryLibraryFile, targetLibraryFile, this.log);
        final Map<String, String> pattern = new HashMap<>();
        pattern.put("net.minecraft", "net.minecraft.server." + version.getPackageVersion());
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
     * @param versions version
     * @param jarFile  jarFile
     * @throws IOException          exception
     * @throws InterruptedException exception
     * @throws MojoFailureException exception
     * @throws ZipException         exception
     */
    public void obfuscateJarFile(File jarFile, Version... versions) throws IOException, InterruptedException, MojoFailureException, ZipException {
        if (versions.length == 0) {
            throw new MojoFailureException("One version has to be atleast defined!");
        }

        this.log.info("Obfuscating jar " + jarFile.getName() + " ...");

        final File tobeObfuscated = new File(this.devTools, TO_BE_OBFUSCATED_JAR);
        File finalObfuscatedJar;
        FileUtils.copyFile(jarFile, tobeObfuscated);

        for (final Version version : versions) {

            this.log.info("Relocating " + version.getVersion() + " ...");
            final Map<String, String> pattern = new HashMap<>();
            pattern.put("net.minecraft.server." + version.getPackageVersion(), "net.minecraft");

            try {
                final JarRelocator jarRelocator = JarRelocator.from(tobeObfuscated, this.log);
                jarRelocator.relocate(pattern);
                this.log.info("Successfully relocated " + version.getVersion() + ".");
            } catch (final Exception e) {
                this.log.info("Relocating and obfuscating skipped.");
                return;
            }

            this.log.info("Obfuscating " + version.getVersion() + " via ForgeGradle...");
            this.gradleService.generateBuildGradleFor(version);
            this.gradleService.executeCommand("build");
            this.log.info("Finished ForgeGradle process.");

            finalObfuscatedJar = new File(this.devTools, "build/libs/" + this.devTools.getName() + ".jar");
            if (!finalObfuscatedJar.exists()) {
                throw new MojoFailureException("Obfuscated jar was not generated!");
            }

            FileUtils.copyFile(finalObfuscatedJar, tobeObfuscated);
            FileUtils.deleteQuietly(finalObfuscatedJar);
        }

        this.log.info("Replacing original jar file...");
        FileUtils.copyFile(tobeObfuscated, jarFile);

        this.log.info("File " + jarFile.getName() + " was replaced.");
        this.log.info("Finished obfuscating jar " + jarFile.getName() + ".");
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
