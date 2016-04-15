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
package com.ceridwen.lcf.server.frontend.jaxrs.resources;

import javax.ws.rs.POST;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.DefaultValue;

import org.bic.ns.lcf.v1_0.ClassTerm;
import org.bic.ns.lcf.v1_0.LcfEntityListResponse;

import com.ceridwen.lcf.server.core.EntityTypes;

public class ClassTermResource {
	ResourceHandler<ClassTerm> handler;
	
	public ClassTermResource() {
		handler = new ResourceHandler<>(EntityTypes.Type.ClassTerm);
	}

	@GET
	@Path("/lcf/1.0/class-code")
	public LcfEntityListResponse List(@DefaultValue("0") @QueryParam("os:startIndex") int startIndex, @DefaultValue("0") @QueryParam("os:count") int count) {
		return handler.List("", "", startIndex, count);
	}

	@GET
	@Path("/lcf/1.0/{parent}/{identifier}/class-code")
	public LcfEntityListResponse List(@PathParam("parent") String parent, @PathParam("identifier") String identifier, @DefaultValue("0") @QueryParam("os:startIndex") int startIndex, @DefaultValue("0") @QueryParam("os:count") int count) {
		return handler.List(parent, identifier, startIndex, count);
	}
	
	@POST
	@Path("/lcf/1.0/class-code")	
	public ClassTerm Create(ClassTerm data) {
		return handler.Create("", "", data);
	}

	@POST
	@Path("/lcf/1.0/{parent}/{identifier}/class-code")	
	public ClassTerm Create(@PathParam("parent") String parent, @PathParam("identifier") String identifier, ClassTerm data) {
		return handler.Create(parent, identifier, data);
	}

	@GET
	@Path("/lcf/1.0/class-code/{identifier}")
	public ClassTerm Retrieve(@PathParam("identifier") String identifier) {
		return handler.Retrieve(identifier);
	}

	@PUT
	@Path("/lcf/1.0/class-code/{identifier}")
	public ClassTerm Modify(@PathParam("identifier") String identifier, ClassTerm data) {
		return handler.Modify(identifier, data);
	}

	@DELETE
	@Path("/lcf/1.0/class-code/{identifier}")
	public void Delete(@PathParam("identifier") String identifier) {
		handler.Delete(identifier);
	}

}
