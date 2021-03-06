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
package com.ceridwen.lcf.server.core;

import java.util.HashMap;
import java.util.Map;

import org.bic.ns.lcf.v1_0.EntityType;

import com.ceridwen.util.indirection.Getter;
import com.ceridwen.util.indirection.Setter;

@SuppressWarnings("rawtypes")
public class EntityTypes {
	public static final String LCF_PREFIX = "/lcf/1.0";
	
	static Map<EntityType, Type> codetable = new HashMap<>();
	static Map<Class, Type> classtable = new HashMap<>();
	
	public static enum Type { 
		Manifestation(EntityType.MANIFESTATIONS, org.bic.ns.lcf.v1_0.Manifestation.class, o->o.getIdentifier(), (o,s)->o.setIdentifier(s)),
		Item(EntityType.ITEMS, org.bic.ns.lcf.v1_0.Item.class, o->o.getIdentifier(), (o,s)->o.setIdentifier(s)),
		Patron(EntityType.PATRONS, org.bic.ns.lcf.v1_0.Patron.class, o->o.getIdentifier(), (o,s)->o.setIdentifier(s)),
		Location(EntityType.LOCATIONS, org.bic.ns.lcf.v1_0.Location.class, o->o.getIdentifier(), (o,s)->o.setIdentifier(s)),
		Loan(EntityType.LOANS, org.bic.ns.lcf.v1_0.Loan.class, o->o.getIdentifier(), (o,s)->o.setIdentifier(s)),
		Reservation(EntityType.RESERVATIONS, org.bic.ns.lcf.v1_0.Reservation.class, o->o.getIdentifier(), (o,s)->o.setIdentifier(s)),
		Charge(EntityType.CHARGES, org.bic.ns.lcf.v1_0.Charge.class, o->o.getIdentifier(), (o,s)->o.setIdentifier(s)),
		Payment(EntityType.PAYMENTS, org.bic.ns.lcf.v1_0.Payment.class, o->o.getIdentifier(), (o,s)->o.setIdentifier(s)),
		Contact(EntityType.CONTACTS, org.bic.ns.lcf.v1_0.Contact.class, o->o.getIdentifier(), (o,s)->o.setIdentifier(s)),
		ClassScheme(EntityType.CLASS_SCHEMES, org.bic.ns.lcf.v1_0.ClassScheme.class, o->o.getIdentifier(), (o,s)->o.setIdentifier(s)),
		ClassTerm(EntityType.CLASS_TERMS, org.bic.ns.lcf.v1_0.ClassTerm.class, o->o.getIdentifier(), (o,s)->o.setIdentifier(s)),
		Property(EntityType.PROPERTIES, org.bic.ns.lcf.v1_0.Property.class, o->o.getIdentifier(), (o,s)->o.setIdentifier(s));

                private final EntityType typeCode;
                private final Class typeClass;
                private final Getter getId;
                private final Setter setId;

            <T> Type(EntityType typeCode, Class<T> clazz, Getter<T> getIdentifier, Setter<T> setIdentifier) {
	    	this.typeCode = typeCode;
	    	this.typeClass = clazz;
	    	this.getId = getIdentifier;
	    	this.setId = setIdentifier;
	    	codetable.put(this.typeCode, this);
	    	classtable.put(this.typeClass, this);
	    }
	    
	    public EntityType getEntityTypeCode() {
	        return this.typeCode;
	    }	

	    public String getEntityTypeCodeValue() {
	        return this.typeCode.value();
	    }	
	    
	    public Class getTypeClass() {
	    	return this.typeClass;
	    }
	    
	    @SuppressWarnings("unchecked")
		public String getIdentifier(Object o) {
	    	return this.getId.get(o);
	    }

	    @SuppressWarnings("unchecked")
		public void setIdentifier(Object o, String s) {
	    	this.setId.set(o, s);
	    }
	}
		
	public static Type lookUpByEntityTypeCode(EntityType type) {
		return codetable.get(type);
	}

	public static Type lookUpByEntityTypeCodeValue(String alpha) {
		try {
			return codetable.get(EntityType.fromValue(alpha));
		} catch (IllegalArgumentException e) {
			return null;
		}
	}
	
	public static Type lookUpByClass(Class typeClass) {
		return classtable.get(typeClass);
	}
}
