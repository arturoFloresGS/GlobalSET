package com.webset.set.egresos.dao;

import java.util.List;
import java.util.Map;

import com.webset.set.egresos.dto.PagosPendientesDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;

import mx.com.gruposalinas.Pagos.DT_Pagos_OBPagos;
import mx.com.gruposalinas.Pagos.DT_Pagos_ResponsePagosResponse;

public interface FactorajeDao {

	public List<PagosPendientesDto> obtenerListaFactoraje(int empresa, int proveedor, String fechaIni, String fechaFin);
	public List<LlenaComboGralDto> obtenerProveedores(String filtro);
	public List<LlenaComboGralDto> obtenerIntermediarios();
	public Map<String, Object> enviarDatos(String folios, int usuario, int noFactorae, String fechaFactoraje);
	public String insertaBitacoraPagoPropuestas(DT_Pagos_ResponsePagosResponse[] pagosRespuestaq, List<DT_Pagos_OBPagos> listPagos, String fecHoy);
	public String consultarConfiguaraSet(int indice);
	public int eliminarZexpFact(String folios);
	public List<LlenaComboGralDto> llenarComboBeneficiario(LlenaComboGralDto dto);
	
}
