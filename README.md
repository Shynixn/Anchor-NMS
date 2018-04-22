# AnchorNMS[![GitHub license](https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg)](https://raw.githubusercontent.com/Shynixn/BlockBall/master/LICENSE)

| branch        | status        | download      |
| ------------- | --------------| --------------| 
| master        | [![Build Status](https://travis-ci.org/Shynixn/Anchor-NMS.svg?branch=master)](https://travis-ci.org/Shynixn/Anchor-NMS) |[Download latest release (recommend)](https://github.com/Shynixn/Anchor-NMS/releases)|
| development   | [![Build Status](https://travis-ci.org/Shynixn/Anchor-NMS.svg?branch=development)](https://travis-ci.org/Shynixn/Anchor-NMS) | [Download snapshots](https://oss.sonatype.org/content/repositories/snapshots/com/github/shynixn/anchornms/anchornms-maven-plugin) |

JavaDocs: https://shynixn.github.io/Anchor-NMS/apidocs/

## Description

AnchorNMS is a maven plugin which is designed to internally use the mod development framework ForgeGradle for 
mod development with Maven.

Compared to ForgeGradle it offers **easy** integration into any existing Multi Module Java Projects. 
Also, you can include it at any part of your build cycle as described below.

## Features

* Plugin goal to generate mcp libraries for 1.12, 1.11 and 1.10 
* Plugin goal which automatically obfuscates your generated .jar file
* Multi version support in one Module as class paths get relocated and restored

## Useage

The plugin is available in the central maven repository.

### Getting the libraries

1. Insert the following lines into your pom.xml.

```maven
  <build>
        <plugins>
            <plugin>
                <groupId>com.github.shynixn.anchornms</groupId>
                <artifactId>anchornms-maven-plugin</artifactId>
                <version>1.2.0</version>
                <executions>
                    <execution>
                        <goals>
                            <goal>obfuscate-jar</goal>
                        </goals>
                    </execution>
                </executions>
                <configuration>
                    <versions>
                        <version>1.12</version>
                    </versions>
                </configuration>
            </plugin>
        </plugins>
    </build>
```

2. Define your versions you want to generate and use.

3. Execute the following goal to generate your libraries. 

```maven
mvn anchornms:generate-mcp-libraries
```

4. After that, you can find the library files in your target/nms-tools folder called mcp-1.12.jar or mcp-any-version.jar
5. Copy these library files anywhere on your pc or install it into your maven cache and include it into your project.
6. Now you can use the classes for this version.

```java
 public void manipulateArmorstand() {
    net.minecraft.anchor.v1_12_mcpR1.entity.item.EntityArmorStand armorStand;
    armorStand.setSilent(true);
 }
```

### Building the project

Make sure you have the plugin above included into your pom.xml.

The obfuscate goal gets automatically applied and is bound per default to the phase package.

```maven
mvn package
```
Congrats, the jar file is now correctly obfuscated.

### Dependencies and relocating

Often you have to shade dependencies into your final jar file or even relocate them. This is fully supported
as only your jar file in the target folder gets obfuscated by the obfuscation goal.

This means you can simply include the plugin after your shading plugin, so your shaded jar gets obfuscated
in the end.

```maven
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-shade-plugin</artifactId>
            <executions>
                <execution>
                    <phase>package</phase>
                    <goals>
                        <goal>shade</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
        <plugin>
             <groupId>com.github.shynixn.anchornms</groupId>
             <artifactId>anchornms-maven-plugin</artifactId>
             <version>1.2.0</version>
             <executions>
                 <execution>
                     <goals>
                         <goal>obfuscate-jar</goal>
                     </goals>
                 </execution>
             </executions>
             <configuration>
                 <versions>
                     <version>1.12</version>
                 </versions>
             </configuration>
        </plugin>
    </plugins>
</build>
```

### Input jar and Output jar

You can optionally define the input jar file which gets obfuscated and the output jar file.

```maven
<build>
    <plugins>
        <plugin>
             <groupId>com.github.shynixn.anchornms</groupId>
             <artifactId>anchornms-maven-plugin</artifactId>
             <version>1.2.0</version>
             <executions>
                 <execution>
                     <goals>
                         <goal>obfuscate-jar</goal>
                     </goals>
                 </execution>
             </executions>
             <configuration>
                <inputFile>SomePath/MyJar.jar</inputFile>
                <outputFile>SomePath/MyFinalJar.jar</outputFile>
                 <versions>
                     <version>1.12</version>
                 </versions>
             </configuration>
        </plugin>
    </plugins>
</build>
```

## Licence

Copyright 2018 Shynixn

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
