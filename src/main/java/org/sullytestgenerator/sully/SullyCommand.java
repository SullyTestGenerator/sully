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

/**
 * SullyCommand
 * 
 * @author JavaJeffG
 *
 */
public class SullyCommand {

   // NOTE: SullyCommands are the normal Selenium (IDE) commands plus a couple
   // extra 'commands', e.g., openTestSuite, closeTestSuite, openTest, closeTest.

   private String commandName = null;

   private String arg1 = null;

   private String arg2 = null;

   public SullyCommand(String commandName, String arg1, String arg2) {
      super();
      this.commandName = commandName;
      this.arg1 = arg1;
      this.arg2 = arg2;
   }

   public String getCommandName() {
      return commandName;
   }

   public String getArg1() {
      return arg1;
   }

   public String getArg2() {
      return arg2;
   }

}
