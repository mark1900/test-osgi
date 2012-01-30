===============================================================

Notes:

===============================================================

*
*

===============================================================

Project "test-osgi-blueprint-xa-atomikos" description

===============================================================

The OSGi Blueprint test configuration:

* test-osgi-blueprint-xa-atomikos\src\main\resources\OSGI-INF\blueprint


The main test executor:

* test-osgi-blueprint-xa-atomikos\src\main\java\test\xa\XaTestExecutor.java


The main test classes:

* test-osgi-blueprint-xa-atomikos\src\main\java\test\xa\camel\XaCamelTest.java
* test-osgi-blueprint-xa-atomikos\src\main\java\test\xa\service\XaServiceTest.java


Utility classes:

* test-osgi-blueprint-xa-atomikos\src\main\java\xo



===============================================================

To install into the ServiceMix 4.4.0 OSGi container:

===============================================================



1) Start ServiceMix
2) Run the following commands:

osgi:install -s mvn:servicemix.test/test-osgi-blueprint-xa-atomikos/0.0.1

## OR ##

osgi:install -s file:C:\\tmp\\test-osgi-blueprint-xa-atomikos-0.0.1.jar


3) Test

Start the bundle in ServiceMix and expect the OSGi bundle to load, then start executing tests.

On success, the following will be output:   "XaTestExecutor.run():  SUCCESS!"
On failure, the following will be output:   "XaTestExecutor.run():  FAILURE."


