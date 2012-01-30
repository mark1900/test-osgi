/**
 * Copyright (c) 2012.
 */
package xo.jms.util.activemq;

import junit.framework.Assert;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.Test;

/**
 *  Please ensure ServiceMix is running before running this test, as
 *  this test will only pass if an ActiveMq instance is already running.
 */
@SuppressWarnings( "static-method" )
public class XoJmsQueueHelperTest
{

    /**
     * @param connectionFactory
     */
    public XoJmsQueueHelperTest()
    {
        super();
    }



    @Test
    public void testMessageCommunication() throws Exception
    {
//        <bean id="jmsNonXaConnectionFactory" class="org.apache.activemq.ActiveMQConnectionFactory">
//            <property name="brokerURL" value="tcp://localhost:61616" />
//            <property name="redeliveryPolicy">
//                <bean class="org.apache.activemq.RedeliveryPolicy">
//                    <property name="maximumRedeliveries" value="0"/>
//                </bean>
//            </property>
//        </bean>

        XoJmsQueueHelper xoJmsQueueHelper = new XoJmsQueueHelper();

        {
            XoJmsConnectionHelper xoJmsConnectionHelper = new XoJmsConnectionHelper();
            {
                ActiveMQConnectionFactory jmsNonXaConnectionFactory = new ActiveMQConnectionFactory();
                jmsNonXaConnectionFactory.setBrokerURL( "tcp://localhost:61616" );

                xoJmsConnectionHelper.setConnectionFactory( jmsNonXaConnectionFactory );
            }
            xoJmsQueueHelper.setXoJmsConnectionHelper( xoJmsConnectionHelper );
            xoJmsQueueHelper.setTimeout( 15000 );
        }



        String theQueueName = "test-xa-service";
        String theMessage = "The test message";

        xoJmsQueueHelper.sendBody( theQueueName, theMessage );

//        String theReceivedMessage = xoJmsHelper.receiveBody( connection, session, theQueueName, 500L );

        String theReceivedMessage = xoJmsQueueHelper.receiveBody( theQueueName );

        Assert.assertEquals( theMessage, theReceivedMessage );

    }

}
