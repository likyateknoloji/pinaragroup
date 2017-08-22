package com.likya.myra.test.helpers;

import java.io.Serializable;
import java.util.ArrayList;

import com.likya.myra.jef.OutputStrategy;
import com.likya.myra.jef.model.OutputData;

public class TestOutput implements OutputStrategy, Serializable {
	
	private static final long serialVersionUID = -2587537493283672093L;

	private ArrayList<OutputData> outputList = new ArrayList<OutputData>();
	
	@Override
	public void sendDataObject(Object dataObject) {
		
		OutputData outputData = (OutputData) dataObject;
		
		outputList.add(outputData);
		
	}

	public ArrayList<OutputData> getOutputList() {
		return outputList;
	}

}
