/**
 * Copyright (c) 2012.
 */
package xo.transaction;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;


/**
 *
 */
public class XoProxyFactory
{


    public XoProxyFactory()
    {

    }

    /**
     * {@inheritDoc}
     */
    public static <T> T createProxy( String proxyInterface, InvocationHandler proxyInvocationHandler ) throws Exception
    {
        return createProxy( new String[]{ proxyInterface }, proxyInvocationHandler );
    }


    /**
     * {@inheritDoc}
     *
     *
     */
    @SuppressWarnings( { "unchecked" } )
    public static <T> T createProxy( String [] proxyInterfaces, InvocationHandler proxyInvocationHandler ) throws Exception
    {

        Class<?> [] proxyinterfaceClasses = new Class<?>[proxyInterfaces.length];

        for ( int i = 0; i < proxyInterfaces.length; i++ )
        {
            proxyinterfaceClasses[i] = Class.forName( proxyInterfaces[i] );

            if ( !proxyinterfaceClasses[i].isInterface() )
            {
                throw new IllegalArgumentException( "Not an interface type:  " + proxyInterfaces[i] );
            }
        }

        try
        {

            return (T)Proxy.newProxyInstance( XoProxyFactory.class.getClassLoader(),
                                              proxyinterfaceClasses, proxyInvocationHandler );
        }
        catch ( Exception e )
        {
            return (T)Proxy.newProxyInstance( Thread.currentThread().getContextClassLoader(),
                                              proxyinterfaceClasses, proxyInvocationHandler );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        return "XoProxyFactory [toString()=" + super.toString() + "]";
    }

}
