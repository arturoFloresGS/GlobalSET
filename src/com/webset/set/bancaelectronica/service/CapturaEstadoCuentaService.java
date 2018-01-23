package com.webset.set.bancaelectronica.service;

import java.util.List;
import java.util.Map;

import com.webset.set.bancaelectronica.dto.CapturaEstadoCuentaDto;
import com.webset.set.utilerias.dto.LlenaComboEmpresasDto;

public interface CapturaEstadoCuentaService {

	public List<CapturaEstadoCuentaDto> llenaComboTipoBanco(int numEmpresa);
	public List<CapturaEstadoCuentaDto> llenaComboChequera(int numEmpresa, int numBanco);
	public List<CapturaEstadoCuentaDto> llenaGrid(int numEmpresa,boolean banco, int numBanco,boolean chequera2, String chequera,boolean fecha2, String fecha);
	public List<CapturaEstadoCuentaDto> llenaComboConcepto(int numBanco);
	public String obtenerCargo(String concepto, int numBanco);
	public String obtenerSalvoBuenCobro(int numBanco, String concepto);
	public String obtenerSucursal(int numBanco, String chequera);
	public String guardarNuevoEstado(List<Map<String, String>> registroGson, String fecha);
	public List<LlenaComboEmpresasDto> llenaComboEmpresas(int numUsuario);
	public String eliminarEstadoCuenta(int empresa, int secuencia);
	
	

}
