/**
 * Copyright (c) 2012.
 */
package test.xa;


import java.util.List;

import xo.jdbc.util.XoJdbcException;
import xo.jdbc.util.XoJdbcHelper;
import xo.jdbc.util.XoRowMapper;
import xo.jms.util.activemq.XoJmsQueueHelper;

/**
 *
 */
public class XaTestPersistenceHelper
{


    protected XoJdbcHelper xoJdbcHelper;

    protected XoJmsQueueHelper xoJmsHelper;


    public XaTestPersistenceHelper()
    {

    }

    public int jdbcUpdate( String sql )
    {
        return xoJdbcHelper.update( sql );
    }

    public int jdbcUpdate( String sql, Object[] objects )
    {
        return xoJdbcHelper.update( sql, objects );
    }

    public <T> List<T> queryForList( String sql, Object[] objects, XoRowMapper<T> xoRowMapper ) throws XoJdbcException
    {
        return xoJdbcHelper.queryForList( sql, objects, xoRowMapper );
    }


    public Long queryForLong( String sql )
    {
        return xoJdbcHelper.queryForLong( sql );
    }


    public Long queryForLong( String sql, Object[] objects )
    {
        return xoJdbcHelper.queryForLong( sql, objects );
    }

    public String queryForString( String sql )
    {
        return xoJdbcHelper.queryForString( sql );
    }

    public String queryForString( String sql, Object[] objects )
    {
        return xoJdbcHelper.queryForString( sql, objects );
    }


    public <T> T queryForObject( String sql, Object[] objects, int returnColumnIndex, Class<T> returnType) throws XoJdbcException
    {
        return xoJdbcHelper.queryForObject( sql, objects, returnColumnIndex, returnType );
    }

    public <T> List<T> queryForObjectList( String sql, Object[] objects, int returnColumnIndex, Class<T> returnType) throws XoJdbcException
    {
        return xoJdbcHelper.queryForObjectList( sql, objects, returnColumnIndex, returnType );
    }

    public int getDatabaseTableRowCount( String tableName )
    {
        return xoJdbcHelper.queryForLong( "select count(*) from dbo." + tableName ).intValue();
    }


    public void sendToQueue( String theQueueName, String theMessage )
    {
        xoJmsHelper.sendBody( theQueueName, theMessage );
    }

    public String receiveMessageFromQueueIfItExists( String queueName )
    {
        return xoJmsHelper.receiveBody( queueName );
    }

    public String receiveMessageFromDeadLetterQueueIfItExists( String queueName )
    {
        String dlq = xoJmsHelper.receiveBody( "DLQ." + queueName );
        return dlq;
    }

    /**
     * @return the xoJdbcHelper
     */
    public XoJdbcHelper getXoJdbcHelper()
    {
        return xoJdbcHelper;
    }

    /**
     * @param xoJdbcHelper the xoJdbcHelper to set
     */
    public void setXoJdbcHelper( XoJdbcHelper xoJdbcHelper )
    {
        this.xoJdbcHelper = xoJdbcHelper;
    }

    /**
     * @return the xoJmsHelper
     */
    public XoJmsQueueHelper getXoJmsHelper()
    {
        return xoJmsHelper;
    }

    /**
     * @param xoJmsHelper the xoJmsHelper to set
     */
    public void setXoJmsHelper( XoJmsQueueHelper xoJmsHelper )
    {
        this.xoJmsHelper = xoJmsHelper;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        return "XaTestPersistenceHelper [xoJdbcHelper=" + xoJdbcHelper + ", xoJmsHelper=" + xoJmsHelper + "]";
    }


}
