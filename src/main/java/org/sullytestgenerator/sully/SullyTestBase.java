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
package org.sullytestgenerator.sully;

import java.io.File;
import java.io.RandomAccessFile;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.sullytestgenerator.sully.command.AllSeleniumCommands;
import org.sullytestgenerator.sully.command.SullyCommand;
import org.sullytestgenerator.sully.output.SullyTestFormatter;
import org.sullytestgenerator.sully.output.katalon.KatalonTestFormatter;

/**
 * SullyTestBase is the base class for SullyTestGenerator. Extend this class
 * with the tests needed by your project.
 * 
 * By default the test formatter is the KatalonTestFormatter. To change this,
 * create another test formatter that implements SullyTestFormatter and then
 * call the SullyTestBase setSullyTestFormatter() method before the call to
 * outputCommands().
 * 
 * For an example, see GoogleSullyTest.java
 * 
 * 
 * @author JavaJeffG
 *
 */
public class SullyTestBase extends AllSeleniumCommands implements SullyTestFormatter {

   private static final String OPEN_TEST_SUITE = "openTestSuite";

   private static final String CLOSE_TEST_SUITE = "closeTestSuite";

   private static final String OPEN_TEST = "openTest";

   private static final String CLOSE_TEST = "closeTest";

   private List<SullyCommand> commands = new ArrayList<>();

   // Sleep (in msec) when running 'highlight' commands.
   private int highlightPauseMsec = 200;

   // The base used for the shortPause(), etc. commands.
   // Adjusting this will speed up/ slow down those commands.
   private int sleepCommandBaseMsec = 1000;

   // By default use the KatalonTestFormatter.
   private SullyTestFormatter sullyTestFormatter = new KatalonTestFormatter();

   public int getHighlightPauseMsec() {
      return highlightPauseMsec;
   }

   public void setHighlightPauseMsec(int highlightPauseMsec) {
      this.highlightPauseMsec = highlightPauseMsec;
   }

   public int getSleepCommandBaseMsec() {
      return sleepCommandBaseMsec;
   }

   public void setSleepCommandBaseMsec(int sleepCommandBaseMsec) {
      this.sleepCommandBaseMsec = sleepCommandBaseMsec;
   }

   public void setSullyTestFormatter(SullyTestFormatter sullyTestFormatter) {
      this.sullyTestFormatter = sullyTestFormatter;
   }

   // -----------------------------------------------------------------------------------
   // Format Sully Commands and Test/Suite 'commands'.

   @Override
   public void formatCommand(List<String> output, SullyCommand sullyCommand) {
      sullyTestFormatter.formatCommand(output, sullyCommand);
   }

   @Override
   public void formatOpenTestSuite(List<String> output, String suiteName) {
      sullyTestFormatter.formatOpenTestSuite(output, suiteName);
   }

   @Override
   public void formatCloseTestSuite(List<String> output) {
      sullyTestFormatter.formatCloseTestSuite(output);
   }

   @Override
   public void formatOpenTest(List<String> output, String testName) {
      sullyTestFormatter.formatOpenTest(output, testName);
   }

   @Override
   public void formatCloseTest(List<String> output) {
      sullyTestFormatter.formatCloseTest(output);
   }

   // -----------------------------------------------------------------------------------
   // TESTS and TESTSUITES

   public void openTestSuite(String suiteName) {
      commands.add(new SullyCommand(OPEN_TEST_SUITE, suiteName, null));
   }

   public void closeTestSuite() {
      commands.add(new SullyCommand(CLOSE_TEST_SUITE, null, null));
   }

   public void openTest(String testName) {
      commands.add(new SullyCommand(OPEN_TEST, testName, null));
   }

   public void closeTest() {
      commands.add(new SullyCommand(CLOSE_TEST, null, null));
   }

   // -----------------------------------------------------------------------------------

   public void outputToFile(String filename) {
      // Convert the commands (including openTestSuite, etc.)
      // into a list of Strings and then write it to a file.
      List<String> lines = formatAllCommands();

      File f = new File(filename);

      if (f.exists()) {
         f.delete();
      }

      try (RandomAccessFile raf = new RandomAccessFile(filename, "rw")) {
         for (String nextLine : lines) {
            raf.writeBytes(nextLine);
         }
      }
      catch (Exception e) {
         e.printStackTrace();
         throw new RuntimeException("Error", e);
      }

      System.out.println("\nDone outputing test file to: " + filename + "\n");
      System.out.println("------------------------------------------------------------------------------\n");
   }

   protected List<String> formatAllCommands() {
      List<String> result = new ArrayList<>();

      for (SullyCommand nextCommand : commands) {
         String commandName = nextCommand.getCommandName();

         if (OPEN_TEST_SUITE.equals(commandName)) {
            formatOpenTestSuite(result, nextCommand.getArg1());
         }
         else if (CLOSE_TEST_SUITE.equals(commandName)) {
            formatCloseTestSuite(result);
         }
         else if (OPEN_TEST.equals(commandName)) {
            formatOpenTest(result, nextCommand.getArg1());
         }
         else if (CLOSE_TEST.equals(commandName)) {
            formatCloseTest(result);
         }
         else {
            formatCommand(result, nextCommand);
         }
      }

      return result;
   }

   @Override
   protected void addCommand(String command, String arg1, String arg2) {
      commands.add(new SullyCommand(command, arg1, arg2));
   }

   // -----------------------------------------------------------------------------------
   // Comment 'commands'.

   public void comment(String comment) {
      command_echo("-- " + comment + " --");
   }

   public void commentBlock(String comment) {
      commentDashed();
      commentDashed();
      comment(comment);
      commentDashed();
      commentDashed();
   }

   public void commentDashed() {
      command_echo("---------------------------------------------");
   }

   // -----------------------------------------------------------------------------------
   // Sleep helper methods.

   public void command_pause(double sleepMsec) {
      DecimalFormat df = new DecimalFormat("###");
      String sleepMsecRounded = df.format(sleepMsec);

      command_pause(sleepMsecRounded);
   }

   public void command_pause(int sleepMsec) {
      command_pause("" + sleepMsec);
   }

   public void longPause() {
      // 5 seconds in mSec.
      command_pause(5.000 * getSleepCommandBaseMsec());
   }

   public void shortPause() {
      // 1 second in mSec.
      command_pause(1.000 * getSleepCommandBaseMsec());
   }

   public void verySortPause() {
      // 0.25 seconds in mSec.
      command_pause(0.250 * getSleepCommandBaseMsec());
   }

   public void tinyPause() {
      // 0.01 seconds in mSec.
      command_pause(0.010 * getSleepCommandBaseMsec());
   }

   // -----------------------------------------------------------------------------------
   // Additional TESTCASE operations.

   // -----------------------------------------------------------------------------------

}
