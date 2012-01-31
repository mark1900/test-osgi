/**
 * Copyright (c) 2012.
 */
package xo.transaction;

import java.util.List;
import java.util.Properties;

import junit.framework.Assert;

import org.junit.Test;

@SuppressWarnings( "static-method" )
public class XoTransactionMappingTest
{
    XoTransactionMapping xoTransactionMapping;

    public XoTransactionMappingTest()
    {
        xoTransactionMapping = new XoTransactionMapping();
    }

    @Test
    public void testIsMatch()
    {

        xoTransactionMapping.setMethodFilter( ".*" );
        Assert.assertTrue( xoTransactionMapping.isMatch( "doServiceMethod" ) );
        Assert.assertTrue( xoTransactionMapping.isMatch( "transactionMethod" ) );

        Assert.assertFalse( xoTransactionMapping.isMatch( "clone" ) );
        Assert.assertFalse( xoTransactionMapping.isMatch( "equals" ) );
        Assert.assertFalse( xoTransactionMapping.isMatch( "finalize" ) );
        Assert.assertFalse( xoTransactionMapping.isMatch( "getClass" ) );
        Assert.assertFalse( xoTransactionMapping.isMatch( "hashCode" ) );
        Assert.assertFalse( xoTransactionMapping.isMatch( "notify" ) );
        Assert.assertFalse( xoTransactionMapping.isMatch( "notifyAll" ) );
        Assert.assertFalse( xoTransactionMapping.isMatch( "toString" ) );
        Assert.assertFalse( xoTransactionMapping.isMatch( "wait" ) );


        xoTransactionMapping.setMethodFilter( "t.*" );
        Assert.assertFalse( xoTransactionMapping.isMatch( "doServiceMethod" ) );
        Assert.assertTrue( xoTransactionMapping.isMatch( "transactionMethod" ) );

        Assert.assertFalse( xoTransactionMapping.isMatch( "clone" ) );
        Assert.assertFalse( xoTransactionMapping.isMatch( "equals" ) );
        Assert.assertFalse( xoTransactionMapping.isMatch( "finalize" ) );
        Assert.assertFalse( xoTransactionMapping.isMatch( "getClass" ) );
        Assert.assertFalse( xoTransactionMapping.isMatch( "hashCode" ) );
        Assert.assertFalse( xoTransactionMapping.isMatch( "notify" ) );
        Assert.assertFalse( xoTransactionMapping.isMatch( "notifyAll" ) );
        Assert.assertTrue( xoTransactionMapping.isMatch( "toString" ) );
        Assert.assertFalse( xoTransactionMapping.isMatch( "wait" ) );

        xoTransactionMapping.setMethodFilter( "toString" );
        Assert.assertFalse( xoTransactionMapping.isMatch( "doServiceMethod" ) );
        Assert.assertFalse( xoTransactionMapping.isMatch( "transactionMethod" ) );

        Assert.assertFalse( xoTransactionMapping.isMatch( "clone" ) );
        Assert.assertFalse( xoTransactionMapping.isMatch( "equals" ) );
        Assert.assertFalse( xoTransactionMapping.isMatch( "finalize" ) );
        Assert.assertFalse( xoTransactionMapping.isMatch( "getClass" ) );
        Assert.assertFalse( xoTransactionMapping.isMatch( "hashCode" ) );
        Assert.assertFalse( xoTransactionMapping.isMatch( "notify" ) );
        Assert.assertFalse( xoTransactionMapping.isMatch( "notifyAll" ) );
        Assert.assertTrue( xoTransactionMapping.isMatch( "toString" ) );
        Assert.assertFalse( xoTransactionMapping.isMatch( "wait" ) );

        xoTransactionMapping.setMethodFilter( ".*|xx" );
        Assert.assertTrue( xoTransactionMapping.isMatch( "doServiceMethod" ) );
        Assert.assertTrue( xoTransactionMapping.isMatch( "transactionMethod" ) );

        Assert.assertTrue( xoTransactionMapping.isMatch( "clone" ) );
        Assert.assertTrue( xoTransactionMapping.isMatch( "equals" ) );
        Assert.assertTrue( xoTransactionMapping.isMatch( "finalize" ) );
        Assert.assertTrue( xoTransactionMapping.isMatch( "getClass" ) );
        Assert.assertTrue( xoTransactionMapping.isMatch( "hashCode" ) );
        Assert.assertTrue( xoTransactionMapping.isMatch( "notify" ) );
        Assert.assertTrue( xoTransactionMapping.isMatch( "notifyAll" ) );
        Assert.assertTrue( xoTransactionMapping.isMatch( "toString" ) );  // Must NOT be a regex match
        Assert.assertTrue( xoTransactionMapping.isMatch( "wait" ) );


    }

    @Test
    public void testPropertyParser()
    {

        Properties transactionHandlerProperties;
        List<XoTransactionMapping> xoTransactionMappings;


        transactionHandlerProperties = new Properties();
        xoTransactionMappings = XoTransactionMapping.parseProperties( transactionHandlerProperties );
        Assert.assertEquals( ".*", xoTransactionMappings.get( 0 ).getMethodFilter() );
        Assert.assertEquals( 1, xoTransactionMappings.size() );

        transactionHandlerProperties = new Properties();
        transactionHandlerProperties.setProperty( "update", "PROPAGATION_REQUIRED,ISOLATION_REPEATABLE_READ,timeout=30" );
        xoTransactionMappings = XoTransactionMapping.parseProperties( transactionHandlerProperties );
        Assert.assertEquals( "update", xoTransactionMappings.get( 0 ).getMethodFilter() );
        Assert.assertEquals( 1, xoTransactionMappings.size() );


        transactionHandlerProperties = new Properties();

//        <prop key="createOutConsumerRequest">PROPAGATION_REQUIRED,ISOLATION_REPEATABLE_READ,timeout=${transactionTimeoutSeconds}</prop>
//        <prop key="updateOptOutAuditLogResponseCode">
//              PROPAGATION_REQUIRED,ISOLATION_REPEATABLE_READ,timeout=${transactionTimeoutSeconds}</prop>
//        <prop key="addOptOuts">PROPAGATION_REQUIRED,ISOLATION_REPEATABLE_READ,timeout=${transactionTimeoutSeconds}</prop>
//        <prop key="*">PROPAGATION_REQUIRED,ISOLATION_REPEATABLE_READ,readOnly,timeout=${transactionTimeoutSeconds}</prop>

        transactionHandlerProperties.setProperty( "createOutConsumerRequest", "PROPAGATION_REQUIRED,ISOLATION_REPEATABLE_READ,timeout=30" );
        transactionHandlerProperties.setProperty( "updateOptOutAuditLogResponseCode",
                                                  "PROPAGATION_REQUIRED,ISOLATION_REPEATABLE_READ,timeout=30" );
        transactionHandlerProperties.setProperty( "addOptOuts", "PROPAGATION_REQUIRED,ISOLATION_REPEATABLE_READ,timeout=30" );
        transactionHandlerProperties.setProperty( "update", "PROPAGATION_REQUIRED,ISOLATION_REPEATABLE_READ,timeout=30" );
        transactionHandlerProperties.setProperty( "*", "PROPAGATION_REQUIRED,ISOLATION_REPEATABLE_READ,readOnly,timeout=30" );
        transactionHandlerProperties.setProperty( "delete", "PROPAGATION_REQUIRED,ISOLATION_REPEATABLE_READ,timeout=30" );

        xoTransactionMappings = XoTransactionMapping.parseProperties( transactionHandlerProperties );

        int index = 0;
        Assert.assertEquals( "updateOptOutAuditLogResponseCode", xoTransactionMappings.get( index++ ).getMethodFilter() );
        Assert.assertEquals( "createOutConsumerRequest", xoTransactionMappings.get( index++ ).getMethodFilter() );
        Assert.assertEquals( "addOptOuts", xoTransactionMappings.get( index++ ).getMethodFilter() );
        Assert.assertEquals( "delete", xoTransactionMappings.get( index++ ).getMethodFilter() );
        Assert.assertEquals( "update", xoTransactionMappings.get( index++ ).getMethodFilter() );
        Assert.assertEquals( ".*", xoTransactionMappings.get( index++ ).getMethodFilter() );





    }
}