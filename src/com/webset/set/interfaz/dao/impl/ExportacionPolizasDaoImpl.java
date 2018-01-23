package com.webset.set.interfaz.dao.impl;

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

import com.webset.set.interfaz.dao.ExportacionPolizasDao;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.ConsultasGenerales;
import com.webset.set.utilerias.dto.GrupoEmpresasDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.set.utilerias.dto.MovimientoDto;
import com.webset.utils.tools.Utilerias;

import mx.com.gruposalinas.Poliza.DT_Polizas_ResponseResponse;

public class ExportacionPolizasDaoImpl implements ExportacionPolizasDao{
	private JdbcTemplate jdbcTemplate;
	ConsultasGenerales consultasGenerales;
	Bitacora bitacora = new Bitacora();
	
	public List<LlenaComboGralDto> llenarComboGrupoEmpresa(GrupoEmpresasDto dto) {
		List<LlenaComboGralDto> result = new ArrayList<LlenaComboGralDto>();
		try {
			consultasGenerales= new ConsultasGenerales(jdbcTemplate);
			result = consultasGenerales.llenarComboGrupoEmpresa(dto);
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Interfaz, C:ExportacionPolizasDaoImpl, M:llenarComboGrupoEmpresas");
		}
		return result;
	}
	public List<MovimientoDto> consultaPolizasExportar(String empresa, String origen, String fecInicio, String fecFin){
		List<MovimientoDto> result = new ArrayList<MovimientoDto>();
		StringBuffer sql = new StringBuffer();
		try {
			sql.append("");
			sql.append("\n Select Zp.Nombre_Poliza,Cg.Desc_Grupo, Cr.Desc_Rubro, P.Razon_Social, Cb.Desc_Banco, M.Id_Chequera, ");
			sql.append("\n 			M.No_Docto,M.Referencia, M.Importe, M.Id_Divisa, M.Fec_Valor,Cfp.Desc_Forma_Pago, ");
			sql.append("\n			M.No_Folio_Det, M.Id_Estatus_Mov, ");
			sql.append("\n			M.Id_Banco,  M.Id_Rubro, M.Id_Grupo,M.No_Empresa,M.Id_Forma_Pago,zp.id_poliza, ");
			sql.append("\n			M.Tipo_Cambio , m.no_cliente, M.Id_Divisa_Original, m.Fec_Alta,Division, Cerp.Libro_Mayor, m.concepto");
			sql.append("\n	From Movimiento M Join Cat_Forma_Pago Cfp On Cfp.Id_Forma_Pago = M.Id_Forma_Pago ");
			sql.append("\n                  Join Cat_Banco Cb On Cb.Id_Banco = M.Id_Banco ");
			sql.append("\n                  Join Cat_Rubro Cr On Cr.Id_Rubro = M.Id_Rubro ");
			sql.append("\n                  Join Cat_Grupo Cg On Cg.Id_Grupo = Cr.Id_Grupo ");
			sql.append("\n                  Join Persona P On P.No_Empresa = M.No_Empresa ");
			sql.append("\n					Join Asignacion_Poliza_Grupo Apg On Apg.Id_Grupo = M.Id_Rubro ");
			sql.append("\n                  join Zimp_Polizamanual zp on zp.id_poliza = apg.id_poliza ");
			sql.append("\n                  Join Cat_Ctas_Contables_Erp Cerp On (Cerp.Id_Chequera = M.Id_Chequera ");
			sql.append("\n                  										and  Cerp.cargo_abono = 'E')");   
			sql.append("\n	Where ");
			if(empresa!= null && !empresa.equals("0")){
				sql.append("\n M.No_Empresa = ");
				sql.append(empresa);
				sql.append("\n and ");
			}
			sql.append(" M.Origen_Mov = '");
			sql.append(Utilerias.validarCadenaSQL(origen));
			sql.append("'");
			sql.append("\n	And (M.Id_Tipo_Operacion In (3700,3701,3705,3706,3708,3709,3101,4101,4102,4103,4104,4001,3200) or (m.id_tipo_operacion = 3200 and M.Origen_Mov = 'CVD' ))");
			sql.append("\n	And M.Id_Estatus_Mov = 'A' ");
			sql.append("\n	And (M.Lote_Salida is null or M.Lote_Salida = 0 )");
			sql.append("\n	And M.Fec_Exportacion Is Null ");
			sql.append("\n	And M.Fec_Valor Between convert(datetime,'");
			sql.append(Utilerias.validarCadenaSQL(fecInicio));
			sql.append("', 103)");
			sql.append(" and convert(datetime,'");
			sql.append(Utilerias.validarCadenaSQL(fecFin));
			sql.append("', 103)");
			System.out.println("Consulta expo polizas "+sql);
			result= jdbcTemplate.query(sql.toString(), new RowMapper<MovimientoDto>(){
				public MovimientoDto mapRow(ResultSet rs, int idx) throws SQLException {
					MovimientoDto mov = new MovimientoDto();
					//mov.setNoPoliza(Integer.parseInt(rs.getString("No_Poliza") != null && !rs.getString("No_Poliza").equals("") ? rs.getString("No_Poliza") : "0"));
					mov.setBServicios(rs.getString("Nombre_Poliza"));
					mov.setDescGrupo(rs.getString("Desc_Grupo"));
					mov.setDescRubro(rs.getString("Desc_Rubro"));
					mov.setDescGrupoFlujo(rs.getString("Razon_Social"));
					mov.setDescBancoBenef(rs.getString("Desc_Banco"));
					mov.setIdChequeraBenef(rs.getString("Id_Chequera"));
					mov.setNoDocto(rs.getString("No_Docto"));
					mov.setReferencia(rs.getString("Referencia"));
					mov.setImporte(rs.getDouble("Importe"));
					mov.setIdDivisa(rs.getString("Id_Divisa"));
					mov.setFecValor(rs.getDate("Fec_Valor"));
					mov.setDescFormaPago(rs.getString("Desc_Forma_Pago"));
					mov.setNoFolioDet(rs.getInt("No_Folio_Det"));
					mov.setIdEstatusMov(rs.getString("Id_Estatus_Mov"));
					mov.setIdBanco(rs.getInt("Id_Banco"));
					mov.setIdRubro(rs.getDouble("Id_Rubro"));
					mov.setIdGrupoCupo(rs.getString("Id_Grupo"));
					mov.setNoEmpresa(rs.getInt("No_Empresa"));
					mov.setIdFormaPago(rs.getInt("Id_Forma_Pago"));
					mov.setNoPoliza(rs.getInt("id_poliza"));
					//
					mov.setFecAlta(rs.getDate("Fec_Alta") != null && !rs.getDate("Fec_Alta").equals("")? rs.getDate("Fec_Alta"): null);
					mov.setDivision(rs.getString("Division"));
					mov.setEquivale(rs.getString("Libro_Mayor"));
					mov.setIdDivisaOriginal(rs.getString("Id_Divisa_Original"));
					mov.setNoCliente(rs.getString("no_cliente"));
					mov.setCab(rs.getString("Tipo_Cambio"));
					mov.setConcepto(rs.getString("concepto"));
					return mov;
				}});
			//System.out.println(result.size()+"tama�o de la lista");
		} catch (CannotGetJdbcConnectionException e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Interfaz, C:ExportacionPolizasDaoImpl, M:consultaPolizasExportar");
		}
		catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Interfaz, C:ExportacionPolizasDaoImpl, M:consultaPolizasExportar");
		}
		return result;
	}
	
	public String consultarConfiguaraSet(int indice){
		consultasGenerales=new ConsultasGenerales(jdbcTemplate);
		return consultasGenerales.consultarConfiguraSet(indice);
	}
	
	public Map<String, Object> insertaBitacoraPoliza(DT_Polizas_ResponseResponse[]  dt_Polizas_ResponseResponse, List<MovimientoDto> movimientos){
		Map<String, Object> mapReturn= new HashMap<String, Object>();
		mapReturn.put("msgUsuario", "");
		try {
			for (int i = 0; i < dt_Polizas_ResponseResponse.length; i++) {
				StringBuffer sqlInsert = new StringBuffer();
				sqlInsert.append("Insert Into BITACORA_POLIZAS Values( '");
				sqlInsert.append(dt_Polizas_ResponseResponse[i].getNO_EMPRESA());
				sqlInsert.append("','");
				sqlInsert.append(dt_Polizas_ResponseResponse[i].getNO_DOC_SAP());
				sqlInsert.append("','");
				sqlInsert.append(dt_Polizas_ResponseResponse[i].getSECUENCIA());
				sqlInsert.append("','");
				sqlInsert.append(dt_Polizas_ResponseResponse[i].getID_POLIZA_SAP());
				sqlInsert.append("','");
				sqlInsert.append(dt_Polizas_ResponseResponse[i].getMENSAJE());
				sqlInsert.append("')");
				if(jdbcTemplate.update(sqlInsert.toString())!=0)
					mapReturn.put("msgUsuario", " Poliza con el documento " + dt_Polizas_ResponseResponse[i].getNO_DOC_SAP().toString() +" se exporto " );
				else
					mapReturn.put("msgUsuario", "");
				
				if (dt_Polizas_ResponseResponse[i].getNO_DOC_SAP() == "") {
					String sqlElimina = "Delete From Zoperaciones_Set Where No_Folio_Set = "+movimientos.get(i).getNoFolioDet() +" ";
					jdbcTemplate.update(sqlElimina);
				}
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Egresos, C:ExportacionPolizasDaoImpl, M:insertaBitacoraPoliza");
		}
		return mapReturn;
	}
	
	public Map<String, Object> insertaZoperaciones(List<MovimientoDto> movimientos, String idUsuario){
		Map<String, Object> mapReturn= new HashMap<String, Object>();
		List<MovimientoDto> movimientosInsertados = new ArrayList<MovimientoDto>();
		mapReturn.put("msgUsuario", "");
		try {
			for (int i = 0; i < movimientos.size(); i++) {
				StringBuffer sqlInsert = new StringBuffer();
				sqlInsert.append("\n INSERT INTO Zoperaciones_Set (no_empresa,no_doc_sap,secuencia,no_pedido,no_folio_set,no_factura,fec_valor,fec_factura,origen,no_persona,");
				sqlInsert.append("\n imp_pago,id_divisa,id_divisa_original,tipo_camb,forma_pago,id_banco,id_cheque,referencia,concepto,mandante,fecha_exp, ");
				sqlInsert.append("\n Estatus,Causa_Rech,Fechap,Cheque_Trn,Imp_Usa,Tipo_Operacion,Tipo_Movto,Capital,Interes,Isr,Chequera_Inv,Casa_Bolsa,Hora_Recibo,");
				sqlInsert.append("\n id_usuario_seg,terminalusr,rubro_erp,no_cheque,cve_control)");
			    
				sqlInsert.append("\n Select S.Soiemp,M.No_Docto,1 as secuencia,0 as no_pedido,M.No_Folio_Det,M.No_Factura,");
				sqlInsert.append("\n m.fec_valor  as fec_valor,m.fec_valor as fec_factura,m.origen_mov,"); 
				sqlInsert.append("\n case id_tipo_operacion ");
				sqlInsert.append("\n when 3000 ");
				sqlInsert.append("\n then  (select equivale_persona from persona where no_persona=m.no_cliente)");
				sqlInsert.append("\n when 3801 ");
				sqlInsert.append("\n then (select distinct no_prov_as400 from zimp_fact where no_doc_sap=m.no_docto)end as no_persona,");
				sqlInsert.append("\n m.importe as importe,m.id_divisa,m.id_divisa_original,1 as tipo_camb,");
				sqlInsert.append("\n case when M.id_forma_pago in (3,6,8,5)  then 3 ");
				sqlInsert.append("\n else case when M.id_forma_pago = 1 then 1  ");
				sqlInsert.append("\n Else M.Id_Forma_Pago End  End as forma_pago,M.Id_Banco,");
				sqlInsert.append("\n m.id_chequera,'' as referencia, m.concepto,'000' as mandante, m.fec_valor fecha_exp,'P','',");
				sqlInsert.append("\n M.Fec_Valor as Fechap,0 as Cheque_Trn,M.Importe as Imp_Usa,3200 as Tipo_Operacion,'E' as Tipo_Movto,0.0 as capital,0.0 as interes,0.0 as isr,M.Id_Chequera,0 as Casa_Bolsa,");
				sqlInsert.append("\n (Select To_Char(Sysdate, 'HH24:MI:SS') From Dual),");
				sqlInsert.append("\n (select substr(clave_usuario,1,10) ");
				sqlInsert.append("\n From seg_usuario ");
				sqlInsert.append("\n where id_usuario="+idUsuario+"),"); 
				sqlInsert.append("\n (select host_name()),case when m.id_forma_pago in (3,6,8,5)  then 103 else case when m.id_forma_pago = 1 then 101  else 0 end  end,0 as no_cheque,Coalesce(cve_control,'') as cve_control ");
				sqlInsert.append("\n from movimiento m  join Cat_Forma_Pago fp on M.Id_Forma_Pago=Fp.Id_Forma_Pago, set006 s ");
				sqlInsert.append("\n where m.no_empresa=s.setemp ");
				sqlInsert.append("\n and (m.PO_HEADERS = '' or m.PO_HEADERS is null or m.PO_HEADERS = ' ')");
				sqlInsert.append("\n And S.Siscod='CP'");
				sqlInsert.append("\n And Id_Tipo_Operacion In (3700,3701,3705,3706,3708,3709,3101,4101,4102,4103,4104,4001)");
				sqlInsert.append("\n And No_Folio_Det In( " +movimientos.get(i).getNoFolioDet() + ")");
				System.out.println("consulta sql de insert "+sqlInsert.toString());
				if(jdbcTemplate.update(sqlInsert.toString())!= 0){
					movimientosInsertados.add(movimientos.get(i));
				}
			}
			if (movimientosInsertados.size()>0) 
				mapReturn.put("movimientosInsertados", movimientosInsertados);
			else
				mapReturn.put("msgUsuario", "Error al procesar la informacion en set.");
			
		}
		
		catch(CannotGetJdbcConnectionException j){
			mapReturn.put("msgUsuario", "La conexion a la base de datos se perdio.");
				bitacora.insertarRegistro(new Date().toString() + " " + j.toString() + "P:Interfaz, C:ExportacionPolizasDaoImpl, M:insertaBitacoraPoliza");
				mapReturn.put("msgUsuario", "\n La coneccion a la base de datos se perdio.");
		} catch (Exception e) {
			mapReturn.put("msgUsuario", "Error desconocido.");
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Interfaz, C:ExportacionPolizasDaoImpl, M:insertaBitacoraPoliza");
		}
		return mapReturn;
	}
	
	public void eliminaPolizasZoperaciones(List<MovimientoDto> movimientos){
		try {
			for (int i = 0; i < movimientos.size(); i++) {
				String sqlElimina = "Delete From Zoperaciones_Set Where No_Folio_Set = "+movimientos.get(i).getNoFolioDet() +" ";
				jdbcTemplate.update(sqlElimina);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Interfaz, C:ExportacionPolizasDaoImpl, M:eliminaPolizasZoperaciones");
		}
		
	}
	//getters and setters
	public void setDataSource(DataSource dataSource){
		try{
			this.jdbcTemplate = new JdbcTemplate(dataSource);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Interfaz, C:ConsultaPropuestasDao, M:setDataSource");
		}
	}
	
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}
	
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

}
