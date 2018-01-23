package com.webset.set.reportes.dao.impl;

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

import com.webset.set.reportes.dao.ReportesDao;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.ConstantesSet;
import com.webset.set.utilerias.ConsultasGenerales;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utilerias.GlobalSingleton;
import com.webset.set.utilerias.dto.LlenaComboChequeraDto;
import com.webset.set.utilerias.dto.LlenaComboEmpresasDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;

/**
 * 
 * @author Jessica Arelly
 *
 */
@SuppressWarnings("unchecked")
public class ReportesDaoImpl implements ReportesDao{

	private JdbcTemplate jdbcTemplate;
	private Bitacora bitacora = new Bitacora();
	private Funciones funciones = new Funciones();
	private static Logger logger = Logger.getLogger(ReportesDaoImpl.class);
	String gsDBM = ConstantesSet.gsDBM;
	private ConsultasGenerales consultasGenerales;
	GlobalSingleton globalSingleton;
	
	/**
	 * FunSQLSelectEmpresasUsuario
	 * global: empresasUsuarioRST
	 * @param usuario
	 * @return
	 */
	
	public List<LlenaComboEmpresasDto> consultarEmpresas(int usuario){
		List<LlenaComboEmpresasDto> list = new ArrayList<LlenaComboEmpresasDto>();
		StringBuffer sbSQL = new StringBuffer();
		try{
			sbSQL.append("\n  SELECT e.no_empresa as id, e.nom_empresa as describ");
		    sbSQL.append("\n  FROM empresa e,persona p");
		    sbSQL.append("\n  Where e.no_empresa > 1");
		    sbSQL.append("\n  and p.id_estatus_per<>'I'");
		    sbSQL.append("\n  and p.id_tipo_persona='E'");
		    sbSQL.append("\n  and p.no_empresa=e.no_empresa");
		    sbSQL.append("\n  and e.no_empresa in  (select no_empresa from usuario_empresa ");
		    sbSQL.append("\n  where no_usuario = " + usuario + ") order by describ");
		    
		    System.out.println("query cheques consultarEmpresas \n" + sbSQL +"\n Fin consultarEmpresas");
			
		    //logger.info("consulta de empresas: "+sSQL.toString());
			list = jdbcTemplate.query(sbSQL.toString(), new RowMapper<LlenaComboEmpresasDto>(){
				public LlenaComboEmpresasDto mapRow(ResultSet rs, int idx) throws SQLException {
					LlenaComboEmpresasDto empresa = new LlenaComboEmpresasDto();
					empresa.setNoEmpresa(rs.getInt("id"));
					empresa.setNomEmpresa(rs.getString("describ"));
					return empresa;
				}
			});
		}catch(Exception e){
			e.printStackTrace();
			System.out.println("---------------------------------- \n ----------------------------------\n");
			System.out.println("Error en consultarEmpresas \n ");
			System.out.println("---------------------------------- \n ----------------------------------\n");
			
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Reportes, C:ReportesDaoImpl, M:consultarEmpresas");
		}
		return list;
	}
	
	/**
	 * FunSQLSelectCajasUsuario
	 */
	public List<LlenaComboGralDto> consultarCajas(int usuario){
		StringBuffer sbSQL = new StringBuffer();
		List<LlenaComboGralDto> list = new ArrayList<LlenaComboGralDto>();
		try{
			sbSQL.append("\n  SELECT id_caja as id,");
		    sbSQL.append("\n  desc_caja as describ");
		    sbSQL.append("\n  FROM cat_caja");
		    sbSQL.append("\n  WHERE id_caja in");
		    sbSQL.append("\n      (select id_caja from caja_usuario");
		    sbSQL.append("\n      where no_usuario = " + usuario + ")");
		    sbSQL.append("\n  ORDER BY desc_caja");
		    System.out.println("query cheques consultarCajas \n" + sbSQL +"\n Fin consultarCajas");
		    list = jdbcTemplate.query(sbSQL.toString(), new RowMapper<LlenaComboGralDto>(){
				public LlenaComboGralDto mapRow(ResultSet rs, int idx) throws SQLException {
					LlenaComboGralDto caja = new LlenaComboGralDto();
					caja.setId(rs.getInt("id"));
					caja.setDescripcion(rs.getString("describ"));
					return caja;
				}
			});
		}catch(Exception e){
			e.printStackTrace();
			System.out.println("---------------------------------- \n ----------------------------------\n");
			System.out.println("Error en consultarCajas \n ");
			System.out.println("---------------------------------- \n ----------------------------------\n");
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
					+ "P:Reportes, C:ReportesDaoImpl, M:consultarCajas");
		}
		return list;
	}
	
	public List<LlenaComboChequeraDto> consultarOrigen(String tipoMovto){
		List<LlenaComboChequeraDto> list = new ArrayList<LlenaComboChequeraDto>();
		StringBuffer sbSQL = new StringBuffer();
		try{
			sbSQL.append("\n  SELECT");
		    sbSQL.append("\n  origen_mov as ID,");
		    sbSQL.append("\n  desc_origen_mov as Descrip");
		    sbSQL.append("\n  FROM ");
		    sbSQL.append("\n  cat_origen_mov");
		    sbSQL.append("\n  WHERE");
		    sbSQL.append("\n  id_tipo_movto = '" + tipoMovto + "'");
		    sbSQL.append("\n  ORDER BY desc_origen_mov");
		    System.out.println("query cheques consultarOrigen \n" + sbSQL +"\n Fin consultarOrigen");
		    
		    list = jdbcTemplate.query(sbSQL.toString(), new RowMapper<LlenaComboChequeraDto>(){
				public LlenaComboChequeraDto mapRow(ResultSet rs, int idx) throws SQLException {
					LlenaComboChequeraDto origen = new LlenaComboChequeraDto();
					origen.setId(rs.getString("ID"));
					origen.setDescripcion(rs.getString("Descrip"));
					return origen;
				}
			});
			
		}catch(Exception e){
			e.printStackTrace();
			System.out.println("---------------------------------- \n ----------------------------------\n");
			System.out.println("Error en consultarOrigen \n ");
			System.out.println("---------------------------------- \n ----------------------------------\n");
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
					+ "P:Reportes, C:ReportesDaoImpl, M:consultarOrigen");
		}
		return list;
	}
	
	public List<LlenaComboGralDto> consultarBancos(int iEmpresa, String sDivisa){
		StringBuffer sbSQL = new StringBuffer();
		List<LlenaComboGralDto> list = new ArrayList<LlenaComboGralDto>();
		globalSingleton = GlobalSingleton.getInstancia();
		
		try{
			sbSQL.append("\n  SELECT");
		    sbSQL.append("\n  distinct");
		    sbSQL.append("\n  cat_banco.id_banco as ID,");
		    sbSQL.append("\n  cat_banco.desc_banco as describ");
		    sbSQL.append("\n  FROM cat_banco,");
		    sbSQL.append("\n  cat_cta_banco");
		    sbSQL.append("\n  WHERE cat_cta_banco.id_banco = cat_banco.id_banco ");
		    
		    if(iEmpresa != 0)
		    	sbSQL.append("\n  and cat_cta_banco.no_empresa = " + iEmpresa);
		    else {
		    	sbSQL.append("\n  and cat_cta_banco.no_empresa in (select no_empresa ");
		    	sbSQL.append(" from usuario_empresa where no_usuario = " + globalSingleton.getUsuarioLoginDto().getIdUsuario() + ")");
		    }
		    sbSQL.append("\n  and cat_cta_banco.id_divisa = '" + sDivisa + "'");
		    sbSQL.append("\n  and cat_cta_banco.tipo_chequera not in ('U','V') ");
		    sbSQL.append("\n  ORDER BY desc_banco");
		    
		    System.out.println("query cheques consultarBancos \n" + sbSQL +"\n Fin consultarBancos");
			   
		    list = jdbcTemplate.query(sbSQL.toString(), new RowMapper<LlenaComboGralDto>(){
				public LlenaComboGralDto mapRow(ResultSet rs, int idx) throws SQLException {
					LlenaComboGralDto caja = new LlenaComboGralDto();
					caja.setId(rs.getInt("ID"));
					caja.setDescripcion(rs.getString("describ"));
					return caja;
				}
			});
		}catch(Exception e){
			e.printStackTrace();
			System.out.println("---------------------------------- \n ----------------------------------\n");
			System.out.println("Error en consultarBancos \n ");
			System.out.println("---------------------------------- \n ----------------------------------\n");
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
					+ "P:Reportes, C:ReportesDaoImpl, M:consultarBancos");
		}
		return list;
	}
	
	public List<LlenaComboChequeraDto> consultarChequeras(int iBanco, int iEmpresa){
		List<LlenaComboChequeraDto> list = new ArrayList<LlenaComboChequeraDto>();
		StringBuffer sbSQL = new StringBuffer();
		globalSingleton = GlobalSingleton.getInstancia();
		
		try {
			sbSQL.append("\n  SELECT id_chequera as ID, id_chequera as describ");
		    sbSQL.append("\n  FROM cat_cta_banco");
		    sbSQL.append("\n  WHERE id_banco = " + iBanco);
		    sbSQL.append("\n  and tipo_chequera in ('C','P','I','M','O') ");
		    
		    if(iEmpresa != 0)
		    	sbSQL.append("\n  and no_empresa = " + iEmpresa);
		    else {
		    	sbSQL.append("\n  and no_empresa in (select no_empresa ");
		    	sbSQL.append(" from usuario_empresa where no_usuario = " + globalSingleton.getUsuarioLoginDto().getIdUsuario() + ")");
		    }
		    sbSQL.append("\n  ORDER BY describ");
		    
		    System.out.println("query cheques consultarChequeras \n" + sbSQL +"\n Fin consultarChequeras");
			
		    list = jdbcTemplate.query(sbSQL.toString(), new RowMapper<LlenaComboChequeraDto>(){
				public LlenaComboChequeraDto mapRow(ResultSet rs, int idx) throws SQLException {
					LlenaComboChequeraDto origen = new LlenaComboChequeraDto();
					origen.setId(rs.getString("ID"));
					origen.setDescripcion(rs.getString("describ"));
					return origen;
				}
			});
			
		}catch(Exception e){
			e.printStackTrace();
			System.out.println("---------------------------------- \n ----------------------------------\n");
			System.out.println("Error en consultarChequeras \n ");
			System.out.println("---------------------------------- \n ----------------------------------\n");
			
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
					+ "P:Reportes, C:ReportesDaoImpl, M:consultarChequeras");
		}
		return list;
	}
	
	public String armarQueryReporteCheques(Map<String, Object> datos , String tabla){
		StringBuffer sbSQL = new StringBuffer();
		try{
			if(tabla.equals("movimiento")|| tabla.equals("hist_movimiento")){
				sbSQL.append("\n SELECT ");
				sbSQL.append("\n m.usuario_alta AS user_al, m.usuario_modif AS user_mod, ");
				sbSQL.append("\n m.importe, m.beneficiario, m.concepto, ");
				sbSQL.append("\n m.id_banco, cb.desc_banco, m.id_chequera, m.no_docto, ");
				sbSQL.append("\n m.no_empresa, nom_empresa, m.id_caja, cc.desc_caja, ");
				sbSQL.append("\n CASE ");
				sbSQL.append("\n 		when m.id_estatus_mov = 'I' then CONVERT(CHAR(10), m.fec_imprime,103) ");
				sbSQL.append("\n        when m.id_estatus_mov = 'R' then CONVERT(CHAR(10), m.fec_imprime,103) ");
				sbSQL.append("\n        when m.id_estatus_mov in ('J','P','V') then CONVERT(CHAR(10), m.fec_valor,103) ");
				sbSQL.append("\n END as fecha, ");
				sbSQL.append("\n m.no_cheque , m.fec_imprime AS fecha_impresion, CONVERT(CHAR(10), m.fec_cheque,103) AS fecha_cheque, ");
				sbSQL.append("\n m.fec_valor AS fec_valor, m.fec_entregado AS fec_entregado, ");
				sbSQL.append("\n m.no_folio_det, m.id_estatus_mov, ");
				sbSQL.append("\n case ");
				sbSQL.append("\n 		when m.id_estatus_mov in ('I', 'R') and b_entregado = 'S' then 'ENTREGADO' ");
				sbSQL.append("\n        when m.id_estatus_mov = 'I' and (b_entregado in ('N', '') or b_entregado is null) then 'IMPRESO'");
				sbSQL.append("\n        when m.id_estatus_mov = 'R' and (b_entregado in ('N', '') or b_entregado is null) then 'REIMPRESO'");
				sbSQL.append("\n        when m.id_estatus_mov in ('P','J','V')  then 'POR IMPRIMIR' ");
				sbSQL.append("\n END as estatus, ");
				sbSQL.append("\n case ");
				sbSQL.append("\n        when id_forma_pago in (1,7) then 'CHEQUE' ");
				sbSQL.append("\n END as tipo_cheque");
				
				if(tabla.equals("movimiento"))
					sbSQL.append("\n FROM   movimiento m ");
				else
					sbSQL.append("\n FROM   hist_movimiento m "); //HISTORICO 
				
				sbSQL.append("\n        LEFT JOIN cat_caja cc ON( m.id_caja = cc.id_caja ), ");
				sbSQL.append("\n        cat_banco cb, empresa e ");
				sbSQL.append("\n WHERE  m.no_empresa = e.no_empresa ");
				sbSQL.append("\n        and m.id_banco = cb.id_banco ");
				sbSQL.append("\n        and m.no_empresa in (" + funciones.validarCadena(datos.get("empresas").toString()) + ") ");
				sbSQL.append("\n        and cc.id_caja in (" + funciones.validarCadena(datos.get("cajas").toString()) + ") ");
				sbSQL.append("\n        and m.id_divisa = '" + funciones.validarCadena(datos.get("divisa").toString()) + "' ");
				
				sbSQL.append("\n        and m.id_banco between " + funciones.convertirCadenaInteger(datos.get("bancoInf").toString()) + " and  " + funciones.convertirCadenaInteger(datos.get("bancoSup").toString()));
				sbSQL.append("\n        and m.id_chequera like ('" + funciones.validarCadena(datos.get("chequera").toString()) + "') ");
				sbSQL.append("\n        and m.id_tipo_movto = 'E' ");
				sbSQL.append("\n        and m.id_forma_pago in (" + funciones.convertirCadenaInteger(datos.get("formaPago").toString()) + ") ");
				sbSQL.append("\n        and m.id_estatus_mov in (" + funciones.validarCadena(datos.get("estatusMov").toString()) + ") ");
				if(!funciones.validarCadena(datos.get("origen").toString()).equals("")){
					if(datos.get("origen").equals("CXP"))
						sbSQL.append("\n        and (m.origen_mov = '' or m.origen_mov is null) ");
					else
						sbSQL.append("\n        and m.origen_mov = '" + datos.get("origen") + "' ");
				}
						        
				if(funciones.validarCadena(datos.get("estatusMov").toString()).equals("'J', 'P', 'V'")){
					sbSQL.append("\n        and m.fec_valor between CONVERT(DATETIME,'" + funciones.ponerFechaSola(datos.get("fechaIni").toString()) + "',103) and CONVERT(DATETIME,'" + funciones.ponerFechaSola(datos.get("fechaFin").toString()) + "', 103) ");
				}else if(funciones.validarCadena(datos.get("estatusMov").toString()).equals("'I', 'P', 'R', 'X', 'J', 'V'")){
				    sbSQL.append("\n        AND ( ");
				    sbSQL.append("\n        case ");
				    sbSQL.append("\n                when m.id_estatus_mov = 'I' then m.fec_imprime ");
				    sbSQL.append("\n                when m.id_estatus_mov = 'R' then m.fec_imprime ");
				    sbSQL.append("\n                when m.id_estatus_mov in ('J','P','V') then m.fec_valor");
				    sbSQL.append("\n        end ) BETWEEN");
				    sbSQL.append("\n       CONVERT(DATETIME,'" + funciones.ponerFechaSola(datos.get("fechaIni").toString()) + "' ,103)");
				    sbSQL.append("\n       AND CONVERT(DATETIME,'" + funciones.ponerFechaSola(datos.get("fechaFin").toString()) + "', 103) ");

				}else if(funciones.validarCadena(datos.get("estatusMov").toString()).equals("'I', 'R'")){
					if(funciones.validarCadena(datos.get("estatusEntregado").toString()).equals("S")){
				        sbSQL.append("\n        and m.fec_entregado between CONVERT(DATETIME,'" + funciones.ponerFechaSola(datos.get("fechaIni").toString()) + "',103) and CONVERT(DATETIME,'" + funciones.ponerFechaSola(datos.get("fechaFin").toString()) + "', 103) ");
				        sbSQL.append("\n        and b_entregado = 'S'");
					}else if(funciones.validarCadena(datos.get("estatusEntregado").toString()).equals("N")){
				        sbSQL.append("\n        and (case ");
				        sbSQL.append("\n                when m.id_estatus_mov = 'I' then m.fec_imprime ");
				        sbSQL.append("\n                when m.id_estatus_mov = 'R' then m.fec_imprime ");
				        sbSQL.append("\n             end) between CONVERT(DATETIME,'" + funciones.ponerFechaSola(datos.get("fechaIni").toString()) + "', 103) and CONVERT(DATETIME,'" + funciones.ponerFechaSola(datos.get("fechaFin").toString()) + "', 103) ");
				        sbSQL.append("\n        and (m.b_entregado is null or m.b_entregado = '' or m.b_entregado = 'N')");
					}
				}else if(funciones.validarCadena(datos.get("estatusMov").toString()).equals("'R'")){
				    //Aqui tiene que ser la fecha_reimprime para buscar los cheques Reimpresos
					sbSQL.append("\n        and m.fec_imprime between CONVERT(DATETIME,'" + funciones.ponerFechaSola(datos.get("fechaIni").toString()) + "',103) and CONVERT(DATETIME,'" + funciones.ponerFechaSola(datos.get("fechaFin").toString()) + "', 103) ");
				}else if(funciones.validarCadena(datos.get("estatusMov").toString()).equals("'I', 'R', 'X'")){
				    sbSQL.append("\n        and (case ");
				    sbSQL.append("\n                when m.id_estatus_mov = 'I' then m.fec_imprime ");
				    sbSQL.append("\n                when m.id_estatus_mov = 'R' then m.fec_reimprime ");
				    sbSQL.append("\n             end) between CONVERT(DATETIME,'" + funciones.ponerFechaSola(datos.get("fechaIni").toString()) + "',103) and CONVERT(DATETIME,'" + funciones.ponerFechaSola(datos.get("fechaFin").toString()) + "', 103) ");
				}

				if(funciones.validarCadena(datos.get("estatusCb").toString()).equals("P"))
				    sbSQL.append("\n        and id_estatus_cb in ('P', 'N') ");

				//sbSQL.append("\n        AND (((select count(*) from usuario_area WHERE no_usuario = " + datos.get("usuario") + ") > 0) or ");//modificado de = 0 a >0
				//sbSQL.append("\n             (id_area in (select id_area from usuario_area where no_usuario = " + datos.get("usuario") + ")))");

				if(!funciones.validarCadena(datos.get("cheIni").toString()).equals("") && !funciones.validarCadena(datos.get("cheFin").toString()).equals("")) {
							sbSQL.append("\n	And m.no_cheque between "+ Integer.parseInt(datos.get("cheIni").toString()) +" and "+ Integer.parseInt(datos.get("cheFin").toString()) +"");
					 }
				
			}
			
			/*Para cancelados*/
			if(tabla.equals("bitacora_cheques")){
				 sbSQL.append("\n SELECT  DISTINCT bc.usuario AS user_al, bc.usuario AS user_mod, bc.importe, bc.beneficiario, ");
			        sbSQL.append("\n         bc.concepto, bc.id_banco, cb.desc_banco, bc.id_chequera, bc.no_docto, bc.no_empresa, ");
			        sbSQL.append("\n         e.nom_empresa, bc.id_caja, cc.desc_caja, ");
			        
			        if(ConstantesSet.gsDBM.equals("DB2"))
			            sbSQL.append("\n         cast(substr(cast(bc.fecha_bitacora as varchar(15)),1,11) as date) AS fecha, ");
			        else
			            sbSQL.append("\n         CONVERT(CHAR(10), bc.fecha_bitacora,103) AS fecha,");
			        
			        sbSQL.append("\n         bc.no_cheque, bc.fec_impresion AS fecha_impresion, ");
			        sbSQL.append("\n         CONVERT(CHAR(10), bc.fec_cheque,103) AS fecha_cheque, bc.fec_impresion AS fec_valor, ");
			        sbSQL.append("\n         bc.fecha_bitacora AS fec_entregado, bc.no_folio_det, bc.id_estatus, ");
			        sbSQL.append("\n         case    when bc.id_estatus in ('C','I','M','N','P','R') then 'CANCELADO' ");
			        sbSQL.append("\n                 when bc.id_estatus in ('O') then 'OMITIDO' ");
			        sbSQL.append("\n         end as estatus, ");
			        sbSQL.append("\n         'CHEQUE' AS tipo_cheque ");
			        sbSQL.append("\n FROM    bitacora_cheques bc ");
			
			        sbSQL.append("\n         LEFT JOIN cat_caja cc ON (bc.id_caja=cc.id_caja), ");
			        sbSQL.append("\n         cat_banco cb, empresa e ");
			        sbSQL.append("\n WHERE   bc.no_empresa = e.no_empresa ");
			        sbSQL.append("\n         AND bc.no_empresa IN (" + funciones.validarCadena(datos.get("empresas").toString()) + ") ");
			        sbSQL.append("\n         AND cc.id_caja in (" + funciones.validarCadena(datos.get("cajas").toString()) + ") ");
			        sbSQL.append("\n         AND bc.id_banco = cb.id_banco ");
			        sbSQL.append("\n         AND bc.id_banco BETWEEN " + funciones.convertirCadenaInteger(datos.get("bancoInf").toString()) + " AND " + funciones.convertirCadenaInteger(datos.get("bancoSup").toString()));
			        sbSQL.append("\n         AND bc.id_chequera LIKE ('" + funciones.validarCadena(datos.get("chequera").toString()) + "') ");
			        
			        if(funciones.validarCadena(datos.get("estatusMov").toString()).equals("'X'") || funciones.validarCadena(datos.get("estatusMov").toString()).equals("'I', 'P', 'R', 'X', 'J', 'V'"))
			        {
			            sbSQL.append("\n         AND ((bc.id_estatus IN ('C')");
			            sbSQL.append("\n              AND bc.id_divisa = '" + funciones.validarCadena(datos.get("divisa").toString()) + "')");
			            sbSQL.append("\n              OR (bc.id_estatus IN ('O'))");
			        }
			        else
			        {
			            sbSQL.append("\n         AND bc.id_divisa = '" + funciones.validarCadena(datos.get("divisa").toString()) + "' ");
			            sbSQL.append("\n         AND ((bc.id_estatus IN ('C')) ");
			        }
			        
			        sbSQL.append("\n              OR (bc.no_cheque IN (SELECT DISTINCT bch.no_cheque_ant ");
			        sbSQL.append("\n                                   FROM bitacora_cheques bch ");
			        sbSQL.append("\n                                   WHERE bc.no_empresa = bch.no_empresa ");
			        sbSQL.append("\n                                         AND bc.id_banco = bch.id_banco ");
			        sbSQL.append("\n                                         AND bc.id_chequera = bch.id_chequera ");
			        sbSQL.append("\n                                         AND bc.no_docto = bch.no_docto ");
			        sbSQL.append("\n                                         AND bc.no_cheque = bch.no_cheque_ant)))");
			       
			        sbSQL.append("\n         AND bc.fecha_bitacora BETWEEN CONVERT(DATETIME,'" + funciones.ponerFechaSola(datos.get("fechaIni").toString()) + "',103) and CONVERT(DATETIME,'" + funciones.ponerFechaSola(datos.get("fechaFin").toString()) + "',103) ");
			        
			        if(funciones.validarCadena(datos.get("estatusMov").toString()).equals("'I', 'P', 'R', 'X', 'J', 'V'")
			        		||funciones.validarCadena(datos.get("estatusMov").toString()).equals("'I', 'R', 'X'")){
			        	//sbSQL.append("\n ) m ");
			        }
			        
			        if(!funciones.validarCadena(datos.get("cheIni").toString()).equals("") &&
							!funciones.validarCadena(datos.get("cheFin").toString()).equals("")) {
							 
		        		sbSQL.append("\n	And bc.no_cheque between "+ Integer.parseInt(datos.get("cheIni").toString()) +" and "+ Integer.parseInt(datos.get("cheFin").toString()) +"");
					}
			}
			/*Para ordenar*/
			if(tabla.equals("ordenar1")){
				if(datos.get("agrupado").equals("BANCO")){
				    if(datos.get("ordenado").equals("MONTO"))
				            sbSQL.append("\n order by m.no_empresa,m.id_banco,m.id_chequera,m.importe ");
				    else if(datos.get("ordenado").equals("NO_CHEQUE"))
				            sbSQL.append("\n order by m.no_empresa,m.id_banco,m.id_chequera,m.no_cheque ");
				    else if(datos.get("ordenado").equals("FECHA"))
			            sbSQL.append("\n order by m.no_empresa,m.id_banco,m.id_chequera,m.fecha_cheque ");
				 } else if(datos.get("agrupado").equals("CAJA")){
				    if(datos.get("ordenado").equals("MONTO"))
				            sbSQL.append("\n order by m.no_empresa,m.importe,m.id_banco,m.id_chequera ");
				    else if(datos.get("ordenado").equals("NO_CHEQUE"))
				            sbSQL.append("\n order by m.no_empresa,m.no_cheque,m.id_banco,m.id_chequera ");
				    else if(datos.get("ordenado").equals("FECHA"))
			            sbSQL.append("\n order by m.no_empresa,m.fecha_cheque,m.id_banco,m.id_chequera ");
				 }
			}
			if(tabla.equals("ordenar2")){
				if(datos.get("agrupado").equals("BANCO")) {
					 if(datos.get("ordenado").equals("MONTO"))
				            sbSQL.append("\n order by bc.id_banco,bc.id_chequera,importe ");
				    else if(datos.get("ordenado").equals("NO_CHEQUE"))
				            sbSQL.append("\n order by bc.id_banco,bc.id_chequera,no_cheque ");
				    else if(datos.get("ordenado").equals("FECHA"))
				            sbSQL.append("\n order by bc.id_banco,bc.id_chequera,fecha_cheque ");
				 } else if(datos.get("agrupado").equals("CAJA")){
				    if(datos.get("ordenado").equals("MONTO"))
				            sbSQL.append("\n order by bc.no_empresa,bc.importe,bc.id_banco,bc.id_chequera ");
				    else if(datos.get("ordenado").equals("NO_CHEQUE"))
				            sbSQL.append("\n order by bc.no_empresa,bc.no_cheque,bc.id_banco,bc.id_chequera ");
				    else if(datos.get("ordenado").equals("FECHA"))
			            sbSQL.append("\n order by bc.no_empresa,bc.fecha_cheque,bc.id_banco,bc.id_chequera ");
				 }
			}
		}catch(Exception e){
			
		}return sbSQL.toString();
	}
	
	public List<Map<String,Object>> consultarReporteCheques(Map<String, Object> datos){
		List<Map<String, Object>> resultado = null;
		StringBuffer sbSQL = new StringBuffer();
		boolean ordena = true;
		try{
			globalSingleton = GlobalSingleton.getInstancia();
			datos.put("formaPago", 1);
			datos.put("fechaHoy", globalSingleton.getFechaHoy());
			String estatusMov=funciones.validarCadena(datos.get("estatusMov").toString());
			Date fechaIni= funciones.ponerFechaDate(datos.get("fechaIni").toString());
			Date fechaHoy=funciones.ponerFechaDate(funciones.ponerFechaSola((globalSingleton.getFechaHoy())));
			if(!estatusMov.equals("X")){
		        sbSQL.append("\n select * from (");
		        sbSQL.append(armarQueryReporteCheques(datos, "movimiento"));
		        /**>> HISTORICO >>>**/
		        if(fechaIni.compareTo(fechaHoy) < 0){
		            sbSQL.append("\n UNION ");
		            sbSQL.append(armarQueryReporteCheques(datos, "hist_movimiento"));   
		        }
		        sbSQL.append("\n ) m");////lllll
		    }
		    /**>>>>>> CANCELADOS >>>>>>>>>>>>>>**/
			 if(estatusMov.equals("'X'")|| estatusMov.equals("'I', 'P', 'R', 'X', 'J', 'V'")|| estatusMov.equals("'I', 'R', 'X'")){
				 if(estatusMov.equals("'X'")){
		            sbSQL.delete(0, sbSQL.length());
		            ordena = false;
				 } else{
		            sbSQL.append("\n UNION ");
		           
				 }
				 sbSQL.append(armarQueryReporteCheques(datos, "bitacora_cheques"));  
				
			 }
			
			 
			 //sbSQL.append("\n order by m.id_banco ");
			 if(ordena){
				 sbSQL.append(armarQueryReporteCheques(datos, "ordenar1")); 
			 }else{
				 sbSQL.append(armarQueryReporteCheques(datos, "ordenar2")); 
			 }
		   
		    logger.info(sbSQL.toString());
		    
		    System.out.println("query cheques consultarReporteCheques \n" + sbSQL +"\n Fin consultarReporteCheques");
		    
		    
		    resultado = (List<Map<String, Object>>)jdbcTemplate.query(sbSQL.toString(), new RowMapper(){
				public Object mapRow(ResultSet rs, int i)
					throws SQLException {
			            Map<String, Object> results = new HashMap<String, Object>();
			            results.put("user_al", rs.getString("user_al"));
			            results.put("user_mod", funciones.validarCadena(rs.getString("user_mod")));
			            results.put("importe", funciones.validarCadena(rs.getString("importe")));
			            results.put("beneficiario", funciones.validarCadena(rs.getString("beneficiario")));
			            results.put("concepto", funciones.validarCadena(rs.getString("concepto")));
			            results.put("id_banco", funciones.validarCadena(rs.getString("id_banco")));
			            results.put("desc_banco", funciones.validarCadena(rs.getString("desc_banco")));
			            results.put("id_chequera", funciones.validarCadena(rs.getString("id_chequera")));
			            results.put("no_docto", funciones.validarCadena(rs.getString("no_docto")));
			            results.put("no_empresa", funciones.validarCadena(rs.getString("no_empresa")));
			            results.put("nom_empresa", funciones.validarCadena(rs.getString("nom_empresa")));
			            results.put("id_caja", funciones.validarCadena(rs.getString("id_caja")));
			            results.put("desc_caja", funciones.validarCadena(rs.getString("desc_caja")));
			            results.put("fecha", funciones.validarCadena(rs.getString("fecha")));
			            results.put("no_cheque", funciones.validarCadena(rs.getString("no_cheque")));
			            results.put("fecha_impresion", funciones.validarCadena(rs.getString("fecha_impresion")));
			            results.put("fecha_cheque", funciones.validarCadena(rs.getString("fecha_cheque")));
			            results.put("fec_valor", funciones.validarCadena(rs.getString("fec_valor")));
			            results.put("fec_entregado", funciones.validarCadena(rs.getString("fec_entregado")));
			            results.put("no_folio_det", funciones.validarCadena(rs.getString("no_folio_det")));
			            //results.put("id_estatus_mov", funciones.validarCadena(rs.getString("id_estatus_mov")));
			            results.put("estatus", funciones.validarCadena(rs.getString("estatus")));
			            results.put("tipo_cheque", funciones.validarCadena(rs.getString("tipo_cheque")));
			            return results;
					}
				});
		    
		}catch(Exception e){
			e.printStackTrace();
			System.out.println("---------------------------------- \n ----------------------------------\n");
			System.out.println("Error en consultarReporteCheques \n ");
			System.out.println("---------------------------------- \n ----------------------------------\n");			
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Reportes, C:ReportesDaoImpl, M:consultarReporteCheques");
		}return resultado;
	}
	
	
	
	/*public List<Map<String,Object>> consultarReporteCheques(Map<String, Object> datos){
		List<Map<String, Object>> resultado = null;
		StringBuffer sbSQL = new StringBuffer();
		boolean ordena = true;
		
		try{
			//pvsEstatus_Mov = Replace(pvsEstatus, ", 'X'", "")
			globalSingleton = GlobalSingleton.getInstancia();
			datos.put("formaPago", 1);
			datos.put("fechaHoy", globalSingleton.getFechaHoy());
			
			System.out.println(funciones.validarCadena(datos.get("estatusMov").toString()));
			//System.out.println(funciones.validarCadena(datos.get("estatusCb").toString()));
		    
			if(!funciones.validarCadena(datos.get("estatusMov").toString()).equals("X"))
			{
		        sbSQL.append("\n select * from (SELECT ");
		        sbSQL.append("\n        m.usuario_alta AS user_al, m.usuario_modif AS user_mod, ");
		        sbSQL.append("\n        m.importe, m.beneficiario, m.concepto, ");
		        sbSQL.append("\n        m.id_banco, cb.desc_banco, m.id_chequera, m.no_docto, ");
		        sbSQL.append("\n        m.no_empresa, nom_empresa, m.id_caja, cc.desc_caja, ");
		        sbSQL.append("\n         case ");
		        sbSQL.append("\n                    when m.id_estatus_mov = 'I' then to_char( m.fec_imprime,'DD/MM/YYYY') ");
		        sbSQL.append("\n                    when m.id_estatus_mov = 'R' then to_char( m.fec_reimprime,'DD/MM/YYYY') ");
		        sbSQL.append("\n                    when m.id_estatus_mov in ('J','P','V') then to_char( m.fec_valor,'DD/MM/YYYY') ");
		        sbSQL.append("\n                end as fecha, ");
		        sbSQL.append("\n        m.no_cheque , m.fec_imprime AS fecha_impresion, to_char( m.fec_cheque,'DD/MM/YYYY') AS fecha_cheque, ");
		        sbSQL.append("\n        m.fec_valor AS fec_valor, m.fec_entregado AS fec_entregado, ");
		        sbSQL.append("\n        m.no_folio_det, m.id_estatus_mov, ");
		        sbSQL.append("\n        case    when m.id_estatus_mov in ('I', 'R') and b_entregado = 'S' then 'ENTREGADO' ");
		        sbSQL.append("\n                when m.id_estatus_mov = 'I' and (b_entregado in ('N', '') or b_entregado is null) then 'IMPRESO'");
		        sbSQL.append("\n                when m.id_estatus_mov = 'R' and (b_entregado in ('N', '') or b_entregado is null) then 'REIMPRESO'");
		        sbSQL.append("\n                when m.id_estatus_mov in ('P','J','V')  then 'POR IMPRIMIR' ");
		        sbSQL.append("\n        end as estatus, ");
		        sbSQL.append("\n        case    when id_forma_pago in (1,7) then 'CHEQUE' ");
		        sbSQL.append("\n        end as tipo_cheque");
		        sbSQL.append("\n FROM   movimiento m ");
		        sbSQL.append("\n        LEFT JOIN cat_caja cc ON( m.id_caja = cc.id_caja ), ");
		        sbSQL.append("\n        cat_banco cb, empresa e ");
		        sbSQL.append("\n WHERE  m.no_empresa = e.no_empresa ");
		        sbSQL.append("\n        and m.no_empresa in (" + funciones.validarCadena(datos.get("empresas").toString()) + ") ");
		        sbSQL.append("\n        and cc.id_caja in (" + funciones.validarCadena(datos.get("cajas").toString()) + ") ");
		        sbSQL.append("\n        and m.id_divisa = '" + funciones.validarCadena(datos.get("divisa").toString()) + "' ");
		        sbSQL.append("\n        and m.id_banco = cb.id_banco ");
		        sbSQL.append("\n        and m.id_banco between " + funciones.convertirCadenaInteger(datos.get("bancoInf").toString()) + " and  " + funciones.convertirCadenaInteger(datos.get("bancoSup").toString()));
		        sbSQL.append("\n        and m.id_chequera like ('" + funciones.validarCadena(datos.get("chequera").toString()) + "') ");
		        sbSQL.append("\n        and m.id_tipo_movto = 'E' ");
		        sbSQL.append("\n        and m.id_forma_pago in (" + funciones.convertirCadenaInteger(datos.get("formaPago").toString()) + ") ");
		        sbSQL.append("\n        and m.id_estatus_mov in (" + funciones.validarCadena(datos.get("estatusMov").toString()) + ") ");
		        
		        if(!funciones.validarCadena(datos.get("origen").toString()).equals(""))
		        {
		        	if(datos.get("origen").equals("CXP"))
		                sbSQL.append("\n        and (m.origen_mov = '' or m.origen_mov is null) ");
		            else
		                sbSQL.append("\n        and m.origen_mov = '" + datos.get("origen") + "' ");
		        }
		        
		        if(funciones.validarCadena(datos.get("estatusMov").toString()).equals("'J', 'P', 'V'"))
		        {
	                sbSQL.append("\n        and m.fec_valor between TO_DATE('" + funciones.ponerFechaSola(datos.get("fechaIni").toString()) + "','DD/MM/YYYY') and TO_DATE('" + funciones.ponerFechaSola(datos.get("fechaFin").toString()) + "', 'DD/MM/YYYY') ");
//	                sbSQL.append(" ) m");
	                
		        }
		        else if(funciones.validarCadena(datos.get("estatusMov").toString()).equals("'I', 'P', 'R', 'X', 'J', 'V'"))
		        {
	                sbSQL.append("\n        and (case ");
	                sbSQL.append("\n                when m.id_estatus_mov = 'I' then m.fec_imprime ");
	                sbSQL.append("\n                when m.id_estatus_mov = 'R' then m.fec_reimprime ");
	                sbSQL.append("\n                when m.id_estatus_mov in ('J','P','V') then m.fec_valor");
	                sbSQL.append("\n             end) between TO_DATE('" + funciones.ponerFechaSola(datos.get("fechaIni").toString()) + "' ,'DD/MM/YYYY') and TO_DATE('" + funciones.ponerFechaSola(datos.get("fechaFin").toString()) + "', 'DD/MM/YYYY') ");
		        }
		        else if(funciones.validarCadena(datos.get("estatusMov").toString()).equals("'I', 'R'"))
                {
		        	if(funciones.validarCadena(datos.get("estatusEntregado").toString()).equals("S"))
		        	{
	                    sbSQL.append("\n        and m.fec_entregado between TO_DATE('" + funciones.ponerFechaSola(datos.get("fechaIni").toString()) + "','DD/MM/YYYY') and TO_DATE('" + funciones.ponerFechaSola(datos.get("fechaFin").toString()) + "', 'DD/MM/YYYY') ");
	                    sbSQL.append("\n        and b_entregado = 'S'");

		        	}
		        	else if(funciones.validarCadena(datos.get("estatusEntregado").toString()).equals("N"))
		        	{
	                    sbSQL.append("\n        and (case ");
	                    sbSQL.append("\n                when m.id_estatus_mov = 'I' then m.fec_imprime ");
	                    sbSQL.append("\n                when m.id_estatus_mov = 'R' then m.fec_reimprime ");
	                   // TO_DATE('13/01/2014','DD/MM/YYYY') AND TO_DATE('17/12/2015', 'DD/MM/YYYY')
	                    sbSQL.append("\n             end) between TO_DATE('" + funciones.ponerFechaSola(datos.get("fechaIni").toString()) + "', 'DD/MM/YYYY') and TO_DATE('" + funciones.ponerFechaSola(datos.get("fechaFin").toString()) + "', 'DD/MM/YYYY') ");
	                    sbSQL.append("\n        and (m.b_entregado is null or m.b_entregado = '' or m.b_entregado = 'N')");
		        	}
                }
		        else if(funciones.validarCadena(datos.get("estatusMov").toString()).equals("'R'"))
                {
	                sbSQL.append("\n        and m.fec_reimprime between TO_DATE('" + funciones.ponerFechaSola(datos.get("fechaIni").toString()) + "','DD/MM/YYYY') and TO_DATE('" + funciones.ponerFechaSola(datos.get("fechaFin").toString()) + "', 'DD/MM/YYYY') ");
                }
		        else if(funciones.validarCadena(datos.get("estatusMov").toString()).equals("'I', 'R', 'X'"))
                {
	                sbSQL.append("\n        and (case ");
	                sbSQL.append("\n                when m.id_estatus_mov = 'I' then m.fec_imprime ");
	                sbSQL.append("\n                when m.id_estatus_mov = 'R' then m.fec_reimprime ");
	                sbSQL.append("\n             end) between TO_DATE('" + funciones.ponerFechaSola(datos.get("fechaIni").toString()) + "','DD/MM/YYYY') and TO_DATE('" + funciones.ponerFechaSola(datos.get("fechaFin").toString()) + "', 'DD/MM/YYYY') ");
                }
		        
		        if(funciones.validarCadena(datos.get("estatusCb").toString()).equals("P"))
		            sbSQL.append("\n        and id_estatus_cb in ('P', 'N') ");
		        
//		        sbSQL.append("\n        AND (((select count(*) from usuario_area WHERE no_usuario = " + datos.get("usuario") + ") > 0) or ");//modificado de = 0 a >0
//		        sbSQL.append("\n             (id_area in (select id_area from usuario_area where no_usuario = " + datos.get("usuario") + ")))");
		        
		        if(!funciones.validarCadena(datos.get("cheIni").toString()).equals("") &&
						!funciones.validarCadena(datos.get("cheFin").toString()).equals("")) {
						 
		        		sbSQL.append("\n	And m.no_cheque between "+ Integer.parseInt(datos.get("cheIni").toString()) +" and "+ Integer.parseInt(datos.get("cheFin").toString()) +"");
					 }
		    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> HISTORICO >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
		        if(funciones.ponerFechaDate(datos.get("fechaIni").toString()).compareTo(funciones.ponerFechaDate(funciones.ponerFechaSola((globalSingleton.getFechaHoy())))) < 0)
		       {
		            sbSQL.append("\n UNION ");
		            sbSQL.append("\n SELECT ");
		            sbSQL.append("\n        m.usuario_alta AS user_al, m.usuario_modif AS user_mod, ");
		            sbSQL.append("\n        m.importe, m.beneficiario, m.concepto, ");
		            sbSQL.append("\n        m.id_banco, cb.desc_banco, m.id_chequera, m.no_docto, ");
		            sbSQL.append("\n        m.no_empresa, nom_empresa, m.id_caja, cc.desc_caja, ");
		            sbSQL.append("\n        case ");
		            sbSQL.append("\n                    when m.id_estatus_mov = 'I' then to_char( m.fec_imprime,'DD/MM/YYYY') ");
		            sbSQL.append("\n                    when m.id_estatus_mov = 'R' then to_char( m.fec_reimprime,'DD/MM/YYYY') ");
		            sbSQL.append("\n                    when m.id_estatus_mov in ('J','P','V') then to_char( m.fec_valor,'DD/MM/YYYY') ");
		            sbSQL.append("\n                end as fecha , ");
		            sbSQL.append("\n        m.no_cheque , m.fec_imprime AS fecha_impresion, to_char( m.fec_cheque,'DD/MM/YYYY') AS fecha_cheque, ");
		            sbSQL.append("\n        m.fec_valor AS fec_valor, m.fec_entregado AS fec_entregado, ");
		            sbSQL.append("\n        m.no_folio_det, m.id_estatus_mov, ");
		            sbSQL.append("\n        case    when m.id_estatus_mov in ('I', 'R') and b_entregado = 'S' then 'ENTREGADO' ");
		            sbSQL.append("\n                when m.id_estatus_mov = 'I' and (b_entregado in ('N', '') or b_entregado = '') then 'IMPRESO'"); //'''b_entregado is NULL) then 'IMPRESO' "
		            sbSQL.append("\n                when m.id_estatus_mov = 'R' and (b_entregado in ('N', '') or b_entregado = '') then 'REIMPRESO'"); //'''b_entregado is NULL) then 'REIMPRESO'  "
		            sbSQL.append("\n                when m.id_estatus_mov in ('P','J','V')  then 'POR IMPRIMIR' ");
		            sbSQL.append("\n        end as estatus, ");
		            sbSQL.append("\n        case    when id_forma_pago in (1,7) then 'CHEQUE' ");
		            sbSQL.append("\n        end as tipo_cheque");
		            sbSQL.append("\n FROM   hist_movimiento m ");
		            sbSQL.append("\n        LEFT JOIN cat_caja cc ON( m.id_caja = cc.id_caja ), ");
		            sbSQL.append("\n        cat_banco cb, empresa e ");
		            sbSQL.append("\n WHERE  m.no_empresa = e.no_empresa ");
		            sbSQL.append("\n        and m.no_empresa in (" + funciones.validarCadena(datos.get("empresas").toString()) + ") ");
		            sbSQL.append("\n        and cc.id_caja in (" + funciones.validarCadena(datos.get("cajas").toString()) + ") ");
		            sbSQL.append("\n        and m.id_divisa = '" + funciones.validarCadena(datos.get("divisa").toString()) + "' ");
		            sbSQL.append("\n        and m.id_banco = cb.id_banco ");
		            sbSQL.append("\n        and m.id_banco between " + funciones.convertirCadenaInteger(datos.get("bancoInf").toString()) + " and  " + funciones.convertirCadenaInteger(datos.get("bancoSup").toString()));
		            sbSQL.append("\n        and m.id_chequera like ('" + funciones.validarCadena(datos.get("chequera").toString()) + "') ");
		            sbSQL.append("\n        and m.id_tipo_movto = 'E' ");
		            sbSQL.append("\n        and m.id_forma_pago in (" + funciones.convertirCadenaInteger(datos.get("formaPago").toString()) + ") ");
		            sbSQL.append("\n        and m.id_estatus_mov in (" + funciones.validarCadena(datos.get("estatusMov").toString()) + ") ");
		            
		            if(!funciones.validarCadena(datos.get("origen").toString()).equals(""))
			        {
			        	if(datos.get("origen").equals("CXP"))
			                sbSQL.append("\n        and (m.origen_mov = '' or m.origen_mov is null) ");
			            else
			                sbSQL.append("\n        and m.origen_mov = '" + datos.get("origen") + "' ");
			        }
		            
		            if(funciones.validarCadena(datos.get("estatusMov").toString()).equals("'J', 'P', 'V'"))
		            {
		                sbSQL.append("\n        and m.fec_valor between TO_DATE('" + funciones.ponerFechaSola(datos.get("fechaIni").toString()) + "','DD/MM/YYYY') and TO_DATE('" + funciones.ponerFechaSola(datos.get("fechaFin").toString()) + "','DD/MM/YYYY') ");
		                sbSQL.append(" ) m");
		            }
		            
		            else if(funciones.validarCadena(datos.get("estatusMov").toString()).equals("'I', 'P', 'R', 'X', 'J', 'V'"))
		            {
		                sbSQL.append("\n        and (case ");
		                sbSQL.append("\n                when m.id_estatus_mov = 'I' then m.fec_imprime ");
		                sbSQL.append("\n                when m.id_estatus_mov = 'R' then m.fec_reimprime ");
		                sbSQL.append("\n                when m.id_estatus_mov in ('J','P','V') then m.fec_valor");
		                sbSQL.append("\n             end) between TO_DATE('" + funciones.ponerFechaSola(datos.get("fechaIni").toString()) + "','DD/MM/YYYY') and TO_DATE('" + funciones.ponerFechaSola(datos.get("fechaFin").toString()) + "','DD/MM/YYYY') ");
		            }
		            
		            else if(funciones.validarCadena(datos.get("estatusMov").toString()).equals("'I', 'R'"))
		            {
		            	if(funciones.validarCadena(datos.get("estatusEntregado").toString()).equals("S"))
		            	{
		                    sbSQL.append("\n        and m.fec_entregado between TO_DATE('" + funciones.ponerFechaSola(datos.get("fechaIni").toString()) + "','DD/MM/YYYY') and TO_DATE('" + funciones.ponerFechaSola(datos.get("fechaFin").toString()) + "','DD/MM/YYYY') ");
		                    sbSQL.append("\n        and b_entregado = 'S'");
//		                    sbSQL.append(")m");
		            	}
		            	else if(funciones.validarCadena(datos.get("estatusEntregado").toString()).equals("N"))
		            	{
		                    sbSQL.append("\n        and (case ");
		                    sbSQL.append("\n                when m.id_estatus_mov = 'I' then m.fec_imprime ");
		                    sbSQL.append("\n                when m.id_estatus_mov = 'R' then m.fec_reimprime ");
		                    sbSQL.append("\n             end) between TO_DATE('" + funciones.ponerFechaSola(datos.get("fechaIni").toString()) + "','DD/MM/YYYY') and TO_DATE('" + funciones.ponerFechaSola(datos.get("fechaFin").toString()) + "','DD/MM/YYYY') ");
		                    sbSQL.append("\n        and (m.b_entregado = NULL or m.b_entregado = '' or m.b_entregado = 'N')");
		                    if(!funciones.validarCadena(datos.get("estatusMov").toString()).equals("'I', 'R'")){
		                    	//sbSQL.append(")m ");
		                    }
		                    
		            	}
		            }
		            
		            else if(funciones.validarCadena(datos.get("estatusMov").toString()).equals("'R'"))
		            {
		                sbSQL.append("\n        and m.fec_reimprime between TO_DATE('" + funciones.ponerFechaSola(datos.get("fechaIni").toString()) + "','DD/MM/YYYY') and TO_DATE('" + funciones.ponerFechaSola(datos.get("fechaFin").toString()) + "','DD/MM/YYYY') ");
		                //sbSQL.append(" ) m");
		            }
		            
		            
		            else if(funciones.validarCadena(datos.get("estatusMov").toString()).equals("'I', 'R', 'X'"))
		            {
		                sbSQL.append("\n        and (case ");
		                sbSQL.append("\n                when m.id_estatus_mov = 'I' then m.fec_imprime ");
		                sbSQL.append("\n                when m.id_estatus_mov = 'R' then m.fec_reimprime ");
		                sbSQL.append("\n             end) between TO_DATE('" + funciones.ponerFechaSola(datos.get("fechaIni").toString()) + "','DD/MM/YYYY') and TO_DATE('" + funciones.ponerFechaSola(datos.get("fechaFin").toString()) + "','DD/MM/YYYY') ");
		            }
		            
		            
		            
		            if(funciones.validarCadena(datos.get("estatusCb").toString()).equals("P"))
		                sbSQL.append("\n        and id_estatus_cb in ('P', 'N') ");
		            
		            if(funciones.validarCadena(datos.get("estatusMov").toString()).equals("'I', 'R'")
		            		|| funciones.validarCadena(datos.get("estatusMov").toString()).equals(" 'R'")){
		            	
		            	 if(funciones.validarCadena(datos.get("cheIni").toString()).equals("") &&
									funciones.validarCadena(datos.get("cheFin").toString()).equals("")) {
		      
		            		 			//sbSQL.append(" ) m");
		            	 }
		            }
		            
//		            sbSQL.append("\n        AND (((select count(*) from usuario_area WHERE no_usuario = " + datos.get("usuario") + ") > 0) or ");//modificado de = 0 a >0
//		            sbSQL.append("\n             (id_area in (select id_area from usuario_area where no_usuario = " + datos.get("usuario") + ")))");
		            
		            if(!funciones.validarCadena(datos.get("cheIni").toString()).equals("") &&
							!funciones.validarCadena(datos.get("cheFin").toString()).equals("")) {
							 
		        		sbSQL.append("\n	And m.no_cheque between "+ Integer.parseInt(datos.get("cheIni").toString()) +" and "+ Integer.parseInt(datos.get("cheFin").toString()) +"");
		        		//sbSQL.append(" ) m "); 
		        		
					}
		       }
			}
		
		    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> CANCELADOS >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>//
			 if(funciones.validarCadena(datos.get("estatusMov").toString()).equals("'X'")
					 || funciones.validarCadena(datos.get("estatusMov").toString()).equals("'I', 'P', 'R', 'X', 'J', 'V'")
				 	 || funciones.validarCadena(datos.get("estatusMov").toString()).equals("'I', 'R', 'X'"))
			 {
				 if(funciones.validarCadena(datos.get("estatusMov").toString()).equals("'X'"))
				 {
		            sbSQL = new StringBuffer();
		            ordena = false;
				 }
				 else
				 {
		            sbSQL.append("\n UNION ");
				 }
		        
		        sbSQL.append("\n SELECT  DISTINCT bc.usuario AS user_al, bc.usuario AS user_mod, bc.importe, bc.beneficiario, ");
		        sbSQL.append("\n         bc.concepto, bc.id_banco, cb.desc_banco, bc.id_chequera, bc.no_docto, bc.no_empresa, ");
		        sbSQL.append("\n         e.nom_empresa, bc.id_caja, cc.desc_caja, ");
		        
		        if(ConstantesSet.gsDBM.equals("DB2"))
		            sbSQL.append("\n         cast(substr(cast(bc.fecha_bitacora as varchar(15)),1,11) as date) AS fecha, ");
		        else
		            sbSQL.append("\n         to_char( bc.fecha_bitacora,'DD/MM/YYYY') AS fecha,");
		        
		        sbSQL.append("\n         bc.no_cheque, bc.fec_impresion AS fecha_impresion, ");
		        sbSQL.append("\n         to_char( bc.fec_cheque,'DD/MM/YYYY') AS fecha_cheque, bc.fec_impresion AS fec_valor, ");
		        sbSQL.append("\n         bc.fecha_bitacora AS fec_entregado, bc.no_folio_det, bc.id_estatus, ");
		        sbSQL.append("\n         case    when bc.id_estatus in ('C','I','M','N','P','R') then 'CANCELADO' ");
		        sbSQL.append("\n                 when bc.id_estatus in ('O') then 'OMITIDO' ");
		        sbSQL.append("\n         end as estatus, ");
		        sbSQL.append("\n         'CHEQUE' AS tipo_cheque ");
		        sbSQL.append("\n FROM    bitacora_cheques bc ");
		
		        sbSQL.append("\n         LEFT JOIN cat_caja cc ON (bc.id_caja=cc.id_caja), ");
		        sbSQL.append("\n         cat_banco cb, empresa e ");
		        sbSQL.append("\n WHERE   bc.no_empresa = e.no_empresa ");
		        sbSQL.append("\n         AND bc.no_empresa IN (" + funciones.validarCadena(datos.get("empresas").toString()) + ") ");
		        sbSQL.append("\n         AND cc.id_caja in (" + funciones.validarCadena(datos.get("cajas").toString()) + ") ");
		        sbSQL.append("\n         AND bc.id_banco = cb.id_banco ");
		        sbSQL.append("\n         AND bc.id_banco BETWEEN " + funciones.convertirCadenaInteger(datos.get("bancoInf").toString()) + " AND " + funciones.convertirCadenaInteger(datos.get("bancoSup").toString()));
		        sbSQL.append("\n         AND bc.id_chequera LIKE ('" + funciones.validarCadena(datos.get("chequera").toString()) + "') ");
		        
		        if(funciones.validarCadena(datos.get("estatusMov").toString()).equals("'X'") || funciones.validarCadena(datos.get("estatusMov").toString()).equals("'I', 'P', 'R', 'X', 'J', 'V'"))
		        {
		            sbSQL.append("\n         AND ((bc.id_estatus IN ('C')");
		            sbSQL.append("\n              AND bc.id_divisa = '" + funciones.validarCadena(datos.get("divisa").toString()) + "')");
		            sbSQL.append("\n              OR (bc.id_estatus IN ('O'))");
		        }
		        else
		        {
		            sbSQL.append("\n         AND bc.id_divisa = '" + funciones.validarCadena(datos.get("divisa").toString()) + "' ");
		            sbSQL.append("\n         AND ((bc.id_estatus IN ('C')) ");
		        }
		        
		        sbSQL.append("\n              OR (bc.no_cheque IN (SELECT DISTINCT bch.no_cheque_ant ");
		        sbSQL.append("\n                                   FROM bitacora_cheques bch ");
		        sbSQL.append("\n                                   WHERE bc.no_empresa = bch.no_empresa ");
		        sbSQL.append("\n                                         AND bc.id_banco = bch.id_banco ");
		        sbSQL.append("\n                                         AND bc.id_chequera = bch.id_chequera ");
		        sbSQL.append("\n                                         AND bc.no_docto = bch.no_docto ");
		        sbSQL.append("\n                                         AND bc.no_cheque = bch.no_cheque_ant)))");
		       
		        sbSQL.append("\n         AND bc.fecha_bitacora BETWEEN TO_DATE('" + funciones.ponerFechaSola(datos.get("fechaIni").toString()) + "','DD/MM/YYYY') and TO_DATE('" + funciones.ponerFechaSola(datos.get("fechaFin").toString()) + "','DD/MM/YYYY') ");
		        
		        if(funciones.validarCadena(datos.get("estatusMov").toString()).equals("'I', 'P', 'R', 'X', 'J', 'V'")
		        		||funciones.validarCadena(datos.get("estatusMov").toString()).equals("'I', 'R', 'X'")){
		        	//sbSQL.append("\n ) m ");
		        }
		        
		        if(!funciones.validarCadena(datos.get("cheIni").toString()).equals("") &&
						!funciones.validarCadena(datos.get("cheFin").toString()).equals("")) {
						 
	        		sbSQL.append("\n	And bc.no_cheque between "+ Integer.parseInt(datos.get("cheIni").toString()) +" and "+ Integer.parseInt(datos.get("cheFin").toString()) +"");
				}
			 }
			 
			 
			 sbSQL.append("\n ) m ");
			 //sbSQL.append("\n order by m.id_banco ");
			 if(ordena)
			 {
				 if(datos.get("agrupado").equals("BANCO"))
				 {
				    if(datos.get("ordenado").equals("MONTO"))
				            sbSQL.append("\n order by m.no_empresa,m.id_banco,m.id_chequera,m.importe ");
				    else if(datos.get("ordenado").equals("NO_CHEQUE"))
				            sbSQL.append("\n order by m.no_empresa,m.id_banco,m.id_chequera,m.no_cheque ");
				    else if(datos.get("ordenado").equals("FECHA"))
			            sbSQL.append("\n order by m.no_empresa,m.id_banco,m.id_chequera,m.fecha_cheque ");
				 }
				 else if(datos.get("agrupado").equals("CAJA"))
				 {
				    if(datos.get("ordenado").equals("MONTO"))
				            sbSQL.append("\n order by m.no_empresa,m.importe,m.id_banco,m.id_chequera ");
				    else if(datos.get("ordenado").equals("NO_CHEQUE"))
				            sbSQL.append("\n order by m.no_empresa,m.no_cheque,m.id_banco,m.id_chequera ");
				    else if(datos.get("ordenado").equals("FECHA"))
			            sbSQL.append("\n order by m.no_empresa,m.fecha_cheque,m.id_banco,m.id_chequera ");
				 }
			 }
			 else
			 {
				 if(datos.get("agrupado").equals("BANCO"))
				 {
					 if(datos.get("ordenado").equals("MONTO"))
				            sbSQL.append("\n order by bc.id_banco,bc.id_chequera,importe ");
				    else if(datos.get("ordenado").equals("NO_CHEQUE"))
				            sbSQL.append("\n order by bc.id_banco,bc.id_chequera,no_cheque ");
				    else if(datos.get("ordenado").equals("FECHA"))
				            sbSQL.append("\n order by bc.id_banco,bc.id_chequera,fecha_cheque ");
				 }
				 else if(datos.get("agrupado").equals("CAJA"))
				 {
				    if(datos.get("ordenado").equals("MONTO"))
				            sbSQL.append("\n order by bc.no_empresa,bc.importe,bc.id_banco,bc.id_chequera ");
				    else if(datos.get("ordenado").equals("NO_CHEQUE"))
				            sbSQL.append("\n order by bc.no_empresa,bc.no_cheque,bc.id_banco,bc.id_chequera ");
				    else if(datos.get("ordenado").equals("FECHA"))
			            sbSQL.append("\n order by bc.no_empresa,bc.fecha_cheque,bc.id_banco,bc.id_chequera ");
				 }
			 }
		   
		    logger.info(sbSQL.toString());
		    
		    System.out.println("query cheques consultarReporteCheques \n" + sbSQL +"\n Fin consultarReporteCheques");
		    
		    
		    resultado = (List<Map<String, Object>>)jdbcTemplate.query(sbSQL.toString(), new RowMapper(){

				public Object mapRow(ResultSet rs, int i)
					throws SQLException {
			            Map<String, Object> results = new HashMap<String, Object>();
			            results.put("user_al", rs.getString("user_al"));
			            results.put("user_mod", funciones.validarCadena(rs.getString("user_mod")));
			            results.put("importe", funciones.validarCadena(rs.getString("importe")));
			            results.put("beneficiario", funciones.validarCadena(rs.getString("beneficiario")));
			            results.put("concepto", funciones.validarCadena(rs.getString("concepto")));
			            results.put("id_banco", funciones.validarCadena(rs.getString("id_banco")));
			            results.put("desc_banco", funciones.validarCadena(rs.getString("desc_banco")));
			            results.put("id_chequera", funciones.validarCadena(rs.getString("id_chequera")));
			            results.put("no_docto", funciones.validarCadena(rs.getString("no_docto")));
			            results.put("no_empresa", funciones.validarCadena(rs.getString("no_empresa")));
			            results.put("nom_empresa", funciones.validarCadena(rs.getString("nom_empresa")));
			            results.put("id_caja", funciones.validarCadena(rs.getString("id_caja")));
			            results.put("desc_caja", funciones.validarCadena(rs.getString("desc_caja")));
			            results.put("fecha", funciones.validarCadena(rs.getString("fecha")));
			            results.put("no_cheque", funciones.validarCadena(rs.getString("no_cheque")));
			            results.put("fecha_impresion", funciones.validarCadena(rs.getString("fecha_impresion")));
			            results.put("fecha_cheque", funciones.validarCadena(rs.getString("fecha_cheque")));
			            results.put("fec_valor", funciones.validarCadena(rs.getString("fec_valor")));
			            results.put("fec_entregado", funciones.validarCadena(rs.getString("fec_entregado")));
			            results.put("no_folio_det", funciones.validarCadena(rs.getString("no_folio_det")));
			            //results.put("id_estatus_mov", funciones.validarCadena(rs.getString("id_estatus_mov")));
			            results.put("estatus", funciones.validarCadena(rs.getString("estatus")));
			            results.put("tipo_cheque", funciones.validarCadena(rs.getString("tipo_cheque")));
			            return results;
					}
				});
		    
		}catch(Exception e){
			e.printStackTrace();
			  System.out.println("---------------------------------- \n ----------------------------------\n");
				System.out.println("Error en consultarReporteCheques \n ");
				System.out.println("---------------------------------- \n ----------------------------------\n");
				
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
					+ "P:Reportes, C:ReportesDaoImpl, M:consultarReporteCheques");
		}
		
		return resultado;
	}*/
	
	public List<Map<String, Object>> buscarDatosReporte(List<Map<String, String>> objParams) {
		StringBuffer sql = new StringBuffer();
		int i;
		globalSingleton = GlobalSingleton.getInstancia();
		List<Map<String, Object>> resultado = null;
		
		try {
			System.out.println("entra al dao impl");
			
			for(i=0; i<1; i++) {
		        sql.append(" SELECT m.usuario_alta, m.usuario_modif, m.no_empresa, m.id_banco, z.desc_banco as nuestro_banco, \n");
		        sql.append(" m.id_chequera_benef, ccb.id_clabe, m.id_forma_pago, m.origen_mov, m.id_divisa, m.id_estatus_mov, e.nom_empresa, \n");
		        sql.append(" m.importe, m.beneficiario, m.concepto, \n");
		        sql.append(" origen_mov, m.id_chequera, fec_conf_trans, \n");
		        sql.append(" cb.desc_banco, m.no_docto, ce.desc_estatus, \n");
		        sql.append(" m.folio_banco , m.no_folio_det, m.lote_entrada  \n"); //, cr.desc_rubro"
		        
		        if(i == 0) {
		            sql.append(" , (select cr.desc_rubro  \n");
		            sql.append("    from cat_rubro cr,  movimiento hmd  \n");
		            sql.append("    where hmd.cve_control = m.cve_control and hmd.id_tipo_operacion = 3201  \n");
		            sql.append("    and cr.id_rubro = hmd.id_rubro and cr.id_grupo = hmd.id_grupo  \n");
		            sql.append("    and m.id_rubro = cr.id_rubro and hmd.no_docto = m.no_docto) as desc_rubro \n");
		            sql.append(" FROM movimiento m, cat_banco cb, cat_estatus ce, cat_banco z, empresa e,ctas_banco ccb \n");// ''', cat_rubro cr \n");
		        }else {
		            sql.append("    , (select cr.desc_rubro  \n");
		            sql.append("    from cat_rubro cr,  hist_mov_det hmd  \n");
		            sql.append("    where hmd.cve_control = m.cve_control and hmd.id_tipo_operacion = 3201  \n");
		            sql.append("    and cr.id_rubro = hmd.id_rubro and cr.id_grupo = hmd.id_grupo  \n");
		            sql.append("    and m.id_rubro = cr.id_rubro and hmd.no_docto = m.no_docto) as desc_rubro \n");
		            sql.append(" FROM hist_movimiento m, cat_banco cb, cat_estatus ce, cat_banco z, empresa e,ctas_banco ccb \n");// ''', cat_rubro cr \n");
		        }
		        sql.append(" WHERE \n");
		        sql.append(" cb.id_banco = m.id_banco_benef \n");
		        sql.append(" and m.id_banco = z.id_banco \n");
		        sql.append(" and m.id_banco_benef = ccb.id_banco  \n");
		        sql.append(" and m.id_chequera_benef = ccb.id_chequera \n");
		        
		        if(gsDBM.equals("SYBASE"))
		            sql.append(" and m.no_cliente = convert(varchar,ccb.no_persona) \n");
		        else if(gsDBM.equals("DB2"))
		            sql.append(" and m.no_cliente = cast(ccb.no_persona as varchar(15)) \n");
		        else
		            sql.append(" and m.no_cliente = ccb.no_persona \n");
		        
		        sql.append(" and m.no_empresa=e.no_empresa \n");
		        sql.append(" and ce.clasificacion = 'MOV' \n");
		        sql.append(" and m.id_tipo_movto = 'E' \n");
		        sql.append(" and id_forma_pago = 3 \n");
		        sql.append(" and ce.id_estatus = m.id_estatus_mov \n");
		        sql.append(" and id_estatus_mov = 'K' \n");
		        
		        if(!objParams.get(0).get("noBanco").equals("")) {
		            sql.append(" and m.id_banco = " + Integer.parseInt(objParams.get(0).get("noBanco").toString()) + " \n");
		            
		            if(objParams.get(0).get("chkSmart").equals("true")) sql.append(" and m.id_chequera in (select id_chequera from cat_cta_banco where pago_mass = 'S') \n");
		        }
		        
		        if(!objParams.get(0).get("noEmpresa").equals("0"))
		            sql.append(" and m.no_empresa = " + Integer.parseInt(objParams.get(0).get("noEmpresa").toString()) + " \n");
		        else {
		            sql.append(" and m.no_empresa  in (select no_empresa  \n");
		            sql.append(" from usuario_empresa where no_usuario = " + globalSingleton.getUsuarioLoginDto().getIdUsuario() + ") \n");
		            /*'adr
					'''la tabla de usuario_area esta vacia
					'''            sql.append(" AND (((select count(*) from usuario_area WHERE no_usuario = " & plNoUsuario & ") = 0) or "
					'''            sql.append("     (id_area in (select id_area from usuario_area where no_usuario = " & plNoUsuario & ")))"
		            'adr*/
		        }
		        /*
		        If psLlamada <> "TODOS" Then
		            If psLlamada = "" Then
		                sql.append(" and (origen_mov = '" & psLlamada & "' or origen_mov is NULL)"
		            Else
		                sql.append(" and origen_mov = '" & psLlamada & "' "
		            End If
		        End If
		        */
		        sql.append(" and m.fec_conf_trans between '" + objParams.get(0).get("fecha_ini") + "' \n");
		        sql.append(" and '" + objParams.get(0).get("fecha_fin") + "' \n");
		        sql.append(" and m.id_divisa like '" + objParams.get(0).get("sDivisa") + "'  \n");
		        sql.append(" and (m.origen_mov <> 'FIL' \n");
		        sql.append(" and m.no_cliente not in (" + consultasGenerales.consultarConfiguraSet(206) + ")) \n");
		        /*'''sql.append(" and m.id_rubro = cr.id_rubro \n");
		        ' verificar el uso de grupo hay que ponerlo en todos los movimientos antes de habilitar esto
		        'sql.append(" and m.id_grupo = cr.id_grupo"
		         */
		        if(i == 0) sql.append(" UNION \n");
			}
	        //' para fil e incidentales
	        sql.append(" UNION \n");
	        
	        for(i=0; i<1; i++) {
		        sql.append(" SELECT \n");
		        sql.append(" m.usuario_alta,m.usuario_modif, \n");
		        sql.append(" m.no_empresa, m.id_banco, z.desc_banco as nuestro_banco, \n");
		        sql.append(" m.id_chequera_benef, \n");
		        sql.append(" m.clabe as id_clabe, \n");
		        sql.append(" m.id_forma_pago , m.origen_mov, \n");
		        sql.append(" m.id_divisa, m.id_estatus_mov, e.nom_empresa, \n");
		        sql.append(" m.importe, m.beneficiario, m.concepto, \n");
		        sql.append(" origen_mov, m.id_chequera, fec_conf_trans, \n");
		        sql.append(" cb.desc_banco, m.no_docto, ce.desc_estatus, \n");
		        sql.append(" m.folio_banco , m.no_folio_det, m.lote_entrada \n");
		        
		        //''', cr.desc_rubro"
		        
		        if(i == 0) {
		            sql.append("    , (select cr.desc_rubro \n");
		            sql.append("    from cat_rubro cr,  movimiento hmd \n");
		            sql.append("    where hmd.cve_control = m.cve_control and hmd.id_tipo_operacion = 3201 \n");
		            sql.append("    and cr.id_rubro = hmd.id_rubro and cr.id_grupo = hmd.id_grupo \n");
		            sql.append("    and m.id_rubro = cr.id_rubro and hmd.no_docto = m.no_docto) as desc_rubro \n");
		            sql.append(" FROM movimiento m, cat_banco cb, cat_estatus ce, cat_banco z, empresa e \n");// ''', cat_rubro cr"
		        }else {
		            sql.append("    , (select cr.desc_rubro \n");
		            sql.append("    from cat_rubro cr,  hist_mov_det hmd \n");
		            sql.append("    where hmd.cve_control = m.cve_control and hmd.id_tipo_operacion = 3201 \n");
		            sql.append("    and cr.id_rubro = hmd.id_rubro and cr.id_grupo = hmd.id_grupo \n");
		            sql.append("    and m.id_rubro = cr.id_rubro and hmd.no_docto = m.no_docto) as desc_rubro \n");
		            sql.append(" FROM hist_movimiento m, cat_banco cb, cat_estatus ce, cat_banco z, empresa e \n");// ''', cat_rubro cr"
		        }
		        sql.append(" WHERE \n");
		        sql.append(" cb.id_banco = m.id_banco_benef \n");
		        sql.append(" and (m.origen_mov = 'FIL' \n");
		        sql.append(" or m.no_cliente in (" + consultasGenerales.consultarConfiguraSet(206) + ")) \n");
		        sql.append(" and m.id_banco = z.id_banco \n");
		        sql.append(" and m.no_empresa=e.no_empresa \n");
		        sql.append(" and ce.clasificacion = 'MOV' \n");
		        sql.append(" and m.id_tipo_movto = 'E' \n");
		        sql.append(" and id_forma_pago = 3 \n");
		        sql.append(" and ce.id_estatus = m.id_estatus_mov \n");
		        sql.append(" and id_estatus_mov = 'K' \n");
		        
		        if(!objParams.get(0).get("noBanco").equals("")) {
		            sql.append(" and m.id_banco = " + Integer.parseInt(objParams.get(0).get("noBanco").toString()) + " \n");
		            
		            if(objParams.get(0).get("chkSmart").equals("true")) sql.append(" and m.id_chequera in (select id_chequera from cat_cta_banco where pago_mass = 'S') \n");
		        }
		        
		        if(!objParams.get(0).get("noEmpresa").equals("0"))
		            sql.append(" and m.no_empresa = " + Integer.parseInt(objParams.get(0).get("noEmpresa").toString()) + " \n");
		        else {
		        	sql.append(" and m.no_empresa  in (select no_empresa \n");
		            sql.append(" from usuario_empresa where no_usuario = " + globalSingleton.getUsuarioLoginDto().getIdUsuario() + ") \n");
		         /*  'adr
		'''            sql.append(" AND (((select count(*) from usuario_area WHERE no_usuario = " & plNoUsuario & ") = 0) or "
		'''            sql.append("     (id_area in (select id_area from usuario_area where no_usuario = " & plNoUsuario & ")))"
		            'adr*/
		        }
		        /*
		        If psLlamada <> "FIL" And psLlamada <> "" Then
		            sql.append(" and origen_mov = '" & psLlamada & "' "
		        Else
		        end if
		         */  
		        sql.append(" and m.fec_conf_trans between '" + objParams.get(0).get("fecha_ini") + "' \n");
		        sql.append(" and '" + objParams.get(0).get("fecha_fin") + "' \n");
		        sql.append(" and m.id_divisa like '" + objParams.get(0).get("sDivisa") + "'  \n");
		//'''        sql.append(" and m.id_rubro = cr.id_rubro \n");
		//'''        sql.append(" and m.id_grupo = cr.id_grupo \n");
		        if(i == 0) sql.append(" UNION  \n");
        	}
	        
	        //System.out.println("query de reportes transfer: " + sql);
	        System.out.println("query cheques buscarDatosReporte \n" + sql +"\n Fin buscarDatosReporte");
			
	        resultado = (List<Map<String, Object>>)jdbcTemplate.query(sql.toString(), new RowMapper(){

				public Object mapRow(ResultSet rs, int i)
					throws SQLException {
			            Map<String, Object> results = new HashMap<String, Object>();
			            results.put("usr_genero", funciones.validarCadena(rs.getString("usuario_alta")));
			            results.put("usr_modifico", funciones.validarCadena(rs.getString("usuario_modif")));
			            results.put("desc_banco", funciones.validarCadena(rs.getString("nuestro_banco")));
			            results.put("clabe_benef", funciones.validarCadena(rs.getString("id_chequera_benef")));
			            results.put("divisa", funciones.validarCadena(rs.getString("id_divisa")));
			            results.put("desc_empresa", funciones.validarCadena(rs.getString("nom_empresa")));
			            results.put("importe", funciones.validarCadena(rs.getString("importe")));
			            results.put("nombre_benef", funciones.validarCadena(rs.getString("beneficiario")));
			            results.put("concepto", funciones.validarCadena(rs.getString("concepto")));
			            results.put("id_chequera", funciones.validarCadena(rs.getString("id_chequera")));
			            results.put("banco_benef", funciones.validarCadena(rs.getString("desc_banco")));
			            results.put("no_docto", funciones.validarCadena(rs.getString("no_docto")));
			            results.put("estatus", funciones.validarCadena(rs.getString("desc_estatus")));
			            results.put("folio", funciones.validarCadena(rs.getString("folio_banco")));
			            results.put("rubro", funciones.validarCadena(rs.getString("desc_rubro")));
			            results.put("fecha", funciones.validarCadena(rs.getString("fec_conf_trans")));
			            return results;
					}
				});
		}catch(Exception e){
			e.printStackTrace();
	        System.out.println("---------------------------------- \n ----------------------------------\n");
			System.out.println("Error en buscarDatosReporte \n ");
			System.out.println("---------------------------------- \n ----------------------------------\n");
			
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Reportes, C:ReportesDaoImpl, M:buscarDatosReporte");
		}
		return resultado;
	}
	
	public List<Map<String, Object>> buscarDatosReportes(Map objParams) {
		System.out.println("En buscar buscarDatosReportes");
		StringBuffer sql = new StringBuffer();
		int i;
		List<Map<String, Object>> resultado = null;
		consultasGenerales= new ConsultasGenerales(jdbcTemplate);
		
		try {
			for(i=0; i<=1; i++) {
		        sql.append(" SELECT m.usuario_alta, m.usuario_modif, m.no_empresa, m.id_banco, z.desc_banco as nuestro_banco, \n");
		        sql.append(" m.id_chequera_benef, ccb.id_clabe, m.id_forma_pago, (case when m.id_forma_pago=3 then 'TRANSFER' else case when m.id_forma_pago=5 then 'CARGO EN CTA' else '' end end) as forma_pago, m.origen_mov, m.id_divisa, m.id_estatus_mov, e.nom_empresa, \n");
		        sql.append(" m.importe, m.beneficiario, m.concepto, \n");
		        sql.append(" origen_mov, m.id_chequera, \n");
		        
		        if(Integer.parseInt(objParams.get("estatusReporte").toString()) == 0)
		        	sql.append("m.fec_conf_trans, ");
		        else
		        	sql.append(" m.fec_valor as fec_conf_trans, ");
		        sql.append(" cb.desc_banco, m.no_docto, ce.desc_estatus, \n");
		        sql.append(" m.folio_banco , m.no_folio_det, m.lote_entrada  \n"); //, cr.desc_rubro"
		        
		        if(i == 0) {
		        	if(Integer.parseInt(objParams.get("estatusReporte").toString()) == 2) {
		        		sql.append("    , (select top 1 cr.desc_rubro \n");
			            sql.append("    from cat_rubro cr \n");
			            sql.append("    where cr.id_rubro = m.id_rubro and cr.id_grupo = m.id_grupo) as desc_rubro, '' as archivo \n");
//			            sql.append(" FROM movimiento m, cat_banco cb, cat_estatus ce, cat_banco z, empresa e,ctas_banco ccb \n");// ''', cat_rubro cr"
			            sql.append(" FROM movimiento m RIGHT JOIN cat_banco cb ON (cb.id_banco = m.id_banco_benef) \n");// ''', cat_rubro cr"
			            sql.append("LEFT JOIN ctas_banco ccb ON (m.id_banco_benef = ccb.id_banco AND m.id_chequera_benef = ccb.id_chequera and m.no_cliente = ccb.no_persona),\n");
			            sql.append("cat_estatus ce, cat_banco z, empresa e ");
		        	}else {
			            sql.append(" , (select top 1 cr.desc_rubro  \n");
			            sql.append("    from cat_rubro cr \n");//,  movimiento hmd  \n");
			            sql.append("    where ");//hmd.cve_control = m.cve_control and hmd.id_tipo_operacion = 3201  \n");
			            sql.append("    cr.id_rubro = m.id_rubro and cr.id_grupo = m.id_grupo  \n");
			            sql.append("    and m.id_rubro = cr.id_rubro) as desc_rubro, d.nom_arch as archivo \n");
			            //sql.append(" FROM movimiento m, det_arch_transfer d, cat_banco cb, cat_estatus ce, cat_banco z, empresa e,ctas_banco ccb \n");// ''', cat_rubro cr \n");
			            sql.append(" FROM movimiento m RIGHT JOIN cat_banco cb ON (cb.id_banco = m.id_banco_benef) \n");// ''', cat_rubro cr"
			            sql.append("LEFT JOIN ctas_banco ccb ON (m.id_banco_benef = ccb.id_banco AND m.id_chequera_benef = ccb.id_chequera and m.no_cliente = ccb.no_persona)\n");
			            if(Integer.parseInt(objParams.get("estatusReporte").toString()) == 0){
				        	//sql.append(" and m.no_folio_det *= d.no_folio_det and d.id_estatus_arch <> 'X' \n");
				        	sql.append("LEFT JOIN det_arch_transfer d ON (m.no_folio_det = d.no_folio_det) \n");
			            }
			            sql.append(", cat_estatus ce, cat_banco z, empresa e ");
		        	}
		        }else {
		        	if(Integer.parseInt(objParams.get("estatusReporte").toString()) == 2) {
		        		sql.append("    , (select top 1 cr.desc_rubro \n");
			            sql.append("    from cat_rubro cr \n");
			            sql.append("    where cr.id_rubro = m.id_rubro and cr.id_grupo = m.id_grupo) as desc_rubro, '' as archivo \n");
			            //sql.append(" FROM hist_movimiento m, cat_banco cb, cat_estatus ce, cat_banco z, empresa e,ctas_banco ccb \n");// ''', cat_rubro cr"
			            sql.append(" FROM hist_movimiento m RIGHT JOIN cat_banco cb ON (cb.id_banco = m.id_banco_benef) \n");// ''', cat_rubro cr"
			            sql.append("LEFT JOIN ctas_banco ccb ON (m.id_banco_benef = ccb.id_banco AND m.id_chequera_benef = ccb.id_chequera and m.no_cliente = ccb.no_persona),\n");
			            sql.append("cat_estatus ce, cat_banco z, empresa e ");
		        	}else {
			            sql.append("    , (select top 1 cr.desc_rubro  \n");
			            sql.append("    from cat_rubro cr,  hist_mov_det hmd  \n");
			            sql.append("    where hmd.cve_control = m.cve_control and hmd.id_tipo_operacion = 3201  \n");
			            sql.append("    and cr.id_rubro = hmd.id_rubro and cr.id_grupo = hmd.id_grupo  \n");
			            sql.append("    and m.id_rubro = cr.id_rubro and hmd.no_docto = m.no_docto) as desc_rubro, d.nom_arch as archivo \n");
			            //sql.append(" FROM hist_movimiento m, cat_banco cb, cat_estatus ce, cat_banco z, empresa e,ctas_banco ccb, det_arch_transfer d \n");// ''', cat_rubro cr \n");
			            sql.append(" FROM hist_movimiento m RIGHT JOIN cat_banco cb ON (cb.id_banco = m.id_banco_benef) \n");// ''', cat_rubro cr"
			            sql.append("LEFT JOIN ctas_banco ccb ON (m.id_banco_benef = ccb.id_banco AND m.id_chequera_benef = ccb.id_chequera and m.no_cliente = ccb.no_persona)\n");
			            if(Integer.parseInt(objParams.get("estatusReporte").toString()) == 0){
				        	//sql.append(" and m.no_folio_det *= d.no_folio_det and d.id_estatus_arch <> 'X' \n");
				        	sql.append("LEFT JOIN det_arch_transfer d ON (m.no_folio_det = d.no_folio_det), \n");
			            }
//			            sql.append("cat_estatus ce, cat_banco z, empresa e,det_arch_transfer d ");
			            sql.append("cat_estatus ce, cat_banco z, empresa e");
		        	}
		        }
		        sql.append(" WHERE \n");
		        //sql.append(" cb.id_banco =* m.id_banco_benef \n");
		        sql.append("  m.id_banco = z.id_banco \n");
//		        sql.append(" and m.id_banco_benef *= ccb.id_banco  \n");
		       // sql.append(" and m.id_chequera_benef *= ccb.id_chequera \n");
		        if(Integer.parseInt(objParams.get("estatusReporte").toString()) == 0){
		        	//sql.append(" and m.no_folio_det *= d.no_folio_det and d.id_estatus_arch <> 'X' \n");
		        	sql.append("and d.id_estatus_arch <> 'X' \n");
		        }
		        if(gsDBM.equals("SYBASE"))
		            sql.append(" and m.no_cliente = convert(varchar,ccb.no_persona) \n");
		        else if(gsDBM.equals("DB2"))
		            sql.append(" and m.no_cliente = cast(ccb.no_persona as varchar(15)) \n");
		        else
		           // sql.append(" and m.no_cliente *= ccb.no_persona \n");
		        
		        sql.append(" and m.no_empresa=e.no_empresa \n");
		        sql.append(" and ce.clasificacion = 'MOV' \n");
		        sql.append(" and m.id_tipo_movto = 'E' \n");
		        sql.append(" and id_forma_pago IN (3,5) \n");
		        sql.append(" and ce.id_estatus = m.id_estatus_mov \n");
		        
		        if(Integer.parseInt(objParams.get("estatusReporte").toString()) == 0)
		        	sql.append(" and id_estatus_mov = 'K' \n");
		        else if(Integer.parseInt(objParams.get("estatusReporte").toString()) == 2)
		        	sql.append(" and id_estatus_mov = 'P' \n");
		        else
		        	sql.append(" and id_estatus_mov in ('K', 'T', 'P') \n");
		        
		        if(Integer.parseInt(objParams.get("estatusReporte").toString()) != 2 && i != 0)
		        	sql.append("  and d.id_estatus_arch <> 'X' \n");
		        
		        if(!objParams.get("noBanco").equals("")) {
		            sql.append(" and m.id_banco = " + Integer.parseInt(objParams.get("noBanco").toString()) + " \n");
		            
		            if(objParams.get("chkSmart").equals("true")) sql.append(" and m.id_chequera in (select id_chequera from cat_cta_banco where pago_mass = 'S') \n");
		        }
		        
		        if(!objParams.get("noEmpresa").equals("0"))
		            sql.append(" and m.no_empresa = " + Integer.parseInt(objParams.get("noEmpresa").toString()) + " \n");
		        else {
		            sql.append(" and m.no_empresa  in (select no_empresa  \n");
		            sql.append(" from usuario_empresa where no_usuario = " + Integer.parseInt(objParams.get("usuario").toString()) + ") \n");
		            /*'adr
					'''la tabla de usuario_area esta vacia
					'''            sql.append(" AND (((select count(*) from usuario_area WHERE no_usuario = " & plNoUsuario & ") = 0) or "
					'''            sql.append("     (id_area in (select id_area from usuario_area where no_usuario = " & plNoUsuario & ")))"
		            'adr*/
		        }
		        /*
		        If psLlamada <> "TODOS" Then
		            If psLlamada = "" Then
		                sql.append(" and (origen_mov = '" & psLlamada & "' or origen_mov is NULL)"
		            Else
		                sql.append(" and origen_mov = '" & psLlamada & "' "
		            End If
		        End If
		        */
		        
		        if(Integer.parseInt(objParams.get("estatusReporte").toString()) == 0)
		        	sql.append(" and convert(datetime, convert(varchar(10), m.fec_conf_trans, 103), 103) between '" + objParams.get("FECHA_INI") + "' \n");
		        else
		        	sql.append(" and convert(datetime, convert(varchar, m.fec_valor, 103), 103) between '" + objParams.get("FECHA_INI") + "' \n");
		        
		        sql.append(" and '" + objParams.get("FECHA_FIN") + "' \n");
		        
		        if(!objParams.get("idDivisa").equals(""))
			        sql.append(" and m.id_divisa like '" + objParams.get("idDivisa") + "'  \n");
		        else
		        	sql.append(" and m.id_divisa like '%'  \n");
		        
		        sql.append(" and (m.origen_mov <> 'FIL' \n");
		        sql.append(" and m.no_cliente not in (" + consultasGenerales.consultarConfiguraSet(206) + ")) \n");
		        
		        if(Integer.parseInt(objParams.get("estatusReporte").toString()) != 2 && i != 0)
		        	//sql.append(" and m.no_folio_det *= d.no_folio_det \n");
		        	sql.append(" and m.no_folio_det = d.no_folio_det \n");
		        
		        /*'''sql.append(" and m.id_rubro = cr.id_rubro \n");
		        ' verificar el uso de grupo hay que ponerlo en todos los movimientos antes de habilitar esto
		        'sql.append(" and m.id_grupo = cr.id_grupo"
		         */
		        if(i == 0) sql.append(" UNION \n");
			}
	        //' para fil e incidentales
	        sql.append(" UNION \n");
	        
	        for(i=0; i<=1; i++) {
		        sql.append(" SELECT \n");
		        sql.append(" m.usuario_alta,m.usuario_modif, \n");
		        sql.append(" m.no_empresa, m.id_banco, z.desc_banco as nuestro_banco, \n");
		        sql.append(" m.id_chequera_benef, \n");
		        sql.append(" m.clabe as id_clabe, \n");
		        sql.append(" m.id_forma_pago,(case when m.id_forma_pago=3 then 'TRANSFER' else case when m.id_forma_pago=5 then 'CARGO EN CTA' else '' end end) as forma_pago , m.origen_mov, \n");
		        sql.append(" m.id_divisa, m.id_estatus_mov, e.nom_empresa, \n");
		        sql.append(" m.importe, m.beneficiario, m.concepto, \n");
		        sql.append(" origen_mov, m.id_chequera, \n");
		        
		        if(Integer.parseInt(objParams.get("estatusReporte").toString()) == 0)
		        	sql.append(" m.fec_conf_trans, ");
		        else
		        	sql.append(" m.fec_valor as fec_conf_trans, ");
		        sql.append(" cb.desc_banco, m.no_docto, ce.desc_estatus, \n");
		        sql.append(" m.folio_banco , m.no_folio_det, m.lote_entrada \n");
		        
		        //''', cr.desc_rubro"
		        
		        if(i == 0) {
		        	if(Integer.parseInt(objParams.get("estatusReporte").toString()) == 2) {
		        		sql.append("    , (select top 1 cr.desc_rubro \n");
			            sql.append("    from cat_rubro cr \n");
			            sql.append("    where cr.id_rubro = m.id_rubro and cr.id_grupo = m.id_grupo) as desc_rubro, '' as archivo \n");
			           // sql.append(" FROM movimiento m, cat_banco cb, cat_estatus ce, cat_banco z, empresa e \n");// ''', cat_rubro cr"
				          sql.append(" FROM movimiento m RIGHT JOIN cat_banco cb ON (cb.id_banco = m.id_banco_benef), cat_estatus ce, cat_banco z, empresa e \n");// ''', cat_rubro cr"

		        	}else {
			            sql.append("    , (select top 1 cr.desc_rubro \n");
			            sql.append("    from cat_rubro cr,  movimiento hmd \n");
			            sql.append("    where hmd.cve_control = m.cve_control and hmd.id_tipo_operacion = 3201 \n");
			            sql.append("    and cr.id_rubro = hmd.id_rubro and cr.id_grupo = hmd.id_grupo \n");
			            sql.append("    and m.id_rubro = cr.id_rubro and hmd.no_docto = m.no_docto) as desc_rubro, d.nom_arch as archivo \n");
			          //  sql.append(" FROM movimiento m, cat_banco cb, cat_estatus ce, cat_banco z, empresa e, det_arch_transfer d \n");// ''', cat_rubro cr"
			            sql.append(" FROM movimiento m RIGHT JOIN cat_banco cb ON (cb.id_banco = m.id_banco_benef) \n");// ''', cat_rubro cr"
			            if(Integer.parseInt(objParams.get("estatusReporte").toString()) != 2){
			            	 sql.append("LEFT JOIN  det_arch_transfer d ON (m.no_folio_det = d.no_folio_det) \n");
			            }
			            sql.append(", cat_estatus ce, cat_banco z, empresa e");
		        	}
		        }else {
		        	if(Integer.parseInt(objParams.get("estatusReporte").toString()) == 2) {
		        		sql.append("    , (select top 1 cr.desc_rubro \n");
			            sql.append("    from cat_rubro cr \n");
			            sql.append("    where cr.id_rubro = m.id_rubro and cr.id_grupo = m.id_grupo) as desc_rubro, '' as archivo  \n");
			            //sql.append(" FROM hist_movimiento m, cat_banco cb, cat_estatus ce, cat_banco z, empresa e \n");// ''', cat_rubro cr"
			            sql.append(" FROM hist_movimiento m RIGHT JOIN cat_banco cb ON (cb.id_banco = m.id_banco_benef), cat_estatus ce, cat_banco z, empresa e \n");// ''', cat_rubro cr"

		        	}else {
			        	sql.append("    , (select top 1 cr.desc_rubro \n");
			            sql.append("    from cat_rubro cr,  hist_mov_det hmd \n");
			            sql.append("    where hmd.cve_control = m.cve_control and hmd.id_tipo_operacion = 3201 \n");
			            sql.append("    and cr.id_rubro = hmd.id_rubro and cr.id_grupo = hmd.id_grupo \n");
			            sql.append("    and m.id_rubro = cr.id_rubro and hmd.no_docto = m.no_docto) as desc_rubro, d.nom_arch as archivo \n");
			         //   sql.append(" FROM hist_movimiento m, cat_banco cb, cat_estatus ce, cat_banco z, empresa e, det_arch_transfer d \n");// ''', cat_rubro cr"
			            sql.append(" FROM hist_movimiento m RIGHT JOIN cat_banco cb ON (cb.id_banco = m.id_banco_benef) \n");// ''', cat_rubro cr"
			            if(Integer.parseInt(objParams.get("estatusReporte").toString()) != 2){
			            	 sql.append("LEFT JOIN  det_arch_transfer d ON (m.no_folio_det = d.no_folio_det) \n");
			            }
				        	
			            sql.append(", cat_estatus ce, cat_banco z, empresa e");
		        	}
		        }
		        sql.append(" WHERE \n");
		       // sql.append(" cb.id_banco =* m.id_banco_benef \n");
		        sql.append("  (m.origen_mov = 'FIL' \n");
		        sql.append(" or m.no_cliente in (" + consultasGenerales.consultarConfiguraSet(206) + ")) \n");
		        sql.append(" and m.id_banco = z.id_banco \n");
		        sql.append(" and m.no_empresa=e.no_empresa \n");
		        sql.append(" and ce.clasificacion = 'MOV' \n");
		        sql.append(" and m.id_tipo_movto = 'E' \n");
		        sql.append(" and id_forma_pago IN (3,5) \n");
		        sql.append(" and ce.id_estatus = m.id_estatus_mov \n");
		        
		        if(Integer.parseInt(objParams.get("estatusReporte").toString()) == 0)
		        	sql.append(" and id_estatus_mov = 'K' \n");
		        else if(Integer.parseInt(objParams.get("estatusReporte").toString()) == 2)
		        	sql.append(" and id_estatus_mov = 'P' \n");
		        else
		        	sql.append(" and id_estatus_mov in ('K', 'T', 'P') \n");
		        
		        if(!objParams.get("noBanco").equals("")) {
		            sql.append(" and m.id_banco = " + Integer.parseInt(objParams.get("noBanco").toString()) + " \n");
		            
		            if(objParams.get("chkSmart").equals("true")) sql.append(" and m.id_chequera in (select id_chequera from cat_cta_banco where pago_mass = 'S') \n");
		        }
		        
		        if(!objParams.get("noEmpresa").equals("0"))
		            sql.append(" and m.no_empresa = " + Integer.parseInt(objParams.get("noEmpresa").toString()) + " \n");
		        else {
		        	sql.append(" and m.no_empresa  in (select no_empresa \n");
		            sql.append(" from usuario_empresa where no_usuario = " + Integer.parseInt(objParams.get("usuario").toString()) + ") \n");
		         /*  'adr
		'''            sql.append(" AND (((select count(*) from usuario_area WHERE no_usuario = " & plNoUsuario & ") = 0) or "
		'''            sql.append("     (id_area in (select id_area from usuario_area where no_usuario = " & plNoUsuario & ")))"
		            'adr*/
		        }
		        /*
		        If psLlamada <> "FIL" And psLlamada <> "" Then
		            sql.append(" and origen_mov = '" & psLlamada & "' "
		        Else
		        end if
		         */  
		        
		        if(Integer.parseInt(objParams.get("estatusReporte").toString()) == 0)
		        	sql.append(" and convert(datetime, convert(varchar(10), m.fec_conf_trans, 103), 103) between '" + objParams.get("FECHA_INI") + "' \n");
		        else
		        	sql.append(" and convert(datetime, convert(varchar, m.fec_valor, 103), 103) between '" + objParams.get("FECHA_INI") + "' \n");
		        sql.append(" and '" + objParams.get("FECHA_FIN") + "' \n");
		        
		        if(!objParams.get("idDivisa").equals(""))
			        sql.append(" and m.id_divisa like '" + objParams.get("idDivisa") + "'  \n");
		        else
		        	sql.append(" and m.id_divisa like '%'  \n");
		        
//		        if(Integer.parseInt(objParams.get("estatusReporte").toString()) != 2)
//		        	sql.append(" and m.no_folio_det *= d.no_folio_det \n");
		//'''        sql.append(" and m.id_rubro = cr.id_rubro \n");
		//'''        sql.append(" and m.id_grupo = cr.id_grupo \n");
		        if(i == 0) sql.append(" UNION  \n");
        	}
	        sql.append(" order by m.id_estatus_mov ");
	        
	        //System.out.println("query de reportes confirmadas: " + sql);
	        System.out.println("query cheques buscarDatosReportes \n" + sql +"\n Fin buscarDatosReportes");
   
	        resultado = (List<Map<String, Object>>)jdbcTemplate.query(sql.toString(), new RowMapper(){

				public Object mapRow(ResultSet rs, int i)
					throws SQLException {
			            Map<String, Object> results = new HashMap<String, Object>();
			            results.put("usr_genero", funciones.validarCadena(rs.getString("usuario_alta")));
			            results.put("usr_modifico", funciones.validarCadena(rs.getString("archivo")));
			            results.put("desc_banco", funciones.validarCadena(rs.getString("nuestro_banco")));
			            results.put("clabe_benef", funciones.validarCadena(rs.getString("id_chequera_benef")));
			            results.put("divisa", funciones.validarCadena(rs.getString("id_divisa")));
			            results.put("desc_empresa", funciones.validarCadena(rs.getString("nom_empresa")));
			            results.put("importe", rs.getDouble("importe"));
			            results.put("nombre_benef", funciones.validarCadena(rs.getString("beneficiario")));
			            results.put("concepto", funciones.validarCadena(rs.getString("concepto")));
			            results.put("id_chequera", funciones.validarCadena(rs.getString("id_chequera")));
			            results.put("banco_benef", funciones.validarCadena(rs.getString("desc_banco")));
			            results.put("no_docto", funciones.validarCadena(rs.getString("no_docto")));
			            results.put("estatus", funciones.validarCadena(rs.getString("desc_estatus")));
			            results.put("folio", funciones.validarCadena(rs.getString("folio_banco")));
			            results.put("rubro", funciones.validarCadena(rs.getString("desc_rubro")));
			            results.put("fecha", funciones.validarCadena(rs.getString("fec_conf_trans")));
			            results.put("forma_pago", funciones.validarCadena(rs.getString("forma_pago")));
			            //results.put("archivo", funciones.validarCadena(rs.getString("nom_arch")));
			            return results;
					}
				});
		}catch(Exception e){
			e.printStackTrace();
			 System.out.println("---------------------------------- \n ----------------------------------\n");
				System.out.println("Error en buscarDatosReportes \n ");
				System.out.println("---------------------------------- \n ----------------------------------\n");
				
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Reportes, C:ReportesDaoImpl, M:buscarDatosReporte");
		}
		return resultado;
	}
	
	public List<Map<String, Object>> buscarTransferidas(Map objParams) {
		StringBuffer sql = new StringBuffer();
		globalSingleton = GlobalSingleton.getInstancia();
		List<Map<String, Object>> resultado = null;
		consultasGenerales = new ConsultasGenerales(jdbcTemplate);
		
		try {
			if(gsDBM.equals("SQL SERVER") || gsDBM.equals("SYBASE")) {
				sql.append(" SELECT \n");
	            sql.append(" m.usuario_alta,m.usuario_modif, \n");
	            sql.append(" m.no_empresa, m.id_banco,z.desc_banco as nuestro_banco, \n");
	            sql.append(" m.id_chequera_benef,ccb.id_clabe, m.id_forma_pago,(case when m.id_forma_pago=3 then 'TRANSFER' else case when m.id_forma_pago=5 then 'CARGO EN CTA' else '' end end) as forma_pago, m.origen_mov,  \n");
	            sql.append(" m.id_divisa, m.id_estatus_mov, e.nom_empresa,  \n");
	            sql.append(" m.importe, m.beneficiario, m.concepto, origen_mov,  \n");
	            sql.append(" m.id_chequera, m.fec_valor, cb.desc_banco, m.no_docto, \n");
	            sql.append(" ce.desc_estatus , m.folio_banco, m.no_folio_det, m.lote_entrada \n");
	            
	            sql.append(" ,(select count(*) from movto_banca_e e  \n");
	            sql.append("       where e.no_empresa = m.no_empresa and e.id_banco = m.id_banco  \n");
	            sql.append("       and e.id_chequera = m.id_chequera and e.fec_Valor = m.fec_valor  \n");
	            sql.append("       and e.importe = m.importe  \n");
	            sql.append("       and e.cargo_abono = m.id_tipo_movto) as cuantos_iguales,  \n");
	            sql.append(" (select count(*) from movto_banca_e e where  \n");
	            sql.append("       e.no_empresa = m.no_empresa and e.id_banco = m.id_banco  \n");
	            sql.append("       and e.id_chequera = m.id_chequera and e.fec_Valor = m.fec_valor  \n");
	            sql.append("       and e.importe = m.importe  \n");
	            sql.append("       and e.cargo_abono = 'I') as cuantos_contrarios_hoy,  \n");
	            sql.append("  (select count(*) from movto_banca_e e  \n");
	            sql.append("       where e.no_empresa = m.no_empresa and e.id_banco = m.id_banco  \n");
	            sql.append("       and e.id_chequera = m.id_chequera and e.fec_Valor + 1 = m.fec_valor  \n");
	            sql.append("       and e.importe = m.importe  \n");
	            sql.append("       and e.cargo_abono = 'I') as cuantos_contrarios_manana  \n");
	            sql.append("  ,d.nom_arch  \n");
	            
//	            sql.append(" FROM movimiento m , cat_banco cb, cat_estatus ce, \n");
//	            sql.append(" cat_banco z,empresa e , ctas_banco ccb, det_arch_transfer d \n");
	            sql.append(" FROM movimiento m RIGHT JOIN cat_banco cb ON(cb.id_banco=m.id_banco_benef) LEFT JOIN ctas_banco ccb ON(m.id_banco_benef=ccb.id_banco and m.id_chequera_benef = ccb.id_chequera) \n");
	            sql.append(" LEFT JOIN det_arch_transfer d ON (m.no_folio_det = d.no_folio_det and id_estatus_arch in ('T', 'R')),cat_estatus ce,cat_banco z,empresa e  \n");
	            sql.append(" WHERE  \n");
//	            sql.append(" cb.id_banco =* m.id_banco_benef  \n");
	            sql.append(" m.id_banco = z.id_banco  \n");
//	            sql.append(" and m.id_banco_benef *= ccb.id_banco \n");
//	            sql.append(" and m.id_chequera_benef *= ccb.id_chequera \n"); 
	            
//	            sql.append(" and m.no_folio_det *= d.no_folio_det and id_estatus_arch in ('T', 'R') \n");
	            

//	            
	            if(gsDBM.equals("SYBASE"))
	                sql.append(" and m.no_cliente=convert(varchar,ccb.no_persona) \n");
//	            else
//	                sql.append(" and m.no_cliente *= ccb.no_persona \n"); 
	            
	            sql.append(" and m.no_empresa=e.no_empresa  \n");
	            sql.append(" and ce.clasificacion = 'MOV'  \n"); 
	            sql.append(" and m.id_tipo_movto = 'E'  \n"); 
	            sql.append(" and id_forma_pago IN (3,5)  \n");
	            sql.append(" and ce.id_estatus = m.id_estatus_mov  \n");
	            sql.append(" and id_estatus_mov = 'T'  \n");
	            
	            if(!objParams.get("noBanco").equals("")) {
		            sql.append(" and m.id_banco = " + Integer.parseInt(objParams.get("noBanco").toString()) + " \n");
		            
		            if(objParams.get("chkSmart").equals("true")) sql.append(" and m.id_chequera in (select id_chequera from cat_cta_banco where pago_mass = 'S') \n");
		        }
		        
		        if(!objParams.get("noEmpresa").equals("0"))
		            sql.append(" and m.no_empresa = " + Integer.parseInt(objParams.get("noEmpresa").toString()) + " \n");
		        else {
		        	sql.append(" and m.no_empresa  in (select no_empresa \n");
		            sql.append(" from usuario_empresa where no_usuario = " + Integer.parseInt(objParams.get("usuario").toString()) + ") \n");
		            sql.append(" AND (((select count(*) from usuario_area WHERE no_usuario = " + Integer.parseInt(objParams.get("usuario").toString()) + ") = 0) or ");
		            sql.append("     (id_area in (select id_area from usuario_area where no_usuario = " + Integer.parseInt(objParams.get("usuario").toString()) + ")))");
		        }
		        
		        /*If psLlamada <>  \n");TODOS \n"); Then
	                If psLlamada =  \n"); \n"); Then
	                    sql.append(" and (origen_mov = ' \n"); & psLlamada &  \n");' or origen_mov is NULL) \n");
	                Else
	                    sql.append(" and origen_mov = ' \n"); & psLlamada &  \n");'  \n");
	                End If
	            End If
	            */
	            sql.append(" and m.fec_valor between '" + objParams.get("FECHA_INI") + "' \n");
	            sql.append(" and '" + objParams.get("FECHA_FIN") + "' \n");
	            
	            if(!objParams.get("idDivisa").equals(""))
			        sql.append(" and m.id_divisa like '" + objParams.get("idDivisa") + "'  \n");
		        else
		        	sql.append(" and m.id_divisa like '%'  \n");
	            
	            sql.append(" and (m.origen_mov <> 'FIL' \n");
	            sql.append(" and m.no_cliente not in (" + consultasGenerales.consultarConfiguraSet(206) + ")) \n");
	            
	            sql.append(" UNION  \n");
	            
	            //' PARA FILIALES Y INCIDENTES
	            sql.append(" SELECT \n");
	            sql.append(" m.usuario_alta,m.usuario_modif, \n");
	            sql.append(" m.no_empresa, m.id_banco,z.desc_banco as nuestro_banco, \n");
	            sql.append(" m.id_chequera_benef,m.clabe as id_clabe, m.id_forma_pago, (case when m.id_forma_pago=3 then 'TRANSFERENCIA' else case when m.id_forma_pago=5 then 'CARGO EN CUENTA' else '' end end) as forma_pago, m.origen_mov,  \n");
	            sql.append(" m.id_divisa, m.id_estatus_mov, e.nom_empresa,  \n");
	            sql.append(" m.importe, m.beneficiario, m.concepto, origen_mov,  \n");
	            sql.append(" m.id_chequera, m.fec_valor, cb.desc_banco, m.no_docto, \n");
	            sql.append(" ce.desc_estatus , m.folio_banco, m.no_folio_det, m.lote_entrada \n");
	            //***********
	            sql.append(" ,(select count(*) from movto_banca_e e  \n");
	            sql.append("       where e.no_empresa = m.no_empresa and e.id_banco = m.id_banco  \n");
	            sql.append("       and e.id_chequera = m.id_chequera and e.fec_Valor = m.fec_valor  \n");
	            sql.append("       and e.importe = m.importe  \n");
	            sql.append("       and e.cargo_abono = m.id_tipo_movto) as cuantos_iguales,  \n");
	            sql.append(" (select count(*) from movto_banca_e e where  \n");
	            sql.append("       e.no_empresa = m.no_empresa and e.id_banco = m.id_banco  \n");
	            sql.append("       and e.id_chequera = m.id_chequera and e.fec_Valor = m.fec_valor  \n");
	            sql.append("       and e.importe = m.importe  \n");
	            sql.append("       and e.cargo_abono = 'I') as cuantos_contrarios_hoy,  \n");
	            sql.append("  (select count(*) from movto_banca_e e  \n");
	            sql.append("       where e.no_empresa = m.no_empresa and e.id_banco = m.id_banco  \n");
	            sql.append("       and e.id_chequera = m.id_chequera and e.fec_Valor + 1 = m.fec_valor  \n");
	            sql.append("       and e.importe = m.importe  \n");
	            sql.append("       and e.cargo_abono = 'I') as cuantos_contrarios_manana  \n");
	            sql.append("  ,d.nom_arch  \n");
	            
	            //***********
	            
	            sql.append(" FROM movimiento m RIGHT JOIN cat_banco cb ON(cb.id_banco = m.id_banco_benef)  \n");
	            sql.append(" LEFT JOIN det_arch_transfer d ON(m.no_folio_det = d.no_folio_det and id_estatus_arch in ('T', 'R')) \n"); 
	            sql.append(" , cat_estatus ce,cat_banco z,empresa e \n");
	            
	            sql.append(" WHERE  \n");
//	            sql.append(" cb.id_banco =* m.id_banco_benef  \n");
	            sql.append(" (m.origen_mov = 'FIL' \n");
	            sql.append(" or m.no_cliente in(" + consultasGenerales.consultarConfiguraSet(206) + ")) \n");
	            sql.append(" and m.id_banco = z.id_banco  \n");
	            sql.append(" and m.no_empresa=e.no_empresa  \n");
	            sql.append(" and ce.clasificacion = 'MOV'  \n");
	            sql.append(" and m.id_tipo_movto = 'E'  \n");
	            sql.append(" and id_forma_pago IN (3,5)  \n");
	            sql.append(" and ce.id_estatus = m.id_estatus_mov  \n");
	            sql.append(" and id_estatus_mov = 'T'  \n");
	            
	            if(!objParams.get("noBanco").equals("")) {
		            sql.append(" and m.id_banco = " + Integer.parseInt(objParams.get("noBanco").toString()) + " \n");
		            
		            if(objParams.get("chkSmart").equals("true")) sql.append(" and m.id_chequera in (select id_chequera from cat_cta_banco where pago_mass = 'S') \n");
		        }
//	            sql.append(" and m.no_folio_det *= d.no_folio_det and id_estatus_arch in ('T', 'R') \n");
	            
	            if(!objParams.get("noEmpresa").equals("0"))
		            sql.append(" and m.no_empresa = " + Integer.parseInt(objParams.get("noEmpresa").toString()) + " \n");
		        else {
		        	sql.append(" and m.no_empresa  in (select no_empresa \n");
		            sql.append(" from usuario_empresa where no_usuario = " + Integer.parseInt(objParams.get("usuario").toString()) + ") \n");
		            sql.append(" AND (((select count(*) from usuario_area WHERE no_usuario = " + Integer.parseInt(objParams.get("usuario").toString()) + ") = 0) or ");
		            sql.append("     (id_area in (select id_area from usuario_area where no_usuario = " + Integer.parseInt(objParams.get("usuario").toString()) + ")))");
		        }
	            
	            /*
	            If psLlamada <>  \n");TODOS \n"); Then
	                If psLlamada <>  \n");FIL \n"); And psLlamada <>  \n"); \n"); Then
	                    sql.append(" and origen_mov = ' \n"); & psLlamada &  \n");'  \n");
	                End If
	            End If
	            */
	            sql.append(" and m.fec_valor between '" + objParams.get("FECHA_INI") + "' \n");
		        sql.append(" and '" + objParams.get("FECHA_FIN") + "' \n");
		        
		        if(!objParams.get("idDivisa").equals(""))
			        sql.append(" and m.id_divisa like '" + objParams.get("idDivisa") + "'  \n");
		        else
		        	sql.append(" and m.id_divisa like '%'  \n");
			}
			//System.out.println("query de reportes transferidas: " + sql);
			
			System.out.println("query cheques buscarTransferidas \n" + sql +"\n Fin buscarTransferidas");
			
			
			resultado = (List<Map<String, Object>>)jdbcTemplate.query(sql.toString(), new RowMapper(){

				public Object mapRow(ResultSet rs, int i)
					throws SQLException {
			            Map<String, Object> results = new HashMap<String, Object>();
			            results.put("usr_genero", funciones.validarCadena(rs.getString("usuario_alta")));
			            results.put("abonos", funciones.validarCadena(rs.getString("cuantos_contrarios_hoy")));
			            results.put("desc_banco", funciones.validarCadena(rs.getString("nuestro_banco")));
			            results.put("clabe_benef", funciones.validarCadena(rs.getString("id_chequera_benef")));
			            results.put("divisa", funciones.validarCadena(rs.getString("id_divisa")));
			            results.put("desc_empresa", funciones.validarCadena(rs.getString("nom_empresa")));
			            results.put("importe", rs.getDouble("importe"));
			            results.put("nombre_benef", funciones.validarCadena(rs.getString("beneficiario")));
			            results.put("concepto", funciones.validarCadena(rs.getString("concepto")));
			            results.put("id_chequera", funciones.validarCadena(rs.getString("id_chequera")));
			            results.put("banco_benef", funciones.validarCadena(rs.getString("desc_banco")));
			            results.put("no_docto", funciones.validarCadena(rs.getString("no_docto")));
			            results.put("cargos", funciones.validarCadena(rs.getString("cuantos_iguales")));
			            results.put("folio", funciones.validarCadena(rs.getString("folio_banco")));
			            results.put("fecha", funciones.validarCadena(rs.getString("fec_valor")));
			            results.put("rubro", funciones.validarCadena(rs.getString("nom_arch")));
			            results.put("estatus", funciones.validarCadena(rs.getString("desc_estatus")));
			            results.put("forma_pago", funciones.validarCadena(rs.getString("forma_pago")));
			            return results;
					}
				});
		}catch(Exception e){
			e.printStackTrace();
			 System.out.println("---------------------------------- \n ----------------------------------\n");
			 System.out.println("Error en buscarTransferidas \n ");
			 System.out.println("---------------------------------- \n ----------------------------------\n");
				
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Reportes, C:ReportesDaoImpl, M:buscarTransferidas");
		}
		return resultado;
	}
	
	public List<Map<String, Object>> entreCuentasEmp(Map objParams, String tipoOperSup) {
		StringBuffer sql = new StringBuffer();
		List<Map<String, Object>> resultado = null;
		
		try {
			sql.append(" SELECT   \n");
            sql.append(" a.usuario_alta,a.usuario_modif, \n");
            sql.append(" a.id_banco, b.desc_banco, a.id_chequera, \n");
            sql.append(" a.importe, a.id_caja, a.concepto, a.beneficiario,  \n");
            sql.append(" f.nom_empresa as originaria,a.id_divisa, a.referencia, \n");
            sql.append(" a.id_banco_benef, g.nom_empresa as beneficiaria, \n");
            sql.append(" d.desc_banco as desc_banco_benef,a.id_chequera_benef,z.id_clabe, a.fec_valor \n");
//            sql.append(" FROM movimiento a, cat_banco b, cat_banco d, \n");
            sql.append(" FROM movimiento a LEFT JOIN cat_banco d ON (a.id_banco_benef = d.id_banco),cat_banco b, \n");
            sql.append(" empresa f, empresa g, cat_cta_banco z, persona p, usuario_empresa ue \n");
            sql.append(" WHERE  \n");
            sql.append(" a.id_banco = b.id_banco \n");
            sql.append(" and a.id_chequera_benef = z.id_chequera \n");
            sql.append(" and a.no_empresa = f.no_empresa \n");
            sql.append(" and z.no_empresa=g.no_empresa \n");
        //    sql.append(" and a.id_banco_benef *= d.id_banco \n");
            sql.append(" and a.id_tipo_movto = 'E' \n");
            
            
            if(Integer.parseInt(objParams.get("tipoTrasp").toString()) == 0)
            	sql.append(" and a.id_estatus_mov='L' \n");
            else
            	sql.append(" and a.id_estatus_mov in ('A', 'Q') \n");
            sql.append(" and id_tipo_operacion in ("+ tipoOperSup +") \n");
            
            if(!objParams.get("noEmpresa").equals("0"))
	            sql.append(" and a.no_empresa = " + Integer.parseInt(objParams.get("noEmpresa").toString()) + " \n");
	        else {
	        	sql.append(" and a.no_empresa  in (select no_empresa \n");
	            sql.append(" from usuario_empresa where no_usuario = " + Integer.parseInt(objParams.get("usuario").toString()) + ") \n");
	        }
            
            if(!objParams.get("idDivisa").equals(""))
		        sql.append(" and a.id_divisa = '" + objParams.get("idDivisa") + "'  \n");
	        
            if(objParams.get("FECHA_FIN").equals(""))
            	sql.append(" and a.fec_valor = '"+ objParams.get("FECHA_FIN") +"'  \n");
            else
            	sql.append(" and a.fec_valor between '"+ objParams.get("FECHA_INI") +"' and  '"+ objParams.get("FECHA_FIN") +"' \n");
            
            sql.append(" and ue.no_usuario = " + Integer.parseInt(objParams.get("usuario").toString()) + " \n");
            sql.append(" and ue.no_empresa=a.no_empresa \n");
            sql.append(" and p.no_empresa=a.no_empresa \n");
            sql.append(" and p.id_tipo_persona='E' \n");
            sql.append(" and p.id_estatus_per<>'I' \n");
            sql.append(" and a.id_banco= "+ objParams.get("noBanco") +" \n");


            sql.append(" Union All \n");
            sql.append(" SELECT \n");
            sql.append(" a.usuario_alta,a.usuario_modif, \n");
            sql.append(" a.id_banco, b.desc_banco, a.id_chequera, a.importe, \n");
            sql.append(" a.id_caja, a.concepto, a.beneficiario, f.nom_empresa as originaria, \n");
            sql.append(" a.id_divisa, a.referencia, a.id_banco_benef, g.nom_empresa \n");
            sql.append(" as beneficiaria, d.desc_banco as desc_banco_benef, a.id_chequera_benef,z.id_clabe, a.fec_valor \n");
            sql.append(" FROM hist_movimiento a LEFT JOIN cat_banco d ON (a.id_banco_benef = d.id_banco),  \n");
            sql.append(" cat_banco b, empresa f, empresa g, cat_cta_banco z, persona p , usuario_empresa ue \n");
            sql.append(" WHERE a.id_banco = b.id_banco  \n");
            sql.append(" and a.id_chequera_benef = z.id_chequera  \n");
            sql.append(" and a.no_empresa = f.no_empresa \n");
            sql.append(" and z.no_empresa = g.no_empresa \n");
          //  sql.append(" and a.id_banco_benef *= d.id_banco \n");
            sql.append(" and a.id_tipo_movto = 'E' \n");
            //sql.append(" and a.id_estatus_mov = 'A' \n");
            sql.append(" and ue.no_empresa=a.no_empresa \n");
            sql.append(" and p.no_empresa=a.no_empresa \n");
            sql.append(" and p.id_tipo_persona='E' \n");
            sql.append(" and p.id_estatus_per<>'I' \n");
            sql.append(" and id_tipo_operacion in ("+ tipoOperSup +") \n");
            sql.append(" and a.id_banco= "+ objParams.get("noBanco") +" \n");
             
            if(Integer.parseInt(objParams.get("tipoTrasp").toString()) == 0)
            	sql.append(" and a.id_estatus_mov='L' \n");
            else
            	sql.append(" and a.id_estatus_mov='A' \n");
            
            
            if(!objParams.get("noEmpresa").equals("0"))
	            sql.append(" and a.no_empresa = " + Integer.parseInt(objParams.get("noEmpresa").toString()) + " \n");
	        else {
	        	sql.append(" and a.no_empresa  in (select no_empresa \n");
	            sql.append(" from usuario_empresa where no_usuario = " + Integer.parseInt(objParams.get("usuario").toString()) + ") \n");
	        }
            
            if(!objParams.get("idDivisa").equals(""))
		        sql.append(" and a.id_divisa = '" + objParams.get("idDivisa") + "'  \n");
	        
            if(objParams.get("FECHA_FIN").equals(""))
            	sql.append(" and a.fec_valor = '"+ objParams.get("FECHA_FIN") +"'  \n");
            else
            	sql.append(" and a.fec_valor between '"+ objParams.get("FECHA_INI") +"' and  '"+ objParams.get("FECHA_FIN") +"' \n");
            
            sql.append(" and ue.no_usuario = " + Integer.parseInt(objParams.get("usuario").toString()) + " \n");
            
            //System.out.println("query de reportes trapasos entre cuentas: " + sql);
            System.out.println("query cheques entreCuentasEmp \n" + sql +"\n Fin entreCuentasEmp");
			
	        resultado = (List<Map<String, Object>>)jdbcTemplate.query(sql.toString(), new RowMapper(){

				public Object mapRow(ResultSet rs, int i)
					throws SQLException {
			            Map<String, Object> results = new HashMap<String, Object>();
			            results.put("usr_genero", funciones.validarCadena(rs.getString("usuario_alta")));
			            results.put("usr_modifico", funciones.validarCadena(rs.getString("usuario_modif")));
			            results.put("desc_banco", funciones.validarCadena(rs.getString("desc_banco")));
			            results.put("clabe_benef", funciones.validarCadena(rs.getString("id_chequera_benef")));
			            results.put("divisa", funciones.validarCadena(rs.getString("id_divisa")));
			            results.put("desc_empresa", funciones.validarCadena(rs.getString("originaria")));
			            results.put("importe", rs.getDouble("importe"));
			            results.put("nombre_benef", funciones.validarCadena(rs.getString("beneficiario")));
			            results.put("concepto", funciones.validarCadena(rs.getString("concepto")));
			            results.put("id_chequera", funciones.validarCadena(rs.getString("id_chequera")));
			            results.put("banco_benef", funciones.validarCadena(rs.getString("desc_banco_benef")));
			            results.put("caja", funciones.validarCadena(rs.getString("id_caja")));
			            results.put("fecha", funciones.validarCadena(rs.getString("fec_valor")));
			            results.put("referencia", funciones.validarCadena(rs.getString("referencia")));
			            return results;
					}
				});
		}catch(Exception e){
			e.printStackTrace();
			System.out.println("---------------------------------- \n ----------------------------------\n");
			 System.out.println("Error en entreCuentasEmp \n ");
			 System.out.println("---------------------------------- \n ----------------------------------\n");
			
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Reportes, C:ReportesDaoImpl, M:entreCuentasEmp");
		}
		return resultado;
	}
	
	//getters && setters
	public void setDataSource(DataSource dataSource){
		try{
			this.jdbcTemplate = new JdbcTemplate(dataSource);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Reportes, C:ReportesDaoImpl, M:setDataSource");
		}
	}
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
}
