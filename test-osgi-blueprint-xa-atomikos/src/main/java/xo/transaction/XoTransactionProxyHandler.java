/**
 * Copyright (c) 2012.
 */
package xo.transaction;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Properties;

import javax.transaction.TransactionManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;


public class XoTransactionProxyHandler implements InvocationHandler
{

    private static final transient Logger LOG = LoggerFactory.getLogger( XoTransactionProxyHandler.class );

    protected TransactionManager standardTransactionManager;

    protected PlatformTransactionManager platformTransactionManager;

    protected Object targetObject;


    protected Properties transactionHandlerProperties;
    protected List<XoTransactionMapping> transactionHandlerMappings;

    boolean initialized;

    public XoTransactionProxyHandler()
    {
        standardTransactionManager = null;
        platformTransactionManager = null;

        targetObject = null;

        initialized = false;
    }

    protected synchronized void initialize()
    {
        if ( initialized )
        {
            initialized = true;
            return;
        }

        transactionHandlerMappings = XoTransactionMapping.parseProperties( transactionHandlerProperties );


        if ( LOG.isDebugEnabled() )
        {
            LOG.debug( "XoTransactionProxyHandler initialized."
                    + "  " + this.toString()
                    );

        }

    }



    protected XoTransactionMapping getTransactionHandlerMapping( Method method )
    {
        for ( XoTransactionMapping transactionHandlerMapping : transactionHandlerMappings )
        {
            if ( transactionHandlerMapping.isMatch( method.getName() ) )
            {
                return transactionHandlerMapping;
            }
        }

        return null;
    }


    /**
     * Handle Transactions!
     *
     *
     * {@inheritDoc}
     */
    public Object invoke( Object proxyObject, Method method, Object[] args ) throws Throwable
    {

        initialize();

        Object returnValue;

        XoTransactionMapping xoTransactionMapping = getTransactionHandlerMapping( method );

        if ( null != xoTransactionMapping )
        {

            Throwable thrownObject = null;
            try
            {
                if ( LOG.isDebugEnabled() )
                {
                    LOG.debug( "Begin Transaction Handling"
                            + " - " + targetObject.getClass().getName() + "." + method.getName() + "(...)"
                            + " -> " + xoTransactionMapping.toString() );

                }

                if ( null != platformTransactionManager )
                {
                    returnValue = doTransactionWithPlatformTransactionManager( xoTransactionMapping, proxyObject, method, args );
                }
                else if ( null != standardTransactionManager )
                {
                    returnValue = doTransactionWithStandardTransactionManager( xoTransactionMapping, proxyObject, method, args );
                }
                else
                {
                    throw new IllegalArgumentException( "No Transaction Manager set" );
                }

            }
            catch ( Throwable t )
            {
                thrownObject = t;
                throw t;
            }
            finally
            {
                if ( null == thrownObject )
                {
                    LOG.debug( "End Transaction Handling with Success." );
                }
                else
                {
                    LOG.debug( "End Transaction Handling with object thrown:  "
                               + thrownObject.getClass().getName()
                               + "  [" + thrownObject.getMessage() + "]" );
                }
            }

        }
        else
        {
            LOG.debug( "Skipping Transaction Handling for "
                       + "[" + this.targetObject.getClass().getName() + "." + method.getName() + "]" );
            returnValue = method.invoke( this.targetObject, args );
        }

        return returnValue;

    }

    protected Object doTransactionWithPlatformTransactionManager(
        XoTransactionMapping xoTransactionMapping,
        @SuppressWarnings( "unused" ) Object proxyObject, Method method, Object[] args )
    {

        Object returnValue;

        TransactionStatus transactionStatus;

        try
        {
            TransactionDefinition platformTransactionDefinition;
            {
                DefaultTransactionDefinition defaultTransactionDefinition = new DefaultTransactionDefinition();
                defaultTransactionDefinition.setPropagationBehaviorName( xoTransactionMapping.getPropagationBehaviorName() );
                defaultTransactionDefinition.setIsolationLevelName( xoTransactionMapping.getIsolationLevelName() );
                defaultTransactionDefinition.setReadOnly( xoTransactionMapping.isReadOnly() );
                defaultTransactionDefinition.setTimeout( xoTransactionMapping.getTimeout() );

                platformTransactionDefinition = defaultTransactionDefinition;
            }


            transactionStatus = platformTransactionManager.getTransaction( platformTransactionDefinition );

        }
        catch ( Exception e )
        {
            LOG.error( "Transaction failed:  ", e );
            throw new RuntimeException( e );
        }

        try
        {

            returnValue = method.invoke( this.targetObject, args );
            platformTransactionManager.commit( transactionStatus );

        }
        catch ( Exception e1 )
        {
            LOG.error( "Commit failed:  ", e1 );

            try
            {
                platformTransactionManager.rollback( transactionStatus );
            }
            catch ( Exception e2 )
            {
                LOG.error( "Rollback failed:  ", e2 );
                throw new RuntimeException( e2 );
            }

            throw new RuntimeException( e1 );
        }

        return returnValue;

    }

    protected Object doTransactionWithStandardTransactionManager(
        @SuppressWarnings( "unused" ) XoTransactionMapping xoTransactionMapping,
        @SuppressWarnings( "unused" ) Object proxyObject, Method method, Object[] args )
    {

        Object returnValue;

        try
        {
            standardTransactionManager.begin();

            returnValue = method.invoke( this.targetObject, args );
            standardTransactionManager.commit();

        }
        catch ( Exception e1 )
        {
            LOG.error( "Commit failed:  ", e1 );

            try
            {
                standardTransactionManager.rollback();
            }
            catch ( Exception e2 )
            {
                LOG.error( "Rollback failed:  ", e2 );
                throw new RuntimeException( e2 );
            }

            throw new RuntimeException( e1 );
        }

        return returnValue;

    }


    /**
     * @return the standardTransactionManager
     */
    public TransactionManager getStandardTransactionManager()
    {
        return standardTransactionManager;
    }


    /**
     * @param standardTransactionManager the standardTransactionManager to set
     */
    public void setStandardTransactionManager( TransactionManager standardTransactionManager )
    {
        this.standardTransactionManager = standardTransactionManager;
    }


    /**
     * @return the platformTransactionManager
     */
    public PlatformTransactionManager getPlatformTransactionManager()
    {
        return platformTransactionManager;
    }


    /**
     * @param platformTransactionManager the platformTransactionManager to set
     */
    public void setPlatformTransactionManager( PlatformTransactionManager platformTransactionManager )
    {
        this.platformTransactionManager = platformTransactionManager;
    }

    /**
     * @return the targetObject
     */
    public Object getTargetObject()
    {
        return targetObject;
    }


    /**
     * @param targetObject the targetObject to set
     */
    public void setTargetObject( Object targetObject )
    {
        this.targetObject = targetObject;
    }



    /**
     * @return the transactionHandlerProperties
     */
    public Properties getTransactionHandlerProperties()
    {
        return transactionHandlerProperties;
    }

    /**
     * @param transactionHandlerProperties the transactionHandlerProperties to set
     */
    public void setTransactionHandlerProperties( Properties transactionHandlerProperties )
    {
        this.transactionHandlerProperties = transactionHandlerProperties;
    }





    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        return "XoTransactionProxyHandler [standardTransactionManager=" + standardTransactionManager
                + ", platformTransactionManager=" + platformTransactionManager + ", targetObject="
                + targetObject + ", transactionHandlerProperties=" + transactionHandlerProperties
                + ", transactionHandlerMappings=" + transactionHandlerMappings + "]";
    }




}