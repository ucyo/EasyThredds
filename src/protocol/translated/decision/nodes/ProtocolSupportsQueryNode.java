package protocol.translated.decision.nodes;

import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import com.amazonaws.services.kms.model.UnsupportedOperationException;

import protocol.CollectiveProtocol;
import protocol.translated.TranslatedProtocol;
import protocol.translated.decision.DecisionNode;
import service.ProtocolPicker;
import service.ProtocolPicker.Protocol;

/**
 * This decision node excludes all protocols that cannot fully handle the
 * requested query.
 * 
 * It then refers to the selection process by either picking the best performing
 * node (@link {@link SelectByWeightedPerformanceNode}) or by using a random
 * node to gather sample performance data ({@link SelectRandomNode}).
 */
public class ProtocolSupportsQueryNode implements DecisionNode
{
    
    // sample probability
    private static final double SAMPLE_RANDOM_PROTOCOL_PROBABILITY = 0.1;
    
    private CollectiveProtocol collective;
    
    public ProtocolSupportsQueryNode(CollectiveProtocol collective) {
	this.collective = collective;
    }

    @Override
    public DecisionNode decide(Set<Protocol> protocols)
    {
	Iterator<Protocol> it = protocols.iterator();
	while (it.hasNext()) {
	    Protocol protocol = it.next();
	    TranslatedProtocol translated = ProtocolPicker.pickByName(protocol, collective);
	    if (!translated.canTranslate(collective)) {
		it.remove();
	    }
	}
	
	Random rand = new Random();
	if (protocols.isEmpty()) {
	    return new NoSuitableProtocol();
	} else if (protocols.size() == 1) {
	    return new BestProtocolNode(protocols);
	} else if (rand.nextDouble() < SAMPLE_RANDOM_PROTOCOL_PROBABILITY){
	    return new SelectRandomNode();
	} else {
	    return new SelectByWeightedPerformanceNode();
	}
    }

    @Override
    public boolean isFinal()
    {
	return false;
    }

    @Override
    public Protocol getFinal()
    {
	throw new UnsupportedOperationException("This is not a final decision");
    }

}
