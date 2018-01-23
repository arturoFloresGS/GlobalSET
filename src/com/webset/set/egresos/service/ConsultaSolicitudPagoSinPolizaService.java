package com.webset.set.egresos.service;

import java.util.List;

import com.webset.set.egresos.dto.ConsultaSolicitudPagoSinPolizaDto;

public interface ConsultaSolicitudPagoSinPolizaService {
	public List<ConsultaSolicitudPagoSinPolizaDto> llenaGrid(String noEmpresa, String fechaIni, String fechaFin, String usuario, boolean bandera, String usuarioEnCurso);
	public String exportaExcel(String datos);
	public List<ConsultaSolicitudPagoSinPolizaDto> llenaComboUsuario(boolean facultad, String usuario);
}