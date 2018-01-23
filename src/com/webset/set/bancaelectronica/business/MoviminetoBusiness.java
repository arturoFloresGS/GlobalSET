package com.webset.set.bancaelectronica.business;

import java.util.List;

import org.apache.log4j.Logger;

import com.webset.set.bancaelectronica.dao.MovimientoDao;
import com.webset.set.bancaelectronica.dto.MovimientoDto;
import com.webset.set.bancaelectronica.dto.ParametroBusquedaMovimientoDto;

/**
 * Clase que contiene los metodos "consultarBusqueda", "consultarEnvioMssPayment", "seleccionarNafin", "actualizarMovimientoTipoEnvio", 
 *	"insertaMovimiento", "obtenerCasaCambio" 
 *  
 */
public class MoviminetoBusiness {
	private MovimientoDao movimientoDao;
	 private static Logger logger = Logger.getLogger(MoviminetoBusiness.class);
	/**
	 * 
	 * @param dto ParametroBusquedaDto
	 * @return List<MovimientoDto>
	 */
	public List<MovimientoDto> consultarBusqueda(ParametroBusquedaMovimientoDto dto){
		try {
			return movimientoDao.consultarBusqueda(dto);
		} catch (Exception e) {
			logger.error(e.toString(),e);
			return null;
		}
	}
	/**
	 * 
	 * @param dto ParametroBusquedaDto
	 * @return List<MovimientoDto>
	 */
	public List<MovimientoDto> consultarEnvioMssPayment(ParametroBusquedaMovimientoDto dto){
		try{
			return movimientoDao.consultarEnvioMssPayment(dto);
		} catch (Exception e) {
			logger.error(e.toString(),e);
			return null;
		}
	}
	
	/**
	 * 
	 * @param dto ParametroBusqueda
	 * @return List<MovimientoDto> 
	 */
	public List<MovimientoDto> seleccionarNafin(ParametroBusquedaMovimientoDto dto){
		try{
			return movimientoDao.seleccionarNafin(dto);
		} catch (Exception e) {
			logger.error(e.toString(),e);
			return null;
		}
	}
	
	/**
	 * Ocurrio una exepcion si retorna -9
	 * @param dto ParametroBusquedaDto
	 * @return int
	 */
	public int actualizarMovimientoTipoEnvio(ParametroBusquedaMovimientoDto dto){
		try{
			return movimientoDao.actualizarMovimientoTipoEnvio(dto);
		} catch (Exception e) {
				logger.error(e.toString(),e);
			return -9;
		}
	}
	
	/**
	 * return -9 Exception
	 * @param dto MovimientoDto
	 * @return int
	 */
	public int insertaMovimiento(MovimientoDto dto){
		try{
			return movimientoDao.insertaMovimiento(dto);
		} catch (Exception e) {
				logger.error(e.toString(),e);
			return -9;
		}
	}
	
	//getters and setters
	public MovimientoDao getMovimientoDao() {
		return movimientoDao;
	}

	public void setMovimientoDao(MovimientoDao movimientoDao) {
		this.movimientoDao = movimientoDao;
	}
}
