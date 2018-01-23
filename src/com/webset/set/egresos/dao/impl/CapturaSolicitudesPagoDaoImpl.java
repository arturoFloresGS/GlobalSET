package com.webset.set.egresos.dao.impl;
/**
 * @autor Cristian Garcia Garcia
 */
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.webset.set.egresos.dao.CapturaSolicitudesPagoDao;
import com.webset.set.egresos.dto.PolizaContableDto;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.ConsultasGenerales;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utilerias.GlobalSingleton;
import com.webset.set.utilerias.dto.CajaUsuarioDto;
import com.webset.set.utilerias.dto.GeneradorDto;
import com.webset.set.utilerias.dto.LlenaComboDivisaDto;
import com.webset.set.utilerias.dto.LlenaComboEmpresasDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.set.utilerias.dto.LlenaFormaPagoDto;
import com.webset.set.utilerias.dto.MovimientoDto;
import com.webset.set.utilerias.dto.ParametroDto;
import com.webset.utils.tools.Utilerias;

import mx.com.gruposalinas.Poliza.DT_Polizas_ResponseResponse;

public class CapturaSolicitudesPagoDaoImpl implements CapturaSolicitudesPagoDao{
	private JdbcTemplate jdbcTemplate;
	Bitacora bitacora=new Bitacora();
	ConsultasGenerales consultasGenerales;
	Funciones funciones= new Funciones();
	GlobalSingleton globalSingleton;
	String gsDBM="SQL SERVER";
	
	
	
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}
	
	public String consultarConfiguraSet(int indice){
		consultasGenerales= new ConsultasGenerales(jdbcTemplate);
		return consultasGenerales.consultarConfiguraSet(indice);
	}

	public List<LlenaComboEmpresasDto>obtenerEmpresas(int idUsuario){
		consultasGenerales = new ConsultasGenerales(jdbcTemplate);
		return consultasGenerales.llenarComboEmpresas(idUsuario);
	}

	public List<LlenaComboDivisaDto> obtenerDivisas(){
		consultasGenerales= new ConsultasGenerales(jdbcTemplate);
		return consultasGenerales.llenarComboDivisa();
	}
	public List<LlenaFormaPagoDto>obtenerFormaPago(){
		consultasGenerales = new ConsultasGenerales(jdbcTemplate);
		return consultasGenerales.llenarComboFormaPago();
	}
	public List<LlenaFormaPagoDto> llenarComboFormaPagoParametrizado(String poliza, String rubro, String Grupo){
		StringBuffer sql= new StringBuffer();
		List<LlenaFormaPagoDto> listFormPag= new ArrayList<LlenaFormaPagoDto>();
		try{
		
			sql.append("\n select * from cat_forma_pago	");
			sql.append("\n Where Id_Forma_Pago in ((");
			sql.append("\n  Select case when cargo_en_cuenta = 'SI' then 5 else 0 end  From Configura_Solicitud_Pago Where Id_Grupo = ");
			sql.append(Grupo);
			sql.append("\n And Id_Rubro = ");
			sql.append(rubro);
			sql.append("\n And Id_Poliza = ");
			sql.append(poliza);
			sql.append(" ),( ");

			sql.append("\n  Select case when transferencia = 'SI' then 3 else 0 end  From Configura_Solicitud_Pago Where Id_Grupo = ");
			sql.append(Grupo);
			sql.append("\n And Id_Rubro = ");
			sql.append(rubro);
			sql.append("\n And Id_Poliza = ");
			sql.append(poliza);
			sql.append(" ) ,(");
			
			sql.append("\n  Select case when cheque_de_caja = 'SI' then 10 else 0 end  From Configura_Solicitud_Pago Where Id_Grupo = ");
			sql.append(Grupo);
			sql.append("\n And Id_Rubro = ");
			sql.append(rubro);
			sql.append("\n And Id_Poliza = ");
			sql.append(poliza);
			sql.append(" ) ,(");
			
			sql.append("\n  Select case when cheque = 'SI' then 1 else 0 end  From Configura_Solicitud_Pago Where Id_Grupo = ");
			sql.append(Grupo);
			sql.append("\n And Id_Rubro = ");
			sql.append(rubro);
			sql.append("\n And Id_Poliza = ");
			sql.append(poliza);
			sql.append(" ) )");
			
			System.out.println("\n consulta parametrizada "+sql);
			
			listFormPag= jdbcTemplate.query(sql.toString(), new RowMapper<LlenaFormaPagoDto>(){
				public LlenaFormaPagoDto mapRow(ResultSet rs, int idx) throws SQLException {
					LlenaFormaPagoDto cons = new LlenaFormaPagoDto();
					cons.setIdFormaPago(rs.getInt("id_forma_pago"));
					cons.setDescFormaPago(rs.getString("desc_forma_pago"));
					return cons;
				}});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Utilerias, C:ConsultasGenerales, M:llenarComboFormaPago");
		}
		return listFormPag;
	}

	public Date obtenerFechaHoy(){
		consultasGenerales = new ConsultasGenerales(jdbcTemplate);
		return consultasGenerales.obtenerFechaHoy();
	}
	public double obtenerTipoCambio(String idDivisa, Date fecha)
	{
		consultasGenerales= new ConsultasGenerales(jdbcTemplate);
		return consultasGenerales.obtenerTipoCambio(idDivisa, fecha);
	}
	public List<CajaUsuarioDto>obtenerCajaUsuario(int idUsuario){
		consultasGenerales = new ConsultasGenerales(jdbcTemplate);
		return consultasGenerales.obtenerCajaUsuario(idUsuario);
	}
	public List<LlenaComboGralDto>llenarComboGral(LlenaComboGralDto dto)
	{
		consultasGenerales = new ConsultasGenerales(jdbcTemplate);
		//return consultasGenerales.llenarComboGral(dto);
		return consultasGenerales.llenarComboGralB(dto);
	}
	public String consultarConfiguaraSet(int indice){
		consultasGenerales=new ConsultasGenerales(jdbcTemplate);
		return consultasGenerales.consultarConfiguraSet(indice);
	}
	public int seleccionarFolioReal(String tipoFolio){
		consultasGenerales= new ConsultasGenerales(jdbcTemplate);
		return consultasGenerales.seleccionarFolioReal(tipoFolio);
	}
	public int actualizarFolioReal(String tipoFolio){
		consultasGenerales= new ConsultasGenerales(jdbcTemplate);
		return consultasGenerales.actualizarFolioReal(tipoFolio);
	}
	
	public Map<String, Object> ejecutarGenerador(GeneradorDto generadorDto){
		consultasGenerales= new ConsultasGenerales(jdbcTemplate);
		return consultasGenerales.ejecutarGenerador(generadorDto);
	}
	//insertaBitacoraPoliza
	public Map<String, Object> insertaBitacoraPoliza(DT_Polizas_ResponseResponse dt_Polizas_ResponseResponse){
		Map<String, Object> mapReturn= new HashMap<String, Object>();
		StringBuffer sqlInsert = new StringBuffer();
		mapReturn.put("msgUsuario", "Error desconocido.");
		mapReturn.put("bandera", false);
		try {
			sqlInsert.append("Insert Into BITACORA_POLIZAS (NO_EMPRESA,NO_DOC_SAP,SECUENCIA,ID_POLIZA_SAP,MENSAJE) Values( '");
			sqlInsert.append(dt_Polizas_ResponseResponse.getNO_EMPRESA());
			sqlInsert.append("','");
			sqlInsert.append(dt_Polizas_ResponseResponse.getNO_DOC_SAP());
			sqlInsert.append("','");
			sqlInsert.append(dt_Polizas_ResponseResponse.getSECUENCIA());
			sqlInsert.append("','");
			sqlInsert.append(dt_Polizas_ResponseResponse.getID_POLIZA_SAP());
			sqlInsert.append("','");
			sqlInsert.append(dt_Polizas_ResponseResponse.getMENSAJE());
			sqlInsert.append("')");
			if(jdbcTemplate.update(sqlInsert.toString())!=0)
				//if(dt_Polizas_ResponseResponse.getNO_DOC_SAP() != null && dt_Polizas_ResponseResponse.getNO_DOC_SAP().equals("")){
				if(dt_Polizas_ResponseResponse.getDOC_POLIZA_SAP() != null && !dt_Polizas_ResponseResponse.getDOC_POLIZA_SAP().equals("")){ 
					  mapReturn.put("msgUsuario",dt_Polizas_ResponseResponse.getDOC_POLIZA_SAP().toString());
					mapReturn.put("bandera", true);
				}else
					mapReturn.put("msgUsuario","El proceso en el web service genero el siguiente error: "+
				dt_Polizas_ResponseResponse.getMENSAJE() != null && !dt_Polizas_ResponseResponse.getMENSAJE().equals("")?
						dt_Polizas_ResponseResponse.getMENSAJE().toString():"Retorno del campo mensaje vacio(Batch Input)." );
			else
				mapReturn.put("msgUsuario", "Error al insertar en bitacora de polizas.");
			
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Egresos, C:CapturaSolicitudesPagoDao, M:insertaBitacoraPoliza");
		}
		return mapReturn;
	}
	
	
	@Override
	public List<String> fijaPlantilla(int idPlantilla, int idUsuario) {
		List<List<String>> valorCampos = new ArrayList<List<String>>();
		StringBuffer sqlPlantilla = new StringBuffer();
		try {
			
			sqlPlantilla.append(" Select Cr.Desc_Rubro As Dec_Rubro,Cg.Desc_Grupo,Zp.Nombre_Poliza, ");
			sqlPlantilla.append("\n Case When P.Razon_Social = Null Then '' Else P.Razon_Social End As Razon_Social, ");
			sqlPlantilla.append("\n Case When cf.desc_forma_pago = Null Then '' Else cf.desc_forma_pago End As desc_forma_pago, ");
			sqlPlantilla.append("\n Pl.*,cbp.desc_banco  "); 
			sqlPlantilla.append("From Plantilla_Solicitud_Pago Pl Join Cat_Rubro Cr On Pl.Id_Rubro = Cr.Id_Rubro ");
			sqlPlantilla.append("\n Join Cat_Grupo Cg On Cg.Id_Grupo = Pl.Id_Grupo ");
			sqlPlantilla.append("\n Join Zimp_Polizamanual Zp On Zp.Id_Poliza= Pl.Id_Poliza ");
			sqlPlantilla.append("\n Left Join persona p on p.equivale_persona = pl.id_beneficiario ");
			sqlPlantilla.append("\n left join Cat_Forma_Pago cf on cf.id_forma_pago = pl.FORMA_PAGO ");
			sqlPlantilla.append("\n left join cat_banco cbp on cbp.id_banco = pl.banco_beneficiario ");
			sqlPlantilla.append("WHERE ID_USUARIO = ");
			sqlPlantilla.append(idUsuario);
			sqlPlantilla.append(" and ID_PLANTILLA = ");
			sqlPlantilla.append(idPlantilla);
			System.out.println("\n consulta plantilla "+sqlPlantilla);
			valorCampos = jdbcTemplate.query(sqlPlantilla.toString(), new RowMapper<List<String>>(){
				public List<String> mapRow(ResultSet rs, int idx) throws SQLException {
					List<String> componenteHabilitados= new ArrayList<String>();
					componenteHabilitados.add(/*"texto",*/ rs.getString("nombre_poliza")== null? "" : rs.getString("nombre_poliza"));
					componenteHabilitados.add(/*"texto",*/ rs.getString("desc_grupo")== null? "" : rs.getString("desc_grupo"));
					componenteHabilitados.add(/*"texto",*/ rs.getString("Dec_Rubro")== null? "" : rs.getString("Dec_Rubro"));
					componenteHabilitados.add(/*"texto",*/ rs.getString("razon_social")== null? "" : rs.getString("razon_social"));//
					componenteHabilitados.add(/*"texto",*/ rs.getString("desc_forma_pago")== null? "" : rs.getString("desc_forma_pago"));
					
					componenteHabilitados.add(/*"texto",*/ rs.getString("NO_EMPRESA")== null? "" : rs.getString("NO_EMPRESA"));
					componenteHabilitados.add(/*"texto",*/ rs.getString("ID_POLIZA")== null? "" : rs.getString("ID_POLIZA"));
					componenteHabilitados.add(/*"texto",*/ rs.getString("ID_GRUPO")== null? "" : rs.getString("ID_GRUPO"));
					componenteHabilitados.add(/*"texto",*/ rs.getString("ID_RUBRO")== null? "" : rs.getString("ID_RUBRO"));
					componenteHabilitados.add(/*"texto",*/ rs.getString("ID_BENEFICIARIO")== null? "" : rs.getString("ID_BENEFICIARIO"));
					componenteHabilitados.add(/*"texto",*/ rs.getString("FORMA_PAGO")== null? "" : rs.getString("FORMA_PAGO"));
//					//componenteHabilitados.add(/*"texto",*/ rs.getString("FECHA_FACTURA")== null? "" : rs.getString("FECHA_FACTURA"));
//					componenteHabilitados.add(/*"texto",*/ rs.getString("DIVISA_ORIGINAL")== null? "" : rs.getString("DIVISA_ORIGINAL"));
//					//componenteHabilitados.add(/*"texto",*/ rs.getString("IMPORTE_ORIGINAL")== null? "" : rs.getString("IMPORTE_ORIGINAL"));
					componenteHabilitados.add(/*"texto",*/ rs.getString("PAGO_SIN_POLIZA_CONTABLE")!= null && rs.getString("PAGO_SIN_POLIZA_CONTABLE")!= "NO"? "true" : "false");
					componenteHabilitados.add(/*"texto",*/ rs.getString("TRANF_NAC_EXT")!= null && rs.getString("TRANF_NAC_EXT") != "NO"? "true": "false");
//					componenteHabilitados.add(/*"texto",*/ rs.getString("BANCO_BENEFICIARIO")== null? "" : rs.getString("BANCO_BENEFICIARIO"));

//					componenteHabilitados.add(/*"texto",*/ rs.getString("SUCURSAL")== null? "" : rs.getString("SUCURSAL"));
//					componenteHabilitados.add(/*"texto",*/ rs.getString("PLAZA")== null? "" : rs.getString("PLAZA"));
					componenteHabilitados.add(/*"texto",*/ rs.getString("TRANF_CHEQUE_LEYENDA")!= null && rs.getString("TRANF_CHEQUE_LEYENDA")!= "NO"?  "true" : "false");
					componenteHabilitados.add(/*"texto",*/ rs.getString("IMPRIME_CAJA")== null? "" : rs.getString("IMPRIME_CAJA"));
					componenteHabilitados.add(/*"texto",*/ rs.getString("BANCO_BENEFICIARIO")== null && rs.getString("BANCO_BENEFICIARIO").equals("")? "" : rs.getString("BANCO_BENEFICIARIO"));
					componenteHabilitados.add(/*"texto",*/ rs.getString("CHEQUERA") == null? "" : rs.getString("CHEQUERA")); 
					componenteHabilitados.add(/*"texto",*/ rs.getString("CLABE")== null? "" : rs.getString("CLABE"));
					componenteHabilitados.add(/*"texto",*/ rs.getString("TEXTO")== null? "" : rs.getString("TEXTO"));
//					componenteHabilitados.add(/*"fechaPago",*/ rs.getString("FECHA_PAGO")== null? "" : rs.getString("FECHA_PAGO"));
//					componenteHabilitados.add(/*"divisaPago",*/ rs.getString("DIVISA_PAGO")== null? "" : rs.getString("DIVISA_PAGO"));
					//componenteHabilitados.add(/*"tipoCambio",*/ rs.getString("TIPO_CAMBIO")== null? "" : rs.getString("TIPO_CAMBIO"));
//					componenteHabilitados.add(/*"importePago",*/ rs.getString("IMPORTE_PAGO")== null? "" : rs.getString("IMPORTE_PAGO"));
//					
					componenteHabilitados.add(/*"texto_cabecera",*/ rs.getString("TEXTO_CABECERA")== null? "" : rs.getString("TEXTO_CABECERA"));
					componenteHabilitados.add(/*"asignacion",*/ rs.getString("ASIGNACION")== null? "" : rs.getString("ASIGNACION"));
					componenteHabilitados.add(/*"referencia",*/ rs.getString("REFERENCIA")== null? "" : rs.getString("REFERENCIA"));
					componenteHabilitados.add(/*"referenciaPago",*/ rs.getString("REFERENCIA_PAGO")== null? "" : rs.getString("REFERENCIA_PAGO"));
					componenteHabilitados.add(/*"solicita",*/ rs.getString("SOLICITA")== null? "" : rs.getString("SOLICITA"));
//					
					componenteHabilitados.add(/*"centroCostos",*/ rs.getString("CENTRO_COSTOS")== null? "" : rs.getString("CENTRO_COSTOS"));
					componenteHabilitados.add(/*"division",*/ rs.getString("DIVISION")== null? "" : rs.getString("DIVISION"));
					componenteHabilitados.add(/*"orden",*/ rs.getString("ORDEN")== null || rs.getString("ORDEN").equals("null") ? "" : rs.getString("ORDEN"));
					componenteHabilitados.add(/*"autoriza",*/ rs.getString("AUTORIZA")== null? "" : rs.getString("AUTORIZA"));
//					
					componenteHabilitados.add(/*"observaciones",*/ rs.getString("OBSERVACIONES")== null? "" : rs.getString("OBSERVACIONES"));
					componenteHabilitados.add(/*"banco",*/ rs.getString("desc_banco")== null? "" : rs.getString("desc_banco"));
//					componenteHabilitados.add(/*"documento",*/ rs.getString("DOCUMENTO")== null? "" : rs.getString("DOCUMENTO"));
//					componenteHabilitados.add(/*"AREA_DESTINO",*/ rs.getString("AREA_DESTINO")== null? "" : rs.getString("AREA_DESTINO"));
//					
					return componenteHabilitados;
				}});
			System.out.println("plantilla "+valorCampos.get(0));
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Egresos, C:CapturaSolicitudesPagoDao, M:fijaPlantilla");
		}
		return valorCampos != null && !valorCampos.isEmpty() ?valorCampos.get(0) : new ArrayList<String>();
	}
	public List<Boolean> habilitaComponetesPerfilEmpresa(String rubro, String poliza, String grupo){
		 List<List<Boolean>> componentesHabilitados = new ArrayList<List<Boolean>>();
		StringBuffer sql = new StringBuffer();
		
		try {
			sql.append("");
			sql.append("Select * From Configura_Solicitud_Pago where id_rubro = ");
			sql.append("\n ");
			sql.append(rubro);
			sql.append("\n  and Id_Poliza = ");
			sql.append(poliza);
			sql.append("\n   and Id_Grupo = ");
			sql.append(grupo);
			System.out.println("\n Consulta "+sql);
			componentesHabilitados = jdbcTemplate.query(sql.toString(), new RowMapper<List<Boolean>>(){
				public List<Boolean> mapRow(ResultSet rs, int idx) throws SQLException {
					List<Boolean> componenteHabilitados= new ArrayList<Boolean>();
					componenteHabilitados.add(/*"texto",*/ rs.getString("TEXTO").equals("SI")? true: false);
					componenteHabilitados.add(/*"fechaPago",*/ rs.getString("FECHA_PAGO").equals("SI")? true: false);
					componenteHabilitados.add(/*"divisaPago",*/ rs.getString("TEXTO").equals("SI")? true: false);
					componenteHabilitados.add(/*"tipoCambio",*/ rs.getString("TEXTO").equals("SI")? true: false);
					componenteHabilitados.add(/*"importePago",*/ rs.getString("TEXTO").equals("SI")? true: false);
					
					componenteHabilitados.add(/*"texto_cabecera",*/ rs.getString("TEXTO_CABECERA").equals("SI")? true: false);
					componenteHabilitados.add(/*"asignacion",*/ rs.getString("ASIGNACION").equals("SI")? true: false);
					componenteHabilitados.add(/*"referencia",*/ rs.getString("REFERENCIA").equals("SI")? true: false);
					componenteHabilitados.add(/*"referenciaPago",*/ rs.getString("REFERENCIA_PAGO").equals("SI")? true: false);
					componenteHabilitados.add(/*"solicita",*/ rs.getString("SOCIEDAD_GL").equals("SI")? true: false);
					
					componenteHabilitados.add(/*"centroCostos",*/ rs.getString("CENTRO_COSTOS").equals("SI")? true: false);
					componenteHabilitados.add(/*"division",*/ rs.getString("DIVISION").equals("SI")? true: false);
					componenteHabilitados.add(/*"orden",*/ rs.getString("ORDEN").equals("SI")? true: false);
					componenteHabilitados.add(/*"autoriza",*/ rs.getString("BANCO_INTERLOCUTOR").equals("SI")? true: false);
					
					componenteHabilitados.add(/*"observaciones",*/ rs.getString("OBSERVACIONES").equals("SI")? true: false);
					
					componenteHabilitados.add(/*"documento",*/ rs.getString("Pago_Sin_Poliza").equals("SI")? true: false);
					componenteHabilitados.add(/*"AREA_DESTINO",*/ rs.getString("OBSERVACIONES").equals("SI")? true: false);
					
					return componenteHabilitados;
				}});
			System.out.println(componentesHabilitados);
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Egresos, C:CapturaSolicitudesPagoDao, M:habilitaComponetesPerfilEmpresa");
		}
		return componentesHabilitados != null && !componentesHabilitados.isEmpty() ?componentesHabilitados.get(0): new ArrayList<Boolean>();
	}
	
	public List<LlenaComboGralDto> obtenerBancoBenef(Map<String,Object> datos){
		String sql="";
		List<LlenaComboGralDto>listDatos= new ArrayList<LlenaComboGralDto>();
		try{
		    sql+= "SELECT id_banco as ID, desc_banco as DESCRIPCION ";
		    sql+= "\n	FROM cat_banco";
		    sql+= "\n	WHERE nac_ext = '" +Utilerias.validarCadenaSQL(String.valueOf(datos.get("nacExt")))+"'";
		    sql+= "\n	AND id_banco in (Select distinct c.id_banco from ctas_banco c, persona p";
		    sql+= "\n	Where p.no_persona = c.no_persona";
		    sql+= "\n	And p.id_tipo_persona = 'P' ";
		    
		    if(datos.get("provUnico").equals("false"))
		    //    sql+= " And (p.equivale_persona = '" +datos.get("idPersona")+ "' or p.no_persona = '" +datos.get("idPersona")+ "')";
	    	sql+= "\n  AND  c.no_persona = (Select no_persona From Persona Where Equivale_Persona = '"+Utilerias.validarCadenaSQL(String.valueOf(datos.get("idPersona")))+"' And Id_Tipo_Persona = 'P')";
		    sql+= "\n	And c.id_tipo_persona = 'P' ";
		    sql+= "\n	And c.no_empresa = "+datos.get("noEmpresa");
		    //SE AGREG� PARA CONSULTAR SOLO LAS CHEQUERAS DE LA DIVISA;
		    sql+= "\n	And c.id_divisa = '" +datos.get("idDivisa")+ "')";
		    System.out.println("------------------------------------- uenvedscd");
		 System.out.println(sql);
		 System.out.println("------------------------------------- uenvedscd");
		    listDatos= jdbcTemplate.query(sql, new RowMapper<LlenaComboGralDto>(){
				public LlenaComboGralDto mapRow(ResultSet rs, int idx) throws SQLException {
					LlenaComboGralDto cons = new LlenaComboGralDto();
					cons.setId(rs.getInt("ID"));
					cons.setDescripcion(rs.getString("DESCRIPCION"));
					return cons;
				}});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Egresos, C:CapturaSolicitudesPagoDao, M:obtenerBancoBenef");
		}
		return listDatos;
	}
	/*Public Function FunSQLCombo395(ByVal pvvValor1 As Variant, ByVal pvvValor2 As Variant, _
                               ByVal pvvValor3 As Variant, Optional bProveedor As Boolean) As ADODB.Recordset*/
	
	public List<LlenaComboGralDto> obtenerChequeras(Map<String,Object> datos){
		String sql="";
		List<LlenaComboGralDto>listDatos= new ArrayList<LlenaComboGralDto>();
		try{
		    sql+= "SELECT c.id_chequera as ID, c.id_chequera + ' - ' + c.id_banco_interlocutor as DESCRIPCION ";
		    sql+= "\n	FROM ctas_banco c, persona p";
		    sql+= "\n 	WHERE c.no_persona = p.no_persona";
		    sql+= "\n	AND p.id_tipo_persona = 'P'";
		    sql+= "\n	AND (p.equivale_persona = '"+Utilerias.validarCadenaSQL(String.valueOf(datos.get("idPersona")))+"' or p.no_persona = '"+Utilerias.validarCadenaSQL(String.valueOf(datos.get("idPersona")))+"')";
		    sql+= "\n  	AND p.no_empresa in (552,"+datos.get("idEmpresa")+")";
		    sql+= "\n  	AND c.id_tipo_persona = 'P'";
		    sql+= "\n  	AND c.id_banco = " +datos.get("idBanco");
		   
		    sql+= "\n  	AND c.no_empresa in (552,"+datos.get("idEmpresa")+")";
		    System.out.println(" chequeras del banco ----->>>> "+sql);
		    listDatos= jdbcTemplate.query(sql, new RowMapper<LlenaComboGralDto>(){
				public LlenaComboGralDto mapRow(ResultSet rs, int idx) throws SQLException {
					LlenaComboGralDto cons = new LlenaComboGralDto();
					//cons.setId(Integer.parseInt(rs.getString("ID")));//se cambio mandando solo la desc como string para evitar la excepcion cast Int
					cons.setDescripcion(rs.getString("DESCRIPCION"));
					return cons;
				}});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Egresos, C:CapturaSolicitudesPagoDao, M:obtenerChequeras");
			e.printStackTrace();
		}
		
		return listDatos;
	}
	
	public List<Map<String,String>> obtenerChequerasBancoInterlocutor(Map<String,Object> datos){
		String sql="";
		List<Map<String, String>> listDatos=new ArrayList<Map<String, String>>();
		try{
		    sql+= "SELECT c.id_chequera as ID, c.id_chequera as DESCRIPCION, id_clabe as clabe ";
			// sql+= "SELECT c.id_chequera as ID, c.id_chequera + ' - ' + c.id_banco_interlocutor as DESCRIPCION, id_clabe as clabe ";
		    sql+= "\n	FROM ctas_banco c, persona p";
		    sql+= "\n 	WHERE c.no_persona = p.no_persona";
		    sql+= "\n	AND p.id_tipo_persona = 'P'";
		    //sql+= "\n	AND (p.equivale_persona = '"+datos.get("idPersona")+"' or p.no_persona = '"+datos.get("idPersona")+"')";
		    sql+= "\n	AND  c.no_persona = (Select no_persona From Persona Where Equivale_Persona = '"+Utilerias.validarCadenaSQL(String.valueOf(datos.get("idPersona")))+"' And Id_Tipo_Persona = 'P')";
		    sql+= "\n  	AND p.no_empresa in (552,"+datos.get("idEmpresa")+")";
		    sql+= "\n  	AND c.id_tipo_persona = 'P'";
		    sql+= "\n  	AND c.id_banco = " +datos.get("idBanco");
		    sql+= "\n  	AND c.no_empresa in (552,"+datos.get("idEmpresa")+")";
		    sql+= "\n   and C.id_divisa= '" +Utilerias.validarCadenaSQL(String.valueOf(datos.get("idDivisa")))+"'";
		    System.out.println("\n llena la chequeras"+sql);
		    listDatos= jdbcTemplate.query(sql, new RowMapper<Map<String, String>>(){
				public Map<String, String> mapRow(ResultSet rs, int idx) throws SQLException {
					HashMap<String, String> cons = new HashMap<String, String>();
					cons.put("id", rs.getString("ID"));
					cons.put("descripcion",rs.getString("DESCRIPCION"));
					cons.put("clabe",rs.getString("CLABE"));
					
					return cons;
				}});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Egresos, C:CapturaSolicitudesPagoDao, M:obtenerChequerasBancoInterlocutor");
			e.printStackTrace();
		}
		System.out.println("esta es la lista de chequera \n"+listDatos);
		return listDatos != null && !listDatos.isEmpty() ?listDatos: new ArrayList<Map<String, String>>();
		
	}
	
	/*Public Function FunSQLSelect922(ByVal pvvValor1 As Variant, ByVal pvvValor2 As Variant, _
                                ByVal pvvValor3 As Variant) As ADODB.Recordset*/
	
	
	public List<Map<String,Integer>> obtenerSucPlazClabe(Map<String, Object> datos){
		System.out.println("-------------");
		System.out.println(datos.get("idPersona")+""+datos.get("idChequera")+""+datos.get("idEmpresa"));
		String sql="";
		List<Map<String, Integer>> listDatos=new ArrayList<Map<String, Integer>>();
		try{
		  	sql+= "SELECT c.sucursal, c.plaza, c.id_clabe ";
		    sql+= "\n	FROM ctas_banco c, persona p";
		    sql+= "\n	WHERE p.no_persona = c.no_persona";
		    sql+= "\n	AND p.id_tipo_persona = 'P' ";
		    sql+= "\n	AND (p.equivale_persona = '"+Utilerias.validarCadenaSQL(String.valueOf(datos.get("idPersona")))+"' or p.no_persona = '"+Utilerias.validarCadenaSQL(String.valueOf(datos.get("idPersona")))+"')";
		    sql+= "\n	AND c.id_tipo_persona = 'P' ";
		    sql+= "\n	AND c.id_chequera = '"+Utilerias.validarCadenaSQL(String.valueOf(datos.get("idChequera")))+"'";
		    sql+= "\n	AND c.no_empresa in (552,"+datos.get("idEmpresa")+")";
		    System.out.println("consulta "+sql);
		   listDatos= jdbcTemplate.query(sql, new RowMapper<Map<String, Integer>>(){
				public Map<String, Integer> mapRow(ResultSet rs, int idx) throws SQLException {
					HashMap<String, Integer> cons = new HashMap<String, Integer>();
					cons.put("idSucursal",rs.getInt("sucursal"));
					cons.put("idPlaza", rs.getInt("plaza"));
					cons.put("idClabe", Integer.parseInt(rs.getString("id_clabe")));
					return cons;
				}});
		 
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Egresos, C:CapturaSolicitudesPagoDao, M:obtenerSucPlazClabe");	
		}
		return listDatos;
	}
	
	
	/*Public Function FunSQLSelect928(ByVal pvvValor1 As Variant, ByVal pvvValor2 As Variant, Optional psEquivaleBenef As String) As ADODB.Recordset  */
	public List<MovimientoDto> consultarMovimientos(Map<String, Object> datos){
		String sql="";
		List<MovimientoDto>listDatos=new ArrayList<MovimientoDto>();
		try{
			sql+= "SELECT folio_ref,no_cliente,id_tipo_docto,id_forma_pago,no_folio_det,";
		    sql+= "       id_tipo_operacion,id_cve_operacion,no_cuenta,no_docto,fec_valor,fec_valor_original,";		    
		    sql+= "       fec_operacion,id_divisa,m.fec_alta,importe,importe_original,tipo_cambio,id_caja,";		    
		    sql+= "       id_divisa_original,referencia,beneficiario,concepto,id_banco_benef,";		    
		    sql+= "       id_chequera_benef,id_estatus_mov,b_salvo_buen_cobro, agrupa1,agrupa2,";
		    sql+= "       id_leyenda,id_chequera,observacion,autoriza,solicita,origen_mov,plaza,sucursal,";
		    sql+= "       p.equivale_persona , lote_entrada, clabe";
		    sql+= "  FROM movimiento m, persona p";
		    
		    if(gsDBM.equals("SYBASE"))
		    	sql+=" 	WHERE (convert(int, m.no_cliente) = p.no_persona or convert(int, m.no_cliente) = p.equivale_persona)";
	        else
	        	sql+=" 	WHERE (m.no_cliente = p.no_persona or m.no_cliente = p.equivale_persona)";
		    
		    sql+="	AND p.id_tipo_persona = 'P'";
		    sql+="	AND no_docto = '" + Utilerias.validarCadenaSQL(String.valueOf(datos.get("noDocto"))) + "'";
		    sql+="	AND id_estatus_mov in ('C','N')";
		    sql+="	AND m.no_empresa = " + datos.get("idEmpresa");
		    
		    if(datos.get("psEquivale")!=null && !datos.get("psEquivale").equals("") && !datos.get("psEquivale").equals("0"))
		    	sql+="   AND p.equivale_persona = '"+datos.get("psEquivale")+"'";
		    
		    bitacora.insertarRegistro("consultar movimientos: "+sql);
		    listDatos= jdbcTemplate.query(sql, new RowMapper<MovimientoDto>(){
				public MovimientoDto mapRow(ResultSet rs, int idx) throws SQLException {
					MovimientoDto cons = new MovimientoDto();
					cons.setFolioRef(rs.getInt("folio_ref"));
					cons.setNoCliente(rs.getString("no_cliente"));
					cons.setIdTipoDocto(rs.getInt("id_tipo_docto"));
					cons.setIdFormaPago(rs.getInt("id_forma_pago"));
					cons.setNoFolioDet(rs.getInt("no_folio_det"));
					cons.setIdTipoOperacion(rs.getInt("id_tipo_operacion"));
					cons.setIdCveOperacion(rs.getInt("id_cve_operacion"));
					cons.setNoCuenta(rs.getInt("no_cuenta"));
					cons.setNoDocto(rs.getString("no_docto"));
					cons.setFecValor(rs.getDate("fec_valor"));
					cons.setFecValorOriginal(rs.getDate("fec_valor_original"));
					cons.setFecOperacion(rs.getDate("fec_operacion"));
					cons.setIdDivisa(rs.getString("id_divisa"));
					cons.setFecAlta(rs.getDate("fec_alta"));
					cons.setImporte(rs.getDouble("importe"));
					cons.setImporteOriginal(rs.getDouble("importe_original"));
					cons.setTipoCambio(rs.getDouble("tipo_cambio"));
					cons.setIdCaja(rs.getInt("id_caja"));
					cons.setIdDivisaOriginal(rs.getString("id_divisa_original"));
					cons.setReferencia(rs.getString("referencia"));
					cons.setBeneficiario(rs.getString("beneficiario"));
					cons.setConcepto(rs.getString("concepto"));
					cons.setIdBancoBenef(rs.getInt("id_banco_benef"));
					cons.setIdChequeraBenef(rs.getString("id_chequera_benef"));
					cons.setIdEstatusMov(rs.getString("id_estatus_mov"));
					cons.setBSalvoBuenCobro(rs.getString("b_salvo_buen_cobro"));
					cons.setAgrupa1(rs.getString("agrupa1"));
					cons.setAgrupa2(rs.getString("agrupa2"));
					cons.setIdLeyenda(rs.getString("id_leyenda"));
					cons.setIdChequera(rs.getString("id_chequera"));
					cons.setObservacion(rs.getString("observacion"));
					cons.setAutoriza(rs.getString("autoriza"));
					cons.setSolicita(rs.getString("solicita"));
					cons.setOrigenMov(rs.getString("origen_mov"));
					cons.setPlaza(rs.getInt("plaza"));
					cons.setSucursal(rs.getInt("sucursal"));
					cons.setClabe(rs.getString("clabe"));
					
					return cons;
				}});
		    
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Egresos, C:CapturaSolicitudesPagoDao, M:consultarMovimientos");	
		}
		return listDatos;
	}
	
	public Map<String,Object> registrarPlantilla(Map<String, String> datIn){
		Map<String, Object> res= new HashMap<String, Object>();
		int noPlantilla;
		System.out.println("registrar plantilla dao");
		try {
			StringBuffer sqlConsul = new StringBuffer();
			sqlConsul.append("SELECT COUNT(*) FROM PLANTILLA_SOLICITUD_PAGO WHERE NOMBRE_PLANTILLA = ");
			sqlConsul.append("'");
			sqlConsul.append(Utilerias.validarCadenaSQL(String.valueOf(datIn.get("nomPlantilla"))));
			sqlConsul.append("'");
			sqlConsul.append(" AND ID_USUARIO = ");
			sqlConsul.append(datIn.get("idUsuario"));
		
			if (jdbcTemplate.queryForInt(sqlConsul.toString())!=0) {
				res.put("msgUsuario", "Ya existe una poliza registrada con ese nombre.");
			} else {
				String banco = "NO";
				String polizaContable = "NO";
				String leyenda = "NO";
				if (datIn.get("tranferenciaBancoNac").equals("true")) 
					banco="SI";
				if (datIn.get("polizaContable").equals("true")) 
					polizaContable = "SI";
				if(datIn.get("leyenda").equals("true"))
					leyenda="SI";
				StringBuffer sqlPlantilla = new StringBuffer();
				StringBuffer sqlInsert = new StringBuffer();
				sqlPlantilla.append("SELECT COUNT(*) FROM PLANTILLA_SOLICITUD_PAGO WHERE ID_USUARIO = ");
				sqlPlantilla.append(datIn.get("idUsuario"));
				System.out.println("\n sql plantiila"+sqlPlantilla);
				
				noPlantilla = (jdbcTemplate.queryForInt(sqlPlantilla.toString()));
				System.out.println("no plantilla"+noPlantilla);
				sqlInsert.append("Insert Into Plantilla_Solicitud_Pago Values( ");
				sqlInsert.append(datIn.get("idEmpresa")+", "+datIn.get("tipoPoliza")+", "+datIn.get("idGrupo")+", ");
				sqlInsert.append(datIn.get("idRubro")+", ");
				sqlInsert.append(datIn.get("idBeneficiario")==null ||  datIn.get("idBeneficiario").equals("") ? "0":datIn.get("idBeneficiario") );
				sqlInsert.append(", '"+Utilerias.validarCadenaSQL(String.valueOf(datIn.get("nomPlantilla")))+"', ");
				sqlInsert.append(datIn.get("idFormaPago")+", getDate() , ");
				sqlInsert.append(datIn.get("importeOriginal") == null || datIn.get("importeOriginal").equals("")? "0": datIn.get("importeOriginal"));
				sqlInsert.append(", '");
				sqlInsert.append(Utilerias.validarCadenaSQL(String.valueOf(datIn.get("idDivOriginal")))+"', '"+Utilerias.validarCadenaSQL(String.valueOf(polizaContable))+"', '"+Utilerias.validarCadenaSQL(String.valueOf(banco))+"', '");
				sqlInsert.append(datIn.get("bancoBeneficiario") == null || datIn.get("bancoBeneficiario").equals("") ? "0": Utilerias.validarCadenaSQL(String.valueOf(datIn.get("bancoBeneficiario"))));
				sqlInsert.append("', '");
				sqlInsert.append(Utilerias.validarCadenaSQL(String.valueOf(datIn.get("chequeraBeneficiario")))+"', '"+Utilerias.validarCadenaSQL(String.valueOf(datIn.get("clabe")))+"', '");
				
				
				
				sqlInsert.append(datIn.get("sucursal") == null || datIn.get("sucursal").equals("") ? "0":"0");
				sqlInsert.append("', '");
				sqlInsert.append(datIn.get("plaza") == null || datIn.get("plaza").equals("")? "0":"0");
				sqlInsert.append("', '");
				
				sqlInsert.append(leyenda);
				sqlInsert.append("', '");
				sqlInsert.append(Utilerias.validarCadenaSQL(String.valueOf(datIn.get("cajaCheque")))+"', '"+Utilerias.validarCadenaSQL(String.valueOf(datIn.get("concepto")))+"', '"+Utilerias.validarCadenaSQL(String.valueOf(datIn.get("descripcion")))+"', '");
				sqlInsert.append(Utilerias.validarCadenaSQL(String.valueOf(datIn.get("asignacion")))+"', '"+Utilerias.validarCadenaSQL(String.valueOf(datIn.get("cabecera")))+"', '"+Utilerias.validarCadenaSQL(String.valueOf(datIn.get("referencia")))+"', '");
				sqlInsert.append(Utilerias.validarCadenaSQL(String.valueOf(datIn.get("solicita")))+"', '"+Utilerias.validarCadenaSQL(String.valueOf(datIn.get("centroCostos")))+"','"+Utilerias.validarCadenaSQL(String.valueOf(datIn.get("division")))+"', '");
				sqlInsert.append(Utilerias.validarCadenaSQL(String.valueOf(datIn.get("orden")))+"', '"+Utilerias.validarCadenaSQL(String.valueOf(datIn.get("autoriza")))+"', convert(datetime,'"+Utilerias.validarCadenaSQL(String.valueOf(datIn.get("fechaPago")))+"',103), '");
				sqlInsert.append(Utilerias.validarCadenaSQL(String.valueOf(datIn.get("observaciones")))+"', ' ', '");
				sqlInsert.append(datIn.get("importePago") == null || datIn.get("importePago").equals("")? "0": Utilerias.validarCadenaSQL(String.valueOf(datIn.get("importePago"))));
				sqlInsert.append("', '");
				sqlInsert.append(Utilerias.validarCadenaSQL(String.valueOf(datIn.get("tipoCambio")))+"', '"+Utilerias.validarCadenaSQL(String.valueOf(datIn.get("idDivPago")))+"', '"+Utilerias.validarCadenaSQL(String.valueOf(datIn.get("areaDestino")))+"', ");
				sqlInsert.append(noPlantilla+","+datIn.get("idUsuario"));
				sqlInsert.append(")");
				System.out.println("insert nu "+sqlInsert);
				//zinfact referencia 16 varchar
				if(jdbcTemplate.update(sqlInsert.toString())!=0)
					res.put("msgUsuario", "La plantilla se guardo exitosamente.");
				else
					res.put("msgUsuario", "Error al insertar la plantilla.");
			} 
			
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:CapturaSolicitudesPagoDao, M:registrarPlantilla");
		}
		return res;
	}
	
	public List<Map<String,Object>> obtenerDetalleMovimiento(int noFolioDet){
		String sql="";
		List<Map<String,Object>> listDatos= new ArrayList<Map<String,Object>>();
		try{
			sql+="\n	SELECT m.invoice_type, e.id_rubro, m.importe, e.desc_rubro,  m.division, e.id_grupo, m.id_area, m.solicita, m.observacion, m.autoriza";
			sql+="\n	FROM movimiento m, cat_rubro e";	
			sql+="\n	WHERE m.id_rubro = e.id_rubro"; 
			sql+="\n	AND m.id_grupo = e.id_grupo";
			sql+="\n	AND folio_ref =" + noFolioDet;
					
		 listDatos= jdbcTemplate.query(sql, new RowMapper<Map<String, Object>>(){
				public Map<String, Object> mapRow(ResultSet rs, int idx) throws SQLException {
					HashMap<String, Object> cons = new HashMap<String, Object>();
					cons.put("noPartida",rs.getInt("invoice_type"));
					cons.put("idRubro", rs.getInt("id_rubro"));
					cons.put("importe", rs.getInt("importe"));
					cons.put("descRubro", rs.getString("desc_rubro"));
					cons.put("division", rs.getString("division"));
					cons.put("idGrupo", rs.getInt("id_grupo"));
					cons.put("idArea",rs.getInt("id_area"));
					cons.put("solicita",rs.getString("solicita"));
					cons.put("observacion",rs.getString("observacion"));
					cons.put("autoriza",rs.getString("autoriza"));
					return cons;
				}});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:CapturaSolicitudesPagoDao, M:obtenerDetalleMovimiento");
			e.printStackTrace();
		}
		return listDatos;
	}
	
	/*FunSQLSelect933*/
	
	public int obtenerPersona(String equivalePersona){
		String sql="";
		List<Integer> listDatos=new ArrayList<Integer>();
		try{
			sql+="\n	SELECT no_persona";
		    sql+="\n	FROM persona";
		    sql+="\n	WHERE id_tipo_persona = 'P'";
		    sql+="\n	AND equivale_persona = '"+Utilerias.validarCadenaSQL(equivalePersona)+"'";
		    listDatos= jdbcTemplate.query(sql, new RowMapper<Integer>(){
				public Integer mapRow(ResultSet rs, int idx) throws SQLException {
					return rs.getInt("no_persona");
				}});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:CapturaSolicitudesPagoDao, M:obtenerPersona");
		}
		return listDatos.isEmpty()?0:listDatos.get(0);
	}
	
	/*Public Function FunSQLSelect921(ByVal pvvValor1 As Variant, ByVal pvvValor2 As Variant, Optional plBeneficiario As Long = 0) As ADODB.Recordset*/
	public boolean verificarDocumento(int noDocto, int empresa){
		StringBuffer sb = new StringBuffer();
		int res=0;
		try{
			sb.append("	SELECT no_docto from movimiento ");
			sb.append("\n	WHERE no_docto = '"+Utilerias.validarCadenaSQL(noDocto)+"'");
			sb.append("\n	AND id_tipo_operacion = 3000 ");
			sb.append("\n	AND no_empresa = "+empresa);
			sb.append("\n	AND id_estatus_mov <> 'B'");
			res=jdbcTemplate.queryForInt(sb.toString());
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:CapturaSolicitudesPagoDao, M:verificarDocumento");
		}
		if(res>0)
			return true;
		else
			return false;
	}
	
	public int obtenerNoCuenta(int empresa){
		
		int result = 0;
		try {
			consultasGenerales= new ConsultasGenerales(jdbcTemplate);
			result = consultasGenerales.obtenerCuenta(empresa);
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:CapturaSolicitudesPagoDao, M:obtenerNoCuenta");
		}
		return result;
	}
	
	public int insertarParametro(ParametroDto dto)
	{
		    int res=0;
	        String sql = "";
	   try{
	        sql+= "\n	INSERT INTO parametro(no_empresa,no_docto,no_folio_param,id_tipo_docto,id_forma_pago, ";
	        sql+= "\n	usuario_alta,id_tipo_operacion,no_cuenta,no_factura,";
	        sql+= "\n	fec_valor,fec_valor_original,fec_operacion,id_divisa, ";
	        sql+= "\n	fec_alta,importe,importe_original,tipo_cambio, ";
	        sql+= "\n	id_caja,id_divisa_original,";
	        
	        if(dto.getReferencia()!=null && !dto.getReferencia().equals(""))
	        {
	        	sql+= "referencia,";
	        }
	        sql+= "\n	beneficiario,concepto,id_banco_benef,id_chequera_benef,aplica,";
	        sql+= "\n	id_estatus_mov,b_salvo_buen_cobro,id_estatus_reg,id_leyenda";
	        
	        if(dto.getFolioRef()>0)
	        	sql+="\n	,folio_ref";
	        if(dto.getIdChequera()!=null && !dto.getIdChequera().equals(""))
	        	sql+="\n	,id_chequera";
	        
	        
	        sql+= "\n	,observacion,origen_mov,no_cliente,";
	        sql+= "\n	solicita, autoriza, plaza, sucursal,";
	        sql+= "\n	grupo, no_folio_mov, id_rubro,agrupa1, agrupa2,agrupa3";

			if(dto.isAdu())
		        sql+=" ,no_pedido ";
		    
			System.out.println("area al insertar" + dto.getIdArea());
			
		    if(dto.getIdArea()>0)
		        sql+=" ,id_area ";
		    
		    if(dto.getDivision()!=null && !dto.getDivision().equals(""))
		        sql+=" ,division ";
		    
		    
    
		    if(dto.isBNomina() && dto.getLote()!=0)
		        sql+=" ,lote ";
		    else if((dto.isBProvAc() && dto.getLote()==0) || (dto.isBProvAc() && dto.getLote()==1))
		    	sql+=" ,lote ";
		    else if (dto.getLote()!=0) 
		    	sql+=" ,lote ";
		    
		
		    if(!dto.getIdGrupo().equals("") && !dto.getIdGrupo().equals("0"))
		        sql+=" ,id_grupo ";
		    if(dto.getPartida()!= null && !dto.getPartida().equals(""))
		        sql+=" ,invoice_type";
		    
		    if(  dto.getIdBanco() != 0 )
		    {
		    	
		    	sql+=" ,id_banco ";
		    }
		    
		    
		    sql+=")";
	                  
	        sql+= "VALUES("+dto.getNoEmpresa()+",'"+Utilerias.validarCadenaSQL(dto.getNoDocto())+"',"+Utilerias.validarCadenaSQL(dto.getNoFolioParam());
	        sql+= ","+dto.getIdTipoDocto()+","+dto.getIdFormaPago()+","+dto.getUsuarioAlta();
	        sql+= ","+dto.getIdTipoOperacion()+",'"+Utilerias.validarCadenaSQL(dto.getNoCuenta())+"'"+",'"+Utilerias.validarCadenaSQL(dto.getNoFactura())+"'";
	        sql+= ",convert(datetime,'" +Utilerias.validarCadenaSQL(funciones.ponerFechaSola(dto.getFecValor()))+"', 103), convert(datetime,'"+Utilerias.validarCadenaSQL(funciones.ponerFechaSola(dto.getFecValorOriginal()))+"', 103)";
	        sql+= ",convert(datetime,'"+Utilerias.validarCadenaSQL(funciones.ponerFechaSola(dto.getFecOperacion()))+"', 103),'"+Utilerias.validarCadenaSQL(dto.getIdDivisa())+"', convert(datetime,'"+Utilerias.validarCadenaSQL(funciones.ponerFechaSola(dto.getFecAlta()))+"', 103),"+dto.getImporte()+",";
	        sql+= " "+dto.getImporteOriginal()+","+dto.getTipoCambio()+","+dto.getIdCaja()+",'"+Utilerias.validarCadenaSQL(dto.getIdDivisaOriginal())+"',";
	        if(dto.getReferencia()!=null && !dto.getReferencia().equals(""))
	        {
	        	sql+= "'"+Utilerias.validarCadenaSQL(dto.getReferencia())+"',";
	        }
	        sql+= "'"+Utilerias.validarCadenaSQL(dto.getBeneficiario())+"','"+Utilerias.validarCadenaSQL(dto.getConcepto())+"',"+dto.getIdBancoBenef()+",";
	        sql+= "'"+Utilerias.validarCadenaSQL(dto.getIdChequeraBenef())+"',"+dto.getAplica()+",'"+Utilerias.validarCadenaSQL(dto.getIdEstatusMov())+"','"+Utilerias.validarCadenaSQL(dto.getBSalvoBuenCobro())+"','"+Utilerias.validarCadenaSQL(dto.getIdEstatusReg())+"','"+Utilerias.validarCadenaSQL(dto.getIdLeyenda())+"'";
	        
	        
	        if(dto.getFolioRef()>0)
	        	sql+=","+dto.getFolioRef();
	        if(dto.getIdChequera()!=null && !dto.getIdChequera().equals(""))
	        	sql+=",'"+Utilerias.validarCadenaSQL(dto.getIdChequera())+"'";
	        
	        sql+= ",'"+dto.getObservacion()+"'";
	        if(dto.isAdu())
	             sql+=",'ADU'";
	        else if (dto.isBCvDivisa())
	         	 sql+=",'CVD'";
	        else
	        	 sql+=",'SET'";

	        sql+= ",'"+Utilerias.validarCadenaSQL(dto.getNoCliente())+"'";
	        sql+= ",'"+Utilerias.validarCadenaSQL(dto.getSolicita())+"','"+Utilerias.validarCadenaSQL(dto.getAutoriza())+"',"+dto.getPlaza()+","+dto.getSucursal();
	        sql+= ","+dto.getGrupo()+","+dto.getNoFolioMov()+","+dto.getIdRubroInt()+","+dto.getAgrupa1()+","+dto.getAgrupa2()+","+dto.getAgrupa3();
	        
	        if(dto.isAdu())
		        sql+=" ,"+dto.getNoPedido();
		    
		    if(dto.getIdArea()>0)
		        sql+=" ,"+dto.getIdArea();
		    
		    if(dto.getDivision()!=null && !dto.getDivision().equals(""))
		        sql+=" ,'"+Utilerias.validarCadenaSQL(dto.getDivision())+"'";
    
    
		    if(dto.isBNomina() && dto.getLote()!=0)
		        sql+=" ,"+dto.getLote();
		    else if((dto.isBProvAc() && dto.getLote()==0) || (dto.isBProvAc() && dto.getLote()==1))
		    	sql+=" ,"+dto.getLote();
		    else if (dto.getLote()!=0) 
		    	sql+=" ,"+dto.getLote();
		    if(!dto.getIdGrupo().equals(""))
		        sql+=" ,"+dto.getIdGrupo();
		    if(dto.getPartida()!= null && !dto.getPartida().equals(""))
		        sql+=" ,'"+Utilerias.validarCadenaSQL(dto.getPartida())+"'";
		    
		    if(  dto.getIdBanco() != 0 )
		    {
		    	
		    	sql+=" ," + dto.getIdBanco();
		    }
		    sql+=")";
		    System.out.println("inserta parametro "+sql);
	       //System.out.println("inicia-------------------------------------------------");
	      // System.out.println("PAEE"+sql);
		   //System.out.println("termina-------------------------------------------------");
	       res=jdbcTemplate.update(sql);
	        
	       
	       
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:ImportaBancaElectronicaDao, M:insertarParametro");
			e.printStackTrace();
		}
	        
	        return res;
	}
 /*Public Function FunSQLSelect925(ByVal pvvValor1 As Variant) As ADODB.Recordset*/
	public String obtenerChequeraPagadora(String idDivisa){
		String sql="";
//		String idChequera="";
		List<String> listCheq=new ArrayList<String>();
		try{
		    sql+= "SELECT id_chequera ";
		    sql+="  FROM chequera_custodia ";
		    sql+= " WHERE id_tipo_docto = 47 ";
		    sql+= "   AND id_divisa = '" +Utilerias.validarCadenaSQL(idDivisa)+ "'";
		    listCheq= jdbcTemplate.query(sql, new RowMapper<String>(){
				public String mapRow(ResultSet rs, int idx) throws SQLException {
					return rs.getString("id_chequera");
				}});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:ImportaBancaElectronicaDao, M:obtenerChequeraPagadora");
		}
		return listCheq.get(0);
	}
	
/**
 * Public Function FunSQLUpdateClabe(ByVal Folio As String, ByVal psClabe As String) As Long
 * @param folio
 * @param clabe
 * @return
 */
	public int actualizarClabe(String folio, String clabe){
		int folioMov=0, res=0;
		try{
			StringBuffer sql = new StringBuffer();
			sql.append("select no_folio_mov from movimiento where no_folio_det = '" + Utilerias.validarCadenaSQL(folio) + "' ");
			folioMov = jdbcTemplate.queryForInt(sql.toString());
			if(folioMov>0)
			{
				sql = new StringBuffer();
				sql.append(" UPDATE movimiento ");
				sql.append("\n   SET clabe = '" + Utilerias.validarCadenaSQL(clabe) + "'");
				sql.append("\n	, folio_ref =" + folio);//Se hace update a folio_ref, por que en generador no se ponia con mas de 2 detalles
				sql.append("\n	 WHERE no_folio_mov = '" +Utilerias.validarCadenaSQL(folioMov) + "'");
				res=jdbcTemplate.update(sql.toString());
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Egresos, C:ImportaBancaElectronicaDao, M:actualizarClabe");
		}
		return res;
	}
	
	public String obtenerSolicitante() {
		String nombreC = "";
		StringBuffer sql = new StringBuffer();
		globalSingleton = GlobalSingleton.getInstancia();
		
		List<String> listNombreC = new ArrayList<String>();
		
		try {
		    sql.append(" SELECT nombre + ' ' + apellido_paterno + ' ' + apellido_materno as nombreC");
		    sql.append(" FROM seg_usuario");
		    sql.append(" WHERE id_usuario = " + globalSingleton.getUsuarioLoginDto().getIdUsuario() + "");
		    
		    listNombreC = jdbcTemplate.query(sql.toString(), new RowMapper<String>(){
				public String mapRow(ResultSet rs, int idx) throws SQLException {
					return rs.getString("nombreC");
				}
			});
		    if(listNombreC.size() > 0) nombreC = listNombreC.get(0);
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Egresos, C:ImportaBancaElectronicaDao, M:obtenerSolicitante");
		}
		return nombreC;
	}
	
	
	/**
	 * Este metodo es para hacer el enlace con el bean de conexion
	 * en el aplicationContext
	 * @param dataSource
	 */
	public void setDataSource(DataSource dataSource){
		try{
			this.jdbcTemplate = new JdbcTemplate(dataSource);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:CapturaSolicitudesPagoDao, M:setDataSource");
		}
	}

	@Override
	public int actualizaPagoCompleto(String strNoFolioDet,String strImporteParcial, String strDescripcion) {
		int res = 0;
		try{
			StringBuffer sql = new StringBuffer();
			
			sql.append(" UPDATE movimiento ");
			sql.append("\n   SET importe = (importe_original - " + strImporteParcial + ")");
			sql.append("\n	,observacion = '" + Utilerias.validarCadenaSQL(strDescripcion )+ "'");
			sql.append("\n	 WHERE no_folio_det = " + strNoFolioDet + "");
			
			res =jdbcTemplate.update(sql.toString());
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Egresos, C:CapturaSolicitudesPago, M:actualizaPagoCompleto");
		}
		
		return res;
	}

	@Override
	public List<String> obtenerGrupoDelRubro(String idRubro) {
		List<List<String>> grupo = new ArrayList<List<String>>();
		try{
			StringBuffer sql = new StringBuffer();			
			sql.append(" select r.Id_Grupo, g.desc_grupo from cat_rubro r join Cat_Grupo g on r.id_grupo = g.id_grupo  where Id_Rubro = ");
			sql.append(idRubro);
			System.out.println("nueva consult "+ sql);
			grupo = jdbcTemplate.query(sql.toString(), new RowMapper<List<String>>(){
				public List<String> mapRow(ResultSet rs, int idx) throws SQLException {
					List<String> grupo = new ArrayList<String>();
					grupo.add(rs.getString("Id_Grupo"));
					grupo.add(rs.getString("desc_grupo"));
					return grupo;
				}
			});
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Egresos, C:CapturaSolicitudesPago, M:obtenerGrupoDelRubro");
		}
		return grupo != null && !grupo.isEmpty() ?grupo.get(0) : new ArrayList<String>();
		
	}

	@Override
	public List<String> obtieneTransacionClasedocumento(String poliza,  String Grupo, String rubro) {
		StringBuffer sql = new StringBuffer();
		List<List<String>> claseDocTransaccion = new ArrayList<List<String>>();
		try {
			sql.append("Select Transaccion, Clase_Doc From Configura_Solicitud_Pago Where Id_Poliza = ");
			sql.append(poliza);
			sql.append("  And Id_Grupo = ");
			sql.append(Grupo);
			sql.append(" And Id_Rubro = ");
			sql.append(rubro);
			System.out.println("\n tansaccion clase doc--"+ sql);
			claseDocTransaccion = jdbcTemplate.query(sql.toString(), new RowMapper<List<String>>(){
				public List<String> mapRow(ResultSet rs, int idx) throws SQLException {
					List<String> complemento = new ArrayList<String>();
					complemento.add(rs.getString("Transaccion"));
					complemento.add(rs.getString("Clase_Doc"));
					return complemento;
				}
			});
			
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:CapturaSolicitudesPago, M:obtieneTransacionClasedocumento");
		}
		return claseDocTransaccion != null && !claseDocTransaccion.isEmpty() ?claseDocTransaccion.get(0) : new ArrayList<String>();
	}

	@Override
	public List<PolizaContableDto> llenarComboPoliza(PolizaContableDto dto) {
			String sql="";
			List<PolizaContableDto> listDatos= new ArrayList<PolizaContableDto>();
			try{
				sql+= "	SELECT	";
				if(dto.getCampoUno()!=null && !dto.getCampoUno().trim().equals(""))
					sql+=""+dto.getCampoUno()+"	as ID";
				if(dto.getCampoDos()!=null && !dto.getCampoDos().trim().equals(""));
					sql+=",	"+dto.getCampoDos()+"	as DESCRIPCION";
			    sql+= "  FROM	";
			    if(dto.getTabla()!=null && !dto.getTabla().trim().equals(""))
			    	sql+=""+dto.getTabla();
			    if(dto.getCondicion()!=null && !dto.getCondicion().trim().equals(""))
			    	sql+="	WHERE	 ID_POLIZA IN (SELECT ID_POLIZA FROM USUARIO_POLIZA WHERE ID_USUARIO = "+dto.getCondicion()+")";
			    if(dto.getOrden()!=null && !dto.getOrden().trim().equals(""))
			    	sql+="	ORDER BY	"+dto.getOrden();
			 
			    System.out.print("llena combo polizas: " + sql);
			    
			    listDatos= jdbcTemplate.query(sql, new RowMapper<PolizaContableDto>(){
					public PolizaContableDto mapRow(ResultSet rs, int idx) throws SQLException {
						PolizaContableDto cons = new PolizaContableDto();
						cons.setIdStr(rs.getString("ID"));
						cons.setDescripcion(rs.getString("DESCRIPCION"));
						return cons;
					}});
			    System.out.println(listDatos);
			}catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
				+ "P:Utilerias, C:ConsultasGenerales, M:llenarComboGral");
			}
			return listDatos;
	
	}
	
	public Map<String, Object> obtieneCheqPagadora(GeneradorDto generadorDto){
		consultasGenerales= new ConsultasGenerales(jdbcTemplate);
		
		return consultasGenerales.obtieneCheqPagadora(generadorDto);
		
	}
	public List<LlenaComboGralDto>obtenerGrupo(){
		consultasGenerales = new ConsultasGenerales(jdbcTemplate);
		return consultasGenerales.obtenerGrupo();
	}
	public List<LlenaComboGralDto>obtenerRubro(LlenaComboGralDto dto)
	{
		consultasGenerales = new ConsultasGenerales(jdbcTemplate);
		//return consultasGenerales.llenarComboGral(dto);
		return consultasGenerales.obtenerRubro(dto);
	}

	

	

	
	
}
