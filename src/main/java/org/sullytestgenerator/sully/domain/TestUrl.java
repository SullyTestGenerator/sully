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

/**
 * Class to store test URLs based on ENV.
 * 
 *
 */
public class TestUrl {

   // Used to store urls based on environment.
   public Map<Env, String> urlByEnv = new HashMap<>();

   /**
    * Store urls based on environment.
    * 
    * e.g., 
    *    TestUrl xyzApplication = new TestUrl();
    *     
    *    xyzApplication
    *    .addUrl(Env.LCL, "http://localhost:8080/myApp")
    *    .addUrl(Env.DEV, "https://dev.xyzapp")
    *    .addUrl(Env.TEST, "https://tst.xyzapp")
    *    .addUrl(Env.PROD, "https://www.xyzapp.com");
    * 
    * @param env
    * @param url
    * @return
    */
   public TestUrl addUrl(Env env, String url) {
      if (env != null) {
         urlByEnv.put(env, url);
      }

      return this;
   }

   // -----------------------------------------------

   protected Map<Env, String> getUrlsByEnv() {
      return urlByEnv;
   }

   protected void setUrlsByEnv(Map<Env, String> urlsByEnv) {
      this.urlByEnv = urlsByEnv;
   }
   
   /**
    * Return the web test URL, based on Env.
    * 
    * @param env
    * @return
    */
   public String getUrl(Env env) {
      return getUrlsByEnv().get(env);
   }

}
