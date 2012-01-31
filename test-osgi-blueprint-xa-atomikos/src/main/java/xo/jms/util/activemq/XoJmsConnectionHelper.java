/**
 * Copyright (c) 2012.
 */
package xo.jms.util.activemq;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Session;

import xo.jms.util.XoJmsException;

/**
 *
 */
@SuppressWarnings( "static-method" )
public class XoJmsConnectionHelper
{

    protected ConnectionFactory connectionFactory;

    protected boolean transactional;

    protected int timeout;

    public XoJmsConnectionHelper()
    {
        this.connectionFactory = null;
        this.transactional = false;
        this.timeout = 15000;
    }

    public javax.jms.Connection getJmsConnection()
    {
        javax.jms.Connection connection;

        if ( transactional )
        {
            connection = getTransactionalJmsConnection();
        }
        else
        {
            connection = getNonTransactionalJmsConnection();
        }

        return connection;
    }

    public javax.jms.Session getJmsSession( Connection connection )
    {
        javax.jms.Session session;

        if ( transactional )
        {
            session = getTransactionalJmsSession( connection );
        }
        else
        {
            session = getNonTransactionalJmsSession( connection );
        }

        return session;
    }



    protected Connection getNonTransactionalJmsConnection() throws XoJmsException
    {
        try
        {
            Connection connection = connectionFactory.createConnection();
            return connection;
        }
        catch ( Exception e )
        {
            throw new XoJmsException( e );
        }
    }

    protected Connection getTransactionalJmsConnection() throws XoJmsException
    {
        try
        {
            Connection connection = connectionFactory.createConnection();
            return connection;
        }
        catch ( Exception e )
        {
            throw new XoJmsException( e );
        }
    }


    protected Session getNonTransactionalJmsSession( Connection connection ) throws XoJmsException
    {
        try
        {
            Session session = connection.createSession( false, Session.AUTO_ACKNOWLEDGE );
            return session;
        }
        catch ( Exception e )
        {
            throw new XoJmsException( e );
        }
    }


    protected Session getTransactionalJmsSession( Connection connection ) throws XoJmsException
    {
        try
        {
            Session session;

//            BEWARE:  The commented out code below does NOT work.
//            if ( connection instanceof XAConnection )
//            {
//                session = ((XAConnection)connection).createXASession();
//
//                return session;
//            }
//            BEWARE:  The commented out code above does NOT work.

            session = connection.createSession( true, Session.SESSION_TRANSACTED );

            return session;
        }
        catch ( Exception e )
        {
            throw new XoJmsException( e );
        }
    }


    public void commitTransactionIfApplicable( Session session ) throws XoJmsException
    {

        try
        {
            if ( null != session && session.getTransacted() )
            {
                session.commit();
            }
        }
        catch ( Exception e )
        {
            throw new XoJmsException( e );
        }

    }

    public void rollbackTransactionIfApplicable( Session session ) throws XoJmsException
    {
        try
        {
            if ( null != session && session.getTransacted() )
            {
                session.rollback();
            }
        }
        catch ( JMSException e )
        {
            throw new XoJmsException( e );
        }

    }


    public void closeSession( Session session )
    {
        try
        {
            if ( session != null )
            {
                session.close();
            }
        }
        catch ( Exception e )
        {
            // ignore
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
     * @return the connectionFactory
     */
    public ConnectionFactory getConnectionFactory()
    {
        return connectionFactory;
    }

    /**
     *      *
     * @param connectionFactory
     */
    public void setConnectionFactory( ConnectionFactory connectionFactory )
    {
        this.connectionFactory = connectionFactory;
    }

    /**
     * @return the transactional
     */
    public boolean isTransactional()
    {
        return transactional;
    }

    /**
     *
     * @param transactional
     */
    public void setTransactional( boolean transactional )
    {
        this.transactional = transactional;
    }

    /**
     * @return the timeout
     */
    public int getTimeout()
    {
        return timeout;
    }

    /**
     *
     * @param timeout
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
        return "XoJmsConnectionHelper [connectionFactory=" + connectionFactory + ", transactional="
                + transactional + ", timeout=" + timeout + "]";
    }

}
