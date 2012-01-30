/**
 * Copyright (c) 2012.
 */
package xo.transaction;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

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

    protected String propagationBehaviorName;
    protected String isolationLevelName;
    protected int timeout;

    protected Object targetObject;


    public XoTransactionProxyHandler()
    {
        standardTransactionManager = null;
        platformTransactionManager = null;

        propagationBehaviorName = "PROPAGATION_REQUIRED"; // TransactionDefinition.PROPAGATION_REQUIRED
        isolationLevelName = "ISOLATION_SERIALIZABLE";  // TransactionDefinition.ISOLATION_SERIALIZABLE

        timeout = -1;  // No timeout

        targetObject = null;
    }


    @Override
    public Object invoke( Object proxyObject, Method method, Object[] args ) throws Throwable
    {

        LOG.info( "Begin XoTransactionProxyHandler [" + this.toString() + "]" );

        try
        {

            if ( null != platformTransactionManager )
            {
                doTransactionWithPlatformTransactionManager( proxyObject, method, args );
            }
            else
            {
                doTransactionWithStandardTransactionManager( proxyObject, method, args );
            }

        }
        finally
        {
            LOG.info( "End XoTransactionProxyHandler [" + this.toString() + "]" );
        }

        return null;

    }

    public void doTransactionWithPlatformTransactionManager(
        @SuppressWarnings( "unused" ) Object proxyObject, Method method, Object[] args )
    {

        LOG.info( "doTransactionCommit() -> BEGIN" );




        TransactionStatus transactionStatus;

        try
        {
            TransactionDefinition platformTransactionDefinition;
            {
                DefaultTransactionDefinition defaultTransactionDefinition = new DefaultTransactionDefinition();
                defaultTransactionDefinition.setPropagationBehaviorName( propagationBehaviorName );
                defaultTransactionDefinition.setIsolationLevelName( isolationLevelName );
                defaultTransactionDefinition.setTimeout( timeout );

                platformTransactionDefinition = defaultTransactionDefinition;
            }


            transactionStatus = platformTransactionManager.getTransaction( platformTransactionDefinition );

        }
        catch ( Exception e )
        {
            LOG.error( "Transaction failed:  ", e );
            return;
        }

        try
        {

            method.invoke( this.targetObject, args );
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

        LOG.info( "doTransactionCommit() <- END" );

    }

    public void doTransactionWithStandardTransactionManager(
        @SuppressWarnings( "unused" ) Object proxyObject, Method method, Object[] args )
    {

        LOG.info( "doTransactionCommit() -> BEGIN" );

        try
        {
            standardTransactionManager.begin();

            method.invoke( this.targetObject, args );
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

        LOG.info( "doTransactionCommit() <- END" );

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
     * @return the propagationBehaviorName
     */
    public String getPropagationBehaviorName()
    {
        return propagationBehaviorName;
    }


    /**
     * @param propagationBehaviorName the propagationBehaviorName to set
     */
    public void setPropagationBehaviorName( String propagationBehaviorName )
    {
        this.propagationBehaviorName = propagationBehaviorName;
    }


    /**
     * @return the isolationLevelName
     */
    public String getIsolationLevelName()
    {
        return isolationLevelName;
    }


    /**
     * @param isolationLevelName the isolationLevelName to set
     */
    public void setIsolationLevelName( String isolationLevelName )
    {
        this.isolationLevelName = isolationLevelName;
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
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        return "XoTransactionProxyHandler [standardTransactionManager=" + standardTransactionManager
                + ", platformTransactionManager=" + platformTransactionManager + ", propagationBehaviorName="
                + propagationBehaviorName + ", isolationLevelName=" + isolationLevelName + ", timeout="
                + timeout + ", targetObject=" + targetObject + "]";
    }

}