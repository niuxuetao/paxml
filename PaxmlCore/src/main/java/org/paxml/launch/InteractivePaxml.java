/**
 * This file is part of PaxmlCore.
 *
 * PaxmlCore is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PaxmlCore is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with PaxmlCore.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.paxml.launch;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.paxml.core.Context;
import org.paxml.core.IEntity;
import org.paxml.core.InMemoryResource;
import org.paxml.core.PaxmlRuntimeException;
import org.paxml.tag.plan.ScenarioTag;

public class InteractivePaxml {
    private static final Log log = LogFactory.getLog(InteractivePaxml.class);

    public static final String ARG_PLANFILE = "paxml.planfile";

    public static final String CMD_HELP = "help";
    public static final String CMD_RESET = "reset";
    public static final String CMD_QUIT = "quit";

    public static interface IStreamFactory {
        InputStream getInputStream();

        OutputStream getOutputStream(boolean error);
    }

    public static class SystemStreamFactory implements IStreamFactory {

        @Override
        public InputStream getInputStream() {
            return System.in;
        }

        @Override
        public OutputStream getOutputStream(boolean error) {
            return error ? System.err : System.out;
        }

    }

    public static void main(String[] args) throws Exception {

        InteractivePaxml ipaxml = new InteractivePaxml(new SystemStreamFactory());

        ipaxml.run(System.getProperty(ARG_PLANFILE));
    }

    private final IStreamFactory streams;
    private final Paxml paxml = new Paxml(0);
    private final Properties properties = new Properties();
    private boolean entryCalled = false;

    public InteractivePaxml(IStreamFactory streamFactory) {
        streams = streamFactory;
        properties.putAll(System.getProperties());
    }

    public Paxml getPaxml() {
        return paxml;
    }
    private Context makeNewContext(){
        return new Context(new Context(properties, paxml.getProcessId()));
    }
    public void run(String planFile) {

        printHelp();

        println("paxml interactive mode started. Please type paxml tags to execute:");

        BufferedReader reader = new BufferedReader(new InputStreamReader(streams.getInputStream()));
        String line;
        StringBuilder sb = null;

        if (StringUtils.isNotBlank(planFile)) {
            if(log.isInfoEnabled()){
                log.info("Running with planfile: "+planFile);
            }
            try {
                LaunchModel model = paxml.executePlanFile(planFile, System.getProperties());
                paxml.addStaticConfig(model.getConfig());
                properties.putAll(model.getGlobalSettings().getProperties());
            } catch (Exception e) {

                e.printStackTrace(new PrintStream(streams.getOutputStream(true)));

            }
        } else {
            if(log.isInfoEnabled()){
                log.info("Running without plan file");
            }
        }

        Context context = makeNewContext();

        while ((line = readLine(reader)) != null) {
            if (line.equals(CMD_HELP)) {
                printHelp();
            } else if (line.equals("reset")) {
                paxml.callEntityExitListener(context);
                context = makeNewContext();
                sb = null;
                println("Context reset");
            } else if (line.equals(CMD_QUIT)) {
                println("Goodbye");
                break;
            } else if (line.endsWith("\\")) {
                sb = new StringBuilder();
                sb.append(line.substring(0, line.length() - 1)).append("\r\n");
            } else if (sb == null) {
                // execute the line
                execute(line, context);
            } else {
                // execute multiple lines
                execute(sb.toString(), context);
                sb = null;
            }
        }
        
    }

    public void execute(String paxml, Context context) {
        paxml = "<" + ScenarioTag.TAG_NAME + ">" + paxml + "</" + ScenarioTag.TAG_NAME + ">";

        try {
            IEntity entity = this.paxml.getParser().parse(new InMemoryResource(paxml), true, null);

            this.paxml.execute(entity, context, !entryCalled, false);

            entryCalled = true;

        } catch (Throwable e) {
            if (log.isErrorEnabled()) {
                log.error(e.getMessage(), e);
            }
            println(Paxml.getCause(e));

        }

    }

    private String readLine(BufferedReader reader) {
        try {
            return reader.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void printHelp() {
        println("");
        println("************************************************************************");
        System.out
                .println("Execute paxml tags as you type. You can add/edit other paxml files before you call them. The change will be automatically picked up.");
        println("A few commands to assist typing the paxml tags:");
        println("    a back slash \\ in the end of a line combines its next line to execute together.");
        println("    " + CMD_RESET + " : start a new context all over, clearing all defined data");
        println("    " + CMD_QUIT + " : quit this program");
        println("    " + CMD_HELP + " : show this help");
        println("");
        println("System properties as launch arguments:");
        println("    "
                + ARG_PLANFILE
                + " : spring resource path pointing to a planfile that specifies the tag libraries, resources, listeners, and global properties. Leaving this argument will use the bare minual settings.");

        println("************************************************************************");
        println("");
    }

    private void println(String line) {
        OutputStream out = streams.getOutputStream(false);
        try {
            out.write(line.getBytes("UTF-8"));
            out.write("\r\n".getBytes("UTF-8"));
            out.flush();
        } catch (IOException e) {
            throw new PaxmlRuntimeException(e.getMessage());
        }
    }
}
