/**
 * Copyright (c) 2012.
 */
package test.xa.service;

import test.xa.XaTest;

/**
 *
 */
public class XaServiceTest implements XaTest
{

    protected TransactionServiceBean transactionServiceBean;
    protected TransactionServiceBeanTester transactionServiceBeanTester;

    public XaServiceTest() throws Exception
    {

    }

    /**
     * {@inheritDoc}
     */
    public void execute() throws Exception
    {

        transactionServiceBeanTester.prepare();
        Thread.sleep( 1000 );
        transactionServiceBean.doTransactionCommit();
        Thread.sleep( 1000 );
        transactionServiceBeanTester.checkTransactionAndExpectTransactionCommit();


        Thread.sleep( 1000 );


        transactionServiceBeanTester.prepare();
        Thread.sleep( 1000 );
        try
        {
            transactionServiceBean.doTransactionRollback();
        }catch(Exception e)
        {
            // Transaction should have rolled back.
        }
        Thread.sleep( 1000 );
        transactionServiceBeanTester.checkTransactionAndExpectTransactionRollback();

    }

    /**
     * @return the transactionServiceBean
     */
    public TransactionServiceBean getTransactionServiceBean()
    {
        return transactionServiceBean;
    }

    /**
     * @param transactionServiceBean the transactionServiceBean to set
     */
    public void setTransactionServiceBean( TransactionServiceBean transactionServiceBeanImpl )
    {
        this.transactionServiceBean = transactionServiceBeanImpl;
    }

    /**
     * @return the transactionServiceBeanTester
     */
    public TransactionServiceBeanTester getTransactionServiceBeanTester()
    {
        return transactionServiceBeanTester;
    }

    /**
     * @param transactionServiceBeanTester the transactionServiceBeanTester to set
     */
    public void setTransactionServiceBeanTester( TransactionServiceBeanTester transactionServiceBeanTester )
    {
        this.transactionServiceBeanTester = transactionServiceBeanTester;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        return "XaServiceTest [transactionServiceBean=" + transactionServiceBean
                + ", transactionServiceBeanTester=" + transactionServiceBeanTester + "]";
    }




}
