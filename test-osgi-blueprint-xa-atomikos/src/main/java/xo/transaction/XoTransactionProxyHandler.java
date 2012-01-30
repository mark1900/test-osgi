/**
 * Copyright (c) 2012.
 */
package xo.transaction;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    protected String targetObjectRegexMethodFilter;

    protected Pattern targetObjectRegexMethodFilterPattern;


    public XoTransactionProxyHandler()
    {
        standardTransactionManager = null;
        platformTransactionManager = null;

        propagationBehaviorName = "PROPAGATION_REQUIRED"; // TransactionDefinition.PROPAGATION_REQUIRED
        isolationLevelName = "ISOLATION_SERIALIZABLE";  // TransactionDefinition.ISOLATION_SERIALIZABLE

        timeout = -1;  // No timeout

        targetObject = null;

        setTargetObjectRegexMethodFilter( ".*" );

    }


    @Override
    public Object invoke( Object proxyObject, Method method, Object[] args ) throws Throwable
    {

        Object returnValue;

        Matcher matcher = targetObjectRegexMethodFilterPattern.matcher( method.getName() );

        if ( matcher.find() )
        {

            try
            {

                LOG.debug( "Begin Transaction Handling [" + this.toString() + "]" );

                if ( null != platformTransactionManager )
                {
                    returnValue = doTransactionWithPlatformTransactionManager( proxyObject, method, args );
                }
                else if ( null != standardTransactionManager )
                {
                    returnValue = doTransactionWithStandardTransactionManager( proxyObject, method, args );
                }
                else
                {
                    throw new IllegalArgumentException( "No Transaction Manager set" );
                }

            }
            finally
            {
                LOG.debug( "End Transaction Handling [" + this.toString() + "]" );
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

    public Object doTransactionWithPlatformTransactionManager(
        @SuppressWarnings( "unused" ) Object proxyObject, Method method, Object[] args )
    {

        LOG.debug( "doTransactionCommit() -> BEGIN" );

        Object returnValue;

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


        LOG.debug( "doTransactionCommit() <- END" );

        return returnValue;

    }

    public Object doTransactionWithStandardTransactionManager(
        @SuppressWarnings( "unused" ) Object proxyObject, Method method, Object[] args )
    {

        LOG.debug( "doTransactionCommit() -> BEGIN" );

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

        LOG.debug( "doTransactionCommit() <- END" );

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
     * @return the targetObjectRegexMethodFilter
     */
    public String getTargetObjectRegexMethodFilter()
    {
        return targetObjectRegexMethodFilter;
    }


    /**
     * @param targetObjectRegexMethodFilter the targetObjectRegexMethodFilter to set
     */
    public void setTargetObjectRegexMethodFilter( String targetObjectRegexMethodFilter )
    {
        this.targetObjectRegexMethodFilter = targetObjectRegexMethodFilter;
        targetObjectRegexMethodFilterPattern = Pattern.compile( targetObjectRegexMethodFilter, Pattern.DOTALL | Pattern.MULTILINE  );

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
                + timeout + ", targetObject=" + targetObject + ", targetObjectRegexMethodFilter="
                + targetObjectRegexMethodFilter + "]";
    }


}