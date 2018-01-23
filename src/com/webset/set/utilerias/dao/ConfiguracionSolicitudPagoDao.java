package com.webset.set.utilerias.dao;

import java.util.List;

import com.webset.set.utileriasmod.dto.ConfiguracionSolicitudPagoDto;


public interface ConfiguracionSolicitudPagoDao {
	public List<ConfiguracionSolicitudPagoDto> llenaComboPoliza(String numUsuario);
	public List<ConfiguracionSolicitudPagoDto> llenaComboGrupo(String idRubro);
	public List<ConfiguracionSolicitudPagoDto> llenaComboRubro(String idPoliza);
	public ConfiguracionSolicitudPagoDto consultarConfiguracionSolicitudPago(ConfiguracionSolicitudPagoDto dto);
	public String existeConfiguracionSolicitudPago(ConfiguracionSolicitudPagoDto dto);
	public String insertaConfiguracionSolicitudPago(ConfiguracionSolicitudPagoDto dto);
	public String updateConfiguracionSolicitudPago(ConfiguracionSolicitudPagoDto dto);
	public String deleteConfiguracionSolicitudPago(ConfiguracionSolicitudPagoDto dto);
	public String existenBeneficiarios(String[] beneficiarios);
	public String guardarBeneficiarios(String[] beneficiarios, ConfiguracionSolicitudPagoDto dto);
	public List<ConfiguracionSolicitudPagoDto> llenaGridBeneficiarios(String idPoliza, String idGrupo, String idRubro, String beneficiarios);
	public List<ConfiguracionSolicitudPagoDto> llenaGridPolizas();
}
