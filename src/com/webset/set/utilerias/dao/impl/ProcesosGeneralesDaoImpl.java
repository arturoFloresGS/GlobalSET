package com.webset.set.utilerias.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.transaction.support.TransactionTemplate;

import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.ConsultasGenerales;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utilerias.dao.ProcesosGeneralesDao;
import com.webset.set.utilerias.dto.GeneradorDto;
import com.webset.set.utileriasmod.dto.ProcesosGeneralesDto;

import mx.com.gruposalinas.ContabilidadElectronica.DT_ContabilidadElectronica_OBContabilidadElectronica;
import mx.com.gruposalinas.ContabilidadElectronica.DT_ContabilidadElectronica_ResponseContabilidadResponse;

public class ProcesosGeneralesDaoImpl implements ProcesosGeneralesDao {
	JdbcTemplate jdbcTemplate;
	Bitacora bitacora;
	ConsultasGenerales objConsultasGenerales;
	String sql = "";
	private Funciones funciones= new Funciones();
	private TransactionTemplate transactionTemplate;
	
	private static Logger logger = Logger.getLogger(ConsultasGenerales.class);
	
	public List<ProcesosGeneralesDto> llenaGrid(){
		List<ProcesosGeneralesDto> listaResultado = new ArrayList<ProcesosGeneralesDto>();
		sql = "";
		try{
			sql = "Select p.spid as idProceso, p.nt_username as usuarioNT, p.loginame, p.nt_domain as dominio, p.hostname, ";
			sql += "\n p.program_name as programa, p.status, p.open_tran, p.last_batch as ultimaConsulta, ";
			sql += "\n (cu.nombre, cu.paterno, cu.materno) as usuario ";
			sql += "\n from master..sysprocesses p, cat_usuario cu ";
			sql += "\n where p.program_name in ('NetroSET') ";
			sql += "\n and p.hostprocess <> '' ";
			sql += "\n and p.dbid = (select dbid from master..sysdatabases ";
			sql += "\n where dbid in (select dbid from master..sysprocesses where spid = @@spid)) ";
			sql += "\n and p.spid <> @@spid ";
			sql += "\n and cu.no_usuario = p.loginame ";
			//ssql = ssql & " and c.no_usuario = convert(int,(RIGHT(rtrim(p.loginame),3)))
			
			listaResultado = jdbcTemplate.query(sql, new RowMapper<ProcesosGeneralesDto>(){
				public ProcesosGeneralesDto mapRow(ResultSet rs, int idx)throws SQLException{
					ProcesosGeneralesDto campos = new ProcesosGeneralesDto();
					campos.setUsuarioNT(rs.getString("usuario"));
					campos.setUsuarioNT(rs.getString("usuarioNT"));
					campos.setDominio(rs.getString("dominio"));
					campos.setHostName(rs.getString("hostname"));
					campos.setPrograma(rs.getString("programa"));
					campos.setUltimaConsulta(rs.getString("ultimaConsulta"));
					campos.setIdProceso(rs.getInt("idProceso"));					
					return campos;
				}
			});
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: ProcesosGeneralesDaoImpl, M: llenaGrid");
		}return listaResultado;
	}
	
	public String obtieneFecha(){
		String mensaje = "";		
		List<ProcesosGeneralesDto> listaResultado = new ArrayList<ProcesosGeneralesDto>();		
		sql = "";
		
		try{
			sql = "Select fec_hoy from fechas ";
			
			listaResultado = jdbcTemplate.query(sql, new RowMapper<ProcesosGeneralesDto>(){
				public ProcesosGeneralesDto mapRow(ResultSet rs, int idx)throws SQLException{
					ProcesosGeneralesDto campos = new ProcesosGeneralesDto();
					campos.setFechaHoy(rs.getString("fec_hoy"));
					return campos;
				}
			});
			
			if (listaResultado.size() > 0){
				mensaje = listaResultado.get(0).getFechaHoy();				
			}
			else
				mensaje = "";
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: ProcesosGeneralesDaoImpl, M: obtieneFecha");
		}return mensaje;
	}
		
	public int validaEstatus(){
		int resultadoEntero = 0;
		sql = "";
		try{
			sql = "SELECT estatus_sist from fechas ";
			
			resultadoEntero = jdbcTemplate.queryForInt(sql);
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: ProcesosGeneralesDaoImpl, M: validaEstatus");
		}return resultadoEntero;
	}
	
	public int validaEstatusSist2(){
		int resultado = 0;
		sql = "";
		
		try{
			sql = "SELECT estatus_sist2 from fechas ";
			
			resultado = jdbcTemplate.queryForInt(sql);
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: ProcesosGeneralesDaoImpl, M: validaEstatusSist2");
		}return resultado;
	}
	
	public int actualizaEstatus(int estatus){
		int resultado = 0;
		sql = "";
		try{
			sql = "Update fechas Set estatus_sist = "+ estatus +" ";
			
			resultado = jdbcTemplate.update(sql);
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: ProcesosGeneralesDaoImpl, M: actualizaEstatus");
		}return resultado;
	}
	
	/**
	 * Actualiza el estatus_sist2 de la tabla fechas.
	 */
	public int actualizaEstatusSist2(String estatus)
	{
		logger.debug("Entra: actualizaEstatusSist2");
		int respuesta=0;
		
		try
		{
			if (estatus.equals("uno"))
				sql = "Update fechas Set estatus_sist2 = 1 ";
			else if (estatus.equals("estatus"))
				sql = "Update fechas Set estatus_sist2 = (Select estatus_sist from fechas)";
			
			respuesta = jdbcTemplate.update(sql);
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: ProcesosGeneralesDaoImpl, M: actualizaEstatusSist2");
			logger.error("Error en actualizaEstatusSist2: " + e.toString());
		}
		logger.debug("Sale: actualizaEstatusSist2 ["+respuesta+"]");
		return respuesta;
	}
	
	public int validaUsuariosConectados(){
		int respuesta = 0;
		sql = "";
		try{
			sql = "Select count(*) ";
			sql += "\n from master..sysprocesses p, cat_usuario cu ";
			sql += "\n where p.program_name in ('NetroSET') ";
			sql += "\n and p.hostprocess <> '' ";
			sql += "\n and p.dbid = (select dbid from master..sysdatabases ";
			sql += "\n where dbid in (select dbid from master..sysprocesses where spid = @@spid)) ";
			sql += "\n and p.spid <> @@spid ";
			sql += "\n and cu.no_usuario = p.loginame ";
			
			respuesta = jdbcTemplate.queryForInt(sql);
			 
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: ProcesosGeneralesDaoImpl, M: validaUsuariosConectados");
		}return respuesta;
	}
	
	public String respaldoBD(int indice)
	{		
		objConsultasGenerales = new ConsultasGenerales(jdbcTemplate);
		return objConsultasGenerales.consultarConfiguraSet(indice);
	}	
	
	public Map<String, Object> correHistorico()
	{
		logger.debug("Entra: correHistorico");
		objConsultasGenerales = new ConsultasGenerales(jdbcTemplate, transactionTemplate);
		
		Map<String, Object> result = new HashMap<String, Object>();
		int j = 5;
		do {
			result = objConsultasGenerales.correHistorico();
			if (j == 1) break;
			j--;
		} while ((int) result.get("vResult") > 0);
		logger.debug("Sale: correHistorico");
		
		System.out.println(result.get("vResult"));
		return result;
	}
	
	@Override
	public int insertaValorDivisa(String[] indicadoresBanc) {
		StringBuffer sql = new StringBuffer();
		int res = 0; 
		try { 
			sql.append("Insert into valor_divisa (id_divisa, fec_divisa, valor) values( " );
			sql.append( "\n  'DLS', " );
			sql.append( "\n (select fec_manana from fechas), "+Double.parseDouble(indicadoresBanc[1])+"");
			sql.append( "\n )");
			System.out.println(sql);	
			res = jdbcTemplate.update(sql.toString()); 
			sql = new StringBuffer();
			
			sql.append("Insert into valor_divisa (id_divisa, fec_divisa, valor) values( " );
			sql.append( "\n  'EUR', " );
			sql.append( "\n (select fec_manana from fechas), "+Double.parseDouble(indicadoresBanc[3])+"");
			sql.append( "\n )");
			System.out.println(sql);	
			res = jdbcTemplate.update(sql.toString());
			sql = new StringBuffer();
			
			sql.append("Insert into valor_divisa (id_divisa, fec_divisa, valor) values( " );
			sql.append( "\n  'GBP', " );
			sql.append( "\n (select fec_manana from fechas), "+Double.parseDouble(indicadoresBanc[5])+"");
			sql.append( "\n )");
			System.out.println(sql);	
			res = jdbcTemplate.update(sql.toString());
			sql = new StringBuffer();
			
			sql.append("Insert into valor_divisa (id_divisa, fec_divisa, valor) values( " );
			sql.append( "\n  'VEF', " );
			sql.append( "\n (select fec_manana from fechas), "+Double.parseDouble(indicadoresBanc[7])+"");
			sql.append( "\n )");
			System.out.println(sql);
			res = jdbcTemplate.update(sql.toString());
			sql = new StringBuffer();
			
			sql.append("Insert into valor_divisa (id_divisa, fec_divisa, valor) values( " );
			sql.append( "\n  'MN', " );
			sql.append( "\n (select fec_manana from fechas), '"+1.0000+"'");
			sql.append( "\n )");
			System.out.println(sql);	
			res = jdbcTemplate.update(sql.toString());
			
			sql= new StringBuffer();
			sql.append("insert into tasa values('CET',(select fec_manana from fechas),");
            sql.append("(select valor from tasa where id_tasa='CET' and fecha = (select fec_hoy from fechas)))");
            System.out.println(sql);
            res = jdbcTemplate.update(sql.toString());
            sql = new StringBuffer();

            sql.append("insert into tasa values('LIB3',(select fec_manana from fechas),");
            sql.append("(select valor from tasa where id_tasa='LIB3' and fecha = (select fec_hoy from fechas)))");
            System.out.println(sql);
            res = jdbcTemplate.update(sql.toString());
            sql = new StringBuffer();

            sql.append("insert into tasa values('TIIE',(select fec_manana from fechas),");
            sql.append("(select valor from tasa where id_tasa='TIIE' and fecha = (select fec_hoy from fechas)))");
            System.out.println(sql);
            res = jdbcTemplate.update(sql.toString());
			
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C: ProcesosGeneralesDaoImpl, M: insertaValorDivisas");
		}
		return res;
	}
	
	public List<ProcesosGeneralesDto> procesoNeteo(String fecHoy, int noEmpresa){
		List<ProcesosGeneralesDto> listaResultado = new ArrayList<ProcesosGeneralesDto>();
		sql = "";
		
		try{
			//Por el momento esta contemplado solo el dia de hoy
			sql = "Select c.no_empresa as coinversora, c.id_divisa, a.no_empresa, ";
			sql += "\n e.no_cuenta_emp, e.nom_empresa, ";
			sql += "\n (Select coalesce(sum(s.importe), 0) ";
			sql += "	from saldo s ";
			sql += "\n where s.no_empresa = c.no_empresa";
			sql += "\n and s.no_cuenta = a.no_empresa";
			sql += "\n and s.id_tipo_saldo = 91 ";
			sql += "\n and s.no_linea = (Select cd.id_divisa_soin from cat_divisa cd where cd.id_divisa = c.id_divisa)";
			sql += "\n and s.fec_valor = '"+ fecHoy +"'";
			sql += "\n ) as sdo_coinversion, ";
			sql += "\n (Select coalesce(sum(s.importe),0) ";
			sql += "\n from saldo s";
			sql += "\n where s.no_empresa = a.no_empresa ";
			sql += "\n and s.no_cuenta = e.no_cuenta_emp ";
			sql += "\n and s.id_tipo_saldo = 91 ";
			sql += "\n and s.no_linea = ";
			sql += "\n (Select cd.id_divisa_soin from cat_divisa cd where cd.id_divisa = c.id_divisa) ";
			sql += "\n and s.fec_valor = '"+ fecHoy +"' ";
			sql += "\n ) as sdo_credito ";
			sql += "\n from arbol_empresa a, empresa e, cuenta c";
			sql += "\n where a.no_empresa = e.no_empresa ";
			sql += "\n and e.no_empresa = c.no_cuenta ";
			sql += "\n and c.id_tipo_cuenta = 'T' ";
			sql += "\n and c.no_empresa in (";
			sql += "\n Select no_empresa from empresa where b_conceptradora = 'S'";
			
			if (noEmpresa != 0)
				sql += "\n and a.no_empresa = "+ noEmpresa +"";
			
			sql += "\n order by c.no_empresa, e.no_empresa, c.id_divisa ASC";
			
			
			listaResultado = jdbcTemplate.query(sql, new RowMapper<ProcesosGeneralesDto>(){
				public ProcesosGeneralesDto mapRow(ResultSet rs, int idx)throws SQLException{
					ProcesosGeneralesDto campos = new ProcesosGeneralesDto();
					campos.setCoinversora(rs.getInt("coinversora"));
					campos.setNoEmpresa(rs.getInt("no_empresa"));
					campos.setIdDivisa(rs.getString("id_divisa"));
					campos.setNoCuentaEmp(rs.getInt("no_cuenta_emp"));
					campos.setNomEmpresa(rs.getString("nom_empresa"));
					campos.setSdoCoinversion(rs.getDouble("sdo_coinversion"));
					campos.setSdoCredito(rs.getDouble("sdo_credito"));
					return campos;
				}
			});
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: ProcesosGeneralesDaoImpl, M: procesoNeteo");
		}return listaResultado;
	}
	
	public int obtieneFolio (String tipoFolio){
		objConsultasGenerales = new ConsultasGenerales(jdbcTemplate);
		return objConsultasGenerales.seleccionarFolioReal(tipoFolio);
	}
	
	public int actualizaFolio (String tipoFolio){
		sql = "";
		try
		{
			sql = "";
			sql += "Update folio set num_folio = num_folio + 1 ";
			sql += "where tipo_folio = '" + tipoFolio + "'";
			
			return jdbcTemplate.update(sql);
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:ProcesosGeneralesDaoImpl, M:actualizaFolio");
			return 0;
		}
	}
	
	public int insertaEnParametro(ProcesosGeneralesDto objDto){
		int resultado = 0;
		sql = "";
		
		try{
			sql = "Insert into parametro (no_empresa, no_folio_param, aplica, secuencia, id_tipo_operacion, ";
			sql += "\n no_cuenta, id_estatus_mov, id_chequera, id_banco, importe, ";
			sql += "\n b_salvo_buen_cobro, fec_valor, fec_valor_original, ";
			sql += "\n id_estatus_reg, usuario_alta, fec_alta, id_divisa, ";
			sql += "\n id_forma_pago, importe_original, fec_operacion, id_banco_benef, ";
			sql += "\n id_chequera_benef, origen_mov, concepto, id_caja, no_folio_mov, ";
			sql += "\n folio_ref, grupo, beneficiario, no_cliente, tipo_cambio, id_rubro) ";
			sql += "\n values (";
			sql += "\n "+ objDto.getNoEmpresa() +", "+ objDto.getFolioParam() +", 1, "+ objDto.getSecuencia() +", ";
			sql += "\n "+ objDto.getTipoOperacion() +", "+ objDto.getNoCuentaEmp() +", 'A', ";
			sql += "\n '"+ objDto.getIdChequera() +"', "+ objDto.getIdBanco() +", "+ objDto.getAjustePrestamo() +", ";
			sql += "\n 'S', '"+ objDto.getFechaHoy() +"', '"+ objDto.getFechaHoy() +"', 'P', ";
			sql += "\n '"+ objDto.getUsuarioAlta() +"', '"+ objDto.getFechaHoy() +"', '"+ objDto.getIdDivisa() +"', ";
			sql += "\n 3, "+ objDto.getAjustePrestamo() +", '"+ objDto.getFechaHoy() +"', "+ objDto.getIdBancoBenef() +", ";
			sql += "\n '"+ objDto.getIdChequeraBenef() +"', 'INI', '"+ objDto.getConcepto() +"', "+ objDto.getIdCaja() +", "+ objDto.getIdBancoBenef() +", ";
			sql += "\n 0, "+ objDto.getFolioParam() +", "+ objDto.getNoEmpresa() +", "+ objDto.getCoinversora() +", ";
			sql += "\n "+ objDto.getTipoCambio() +", 31505) ";
						
			resultado = jdbcTemplate.update(sql);
	
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: ProcesosGeneralesDaoImpl, M: insertaEnParametro");
		}return resultado;
	}
	
	
	public Map<String, Object> llamaGenerador(GeneradorDto generadorDto)
	{		
		objConsultasGenerales = new ConsultasGenerales(jdbcTemplate);
		
		return objConsultasGenerales.ejecutarGenerador(generadorDto);
	}
		
	public int creaRespaldoBD(String baseDatos, String ruta){
		int resultado = 0;
		sql = "";
		try{
			sql = "BACKUP DATABASE ";
			sql += "\n '"+ baseDatos +"' ";
			sql += "\n TO DISK = '"+ ruta +"' ";
			
			resultado = jdbcTemplate.queryForInt(sql);
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C: ProcesosGeneralesDaoImpl, M: creaRespaldoBD");
		}return resultado;
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
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C: EquivalenciaEmpresasDaoImpl, M:setDataSource");
		}
	}

	@Override
	public void bloqueaUsuarios() {
		try
		{
			jdbcTemplate.update("UPDATE SEG_USUARIO SET ESTATUS = 'I' WHERE ((SELECT FEC_HOY FROM FECHAS) - FECHA_ACCESO) >= 90");
		}
		catch(Exception e)
		{
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:ProcesosGenerales, M:bloqueaUsuarios");
		}
		
	}

	@Override
	public List<DT_ContabilidadElectronica_OBContabilidadElectronica> obtenerDatosContabilidadElectronica() {
		List<DT_ContabilidadElectronica_OBContabilidadElectronica>datos= new ArrayList<>();
		StringBuffer sql= new StringBuffer();
		try{
			sql.append("select z.no_empresa,m.po_headers,z.fechap,m.id_chequera,m.id_banco, \n");
			sql.append("m.id_chequera_benef,m.id_banco_benef, m.id_banco,z.no_persona,m.no_cheque, \n");
			sql.append("m.referencia,z.no_doc_sap, z.no_empresa,z.fec_valor, m.no_cobrador \n");
			sql.append("from zexp_fact z, movimiento m where ltrim(rtrim(z.no_doc_sap)) = ltrim(rtrim(m.no_docto)) \n");
			sql.append("and m.no_folio_det = z.no_folio_set \n");
			sql.append("and m.grupo_pago =0 \n");
			sql.append("and z.estatus = 'P' ");
			
			datos = jdbcTemplate.query(sql.toString(), new RowMapper <DT_ContabilidadElectronica_OBContabilidadElectronica>(){
			

				public DT_ContabilidadElectronica_OBContabilidadElectronica mapRow(ResultSet rs, int idx)throws SQLException{
					DT_ContabilidadElectronica_OBContabilidadElectronica campos = new DT_ContabilidadElectronica_OBContabilidadElectronica();
					if (rs.getString("no_cobrador")!=null && !rs.getString("no_cobrador").equals("")) {
						
						campos.setSOC(funciones.ajustarLongitudCampo(
								rs.getString("no_cobrador"), 4, "D", "", "0"));
						
					} else {
						campos.setSOC(funciones.ajustarLongitudCampo(
								rs.getString("no_empresa"), 4, "D", "", "0"));
					}
					
					campos.setNUMDOCTO(rs.getString("po_headers"));
					campos.setEJERCICIO(rs.getString("fechaP").substring(6));
					campos.setCTAORI(rs.getString("id_chequera"));
					campos.setBANCOORINAL(rs.getString("id_banco"));
					campos.setBANCOORIEXT("id_banco");
					campos.setCTADEST(rs.getString("id_chequera_benef"));
					campos.setBANCODESTNAL(rs.getString("id_banco_benef"));
					campos.setBANCODESTEXT(rs.getString("id_banco"));
					campos.setNUMACREE(rs.getString("no_persona"));
					campos.setNUM(rs.getString("no_cheque"));
					campos.setFOLIO(rs.getString("referencia"));
					campos.setDOCTOPAG(rs.getString("no_doc_sap"));
					campos.setSOCDOCPAG(rs.getString("no_empresa"));
					campos.setEJERDOCPAG(rs.getString("fec_valor").substring(6));
					return campos;
				}
			}
			);
			
			
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C: ProcesosGeneralesDaoImpl, M: creaRespaldoBD");
		}return datos;
	}

	@Override
	public String configuraSet(int indice) {
		try {
			objConsultasGenerales= new ConsultasGenerales(jdbcTemplate);
			return objConsultasGenerales.consultarConfiguraSet(indice);
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C: ProcesosGeneralesDaoImpl, M: configuraSet");

		}return "";
	}

	@Override
	public void actualizarMovimiento(DT_ContabilidadElectronica_ResponseContabilidadResponse respuesta, 
			DT_ContabilidadElectronica_OBContabilidadElectronica mov) {
		StringBuffer sql = new StringBuffer();
		try {
			sql.append("update zexp_fact set causa_rech ='"+respuesta.getMensaje()+"',"
					+ " estatus='"+(respuesta.getMensaje().indexOf(
					"El registro se inserto con exito. Num.")==-1?"R":"E")+"' where no_folio_set in (");
			sql.append("select z.no_folio_set from zexp_fact z, movimiento m");
			sql.append("where ltrim(rtrim(z.no_doc_sap)) = ltrim(rtrim(m.no_docto))");
			sql.append("and m.no_folio_det = z.no_folio_set");
			sql.append("and m.grupo_pago =0");
			sql.append("and z.estatus = 'P'");
			sql.append("and z.no_doc_sap ='"+mov.getDOCTOPAG()+"');");

			System.out.println(sql);
			
			jdbcTemplate.update(sql.toString()); 
			
	
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C: ProcesosGeneralesDaoImpl, M: actualizarMovimiento");
		}
		
	}

	@Override
	public Map<String, Object> importaProv() {
		Map<String, Object> result = new HashMap<>();
		result.put("Error", true);
		try {
			objConsultasGenerales = new ConsultasGenerales(jdbcTemplate);
			result = objConsultasGenerales.importaProv();
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + 
					"P:Utilerias, C: ProcesosGeneralesDaoImpl, M: importaProv");
		}
		return result;
	}

	@Override
	public Map<String, Object> importaPasivos() {
		Map<String, Object> result = new HashMap<>();
		result.put("Error", true);
		try {
			objConsultasGenerales = new ConsultasGenerales(jdbcTemplate);
			result = objConsultasGenerales.importaPasivos();
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + 
					"P:Utilerias, C: ProcesosGeneralesDaoImpl, M: importaPasivos");
		}
		return result;
	}
	
	public TransactionTemplate getTransactionTemplate() {
		return transactionTemplate;
	}

	public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
		this.transactionTemplate = transactionTemplate;
	}
}
