/**
 * Copyright 2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.sullytestgenerator.sully.output.katalon;

import java.util.List;

import org.sullytestgenerator.sully.command.SullyCommand;
import org.sullytestgenerator.sully.output.SullyTestFormatter;

/**
 * KatalonTestFormatter.
 * 
 * This matches the format used by the Katalon Recorder tool. According to their
 * website that tool is a lightweight web browser extension for record and
 * playback. It is a Selenium IDE-compatible alternative. The tool is not
 * open-source, but it is free to download and use. The tool can be used as an
 * extension with Chrome and Firefox browsers.
 * 
 * From their website
 * (https://www.katalon.com/resources-center/blog/katalon-automation-recorder/):
 * 
 * "Is Katalon Recorder available for commercial use?"
 * 
 * "Katalon Recorder is free to use on any projects (commercial or not) of
 * yours. But it’s prohibited for you to sell/redistribute/decode Katalon
 * Recorder."
 * 
 * One nice feature of the Katalon Recorder tool is that it supports pretty much
 * all of the original Selenium IDE commands (back when Selenium IDE only worked
 * as a plug-in for Firefox). Supported commands include the 'present' commands
 * (e.g., assertTextPresent) which is not available in the latest Selenium IDE
 * tool.
 * 
 * The Selenium IDE file format had one 'test case' per file. The Katalon
 * Recorder tool file format has one 'test suite' per file, with one or more
 * 'test cases' within the 'test suite'.
 * 
 * See: https://www.katalon.com/ and
 * https://www.katalon.com/katalon-recorder-ide/
 * https://addons.mozilla.org/en-US/firefox/addon/katalon-automation-record/
 * 
 * Note that while Sully can generate a test suite file that the Katalon
 * Recorder tool can read and execute, Katalon LLC is a completely separate
 * entity and has no relationship with this Sully open source tool. The author
 * of Sully does not work for or have any involvement with Katalon LLC other
 * than asking questions on their public forums and being a user of their
 * Katalon Recorder tool.
 * 
 * @author JavaJeffG
 *
 */
public class KatalonTestFormatter implements SullyTestFormatter {

   @Override
   public void formatCommand(List<String> output, SullyCommand sullyCommand) {
      String command = sullyCommand.getCommandName();
      String arg1 = sullyCommand.getArg1();
      String arg2 = sullyCommand.getArg2();

      if (arg1 == null) {
         arg1 = "";
      }

      if (arg2 == null) {
         arg2 = "";
      }
      String datalistOption = "<datalist><option>" + arg1 + "</option></datalist>";

      String nextRow = "<td>" + command + "</td><td>" + arg1 + datalistOption + "</td><td>" + arg2 + "</td>";

      output.add("<tr>" + nextRow + CR);
      output.add("</tr>" + CR);
   }

   @Override
   public void formatOpenTestSuite(List<String> output, String suiteName) {
      output.add("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + CR);
      output.add(
            "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">"
                  + CR);
      output.add("<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\" lang=\"en\">" + CR);
      output.add("<head>" + CR);
      output.add("    <meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\">" + CR);
      output.add("    <title>" + suiteName + "</title>" + CR);
      output.add("</head>" + CR);
      output.add("<body>" + CR);
   }

   @Override
   public void formatCloseTestSuite(List<String> output) {
      output.add("</body>" + CR);
      output.add("</html>" + CR);
   }

   @Override
   public void formatOpenTest(List<String> output, String testName) {
      output.add("<table cellpadding=\"1\" cellspacing=\"1\" border=\"1\">" + CR);
      output.add("<thead>" + CR);
      output.add("<tr><td rowspan=\"1\" colspan=\"3\">GEN_" + testName + "</td></tr>" + CR);
      output.add("</thead>" + CR);
      output.add("<tbody>" + CR);
   }

   @Override
   public void formatCloseTest(List<String> output) {
      output.add("</tbody></table>" + CR);
   }

}
