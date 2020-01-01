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

   public static final String SEARCH_BUTTON = "//input[@value='Google Search']";
   public static final String SEARCH_TEXTFIELD = "//input[@title='Search']";

   public void run() {
      // Note: I usually put "_GEN" on the file name to highlight
      // that this is a generated file and any changes may be overwritten.

      String outputfilename = "C:/GoogleSullyTest_GEN.html";

      try {
         File temp = File.createTempFile("GoogleSullyTest_GEN", ".html");
         outputfilename = temp.getAbsolutePath();
      }
      catch (IOException e) {
         e.printStackTrace();
      }

      openTestSuite("GoogleSullyTestSuite");

      createTest_GoogleSearch();

      closeTestSuite();

      outputToFile(outputfilename);
   }

   protected void createTest_GoogleSearch() {
      openTest("Google search - Selenium IDE");

      commentDashed();
      comment("Test of Google search: 'Selenium IDE'");

      command_open("https://google.com");
      command_waitForTextPresent("Store");
      shortPause();

      command_highlight(SEARCH_TEXTFIELD);
      shortPause();

      command_type(SEARCH_TEXTFIELD, "Selenium IDE");
      shortPause();

      comment("Highlight 'Google Search' button twice.");
      command_highlight(SEARCH_BUTTON);
      verySortPause();
      command_highlight(SEARCH_BUTTON);
      verySortPause();
      commentDashed();

      command_click(SEARCH_BUTTON);

      command_waitForTextPresent("Getting started with Selenium IDE requires no");
      command_assertTextPresent("Getting started with Selenium IDE requires no");

      commentDashed();

      closeTest();
   }

   public static void main(String[] args) {

      (new GoogleSullyTest()).run();

   }

}
