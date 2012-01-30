/**
 * Copyright (c) 2012.
 */
package xo.jdbc.util;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 */
public interface XoRowMapper<T>
{
    T mapRow( ResultSet rs, int rowNum ) throws SQLException;
}
