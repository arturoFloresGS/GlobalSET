package com.webset.set.impresion.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.webset.set.impresion.dao.ChequesPorEntregarDao;
import com.webset.set.impresion.dto.CartasChequesDto;
import com.webset.set.impresion.dto.ChequePorEntregarDto;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.ConsultasGenerales;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.set.utileriasmod.dto.MantenimientoSolicitantesFirmantesDto;
import com.webset.utils.tools.Utilerias;


public class ChequesPorEntregarDaoImpl implements ChequesPorEntregarDao {
	private JdbcTemplate jdbcTemplate;
	Bitacora bitacora = new Bitacora();
	ConsultasGenerales consultasGenerales;
	
	@Override
	public List<ChequePorEntregarDto> obtenerCheques(List<Map<String, String>> datos, String entregado) {
		System.out.println(entregado+" Estatus");
		List<ChequePorEntregarDto> listaCheques = new ArrayList<ChequePorEntregarDto>();
		StringBuffer sb = new StringBuffer();
		 
		sb.append("SELECT e.NO_EMPRESA,NOM_EMPRESA,NO_FOLIO_MOV, EQUIVALE_PERSONA, RAZON_SOCIAL, b.ID_BANCO,DESC_BANCO, ID_CHEQUERA, \n");
		sb.append("NO_CHEQUE, m.ID_DIVISA, DESC_DIVISA,B_ENTREGADO,CONVERT(DATE,FEC_ENTREGADO,103) AS FEC_ENTREGADO,CONVERT(DATE,FEC_PROPUESTA,103)  FEC_PROPUESTA,CONVERT(DATE,FEC_IMPRIME,103) AS FEC_IMPRIME, sum(M.IMPORTE) AS IMPORTE, OBSERVACION \n");
		sb.append("FROM MOVIMIENTO m JOIN EMPRESA e ON  \n");
		sb.append("m.NO_EMPRESA = e.NO_EMPRESA JOIN PERSONA p ON m.NO_CLIENTE = p.NO_PERSONA \n");
		sb.append("JOIN CAT_BANCO b ON m.ID_BANCO = b.ID_BANCO JOIN CAT_DIVISA d ON m.ID_DIVISA = d.ID_DIVISA \n");
		sb.append("AND ID_ESTATUS_MOV IN ('I', 'R') \n");
		sb.append("AND ID_FORMA_PAGO = 1 \n");
		sb.append("AND id_tipo_operacion IN (3200, 3000) \n");
		if(!datos.get(0).get("empresa").equals("")){
			sb.append("AND m.NO_EMPRESA = '"+Utilerias.validarCadenaSQL(datos.get(0).get("empresa"))+"' \n");
		}if(!datos.get(0).get("fInicial").equals("") && !datos.get(0).get("fFinal").equals("")){
			if(entregado.equals("true")){
				sb.append("AND FEC_ENTREGADO >= CONVERT(DATE,'"+ Utilerias.validarCadenaSQL(datos.get(0).get("fInicial")) +"', 103) AND FEC_ENTREGADO < CONVERT(DATE,'"+Utilerias.validarCadenaSQL(datos.get(0).get("fFinal"))+"',103) +1 \n");
			}else{
				sb.append("AND FEC_PROPUESTA >= CONVERT(DATE,'"+ Utilerias.validarCadenaSQL(datos.get(0).get("fInicial")) +"',103) AND FEC_PROPUESTA < CONVERT(DATE,'"+Utilerias.validarCadenaSQL(datos.get(0).get("fFinal"))+"', 103) +1 \n");
			}		
		}if(!datos.get(0).get("iUno").equals("") && !datos.get(0).get("iDos").equals("")){
			sb.append("AND IMPORTE >= '"+Utilerias.validarCadenaSQL(datos.get(0).get("iUno"))+"' AND IMPORTE <= '"+Utilerias.validarCadenaSQL(datos.get(0).get("iDos"))+"' \n");
		}if(!datos.get(0).get("clave").equals("")){
			sb.append("AND CVE_CONTROL='"+Utilerias.validarCadenaSQL(datos.get(0).get("clave"))+"' \n");
		}if(!datos.get(0).get("noCheque").equals("")){
			sb.append("AND NO_CHEQUE='"+Utilerias.validarCadenaSQL(datos.get(0).get("noCheque"))+"' \n");
		}if(!datos.get(0).get("chequera").equals("")){
			sb.append("AND m.ID_CHEQUERA='"+Utilerias.validarCadenaSQL(datos.get(0).get("chequera"))+"' \n");
		}if(!datos.get(0).get("banco").equals("")){
			sb.append("AND m.ID_BANCO='"+Utilerias.validarCadenaSQL(datos.get(0).get("banco"))+"' \n");
		}if(!datos.get(0).get("proveedor").equals("")){
			sb.append("AND m.NO_CLIENTE='"+Utilerias.validarCadenaSQL(datos.get(0).get("proveedor"))+"' \n");
		}if(entregado.equals("true")){
			sb.append("AND (B_ENTREGADO is null or B_ENTREGADO = 'S') \n");
		}else if(entregado.equals("false")){
			sb.append("AND (B_ENTREGADO is null or B_ENTREGADO = 'N') \n");
		}	
		sb.append("AND m.NO_FOLIO_MOV is not null \n");
		sb.append("GROUP BY e.NO_EMPRESA, NOM_EMPRESA,NO_FOLIO_MOV, b.ID_BANCO, ID_CHEQUERA,\n");
		sb.append("DESC_BANCO, DESC_DIVISA, EQUIVALE_PERSONA, RAZON_SOCIAL,  \n");
		sb.append("FEC_IMPRIME, m.ID_DIVISA, NO_CHEQUE, FEC_PROPUESTA, B_ENTREGADO, IMPORTE, FEC_ENTREGADO, OBSERVACION \n");
		System.out.println(sb.toString());
		
		try {
			
			listaCheques = jdbcTemplate.query(sb.toString(), new RowMapper<ChequePorEntregarDto>() {
				public ChequePorEntregarDto mapRow(ResultSet rs, int idx) throws SQLException{
					ChequePorEntregarDto cheque = new ChequePorEntregarDto();
					
					//cheque.setFolioSet(rs.getString("NO_FOLIO_DET"));
					cheque.setNoEmpresa(rs.getInt("NO_EMPRESA"));
					cheque.setNomEmpresa(rs.getString("NOM_EMPRESA"));
					cheque.setPoHeaders(rs.getString("NO_FOLIO_MOV"));
					cheque.setNoProveedor(rs.getString("EQUIVALE_PERSONA"));
					cheque.setNombreProveedor(rs.getString("BENEFICIARIO"));
					cheque.setIdBanco(rs.getInt("ID_BANCO"));
					cheque.setNombreBanco(rs.getString("DESC_BANCO"));
					cheque.setIdChequera(rs.getString("ID_CHEQUERA"));
					cheque.setNoCheque(rs.getString("NO_CHEQUE"));
					cheque.setImporte(rs.getDouble("IMPORTE"));
					cheque.setFecImprime(rs.getString("FEC_IMPRIME"));
					cheque.setDivisa(rs.getString("DESC_DIVISA"));
					cheque.setEntregado(rs.getString("B_ENTREGADO"));
					cheque.setFecEntregado(rs.getString("FEC_ENTREGADO"));
					cheque.setFecPropuesta(rs.getString("FEC_PROPUESTA"));
					cheque.setUsuEntregado(rs.getString("OBSERVACION"));
					return cheque;
			}});
			
		} catch (CannotGetJdbcConnectionException e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Impresion, C:ChequesPorEntregarDaoImpl, M:obtenerCheques");
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Impresion, C:ChequesPorEntregarDaoImpl, M:obtenerCheques");
		}
		/*for (ChequePorEntregarDto chequePorEntregarDto : listaCheques) {
			System.out.println(chequePorEntregarDto.getFolioSet());
		}*/
		return listaCheques;
	}
	
	@Override
	public List<MantenimientoSolicitantesFirmantesDto> obtenerSolicitantes(String tipoSol){
		List<MantenimientoSolicitantesFirmantesDto> listaSolicitantes = new ArrayList<MantenimientoSolicitantesFirmantesDto>();
		StringBuffer sb = new StringBuffer();
		if(tipoSol.equals("solicitantes")){
			sb.append("SELECT IDENTIFICACIONC1_SOL AS ID, NOMBREC1_SOL AS NOMBRE FROM CAT_SOLICITANTES \n");
		}else if(tipoSol.equals("alternos")){
			sb.append("SELECT IDENTIFICACIONC2_SOL AS ID, NOMBREC2_SOL AS NOMBRE FROM CAT_SOLICITANTES \n");
		}
		
		System.out.println(sb.toString());
		try {
			listaSolicitantes = jdbcTemplate.query(sb.toString(), new RowMapper<MantenimientoSolicitantesFirmantesDto>() {
				public MantenimientoSolicitantesFirmantesDto mapRow(ResultSet rs, int idx) throws SQLException{
					MantenimientoSolicitantesFirmantesDto solicitante = new MantenimientoSolicitantesFirmantesDto();
					
					solicitante.setIdPersona(rs.getString("ID"));
					solicitante.setNombre(rs.getString("NOMBRE"));
					
					return solicitante;
			}});

			
		} catch (CannotGetJdbcConnectionException e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Impresion, C:ChequesPorEntregarDaoImpl, M:obtenerSolicitantes");	
			
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Impresion, C:ChequesPorEntregarDaoImpl, M:obtenerSolicitantes"); 
		}
		
		return listaSolicitantes;
	}
	
	@Override
	public String actualizarMovimiento(List<Map<String, String>> datos){
		String mensaje = "";
		StringBuffer sb = new StringBuffer();
		System.out.println(datos);
		sb.append("UPDATE MOVIMIENTO SET b_entregado = 'S', \n");
		sb.append("fec_entregado = CONVERT(DATE,'"+ Utilerias.validarCadenaSQL(datos.get(0).get("fecha")) +"' ,103), \n");
		sb.append("OBSERVACION = '"+ Utilerias.validarCadenaSQL(datos.get(0).get("nombre")) +"' \n");
		String poHeaders = datos.get(0).get("poHeaders").replace(",","','");
		sb.append("WHERE NO_FOLIO_MOV IN('" +poHeaders+"')\n");
		//sb.append("AND NO_EMPRESA IN('" + Utilerias.validarCadenaSQL(datos.get(0).get("noEmpresa"))+"')\n");
		//sb.append("AND ID_BANCO IN('" + Utilerias.validarCadenaSQL(datos.get(0).get("idBanco"))+"')\n");
		//String idChequera = datos.get(0).get("idChequera").replace(",","','");
		//sb.append("AND ID_CHEQUERA IN('"+idChequera+"')\n");
		//String fechas = datos.get(0).get("fecPropuesta").replace(",","',103), CONVERT('");
		//sb.append("AND FEC_PROPUESTA IN(CONVERT(DATE,'"+fechas+"',103))\n");
		System.out.println(sb.toString());
		
		try {
			
			if(jdbcTemplate.update(sb.toString()) > 0){
				mensaje = "Cheques Actualizados";
			}else{
				mensaje = "No se guardaron cambios";
			}
			
		} catch (CannotGetJdbcConnectionException e){
			mensaje = "Se perdio la conexion con la base de datos";
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Impresion, C:ChequesPorEntregarDaoImpl, M:obtenerSolicitantes");	
			
		} catch (Exception e) {
			mensaje = "Ocurrio un error al actualizar los registros";
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Impresion, C:ChequesPorEntregarDaoImpl, M:obtenerSolicitantes");
		}
		
		return mensaje;
	}
	
	//RCG

	@Override
	public List<ChequePorEntregarDto> llenaEmpresa() {
		List<ChequePorEntregarDto> listaResultado = new ArrayList<ChequePorEntregarDto>();
		StringBuffer sql = new StringBuffer();
		try{
			sql.append("select distinct e.no_empresa,e.nom_empresa from empresa e join cat_cta_banco ct on e.no_empresa=ct.no_empresa order by e.nom_empresa \n");		
			System.out.println(sql);		
			listaResultado = jdbcTemplate.query(sql.toString(), new RowMapper<ChequePorEntregarDto>(){
				public ChequePorEntregarDto mapRow(ResultSet rs, int idx)throws SQLException{
					ChequePorEntregarDto campos = new ChequePorEntregarDto();
					campos.setIdEmpresa(rs.getString("no_empresa"));
					campos.setDescEmpresa(rs.getString("nom_empresa"));
					return campos;
				}
			});
		}
		catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Impresion, C: CartasChequesDaoImpl, M: llenaEmpresa");
		}return listaResultado;
	}

	
	@Override
	public List<ChequePorEntregarDto> llenaBanco(String idEmpresa) {
		List<ChequePorEntregarDto> listaResultado = new ArrayList<ChequePorEntregarDto>();
		StringBuffer sql= new StringBuffer();
		
		try{
			sql.append("SELECT distinct b.ID_BANCO, DESC_BANCO FROM CAT_BANCO b ");
			sql.append("JOIN CAT_CTA_BANCO cb ON b.id_banco= cb.id_banco WHERE NO_EMPRESA="+Utilerias.validarCadenaSQL(idEmpresa)+"");
				
			System.out.println(sql);	
			listaResultado = jdbcTemplate.query(sql.toString(), new RowMapper<ChequePorEntregarDto>(){
				public ChequePorEntregarDto mapRow(ResultSet rs, int idx)throws SQLException{
					ChequePorEntregarDto campos = new ChequePorEntregarDto();
					campos.setIdBanco(rs.getInt("id_banco"));
					campos.setDescBanco(rs.getString("desc_banco"));
					return campos;
				}
			});
		}
		catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Impresion, C: CartasChequesDaoImpl, M: llenaBanco");
		}
		return listaResultado;
	}
	@Override
	public List<ChequePorEntregarDto> llenaChequera(String idEmpresa, String idBanco) {
		List<ChequePorEntregarDto> listaResultado = new ArrayList<ChequePorEntregarDto>();
		String sql = "";
		try{
			sql = "select no_empresa ,id_chequera, desc_chequera from cat_cta_banco"
					+ "\n where no_empresa="+Utilerias.validarCadenaSQL(idEmpresa)+" and id_banco="+Utilerias.validarCadenaSQL(idBanco)+"";
					
			
			System.out.println(sql);
			
			listaResultado = jdbcTemplate.query(sql, new RowMapper<ChequePorEntregarDto>(){
				public ChequePorEntregarDto mapRow(ResultSet rs, int idx)throws SQLException{
					ChequePorEntregarDto campos = new ChequePorEntregarDto();
					campos.setIdChequera(rs.getString("id_chequera"));
					campos.setDescChequera(rs.getString("id_chequera"));
					return campos;
				}
			});
		}
		catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Impresion, C: CartasChequesDaoImpl, M: llenaChequera");
		}return listaResultado;
	}
	//Sustituye metodo por todos los bancos que tengan chequera. EMS - 29/01/2016
		/*public List<LlenaComboGralDto> llenarComboBancos(){
			
			StringBuffer sb = new StringBuffer();
			List<LlenaComboGralDto> list = new ArrayList<LlenaComboGralDto>();
			try
			{
				/*sb.append( " SELECT id_banco as ID, desc_banco as describ ");
				sb.append( "\n   FROM cat_banco ");
				sb.append( "\n  WHERE b_cheque_elect = 'S'");
				sb.append( "\n  ORDER by desc_banco");
				
				
				sb.append( "\n SELECT DISTINCT ca.id_banco as ID, ca.desc_banco as describ ");
				sb.append( "\n FROM cat_banco ca JOIN CTAS_BANCO cb ON ca.id_banco = cb.id_banco");
				sb.append( "\n ORDER by desc_banco ");
							
				list= jdbcTemplate.query(sb.toString(), new RowMapper<LlenaComboGralDto>(){
				public LlenaComboGralDto mapRow(ResultSet rs, int idx) throws SQLException {
					LlenaComboGralDto cons = new LlenaComboGralDto();
					cons.setId(rs.getInt("ID"));
					cons.setDescripcion(rs.getString("describ"));
					return cons;
				}});
				
			}catch(Exception e){
				e.printStackTrace();
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
				+ "P:Impresion, C:ChequesPorEntregarDaoImpl, M:llenarComboBancos");
			}
			return list;
		    
		}
		*/
	
		/*public List<LlenaComboGralDto>llenarComboGral(LlenaComboGralDto dto){
			String sql="";
			List<LlenaComboGralDto> listDatos= new ArrayList<LlenaComboGralDto>();
			try{
				sql+= "	SELECT	";
				if(dto.getCampoUno()!=null && !dto.getCampoUno().trim().equals(""))
					sql+=""+Utilerias.validarCadenaSQL(dto.getCampoUno())+"	as ID";
				//aplica solo para combos que tengan el mismo campo como id y descripcion (String)
				//ejemplo: chequeras
				else
					sql+=""+2+"	as ID"+",	";	
				if(dto.getCampoDos()!=null && !dto.getCampoDos().trim().equals(""));
					sql+=Utilerias.validarCadenaSQL(dto.getCampoDos())+"	as DESCRIPCION";
			    sql+= "  FROM	";
			    if(dto.getTabla()!=null && !dto.getTabla().trim().equals(""))
			    	sql+=""+Utilerias.validarCadenaSQL(dto.getTabla());
			    if(dto.getCondicion()!=null && !dto.getCondicion().trim().equals(""))
			    	sql+="	WHERE	"+Utilerias.validarCadenaSQL(dto.getCondicion());
			    if(dto.getOrden()!=null && !dto.getOrden().trim().equals(""))
			    	sql+="	ORDER BY	"+Utilerias.validarCadenaSQL(dto.getOrden());
			    System.out.println(sql);
			    
			    listDatos= jdbcTemplate.query(sql, new RowMapper<LlenaComboGralDto>(){
					public LlenaComboGralDto mapRow(ResultSet rs, int idx) throws SQLException {
						LlenaComboGralDto cons = new LlenaComboGralDto();
						cons.setId(rs.getInt("ID"));
						cons.setDescripcion(rs.getString("DESCRIPCION"));
						return cons;
					}});
			}catch(Exception e){
				e.printStackTrace();
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
				+ "P:Impresion, C:ChequesPorEntregarDaoImpl, M:llenarComboGral");
			}
			return listDatos;
		}*/
		
		public List<LlenaComboGralDto>llenarComboBeneficiario(LlenaComboGralDto dto){
			
			String cond="";
			cond=dto.getCondicion();
			dto.setCampoDos("rTRIM(COALESCE(razon_social,'')) ");
			
			cond = Utilerias.validarCadenaSQL(cond);
			
			if(dto.isRegistroUnico()){
				dto.setCondicion("no_persona="+cond);
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
				+ "P:Impresion, C:ChequesPorEntregarDaoImpl, M:consultarProveedores");
			}catch(Exception e){
				e.printStackTrace();
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
				+ "P:Impresion, C:ChequesPorEntregarDaoImpl, M:consultarProveedores");
				System.err.println(e);
			}
			return list;
		    
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
