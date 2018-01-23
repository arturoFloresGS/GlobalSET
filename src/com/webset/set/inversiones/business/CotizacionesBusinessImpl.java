package com.webset.set.inversiones.business;

import java.math.BigDecimal;
import java.math.MathContext;
import java.sql.Date;
import java.util.List;
import java.util.Map;


import com.webset.set.inversiones.middleware.service.CotizacionesService;
import com.webset.set.inversiones.dto.ContratoInstitucionDto;
import com.webset.set.inversiones.dto.CotizacionDto;
import com.webset.set.inversiones.dto.CotizacionesDto;
import com.webset.set.inversiones.dto.LlenaComboValoresDto;
import com.webset.set.inversiones.dao.CotizacionesDao;
import com.webset.set.utilerias.Bitacora;

public class CotizacionesBusinessImpl implements CotizacionesService{
	Bitacora bitacora = new Bitacora();
	private CotizacionesDao cotizacionesDao;
	
	/* (non-Javadoc)
	 * @see com.webset.set.inversiones.middleware.service.InversionesComunService#consultarInstitucion()
	 */
	@Override
	public List<LlenaComboValoresDto> consultarContactosInst(int noInstitucion) {
		return this.getCotizacionesDao().consultarContactosInst(noInstitucion);
	}
	
	

	/* (non-Javadoc)
	 * @see com.webset.set.inversiones.middleware.service.CotizacionesService#consultarContratos(int)
	 */
	@Override
	public List<ContratoInstitucionDto> consultarContratos(int noInstitucion) {
		return this.getCotizacionesDao().consultarContratos(noInstitucion);
	}

	/* (non-Javadoc)
	 * @see com.webset.set.inversiones.middleware.service.CotizacionesService#consultarDivisaTV(java.lang.String)
	 */
	@Override
	public String consultarDivisaTV(String idTipoValor) {
		return this.getCotizacionesDao().consultarDivisaTV(idTipoValor);
	}

	/* (non-Javadoc)
	 * @see com.webset.set.inversiones.middleware.service.CotizacionesService#registrarCotizaciones(com.webset.set.inversiones.dto.CotizacionesDto)
	 */
	@Override
	public String registrarCotizaciones(CotizacionesDto cotizacionesDto) {
		final int constante = 36000;
		boolean existeCotizacion = false;
		double ldPotencia = 0;
		double ldPrimera = 0;
		BigDecimal ldTasa;
		double ldTasaEntrada = 0;
		int liPlazoEntrada = 0;
		
		List<Map<String, Object>> listaTasas = cotizacionesDto.getTasas();
		Map<String, Object> mapaTasa;
		CotizacionDto cotizacionDto;
		
		MathContext mc = new MathContext(4);
		
		if (listaTasas == null)
			return "La lista de tasas esta vacia";
		
		for (int i = 0; i < listaTasas.size(); i++){
			mapaTasa = listaTasas.get(i);
			//System.out.println("Tasa: <" + (String)mapaTasa.get("Tasa") + ">");
			ldTasaEntrada = Double.parseDouble((String)mapaTasa.get("Tasa"));
			liPlazoEntrada = (Integer) mapaTasa.get("Plazo");
			
			ldPotencia = 28 / liPlazoEntrada;
			ldPrimera = Math.pow( (ldTasaEntrada * liPlazoEntrada / constante) + 1, ldPotencia) * constante / 28;
			ldTasa = new BigDecimal(ldPrimera, mc);
			
			existeCotizacion = this.getCotizacionesDao().validarCotizacion(liPlazoEntrada, cotizacionesDto.getTipoValor(), 
																			cotizacionesDto.getIdInstitucion(), cotizacionesDto.getFecha());
			
			if (existeCotizacion)
				return "Existe una cotizacion previa";
			
			cotizacionDto = new CotizacionDto();
			cotizacionDto.setFecha2(cotizacionesDto.getFecha());
			cotizacionDto.setHora(cotizacionesDto.getHora());
			cotizacionDto.setIdDivisa(cotizacionesDto.getIdDivisa());
			cotizacionDto.setIdInstitucion(cotizacionesDto.getIdInstitucion());
			cotizacionDto.setIdUsuario(cotizacionesDto.getIdUsuario());
			cotizacionDto.setNoEmpresa(cotizacionesDto.getNoEmpresa());
			cotizacionDto.setPlazo(liPlazoEntrada);
			cotizacionDto.setTasa((String)mapaTasa.get("Tasa"));
			cotizacionDto.setTasaEquiv28(ldTasa);
			cotizacionDto.setTipoValor(cotizacionesDto.getTipoValor());
			
			this.cotizacionesDao.insertarCotizacion(cotizacionDto);
		}
		return "Cotizacion registrada exitosamente";
	}

	/* (non-Javadoc)
	 * @see com.webset.set.inversiones.middleware.service.CotizacionesService#obtenerRepCotizaciones(java.lang.String, int)
	 */
	@Override
	public List<Map<String, Object>> obtenerRepCotizaciones(String fecha, int noEmpresa) {
		return this.getCotizacionesDao().consultarRepCotizaciones(fecha, noEmpresa);
	}



	/**
	 * @return the cotizacionesDao
	 */
	public CotizacionesDao getCotizacionesDao() {
		return cotizacionesDao;
	}

	/**
	 * @param cotizacionesDao the cotizacionesDao to set
	 */
	public void setCotizacionesDao(CotizacionesDao cotizacionesDao) {
		this.cotizacionesDao = cotizacionesDao;
	}
	
	
	
}
