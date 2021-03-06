> Copyright (c) 2016 Matthew J. Dovey (www.ceridwen.com).
>
> Licensed under the Apache License, Version 2.0 (the "License");
> You may not use this file except in compliance with the License.
> You may obtain a copy of the License at
>
>  [http://www.apache.org/licenses/LICENSE-2.0](http://www.apache.org/licenses/LICENSE-2.0)
>
> Unless required by applicable law or agreed to in writing, software
> distributed under the License is distributed on an "AS IS" BASIS,
> WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
> See the License for the specific language governing permissions and
> limitations under the License.

## LCF Server

Contains implementations of the LCF protocol using the LCF schema generated classes as the java model.

**Build requirements**

* JDK 8 (or later)
* Maven (3.3 or later)

**To build**

```bash
      mvn package
```

**Output**

* __lcfserver-frontend-restlet-cli__  Standalone server (org.restlet framework. To run: `java -jar -lcfserver.jar`. For alternate start-up options: `java -jar lcfserver.jar --help`
* __lcfserver-frontend-restlet-war__  Webapp to deploy on J2EE compliant server (org.restlet framework)
* __lcfserver-frontend-servlet-war__  Webapp to deploy on J2EE compliant server.
* ~~__lcfserver-frontend-jaxrs-war__  Jaxrs framework~~ - _not implemented_


## Architecture

**Backend**

***Implementing a new data source***

You need to create new classes implementing the following interfaces:

***`EntitySourceInterface<E>`***

for each LCF entity type you will need to implement `EntitySourceInterface<E>` (e.g. `EntitySourceInterface<Patron>`). 

This should return the populated entity from the LMS, allow creation\modification of entities in the LMS, etc.

***`EntitySourcesInterface`***

This has a single function which should return an instance of the class implementing EntitySourceInterface for the given entity type

***`Configuration`***

This returns the ReferenceEditor and an instance of the above EntitySourcesInterface class.

You should put the name of the new Configuration class in  the file 

     META-INF/services/com.ceridwen.lcf.server.core.config.Configuration

***Default configuration***

By default, the server loads class `com.ceridwen.lcf.impl.hashmap.HashMapSourceConfiguration`

This returns the BasicReferenceEditor, and loads a HashMap based datasource which a number of filters. Some of these
can be used by a custom datasource but are designed for the reference memory datasource:

* `DefaultDataLoaderFilter()` loads a default set of data
* `PersistentFilter(file)`	adds snapshotting and loading the memory data to an xml file
* `ServerGeneratedIdFilter(class)` the default behaviour for creating a new entity that a client supplied id is used if supplied, otherwise one is generated. This filter will always generate a server id for the designated entity type
* `ClientGeneratedIdFilter(class)`	- the default behaviour for creating a new entity that a client supplied id is used if supplied, otherwise one is generated. This filter will return an error if no client supplied id is present
* `ReferentialIntegrityFilter`		- used the routines in the com.ceridwen.lcf.model.integrity to ensure parent\child relations      
* `LoanFilter`						- generates specific lcf-loan-response messages for loans				
* `PatronCountFieldsFilter`			- automatically calculate the (readonly) loan\reservation\... fields in a patron record
* `ReadOnlyFilter(class)`			- Forces the designated type to be readonly by rejecting create\update requests

------
**Core**
Part of the design of this framework was to work as closely to the classes generated by JAXB 
from the LCF schema as possible, so that updates to the scheme could be accommodated by the framework 
with minimal effort (ideally just regenerating the schema and recompiling the java).

There are two key packages for walking and modifying entities

**`com.ceridwen.lcf.server.core.referencing`**

Parent and child entities are referenced by "href" entries which are a URL to the REST service where the entity can be retrieved.
These will be of the form 

`http[s]://{server}[:{port}]/{path}/lcf/1.0/{entity}/{id}`

As the protocol, server, port and path will vary according to where the service is deployed, these are not easy to handle internally. 
The referencing classes allow these to be modified on incoming messages to any internal representation (dereferenced), and allow the internal 
representation to be converted back (referenced).

A class implementing the `ReferenceEditor` interface determines the conversion rules. Classes in the modifier subpackage determine how this is
applied to the various LCF schema generated classes.

BasicReferenceEditor implements the following dereferencing\referencing rules:

* dereference:
	
`http[s]://{server}[:{port}]/{path}/lcf/1.0/{entity}/{id} -> {id}`

* reference:
		
`{id -> http[s]://{server}[:{port}]/{path}/lcf/1.0/{entity}/{id}`


**`com.ceridwen.lcf.server.core.integrity`**

This controls handling parent\child relations - updating href's and if necessary deleting orphaned children on deletion of the parent

This can be overlaid on any datasource by applying the `ReferentialIntegrityFilter`, but it is likely an LMS will handle parent\child anyway

RelationshipFactory contains the definitions of the different parent\child relations in the LCF schema classes. It is also used for constructing\validating
urls of the form 

`http[s]:/{server}[:{port}]/{path}/lcf/1.0/{parent}/id/{child}`

------
**Frontend**

It includes three different frontend implementations:

***basic servlet***
To run servlet implementation:
```bash
mvn jetty:run -Dlcf.server.frontend=servlet
```

***org.restlet***
To run org.restlet implementation:
```bash
mvn jetty:run -Dlcf.server.frontend=restlet
```

To run org.restlet implementation with optional raml modules:
```bash
mvn jetty:run -Dlcf.server.frontend=restlet -Dlcf.server.frontend.module.raml
```

To run org.restlet implementation with optional swagger modules:
```bash
mvn jetty:run -Dlcf.server.frontend=restlet-Dlcf.server.frontend.module.swagger
```

To run org.restlet implementation with optional raml and swagger modules:
```bash
mvn jetty:run -Dlcf.server.frontend=restlet -Dlcf.server.frontend.module.raml -Dlcf.server.frontend.module.swagger
```

***~~jax-rs~~ [Not implemented]***
~~To run org.restlet implementation with optional raml and swagger modules:~~
~~```
mvn jetty:run -Dlcf.server.frontend=jaxrs
```~~

