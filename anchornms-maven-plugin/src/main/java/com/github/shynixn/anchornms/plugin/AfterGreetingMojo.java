package com.github.shynixn.anchornms.plugin;

import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.IOException;

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
@Mojo(name = "faterhi", defaultPhase = LifecyclePhase.PACKAGE)
public class AfterGreetingMojo extends AbstractMojo {

    @Parameter(readonly = true, defaultValue = "${project}")
    private MavenProject project;

    private DevSourceSetupService devSourceSetupService;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        File file = project.getArtifact().getFile();

        if (file == null || !file.exists()) {
            throw new MojoFailureException("Artifact jar does not exist!");
        }

        devSourceSetupService = new DevSourceSetupService(new File(project.getBuild().getDirectory()), getLog());

        getLog().info("Obfuscating jar " + file.getName() + " ...");

        try {
            devSourceSetupService.obfuscateJarFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ZipException e) {
            e.printStackTrace();
        }

        getLog().info("Finished obfuscating jar " + file.getName() + ".");
    }
}
