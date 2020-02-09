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
package org.sullytestgenerator.sully.domain;

import java.util.HashMap;
import java.util.Map;

import org.sullytestgenerator.sully.SullyTestBase;

/**
 * Class to store username and password.
 * 
 * Different passwords may be stored for different environments.
 * 
 * Passwords may be set at runtime so they are not stored in code.
 * See: SullyTestBase.loadTestUsers()
 *
 */
public class User {

   public String userName = null;

   // Used to store a single password.
   public String password = null;

   // Used to store passwords based on environment.
   public Map<Env, String> passwordsByEnv = new HashMap<>();

   public User(String userName) {
      this(userName, null);
   }

   public User(String userName, String password) {
      super();
      this.userName = userName;
      
      // Check if the password should be overridden by a value
      //  passed in the JVM program arguments.
      String finalPassword = findFinalPassword(userName, null, password);
      setPassword(finalPassword);
   }
   
   
   protected String findFinalPassword(String userName, Env env, String password) {
      String result = password;
      String runtimePassword = SullyTestBase.checkRuntimePassword(userName, env);
      
      if (runtimePassword != null) {
         result = runtimePassword;
      }
      
      return result;
   }
   

   /**
    * Store passwords based on environment.
    * 
    * e.g., 
    *    User aUser = new User("myUserName");
    *     
    *    aUser.addPassword(Env.DEV, "myDevPassword")
    *    .addPassword(Env.TEST, "myTestPassword")
    *    .addPassword(Env.PROD, "myProdPassword");
    * 
    * @param env
    * @param password
    * @return
    */
   public User addPassword(Env env, String password) {
      if (env != null) {
         String finalPassword = findFinalPassword(userName, env, password);
         
         passwordsByEnv.put(env, finalPassword);
      }

      return this;
   }


   /**
    * Returns the password for a given environment.
    * 
    * If the password for a given 'env' has not been stored, the default 'password'
    * is returned.
    * 
    * @param env
    * @return
    */
   public String getPassword(Env env) {
      String result = getPassword();

      if (passwordsByEnv.containsKey(env)) {
         result = passwordsByEnv.get(env);
      }

      return result;
   }

   // -----------------------------------------------

   public String getUserName() {
      return userName;
   }

   protected void setUserName(String userName) {
      this.userName = userName;
   }

   public String getPassword() {
      return password;
   }

   /**
    * Better to set the password in this class's constructor
    * or using the addPassword(Env, String) method.
    * 
    * @param password
    */
   protected void setPassword(String password) {
      
      this.password = password;
   }

}
