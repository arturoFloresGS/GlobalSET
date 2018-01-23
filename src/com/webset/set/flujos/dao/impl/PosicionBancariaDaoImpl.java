package com.webset.set.flujos.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.webset.set.flujos.dao.PosicionBancariaDao;
import com.webset.set.flujos.dto.PosicionBancariaDto;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.ConsultasGenerales;
import com.webset.set.utilerias.Funciones;

public class PosicionBancariaDaoImpl implements PosicionBancariaDao{
	JdbcTemplate jdbcTemplate;
	Bitacora bitacora = new Bitacora();
	Funciones funciones = new Funciones();
	ConsultasGenerales consultasGenerales = new ConsultasGenerales(jdbcTemplate);
	
	@SuppressWarnings("unchecked")
	public List<PosicionBancariaDto> buscaDivisas() {
		StringBuffer sql = new StringBuffer();
		List<PosicionBancariaDto> listDivisa = new ArrayList<PosicionBancariaDto>();
		
		try {
			sql.append(" SELECT id_divisa, desc_divisa");
			sql.append(" FROM cat_divisa");
			sql.append(" ORDER BY 1");
			
			listDivisa = jdbcTemplate.query(sql.toString(), new RowMapper(){
				public PosicionBancariaDto mapRow(ResultSet rs, int idx) throws SQLException {
					PosicionBancariaDto cons = new PosicionBancariaDto();
					cons.setIdDivisa(rs.getString("id_divisa"));
					cons.setDescDivisa(rs.getString("desc_divisa"));
					return cons;
				}});
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Flujos, C:PosicionBancariaDaoImp, M:buscaDivisas");
		}
		return listDivisa;
	}
	
	@SuppressWarnings("unchecked")
	public List<PosicionBancariaDto> posicionBancariaBE() {
		StringBuffer sql = new StringBuffer();
		List<PosicionBancariaDto> listFlujoBE = new ArrayList<PosicionBancariaDto>();
		
		try {
			sql.append(" SELECT Distinct Cat.id_banco, Cat.desc_banco, Emp.no_empresa, Emp.nom_empresa \n");
			sql.append(" FROM cat_banco Cat, cat_cta_banco Cta, Empresa Emp \n");
			sql.append(" WHERE Cat.id_banco = Cta.id_banco \n");
			sql.append(" 	And Cta.No_Empresa = Emp.No_Empresa \n");
			sql.append(" ORDER BY 1");
			
			listFlujoBE = jdbcTemplate.query(sql.toString(), new RowMapper(){
				public PosicionBancariaDto mapRow(ResultSet rs, int idx) throws SQLException {
					PosicionBancariaDto cons = new PosicionBancariaDto();
					cons.setCol0(rs.getString("id_banco"));
					cons.setCol1(rs.getString("desc_banco"));
					cons.setCol2(rs.getString("no_empresa"));
					cons.setCol3(rs.getString("nom_empresa"));
					return cons;
				}});
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Flujos, C:PosicionBancariaDaoImp, M:buscaDivisas");
		}
		return listFlujoBE;
	}
	
	@SuppressWarnings("unchecked")
	public List<PosicionBancariaDto> posicionBancariaB() {
		StringBuffer sql = new StringBuffer();
		List<PosicionBancariaDto> listFlujoBE = new ArrayList<PosicionBancariaDto>();
		
		try {
			sql.append(" SELECT Distinct Cat.id_banco , Cat.desc_banco \n");
			sql.append(" FROM cat_banco Cat, cat_cta_banco Cta \n");
			sql.append(" WHERE Cat.id_banco = Cta.id_banco \n");
			sql.append(" ORDER BY 1");
			
			listFlujoBE = jdbcTemplate.query(sql.toString(), new RowMapper(){
				public PosicionBancariaDto mapRow(ResultSet rs, int idx) throws SQLException {
					PosicionBancariaDto cons = new PosicionBancariaDto();
					cons.setCol0(rs.getString("id_banco"));
					cons.setCol1(rs.getString("desc_banco"));
					return cons;
				}});
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Flujos, C:PosicionBancariaDaoImp, M:posicionBancariaB");
		}
		return listFlujoBE;
	}
	
	@SuppressWarnings("unchecked")
	public List<PosicionBancariaDto> posicionBancariaC() {
		StringBuffer sql = new StringBuffer();
		List<PosicionBancariaDto> listFlujoBE = new ArrayList<PosicionBancariaDto>();
		
		try {
			sql.append(" SELECT Distinct Cat.id_banco, Cat.desc_banco, Cta.id_chequera, Emp.nom_empresa, desc_chequera \n");
			sql.append(" FROM cat_banco Cat, cat_cta_banco Cta, empresa Emp \n");
			sql.append(" WHERE Cat.id_banco = Cta.id_banco \n");
			sql.append(" 	And Emp.no_empresa = Cta.no_empresa \n");
			sql.append(" ORDER BY 1");
			
			listFlujoBE = jdbcTemplate.query(sql.toString(), new RowMapper(){
				public PosicionBancariaDto mapRow(ResultSet rs, int idx) throws SQLException {
					PosicionBancariaDto cons = new PosicionBancariaDto();
					cons.setCol0(rs.getString("nom_empresa"));
					cons.setCol1(rs.getString("id_banco"));
					cons.setCol2(rs.getString("desc_banco"));
					cons.setCol3(rs.getString("id_chequera"));
					cons.setCol4(rs.getString("desc_chequera"));
					return cons;
				}});
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Flujos, C:PosicionBancariaDaoImp, M:posicionBancariaC");
		}
		return listFlujoBE;
	}
	
	@SuppressWarnings("unchecked")
	public List<PosicionBancariaDto> posicionBancariaE() {
		StringBuffer sql = new StringBuffer();
		List<PosicionBancariaDto> listFlujoBE = new ArrayList<PosicionBancariaDto>();
		
		try {
			sql.append(" SELECT Distinct Emp.no_empresa as No_Empresa , Emp.nom_empresa as Nombre_Empresa \n");
			sql.append(" FROM empresa Emp \n");
			sql.append(" ORDER BY 2");
			
			listFlujoBE = jdbcTemplate.query(sql.toString(), new RowMapper(){
				public PosicionBancariaDto mapRow(ResultSet rs, int idx) throws SQLException {
					PosicionBancariaDto cons = new PosicionBancariaDto();
					cons.setCol0(rs.getString("No_Empresa"));
					cons.setCol1(rs.getString("Nombre_Empresa"));
					return cons;
				}});
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Flujos, C:PosicionBancariaDaoImp, M:posicionBancariaE");
		}
		return listFlujoBE;
	}
	
	@SuppressWarnings("unchecked")
	public List<PosicionBancariaDto> posicionBancariaR() {
		StringBuffer sql = new StringBuffer();
		List<PosicionBancariaDto> listFlujoBE = new ArrayList<PosicionBancariaDto>();
		
		try {
			sql.append(" SELECT id_rubro, desc_rubro \n");
			sql.append(" FROM cat_Rubro \n");
			sql.append(" ORDER BY 2");
			
			listFlujoBE = jdbcTemplate.query(sql.toString(), new RowMapper(){
				public PosicionBancariaDto mapRow(ResultSet rs, int idx) throws SQLException {
					PosicionBancariaDto cons = new PosicionBancariaDto();
					cons.setCol0(rs.getString("id_rubro"));
					cons.setCol1(rs.getString("desc_rubro"));
					return cons;
				}});
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Flujos, C:PosicionBancariaDaoImp, M:posicionBancariaR");
		}
		return listFlujoBE;
	}
	
	@SuppressWarnings("unchecked")
	public List<PosicionBancariaDto> buscaDetalleBE(List<Map<String, String>> datosGrid, List<Map<String, String>> parametros) {
		StringBuffer sql = new StringBuffer();
		List<PosicionBancariaDto> listFlujoBE = new ArrayList<PosicionBancariaDto>();
		String sTabla = "movimiento m";
		
		try {
			for(int i=0; i<2; i++) {
				if(i == 1) {
					sql.append(" UNION ALL \n");
					sTabla = "hist_movimiento m";
				}
				
				sql.append(" SELECT ban.desc_banco, ccb.id_chequera, cr.id_grupo as rubro, cr.id_rubro as sub_Rubro, \n");
				sql.append(" 	m.fec_valor , m.fec_operacion, m.importe, m.referencia, m.concepto, m.id_tipo_movto \n");
				sql.append(" FROM " + sTabla + ", cat_cta_banco ccb, cat_rubro cr, cat_banco ban \n");
				
				//Se comenta este if por que no esta dado de alta el configura set 400 darlo de alta en la tabla victor tello				
				//if(consultasGenerales.consultarConfiguraSet(400).equals("NO")) {
					sql.append(" WHERE cr.id_rubro = m.id_rubro and \n");
				/*}else {
					sql.append(" WHERE cr.id_grupo = cg.id_grupo and \n");
					sql.append(" 	cr.id_rubro = m.id_rubro and \n");
					sql.append(" 	cr.id_grupo = m.id_grupo and \n");
				}*/
				sql.append(" 	ccb.id_banco = Ban.id_banco and \n");
				sql.append(" 	ccb.id_banco = m.id_banco and \n");
				sql.append(" 	ccb.id_chequera = m.id_chequera and \n");
				sql.append(" 	id_estatus_mov <> 'X' and \n");
				sql.append(" 	m.id_tipo_operacion in(3200, 3701, 3120, 3101, 3116, 1016, 1017, 1018, 1019, 1020, 1023, 3107, 3700, 3702, 3705, 3706, 3708, 3709, 3707, 4001, 4102, 4103, 4104, 3500, 3115, 3154) \n");
				
				switch (Integer.parseInt(parametros.get(0).get("selec"))) {
					case 3:
						sql.append(" 	and ccb.id_banco in (" + datosGrid.get(0).get("campo1") + ") \n");
						sql.append(" 	and ccb.id_chequera in( " + datosGrid.get(0).get("campo2") + ") \n");
						sql.append(" 	and m.fec_valor >= '" + parametros.get(0).get("fecIni") + "' and m.fec_valor <= '" + parametros.get(0).get("fecFin") + "' \n");
						if(i == 1) sql.append(" ORDER BY ccb.id_chequera, rubro, sub_Rubro asc \n");
						break;
					case 1:
						sql.append(" 	and ccb.id_banco in (" + datosGrid.get(0).get("campo1") + ") \n");
						sql.append(" 	and m.no_empresa in (" + datosGrid.get(0).get("campo2") + ") \n");
						sql.append(" 	and m.fec_valor >= '" + parametros.get(0).get("fecIni") + "' and m.fec_valor <= '" + parametros.get(0).get("fecFin") + "' \n");
						if(i == 1) sql.append(" ORDER BY ccb.id_chequera, rubro, sub_Rubro asc \n");
						break;
					case 2:
						sql.append(" 	and ccb.id_banco in (" + datosGrid.get(0).get("campo1") + ") \n");
						sql.append(" 	and m.fec_valor >= '" + parametros.get(0).get("fecIni") + "' and m.fec_valor <= '" + parametros.get(0).get("fecFin") + "' \n");
						if(i == 1) sql.append(" ORDER BY ccb.id_chequera, rubro, sub_Rubro asc \n");
						break;
					case 4:
						sql.append(" 	and m.no_empresa in( " + datosGrid.get(0).get("campo1") + ") \n");
						sql.append(" 	and m.fec_valor >= '" + parametros.get(0).get("fecIni") + "' and m.fec_valor <= '" + parametros.get(0).get("fecFin") + "' \n");
						if(i == 1) sql.append(" ORDER BY ccb.id_chequera, rubro, sub_Rubro asc \n");
						break;
				}
			}
			
			System.out.println(sql.toString());
			
			listFlujoBE = jdbcTemplate.query(sql.toString(), new RowMapper(){
				public PosicionBancariaDto mapRow(ResultSet rs, int idx) throws SQLException {
					PosicionBancariaDto cons = new PosicionBancariaDto();
					cons.setDescBanco(rs.getString("desc_banco"));
					cons.setIdChequera(rs.getString("id_chequera"));
					cons.setIdGrupo(rs.getString("rubro"));
					cons.setIdRubro(rs.getString("sub_Rubro"));
					cons.setFecValor(rs.getString("fec_valor"));
					cons.setFecOperacion(rs.getString("fec_operacion"));
					cons.setImporte(rs.getDouble("importe"));
					cons.setReferencia(rs.getString("referencia"));
					cons.setConcepto(rs.getString("concepto"));
					cons.setIdTipoMovto(rs.getString("id_tipo_movto"));
					return cons;
				}});
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Flujos, C:PosicionBancariaDaoImp, M:buscaDetalleBE");
		}
		return listFlujoBE;
	}
	
	@SuppressWarnings("unchecked")
	public List<PosicionBancariaDto> obtenerCuentas(String bancos, String empresas, int iSelec) {
		List<PosicionBancariaDto> listCtas = new ArrayList<PosicionBancariaDto>(); 
		StringBuffer sql = new StringBuffer();
		
		try {
			sql.append(" SELECT DISTINCT ID_BANCO, ID_CHEQUERA, NOM_EMPRESA \n");
			sql.append(" FROM CAT_CTA_BANCO   CCB, EMPRESA E \n");
			
			switch (iSelec) {
				case 1:
					sql.append(" WHERE ID_BANCO IN ("+ bancos +") AND CCB.NO_EMPRESA IN ("+ empresas +") \n");
					break;
				case 2:
					sql.append(" WHERE ID_BANCO IN ("+ bancos +") \n");
					break;
				case 4:
					sql.append(" WHERE CCB.NO_EMPRESA IN ("+ bancos +") \n"); //Esto es por que en la variable bancos viene el valor de las empresas para este caso nada m�s
					break;
			}
			sql.append(" 	AND CCB.NO_EMPRESA = E.NO_EMPRESA \n");
			
			listCtas = jdbcTemplate.query(sql.toString(), new RowMapper(){
				public PosicionBancariaDto mapRow(ResultSet rs, int idx) throws SQLException {
					PosicionBancariaDto cons = new PosicionBancariaDto();
					cons.setIdBanco(rs.getInt("ID_BANCO"));
					cons.setIdChequera(rs.getString("ID_CHEQUERA"));
					cons.setNomEmpresa(rs.getString("NOM_EMPRESA"));
					return cons;
				}});
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Flujos, C:PosicionBancariaDaoImp, M:obtenerCuentas");
		}
		return listCtas;
	}
	
	@SuppressWarnings("unchecked")
	public List<PosicionBancariaDto> obtieneFlujoEmp(String bancos, String ctas) {
		List<PosicionBancariaDto> listFlujoEmp = new ArrayList<PosicionBancariaDto>(); 
		StringBuffer sql = new StringBuffer();
		
		try {
			sql.append(" SELECT distinct e.no_empresa, e.nom_empresa \n");
			sql.append(" FROM empresa e, cat_cta_banco c \n");
			sql.append(" WHERE e.no_empresa = c.no_empresa \n");
			sql.append(" 	And c.id_banco IN ("+ bancos +") \n");
			sql.append(" 	And c.id_chequera IN ("+ ctas +") \n");
			sql.append(" ORDER BY e.no_empresa \n");
			
			listFlujoEmp = jdbcTemplate.query(sql.toString(), new RowMapper(){
				public PosicionBancariaDto mapRow(ResultSet rs, int idx) throws SQLException {
					PosicionBancariaDto cons = new PosicionBancariaDto();
					cons.setNoEmpresa(rs.getInt("no_empresa"));
					cons.setNomEmpresa(rs.getString("nom_empresa"));
					return cons;
				}});	
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Flujos, C:PosicionBancariaDaoImpl, M:obtieneFlujoEmp");
		}
		return listFlujoEmp;
	}
	
	@SuppressWarnings("unchecked")
	public List<PosicionBancariaDto> obtieneCuentaEmpresa(String sTipoMovto, String sBancos, String ctas, int iTipoSelec, List<Map<String, String>> parametros) {
		List<PosicionBancariaDto> listFlujoEmp = new ArrayList<PosicionBancariaDto>(); 
		StringBuffer sql = new StringBuffer();
		String sOcupaGrupo = "NO";
		
		try {
			if(iTipoSelec == 0) {
		        if(sOcupaGrupo.equals("NO")) {
		            sql.append(" SELECT '"+ sTipoMovto +"' AS 'TIPO_MOVTO', E.NO_EMPRESA, SUM(V.IMPORTE) AS IMPORTE,RTRIM(CONVERT(CHAR(6),E.NO_EMPRESA))  + ':' + E.NOM_EMPRESA AS NOM_EMPRESA, \n");
		            sql.append(" 		V.ID_DIVISA, V.FEC_VALOR,CG.ID_GRUPO,CG.DESC_GRUPO \n"); 
		            sql.append(" FROM VISTA_MOVIMIENTO V, CAT_RUBRO CR, CAT_GRUPO CG,EMPRESA E \n");
		            sql.append(" WHERE V.ID_RUBRO = CR.ID_RUBRO \n");
		            sql.append(" 	AND V.NO_EMPRESA=E.NO_EMPRESA \n");
		            sql.append(" 	AND CG.ID_GRUPO = CR.ID_GRUPO \n");
		            sql.append(" 	AND CG.ID_GRUPO IN (SELECT DISTINCT R.ID_GRUPO \n");
		            sql.append("                     	FROM CAT_RUBRO R, CAT_GRUPO G \n");
		            sql.append("                     	WHERE G.ID_GRUPO = R.ID_GRUPO \n");
		            //sql.append("                     	AND R.ID_GRUPO NOT IN ('TCBC','TCIC') \n");
		            sql.append("                     	AND R.ID_GRUPO NOT IN (0) \n");
		            sql.append("                     	AND (INGRESO_EGRESO = '" + sTipoMovto + "' \n");
		            sql.append(" 						OR INGRESO_EGRESO IS NULL)) \n");
		            sql.append(" 	AND ID_BANCO IN (" + sBancos + ") \n");
		            sql.append(" 	AND ID_CHEQUERA IN (" + ctas + ") \n");
		            sql.append(" 	AND ID_TIPO_MOVTO = '" + sTipoMovto + "' \n");
		        }else{
		            sql.append(" SELECT '"+ sTipoMovto +"' AS 'TIPO_MOVTO', E.NO_EMPRESA, SUM(V.IMPORTE) AS IMPORTE,V.BENEFICIARIO, V.ID_DIVISA, V.FEC_VALOR \n");
		            sql.append(" FROM VISTA_MOVIMIENTO V, CAT_RUBRO CR, CAT_GRUPO CG,EMPRESA E \n");
		            sql.append(" WHERE V.ID_RUBRO = CR.ID_RUBRO \n");
		            sql.append(" 	AND V.NO_EMPRESA=E.NO_EMPRESA \n");
		            sql.append(" 	AND CG.ID_GRUPO = CR.ID_GRUPO \n");
		            sql.append(" 	AND CG.ID_GRUPO IN (SELECT DISTINCT R.ID_GRUPO \n");
		            sql.append("                     	FROM CAT_RUBRO R, CAT_GRUPO G \n");
		            sql.append("                     	WHERE G.ID_GRUPO = R.ID_GRUPO \n");
		            //sql.append("                     	AND R.ID_GRUPO NOT IN ('TCBC','TCIC') \n");
		            sql.append("                     	AND R.ID_GRUPO NOT IN ('TCBC','TCIC','VTPB','ITPB','INPB','TPIC','INPP', 'INGU', 'ITGU','VTGU','TCPI', 'TPBC','TCFA','TCIE') \n");
		            sql.append("                     	AND (INGRESO_EGRESO = '" + sTipoMovto + "' \n");
		            sql.append(" 						OR INGRESO_EGRESO IS NULL)) \n");
		            sql.append(" 	AND V.ID_GRUPO = CR.ID_GRUPO \n");
		            sql.append(" 	AND ID_BANCO IN (" + sBancos + ") \n");
		            sql.append(" 	AND ID_CHEQUERA IN (" + ctas + ") \n");
		            sql.append(" 	AND ID_TIPO_MOVTO = '" + sTipoMovto + "' \n");
		        }
		        
		        if(sTipoMovto.equals("I")) {
		            //CHANGE OSCAR 1 + BEFORE SE OMITE 4102, 4103
		            //sql.append(" AND ((ID_TIPO_OPERACION IN (3100,3107,3101,3102,3103,4102,4103,3700,3701,3705,3706,3708,3709,1020,3120) \n");
		            //LATER
		            //sql.append(" AND ((ID_TIPO_OPERACION IN (3100,3107,3101,3102,3103,3700,3701,3705,3706,3708,3709,1020,3120,4102,4103,3500,3115,3154) \n"); //COMENTO ORR
		            sql.append(" AND ID_TIPO_OPERACION IN (3101,3107,3115,3154,3500) \n");
		            //CHANGE OSCAR 1
		            //sql.append(" AND ID_ESTATUS_MOV = 'A') or (ID_TIPO_OPERACION IN (3102,3103) \n"); //COMENTO ORR
		            //sql.append(" AND ID_ESTATUS_MOV = 'P') OR  (ID_TIPO_OPERACION IN (3500) AND ID_ESTATUS_MOV = 'W')) \n"); //COMENTO ORR
		        }else if(sTipoMovto.equals("E")) {
			            sql.append(" AND ((ID_TIPO_OPERACION IN (3200) \n");
			            sql.append(" AND ID_ESTATUS_MOV IN ('T','K','I','P','Z')) OR \n");
			            //CHANGE OSCAR 1 + BEFORE SE OMITE 4001
			            //sql.append(" (ID_TIPO_OPERACION IN (1012,1016,1017,1018,3022,4001,1019,1020,4104,3700,3701,3705,3706,3708,3709,1019) \n");
			            //LATER
			            sql.append(" (ID_TIPO_OPERACION IN (4001,1012,1016,1017,1018,3022,1019,1020,4104,3700,3701,3705,3706,3708,3709,1019) \n");
			            //CHANGE OSCAR 1
			            sql.append(" AND ID_ESTATUS_MOV = 'A')) \n");
		        }
		        /*
		        '*******************before change divisa
		        'sql.append(" AND LTRIM(RTRIM(ID_DIVISA)) IN (SELECT ID_DIVISA FROM CAT_CTA_BANCO \n");
		        'sql.append("                                 WHERE ID_BANCO IN (" & pqBancos & \n");) \n");
		        'sql.append("                                 AND ID_CHEQUERA IN (" & pqCuentas & \n");)) \n");
		        '*******************before change divisa
		        
		        '*******************after change divisa
		       ' sql.append(" AND RTRIM(LTRIM(ID_DIVISA)) IN ('" & txtDivisa.Text & \n");') \n");
		        '*******************after change divisa
		        */
		        sql.append(" AND FEC_VALOR BETWEEN '"+ parametros.get(0).get("fecIni") +"' AND '"+ parametros.get(0).get("fecFin") +"' \n");
		        
		        if(sOcupaGrupo.equals("NO")){
		            sql.append(" GROUP BY E.NO_EMPRESA, RTRIM(CONVERT(CHAR(6),E.NO_EMPRESA)) + ':' + E.NOM_EMPRESA, V.ID_DIVISA, V.FEC_VALOR, CG.ID_GRUPO, CG.DESC_GRUPO \n");
		            sql.append(" ORDER BY E.NO_EMPRESA, CG.ID_GRUPO \n");
		            
		            //sql.append(" GROUP BY E.NO_EMPRESA, RTRIM(CONVERT(CHAR(6),E.NO_EMPRESA)) + ':' + E.NOM_EMPRESA, V.ID_DIVISA, V.FEC_VALOR \n");
		            //sql.append(" ORDER BY E.NO_EMPRESA \n");		            
		        }else
		            sql.append(" GROUP BY E.NOM_EMPRESA,V.ID_DIVISA, V.FEC_VALOR \n");
		    }else{
	            if(sOcupaGrupo.equals("NO")) {
	                //sql.append(" SELECT COALESCE(SUM(V.IMPORTE),0) AS IMPORTE, CR.ID_GRUPO,  \n");
	                //sql.append(" V.ID_CHEQUERA, CG.DESC_GRUPO,V.FEC_VALOR \n");
	                sql.append(" SELECT '"+ sTipoMovto +"' AS 'TIPO_MOVTO', E.NO_EMPRESA, SUM(V.IMPORTE) AS IMPORTE,V.BENEFICIARIO, V.ID_DIVISA, V.FEC_VALOR \n");
	                sql.append(" FROM VISTA_MOVIMIENTO V, CAT_RUBRO CR, CAT_GRUPO CG,EMPRESA E \n");
	                sql.append(" WHERE V.ID_RUBRO = CR.ID_RUBRO \n");
	                sql.append(" 	AND CG.ID_GRUPO = CR.ID_GRUPO \n");
	                sql.append(" 	AND V.NO_EMPRESA=E.NO_EMPRESA \n");
	                sql.append(" 	AND  CR.ID_GRUPO IN (SELECT DISTINCT R.ID_GRUPO \n");
	                sql.append("                      		FROM CAT_RUBRO R \n");
	                sql.append("                      		WHERE (INGRESO_EGRESO = '"+ sTipoMovto +"' \n");
	                sql.append(" 							OR INGRESO_EGRESO IS NULL) \n");
	            }else {
	            	//sql.append(" SELECT COALESCE(SUM(V.IMPORTE),0) AS IMPORTE, V.ID_GRUPO,  \n");
	            	//sql.append(" V.ID_CHEQUERA, CG.DESC_GRUPO,V.FEC_VALOR \n");
	                sql.append(" SELECT '"+ sTipoMovto +"' AS 'TIPO_MOVTO', SUM(V.IMPORTE) AS IMPORTE,V.BENEFICIARIO, V.ID_DIVISA, V.FEC_VALOR \n");
	                sql.append(" FROM VISTA_MOVIMIENTO V, CAT_RUBRO CR, CAT_GRUPO CG,EMPRESA E \n");
	                sql.append(" WHERE V.ID_RUBRO = CR.ID_RUBRO \n");
	                sql.append(" 	AND V.ID_GRUPO = CR.ID_GRUPO \n");
	                sql.append(" 	AND CG.ID_GRUPO = CR.ID_GRUPO \n");
	                sql.append(" 	AND CG.ID_GRUPO = V.ID_GRUPO  \n");
	                sql.append(" 	AND V.NO_EMPRESA=E.NO_EMPRESA \n");
	                sql.append(" 	AND V.ID_GRUPO IN (SELECT DISTINCT R.ID_GRUPO \n");
	                sql.append("                      	FROM CAT_RUBRO R \n");
	                sql.append("                      	WHERE (INGRESO_EGRESO = '" + sTipoMovto + "' \n");
	                sql.append(" 						OR INGRESO_EGRESO IS NULL) \n");
	            }
		           
		        if(sTipoMovto.equals("I")) {
		            //CHANGE OSCAR 1
		            //BEFORE SE AGREGA VTPP ITPP VTGU ITGU
		            //sql.append("                      AND R.ID_GRUPO IN ('TCBC','TCIC','VTPB','ITPB')) \n");
		            //LATER
		            if(sOcupaGrupo.equals("NO"))
		                sql.append("                      AND R.ID_GRUPO IN (0)) \n");
		            else
		                sql.append("                      AND R.ID_GRUPO IN ('TCBC','TCIC','VTPB','ITPB','VTPP', 'ITPP', 'VTGU', 'ITGU','TCPI','TCFA','TCIE')) \n");
		            //CHANGE OSCAR 1
		        }else if(sTipoMovto.equals("E")) {
			            //sql.append("                      AND R.ID_GRUPO IN ('TPBC','TPIC')) \n");
			            //CHANGE OSCAR 2
			            //BEFORE SE AGREGA INGU
			            //sql.append("                      AND R.ID_GRUPO IN ('TPBC','TPIC','INPB','INPP')) \n");
			            //LATER
			            if(sOcupaGrupo.equals("NO"))
			            	sql.append("                      AND R.ID_GRUPO IN (0)) \n");
			            else
			            	sql.append("                      AND R.ID_GRUPO IN ('TPBC','TPIC','INPB','INPP','INGU')) \n");
			            //CHANGE OSCAR 2
		        }
		        
	            if(!sOcupaGrupo.equals("NO"))
	                sql.append(" AND V.ID_GRUPO = CR.ID_GRUPO \n");
	            
                sql.append(" AND V.ID_RUBRO = CR.ID_RUBRO \n");
                sql.append(" AND V.ID_BANCO IN ("+ sBancos +") \n");
                sql.append(" AND V.ID_CHEQUERA IN ("+ ctas +") \n");
                sql.append(" AND V.ID_TIPO_MOVTO = '" + sTipoMovto + "' \n");
                
                if(sTipoMovto.equals("I")) {
		            //sql.append(" AND V.ID_TIPO_OPERACION IN (1018,3700,3701,3022,1019,1020,3705,3706,3708,3709,3101) \n");
		            //sql.append(" AND V.ID_TIPO_OPERACION IN (1018,3700,3701,3022,1019,1020,3705,3706,3708,3709,3101,4102,4103,1020) \n"); //COMENTO ORR
                	sql.append(" AND V.ID_TIPO_OPERACION IN (3101,3107,3115,3154,3500) \n");
		                                                     //3100,3107,3101,3102,3103,4102,4103,3700,3701,3705,3706,3708,3709,1020 \n");
                }else if(sTipoMovto.equals("E")) {
		            //sql.append(" AND V.ID_TIPO_OPERACION IN (3700,3701,4001,3101,3107,1019,1020,3705,3706,3708,3709,1018,4104) \n");
		            sql.append(" AND V.ID_TIPO_OPERACION IN (3700,3701,4001,3101,3107,1019,1020,3705,3706,3708,3709,1018,4104,3200) \n");
                }
		        /*
		        //sql.append(" AND V.ID_ESTATUS_MOV = 'A' \n");
		        sql.append(" AND V.ID_ESTATUS_MOV IN( 'A','T', 'K') \n");
		        
		        
		        '*******************before change divisa
		        'sql.append(" AND RTRIM(LTRIM(V.ID_DIVISA)) IN (SELECT ID_DIVISA FROM CAT_CTA_BANCO \n");
		        'sql.append("                                   WHERE ID_BANCO IN (" & pqBancos & \n");) \n");
		        'sql.append("                                   AND ID_CHEQUERA IN (" & pqCuentas & \n");)) \n");
		        '*******************before change divisa
		        */
		        //*******************after change divisa
		        sql.append(" AND RTRIM(LTRIM(V.ID_DIVISA)) IN ('"+ parametros.get(0).get("divisa") +"' \n");
		        //*******************after change divisa
		        
		        sql.append(" AND FEC_VALOR BETWEEN '"+ parametros.get(0).get("fecIni") +"' AND '"+ parametros.get(0).get("fecFin") +"' \n");
		        
		        if(sOcupaGrupo.equals("NO")) {
		            //sql.append(" GROUP BY CR.ID_GRUPO,  V.ID_CHEQUERA,  CG.DESC_GRUPO,V.FEC_VALOR"
		            sql.append(" GROUP BY E.NO_EMPRESA, E.NOM_EMPRESA,V.ID_DIVISA, V.FEC_VALOR ");
		        }else{
		            //sql.append(" GROUP BY V.ID_GRUPO,  V.ID_CHEQUERA,  CG.DESC_GRUPO,V.FEC_VALOR"
		            sql.append(" GROUP BY E.NOM_EMPRESA,V.ID_DIVISA, V.FEC_VALOR ");
		        }
		    }
			
			listFlujoEmp = jdbcTemplate.query(sql.toString(), new RowMapper(){
				public PosicionBancariaDto mapRow(ResultSet rs, int idx) throws SQLException {
					PosicionBancariaDto cons = new PosicionBancariaDto();
					cons.setImporte(rs.getDouble("IMPORTE"));
					cons.setNoEmpresa(rs.getInt("NO_EMPRESA"));
					cons.setNomEmpresa(rs.getString("NOM_EMPRESA"));
					cons.setIdDivisa(rs.getString("ID_DIVISA"));
					cons.setFecValor(rs.getString("FEC_VALOR"));
					cons.setIdGrupo(rs.getString("ID_GRUPO"));
					cons.setDescGrupo(rs.getString("DESC_GRUPO"));
					cons.setIdTipoMovto(rs.getString("TIPO_MOVTO"));
					return cons;
				}});	
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Flujos, C:PosicionBancariaDaoImpl, M:obtieneCuentaEmpresa");
		}
		return listFlujoEmp;
	}
	
	@SuppressWarnings("unchecked")
	public List<PosicionBancariaDto> obtieneSaldodiario(String bancos, String ctas, boolean chkSaldos, String fecha, int noEmpresa, String sDivisa) {
		List<PosicionBancariaDto> listFlujoEmp = new ArrayList<PosicionBancariaDto>(); 
		StringBuffer sql = new StringBuffer();
		Date fechaDate = funciones.ponerFechaDate(fecha);
		consultasGenerales = new ConsultasGenerales(jdbcTemplate);
		Date fechaHoy = consultasGenerales.obtenerFechaHoy(); 
		
		try {
			if(chkSaldos) {
				sql.append(" SELECT COALESCE(SUM(SALDO), 0) AS SALDO \n");
				sql.append(" FROM SALDOS_BANCARIOS \n");
			}else {
				sql.append(" SELECT COALESCE(SUM(SALDO_INICIAL),0) AS SALDO \n");
				
				if(fechaDate.compareTo(fechaHoy) == 0)
					sql.append(" FROM CAT_CTA_BANCO\n");
				else
					sql.append(" FROM HIST_CAT_CTA_BANCO \n");
			}
			sql.append(" WHERE ID_BANCO IN (" + bancos + ") \n");
			sql.append(" 	AND NO_EMPRESA = "+ noEmpresa +" \n");
			
			if(chkSaldos)
				sql.append(" 	AND FECHA = '" + funciones.ponerFechaSola(consultasGenerales.obtenerFechaHoy()) + "' \n");
			else
				sql.append(" 	AND ID_DIVISA = '" + sDivisa + "' \n");
			
			if(fechaDate.compareTo(fechaHoy) != 0) {
				sql.append(" AND FEC_VALOR BETWEEN '"+ fecha +"' And '"+ fecha +"' \n");
			}
			
			listFlujoEmp = jdbcTemplate.query(sql.toString(), new RowMapper(){
				public PosicionBancariaDto mapRow(ResultSet rs, int idx) throws SQLException {
					PosicionBancariaDto cons = new PosicionBancariaDto();
					cons.setImporte(rs.getDouble("SALDO"));
					return cons;
				}});	
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Flujos, C:PosicionBancariaDaoImpl, M:obtieneSaldodiario");
		}
		return listFlujoEmp;
	}
	
	//query para el reporte de ficha valor por cuenta
	@SuppressWarnings("unchecked")
	public List<PosicionBancariaDto> obtieneFichaCuenta(String sTipoMovto, String sBancos, String ctas, int iTipoSelec, List<Map<String, String>> parametros) {
		StringBuffer sql = new StringBuffer();
		String psOcupaGrupo = "NO";
		String ctasPreliminar = "";
		List<PosicionBancariaDto> listFlujoEmp = new ArrayList<PosicionBancariaDto> ();
		boolean chkSaldos = parametros.get(0).get("chkSaldos").equals("true") ? true : false;
		
		try {
			if(iTipoSelec == 0) {
		        if(psOcupaGrupo.equals("NO")) {
		            sql.append(" SELECT V.NO_EMPRESA, SUM(V.IMPORTE) AS IMPORTE, CR.ID_GRUPO, V.ID_BANCO, \n");
		            sql.append(" V.ID_CHEQUERA, CG.DESC_GRUPO, V.FEC_VALOR \n");
		            sql.append(" FROM VISTA_MOVIMIENTO V, CAT_RUBRO CR, CAT_GRUPO CG \n");
		            sql.append(" WHERE V.ID_RUBRO = CR.ID_RUBRO \n");
		            sql.append(" AND CG.ID_GRUPO = CR.ID_GRUPO \n");
		            sql.append(" AND CG.ID_GRUPO IN (SELECT DISTINCT R.ID_GRUPO \n");
		            sql.append("                     FROM CAT_RUBRO R, CAT_GRUPO G \n");
		            sql.append("                     WHERE G.ID_GRUPO = R.ID_GRUPO \n");
		            //sql.append("                     AND R.ID_GRUPO NOT IN ('TCBC','TCIC') \n");
		            sql.append("                     AND R.ID_GRUPO NOT IN (0) \n");
		            sql.append("                     AND (INGRESO_EGRESO = '" + sTipoMovto + "' \n");
		            sql.append(" OR INGRESO_EGRESO IS NULL)) \n");
		            sql.append(" AND ID_BANCO IN (" + sBancos + ") \n");
		            sql.append(" AND ID_CHEQUERA IN (" + ctas + ") \n");
		            sql.append(" AND ID_TIPO_MOVTO = '" + sTipoMovto + "' \n");
		        }else {
		            sql.append(" SELECT V.NO_EMPRESA, SUM(V.IMPORTE) AS IMPORTE, V.ID_GRUPO, V.ID_BANCO, \n");
		            sql.append(" V.ID_CHEQUERA, CG.DESC_GRUPO, V.FEC_VALOR \n");
		            sql.append(" FROM VISTA_MOVIMIENTO V, CAT_RUBRO CR, CAT_GRUPO CG \n");
		            sql.append(" WHERE V.ID_RUBRO = CR.ID_RUBRO \n");
		            sql.append(" AND CG.ID_GRUPO = CR.ID_GRUPO \n");
		            sql.append(" AND CG.ID_GRUPO IN (SELECT DISTINCT R.ID_GRUPO \n");
		            sql.append("                     FROM CAT_RUBRO R, CAT_GRUPO G \n");
		            sql.append("                     WHERE G.ID_GRUPO = R.ID_GRUPO \n");
		            //sql.append("                     AND R.ID_GRUPO NOT IN ('TCBC','TCIC') \n");
		            sql.append("                     AND R.ID_GRUPO NOT IN ('TCBC','TCIC','VTPB','ITPB','INPB','TPIC','INPP', 'INGU', 'ITGU','VTGU','TCPI', 'TPBC','TCFA','TCIE') \n");
		            sql.append("                     AND (INGRESO_EGRESO = '" + sTipoMovto + "' \n");
		            sql.append(" OR INGRESO_EGRESO IS NULL)) \n");
		            sql.append(" AND V.ID_GRUPO = CR.ID_GRUPO \n");
		            sql.append(" AND ID_BANCO IN (" + sBancos + ") \n");
		            sql.append(" AND ID_CHEQUERA IN (" + ctas + ") \n");
		            sql.append(" AND ID_TIPO_MOVTO = '" + sTipoMovto + "' \n");
		        }
		        
		        if(sTipoMovto.equals("I")) {
		        	/*
		            'CHANGE OSCAR 1
		            'BEFORE SE OMITE 4102, 4103
		            'sql.append(" AND ((ID_TIPO_OPERACION IN (3100,3107,3101,3102,3103,4102,4103,3700,3701,3705,3706,3708,3709,1020,3120) \n");
		            'LATER
		            */
		            //sql.append(" AND ((ID_TIPO_OPERACION IN (3100,3107,3101,3102,3103,3700,3701,3705,3706,3708,3709,1020,3120,4102,4103,3500, 3115, 3154) \n"); //COMENTO ORR
		        	sql.append(" AND ID_TIPO_OPERACION IN (3101,3107,3115,3154,3500) \n");
		            //CHANGE OSCAR 1
		            //sql.append(" AND (ID_ESTATUS_MOV = 'A' or ID_ESTATUS_MOV = 'W' )) or (ID_TIPO_OPERACION IN (3102,3103) \n"); //COMENTO ORR
		            //sql.append(" AND ID_ESTATUS_MOV = 'P')) \n"); //COMENTO ORR
		            
		            if(chkSaldos) {
		                sql.append(" AND ORIGEN_MOV NOT IN ('BCE') \n");
		            }
		        }else if(sTipoMovto.equals("E")) {
		            sql.append(" AND ((ID_TIPO_OPERACION IN (3200) \n");
		            sql.append(" AND ID_ESTATUS_MOV IN ('T','K','I','P','Z')) OR \n");
		            /*'CHANGE OSCAR 1
		            'BEFORE SE OMITE 4001
		            'sql.append(" (ID_TIPO_OPERACION IN (1012,1016,1017,1018,3022,4001,1019,1020,4104,3700,3701,3705,3706,3708,3709,1019) \n");
		            'LATER*/
		            sql.append(" (ID_TIPO_OPERACION IN (4001,1012,1016,1017,1018,3022,1019,1020,4104,3700,3701,3705,3706,3708,3709,1019) \n");
		            //'CHANGE OSCAR 1
		            sql.append(" AND ID_ESTATUS_MOV = 'A')) \n");
		            
		            if(chkSaldos) {
		                sql.append(" AND ORIGEN_MOV NOT IN ('BCE') \n");
		            }
		        }
		        /*
		        '*******************before change divisa
		        'sql.append(" AND LTRIM(RTRIM(ID_DIVISA)) IN (SELECT ID_DIVISA FROM CAT_CTA_BANCO \n");
		        'sql.append("                                 WHERE ID_BANCO IN (" & pqBancos & ") \n");
		        'sql.append("                                 AND ID_CHEQUERA IN (" & pqCuentas & ")) \n");
		        '*******************before change divisa
		        */
		        //'*******************after change divisa
		        sql.append(" AND RTRIM(LTRIM(ID_DIVISA)) IN ('" + parametros.get(0).get("divisa") + "') \n");
		        //'*******************after change divisa
		        
		        sql.append(" AND FEC_VALOR BETWEEN '" + parametros.get(0).get("fecIni") + "' AND '" + parametros.get(0).get("fecFin") + "' \n");
		        
		        if(psOcupaGrupo.equals("NO")) {
		            sql.append(" GROUP BY V.NO_EMPRESA, CR.ID_GRUPO, V.ID_BANCO, V.ID_CHEQUERA, CG.DESC_GRUPO, V.FEC_VALOR ");
		        }else {
		            sql.append(" GROUP BY V.NO_EMPRESA, V.ID_GRUPO, V.ID_BANCO, V.ID_CHEQUERA, CG.DESC_GRUPO, V.FEC_VALOR ");
		        }
		    }else {
		            if(psOcupaGrupo.equals("NO")) {
		                sql.append(" SELECT V.NO_EMPRESA, COALESCE(SUM(V.IMPORTE),0) AS IMPORTE, CR.ID_GRUPO, \n");
		                sql.append(" V.ID_CHEQUERA, CG.DESC_GRUPO,V.FEC_VALOR \n");
		                sql.append(" FROM VISTA_MOVIMIENTO V, CAT_RUBRO CR, CAT_GRUPO CG \n");
		                sql.append(" WHERE CR.ID_RUBRO = CR.ID_RUBRO \n");
		                sql.append(" AND CG.ID_GRUPO = CR.ID_GRUPO \n");
		                sql.append(" AND  CR.ID_GRUPO IN (SELECT DISTINCT R.ID_GRUPO \n");
		                sql.append("                      FROM CAT_RUBRO R \n");
		                sql.append("                      WHERE (INGRESO_EGRESO = '" + sTipoMovto + "' \n");
		                sql.append(" OR INGRESO_EGRESO IS NULL) \n");
		            }else {
		                sql.append(" SELECT V.NO_EMPRESA, COALESCE(SUM(V.IMPORTE),0) AS IMPORTE, V.ID_GRUPO, \n");
		                sql.append(" V.ID_CHEQUERA, CG.DESC_GRUPO,V.FEC_VALOR \n");
		                sql.append(" FROM VISTA_MOVIMIENTO V, CAT_RUBRO CR, CAT_GRUPO CG \n");
		                sql.append(" WHERE V.ID_RUBRO = CR.ID_RUBRO \n");
		                sql.append(" AND V.ID_GRUPO = CR.ID_GRUPO \n");
		                sql.append(" AND CG.ID_GRUPO = CR.ID_GRUPO \n");
		                sql.append("  AND CG.ID_GRUPO = V.ID_GRUPO  \n");
		                sql.append(" AND  V.ID_GRUPO IN (SELECT DISTINCT R.ID_GRUPO \n");
		                sql.append("                      FROM CAT_RUBRO R \n");
		                sql.append("                      WHERE (INGRESO_EGRESO = '" + sTipoMovto + "' \n");
		                sql.append(" OR INGRESO_EGRESO IS NULL) \n");
		        
		            }
		        if(sTipoMovto.equals("I")) {
		            /*'CHANGE OSCAR 1
		            'BEFORE SE AGREGA VTPP ITPP VTGU ITGU
		            'sql.append("                      AND R.ID_GRUPO IN ('TCBC','TCIC','VTPB','ITPB')) \n");
		            'LATER*/
		            if(psOcupaGrupo.equals("NO")) {
		            	sql.append("                      AND R.ID_GRUPO IN (0)) \n");
		            }else {
		                sql.append("                      AND R.ID_GRUPO IN ('TCBC','TCIC','VTPB','ITPB','VTPP', 'ITPP', 'VTGU', 'ITGU','TCPI','TCFA','TCIE')) \n");
		            }
		            //'CHANGE OSCAR 1
		        }else if(sTipoMovto.equals("E")) {
		            /*'sql.append("                      AND R.ID_GRUPO IN ('TPBC','TPIC')) \n");
		            'CHANGE OSCAR 2
		            'BEFORE SE AGREGA INGU
		            'sql.append("                      AND R.ID_GRUPO IN ('TPBC','TPIC','INPB','INPP')) \n");
		            'LATER*/
		            if(psOcupaGrupo.equals("NO")) {
		            	sql.append("                      AND R.ID_GRUPO IN (0)) \n");
		            }else {
		            	sql.append("                      AND R.ID_GRUPO IN ('TPBC','TPIC','INPB','INPP','INGU')) \n");
		            }
		            //'CHANGE OSCAR 2
		        }
		        
		            if(psOcupaGrupo.equals("NO")) {
		                sql.append("  ");
		            }else {
		                sql.append(" AND V.ID_GRUPO = CR.ID_GRUPO \n");
		            }
		                sql.append(" AND V.ID_RUBRO = CR.ID_RUBRO \n");
		                sql.append(" AND V.ID_BANCO IN (" + sBancos + ") \n");
		                sql.append(" AND V.ID_CHEQUERA IN (" + ctas + ") \n");
		                sql.append(" AND V.ID_TIPO_MOVTO = '" + sTipoMovto + "' \n");
		                            
		                            
		        if(sTipoMovto.equals("I")) {
		            //'sql.append(" AND V.ID_TIPO_OPERACION IN (1018,3700,3701,3022,1019,1020,3705,3706,3708,3709,3101) \n");
		            //sql.append(" AND V.ID_TIPO_OPERACION IN (1018,3700,3701,3022,1019,1020,3705,3706,3708,3709,3101, 3107,4102,4103,1020,3500, 3115, 3154) \n"); //COMENTO ORR
		        	sql.append(" AND V.ID_TIPO_OPERACION IN (3101,3107,3115,3154,3500) \n");
		                                                             //'3100,3107,3101,3102,3103,4102,4103,3700,3701,3705,3706,3708,3709,1020
		        }else if(sTipoMovto.equals("E")) {
		            //'sql.append(" AND V.ID_TIPO_OPERACION IN (3700,3701,4001,3101,3107,1019,1020,3705,3706,3708,3709,1018,4104) \n");
		            sql.append(" AND V.ID_TIPO_OPERACION IN (3700,3701,4001,3101,3107,1019,1020,3705,3706,3708,3709,1018,4104,3200) \n");
		        }
		        //'sql.append(" AND V.ID_ESTATUS_MOV = 'A' \n");
		        sql.append(" AND V.ID_ESTATUS_MOV IN( 'A','T', 'K','W') \n");
		        /*
		        '*******************before change divisa
		        'sql.append(" AND RTRIM(LTRIM(V.ID_DIVISA)) IN (SELECT ID_DIVISA FROM CAT_CTA_BANCO \n");
		        'sql.append("                                   WHERE ID_BANCO IN (" & pqBancos & ") \n");
		        'sql.append("                                   AND ID_CHEQUERA IN (" & pqCuentas & ")) \n");
		        '*******************before change divisa
		        */
		        //'*******************after change divisa
		        sql.append(" AND RTRIM(LTRIM(V.ID_DIVISA)) IN ('" + parametros.get(0).get("divisa") + "') \n");
		        //'*******************after change divisa
		        
		        sql.append(" AND FEC_VALOR BETWEEN '" + parametros.get(0).get("fecIni") + "' AND '" + parametros.get(0).get("fecFin") + "' \n");
		        
		        if(psOcupaGrupo.equals("NO")) {
		            sql.append(" GROUP BY V.NO_EMPRESA, CR.ID_GRUPO,  V.ID_CHEQUERA,  CG.DESC_GRUPO,V.FEC_VALOR ");
		        }else {
		            sql.append(" GROUP BY V.NO_EMPRESA, V.ID_GRUPO,  V.ID_CHEQUERA,  CG.DESC_GRUPO,V.FEC_VALOR ");
		        }
		        //sql.append(" ORDER BY V.NO_EMPRESA, V.ID_CHEQUERA ");
		        //'psquery = psquery & Chr(10) & "order by V.ID_CHEQUERA"
		    }
			
			if(chkSaldos) {
				if(sTipoMovto.equals("I")) {
					ctasPreliminar = obtieneFichaCuentaPreliminar("E", sBancos, ctas, 0, parametros);
					sql.append(" \n UNION ALL \n");
					sql.append(ctasPreliminar);
				}else if(sTipoMovto.equals("I")) {
					ctasPreliminar = obtieneFichaCuentaPreliminar("E", sBancos, ctas, 0, parametros);
					sql.append(" \n UNION ALL \n");
					sql.append(ctasPreliminar);
					ctasPreliminar = obtieneFichaCuentaPreliminarDLS("E", sBancos, ctas, 0);
					sql.append(" \n UNION ALL \n");
					sql.append(ctasPreliminar);
				}
			}
			sql.append(" ORDER BY V.NO_EMPRESA, V.ID_CHEQUERA ");
			
			listFlujoEmp = jdbcTemplate.query(sql.toString(), new RowMapper(){
				public PosicionBancariaDto mapRow(ResultSet rs, int idx) throws SQLException {
					PosicionBancariaDto cons = new PosicionBancariaDto();
					cons.setImporte(rs.getDouble("IMPORTE"));
					cons.setIdGrupo(rs.getString("ID_GRUPO"));
					cons.setIdBanco(rs.getInt("ID_BANCO"));
					cons.setIdChequera(rs.getString("ID_CHEQUERA"));
					cons.setDescGrupo(rs.getString("DESC_GRUPO"));
					cons.setFecValor(rs.getString("FEC_VALOR"));
					cons.setNoEmpresa(rs.getInt("NO_EMPRESA"));
					return cons;
				}
			});
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Flujos, C:PosicionBancariaDaoImpl, M:obtieneSaldodiario");
		}
		return listFlujoEmp;
	}
	
	//query para el reporte de ficha valor por cuenta
	//@SuppressWarnings("unchecked")
	public String obtieneFichaCuentaPreliminar(String sTipoMovto, String sBancos, String ctas, int iTipoSelec, List<Map<String, String>> parametros) {
		StringBuffer sql = new StringBuffer();
		String psOcupaGrupo = "NO";
		//List<PosicionBancariaDto> listFlujoEmp = new ArrayList<PosicionBancariaDto> ();
		consultasGenerales = new ConsultasGenerales(jdbcTemplate);
		
		try {
			if(iTipoSelec == 0) {		        
			        if(psOcupaGrupo.equals("NO")) {
			            sql.append(" SELECT V.NO_EMPRESA, SUM(V.IMPORTE) AS IMPORTE, CR.ID_GRUPO, V.ID_BANCO, \n");
			            sql.append(" V.ID_CHEQUERA, CG.DESC_GRUPO, V.FEC_VALOR \n");
			            sql.append(" FROM VISTA_MOVIMIENTO V, CAT_RUBRO CR, CAT_GRUPO CG \n");
			            sql.append(" WHERE V.ID_RUBRO = CR.ID_RUBRO \n");
			            sql.append(" AND CG.ID_GRUPO = CR.ID_GRUPO \n");
			            sql.append(" AND CG.ID_GRUPO IN (SELECT DISTINCT R.ID_GRUPO \n");
			            sql.append("                     FROM CAT_RUBRO R, CAT_GRUPO G \n");
			            sql.append("                     WHERE G.ID_GRUPO = R.ID_GRUPO \n");
			            sql.append("                     AND R.ID_GRUPO NOT IN (0) \n");
			            sql.append("                     AND (INGRESO_EGRESO = '" + sTipoMovto + "' \n");
			            sql.append(" OR INGRESO_EGRESO IS NULL)) \n");
			            sql.append(" AND ID_BANCO IN (" + sBancos + ") \n");
			            sql.append(" AND ID_CHEQUERA IN (" + ctas + ") \n");
			            sql.append(" AND ID_TIPO_MOVTO = '" + sTipoMovto + "' \n");
			        }else {
			            sql.append(" SELECT V.NO_EMPRESA, SUM(V.IMPORTE) AS IMPORTE, V.ID_GRUPO, V.ID_BANCO, \n");
			            sql.append(" V.ID_CHEQUERA, CG.DESC_GRUPO, V.FEC_VALOR \n");
			            sql.append(" FROM VISTA_MOVIMIENTO V, CAT_RUBRO CR, CAT_GRUPO CG \n");
			            sql.append(" WHERE V.ID_RUBRO = CR.ID_RUBRO \n");
			            sql.append(" AND CG.ID_GRUPO = CR.ID_GRUPO \n");
			            sql.append(" AND CG.ID_GRUPO IN (SELECT DISTINCT R.ID_GRUPO \n");
			            sql.append("                     FROM CAT_RUBRO R, CAT_GRUPO G \n");
			            sql.append("                     WHERE G.ID_GRUPO = R.ID_GRUPO \n");
			            sql.append("                     AND R.ID_GRUPO NOT IN ('TCBC','TCIC','VTPB','ITPB','INPB','TPIC','INPP', 'INGU', 'ITGU','VTGU','TCPI', 'TPBC','TCFA','TCIE') \n");
			            sql.append("                     AND (INGRESO_EGRESO = '" + sTipoMovto + "' \n");
			            sql.append(" OR INGRESO_EGRESO IS NULL)) \n");
			            sql.append(" AND V.ID_GRUPO = CR.ID_GRUPO \n");
			            sql.append(" AND ID_BANCO IN (" + sBancos + ") \n");
			            sql.append(" AND ID_CHEQUERA IN (" + ctas + ") \n");
			            sql.append(" AND ID_TIPO_MOVTO = '" + sTipoMovto + "' \n");
			        }
			        
			        if(sTipoMovto.equals("I")) {
			        /*
			'            sql.append(" AND ((ID_TIPO_OPERACION IN (3100,3107,3101,3102,3103,3700,3701,3705,3706,3708,3709,1020,3120,4102,4103,3500) \n");
			'
			'            sql.append(" AND (ID_ESTATUS_MOV = 'A' or ID_ESTATUS_MOV = 'W' )"
			'            sql.append(" AND FEC_VALOR BETWEEN '" & Format$(TxtFecIni.Text, "dd/mm/yyyy") & "'"
			'            sql.append(" AND '" & Format$(TxtFecfin.Text, "dd/mm/yyyy") & "' \n");
			'            sql.append(" )"
			'
			'            sql.append(" or (ID_TIPO_OPERACION IN (3102,3103) \n");
			'            sql.append(" AND ID_ESTATUS_MOV = 'P' \n");
			'            sql.append(" AND FEC_VALOR BETWEEN '" & Format$(TxtFecIni.Text, "dd/mm/yyyy") & "'"
			'            sql.append(" AND '" & Format$(TxtFecfin.Text, "dd/mm/yyyy") & "' \n");
			'            sql.append(" )"
			            
			            
			                'sql.append(" and ((ID_TIPO_OPERACION IN (3101)AND ID_ESTATUS_MOV = 'A' and origen_mov='SET'and fec_valor='" & GT_FECHA_HOY & "')" & " or "
			          */ 
			        	sql.append(" and ((ID_TIPO_OPERACION IN (3801)AND ID_ESTATUS_MOV = 'L' and origen_mov='AS4') \n");
			            
			            
			            sql.append(" )");
			            
			        }else if(sTipoMovto.equals("E")) {
			            /*
			'            sql.append(" AND ((ID_TIPO_OPERACION IN (3200) \n");
			'            sql.append(" AND ID_ESTATUS_MOV IN ('T','K','I','P','Z') \n");
			'            sql.append(" AND FEC_VALOR BETWEEN '" & Format$(TxtFecIni.Text, "dd/mm/yyyy") & "'"
			'            sql.append(" AND '" & Format$(TxtFecfin.Text, "dd/mm/yyyy") & "' \n");
			'            sql.append(" )"
			'            sql.append(" OR (ID_TIPO_OPERACION IN (4001,1012,1016,1017,1018,3022,1019,1020,4104,3700,3701,3705,3706,3708,3709,1019) \n");
			'            sql.append(" AND ID_ESTATUS_MOV = 'A' \n");
			'            sql.append(" AND FEC_VALOR BETWEEN '" & Format$(TxtFecIni.Text, "dd/mm/yyyy") & "'"
			'            sql.append(" AND '" & Format$(TxtFecfin.Text, "dd/mm/yyyy") & "' \n");
			'            sql.append(" )"
			            
			            'if( chk_saldos.Value = 1 ) {*/
			            sql.append(" AND ((id_tipo_operacion =3000 and id_estatus_mov='N' and origen_mov='AS4')  \n");
			            sql.append(" or (ID_ESTATUS_MOV = 'L' and id_tipo_operacion in (3801) and origen_mov='AS4') \n");
			            sql.append(" or (ID_ESTATUS_MOV = 'C' and id_tipo_operacion in (3000) and origen_mov='CVD' and fec_valor='" + funciones.ponerFechaSola(consultasGenerales.obtenerFechaHoy()) + "') \n");
			           //' sql.append(" or (ID_ESTATUS_MOV = 'A' and id_tipo_operacion in (1018) and origen_mov='SET' and fec_valor='" & GT_FECHA_HOY & "') \n");
			
			           // '}
			            
			            sql.append(" )");
			        }
			                
			        sql.append(" AND RTRIM(LTRIM(ID_DIVISA)) IN ('" + parametros.get(0).get("divisa") + "') \n");
			        
			        
			        if(psOcupaGrupo.equals("NO")) {
			            sql.append(" GROUP BY V.NO_EMPRESA, CR.ID_GRUPO, V.ID_BANCO, V.ID_CHEQUERA, CG.DESC_GRUPO, V.FEC_VALOR");
			        }else {
			            sql.append(" GROUP BY V.NO_EMPRESA, V.ID_GRUPO, V.ID_BANCO, V.ID_CHEQUERA, CG.DESC_GRUPO, V.FEC_VALOR");
			        }
			    
			    }else {
			            if(psOcupaGrupo.equals("NO")) {
			                sql.append(" SELECT V.NO_EMPRESA, COALESCE(SUM(V.IMPORTE),0) AS IMPORTE, CR.ID_GRUPO, \n");
			                sql.append(" V.ID_CHEQUERA, CG.DESC_GRUPO,V.FEC_VALOR \n");
			                sql.append(" FROM VISTA_MOVIMIENTO V, CAT_RUBRO CR, CAT_GRUPO CG \n");
			                sql.append(" WHERE CR.ID_RUBRO = CR.ID_RUBRO \n");
			                sql.append(" AND CG.ID_GRUPO = CR.ID_GRUPO \n");
			                sql.append(" AND  CR.ID_GRUPO IN (SELECT DISTINCT R.ID_GRUPO \n");
			                sql.append("                      FROM CAT_RUBRO R \n");
			                sql.append("                      WHERE (INGRESO_EGRESO = '" + sTipoMovto + "' \n");
			                sql.append(" OR INGRESO_EGRESO IS NULL) \n");
			            }else {
			                sql.append(" SELECT V.NO_EMPRESA, COALESCE(SUM(V.IMPORTE),0) AS IMPORTE, V.ID_GRUPO, \n");
			                sql.append(" V.ID_CHEQUERA, CG.DESC_GRUPO,V.FEC_VALOR \n");
			                sql.append(" FROM VISTA_MOVIMIENTO V, CAT_RUBRO CR, CAT_GRUPO CG \n");
			                sql.append(" WHERE V.ID_RUBRO = CR.ID_RUBRO \n");
			                sql.append(" AND V.ID_GRUPO = CR.ID_GRUPO \n");
			                sql.append(" AND CG.ID_GRUPO = CR.ID_GRUPO \n");
			                sql.append("  AND CG.ID_GRUPO = V.ID_GRUPO  \n");
			                sql.append(" AND  V.ID_GRUPO IN (SELECT DISTINCT R.ID_GRUPO \n");
			                sql.append("                      FROM CAT_RUBRO R \n");
			                sql.append("                      WHERE (INGRESO_EGRESO = '" + sTipoMovto + "' \n");
			                sql.append(" OR INGRESO_EGRESO IS NULL) \n");
			            }
			        if(sTipoMovto.equals("I")) {
			            if(psOcupaGrupo.equals("NO")) {
			                sql.append("                      AND R.ID_GRUPO IN (0)) \n");
			            }else {
			                sql.append("                      AND R.ID_GRUPO IN ('TCBC','TCIC','VTPB','ITPB','VTPP', 'ITPP', 'VTGU', 'ITGU','TCPI','TCFA','TCIE')) \n");
			            }
			        }else if(sTipoMovto.equals("E")) {
			            if( psOcupaGrupo.equals("NO")) {
			                    sql.append("                      AND R.ID_GRUPO IN (0)) \n");
			            }else {
			                    sql.append("                      AND R.ID_GRUPO IN ('TPBC','TPIC','INPB','INPP','INGU')) \n");
			            }
			            
			        }
			        
			            if( psOcupaGrupo.equals("NO")) {
			                sql.append("  ");
			            }else {
			                sql.append(" AND V.ID_GRUPO = CR.ID_GRUPO \n");
			            }
			                sql.append(" AND V.ID_RUBRO = CR.ID_RUBRO \n");
			                sql.append(" AND V.ID_BANCO IN (" + sBancos + ") \n");
			                sql.append(" AND V.ID_CHEQUERA IN (" + ctas + ") \n");
			                sql.append(" AND V.ID_TIPO_MOVTO = '" + sTipoMovto + "' \n");
			                            
			                            
			        if(sTipoMovto.equals("I")) {
			/*'            sql.append(" AND ((V.ID_TIPO_OPERACION IN (1018,3700,3701,3022,1019,1020,3705,3706,3708,3709,3101,4102,4103,1020,3500) \n");
			'                                                            '3100,3107,3101,3102,3103,4102,4103,3700,3701,3705,3706,3708,3709,1020"
			'            sql.append(" AND V.ID_ESTATUS_MOV IN( 'A','T', 'K','W') \n");
			'            sql.append(" AND FEC_VALOR BETWEEN '" & Format$(TxtFecIni.Text, "dd/mm/yyyy") & "'"
			'            sql.append(" AND '" & Format$(TxtFecfin.Text, "dd/mm/yyyy") & "' \n");
			'            sql.append(" )"
			            
			'            if( chk_saldos.Value = 1 ) {
			*/                sql.append(" or (ID_TIPO_OPERACION IN (3101)AND ID_ESTATUS_MOV = 'A' and origen_mov='SET') or (ID_TIPO_OPERACION IN (3801)AND ID_ESTATUS_MOV = 'L' and origen_mov='AS4') \n");
			//'            }
			            sql.append(" )");
			            
			        }else if( sTipoMovto.equals("E")) {
			/*'            sql.append(" AND ((V.ID_TIPO_OPERACION IN (3700,3701,4001,3101,3107,1019,1020,3705,3706,3708,3709,1018,4104,3200) \n");
			'            sql.append(" AND V.ID_ESTATUS_MOV IN( 'A','T', 'K','W') \n");
			'            sql.append(" AND FEC_VALOR BETWEEN '" & Format$(TxtFecIni.Text, "dd/mm/yyyy") & "'"
			'            sql.append(" AND '" & Format$(TxtFecfin.Text, "dd/mm/yyyy") & "' \n");
			'            sql.append(" )"
			            
			'            if( chk_saldos.Value = 1 ) {
			*/                sql.append(" and  ((id_tipo_operacion =3000 and id_estatus_mov='N' and origen_mov='AS4')  \n");
			                sql.append(" or (ID_ESTATUS_MOV = 'L' and id_tipo_operacion in (3801) and origen_mov='AS4') \n");
			                sql.append(" or (ID_ESTATUS_MOV = 'C' and id_tipo_operacion in (3000) and origen_mov='CVD' and fec_valor='" + funciones.ponerFechaSola(consultasGenerales.obtenerFechaHoy()) + "') \n");
			                sql.append(" or (ID_ESTATUS_MOV = 'A' and id_tipo_operacion in (1018) and origen_mov='SET' and fec_valor='" + funciones.ponerFechaSola(consultasGenerales.obtenerFechaHoy()) + "') \n");
			
			//'            }
			            sql.append(" )");
			        }
			        
			        sql.append(" AND RTRIM(LTRIM(V.ID_DIVISA)) IN ('" + parametros.get(0).get("divisa") + "') \n");
			        
			        
			        sql.append(" AND FEC_VALOR BETWEEN '" + parametros.get(0).get("fecIni") + "' AND '" + parametros.get(0).get("fecFin") + "' \n");
			        
			        if( psOcupaGrupo.equals("NO")) {
			            sql.append(" GROUP BY V.NO_EMPRESA, CR.ID_GRUPO,  V.ID_CHEQUERA,  CG.DESC_GRUPO,V.FEC_VALOR");
			        }else {
			            sql.append(" GROUP BY V.NO_EMPRESA, V.ID_GRUPO,  V.ID_CHEQUERA,  CG.DESC_GRUPO,V.FEC_VALOR");
			        }
			        //'psQueryP = psQueryP & Chr(10) & "order by V.ID_CHEQUERA"
			    }
	        /*
			listFlujoEmp = jdbcTemplate.query(sql.toString(), new RowMapper(){
				public PosicionBancariaDto mapRow(ResultSet rs, int idx) throws SQLException {
					PosicionBancariaDto cons = new PosicionBancariaDto();
					cons.setImporte(rs.getDouble("IMPORTE"));
					cons.setIdGrupo(rs.getString("ID_GRUPO"));
					cons.setIdBanco(rs.getInt("ID_BANCO"));
					cons.setIdChequera(rs.getString("ID_CHEQUERA"));
					cons.setDescGrupo(rs.getString("DESC_GRUPO"));
					cons.setFecValor(rs.getString("FEC_VALOR"));
					return cons;
				}
			});
			*/	
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Flujos, C:PosicionBancariaDaoImpl, M:obtieneSaldodiario");
		}
		return sql.toString();
    }
	
	public String obtieneFichaCuentaPreliminarDLS(String sTipoMovto, String sBancos, String ctas, int iTipoSelec) {
		StringBuffer sql = new StringBuffer();
		String psOcupaGrupo = "NO";
		consultasGenerales = new ConsultasGenerales(jdbcTemplate);
		
		try {
			if( iTipoSelec == 0 ) {
		        if( psOcupaGrupo.equals("NO")) {
		            sql.append(" SELECT coalesce(V.IMPORTE *(select valor from valor_divisa where fec_divisa= '" + funciones.ponerFechaSola(consultasGenerales.obtenerFechaHoy()) + "' \n");
		            sql.append(" and valor_divisa.id_divisa=v.id_divisa),0) AS IMPORTE, CR.ID_GRUPO, V.ID_BANCO,  \n");
		            sql.append(" V.ID_CHEQUERA, CG.DESC_GRUPO, V.FEC_VALOR \n");
		/*'            if( pqIngresoEgreso = "I" ) {
		'                sql.append(" Into ingresos \n");
		'            else
		'                sql.append(" Into egresos \n");
		'            )*/
		            sql.append(" FROM VISTA_MOVIMIENTO V, CAT_RUBRO CR, CAT_GRUPO CG \n");
		            sql.append(" WHERE V.ID_RUBRO = CR.ID_RUBRO \n");
		            sql.append(" AND CG.ID_GRUPO = CR.ID_GRUPO \n");
		            sql.append(" AND CG.ID_GRUPO IN (SELECT DISTINCT R.ID_GRUPO \n");
		            sql.append("                     FROM CAT_RUBRO R, CAT_GRUPO G \n");
		            sql.append("                     WHERE G.ID_GRUPO = R.ID_GRUPO \n");
		            sql.append("                     AND R.ID_GRUPO NOT IN (0) \n");
		            sql.append("                     AND (INGRESO_EGRESO = '" + sTipoMovto + "' \n");
		            sql.append(" OR INGRESO_EGRESO IS NULL)) \n");
		            sql.append(" AND ID_BANCO IN (" + sBancos + ") \n");
		            sql.append(" AND ID_CHEQUERA IN (" + ctas + ") \n");
		            sql.append(" AND ID_TIPO_MOVTO = '" + sTipoMovto + "' \n");
		        
		        }
		        
		        if( sTipoMovto.equals("E")) {
		            sql.append(" AND (");
		            sql.append(" (ID_ESTATUS_MOV = 'N' and id_tipo_operacion in (3000) and origen_mov='AS4' ) \n");
		            
		            sql.append(" )");
		        }
		                
		       //' sql.append(" AND RTRIM(LTRIM(ID_DIVISA)) IN ('" & txtDivisa.Text & "') \n");
		         sql.append(" AND RTRIM(LTRIM(ID_DIVISA))not  IN ('MN') \n");
			}
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Flujos, C:PosicionBancariaDaoImpl, M:obtieneFichaCuentaPreliminarDLS");
		}
		return sql.toString();
	}
	
	/**
	 * Este metodo es para hacer el enlace con el bean de conexion en el aplicationContext
	 * @param dataSource
	 */
	public void setDataSource(DataSource dataSource){
		try {
			this.jdbcTemplate = new JdbcTemplate(dataSource);
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Flujos, C:PosicionBancariaDaoImpl, M:setDataSource");
		}
	}
}
