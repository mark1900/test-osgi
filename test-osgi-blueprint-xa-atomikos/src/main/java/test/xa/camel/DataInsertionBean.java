package test.xa.camel;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import test.xa.XaTestPersistenceHelper;

public class DataInsertionBean implements Processor
{

    private static final transient Logger LOG = LoggerFactory.getLogger( DataInsertionBean.class );

    protected XaTestPersistenceHelper xaTestPersistenceHelper;

    public DataInsertionBean()
    {

    }


    public void process( Exchange exchange ) throws Exception
    {
        String body = String.valueOf( exchange.getIn().getBody() );

        LOG.info( "DataInsertionBean.process -> getBody()  [" + body + "]");

        int rowsUpdated = xaTestPersistenceHelper.jdbcUpdate( body );

        LOG.info( "DataInsertionBean.process -> Rows Updated  [" + rowsUpdated + "]");
    }



    /**
     * @return the xaTestPersistenceHelper
     */
    public XaTestPersistenceHelper getXaTestPersistenceHelper()
    {
        return xaTestPersistenceHelper;
    }


    /**
     *
     * @param xaTestPersistenceHelper
     */
    public void setXaTestPersistenceHelper( XaTestPersistenceHelper xaTestPersistenceHelper )
    {
        this.xaTestPersistenceHelper = xaTestPersistenceHelper;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        return "DataInsertionBean []";
    }

}