package com.webset.set.inversiones.business;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.webset.set.inversiones.middleware.service.InversionesValoresService;
import com.webset.set.inversiones.dto.MantenimientoValoresDto;
import com.webset.set.inversiones.dto.LlenaComboValoresDto;
import com.webset.set.inversiones.dao.InversionesValoresDao;
import com.webset.set.utilerias.Bitacora;

public class InversionesValoresBusinessImpl implements InversionesValoresService{
	
	Bitacora bitacora = new Bitacora();
	InversionesValoresDao objInversionesValoresDao;
	
	public  List<MantenimientoValoresDto> consultarValores(){
		
		List<MantenimientoValoresDto> listConsVal = new ArrayList<MantenimientoValoresDto>();
		try{
		     listConsVal = objInversionesValoresDao.consultarValores();
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Inversiones, C:InversionesValoresBusinessImpl, M:consultarValores");
		}
		return listConsVal;
		
	}
	
	public List<LlenaComboValoresDto> consultarDivisa(){
        List<LlenaComboValoresDto> listDivisa=new ArrayList<LlenaComboValoresDto>();
        try{
             listDivisa=objInversionesValoresDao.consultarDivisa();
        }catch (Exception e) {
			 bitacora.insertarRegistro(new Date().toString()+" "+Bitacora.getStackTrace(e)+"P:Inversiones, C:InversionesValoresBusinessImpl+ M:ConsultarDivisa");
		}
		return listDivisa;
	}
	
	public Map<String, Object> insertarModificarValores( boolean bNuevo, boolean bModifi, List<MantenimientoValoresDto> listVal ){
		
		Map<String, Object> mapRet = new HashMap<String, Object>();
		
		int iRes = objInversionesValoresDao.buscaValores(listVal);
		
		try{
			if( bNuevo ) {
				
				if(iRes==0){
					
				    iRes = objInversionesValoresDao.insertarValores(listVal);
				    
				    if(iRes != 0) mapRet.put("msgUsuario", "Datos registrados!!"           );
				    else          mapRet.put("msgUsuario", "Error al registrar los datos!!");
				    
				}else{
					
					mapRet.put("msgUsuario", "Error el registro ya existe");
					
				}
				
				
			}else if(bModifi){
				
				iRes = objInversionesValoresDao.modificarValores(listVal);
				
				if(iRes != 0) mapRet.put("msgUsuario", "Datos modificados!!");
				else          mapRet.put("msgUsuario", "Error al modificar los datos!!");
			}
			
			mapRet.put("terminado",iRes);
		
		}catch(Exception e){
		      bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)+ "P:Inversiones, C:InversionesValoresBusinessImpl, M:insertarModificarValores");
		      mapRet.put("msgUsuario", "Ocurrio un error durante la operación");
		}
		
		return mapRet;
	}
	
	public Map<String, Object> eliminarValores(String sIdValor){
		Map<String, Object> mapRet = new HashMap<String, Object>();
		List<MantenimientoValoresDto> listOrdenInv =  new ArrayList<MantenimientoValoresDto>();
		try{
			int iAfecVal = 0;
			
			iAfecVal = objInversionesValoresDao.existeValorAsociadoAPapel(sIdValor);
			
			if(iAfecVal>0){
				mapRet.put("msgUsuario","Existen Papeles asociados a este Valor.");
				return mapRet;
			}
			
			if(listOrdenInv.size() > 0)
			{
				mapRet.put("msgUsuario","Este contrato no puede ser eliminado ya que tiene ordenes pendientes");
				return mapRet;
			}
			iAfecVal = objInversionesValoresDao.eliminarValores(sIdValor);
			if(iAfecVal > 0)
				mapRet.put("msgUsuario", "Datos eliminados correctamente");
			else
				mapRet.put("msgUsuario", "Ocurrio un error al eliminar");
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)+ "P:Inversiones, C:InversionesValoresBusinessImpl, M:eliminarValores");
			mapRet.put("msgUsuario", "Ocurrio una excepción al eliminar");
		}
		return mapRet;
	}
	public InversionesValoresDao getObjInversionesValoresDao() {
		return objInversionesValoresDao;
	}
	public void setObjInversionesValoresDao(InversionesValoresDao objInversionesValoresDao) {
		this.objInversionesValoresDao = objInversionesValoresDao;
	}
	
}
