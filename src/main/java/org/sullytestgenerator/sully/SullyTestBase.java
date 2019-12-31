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
import java.util.ArrayList;
import java.util.List;

import org.sullytestgenerator.sully.katalon.KatalonTestFormatter;

/**
 * SullyTestBase
 * 
 * @author JavaJeffG
 *
 */
public class SullyTestBase implements SullyTestFormatter {

   private static final String OPEN_TEST_SUITE = "openTestSuite";

   private static final String CLOSE_TEST_SUITE = "closeTestSuite";

   private static final String OPEN_TEST = "openTest";

   private static final String CLOSE_TEST = "closeTest";

   private List<SullyCommand> commands = new ArrayList<>();

   // Sleep (in msec) when running 'highlight' commands.
   private int highlightPauseMsec = 200;

   // By default use the KatalonTestFormatter.
   private SullyTestFormatter sullyTestFormatter = new KatalonTestFormatter();

   public int getHighlightPauseMsec() {
      return highlightPauseMsec;
   }

   public void setHighlightPauseMsec(int highlightPauseMsec) {
      this.highlightPauseMsec = highlightPauseMsec;
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

   private List<String> formatAllCommands() {
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
      }

      return result;
   }

}
