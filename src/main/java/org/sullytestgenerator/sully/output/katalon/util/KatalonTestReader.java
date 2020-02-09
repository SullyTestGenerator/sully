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
package org.sullytestgenerator.sully.output.katalon.util;

import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.sullytestgenerator.sully.SullyTestBase;

/**
 * KatalonTestReader can be used to read in a Katalon test suite file to list
 * the corresponding 'Sully' commands. A Katalon test suite file is in HTML
 * format where the test cases are represented as HTML table elements. 'Sully'
 * commands are just Java method calls.
 * 
 * A common create-then-modify test process is:
 * 
 * 1. Create a simple 'Sully' test by extending the SullyTestBase class.
 * 
 * 2. Run the SullyTestBase outputToFile() method to output a Katalon test suite
 * file.
 * 
 * 3. Read the Katalon test suite file into Katalon Recorder tool, see:
 * https://www.katalon.com/katalon-recorder-ide
 * 
 * 4. Use Katalon Recorder tool to run/modify/extend the test steps.
 * 
 * 5. Save the modified Katalon test suite.
 * 
 * 6. Use KatalonTestReader to read in the modified test suite and list the
 * 'Sully' commands.
 * 
 * 7. Copy any modified/new 'Sully' commands into original 'Sully' test.
 * 
 * See the main() method below for how to pass in the two required arguments.
 * 
 * 
 * @author JavaJeffG
 *
 */
public class KatalonTestReader {

   private static final boolean DEBUG = false;

   private static final String COMMENT_DASHED = "commentDashed();";
   private static final String COMMAND_HIGHLIGHT = "command_highlight(\"";
   private static final String COMMAND_PAUSE = "command_pause(\"";

   class KatalonTestCase {
      public String name = "";

      public List<String> commands = new ArrayList<>();
   }

   public void parseFile(String testFileName) {
      List<KatalonTestCase> testCases = new ArrayList<>();

      System.out.println("Parsing file: '" + testFileName + "'...\n");

      try (RandomAccessFile raf = new RandomAccessFile(testFileName, "r");) {
         String nextLine = raf.readLine();

         KatalonTestCase nextTestCase = null;

         while (nextLine != null) {
            if (nextLine.contains("<td rowspan=\"1\"")) {
               nextTestCase = new KatalonTestCase();
               testCases.add(nextTestCase);

               String name = nextLine.substring(nextLine.indexOf("\"3\"") + 4);
               name = name.substring(0, name.indexOf("<"));
               nextTestCase.name = name;
            }

            if (nextLine.startsWith("<tr><td>")) {
               if (DEBUG) {
                  System.out.println(nextLine);
               }

               String command = parseCommand(nextLine, "<tr><td>(.*?)</td><td>.*");
               String arg1 = parseCommand(nextLine, ".*</td><td>(.*?)<datalist>.*");
               String arg2 = parseCommand(nextLine, ".*<td>(.*?)</td>");

               String arg2Display = "";

               if (arg2.length() >= 1) {
                  arg2Display = ", \"" + arg2 + "\"";
               }
               String nextCommand = determineCommand(command, arg1, arg2Display);
               nextTestCase.commands.add(nextCommand);
            }

            nextLine = raf.readLine();
         }
      }
      catch (Exception e) {
         e.printStackTrace();
      }

      filterMultiLineCommands(testCases);

      outputTestCases(testCases);
      System.out.println("------------------------------------------------------------------");
      System.out.println("...done.\n");
   }

   protected String parseCommand(String nextLine, String patternString) {
      Pattern pattern = Pattern.compile(patternString);
      Matcher matcher = pattern.matcher(nextLine);
      matcher.matches();

      String result = matcher.group(1);
      return result;
   }

   protected void outputTestCases(List<KatalonTestCase> testCases) {
      for (KatalonTestCase nextTestCase : testCases) {
         System.out.println("");
         System.out.println("-----------------------------");
         System.out.println(nextTestCase.name);
         System.out.println("");

         for (String nextCommand : nextTestCase.commands) {
            System.out.println("    " + nextCommand);
         }
         System.out.println("");
      }
   }

   protected String determineCommand(String command, String arg1, String arg2Display) {
      String nextCommand = "command_" + command + "(\"" + arg1 + "\"" + arg2Display + ");";

      if (command.equals("#") && SullyTestBase.COMMENT_DASHED_LINE.equals(arg1)) {
         nextCommand = "commentDashed();";
      }
      else if (command.equals("#") && arg1.startsWith(SullyTestBase.COMMENT_PREFIX)
            && arg1.endsWith(SullyTestBase.COMMENT_POSTFIX)) {

         String trimCommand = arg1.substring(SullyTestBase.COMMENT_PREFIX.length(),
               arg1.length() - SullyTestBase.COMMENT_POSTFIX.length());

         nextCommand = "comment(\"" + trimCommand + "\");";
      }
      else if (nextCommand.equals("command_pause(\"5000\");")) {
         nextCommand = "longPause();";
      }
      else if (nextCommand.equals("command_pause(\"1000\");")) {
         nextCommand = "shortPause();";
      }
      else if (nextCommand.equals("command_pause(\"250\");")) {
         nextCommand = "veryShortPause();";
      }
      else if (nextCommand.equals("command_pause(\"10\");")) {
         nextCommand = "tinyPause();";
      }

      return nextCommand;
   }

   protected void filterMultiLineCommands(List<KatalonTestCase> testCases) {
      for (KatalonTestCase nextTestCase : testCases) {
         List<String> commands = nextTestCase.commands;
         List<String> finalCommandList = new ArrayList<String>();

         int counter = 0;

         while (counter < commands.size()) {
            String nextCommand = commands.get(counter);
            int remainingCommands = commands.size() - counter;

            // Check for Block Comment.
            boolean isCommentBlock = checkCommentBlock(nextCommand, commands, counter, remainingCommands);

            if (isCommentBlock) {
               nextCommand = commands.get(counter + 2);
               nextCommand = nextCommand.replace("comment(\"", "commentBlock(\"");
               counter = counter + 4;
            }

            // Check for Highlight twice compound command.
            boolean isHighlightTwice = checkHighLightTwice(nextCommand, commands, counter, remainingCommands);

            if (isHighlightTwice) {
               nextCommand = commands.get(counter + 4);
               int underscore = nextCommand.indexOf("_");
               String finalCommand = "highlightTwiceAnd";
               finalCommand += nextCommand.substring(underscore + 1, underscore + 2).toUpperCase();
               finalCommand += nextCommand.substring(underscore + 2);

               nextCommand = finalCommand;
               counter = counter + 4;
            }

            // Check for single Highlight compound command.
            boolean isHighlight = checkHighLight(nextCommand, commands, counter, remainingCommands);

            if (isHighlight) {
               nextCommand = commands.get(counter + 2);
               int underscore = nextCommand.indexOf("_");
               String finalCommand = "highlightAnd";
               finalCommand += nextCommand.substring(underscore + 1, underscore + 2).toUpperCase();
               finalCommand += nextCommand.substring(underscore + 2);

               nextCommand = finalCommand;
               counter = counter + 2;
            }

            finalCommandList.add(nextCommand);

            counter++;
         }
         nextTestCase.commands = finalCommandList;
      }
   }

   protected boolean checkCommentBlock(String nextCommand, List<String> commands, int counter, int remainingCommands) {
      boolean result = false;

      if (nextCommand.equals(COMMENT_DASHED) && remainingCommands >= 4) {
         if (commands.get(counter + 1).equals(COMMENT_DASHED) && commands.get(counter + 3).equals(COMMENT_DASHED)
               && commands.get(counter + 4).equals(COMMENT_DASHED)) {

            if (commands.get(counter + 2).startsWith("comment(")) {
               result = true;
            }
         }
      }

      return result;
   }

   protected boolean checkHighLightTwice(String nextCommand, List<String> commands, int counter, int remainingCommands) {
      boolean result = false;

      if (nextCommand.startsWith(COMMAND_HIGHLIGHT) && remainingCommands >= 4) {
         if (commands.get(counter + 1).startsWith(COMMAND_PAUSE) && commands.get(counter + 2).startsWith(COMMAND_HIGHLIGHT)
               && commands.get(counter + 3).startsWith(COMMAND_PAUSE)) {

            result = isSpecialHighlightCommand(commands.get(counter + 4));
         }
      }

      return result;
   }

   protected boolean checkHighLight(String nextCommand, List<String> commands, int counter, int remainingCommands) {
      boolean result = false;

      if (nextCommand.startsWith(COMMAND_HIGHLIGHT) && remainingCommands >= 2) {
         if (commands.get(counter + 1).startsWith(COMMAND_PAUSE)) {

            result = isSpecialHighlightCommand(commands.get(counter + 2));
         }
      }

      return result;
   }

   protected boolean isSpecialHighlightCommand(String command) {
      boolean result = false;

      if (command.startsWith("command_assertText") || command.startsWith("command_click") || command.startsWith("command_select")
            || command.startsWith("command_type")) {
         result = true;
      }

      return result;
   }

   /**
    * KatalonTestReader main() needs two values passed in:
    * 
    * 1. The directory path of the file to be read.
    * 
    * 2. The simple filename of the file to be read.
    * 
    * Any additional arguments are ignored.
    * 
    * With Eclipse, these arguments may be separate lines, so the
    * output from generating a test file can be used by this
    * class to read the test file.
    * 
    * For example, this output could be in the Arguments > Program arguments
    * window when executing KatalonTestReader using Eclipse:
    * 
    *  C:\Users\Stephen\AppData\Local\Temp
    *  GoogleSullyTest_GEN8367005133433190223.html
    *  -----------------------------------
    * 
    * @param args
    */
   public static void main(String[] args) {
      if (args.length <= 1) {
         System.err.println("KatalonTestReader main() needs two values passed in:");
         System.err.println("");
         System.err.println(" 1. The directory path of the file to be read.");
         System.err.println(" 2. The simple filename of the file to be read.");
         System.err.println("");
         System.err.println("The directory may end with a file separator character,");
         System.err.println("but it is not necessary.");
         System.err.println("");
         System.err.println("The output generated by running the SullyTestBase");
         System.err.println("outputToFile() can just be passed as arguments to");
         System.err.println("KatalonTestReader main().");
         System.err.println("");
         System.err.println("Any additional arguments passed in are ignored.");
         System.err.println("------------------------------------------------------");
      }
      else {
         String path = args[0].trim();

         if (!path.endsWith(SullyTestBase.FILE_SEPARATOR) && !path.endsWith("\\") && !path.endsWith("/")) {

            path += SullyTestBase.FILE_SEPARATOR;
         }
         String filename = args[1].trim();
         String testFileName = path + filename;

         (new KatalonTestReader()).parseFile(testFileName);
      }
   }

}
