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
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.sullytestgenerator.sully.command.AllSeleniumCommands;
import org.sullytestgenerator.sully.command.SullyCommand;
import org.sullytestgenerator.sully.domain.Env;
import org.sullytestgenerator.sully.domain.UserEnv;
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
 * For an example of a 'Sully' test see:
 * org.sullytestgenerator.sully.example.GoogleSullyTest.java
 * 
 * 
 * @author JavaJeffG
 *
 */
public class SullyTestBase extends AllSeleniumCommands implements SullyTestFormatter {

   public static final String COMMENT_DASHED_LINE = "---------------------------------------------";

   public static final String COMMENT_POSTFIX = " --";

   public static final String COMMENT_PREFIX = "-- ";

   public static final String FILE_SEPARATOR = System.getProperty("file.separator");

   private static final String OPEN_TEST_SUITE = "openTestSuite";

   private static final String CLOSE_TEST_SUITE = "closeTestSuite";

   private static final String OPEN_TEST = "openTest";

   private static final String CLOSE_TEST = "closeTest";

   private static String userNamePasswordSeperator = ":";

   private List<SullyCommand> commands = new ArrayList<>();

   // Sleep (in msec) when running 'highlight' commands.
   private int highlightPauseMsec = 200;

   // The base used for the shortPause(), etc. commands.
   // Adjusting this will speed up/ slow down those commands.
   private int sleepCommandBaseMsec = 1000;

   // By default use the KatalonTestFormatter.
   private SullyTestFormatter sullyTestFormatter = new KatalonTestFormatter();

   // Map of UserEnv/password pairs of test users, passed in as Java program
   // arguments.
   // See: loadTestUsers()
   private static Map<UserEnv, String> runtimeTestUsers = new HashMap<>();

   /**
    * This loads testUsers which can be used to override any passwords that have
    * been set in the code.
    * 
    * This assumes the values are in the format: userName:password or
    * userName:env:password.  (NOTE: The 'env' is case insensitive, but must
    * match one of the Env enum values.)
    * 
    * To use, put this command at the beginning of your test class's main()
    * method:
    * 
    * SullyTestBase.loadTestUsers(args);
    * 
    * --------------------------------------------------
    * 
    * For example: User tester4 = new User("testAccount4", "pw77667");
    * 
    * Java program arguments: testAccount4:runtimepw223
    * 
    * Then when run, tester4.getPassword() --> runtimepw223
    * 
    * --------------------------------------------------
    * 
    * An example with an 'env' value:
    * 
    * User tester8 = new User("testAccount8"); 
    * tester8.addPassword(Env.DEV, "pwDev882244");
    * tester8.addPassword(Env.TEST, "pwTest882244");
    * 
    * Java program arguments: 
    * 
    *     testAccount8:dev:runtimepw997
    *     testAccount8:prod:runtimeProd223
    * 
    * Then when run: 
    *     tester8.getPassword(Env.DEV)  --> runtimepw997
    *     tester8.getPassword(Env.TEST) --> pwTest882244
    *     tester8.getPassword(Env.PROD) --> runtimeProd223
    * 
    * @param args
    */
   public static void loadTestUsers(String[] args) {

      if (args.length >= 1) {

         for (String nextTestUser : args) {
            String password = null;
            String envString = null;
            Env env = null;

            StringTokenizer st = new StringTokenizer(nextTestUser, userNamePasswordSeperator);
            String userName = st.nextToken().trim();
            String secondToken = st.nextToken().trim();

            if (st.hasMoreTokens()) {
               // Format: userName:env:password
               envString = secondToken;
               password = st.nextToken().trim();
            }
            else {
               // Format: userName:password
               password = secondToken;
            }

            if (envString != null) {
               env = Env.valueOf(envString.toUpperCase());
            }

            runtimeTestUsers.put(new UserEnv(userName, env), password);
         }
      }
      else {
         System.out.println("To load testUsers, set program arguments to a list of");
         System.out.println("userName:password or userName:env:password values.");
         System.out.println("The value of 'env' must match one of the Env enum");
         System.out.println("values (ignoring case).  Examples:");
         System.out.println("");
         System.out.println("myUserName:myPassword345");
         System.out.println("myUserName:test:myOtherPassword997");
         System.out.println("devUser:dev:thatPw56");
         System.out.println("ProdTestUsr:prod:hereWeGo99");
         System.out.println("-----------------------------------------------------");
      }
   }

   public static String checkRuntimePassword(String userName, Env env) {
      UserEnv key = new UserEnv(userName, env);

      String result = runtimeTestUsers.get(key);

      return result;
   }

   /**
    * Use this to change the delimiter character between the userName and password
    * (and optionally the env) if the default character ":" is part of the userName
    * or password.
    * 
    * @param value
    */
   public static void setUserNamePasswordSeperator(String value) {
      userNamePasswordSeperator = value;
   }

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

   /**
    * Outputs to a Java 'temp' file with the chose filenamePrefix and a filename
    * extension of ".html".
    * 
    * e.g.,
    * 
    * outputToTempHtmlFile("GoogleSullyTest_GEN");
    * 
    * C:\Users\Stephen\AppData\Local\Temp\GoogleSullyTest_GEN4271280384385216753.html
    * 
    * @param filenamePrefix
    */
   public void outputToTempHtmlFile(String filenamePrefix) {
      outputToTempFile(filenamePrefix, ".html");
   }

   /**
    * Outputs to a Java 'temp' file with the chose filenamePrefix and
    * fileExtension.
    * 
    * e.g.,
    * 
    * outputToTempFile("XYZ_login_test", ".txt");
    * 
    * C:\Users\Stephen\AppData\Local\Temp\XYZ_login_test4271280384385216753.txt
    * 
    * @param filenamePrefix
    * @param fileExtension
    */
   public void outputToTempFile(String filenamePrefix, String fileExtension) {
      String outputfilename = null;

      try {
         File temp = File.createTempFile(filenamePrefix, fileExtension);
         outputfilename = temp.getAbsolutePath();
      }
      catch (IOException e) {
         e.printStackTrace();
      }

      outputToFile(outputfilename);
   }

   /**
    * Useful for storing output files in a single directory.
    * 
    * e.g., final String ALL_MY_SELENIUM_FILES = "C:/temp/seleniumFiles/";
    * 
    * outputToFile(ALL_MY_SELENIUM_FILES, "MyFirstSeleniumTest.html");
    * 
    * @param path
    * @param shortFilename (with file extension) - e.g. "MyTest.html"
    */
   public void outputToFile(String path, String shortFilename) {
      if (path != null && (path.endsWith("/") || path.endsWith("\\") || path.endsWith(File.pathSeparator))) {
         path = path.substring(0, path.length() - 1);
      }
      String outputfilename = path + File.separator + shortFilename;

      outputToFile(outputfilename);
   }

   /**
    * Output generated test file.
    * 
    * @param filename (full path filename with file extension)
    */
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

      int fileNameIndex = filename.lastIndexOf(FILE_SEPARATOR);

      if (fileNameIndex == -1) {
         fileNameIndex = filename.lastIndexOf("/");
      }

      if (fileNameIndex == -1) {
         fileNameIndex = filename.lastIndexOf("\\");
      }

      System.out.println(filename.substring(0, fileNameIndex));
      System.out.println(filename.substring(fileNameIndex + 1));
      System.out.println("-----------------------------------\n");

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
            // Regular Selenium command.
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
      command_comment(COMMENT_PREFIX + comment + COMMENT_POSTFIX);
   }

   public void commentBlock(String comment) {
      commentDashed();
      commentDashed();
      comment(comment);
      commentDashed();
      commentDashed();
   }

   public void commentDashed() {
      command_comment(COMMENT_DASHED_LINE);
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

   public void veryShortPause() {
      // 0.25 seconds in mSec.
      command_pause(0.250 * getSleepCommandBaseMsec());
   }

   public void tinyPause() {
      // 0.01 seconds in mSec.
      command_pause(0.010 * getSleepCommandBaseMsec());
   }

   // -----------------------------------------------------------------------------------
   // Additional TESTCASE operations.

   public void highlightAndAssertText(String element, String text) {
      command_highlight(element);
      command_pause(getHighlightPauseMsec());
      command_assertText(element, text);
   }

   public void highlightAndClick(String element) {
      command_highlight(element);
      command_pause(getHighlightPauseMsec());
      command_click(element);
   }

   public void highlightAndSelect(String element, String text) {
      command_highlight(element);
      command_pause(getHighlightPauseMsec());
      command_select(element, text);
   }

   public void highlightAndType(String element, String text) {
      command_highlight(element);
      command_pause(getHighlightPauseMsec());
      command_type(element, text);
   }

   public void highlightTwiceAndAssertText(String element, String text) {
      command_highlight(element);
      command_pause(getHighlightPauseMsec());
      highlightAndAssertText(element, text);
   }

   public void highlightTwiceAndClick(String element) {
      command_highlight(element);
      command_pause(getHighlightPauseMsec());
      highlightAndClick(element);
   }

   public void highlightTwiceAndSelect(String element, String text) {
      command_highlight(element);
      command_pause(getHighlightPauseMsec());
      highlightAndSelect(element, text);
   }

   public void highlightTwiceAndType(String element, String text) {
      command_highlight(element);
      command_pause(getHighlightPauseMsec());
      highlightAndType(element, text);
   }

   // -----------------------------------------------------------------------------------

}
