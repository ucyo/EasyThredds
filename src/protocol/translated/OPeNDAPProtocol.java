package protocol.translated;

import config.ConfigReader;
import protocol.CollectiveProtocol;

public class OPeNDAPProtocol extends DapProtocol
{

    public OPeNDAPProtocol(CollectiveProtocol query)
    {
	super(query);
    }

    @Override
    protected String getProtocolUrlAbbrevation()
    {
	return ConfigReader.getInstace().getOpenDapUrlName();
    }
}
