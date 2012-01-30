/**
 * Copyright (c) 2012.
 */
package xo.jdbc.util;

import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


/**
 *
 */
@SuppressWarnings( "static-method" )
public class XoJdbcHelper
{
    public static final int DEFAULT_TIMEOUT = 15000;

    protected XoJdbcConnectionHelper xoJdbcConnectionHelper;

    protected int timeout;


    public XoJdbcHelper()
    {
        this.xoJdbcConnectionHelper = null;
        timeout = DEFAULT_TIMEOUT;
    }


    /**
     * Execute SQL and return row count affected.
     *
     * @param sql
     * @return
     */
    public int update( String sql )
    {
        return update( sql, null );
    }

    /**
     * Execute SQL and return row count affected.
     * The sql "?" tokens will be replaced by the Object[] values.
     *
     * @param sql
     * @return
     */
    public int update( String sql, Object[] objects )
    {

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        int rowsUpdated;

        try
        {
            connection = xoJdbcConnectionHelper.getDatabaseConnection();
            preparedStatement = connection.prepareStatement( sql );
            setPreparedStatementValues( preparedStatement, objects );
            rowsUpdated = preparedStatement.executeUpdate();
        }
        catch ( SQLException e )
        {
            throw new XoJdbcException( e );
        }
        finally
        {
            closePreparedStatement( preparedStatement );
            xoJdbcConnectionHelper.closeConnection( connection );
        }
        return rowsUpdated;

    }

    /**
     * Execute SQL and return.
     *
     * @param sql
     * @param xoBatchPreparedStatementSetter
     * @return
     */
    public int [] batchUpdate( String sql, XoBatchPreparedStatementSetter xoBatchPreparedStatementSetter )
    {

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        int [] rowsUpdated;

        try
        {
            connection = xoJdbcConnectionHelper.getDatabaseConnection();
            preparedStatement = connection.prepareStatement( sql );

            for ( int i = 0; i < xoBatchPreparedStatementSetter.getBatchSize(); i++ )
            {
                xoBatchPreparedStatementSetter.setValues( preparedStatement, i );
                preparedStatement.addBatch();
            }

            rowsUpdated = preparedStatement.executeBatch();
        }
        catch ( SQLException e )
        {
            throw new XoJdbcException( e );
        }
        finally
        {
            closePreparedStatement( preparedStatement );
            xoJdbcConnectionHelper.closeConnection( connection );
        }

        return rowsUpdated;

    }

    /**
     * Used for retrieving rows/lists of data.
     *
     * @param sql
     * @param objects
     * @param xoRowMapper
     * @return
     */
    public <T> List<T> queryForList( String sql, Object[] objects, XoRowMapper<T> xoRowMapper )
    {

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        List<T> result = null;

        try
        {
            connection = xoJdbcConnectionHelper.getDatabaseConnection();
            preparedStatement = connection.prepareStatement( sql );
            setPreparedStatementValues( preparedStatement, objects );
            resultSet = preparedStatement.executeQuery();

            result = new XoRowMapperResultGetter<T>( xoRowMapper ).getList( resultSet );

        }
        catch ( SQLException e )
        {
            throw new XoJdbcException( e );
        }
        finally
        {
            closeResultSet( resultSet );
            closePreparedStatement( preparedStatement );
            xoJdbcConnectionHelper.closeConnection( connection );
        }

        return result;
    }


    /**
     * Retrieve the first result value from the first column.
     *
     * @param sql
     * @return
     */
    public Long queryForLong( String sql )
    {
        return queryForLong( sql, null );
    }

    /**
     * Retrieve the first result value from the first column.
     *
     * @param sql
     * @param objects
     * @return
     */
    public Long queryForLong( String sql, Object[] objects )
    {
        return queryForObject( sql, objects, 1, Long.class );
    }

    /**
     * Retrieve the first result value from the first column.
     *
     * @param sql
     * @return
     */
    public String queryForString( String sql )
    {
        return queryForString( sql, null );
    }

    /**
     * Retrieve the first result value from the first column.
     *
     * @param sql
     * @param objects
     * @return
     */
    public String queryForString( String sql, Object[] objects )
    {
        return queryForObject( sql, objects, 1, String.class );
    }

    /**
     * Retrieve the first result value from a column.
     *
     * @param sql
     * @param objects
     * @param returnColumnIndex
     * @param returnType
     * @return
     * @throws XoJdbcException
     */
    @SuppressWarnings( "unchecked" )
    public <T> T queryForObject( String sql, Object[] objects, int returnColumnIndex, Class<T> returnType ) throws XoJdbcException
    {

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Object result = null;

        try
        {
            connection = xoJdbcConnectionHelper.getDatabaseConnection();
            preparedStatement = connection.prepareStatement( sql );
            setPreparedStatementValues( preparedStatement, objects );
            resultSet = preparedStatement.executeQuery();

            if ( resultSet.next() )
            {
                result = getResultSetValue( resultSet, returnColumnIndex, returnType );
            }


        }
        catch ( SQLException e )
        {
            throw new XoJdbcException( e );
        }
        finally
        {
            closeResultSet( resultSet );
            closePreparedStatement( preparedStatement );
            xoJdbcConnectionHelper.closeConnection( connection );
        }

        return (T)result;

    }


    /**
     * Retrieve a list of column result values.
     *
     * @param sql
     * @param objects
     * @param returnColumnIndex
     * @param returnType
     * @return
     * @throws XoJdbcException
     */
    @SuppressWarnings( "unchecked" )
    public <T> List<T> queryForObjectList( String sql, Object[] objects, int returnColumnIndex, Class<T> returnType ) throws XoJdbcException
    {

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        int listBufferSize = 1024;
        List<T> results = new ArrayList<T>( listBufferSize );

        try
        {
            connection = xoJdbcConnectionHelper.getDatabaseConnection();
            preparedStatement = connection.prepareStatement( sql );
            setPreparedStatementValues( preparedStatement, objects );
            resultSet = preparedStatement.executeQuery();

            while ( resultSet.next() )
            {
                results.add( (T)getResultSetValue( resultSet, returnColumnIndex, returnType ) );
            }

        }
        catch ( SQLException e )
        {
            throw new XoJdbcException( e );
        }
        finally
        {
            closeResultSet( resultSet );
            closePreparedStatement( preparedStatement );
            xoJdbcConnectionHelper.closeConnection( connection );
        }

        return results;

    }

    /**
     * Helper method to retrieve value from the ResultSet argument.
     *
     * @param resultSet
     * @param columnIndex
     * @param returnType
     * @return
     * @throws SQLException
     */
    protected Object getResultSetValue( ResultSet resultSet, int columnIndex, Class<?> returnType ) throws SQLException
    {

        if ( columnIndex < 1 )
        {
            throw new IllegalArgumentException( "Invalid columnIndex:  " + columnIndex );
        }

        Object resultSetValue = null;

        if ( null == returnType )
        {
            resultSetValue = resultSet.getObject( columnIndex );

        }
        else if ( String.class.equals( returnType ) )
        {
            resultSetValue = resultSet.getString( columnIndex );
        }
        else if ( Clob.class.equals( returnType ) )
        {
            resultSetValue = resultSet.getClob( columnIndex );
        }
        else if ( BigDecimal.class.equals( returnType ) )
        {
            resultSetValue = resultSet.getBigDecimal( columnIndex );
        }
        else if ( Boolean.class.equals( returnType ) || Boolean.TYPE.equals( returnType ) )
        {
            resultSetValue = resultSet.getBoolean( columnIndex );
        }
        else if ( byte[].class.equals( returnType ) )
        {
            resultSetValue = resultSet.getBytes( columnIndex );
        }
        else if ( Blob.class.equals( returnType ) )
        {
            resultSetValue = resultSet.getBlob( columnIndex );
        }
        else if ( java.sql.Date.class.equals( returnType ) )
        {
            resultSetValue = resultSet.getDate( columnIndex );
        }
        else if ( java.sql.Time.class.equals( returnType ) )
        {
            resultSetValue = resultSet.getTime( columnIndex );
        }
        else if ( java.sql.Timestamp.class.equals( returnType ) )
        {
            resultSetValue = resultSet.getTimestamp( columnIndex );
        }
        else if ( Byte.class.equals( returnType ) || Byte.TYPE.equals( returnType ) )
        {
            resultSetValue = resultSet.getByte( columnIndex );
        }
        else if ( Short.class.equals( returnType ) || Short.TYPE.equals( returnType ) )
        {
            resultSetValue = resultSet.getShort( columnIndex );
        }
        else if ( Integer.class.equals( returnType ) || Integer.TYPE.equals( returnType ) )
        {
            resultSetValue = resultSet.getInt( columnIndex );
        }
        else if ( Long.class.equals( returnType ) || Long.TYPE.equals( returnType ) )
        {
            resultSetValue = resultSet.getLong( columnIndex );
        }
        else if ( Float.class.equals( returnType ) || Float.TYPE.equals( returnType ) )
        {
            resultSetValue = resultSet.getFloat( columnIndex );
        }
        else if ( Double.class.equals( returnType ) || Double.TYPE.equals( returnType ) )
        {
            resultSetValue = resultSet.getDouble( columnIndex );
        }
        else if ( Object.class.equals( returnType ) )
        {
            resultSetValue = resultSet.getObject( columnIndex );
        }
        else
        {
            throw new IllegalArgumentException( "Invalid sql type:  " + returnType );
        }

        if ( resultSet.wasNull() )
        {
            resultSetValue = null;
        }

        return resultSetValue;
    }

    /**
     * Helper method to set the appropriate values in a prepared statement.
     *
     * @param preparedStatement
     * @param objects
     */
    protected void setPreparedStatementValues( PreparedStatement preparedStatement, Object[] objects )
    {
        if ( null == objects )
        {
            return;
        }

        try
        {
            for ( int i = 0; i < objects.length; i++ )
            {

                if ( null == objects[i] )
                {
                    // This is a big HACK and seems to work.
                    //  Currently I don't know how to determine the object type if the value is null.

                    preparedStatement.setNull( i + 1, java.sql.Types.JAVA_OBJECT );
                }
                else if ( objects[i] instanceof String )
                {
                    preparedStatement.setString( i + 1, (String)objects[i] );
                }
                else if ( objects[i] instanceof Clob )
                {
                    preparedStatement.setClob( i + 1, (Clob)objects[i] );
                }
                else if ( objects[i] instanceof BigDecimal )
                {
                    preparedStatement.setBigDecimal( i + 1, (BigDecimal)objects[i] );
                }
                else if ( objects[i] instanceof Boolean )
                {
                    preparedStatement.setBoolean( i + 1, (Boolean)objects[i] );
                }
                else if ( objects[i] instanceof byte[] )
                {
                    preparedStatement.setBytes( i + 1, (byte[])objects[i] );
                }
                else if ( objects[i] instanceof Blob )
                {
                    preparedStatement.setBlob( i + 1, (Blob)objects[i] );
                }
                else if ( objects[i] instanceof java.sql.Date )
                {
                    preparedStatement.setDate( i + 1, (java.sql.Date)objects[i] );
                }
                else if ( objects[i] instanceof java.sql.Time )
                {
                    preparedStatement.setTime( i + 1, (java.sql.Time)objects[i] );
                }
                else if ( objects[i] instanceof java.sql.Timestamp )
                {
                    preparedStatement.setTimestamp( i + 1, (java.sql.Timestamp)objects[i] );
                }
                else if ( objects[i] instanceof Byte )
                {
                    preparedStatement.setByte( i + 1, (Byte)objects[i] );
                }
                else if ( objects[i] instanceof Short )
                {
                    preparedStatement.setShort( i + 1, (Short)objects[i] );
                }
                else if ( objects[i] instanceof Integer )
                {
                    preparedStatement.setInt( i + 1, (Integer)objects[i] );
                }
                else if ( objects[i] instanceof Long )
                {
                    preparedStatement.setLong( i + 1, (Long)objects[i] );
                }
                else if ( objects[i] instanceof Float )
                {
                    preparedStatement.setFloat( i + 1, (Float)objects[i] );
                }
                else if ( objects[i] instanceof Double )
                {
                    preparedStatement.setDouble( i + 1, (Double)objects[i] );
                }
                else
                {
                    throw new IllegalArgumentException( "Invalid sql type:  " + objects[i].getClass() );
                }
            }
        }
        catch ( SQLException e )
        {
            throw new XoJdbcException( e );
        }

    }


    /**
     * Close resource if applicable.
     *
     * @param resultSet
     */
    protected void closeResultSet( ResultSet resultSet )
    {
        try
        {
            if ( resultSet != null )
            {
                resultSet.close();
            }
        }
        catch ( Exception e )
        {
            // ignore
        }
    }

    /**
     * Close resource if applicable.
     *
     * @param preparedStatement
     */
    protected void closePreparedStatement( PreparedStatement preparedStatement )
    {
        try
        {
            if ( preparedStatement != null )
            {
                preparedStatement.close();
            }
        }
        catch ( Exception e )
        {
            // ignore
        }
    }

    /**
     * @return the xoJdbcConnectionHelper
     */
    public XoJdbcConnectionHelper getXoJdbcConnectionHelper()
    {
        return xoJdbcConnectionHelper;
    }


    /**
     * @param xoJdbcConnectionHelper the xoJdbcConnectionHelper to set
     */
    public void setXoJdbcConnectionHelper( XoJdbcConnectionHelper xoJdbcConnectionHelper )
    {
        this.xoJdbcConnectionHelper = xoJdbcConnectionHelper;
        if ( null != xoJdbcConnectionHelper )
        {
            xoJdbcConnectionHelper.setTimeout( timeout );
        }
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
        if ( null != xoJdbcConnectionHelper )
        {
            xoJdbcConnectionHelper.setTimeout( timeout );
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        return "XoJdbcHelper [xoJdbcConnectionHelper=" + xoJdbcConnectionHelper + ", timeout=" + timeout + "]";
    }



}
