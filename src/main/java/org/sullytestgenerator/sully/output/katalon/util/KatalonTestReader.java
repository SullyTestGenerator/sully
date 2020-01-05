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

public class KatalonTestReader {

   private static final String BASE_FILE_DIRECTORY = "C:\\Users\\Jane\\AppData\\Local\\Temp\\";

   private static final String testFileName = BASE_FILE_DIRECTORY + "GoogleSullyTest_GEN3908954664261477856.html";

   private static final boolean DEBUG = false;

   private static final String COMMENT_DASHED = "commentDashed();";
   private static final String COMMAND_HIGHLIGHT = "command_highlight(\"";
   private static final String COMMAND_PAUSE = "command_pause(\"";

   class KatalonTestCase {
      public String name = "";

      public List<String> commands = new ArrayList<>();
   }

   public void parseFile() {
      List<KatalonTestCase> testCases = new ArrayList<>();

      System.out.println("Parsing file: '" + testFileName + "'...");

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

      System.out.println("------------------------------------------------------------------");
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

      if (command.equals("echo") && SullyTestBase.COMMENT_DASHED_LINE.equals(arg1)) {
         nextCommand = "commentDashed();";
      }
      else if (command.equals("echo") && arg1.startsWith(SullyTestBase.COMMENT_PREFIX)
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
         nextCommand = "verySortPause();";
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

            boolean isCommentBlock = checkCommentBlock(nextCommand, commands, counter, remainingCommands);

            if (isCommentBlock) {
               nextCommand = commands.get(counter + 2);
               nextCommand = nextCommand.replace("comment(\"", "commentBlock(\"");
               counter = counter + 4;
            }

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

   public static void main(String[] args) {

      (new KatalonTestReader()).parseFile();
   }

}
