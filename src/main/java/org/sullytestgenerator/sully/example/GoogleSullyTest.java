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

import org.sullytestgenerator.sully.SullyTestBase;

/**
 * An example class showing how to create a test suite file
 * with two Selenium tests, output in the Katalon IDE format.
 * 
 * By default Sully formats using KatalonTestFormatter.java. 
 * Other formats can be produced by implementing the 
 * SullyTestFormatter.java interface.
 *
 */
public class GoogleSullyTest extends SullyTestBase {

   public static final String SEARCH_BUTTON = "//input[@value='Google Search']";
   public static final String SEARCH_TEXTFIELD = "//input[@title='Search']";
   
   public void run() {
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
      outputToTempHtmlFile("GoogleSullyTest_GEN");
      
      // or to a specific file:  
      // outputToFile("C:/temp/GoogleSullyTest_GEN.html");
   }

   protected void createTest_GoogleSearch() {
      // ---------------------------------------------------
      // Open the test case.
      openTest("Test 1 - Google search - Selenium IDE");

      // ---------------------------------------------------
      // Add a bunch of Sully comments and commands.
      // For a full list of commands, see: AllSeleniumCommands.java

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
      tinyPause();
      commentDashed();

      highlightTwiceAndClick(SEARCH_BUTTON);

      commentDashed();

      highlightAndClick(SEARCH_BUTTON);

      command_waitForTextPresent("Getting started with Selenium IDE requires no");
      command_assertTextPresent("Getting started with Selenium IDE requires no");
      longPause();
      commentDashed();

      // ---------------------------------------------------
      // Close the test case.
      closeTest();
   }

   protected void createTest_GoogleSearch2() {
      openTest("Test 2 - Google search - Selenium IDE");

      commentBlock("Test of Google search: 'Selenium IDE'");
      command_open("https://google.com");
      command_waitForTextPresent("Store");
      shortPause();
      command_highlight("//input[@title='Search']");
      shortPause();
      command_type("//input[@title='Search']", "Selenium IDE");
      shortPause();
      comment("Highlight 'Google Search' button twice.");
      command_highlight("//input[@value='Google Search']");
      veryShortPause();
      command_highlight("//input[@value='Google Search']");
      tinyPause();
      commentDashed();
      highlightTwiceAndClick("//input[@value='Google Search']");
      commentDashed();
      highlightAndClick("//input[@value='Google Search']");
      command_waitForTextPresent("Getting started with Selenium IDE requires no");
      command_assertTextPresent("Getting started with Selenium IDE requires no");
      longPause();
      commentDashed();

      closeTest();
   }
   
   public static void main(String[] args) {
      SullyTestBase.loadTestUsers(args);

      (new GoogleSullyTest()).run();

   }

}
