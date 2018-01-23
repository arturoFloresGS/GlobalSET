package com.webset.set.financiamiento.service;

import java.util.List;
import java.util.Map;
import com.webset.set.financiamiento.dto.AmortizacionCreditoDto;
import com.webset.set.financiamiento.dto.ContratoCreditoDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;

public interface FinanciamientoModificacionCService {
	public List<LlenaComboGralDto> llenarCmbTasa();
	public List<AmortizacionCreditoDto> selectAmortizaciones(String psIdContrato,int piDisposicion,boolean pbCambioTasa ,String psTipoMenu ,String psProyecto ,int piCapital);
	public List<LlenaComboGralDto> obtenerContratos(int empresa);
	public List<LlenaComboGralDto> obtenerEmpresas(int idUsuario, boolean bMantenimiento);
	public List<LlenaComboGralDto> obtenerDisposiciones(String linea, boolean estatus);
	public List<ContratoCreditoDto> funSQLTasa(String psTasa);
	public Map<String, Object> modificar(String contrato, int disposicion,int optTasa,String cmbTasaBase, String txtValTasa, String txtPuntos, String txtTasaVig,
			String txtTasaFij, String txtFecCor, boolean chkCapital, String txtRenta, String txtIva, String jsonGrid);
	public Map<String, Object> modificaProvision(String contrato, int disposicion, int empresa);



} 
