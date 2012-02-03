Nexus Jar Reader Plugin
=======================

Nexus Jar Reader Plugin is released with GPL v2 license (http://www.gnu.org/licenses/gpl.html)

This plugin works with Nexus 1.9 and above. 
This plugin able to read any content of jar and applicable to Hosted Repository, Proxy Repository and Group Repository.
When you try to browse content of jar in Group Repository or Proxy Repository, if the jar is not exists in local storage,
it will automatic download the jar from remote storage.

Syntax for read content of jar.
append exclamation mark ( ! ) after the jar want you read.
Eg:
Reading readme.txt in hibernate-jpa-2.0-api-1.0.1.Final.jar
http://localhost:8080/nexus/content/groups/public/org/hibernate/javax/persistence/hibernate-jpa-2.0-api/1.0.1.Final/hibernate-jpa-2.0-api-1.0.1.Final.jar!/readme.txt