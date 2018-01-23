package com.webset.set.egresos.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.webset.set.egresos.dao.FactorajeDao;
import com.webset.set.egresos.dto.PagosPendientesDto;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.ConsultasGenerales;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.utils.tools.Utilerias;

import mx.com.gruposalinas.Pagos.DT_Pagos_OBPagos;
import mx.com.gruposalinas.Pagos.DT_Pagos_ResponsePagosResponse;

public class FactorajeDaoImpl implements FactorajeDao {
	private JdbcTemplate jdbcTemplate;
	Bitacora bitacora = new Bitacora();
	ConsultasGenerales consultasGenerales;
	
	
	@Override
	public List<PagosPendientesDto> obtenerListaFactoraje(int empresa, int proveedor, String fechaIni, String fechaFin){
		List<PagosPendientesDto> listaFactoraje = new ArrayList<PagosPendientesDto>();
		StringBuffer sb = new StringBuffer();

		sb.append("		SELECT NO_FACTURA, M.CVE_CONTROL, FEC_VALOR, FECHA_CONTABILIZACION,");
		sb.append(" 	FEC_OPERACION, NO_FOLIO_DET ,NOM_EMPRESA, RAZON_SOCIAL AS beneficiario,");
		sb.append("		NO_DOCTO, m.CONCEPTO, IMPORTE, DESC_FORMA_PAGO, \n");
		sb.append(" 	(SELECT DESC_BANCO FROM CAT_BANCO WHERE ID_BANCO = m.ID_BANCO) AS bancoP, ID_CHEQUERA, \n");
		sb.append(" 	(SELECT DESC_BANCO FROM CAT_BANCO WHERE ID_BANCO = m.ID_BANCO_BENEF) AS bancoB , ID_CHEQUERA_BENEF \n");
		sb.append("		FROM MOVIMIENTO m JOIN EMPRESA e ON m.NO_EMPRESA = E.NO_EMPRESA");
		sb.append(" 					  JOIN PERSONA p ON m.NO_CLIENTE = p.NO_PERSONA \n");
		sb.append("						  JOIN CAT_FORMA_PAGO cp ON m.ID_FORMA_PAGO = cp.ID_FORMA_PAGO \n");
		sb.append("						  JOIN SELECCION_AUTOMATICA_GRUPO sag ON sag.CVE_CONTROL = m.CVE_CONTROL");
		sb.append("		WHERE ID_TIPO_OPERACION = 3000 AND ID_ESTATUS_MOV = 'N' AND (PO_HEADERS = '' OR PO_HEADERS IS NULL) \n");
		sb.append("		AND m.CVE_CONTROL IS NOT NULL \n");
		sb.append("		AND sag.USUARIO_UNO IS NOT NULL \n");
		
		if(empresa != 0){
			sb.append("AND m.NO_EMPRESA = "+ Utilerias.validarCadenaSQL(empresa) +" \n");
	
		}
		if(proveedor != 0){
			sb.append("AND m.NO_CLIENTE = (SELECT NO_PERSONA FROM PERSONA WHERE EQUIVALE_PERSONA = '"+ Utilerias.validarCadenaSQL(proveedor) +"' ) \n");

		}
		if(!fechaFin.equals("") && !fechaIni.equals("")){
			sb.append("AND FEC_VALOR BETWEEN convert(datetime,'"+Utilerias.validarCadenaSQL(fechaIni)+"', 103) AND convert(datetime,'"+Utilerias.validarCadenaSQL(fechaFin)+"', 103)");
	
		}
		
		
		System.out.println("grid factoraje"+sb.toString());
		//System.out.println(sb.toString());
		
		try {
			listaFactoraje = jdbcTemplate.query(sb.toString(), new RowMapper<PagosPendientesDto>() {
				public PagosPendientesDto mapRow(ResultSet rs, int idx) throws SQLException{
					PagosPendientesDto pagosPendienteDto = new PagosPendientesDto();
					
					pagosPendienteDto.setFactura(rs.getString("NO_FOLIO_DET"));
					pagosPendienteDto.setNombreEmpresa(rs.getString("NOM_EMPRESA"));
					pagosPendienteDto.setBeneficiario(rs.getString("beneficiario"));
					pagosPendienteDto.setNumeroDcoumento(rs.getString("NO_DOCTO"));
					pagosPendienteDto.setTexto(rs.getString("CONCEPTO"));
					pagosPendienteDto.setTotal(rs.getDouble("IMPORTE"));
					pagosPendienteDto.setFormaPago(rs.getString("DESC_FORMA_PAGO"));
					pagosPendienteDto.setBancoP(rs.getString("bancoP"));
					pagosPendienteDto.setChequera(rs.getString("ID_CHEQUERA"));
					pagosPendienteDto.setReferenciaBanc(rs.getString("bancoB"));
					pagosPendienteDto.setEstatus(rs.getString("ID_CHEQUERA_BENEF"));
					pagosPendienteDto.setFecValor(rs.getDate("FEC_VALOR"));
					pagosPendienteDto.setFecCont(rs.getDate("FECHA_CONTABILIZACION"));
					pagosPendienteDto.setFecOperacion(rs.getDate("FEC_OPERACION"));
					pagosPendienteDto.setNoFact(rs.getString("NO_FACTURA"));
					pagosPendienteDto.setCveControl(rs.getString("CVE_CONTROL"));
					
				return pagosPendienteDto;
				}});
			
		}catch(CannotGetJdbcConnectionException e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:FactorajeDaoImpl, M:obtenerListaFactoraje");
		
		}catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:FactorajeDaoImpl, M:obtenerListaFactoraje");
		}
		
		return listaFactoraje;
	}
	
	@Override
	public List<LlenaComboGralDto> llenarComboBeneficiario(LlenaComboGralDto dto) {
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

	
	@Override
	public List<LlenaComboGralDto> obtenerProveedores(String filtro){
		List<LlenaComboGralDto> listProveddores = new ArrayList<LlenaComboGralDto>();
		StringBuffer sb = new StringBuffer();
		
		sb.append("SELECT EQUIVALE_PERSONA, RAZON_SOCIAL FROM PERSONA  \n");
		sb.append("WHERE ID_TIPO_PERSONA = 'P' AND RAZON_SOCIAL LIKE '%"+Utilerias.validarCadenaSQL(filtro.toUpperCase())+"%'\n");
		System.out.println(sb.toString());
		try {
			listProveddores = jdbcTemplate.query(sb.toString(), new RowMapper<LlenaComboGralDto>() {
				public LlenaComboGralDto mapRow(ResultSet rs, int idx) throws SQLException{
					LlenaComboGralDto proveedor = new LlenaComboGralDto();
					
					proveedor.setId(rs.getInt("EQUIVALE_PERSONA"));
					proveedor.setDescripcion(rs.getString("RAZON_SOCIAL"));
					
					return proveedor;
				}});
			
		}catch(CannotGetJdbcConnectionException e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:FactorajeDaoImpl, M:obtenerListaFactoraje");
		
			
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:FactorajeDaoImpl, M:obtenerListaFactoraje");
		}
		
		return listProveddores;
		
	}
	
	
	@Override
	public List<LlenaComboGralDto> obtenerIntermediarios(){
		List<LlenaComboGralDto>  listIntermediarios = new ArrayList<LlenaComboGralDto>();
		StringBuffer sb = new StringBuffer();
		
		sb.append("SELECT NO_PROV_INTERMEDIARIO, RAZON_SOCIAL FROM PERSONA p JOIN CAT_INTERMEDIARIO_FACTORAJE i \n");
		sb.append("ON p.EQUIVALE_PERSONA = i.NO_PROV_INTERMEDIARIO \n");
		System.out.println("rrubio combo factoraje "+sb.toString());
		try {
			listIntermediarios = jdbcTemplate.query(sb.toString(), new RowMapper<LlenaComboGralDto>() {
				public LlenaComboGralDto mapRow(ResultSet rs, int idx) throws SQLException{
					LlenaComboGralDto proveedor = new LlenaComboGralDto();
					
					proveedor.setId(rs.getInt("NO_PROV_INTERMEDIARIO"));
					proveedor.setDescripcion(rs.getString("RAZON_SOCIAL"));
					
					return proveedor;
				}});
			
		}catch(CannotGetJdbcConnectionException e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:FactorajeDaoImpl, M:obtenerListaFactoraje");
			
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:FactorajeDaoImpl, M:obtenerListaFactoraje");
		}
		
		return listIntermediarios;
	}
	
	
	@Override
	public Map<String,Object> enviarDatos(String folios, int usuario, int noFactorae, String fechaFactoraje){
		StringBuffer sql = new StringBuffer();
		StringBuffer sqlInsert = new StringBuffer();
		List<DT_Pagos_OBPagos> listDtPagos = new ArrayList<DT_Pagos_OBPagos>();
		Map<String, Object> mapaPagoa = new HashMap<String, Object>();
		mapaPagoa.put("estatus", false);
		
		sql.append("\n  Select S.Soiemp,M.No_Docto,1 as secuencia,0 as no_pedido,M.No_Folio_Det,M.No_Factura,");
		sql.append("\n  convert(char(11),m.fec_valor ,103) as fec_valor,convert(char(11),m.fec_valor ,103) as fec_factura,m.origen_mov,"); 
		sql.append("\n  '"+ noFactorae +"' as no_persona,");
		sql.append("\n  m.importe as importe,m.id_divisa,m.id_divisa_original,1 as tipo_camb,");
		sql.append("\n  case when M.id_forma_pago in (3,6,8,5)  then 3 ");
		sql.append("\n  else case when M.id_forma_pago = 1 then 1  ");
		sql.append("\n  Else M.Id_Forma_Pago End  End as forma_pago,M.Id_Banco,");
		sql.append("\n  m.id_chequera,'' as referencia, m.concepto,'000' as mandante,'P','',");
		sql.append("\n  convert(char(11),M.Fec_Valor ,103)as Fechap,0 as Cheque_Trn,M.Importe as Imp_Usa,3200 as Tipo_Operacion,'E' as Tipo_Movto,0.0 as capital,0.0 as interes,0.0 as isr,M.Id_Chequera,0 as Casa_Bolsa,");
		sql.append("\n   convert(char(8),getdate(), 108),");
		sql.append("\n  (select substring(clave_usuario,1,10) ");
		sql.append("\n  From seg_usuario ");
		sql.append("\n  where id_usuario="+ usuario+ "),"); 
		sql.append("\n  (select host_name ()),case when m.id_forma_pago in (3,6,8,5)  then 103 else case when m.id_forma_pago = 1 then 101  else 0 end  end,0 as no_cheque,Coalesce(cve_control,'') as cve_control ");
		sql.append("\n  , 'KC' as clase_docto, Cerp.libro_mayor, "+ noFactorae +" as factoraje "); 
		sql.append("\n  from movimiento m   Join Cat_Ctas_Contables_Erp Cerp On Cerp.Id_Chequera = M.Id_Chequera and m.id_banco = Cerp.id_banco, set006 s ");
		sql.append("\n  where m.no_empresa=s.setemp ");
		sql.append("\n  And Cerp.cargo_abono='E'");
		sql.append("\n and (m.PO_HEADERS = '' or m.PO_HEADERS is null)");
		sql.append("\n  And S.Siscod='CP'");
		sql.append("\n  And Id_Tipo_Operacion In (3000,3801)");
		sql.append("\n  And No_Folio_Det In "+ folios +" ");
		
		System.out.println("consulta del select "+sql.toString());
		
		try {

			listDtPagos = jdbcTemplate.query(sql.toString(), new RowMapper<DT_Pagos_OBPagos>(){
				public DT_Pagos_OBPagos mapRow(ResultSet rs, int idx) throws SQLException {
					return new DT_Pagos_OBPagos(
							rs.getString("Soiemp"), rs.getString("No_Docto"), rs.getString("secuencia"), 
							rs.getString("no_pedido"), rs.getString("No_Folio_Det"), rs.getString("No_Factura"), 
							rs.getString("fec_valor"), rs.getString("fec_factura"), rs.getString("origen_mov"), 
							rs.getString("no_persona"), rs.getString("importe"), rs.getString("id_divisa"), 
							rs.getString("id_divisa_original"), rs.getString("tipo_camb"), rs.getString("forma_pago"), 
							rs.getString("Id_Banco"), "", rs.getString("referencia"), 
							rs.getString("concepto"), rs.getString("mandante"), "", 
							"", "", rs.getString("Fechap"), 
							rs.getString("Cheque_Trn"), rs.getString("Imp_Usa"), rs.getString("Tipo_Operacion"),
							rs.getString("Tipo_Movto"), rs.getString("capital"), rs.getString("interes"),
							rs.getString("isr"), rs.getString("libro_mayor"), rs.getString("Casa_Bolsa"),
							"", "", "",
							"", rs.getString("no_cheque"), rs.getString("cve_control"),
							"", "", "",
							"", "", "",
							"", "", "",
							"", "", "",
							"", "", "",
							"", "", "",
							"", "", "", rs.getString("clase_docto"));
				}
			});
			
			
			mapaPagoa.put("lista", listDtPagos);
			
			sqlInsert.append("\n INSERT INTO ZEXP_FACT (no_empresa,no_doc_sap,secuencia,no_pedido,no_folio_set,no_factura,fec_valor,fec_factura,origen,no_persona,");
			sqlInsert.append("\n imp_pago,id_divisa,id_divisa_original,tipo_camb,forma_pago,id_banco,id_cheque,referencia,concepto,mandante,fecha_exp, ");
			sqlInsert.append("\n Estatus,Causa_Rech,Fechap,Cheque_Trn,Imp_Usa,Tipo_Operacion,Tipo_Movto,Capital,Interes,Isr,Chequera_Inv,Casa_Bolsa,Hora_Recibo,");
			sqlInsert.append("\n id_usuario_seg,terminalusr,rubro_erp,no_cheque,cve_control)");
		    
			sqlInsert.append("\n Select S.Soiemp,M.No_Docto,1 as secuencia,0 as no_pedido,M.No_Folio_Det,M.No_Factura,");
			sqlInsert.append("\n convert(datetime,'"+ Utilerias.validarCadenaSQL(fechaFactoraje) +"', 103) as fec_valor, m.fec_valor as fec_factura, m.origen_mov,"); 
			sqlInsert.append("\n '"+ Utilerias.validarCadenaSQL(noFactorae) +"' as no_persona,");
			sqlInsert.append("\n m.importe as importe,m.id_divisa,m.id_divisa_original,1 as tipo_camb,");
			sqlInsert.append("\n case when M.id_forma_pago in (3,6,8,5)  then 3 ");
			sqlInsert.append("\n else case when M.id_forma_pago = 1 then 1  ");
			sqlInsert.append("\n Else M.Id_Forma_Pago End  End as forma_pago,M.Id_Banco,");
			sqlInsert.append("\n m.id_chequera,'' as referencia, m.concepto,'000' as mandante, m.fec_valor as fecha_exp,'P','',");
			sqlInsert.append("\n convert(char(11),M.Fec_Valor ,103)as Fechap,0 as Cheque_Trn,M.Importe as Imp_Usa,3200 as Tipo_Operacion,'E' as Tipo_Movto,0.0 as capital,0.0 as interes,0.0 as isr,M.Id_Chequera,0 as Casa_Bolsa,");
			sqlInsert.append("\n convert(char(8),getdate(), 108),");
			sqlInsert.append("\n (select substring(clave_usuario,1,10) ");
			sqlInsert.append("\n From seg_usuario ");
			sqlInsert.append("\n where id_usuario="+usuario+"),"); 
			sqlInsert.append("\n (select host_name()),case when m.id_forma_pago in (3,6,8,5)  then 103 else case when m.id_forma_pago = 1 then 101  else 0 end  end,0 as no_cheque,Coalesce(cve_control,'') as cve_control ");
			sqlInsert.append("\n from movimiento m  join Cat_Forma_Pago fp on M.Id_Forma_Pago=Fp.Id_Forma_Pago, set006 s ");
			sqlInsert.append("\n where m.no_empresa=s.setemp ");
			sqlInsert.append("\n and (m.PO_HEADERS = '' or m.PO_HEADERS is null)");
			sqlInsert.append("\n And S.Siscod='CP'");
			sqlInsert.append("\n And Id_Tipo_Operacion In (3000,3801)");
			sqlInsert.append("\n And No_Folio_Det In "+ Utilerias.validarCadenaSQL(folios) +" ");
			
			
			jdbcTemplate.update(sqlInsert.toString());
			mapaPagoa.put("estatus", true);
			
			
		}catch(CannotGetJdbcConnectionException e){
			mapaPagoa.put("msg", "Se perdio la conexion con la base de datos");
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:FactorajeDaoImpl, M:enviarDatos");
			
		} catch (Exception e) {
			eliminarZexpFact(folios);
			mapaPagoa.put("msg", "Error en la actualizacion de datos");
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:FactorajeDaoImpl, M:enviarDatos");
		}	
				
		
		
		return mapaPagoa;
	}
	
	@Override
	public String insertaBitacoraPagoPropuestas(DT_Pagos_ResponsePagosResponse[] pagosRespuestaq, List<DT_Pagos_OBPagos> listPagos, String fecHoy){
		
		StringBuffer mensaje = new StringBuffer();
		mensaje.append("Resultado: \n");
		
		try {
			
			if (pagosRespuestaq.length > 0) {//no_doc_poliza_sap
				for (int j = 0; j < pagosRespuestaq.length; j++) {
					if (pagosRespuestaq[j].getDOC_POLIZA_SAP() == null ||pagosRespuestaq[j].getDOC_POLIZA_SAP().equals("")) {
						//error
						StringBuffer sqlBitacora = new StringBuffer();
						sqlBitacora.append("Insert Into Bitacora_Pago_Propuestas Values('");
						sqlBitacora.append(Utilerias.validarCadenaSQL(pagosRespuestaq[j].getNO_EMPRESA()));
						sqlBitacora.append("','");
						sqlBitacora.append(Utilerias.validarCadenaSQL(pagosRespuestaq[j].getNO_DOC_SAP()));
						sqlBitacora.append("','");
						sqlBitacora.append(Utilerias.validarCadenaSQL(pagosRespuestaq[j].getSECUENCIA()));
						sqlBitacora.append("','");
						sqlBitacora.append(Utilerias.validarCadenaSQL(pagosRespuestaq[j].getMensaje()));
						sqlBitacora.append("', getdate())");
						jdbcTemplate.update(sqlBitacora.toString());
						sqlBitacora.delete(0, sqlBitacora.length());
						String sqlElimina = "Delete From Zexp_Fact Where No_Folio_Set = "+Utilerias.validarCadenaSQL(listPagos.get(j).getNO_FOLIO_SET()) +" ";
						jdbcTemplate.update(sqlElimina);
						mensaje.append("Documento Fallido: " + pagosRespuestaq[j].getNO_DOC_SAP() + "Razon: " + pagosRespuestaq[j].getMensaje() + "\n");
						
					} else {

						String sqlActulizaMob = "";
						sqlActulizaMob = "update movimiento set fec_exportacion = convert(datetime,'"+ fecHoy +"', 103) ,lote_salida = '1', ";
						sqlActulizaMob += "PO_HEADERS = '"+pagosRespuestaq[j].getDOC_POLIZA_SAP()+"'";
						sqlActulizaMob += " where no_folio_det = "+ listPagos.get(j).getNO_FOLIO_SET() ;
						jdbcTemplate.update(sqlActulizaMob);
						mensaje.append("Documento: " + pagosRespuestaq[j].getNO_DOC_SAP() + "SAP: " + pagosRespuestaq[j].getDOC_POLIZA_SAP() + "\n");
						
						modificaRenglones(listPagos.get(j).getNO_FOLIO_SET());
						

					}
				}
			}
			
		}catch(CannotGetJdbcConnectionException e){
			mensaje.append("Se perdio la conexion con la base de datos \n"); 
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:FactorajeDaoImpl, M:enviarDatos");
				
			
		} catch (Exception e) {
			mensaje.append("Error en la actualizacion de datos \n");
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:FactorajeDaoImpl, M:enviarDatos");
		}
		
		return mensaje.toString();
	}
	
	
	
	public int modificaRenglones(String folio){
		StringBuffer sb = new StringBuffer();
		StringBuffer sbMov = new StringBuffer();
		
		sb.append("UPDATE SELECCION_AUTOMATICA_GRUPO SET ");
		sb.append("MONTO_MAXIMO = (MONTO_MAXIMO - (SELECT IMPORTE FROM MOVIMIENTO WHERE NO_FOLIO_DET = '"+ Utilerias.validarCadenaSQL(folio) +"')), \n");
		sb.append("CUPO_AUTOMATICO = (CUPO_AUTOMATICO - (SELECT IMPORTE FROM MOVIMIENTO WHERE NO_FOLIO_DET = '"+ Utilerias.validarCadenaSQL(folio) +"')), \n");
		sb.append("CUPO_TOTAL = (CUPO_TOTAL - (SELECT IMPORTE FROM MOVIMIENTO WHERE NO_FOLIO_DET = '"+ Utilerias.validarCadenaSQL(folio) +"'))  \n");
		sb.append("WHERE (USUARIO_UNO = '' OR USUARIO_UNO IS NULL) AND (USUARIO_DOS = '' OR USUARIO_DOS IS NULL) \n");
		sb.append("AND CVE_CONTROL = (SELECT CVE_CONTROL FROM MOVIMIENTO WHERE NO_FOLIO_DET = '"+ Utilerias.validarCadenaSQL(folio) +"') \n");
		
		sbMov.append("UPDATE MOVIMIENTO SET CVE_CONTROL = '', ID_CODIGO = 'KC', ID_ESTATUS_MOV = 'A' \n");
		sbMov.append("WHERE NO_FOLIO_DET = "+ Utilerias.validarCadenaSQL(folio) +" \n");
		
		
		try {
			
			jdbcTemplate.update(sb.toString());
			jdbcTemplate.update(sbMov.toString());
			
		}catch(CannotGetJdbcConnectionException e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:FactorajeDaoImpl, M:modificaRenglones");
			
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:FactorajeDaoImpl, M:modificaRenglones");
		}
		
		return 0;
	}
	
	@Override
	public String consultarConfiguaraSet(int indice){
		ConsultasGenerales consultasGenerales = new ConsultasGenerales(jdbcTemplate);
		return consultasGenerales.consultarConfiguraSet(indice);
	}
	
	@Override
	public int eliminarZexpFact(String folios){
		StringBuffer sb = new StringBuffer();
		sb.append("DELETE FROM zexp_fact WHERE NO_FOLIO_SET IN "+ Utilerias.validarCadenaSQL(folios) +"\n");
		try {
			jdbcTemplate.update(sb.toString());
		
		}catch(CannotGetJdbcConnectionException e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:FactorajeDaoImpl, M:eliminarZexpFact");
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:FactorajeDaoImpl, M:eliminarZexpFact");
		}
		return 0;
	}

	
	/****************************Getters and Setters***********************/
	
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
