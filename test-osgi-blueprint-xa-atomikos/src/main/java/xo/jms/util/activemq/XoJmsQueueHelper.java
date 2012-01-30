/**
 * Copyright (c) 2012.
 */
package xo.jms.util.activemq;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.command.ActiveMQQueue;

import xo.jms.util.XoJmsException;
import xo.jms.util.XoJmsHelper;

/**
 *
 */
@SuppressWarnings( "static-method" )
public class XoJmsQueueHelper extends XoJmsHelper
{

    public static final int DEFAULT_TIMEOUT = 15000;

    protected XoJmsConnectionHelper xoJmsConnectionHelper;

    protected int timeout;

    /**
     * @param connectionFactory
     */
    public XoJmsQueueHelper()
    {
        this.xoJmsConnectionHelper = null;
        timeout = DEFAULT_TIMEOUT;
    }


    /**
     * Send JMS message text to relevant queue.
     *
     * @param theQueueName
     * @param theMessage
     */
    public void sendBody( String theQueueName, String theMessage )
    {

        Connection connection = null;
        Session session = null;
        Destination destination = null;
        MessageProducer messageProducer = null;

        try
        {
            connection = xoJmsConnectionHelper.getJmsConnection();

            // Starts (or restarts) a connection's delivery of incoming messages
            // A call to start on a connection that has already been started is ignored.
            connection.start();

            session = xoJmsConnectionHelper.getJmsSession( connection );

            destination = new ActiveMQQueue( theQueueName );
            messageProducer = session.createProducer( destination );

            TextMessage jmsMessage = session.createTextMessage( theMessage );
            messageProducer.send( jmsMessage );

        }
        catch ( JMSException e )
        {
            throw new XoJmsException( e );
        }
        finally
        {
            closeMessageProducer( messageProducer );
            xoJmsConnectionHelper.closeSession( session );
            xoJmsConnectionHelper.closeConnection( connection );
        }

    }


    /**
     * Receive JMS message text from relevant queue.
     *
     * @param theQueueName
     * @param theMessage
     */
    public String receiveBody( String theQueueName )
    {
        Connection connection = null;
        Session session = null;
        Destination destination = null;
        MessageConsumer messageConsumer = null;

        try
        {
            connection = xoJmsConnectionHelper.getJmsConnection();

            // Starts (or restarts) a connection's delivery of incoming messages
            // A call to start on a connection that has already been started is ignored.
            connection.start();

            session = xoJmsConnectionHelper.getJmsSession( connection );

            destination = new ActiveMQQueue( theQueueName );
            MessageConsumer consumer = session.createConsumer( destination );

            Message theMessage = null;

            if ( timeout == 0 )
            {

                /*
                    -- Be careful --

                    http://mail-archives.apache.org/mod_mbox/activemq-users/200608.mbox
                        /%3Cec6e67fd0608180116n2568496bjea9f3fdc0651df19@mail.gmail.com%3E

                    James Strachan <james.strachan@...> writes:

                    Note that in ActiveMQ receiveNoWait() really and truly is that - we
                    don't wait a single millisecond or request-response communication with
                    the broker - if there is a message available it is returned without
                    any delay.

                    Some JMS providers interpret receiveNoWait() as a 'poll the broker and
                    see if there is a message available and if so fetch it'.
                */
                theMessage = consumer.receiveNoWait();
            }
            else if ( timeout == -1  )
            {
                theMessage = consumer.receive();
            }
            else
            {
                theMessage = consumer.receive( timeout );
            }

            if ( null == theMessage )
            {
                return null;
            }
            else if ( theMessage instanceof TextMessage )
            {
                return ( (TextMessage)theMessage ).getText();
            }
            else
            {
                return theMessage.toString();
            }

        }
        catch ( JMSException e )
        {
            throw new XoJmsException( e );
        }
        finally
        {
            closeMessageConsumer( messageConsumer );
            xoJmsConnectionHelper.closeSession( session );
            xoJmsConnectionHelper.closeConnection( connection );
        }
    }


    /**
     * Close resource if applicable.
     *
     * @param messageProducer
     */
    public void closeMessageProducer( MessageProducer messageProducer )
    {
        try
        {
            if ( messageProducer != null )
            {
                messageProducer.close();
            }
        }
        catch ( Exception e )
        {
            // ignore
        }
    }

    /**
     * Close resource if applicable.
     *
     * @param messageConsumer
     */
    public void closeMessageConsumer( MessageConsumer messageConsumer )
    {
        try
        {
            if ( messageConsumer != null )
            {
                messageConsumer.close();
            }
        }
        catch ( Exception e )
        {
            // ignore
        }
    }


    /**
     * @return the xoJmsConnectionHelper
     */
    public XoJmsConnectionHelper getXoJmsConnectionHelper()
    {
        return xoJmsConnectionHelper;
    }


    /**
     *
     * @param xoJmsConnectionHelper
     */
    public void setXoJmsConnectionHelper( XoJmsConnectionHelper xoJmsConnectionHelper )
    {
        this.xoJmsConnectionHelper = xoJmsConnectionHelper;
        if ( null != xoJmsConnectionHelper )
        {
            xoJmsConnectionHelper.setTimeout( timeout );
        }
    }


    /**
     *
     * @return
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
        if ( null != xoJmsConnectionHelper )
        {
            xoJmsConnectionHelper.setTimeout( timeout );
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        return "XoJmsQueueHelper [xoJmsConnectionHelper=" + xoJmsConnectionHelper + ", timeout=" + timeout
                + "]";
    }


}
