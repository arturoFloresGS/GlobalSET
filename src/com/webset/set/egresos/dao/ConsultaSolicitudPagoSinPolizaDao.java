package com.webset.set.egresos.dao;

import java.util.List;

import com.webset.set.egresos.dto.ConsultaSolicitudPagoSinPolizaDto;


public interface ConsultaSolicitudPagoSinPolizaDao {
	public List<ConsultaSolicitudPagoSinPolizaDto> llenaGrid(String noEmpresa, String fechaIni, String fechaFin, String usuario, boolean facultad, String usuarioEnCurso);
	public List<ConsultaSolicitudPagoSinPolizaDto> llenaComboUsuario(boolean facultad, String usuario);
}
