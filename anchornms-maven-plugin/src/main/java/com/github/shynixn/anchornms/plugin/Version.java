package com.github.shynixn.anchornms.plugin;

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
    SPONGE_NMS_v1_11("1.11", "snapshot_20170120", "v1_11_mcpR1", "gradle\\caches\\minecraft\\net\\minecraft\\minecraft_server\\1.11\\snapshot\\20170120\\minecraft_serverSrc-1.11.jar");

    private final String version;
    private final String packageVersion;
    private final String snapshotVersion;
    private final String gradleInstallPath;

    /**
     * Initializes a new version to be supported by this plugin.
     *
     * @param version           version
     * @param snapshotVersion   snapshotVersion
     * @param packageVersion    packetVersion
     * @param gradleInstallPath gradleInstallPath
     */
    Version(String version, String snapshotVersion, String packageVersion, String gradleInstallPath) {
        this.version = version;
        this.packageVersion = packageVersion;
        this.snapshotVersion = snapshotVersion;
        this.gradleInstallPath = gradleInstallPath;
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
        for (Version version : Version.values()) {
            if (version.getVersion().equals(versionText)) {
                return version;
            }
        }
        return null;
    }
}
