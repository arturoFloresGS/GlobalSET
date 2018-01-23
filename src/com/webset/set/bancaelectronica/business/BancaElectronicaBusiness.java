package com.webset.set.bancaelectronica.business;

import java.util.List;

import com.webset.set.bancaelectronica.dao.BancaElectronicaDao;
import com.webset.set.utilerias.dto.ComunDto;
import com.webset.set.utilerias.dto.ReferenciaEncDto;
import com.webset.set.utilerias.dto.Retorno;

/**
 * 
 * @author Sergio Vaca
 *
 */
public class BancaElectronicaBusiness {
	private BancaElectronicaDao bancaElectronicaDao;
	/**
	 * 
	 * @return List<ComunDto>
	 * 
	 * Obtiene los bancos activos
	 * Public Function FunSQLCombo367() As ADODB.Recordset
	 */
	public List<ComunDto> seleccionarBancosActivos(){
		return bancaElectronicaDao.seleccionarBancosActivos();
	}

	//setters & getters
	public BancaElectronicaDao getBancaElectronicaDao() {
		return bancaElectronicaDao;
	}

	public void setBancaElectronicaDao(BancaElectronicaDao bancaElectronicaDao) {
		this.bancaElectronicaDao = bancaElectronicaDao;
	}
	
	/**
	 * 
	 * @return List<ReferenciaEncDto>
	 * 
	 * Public Function FunSQLLevantaRefEnc() As ADODB.Recordset
    	Se llama en la forma frmTransIng
	 */
	public List<ReferenciaEncDto> levantarRefEnc(){
		return bancaElectronicaDao.levantarRefEnc();
	}
	
	/**
	 * Consulta la tabla del configura_set
	 * 
	 * @param indice
	 * @return String
	 */
	public String consultarConfiguraSet(int indice) {
		return bancaElectronicaDao.consultarConfiguraSet(indice);
	}
	
	/**
	 * Consulta la tabla del configura_set
	 * 
	 * @param indice
	 * @return List<Retorno>
	 */
	public List<Retorno> consultarConfiguraSet() {
		return bancaElectronicaDao.consultarConfiguraSet();
	}
}
