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
package com.ceridwen.lcf.server.frontend.restlet.modules;

import org.restlet.Application;
import org.restlet.Client;
import org.restlet.data.Protocol;
import org.restlet.resource.Directory;
import org.restlet.ext.swagger.Swagger2SpecificationRestlet;
import org.restlet.routing.Router;

import com.ceridwen.lcf.server.frontend.restlet.core.OptionalModuleConfiguration;
//import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;
import com.wordnik.swagger.util.Json;
import com.wordnik.swagger.util.Yaml;
import javax.servlet.ServletContext;


public class SwaggerModule implements OptionalModuleConfiguration {

	@Override
	public void initialise(Application application, Router router) {
		Json.mapper().registerModule(new JaxbAnnotationModule());
    Yaml.mapper().registerModule(new JaxbAnnotationModule());

    String path = "http://lcf.ceridwen.com";
    try {
      ServletContext sc = (ServletContext) application.getContext().getAttributes().get("org.restlet.ext.servlet.ServletContext");
      path = path + sc.getContextPath();
    } catch (Exception ex) {
    }
    
    // Configuring Swagger 2 support
    Swagger2SpecificationRestlet swagger2SpecificationRestlet
                   = new Swagger2SpecificationRestlet(application);
    swagger2SpecificationRestlet.setBasePath(path);
    swagger2SpecificationRestlet.attach(router, "/api-docs/swagger.json");
    
    Directory swaggerUI = new Directory(application.getContext(), "clap://classloader/swagger-2.1.4/");
    swaggerUI.setIndexName("index.html");
    application.getContext().setClientDispatcher(new Client(application.getContext(), Protocol.CLAP));
    
    router.attach("/swagger/", swaggerUI);
  }
}
