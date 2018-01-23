package com.webset.set.inversiones.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;

import com.webset.set.financiamiento.dao.FinanciamientoModificacionCDao;
import com.webset.set.financiamiento.dto.Amortizaciones;
import com.webset.set.inversiones.dao.PosicionInversionDao;
import com.webset.set.inversiones.dto.PosicionInversionDto;
import com.webset.set.inversiones.middleware.service.PosicionInversionService;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.ConsultasGenerales;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utilerias.GlobalSingleton;
import com.webset.set.utilerias.dto.LlenaComboGralDto;



public class PosicionInversionBusinessImpl implements PosicionInversionService {
	private PosicionInversionDao posicionInversionDao;
	
	public PosicionInversionDao getPosicionInversionDao() {
		return posicionInversionDao;
	}

public void setPosicionInversionDao(PosicionInversionDao posicionInversionDao) {
		this.posicionInversionDao = posicionInversionDao;
	}


	private JdbcTemplate jdbcTemplate;
	Bitacora bitacora=new Bitacora();
	ConsultasGenerales consultasGenerales;
	Funciones funciones= new Funciones();
	GlobalSingleton globalSingleton;


	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}
	

	
	
	public void setDataSource(DataSource dataSource)
	{
		try
		{
			this.jdbcTemplate = new JdbcTemplate(dataSource);
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Personas, C: ConsultaPersonasDaoImpl, M:setDataSource");
		}
	}


	public GlobalSingleton getGlobalSingleton() {
		return globalSingleton;
	}


	public void setGlobalSingleton(GlobalSingleton globalSingleton) {
		this.globalSingleton = globalSingleton;
	}


	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public List<LlenaComboGralDto> consultarEmpresa(boolean bExistentes) {
			List<LlenaComboGralDto> listLineas= new ArrayList<LlenaComboGralDto>();
		
		try{
			System.out.println("DaoEmpresa:..:"+bExistentes);
			listLineas = posicionInversionDao.consultarEmpresa(bExistentes);
		}catch(Exception e){ 
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PosicionInversion C: PosicionInversionImpl M: consultarEmpresa");
		}
		return listLineas;
	}

	@Override
	public List<LlenaComboGralDto> consultarDivisa(int empresa) {
		List<LlenaComboGralDto> listLineas= new ArrayList<LlenaComboGralDto>();
		
		try{
			
			listLineas = posicionInversionDao.consultarDivisa(empresa);
		}catch(Exception e){ 
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PosicionInversion C: PosicionInversionImpl M: consultarEmpresa");
		}
		return listLineas;
	}

	@Override
	public List<LlenaComboGralDto> consultarBanco(int emp, String divisa) {
		List<LlenaComboGralDto> listLineas= new ArrayList<LlenaComboGralDto>();
		
		try{
			listLineas = posicionInversionDao.consultarBanco(emp,divisa);
		}catch(Exception e){ 
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PosicionInversion C: PosicionInversionImpl M: consultarEmpresa");
		}
		return listLineas;
	}

	@Override
	public List<LlenaComboGralDto> consultarChequera(int emp, String divisa, int banco) {
	List<LlenaComboGralDto> listLineas= new ArrayList<LlenaComboGralDto>();
		
		try{
			listLineas = posicionInversionDao.consultarChequera(emp,divisa,banco);
		}catch(Exception e){ 
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PosicionInversion C: PosicionInversionImpl M: consultarEmpresa");
		}
		return listLineas;
	}

	@Override
	public String consultarInstitucion() {
String  listLineas="";
		
		try{
			listLineas = posicionInversionDao.consultarInstitucion();
		}catch(Exception e){ 
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PosicionInversion C: PosicionInversionImpl M: consultarEmpresa");
		}
		return listLineas;
	}

	@Override
	public List<PosicionInversionDto> llenarGrid1(String fecha, int caja, String institucion, int empresa,String divisa, int banco, String chequera) {
		List<PosicionInversionDto> listDisp = new ArrayList<PosicionInversionDto>();
	
		
		try{
			listDisp = posicionInversionDao.consultarVencimientos(fecha, caja, institucion, empresa,divisa, banco, chequera);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P: Financiamiento C: FinanciamientoModificacionCImpl M: consultarLineas");
				}
		return listDisp;
	}

	@Override
	public List<PosicionInversionDto> llenarGrid2(String fecha, int caja, String institucion, int empresa,
			String divisa, int banco, String chequera) {
List<PosicionInversionDto> listDisp = new ArrayList<PosicionInversionDto>();
	
		
		try{
			listDisp = posicionInversionDao.consultarBarridos(fecha, caja, institucion, empresa,divisa, banco, chequera);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P: Financiamiento C: FinanciamientoModificacionCImpl M: consultarLineas");
				}
		return listDisp;
	}

	@Override
	public List<PosicionInversionDto> llenarGrid3(String fecha, int caja, String institucion, int empresa,
			String divisa, int banco, String chequera) {
List<PosicionInversionDto> listDisp = new ArrayList<PosicionInversionDto>();
	
		
		try{
			listDisp = posicionInversionDao.consultarPorInvertir(fecha, caja, institucion, empresa,divisa, banco, chequera);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P: Financiamiento C: FinanciamientoModificacionCImpl M: consultarLineas");
				}
		return listDisp;
	}

	@Override
	public List<PosicionInversionDto> llenarGrid4(String fecha, int caja, String institucion, int empresa,
			String divisa, int banco, String chequera) {
List<PosicionInversionDto> listDisp = new ArrayList<PosicionInversionDto>();
	
		
		try{
			listDisp = posicionInversionDao.consultarInversiones(fecha, caja, institucion, empresa,divisa, banco, chequera);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P: Financiamiento C: FinanciamientoModificacionCImpl M: consultarLineas");
				}
		return listDisp;
	}

	@Override
	public int consultarSaldoInicial(int banco, String chequera) {
 int listLineas=0;
		
		try{
			listLineas = posicionInversionDao.saldoInicial(banco, chequera);
		}catch(Exception e){ 
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PosicionInversion C: PosicionInversionImpl M: consultarEmpresa");
		}
		return listLineas;
	}




	
	
	
	
	
	
	
	
	
}
