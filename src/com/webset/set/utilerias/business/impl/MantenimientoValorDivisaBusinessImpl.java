package com.webset.set.utilerias.business.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.dao.MantenimientoValorDivisaDao;
import com.webset.set.utilerias.service.MantenimientoValorDivisaService;
import com.webset.set.utileriasmod.dto.MantenimientoValorDivisaDto;

public class MantenimientoValorDivisaBusinessImpl implements MantenimientoValorDivisaService{
	private Bitacora bitacora = new Bitacora();
	private MantenimientoValorDivisaDao objMantenimientoValorDivisaDao;	
	private List<MantenimientoValorDivisaDto> recibeDatos = new ArrayList<MantenimientoValorDivisaDto>();

	public List<MantenimientoValorDivisaDto> llenaComboTasas(){		
		return objMantenimientoValorDivisaDao.llenaComboTasas();
	}
	
	public List<MantenimientoValorDivisaDto> llenaComboDivisas(){
		return objMantenimientoValorDivisaDao.llenaComboDivisas();
	}
	
	public List<MantenimientoValorDivisaDto> llenaGridTasas(String fecha){
			
		recibeDatos = objMantenimientoValorDivisaDao.llenaGridTasas(fecha);
		
		if (recibeDatos.size() <= 0)
		{
			//Esta llamada es para cuando no hay informacion dada de alta para el dia de hoy, se coloca un 1 por defaul
			recibeDatos = objMantenimientoValorDivisaDao.llenaGridTasasDefault();			
		}
		return recibeDatos;
	}
	
	public List<MantenimientoValorDivisaDto> llenaGridDivisas(String fecha){
		recibeDatos = objMantenimientoValorDivisaDao.llenaGridDivisas(fecha);
		
		if (recibeDatos.size() <= 0)
		{
			//Esta llamada es para cuando no hay informacion dada de alta para el dia de hoy, se coloca un 1 por defaul
			recibeDatos = objMantenimientoValorDivisaDao.llenaGridDivisasDefault();			
		}
		return recibeDatos;
	}
	
	
	public String insertaActualiza(List<Map<String, String>> gridTasas, List<Map<String, String>> gridDivisas, String fecha){
		int contadorCorrecto = 0;
		int contadorErroneo = 0;
		String respuestaFinal = "";
		int respuesta = 0;
		
		try{			
			/*Se empieza a validar el grid de las tasas, 
			se busca en el catalogo por dia si no se encuentra se hace un insert si existe el registro en esta fecha se hace un update*/
			
			for (int t = 0; t < gridTasas.size(); t++)
			{
				recibeDatos = objMantenimientoValorDivisaDao.buscaRegistroTasa(fecha, gridTasas.get(t).get("idTasa"));
				System.out.println(recibeDatos.size() + "tama�o tasas");
				if (recibeDatos.size() != 0)
				{
					respuesta = objMantenimientoValorDivisaDao.actualizaTasa(gridTasas, t, fecha);
					System.out.println(respuesta + "cuantos se actualizaron de tasas");
					if (respuesta > 0)
						contadorCorrecto = contadorCorrecto + 1;
					else
						contadorErroneo = contadorErroneo + 1;
					
				}
				else{
					respuesta = objMantenimientoValorDivisaDao.insertaTasa(gridTasas, t, fecha);
					System.out.println(respuesta + "cuantos se insertaron de tasas");
					if (respuesta > 0)
						contadorCorrecto = contadorCorrecto + 1;
					else
						contadorErroneo = contadorErroneo + 1;
				}				
			}
						
			/*Se empieza a validar el grid de las divisas, 
			se busca en el catalogo por dia si no se encuentra se hace un insert si existe el registro en esta fecha se hace un update*/
			
			for (int d = 0; d < gridDivisas.size(); d++)
			{
				recibeDatos = objMantenimientoValorDivisaDao.buscaRegistroDivisa(fecha, gridDivisas.get(d).get("idDivisa"));
				System.out.println(recibeDatos.size() + "tama�o result");
				if (recibeDatos.size() != 0)
				{
					System.out.println("Entro al encuentra de divisa");
					respuesta = objMantenimientoValorDivisaDao.actualizaDivisa(gridDivisas, d, fecha);
					
					if (respuesta > 0)
						contadorCorrecto = contadorCorrecto + 1;
					else
						contadorErroneo = contadorErroneo + 1;
					
				}
				else{
					respuesta = objMantenimientoValorDivisaDao.insertaDivisa(gridDivisas, d, fecha);
					System.out.println("Entro al insert de divisa");
					if (respuesta > 0)
						contadorCorrecto = contadorCorrecto + 1;
					else
						contadorErroneo = contadorErroneo + 1;
				}				
			}
		
			//Se manda la respuesta al usuario dependiendo los insert o update realizados anteriormente
			if (contadorErroneo > 1)
				respuestaFinal = "Ocurrio un error durante el registro";
			else{ 				
				respuestaFinal = "Datos Registrados";
				
				//Se actualiza la tabla Fechas, colocando al campo estatus_sist = 1
				objMantenimientoValorDivisaDao.actualizaEstatusFecha();
				}
			
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C: MantenimientoValorDivisaBusinessImpl, M: insertarActualiza");
		}return respuestaFinal;
	}
	
	//*********************************************************************
	public MantenimientoValorDivisaDao getObjMantenimientoValorDivisaDao() {
		return objMantenimientoValorDivisaDao;
	}

	public void setObjMantenimientoValorDivisaDao(
			MantenimientoValorDivisaDao objMantenimientoValorDivisaDao) {
		this.objMantenimientoValorDivisaDao = objMantenimientoValorDivisaDao;
	}
	
}
