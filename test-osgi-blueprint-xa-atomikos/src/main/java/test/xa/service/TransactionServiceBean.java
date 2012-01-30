/**
 * Copyright (c) 2012.
 */
package test.xa.service;

/**
 *
 */
public interface TransactionServiceBean
{

    public abstract void doTransactionCommit();

    public abstract void doTransactionRollback();

    /**
     * {@inheritDoc}
     */
    public abstract String toString();

}