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
package com.teamtter.maven.plugins.bash;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * Runs a bash script.
 * 
 * @author Adrien Ragot
 * @author Francois Marot
 */
@Mojo(name = "run", aggregator = true, threadSafe = true)
public class RunMojo extends AbstractMojo {

	@Parameter(required = false)
	boolean skip;

	@Parameter(required = false)
	BashScript bashScript;

	@Parameter(required = false)
	BatScript batScript;

	/** use bat when both types are available (typically msysGit/GitBash...) */
	@Parameter(required = false, defaultValue = "false")
	Boolean preferredTypeIsBat;

	public void execute() throws MojoExecutionException, MojoFailureException {
		if (skip) {
			getLog().info("Skipping bash-maven-plugin:run");
			return;
		}

		if (batScript != null && bashScript == null) {
			runScript(batScript);
		} else if (batScript != null && preferredTypeIsBat) {
			runScript(batScript);
		} else if (batScript != null && bashScript != null && ! preferredTypeIsBat) {
			runScript(bashScript);
		} else { // batScript is null
			if (bashScript != null) {
				runScript(bashScript);
			} else {
				getLog().error("Both bash and bat are undefined... Can not execute any script !");
			}
		}
	}
	
	private void runScript(BashScript bashScript2) {
		
	}

	private void runScript(BatScript batScript2) {
		// TODO : convert line endings just to make sure (pom may have been encooding with Linux LineBreaks
	}

	void toto() {
		String script = "";
		boolean debugMode = getLog().isDebugEnabled();

		getLog().info("Executing bash script" + (debugMode ? " in debug mode" : ""));

		PrintWriter writer = null;
		try {
			// Output to a file
			File file = File.createTempFile("bash-maven-plugin", ".sh.tmp");
			writer = new PrintWriter(new FileWriter(file));
			writer.print(script);
			writer.close();

			
			try (StringReader scriptContent = new StringReader(script)) {
				IOUtils.readLines(scriptContent).stream()
					.forEach(line -> getLog().info("File contents: " + line));
			}
			

			// Now, execute
			List<String> arguments = new ArrayList<String>();
			arguments.add("bash");
			if (debugMode)
				arguments.add("-x");
			arguments.add(file.getAbsolutePath());

			ProcessBuilder ps = new ProcessBuilder(arguments);
			ps.redirectErrorStream(true);
			Process pr = ps.start();
			BufferedReader in = new BufferedReader(new InputStreamReader(pr.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				getLog().info("Execution: " + line);
			}
			pr.waitFor();

			if (pr.exitValue() != 0) {
				getLog().error("Execution Exit Code: " + pr.exitValue());
				throw new MojoFailureException("The execution exited with code " + pr.exitValue());
			}
		} catch (IOException e) {
			if (writer != null) {
				writer.close();
			}
			throw new MojoExecutionException("Can't run", e);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new MojoExecutionException("Can't run");
		}
	}
}