/*
 * Copyright 2013 Atlassian PTY LTD
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.atlassian.maven.plugins.bash;

import org.apache.commons.lang.StringUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Runs a bash script.
 *
 * @author Adrien Ragot
 */
@Mojo(name = "run")
public class RunMojo extends AbstractMojo
{

    @Parameter
    String script;

    @Parameter
    boolean skip;

    public void execute() throws MojoExecutionException, MojoFailureException
    {
        if (skip) {
            getLog().info("Skipping bash-maven-plugin:run");
            return;
        }
        if (StringUtils.isBlank(script))
        {
            getLog().error("No script provided");
            throw new MojoFailureException("No script provided");
        }

//        if (System.getProperty("os.name").toUpperCase(Locale.ENGLISH).startsWith("WINDOWS")) {
//            getLog().error("The system property os.name is " + System.getProperty("os.name"));
//            getLog().error("Windows is not a compatible platform for bash-maven-plugin.");
//            throw new MojoFailureException("Can't execute bash-maven-plugin on Windows (yet).");
//        }

        boolean debugMode = getLog().isDebugEnabled();

        getLog().info("Executing bash script" + (debugMode ? " in debug mode" : ""));

        PrintWriter writer = null;
        try
        {
            // Output to a file
            File file = File.createTempFile("bash-maven-plugin", ".sh.tmp");
            writer = new PrintWriter(new FileWriter(file));
            writer.print(script);
            writer.close();

            // Display the file
            if (getLog().isInfoEnabled())
            {
                BufferedReader in = new BufferedReader(new FileReader(file));
                String text;
                while ((text = in.readLine()) != null)
                {
                    getLog().info("File contents: " + text);
                }
                in.close();
                getLog().info("Execution - Debug is on");
            }

            // Now, execute
            List<String> arguments = new ArrayList<String>();
            arguments.add("bash");
            if (debugMode) arguments.add("-x");
            arguments.add(file.getAbsolutePath());

            ProcessBuilder ps = new ProcessBuilder(arguments);
            ps.redirectErrorStream(true);
            Process pr = ps.start();
            BufferedReader in = new BufferedReader(new InputStreamReader(pr.getInputStream()));
            String line;
            while ((line = in.readLine()) != null)
            {
                getLog().info("Execution: " + line);
            }
            pr.waitFor();

            if (pr.exitValue() != 0)
            {
                getLog().error("Execution Exit Code: " + pr.exitValue());
                throw new MojoFailureException("The execution exited with code " + pr.exitValue());
            }
        }
        catch (IOException e)
        {
            if (writer != null)
            {
                writer.close();
            }
            throw new MojoExecutionException("Can't run", e);
        }
        catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();
            throw new MojoExecutionException("Can't run");
        }
    }
}