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
import org.sullytestgenerator.sully.domain.Env;
import org.sullytestgenerator.sully.domain.User;

/**
 * An example class showing how test user passwords
 * may be set at runtime so values do not need to 
 * be set in code (or if they are they can optionally
 * be overridden by runtime values).
 *
 */
public class ExampleTestWithUsers extends SullyTestBase { 

   protected static User myStaticUser = new User("myStaticUserAccount", "pwStaticSetInTheCode");

   static {
      myStaticUser.addPassword(Env.DEV, "pwSetInCodeDEVstaticPw222");
   }
   
   public void run() {
      // Open the overall test suite.
      openTestSuite("ExampleTestWithUsersSuite");
  
      createTest_CheckUser();

      // Close the overall test suite.
      closeTestSuite();

      // Generate the output.
      // By default, this method uses the KatalonTestFormatter
      // to produce Katalon IDE tests.
      outputToTempHtmlFile("ExampleTestWithUsers_GEN");
      
      // or to a specific file:  
      // outputToFile("C:/temp/GoogleSullyTest_GEN.html");
   }

   protected void createTest_CheckUser() {
      openTest("Test - Check User");

      commentDashed();
      commentDashed();
      comment("Test - Check User name and password.");
      commentDashed();
      commentDashed();
      
      User myUserAccount = new User("myUserAccount", "pwSetInTheCode");
      
      myUserAccount.addPassword(Env.DEV, "devPwSetInCode");
      
      System.out.println("myUserAccount password:     " + myUserAccount.password);
      System.out.println("myUserAccount password:     " + myUserAccount.getPassword());
      
      System.out.println("myUserAccount DEV password: " + myUserAccount.getPassword(Env.DEV));
      
      System.out.println("-----------------------------------");
       
      System.out.println("myStaticUser password:      " + myStaticUser.password);
      System.out.println("myStaticUser password:      " + myStaticUser.getPassword());
      
      System.out.println("myStaticUser DEV password:  " + myStaticUser.getPassword(Env.DEV));
      System.out.println("myStaticUser PROD password: " + myStaticUser.getPassword(Env.PROD));
      
      System.out.println("-----------------------------------");
      
      closeTest();
   }

   public static void main(String[] args) {
      SullyTestBase.loadTestUsers(args);

      (new ExampleTestWithUsers()).run();

   }

}
