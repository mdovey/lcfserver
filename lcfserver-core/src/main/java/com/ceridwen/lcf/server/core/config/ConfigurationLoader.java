/*******************************************************************************
 * Copyright (c) 2016, Matthew J. Dovey (www.ceridwen.com).
 *   
 * Licensed under the Apache License, Version 2.0 (the "License");
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *   
 *     http://www.apache.org/licenses/LICENSE-2.0
 *   
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *    
 *   
 * Contributors:
 *     Matthew J. Dovey (www.ceridwen.com) - initial API and implementation
 *
 *     
 *******************************************************************************/
package com.ceridwen.lcf.server.core.config;

import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;

public class ConfigurationLoader {
	
	private static Configuration configuration = null;
	
	public static Configuration getConfiguration() {
		Configuration conf = null;
		if (configuration == null) {
			ServiceLoader<Configuration> configurationLoader = ServiceLoader.load(Configuration.class);
			   
			   while (conf == null && configurationLoader.iterator().hasNext()) {
			    	try{
			    		conf = configurationLoader.iterator().next();
			        } catch ( LinkageError | ServiceConfigurationError | Exception e) {
			        	conf = null;
				    }
			    }
			
			if (conf == null) {
				conf = new EmptyConfiguration();
				System.out.println("Unable to load backend");
			}
		
			System.out.println("Loaded backend: " + conf.getClass().getName());
			configuration= conf;
		} 
		return configuration;
	}

}