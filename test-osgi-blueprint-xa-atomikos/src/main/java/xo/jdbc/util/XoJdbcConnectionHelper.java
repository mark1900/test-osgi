/**
 * Copyright (c) 2012.
 */
package xo.jdbc.util;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

/**
 *
 */
@SuppressWarnings( "static-method" )
public class XoJdbcConnectionHelper
{

    public static final int DEFAULT_TIMEOUT = 15000;

    protected DataSource dataSource;

    protected boolean transactional;
    protected int transactionIsolationLevel;

    protected int timeout;


    public XoJdbcConnectionHelper()
    {
        this.dataSource = null;
        this.timeout = DEFAULT_TIMEOUT;

        this.transactional = false;
        transactionIsolationLevel = java.sql.Connection.TRANSACTION_NONE;
    }

    public java.sql.Connection getDatabaseConnection()
    {
        java.sql.Connection connection;

        if ( transactional )
        {
            connection = getTransactionalDatabaseConnection( transactionIsolationLevel );
        }
        else
        {
            connection = getNonTransactionalDatabaseConnection();
        }

        return connection;
    }


    protected Connection getNonTransactionalDatabaseConnection()
    {
        try
        {
            return getNonTransactionalDatabaseConnection( dataSource );
        }
        catch ( Exception e )
        {
            throw new XoJdbcException( e );
        }

    }

    protected Connection getNonTransactionalDatabaseConnection( DataSource theDataSource )
    {
        Connection connection;
        try
        {
            connection = theDataSource.getConnection();
            connection.setAutoCommit( true );
            connection.setTransactionIsolation( Connection.TRANSACTION_NONE );
        }
        catch ( SQLException e )
        {
            throw new XoJdbcException( e );
        }

        return connection;
    }



    protected Connection getTransactionalDatabaseConnection(  int theTransactionIsolationLevel )
    {
        try
        {
            return getTransactionalDatabaseConnection( dataSource, theTransactionIsolationLevel );
        }
        catch ( Exception e )
        {
            throw new XoJdbcException( e );
        }

    }

    protected Connection getTransactionalDatabaseConnection( DataSource theDataSource, int theTransactionIsolationLevel )
    {
        Connection connection;
        try
        {
            connection = theDataSource.getConnection();
            connection.setAutoCommit( false );
            connection.setTransactionIsolation( theTransactionIsolationLevel );
        }
        catch ( SQLException e )
        {
            throw new XoJdbcException( e );
        }

        return connection;
    }

    public void commitTransactionIfApplicable( Connection connection ) throws XoJdbcException
    {

        try
        {
            if ( null != connection && connection.getTransactionIsolation() != Connection.TRANSACTION_NONE )
            {
                connection.commit();
            }
        }
        catch ( Exception e )
        {
            throw new XoJdbcException( e );
        }

    }

    public void rollbackTransactionIfApplicable( Connection connection ) throws XoJdbcException
    {
        try
        {
            if ( null != connection && connection.getTransactionIsolation() != Connection.TRANSACTION_NONE )
            {
                connection.rollback();
            }
        }
        catch ( Exception e )
        {
            throw new XoJdbcException( e );
        }

    }

    public void closeConnection( Connection connection )
    {
        try
        {
            if ( connection != null )
            {
                connection.close();
            }
        }
        catch ( Exception e )
        {
            // ignore
        }
    }

    /**
     * @return the dataSource
     */
    public DataSource getDataSource()
    {
        return dataSource;
    }

    /**
     * @param dataSource the dataSource to set
     */
    public void setDataSource( DataSource dataSource )
    {
        this.dataSource = dataSource;
    }

    /**
     * @return the transactional
     */
    public boolean isTransactional()
    {
        return transactional;
    }

    /**
     * @param transactional the transactional to set
     */
    public void setTransactional( boolean transactional )
    {
        this.transactional = transactional;
    }

    /**
     * @return the transactionIsolationLevel
     */
    public int getTransactionIsolationLevel()
    {
        return transactionIsolationLevel;
    }

    /**
     * @param transactionIsolationLevel the transactionIsolationLevel to set
     */
    public void setTransactionIsolationLevel( int transactionIsolationLevel )
    {
        this.transactionIsolationLevel = transactionIsolationLevel;
    }

    /**
     * @return the timeout
     */
    public int getTimeout()
    {
        return timeout;
    }

    /**
     * @param timeout the timeout to set
     */
    public void setTimeout( int timeout )
    {
        this.timeout = timeout;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        return "XoJdbcConnectionHelper [dataSource=" + dataSource + ", transactional=" + transactional
                + ", transactionIsolationLevel=" + transactionIsolationLevel + ", timeout=" + timeout + "]";
    }


}
