package com.webset.set.impresion.business;

import java.util.Date;
import java.util.List;

import com.webset.set.impresion.dao.ImpresorasDao;
import com.webset.set.impresion.dto.MantenimientosDto;
import com.webset.set.impresion.service.ImpresorasService;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.dto.CajaUsuarioDto;

public class ImpresorasBusinessImpl implements ImpresorasService{
	ImpresorasDao impresorasDao;
	Bitacora bitacora;
	
	public List<CajaUsuarioDto> llenaComboCajas() { return impresorasDao.llenaComboCajas(); }
	public List<MantenimientosDto> buscarImpresoras() { return impresorasDao.buscarImpresoras(); }
	
	public String eliminarImpre(int noImpresora) {
		int resp = 0;
		int resp1 = 0;
		String result = "";
		
		try {
			resp = impresorasDao.eliminarImpre(noImpresora);
			resp1 = impresorasDao.eliminarImpreCharola(noImpresora);
			
			if(resp != 0 || resp1 != 0) result = "Datos Eliminado";
			else result = "Error en la eliminaci�n";
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Impresion, C:ImpresorasDaoImpl, M:eliminarImpre");	
			e.printStackTrace();
		}
		
		return result;
	}
	
	public String insertarImpre(int noImpresora, int noCaja, int noCharola) {
		int resp = 0;
		String result = "";
		
		try {
			resp = impresorasDao.buscarImpreEsp(noImpresora);
			
			if(resp != 0) return "La impresora ya est� asignada";
			
			resp = impresorasDao.insertarImpre(noImpresora, noCaja, noCharola);
			
			if(resp != 0) result = "Impresora Registrada";
			else result = "Error al registrar impresora";
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Impresion, C:ImpresorasDaoImpl, M:insertarImpre");	
			e.printStackTrace();
		}
		return result;
	}
	
	public ImpresorasDao getImpresorasDao() {
		return impresorasDao;
	}

	public void setImpresorasDao(ImpresorasDao impresorasDao) {
		this.impresorasDao = impresorasDao;
	}
}
