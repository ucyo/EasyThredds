package protocol;

import java.net.URI;
import java.net.URISyntaxException;

public abstract class TranslatedProtocol extends Protocol
{
    private CollectiveProtocol query;

    public TranslatedProtocol(CollectiveProtocol query)
    {
	super(query);
	this.query = query;
    }

    protected abstract String getProtocolUrlAbbrevation();

    protected abstract String getTranslatedQuery(CollectiveProtocol protocol);

    public URI getTranslatedUrl()
    {
	StringBuilder builder = new StringBuilder();
	builder.append(getBaseUrl());
	builder.append("/");
	builder.append(getProtocolUrlAbbrevation());
	builder.append("/");
	builder.append(getDataset());
	builder.append("?");
	builder.append(getTranslatedQuery(query));
	try {
	    return new URI(builder.toString());
	} catch (URISyntaxException e) {
	    e.printStackTrace();
	    return null;
	}
    }
}
