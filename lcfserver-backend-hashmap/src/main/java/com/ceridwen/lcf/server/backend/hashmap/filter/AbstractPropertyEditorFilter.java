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
package com.ceridwen.lcf.server.backend.hashmap.filter;

import com.ceridwen.lcf.server.core.QueryResults;
import com.ceridwen.lcf.server.core.EntityTypes.Type;
import com.ceridwen.lcf.server.core.filter.EntitySourcesFilter;
import com.ceridwen.lcf.server.core.persistence.EntitySourceInterface;
import com.ceridwen.lcf.server.core.persistence.EntitySourcesInterface;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class AbstractPropertyEditorFilter<E> implements EntitySourcesFilter {
	
	EntitySourcesInterface entitySources;
	
	abstract Class<E> getHandledClass();
  abstract public List<String> filterListEditableProperties();
  abstract public void filterSetProperty(String identifier, String property, String value);
	
	@Override
	public EntitySourcesInterface filters(EntitySourcesInterface entitySources) {
		
		this.entitySources = entitySources;
		
		return new EntitySourcesInterface() {
			@SuppressWarnings({ "unchecked" })
			@Override
			public <T> EntitySourceInterface<T> getEntitySource(Type type, Class<T> clazz) {
				if (clazz == getHandledClass()) {
					return (EntitySourceInterface<T>) new ReadOnlyFieldsManagedEntitySource(entitySources.getEntitySource(type, getHandledClass()));
				} else {
					return entitySources.getEntitySource(type, clazz);
				}
			}
		};
	}

	class ReadOnlyFieldsManagedEntitySource implements EntitySourceInterface<E> {
		private EntitySourceInterface<E> wrapped;
	
		public ReadOnlyFieldsManagedEntitySource(EntitySourceInterface<E> wrapped) {
			this.wrapped = wrapped;
		}
	

		@Override
		public String Create(Object parent, E entity) {
			return wrapped.Create(parent, entity);
		}

	
		@Override
		public String Create(E entity) {
			return wrapped.Create(entity);
		}

		@Override
		public E Retrieve(String identifier) {
			return wrapped.Retrieve(identifier);
		}
	
		@Override
		public E Modify(String identifier, E entity) {
			return wrapped.Modify(identifier, entity);
		}
	
		@Override
		public void Delete(String identifier) {
			wrapped.Delete(identifier);		
		}
	
		@Override
		public QueryResults<E> Query(Object parent, int start, int max, String query) {
			return wrapped.Query(parent, start, max, query);
		}
	
		@Override
		public QueryResults<E> Query(String query, int start, int max) {
			return wrapped.Query(query, start, max);
		}

    @Override
    public List<String> listEditableProperties() {
      ArrayList<String> props = new ArrayList<>(this.wrapped.listEditableProperties());
      props.addAll(filterListEditableProperties());
      return props;
    }

    @Override
    public void setProperty(String identifier, String property, String value) {
      filterSetProperty(identifier, property, value);
      this.wrapped.setProperty(identifier, property, value);
    }
	}
}
