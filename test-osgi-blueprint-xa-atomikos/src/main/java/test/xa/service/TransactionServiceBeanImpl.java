/**
 * Copyright (c) 2012.
 */
package test.xa.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import test.xa.XaTestPersistenceHelper;

/**
 *
 */
public class TransactionServiceBeanImpl implements TransactionServiceBean
{

    private static final transient Logger LOG = LoggerFactory.getLogger( TransactionServiceBeanImpl.class );

    public static final String theTableName = "test_xa_service";
    public static final String theQueueName = "test-xa-service";

    public String currentQueueTestMessage;
    public Long currentTableTestTimestamp;
    public String currentTableTestMessage;

    protected XaTestPersistenceHelper xaTestPersistenceHelper;


    public TransactionServiceBeanImpl()
    {

    }

    /**
     * {@inheritDoc}
     */
    public void doTransactionCommit()
    {

        LOG.info( "doTransactionCommit() -> BEGIN" );

        createJmsMessageAndDatabaseRow();

        LOG.info( "doTransactionCommit() <- END" );


    }



    /**
     * {@inheritDoc}
     */
    public void doTransactionRollback()
    {

        LOG.info( "doTransactionRollback() -> BEGIN" );

        createJmsMessageAndDatabaseRow();

        if ( "1".equals( "1" ) )
        {
            throw new RuntimeException("Rollback on Exception");
        }

        LOG.info( "doTransactionRollback() <- END" );


    }


    protected void createJmsMessageAndDatabaseRow()
    {

        currentQueueTestMessage = "This is a test message";
        currentTableTestTimestamp = System.currentTimeMillis();
        currentTableTestMessage = "This is access entry";


        xaTestPersistenceHelper.sendToQueue( theQueueName, currentQueueTestMessage );

        StringBuilder sqlForAuditLogInsert = new StringBuilder();

        sqlForAuditLogInsert.append( "INSERT INTO dbo." + theTableName + " ("
                + "[TIMESTAMP],[MESSAGE]) VALUES"
                + "( ?, ? )" );

        int rowsUpdated = xaTestPersistenceHelper.jdbcUpdate( sqlForAuditLogInsert.toString(),
                         new Object[] { currentTableTestTimestamp, currentTableTestMessage,
                                 } );

        int databaseRowCount = xaTestPersistenceHelper.getDatabaseTableRowCount( TransactionServiceBeanImpl.theTableName );

        if ( rowsUpdated != 1  || databaseRowCount != 1  )
        {
            LOG.error( "Failed to insert record into database."
                    + "Sql [" + sqlForAuditLogInsert.toString()
                    .replaceFirst( "\\?", String.valueOf( currentTableTestTimestamp )  )
                    .replaceFirst( "\\?", "'" + String.valueOf( currentTableTestMessage ) + "'" )
                    + "]"
                    + "  rowsUpdated:  [" + rowsUpdated + "]");

            throw new RuntimeException("Incorrect database table row count.");
        }

    }


    /**
     * @return the xaTestPersistenceHelper
     */
    public XaTestPersistenceHelper getPersistenceHelper()
    {
        return xaTestPersistenceHelper;
    }


    /**
     *
     * @param xaTestPersistenceHelper
     */
    public void setPersistenceHelper( XaTestPersistenceHelper xaTestPersistenceHelper )
    {
        this.xaTestPersistenceHelper = xaTestPersistenceHelper;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        return "TransactionServiceBeanImpl [currentQueueTestMessage=" + currentQueueTestMessage
                + ", currentTableTestTimestamp=" + currentTableTestTimestamp + ", currentTableTestMessage="
                + currentTableTestMessage + ", xaTestPersistenceHelper=" + xaTestPersistenceHelper + "]";
    }


}
