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
package com.ceridwen.lcf.server.core.integrity;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;


import org.apache.commons.lang.StringUtils;
import org.bic.ns.lcf.v1_0.Charge;
import org.bic.ns.lcf.v1_0.Contact;
import org.bic.ns.lcf.v1_0.Item;
import org.bic.ns.lcf.v1_0.Loan;
import org.bic.ns.lcf.v1_0.Location;
import org.bic.ns.lcf.v1_0.Manifestation;
import org.bic.ns.lcf.v1_0.Patron;
import org.bic.ns.lcf.v1_0.Reservation;

import com.ceridwen.lcf.server.core.EntityTypes;

import org.bic.ns.lcf.v1_0.AssociatedLocation;

// FIXME Complete all relationship entries

public class RelationshipFactory {
	
	private static final List<Relationship<?,?>> relationships = new ArrayList<Relationship<?,?>>() {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1;
		{

      // Manifestation 1 -> n Item
			add(new Relationship<>(
				EntityTypes.Type.Manifestation, 
				new SingletonRef<>(
					Item::getManifestationRef, 
          Item::setManifestationRef),
				EntityTypes.Type.Item, 
				new ListRef<>(
					(Manifestation o,String s) -> o.getItemRef().add(s),
					(Manifestation o,String s) -> o.getItemRef().remove(s),
					(Manifestation o,String s) -> o.getItemRef().contains(s),
           Manifestation::getItemRef
        ),
				Relationship.Integrity.ParentRequired
			));		
			
      // Item 1 -> n Location
			add(new Relationship<>(
				EntityTypes.Type.Item,
				new NullListRef<>(),
				EntityTypes.Type.Location, 
				new ListRef<Item>(
					(Item o, String s) -> {}, // AssociatedLocation is complex type so cannot be added automatically
					(Item o, String s) -> o.getAssociatedLocation().removeIf(
                                  a -> StringUtils.equals(a.getLocationRef(), s)
                                ),
					(Item o, String s) -> {	for (AssociatedLocation aloc: o.getAssociatedLocation()) {
                                    if (StringUtils.equals(aloc.getLocationRef(), s)) {
                                      return true;
                                    }
                                  }
                                  return false;
                                },
					o -> {  Vector<String> locs = new Vector<>();
                  o.getAssociatedLocation().forEach(
                    a -> locs.add(a.getLocationRef())
                  );					
                  return locs;
                }
				)
			));				
			
      // Item 1 -> n Reservation      
			add(new Relationship<>(
				EntityTypes.Type.Item, 
				new SingletonRef<Reservation>(
					Reservation::getItemRef,
          Reservation::setItemRef),
				EntityTypes.Type.Reservation, 
				new ListRef<>(
					(Item o,String s) -> o.getReservationRef().add(s),
					(Item o,String s) -> o.getReservationRef().remove(s),
					(Item o,String s) -> o.getReservationRef().contains(s),
           Item::getReservationRef
        ),
				Relationship.Integrity.ParentRequired
			));

      // Item 1 -> 1 Loan
			add(new Relationship<>(
				EntityTypes.Type.Item, 
				new SingletonRef<>(
          Loan::getItemRef,
          Loan::setItemRef
				),
				EntityTypes.Type.Loan, 
				new SingletonRef<>(
          Item::getOnLoanRef,
          Item::setOnLoanRef
				),
				Relationship.Integrity.ParentRequired
			));			
			
      // Patron 1 -> n Contact
			add(new Relationship<>(
				EntityTypes.Type.Patron, 
				new SingletonRef<>(
          Contact::getPatronRef,
          Contact::setPatronRef
				),
				EntityTypes.Type.Contact, 
				new ListRef<>(
					(Patron o,String s) -> o.getContactRef().add(s),
					(Patron o,String s) -> o.getContactRef().remove(s),
					(Patron o,String s) -> o.getContactRef().contains(s),
           Patron::getContactRef
				),
				Relationship.Integrity.ParentRequired
			));

      // Patron 0 -> n Location
			add(new Relationship<>(
				EntityTypes.Type.Patron, 
				new NullListRef<>(),
				EntityTypes.Type.Location, 
				new ListRef<>(
					(Patron o,String s) -> {}, // AssociatedLocation is complex type so cannot be added automatically
					(Patron o,String s) ->  o.getAssociatedLocation().removeIf(
                                    a -> StringUtils.equals(a.getLocationRef(), s)
                                  ),
					(Patron o,String s) ->  { for (AssociatedLocation aloc: o.getAssociatedLocation()) {
                        if (StringUtils.equals(aloc.getLocationRef(), s)) {
                          return true;
                        }
                      }
                      return false;
                    },
					(Patron o) -> { Vector<String> locs = new Vector<>();
                          o.getAssociatedLocation().forEach(
                            a -> locs.add(a.getLocationRef()) 
                          );					
                          return locs;
                        }
				)
			));
			
      // Patron 1 -> n Loan
			add(new Relationship<>(
				EntityTypes.Type.Patron, 
				new SingletonRef<>(
          Loan::getPatronRef,
          Loan::setPatronRef
				),
				EntityTypes.Type.Loan, 
				new ListRef<>(
					(Patron o, String s) -> o.getLoanRef().add(s),
					(Patron o, String s) -> o.getLoanRef().remove(s),
					(Patron o, String s) -> o.getLoanRef().contains(s),
           Patron::getLoanRef
				),
				Relationship.Integrity.ParentRequired
			));

      // Patron 1 -> n Reservation
			add(new Relationship<>(
				EntityTypes.Type.Patron, 
				new SingletonRef<>(
          Reservation::getPatronRef,
          Reservation::setPatronRef
				),
				EntityTypes.Type.Reservation, 
				new ListRef<>(
					(Patron o, String s) -> o.getReservationRef().add(s),
					(Patron o, String s) -> o.getReservationRef().remove(s),
					(Patron o, String s) -> o.getReservationRef().contains(s),
           Patron::getReservationRef
				),
				Relationship.Integrity.ParentRequired
			));
			
      // Patron 1-> n Charge
			add(new Relationship<>(
				EntityTypes.Type.Patron, 
				new SingletonRef<>(
          Charge::getPatronRef,
					Charge::setPatronRef
				),
				EntityTypes.Type.Charge, 
				new ListRef<>(
					(Patron o,String s) -> o.getChargeRef().add(s),
					(Patron o,String s) -> o.getChargeRef().remove(s),
					(Patron o,String s) -> o.getChargeRef().contains(s),
           Patron::getChargeRef
				),
				Relationship.Integrity.ParentRequired
			));
			
		}
	};
	
	public static List<Relationship<?,?>> getRelationshipsAsParent(EntityTypes.Type parent) {
		List<Relationship<?,?>> result = new Vector<>();		
    relationships.stream().filter(r -> r.getParentType() == parent).forEach(result::add);
		return result;
	}

	public static List<Relationship<?,?>> getRelationshipsAsChild(EntityTypes.Type child) {
		List<Relationship<?,?>> result = new Vector<>();		
    relationships.stream().filter(r -> r.getChildType() == child).forEach(result::add);		
		return result;
	}
	
	public static Relationship<?,?> getRelationship(EntityTypes.Type parent, EntityTypes.Type child) {
		for (Relationship<?,?> r: relationships) {
			if (r.getParentType() == parent && r.getChildType() == child) {
				return r;
			}			
		}
		
		return null;		
	}

}
