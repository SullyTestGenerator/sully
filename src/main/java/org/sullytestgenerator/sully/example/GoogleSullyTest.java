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
package org.sullytestgenerator.sully.example;

import java.io.File;
import java.io.IOException;

import org.sullytestgenerator.sully.SullyTestBase;

public class GoogleSullyTest extends SullyTestBase {

   public static final boolean CREATE_AS_TEMP_FILE = true;

   public static final String SEARCH_BUTTON = "//input[@value='Google Search']";
   public static final String SEARCH_TEXTFIELD = "//input[@title='Search']";

   public void run() {
      // Note: I usually put "_GEN" on the file name to highlight
      // that this is a generated file and any changes may be overwritten.

      // Full filename used if CREATE_AS_TEMP_FILE is false.
      String outputfilename = "C:/GoogleSullyTest_GEN.html";

      if (CREATE_AS_TEMP_FILE) {
         try {
            File temp = File.createTempFile("GoogleSullyTest_GEN", ".html");
            outputfilename = temp.getAbsolutePath();
         }
         catch (IOException e) {
            e.printStackTrace();
         }
      }

      // Open the overall test suite.
      openTestSuite("GoogleSullyTestSuite");

      // Call 'createTest' methods to create individual test cases.
      createTest_GoogleSearch();
      createTest_GoogleSearch2();

      // Close the overall test suite.
      closeTestSuite();

      // Generate the output.
      // By default, this method uses the KatalonTestFormatter
      // to produce Katalon IDE tests.
      outputToFile(outputfilename);
   }

   protected void createTest_GoogleSearch() {
      // ---------------------------------------------------
      // Open the test case.
      openTest("Test 1 - Google search - Selenium IDE");

      // ---------------------------------------------------
      // Add a bunch of Sully comments and commands.
      // For a fully list of commands, see: AllSeleniumCommands.java

      commentBlock("Test of Google search: 'Selenium IDE'");

      command_open("https://google.com");
      command_waitForTextPresent("Store");
      shortPause();

      command_highlight(SEARCH_TEXTFIELD);
      shortPause();

      command_type(SEARCH_TEXTFIELD, "Selenium IDE");
      shortPause();

      comment("Highlight 'Google Search' button twice.");
      command_highlight(SEARCH_BUTTON);
      veryShortPause();
      command_highlight(SEARCH_BUTTON);
      veryShortPause();
      commentDashed();

      highlightTwiceAndClick(SEARCH_BUTTON);

      commentDashed();

      highlightAndClick(SEARCH_BUTTON);

      command_waitForTextPresent("Getting started with Selenium IDE requires no");
      command_assertTextPresent("Getting started with Selenium IDE requires no");

      commentDashed();

      // ---------------------------------------------------
      // Close the test case.
      closeTest();
   }

   protected void createTest_GoogleSearch2() {
      openTest("Test 2 - Google search - Selenium IDE");

      command_echo("---------------------------------------------");
      command_echo("---------------------------------------------");
      command_echo("-- Test of Google search: 'Selenium IDE' --");
      command_echo("---------------------------------------------");
      command_echo("---------------------------------------------");
      command_open("https://google.com");
      command_waitForTextPresent("Store");
      longPause();
      command_highlight("//input[@title='Search']");
      shortPause();
      command_type("//input[@title='Search']", "Selenium IDE");
      command_pause("1000");
      command_echo("-- Highlight 'Google Search' button twice. --");
      command_highlight("//input[@value='Google Search']");
      veryShortPause();
      command_highlight("//input[@value='Google Search']");
      tinyPause();
      command_echo("---------------------------------------------");
      command_click("//input[@value='Google Search']");
      command_waitForTextPresent("Getting started with Selenium IDE requires no");
      command_assertTextPresent("Getting started with Selenium IDE requires no");
      command_echo("---------------------------------------------");

      closeTest();
   }

   public static void main(String[] args) {

      (new GoogleSullyTest()).run();

   }

}
