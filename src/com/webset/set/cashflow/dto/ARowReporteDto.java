package com.webset.set.cashflow.dto;

import java.util.HashMap;
import java.util.List;

public class ARowReporteDto {
	
	private List<HashMap> row;
	
	public ARowReporteDto() {		
		
	}

	public ARowReporteDto(List<HashMap> row) {		
		this.row = row;
	}

	public List<HashMap> getRow() {
		return row;
	}

	public void setRow(List<HashMap> row) {
		this.row = row;
	}

	

}
