/**
 * Copyright (c) 2012.
 */
package xo.jdbc.util;

/**
 *
 */
public class XoJdbcException extends RuntimeException
{

    private static final long serialVersionUID = 1L;

    /**
     * {@inheritDoc}
     */
    public XoJdbcException()
    {
        super();
    }

    /**
     * {@inheritDoc}
     */
    public XoJdbcException( String message )
    {
        super( message );
    }

    /**
     * {@inheritDoc}
     */
    public XoJdbcException( String message, Throwable cause )
    {
        super( message, cause );
    }

    /**
     * {@inheritDoc}
     */
    public XoJdbcException( Throwable cause )
    {
        super( cause );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        return super.toString();
    }

}
