/**
 * Copyright (c) 2012.
 */
package test.xa;

import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class XaTestExecutor extends TimerTask
{
    private static final transient Logger LOG = LoggerFactory.getLogger( XaTestExecutor.class );


    private final Timer timer = new Timer();

    protected XaTest [] xaTests;

    public XaTestExecutor( XaTest [] xaTests, int delay )
    {
        super();
        this.xaTests = xaTests;
        timer.schedule( this, delay );



    }

    @Override
    public void run()
    {
        boolean testSuccess = false;
        XaTest currentXaTest = null;

        try
        {
            for ( XaTest xaTest : xaTests )
            {
                currentXaTest = xaTest;

                LOG.info( "xaTest.execute():  "  + String.valueOf( currentXaTest ) );
                xaTest.execute();


            }

            testSuccess = true;
        }
        catch ( Exception e )
        {
            String currentXaTestClass = null;
            if ( null != currentXaTest )
            {
                currentXaTestClass = String.valueOf( currentXaTest.getClass() );
            }

            LOG.error( "Unexpected Error executing XaTest "
                       + "[" + String.valueOf( currentXaTestClass ) + "]."
                        + "  Stopping timer.  ", e );
            timer.cancel();
        }
        finally
        {
            //  Print directly to the ServiceMix OSGi console and the logs
            if ( testSuccess )
            {
                System.out.println( "XaTestExecutor.run():  SUCCESS!" );
                LOG.info( "XaTestExecutor.run():  SUCCESS!"  );
            }
            else
            {
                System.out.println( "XaTestExecutor.run():  FAILURE." );
                LOG.info( "XaTestExecutor.run():  FAILURE."  );
            }
        }
    }

}
