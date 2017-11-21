# What does it do?

Executes a bash script from Maven.

# Why?

It's an alternative to the AntRun Maven plugin. AntRun requires to describe the tasks in an XML format. Bash-maven-plugin makes it easier to debug.

# How?

See src/test/resources/pom.xml:

    :::xml
        <build>
            <plugins>
                <plugin>
                    <!-- Run with:
                        mvn bash:run
                        mvn install
                    -->
                    <groupId>com.atlassian.maven.plugins</groupId>
                    <artifactId>bash-maven-plugin</artifactId>
                    <version>1.0-SNAPSHOT</version>
                    <executions>
                        <execution>
                            <id>test</id>
                            <phase>integration-test</phase>
                            <goals>
                                <goal>run</goal>
                            </goals>
                        </execution>
                    </executions>
                    <configuration>
                        <script>
                            COOK="Ducasse"
                            echo "The best cook is: $COOK"
                            echo "${example.one}"
                            exit 1
                        </script>
                    </configuration>
                </plugin>
            </plugins>
        </build>
        
        <properties>
            <example.one>Variable replacement is available from Maven.</example.one>
        </properties>
        <dependencies>
            <dependency>
                <groupId>com.atlassian.maven.plugins</groupId>
                <artifactId>bash-maven-plugin</artifactId>
                <version>1.0-SNAPSHOT</version>
                <scope>provided</scope>
            </dependency>
        </dependencies>

# How to use

    :::bash
        git clone git@bitbucket.org:atlassian/bash-maven-plugin.git
        cd bash-maven-plugin

        # Compile and install the plugin in your local repository
        mvn clean install

        # See an example
        cd src/test/resources
        mvn clean install