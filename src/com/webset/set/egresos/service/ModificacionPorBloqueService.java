package com.webset.set.egresos.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.webset.set.conciliacionbancoset.dto.ConciliaBancoDto;
import com.webset.set.conciliacionbancoset.dto.CriteriosBusquedaDto;
import com.webset.set.egresos.dto.ConfirmacionCargoCtaDto;
import com.webset.set.egresos.dto.PolizaContableDto;
import com.webset.set.egresos.dto.SolicitudPagoDto;
import com.webset.set.utilerias.dto.CajaUsuarioDto;
import com.webset.set.utilerias.dto.LlenaComboDivisaDto;
import com.webset.set.utilerias.dto.LlenaComboEmpresasDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.set.utilerias.dto.LlenaFormaPagoDto;
import com.webset.set.utilerias.dto.MovimientoDto;
import com.webset.set.utilerias.dto.StoreParamsComunDto;

public interface ModificacionPorBloqueService {

	public List<LlenaComboDivisaDto> obtenerDivisas();

	public List<LlenaComboGralDto> obtenerBancos(int noEmpresa);

	public List<LlenaComboGralDto> obtenerDivision(int noEmpresa);

	public List<LlenaComboGralDto> LlenarComboGral(LlenaComboGralDto dto);

	public List<SolicitudPagoDto> obtenerSolicitudes(CriteriosBusquedaDto dto);

	public List<LlenaComboGralDto> obtenerProveedores(String texto, int iNoEmpresa);

	public List<LlenaComboGralDto> obtenerBeneficiario(int noEmpresa, String texto);

	public List<LlenaComboGralDto> obtenerBeneficiario2(int noEmpresa, String texto);

	public boolean validarFormaPagoBenef(int noEmpresa, int formaPago);
	
	public List<SolicitudPagoDto> validarBancoCheqBenef(int noProveedor, String divisa, int noEmpresa);
	
	public int actualizaBancoCheqBenef(int noFolioDet, int iBancoBenef, String sChequeraBenef);
	
	public int actualizaLimpiaBancoCheqBenef(int noFolioDet);
	
	public Map<String, Object> cambiarCuadrantes(StoreParamsComunDto dto);

	public int modificarBloqueo(int noFolioDet,String psBloqueo);
}

