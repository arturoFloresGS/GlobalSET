package com.webset.set.barridosfondeos.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.webset.set.barridosfondeos.dao.BarridosFondeosDao;
import com.webset.set.barridosfondeos.dto.FondeoAutomaticoDto;
import com.webset.set.barridosfondeos.service.BarridosFondeosRepService;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.Funciones;

public class BarridosFondeosRepBusinessImpl implements BarridosFondeosRepService{
	private BarridosFondeosDao barridosFondeosDao;
	Funciones funciones= new Funciones();
	Bitacora bitacora = new Bitacora();
	
	/* (non-Javadoc)
	 * @see com.webset.set.barridosfondeos.service.BarridosFondeosRepService#obtenerReporteArbol(int)
	 */
	@Override
	public List<Map<String, Object>> obtenerReporteArbol(int noEmpresaRaiz) {
		return this.barridosFondeosDao.obtenerReporteArbolEmpresa(noEmpresaRaiz);
	}
	
	/* (non-Javadoc)
	 * @see com.webset.set.barridosfondeos.service.BarridosFondeosRepService#obtenerReporteFondeo(int)
	 */
	@Override
	public List<Map<String, Object>> obtenerReporteFondeo(int idUsuario, String tipoOperacion) {
		return this.barridosFondeosDao.obtenerReporteFondeo(idUsuario, tipoOperacion);
	}

	/* (non-Javadoc)
	 * @see com.webset.set.barridosfondeos.service.BarridosFondeosRepService#obtenerReporteFondeoBarridoAut(java.util.List, com.webset.set.barridosfondeos.dto.BusquedaFondeoDto)
	 */
	@Override
	public List<Map<String, Object>> obtenerReporteFondeoBarridoAut(List<FondeoAutomaticoDto> barridos) {
		FondeoAutomaticoDto barrido = null;
		
		Map<String, Object> mapaBarrido = new HashMap<String, Object>();
		List<Map<String, Object>> listaMapas = new ArrayList<Map<String,Object>>();
		
		
		try{
			for (int i = 0; i < barridos.size(); i++){
				barrido = new FondeoAutomaticoDto();
				barrido = barridos.get(i);
				mapaBarrido = new HashMap<String, Object>();
				
				mapaBarrido.put("empresaOrigen", barrido.getNomEmpresaOrigen());
				mapaBarrido.put("empresaDestino", barrido.getNomEmpresaDestino());
				mapaBarrido.put("chequeraOrigen", barrido.getIdChequeraPadre());
				mapaBarrido.put("chequeraDestino", barrido.getIdChequera());
				mapaBarrido.put("fondeo", barrido.getImporteTraspaso());
				mapaBarrido.put("barrido", barrido.getImporteBarrido());
				
				listaMapas.add(mapaBarrido);
			}
			
		}catch (Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: BarridosFondeos C: BarridosFondeosBusinessImpl M: ejecutarFondeoAutomatico");
			return null;
		}
		
		return listaMapas;
	}

	/* (non-Javadoc)
	 * @see com.webset.set.barridosfondeos.service.BarridosFondeosRepService#obtenerReporteArbolEstruc(int, int)
	 */
	@Override
	public List<Map<String, Object>> obtenerReporteArbolEstruc(int noEmpresaRaiz, int idUsuario) {
		return this.barridosFondeosDao.obtenerReporteArbolEmpresaEst(noEmpresaRaiz, idUsuario);
	}

	/* (non-Javadoc)
	 * @see com.webset.set.barridosfondeos.service.BarridosFondeosRepService#obtenerReporteFiliales(int)
	 */
	@Override
	public List<Map<String, Object>> obtenerReporteFiliales(int noEmpresa) {
		return this.barridosFondeosDao.obtenerReporteFiliales(noEmpresa);
	}

	/* (non-Javadoc)
	 * @see com.webset.set.barridosfondeos.service.BarridosFondeosRepService#obtenerReporteArbolEstruc(int, int, java.lang.String)
	 */
	@Override
	public List<Map<String, Object>> obtenerReporteBarridosFondeos(int noEmpresa, int idUsuario, String fecha) {
		return this.barridosFondeosDao.obtenerReporteBarridosFondeos(noEmpresa, idUsuario, fecha);
	}

	/* (non-Javadoc)
	 * @see com.webset.set.barridosfondeos.service.BarridosFondeosRepService#obtenerReporteBarridosFondeosStr(int, int, java.lang.String)
	 */
	@Override
	public List<Map<String, String>> obtenerReporteBarridosFondeosStr(
			int noEmpresa, int idUsuario, String fecha) {
		return this.barridosFondeosDao.obtenerReporteBarridosFondeosStr(noEmpresa, idUsuario, fecha);
	}

	/* (non-Javadoc)
	 * @see com.webset.set.barridosfondeos.service.BarridosFondeosRepService#obtenerReporteCuadreFondeo(int, int, java.lang.String)
	 */
	@Override
	public List<Map<String, String>> obtenerReporteCuadreFondeo(int noEmpresa, int idUsuario, String fecha) {
		return this.barridosFondeosDao.obtenerReporteCuadre(noEmpresa, idUsuario, fecha);
	}

	public BarridosFondeosDao getBarridosFondeosDao() {
		return barridosFondeosDao;
	}
	public void setBarridosFondeosDao(
			BarridosFondeosDao barridosFondeosDao) {
		this.barridosFondeosDao = barridosFondeosDao;
	}

}
