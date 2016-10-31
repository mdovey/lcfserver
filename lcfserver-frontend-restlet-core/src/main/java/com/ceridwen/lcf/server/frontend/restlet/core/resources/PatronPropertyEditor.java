/*
 * Copyright 2016 Ceridwen Limited.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ceridwen.lcf.server.frontend.restlet.core.resources;

import com.ceridwen.lcf.server.core.EntityTypes;
import java.util.List;
import org.bic.ns.lcf.v1_0.Patron;
import org.restlet.resource.Put;

/**
 *
 * @author Matthew.Dovey
 */
public class PatronPropertyEditor extends AbstractPropertyEditorResource {

	ResourceHandler<Patron> handler;
	
	public PatronPropertyEditor() {
		handler = new ResourceHandler<>(this, EntityTypes.Type.Patron);
	}


	@Put(ResourceHandler.CONSUME_PRODUCES_TYPES)
	public void Modify(String data) {
		handler.setProperty(data);
	}
  
  @Override
  public List<String> listEditableProperties() {
    return handler.listEditableProperties();
  }
}
