/**
 * Copyright (c) 2012.
 */
package test.xa.camel;

import test.xa.XaTest;
import test.xa.XaTestPersistenceHelper;


/**
 *
 */
public class XaCamelTest implements XaTest
{

//    private static final transient Logger LOG = LoggerFactory.getLogger( XaCamelTest.class );

    public static final String theTableName = "test_xa_camel";
    public static final String theQueueName = "test-xa-camel";

    protected int testTimeoutValue = 3000;

    protected String [] xaCommitTestRouteNames;
    protected String [] xaRollbackTestRouteNames;

    protected XaTestPersistenceHelper xaTestPersistenceHelper;


    public XaCamelTest() throws Exception
    {

    }


    public void execute() throws Exception
    {


        String [] xaCommitTestRouteQueueNames = new String[ xaCommitTestRouteNames.length ];
        for ( int i = 0; i < xaCommitTestRouteNames.length; i++  )
        {
            xaCommitTestRouteQueueNames[i] = "test-" + xaCommitTestRouteNames[i];
        }

        String [] xaRollbackTestRouteQueueNames = new String[ xaRollbackTestRouteNames.length ];
        for ( int i = 0; i < xaRollbackTestRouteNames.length; i++  )
        {
            xaRollbackTestRouteQueueNames[i] = "test-" + xaRollbackTestRouteNames[i];
        }


        for ( int i = 0; i < xaCommitTestRouteQueueNames.length; i++ )
        {
            emptyTestTable( theTableName );
            emptyTestQueues( xaCommitTestRouteQueueNames );
            emptyTestQueues( xaRollbackTestRouteQueueNames );

            attemptTransactionAndExpectTransactionToCommit( theTableName, xaCommitTestRouteQueueNames[i] );
        }

        for ( int i = 0; i < xaRollbackTestRouteQueueNames.length; i++ )
        {
            emptyTestTable( theTableName );
            emptyTestQueues( xaCommitTestRouteQueueNames );
            emptyTestQueues( xaRollbackTestRouteQueueNames );

            attemptTransactionAndExpectTransactionToRollback( theTableName, xaRollbackTestRouteQueueNames[i] );
        }


        emptyTestTable( theTableName );
        emptyTestQueues( xaCommitTestRouteQueueNames );
        emptyTestQueues( xaRollbackTestRouteQueueNames );


    }


    protected void attemptTransactionAndExpectTransactionToRollback( String tableName, String queueName ) throws Exception
    {

        if ( 0 != xaTestPersistenceHelper.getDatabaseTableRowCount( tableName ) )
        {
            throw new RuntimeException("Incorrect database table row count.");
        }

        sendTestMessage( queueName );

        long stopWatch = System.currentTimeMillis();
        String dlq = null;
        do
        {
            dlq = xaTestPersistenceHelper.receiveMessageFromDeadLetterQueueIfItExists( queueName );
        } while ( null == dlq && System.currentTimeMillis() - stopWatch < testTimeoutValue );


        if ( null == dlq )
        {
            throw new RuntimeException( "Should not lose message message in queue." );
        }


        if ( 0 != xaTestPersistenceHelper.getDatabaseTableRowCount( tableName ) )
        {
            throw new RuntimeException("Incorrect database table row count.");
        }

    }

    protected void attemptTransactionAndExpectTransactionToCommit( String tableName, String queueName ) throws Exception
    {

        if ( 0 != xaTestPersistenceHelper.getDatabaseTableRowCount( tableName ) )
        {
            throw new RuntimeException("Incorrect database table row count.");
        }

        sendTestMessage( queueName );

        long stopWatch = System.currentTimeMillis();
        int thirdPartyTestDatabaseTableRowCount = 0;
        do
        {
            thirdPartyTestDatabaseTableRowCount = xaTestPersistenceHelper.getDatabaseTableRowCount( tableName );
        } while ( 0 == thirdPartyTestDatabaseTableRowCount
                && System.currentTimeMillis() - stopWatch < testTimeoutValue );

        if ( 1 != thirdPartyTestDatabaseTableRowCount )
        {
            throw new RuntimeException("Incorrect database table row count.");
        }


        String dlq = xaTestPersistenceHelper.receiveMessageFromDeadLetterQueueIfItExists( queueName );

        if ( null != dlq )
        {
            throw new RuntimeException( "Should not find message message in queue." );
        }
    }


    /*
     *
     * Helper Methods
     */

    protected void emptyTestTable( String tableName )
    {
        try
        {
            xaTestPersistenceHelper.jdbcUpdate( "drop table dbo." + tableName );
        }
        catch ( Exception e )
        {
            // ignore
        }
        xaTestPersistenceHelper.jdbcUpdate( "create table dbo." + tableName + " ( "
                + "thirdparty_id varchar(10), name varchar(128), created varchar(20), status_code varchar(3) )" );
    }


    protected void sendTestMessage( String queueName )
    {

        String xml =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                    + "<thirdparty id=\"123\"><name>THE TEST THIRDPARTY</name><date>201110140815</date><code>200</code></thirdparty>";
        xaTestPersistenceHelper.sendToQueue( queueName, xml );

    }


    protected void emptyTestQueues( String [] xaTestRouteNames )
    {

        String dlq = null;

        for ( String xaTestRouteNameArray : xaTestRouteNames )
        {
            do
            {
                dlq = xaTestPersistenceHelper.receiveMessageFromQueueIfItExists( xaTestRouteNameArray );
            } while ( null != dlq );

            do
            {
                dlq = xaTestPersistenceHelper.receiveMessageFromDeadLetterQueueIfItExists( xaTestRouteNameArray );
            } while ( null != dlq );
        }

    }



    /**
     * @return the xaCommitTestRouteNames
     */
    public String[] getXaCommitTestRouteNames()
    {
        return xaCommitTestRouteNames;
    }

    /**
     * @param xaCommitTestRouteNames the xaCommitTestRouteNames to set
     */
    public void setXaCommitTestRouteNames( String[] xaCommitTestRouteNames )
    {
        this.xaCommitTestRouteNames = xaCommitTestRouteNames;
    }

    /**
     * @return the xaRollbackTestRouteNames
     */
    public String[] getXaRollbackTestRouteNames()
    {
        return xaRollbackTestRouteNames;
    }

    /**
     * @param xaRollbackTestRouteNames the xaRollbackTestRouteNames to set
     */
    public void setXaRollbackTestRouteNames( String[] xaRollbackTestRouteNames )
    {
        this.xaRollbackTestRouteNames = xaRollbackTestRouteNames;
    }


    /**
     * @return the xaTestPersistenceHelper
     */
    public XaTestPersistenceHelper getPersistenceTestHelper()
    {
        return xaTestPersistenceHelper;
    }


    /**
     * @param xaTestPersistenceHelper the xaTestPersistenceHelper to set
     */
    public void setPersistenceTestHelper( XaTestPersistenceHelper xaTestPersistenceHelper )
    {
        this.xaTestPersistenceHelper = xaTestPersistenceHelper;
    }


    /**
     * @return the testTimeoutValue
     */
    public int getTestTimeoutValue()
    {
        return testTimeoutValue;
    }


    /**
     * @param testTimeoutValue the testTimeoutValue to set
     */
    public void setTestTimeoutValue( int testTimeoutValue )
    {
        this.testTimeoutValue = testTimeoutValue;
    }


}
