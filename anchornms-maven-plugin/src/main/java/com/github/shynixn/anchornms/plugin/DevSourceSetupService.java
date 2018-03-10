package com.github.shynixn.anchornms.plugin;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;

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
public class DevSourceSetupService {

    private static final String DEV_FOLDER = "nms-tools";
    private static final String TO_BE_OBFUSCATED_JAR = "to-be-obfuscated.jar";
    private static final String GRADLE_LOCAL = "gradle.jar";
    private static final String GRADLE_VERSION = "gradle-4.6";
    private static final String GRADLE_DOWNLOAD = "https://services.gradle.org/distributions/gradle-4.6-bin.zip";

    private final File devTools;
    private final Log log;
    private final File userHomer = new File(System.getProperty("user.home"));

    /**
     * Initializes a new setup service with the targetFolder and logger.
     *
     * @param targetFolder targetFolder
     * @param log          logger
     */
    public DevSourceSetupService(File targetFolder, Log log) {
        super();
        this.log = log;
        this.devTools = new File(targetFolder, DEV_FOLDER);
        if (!this.devTools.exists()) {
            this.devTools.mkdir();
        }
    }

    public void generateMinecraftServerLibraries() throws IOException, ZipException, InterruptedException, MojoFailureException {
        this.log.info("Checking libraries...");
        final File projectFolder = new File(this.devTools.getParentFile().getParentFile(), "lib");
        if (!projectFolder.exists()) {
            projectFolder.mkdir();
        }

        final File targetLibraryFile = new File(projectFolder, "minecraftserver-1.11.jar");
        if (targetLibraryFile.exists()) {
            this.log.info("Finished checking libraries. No new libraries found.");
        }

        this.log.info("Setting up workspace by ForgeGradle v2.2-SNAPSHOT...");
        this.executeGradleCommand(this.devTools, "setupDecompWorkspace");

        final File minecraftServerFile = new File(this.userHomer, "gradle\\caches\\minecraft\\net\\minecraft\\minecraft_server\\1.11\\snapshot\\20170120\\minecraft_serverSrc-1.11.jar");
        FileUtils.copyFile(minecraftServerFile, targetLibraryFile);

        this.cleanUpGradle();

        this.log.info("Copied " + targetLibraryFile.getName() + " to lib folder.");
        this.log.info("Finished checking libraries. New libraries added.");
    }

    public void obfuscateJarFile(File jarFile) throws IOException, InterruptedException, MojoFailureException, ZipException {
        final File tobeObfuscated = new File(this.devTools, TO_BE_OBFUSCATED_JAR);
        FileUtils.copyFile(jarFile, tobeObfuscated);

        this.log.info("Managing contents...");
        this.executeGradleCommand(this.devTools, "build");
        this.log.info("Finished managing contents.");

        final File finalObfuscatedJar = new File(this.devTools, "build/libs/" + this.devTools.getName() + ".jar");
        if (!finalObfuscatedJar.exists()) {
            throw new MojoFailureException("Obfuscated jar was not generated!");
        }

        this.cleanUpGradle();

        this.log.info("Replacing original jar file...");
        FileUtils.copyFile(finalObfuscatedJar, jarFile);
        this.log.info("File " + jarFile.getName() + " was replaced.");
    }

    private void setupProjectGradle() throws IOException, ZipException, MojoFailureException, InterruptedException {
        final File gradleDownloadFile = new File(this.devTools.getParentFile().getParentFile(), GRADLE_LOCAL);
        if (!gradleDownloadFile.exists()) {
            this.log.info("Downloading gradle...");
            FileUtils.copyURLToFile(new URL(GRADLE_DOWNLOAD), gradleDownloadFile);
            this.log.info("Finished downloading gradle.");
        }

        final File buildGradleTargetFile = new File(this.devTools, "build.gradle");
        if (!buildGradleTargetFile.exists()) {
            this.log.info("Installing gradle...");
            final ZipFile zipFile = new ZipFile(gradleDownloadFile);
            zipFile.extractAll(this.devTools.getAbsolutePath());
            this.executeGradleCommand(this.devTools, "wrapper", true);
            final URL buildGradleFile = Thread.currentThread().getContextClassLoader().getResource("build.gradle.txt");
            FileUtils.copyURLToFile(buildGradleFile, buildGradleTargetFile);
            this.log.info("Finished installing gradle.");
        }
    }

    private void executeGradleCommand(File commandFolder, String command) throws IOException, InterruptedException, ZipException, MojoFailureException {
        this.executeGradleCommand(commandFolder, command, false);
    }

    private void cleanUpGradle() throws InterruptedException, ZipException, MojoFailureException, IOException {
        this.executeGradleCommand(this.devTools, "--stop");
    }

    private void executeGradleCommand(File commandFolder, String command, boolean recursive) throws IOException, InterruptedException, ZipException, MojoFailureException {
        final String winCommand = '"' + this.devTools.getAbsolutePath() + '\\' + GRADLE_VERSION + "\\bin\\gradle.bat" + '"';
        final String otherCommand = '"' + this.devTools.getAbsolutePath() + '\\' + GRADLE_VERSION + "\\bin\\gradle" + '"';

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
                    throw new MojoFailureException("Failed to execute gradle command.", e);
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
}
