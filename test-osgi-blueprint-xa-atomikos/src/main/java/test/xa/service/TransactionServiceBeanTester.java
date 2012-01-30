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
public class TransactionServiceBeanTester
{

    private static final transient Logger LOG = LoggerFactory.getLogger( TransactionServiceBeanImpl.class );

    protected XaTestPersistenceHelper xaTestPersistenceHelper;


    public void prepare()
    {
        emptyTable( TransactionServiceBeanImpl.theTableName );
        emptyQueue( TransactionServiceBeanImpl.theQueueName );

        if ( 0 != xaTestPersistenceHelper.getDatabaseTableRowCount( TransactionServiceBeanImpl.theTableName ) )
        {
            throw new RuntimeException("Incorrect database table row count.");
        }

        if ( null != xaTestPersistenceHelper.receiveMessageFromQueueIfItExists( TransactionServiceBeanImpl.theQueueName ) )
        {
            throw new RuntimeException( "Should not find message message in queue." );
        }

        if ( null != xaTestPersistenceHelper.receiveMessageFromDeadLetterQueueIfItExists( TransactionServiceBeanImpl.theQueueName ) )
        {
            throw new RuntimeException( "Should not find message message in queue." );
        }

    }

    public void checkTransactionAndExpectTransactionCommit() throws Exception
    {
        LOG.info( "checkTransactionAndExpectTransactionCommit()" );

        if ( 1 != xaTestPersistenceHelper.getDatabaseTableRowCount( TransactionServiceBeanImpl.theTableName ) )
        {
            throw new RuntimeException("Incorrect database table row count.");
        }


        String dlq = xaTestPersistenceHelper.receiveMessageFromQueueIfItExists( TransactionServiceBeanImpl.theQueueName );

        if ( null == dlq )
        {
            throw new RuntimeException( "Should find message message in queue." );
        }
    }


    public void checkTransactionAndExpectTransactionRollback() throws Exception
    {
        LOG.info( "checkTransactionAndExpectTransactionRollback()" );

        if ( 0 != xaTestPersistenceHelper.getDatabaseTableRowCount( TransactionServiceBeanImpl.theTableName ) )
        {
            throw new RuntimeException("Incorrect database table row count.");
        }

        if ( null != xaTestPersistenceHelper.receiveMessageFromQueueIfItExists( TransactionServiceBeanImpl.theQueueName ) )
        {
            throw new RuntimeException( "Should not find message message in queue." );
        }

        if ( null != xaTestPersistenceHelper.receiveMessageFromDeadLetterQueueIfItExists( TransactionServiceBeanImpl.theQueueName ) )
        {
            throw new RuntimeException( "Should not find message message in queue." );
        }

    }

    private void emptyTable( String theTableName )
    {

        String sql = null;

        try
        {
            sql = "drop table dbo." + theTableName;
            xaTestPersistenceHelper.jdbcUpdate( sql );
        }
        catch ( Exception e )
        {
            // ignore
//            LOG.error( "Failed to drop database table:  " + theTableName
//                       + " Sql [" + sql + "]", e );

        }

        try
        {
            sql =  "create table dbo." + theTableName + " ( "
                    + "timestamp [numeric](20, 0), message varchar(128) )";
            xaTestPersistenceHelper.jdbcUpdate( sql );
        }
        catch ( Exception e )
        {
            LOG.error( "Failed to create database table:  " + theTableName
                       + " Sql [" + sql + "]", e );
            throw new RuntimeException( e );
        }

        if ( 0 != xaTestPersistenceHelper.getDatabaseTableRowCount( TransactionServiceBeanImpl.theTableName ) )
        {
            throw new RuntimeException("Incorrect database table row count.");
        }


    }

    private void emptyQueue( String theQueueName )
    {

        String dlq = null;

        do
        {
            dlq = xaTestPersistenceHelper.receiveMessageFromQueueIfItExists( theQueueName );
        } while ( null != dlq );

        do
        {
            dlq = xaTestPersistenceHelper.receiveMessageFromDeadLetterQueueIfItExists( theQueueName );
        } while ( null != dlq );

    }

    /**
     * @return the xaTestPersistenceHelper
     */
    public XaTestPersistenceHelper getPersistenceHelper()
    {
        return xaTestPersistenceHelper;
    }

    /**
     * @param xaTestPersistenceHelper the xaTestPersistenceHelper to set
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
        return "TransactionServiceBeanTester [xaTestPersistenceHelper=" + xaTestPersistenceHelper + "]";
    }



}
