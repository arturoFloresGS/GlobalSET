package com.webset.set.ingresos.business;

import java.util.List;

import com.webset.set.ingresos.dao.CatBancoDao;
import com.webset.set.ingresos.dto.CatArmaIngresoDto;
import com.webset.set.ingresos.dto.CatBancoDto;
import com.webset.set.ingresos.dto.CatCtaBancoDto;
import com.webset.set.ingresos.dto.CatGrupoRubroDto;
import com.webset.set.seguridad.dto.EmpresaDto;
import com.webset.set.seguridad.exceptions.BusinessException;

/**
 * Clase que contiene las reglas de negocio de la aplicacion
 * @author Jessica Arelly 
*/
public class CatBancoBusiness {
	private CatBancoDao catBancoDao;
	
	/**
	 * Busca en la tabla seg_usuario los datos segun los
	 *	parametros recibidos
	 * @param usuario
	 * @return
	 * @throws Exception
	 * @throws BusinessException
	 */
	public List<EmpresaDto> consultar(EmpresaDto empresa) throws Exception, BusinessException{
		List<EmpresaDto> empresas = catBancoDao.consultarEmpresa(empresa); 
		return empresas;
	}
	
	public List<EmpresaDto> consultarFID(EmpresaDto empresa) throws Exception, BusinessException{
		List<EmpresaDto> empresas = catBancoDao.consultarEmpresaFID(empresa);
		return empresas;
	}
	
	/**
	 * Busca en la tabla cat_banco los datos segun los
	 *	parametros recibidos
	 * @param banco, emp
	 * @return bancos
	 * @throws Exception
	 */
	public List<CatBancoDto> consultar(int emp) throws Exception{
		List<CatBancoDto> bancos = catBancoDao.consultarBanco(emp); 
		return bancos;
	}
	
	public List<CatBancoDto> consultarFID(int emp) throws Exception{
		List<CatBancoDto> bancos = catBancoDao.consultarBancoFID(emp); 
		return bancos;
	}

	/**
	 * Busca en la tabla cat_cta_banco los datos segun los
	 *	parametros recibidos
	 * @param banco
	 * @return bancos
	 * @throws Exception
	 * @throws BusinessException
	 */
	public List<CatCtaBancoDto> consultar(int ban, int emp) throws Exception{
		List<CatCtaBancoDto> bancos = catBancoDao.consultarChequera(ban , emp); 
		return bancos;
	}
	
	public List<CatCtaBancoDto> consultarFID(int ban, int emp) throws Exception{
		List<CatCtaBancoDto> bancos = catBancoDao.consultarChequeraFID(ban , emp); 
		return bancos;
	}
	
	
	/**
	 * Busca en la tabla cat_arma_ingreso los datos segun los
	 *	parametros recibidos
	 * @param cai
	 * @return
	 * @throws Exception
	 * @throws BusinessException
	 */
	public List<CatArmaIngresoDto> llenaGrid(int ban, int emp, String cheque) throws Exception, BusinessException{
		List<CatArmaIngresoDto> ingreso = catBancoDao.llenarGrid(ban, emp, cheque); 
		return ingreso;
	}
	
	/**
	 * Busca en la tabla cat_corresponde para llenar el combo
	 * @param idEmpresa, idBanco, chequera
	 * @return ingresos
	 * @throws Exception
	 */
	public List<CatArmaIngresoDto> llenarTipoIngreso(int idEmpresa, int idBanco, String chequera) throws Exception{
		List<CatArmaIngresoDto> ingresos = catBancoDao.llenarTipoIngreso(idEmpresa, idBanco, chequera);
		return ingresos;
	}
	
	 /** Busca en las tablas cat_rubro y cat_grupo
	 * @param idRubro
	 * @return grupo
	 * @throws Exception
	 */
	public List<CatGrupoRubroDto> llenarGrupo() throws Exception{
		List<CatGrupoRubroDto> grupo = catBancoDao.llenarGrupo();
		return grupo;
	}
	
	/** Busca en las tablas cat_rubro y cat_grupo
	 * @param idGrupo
	 * @return rubro
	 * @throws Exception
	 */
	public List<CatGrupoRubroDto> llenarRubro(int idGrupo) throws Exception{
		List<CatGrupoRubroDto> rubro = catBancoDao.llenarRubro(idGrupo);
		return rubro;
	}
	
	/**
	 * Se inserta en la tabla cat_arma_ingreso 
	 * un nuevo componente
	 * @param ingreso
	 * @throws Exception
	 * @throws BusinessException
	 */	
	public int agregar(CatArmaIngresoDto ingreso, boolean pbCambiaCtas) throws Exception, BusinessException{
		return catBancoDao.insertarArmaIngreso(ingreso, pbCambiaCtas);
	}
	
	/**
	 * Modifica en la tabla cat_arma_ingreso 
	 * @param ingreso
	 * @throws Exception
	 * @throws BusinessException
	 */
	public int modificar(CatArmaIngresoDto ingreso, boolean pbCambiaCtas) throws Exception, BusinessException{
			return catBancoDao.modificarArmaIngreso(ingreso, pbCambiaCtas);
		
	}
	/**
	 * Elimina un registro de la tabla cat_arma_ingreso
	 * */
	public int eliminar(CatArmaIngresoDto ingreso) throws Exception, BusinessException{
		return catBancoDao.eliminarArmaIngreso(ingreso);
	}

	public CatBancoDao getCatBancoDao() {
		return catBancoDao;
	}

	public void setCatBancoDao(CatBancoDao catBancoDao) {
		this.catBancoDao = catBancoDao;
	}
}
