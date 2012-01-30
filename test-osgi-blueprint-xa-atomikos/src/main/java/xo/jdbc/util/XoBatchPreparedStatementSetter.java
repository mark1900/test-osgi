/**
 * Copyright (c) 2012.
 */
package xo.jdbc.util;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 *
 */
public interface XoBatchPreparedStatementSetter
{
    void setValues( PreparedStatement ps, int i ) throws SQLException;

    int getBatchSize();
}
