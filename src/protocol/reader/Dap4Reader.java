package protocol.reader;

import java.io.File;
import java.io.IOException;

import ucar.nc2.NetcdfFile;
import ucar.nc2.dataset.NetcdfDataset;

/**
 * A reader that is capable of downloading remote DAP4 data sets.
 * 
 * This should work similarly to {@link protocol.reader.OPeNDAPReader} but could
 * not be tested as the DAP4 protocol did not work properly.
 */
public class Dap4Reader extends NetCdfReader
{
    private File dapFile;
    private File ddsFile;
    
    @Override
    protected NetcdfFile buildNetCdfFile(String baseUri, String query, String indentifier) {
	try {
	    //URL dodsUrl = new URL(baseUri + ".dap?" + query);
	    //URL ddsUrl = new URL(baseUri + ".dds?" + query);
	    
	    return NetcdfDataset.openDataset("dap4:" + baseUri);
	    
//	    String fileName = "dap4File.dap";
//	    File downloadedFile = new File(fileName);
//	    dapFile = new File(fileName + ".dap");
//	    ddsFile = new File(fileName + ".dds");
//	    FileUtils.copyURLToFile(dodsUrl, dapFile);
//	    FileUtils.copyURLToFile(ddsUrl, ddsFile);
//	    return NetcdfDataset.openDataset("dap4:file:" + downloadedFile.getAbsolutePath());
	} catch (IOException e) {
	    throw new IllegalArgumentException("Could not build the NetCdf file", e);
	}
    }
    
    @Override
    public void close() throws Exception {
	if (ddsFile != null) {
	    ddsFile.delete();
	}
	if (dapFile != null) {
	    dapFile.delete();
	}
	
	super.close();
    }
}
