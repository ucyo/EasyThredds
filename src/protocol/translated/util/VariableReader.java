package protocol.translated.util;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import protocol.parse.NumericRange;
import protocol.parse.SpatialRange;
import protocol.parse.TimeRange;
import protocol.reader.IReader;
import ucar.nc2.NetcdfFile;

public class VariableReader
{
    private Map<String, DimensionArray> datasets;
    private static VariableReader instance;

    private VariableReader()
    {
	this.datasets = new HashMap<>();
    }

    public static VariableReader getInstance()
    {
	if (instance == null)
	{
	    instance = new VariableReader();
	}

	return instance;
    }

    public synchronized void close()
    {
	List<Throwable> errors = new LinkedList<>();

	for (DimensionArray dims : datasets.values())
	{
	    try
	    {
		dims.close();
	    } catch (Throwable t)
	    {
		errors.add(t);
	    }
	}

	if (!errors.isEmpty())
	{
	    // if there was any error return the first one
	    throw new IllegalStateException(errors.get(0));
	}
    }

    public synchronized void clear()
    {
	datasets.clear();
    }

    public synchronized boolean hasDataset(String datasetBaseUrl)
    {
	return datasets.containsKey(datasetBaseUrl);
    }

    public synchronized void addDataset(String datasetBaseUrl, DimensionArray dims)
    {
	if (hasDataset(datasetBaseUrl))
	    throw new IllegalArgumentException("Already stored data for the given dataset");

	datasets.put(datasetBaseUrl, dims);
    }

    public synchronized NumericRange getLongitudeIndexRange(String datasetBaseUrl, SpatialRange valueRange)
    {
	if (datasetBaseUrl.contains("?"))
	{
	    throw new IllegalArgumentException("The dataset must be provided without a query");
	}

	DimensionArray dims = this.datasets.get(datasetBaseUrl);
	if (dims == null)
	{
	    throw new IllegalStateException("No dimension data has been fetched for the given dataset");
	} else if (!dims.hasLongitudeDimension())
	{
	    throw new IllegalStateException("The dataset does not have a longitude dimension");
	}

	float[] lonData = dims.getLongitudeData();
	return VariableIndexUtil.getIndexRange(valueRange, lonData);
    }

    public synchronized boolean isFullLongitudeRange(String datasetBaseUrl, SpatialRange valueRange)
    {
	if (datasetBaseUrl.contains("?"))
	{
	    throw new IllegalArgumentException("The dataset must be provided without a query");
	}

	DimensionArray dims = this.datasets.get(datasetBaseUrl);
	if (dims == null)
	{
	    throw new IllegalStateException("No dimension data has been fetched for the given dataset");
	} else if (!dims.hasLongitudeDimension())
	{
	    throw new IllegalStateException("The dataset does not have a longitude dimension");
	}

	float[] lonData = dims.getLongitudeData();
	// the given absolute value range comprises the available value range if
	// the first value and the last value are outside of the available one
	return valueRange.getStartCoordinate() <= lonData[0] && valueRange.getEndCoordinate() >= lonData[lonData.length - 1];
    }

    public synchronized NumericRange getLatitudeIndexRange(String datasetBaseUrl, SpatialRange valueRange)
    {
	if (datasetBaseUrl.contains("?"))
	{
	    throw new IllegalArgumentException("The dataset must be provided without a query");
	}

	DimensionArray dims = this.datasets.get(datasetBaseUrl);
	if (dims == null)
	{
	    throw new IllegalStateException("No dimension data has been fetched for the given dataset");
	} else if (!dims.hasLongitudeDimension())
	{
	    throw new IllegalStateException("The dataset does not have a latitude dimension");
	}

	float[] latData = dims.getLatitudeData();
	NumericRange indexRange = VariableIndexUtil.getIndexRange(valueRange, latData);
	return indexRange;
    }
    
    public synchronized boolean isFullLatitudeRange(String datasetBaseUrl, SpatialRange valueRange)
    {
	if (datasetBaseUrl.contains("?"))
	{
	    throw new IllegalArgumentException("The dataset must be provided without a query");
	}

	DimensionArray dims = this.datasets.get(datasetBaseUrl);
	if (dims == null)
	{
	    throw new IllegalStateException("No dimension data has been fetched for the given dataset");
	} else if (!dims.hasLongitudeDimension())
	{
	    throw new IllegalStateException("The dataset does not have a latitude dimension");
	}

	float[] latData = dims.getLatitudeData();
	// the given absolute value range comprises the available value range if
	// the first value and the last value are outside of the available one
	return valueRange.getStartCoordinate() <= latData[0] && valueRange.getEndCoordinate() >= latData[latData.length - 1];
    }

    public synchronized NumericRange getTimeIndexRange(String datasetBaseUrl, TimeRange valueRange)
    {
	if (datasetBaseUrl.contains("?"))
	{
	    throw new IllegalArgumentException("The dataset must be provided without a query");
	}

	DimensionArray dims = this.datasets.get(datasetBaseUrl);
	if (dims == null)
	{
	    throw new IllegalStateException("No dimension data has been fetched for the given dataset");
	} else if (!dims.hasLongitudeDimension())
	{
	    throw new IllegalStateException("The dataset does not have a  dimension");
	}

	double[] timeData = dims.getTimeData();
	return VariableIndexUtil.getIndexRange(valueRange, timeData);
    }

    public synchronized NumericRange getAltitudeIndexRange(String datasetBaseUrl, NumericRange valueRange)
    {
	if (datasetBaseUrl.contains("?"))
	{
	    throw new IllegalArgumentException("The dataset must be provided without a query");
	}

	DimensionArray dims = this.datasets.get(datasetBaseUrl);
	if (dims == null)
	{
	    throw new IllegalStateException("No dimension data has been fetched for the given dataset");
	} else if (!dims.hasLongitudeDimension())
	{
	    throw new IllegalStateException("The dataset does not have an altitude dimension");
	}

	float[] lvlData = dims.getAltitudeData();
	return VariableIndexUtil.getIndexRange(valueRange, lvlData);
    }
    
    public synchronized boolean isFullAltitudeRange(String datasetBaseUrl, NumericRange valueRange)
    {
	if (datasetBaseUrl.contains("?"))
	{
	    throw new IllegalArgumentException("The dataset must be provided without a query");
	}

	DimensionArray dims = this.datasets.get(datasetBaseUrl);
	if (dims == null)
	{
	    throw new IllegalStateException("No dimension data has been fetched for the given dataset");
	} else if (!dims.hasLongitudeDimension())
	{
	    throw new IllegalStateException("The dataset does not have an altitude dimension");
	}

	float[] lvlData = dims.getAltitudeData();
	// the given absolute value range comprises the available value range if
	// the first value and the last value are outside of the available one
	return valueRange.getStart().floatValue() <= lvlData[0]
		&& valueRange.getEnd().floatValue() >= lvlData[lvlData.length - 1];
    }
    
    public synchronized boolean isSingleAltitudeLevel(String datasetBaseUrl, NumericRange valueRange)
    {
	NumericRange indexRange = getAltitudeIndexRange(datasetBaseUrl, valueRange);
	return indexRange.isPoint();
    }
}
