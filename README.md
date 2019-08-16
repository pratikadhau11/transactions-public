**Task to run**
* **./gradlew clean build** 
   * _run all tests_
* **./gradlew fatCapsule** 
    * _create executable_
* **java -jar build/libs/accounts-capsule.jar db migrate application.yml** 
    * _create data base_
* **java -jar build/libs/accounts-capsule.jar server application.yml**
    * _run application_


**Scenario tests are under** 
   * _src/test/java/integration_
