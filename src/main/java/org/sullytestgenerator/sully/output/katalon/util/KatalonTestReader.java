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

   private static final String BASE_FILE_DIRECTORY = "C:\\";

   private static final String testFileName = BASE_FILE_DIRECTORY + "GoogleSullyTest_GEN3461932608411652767.html";

   private static final boolean DEBUG = false;

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
      System.out.println("------------------------------------------------------------------");
      outputTestCases(testCases);
      System.out.println("------------------------------------------------------------------");
      System.out.println("...done.\n");
   }

   private String parseCommand(String nextLine, String patternString) {
      Pattern pattern = Pattern.compile(patternString);
      Matcher matcher = pattern.matcher(nextLine);
      matcher.matches();

      String result = matcher.group(1);
      return result;
   }

   private void outputTestCases(List<KatalonTestCase> testCases) {
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

   private String determineCommand(String command, String arg1, String arg2Display) {
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

   public static void main(String[] args) {

      (new KatalonTestReader()).parseFile();
   }

}
