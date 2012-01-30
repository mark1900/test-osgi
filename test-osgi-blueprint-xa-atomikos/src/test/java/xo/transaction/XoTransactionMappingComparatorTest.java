/**
 * Copyright (c) 2012.
 */
package xo.transaction;

import java.util.Comparator;
import java.util.List;
import java.util.Properties;

import junit.framework.Assert;

import org.junit.Test;

@SuppressWarnings( "static-method" )
public class XoTransactionMappingComparatorTest
{
    Comparator<XoTransactionMapping> xoTransactionMappingComparator;

    public XoTransactionMappingComparatorTest()
    {
        xoTransactionMappingComparator = XoTransactionMapping.getComparator();
    }

    @Test
    public void testComparision()
    {
        Properties transactionHandlerProperties;
        List<XoTransactionMapping> xoTransactionMappings;


        transactionHandlerProperties = new Properties();
        xoTransactionMappings = XoTransactionMapping.parseProperties( transactionHandlerProperties );
        Assert.assertEquals( ".*", xoTransactionMappings.get( 0 ).getMethodRegexFilter() );
        Assert.assertEquals( 1, xoTransactionMappings.size() );

        transactionHandlerProperties = new Properties();
        transactionHandlerProperties.setProperty( "update", "PROPAGATION_REQUIRED,ISOLATION_REPEATABLE_READ,timeout=30" );
        xoTransactionMappings = XoTransactionMapping.parseProperties( transactionHandlerProperties );
        Assert.assertEquals( "update", xoTransactionMappings.get( 0 ).getMethodRegexFilter() );
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
        Assert.assertEquals( "updateOptOutAuditLogResponseCode", xoTransactionMappings.get( index++ ).getMethodRegexFilter() );
        Assert.assertEquals( "createOutConsumerRequest", xoTransactionMappings.get( index++ ).getMethodRegexFilter() );
        Assert.assertEquals( "addOptOuts", xoTransactionMappings.get( index++ ).getMethodRegexFilter() );
        Assert.assertEquals( "delete", xoTransactionMappings.get( index++ ).getMethodRegexFilter() );
        Assert.assertEquals( "update", xoTransactionMappings.get( index++ ).getMethodRegexFilter() );
        Assert.assertEquals( ".*", xoTransactionMappings.get( index++ ).getMethodRegexFilter() );





    }
}