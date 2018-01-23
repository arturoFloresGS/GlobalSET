package com.webset.set.egresos.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Luis Alfredo Serrato Montes de Oca
 * 20/01/2016
 **/

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.webset.set.egresos.dao.AplicacionDescuentosPropuestasDao;
import com.webset.set.egresos.dto.PagosPendientesDto;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.ConsultasGenerales;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.utils.tools.Utilerias;

public class AplicacionDescuentosPropuestasDaoImpl implements AplicacionDescuentosPropuestasDao {
	private JdbcTemplate jdbcTemplate;
	Bitacora bitacora = new Bitacora();
	ConsultasGenerales consultasGenerales;
	
	
	@Override
	public List<LlenaComboGralDto> llenarComboGrpEmpresas(String idUsuario) {
		List<LlenaComboGralDto> listaGrupos = new ArrayList<LlenaComboGralDto>();
		StringBuffer sb = new StringBuffer();
		
		sb.append("SELECT DISTINCT cg.ID_GRUPO_FLUJO, DESC_GRUPO_FLUJO FROM \n");
		sb.append("CAT_GRUPO_FLUJO cg JOIN GRUPO_EMPRESA ge ON cg.ID_GRUPO_FLUJO = ge.ID_GRUPO_FLUJO \n");
		sb.append("JOIN EMPRESA e on ge.NO_EMPRESA = e.NO_EMPRESA JOIN USUARIO_EMPRESA ue \n");
		sb.append("ON e.NO_EMPRESA = ue.NO_EMPRESA WHERE ue.NO_USUARIO = '"+Utilerias.validarCadenaSQL(idUsuario)+"' \n");
		
		System.out.println(sb.toString());
		try {
			
			listaGrupos = jdbcTemplate.query(sb.toString(), new RowMapper<LlenaComboGralDto>(){
				public LlenaComboGralDto mapRow(ResultSet rs, int idx) throws SQLException{
					LlenaComboGralDto grupoEmpresa = new LlenaComboGralDto();
						grupoEmpresa.setIdStr(rs.getString("ID_GRUPO_FLUJO"));
						grupoEmpresa.setDescripcion(rs.getString("DESC_GRUPO_FLUJO"));
					return grupoEmpresa;
				}});
			
		}catch(CannotGetJdbcConnectionException e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:AplicacionDescuentosPropuestasDaoImpl, M:obtenerListaFactoraje");
		
		}catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:AplicacionDescuentosPropuestasDaoImpl, M:obtenerListaFactoraje");
		}
		
		return listaGrupos;
	}
	
	
	@Override
	public List<PagosPendientesDto> obtenerPagos(List<Map<String, String>> datos){
		List<PagosPendientesDto> listaPagos = new ArrayList<PagosPendientesDto>();
		StringBuffer sb = new StringBuffer();
		
		sb.append("SELECT DISTINCT sa.CVE_CONTROL, DESC_GRUPO_FLUJO ,sa.CONCEPTO, sa.MONTO_MAXIMO, sa.FECHA_PROPUESTA \n");
		sb.append("FROM SELECCION_AUTOMATICA_GRUPO sa JOIN CAT_GRUPO_FLUJO cg ON sa.ID_GRUPO_FLUJO = cg.ID_GRUPO_FLUJO \n");
		sb.append("JOIN MOVIMIENTO m ON sa.CVE_CONTROL = m.CVE_CONTROL \n");
		sb.append("JOIN PERSONA p ON m.NO_CLIENTE = P.NO_PERSONA \n");
		sb.append("WHERE (USUARIO_UNO IS NULL OR USUARIO_UNO = '') AND (USUARIO_DOS IS NULL OR USUARIO_DOS = '') \n");
		sb.append("AND (USUARIO_TRES IS NULL OR USUARIO_TRES = '') AND (sa.CONCEPTO  LIKE 'C-%') \n");
		sb.append("AND m.ID_TIPO_OPERACION = '3000' \n");
		sb.append("AND m.ID_ESTATUS_MOV IN('C','N') \n");
		if(!datos.get(0).get("divisa").equals("")){
			sb.append("AND ID_DIVISA = '"+Utilerias.validarCadenaSQL(datos.get(0).get("divisa"))+"' \n");
		}if(!datos.get(0).get("ePersona").equals("")){
			sb.append("AND p.EQUIVALE_PERSONA = '"+Utilerias.validarCadenaSQL(datos.get(0).get("ePersona"))+"' \n");
		}if(!datos.get(0).get("fIni").equals("") && !datos.get(0).get("fFin").equals("")){
			sb.append("AND sa.FECHA_PROPUESTA BETWEEN convert(datetime, '"+Utilerias.validarCadenaSQL(datos.get(0).get("fIni"))+"', 103) AND convert(datetime,'"+datos.get(0).get("fFin")+"', 103) \n");
		}if(!datos.get(0).get("empresa").equals("")){
			sb.append("AND sa.ID_GRUPO_FLUJO = '"+Utilerias.validarCadenaSQL(datos.get(0).get("empresa"))+"' \n");
		}
		
		System.out.println(sb.toString());
		
		try {
			listaPagos = jdbcTemplate.query(sb.toString(), new RowMapper<PagosPendientesDto>(){
				public PagosPendientesDto mapRow(ResultSet rs, int idx) throws SQLException{
					PagosPendientesDto pago = new PagosPendientesDto();
						pago.setCveControl(rs.getString("CVE_CONTROL"));
						pago.setBeneficiario(rs.getString("DESC_GRUPO_FLUJO"));
						pago.setTexto(rs.getString("CONCEPTO"));
						pago.setTotal(rs.getDouble("MONTO_MAXIMO"));
						pago.setFechaPropuesta(rs.getString("FECHA_PROPUESTA"));
					return pago;
				}
				
			});
		}catch(CannotGetJdbcConnectionException e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:AplicacionDescuentosPropuestasDaoImpl, M:obtenerPagos");
		
		}catch (Exception e) { 
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:AplicacionDescuentosPropuestasDaoImpl, M:obtenerPagos");
		}
		
		return listaPagos;
		
	}
	
	
	@Override
	public List<PagosPendientesDto> obtenerDetallePagos(String cveControl, String cveControlD){
		List<PagosPendientesDto> listaDetalles = new ArrayList<PagosPendientesDto>();
		StringBuffer sb = new StringBuffer();
		System.out.println("Llego DAO "+cveControlD);
		sb.append("SELECT EQUIVALE_PERSONA, RAZON_SOCIAL, SUM(IMPORTE) as IMPORTE_TOTAL,E.NO_EMPRESA,E.NOM_EMPRESA FROM  \n");
		sb.append("(SELECT * FROM MOVIMIENTO WHERE CVE_CONTROL IN ("+cveControlD+")  \n");
		sb.append("AND NO_CLIENTE IN (SELECT NO_CLIENTE FROM MOVIMIENTO WHERE CVE_CONTROL IN ("+cveControl+") AND ID_TIPO_OPERACION='3000')  \n");
		sb.append("UNION SELECT * FROM MOVIMIENTO WHERE CVE_CONTROL IN ("+cveControl+")  \n");
		sb.append("AND NO_CLIENTE IN (SELECT NO_CLIENTE FROM MOVIMIENTO WHERE CVE_CONTROL IN ("+cveControlD+") AND ID_TIPO_OPERACION='3000')) M \n");
		sb.append("JOIN PERSONA P ON P.NO_PERSONA = M.NO_CLIENTE JOIN EMPRESA E ON M.NO_EMPRESA=E.NO_EMPRESA \n");
		//sb.append("WHERE CVE_CONTROL IN ("+cveControl+",'"+cveControlD+"') \n");
		sb.append("WHERE ID_TIPO_OPERACION = '3000' AND m.ID_ESTATUS_MOV IN('C','N')  GROUP BY EQUIVALE_PERSONA, RAZON_SOCIAL,E.NO_EMPRESA,E.NOM_EMPRESA \n");
		//sb.append("AND ID_TIPO_OPERACION = '3000' GROUP BY NO_EMPRESA \n");
		
		System.out.println(sb.toString());
		try {
		
			listaDetalles = jdbcTemplate.query(sb.toString(), new RowMapper<PagosPendientesDto>(){
				public PagosPendientesDto mapRow(ResultSet rs, int idx) throws SQLException{
					PagosPendientesDto detallePago = new PagosPendientesDto();
					//detallePago.setNoFact(rs.getString("NO_FOLIO_DET"));
					detallePago.setEquivalePersona(rs.getString("EQUIVALE_PERSONA"));
					detallePago.setBeneficiario(rs.getString("RAZON_SOCIAL"));
					detallePago.setImporteNeto(rs.getDouble("IMPORTE_TOTAL"));
					detallePago.setImporteOriginal(rs.getDouble("IMPORTE_TOTAL"));
					detallePago.setNumeroEmpresa(rs.getInt("NO_EMPRESA"));
					detallePago.setNombreEmpresa(rs.getString("NOM_EMPRESA"));
					//detallePago.setTexto(rs.getString("CONCEPTO"));
					//detallePago.setFormaPago(rs.getString("DESC_FORMA_PAGO"));
					//detallePago.setFecValor(rs.getDate("FEC_VALOR"));
					//detallePago.setFechaPropuesta(rs.getString("FECHA_PROPUESTA"));
					//detallePago.setEstatus("P");
					return detallePago;

				}});
					
		}catch(CannotGetJdbcConnectionException e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:AplicacionDescuentosPropuestasDaoImpl, M:obtenerDetallePagos");
			
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:AplicacionDescuentosPropuestasDaoImpl, M:obtenerDetallePagos");
		}
		
		return listaDetalles;
	}
	
	
	@Override
	public List<PagosPendientesDto> obtenerDescuentos(String cveControl,String noEmpresa){
		List<PagosPendientesDto> listaDescuentos = new ArrayList<PagosPendientesDto>();
		StringBuffer sb = new StringBuffer();
		System.out.println("Llego dao "+cveControl+" "+noEmpresa);
		sb.append("SELECT DISTINCT sa.CVE_CONTROL, DESC_GRUPO_FLUJO ,sa.CONCEPTO, sa.MONTO_MAXIMO, sa.FECHA_PROPUESTA  \n");
		sb.append("FROM SELECCION_AUTOMATICA_GRUPO sa JOIN CAT_GRUPO_FLUJO cg ON sa.ID_GRUPO_FLUJO = cg.ID_GRUPO_FLUJO  \n");
		sb.append("JOIN MOVIMIENTO m ON sa.CVE_CONTROL = m.CVE_CONTROL \n");
		sb.append("JOIN PERSONA p ON m.NO_CLIENTE = P.NO_PERSONA \n");
		sb.append("WHERE (USUARIO_UNO IS NULL OR USUARIO_UNO = '') AND (USUARIO_DOS IS NULL OR USUARIO_DOS = '') \n");
		sb.append("AND (USUARIO_TRES IS NULL OR USUARIO_TRES = '') AND (sa.CONCEPTO  LIKE 'D-%') \n");
		//sb.append("AND p.EQUIVALE_PERSONA = '"+ePersona+"' AND ID_TIPO_OPERACION = '3000'\n");
		sb.append("AND sa.ID_GRUPO_FLUJO ='"+noEmpresa+"' \n");
		sb.append("AND NO_CLIENTE IN (SELECT NO_CLIENTE FROM MOVIMIENTO WHERE CVE_CONTROL IN("+cveControl+") AND ID_TIPO_OPERACION = '3000' ) \n");
		//sb.append("AND SUBSTR(sa.CONCEPTO, 3, 27) LIKE (SELECT SUBSTR(CONCEPTO, 3, 27) FROM SELECCION_AUTOMATICA_GRUPO WHERE CVE_CONTROL = '"+Utilerias.validarCadenaSQL(cveControl)+"') \n");
		sb.append("AND ID_TIPO_OPERACION = '3000' \n");
		sb.append("AND m.ID_ESTATUS_MOV IN('C','N') \n");
		System.out.println(sb.toString());
		
		try {
			listaDescuentos = jdbcTemplate.query(sb.toString(), new RowMapper<PagosPendientesDto>(){
				public PagosPendientesDto mapRow(ResultSet rs, int idx) throws SQLException{
					PagosPendientesDto descuento = new PagosPendientesDto();
						descuento.setCveControl(rs.getString("CVE_CONTROL"));
						descuento.setBeneficiario(rs.getString("DESC_GRUPO_FLUJO"));
						descuento.setTexto(rs.getString("CONCEPTO"));
						descuento.setTotal(rs.getDouble("MONTO_MAXIMO"));
						descuento.setFechaPropuesta(rs.getString("FECHA_PROPUESTA"));
					return descuento;
				}
				
			});
		}catch(CannotGetJdbcConnectionException e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:AplicacionDescuentosPropuestasDaoImpl, M:obtenerDescuentos");
		
		}catch (Exception e) { 
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:AplicacionDescuentosPropuestasDaoImpl, M:obtenerDescuentos");
		}
		
		return listaDescuentos;
		
	}
	
	public int actualizarMovimiento(String clavesPagos, String clavePago){
		int res = 0;
		StringBuffer sb = new StringBuffer();
		sb.append("UPDATE MOVIMIENTO SET CVE_CONTROL = "+clavePago+", FEC_PROPUESTA=(SELECT FECHA_PROPUESTA FROM SELECCION_AUTOMATICA_GRUPO WHERE CVE_CONTROL= "+clavePago+") \n");
		sb.append("WHERE CVE_CONTROL IN("+clavesPagos+") \n");
		sb.append("AND ID_TIPO_OPERACION = '3000' \n");
		sb.append("AND ID_ESTATUS_MOV IN('C','N') \n");
		System.out.println(sb.toString());
		try {
			res = jdbcTemplate.update(sb.toString());
		}catch(CannotGetJdbcConnectionException e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:AplicacionDescuentosPropuestasDaoImpl, M:actualizarMovimiento");
		
		}catch (Exception e) { 
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:AplicacionDescuentosPropuestasDaoImpl, M:actualizarMovimiento");
		}

		return res;

	}
	
	public int eliminarSeleccionAut(String clavesPagos){
		int res = 0;
		StringBuffer sb = new StringBuffer();
		sb.append("DELETE FROM SELECCION_AUTOMATICA_GRUPO \n");
		sb.append("WHERE CVE_CONTROL IN("+clavesPagos+") \n");
		System.out.println(sb.toString());
		try {
			res = jdbcTemplate.update(sb.toString());
		}catch(CannotGetJdbcConnectionException e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:AplicacionDescuentosPropuestasDaoImpl, M:eliminarSeleccionAut");
		
		}catch (Exception e) { 
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:AplicacionDescuentosPropuestasDaoImpl, M:eliminarSeleccionAut");
		}

		return res;
	}
	
	@Override
	public int modificarMovimiento(String foliDet, String cvePago, String cveDescuento){
		int res = 0;
		StringBuffer sb = new StringBuffer();
		sb.append("UPDATE MOVIMIENTO SET CVE_CONTROL = "+cvePago+", FEC_PROPUESTA=(SELECT FECHA_PROPUESTA FROM SELECCION_AUTOMATICA_GRUPO WHERE CVE_CONTROL= "+cvePago+") \n");
		sb.append("WHERE NO_FOLIO_DET IN \n");
		sb.append("(SELECT NO_FOLIO_DET FROM MOVIMIENTO WHERE CVE_CONTROL IN ("+cveDescuento+") \n"); 
		sb.append("AND NO_CLIENTE IN(SELECT NO_CLIENTE FROM MOVIMIENTO WHERE CVE_CONTROL= "+cvePago+") \n"); 
		if(foliDet.length()>0){
		sb.append("AND NO_FOLIO_DET NOT IN("+foliDet+") \n");
		}
		sb.append("AND ID_TIPO_OPERACION = '3000')\n");
		sb.append("AND ID_ESTATUS_MOV IN('C','N') \n");
		
		System.out.println(sb.toString());
		try {
			res = jdbcTemplate.update(sb.toString());
		}catch(CannotGetJdbcConnectionException e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:AplicacionDescuentosPropuestasDaoImpl, M:modificarMovimiento");
		
		}catch (Exception e) { 
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:AplicacionDescuentosPropuestasDaoImpl, M:modificarMovimiento");
		}

		return res;
	}
	
	@Override
	public int modificarPopuestaPago(String cveDesc, String cvePago, String folios){
		String mensaje = "";
		int res = 0;
		StringBuffer sb = new StringBuffer();
		
		sb.append("UPDATE SELECCION_AUTOMATICA_GRUPO SET \n");
		sb.append("MONTO_MAXIMO = (SELECT SUM(IMPORTE) FROM MOVIMIENTO WHERE CVE_CONTROL = "+cvePago+" AND ID_TIPO_OPERACION = '3000' AND ID_ESTATUS_MOV IN('C','N')), \n");
		sb.append("CUPO_AUTOMATICO = (SELECT SUM(IMPORTE) FROM MOVIMIENTO WHERE CVE_CONTROL = "+cvePago+" AND ID_TIPO_OPERACION = '3000' AND ID_ESTATUS_MOV IN('C','N')), \n");
		sb.append("CUPO_TOTAL = (SELECT SUM(IMPORTE) FROM MOVIMIENTO WHERE CVE_CONTROL = "+cvePago+" AND ID_TIPO_OPERACION = '3000' AND ID_ESTATUS_MOV IN('C','N')) \n");
		sb.append("WHERE CVE_CONTROL = "+cvePago);
		
		System.out.println(sb.toString());
		try {
			res = jdbcTemplate.update(sb.toString());
			
		}catch(CannotGetJdbcConnectionException e){
			mensaje = "Se perdio la conexion contacte a su DBA";
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:AplicacionDescuentosPropuestasDaoImpl, M:modificarPopuestaPago");
		
		}catch (Exception e) { 
			mensaje = "Error durante el proceso de descuento";
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:AplicacionDescuentosPropuestasDaoImpl, M:modificarPopuestaPago");
		}
		
		return res;
	}
	
	
	public int modificarPropuestaDescuento(String cveDescuento, String folios){
		String mensaje = "";
		int res = 0;
		StringBuffer sb = new StringBuffer();
		System.out.println(cveDescuento.length());
		sb.append("UPDATE SELECCION_AUTOMATICA_GRUPO SET \n");
		sb.append("MONTO_MAXIMO = (SELECT SUM(IMPORTE) FROM MOVIMIENTO WHERE CVE_CONTROL = "+cveDescuento+" AND ID_TIPO_OPERACION = '3000' AND ID_ESTATUS_MOV IN('C','N')), \n");
		sb.append("CUPO_AUTOMATICO = (SELECT SUM(IMPORTE) FROM MOVIMIENTO WHERE CVE_CONTROL = "+cveDescuento+" AND ID_TIPO_OPERACION = '3000' AND ID_ESTATUS_MOV IN('C','N')), \n");
		sb.append("CUPO_TOTAL = (SELECT SUM(IMPORTE) FROM MOVIMIENTO WHERE CVE_CONTROL = "+cveDescuento+" AND ID_TIPO_OPERACION = '3000' AND ID_ESTATUS_MOV IN('C','N')) \n");
		sb.append("WHERE CVE_CONTROL = "+cveDescuento+"");
		System.out.println(sb.toString());
		
		try {
			
				res = jdbcTemplate.update(sb.toString());
			
		}catch(CannotGetJdbcConnectionException e){
			mensaje = "Se perdio la conexion contacte a su DBA";
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:AplicacionDescuentosPropuestasDaoImpl, M:modificarPropuestaDescuento");
			
		}catch (Exception e) { 
			mensaje = "Error durante el proceso de descuento";
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:AplicacionDescuentosPropuestasDaoImpl, M:modificarPropuestaDescuento");
		}
		
		return res; 
	}
	
	@Override
	public List<PagosPendientesDto> obtenerDetalleDesc(String cveControl, String ePersona, String numeroEmpresa){
		List<PagosPendientesDto> listaDetalles = new ArrayList<PagosPendientesDto>();
		StringBuffer sb = new StringBuffer();
		
		sb.append("SELECT m.CVE_CONTROL, NO_FOLIO_DET, p.EQUIVALE_PERSONA, BENEFICIARIO, IMPORTE, m.CONCEPTO, DESC_FORMA_PAGO, FEC_VALOR, FECHA_PROPUESTA  \n");
		sb.append("FROM MOVIMIENTO m JOIN CAT_FORMA_PAGO cf ON m.ID_FORMA_PAGO = cf.ID_FORMA_PAGO JOIN \n");
		sb.append("SELECCION_AUTOMATICA_GRUPO sa ON m.CVE_CONTROL = sa.CVE_CONTROL JOIN PERSONA p ON m.NO_CLIENTE = p.NO_PERSONA WHERE m.CVE_CONTROL IN ("+cveControl+") \n");
		sb.append("AND EQUIVALE_PERSONA = '"+ePersona+"'\n");
		sb.append("AND M.NO_EMPRESA='"+numeroEmpresa+"' \n");
		sb.append("AND ID_TIPO_OPERACION = '3000' \n");
		sb.append("AND ID_ESTATUS_MOV IN('C','N') \n");
		
		
		System.out.println(sb.toString());
		try {
		
			listaDetalles = jdbcTemplate.query(sb.toString(), new RowMapper<PagosPendientesDto>(){
				public PagosPendientesDto mapRow(ResultSet rs, int idx) throws SQLException{
					PagosPendientesDto detallePago = new PagosPendientesDto();
					detallePago.setCveControl(rs.getString("CVE_CONTROL"));
					detallePago.setNoFact(rs.getString("NO_FOLIO_DET"));
					detallePago.setNombreEmpresa(rs.getString("EQUIVALE_PERSONA"));;
					detallePago.setBeneficiario(rs.getString("BENEFICIARIO"));
					detallePago.setImporteNeto(rs.getDouble("IMPORTE"));
					detallePago.setTexto(rs.getString("CONCEPTO"));
					detallePago.setFormaPago(rs.getString("DESC_FORMA_PAGO"));
					detallePago.setFecValor(rs.getDate("FEC_VALOR"));
					detallePago.setFechaPropuesta(rs.getString("FECHA_PROPUESTA"));
					detallePago.setEstatus("P");
					return detallePago;

				}});
					
		}catch(CannotGetJdbcConnectionException e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:AplicacionDescuentosPropuestasDaoImpl, M:obtenerDetallePagos");
			
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:AplicacionDescuentosPropuestasDaoImpl, M:obtenerDetallePagos");
		}
		
		return listaDetalles;
	}
	
	public List<LlenaComboGralDto>llenarComboBeneficiario(LlenaComboGralDto dto){
		
		String cond="";
		cond=dto.getCondicion();
		dto.setCampoDos("rTRIM(COALESCE(razon_social,'')) +' '+ rTRIM(COALESCE(NOMBRE,'')) + ' ' + rTRIM(COALESCE(PATERNO,'')) " +
				"+ ' ' + rTRIM(COALESCE(MATERNO,''))");
		
		cond = Utilerias.validarCadenaSQL(cond);
		
		if(dto.isRegistroUnico()){
			dto.setCondicion("equivale_persona="+cond);
		}else{
			dto.setCondicion("id_tipo_persona='P'	"
					+"	AND no_empresa in(552,217)"
					+"	AND ((razon_social like '"+cond+"%'"     
					+"	or paterno like '"+cond+"%'" 
					+"	or materno like '"+cond+"%'"   
					+"	or nombre like '"+cond+"%' )" 
					+"	or (equivale_persona like '"+cond+"%'))");	
		}
		
		consultasGenerales = new ConsultasGenerales(jdbcTemplate);
		return consultasGenerales.llenarComboGral(dto);
	}
		
	//Modificado EMS: 15/12/2015
	public List<LlenaComboGralDto>consultarProveedores(String texto)
	{
		StringBuffer sql = new StringBuffer();
		List<LlenaComboGralDto> list= new ArrayList<LlenaComboGralDto>();
		try
		{
			
			sql.append( "SELECT p.no_persona as ID, ");
			sql.append( "       case when p.pers_juridica = 'F' then rtrim(ltrim(p.nombre + ' ' + p.paterno  + ' ' + p.materno)) ");
			sql.append( "       else rtrim(ltrim(p.razon_social)) end as Descrip ");
			sql.append( "  FROM persona p");
			sql.append( " WHERE p.id_tipo_persona = 'P' ");
			sql.append( "   AND (p.razon_social like '" + Utilerias.validarCadenaSQL(texto) + "%'");
			sql.append( "    OR p.nombre + ' ' + p.paterno  + ' ' + p.materno like '" + Utilerias.validarCadenaSQL(texto) + "%')");
			
			/*if(ConstantesSet.gsDBM.equals("ORACLE") || ConstantesSet.gsDBM.equals("POSTGRESQL"))
			{
				sql.append( "SELECT p.no_persona as ID, ");
				sql.append( "       case when p.pers_juridica = 'F' then rtrim(ltrim(p.nombre || ' ' || p.paterno  || ' ' || p.materno)) ");
				sql.append( "       else rtrim(ltrim(p.razon_social)) end as Descrip ");
				sql.append( "  FROM persona p");
				sql.append( " WHERE p.id_tipo_persona = 'P' ");
				sql.append( "   AND (p.razon_social like '" + texto + "%'");
				sql.append( "    OR p.nombre || ' ' || p.paterno  || ' ' || p.materno like '" + texto + "%')");
			}
			
			if(ConstantesSet.gsDBM.equals("SQL SERVER") || ConstantesSet.gsDBM.equals("SYBASE"))
			{
				sql.append( "SELECT p.no_persona as ID, ");
				sql.append( "       case when p.pers_juridica = 'F' then rtrim(ltrim(p.nombre + ' ' + p.paterno  + ' ' + p.materno)) ");
				sql.append( "       else rtrim(ltrim(p.razon_social)) end as Descrip ");
				sql.append( "  FROM persona p");
				sql.append( " WHERE p.id_tipo_persona = 'P' ");
				sql.append( "   AND (p.razon_social like '" + texto + "%'");
				sql.append( "    OR p.nombre + ' ' + p.paterno  + ' ' + p.materno like '" + texto + "%')");
			}*/
			
			System.out.println("consultarProveedores: " + sql);
			
			list= jdbcTemplate.query(sql.toString(), new RowMapper<LlenaComboGralDto>(){
				public LlenaComboGralDto mapRow(ResultSet rs, int idx) throws SQLException {
					LlenaComboGralDto cons = new LlenaComboGralDto();
					cons.setId(rs.getInt("ID"));
					cons.setDescripcion(rs.getString("Descrip"));
					return cons;
			}});
			
		}catch(CannotGetJdbcConnectionException e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Impresion, C:AplicacionDescuentosPropuestasDaoImpl, M:consultarProveedores");
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Impresion, C:AplicacionDescuentosPropuestasDaoImpl, M:consultarProveedores");
			System.err.println(e);
		}
		return list;
	    
	}
	
	@Override
	public List<Map<String, String>> consultaMovimientos(String claveP, String claveD) {
		List<Map<String, String>> listaResultado = new ArrayList<Map<String, String>>();
		StringBuffer sql= new StringBuffer();	
		System.out.println("claveP dao"+claveP);
		System.out.println("claveD dao"+claveD);
		try{
			//claveP = claveP.substring(0,claveP.length()-1 );
			sql.append("SELECT cve_control,beneficiario,importe FROM movimiento \n");
		    sql.append("WHERE cve_control IN("+claveP+" "+claveD+")\n");
		    sql.append("AND ID_TIPO_OPERACION = '3000' \n");
			sql.append("AND ID_ESTATUS_MOV IN('C','N') ORDER BY cve_control,beneficiario\n");
	        System.out.println(sql.toString());
	        listaResultado = jdbcTemplate.query(sql.toString(), new RowMapper<Map<String, String>>(){
				public Map<String, String> mapRow(ResultSet rs, int idx) throws SQLException{
					Map<String, String> campos = new HashMap<String, String>();
					campos.put("cve_control", rs.getString("cve_control"));
					campos.put("beneficiario", rs.getString("beneficiario"));
					campos.put("importe", rs.getString("importe"));
					return campos;
				}				
				
			});	

		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:AplicacionDescuentosPropuestasDaoImpl, M:consultaMovimientos");
			e.printStackTrace();
		}
		return listaResultado;
	}
	
	@Override
	public List<Map<String, String>> consultaHeader(String claveP, String claveD) {
		List<Map<String, String>> listaResultado = new ArrayList<Map<String, String>>();
		StringBuffer sql= new StringBuffer();	
		System.out.println("claveP dao"+claveP);
		System.out.println("claveD dao"+claveD);
		try{
			//claveP = claveP.substring(0,claveP.length()-1 );
			sql.append("SELECT cve_control,fecha_propuesta,concepto,cupo_total FROM seleccion_automatica_grupo \n");
		    sql.append("WHERE cve_control IN("+claveP+" "+claveD+")\n");
			sql.append("ORDER BY concepto,cve_control \n");
	        System.out.println(sql.toString());
	        listaResultado = jdbcTemplate.query(sql.toString(), new RowMapper<Map<String, String>>(){
				public Map<String, String> mapRow(ResultSet rs, int idx) throws SQLException{
					Map<String, String> campos = new HashMap<String, String>();
					campos.put("cve_control", rs.getString("cve_control"));
					campos.put("fecha_propuesta", rs.getString("fecha_propuesta"));
					campos.put("concepto", rs.getString("concepto"));
					campos.put("cupo_total", rs.getString("cupo_total"));
					return campos;
				}				
				
			});	

		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:AplicacionDescuentosPropuestasDaoImpl, M:consultaHeader");
			e.printStackTrace();
		}
		return listaResultado;

	}
	
/****************************Getters and Setters **********************/
	
	public void setDataSource(DataSource dataSource){
		try{
			this.jdbcTemplate = new JdbcTemplate(dataSource);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:RentasDaoImpl, M:setDataSource");
		}
	}
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}
	
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
}
