/**
 * Copyright (c) 2012.
 */
package xo.jdbc.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class XoRowMapperResultGetter<T>
{
    protected int listBufferSize = 1024;

    protected final XoRowMapper<T> xoRowMapper;

    public XoRowMapperResultGetter( XoRowMapper<T> xoRowMapper )
    {
        this.xoRowMapper = xoRowMapper;
    }

    public XoRowMapperResultGetter( XoRowMapper<T> xoRowMapper, int listBufferSize )
    {
        this.xoRowMapper = xoRowMapper;
        this.listBufferSize = listBufferSize;
    }

    public List<T> getList( ResultSet resultSet ) throws SQLException
    {
        return getList( resultSet, null );
    }

    public List<T> getList( ResultSet resultSet, Integer resultSizeMaximum ) throws SQLException
    {
        List<T> results = new ArrayList<T>( listBufferSize );

        int rowIndex = 0;
        while ( resultSet.next() )
        {
            if ( null != resultSizeMaximum && rowIndex + 1 > resultSizeMaximum )
            {
                break;
            }

            results.add( xoRowMapper.mapRow( resultSet,  rowIndex ) );
            rowIndex++;
        }
        return results;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        return "XoRowMapperResultGetter [listBufferSize=" + listBufferSize + ", xoRowMapper=" + xoRowMapper
                + "]";
    }
}
