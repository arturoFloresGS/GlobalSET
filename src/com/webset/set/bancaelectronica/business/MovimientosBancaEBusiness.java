package com.webset.set.bancaelectronica.business;

import java.util.ArrayList;
import java.util.List;

import com.webset.set.bancaelectronica.dao.MovimientosBancaEDao;
import com.webset.set.utilerias.dto.ComunDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
/**
 * @author Jessica Arelly Cruz Cruz
 * @since 01/03/2011
 */
public class MovimientosBancaEBusiness {
	MovimientosBancaEDao movimientosBancaEDao = new MovimientosBancaEDao();
	
	public String obtenerFechaHoy(){
		return movimientosBancaEDao.obtenerFechaHoy();
	}
	
	public List<LlenaComboGralDto> llenarComboBancos(int noEmpresa){
		return movimientosBancaEDao.llenarComboBancos(noEmpresa);
	}

	public MovimientosBancaEDao getMovimientosBancaEDao() {
		return movimientosBancaEDao;
	}
	
	public List<LlenaComboGralDto> llenarComboChequeras(int noBanco,int noEmpresa){
		return movimientosBancaEDao.llenarComboChequeras(noBanco, noEmpresa);
	}
	
	public List<ComunDto>obtenerConceptos(boolean lbGenerico, int noBanco){
		List<ComunDto> conceptos = new ArrayList<ComunDto>();
		String generico = movimientosBancaEDao.seleccionarBancaElect(noBanco);
		if (generico.trim().equals("I") || generico.trim().equals("A") || generico.trim().equals("I,A"))
	        lbGenerico = true;
	    else //conceptos genericos
	        lbGenerico = false;
	    
		conceptos = movimientosBancaEDao.obtenerConceptos(lbGenerico, noBanco);
		return conceptos;
	}
	
	

	public void setMovimientosBancaEDao(MovimientosBancaEDao movimientosBancaEDao) {
		this.movimientosBancaEDao = movimientosBancaEDao;
	}

}
