/**
 * Copyright (c) 2012.
 */
package xo.transaction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XoTransactionMapping
{
    protected final String [] standardObjectClassMethodNames = {
            "clone", "equals", "finalize", "getClass", "hashCode", "notify", "notifyAll", "toString", "wait"
    };


    protected String methodFilter;
    protected Pattern methodFilterRegexPattern;

    protected String propagationBehaviorName;
    protected String isolationLevelName;
    protected boolean isReadOnly;
    protected int timeout;


    public XoTransactionMapping()
    {
        methodFilter = ".*";

        propagationBehaviorName = "PROPAGATION_REQUIRED";
        isolationLevelName = "ISOLATION_DEFAULT";
        isReadOnly = false;
        timeout = -1;
    }

    public boolean isMatch( String method )
    {

        if ( null == methodFilterRegexPattern )
        {

            if ( methodFilter.equals( method ) )
            {
                return true;
            }

            return false;
        }
        else if ( ".*".equals( methodFilter ) )
        {
            // The methodRegexFilter will NOT match these stand method names,
            // unless they are explicitly defined.
            for ( int i = 0; i < standardObjectClassMethodNames.length; i++ )
            {
                if ( standardObjectClassMethodNames[i].equals( method ) )
                {
                    if ( !standardObjectClassMethodNames[i].equals( methodFilter ) )
                    {
                        return false;
                    }
                }
            }

        }

        Matcher matcher = methodFilterRegexPattern.matcher( method );
        return matcher.find();

    }



    /**
     * @return the methodFilter
     */
    public String getMethodFilter()
    {
        return methodFilter;
    }

    /**
     * @param methodFilter the methodFilter to set
     */
    public void setMethodFilter( String methodFilter )
    {
        this.methodFilter = methodFilter;

        if ( methodFilter.matches( "\\w+" ) )
        {
            this.methodFilterRegexPattern = null;
        }
        else
        {
            this.methodFilterRegexPattern = Pattern.compile( "^" + methodFilter + "$", Pattern.DOTALL | Pattern.MULTILINE  );
        }

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
     * @return the isReadOnly
     */
    public boolean isReadOnly()
    {
        return isReadOnly;
    }



    /**
     * @param isReadOnly the isReadOnly to set
     */
    public void setReadOnly( boolean isReadOnly )
    {
        this.isReadOnly = isReadOnly;
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



    public static XoTransactionMapping getDefaultXoTransactionMapping()
    {
        return parse( "*", "PROPAGATION_REQUIRED" );
    }


    public static XoTransactionMapping parse( String transactionPropertyKey, String transactionPropertyValueString )
    {
        XoTransactionMapping xoTransactionMapping = new XoTransactionMapping();

        if ( "*".equals( transactionPropertyKey ) )
        {
            xoTransactionMapping.setMethodFilter( ".*" );
        }
        else
        {
            xoTransactionMapping.setMethodFilter( transactionPropertyKey );
        }



        String [] transactionPropertyValueArray = transactionPropertyValueString.replaceAll( "(?<!\\.)\\*", ".*" ).split( "," );

        for ( int i = 0; i < transactionPropertyValueArray.length; i++ )
        {
            transactionPropertyValueArray[i] = transactionPropertyValueArray[i].trim();

            if ( null == transactionPropertyValueArray[i] || transactionPropertyValueArray[i].length() == 0 )
            {
                throw new IllegalArgumentException( "Unexpected Property Value String" );
            }
            else if ( transactionPropertyValueArray[i].startsWith( "PROPAGATION_" ) )
            {
                xoTransactionMapping.setPropagationBehaviorName( transactionPropertyValueArray[i] );
            }
            else if ( transactionPropertyValueArray[i].startsWith( "ISOLATION_" ) )
            {
                xoTransactionMapping.setIsolationLevelName( transactionPropertyValueArray[i] );
            }
            else if ( transactionPropertyValueArray[i].equals( "readOnly" ) )
            {
                xoTransactionMapping.setReadOnly( true );
            }
            else if ( transactionPropertyValueArray[i].toLowerCase().contains( "timeout" ) )
            {
                String [] timeoutParts = transactionPropertyValueArray[i].toLowerCase().split( "=" );

                xoTransactionMapping.setTimeout( Integer.parseInt( timeoutParts[1] ) );
            }
            else
            {
                throw new IllegalArgumentException( "Unexpected Property Value String" );
            }
        }

        return xoTransactionMapping;

    }

    public static List<XoTransactionMapping> parseProperties( Properties transactionHandlerProperties )
    {

        List <XoTransactionMapping> transactionHandlerMappings = new ArrayList<XoTransactionMapping>();

        if ( null == transactionHandlerProperties || transactionHandlerProperties.size() == 0 )
        {
            transactionHandlerMappings.add( XoTransactionMapping.getDefaultXoTransactionMapping() );
            return transactionHandlerMappings;

        }


        Iterator<Map.Entry<Object, Object>> transactionHandlerPropertyIterator = transactionHandlerProperties.entrySet().iterator();
        while ( transactionHandlerPropertyIterator.hasNext() )
        {
            Map.Entry<Object, Object> transactionHandlerPropertyMapEntry = transactionHandlerPropertyIterator.next();

            transactionHandlerMappings.add( XoTransactionMapping.parse(
                String.valueOf( transactionHandlerPropertyMapEntry.getKey() ),
                String.valueOf( transactionHandlerPropertyMapEntry.getValue() )
                ) );
        }

        Collections.sort( transactionHandlerMappings, XoTransactionMapping.getComparator() );

        return transactionHandlerMappings;
    }

    public static Comparator<XoTransactionMapping> getComparator()
    {
        return new Comparator<XoTransactionMapping>() {

            public int compare( XoTransactionMapping o1, XoTransactionMapping o2 )
            {

                String s1 = o1.getMethodFilter();
                String s2 = o2.getMethodFilter();

                if ( s1.length() > s2.length() )
                {
                    return -1;  // s1 before s2
                }
                else if ( s1.length() < s2.length() )
                {
                    return 1;  // s1 after s2
                }
                else
                {
                    // o1 equals to o2
                    return s1.compareTo( s2 );
                }
            }


        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        return "XoTransactionMapping [methodRegexFilter=" + methodFilter
                + ", methodRegexFilterPattern=" + methodFilterRegexPattern + ", propagationBehaviorName="
                + propagationBehaviorName + ", isolationLevelName=" + isolationLevelName + ", isReadOnly="
                + isReadOnly + ", timeout=" + timeout + "]";
    }


}
