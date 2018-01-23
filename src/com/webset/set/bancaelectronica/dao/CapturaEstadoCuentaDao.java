package com.webset.set.bancaelectronica.dao;

import java.util.List;

import com.webset.set.bancaelectronica.dto.CapturaEstadoCuentaDto;
import com.webset.set.utilerias.dto.LlenaComboEmpresasDto;

public interface CapturaEstadoCuentaDao {

	public List<CapturaEstadoCuentaDto> llenaComboTipoBanco(int numEmpresa);

	public List<CapturaEstadoCuentaDto> llenaComboChequera(int numEmpresa, int numBanco);
	public List<CapturaEstadoCuentaDto> llenaGrid(int numEmpresa,boolean banco, int numBanco,boolean chequera2, String chequera,boolean fecha2, String fecha);

	public List<CapturaEstadoCuentaDto> llenaComboConcepto(int numBanco);

	public String obtenerCargo(String concepto, int numBanco);

	public String obtenerSalvoBuenCobro(int numBanco, String concepto);

	public String obtenerSucursal(int numBanco, String chequera);

	public int guardarNuevoEstado(CapturaEstadoCuentaDto objCapturaEstadoCuentaDto, String string, String string2);

	public int actualizarEstadoCuenta(CapturaEstadoCuentaDto objCapturaEstadoCuentaDto);

	public List<LlenaComboEmpresasDto> llenaComboEmpresas(int numUsuario);

	public int eliminarEstadoCuenta(int numEmpresa, int secuencia);


}
