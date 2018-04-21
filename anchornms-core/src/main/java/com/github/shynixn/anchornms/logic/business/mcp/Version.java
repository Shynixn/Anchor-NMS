package com.github.shynixn.anchornms.logic.business.mcp;

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
public enum Version {
    MCP_VERSION_v1_10_R1("1.10", "stable_29", "v1_10_mcpR1", "2.2-SNAPSHOT"),
    MCP_VERSION_v1_11_R1("1.11", "stable_32", "v1_11_mcpR1", "2.2-SNAPSHOT"),
    MCP_VERSION_v1_12_R1("1.12", "snapshot_20180323", "v1_12_mcpR1", "2.3-SNAPSHOT");

    private final String version;
    private final String packageVersion;
    private final String snapshotVersion;
    private final String forgeGradleVersion;
    private final String gradleInstallPath;

    /**
     * Initializes a new version to be supported by this plugin.
     *
     * @param version         version
     * @param snapshotVersion snapshotVersion
     * @param packageVersion  packetVersion
     */
    Version(String version, String snapshotVersion, String packageVersion, String forgeGradleVersion) {
        this.version = version;
        this.packageVersion = packageVersion;
        this.snapshotVersion = snapshotVersion;
        this.forgeGradleVersion = forgeGradleVersion;

        if (this.snapshotVersion.startsWith("snapshot")) {
            this.gradleInstallPath = System.getProperty("user.home") + "/gradle/caches/minecraft/net/minecraft/minecraft_server/" + version + "/snapshot/" + snapshotVersion.split(java.util.regex.Pattern.quote("_"))[1] + "/minecraft_serverSrc-" + version + ".jar";
        } else {
            this.gradleInstallPath = System.getProperty("user.home") + "/gradle/caches/minecraft/net/minecraft/minecraft_server/" + version + "/stable/" + snapshotVersion.split(java.util.regex.Pattern.quote("_"))[1] + "/minecraft_serverSrc-" + version + ".jar";
        }
    }

    /**
     * Returns the required forge Gradle version.
     *
     * @return version
     */
    public String getForgeGradleVersion() {
        return this.forgeGradleVersion;
    }

    /**
     * Returns the version.
     *
     * @return version
     */
    public String getVersion() {
        return this.version;
    }

    /**
     * Returns the snapshot version.
     *
     * @return snapshot
     */
    public String getSnapshotVersion() {
        return this.snapshotVersion;
    }

    /**
     * Returns the gradle install path.
     *
     * @return path
     */
    public String getGradleInstallPath() {
        return this.gradleInstallPath;
    }

    /**
     * Returns the package version.
     *
     * @return version
     */
    public String getPackageVersion() {
        return this.packageVersion;
    }

    /**
     * Returns the version from the text.
     *
     * @param versionText versiontext
     * @return text
     */
    public static Version getVersionFromText(String versionText) {
        for (final Version version : Version.values()) {
            if (version.getVersion().equals(versionText)) {
                return version;
            }
        }
        return null;
    }
}
