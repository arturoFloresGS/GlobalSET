package com.webset.set.personas.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.webset.set.personas.dao.ConsultaPersonasDao;
import com.webset.set.personas.dto.ConsultaPersonasDto;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.ConsultasGenerales;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utilerias.GlobalSingleton;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.utils.tools.Utilerias;

public class ConsultaPersonasDaoImpl implements ConsultaPersonasDao{
	private static Logger logger = Logger.getLogger(ConsultaPersonasDao.class);
	
	ConsultasGenerales objConsultasGenerales;
	Funciones funciones = new Funciones();
	JdbcTemplate jdbcTemplate;
	Bitacora bitacora = new Bitacora();
	String sql = "";	
//	public int seleccionarFolioReal(String tipoFolio){
//		int r=0;
//		try{
//			objConsultasGenerales = new ConsultasGenerales(jdbcTemplate);
//			r= objConsultasGenerales.seleccionarFolioReal(tipoFolio);
//		}
//		catch(Exception e){
//			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasDaoImpl, M: seleccionarFolioReal");
//		}
//		return r;
//		
//	}
//	
//	public int actualizarFolioReal(String tipoFolio){	
//		int r=0;
//		try{
//			objConsultasGenerales = new ConsultasGenerales(jdbcTemplate);
//			r = objConsultasGenerales.actualizarFolioReal(tipoFolio);
//		}
//		catch(Exception e){
//			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasDaoImpl, M: actualizarFolioReal");
//		}return r;
//		
//	}
	
	@SuppressWarnings("unchecked")
	public Map actualizaChequerasProv(int noPersona){
		Map res=new HashMap<>();
		try{
			objConsultasGenerales = new ConsultasGenerales(jdbcTemplate);
			res = objConsultasGenerales.actualizaChequerasProv(noPersona);
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasDaoImpl, M: actualizaChequerasProv");
		}return res;
		
	}
		
	@SuppressWarnings("unchecked")
	public List<ConsultaPersonasDto> llenaComboEmpresas(){
		List<ConsultaPersonasDto> listaResultado = new ArrayList<ConsultaPersonasDto>();
		sql = "";
		try{
			sql = "Select * from empresa";
			
			listaResultado = jdbcTemplate.query(sql, new RowMapper(){
				public ConsultaPersonasDto mapRow(ResultSet rs, int idx)throws SQLException{
					ConsultaPersonasDto campos = new ConsultaPersonasDto();
					campos.setNoEmpresa(rs.getInt("no_empresa"));
					campos.setNomEmpresa(rs.getString("nom_empresa"));
					return campos;
				}
			});
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasDaoImpl, M: llenaComboEmpresas");
		}return listaResultado;
	}
	
	@SuppressWarnings("unchecked")
	public List<ConsultaPersonasDto> llenaComboTipoPersona(){
		List<ConsultaPersonasDto> listaResultado = new ArrayList<ConsultaPersonasDto>();
		StringBuffer sql = new StringBuffer();
		try{
			sql.append("Select * from cat_tipo_persona where id_tipo_persona ='E' or id_tipo_persona ='P' or id_tipo_persona ='K'");
			
			
			listaResultado = jdbcTemplate.query(sql.toString(), new RowMapper(){
				public ConsultaPersonasDto mapRow(ResultSet rs, int idx)throws SQLException{
					ConsultaPersonasDto campos = new ConsultaPersonasDto();
					campos.setIdTipoPersona(rs.getString("id_tipo_persona"));
					campos.setDescTipoPersona(rs.getString("desc_tipo_persona"));
					return campos;
				}
			});
		}
		catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasDaoImpl, M: llenaComboTipoPersonas");
		}return listaResultado;
	}
	@SuppressWarnings("unchecked")
	public List<ConsultaPersonasDto> llenaComboDivisaCP(){
		List<ConsultaPersonasDto> listaResultado = new ArrayList<ConsultaPersonasDto>();
		StringBuffer sql = new StringBuffer();
		try{
			sql.append("Select * from cat_divisa");
			
			
			listaResultado = jdbcTemplate.query(sql.toString(), new RowMapper(){
				public ConsultaPersonasDto mapRow(ResultSet rs, int idx)throws SQLException{
					ConsultaPersonasDto campos = new ConsultaPersonasDto();
					campos.setDivisa(rs.getString("desc_divisa"));
					campos.setIdDivisa(rs.getString("id_divisa"));
					return campos;
				}
			});
		}
		catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasDaoImpl, M: llenaComboTipoPersonas");
		}return listaResultado;
	}

	@SuppressWarnings("unchecked")
	public List<ConsultaPersonasDto> llenaGrid(String tipoPersona,String equivalePersona, String razonSocial, String paterno, 
											   String materno, String nombre,  boolean inactivas){

		List<ConsultaPersonasDto> listaResultado = new ArrayList<ConsultaPersonasDto>();
		String empresa = "";
		sql = "";
		try{
			sql = "Select case when p.razon_social = '' or p.razon_social is null then p.nombre + ' ' + p.paterno + ' ' + p.materno else p.razon_social end as nombre,";
			sql += "\n (select desc_estatus as estatus from cat_estatus where clasificacion = 'PER' and id_estatus = p.id_estatus_per) as id_estatus, p.* from persona p";
			sql += "\n where ";
			if (inactivas)
				sql += "\n p.id_estatus_per = 'I' ";
			else
				sql += "\n p.id_estatus_per <> 'I' ";
			
			//Se valida el tipo de persona
			if (!tipoPersona.equals(""))
				sql += "\n and p.id_tipo_persona = '"+Utilerias.validarCadenaSQL( tipoPersona) +"' ";
			
			//Clasificacion del tipo de persona
			if (tipoPersona.equals("F") || tipoPersona.equals("B"))
				sql += "\n and p.no_empresa = 1 ";
			else if(tipoPersona.equals("R") || tipoPersona.equals("P")) {
				sql += "\n and p.no_empresa in (552) ";
				if(tipoPersona.equals("P")) {
					System.out.println("si es proveedor");
					
				}
			}else if (tipoPersona.equals("C") || tipoPersona.equals("E")){
				
			}
			
			if(tipoPersona.equals("P")) {
				if (!razonSocial.equals("")){
					sql += "\n and p.razon_social like '%"+Utilerias.validarCadenaSQL( razonSocial )+"%' ";
				}
				if(!equivalePersona.equals("")){
					sql += "\n and p.no_persona = '"+ Utilerias.validarCadenaSQL(equivalePersona) + "' ";
				}
				
			}else{
			
			//Se valida el equivale_persona
			
			//Se valida la razon social
			if (!razonSocial.equals(""))
				sql += "\n and p.razon_social like '%"+ Utilerias.validarCadenaSQL(razonSocial) +"%' ";
			
			//Se valida el apellido paterno
			if (!paterno.equals(""))
				sql += "\n and p.paterno like '%"+ Utilerias.validarCadenaSQL(paterno) +"%' ";
			
			//Se valida el apellido materno
			if (!materno.equals(""))
				sql += "\n and p.materno like '%"+ Utilerias.validarCadenaSQL(materno) +"%' ";
			
			//Se valida el nombre
			if (!nombre.equals(""))
				sql += "\n and p.nombre like '%"+Utilerias.validarCadenaSQL( nombre) +"%' ";
			
			if(!equivalePersona.equals(""))
				sql += "\n and p.no_persona = '"+ Utilerias.validarCadenaSQL(equivalePersona) + "'";
			}
			//se validan las fechas de rango
			
			
			sql += "order by p.no_empresa ";
			System.out.println("-----------------");
			System.out.println("imprime query");
			System.out.println("-----------------");
			System.out.println(sql);

			listaResultado = jdbcTemplate.query(sql, new RowMapper <ConsultaPersonasDto>(){
				public ConsultaPersonasDto mapRow(ResultSet rs, int idx)throws SQLException{
					ConsultaPersonasDto campos = new ConsultaPersonasDto();
                   
					campos.setEquivalePersona(rs.getString("equivale_persona"));
					campos.setNombre(rs.getString("nombre"));
					campos.setNoEmpresa(rs.getInt("no_empresa"));
					campos.setIdTipoPersona(rs.getString("id_tipo_persona"));
					campos.setNoPersona(rs.getInt("no_persona"));
					campos.setFisicaMoral(rs.getString("pers_juridica"));					System.out.println("WebR"+rs.getString("id_estatus"));
					campos.setEstatus(rs.getString("id_estatus"));
					campos.setIdEstatus(rs.getString("id_estatus_per"));
			
					return campos;
				}
			});
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasDaoImpl, M: llenaGrid");
		}
		
		return listaResultado;
	}
	@SuppressWarnings("unchecked")
	public List<ConsultaPersonasDto> llenaGridCP(String tipoPersona,String noPersona){

		List<ConsultaPersonasDto> listaResultado = new ArrayList<ConsultaPersonasDto>();
		String empresa = "";
		sql = "";
		try{
			sql = "select desc_banco,* from ctas_banco C,cat_banco CB where id_tipo_persona='"+tipoPersona+"' and no_persona="+noPersona+" and C.id_banco=CB.id_banco";

			
			System.out.println("-----------------");
			System.out.println("imprime query");
			System.out.println("-----------------");
			System.out.println(sql);

			listaResultado = jdbcTemplate.query(sql, new RowMapper <ConsultaPersonasDto>(){
				public ConsultaPersonasDto mapRow(ResultSet rs, int idx)throws SQLException{
					ConsultaPersonasDto campos = new ConsultaPersonasDto();
                   
					campos.setBancoI(rs.getString("desc_banco"));
					campos.setChequera(rs.getString("id_chequera"));
					campos.setDivisa(rs.getString("id_divisa"));
					campos.setDescChequera(rs.getString("desc_chequera"));
					campos.setSucursal(rs.getInt("sucursal"));
					campos.setPlaza(rs.getInt("plaza"));	
					campos.setClabe(rs.getString("id_clabe"));
					campos.setSwift(rs.getString("swift_code"));
					campos.setAba(rs.getString("aba"));
					campos.setBankTrue(rs.getInt("id_bank_true"));
					campos.setBankCorresponding(rs.getInt("id_bank_corresponding"));
					campos.setChequeraTrue(rs.getString("id_chequera_true"));
					campos.setBancoAnterior(rs.getString("id_banco_anterior"));
					campos.setChequeraAnterior(rs.getString("id_chequera_anterior"));
					campos.setIdBanco(rs.getInt("id_banco"));
					campos.setEspecial(rs.getString("especiales"));

					return campos;
				}
			});
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasDaoImpl, M: llenaGridCP");
		}
		
		return listaResultado;
	}
	
	@SuppressWarnings("unchecked")
	public List<ConsultaPersonasDto> comboEstadoCivil(){
		List<ConsultaPersonasDto> listaResultado = new ArrayList<ConsultaPersonasDto>();
		sql = "";
		try{
			sql = "Select * from cat_edo_civil";
			
			listaResultado = jdbcTemplate.query(sql, new RowMapper(){
				public ConsultaPersonasDto mapRow(ResultSet rs, int idx)throws SQLException{
					ConsultaPersonasDto campos = new ConsultaPersonasDto();
					campos.setIdEstadoCivil(rs.getString("id_edo_civil"));
					campos.setDescEstadoCivil(rs.getString("desc_edo_civil"));
					return campos;
				}
			});
		}
		
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasDaoImpl, M: comboEstadoCivil");
		}return listaResultado;
	}
	
	@SuppressWarnings("unchecked")
	public List<ConsultaPersonasDto> llenaComboGrupo(){
		List<ConsultaPersonasDto> listaResultado = new ArrayList<ConsultaPersonasDto>();
		sql = "";
		
		try{
			sql = "Select * from cat_grupo_flujo ";
			
			listaResultado = jdbcTemplate.query(sql, new RowMapper(){
				public ConsultaPersonasDto mapRow(ResultSet rs, int idx)throws SQLException{
					ConsultaPersonasDto campos = new ConsultaPersonasDto();
					campos.setIdGrupo(rs.getInt("id_grupo_flujo"));
					campos.setDescGrupo(rs.getString("desc_grupo_flujo"));
					return campos;
				}
			});
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasDaoImpl, M: llenaComboGrupo");
		}return listaResultado;
	}
	
	@SuppressWarnings("unchecked")
	public List<ConsultaPersonasDto> llenaActividadEconomica(){
		List<ConsultaPersonasDto> listaResultado = new ArrayList<ConsultaPersonasDto>();
		sql = "";
		try {
			sql = "select * from cat_act_economica ";
			
			listaResultado = jdbcTemplate.query(sql, new RowMapper(){
				public ConsultaPersonasDto mapRow(ResultSet rs, int idx)throws SQLException{
					ConsultaPersonasDto campos = new ConsultaPersonasDto();
					campos.setIdActividadEconomica(rs.getInt("id_act_economica"));
					campos.setDescActividadEconomica(rs.getString("desc_act_economica"));
					return campos;
				}
			});
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasDaoImpl, M: llenaActividadEconomica");
		}return listaResultado;
	}
	
	@SuppressWarnings("unchecked")
	public List<ConsultaPersonasDto> llenaActividadGenerica(){
		List<ConsultaPersonasDto> listaResultado = new ArrayList<ConsultaPersonasDto>();
		sql = "";
		try{
			sql = "Select * from cat_act_generica";
			//sql = "Select * from cat_act_generica Select * from cat_act_g";
			
			listaResultado = jdbcTemplate.query(sql, new RowMapper(){
				public ConsultaPersonasDto mapRow(ResultSet rs, int idx)throws SQLException{
					ConsultaPersonasDto campos = new ConsultaPersonasDto();
					campos.setIdActividadGenerica(rs.getInt("id_act_generica"));
					campos.setDescActividadGenerica(rs.getString("desc_act_generica"));
					return campos;
				}
			});
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasDaoImpl, M: llenaActividadGenerica");
		}return listaResultado;
	}
	
	@SuppressWarnings("unchecked")
	public List<ConsultaPersonasDto> llenaEstadoCivil(){
		List<ConsultaPersonasDto> listaResultado = new ArrayList<ConsultaPersonasDto>();
		sql = "";
		try{
			sql = "Select  * from cat_edo_civil ";
			
			listaResultado = jdbcTemplate.query(sql, new RowMapper(){
				public ConsultaPersonasDto mapRow(ResultSet rs, int idx)throws SQLException{
					ConsultaPersonasDto campos = new ConsultaPersonasDto();
					campos.setIdEstadoCivil(rs.getString("id_edo_civil"));
					campos.setDescEstadoCivil(rs.getString("desc_edo_civil"));
					return campos;
				}
			});
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasDaoImpl, M: llenaEstadoCivil");			
		}return listaResultado;
	}
	///////////////////////////////////////////////////////////
	
	@SuppressWarnings("unchecked")
	public List<ConsultaPersonasDto> llenaPais(){
		List<ConsultaPersonasDto> listaResultado = new ArrayList<ConsultaPersonasDto>();
		sql = "";
		try{
			sql = " select * from cat_pais";
			
			listaResultado = jdbcTemplate.query(sql, new RowMapper(){
				public ConsultaPersonasDto mapRow(ResultSet rs, int idx)throws SQLException{
					ConsultaPersonasDto campos = new ConsultaPersonasDto();
					campos.setIdPais(rs.getString("id_pais"));
					campos.setDescPais(rs.getString("desc_pais"));
					return campos;
				}
			});
		}
		catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasDaoImpl, M: llenaPais");
		}return listaResultado;
	}
	
	@SuppressWarnings("unchecked")
	public List<ConsultaPersonasDto> llenaEstado(){
		List<ConsultaPersonasDto> listaResultado = new ArrayList<ConsultaPersonasDto>();
		sql = "";
		try{
			sql = " select * from cat_estado";
			
			listaResultado = jdbcTemplate.query(sql, new RowMapper(){
				public ConsultaPersonasDto mapRow(ResultSet rs, int idx)throws SQLException{
					ConsultaPersonasDto campos = new ConsultaPersonasDto();
					campos.setIdEstado(rs.getString("id_estado"));
					campos.setDescEstado(rs.getString("desc_estado"));
					return campos;
				}
			});
		}
		catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasDaoImpl, M: llenaEstado");
		}return listaResultado;
	}
	
	@SuppressWarnings("unchecked")
	public List<ConsultaPersonasDto> llenaDireccion(){
		List<ConsultaPersonasDto> listaResultado = new ArrayList<ConsultaPersonasDto>();
		sql = "";
		try{
			sql = "Select * from cat_tipo_direccion";
			
			listaResultado = jdbcTemplate.query(sql, new RowMapper(){
				public ConsultaPersonasDto mapRow(ResultSet rs, int idx)throws SQLException{
					ConsultaPersonasDto campos = new ConsultaPersonasDto();
					campos.setIdDireccion(rs.getString("id_tipo_direccion"));
					campos.setDescDireccion(rs.getString("desc_tipo_direccion"));
					return campos;
				}
			});
		}
		catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasDaoImpl, M: llenaDireccion");
		}return listaResultado;
	}
	/////////////////////////////////////////////////////////
	
	@SuppressWarnings("unchecked")
	public List<ConsultaPersonasDto> llenaGiro(){
		List<ConsultaPersonasDto> listaResultado = new ArrayList<ConsultaPersonasDto>();
		sql = "";
		try{
			sql = "Select * from cat_giro ";
			
			listaResultado = jdbcTemplate.query(sql, new RowMapper(){
				public ConsultaPersonasDto mapRow(ResultSet rs, int idx)throws SQLException{
					ConsultaPersonasDto campos = new ConsultaPersonasDto();
					campos.setIdGiro(rs.getInt("id_giro"));
					campos.setDescGiro(rs.getString("desc_giro"));
					return campos;
				}
			});
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasDaoImpl, M: llenaGiro");
		}return listaResultado;
	}
	
	@SuppressWarnings("unchecked")
	public List<ConsultaPersonasDto> llenaCaja(){
		List<ConsultaPersonasDto> listaResultado = new ArrayList<ConsultaPersonasDto>();
		sql = "";
		try{
			sql = "select * from cat_caja ";
			
			listaResultado = jdbcTemplate.query(sql, new RowMapper(){
				public ConsultaPersonasDto mapRow(ResultSet rs, int idx)throws SQLException{
					ConsultaPersonasDto campos = new ConsultaPersonasDto();
					campos.setIdCaja(rs.getInt("id_caja"));
					campos.setDescCaja(rs.getString("desc_caja"));
					return campos;
				}
			});
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasDaoImpl, M: llenaCaja");
		}return listaResultado;
	}
	
	@SuppressWarnings("unchecked")
	public List<ConsultaPersonasDto> llenaRiesgo(){
		List<ConsultaPersonasDto> listaResultado = new ArrayList<ConsultaPersonasDto>();
		sql = "";
		try{
			sql = "Select * from cat_riesgo_empresa";
				
			listaResultado = jdbcTemplate.query(sql, new RowMapper(){
				public ConsultaPersonasDto mapRow(ResultSet rs, int idx)throws SQLException{
					ConsultaPersonasDto campos = new ConsultaPersonasDto();
					campos.setIdRiesgo(rs.getInt("id_riesgo_empresa"));
					campos.setDescRiesgo(rs.getString("desc_riesgo_empresa"));
					return campos;
				}
			});
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasDaoImpl, M: llenaRiesgo");
		}return listaResultado;
	}
	
	@SuppressWarnings("unchecked")
	public List<ConsultaPersonasDto> comboTamano(){
		List<ConsultaPersonasDto> listaResultado = new ArrayList<ConsultaPersonasDto>();
		sql = "";
		try{
			sql = "Select * from cat_tamano";
				
			listaResultado = jdbcTemplate.query(sql, new RowMapper(){
				public ConsultaPersonasDto mapRow(ResultSet rs, int idx)throws SQLException{
					ConsultaPersonasDto campos = new ConsultaPersonasDto();
					campos.setIdTamano(rs.getString("id_tamano"));
					campos.setDescTamano(rs.getString("desc_tamano"));
					return campos;
				}
			});
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasDaoImpl, M: comboTama\u00f1o");
		}return listaResultado;
	}
	
	@SuppressWarnings("unchecked")
	public List<ConsultaPersonasDto> comboCalidad(){
		List<ConsultaPersonasDto> listaResultado = new ArrayList<ConsultaPersonasDto>();
		sql = "";
		try{
			sql = "Select * from cat_calidad_empresa order by id_calidad_empresa ";
			
			listaResultado = jdbcTemplate.query(sql, new RowMapper(){
				public ConsultaPersonasDto mapRow(ResultSet rs, int idx)throws SQLException{
					ConsultaPersonasDto campos = new ConsultaPersonasDto();
					campos.setIdCalidad(rs.getString("id_calidad_empresa"));
					campos.setDescCalidad(rs.getString("desc_calidad_empresa"));
					return campos;
				}
			});
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasDaoImpl, M: comboCalidad");
		}return listaResultado;
	}
	
	@SuppressWarnings("unchecked")
	public List<ConsultaPersonasDto> comboTipoInmueble(){
		List<ConsultaPersonasDto> listaResultado = new ArrayList<ConsultaPersonasDto>();
		sql = "";
		try{
			sql = "Select * from cat_tipo_inmueble order by id_tipo_inmueble ";
			
			listaResultado = jdbcTemplate.query(sql, new RowMapper(){
				public ConsultaPersonasDto mapRow (ResultSet rs, int idx) throws SQLException{
					ConsultaPersonasDto campos = new ConsultaPersonasDto();
					campos.setIdInmueble(rs.getString("id_tipo_inmueble"));
					campos.setDescInmueble(rs.getString("desc_tipo_inmueble"));
					return campos;
				}
			});
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasDaoImpl, M: comboTipoInmueble");
		}return listaResultado;
	}
	
	@SuppressWarnings("unchecked")
	public List<ConsultaPersonasDto> comboFormaPago(){
		List<ConsultaPersonasDto> listaResultado = new ArrayList<ConsultaPersonasDto>();
		sql = "";
		
		try{
			sql = "Select * from cat_forma_pago order by id_forma_pago ";
			
			
			
			System.out.println(sql);
			
			listaResultado = jdbcTemplate.query(sql, new RowMapper(){
				public ConsultaPersonasDto mapRow(ResultSet rs, int idx)throws SQLException{
					ConsultaPersonasDto campos = new ConsultaPersonasDto();
					campos.setIdFormaPagoProv(rs.getInt("id_forma_pago"));
					campos.setDescFormaPagoProv(rs.getString("desc_forma_pago"));
					return campos;
				}
			});
				
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasDaoImpl, M: comboFormaPagoProv");
		}return listaResultado;
	}
	
	@SuppressWarnings("unchecked")
	public List<ConsultaPersonasDto> comboFormaPagoProv(int noPersona, int noEmpresa){
		List<ConsultaPersonasDto> listaResultado = new ArrayList<ConsultaPersonasDto>();
		sql = "";
		
		try{
//			sql = "Select * from cat_forma_pago order by id_forma_pago ";
			
			sql = "Select distinct coalesce(cfp.id_forma_pago, 0) as id_forma_pago, coalesce(cfp.desc_forma_pago, '') as desc_forma_pago ";
			sql += "\n from forma_pago_prov fpp, cat_forma_pago cfp ";
			sql += "\n where fpp.id_forma_pago = cfp.id_forma_pago ";
			
			if (noPersona != 0)
				sql += "\n and fpp.no_persona = "+Utilerias.validarCadenaSQL( noPersona) +" ";
			
			if (noEmpresa != 0)
				sql += "\n and fpp.no_empresa in (552, "+ Utilerias.validarCadenaSQL(noEmpresa) +") ";
			else
				sql += "\n and fpp.no_empresa in (552) ";
			
			sql += "\n and fpp.id_tipo_persona = 'P' ";
			sql += "\n order by cfp.id_forma_pago ";
			
			System.out.println(sql);
			
			listaResultado = jdbcTemplate.query(sql, new RowMapper(){
				public ConsultaPersonasDto mapRow(ResultSet rs, int idx)throws SQLException{
					ConsultaPersonasDto campos = new ConsultaPersonasDto();
					campos.setIdFormaPagoProv(rs.getInt("id_forma_pago"));
					campos.setDescFormaPagoProv(rs.getString("desc_forma_pago"));
					return campos;
				}
			});
				
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasDaoImpl, M: comboFormaPagoProv");
		}return listaResultado;
	}
	
	@SuppressWarnings("unchecked")
	public List<ConsultaPersonasDto> comboTEF (){
		List<ConsultaPersonasDto> listaResultado = new ArrayList<ConsultaPersonasDto>();
		sql = "";
		
		try{
			sql = "Select * from cat_contrato_tef ";
			
			listaResultado = jdbcTemplate.query(sql, new RowMapper(){
				public ConsultaPersonasDto mapRow(ResultSet rs, int idx)throws SQLException{
					ConsultaPersonasDto campos = new ConsultaPersonasDto();
					campos.setIdContrato(rs.getInt("id_contrato"));
					campos.setDescContrato(rs.getDouble("no_contrato"));
					return campos;
				}
			});
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasDaoImpl, M: comboTEF");
		}return listaResultado;
	}
	
	@SuppressWarnings("unchecked")
	public List<ConsultaPersonasDto> comboPayment (){
		List<ConsultaPersonasDto> listaResultado = new ArrayList<ConsultaPersonasDto>();
		sql = "";
		
		try{
			sql = "Select * from cat_contrato_mass_payment ";
			
			listaResultado = jdbcTemplate.query(sql, new RowMapper(){
				public ConsultaPersonasDto mapRow(ResultSet rs, int idx)throws SQLException{
					ConsultaPersonasDto campos = new ConsultaPersonasDto();
					campos.setIdContratoPayment(rs.getInt("id_contrato_mass"));
					campos.setDescContratoPayment(rs.getString("no_contrato_mass"));
					return campos;
				}
			});
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasDaoImpl, M: comboPayment");
		}return listaResultado;
	}
	
	@SuppressWarnings("unchecked")
	public List<ConsultaPersonasDto> obtienePersonaFisica(int noEmpresa, String noPersona, String tipoPersona){
		System.out.println("Entro a dao Obtiene Datos");
		List<ConsultaPersonasDto> listaResultado = new ArrayList<ConsultaPersonasDto>();
		sql = "";
		@SuppressWarnings("unused")
		SimpleDateFormat formato = new SimpleDateFormat("dd-MM-yyyy");
		
		try
		{
			System.out.println("Entra al obtiene datos dao");
			logger.debug("Entra: obtieneDatos");
			if (!tipoPersona.equals("E")) {
				System.out.println("Entra a diferente de empresa");
				sql = "Select p.no_empresa, p.no_persona, p.equivale_persona, p.paterno, p.materno, p.nombre, ";
				sql += "\n p.razon_social, p.nombre_corto, p.rfc, p.puesto, ";
				sql += "\n p.id_edo_civil, coalesce((select desc_edo_civil from cat_edo_civil where id_edo_civil = p.id_edo_civil), '')as desc_edo_civil, ";
				sql += "\n p.id_act_economica, coalesce((select desc_act_economica from cat_act_economica where id_act_economica = p.id_act_economica), '')as descActividadEconomica, ";
				sql += "\n p.id_giro, coalesce((select desc_giro from cat_giro where id_giro = p.id_giro), '') as desc_giro, ";
				sql += "\n p.id_act_generica,coalesce((select desc_act_generica from cat_act_generica where id_act_generica = p.id_act_generica), '')as descActividadGenerica, ";
				sql += "\n p.id_riesgo_empresa, coalesce((select desc_riesgo_empresa from cat_riesgo_empresa where id_riesgo_empresa = p.id_riesgo_empresa), '')as desc_riesgo, ";
				sql += "\n p.id_calidad_empresa, coalesce((select desc_calidad_empresa from cat_calidad_empresa where id_calidad_empresa = p.id_calidad_empresa), '')as desc_calidad, ";
				sql += "\n p.id_tamano, coalesce((select desc_tamano from cat_tamano where id_tamano = p.id_tamano), '')as desc_tamano, ";				
				sql += "\n p.id_tipo_inmueble, coalesce((select desc_tipo_inmueble from cat_tipo_inmueble where id_tipo_inmueble = p.id_tipo_inmueble), '')as desc_inmueble, ";
				sql += "\n coalesce(p.objeto_social, '') as objeto_social_c, coalesce(p.ventas_anuales, 0) as ventas_anuales, " ;
				sql += "\n coalesce(p.num_empleados, 0) as num_empleados, coalesce(dia_limite_pago, 0) as dia_limite_pago, ";
				sql += "\n coalesce(p.dia_recep_fact, 0) as dia_recep_fact, coalesce(p.no_acciones_a, 0) as no_acciones_a, ";
				sql += "\n coalesce(p.no_acciones_b, 0) as no_acciones_b, coalesce(p.no_acciones_c, 0) as no_acciones_c, ";
				sql += "\n coalesce(p.b_proveedor, '') as b_proveedor, coalesce(p.b_asociacion, '') as b_asociacion, ";
				sql += "\n coalesce(p.b_contrato_inversion, '') as b_contrato_inversion, p.sexo, p.referencia_cte, p.fec_ing, coalesce(p.id_grupo, '0') as id_grupo, coalesce(cg.desc_grupo, '') as desc_grupo, coalesce(p.id_rubro, 0) as id_rubro, coalesce(cr.desc_rubro, '') as desc_rubro, ";
				sql += "\n (select nom_empresa from empresa where no_empresa = substring(p.equivale_persona, 1, 4)) as nom_empresa ";
				sql += "\n from persona p left join cat_rubro cr on (p.id_rubro = cr.id_rubro) left join cat_grupo cg on (p.id_grupo = cr.id_grupo and cr.id_grupo = cg.id_grupo) ";
				
				if (tipoPersona.equals("P") || tipoPersona.equals("K")){
					sql += "\n where p.no_empresa = 552 ";					
					sql += "\n and p.equivale_persona = '"+Utilerias.validarCadenaSQL( noPersona) +"' ";
				}
				else{
					sql += "\n where p.no_empresa in ("+Utilerias.validarCadenaSQL( noEmpresa) +", 1) ";
					sql += "\n and p.no_persona = '"+Utilerias.validarCadenaSQL( noPersona) +"' ";
				}				
				sql += "\n and p.id_tipo_persona = '"+ Utilerias.validarCadenaSQL(tipoPersona) +"' ";
			}else{
				System.out.println(noEmpresa);
				System.out.println("Entra al obtiene datos dao en empresa");
				sql = "Select p.no_empresa, p.no_persona, p.equivale_persona, p.paterno, p.materno, p.nombre, ";
				sql += "\n p.razon_social, p.nombre_corto, p.rfc, p.puesto, ";
				sql += "\n p.id_edo_civil, coalesce((select desc_edo_civil from cat_edo_civil where id_edo_civil = p.id_edo_civil), '')as desc_edo_civil, ";
				sql += "\n p.id_act_economica, coalesce((select desc_act_economica from cat_act_economica where id_act_economica = p.id_act_economica), '')as descActividadEconomica, ";
				sql += "\n p.id_giro, coalesce((select desc_giro from cat_giro where id_giro = p.id_giro), '') as desc_giro, ";
				sql += "\n p.id_act_generica,coalesce((select desc_act_generica from cat_act_generica where id_act_generica = p.id_act_generica), '')as descActividadGenerica, ";
				sql += "\n p.id_riesgo_empresa, coalesce((select desc_riesgo_empresa from cat_riesgo_empresa where id_riesgo_empresa = p.id_riesgo_empresa), '')as desc_riesgo, ";
				sql += "\n p.id_calidad_empresa, coalesce((select desc_calidad_empresa from cat_calidad_empresa where id_calidad_empresa = p.id_calidad_empresa), '')as desc_calidad, ";
				sql += "\n p.id_tamano, coalesce((select desc_tamano from cat_tamano where id_tamano = p.id_tamano), '')as desc_tamano, ";				
				sql += "\n p.id_tipo_inmueble, coalesce((select desc_tipo_inmueble from cat_tipo_inmueble where id_tipo_inmueble = p.id_tipo_inmueble), '')as desc_inmueble, ";
				sql += "\n coalesce(p.objeto_social, '') as objeto_social_c, coalesce(p.ventas_anuales, 0) as ventas_anuales, " ;
				sql += "\n coalesce(p.num_empleados, 0) as num_empleados, coalesce(p.dia_limite_pago, 0) as dia_limite_pago, ";
				sql += "\n coalesce(p.dia_recep_fact, 0) as dia_recep_fact, coalesce(p.no_acciones_a, 0) as no_acciones_a, ";
				sql += "\n coalesce(p.no_acciones_b, 0) as no_acciones_b, coalesce(p.no_acciones_c, 0) as no_acciones_c, " ;
				sql += "\n coalesce(p.b_proveedor, '') as b_proveedor, coalesce(p.b_asociacion, '') as b_asociacion, ";
				sql += "\n coalesce(p.b_contrato_inversion, '') as b_contrato_inversion, p.sexo, p.referencia_cte, p.fec_ing, coalesce(p.id_grupo, '0') as id_grupo, coalesce(cg.desc_grupo, '') as desc_grupo, coalesce(p.id_rubro, 0) as id_rubro, coalesce(cr.desc_rubro, '') as desc_rubro, ";
				sql += "\n (select nom_empresa from empresa where no_empresa = substring(p.equivale_persona, 1, 4)) as nom_empresa ";
				sql += "\n from persona p left join cat_rubro cr on (p.id_rubro = cr.id_rubro) left join cat_grupo cg on (p.id_grupo = cr.id_grupo and cr.id_grupo = cg.id_grupo) ";
				sql += "\n where p.no_empresa = "+ Utilerias.validarCadenaSQL(noEmpresa )+" ";
				sql += "\n and p.no_persona = "+ Utilerias.validarCadenaSQL(noEmpresa )+" ";
				sql += "\n and p.id_tipo_persona = '"+Utilerias.validarCadenaSQL( tipoPersona )+"' ";
			}
			System.out.println(sql);
			logger.info("SQL: " + sql);
			
			listaResultado = jdbcTemplate.query(sql, new RowMapper(){
				public ConsultaPersonasDto mapRow(ResultSet rs, int idx)throws SQLException{
					ConsultaPersonasDto campos = new ConsultaPersonasDto();
					campos.setNoEmpresa(rs.getInt("no_empresa"));
					campos.setNoPersona(rs.getInt("no_persona"));
					//campos.setEquivalePersona(rs.getString("equivale_persona"));
					campos.setPaterno(rs.getString("paterno"));
					campos.setMaterno(rs.getString("materno"));
					campos.setNombre(rs.getString("nombre"));
					campos.setRazonSocial(rs.getString("razon_social"));
					campos.setNombreCorto(rs.getString("nombre_corto"));
					campos.setRfc(rs.getString("rfc"));

					campos.setIdGiro(rs.getInt("id_giro"));

					campos.setIdCalidad(rs.getString("id_calidad_empresa"));
					campos.setDescCalidad(rs.getString("desc_calidad"));

					campos.setIdInmueble(rs.getString("id_tipo_inmueble"));
					campos.setDescInmueble(rs.getString("desc_inmueble"));
					campos.setIdGrupo(rs.getInt("id_grupo"));
					campos.setDescGrupo(rs.getString("desc_grupo"));

					campos.setSexo(rs.getString("sexo"));

					return campos;
				}
			});
		}
		catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasDaoImpl, M: obtienePersonaFisica");
			logger.error(e.toString());
			return null;
		}
		logger.debug("Sale: obtieneDatos");
		System.out.println("???????????????????????????");
		System.out.println(listaResultado.get(0).getDescGrupo());
		System.out.println("???????????????????????????");
		return listaResultado;
	}
	
	
	@SuppressWarnings("unchecked")
	public List<ConsultaPersonasDto> obtieneDatos(int noEmpresa, String noPersona, String tipoPersona){
		System.out.println("Entro a dao Obtiene Datos");
		List<ConsultaPersonasDto> listaResultado = new ArrayList<ConsultaPersonasDto>();
		sql = "";
		@SuppressWarnings("unused")
		SimpleDateFormat formato = new SimpleDateFormat("dd-MM-yyyy");
		
		try
		{
			System.out.println("Entra al obtiene datos dao");
			logger.debug("Entra: obtieneDatos");
			if (!tipoPersona.equals("E")) {
				System.out.println("Entra a diferente de empresa");
				sql = "Select p.no_empresa, p.no_persona, p.equivale_persona, p.paterno, p.materno, p.nombre, ";
				sql += "\n p.razon_social, p.nombre_corto, p.rfc, p.puesto, ";
				sql += "\n p.id_edo_civil, coalesce((select desc_edo_civil from cat_edo_civil where id_edo_civil = p.id_edo_civil), '')as desc_edo_civil, ";
				sql += "\n p.id_act_economica, coalesce((select desc_act_economica from cat_act_economica where id_act_economica = p.id_act_economica), '')as descActividadEconomica, ";
				sql += "\n p.id_giro, coalesce((select desc_giro from cat_giro where id_giro = p.id_giro), '') as desc_giro, ";
				sql += "\n p.id_act_generica,coalesce((select desc_act_generica from cat_act_generica where id_act_generica = p.id_act_generica), '')as descActividadGenerica, ";
				sql += "\n p.id_riesgo_empresa, coalesce((select desc_riesgo_empresa from cat_riesgo_empresa where id_riesgo_empresa = p.id_riesgo_empresa), '')as desc_riesgo, ";
				sql += "\n p.id_calidad_empresa, coalesce((select desc_calidad_empresa from cat_calidad_empresa where id_calidad_empresa = p.id_calidad_empresa), '')as desc_calidad, ";
				sql += "\n p.id_tamano, coalesce((select desc_tamano from cat_tamano where id_tamano = p.id_tamano), '')as desc_tamano, ";				
				sql += "\n p.id_tipo_inmueble, coalesce((select desc_tipo_inmueble from cat_tipo_inmueble where id_tipo_inmueble = p.id_tipo_inmueble), '')as desc_inmueble, ";
				sql += "\n coalesce(p.objeto_social, '') as objeto_social_c, coalesce(p.ventas_anuales, 0) as ventas_anuales, " ;
				sql += "\n coalesce(p.num_empleados, 0) as num_empleados, coalesce(dia_limite_pago, 0) as dia_limite_pago, ";
				sql += "\n coalesce(p.dia_recep_fact, 0) as dia_recep_fact, coalesce(p.no_acciones_a, 0) as no_acciones_a, ";
				sql += "\n coalesce(p.no_acciones_b, 0) as no_acciones_b, coalesce(p.no_acciones_c, 0) as no_acciones_c, ";
				sql += "\n coalesce(p.b_proveedor, '') as b_proveedor, coalesce(p.b_asociacion, '') as b_asociacion, ";
				sql += "\n coalesce(p.b_contrato_inversion, '') as b_contrato_inversion, p.sexo, p.referencia_cte, p.fec_ing, coalesce(p.id_grupo, '0') as id_grupo, coalesce(cg.desc_grupo, '') as desc_grupo, coalesce(p.id_rubro, 0) as id_rubro, coalesce(cr.desc_rubro, '') as desc_rubro, ";
				sql += "\n (select nom_empresa from empresa where convert(varchar,no_empresa) = substring(p.equivale_persona, 1, 4)) as nom_empresa ";
				sql += "\n from persona p left join cat_rubro cr on (p.id_rubro = cr.id_rubro) left join cat_grupo cg on (p.id_grupo = cr.id_grupo and cr.id_grupo = cg.id_grupo) ";
				
				if (tipoPersona.equals("P") || tipoPersona.equals("K")){
					sql += "\n where p.no_empresa = 552 ";					
					sql += "\n and p.equivale_persona = '"+ Utilerias.validarCadenaSQL(noPersona) +"' ";
				}
				else{
					sql += "\n where p.no_empresa in ("+ Utilerias.validarCadenaSQL(noEmpresa) +", 1) ";
					sql += "\n and p.no_persona = '"+ Utilerias.validarCadenaSQL(noPersona) +"' ";
				}				
				sql += "\n and p.id_tipo_persona = '"+ Utilerias.validarCadenaSQL(tipoPersona) +"' ";
			}else{
				System.out.println(noEmpresa);
				System.out.println("Entra al obtiene datos dao en empresa");
				sql = "Select p.no_empresa, p.no_persona, p.equivale_persona, p.paterno, p.materno, p.nombre, ";
				sql += "\n p.razon_social, p.nombre_corto, p.rfc, p.puesto, ";
				sql += "\n p.id_edo_civil, coalesce((select desc_edo_civil from cat_edo_civil where id_edo_civil = p.id_edo_civil), '')as desc_edo_civil, ";
				sql += "\n p.id_act_economica, coalesce((select desc_act_economica from cat_act_economica where id_act_economica = p.id_act_economica), '')as descActividadEconomica, ";
				sql += "\n p.id_giro, coalesce((select desc_giro from cat_giro where id_giro = p.id_giro), '') as desc_giro, ";
				sql += "\n p.id_act_generica,coalesce((select desc_act_generica from cat_act_generica where id_act_generica = p.id_act_generica), '')as descActividadGenerica, ";
				sql += "\n p.id_riesgo_empresa, coalesce((select desc_riesgo_empresa from cat_riesgo_empresa where id_riesgo_empresa = p.id_riesgo_empresa), '')as desc_riesgo, ";
				sql += "\n p.id_calidad_empresa, coalesce((select desc_calidad_empresa from cat_calidad_empresa where id_calidad_empresa = p.id_calidad_empresa), '')as desc_calidad, ";
				sql += "\n p.id_tamano, coalesce((select desc_tamano from cat_tamano where id_tamano = p.id_tamano), '')as desc_tamano, ";				
				sql += "\n p.id_tipo_inmueble, coalesce((select desc_tipo_inmueble from cat_tipo_inmueble where id_tipo_inmueble = p.id_tipo_inmueble), '')as desc_inmueble, ";
				sql += "\n coalesce(p.objeto_social, '') as objeto_social_c, coalesce(p.ventas_anuales, 0) as ventas_anuales, " ;
				sql += "\n coalesce(p.num_empleados, 0) as num_empleados, coalesce(p.dia_limite_pago, 0) as dia_limite_pago, ";
				sql += "\n coalesce(p.dia_recep_fact, 0) as dia_recep_fact, coalesce(p.no_acciones_a, 0) as no_acciones_a, ";
				sql += "\n coalesce(p.no_acciones_b, 0) as no_acciones_b, coalesce(p.no_acciones_c, 0) as no_acciones_c, " ;
				sql += "\n coalesce(p.b_proveedor, '') as b_proveedor, coalesce(p.b_asociacion, '') as b_asociacion, ";
				sql += "\n coalesce(p.b_contrato_inversion, '') as b_contrato_inversion, p.sexo, p.referencia_cte, p.fec_ing, coalesce(p.id_grupo, '0') as id_grupo, coalesce(cg.desc_grupo, '') as desc_grupo, coalesce(p.id_rubro, 0) as id_rubro, coalesce(cr.desc_rubro, '') as desc_rubro, ";
				sql += "\n (select nom_empresa from empresa where no_empresa = substring(p.equivale_persona, 1, 4)) as nom_empresa ";
				sql += "\n from persona p left join cat_rubro cr on (p.id_rubro = cr.id_rubro) left join cat_grupo cg on (p.id_grupo = cr.id_grupo and cr.id_grupo = cg.id_grupo) ";
//				sql += "\n left join medios_contacto mc on p.no_persona= mc.no_persona";
				sql += "\n where p.no_empresa = "+Utilerias.validarCadenaSQL( noEmpresa) +" ";
				sql += "\n and p.no_persona = "+ Utilerias.validarCadenaSQL(noEmpresa )+" ";
				sql += "\n and p.id_tipo_persona = '"+Utilerias.validarCadenaSQL( tipoPersona) +"' ";
			}
			System.out.println(sql);
			logger.info("SQL: " + sql);
			
			listaResultado = jdbcTemplate.query(sql, new RowMapper(){
				public ConsultaPersonasDto mapRow(ResultSet rs, int idx)throws SQLException{
					ConsultaPersonasDto campos = new ConsultaPersonasDto();
					campos.setNoEmpresa(rs.getInt("no_empresa"));
					campos.setNoPersona(rs.getInt("no_persona"));
					campos.setEquivalePersona(rs.getString("equivale_persona"));
					campos.setPaterno(rs.getString("paterno"));
					campos.setMaterno(rs.getString("materno"));
					campos.setNombre(rs.getString("nombre"));
					campos.setRazonSocial(rs.getString("razon_social"));
					campos.setNombreCorto(rs.getString("nombre_corto"));
					campos.setRfc(rs.getString("rfc"));
//					campos.setPuesto(rs.getString("puesto"));
//					campos.setIdEstadoCivil(rs.getString("id_edo_civil"));
//					campos.setDescEstadoCivil(rs.getString("desc_edo_civil"));
//					campos.setIdActividadEconomica(rs.getInt("id_act_economica"));
//					campos.setDescActividadEconomica(rs.getString("descActividadEconomica"));
					campos.setFechaIngreso(rs.getString("fec_ing").substring(0, 10));
					//System.out.println();
					//fecha = formatoDeFecha.format(formatoDeFecha.parse(fecha));
//					campos.setIdActividadGenerica(rs.getInt("id_act_generica"));
//					campos.setDescActividadGenerica(rs.getString("descActividadGenerica"));
//					campos.setBAsociacion(rs.getString("b_asociacion"));
//					campos.setBProveedor(rs.getString("b_proveedor"));
//					campos.setBContratoInversion(rs.getString("b_contrato_inversion"));
					//campos.setFechaIngreso(rs.getString("fec_ing"));
//					campos.setIdDireccion(rs.getString("id_tipo_direccion"));
//					campos.setDescDireccion(rs.getString("desc_tipo_direccion"));
					campos.setIdGiro(rs.getInt("id_giro"));
//					campos.setDescGiro(rs.getString("desc_giro"));
//					campos.setIdRiesgo(rs.getInt("id_riesgo_empresa"));
//					campos.setDescRiesgo(rs.getString("desc_riesgo"));
					campos.setIdCalidad(rs.getString("id_calidad_empresa"));
					campos.setDescCalidad(rs.getString("desc_calidad"));
//					campos.setIdTamano(rs.getString("id_tamano"));
//					campos.setDescTamano(rs.getString("desc_tamano"));
					campos.setIdInmueble(rs.getString("id_tipo_inmueble"));
					campos.setDescInmueble(rs.getString("desc_inmueble"));
					campos.setIdGrupo(rs.getInt("id_grupo"));
					campos.setDescGrupo(rs.getString("desc_grupo"));
//					campos.setObjetoSocial(rs.getString("objeto_social_c"));
//					campos.setVentasAnuales(rs.getDouble("ventas_anuales"));
//					campos.setNoEmpleados(rs.getInt("num_empleados"));
//					campos.setDiaLimite(rs.getInt("dia_limite_pago"));
//					campos.setDiaRecepcion(rs.getInt("dia_recep_fact"));
//					campos.setAccionA(rs.getInt("no_acciones_a"));
//					campos.setAccionB(rs.getInt("no_acciones_b"));
//					campos.setAccionC(rs.getInt("no_acciones_c"));
//					//campos.setEstatus(rs.getString("id_estatus_per"));
					campos.setSexo(rs.getString("sexo"));
					campos.setConcentradora(rs.getString("b_contrato_inversion"));
					campos.setReferencia(rs.getString("referencia_cte"));
//					campos.setSGrupoRubro(rs.getInt("id_grupo"));
//					campos.setSRubro(rs.getInt("id_rubro"));
//					campos.setSDescGrupoRubro(rs.getString("desc_grupo"));
//					campos.setSDescRubro(rs.getString("desc_rubro"));
//					campos.setNomEmpresa(rs.getString("nom_empresa"));
					return campos;
				}
			});
		}
		catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasDaoImpl, M: obtieneDatos");
			logger.error(e.toString());
			return null;
		}
		logger.debug("Sale: obtieneDatos");
		System.out.println("???????????????????????????");
		System.out.println(listaResultado.get(0).getDescGrupo());
		System.out.println("???????????????????????????");
		return listaResultado;
	}
	
	public String configuraSet(int indice){
		String res="";
		try{
			objConsultasGenerales= new ConsultasGenerales(jdbcTemplate);
			res = objConsultasGenerales.consultarConfiguraSet(indice);
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasDaoImpl, M: configuraSet");
		}return res;
		
	}
	
	@SuppressWarnings("unchecked")
	public String proveedorBasico(int noEmpresa, int noPersona){
		List<ConsultaPersonasDto> listaResultado = new ArrayList<ConsultaPersonasDto>();
		String cadena = "";		
		String sql = "";
		try{
			sql = "Select coalesce(b_servicios, '') as b_servicios from proveedor ";
			sql += "\n where no_empresa in (552, "+ Utilerias.validarCadenaSQL(noPersona) +") ";
			sql += "\n and no_proveedor = "+ Utilerias.validarCadenaSQL(noPersona) +" ";
			
			System.out.println(sql);
			
			listaResultado = jdbcTemplate.query(sql, new RowMapper(){
				public ConsultaPersonasDto mapRow(ResultSet rs, int idx)throws SQLException{
					ConsultaPersonasDto campos = new ConsultaPersonasDto();
					campos.setBServicios(rs.getString("b_servicios"));
					return campos;
				}
			});	
			
			if (listaResultado.size() > 0)
				cadena = listaResultado.get(0).getBServicios();
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasDaoImpl, M: proveedorBasico");
		}return cadena;
	}
	
	@SuppressWarnings("unchecked")
	public List<ConsultaPersonasDto> obtieneCaja (int noEmpresa){
		System.out.println("LLega a obtiene caja dao");
		List<ConsultaPersonasDto> listaResultado = new ArrayList<ConsultaPersonasDto> ();
		sql = "";
		
		try{
			sql = "Select coalesce(e.id_caja, 0) as id_caja, coalesce(c.desc_caja, '') as desc_caja from empresa e, cat_caja c ";
			sql += "\n where e.no_empresa = "+Utilerias.validarCadenaSQL( noEmpresa) +" ";
			sql += "\n and e.id_caja = c.id_caja ";
			
			System.out.println(sql);
			listaResultado = jdbcTemplate.query(sql, new RowMapper(){
				public ConsultaPersonasDto mapRow(ResultSet rs, int idx) throws SQLException{
					ConsultaPersonasDto campos = new ConsultaPersonasDto();
					campos.setIdCaja(rs.getInt("id_caja"));
					campos.setDescCaja(rs.getString("desc_caja"));
					return campos;
				}
			});
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasDaoImpl, M: obtieneCaja");
		}return listaResultado;
	}
	

	public List<ConsultaPersonasDto> obtieneDireccion(int noEmpresa, String persona, String tipoPersona){
		List<ConsultaPersonasDto> listaResultado = new ArrayList<ConsultaPersonasDto>();
		System.out.println("Entra a obtiene direccion");
		StringBuffer sb= new StringBuffer();
		sb.append(" select id_tipo_direccion, calle_no, colonia, id_cp, \n"); 
		sb.append("deleg_municipio, ciudad, id_estado, id_pais from \n");
		sb.append("direccion where no_persona= "+Utilerias.validarCadenaSQL(persona)+" \n");
		System.out.println(sb);
		try{
			
			listaResultado = jdbcTemplate.query(sb.toString(), new RowMapper<ConsultaPersonasDto>(){
				public ConsultaPersonasDto mapRow(ResultSet rs, int idx)throws SQLException{
					ConsultaPersonasDto campos = new ConsultaPersonasDto();
					campos.setIdTipoD(rs.getString("id_tipo_direccion"));
					campos.setCalle(rs.getString("calle_no"));
					campos.setColonia(rs.getString("colonia"));
					campos.setCp(rs.getString("id_cp"));
					campos.setDelegacion(rs.getString("deleg_municipio"));
					campos.setCiudad(rs.getString("ciudad"));
					campos.setEstado(rs.getString("id_estado"));
					campos.setPais(rs.getString("id_pais"));
					return campos;
				}
			});
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasDaoImpl, M: obtieneDireccion");
		}return listaResultado;
	}
	
	@SuppressWarnings("unchecked")
	public List<ConsultaPersonasDto> obtenerDatosEmpresa(int noEmpresa){
		List<ConsultaPersonasDto> listaResultado = new ArrayList<ConsultaPersonasDto>();
		sql = "";
		try{
			System.out.println("|||||||||||||||||||||||||||||||||||||");
			System.out.println("entra a concetradora");
			System.out.println("|||||||||||||||||||||||||||||||||||||");
			sql = "Select e.b_cxc, e.b_concentradora, e.b_division, e.b_pag_cruzados_aut, ";
			sql += "\n (select desc_grupo_flujo from cat_grupo_flujo where id_grupo_flujo = e.id_grupo_flujo) as desc_grupo, e.* from empresa e";
			sql += "\n where e.no_empresa = "+Utilerias.validarCadenaSQL( noEmpresa )+" ";
			System.out.println(sql);
			listaResultado = jdbcTemplate.query(sql, new RowMapper(){
				public ConsultaPersonasDto mapRow(ResultSet rs, int idx)throws SQLException{
					ConsultaPersonasDto campos = new ConsultaPersonasDto();
					campos.setExporta(rs.getString("b_cxc"));
					campos.setConcentradora(rs.getString("b_concentradora"));
					campos.setDivision(rs.getString("b_division"));
					campos.setPagoCruzado(rs.getString("b_pag_cruzados_aut"));
					campos.setIdGrupo(rs.getInt("id_grupo_flujo"));
					campos.setDescGrupo(rs.getString("desc_grupo"));
					return campos;
				}
			});
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasDaoImpl, M: obtenerDatosEmpresa");
		}return listaResultado;
	}
	
	@SuppressWarnings("unchecked")
	public List<ConsultaPersonasDto> validaGrupoEmpresa(int noEmpresa){
		List<ConsultaPersonasDto> listaResultado = new ArrayList<ConsultaPersonasDto>();
		sql = "";
		try{
			sql = "Select no_empresa from grupo_empresa ";
			sql += "\n where no_empresa = "+Utilerias.validarCadenaSQL( noEmpresa) +" ";
			
			listaResultado = jdbcTemplate.query(sql, new RowMapper(){
				public ConsultaPersonasDto mapRow(ResultSet rs, int idx)throws SQLException{
					ConsultaPersonasDto campos = new ConsultaPersonasDto();
					campos.setNoEmpresa(rs.getInt("no_empresa"));
					return campos;
				}
			});
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasDaoImpl, M: validaGrupoEmpresa");
		}return listaResultado;
	}
	
	@SuppressWarnings("unchecked")
	public List<ConsultaPersonasDto> validaDivisionesEmpresa(int noEmpresa){
		List<ConsultaPersonasDto> listaResultado = new ArrayList<ConsultaPersonasDto>();
		sql = "";
		try{
			sql = "Select count(id_division) as divisiones from cat_division ";
			sql += "\n where no_empresa = "+Utilerias.validarCadenaSQL( noEmpresa) +" ";
			
			listaResultado = jdbcTemplate.query(sql, new RowMapper(){
				public ConsultaPersonasDto mapRow(ResultSet rs, int idx)throws SQLException{
					ConsultaPersonasDto campos = new ConsultaPersonasDto();
					campos.setNoDivisiones(rs.getInt("divisiones"));
					return campos;
				}
			});
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasDaoImpl, M: validaDivisionEmpresa");
		}return listaResultado;
	}
	
	public int actualizaPersonaFisica(ConsultaPersonasDto obj){
		int resultado = 0;
		sql = "";
		String idTamano = obj.getIdTamano() != null ? obj.getIdTamano().toString() : "";
		
		try{
			
			sql = "Update persona Set ";
			sql += "\n fec_modif = "+ obj.getFechaHoy() +", ";
			
			if (obj.getPaterno()==null){
				sql += "\n paterno = NULL, ";
				System.out.println("Entra a actualiza datos");
			}else
				sql += "\n paterno = '"+ Utilerias.validarCadenaSQL(obj.getPaterno()) +"', ";
				
			if (obj.getMaterno()==null)
				sql += "\n materno = NULL,"; 
			else
				sql += "\n materno = '"+Utilerias.validarCadenaSQL( obj.getMaterno()) +"',"; 
				
			if (obj.getNombre()==null)
				sql += "\n nombre = NULL, ";
			else
				sql += "\n nombre = '"+Utilerias.validarCadenaSQL( obj.getNombre()) +"', ";
			
			if (obj.getRazonSocial()==null)
				sql += "\n razon_social = NULL,";
			else
				sql += "\n razon_social = '"+ Utilerias.validarCadenaSQL(obj.getRazonSocial()) +"',";
				
			sql += "\n id_edo_civil = '"+ 'N' +"', ";
			if (obj.getSexo() == "M")
				sql += "\n sexo = '"+ 'M' +"',";
			else
				sql += "\n sexo = '"+ 'F' +"',"; 
			sql += " nombre_corto = '"+ Utilerias.validarCadenaSQL(obj.getNombreCorto()) +"', ";
			sql += "\n rfc = '"+Utilerias.validarCadenaSQL( obj.getRfc()) +"', fec_ing = "+ Utilerias.validarCadenaSQL(obj.getFechaIngreso()) +", ";
			
			sql += "\n puesto = '"+ Utilerias.validarCadenaSQL(obj.getPuesto()) +"', id_act_economica = "+ Utilerias.validarCadenaSQL(obj.getIdActividadEconomica()) +", ";
			sql += "\n id_act_generica = "+Utilerias.validarCadenaSQL( obj.getIdActividadGenerica()) +", id_giro = "+ Utilerias.validarCadenaSQL(obj.getIdGiro()) +", ";
			sql += "\n id_riesgo_empresa = "+ Utilerias.validarCadenaSQL(obj.getIdRiesgo()) +", id_calidad_empresa = '"+ 0 +"', ";
			sql += "\n id_tamano = '"+Utilerias.validarCadenaSQL( idTamano) +"', id_tipo_inmueble = '"+ 0 +"', ";
			sql += "\n objeto_social = '"+ Utilerias.validarCadenaSQL(obj.getObjetoSocial()) +"', ventas_anuales = "+ Utilerias.validarCadenaSQL(obj.getVentasAnuales()) +", ";
			
			sql += "\n num_empleados = "+ Utilerias.validarCadenaSQL(obj.getNoEmpleados()) +", dia_limite_pago = "+ Utilerias.validarCadenaSQL(obj.getDiaLimite()) +", ";
			sql += "\n dia_recep_fact = "+ Utilerias.validarCadenaSQL(obj.getDiaRecepcion()) +", no_acciones_a = "+ Utilerias.validarCadenaSQL(obj.getAccionA()) +", ";
			sql += "\n no_acciones_b = "+ Utilerias.validarCadenaSQL(obj.getAccionB()) +", no_acciones_c = "+ Utilerias.validarCadenaSQL(obj.getAccionC()) +", ";
			sql += "\n usuario_modif = "+ Utilerias.validarCadenaSQL(obj.getUsuarioModif()) +", b_proveedor = '"+ 'S' +"', ";
			sql += "\n b_asociacion = '"+ 'N' +"', id_estatus_per = '"+ 'A' +"', ";
			sql += "\n b_contrato_inversion = '"+ 'N' +"' ";
			
			
				sql += "\n , referencia_cte = "+ null +" ";
			
			sql += "\n , id_grupo = "+ null +", id_rubro = "+ null+" ";
			
			
			sql += "\n where ";
			
			if (obj.getTipoPersona().equals("P")){
				if (obj.getNoEmpresa() == 0){
					sql += "\n no_empresa in (552, "+ Utilerias.validarCadenaSQL(obj.getNoEmpresa()) +") ";
				}
				else
					sql += "\n no_empresa in (552, "+ Utilerias.validarCadenaSQL(obj.getNoEmpresa()) +") ";	
			}else if (obj.getTipoPersona().equals("F")){
				sql += "\n no_empresa = 1 ";
				sql += "\n and no_persona = "+ Utilerias.validarCadenaSQL(obj.getNoPersona()) +" ";
			}else if(obj.getTipoPersona().equals("K")){
				sql += "\n no_empresa in (552, "+ Utilerias.validarCadenaSQL(obj.getNoEmpresa()) +") ";
				sql += "\n and no_persona = "+ Utilerias.validarCadenaSQL(obj.getNoPersona()) +" ";
			}else if(obj.getTipoPersona().equals("B")){
				System.out.println("Entra a elseif eB");
				sql += "\n no_empresa = 1 ";
				sql += "\n and no_persona = "+ Utilerias.validarCadenaSQL(obj.getNoPersona()) +" ";
			}else{
				sql += "\n no_empresa = "+ Utilerias.validarCadenaSQL(obj.getNoEmpresa()) +" ";	
			sql += "\n and no_persona = "+ Utilerias.validarCadenaSQL(obj.getNoEmpresa()) +" ";
			}
			sql += "\n and id_tipo_persona = '"+ Utilerias.validarCadenaSQL(obj.getTipoPersona()) +"' ";
			System.out.println("99999999999999999999999");
			System.out.println(sql);
			System.out.println("99999999999999999999999");
			resultado = jdbcTemplate.update(sql);			
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasDaoImpl, M: actualizaPersonaFisica");
		}return resultado;
	}
	
	public int actualizaDatos(ConsultaPersonasDto obj){
		int resultado = 0;
		sql = "";
		String idTamano = obj.getIdTamano() != null ? obj.getIdTamano().toString() : "";
		
		try{
			
			sql = "Update persona Set ";
			sql += "\n fec_modif = convert(datetime,'"+ Utilerias.validarCadenaSQL(obj.getFechaHoy()) +"',103), ";
			
			if (obj.getPaterno().equals("NULL"))
				sql += "\n paterno = NULL, ";
			else
				sql += "\n paterno = '"+ Utilerias.validarCadenaSQL(obj.getPaterno()) +"', ";
				
			if (obj.getMaterno().equals("NULL"))
				sql += "\n materno = NULL,"; 
			else
				sql += "\n materno = '"+ Utilerias.validarCadenaSQL(obj.getMaterno()) +"',"; 
				
			if (obj.getNombre().equals("NULL"))
				sql += "\n nombre = NULL, ";
			else
				sql += "\n nombre = '"+ Utilerias.validarCadenaSQL(obj.getNombre()) +"', ";
			
			if (obj.getRazonSocial().equals("NULL"))
				sql += "\n razon_social = NULL,";
			else
				sql += "\n razon_social = '"+ Utilerias.validarCadenaSQL(obj.getRazonSocial()) +"',";
				
			sql += "\n id_edo_civil = '"+ 'N' +"', ";
			sql += "\n sexo = '"+ 'M' +"', nombre_corto = '"+ Utilerias.validarCadenaSQL(obj.getNombreCorto()) +"', ";
			sql += "\n rfc = '"+ Utilerias.validarCadenaSQL(obj.getRfc()) +"', fec_ing = convert(datetime,'"+ Utilerias.validarCadenaSQL(obj.getFechaIngreso()) +"',103), ";
			
			sql += "\n puesto = '"+ obj.getPuesto() +"', id_act_economica = "+ obj.getIdActividadEconomica() +", ";
			sql += "\n id_act_generica = "+ Utilerias.validarCadenaSQL(obj.getIdActividadGenerica()) +", id_giro = "+ Utilerias.validarCadenaSQL(obj.getIdGiro()) +", ";
			sql += "\n id_riesgo_empresa = "+ Utilerias.validarCadenaSQL(obj.getIdRiesgo()) +", id_calidad_empresa = '"+ 0 +"', ";
			sql += "\n id_tamano = '"+ idTamano +"', id_tipo_inmueble = '"+ 0 +"', ";
			sql += "\n objeto_social = '"+obj.getObjetoSocial() +"', ventas_anuales = "+ obj.getVentasAnuales() +", ";
			
			sql += "\n num_empleados = "+ Utilerias.validarCadenaSQL(obj.getNoEmpleados()) +", dia_limite_pago = "+ Utilerias.validarCadenaSQL(obj.getDiaLimite()) +", ";
			sql += "\n dia_recep_fact = "+Utilerias.validarCadenaSQL( obj.getDiaRecepcion()) +", no_acciones_a = "+ Utilerias.validarCadenaSQL(obj.getAccionA()) +", ";
			sql += "\n no_acciones_b = "+ Utilerias.validarCadenaSQL(obj.getAccionB()) +", no_acciones_c = "+ Utilerias.validarCadenaSQL(obj.getAccionC()) +", ";
			sql += "\n usuario_modif = "+ Utilerias.validarCadenaSQL(obj.getUsuarioModif()) +", b_proveedor = '"+ 'S' +"', ";
			sql += "\n b_asociacion = '"+ 'N' +"', id_estatus_per = '"+ 'A' +"', ";
			if (obj.getTipoPersona().equals("E")) {
				sql += "\n b_contrato_inversion = '"+ Utilerias.validarCadenaSQL(obj.getConcentradora())+"' ";
			} else {
				sql += "\n b_contrato_inversion = '"+ 'N' +"' ";
			}
			
			
			
				sql += "\n , referencia_cte = "+ null +" ";
			
			sql += "\n , id_grupo = "+ null +", id_rubro = "+ null+" ";
			
			
			sql += "\n where ";
			
			if (obj.getTipoPersona().equals("P")){
				if (obj.getNoEmpresa() == 0){
					sql += "\n no_empresa in (552, "+Utilerias.validarCadenaSQL( obj.getNoEmpresa()) +") ";
				}
				else
					sql += "\n no_empresa in (552, "+Utilerias.validarCadenaSQL( obj.getNoEmpresa()) +") ";	
			}else if (obj.getTipoPersona().equals("F")){
				sql += "\n no_empresa = 1 ";
			}else if(obj.getTipoPersona().equals("K")){
				sql += "\n no_empresa in (552, "+ Utilerias.validarCadenaSQL(obj.getNoEmpresa()) +") ";	
				sql += "\n and no_persona = "+ Utilerias.validarCadenaSQL(obj.getNoPersona()) +" ";
			}else if(obj.getTipoPersona().equals("B")){
				System.out.println("Entra a elseif eB");
				sql += "\n no_empresa = 1 ";
				sql += "\n and no_persona = "+ Utilerias.validarCadenaSQL(obj.getNoPersona()) +" ";
			}else if(obj.getTipoPersona().equals("E")){
				sql += "\n no_empresa = "+ Utilerias.validarCadenaSQL(obj.getNoEmpresa()) +" ";
				sql += "\n and no_persona = "+ Utilerias.validarCadenaSQL(obj.getNoEmpresa()) +" ";
			}else{ 
			sql += "\n and no_persona = "+ Utilerias.validarCadenaSQL(obj.getNoEmpresa()) +" ";
			}
			sql += "\n and id_tipo_persona = '"+ Utilerias.validarCadenaSQL(obj.getTipoPersona()) +"' ";
			System.out.println("99999999999999999999999");
			System.out.println(sql);
			System.out.println("99999999999999999999999");
			resultado = jdbcTemplate.update(sql);			
		}
		catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasDaoImpl, M: actualizaDatos");
		}return resultado;
	}
	
	public int actualizaCaja(int idCaja, int noPersona){
		int resultado = 0;
		sql = "";
		try{
			sql = "Update empresa";
			
			if (idCaja == -1)
				sql += "\n set id_caja = null";
			else
				sql += "\n set id_caja = "+ Utilerias.validarCadenaSQL(idCaja) +" ";
			
			sql += "\n where no_empresa = "+Utilerias.validarCadenaSQL( noPersona) +" ";
			
			resultado = jdbcTemplate.update(sql);
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasDaoImpl, M: actualizaCaja");
		}return resultado;
	}
	
	public int actualizaProveedor(int noPersona, int noEmpresa, String proveedorBasico){
		int resultado = 0;
		sql = "";
		try{
			sql = "Update proveedor set ";
			sql += "\n b_servicios = '"+Utilerias.validarCadenaSQL( proveedorBasico) +"' ";
			sql += "\n where no_proveedor = "+ Utilerias.validarCadenaSQL(noPersona) +"";
			
			resultado = jdbcTemplate.update(sql);			
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasDaoImpl, M: actualizaProveedor");
		}return resultado;
	}
	
	//////////////////////////////////////
	public int actualizaDireccion (ConsultaPersonasDto obj){
		System.out.println("Entra a actualiza direccion");
		int resultado = 0;
		sql = "";
		GlobalSingleton globalSingleton = GlobalSingleton.getInstancia();
		try{
			sql = "Update direccion Set ";
			
			sql += "\n no_direccion = "+ 1 +", ";
			
			if (obj.getIdTipoD().equals("NULL"))
				sql += "\n id_tipo_direccion = NULL,"; 
			else
				sql += "\n id_tipo_direccion = '"+ Utilerias.validarCadenaSQL(obj.getIdTipoD()) +"',";
			
			if (obj.getPais().equals("NULL"))
				sql += "\n id_pais = NULL,"; 
			else
				sql += "\n id_pais = '"+ Utilerias.validarCadenaSQL(obj.getPais()) +"',"; 
			
			if (obj.getPais().equals("NULL"))
				sql += "\n id_estado = NULL,"; 
			else
				sql += "\n id_estado = '"+ Utilerias.validarCadenaSQL(obj.getEstado()) +"',"; 
			
			if (obj.getCp().equals("NULL"))
				sql += "\n id_cp = NULL,"; 
			else
				sql += "\n id_cp = '"+ Utilerias.validarCadenaSQL(obj.getCp()) +"',"; 
			
			if (obj.getCalle().equals("NULL"))
				sql += "\n calle_no = NULL,"; 
			else
				sql += "\n calle_no = '"+ Utilerias.validarCadenaSQL(obj.getCalle()) +"',"; 
			
			if (obj.getColonia().equals("NULL"))
				sql += "\n colonia = NULL,"; 
			else
				sql += "\n colonia = '"+ Utilerias.validarCadenaSQL(obj.getColonia()) +"',"; 
			
			if (obj.getDelegacion().equals("NULL"))
				sql += "\n deleg_municipio = NULL,"; 
			else
				sql += "\n deleg_municipio = '"+ Utilerias.validarCadenaSQL(obj.getDelegacion()) +"',"; 
			
			if (obj.getCiudad().equals("NULL"))
				sql += "\n ciudad = NULL,"; 
			else
				sql += "\n ciudad = '"+ Utilerias.validarCadenaSQL(obj.getCiudad()) +"',";
			
			sql += "\n fec_alta = "+ "(select fec_hoy from fechas)" +", ";
			
			sql += "\n usuario_alta = "+ globalSingleton .getUsuarioLoginDto().getIdUsuario()+ ", ";
		
			sql += "\n fec_modif = "+" (select fec_hoy from fechas) "+", ";
			
			sql += "\n usuario_modif = "+ globalSingleton .getUsuarioLoginDto().getIdUsuario()+", ";
			
			if (obj.getPais().equals("NULL"))
				sql += "\n desc_pais = NULL,"; 
			else
				sql += "\n desc_pais = '"+ Utilerias.validarCadenaSQL(obj.getPais()) +"' "; 
			
			sql += "\n where ";
			
			
			if (obj.getTipoPersona().equals("K") || obj.getTipoPersona().equals("P") ){
				sql += "\n  no_empresa = "+ 552 +"";
				
			}else if(obj.getTipoPersona().equals("F") || obj.getTipoPersona().equals("B") ){
				sql += "\n  no_empresa = "+ 1 +"";
			}else{
				sql += "\n  no_empresa = "+ Utilerias.validarCadenaSQL(obj.getPersona()) +"";
			}
			
			sql += "\n and no_persona = "+ Utilerias.validarCadenaSQL(obj.getPersona()) +" ";
			sql += "\n and id_tipo_persona = '"+ Utilerias.validarCadenaSQL(obj.getTipoPersona()) +"' ";
			
			
			
			
			System.out.println(sql);
			
			resultado = jdbcTemplate.update(sql);			
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasDaoImpl, M: actualizaDireccion");
		}return resultado;
	}
	/////////////////////////////////////
	
	public int actualizaEmpresa (ConsultaPersonasDto obj){
		int resultado = 0;
		sql = "";
		try{
			sql = "Update empresa Set ";
			sql += "\n nom_empresa = '"+Utilerias.validarCadenaSQL( obj.getRazonSocial()) +"', ";
			sql += "\n b_control_bancario = 'S', ";
			sql += "\n b_concentradora = '"+ Utilerias.validarCadenaSQL(obj.getConcentradora()) +"', ";
			sql += "\n id_grupo_flujo = "+ Utilerias.validarCadenaSQL(obj.getIdGrupo()) +", ";
			sql += "\n b_cxc = '"+ 'N' +"', ";
			sql += "\n b_division = '"+ 'N' +"', ";
			sql += "\n id_contrato_tef = "+ Utilerias.validarCadenaSQL(obj.getIdContrato()) +", ";
			sql += "\n id_contrato_mass = "+ Utilerias.validarCadenaSQL(obj.getIdContratoPayment()) +", ";
			sql += "\n b_pag_cruzados_aut = '"+ 'N' +"' ";
			//sql += "\n benef_nafin = '"+ obj.get +"'";
			sql += "\n where no_empresa = "+ Utilerias.validarCadenaSQL(obj.getNoEmpresa())+"";
			System.out.println("-------------------------------");
			System.out.println(sql);
			System.out.println("-------------------------------");
			resultado = jdbcTemplate.update(sql);			
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasDaoImpl, M: actualizaEmpresa");
		}return resultado;
	}
	
	public int insertaDireccion(ConsultaPersonasDto obj){
		int resultado = 0;
//		String sNoEmpresa = Integer.toString(noEmpresaC).length() == 1 ? "0"+noEmpresaC : noEmpresaC+"";
		sql = "";
		GlobalSingleton globalSingleton = GlobalSingleton.getInstancia();
		try{
			System.out.println("Entra a insertaDireccion");
			
			sql = "Insert into direccion (";
			
			sql += "\n no_empresa,no_persona,id_tipo_persona,no_direccion, id_tipo_direccion, id_pais,  ";
			sql += "\n id_estado, id_cp, calle_no, colonia, deleg_municipio, ciudad, fec_alta, usuario_alta, ";
			sql += "\n fec_modif, usuario_modif, desc_pais)";
			sql += "\n values(";
			
			if (obj.getTipoPersona().equals("K") || obj.getTipoPersona().equals("P") ){
				sql += "\n "+ 552 +",";
				
			}else if(obj.getTipoPersona().equals("F") || obj.getTipoPersona().equals("B") ){
				sql += "\n  "+ 1 +",";
			}else{
				sql += "\n "+ Utilerias.validarCadenaSQL(obj.getPersona()) +", ";
			}
		
			sql += "\n "+ Utilerias.validarCadenaSQL(obj.getPersona())+", ";
			sql += "\n '"+ Utilerias.validarCadenaSQL(obj.getTipoPersona()) +"', ";
			sql += "\n "+ 1 +", ";
			sql += "\n '"+ Utilerias.validarCadenaSQL(obj.getIdTipoD()) +"', ";
			sql += "\n '"+ Utilerias.validarCadenaSQL(obj.getPais()) +"', ";
			sql += "\n '"+ Utilerias.validarCadenaSQL(obj.getEstado()) +"', ";
			sql += "\n "+ Utilerias.validarCadenaSQL(obj.getCp()) +", ";
			sql += "\n '"+Utilerias.validarCadenaSQL( obj.getCalle()) +"', ";
			sql += "\n '"+ Utilerias.validarCadenaSQL(obj.getColonia()) +"', ";
			sql += "\n '"+ Utilerias.validarCadenaSQL(obj.getDelegacion()) +"', ";
			sql += "\n '"+ Utilerias.validarCadenaSQL(obj.getCiudad()) +"', ";
			sql += "\n (select fec_hoy from fechas), ";
			sql += "\n "+ globalSingleton .getUsuarioLoginDto().getIdUsuario()+", ";
			sql += "\n (select fec_hoy from fechas), ";
			
			sql += "\n "+ globalSingleton .getUsuarioLoginDto().getIdUsuario()+", ";
			sql += "\n '"+Utilerias.validarCadenaSQL(obj.getPais())+"' ";	
			sql += " \n )";
			
			
			System.out.println(sql+"sql");
			resultado = jdbcTemplate.update(sql);	
			
		}
		catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasDaoImpl, M: insertaDireccion");
		}return resultado;
	}
	//inserta dentro de persona
	public int insertaDatos(ConsultaPersonasDto obj, int noCuenta, int noLinea, int noEmpresaC){
		int resultado = 0;
		String sNoEmpresa = Integer.toString(noEmpresaC).length() == 1 ? "0"+noEmpresaC : noEmpresaC+"";
		sql = "";
		
		try{
			
			sql = "Insert into persona (";
			
			sql += "\n no_empresa,no_persona,id_tipo_persona,dia_limite_pago,dia_recep_fact,  ";
			sql += "\n id_act_generica,id_act_economica,id_giro,id_riesgo_empresa,num_empleados, ";
			sql += "\n usuario_alta,no_acciones_a,no_acciones_b,no_acciones_c,fec_ing,fec_alta, ";
			sql += "\n ventas_anuales,rfc,razon_social,paterno,materno,nombre,nombre_corto, ";
			sql += "\n objeto_social,puesto,id_estatus_per,id_calidad_empresa, ";
			sql += "\n id_tipo_inmueble, pers_juridica,id_edo_civil,sexo,b_proveedor,b_asociacion, ";
			sql += "\n b_contrato_inversion,id_rubro,equivale_persona,referencia_cte,id_grupo ";
			sql += ")";
			sql += "\n values(";
			
			if (obj.getTipoPersona().equals("P"))
				sql += "\n 552, ";
			else
				sql += "\n "+ Utilerias.validarCadenaSQL(obj.getNoEmpresa() )+", ";
			
			sql += "\n "+ Utilerias.validarCadenaSQL(obj.getNoPersona())+", ";
			
			sql += "\n '"+ Utilerias.validarCadenaSQL(obj.getTipoPersona()) +"', ";
			sql += "\n "+ Utilerias.validarCadenaSQL(obj.getDiaLimite()) +", ";
			sql += "\n "+ Utilerias.validarCadenaSQL(obj.getDiaRecepcion()) +", ";
			sql += "\n "+ Utilerias.validarCadenaSQL(obj.getIdActividadGenerica()) +", ";
			sql += "\n "+ Utilerias.validarCadenaSQL(obj.getIdActividadEconomica()) +", ";
			sql += "\n "+ Utilerias.validarCadenaSQL(obj.getIdGiro()) +", ";
			sql += "\n "+ Utilerias.validarCadenaSQL(obj.getIdRiesgo()) +", ";
			sql += "\n "+ Utilerias.validarCadenaSQL(obj.getNoEmpleados()) +", ";
			sql += "\n "+ Utilerias.validarCadenaSQL(obj.getUsuarioAlta()) +", ";
			sql += "\n "+ Utilerias.validarCadenaSQL(obj.getAccionA()) +", ";
			sql += "\n "+ Utilerias.validarCadenaSQL(obj.getAccionB()) +", ";
			sql += "\n "+ Utilerias.validarCadenaSQL(obj.getAccionC()) +", ";
			////Cambio Edgar
			if(obj.getFechaIngreso()==null)
				sql += "\n NULL, ";
			else
			sql += "\n convert(datetime,'"+ Utilerias.validarCadenaSQL(obj.getFechaIngreso()) +"',103), ";
			if(obj.getFechaHoy()==null)
				sql += "\n NULL, ";
			else
			sql += "\n convert(datetime,'"+ Utilerias.validarCadenaSQL(obj.getFechaHoy())+"',103), ";///////////////////
			////////////
			sql += "\n "+ Utilerias.validarCadenaSQL(obj.getVentasAnuales()) +", ";
			sql += "\n '"+ Utilerias.validarCadenaSQL(obj.getRfc()) +"', ";
			
			if (obj.getRazonSocial()==null){
				sql += "\n NULL, ";
				System.out.println("razon");
			}else{				
				sql += "\n '"+ Utilerias.validarCadenaSQL(obj.getRazonSocial()) +"', ";
			}
			
			if (obj.getPaterno()==null)
				sql += "\n NULL, ";
			else
				sql += "\n  '"+ Utilerias.validarCadenaSQL(obj.getPaterno()) +"', ";
			
			if (obj.getMaterno()==null)
				sql += "NULL, ";
			else
				sql += "\n '"+Utilerias.validarCadenaSQL( obj.getMaterno())+"', ";
		
			if (obj.getNombre()==null)
				sql += "NULL, ";
			else
				sql += "\n '"+ Utilerias.validarCadenaSQL(obj.getNombre()) +"', ";
		
			sql += "\n '"+ Utilerias.validarCadenaSQL(obj.getNombreCorto()) +"', ";
			
			sql += "\n '"+ obj.getObjetoSocial() +"', ";
	
			sql += "\n '"+ obj.getPuesto() +"', ";
			
			sql += "\n 'A', ";
			//sql += "\n '"+ obj.getIdTamano() +"', ";
			sql += "\n "+ obj.getIdCalidad() +", ";
			sql += "\n "+ obj.getIdInmueble() +", ";
			if (obj.getTipoPersona().equals("F"))
				sql += "\n 'F',";
			else
				sql += "\n 'M',";
			
			sql += "\n null, ";
			
			if (obj.getTipoPersona().equals("F")){
					if(obj.getSexo().equals("F")){
							sql += "\n 'F',";
					}else{
						sql += "\n 'M',";
					}
			}else
				sql += "\n null,";
			
			
			sql += "\n null, ";
			sql += "\n null, ";
			
			System.out.println(obj.getConcentradora());
			if (obj.getTipoPersona().equals("E")){
				if(obj.getConcentradora().equals("S")){
						sql += "\n 'S',";
				}else{
					sql += "\n 'N',";
				}
			}else
				sql += "\n null,";
			

			sql += "\n null, ";
			
			
//			if (obj.getTipoPersona().equals("E") || obj.getTipoPersona().equals("B") || 
//					obj.getTipoPersona().equals("K") || obj.getTipoPersona().equals("I") ||
//					obj.getTipoPersona().equals("R"))
				sql += "\n '"+ Utilerias.validarCadenaSQL(obj.getNoPersona()) +"', ";
//			else
//				sql += "\n '"+ sNoEmpresa + funciones.ajustarLongitudCampo(Integer.toString(obj.getNoPersona()), 10, "D", " ", "") +"', ";
			
//			if (!obj.getPagoReferenciado().equals(""))
//				sql += "\n '"+ obj.getPagoReferenciado() +"', ";
		//	else
				sql += "\n null,";
			
			sql += "\n "+ Utilerias.validarCadenaSQL(obj.getIdGrupo())+"";
				
			sql += " \n )";
			
			
			System.out.println(sql+"sql");
			resultado = jdbcTemplate.update(sql);	
		
		}
		catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasDaoImpl, M: insertaDatos");
		}return resultado;
	}
	
	public int existeDireccion(int persona,ConsultaPersonasDto obj, String tipoPersona){
		int resultado = 0;
		sql = "";
		try{
			System.out.println(persona);
			sql = "Select count(*) from direccion where no_persona = "+ Utilerias.validarCadenaSQL(persona) +" ";
			if (obj.getTipoPersona().equals("K") || obj.getTipoPersona().equals("P") ){
				sql += "\n and no_empresa = "+ 552 +"";
				
			}else if(obj.getTipoPersona().equals("F") || obj.getTipoPersona().equals("B") ){
				sql += "\n and no_empresa = "+ 1 +"";
			}else{
				sql += "\n and no_empresa = "+ Utilerias.validarCadenaSQL(persona )+"";
			}
				
			
			sql += "\n and id_tipo_persona = '"+ Utilerias.validarCadenaSQL(tipoPersona) +"' ";
			
			System.out.println(sql);
			resultado = jdbcTemplate.queryForInt(sql);
		}
		catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasDaoImpl, M: existeDireccion");
		}return resultado;
	}
	
	public int existePersona(int noPersona, int noEmpresa, String tipoPersona){
		int resultado = 0;
		sql = "";
		try{
			
			sql = "Select count(*) from persona where no_persona = "+ Utilerias.validarCadenaSQL(noPersona) +" ";
			sql += "\n and no_empresa = "+ Utilerias.validarCadenaSQL(noEmpresa) +"";
			sql += "\n and id_tipo_persona = '"+ Utilerias.validarCadenaSQL(tipoPersona) +"' ";
			System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");
			System.out.println(sql);
			System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");
			

			resultado = jdbcTemplate.queryForInt(sql);
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasDaoImpl, M: existePersona");
		}return resultado;
	}
	
	@SuppressWarnings("unchecked")
	public String verificaEmpresa (int noEmpresa){
		String cadena = "";
		List<ConsultaPersonasDto> listaResultado = new ArrayList<ConsultaPersonasDto>();		
		sql = "";
		try{
			sql = "Select e.* ";
			sql += "\n from persona p, empresa e ";
			sql += "\n where e.no_empresa = "+ Utilerias.validarCadenaSQL(noEmpresa) +" ";
			sql += "\n and p.id_tipo_persona = 'E' ";
			sql += "\n and e.no_empresa = p.no_persona ";
			
			listaResultado = jdbcTemplate.query(sql, new RowMapper(){
				public ConsultaPersonasDto mapRow(ResultSet rs, int idx){
					ConsultaPersonasDto campos = new ConsultaPersonasDto();
					return campos;
				}
			});
			
			if (listaResultado.size() > 0)
				cadena = listaResultado.get(0).getMultiempresa();
			
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasDaoImpl, M: verificaEmpresa");
		}return cadena;
	}
	

	public int cambiaTipoPersona (int noPersona){
		sql = "";
		int resultado = 0;
		try{
			sql = "Update persona set id_tipo_persona = 'S' where no_persona = "+ Utilerias.validarCadenaSQL(noPersona )+" ";
			
			resultado = jdbcTemplate.update(sql);
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasDaoImpl, M: cambiaTipoPersona");
		}return resultado;
	}
	
	@SuppressWarnings("unchecked")
	public List<ConsultaPersonasDto> comboBenef(int noEmpresa, String noBenef, String descBenef){
		List<ConsultaPersonasDto> listaResultado = new ArrayList<ConsultaPersonasDto>();
		sql = "";
		try{
			sql = "Select p.equivale_persona, p.razon_social ";
			sql += "\n From persona p, proveedor pr ";
			sql += "\n Where p.id_tipo_persona = 'P' ";
			sql += "\n and p.no_persona = pr.no_proveedor ";
			sql += "\n and p.no_empresa = pr.no_empresa ";
			sql += "\n and p.no_empresa in (552, "+ Utilerias.validarCadenaSQL(noEmpresa) +") ";
			
			if (!noBenef.equals(""))
				sql += "\n p.equivale_persona = '"+ Utilerias.validarCadenaSQL(noBenef) +"' ";
			
			if (!descBenef.equals(""))
				sql += "\n p.razon_social like '%"+ Utilerias.validarCadenaSQL(descBenef) +"%' ";
			
			listaResultado = jdbcTemplate.query(sql, new RowMapper(){
				public ConsultaPersonasDto mapRow(ResultSet rs, int idx)throws SQLException{
					ConsultaPersonasDto campos = new ConsultaPersonasDto();
					campos.setNoBenef(rs.getString("equivale_persona"));
					campos.setDescBenef(rs.getString("razon_social"));
					return campos;
				}
			});				
			
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Persona, C: ConsultaPersonasDaoImpl, M: comboBenef");
		}return listaResultado;		
	}
	
	public int actualizaInstFinanciera(int noPersona, String noEmpresa){
		int resultado = 0;
		sql = "";
		try{
			sql = "Update empresa ";
			
			if (noEmpresa.equals("NULL")){
				sql += "\n SET institucion_financiera = null";
				sql += "\n where institucion_financiera = "+Utilerias.validarCadenaSQL( noPersona )+" ";
			}
			else{
				sql += "\n SET institucion_financiera = "+Utilerias.validarCadenaSQL( noPersona) +" ";
				sql += "\n where no_empresa = "+ Utilerias.validarCadenaSQL(Integer.parseInt(noEmpresa)) +" ";
			}
			
			resultado = jdbcTemplate.update(sql);
			
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Persona, C: ConsultaPersonasDaoImpl, M: actualizaInstFinanciera");
		}return resultado;
	}
	
	public int insertaLinea (int noEmpresa, int noLinea, String fechaHoy, int usuario){
		
		int resultado = 0;
		sql = "";
		try{
			sql = "Insert into linea (";
			sql += "\n no_empresa, no_linea, ";
			sql += "\n fec_alta, usuario_alta)";
			sql += "\n values (";
			sql += "\n "+ Utilerias.validarCadenaSQL(noEmpresa) +", ";
			//se comento el query para asignar directamente noLinea
			sql += "\n "+ Utilerias.validarCadenaSQL(noLinea)+", ";
//			sql += "\n CASE WHEN (SELECT MAX(no_linea) FROM linea) IS NOT NULL ";
//			sql += "\n THEN (SELECT MAX(no_linea) + 1 FROM linea)";
//			sql +="\n ELSE 1 END,";
			
					//+ "(select max(no_linea) + 1 from linea), ";
			sql += "\n convert(datetime,'"+ Utilerias.validarCadenaSQL(fechaHoy) +"',103), "+ Utilerias.validarCadenaSQL(usuario) +") ";
			System.out.println(sql);
			resultado = jdbcTemplate.update(sql);	
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Persona, C: ConsultaPersonasDaoImpl, M: insertaLinea");	
		}
		return resultado;
		
		
	}
		
	public int insertaCuenta (int noEmpresa, int noLinea, int noPersona, int noCuenta, String fechaHoy, int usuario){
		int resultado = 0;
		sql = "";
		try{
			sql = "Insert into cuenta (";
			sql += "\n no_empresa, no_linea, no_persona, ";
			sql += "\n no_cuenta, fec_alta, usuario_alta, id_tipo_cuenta)";
			sql += "\n values (";
			//se comentaron los querys para agregar directamente noLinea y noCuenta
			sql += "\n "+ Utilerias.validarCadenaSQL(noEmpresa) +", "+Utilerias.validarCadenaSQL(noLinea)+", "+ Utilerias.validarCadenaSQL(noPersona) +", ";
//			sql += "\n "+ Utilerias.validarCadenaSQL(noEmpresa) +", (select max(no_linea) from linea), "+ Utilerias.validarCadenaSQL(noPersona) +", ";
			sql += "\n "+ Utilerias.validarCadenaSQL(noCuenta)+", ";
//			sql += "\n CASE WHEN (SELECT MAX(no_cuenta) FROM cuenta) IS NOT NULL ";
//			sql += "\n THEN (SELECT MAX(no_cuenta) + 1 FROM cuenta)";
//			sql +="\n ELSE 1 END,";
			
			//sql += "\n (select max(no_cuenta) + 1 from cuenta),
			sql += "\n convert(datetime,'"+ Utilerias.validarCadenaSQL(fechaHoy) +"',103), "+ Utilerias.validarCadenaSQL(usuario) +",'P') ";
			System.out.println(sql);
			resultado = jdbcTemplate.update(sql);			
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Persona, C: ConsultaPersonasDaoImpl, M: insertaCuenta");
		}return resultado;
	}
	//inserta en empresa
	public int insertaEmpresa(ConsultaPersonasDto obj){
		int resultado = 0;
		sql = "";
		//La ruta del logo no se si aplica como estaba en visual
		try{
			sql = "Insert into empresa (";
			sql += "\n no_empresa, no_linea_emp, no_cuenta_emp, usuario_alta, fec_alta, ";
			sql += "\n fec_constitucion, nom_empresa, arch_logo, b_control_bancario, b_concentradora, ";
			sql += "\n multiempresa, id_grupo_flujo, b_cxc, b_division, id_contrato_tef, ";
			sql += "\n id_contrato_mass, benef_nafin, ";
			
			if (obj.getIdCaja() != 0)
				sql += "\n id_caja, ";
			
			sql += "\n b_pag_cruzados_aut) ";
			sql += "\n values (";
			sql += "\n "+ Utilerias.validarCadenaSQL(obj.getNoEmpresa()) +", "+ Utilerias.validarCadenaSQL(obj.getNoLinea()) +", "+ Utilerias.validarCadenaSQL(obj.getNoCuenta()) +", "+ Utilerias.validarCadenaSQL(obj.getUsuarioModif()) +", convert(datetime,'"+ Utilerias.validarCadenaSQL(obj.getFechaHoy()) +"',103), ";
			sql += "\n convert(datetime,'"+ Utilerias.validarCadenaSQL(obj.getFechaHoy()) +"',103), '"+ Utilerias.validarCadenaSQL(obj.getRazonSocial()) +"', 'logo/logo.bmp', 'S', '"+ 'N' +"', ";
			sql += "\n 'S', "+ Utilerias.validarCadenaSQL(obj.getIdGrupo()) +", '"+ 'N' +"', '"+ 'N' +"', "+ Utilerias.validarCadenaSQL(obj.getIdContrato()) +", ";
			sql += "\n "+ obj.getIdContratoPayment() +", '"+ obj.getDescBenef() +"', ";
			
			if (obj.getIdCaja() != 0)
				sql += "\n "+ Utilerias.validarCadenaSQL(obj.getIdCaja()) +", ";
			
			sql += "\n '"+ Utilerias.validarCadenaSQL(obj.getConcentradora()) +"' )";
			System.out.println("*********************************-");
			System.out.println(sql);
			System.out.println("*********************************-");
			resultado = jdbcTemplate.update(sql);			
		}
		catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Persona, C: ConsultaPersonasDaoImpl, M: insertaEmpresa");
		}return resultado;
	}
	
	public int insertaSaldos(int noEmpresa, int noLinea, int noCuenta, int valor){
		int resultado = 0;
		sql = "";
		try{	
			sql = "Insert into saldo (";
			sql += "\n no_empresa, no_linea, no_cuenta, ";
			sql += "\n id_tipo_saldo, importe)";
			sql += "\n values(";
			sql += "\n "+ Utilerias.validarCadenaSQL(noEmpresa) +", "+ Utilerias.validarCadenaSQL(noLinea) +", "+ Utilerias.validarCadenaSQL(noCuenta) +", ";
			sql += "\n "+ Utilerias.validarCadenaSQL(valor) +", 0)";
			
			resultado = jdbcTemplate.update(sql);
		}
		catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Persona, C: ConsultaPersonasDaoImpl, M: insertaSaldos");
		}return resultado;
	}
		
	public int obtieneCuentaEmpresa(int noEmpresa){
		int resultado = 0;
		sql = "";
		try{
			sql = "Select coalesce(no_cuenta_emp, 0) as no_cuenta_emp ";
			sql += "\n from empresa ";
			sql += "\n where no_empresa = "+ Utilerias.validarCadenaSQL(noEmpresa) +"";
			
			resultado = jdbcTemplate.queryForInt(sql);		
			
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Persona, C: ConsultaPersonasDaoImpl, M: obtieneCuentaEmpresa");
		}return resultado;
	}
	
	public int insertaProveedor(int noEmpresa, int noPersona, int noUsuario, String fechaHoy, int noCuentaEmp, boolean bProvBasico, String provBasico){
		System.out.println("Entra a Insertar proveedor con tipo moral");
		int resultado = 0;
		String sql = "";
		try{
			
			sql = "Insert into proveedor (";
			sql += "\n no_empresa,  no_proveedor, usuario_alta, fec_alta, no_cuenta, ";
			sql += "\n condiciones_pago, b_descto_vol, b_anticipo ";
			System.out.println("prueba 1");
			if (bProvBasico)
				sql += "\n , b_servicios";
			
			sql += ")";
			sql += "\n values (";
			sql += "\n 552, "+ Utilerias.validarCadenaSQL(noPersona) +", "+ Utilerias.validarCadenaSQL(noUsuario) +", '"+ Utilerias.validarCadenaSQL(fechaHoy) +"', "+ Utilerias.validarCadenaSQL(noCuentaEmp) +", ";
			sql += "\n 0, 'N', 'N'";
			
			if (bProvBasico)
				sql += "\n , provBasico";
			
			sql += " )";
			
			resultado = jdbcTemplate.update(sql);
			
			System.out.println(sql);
			
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Persona, C: ConsultaPersonasDaoImpl, M: insertaProveedor");
		}return resultado;	
	}
	
	
	public String guardaNuevoMedioContacto (String no_persona,String mail, int empre){
		List<ConsultaPersonasDto> listaResultado = new ArrayList<ConsultaPersonasDto>();
		int resultado = 0;
		String sql = "";
		String sql2="";
		try{
			sql="select MAX(no_medio) as maxi from medios_contacto where  no_persona="+no_persona;
			
			listaResultado = jdbcTemplate.query(sql, new RowMapper <ConsultaPersonasDto>(){
				public ConsultaPersonasDto mapRow(ResultSet rs, int idx)throws SQLException{
					ConsultaPersonasDto campos = new ConsultaPersonasDto();
                   
				
					campos.setNoLinea(rs.getInt("maxi"));				
					return campos;
				}
			});
			resultado=listaResultado.get(0).getNoLinea();
			if(resultado>0){
				resultado=resultado+1;
				sql2="insert into medios_contacto values("+empre+","+no_persona+",'P',"+resultado+",'MAIL','"+mail+"',GETDATE(),1,GETDATE(),1)";
				
			}else{
				sql2="insert into medios_contacto values("+empre+","+no_persona+",'P',1,'MAIL','"+mail+"',GETDATE(),1,GETDATE(),1)";
			}
			resultado = jdbcTemplate.update(sql2);
			
			System.out.println(sql2);
			
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Persona, C: ConsultaPersonasDaoImpl, M: insertaProveedor");
		}return null;	
	}
	
	@SuppressWarnings("unchecked")
	public List<ConsultaPersonasDto> verificaRegistro(int noPersona, String tipoPersona){
		List<ConsultaPersonasDto> listaResultado = new ArrayList<ConsultaPersonasDto>();
		sql = "";
		try{			
			sql = "Select * from persona ";
			sql += "\n where no_persona = "+ Utilerias.validarCadenaSQL(noPersona) +" ";
			sql += "\n and id_tipo_persona = '"+ Utilerias.validarCadenaSQL(tipoPersona) +"' ";
			
			listaResultado = jdbcTemplate.query(sql, new RowMapper(){
				public ConsultaPersonasDto mapRow(ResultSet rs, int idx)throws SQLException{
					ConsultaPersonasDto campos = new ConsultaPersonasDto();
					campos.setNoPersona(rs.getInt("no_persona"));
					campos.setTipoPersona(rs.getString("id_tipo_persona"));
					return campos;
				}
			});		
		}		
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasDaoImpl, M: verificaRegistro");
		}return listaResultado;
	}
	
	public int inhabilitaPersona(int noPersona, String tipoPersona){
		int resultado = 0;
		sql = "";
		
		try{			
			sql = "Update persona ";
			sql += "\n Set id_estatus_per = 'I' ";
			sql += "\n where no_persona = "+ Utilerias.validarCadenaSQL(noPersona) +" ";
			sql += "\n and id_tipo_persona = '"+ Utilerias.validarCadenaSQL(tipoPersona )+"' ";
			
			resultado = jdbcTemplate.update(sql);
			
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasDaoImpl, M: inhabilitaPersona");
		}return resultado;
	}
	//******************************** MANTENIMIENTO CUENTAS PROVEEDOR *********************************************************************
	@SuppressWarnings("unchecked")
	public List<ConsultaPersonasDto> consultaCuentasProveedor(int noPersona, int noEmpresa, String tipoPersona){
		List<ConsultaPersonasDto> listaResultado =  new ArrayList<ConsultaPersonasDto>();
		System.out.println("Inicio de metodo de consulta dao");
		sql = "";
		try{
			sql = "Select csb.no_persona, csb.id_banco_anterior, csb.id_chequera_anterior, csb.complemento, 'T' as grabado, ";
			sql += "\n csb.id_banco, csb.id_chequera, coalesce(csb.desc_chequera, '') as desc_chequera,  csb.sucursal, csb.plaza, ";
			sql += "\n csb.id_clabe, csb.id_divisa, csb.usuario_modif, csb.fec_modif, csb.no_empresa, ";
			sql += "\n csb.swift_code, csb.aba, csb.especiales, csb.id_banco, coalesce(csb.id_bank_true, 0) as id_bank_true, ";
			sql += "\n coalesce(csb.id_bank_corresponding, 0) as id_bank_corresponding, csb.id_chequera_true, cb.desc_banco, cb.nac_ext, b.desc_banco as desc_banco_ant, ";
			sql += "\n csb.tipo_envio_layout, csb.aba_intermediario, csb.swift_intermediario, csb.aba_corresponsal, csb.swift_corresponsal, ";
			sql += "\n 'S' as actualiza_chequera_prov, csb.referencia, csb.id_banco_interlocutor ";
			sql += "\n from ctas_banco csb left join cat_banco b "
					+ "on csb.id_banco_anterior = b.id_banco "
					+ "left join cat_banco cb on csb.id_banco = cb.id_banco ";
			sql += "\n where no_persona = "+ Utilerias.validarCadenaSQL(noPersona) +" ";
			
			
			if (tipoPersona.equals("P") || tipoPersona.equals("K"))
				sql += "\n and csb.no_empresa in (552, "+ Utilerias.validarCadenaSQL(noEmpresa )+") ";
			else
				sql += "\n and csb.no_empresa = "+ Utilerias.validarCadenaSQL(noEmpresa )+" ";
			
			sql += "\n and csb.id_tipo_persona = '"+ Utilerias.validarCadenaSQL(tipoPersona )+"' ";
			System.out.println("sql" + sql);
			
			listaResultado = jdbcTemplate.query(sql, new RowMapper(){
				public ConsultaPersonasDto mapRow(ResultSet rs, int idx)throws SQLException{
					ConsultaPersonasDto campos = new ConsultaPersonasDto();
					campos.setIdBanco(rs.getInt("id_banco"));
					campos.setDescBanco(rs.getString("desc_banco"));
					campos.setChequera(rs.getString("id_chequera"));
					campos.setDescChequera(rs.getString("desc_chequera"));
					campos.setDivisa(rs.getString("id_divisa"));
					campos.setGrabado(rs.getString("grabado"));
					campos.setSucursal(rs.getInt("sucursal"));
					campos.setPlaza(rs.getInt("plaza"));
//					campos.setClabe(rs.getString("clabe"));
					campos.setClabe(rs.getString("id_clabe"));
					campos.setChequeraAnt(rs.getString("id_chequera_anterior"));
					campos.setIdBancoAnt(rs.getInt("id_banco_anterior"));
					campos.setUsuarioModif(rs.getInt("usuario_modif"));
					campos.setFecModif(rs.getString("fec_modif"));
					campos.setNoEmpresa(rs.getInt("no_empresa"));
					campos.setNoPersona(rs.getInt("no_persona"));
					campos.setChequeraBenef(rs.getString("id_chequera_anterior"));
					campos.setSwift(rs.getString("swift_code"));
					campos.setAba(rs.getString("aba"));
					campos.setBankTrue(rs.getInt("id_bank_true"));
					campos.setBankCorresponding(rs.getInt("id_bank_corresponding"));
					campos.setNacionalidad(rs.getString("nac_ext"));
					campos.setChequeraTrue(rs.getString("id_chequera_true"));
					campos.setBancoAnterior(rs.getString("desc_banco_ant"));
					campos.setChequeraAnterior(rs.getString("id_chequera_anterior"));
					campos.setAbaInter(rs.getString("aba_intermediario"));
					campos.setSwiftInter(rs.getString("swift_intermediario"));
					campos.setAbaCorresp(rs.getString("aba_corresponsal"));
					campos.setSwiftCorresp(rs.getString("swift_corresponsal"));
					campos.setActualizaChequeraProv(rs.getString("actualiza_chequera_prov"));
					campos.setReferencia(rs.getString("referencia"));
					campos.setBancoI(rs.getString("id_banco_interlocutor"));
					campos.setEspecial(rs.getString("especiales"));
						
				
					return campos;
				}
			});			
		}
		catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Persona, C: ConsultaPersonasDaoImpl, M: consultaCuentasProveedor");
		}return listaResultado;
	}
	
	@SuppressWarnings("unchecked")
	public List<ConsultaPersonasDto> comboDivisasCuentas(){
		List<ConsultaPersonasDto> listaResultado =  new ArrayList<ConsultaPersonasDto>();
		sql = "";
		try{
			sql = "Select id_divisa from cat_divisa ";
			sql += "\n where clasificacion = 'D' ";
			
			listaResultado = jdbcTemplate.query(sql, new RowMapper(){				
				public ConsultaPersonasDto mapRow(ResultSet rs, int idx)throws SQLException{
					ConsultaPersonasDto campos = new ConsultaPersonasDto();
					campos.setIdDivisa(rs.getString("id_divisa"));
					return campos;
				}
			});			
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasDaoImpl, M: comboDivisasCuentas");
		}return listaResultado;
	}
	
	@SuppressWarnings("unchecked")
	public List<ConsultaPersonasDto> comboBancoCuentas(String nacionalidad){
		List<ConsultaPersonasDto> listaResultado = new ArrayList<ConsultaPersonasDto>();
		sql = "";
		try{
			
			sql = "Select id_banco, desc_banco ";
			sql += "\n from cat_banco ";
			sql += "\n where nac_ext = '"+ Utilerias.validarCadenaSQL(nacionalidad )+"' ";
			
			
			listaResultado = jdbcTemplate.query(sql, new RowMapper(){
				public ConsultaPersonasDto mapRow(ResultSet rs, int idx)throws SQLException{
					ConsultaPersonasDto campos = new ConsultaPersonasDto();
					campos.setIdBanco(rs.getInt("id_banco"));
					campos.setDescBanco(rs.getString("desc_banco"));
					return campos;
				}
			});
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasDaoImpl, M: comboBancoCuentas");
		}return listaResultado;
	}
	
	@SuppressWarnings("unchecked")
	public String buscaDescripcionBanco(int noBanco){
		List<ConsultaPersonasDto> listaResultado = new ArrayList<ConsultaPersonasDto>();
		String descripcion = "";
		sql = "";
		try{
			sql = "Select coalesce(desc_banco, '') as desc_banco from cat_banco ";
			sql += "\n where id_banco = "+ noBanco +" ";
			
			 listaResultado = jdbcTemplate.query(sql, new RowMapper(){
				public ConsultaPersonasDto mapRow(ResultSet rs, int idx)throws SQLException{
					ConsultaPersonasDto campos = new ConsultaPersonasDto();
					campos.setDescBanco(rs.getString("desc_banco"));
					return campos;
				}
			});
			 
			if (listaResultado.size() > 0)
				descripcion = listaResultado.get(0).getDescBanco();
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasDaoImpl, M: buscaDescripcionBanco");
		}return descripcion;
	}
	
	
	@SuppressWarnings("unchecked")
	public List<ConsultaPersonasDto> validaMovimientos(int noPersona, String chequera){
		List<ConsultaPersonasDto> listaResultado = new ArrayList<ConsultaPersonasDto>();
		sql = "";
		try{
			sql = "Select m.no_empresa, m.no_docto, m.usuario_alta, c.nombre + ' ' + c.paterno as usuario ";
			sql += "\n from movimiento m left join cat_usuario c ";
			sql += "\n on (m.usuario_alta = c.no_usuario) ";
			sql += "\n where m.id_tipo_operacion = 3200 ";
			sql += "\n and m.id_estatus_mov = 'T' ";
			sql += "\n and m.id_forma_pago = 3 ";
			sql += "\n and m.no_cliente = "+ Utilerias.validarCadenaSQL(noPersona) +" ";
			sql += "\n and m.id_chequera_benef = '"+ Utilerias.validarCadenaSQL(chequera) +"' ";
			
			listaResultado = jdbcTemplate.query(sql, new RowMapper(){
				public ConsultaPersonasDto mapRow (ResultSet rs, int idx)throws SQLException{
					ConsultaPersonasDto campos = new ConsultaPersonasDto();
					campos.setNoEmpresa(rs.getInt("no_empresa"));
					campos.setNoDocto(rs.getString("no_docto"));
					campos.setUsuarioAlta(rs.getInt("usuario_alta"));
					campos.setNombre(rs.getString("usuario"));
					return campos;
				}
			});			
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasDaoImpl, M: validaMovimientos");
		}return listaResultado;
	}
	
	@SuppressWarnings("unchecked")
	public List<ConsultaPersonasDto> validaTransferencias(ConsultaPersonasDto obj){
		List<ConsultaPersonasDto> listaResultado = new ArrayList<ConsultaPersonasDto>();
		sql = "";
		try{
			sql = "Select coalesce(no_folio_det, '') as no_folio_det from movimiento ";
			sql += "\n where id_tipo_operacion = 3200 ";
			sql += "\n and id_estatus_mov = 'P' ";
			sql += "\n and no_cliente = "+ Utilerias.validarCadenaSQL(obj.getNoPersona()) +" ";
			sql += "\n and id_banco_benef = "+ Utilerias.validarCadenaSQL(obj.getIdBanco()) +" ";
			sql += "\n and id_chequera_benef = '"+ Utilerias.validarCadenaSQL(obj.getChequera()) +"' ";
			
			listaResultado = jdbcTemplate.query(sql, new RowMapper(){
				public ConsultaPersonasDto mapRow(ResultSet rs, int idx)throws SQLException{					
					ConsultaPersonasDto campos = new ConsultaPersonasDto();
					campos.setFolioDet(rs.getInt("no_folio_det"));
					return campos;
				}
			});
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasDaoImpl, M: validaTransferencias");
		}return listaResultado;
	}
	
	public int eliminaCuentaProveedor(ConsultaPersonasDto obj){
		int resultado = 0;
		sql = "";
		try{
			sql = "Delete from ctas_banco ";
			sql += "\n where id_banco = "+ Utilerias.validarCadenaSQL(obj.getIdBanco()) +" ";
			sql += "\n and id_chequera = '"+ Utilerias.validarCadenaSQL(obj.getChequera()) +"' ";
			
			if (obj.getTipoPersona().equals("P"))
				sql += "\n and no_empresa in (552, "+ Utilerias.validarCadenaSQL(obj.getNoEmpresa()) +") ";
			else
				sql += "\n and no_empresa = "+ Utilerias.validarCadenaSQL(obj.getNoEmpresa()) +" ";
			
			sql += "\n and no_persona = "+ Utilerias.validarCadenaSQL(obj.getNoPersona()) +" ";
			sql += "\n and id_tipo_persona = '"+ Utilerias.validarCadenaSQL(obj.getTipoPersona()) +"' ";
			
			
			resultado = jdbcTemplate.update(sql);
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasDaoImpl, M: eliminaCuentaProveedor");
		}return resultado;
	}
	
	public int eliminaCuentaProveedor(String no_persona,String mail){
		int resultado = 0;
		sql = "";
		try{
		 sql="delete from medios_contacto where no_persona="+no_persona+" and contacto='"+mail+"'";
			
			resultado = jdbcTemplate.update(sql);
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasDaoImpl, M: eliminaCuentaProveedor");
		}return resultado;
	}
	
	public int insertaCuentaHistorico(ConsultaPersonasDto obj){
		int resultado = 0;
		sql = "";		
		try{
			sql = "Insert into hist_ctas_banco (";
			sql += "no_modificacion, no_empresa, ";
			
			if (obj.getTipoPersona().equals("P"))
				sql += "id_clabe, ";
			
			sql += "\n no_persona, id_banco, id_divisa, id_chequera, desc_chequera, ";
			sql += "\n sucursal, plaza, fec_modif, usuario_modif, complemento, ";
			sql += "\n swift_code, aba, especiales, id_tipo_persona, id_bank_true, ";
			sql += "\n id_bank_corresponding, id_chequera_true, tipo_envio_layout, aba_intermediario, swift_intermediario, ";
			sql += "\n aba_corresponsal, swift_corresponsal, descripcion_cambio, horario_cambio) ";
			sql += "\n values(";
			sql += ""+Utilerias.validarCadenaSQL( obj.getFolioReal()) +", ";
			
			if (obj.getTipoPersona().equals("P")){
				sql += "552, '"+ Utilerias.validarCadenaSQL(obj.getClabe()) +"', ";
			}
			else
				sql += ""+ Utilerias.validarCadenaSQL(obj.getNoEmpresa()) +", ";
			
			sql += "\n "+ Utilerias.validarCadenaSQL(obj.getNoPersona()) +", "+ Utilerias.validarCadenaSQL(obj.getIdBanco()) +", '"+ Utilerias.validarCadenaSQL(obj.getDivisa())
			+"', '"+ Utilerias.validarCadenaSQL(obj.getChequera()) +"', '"+ Utilerias.validarCadenaSQL(obj.getDescChequera()) +"', ";
			sql += "\n "+ Utilerias.validarCadenaSQL(obj.getSucursal()) +", "+ Utilerias.validarCadenaSQL(obj.getPlaza()) +", '"+ Utilerias.validarCadenaSQL(obj.getFechaHoy()) +"', "+ Utilerias.validarCadenaSQL(obj.getUsuarioAlta()) +", '', ";
			sql += "\n '"+ Utilerias.validarCadenaSQL(obj.getSwift()) +"', '"+ Utilerias.validarCadenaSQL(obj.getAba()) +"', '', '"+ Utilerias.validarCadenaSQL(obj.getTipoPersona()) +"', "+ Utilerias.validarCadenaSQL(obj.getBankTrue()) +", ";
			sql += "\n "+ Utilerias.validarCadenaSQL(obj.getBankCorresponding()) +", '"+ Utilerias.validarCadenaSQL(obj.getChequeraTrue()) +"', '', '"+ Utilerias.validarCadenaSQL(obj.getAbaInter()) +"', '"+ Utilerias.validarCadenaSQL(obj.getSwiftInter()) +"', ";
			sql += "\n '"+ Utilerias.validarCadenaSQL(obj.getAbaCorresp()) +"', '"+ Utilerias.validarCadenaSQL(obj.getSwiftCorresp()) +"', '"+ Utilerias.validarCadenaSQL(obj.getConcepto()) +"', '')";
			
			resultado = jdbcTemplate.update(sql);
			
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasDaoImpl, M: insertaCuentaHistorico");
		}return resultado;
	}	
	
	public int insertaCuentaProveedor(ConsultaPersonasDto obj){
		int resultado = 0;
		sql = "";
		try{
			sql = "Insert into ctas_banco (";
			sql += "\n no_empresa, ";
			
			if (obj.getTipoPersona().equals("P"))
				sql += "\n id_clabe, ";
			
			sql += "\n no_persona, id_banco, id_divisa, id_chequera, desc_chequera, ";
			sql += "\n sucursal, plaza, fec_modif, usuario_modif, complemento, ";
			sql += "\n swift_code, aba, especiales, id_tipo_persona, id_bank_true, ";
			sql += "\n id_bank_corresponding, id_chequera_true, tipo_envio_layout, aba_intermediario, swift_intermediario, ";
			sql += "\n aba_corresponsal, swift_corresponsal, id_banco_anterior, id_chequera_anterior) ";
			sql += "\n values (";
			
			if (obj.getTipoPersona().equals("P")){
				sql += "\n 552, '"+ Utilerias.validarCadenaSQL(obj.getClabe()) +"', ";
			}
			else
				sql += "\n "+ Utilerias.validarCadenaSQL(obj.getNoEmpresa()) +", ";
			
			sql += "\n "+ Utilerias.validarCadenaSQL(obj.getNoPersona()) +", "+ Utilerias.validarCadenaSQL(obj.getIdBanco()) +", '"+ Utilerias.validarCadenaSQL(obj.getDivisa()) +"', '"+ Utilerias.validarCadenaSQL(obj.getChequera()) +"', '"+ Utilerias.validarCadenaSQL(obj.getDescChequera()) +"', ";
			sql += "\n "+ Utilerias.validarCadenaSQL(obj.getSucursal()) +", "+ Utilerias.validarCadenaSQL(obj.getPlaza()) +", '"+ Utilerias.validarCadenaSQL(obj.getFechaHoy()) +"', "+ Utilerias.validarCadenaSQL(obj.getUsuarioAlta()) +", '', ";
			sql += "\n '"+ Utilerias.validarCadenaSQL(obj.getSwift()) +"', '"+ Utilerias.validarCadenaSQL(obj.getAba()) +"', '', '"+ Utilerias.validarCadenaSQL(obj.getTipoPersona()) +"', "+ Utilerias.validarCadenaSQL(obj.getBankTrue()) +", ";
			sql += "\n "+ Utilerias.validarCadenaSQL(obj.getBankCorresponding()) +", '"+ Utilerias.validarCadenaSQL(obj.getChequeraTrue()) +"', '', '"+ Utilerias.validarCadenaSQL(obj.getAbaInter()) +"', '"+ Utilerias.validarCadenaSQL(obj.getSwiftInter()) +"', ";
			sql += "\n '"+ Utilerias.validarCadenaSQL(obj.getAbaCorresp()) +"', '"+ Utilerias.validarCadenaSQL(obj.getSwiftCorresp()) +"', "+ Utilerias.validarCadenaSQL(obj.getIdBancoAnt()) +", '"+ Utilerias.validarCadenaSQL(obj.getChequeraAnt()) +"') ";
			
			resultado = jdbcTemplate.update(sql);		
			
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P :Personas, C: ConsultaPersonasDaoImpl, M: insertaCuentaProveedor");
		}return resultado;
	}
	
	public int actualizaCuentaProveedor(ConsultaPersonasDto obj){
		int resultado = 0;
		sql = "";
		
		try{
			sql = "Update ctas_banco ";
			sql += "\n Set complemento = '', desc_chequera = '"+ Utilerias.validarCadenaSQL(obj.getDescChequera()) +"', sucursal = "+ Utilerias.validarCadenaSQL(obj.getSucursal()) +", id_chequera = '"+ Utilerias.validarCadenaSQL(obj.getChequera()) +"', ";
			sql += "\n plaza = "+ Utilerias.validarCadenaSQL(obj.getPlaza()) +", id_clabe = '"+ Utilerias.validarCadenaSQL(obj.getClabe()) +"', fec_modif = '"+ Utilerias.validarCadenaSQL(obj.getFechaHoy()) +"', ";
			sql += "\n usuario_modif = "+ Utilerias.validarCadenaSQL(obj.getUsuarioAlta()) +", swift_code = '"+ Utilerias.validarCadenaSQL(obj.getSwift()) +"', aba = '"+ Utilerias.validarCadenaSQL(obj.getAba()) +"', ";
			sql += "\n especiales = '', id_bank_true = "+ Utilerias.validarCadenaSQL(obj.getBankTrue()) +", id_bank_corresponding = "+ Utilerias.validarCadenaSQL(obj.getBankCorresponding()) +", ";
			sql += "\n id_chequera_true = '"+ Utilerias.validarCadenaSQL(obj.getChequeraTrue()) +"', id_divisa = '"+ Utilerias.validarCadenaSQL(obj.getDivisa()) +"', aba_intermediario = '"+ Utilerias.validarCadenaSQL(obj.getAbaInter()) +"',";
			sql += "\n swift_intermediario = '"+ Utilerias.validarCadenaSQL(obj.getSwiftInter()) +"', aba_corresponsal = '"+ Utilerias.validarCadenaSQL(obj.getAbaCorresp()) +"', swift_corresponsal = '"+ Utilerias.validarCadenaSQL(obj.getSwiftCorresp()) +"', ";
			//validar el tipo de envio
			sql += "\n tipo_envio_layout = '', id_banco_anterior = "+Utilerias.validarCadenaSQL( obj.getIdBancoAnt()) +", id_chequera_anterior = '"+ Utilerias.validarCadenaSQL(obj.getChequeraAnt()) +"' ";
			sql += "\n where id_banco = "+ Utilerias.validarCadenaSQL(obj.getIdBanco()) +" ";
			//sql += "\n and id_chequera = '"+ obj.getChequera() +"' ";
			
			if (obj.getTipoPersona().equals("P"))
				sql += "\n and no_empresa in (552, "+Utilerias.validarCadenaSQL( obj.getNoEmpresa()) +") ";
			else
				sql += "\n and no_empresa = "+ Utilerias.validarCadenaSQL(obj.getNoEmpresa()) +" ";
			
			sql += "\n and no_persona = "+ Utilerias.validarCadenaSQL(obj.getNoPersona()) +" ";
			sql += "\n and id_tipo_persona = '"+ Utilerias.validarCadenaSQL(obj.getTipoPersona()) +"' ";
			
			resultado = jdbcTemplate.update(sql);
			
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasDaoImpl, M: actualizaCuentaProveedor");
		}
		System.out.println(resultado);
		return resultado;
	}
	
	@SuppressWarnings("unchecked")
	public List<ConsultaPersonasDto> buscaFormaPagoProv(int noPersona, int noEmpresa){
		List<ConsultaPersonasDto> listaResultado = new ArrayList<ConsultaPersonasDto>();
		sql = "";
		try{
			sql = "Select no_persona, no_empresa, id_tipo_persona, id_forma_pago ";
			sql += "\n from forma_pago_prov ";
			sql += "\n where no_persona = "+Utilerias.validarCadenaSQL( noPersona) +" ";
			sql += "\n and no_empresa in (552, "+ Utilerias.validarCadenaSQL(noEmpresa )+") ";
			sql += "\n and id_forma_pago = 3 ";
			
			listaResultado = jdbcTemplate.query(sql, new RowMapper(){
				public ConsultaPersonasDto mapRow(ResultSet rs, int idx)throws SQLException{
					ConsultaPersonasDto campos = new ConsultaPersonasDto();
					campos.setNoPersona(rs.getInt("no_persona"));
					campos.setNoEmpresa(rs.getInt("no_empresa"));
					campos.setIdTipoPersona(rs.getString("id_tipo_persona"));
					campos.setIdFormaPagoProv(rs.getInt("id_forma_pago"));
					return campos;
				}
			});	
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasDaoImpl, M: buscaFormaPagoProv");
		}return listaResultado;
	}
	
	public int insertaFormaPago (int noEmpresa, int noPersona, String tipoPersona){
		int resultado = 0;
		sql = "";		
		try{					
			sql = "Insert into forma_pago_prov ";
			sql += "\n (no_empresa, no_persona, id_tipo_persona, id_forma_pago) ";
			sql += "\n values (";
			
			if (tipoPersona.equals("P"))				
				sql += "\n 552, ";
			else
				sql += "\n "+Utilerias.validarCadenaSQL( noEmpresa) +", ";
			
			sql += "\n "+ Utilerias.validarCadenaSQL(noPersona) +", '"+ Utilerias.validarCadenaSQL(tipoPersona) +"', 3) ";
			
			resultado = jdbcTemplate.update(sql);			
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasDaoImpl, M: insertaFormaPago");
		}return resultado;
	}
	
	//**************************************************************************************************************************************
	@SuppressWarnings("unchecked")
	public List<ConsultaPersonasDto> obtenerPersonas(String condicion,boolean todos) {
		StringBuffer sql = new StringBuffer();
		List<ConsultaPersonasDto> list = new ArrayList<ConsultaPersonasDto>();
		
		try {
			sql.append(" SELECT no_persona  as ID, nombre + ' ' + paterno + ' ' + materno as describ \n");
			sql.append(" FROM persona \n");
			sql.append(" WHERE no_empresa = 1 \n");
			sql.append("	And id_tipo_persona = 'F' \n");
			sql.append("	And id_estatus_per = 'A' \n");
			if(!todos){
				sql.append("	And (nombre like '%"+Utilerias.validarCadenaSQL( condicion) +"%' or paterno like '%"+ Utilerias.validarCadenaSQL(condicion) +"%' or materno like '%"+ Utilerias.validarCadenaSQL(condicion) +"%') \n");
			}
			
			sql.append(" ORDER BY describ ");
			System.out.println(sql);
			list = jdbcTemplate.query(sql.toString(), new RowMapper(){
				public ConsultaPersonasDto mapRow(ResultSet rs, int idx)throws SQLException{
					ConsultaPersonasDto campos = new ConsultaPersonasDto();
					campos.setNoPersona(rs.getInt("ID"));
					campos.setNombre(rs.getString("describ"));
					return campos;
				}
			});
		}catch(Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasDaoImpl, M: obtenerPersonas");
		}
		return list;
	}
	
	public List<LlenaComboGralDto> obtenerRelaciones(LlenaComboGralDto obj) {
		List<LlenaComboGralDto> res = new ArrayList<LlenaComboGralDto>();
		try{
			objConsultasGenerales = new ConsultasGenerales(jdbcTemplate);
			res = objConsultasGenerales.llenarComboGral(obj);
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasDaoImpl, M: obtenerRelaciones");
		}return res;
		
	}
	
	public int existeRelacion(List<Map<String, String>> datos) {
		StringBuffer sql = new StringBuffer();
		int res = 0;
		
		try {
			sql.append(" SELECT count(no_persona) as existe \n");
			sql.append(" FROM relacion \n");
			sql.append(" WHERE no_persona_rel = "+ Utilerias.validarCadenaSQL(datos.get(0).get("noPersonaRel") +" \n"));
			sql.append("	And id_tipo_relacion = "+ Utilerias.validarCadenaSQL(datos.get(0).get("tipoRelacion") +" \n"));
			sql.append("	And no_persona = "+ Utilerias.validarCadenaSQL(datos.get(0).get("noPersona") +" \n"));
			sql.append("	And no_empresa = "+ Utilerias.validarCadenaSQL(datos.get(0).get("noEmpresa") +" \n"));
			
			
			res = jdbcTemplate.queryForInt(sql.toString());
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasDaoImpl, M: existeRelacion");
		}
		return res;
	}
	
	public int altaRelacion(ConsultaPersonasDto obj) {
		/*System.out.println("LLegando a inser alta relacion");
		StringBuffer sql = new StringBuffer();
		int res = 0;*/
		int resultado = 0;
		//String sNoEmpresa = Integer.toString(noEmpresaC).length() == 1 ? "0"+noEmpresaC : noEmpresaC+"";
		sql = "";
		
		try {
			sql = "Insert into relacion (";
			
			
			sql += "\n no_empresa,no_persona,id_tipo_relacion,no_empresa_rel, no_persona_rel, no_cuenta,  ";
			sql += "\n fec_registro, fec_alta, usuario_alta, fec_modif, usuario_modif )";
			sql += "\n values(";
			
			sql += "\n "+ 1 +", ";
			sql += "\n "+ Utilerias.validarCadenaSQL(obj.getNoPersona())+", ";
			sql += "\n "+ Utilerias.validarCadenaSQL(obj.getTipoRelacion()) +", ";
			sql += "\n "+ 1 +", ";
			sql += "\n '"+ Utilerias.validarCadenaSQL(obj.getNoPersonaRel()) +"', ";
			sql += "\n "+ Utilerias.validarCadenaSQL(obj.getCuenta()) +", ";
			sql += "\n '"+ Utilerias.validarCadenaSQL(obj.getFecha()) +"', ";
			sql += "\n (select fec_hoy from fechas), ";
			sql += "\n "+ 2+", ";
			sql += "\n (select fec_hoy from fechas), ";
			
			sql += "\n "+null+" ";
			
			sql += " \n )";
			
			
			System.out.println(sql+"sql");
			resultado = jdbcTemplate.update(sql);	
			
//			/*sql.append(" INSERT INTO relacion VALUES( \n");
//			sql.append(" "+ 1 +", "+ registro.get(0).get("noPersona") +", "+ registro.get(0).get("tipoRelacion") +", \n");
//			sql.append(" 1, "+ registro.get(0).get("noPersonaRel") +", "+ registro.get(0).get("cuenta") +", '"+ registro.get(0).get("fecha") +"', \n");
//			sql.append(" '"+ registro.get(0).get("fecha") +"', "+ registro.get(0).get("usuario") +", null, '') \n");
//			
//			System.out.println(sql);
//			res = jdbcTemplate.update(sql.toString());
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasDaoImpl, M: altaRelacion");
		}
		return resultado;
	}
	@SuppressWarnings("unchecked")
	public List<ConsultaPersonasDto> consultarRelaciones(int noPersona, int noEmpresa) {
		StringBuffer sql = new StringBuffer();
		List<ConsultaPersonasDto> list = new ArrayList<ConsultaPersonasDto>();
		
		try {
			sql.append(" SELECT r.no_empresa, r.no_persona, r.no_persona_rel, p.paterno, p.materno, p.nombre, \n");
			sql.append(" 		ctr.desc_tipo_relacion, r.fec_registro, r.no_cuenta \n");
			sql.append(" FROM relacion r, persona p, cat_tipo_relacion ctr \n");
			sql.append(" WHERE r.no_persona_rel = p.no_persona \n");
			sql.append(" 	And p.id_tipo_persona = 'F' \n");
			sql.append(" 	And r.id_tipo_relacion = ctr.id_tipo_relacion \n");
			sql.append(" 	And r.no_persona = "+ Utilerias.validarCadenaSQL(noPersona) +" \n");
			sql.append("	And r.no_empresa = "+ 1 +" \n");
			System.out.println(sql);
			list = jdbcTemplate.query(sql.toString(), new RowMapper(){
				public ConsultaPersonasDto mapRow(ResultSet rs, int idx)throws SQLException{
					ConsultaPersonasDto campos = new ConsultaPersonasDto();
					campos.setNoEmpresa(rs.getInt("no_empresa"));
					campos.setNoPersona(rs.getInt("no_persona"));
					campos.setNoPersonaRel(rs.getInt("no_persona_rel"));
					campos.setPaterno(rs.getString("paterno"));
					campos.setMaterno(rs.getString("materno"));
					campos.setNombre(rs.getString("nombre"));
					campos.setDescTipoRelacion(rs.getString("desc_tipo_relacion"));
					campos.setFechaIngreso(rs.getString("fec_registro"));
					campos.setCuenta(rs.getString("no_cuenta"));
					return campos;
				}
			});
		}catch(Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasDaoImpl, M: consultarRelaciones");
		}
		return list;
	}
	
	public int eliminarRelaciones(List<Map<String, String>> datos, int i) {
		StringBuffer sql = new StringBuffer();
		int res = 0;
		
		try {
			sql.append(" DELETE FROM relacion \n");
			sql.append(" WHERE no_empresa = "+ Utilerias.validarCadenaSQL(datos.get(i).get("noEmpresa")) +" \n");
			sql.append(" 	And no_persona = "+ Utilerias.validarCadenaSQL(datos.get(i).get("noPersona")) +" \n");
			sql.append(" 	And no_persona_rel = "+ Utilerias.validarCadenaSQL(datos.get(i).get("noPersonaRel")) +" \n");
			
			res = jdbcTemplate.update(sql.toString());
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasDaoImpl, M: eliminarRelaciones");
		}
		return res;
	}
	
	public int modificarReferencia(String tipoReferencia, int noPersona, String tipoPersona) {
		StringBuffer sql = new StringBuffer();
		int res = 0;
		
		try {
			sql.append(" UPDATE PERSONA SET referencia_cte='"+tipoReferencia+"' \n");
			sql.append(" WHERE no_persona = "+ Utilerias.validarCadenaSQL(noPersona) +" \n");
			sql.append(" 	And id_tipo_persona = '"+ Utilerias.validarCadenaSQL(tipoPersona) +"' \n");
			
			res = jdbcTemplate.update(sql.toString());
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasDaoImpl, M: modificarReferencia");
		}
		return res;
	}
	
	//******************************************************************************************************************************
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

	@Override
	public List<Map<String, String>> reportePersonas(String tipoPersona) {
		List<Map<String, String>> listaResultado = new ArrayList<Map<String, String>>();
		sql = "";
		try{
			sql = "Select case when p.razon_social = '' or p.razon_social is null then p.nombre + ' ' + p.paterno + ' ' + p.materno else p.razon_social end as nombre,";
			sql += "\n (select desc_estatus as estatus from cat_estatus where clasificacion = 'PER' and id_estatus = p.id_estatus_per) as id_estatus, p.* ";
			sql	+= "\n , id_tipo_direccion, calle_no, colonia, id_cp, \n"; 
			sql += "deleg_municipio, ciudad, id_estado, id_pais from persona p left join \n";
			System.out.println(tipoPersona);
			if (tipoPersona.equals("P")||tipoPersona.equals("K") ){
				sql += "direccion d on d.no_persona= p.no_persona \n";
			}else{
				sql += "direccion d on d.no_persona= p.no_persona \n";
			}
			sql += "\n  where ";
			
				sql += "\n p.id_estatus_per <> 'I' ";
			
			//Se valida el tipo de persona
			if (!tipoPersona.equals(""))
				sql += "\n and p.id_tipo_persona = '"+ Utilerias.validarCadenaSQL(tipoPersona) +"' ";
			
			//Clasificacion del tipo de persona
			if (tipoPersona.equals("F") || tipoPersona.equals("B"))
				sql += "\n and p.no_empresa = 1 ";
			else if(tipoPersona.equals("R") || tipoPersona.equals("P")) {
				sql += "\n and p.no_empresa in (552) ";
				if(tipoPersona.equals("P")) {
					System.out.println("si es proveedor");
					
				}
			}
			
			sql += "order by p.no_empresa ";
			System.out.println("-----------------");
			System.out.println("imprime query");
			System.out.println("-----------------");
			System.out.println(sql);
			listaResultado = jdbcTemplate.query(sql, new RowMapper<Map<String, String>>(){
				public Map<String, String> mapRow(ResultSet rs, int idx)throws SQLException{
					Map<String, String> campos = new HashMap<String, String>();
					campos.put("equivalePersona",rs.getString("equivale_persona"));
					campos.put("nombre",rs.getString("nombre"));
					campos.put("noEmpresa",rs.getString("no_empresa"));
					campos.put("idTipoPersona",rs.getString("id_tipo_persona"));					
					campos.put("noPersona",rs.getString("no_persona"));
					campos.put("fisicaMoral",rs.getString("pers_juridica"));
					campos.put("estatus",rs.getString("id_estatus"));
					campos.put("idEstatus",rs.getString("id_estatus_per"));
					campos.put("idTipoD",rs.getString("id_tipo_direccion"));
					campos.put("calle",rs.getString("calle_no"));
					campos.put("colonia",rs.getString("colonia"));
					campos.put("cp",rs.getString("id_cp"));
					campos.put("delegacion",rs.getString("deleg_municipio"));
					campos.put("ciudad",rs.getString("ciudad"));
					campos.put("estado",rs.getString("id_estado"));
					campos.put("pais",rs.getString("id_pais"));
					
					return campos;
				}
			});
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasDaoImpl, M: llenaGrid");
		}return listaResultado;	
	}

	@Override
	public void eliminaPersona(ConsultaPersonasDto obj) {
			
			sql = "";
			try{
				sql = "Delete from persona ";
				sql += "\n where no_persona = "+ Utilerias.validarCadenaSQL(obj.getNoPersona())+" ";
			
				
				jdbcTemplate.update(sql);
			}
			catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasDaoImpl, M: eliminaPersona");
			}
		}

	@Override
	public void eliminaEmpresa(ConsultaPersonasDto obj) {
		
		sql = "";
		try{
			sql = "Delete from empresa ";
			sql += "\n where no_empresa = "+ Utilerias.validarCadenaSQL(obj.getNoEmpresa())+" ";
		
			
			jdbcTemplate.update(sql);
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasDaoImpl, M: eliminaEmpresa");
		}
		
	}

	@Override
	public void eliminaLinea(ConsultaPersonasDto obj) {
		
		sql = "";
		try{
			sql = "Delete from linea ";
			sql += "\n where no_empresa = "+ Utilerias.validarCadenaSQL(obj.getNoEmpresa())+" ";
		
			
			jdbcTemplate.update(sql);
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasDaoImpl, M: eliminaLinea");
		}
		
		
	}

	@Override
	public List<ConsultaPersonasDto> llenaGridMedios(String equivale) {
		List<ConsultaPersonasDto> listaResultado = new ArrayList<ConsultaPersonasDto>();
		String empresa = "";
		StringBuffer sql = new StringBuffer();
		try{
			System.out.println("llego daoimpl");
			sql.append("select * from medios_contacto m join cat_tipo_medio ct \n");
			sql.append("on m.id_tipo_medio=ct.id_tipo_medio where no_persona="+equivale+" \n");
			
			
			System.out.println("-----------------");
			System.out.println("imprime query");
			System.out.println("-----------------");
			System.out.println(sql);
			listaResultado = jdbcTemplate.query(sql.toString(), new RowMapper<ConsultaPersonasDto>(){
				public ConsultaPersonasDto mapRow(ResultSet rs, int idx)throws SQLException{
					ConsultaPersonasDto campos = new ConsultaPersonasDto();
					campos.setContactoMedio(rs.getString("contacto"));
					campos.setDescMedio(rs.getString("desc_tipo_medio"));
					return campos;
				}
			});
		}
		catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasDaoImpl, M: llenaGrid");
		}
		
		return listaResultado;
	}

	@Override
	public int seleccionarFolioReal(String tipoFolio) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int actualizarFolioReal(String tipoFolio) {
		// TODO Auto-generated method stub
		return 0;
	}

	
		
	

	

}