/**
 * Copyright (c) 2012.
 */
package test.xa.camel;

import org.apache.camel.language.XPath;

/**
 *
 */
@SuppressWarnings( "unused" )
public class CamelTestBean
{
    /*
     * <?xml version=\"1.0\"?><thirdparty id=\"123\"><name>THE TEST
     * THIRDPARTY</name><date>201110140815</date><code>200</code></thirdparty>
     */
    public static String toInsertSql( @XPath( "thirdparty/@id" ) int thirdpartyId,
        @XPath( "thirdparty/name/text()" ) String name, @XPath( "thirdparty/date/text()" ) long created,
        @XPath( "thirdparty/code/text()" ) int statusCode )
    {

        if ( thirdpartyId <= 0 )
        {
            throw new IllegalArgumentException( "ThirdPartyId is invalid, was " + thirdpartyId );
        }

        StringBuilder sb = new StringBuilder();
        sb.append( "INSERT INTO dbo." + XaCamelTest.theTableName
                   + " (thirdparty_id, name, created, status_code) VALUES (" );
        sb.append( "'" ).append( thirdpartyId ).append( "', " );
        sb.append( "'" ).append( name ).append( "', " );
        sb.append( "'" ).append( created ).append( "', " );
        sb.append( "'" ).append( statusCode ).append( "') " );

        return sb.toString();
    }

    public static String toException( @XPath( "thirdparty/@id" ) int thirdpartyId,
        @XPath( "thirdparty/name/text()" ) String name, @XPath( "thirdparty/date/text()" ) long created,
        @XPath( "thirdparty/code/text()" ) int statusCode ) throws Exception
    {

        throw new Exception();

    }

    public static String toRuntimeException( @XPath( "thirdparty/@id" ) int thirdpartyId,
        @XPath( "thirdparty/name/text()" ) String name, @XPath( "thirdparty/date/text()" ) long created,
        @XPath( "thirdparty/code/text()" ) int statusCode ) throws Exception
    {

        throw new RuntimeException();

    }
}
