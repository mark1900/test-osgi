/**
 * Copyright (c) 2012.
 */
package xo.jms.util;

/**
 *
 */
public class XoJmsException extends RuntimeException
{

    private static final long serialVersionUID = 1L;

    /**
     * {@inheritDoc}
     */
    public XoJmsException()
    {
        super();
    }

    /**
     * {@inheritDoc}
     */
    public XoJmsException( String message )
    {
        super( message );
    }

    /**
     * {@inheritDoc}
     */
    public XoJmsException( String message, Throwable cause )
    {
        super( message, cause );
    }

    /**
     * {@inheritDoc}
     */
    public XoJmsException( Throwable cause )
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
