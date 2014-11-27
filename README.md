Hermes
======

Hermes is a web application developed with Grails. Grails is a web framework specialized for rapid-web-development and based on established technologies like Spring and Hibernate. A lightweight development is promoted by using the JVM script language Groovy and by making massive use of the covention-over-configuration paradigma.

The Job and Node class are the central classes of Hermes. A Job object is the representation of a job configuration, it consist of a malware sample and a sensor, as well as the virtual machine that should be used to run this job. It is also possible, to configure additional metadataq, such as priority, simulated time or earliest publishing time. The Node objects represent the registered cuckoo nodes.

The user interface of Hermes is a custom developed content management system (CMS). The CMS is splitted into security-, information, administration- and api-component. The administration-component is used for CRUD operations on the domain objects. The security-component manages Role-Based-Access-Control (RBAC).

It is also possible to monitor the cuckoo nodes and their status. This functionality is realised by the information-component. It shows the node cpu usage as well as the status of the current job.
