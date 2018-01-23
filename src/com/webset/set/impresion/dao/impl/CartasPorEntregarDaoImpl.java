package com.webset.set.impresion.dao.impl;

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

import com.webset.set.impresion.dao.CartasPorEntregarDao;
import com.webset.set.impresion.dto.CartasPorEntregarDto;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utileriasmod.dto.MantenimientoSolicitantesFirmantesDto;
import com.webset.utils.tools.Utilerias;

public class CartasPorEntregarDaoImpl implements CartasPorEntregarDao{

	private JdbcTemplate jdbcTemplate;
	Bitacora bitacora = new Bitacora();
	String sql = "";
	
//
//	@Override
	public List<MantenimientoSolicitantesFirmantesDto> obtenerSolicitantes(String tipoSol){
		List<MantenimientoSolicitantesFirmantesDto> listaSolicitantes = new ArrayList<MantenimientoSolicitantesFirmantesDto>();
		StringBuffer sb = new StringBuffer();
		try {
		if(tipoSol.equals("solicitantes")){
			sb.append("SELECT IDENTIFICACIONC1_SOL AS ID, NOMBREC1_SOL AS NOMBRE FROM CAT_SOLICITANTES \n");
		}else if(tipoSol.equals("alternos")){
			sb.append("SELECT IDENTIFICACIONC2_SOL AS ID, NOMBREC2_SOL AS NOMBRE FROM CAT_SOLICITANTES \n");
		}
			listaSolicitantes = jdbcTemplate.query(sb.toString(), new RowMapper<MantenimientoSolicitantesFirmantesDto>() {
				public MantenimientoSolicitantesFirmantesDto mapRow(ResultSet rs, int idx) throws SQLException{
					MantenimientoSolicitantesFirmantesDto solicitante = new MantenimientoSolicitantesFirmantesDto();
					solicitante.setIdPersona(rs.getString("ID"));
					solicitante.setNombre(rs.getString("NOMBRE"));
					return solicitante;
			}});

			
		} catch (CannotGetJdbcConnectionException e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Impresion, C:CartasPorEntregarDaoImpl, M:obtenerSolicitantes");	
			
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Impresion, C:CartasPorEntregarDaoImpl, M:obtenerSolicitantes"); 
		}
		
		return listaSolicitantes;
	}
	
//	@Override
	public String actualizarEstatus(List<Map<String, String>> datos){
		System.out.println("dao actualiza");
		String mensaje = "";
		StringBuffer sb = new StringBuffer();
		
		sb.append("UPDATE EMISION_CARTAS SET ESTATUS = 'E', FEC_ENTREGA= convert(char(10),'"+Utilerias.validarCadenaSQL(datos.get(0).get("fecha"))+"', 103), USU_ENTREGA='"+Utilerias.validarCadenaSQL(datos.get(0).get("nombre"))+"' where id_emision in " +Utilerias.validarCadenaSQL(datos.get(0).get("folios"))+ "\n");
		
		System.out.println(sb.toString());
		
		try {
			
			if(jdbcTemplate.update(sb.toString()) > 0){
				mensaje = "Carta Actualizada";
			}else{
				mensaje = "No se guardaron cambios";
			}
			
		} catch (CannotGetJdbcConnectionException e){
			mensaje = "Se perdio la conexion con la base de datos";
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Impresion, C:CartasPorEntregarDaoImpl, M:actualizarEstatus");	
			
		} catch (Exception e) {
			mensaje = "Ocurrio un error al actualizar los registros";
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Impresion, C:CartasPorEntregarDaoImpl, M:actualizarEstatus");
		}
		
		return mensaje;
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

	@Override
	public List<CartasPorEntregarDto> obtenerCartas(String folio, String estatus,String tipo) {
		List<CartasPorEntregarDto> listaCartas = new ArrayList<CartasPorEntregarDto>();
		StringBuffer sb = new StringBuffer();
		
		try {
		
		sb.append("SELECT * FROM EMISION_CARTAS EC JOIN DET_EMISION_CARTAS DC ON EC.ID_EMISION=DC.ID_EMISION  \n");
		sb.append("JOIN MOVIMIENTO M ON DC.NO_FOLIO_DET=M.NO_FOLIO_DET JOIN CAT_BANCO CB ON M.ID_BANCO=CB.ID_BANCO \n");
		sb.append("JOIN EMPRESA E ON M.NO_EMPRESA=E.NO_EMPRESA JOIN PERSONA P ON M.NO_CLIENTE=P.NO_PERSONA \n");
		sb.append("JOIN CAT_DIVISA d ON m.ID_DIVISA = d.ID_DIVISA \n");
		sb.append("WHERE EC.ID_EMISION='"+Utilerias.validarCadenaSQL(folio)+"'\n");
		sb.append("AND EC.ESTATUS='"+Utilerias.validarCadenaSQL(estatus)+"'\n");
		if(tipo.equals("ACHEQ") || tipo.equals("CCHEQ")){
			sb.append("AND ID_FORMA_PAGO = 10 \n");
		}else{
			sb.append("AND ID_FORMA_PAGO = 1 \n");
		}
		System.out.println(sb.toString());	
			listaCartas = jdbcTemplate.query(sb.toString(), new RowMapper<CartasPorEntregarDto>() {
				public CartasPorEntregarDto mapRow(ResultSet rs, int idx) throws SQLException{
					CartasPorEntregarDto carta = new CartasPorEntregarDto();
							
					carta.setClaveP(rs.getString("cve_control"));
					//carta.setCuenta(rs.getString("id_chequera"));
					carta.setFolioSet(rs.getString("NO_FOLIO_DET"));
					carta.setNomEmpresa(rs.getString("NOM_EMPRESA"));
					carta.setNoProveedor(rs.getString("EQUIVALE_PERSONA"));
					carta.setNombreProveedor(rs.getString("RAZON_SOCIAL"));
					carta.setNombreBanco(rs.getString("DESC_BANCO"));
					carta.setIdChequera(rs.getString("ID_CHEQUERA"));
					carta.setNoCheque(rs.getInt("NO_CHEQUE"));
					carta.setImporte(rs.getDouble("IMPORTE"));
					carta.setDivisa(rs.getString("DESC_DIVISA"));
					carta.setIdEmision(rs.getString("id_emision"));
					carta.setNoCheque(rs.getInt("no_cheque"));
					//carta.setBeneficiario(rs.getString("beneficiario"));
					carta.setTipoC(rs.getString("tipo"));
					
					return carta;
			}});
			
		} catch (CannotGetJdbcConnectionException e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Impresion, C:ChequesPorEntregarDaoImpl, M:obtenerCartas");
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Impresion, C:ChequesPorEntregarDaoImpl, M:obtenerCartas");
		}
		return listaCartas;
	}

	@Override
	public List<CartasPorEntregarDto> obtenerCartasE(String folio, String idBanco, String tipo, String estatus, String fechaIni, String fechaFin) {
		List<CartasPorEntregarDto> listaCartas = new ArrayList<CartasPorEntregarDto>();
		StringBuffer sb = new StringBuffer();
		
		try {
		
		sb.append("select * from emision_cartas ec join cat_banco cb on ec.id_banco=cb.id_Banco join \n");
		sb.append("cat_solicitantes cs on ec.no_solicitante=cs.identificacionc1_sol \n");
		sb.append(" where ec.estatus='"+Utilerias.validarCadenaSQL(estatus)+"'\n");
			
		if (!folio.equals("")) {
			sb.append("and ec.id_emision="+Utilerias.validarCadenaSQL(folio)+"\n");
		} 
		if (!idBanco.equals("")) {
			sb.append("and ec.id_banco="+Utilerias.validarCadenaSQL(idBanco)+"\n");
		} 
		
		if(!tipo.equals("")){
			sb.append("and ec.tipo='"+Utilerias.validarCadenaSQL(tipo)+"'\n");
		}
		if(!fechaIni.equals("") && !fechaFin.equals("")){
			//sb.append("and ec.fec_impresion between TO_DATE('"+fechaIni+"' , 'DD/MM/YYYY') and TO_DATE('"+fechaFin+"', 'DD/MM/YYYY')\n");
			sb.append("and ec.fec_impresion >= convert(datetime,'"+fechaIni+"', 103) and ec.fec_impresion < convert(datetime,'"+fechaFin+"', 103) +1 \n");
		}
		sb.append("order by ec.id_emision");
	
		System.out.println(sb.toString());
		
		
			
			listaCartas = jdbcTemplate.query(sb.toString(), new RowMapper<CartasPorEntregarDto>() {
				public CartasPorEntregarDto mapRow(ResultSet rs, int idx) throws SQLException{
					CartasPorEntregarDto carta = new CartasPorEntregarDto();
					
				 	carta.setIdEmision(rs.getString("id_emision"));
				 	carta.setIdBanco(rs.getString("id_banco"));
				 	carta.setDescBanco(rs.getString("desc_banco"));
				 	carta.setNomSolicitante(rs.getString("nombrec1_sol"));
				 	carta.setNomSolicitante2(rs.getString("nombrec2_sol"));
					carta.setFechaI(rs.getString("fec_impresion"));
					carta.setTipo(rs.getString("tipo"));
				 	carta.setEstatus(rs.getString("estatus"));
				 	carta.setMotCancelacion(rs.getString("mot_cancelacion"));
				 	carta.setFechaE(rs.getString("fec_entrega"));
				 	carta.setUsuEntrega(rs.getString("usu_entrega"));
					return carta;
			}});
			
		} catch (CannotGetJdbcConnectionException e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Impresion, C:CartasPorEntregarDaoImpl, M:obtenerCartasE");
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Impresion, C:CartasPorEntregarDaoImpl, M:obtenerCartasE");
		}
		return listaCartas;
	}
	
	@Override
	public List<Map<String, String>> obtenerCartasEmitidas(String folio, String idBanco, String tipo, String estatus, String fechaIni, String fechaFin) {
		List<Map<String, String>> listaCartas = new ArrayList<Map<String, String>>();
		StringBuffer sb = new StringBuffer();
		
		try {
		
		sb.append("select * from emision_cartas ec join cat_banco cb on ec.id_banco=cb.id_Banco join \n");
		sb.append("cat_solicitantes cs on ec.no_solicitante=cs.identificacionc1_sol \n");
		sb.append(" where ec.estatus='"+Utilerias.validarCadenaSQL(estatus)+"'\n");
			
		if (!folio.equals("")) {
			sb.append("and ec.id_emision="+Utilerias.validarCadenaSQL(folio)+"\n");
		} 
		if (!idBanco.equals("")) {
			sb.append("and ec.id_banco="+Utilerias.validarCadenaSQL(idBanco)+"\n");
		} 
		
		if(!tipo.equals("")){
			sb.append("and ec.tipo='"+Utilerias.validarCadenaSQL(tipo)+"'\n");
		}
		if(!fechaIni.equals("") && !fechaFin.equals("")){
			sb.append("and ec.fec_impresion >= convert(datetime,'"+fechaIni+"' , 103) and ec.fec_impresion < convert(datetime,'"+fechaFin+"', 103) +1 \n");
					//+ "ec.fec_impresion between TO_DATE('"+fechaIni+"' , 'DD/MM/YYYY') and TO_DATE('"+fechaFin+"', 'DD/MM/YYYY')\n");
		}
		sb.append("order by ec.id_emision");
	
	
		System.out.println(sb.toString());
		
		
			
			listaCartas = jdbcTemplate.query(sb.toString(), new RowMapper<Map<String, String>>() {
				public Map<String, String> mapRow(ResultSet rs, int idx) throws SQLException{
					Map<String, String> carta = new HashMap<String, String>();
					
				 	carta.put("idEmision", rs.getString("id_emision"));
				 	carta.put("idBanco", rs.getString("id_banco"));
				 	carta.put("descBanco", rs.getString("desc_banco"));
				 	carta.put("nomSolicitante", rs.getString("nombrec1_sol"));
					carta.put("fechaI", rs.getString("fec_impresion"));
					carta.put("tipo", rs.getString("tipo"));
				 	carta.put("estatus", rs.getString("estatus"));
				 	carta.put("motCancelacion",rs.getString("mot_cancelacion"));
				 	carta.put("fechaE",rs.getString("fec_entrega"));
				 	carta.put("usuEntrega",rs.getString("usu_entrega"));
					return carta;
			}});
			
		} catch (CannotGetJdbcConnectionException e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Impresion, C:CartasPorEntregarDaoImpl, M:obtenerCartasE");
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Impresion, C:CartasPorEntregarDaoImpl, M:obtenerCartasE");
		}
		return listaCartas;
	}
	
	@Override
	public List<Map<String, String>> obtenerCartasDetalle(String folio, String estatus, String tipo) {
		List<Map<String, String>> listaCartas = new ArrayList<Map<String, String>>();
		StringBuffer sb = new StringBuffer();
		
		try {
		
		sb.append("SELECT * FROM EMISION_CARTAS EC JOIN DET_EMISION_CARTAS DC ON EC.ID_EMISION=DC.ID_EMISION  \n");
		sb.append("JOIN MOVIMIENTO M ON DC.NO_FOLIO_DET=M.NO_FOLIO_DET JOIN CAT_BANCO CB ON M.ID_BANCO=CB.ID_BANCO \n");
		sb.append("JOIN EMPRESA E ON M.NO_EMPRESA=E.NO_EMPRESA JOIN PERSONA P ON M.NO_CLIENTE=P.NO_PERSONA \n");
		sb.append("JOIN CAT_DIVISA d ON m.ID_DIVISA = d.ID_DIVISA \n");
		sb.append("WHERE EC.ID_EMISION='"+Utilerias.validarCadenaSQL(folio)+"'\n");
		sb.append("AND EC.ESTATUS='"+Utilerias.validarCadenaSQL(estatus)+"'\n");
		if(tipo.equals("ACHEQ") || tipo.equals("CCHEQ")){
			sb.append("AND ID_FORMA_PAGO = 10 \n");
		}else{
			sb.append("AND ID_FORMA_PAGO = 1 \n");
		}
		
		System.out.println(sb.toString());
		
		
			
			listaCartas = jdbcTemplate.query(sb.toString(), new RowMapper<Map<String, String>>() {
				public Map<String, String> mapRow(ResultSet rs, int idx) throws SQLException{
					Map<String, String> carta = new HashMap<String, String>();					
					
					carta.put("ClaveP", rs.getString("cve_control"));
					//carta.put("Cuenta", rs.getString("id_chequera_benef"));
					carta.put("Cuenta", rs.getString("id_chequera"));
					carta.put("FolioSet", rs.getString("NO_FOLIO_DET"));
					carta.put("NomEmpresa", rs.getString("NOM_EMPRESA"));
					carta.put("NoProveedor", rs.getString("EQUIVALE_PERSONA"));
					carta.put("NombreProveedor", rs.getString("RAZON_SOCIAL"));
					carta.put("NombreBanco", rs.getString("DESC_BANCO"));
					carta.put("IdChequera", rs.getString("ID_CHEQUERA"));
					carta.put("NoCheque", rs.getString("NO_CHEQUE"));
					carta.put("Importe", rs.getString("IMPORTE"));
					carta.put("Divisa", rs.getString("DESC_DIVISA"));
					carta.put("IdEmision", rs.getString("id_emision"));
					carta.put("NoCheque", rs.getString("no_cheque"));
					//carta.put("Beneficiario", rs.getString("beneficiario"));
					carta.put("TipoC", rs.getString("tipo"));
					
					return carta;
			}});
			
		} catch (CannotGetJdbcConnectionException e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Impresion, C:ChequesPorEntregarDaoImpl, M:obtenerCartasDetalle");
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Impresion, C:ChequesPorEntregarDaoImpl, M:obtenerCartasDetalle");
		}
		return listaCartas;
	}

	@Override
	public List<CartasPorEntregarDto> obtieneDatos(String idEmision, String idBanco) {
		List<CartasPorEntregarDto> listaResultado = new ArrayList<CartasPorEntregarDto>();
		StringBuffer sql=new StringBuffer();
		System.out.println("entro dao carta");
		try{
			sql.append("select pc.textob1, pc.textob2,pc.textob3, pc.textob4, pc.textoc1,\n");
			sql.append("case when pc.textoc2 is null then ' ' else pc.textoc2 end as textoc2, pc.textoc3, pc.textoc4, pc.textoc5,pc.textoc6,pc.lugar_fecha,\n");		
			sql.append("d.calle_no, d.colonia, d.ciudad, d.id_cp, d.id_estado,\n");
			sql.append("case when cp.nombre is null then ' ' else cp.nombre end as cp,case when ctp.nombre is null then ' ' else ctp.nombre end as ctp,\n");
			sql.append("cs.nombrec1_sol as cs, cs.identificacionc1_sol as fii \n");
			sql.append("from emision_cartas ec join det_emision_cartas dc on ec.id_emision= dc.id_emision\n");
			sql.append("join movimiento m on dc.no_folio_det=m.no_folio_det \n");
			sql.append("left join cat_persona cp on ec.no_firma_uno=cp.no_persona \n");
			sql.append("left join cat_persona ctp on ec.no_firma_dos=ctp.no_persona \n");
			sql.append("join cat_solicitantes cs on ec.no_solicitante=cs.identificacionc1_sol \n");
			sql.append("join parametros_carta pc on  ec.tipo=pc.tipo_carta\n");
			sql.append("join direccion d on m.no_empresa=d.no_empresa\n");
			sql.append("where ec.id_emision='"+Utilerias.validarCadenaSQL(idEmision)+"'\n");
			sql.append("and pc.id_banco='"+idBanco+"'\n");
		
			System.out.println(sql);
			
			listaResultado = jdbcTemplate.query(sql.toString(), new RowMapper<CartasPorEntregarDto>(){
				public CartasPorEntregarDto mapRow(ResultSet rs, int idx)throws SQLException{
					CartasPorEntregarDto campos = new CartasPorEntregarDto();
					campos.setB1(rs.getString("textob1"));
					campos.setB2(rs.getString("textob2"));
					campos.setB3(rs.getString("textob3"));
					campos.setB4(rs.getString("textob4"));
					campos.setC1(rs.getString("textoc1"));
					campos.setC2(rs.getString("textoc2"));
					campos.setC3(rs.getString("textoc3"));
					campos.setC4(rs.getString("textoc4"));
					campos.setC5(rs.getString("textoc5"));
					campos.setC6(rs.getString("textoc6"));
					campos.setLugarFecha(rs.getString("lugar_fecha"));
					campos.setCalle(rs.getString("calle_no"));
					campos.setColonia(rs.getString("colonia"));
					campos.setCiudad(rs.getString("ciudad"));
					campos.setCp(rs.getString("id_cp"));
					campos.setEstado(rs.getString("id_estado"));
					campos.setAutorizacion1(rs.getString("cp"));
					campos.setAutorizacion2(rs.getString("ctp"));
					campos.setSolicitante(rs.getString("cs"));
					campos.setIdentificacion(rs.getString("fii"));
					
					
					return campos;
				}
			});
		}
		catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Impresion, C: CartasChequesDaoImpl, M: obtieneDatos");
		}return listaResultado;
	}

	@Override
	public List<CartasPorEntregarDto> llenaFolio(String fechaIni, String fechaFin) {
		List<CartasPorEntregarDto> listaResultado = new ArrayList<CartasPorEntregarDto>();
		StringBuffer sql= new StringBuffer();
		
		try{
			sql.append("select * from emision_cartas where fec_impresion >= convert(datetime,'"+fechaIni+"' , 103) and fec_impresion < convert(datetime,'"+fechaFin+"', 103) +1 order by id_emision");
		
			System.out.println(sql);
			
			listaResultado = jdbcTemplate.query(sql.toString(), new RowMapper<CartasPorEntregarDto>(){
				public CartasPorEntregarDto mapRow(ResultSet rs, int idx)throws SQLException{
					CartasPorEntregarDto campos = new CartasPorEntregarDto();
					campos.setIdEmision(rs.getString("id_emision"));
					return campos;
				}
			});
		}
		catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Impresion, C: CartasPorEntregarDaoImpl, M: llenaFolio");
		}return listaResultado;
	}
	
	@Override
	public String actualizarFecha(Map<String, String> valor, String fechaImp) {
		String mensaje = "";
		StringBuffer sb = new StringBuffer();
		if(valor.get("tipoCar").equals("ACERT")||valor.get("tipoCar").equals("ACHEQ")){
			sb.append("UPDATE emision_cartas SET fec_impresion= convert(char(10),'"+ Utilerias.validarCadenaSQL(fechaImp) +"' , 103) \n");
		}else{
			sb.append("UPDATE emision_cartas SET fec_impresion= convert(char(10),'"+ Utilerias.validarCadenaSQL(fechaImp) +"' , 103),  \n");
			sb.append("fec_cancelacion= convert(char(10),'"+ Utilerias.validarCadenaSQL(fechaImp) +"' , 103) \n");
		}

		sb.append("WHERE id_emision = " + Utilerias.validarCadenaSQL(valor.get("emision")));
		
		System.out.println(sb.toString());
		
		try {
			
			if(jdbcTemplate.update(sb.toString()) > 0){
				mensaje = "Actualizado con exito";
			}else{
				mensaje = "No se guardaron cambios";
			}
			
		} catch (CannotGetJdbcConnectionException e){
			mensaje = "Se perdio la conexion con la base de datos";
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Impresion, C: CartasPorEntregarDaoImpl, M: actualizarFecha");	
			
		} catch (Exception e) {
			mensaje = "Ocurrio un error al actualizar los registros";
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Impresion, C: CartasPorEntregarDaoImpl, M: actualizarFecha");	
		}
		
		return mensaje;
	}
	
	
}
