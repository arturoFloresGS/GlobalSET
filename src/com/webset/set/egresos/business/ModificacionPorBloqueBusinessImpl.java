package com.webset.set.egresos.business;

import java.util.List;
import java.util.Map;

import com.webset.set.conciliacionbancoset.dto.CriteriosBusquedaDto;
import com.webset.set.egresos.dao.CapturaSolicitudesPagoDao;
import com.webset.set.egresos.dao.ModificacionPorBloqueDao;

import com.webset.set.egresos.dao.impl.ConsultasGeneralesEgresosDao;

import com.webset.set.egresos.dto.SolicitudPagoDto;

import com.webset.set.egresos.service.ModificacionPorBloqueService;
import com.webset.set.utilerias.Bitacora;

import com.webset.set.utilerias.Funciones;

import com.webset.set.utilerias.dto.LlenaComboDivisaDto;

import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.set.utilerias.dto.StoreParamsComunDto;

public class ModificacionPorBloqueBusinessImpl implements ModificacionPorBloqueService {
	private CapturaSolicitudesPagoDao capturaSolicitudesPagoDao;
	private ModificacionPorBloqueDao modificacionPorBloqueDao;
	private ConsultasGeneralesEgresosDao consultasGeneralesEgresosDao;
	Funciones funciones = new Funciones();
	Bitacora bitacora = new Bitacora();

	public List<LlenaComboDivisaDto> obtenerDivisas() {
		return modificacionPorBloqueDao.obtenerDivisas();
	}

	public List<LlenaComboGralDto> obtenerBancos(int noEmpresa) {
		return modificacionPorBloqueDao.obtenerBancos(noEmpresa);
	}

	public List<LlenaComboGralDto> obtenerDivision(int noEmpresa) {
		return modificacionPorBloqueDao.obtenerDivision(noEmpresa);
	}

	public List<LlenaComboGralDto> LlenarComboGral(LlenaComboGralDto dto) {
		return capturaSolicitudesPagoDao.llenarComboGral(dto);
	}

	public List<SolicitudPagoDto> obtenerSolicitudes(CriteriosBusquedaDto dto) {
		return modificacionPorBloqueDao.obtenerSolicitudes(dto);

	}

	public List<LlenaComboGralDto> obtenerProveedores(String texto, int iNoEmpresa) {
		return modificacionPorBloqueDao.obtenerProveedores(texto, iNoEmpresa);
	}

	public List<LlenaComboGralDto> obtenerBeneficiario(int noEmpresa, String texto) {
		return modificacionPorBloqueDao.obtenerBeneficiario(noEmpresa, texto);
	}

	public List<LlenaComboGralDto> obtenerBeneficiario2(int noEmpresa, String texto) {
		return modificacionPorBloqueDao.obtenerBeneficiario2(noEmpresa, texto);
	}

	public boolean validarFormaPagoBenef(int noEmpresa, int formaPago) {
		return modificacionPorBloqueDao.validarFormaPagoBenef(noEmpresa, formaPago);
	}

	public List<SolicitudPagoDto> validarBancoCheqBenef(int noProveedor, String divisa, int noEmpresa) {
		return modificacionPorBloqueDao.validarBancoCheqBenef(noProveedor, divisa, noEmpresa);
	}

	public int actualizaBancoCheqBenef(int noFolioDet, int iBancoBenef, String sChequeraBenef) {
		return modificacionPorBloqueDao.actualizaBancoCheqBenef(noFolioDet, iBancoBenef, sChequeraBenef);
	}

	public int actualizaLimpiaBancoCheqBenef(int noFolioDet) {
		return modificacionPorBloqueDao.actualizaLimpiaBancoCheqBenef(noFolioDet);
	}
	public Map<String, Object> cambiarCuadrantes(StoreParamsComunDto dto) {

		return modificacionPorBloqueDao.cambiarCuadrantes(dto);
	}
	public int modificarBloqueo(int noFolioDet, String psBloqueo) {
		return modificacionPorBloqueDao.modificarBloqueo(noFolioDet, psBloqueo);
	}

	//
	public ModificacionPorBloqueDao getModificacionPorBloqueDao() {
		return modificacionPorBloqueDao;
	}

	public void setModificacionPorBloqueDao(ModificacionPorBloqueDao modificacionPorBloqueDao) {
		this.modificacionPorBloqueDao = modificacionPorBloqueDao;
	}

}
