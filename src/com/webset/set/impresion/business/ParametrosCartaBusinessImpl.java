package com.webset.set.impresion.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.webset.set.impresion.dao.ParametrosCartaDao;
import com.webset.set.impresion.dto.ParametrosCartaDto;
import com.webset.set.impresion.service.ParametrosCartaService;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.ConsultasGenerales;
import com.webset.set.utilerias.Funciones;


public class ParametrosCartaBusinessImpl <objParametrosCartaDao> implements ParametrosCartaService {
	
private ParametrosCartaDao objParametrosCartaDao;
	

	public ParametrosCartaDao getObjParametrosCartaDao() {
	return objParametrosCartaDao;
}


public void setObjParametrosCartaDao(ParametrosCartaDao objParametrosCartaDao) {
	this.objParametrosCartaDao = objParametrosCartaDao;
}


	Bitacora bitacora;
	Funciones funciones = new Funciones();
	ParametrosCartaDto objParametrosCartaDto = new ParametrosCartaDto();
	ConsultasGenerales consultasGenerales;
	List<ParametrosCartaDto> recibeDatos = new ArrayList<ParametrosCartaDto>();
	int recibeResultadoEntero = 0;
	
	
	@Override
	public List<ParametrosCartaDto> llenaBanco() {
		return objParametrosCartaDao.llenaBanco();
	}
	
	@Override
	public String insertaCarta(List<Map<String, String>> registro) {
		System.out.println("Entra a bussines");
		int recibeDatoEntero= 0;
		String mensaje;
		
		
		ParametrosCartaDto objParametrosCartaDto = new ParametrosCartaDto();
		
		objParametrosCartaDto.setIdBanco(Integer.parseInt(registro.get(0).get("idBanco")));
		objParametrosCartaDto.setTipo(registro.get(0).get("tipo")==null || registro.get(0).get("tipo").equals("")
				? "": registro.get(0).get("tipo"));
		objParametrosCartaDto.setB1(registro.get(0).get("b1")==null || registro.get(0).get("b1").equals("")
				? "": registro.get(0).get("b1"));
		objParametrosCartaDto.setB2(registro.get(0).get("b2")==null || registro.get(0).get("b2").equals("")
				? "": registro.get(0).get("b2"));
		objParametrosCartaDto.setB3(registro.get(0).get("b3")==null || registro.get(0).get("b3").equals("")
				? "": registro.get(0).get("b3"));
		objParametrosCartaDto.setB4(registro.get(0).get("b4")==null || registro.get(0).get("b4").equals("")
				? "": registro.get(0).get("b4"));
		objParametrosCartaDto.setC1(registro.get(0).get("c1")==null || registro.get(0).get("c1").equals("")
				? "": registro.get(0).get("c1"));
		objParametrosCartaDto.setC2(registro.get(0).get("c2")==null || registro.get(0).get("c2").equals("")
				? "": registro.get(0).get("c2"));
		objParametrosCartaDto.setC3(registro.get(0).get("c3")==null || registro.get(0).get("c3").equals("")
				? "": registro.get(0).get("c3"));
		objParametrosCartaDto.setC4(registro.get(0).get("c4")==null || registro.get(0).get("c4").equals("")
				? "": registro.get(0).get("c4"));
		objParametrosCartaDto.setC5(registro.get(0).get("c5")==null || registro.get(0).get("c5").equals("")
				? "": registro.get(0).get("c5"));
		objParametrosCartaDto.setC6(registro.get(0).get("c6")==null || registro.get(0).get("c6").equals("")
				? "": registro.get(0).get("c6"));
		objParametrosCartaDto.setC7(registro.get(0).get("c7")==null || registro.get(0).get("c7").equals("")
				? "": registro.get(0).get("c7"));
		objParametrosCartaDto.setC8(registro.get(0).get("c8")==null || registro.get(0).get("c8").equals("")
				? "": registro.get(0).get("c8"));
		objParametrosCartaDto.setC9(registro.get(0).get("c9")==null || registro.get(0).get("c9").equals("")
				? "": registro.get(0).get("c9"));
		objParametrosCartaDto.setC10(registro.get(0).get("c10")==null || registro.get(0).get("c10").equals("")
				? "": registro.get(0).get("c10"));
		objParametrosCartaDto.setC11(registro.get(0).get("c11")==null || registro.get(0).get("c11").equals("")
				? "": registro.get(0).get("c11"));
		objParametrosCartaDto.setC12(registro.get(0).get("c12")==null || registro.get(0).get("c12").equals("")
				? "": registro.get(0).get("c12"));
		objParametrosCartaDto.setC13(registro.get(0).get("c13")==null || registro.get(0).get("c13").equals("")
				? "": registro.get(0).get("c13"));
		objParametrosCartaDto.setC14(registro.get(0).get("c14")==null || registro.get(0).get("c14").equals("")
				? "": registro.get(0).get("c14"));
		objParametrosCartaDto.setC15(registro.get(0).get("c15")==null || registro.get(0).get("c15").equals("")
				? "": registro.get(0).get("c15"));
		
		recibeDatoEntero = objParametrosCartaDao.existeCarta(objParametrosCartaDto.getIdBanco(), objParametrosCartaDto.getTipo(), objParametrosCartaDto );
		
		if (recibeDatoEntero > 0){
			mensaje = "Esta carta ya cuenta con parametros";
		}
		else{				
			recibeDatoEntero = objParametrosCartaDao.insertaCarta(objParametrosCartaDto);
			
			if (recibeDatoEntero>0) {
				mensaje = "Se almacenaron los parametros correctamente";
			} else {
				mensaje = "Ocurrio un error al almacenar los parametros";
			}
			
		
		}
		return mensaje;
	}
	
	@Override
	public List<ParametrosCartaDto> verificaRegistro(int idBanco, String tipo) {
			System.out.println("VerificaBunisses");
			List<ParametrosCartaDto> result = new ArrayList<ParametrosCartaDto>();
			try {
				result = objParametrosCartaDao.verificaRegistro(idBanco, tipo);
			} catch (Exception e) {
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + 
						"P: Utilerias, C: MapeoBusinessImpl, M: verificaRegistro");
			}
			return result;
		
	}
	
	
	@Override
	public List<ParametrosCartaDto> llenaGrid() {
		
			return objParametrosCartaDao.llenaGrid();
	}


	@Override
	public String eliminaCarta(int idBanco, String tipo) {
		String mensaje = "";
		int recibeDatoEntero = 0;
		try{
			recibeDatoEntero = objParametrosCartaDao.eliminaCarta(idBanco, tipo);
			
			if (recibeDatoEntero > 0)
				mensaje = "Se elimino con exito los parametros de la carta";
			else
				mensaje = "Ocurrio un error durante el proceso";
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Impresion, C: ParametrosCartaBusinessImpl, M: eliminaCarta");
		}return mensaje;
	}


	@Override
	public List<ParametrosCartaDto> obtieneCarta(int idBanco, String tipo) {
		List<ParametrosCartaDto> result = new ArrayList<ParametrosCartaDto>();
		try {
			result = objParametrosCartaDao.obtieneCarta(idBanco, tipo);
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + 
					"P: Impresion, C: ParametrosCartaBusinessImpl, M: obtieneCarta");
		}
		return result;
	}


	@Override
	public String actualizaCarta(List<Map<String, String>> registro, String idBanco, String tipo) {
		System.out.println("Entra a bussines");
		String resultado = "";
		String mensaje="";
			try {
				
				ParametrosCartaDto objParametrosCartaDto = new ParametrosCartaDto();
				
				objParametrosCartaDto.setIdBanco(Integer.parseInt(registro.get(0).get("idBanco")));
				objParametrosCartaDto.setTipo(registro.get(0).get("tipo")==null || registro.get(0).get("tipo").equals("")
						? "": registro.get(0).get("tipo"));
				objParametrosCartaDto.setB1(registro.get(0).get("b1")==null || registro.get(0).get("b1").equals("")
						? "": registro.get(0).get("b1"));
				objParametrosCartaDto.setB2(registro.get(0).get("b2")==null || registro.get(0).get("b2").equals("")
						? "": registro.get(0).get("b2"));
				objParametrosCartaDto.setB3(registro.get(0).get("b3")==null || registro.get(0).get("b3").equals("")
						? "": registro.get(0).get("b3"));
				objParametrosCartaDto.setB4(registro.get(0).get("b4")==null || registro.get(0).get("b4").equals("")
						? "": registro.get(0).get("b4"));
				objParametrosCartaDto.setC1(registro.get(0).get("c1")==null || registro.get(0).get("c1").equals("")
						? "": registro.get(0).get("c1"));
				objParametrosCartaDto.setC2(registro.get(0).get("c2")==null || registro.get(0).get("c2").equals("")
						? "": registro.get(0).get("c2"));
				objParametrosCartaDto.setC3(registro.get(0).get("c3")==null || registro.get(0).get("c3").equals("")
						? "": registro.get(0).get("c3"));
				objParametrosCartaDto.setC4(registro.get(0).get("c4")==null || registro.get(0).get("c4").equals("")
						? "": registro.get(0).get("c4"));
				objParametrosCartaDto.setC5(registro.get(0).get("c5")==null || registro.get(0).get("c5").equals("")
						? "": registro.get(0).get("c5"));
				objParametrosCartaDto.setC6(registro.get(0).get("c6")==null || registro.get(0).get("c6").equals("")
						? "": registro.get(0).get("c6"));
				objParametrosCartaDto.setC7(registro.get(0).get("c7")==null || registro.get(0).get("c7").equals("")
						? "": registro.get(0).get("c7"));
				objParametrosCartaDto.setC8(registro.get(0).get("c8")==null || registro.get(0).get("c8").equals("")
						? "": registro.get(0).get("c8"));
				objParametrosCartaDto.setC9(registro.get(0).get("c9")==null || registro.get(0).get("c9").equals("")
						? "": registro.get(0).get("c9"));
				objParametrosCartaDto.setC10(registro.get(0).get("c10")==null || registro.get(0).get("c10").equals("")
						? "": registro.get(0).get("c10"));
				objParametrosCartaDto.setC11(registro.get(0).get("c11")==null || registro.get(0).get("c11").equals("")
						? "": registro.get(0).get("c11"));
				objParametrosCartaDto.setC12(registro.get(0).get("c12")==null || registro.get(0).get("c12").equals("")
						? "": registro.get(0).get("c12"));
				objParametrosCartaDto.setC13(registro.get(0).get("c13")==null || registro.get(0).get("c13").equals("")
						? "": registro.get(0).get("c13"));
				objParametrosCartaDto.setC14(registro.get(0).get("c14")==null || registro.get(0).get("c14").equals("")
						? "": registro.get(0).get("c14"));
				objParametrosCartaDto.setC15(registro.get(0).get("c15")==null || registro.get(0).get("c15").equals("")
						? "": registro.get(0).get("c15"));
				System.out.println(registro.get(0).get("c15"));
				resultado=objParametrosCartaDao.actualizaCarta(objParametrosCartaDto, idBanco, tipo);
				
			
					if (resultado == "Exito al modificar el mapeo")
						mensaje = "El registro se actualizo correctamente";
					else
						mensaje = "Ocurrio un error durante la actualizaci\u00f2n";
					System.out.println("10");
				
				
			} catch (Exception e) {
				e.printStackTrace();
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Impresion, C: ParametrosCartaBusinessImpl, M: actualizaCarta");
			}return mensaje;
			
		
	}
	
	
	
	
	
}
