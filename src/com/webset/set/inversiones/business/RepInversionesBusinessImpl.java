package com.webset.set.inversiones.business;

import java.util.List;
import java.util.Map;

import com.webset.set.inversiones.middleware.service.RepInversionesService;
import com.webset.set.inversiones.dao.RepInversionesDao;
import com.webset.set.utilerias.Bitacora;

public class RepInversionesBusinessImpl implements RepInversionesService{
	Bitacora bitacora = new Bitacora();
	private RepInversionesDao repInversionesDao;

	/* (non-Javadoc)
	 * @see com.webset.set.inversiones.middleware.service.RepInversionesService#obtenerReporteInversiones(int, java.lang.String, java.lang.String, java.lang.String, int, java.lang.String)
	 */
	@Override
	public List<Map<String, Object>> obtenerReporteInversiones(int noEmpresa,
			String fechaInicial, String fechaFinal, String anio, int noInstitucion, String idDivisa) {

		return this.getRepInversionesDao().consultarReporteInversiones(noEmpresa, fechaInicial, fechaFinal, anio, noInstitucion, idDivisa);
	}

	/* (non-Javadoc)
	 * @see com.webset.set.inversiones.middleware.service.RepInversionesService#obtenerReporteInvEstablecidas(int, java.lang.String, java.lang.String, java.lang.String, int)
	 */
	@Override
	public List<Map<String, Object>> obtenerReporteInvEstablecidas(int noEmpresa, String idDivisa, String fechaInicial,
			String fechaFinal, int idUsuario) {

		return this.getRepInversionesDao().consultarReporteInvEstablecidas(noEmpresa, idDivisa, fechaInicial, fechaFinal, idUsuario);
	}

	/* (non-Javadoc)
	 * @see com.webset.set.inversiones.middleware.service.RepInversionesService#obtenerReporteVencimientos(int, java.lang.String, java.lang.String, java.lang.String, int)
	 */
	@Override
	public List<Map<String, Object>> obtenerReporteVencimientos(int noEmpresa, String idDivisa, String fechaInicial, String fechaFinal,
			int idUsuario) {
		
		return this.getRepInversionesDao().consultarReporteVencimiento(noEmpresa, idDivisa, fechaInicial, fechaFinal, idUsuario);
	}
	
	/* (non-Javadoc)
	 * @see com.webset.set.inversiones.middleware.service.RepInversionesService#obtenerReportePosicionInv(int, java.lang.String, java.lang.String, java.lang.String, int)
	 */
	@Override
	public List<Map<String, Object>> obtenerReportePosicionInv(int noEmpresa, String idDivisa, String fechaInicial, String fechaFinal,
			int idUsuario) {
		List<Map<String, Object>> lista = null;
		try{
			this.getRepInversionesDao().eliminarTmpPosicion(idUsuario);
			this.getRepInversionesDao().insertarTmpPosicion(idUsuario, fechaInicial, fechaFinal, noEmpresa, idDivisa);
			lista = this.getRepInversionesDao().consultarReportePosicionInv(noEmpresa, idUsuario);
		}catch (Exception e){
			e.printStackTrace();
		}
		
		return lista;
	}

	/* (non-Javadoc)
	 * @see com.webset.set.inversiones.middleware.service.RepInversionesService#obtenerReporteSaldosInv(int, java.lang.String, java.lang.String, java.lang.String, int)
	 */
	@Override
	public List<Map<String, Object>> obtenerReporteSaldosInv(int noEmpresa,
			String idDivisa, String fechaInicial, String fechaFinal,
			int idUsuario) {
		return this.getRepInversionesDao().consultarReporteSaldosInv(noEmpresa, idDivisa, fechaInicial, fechaFinal, idUsuario);
	}

	/* (non-Javadoc)
	 * @see com.webset.set.inversiones.middleware.service.RepInversionesService#obtenerExcelInvEstablecidas(int, java.lang.String, java.lang.String, java.lang.String, int)
	 */
	@Override
	public List<Map<String, String>> obtenerExcelInvEstablecidas(int noEmpresa,
			String idDivisa, String fechaInicial, String fechaFinal,
			int idUsuario) {
		return this.getRepInversionesDao().consultarExcelInvEstablecidas(noEmpresa, idDivisa, fechaInicial, fechaFinal, idUsuario);
	}

	/* (non-Javadoc)
	 * @see com.webset.set.inversiones.middleware.service.RepInversionesService#obtenerExcelVencimientos(int, java.lang.String, java.lang.String, java.lang.String, int)
	 */
	@Override
	public List<Map<String, String>> obtenerExcelVencimientos(int noEmpresa,
			String idDivisa, String fechaInicial, String fechaFinal,
			int idUsuario) {
		return this.getRepInversionesDao().consultarExcelVencimiento(noEmpresa, idDivisa, fechaInicial, fechaFinal, idUsuario);
	}

	/* (non-Javadoc)
	 * @see com.webset.set.inversiones.middleware.service.RepInversionesService#obtenerExcelPosicionInv(int, java.lang.String, java.lang.String, java.lang.String, int)
	 */
	@Override
	public List<Map<String, String>> obtenerExcelPosicionInv(int noEmpresa,
			String idDivisa, String fechaInicial, String fechaFinal,
			int idUsuario) {
		List<Map<String, String>> lista = null;
		try{
			this.getRepInversionesDao().eliminarTmpPosicion(idUsuario);
			this.getRepInversionesDao().insertarTmpPosicion(idUsuario, fechaInicial, fechaFinal, noEmpresa, idDivisa);
			lista = this.getRepInversionesDao().consultarExcelPosicionInv(noEmpresa, idUsuario);
		}catch (Exception e){
			e.printStackTrace();
		}
		
		return lista;
	}

	/* (non-Javadoc)
	 * @see com.webset.set.inversiones.middleware.service.RepInversionesService#obtenerExcelSaldosInv(int, java.lang.String, java.lang.String, java.lang.String, int)
	 */
	@Override
	public List<Map<String, String>> obtenerExcelSaldosInv(int noEmpresa,
			String idDivisa, String fechaInicial, String fechaFinal,
			int idUsuario) {
		return this.getRepInversionesDao().consultarExcelSaldosInv(noEmpresa, idDivisa, fechaInicial, fechaFinal, idUsuario);
	}

	/**
	 * @return the repInversionesDao
	 */
	public RepInversionesDao getRepInversionesDao() {
		return repInversionesDao;
	}
	/**
	 * @param repInversionesDao the repInversionesDao to set
	 */
	public void setRepInversionesDao(RepInversionesDao repInversionesDao) {
		this.repInversionesDao = repInversionesDao;
	}
	
	
}
