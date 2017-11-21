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

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.junit.matchers.JUnitMatchers.containsString;


public class RunMojoTest
{

    private RunMojo run = new RunMojo();
    private MockLogger log = new MockLogger();

    @Before
    public void setUp()
    {
        run.setLog(log);
    }

    @Test
    public void testOutputRedirected() throws MojoExecutionException, MojoFailureException
    {
        run.script = "echo AAA";
        run.execute();

        assertThat(log.info(), containsString("Execution: AAA"));
    }

    @Test
    public void testErrorStreamRedirected() throws MojoExecutionException, MojoFailureException
    {
        run.script = "echo AAA > &2";
        try
        {
            run.execute();
        }
        catch (MojoFailureException mfe)
        {
            assertThat(mfe.getMessage(), equalTo("The execution exited with code 2"));
            assertThat(log.info(), containsString("AAA"));
            assertThat(log.error(), containsString("Execution Exit Code: 2"));
            return;
        }
        fail("An exception was expected");
    }

    @Test
    public void testSkip() throws MojoExecutionException, MojoFailureException
    {
        run.script = "echo AAA";
        run.skip = true;
        run.execute();

        assertThat(log.info(), containsString("Skipping"));
        assertThat(log.info(), not(containsString("AAA")));
    }

    private static final class MockLogger implements Log
    {

        StringBuilder debug = new StringBuilder();
        StringBuilder info = new StringBuilder();
        StringBuilder warn = new StringBuilder();
        StringBuilder error = new StringBuilder();

        @Override
        public boolean isDebugEnabled()
        {
            return false;
        }

        @Override
        public boolean isInfoEnabled()
        {
            return true;
        }

        @Override
        public boolean isWarnEnabled()
        {
            return true;
        }

        @Override
        public boolean isErrorEnabled()
        {
            return true;
        }

        @Override
        public void debug(CharSequence content)
        {
            debug.append(content);
        }

        @Override
        public void debug(CharSequence content, Throwable error)
        {
            debug.append(content);
            error.printStackTrace();
        }

        @Override
        public void debug(Throwable error)
        {
            error.printStackTrace();
        }

        @Override
        public void info(CharSequence content)
        {
            info.append(content);
        }

        @Override
        public void info(CharSequence content, Throwable throwable)
        {
            info.append(content);
            throwable.printStackTrace();
        }

        @Override
        public void info(Throwable throwable)
        {
            throwable.printStackTrace();
        }

        @Override
        public void warn(CharSequence content)
        {
            warn.append(content);
        }

        @Override
        public void warn(CharSequence content, Throwable throwable)
        {
            warn.append(content);
            throwable.printStackTrace();
        }

        @Override
        public void warn(Throwable throwable)
        {
            throwable.printStackTrace();
        }

        @Override
        public void error(CharSequence content)
        {
            error.append(content);
        }

        @Override
        public void error(CharSequence content, Throwable throwable)
        {
            error.append(content);
            throwable.printStackTrace();
        }

        @Override
        public void error(Throwable throwable)
        {
            throwable.printStackTrace();
        }

        public String debug()
        {
            return debug.toString();
        }

        public String info()
        {
            return info.toString();
        }

        public String warn()
        {
            return warn.toString();
        }

        public String error()
        {
            return error.toString();
        }
    }
}
