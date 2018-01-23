
package com.webset.set.derivados.dao.impl;
/**
 * @autor COINSIDE
 */


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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.webset.set.derivados.dao.ForwardsDao;
import com.webset.set.derivados.dto.DatosExcelFwsDto;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.ConsultasGenerales;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utilerias.GlobalSingleton;
import com.webset.set.utilerias.dto.GeneradorDto;
import com.webset.set.utilerias.dto.LlenaComboEmpresasDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.set.utilerias.dto.LlenarConsultaFws;


public class ForwardsDaoImpl implements ForwardsDao{
	private JdbcTemplate jdbcTemplate;
	Bitacora bitacora=new Bitacora();
	ConsultasGenerales consultasGenerales;
	Funciones funciones= new Funciones();
	GlobalSingleton gb = GlobalSingleton.getInstancia();
	SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
	private static Logger logger = Logger.getLogger(ForwardsDaoImpl.class);

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public String consultarConfiguraSet(int indice){
		consultasGenerales= new ConsultasGenerales(jdbcTemplate);
		return consultasGenerales.consultarConfiguraSet(indice);
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
			logger.error(e.toString());
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Derivados, C:ForwardsDaoImpl, M:setDataSource");
		}
	}	



	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public List<LlenaComboEmpresasDto> consultarEmpresas(int iEmpRaiz) {

		String sql= null;
		StringBuilder sb = new StringBuilder();

		sb.append("SELECT no_empresa as ID, nom_empresa as describ"	).append( "\n" ); 
		sb.append("FROM EMPRESA"									).append( "\n" );	
		sb.append("ORDER BY describ"								).append( "\n" );

		sql = sb.toString(); 
		System.out.println("QUERY: " + sql);

		return jdbcTemplate.query(sql, new RowMapper(){
			public LlenaComboEmpresasDto mapRow(ResultSet rs, int idx) throws SQLException {
				LlenaComboEmpresasDto cons = new LlenaComboEmpresasDto();
				cons.setNoEmpresa (rs.getInt   ("ID")      );
				cons.setNomEmpresa(rs.getString("describ") );
				return cons;
			}});
	}


	@Override
	public List<DatosExcelFwsDto> validarDatosExcel(String datos) {
		System.out.println("validando datos fws dao impl: " + datos );
		Gson gson = new Gson();
		List<DatosExcelFwsDto> datosExcel = new ArrayList<DatosExcelFwsDto>();
		List<Map<String, String>> renglon = gson.fromJson(datos, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yy");
		Funciones fn = new Funciones();
//		System.out.println(renglon.size() + "__________________________tamaño renglon");
		for(int i = 0; i < renglon.size(); i++){
			//			System.out.println(renglon.get(i).get("folio") + "CHEQUERA" + renglon.get(i).get("chequera_cargo") + " UNEGO:" +renglon.get(i).get("unidad_negocio"));

			DatosExcelFwsDto datosExcelDto = new DatosExcelFwsDto();
			try {
				datosExcelDto.setFolio(Integer.parseInt(renglon.get(i).get("folio")));
			} catch (Exception e) {
				datosExcelDto.setFolio(0);
				logger.error(e.toString());
			}

			try{
				datosExcelDto.setUnidad_negocio(renglon.get(i).get("unidad_negocio"));
			}catch(Exception e){
				datosExcelDto.setUnidad_negocio("");
				logger.error(e.toString());
			}

			datosExcelDto.setChequera_cargo(renglon.get(i).get("chequera_cargo"));	
			datosExcelDto.setChequera_abono(renglon.get(i).get("chequera_abono"));
			datosExcelDto.setForma_pago(renglon.get(i).get("forma_pago"));

			try{
				String importe_pago =  renglon.get(i).get("importe_pago").replaceAll("\\$", "");
				datosExcelDto.setImporte_pago(Double.parseDouble(importe_pago.replaceAll("\\,", "")));
			}catch(Exception e){
				datosExcelDto.setImporte_pago(0.0);
				logger.error(e.toString());
			}

			try{
				String importe_compra =  renglon.get(i).get("importe_compra").replaceAll("\\$", "");
				datosExcelDto.setImporte_compra(Double.parseDouble(importe_compra.replaceAll("\\,", "")));
			}catch(Exception e){
				datosExcelDto.setImporte_compra(0.0);
				logger.error(e.toString());
			}

			try{
				String tc =  renglon.get(i).get("tc").replaceAll("\\$", "");
				datosExcelDto.setTc(Double.parseDouble(tc.replaceAll("\\,", "")));
			}catch(Exception e){
				datosExcelDto.setTc(0.0);
				logger.error(e.toString());
			}

			try{
				Date fec_vto = format.parse(renglon.get(i).get("fec_vto"));
				datosExcelDto.setFec_vto(fn.ponerFechaSola(fec_vto));
			}catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
				+"P:Derivados, C:ForwardsDaoImpl, M:validarDatosExcel");
				logger.error(e.toString());
			}

			try{
				datosExcelDto.setInstitucion(Integer.parseInt(renglon.get(i).get("institucion")));
			}catch(Exception e){
				datosExcelDto.setInstitucion(0);
				logger.error(e.toString());
			}

			datosExcelDto.setRubro_cargo(renglon.get(i).get("rubro_cargo"));
			datosExcelDto.setSubrubro_cargo(renglon.get(i).get("subrubro_cargo"));
			datosExcelDto.setRubro_abono(renglon.get(i).get("rubro_abono"));
			datosExcelDto.setSubrubro_abono(renglon.get(i).get("subrubro_abono"));

			try{
				Date fec_compra = format.parse(renglon.get(i).get("fec_compra"));
				datosExcelDto.setFec_compra(fn.ponerFechaSola(fec_compra));
			}catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
				+"P:Derivados, C:ForwardsDaoImpl, M:validarDatosExcel");
				logger.error(e.toString());
			}

			try{
				String spot =  renglon.get(i).get("spot").replaceAll("\\$", "");
				datosExcelDto.setSpot(Double.parseDouble(spot.replaceAll("\\,", "")));
			}catch(Exception e){
				datosExcelDto.setSpot(0.0);
				logger.error(e.toString());
			}

			try{
				String puntos_forward =  renglon.get(i).get("puntos_forward").replaceAll("\\$", "");
				datosExcelDto.setPuntos_forward(Double.parseDouble(puntos_forward.replaceAll("\\,", "")));
			}catch(Exception e){
				datosExcelDto.setPuntos_forward(0.0);
				logger.error(e.toString());
			}

			datosExcelDto.setReferencia(renglon.get(i).get("referencia"));
			datosExcelDto.setConcepto(renglon.get(i).get("concepto"));
			datosExcelDto.setObservacion("");
			datosExcel.add(cosultasPorCampoMov(datosExcelDto));

		}

		return datosExcel;
	}


	// VALIDA LOS DATOS CARGADOS
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private DatosExcelFwsDto cosultasPorCampoMov(DatosExcelFwsDto datosExcel) {
		StringBuffer sb = new StringBuffer();


		try {
			// BUSCA DUPLICADOS
			sb.append(" SELECT COUNT(*) FROM CONTROL_FORWARDS ");
			sb.append("\n  WHERE ID_CHEQUERA_CARGO = '" + datosExcel.getChequera_cargo()             + "'");
			sb.append("\n    AND ID_CHEQUERA_ABONO = '" + datosExcel.getChequera_abono()             + "'");
			sb.append("\n    AND IMPORTE_PAGO = " + datosExcel.getImporte_pago()                          );
			sb.append("\n    AND IMPORTE_FWS = " + datosExcel.getImporte_compra()                         );
			sb.append("\n    AND SPOT = " + datosExcel.getSpot()                                          );
			sb.append("\n    AND PUNTOS_FORWARD = " + datosExcel.getPuntos_forward()                      );
			sb.append("\n    AND TIPO_CAMBIO = " + datosExcel.getTc()                                     );
			sb.append("\n    AND NO_INSTITUCION = " + datosExcel.getInstitucion()                         );
			sb.append("\n    AND FEC_VENC = convert(datetime,'" + datosExcel.getFec_vto()       + "',103)");
			
			System.out.println(sb.toString());
			if(jdbcTemplate.queryForInt(sb.toString()) > 0) {
				datosExcel.setObservacion("Registro dado de alta anteriormente");
				return datosExcel;
			}

			// VALIDA CAMPO VACIO
			if ( 	datosExcel.getFolio() == 0       || datosExcel.getUnidad_negocio().equals("") || datosExcel.getChequera_cargo().equals("") 
					|| datosExcel.getChequera_abono().equals("") ||  datosExcel.getForma_pago().equals("")  || datosExcel.getImporte_pago() == 0.0
					|| datosExcel.getImporte_compra() == 0.0 || datosExcel.getSpot() == 0.0 || datosExcel.getTc() == 0.0
//					||  datosExcel.getPuntos_forward() == 0.0
					|| datosExcel.getTc() == 0 || datosExcel.getInstitucion() == 0
					||  datosExcel.getFec_vto().equals("") ) {
				datosExcel.setObservacion("Datos icompletos");
				return datosExcel;
			}


			// VALIDA FOLIO
			sb = new StringBuffer();
			sb.append("SELECT COUNT(*) FROM CONTROL_FORWARDS WHERE NO_FOLIO = " + datosExcel.getFolio());
			System.out.println(sb.toString());
			if(jdbcTemplate.queryForInt(sb.toString()) > 0) {
				datosExcel.setObservacion("Ya existe folio");
				return datosExcel;
			}

			// VERIFICA QUE EXISTE LA UNIDAD DE NEGOCIO
			//			sb = new StringBuffer();
			//		    sb.append("SELECT COUNT(*) FROM SET006 WHERE SOIEMP = '" + datosExcel.getUnidad_negocio() + "'");
			//			System.out.println(sb.toString());
			//			if(jdbcTemplate.queryForInt(sb.toString()) <= 0) {
			//				datosExcel.setObservacion("No existe unidad de negocio");
			//				return datosExcel;
			//			}


			//			validar chequera y pago de abono sean numericas y que la longitud no sea menor a 8						 			    
			// chequera cargo

			sb = new StringBuffer();
			sb.append("SELECT COUNT(*) FROM CAT_CTA_BANCO WHERE ID_CHEQUERA  = '" + datosExcel.getChequera_cargo() + "'");
			System.out.println(sb.toString());
			if(jdbcTemplate.queryForInt(sb.toString()) == 0) {
				datosExcel.setObservacion("No existe chequera cargo");
				return datosExcel;
			}

			//chequera abono
			sb = new StringBuffer();
			sb.append("SELECT COUNT(*) FROM CAT_CTA_BANCO WHERE ID_CHEQUERA = '" + datosExcel.getChequera_abono() + "'");
			System.out.println(sb.toString());
			if(jdbcTemplate.queryForInt(sb.toString()) == 0) {
				datosExcel.setObservacion("No existe chequera abono");
				return datosExcel;
			}


			//		     '******************************
			//           '***VALIDACION DE FORMA PAGO***
			//           '******************************
			//		   validar que la longitud sea mayor a 1 y verificar que existe
			if(datosExcel.getForma_pago().length() > 1){
				sb = new StringBuffer();
				sb.append("SELECT COUNT(*) FROM CAT_FORMA_PAGO WHERE DESC_FORMA_PAGO = '" + datosExcel.getForma_pago() + "'");
				if(jdbcTemplate.queryForInt(sb.toString()) == 0) {
					datosExcel.setObservacion("No existe Forma de Pago");
					return datosExcel;
				}
			}else{
				datosExcel.setObservacion("Forma de pago no válida");
				return datosExcel;
			}

			//		     '************************************************
			//           '***VALIDACION DE IMPORTE PAGO, IMPORTE COMPRA***
			//           '************************************************
			//		   que sea numero y que no sea menor a 0

			if (datosExcel.getImporte_pago() < 0) {
				datosExcel.setObservacion("Importe de pago no válido");
				return datosExcel;
			}
			if (datosExcel.getImporte_compra() < 0) {
				datosExcel.setObservacion("Importe de compra no válido");
				return datosExcel;
			}

			//		     '*************************************
			//           '***VALIDACION DE FECHA VENCIMIENTO***
			//           '*************************************

			if (datosExcel.getFec_vto() == null && 
					datosExcel.getFec_vto().equals("null") ){
				datosExcel.setObservacion("Falta la fecha de vencimiento");
				return datosExcel;
			}

			sb = new StringBuffer();
			sb.append("SELECT COUNT(*) FROM DIA_INHABIL WHERE FEC_INHABIL = CONVERT(datetime,'"+ datosExcel.getFec_vto() +"', 103) \n");
			System.out.println(sb.toString());
			if(jdbcTemplate.queryForInt(sb.toString()) > 0) {
				datosExcel.setObservacion("Fecha de vencimiento no válida (Día inhabil)");
				return datosExcel;
			}

			//		     '*******************************
			//           '***VALIDACION DE INSTITUCION***
			//           '*******************************
			//validar que no sea menor a 0 y que exista
			if (!(datosExcel.getInstitucion() < 0)){
				sb = new StringBuffer();
				sb.append("SELECT COUNT(*) FROM PERSONA WHERE NO_PERSONA = " + datosExcel.getInstitucion() ); //+ " AND ID_TIPO_PERSONA IN('B','K')");
				System.out.println(sb.toString());
				if(jdbcTemplate.queryForInt(sb.toString()) == 0) {		   
					datosExcel.setObservacion("No existe Institución	");
					return datosExcel;
				}
			}else{
				datosExcel.setObservacion("Institución no válida");
				return datosExcel;
			}

			//		     '********************************
			//           '***VALIDACION DE RUBROS ABONO***
			//           '********************************
			//		   que no sea menor a 4 

			if (!(datosExcel.getRubro_abono().length() < 4 || datosExcel.getSubrubro_abono().length() < 4 )){
				sb = new StringBuffer();
				sb.append("SELECT COUNT(*) FROM CAT_RUBRO WHERE ID_GRUPO = '" + datosExcel.getRubro_abono() + "' AND ID_RUBRO = '" + datosExcel.getSubrubro_abono() + "'"); 
				System.out.println(sb.toString());
				if(jdbcTemplate.queryForInt(sb.toString()) == 0) {		   
					datosExcel.setObservacion("No existe rubros abono");
					return datosExcel;
				}else{
					// validar que ingreso_egreso diferente de E
					List<String> ingreso_egreso = new ArrayList<String>();
					sb = new StringBuffer();  			
					sb.append("SELECT * FROM CAT_RUBRO WHERE ID_GRUPO = '" + datosExcel.getRubro_abono() + "' AND ID_RUBRO = '" + datosExcel.getSubrubro_abono() + "'");


					ingreso_egreso = jdbcTemplate.query(sb.toString(), new RowMapper(){
						public String mapRow(ResultSet rs, int idx)throws SQLException{
							return rs.getString("ingreso_egreso");
						}
					});
					System.out.println(ingreso_egreso.get(0));
					String ie = ingreso_egreso.get(0);
					if (ie == "E" ) {
						datosExcel.setObservacion("Rubros abono no válidos");
						return datosExcel;
					}

				}
			}else{
				datosExcel.setObservacion("Rubros abono no validos (Longitud)");
				return datosExcel;
			}

			//		   '********************************
			//         '***VALIDACION DE RUBROS CARGO***
			//         '******************************** 

			if (!(datosExcel.getRubro_cargo().length() < 4 || datosExcel.getSubrubro_cargo().length() < 4 )){
				sb = new StringBuffer();
				sb.append("SELECT COUNT(*) FROM CAT_RUBRO WHERE ID_GRUPO = '" + datosExcel.getRubro_cargo() + "' AND ID_RUBRO = '" + datosExcel.getSubrubro_cargo() + "'"); 
				System.out.println(sb.toString());
				if(jdbcTemplate.queryForInt(sb.toString()) == 0) {		   
					datosExcel.setObservacion("No existe rubros cargo");
					return datosExcel;
				}else{

					// validar que ingreso_egreso diferente de I
					List<String> ingreso_egreso = new ArrayList<String>();
					sb = new StringBuffer();
					sb.append("SELECT * FROM CAT_RUBRO WHERE ID_GRUPO = '" + datosExcel.getRubro_cargo() + "' AND ID_RUBRO = '" + datosExcel.getSubrubro_cargo() + "'"); 
					ingreso_egreso = jdbcTemplate.query(sb.toString(), new RowMapper(){
						public String mapRow(ResultSet rs, int idx)throws SQLException{
							return rs.getString("ingreso_egreso");
						}
					});

					System.out.println(ingreso_egreso.get(0));
					String ie = ingreso_egreso.get(0);
					if (ie == "I" ) {
						datosExcel.setObservacion("Rubros cargo no válidos");
						return datosExcel;
					}

				}
			}else{
				datosExcel.setObservacion("Rubros cargo no validos");
				return datosExcel;
			}
			
			


		} catch (Exception e) {
			logger.error(e.toString());
			datosExcel.setObservacion("Error de formato");
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Derivados, C:ForwardsDaoImpl, M:cosultasPorCampoMov");
		}
		return datosExcel;
	}

	@Override
	public List<DatosExcelFwsDto> datosFwsFaltantes(String matrizDatos, int usuario_alta) {

		StringBuffer sb = new StringBuffer();
		//		String fechaHoy = formato.format(gb.getFechaHoy());
//		System.out.println("Obtener datos faltantes para llenar los forwards");
		List<DatosExcelFwsDto> datosExcel = new ArrayList<DatosExcelFwsDto>();
		Gson gson = new Gson();
		List<Map<String, String>> datos = gson.fromJson(matrizDatos, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		System.out.println(datos);
		for(int i = 0; i < datos.size(); i++){
			DatosExcelFwsDto datoFws = new DatosExcelFwsDto();
			datoFws.setFolio(Integer.parseInt(datos.get(i).get("folio")));
			datoFws.setUnidad_negocio(datos.get(i).get("unidad_negocio"));

			String chequera_cargo =  datos.get(i).get("chequera_cargo");
			//			System.out.println(chequera_cargo);
			datoFws.setChequera_cargo(chequera_cargo);

			//con la chequera cargo obtener divisa venta y banco cargo
			String divisaVenta = "";
			try {
				String bancoCargo;

				sb = new StringBuffer();
				sb.append("SELECT DISTINCT id_divisa FROM CAT_CTA_BANCO WHERE ID_CHEQUERA = '" + datos.get(i).get("chequera_cargo") + "'");
				System.out.println(sb.toString());
				divisaVenta = this.getJdbcTemplate().queryForObject(sb.toString(), String.class);
				datoFws.setDivisa_venta(divisaVenta);

				sb = new StringBuffer();
				sb.append("SELECT DISTINCT id_banco FROM CAT_CTA_BANCO WHERE ID_CHEQUERA = '" + datos.get(i).get("chequera_cargo") + "'");
				System.out.println(sb.toString());
				bancoCargo = this.getJdbcTemplate().queryForObject(sb.toString(), String.class);
				datoFws.setBanco_cargo(Integer.parseInt(bancoCargo));


			} catch (Exception e) {
				System.out.println(e);
				logger.error(e.toString());
			}



			//con la chequera abono obtener divisa compra y banco abono	
			String chequera_abono =  datos.get(i).get("chequera_abono");
			//System.out.println(chequera_abono);
			datoFws.setChequera_abono(chequera_abono);

			try {
				String divisaCompra;
				String banco_abono;

				sb = new StringBuffer();
				sb.append("SELECT DISTINCT id_divisa FROM CAT_CTA_BANCO WHERE ID_CHEQUERA = '" + datos.get(i).get("chequera_abono") + "'");
				System.out.println(sb.toString());
				divisaCompra = this.getJdbcTemplate().queryForObject(sb.toString(), String.class);
				datoFws.setDivisa_compra(divisaCompra);

				sb = new StringBuffer();
				sb.append("SELECT DISTINCT id_banco FROM CAT_CTA_BANCO WHERE ID_CHEQUERA = '" + datos.get(i).get("chequera_abono") + "'");
				System.out.println(sb.toString());
				banco_abono = this.getJdbcTemplate().queryForObject(sb.toString(), String.class);
				datoFws.setBanco_abono(Integer.parseInt(banco_abono));

			} catch (Exception e) {
				System.out.println(e);
				logger.error(e.toString());
			}

			// Forma de pago 
			String id_forma_pago = "";
			try{

//				System.out.println(datos.get(i).get("forma_pago") + "++++++++++++++++Forma pago");
				sb = new StringBuffer();
				sb.append("SELECT DISTINCT id_forma_pago FROM CAT_FORMA_PAGO WHERE DESC_FORMA_PAGO = '" + datos.get(i).get("forma_pago") + "'");
				System.out.println(sb.toString());
				id_forma_pago = this.getJdbcTemplate().queryForObject(sb.toString(), String.class);
				datoFws.setForma_pago(id_forma_pago);

			}catch(Exception e){
				System.out.println(e);
				logger.error(e.toString());
			}


			datoFws.setImporte_pago(Double.parseDouble(datos.get(i).get("importe_pago").replaceAll("\\$", "")));
			datoFws.setImporte_compra(Double.parseDouble(datos.get(i).get("importe_compra").replaceAll("\\$", "")));
			datoFws.setTc(Double.parseDouble(datos.get(i).get("tc").replaceAll("\\$", "")));
			datoFws.setFec_compra(datos.get(i).get("fec_compra"));
			datoFws.setFec_vto(datos.get(i).get("fec_vto"));

//			System.out.println("Iniciando institucion");
			datoFws.setInstitucion(Integer.parseInt(datos.get(i).get("institucion")));


			String desc_institucion = "";
			try{
				sb = new StringBuffer();
				sb.append("SELECT DISTINCT razon_social FROM PERSONA WHERE NO_PERSONA = '" + datos.get(i).get("institucion") + "'");
				sb.append(" AND ID_TIPO_PERSONA IN('B','K')");
				System.out.println(sb.toString());
				desc_institucion = this.getJdbcTemplate().queryForObject(sb.toString(), String.class);
				datoFws.setDesc_institucion(desc_institucion);

			}catch(Exception e){
				System.out.println(e);
				logger.error(e.toString());
			}


			//obtener Operador

			try {
				String operador;
				//				System.out.println(datos.get(i).get("institucion") + "++++++++++++++++  Institucion");
				sb = new StringBuffer();
				sb.append("SELECT DISTINCT nombre_corto FROM PERSONA WHERE NO_PERSONA in (select no_persona_rel from relacion where no_persona = '" + datos.get(i).get("institucion") + "') and id_tipo_persona = 'F'");
				System.out.println(sb.toString());
				operador = this.getJdbcTemplate().queryForObject(sb.toString(), String.class);
				datoFws.setNom_contacto(operador);

//				System.out.println(id_forma_pago + " id Forma de pago");
				String id_banco_benef = "";
				String id_chequera_benef = "";
				if (id_forma_pago == "3") {
					try {
						sb = new StringBuffer();
						sb.append(" SELECT id_banco as ID");
						sb.append(" FROM cat_banco ");
						sb.append(" WHERE id_banco in (Select distinct c.id_banco from ctas_banco c, persona p ");
						sb.append(" Where p.no_persona = c.no_persona ");
						sb.append(" And p.id_tipo_persona = 'K'  ");
						sb.append(" And c.id_divisa = '" + divisaVenta + "'");
						sb.append(" And p.no_persona = '" + datos.get(i).get("institucion") + "'");
						sb.append(" And c.no_empresa = 552) ");
						System.out.println(sb.toString());
						id_banco_benef = this.getJdbcTemplate().queryForObject(sb.toString(), String.class);
						datoFws.setBanco_benef(Integer.parseInt(id_banco_benef));
					} catch (Exception e) {
						datoFws.setBanco_benef(0);
						System.out.println(e);
						logger.error(e.toString());
					}

					try {
						sb = new StringBuffer();
						sb.append(" SELECT id_chequera as id");
						sb.append(" FROM ctas_banco ");
						sb.append(" WHERE id_divisa = '" + divisaVenta + "'");
						sb.append(" And p.no_persona = '" + datos.get(i).get("institucion") + "'");
						sb.append(" And c.id_banco = '" + id_banco_benef +"'");
						System.out.println(sb.toString());
						id_chequera_benef = this.getJdbcTemplate().queryForObject(sb.toString(), String.class);
						datoFws.setChequera_benef(id_chequera_benef);
					} catch (Exception e) {
						datoFws.setChequera_benef("0");
						System.out.println(e);
						logger.error(e.toString());
					}
				}else{
					datoFws.setBanco_benef(0);
					datoFws.setChequera_benef("NULL");
				}

			} catch (Exception e) {
				datoFws.setNom_contacto("");
				System.out.println(e);
				logger.error(e.toString());
			}


			//			fijar rubros

			datoFws.setRubro_cargo(datos.get(i).get("rubro_cargo"));
			datoFws.setSubrubro_cargo(datos.get(i).get("subrubro_cargo"));
			datoFws.setRubro_abono(datos.get(i).get("rubro_abono"));
			datoFws.setSubrubro_abono(datos.get(i).get("subrubro_abono"));

			//tasas
			datoFws.setSpot(Double.parseDouble(datos.get(i).get("spot").replaceAll("\\$", "")));
			datoFws.setPuntos_forward(Double.parseDouble(datos.get(i).get("puntos_forward").replaceAll("\\$", "")));


			//FIRMANTES

			//			try {
			//				String firmante1;
			//				System.out.println(datos.get(i).get("institucion") + "++++++++++++++++  Institucion");
			//				sb = new StringBuffer();
			//				sb.append("SELECT DISTINCT nombre_firmante FROM firma_cvd");
			//				System.out.println(sb.toString());
			//				firmante1 = this.getJdbcTemplate().queryForObject(sb.toString(), String.class);
			//				System.out.println("FIRMANTE " + firmante1);
			//				datoFws.setFirmante1(firmante1);
			//			} catch (Exception e) {
			//				System.out.println(e);
			//			}

			// CAJA USUARIO
			int id_caja  = 0;
			try{
				sb = new StringBuffer();
				sb.append("SELECT id_caja from SEG_USUARIO where id_usuario = '" + usuario_alta + "'");
				System.out.println(sb.toString());
				id_caja = this.getJdbcTemplate().queryForInt(sb.toString());
				datoFws.setCaja(id_caja);
			}catch(Exception e){
				System.out.println(e);
				logger.error(e.toString());
			}

			datoFws.setFirmante1("firmante1");
			datoFws.setFirmante2("firmante2");
			datoFws.setEstatus_imp('P');
			datoFws.setEstatus_mov('A');
			
			datoFws.setReferencia(datos.get(i).get("referencia"));
			datoFws.setConcepto(datos.get(i).get("concepto"));

			datosExcel.add(datoFws);

		}

		return datosExcel;
	}



	// Lod datos llegan ya listos solo para insertarse en la tabla 




	@Override
	public void insertarFwdCarga(int folio, String unidad_negocio, String divisa_venta, int banco_cargo,
			String chequera_cargo, String divisa_compra, int banco_abono, String chequera_abono, String forma_pago,
			Double importe_pago, Double importe_compra, Double tc, String fec_compra, String fec_vto, int institucion,
			String nom_contacto, int banco_benef, String chequera_benef, String rubro_cargo, String subrubro_cargo,
			String rubro_abono, String subrubro_abono, char estatus_mov, char estatus_imp, String firmante1,
			String firmante2, String no_docto, Double spot, Double puntos_forward,String referencia, String concepto) {

		StringBuffer sbSql = new StringBuffer();
		try{

			sbSql.append("INSERT INTO CONTROL_FORWARDS");
			sbSql.append("\n (NO_FOLIO, NO_EMPRESA,  ID_DIVISA_VENTA, ID_BANCO_CARGO, ID_CHEQUERA_CARGO, "
					+ "ID_DIVISA_COMPRA, ID_BANCO_ABONO, ID_CHEQUERA_ABONO, ID_FORMA_PAGO, IMPORTE_PAGO,  "
					+ "IMPORTE_FWS,  TIPO_CAMBIO,  FEC_ALTA,  FEC_VENC, NO_INSTITUCION, NOM_CONTACTO, "
					+ "ID_BANCO_BENEF,  ID_CHEQUERA_BENEF, RUBRO_CGO, SUBRUBRO_CGO, RUBRO_ABN, SUBRUBRO_ABN, "
					+ "ESTATUS_MOV, ESTATUS_IMP, FIRMANTE_1, FIRMANTE_2, SPOT, PUNTOS_FORWARD,REFERENCIA,CONCEPTO)");
			sbSql.append("\n values (" + folio + ", '" + unidad_negocio + "', '" +divisa_venta + "' ," 
					+ banco_cargo + ", '" + chequera_cargo + "', '" + divisa_compra + "', "
					+ banco_abono + ", '" + chequera_abono + "', '" + forma_pago + "', " 
					+ importe_pago + "," + importe_compra + "," + tc + ", '" + fec_compra +"', "
					+ "'" + fec_vto + "', " + institucion + ", '" + nom_contacto + "', " + banco_benef +", '" 
					+ chequera_benef + "', '" + rubro_cargo + "', '" + subrubro_cargo + "', '"
					+ rubro_abono + "', '" + subrubro_abono + "', '" + estatus_mov+"', '"
					+ estatus_imp + "', '" + firmante1 + "', '" + firmante2 + "', '" + spot + "', "
					+ puntos_forward + ", '" + referencia + "', '" + concepto + "'" + ")");

			System.out.println("querys"+sbSql.toString());
			jdbcTemplate.update(sbSql.toString());	
		}
		catch(Exception e){
			logger.error(e.toString());
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Derivados, C:ForwardsDaoImpl, M:insertarFwdCarga");
		}


	}

	@Override
	public void funSQLInsert244(String unidad_negocio, String no_docto, int no_folio_param, int id_tipo_docto,
			String forma_pago, int usuario_alta, int id_tipo_operacion, int no_cuenta, String no_factura,
			String fec_valor, String fec_valor_original, String fec_operacion, String id_divisa, String fec_alta,
			Double importe, Double importe_original, Double tipo_cambio, int id_caja, String id_divisa_original,
			String referencia, String beneficiario, String concepto, int id_banco_benef, String id_chequera_benef,
			String id_leyenda, String id_chequera, int id_banco, String observacion, Boolean adu, int no_cliente,
			String solicita, String autoriza, int plaza, int sucursal, int grupo, int agrupa1, int agrupa2, int agrupa3,
			String id_rubro, Boolean cvDivisa, int no_pedido, int id_area, String id_grupo, String no_cheque) {

		System.out.println("funSQLInsert 3000");

		StringBuffer sbSql;
		try{		
			//			System.out.println("funSQLInsert2 3000");
			//			sbSql = new StringBuffer();
			//			sbSql.append("  select MAX(no_folio_param)+1 from parametro ");
			//			System.out.println(sbSql.toString());
			//			try {
			//				no_folio_param = Integer.parseInt(getJdbcTemplate().queryForObject(sbSql.toString(), String.class));
			//				System.out.println("Folio parametro: " + no_folio_param);
			//			} catch (Exception e) {
			//				logger.error(e.toString());
			//				no_folio_param = 1;
			//			}


			sbSql = new StringBuffer();
			sbSql.append("INSERT INTO parametro");
			sbSql.append("\n (no_empresa,no_docto,no_folio_param,id_tipo_docto,id_forma_pago,"
					+ "usuario_alta,id_tipo_operacion,no_cuenta,no_factura,"
					+ "fec_valor,fec_valor_original,fec_operacion,id_divisa,"
					+ "fec_alta,importe,importe_original,tipo_cambio,"
					+ "id_caja,id_divisa_original,referencia,beneficiario,"
					+ "concepto,id_banco_benef,id_chequera_benef,aplica,"
					+ "id_estatus_mov,b_salvo_buen_cobro,id_estatus_reg,id_leyenda,"
					+ "id_chequera,id_banco,observacion,origen_mov,no_cliente,"
					+ "solicita, autoriza, plaza, sucursal,"
					+ "grupo, no_folio_mov, agrupa1, agrupa2, agrupa3, id_rubro");
			if (adu) {sbSql.append(",no_pedido");}
			if (id_area != -1) {sbSql.append(",id_area");}
			sbSql.append(",id_grupo");
			if (!no_cheque.equals("")) {sbSql.append(",no_cheque");}
			sbSql.append(")");


			sbSql.append("\n values ('"+ unidad_negocio + "'," + no_docto + "," + no_folio_param + "," + id_tipo_docto 
					+ ",'" + forma_pago + "', " + usuario_alta + "," + id_tipo_operacion + "," + no_cuenta + ", '" 
					+ no_factura + "', '" + fec_valor + "', '" + fec_valor_original + "', '" + fec_operacion + "', '"
					+ id_divisa + "', '" + fec_alta + "', " + importe + ", " + importe_original + ", " + tipo_cambio + ", "
					+ id_caja + ", '" + id_divisa_original + "', '" + referencia + "', '" + beneficiario + "', '" 
					+ concepto + "', " );
			if (id_banco_benef != 0)sbSql.append(id_banco_benef);
			else sbSql.append("NULL");
			sbSql.append(", '" + id_chequera_benef + "', 1 , 'C', 'S', 'P', " + "'"        //   aplica = 1
					+ id_leyenda + "', '" + id_chequera + "', " + id_banco + ", '" + observacion + "', ");
			if (adu) { sbSql.append("'ADU',");
			}else{ if (cvDivisa) { sbSql.append("'CVD',");
			}else{ sbSql.append("'SET', "); }}
			sbSql.append(no_cliente + ", ");
			if (solicita.equals("")) { sbSql.append("NULL, ");
			}else{sbSql.append("'" + solicita + "', ");}
			if (autoriza.equals("")) { sbSql.append("NULL, ");
			}else{sbSql.append("'" + autoriza + "', ");}
			sbSql.append(plaza + ", " + sucursal + ", '" + grupo + "', 1, '" + agrupa1 + "', '" + agrupa2 + "', '" 
					+ agrupa3 + "', '" + id_rubro + "', ");
			if (adu) {sbSql.append(no_pedido + ", ");}
			if (id_area != -1) {sbSql.append(id_area + ", ");}
			sbSql.append("'" + id_grupo + "'");
			if (!no_cheque.equals("")) { sbSql.append(", '" +no_cheque + "'");}	
			sbSql.append(")");
			System.out.println("funSQLInsert3000");
			System.out.println("querys"+sbSql.toString());
			jdbcTemplate.update(sbSql.toString());	
		}
		catch(Exception e){
			logger.error(e.toString());
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Derivados, C:ForwardsDaoImpl, M:funSQLInsert244");
		}		
	}

	@Override
	public void funSQLInsert245(String unidad_negocio, String no_docto, int no_folio_param, int id_tipo_docto,
			String forma_pago, int usuario_alta, int id_tipo_operacion, int no_cuenta, String no_factura,
			String fec_valor, String fec_valor_original, String fec_operacion, String id_divisa, String fec_alta,
			Double importe, Double importe_original, Double tipo_cambio, int id_caja, String id_divisa_original,
			String referencia, String beneficiario, String concepto, int id_banco_benef, String id_chequera_benef,
			String id_leyenda, String id_chequera, int id_banco, String observacion, Boolean adu, int no_cliente,
			String solicita, String autoriza, int plaza, int sucursal, int grupo, int agrupa1, int agrupa2, int agrupa3,
			String id_rubro, Boolean cvDivisa, int no_pedido, int id_area, String id_grupo, String no_cheque) {

		System.out.println("funSQLInsert245");

		StringBuffer sbSql;
		try{


			sbSql = new StringBuffer();
			sbSql.append("INSERT INTO parametro");
			sbSql.append("\n (no_empresa,no_docto,no_folio_param,id_tipo_docto,id_forma_pago,"
					+ "usuario_alta,id_tipo_operacion,no_cuenta,no_factura,"
					+ "fec_valor,fec_valor_original,fec_operacion,id_divisa,"
					+ "fec_alta,importe,importe_original,tipo_cambio,"
					+ "id_caja,id_divisa_original,referencia,beneficiario,"
					+ "concepto,id_banco_benef,id_chequera_benef,aplica,"
					+ "id_estatus_mov,b_salvo_buen_cobro,id_estatus_reg,id_leyenda,folio_ref,"   //Aumenta folio_ref
					+ "id_chequera,id_banco,observacion,origen_mov,no_cliente,"
					+ "solicita, autoriza, plaza, sucursal,"
					+ "grupo, no_folio_mov,id_rubro, agrupa1, agrupa2, agrupa3");
			if (adu) {sbSql.append(",no_pedido");}
			if (id_area != -1) {sbSql.append(",id_area");}
			sbSql.append(",id_grupo");
			if (!no_cheque.equals("")) {sbSql.append(",no_cheque");}
			sbSql.append(")");


			sbSql.append("\n values ('"+ unidad_negocio + "'," + no_docto + "," + no_folio_param + "," + 41 
					+ ",'" + forma_pago + "', " + usuario_alta + "," + id_tipo_operacion + "," + no_cuenta + ", '" 
					+ no_docto + "', '" + fec_valor + "', '" + fec_valor_original + "', '" + fec_operacion + "', '"
					+ id_divisa + "', '" + fec_alta + "', " + importe + ", " + importe + ", " + tipo_cambio + ", "
					+ id_caja + ", '" + id_divisa_original + "', '" + "NULL" + "', '" + beneficiario + "', '" 
					+ concepto + "', " );
			if (id_banco_benef != 0)sbSql.append(id_banco_benef);
			else sbSql.append("NULL");
			sbSql.append(", '" + id_chequera_benef + "', 1 , 'H', 'S', 'P', " + "'"        //   aplica = 1
					+ id_leyenda + "', 1, '" + id_chequera + "', " + id_banco + ", '" + observacion + "', ");
			if (adu) { sbSql.append("'ADU',");
			}else{ if (cvDivisa) { sbSql.append("'CVD',");
			}else{ sbSql.append("'SET', "); }}
			sbSql.append(no_cliente + ", ");
			if (solicita.equals("")) { sbSql.append("NULL, ");
			}else{sbSql.append("'" + solicita + "', ");}
			if (autoriza.equals("")) { sbSql.append("NULL, ");
			}else{sbSql.append("'" + autoriza + "', ");}
			sbSql.append(plaza + ", " + sucursal + ", '" + grupo + "', 1, '" + id_rubro + "', '" + agrupa1 + "', '" + agrupa2 + "', '" 
					+ agrupa3 + "', ");
			if (adu) {sbSql.append(no_pedido + ", ");}
			if (id_area != -1) {sbSql.append(id_area + ", ");}
			sbSql.append("'" + id_grupo + "'");
			if (!no_cheque.equals("")) { sbSql.append(", '" +no_cheque + "'");}	
			sbSql.append(")");
			System.out.println("funSQLInsert3");
			System.out.println("querys"+sbSql.toString());
			jdbcTemplate.update(sbSql.toString());	
		}
		catch(Exception e){
			logger.error(e.toString());
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Derivados, C:ForwardsDaoImpl, M:funSQLInsert245");
		}
	}

	@Override
	public void funSQLInsert247(String unidad_negocio, String no_docto, int no_folio_param, int id_tipo_docto,
			String forma_pago, int usuario_alta, int id_tipo_operacion, int no_cuenta, String no_factura,
			String fec_valor, String fec_valor_original, String fec_operacion, String id_divisa, String fec_alta,
			Double importe, Double importe_original, Double tipo_cambio, int id_caja, String id_divisa_original,
			String referencia, String beneficiario, String concepto, int id_banco_benef, String id_chequera_benef,
			String id_leyenda, String id_chequera, int id_banco, String observacion, Boolean aDU, int no_cliente,
			String solicita, String autoriza, int plaza, int sucursal, int grupo, int agrupa1, int agrupa2, int agrupa3,
			String id_rubro, Boolean cVDivisa, int no_pedido, int id_area, String id_grupo, String no_cheque) {


		System.out.println("funSQLInsert247");

		StringBuffer sbSql;
		try{
			sbSql = new StringBuffer();
			sbSql.append("INSERT INTO parametro");
			sbSql.append("\n (no_empresa, no_docto,no_folio_param, id_tipo_docto, id_forma_pago,"
					+ "usuario_alta, id_tipo_operacion, no_cuenta, no_factura,"
					+ "fec_valor, fec_valor_original, fec_operacion, id_divisa,"
					+ "fec_alta, importe, importe_original, tipo_cambio,"
					+ " id_caja, id_divisa_original, referencia, beneficiario,"
					+ "concepto, id_banco_benef, id_chequera_benef, aplica,"
					+ "id_estatus_mov, b_salvo_buen_cobro, id_estatus_reg, id_leyenda,"
					+ "folio_ref, id_chequera, observacion, origen_mov,"
					+ "no_cliente, solicita, autoriza, plaza,"
					+ "sucursal, grupo, no_folio_mov,"
					+ "id_rubro,agrupa1,agrupa2, agrupa3, id_banco");

			if (aDU) {
				sbSql.append(",no_pedido,id_grupo)");
			}else{ 
				if(!id_grupo.equals("")){
					sbSql.append(",id_grupo)");
				}else{
					sbSql.append(")");
				}
			}




			sbSql.append("\n values ('"+ unidad_negocio + "'," + no_docto + "," + no_folio_param + "," + 41 
					+ ",'" + forma_pago + "', " + usuario_alta + "," + id_tipo_operacion + "," + no_cuenta + ", '" 
					+ no_docto + "', '" + fec_valor + "', '" + fec_valor_original + "', '" + fec_operacion + "', '"
					+ id_divisa + "', '" + fec_alta + "', " + importe + ", " + importe + ", " + tipo_cambio + ", "
					+ id_caja + ", '" + id_divisa_original + "', '" + "NULL" + "', '" + beneficiario + "', '" 
					+ concepto + "', " );
			if (id_banco_benef != 0)sbSql.append(id_banco_benef);
			else sbSql.append("NULL");
			sbSql.append(", '" + id_chequera_benef + "', 1 , 'L', 'S', 'P', " + "'"        //   aplica = 1
					+ id_leyenda + "', 1, '" + id_chequera +  "', '" + observacion + "', ");
			if (aDU) { sbSql.append("'ADU',");
			}else{ if (cVDivisa) { sbSql.append("'CVD',");
			}else{ sbSql.append("'SET', "); }}
			sbSql.append(no_cliente + ", ");
			if (solicita.equals("")) { sbSql.append("NULL, ");
			}else{sbSql.append("'" + solicita + "', ");}
			if (autoriza.equals("")) { sbSql.append("NULL, ");
			}else{sbSql.append("'" + autoriza + "', ");}
			sbSql.append(plaza + ", " + sucursal + ", '" + grupo + "', 1, '" + id_rubro + "', '" + agrupa1 + "', '" + agrupa2 + "', '" 
					+ agrupa3 + "', " + id_banco);

			if (aDU) {
				sbSql.append(", " + no_pedido + ", '" + id_grupo + "')");
			}else{
				if(!id_grupo.equals("")){
					sbSql.append(", '" + id_grupo + "')");
				}else{
					sbSql.append(")");
				}
			}
			System.out.println("querys"+sbSql.toString());
			jdbcTemplate.update(sbSql.toString());	
		}
		catch(Exception e){
			logger.error(e.toString());
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Derivados, C:ForwardsDaoImpl, M:funSQLInsert247");
		}	
	}

	@Override
	public void funSQLInsertFlujoProgramado(String id_grupo, int id_cuadrante, String id_rubro, String id_divisa,
			String fec_valor, int agrupa1, int agrupa2, int agrupa3, String unidad_negocio, Double importe) {


		System.out.println("FunSQLInsertFlujoProgramado");
		StringBuffer sbSql;
		try{
			sbSql = new StringBuffer();
			sbSql.append("INSERT INTO SALDO_CUADRANTE");
			sbSql.append("\n (ID_GRUPO, ID_CUADRANTE,ID_RUBRO,ID_DIVISA,FEC_VALOR,AGRUPA1,AGRUPA2,"
					+ "AGRUPA3,NO_EMPRESA,IMPORTE)");

			sbSql.append("\n values ('"+ id_grupo + "'," + id_cuadrante + ",'" + id_rubro + "', '"
					+ id_divisa + "', '" + fec_valor + "','" + agrupa1 + "', '" + agrupa2 + "', '" 
					+ agrupa3 + "', " + unidad_negocio + ", " + importe +")");	

			System.out.println("querys"+sbSql.toString());
			jdbcTemplate.update(sbSql.toString());	
		}
		catch(Exception e){
			logger.error(e.toString());
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Derivados, C:ForwardsDaoImpl, FunSQLInsertFlujoProgramado");
		}	


	}

	@Override
	public int funSqlGetFlujoProgramado(String id_grupo, int id_cuadrante, String id_rubro, String id_divisa,
			String fec_valor, String unidad_negocio) {

		StringBuffer sbSql;
		int rows = 0;
		try {
			sbSql = new StringBuffer();
			sbSql.append(" SELECT COUNT(*) FROM SALDO_CUADRANTE ");
			sbSql.append("\n  WHERE LTRIM(RTRIM(id_grupo)) = '" + id_grupo  + "'");
			sbSql.append("\n    AND LTRIM(RTRIM(id_rubro)) = '" + id_rubro  + "'");
			sbSql.append("\n    AND ID_CUADRANTE = " + id_cuadrante              );
			sbSql.append("\n    AND FEC_VALOR = convert(datetime,'" +fec_valor+ "',103)"); 
			sbSql.append("\n    AND ID_DIVISA = '" + id_divisa			    + "'");
			sbSql.append("\n    AND NO_EMPRESA = " + unidad_negocio              );

			System.out.println(sbSql.toString());
			rows = jdbcTemplate.queryForInt(sbSql.toString());
		} catch (Exception e) {
			logger.error(e.toString());
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Derivados, C:ForwardsDaoImpl, M:funSqlGetFlujoProgramado");
		}

		return rows;


	}

	@Override
	public int actualizarFolioReal(String tipoFolio) {

		String sql = null; 
		StringBuilder sb = new StringBuilder();

		sb.append("UPDATE FOLIO "					).append("\n");
		sb.append("SET num_folio = num_folio + 1 "	).append("\n");
		sb.append("WHERE tipo_folio = ?"			).append("\n");

		sql = sb.toString();
		System.out.println(sql);
		return jdbcTemplate.update(sql, new Object[]{ tipoFolio } );

	}//END METHOD: actualizarFolioReal

	@Override
	public int seleccionarFolioReal(String tipoFolio) {

		String sql = null; 
		StringBuilder sb = new StringBuilder();

		sb.append("SELECT num_folio"		).append("\n");
		sb.append("FROM FOLIO"				).append("\n");
		sb.append("WHERE tipo_folio = ?"	).append("\n");

		sql = sb.toString(); 
		System.out.println(sql);
		return jdbcTemplate.queryForInt(sql, new Object[]{ tipoFolio } );

	}//END METHOD:seleccionarFolioReal 

	@Override
	public void funSQLUpdateFlujoProgramado(String id_grupo, int id_cuadrante, String id_rubro, String id_divisa,
			String fec_valor, int agrupa1, int agrupa2, int agrupa3, String unidad_negocio, Double importePago) {	
		//		System.out.println("funSQLUpdateFlujoProgramado");
		StringBuffer sbSql;
		try{
			sbSql = new StringBuffer();
			sbSql.append("UPDATE SALDO_CUADRANTE SET IMPORTE = (IMPORTE + " + importePago + ")");
			sbSql.append("\n WHERE LTRIM(RTRIM(id_grupo)) = '" + id_grupo + "'");
			sbSql.append("\n AND LTRIM(RTRIM(id_rubro)) = '" + id_rubro + "'");
			sbSql.append("\n AND ID_CUADRANTE = " + id_cuadrante);
			sbSql.append("\n AND FEC_VALOR = convert(datetime,'" +fec_valor+ "',103)");
			sbSql.append("\n AND ID_DIVISA = '"+ id_divisa + "'");
			sbSql.append("\n AND NO_EMPRESA = '" + unidad_negocio + "'");
			System.out.println("querys"+sbSql.toString());
			jdbcTemplate.update(sbSql.toString());	
		}
		catch(Exception e){
			logger.error(e.toString());
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Derivados, C:ForwardsDaoImpl, funSQLUpdateFlujoProgramado");

		}	

	}



	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public List<LlenaComboGralDto> consultarDivisas(int bExistentes, int idDiv) {
		StringBuffer sbSql = new StringBuffer();
		List<LlenaComboGralDto> listDiv= new ArrayList<LlenaComboGralDto>();

		try{
			sbSql.append("\n SELECT id_divisa as ID, desc_divisa as Descrip   ");
			sbSql.append("\n FROM cat_divisa ");	
			sbSql.append("\n WHERE clasificacion = 'D' ");
			sbSql.append("\n AND id_divisa IN( SELECT DISTINCT   id_divisa ");
			sbSql.append("\n   FROM cat_cta_banco ");
			sbSql.append("\n  WHERE no_empresa="+idDiv+"  ");
			sbSql.append("\n AND no_empresa in( SELECT no_empresa ");	
			sbSql.append("\n FROM usuario_empresa  ");
			sbSql.append("\n WHERE no_usuario = "+bExistentes+" )) ");


			System.out.println("query"+sbSql.toString());
			listDiv = jdbcTemplate.query(sbSql.toString(), new RowMapper()
			{
				public LlenaComboGralDto mapRow(ResultSet rs, int idx)throws SQLException
				{
					LlenaComboGralDto dtoCons = new LlenaComboGralDto();

					dtoCons.setIdStr(rs.getString("ID"));

					dtoCons.setDescripcion(rs.getString("Descrip"));

					return dtoCons;
				}
			});
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P: FinanciamientoModificacionC, C: FinanciamientoModificacionCDaoImpl, consultarLineas");
		}
		return listDiv;	


	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public List<LlenaComboGralDto> consultarBancos(int idEmp, String idDiv) {
		StringBuffer sbSql = new StringBuffer();
		List<LlenaComboGralDto> listDiv= new ArrayList<LlenaComboGralDto>();

		try
		{


			sbSql.append("\n  SELECT id_banco as ID, desc_banco as Descrip ");
			sbSql.append("\n  FROM cat_banco ");
			sbSql.append("\n WHERE id_banco in (SELECT distinct id_banco ");	
			sbSql.append("\n FROM cat_cta_banco");
			sbSql.append("\n WHERE tipo_chequera in ('P','M') ");
			sbSql.append("\n AND no_empresa = " + idEmp );
			sbSql.append("\n AND id_divisa = '" + idDiv + "') ");


			System.out.println("query"+sbSql.toString());
			listDiv = jdbcTemplate.query(sbSql.toString(), new RowMapper()
			{
				public LlenaComboGralDto mapRow(ResultSet rs, int idx)throws SQLException{
					LlenaComboGralDto dtoCons = new LlenaComboGralDto();
					dtoCons.setId(rs.getInt("ID"));
					dtoCons.setDescripcion(rs.getString("Descrip"));

					return dtoCons;
				}
			});
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P: FinanciamientoModificacionC, C: FinanciamientoModificacionCDaoImpl, consultarLineas");
		}
		return listDiv;	

	}




	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public List<LlenaComboGralDto> obtenerChequeraAbo( int noEmpresa, String idDivisa,
			int idBanco) {
		StringBuffer sbSql = new StringBuffer();
		List<LlenaComboGralDto> list = new ArrayList<LlenaComboGralDto>();

		try{ 
			sbSql.append("\n  SELECT id_chequera as id ");
			sbSql.append("\n FROM cat_cta_banco ");
			sbSql.append("\n WHERE tipo_chequera in ('P','M') ");	
			sbSql.append("\n AND no_empresa = " + noEmpresa );
			sbSql.append("\n AND id_divisa = '" + idDivisa + "' ");
			sbSql.append("\n AND id_banco = " + idBanco );


			System.out.println("query"+sbSql.toString());
			list = jdbcTemplate.query(sbSql.toString(), new RowMapper(){
				public LlenaComboGralDto mapRow(ResultSet rs, int idx) throws SQLException {
					LlenaComboGralDto cons = new LlenaComboGralDto();
					cons.setIdStr(rs.getString("id"));   
					return cons;
				}
			});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionCargoCtaDaoImpl, M:obtenerChequeraVta");
		}
		return list;	

	}


	@Override
	public void insertarFwd(int noEmpresa, String idDivVenta, int bancoCargo, String cheCargo, String idDivCompra,
			int bancoAbono, String cheAbono, int formaPago, double importePago, double importeFwd, double tipoCambio,
			String fecAlta, String fecVencimiento, int noInstitucion, String nomContacto, int idBancoBenef,
			String idCheBenef, String rubroCarg, String subRubroCarg, String rubroAbono, String subRubroAbono,
			String estatusMov, String estatusImporte, String firmante1, String firmante2, String noDocto, double spot,
			double pntsFwd) {


		String campos=" NO_FOLIO, NO_EMPRESA,  ID_DIVISA_VENTA, ID_BANCO_CARGO, ID_CHEQUERA_CARGO, ID_DIVISA_COMPRA, ID_BANCO_ABONO, ID_CHEQUERA_ABONO, ID_FORMA_PAGO, IMPORTE_PAGO,  IMPORTE_FWS,  TIPO_CAMBIO,  FEC_ALTA,  FEC_VENC, NO_INSTITUCION, NOM_CONTACTO, ID_BANCO_BENEF,  ID_CHEQUERA_BENEF, RUBRO_CGO, SUBRUBRO_CGO,  RUBRO_ABN, SUBRUBRO_ABN, ESTATUS_MOV, ESTATUS_IMP, FIRMANTE_1, FIRMANTE_2, NO_DOCTO,   SPOT,  PUNTOS_FORWARD ";
		String idFwd;
		StringBuffer sbSql = new StringBuffer();
		StringBuffer sbSql2 = new StringBuffer();

		try{
			int folio = 0;
			try {
				sbSql2.append("  select MAX(NO_FOLIO)+1 from CONTROL_FORWARDS ");
				idFwd = this.getJdbcTemplate().queryForObject(sbSql2.toString(), String.class);
				folio=  Integer.parseInt(idFwd);
				sbSql2.delete(0, sbSql2.length());
				System.out.println(folio);
			} catch (Exception e) {
				folio = 1;
			}

			sbSql.append("Insert into CONTROL_FORWARDS");
			sbSql.append("\n (" + campos + " )");
			sbSql.append("\n values (" + folio +","+noEmpresa+","+"'"+idDivVenta+"'"+","+bancoCargo+","+"'"+cheCargo+"'"+","+"'"+idDivCompra+"'"+","+
					bancoAbono+","+"'"+cheAbono+"'"+","+formaPago+","+importePago+","+importeFwd+","+ tipoCambio+
					","+"'"+fecAlta+"'"+","+"'"+fecVencimiento+"'"+","+noInstitucion+","+"'"+nomContacto+"'"+","+idBancoBenef+
					","+"'"+idCheBenef+"'"+","+"'"+rubroCarg+"'"+","+"'"+subRubroCarg+"'"+","+"'"+rubroAbono+"'"+","+"'"+subRubroAbono+"'"+
					","+"'"+estatusMov+"'"+","+"'"+estatusImporte+"'"+","+"'"+firmante1+"'"+","+"'"+firmante2+"'"+","+"'"+noDocto+"'"+","+spot+","+pntsFwd+ ")");

			System.out.println("querys"+sbSql.toString());
			jdbcTemplate.update(sbSql.toString());

		}
		catch(Exception e){

			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionCargoCtaDaoImpl, M:obtenerChequeraVta");

		}

	}


	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public List<LlenarConsultaFws> consultarGrid(boolean foco, int tipo, String fecA, String fecV,int noEm) {
//		System.out.println(foco+"/*******************/");
//		System.out.println(tipo+"/*******************/");
		StringBuffer sbSql = new StringBuffer();
		List<LlenarConsultaFws>listDisp= new ArrayList<LlenarConsultaFws>();

		try{
			if((foco)&&(tipo==1)){

				sbSql.append("\n select * from CONTROL_FORWARDS ");
				sbSql.append("\n where  FEC_ALTA>='"+fecA+"' and FEC_ALTA<= '"+fecV+"' ");
				sbSql.append("\n order by 1");
				System.out.println("querys"+sbSql.toString());
			} else{
				if((foco)&&(tipo==2)){

					sbSql.append("\n select * from CONTROL_FORWARDS ");
					sbSql.append("\n  where FEC_VENC>='"+fecA+"' and FEC_VENC<= '"+fecV+"'   ");
					sbSql.append("\n order by 1");
					System.out.println("querys"+sbSql.toString());


				}else{
					if(tipo==1){
						sbSql.append("\n select * from CONTROL_FORWARDS where FEC_ALTA>='"+fecA+"' and FEC_ALTA<= '"+fecV+"' ");
						sbSql.append("\n and NO_EMPRESA="+noEm+" ");
						sbSql.append("\n order by 1");

						System.out.println("querys"+sbSql.toString());
					}else{
						sbSql.append("\n select * from CONTROL_FORWARDS where FEC_VENC>='"+fecA+"' and FEC_VENC<= '"+fecV+"'  ");
						sbSql.append("\n and NO_EMPRESA="+noEm+" ");
						sbSql.append("\n order by 1");


						System.out.println("querys"+sbSql.toString());
					}

				}


			}



			listDisp = jdbcTemplate.query(sbSql.toString(), new RowMapper()	{
				public LlenarConsultaFws mapRow(ResultSet rs, int idx)throws SQLException{
					LlenarConsultaFws reporte = new LlenarConsultaFws();

					//					System.out.println("******1******");
					//					System.out.println(rs.getInt("NO_FOLIO"));
					//                    System.out.println("******2******");
					//                	System.out.println(rs.getInt("NO_EMPRESA"));
					//                    System.out.println("******3******");
					//					System.out.println(rs.getString("ID_DIVISA_VENTA"));
					//					System.out.println("******4******");
					//                    System.out.println(rs.getInt("ID_BANCO_CARGO"));
					//					System.out.println("******5******");
					//                    System.out.println(rs.getString("ID_CHEQUERA_CARGO"));
					//					System.out.println("******6******");
					//                    System.out.println(rs.getString("ID_DIVISA_COMPRA"));
					//					System.out.println("******7******");
					//                    System.out.println(rs.getInt("ID_BANCO_ABONO"));
					//					System.out.println("******8******");
					//                    System.out.println(rs.getString("ID_CHEQUERA_ABONO"));
					//					System.out.println("******9******");
					//                    System.out.println(rs.getInt("ID_FORMA_PAGO"));
					//					System.out.println("******10******");
//					                    System.out.println(rs.getDouble("IMPORTE_PAGO"));
//										System.out.println("******11******double");
//					                    System.out.println(rs.getDouble("IMPORTE_FWS"));
//										System.out.println("******12******");
//					                    System.out.println(rs.getDouble("TIPO_CAMBIO"));
					//					System.out.println("******13******");
					//                    System.out.println(rs.getString("FEC_ALTA"));
					//					System.out.println("******14******");
					//                    System.out.println(rs.getString("FEC_VENC"));
					//					System.out.println("******15******");
					//                    System.out.println(rs.getInt("NO_INSTITUCION"));
					//					System.out.println("******16******");
					//                    System.out.println(rs.getString("NOM_CONTACTO"));
					//					System.out.println("******17******");
					//                    System.out.println(rs.getInt("ID_BANCO_BENEF"));
					//					System.out.println("******18******");
					//                    System.out.println(rs.getString("ID_CHEQUERA_BENEF"));
					//					System.out.println("******19******");
					//                    System.out.println(rs.getString("RUBRO_CGO"));
					//					System.out.println("******20******");
					//                    System.out.println(rs.getString("SUBRUBRO_CGO"));
					//					System.out.println("******21******");
					//                    System.out.println(rs.getString("RUBRO_ABN"));
					//					System.out.println("******22******");
					//                    System.out.println(rs.getString("SUBRUBRO_ABN"));
					//					System.out.println("******23******");
					//                    System.out.println(rs.getString("ESTATUS_MOV"));
					//					System.out.println("******24******");
					//                    System.out.println(rs.getString("ESTATUS_IMP"));
					//					System.out.println("******25******");
					//                    System.out.println(rs.getString("FIRMANTE_1"));
					//					System.out.println("******26******");
					//                    System.out.println(rs.getString("FIRMANTE_2"));
					//					System.out.println("******27******");
					//                    System.out.println(rs.getString("NO_DOCTO"));
					//					System.out.println("******28******");
					//                    System.out.println(rs.getFloat("SPOT"));
					//					System.out.println("******29******");
					//                    System.out.println(rs.getFloat("PUNTOS_FORWARD"));
					//					
					//					System.out.println("******/******");
					
					reporte.setNO_FOLIO(rs.getInt("NO_FOLIO"));
					reporte.setNO_EMPRESA(rs.getInt("NO_EMPRESA"));
					reporte.setID_DIVISA_VENTA(rs.getString("ID_DIVISA_VENTA"));
					reporte.setID_BANCO_CARGO(rs.getInt("ID_BANCO_CARGO"));
					reporte.setID_CHEQUERA_CARGO(rs.getString("ID_CHEQUERA_CARGO"));
					reporte.setID_DIVISA_COMPRA(rs.getString("ID_DIVISA_COMPRA"));
					reporte.setID_BANCO_ABONO(rs.getInt("ID_BANCO_ABONO"));
					reporte.setID_CHEQUERA_ABONO(rs.getString("ID_CHEQUERA_ABONO"));
					reporte.setID_FORMA_PAGO(rs.getInt("ID_FORMA_PAGO"));
					reporte.setIMPORTE_PAGO(rs.getDouble("IMPORTE_PAGO"));
					reporte.setIMPORTE_FWS(rs.getDouble("IMPORTE_FWS"));
					reporte.setTIPO_CAMBIO(rs.getDouble("TIPO_CAMBIO"));
					reporte.setFEC_ALTA(rs.getString("FEC_ALTA"));
					reporte.setFEC_VENC(rs.getString("FEC_VENC"));
					reporte.setNO_INSTITUCION(rs.getInt("NO_INSTITUCION"));
					reporte.setNOM_CONTACTO(rs.getString("NOM_CONTACTO"));
					reporte.setID_BANCO_BENEF(rs.getInt("ID_BANCO_BENEF"));
					reporte.setID_CHEQUERA_BENEF(rs.getString("ID_CHEQUERA_BENEF"));
					reporte.setRUBRO_CGO(rs.getString("RUBRO_CGO"));
					reporte.setSUBRUBRO_CGO(rs.getString("SUBRUBRO_CGO"));
					reporte.setRUBRO_ABN(rs.getString("RUBRO_ABN"));
					reporte.setSUBRUBRO_ABN(rs.getString("SUBRUBRO_ABN"));
					reporte.setESTATUS_MOV(rs.getString("ESTATUS_MOV"));
					reporte.setESTATUS_IMP(rs.getString("ESTATUS_IMP"));
					reporte.setFIRMANTE_1(rs.getString("FIRMANTE_1"));
					reporte.setFIRMANTE_2(rs.getString("FIRMANTE_2"));
					reporte.setNO_DOCTO(rs.getString("NO_DOCTO"));
					reporte.setSPOT(rs.getDouble("SPOT"));
					reporte.setPUNTOS_FORWARD(rs.getDouble("PUNTOS_FORWARD"));
					reporte.setREFERENCIA(rs.getString("REFERENCIA"));
					reporte.setCONCEPTO(rs.getString("CONCEPTO"));
					reporte.setESTATUS_SWAP(rs.getString("ESTATUS_SWAP"));
					reporte.setID_NETEO(rs.getString("ID_NETEO"));
					return reporte;


				} 
			});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionCargoCtaDaoImpl, M:obtenerChequeraVta");

		}

		return listDisp;


	}


	@Override
	public List<Map<String, String>> reporteFwd(String fwd) {
		@SuppressWarnings("unused")
		int Fwd=Integer.parseInt(fwd);
		List<Map<String, String>> listaResultado = new ArrayList<Map<String, String>>();
		StringBuffer sbSql = new StringBuffer();

		try{
			sbSql.append("\n select * from CONTROL_FORWARDS order by 1");


			//			System.out.println("-----------------");
			//			System.out.println("imprime query");
			//			System.out.println("-----------------");
			System.out.println(sbSql.toString());

			listaResultado = jdbcTemplate.query(sbSql.toString(), new RowMapper<Map<String, String>>(){
				public Map<String, String> mapRow(ResultSet rs, int idx)throws SQLException{
					Map<String, String> campos = new HashMap<String, String>();
					//					
					//					System.out.println("******1******");
					//					System.out.println(rs.getInt("NO_FOLIO"));
					//                    System.out.println("******2******");
					//                	System.out.println(rs.getInt("NO_EMPRESA"));
					//                    System.out.println("******3******");
					//					System.out.println(rs.getString("ID_DIVISA_VENTA"));
					//					System.out.println("******4******");
					//                    System.out.println(rs.getInt("ID_BANCO_CARGO"));
					//					System.out.println("******5******");
					//                    System.out.println(rs.getString("ID_CHEQUERA_CARGO"));
					//					System.out.println("******6******");
					//                    System.out.println(rs.getString("ID_DIVISA_COMPRA"));
					//					System.out.println("******7******");
					//                    System.out.println(rs.getInt("ID_BANCO_ABONO"));
					//					System.out.println("******8******");
					//                    System.out.println(rs.getString("ID_CHEQUERA_ABONO"));
					//					System.out.println("******9******");
					//                    System.out.println(rs.getInt("ID_FORMA_PAGO"));
					//					System.out.println("******10******");
					//                    System.out.println(rs.getFloat("IMPORTE_PAGO"));
					//					System.out.println("******11******");
					//                    System.out.println(rs.getFloat("IMPORTE_FWS"));
					//					System.out.println("******12******");
					//                    System.out.println(rs.getFloat("TIPO_CAMBIO"));
					//					System.out.println("******13******");
					//                    System.out.println(rs.getString("FEC_ALTA"));
					//					System.out.println("******14******");
					//                    System.out.println(rs.getString("FEC_VENC"));
					//					System.out.println("******15******");
					//                    System.out.println(rs.getInt("NO_INSTITUCION"));
					//					System.out.println("******16******");
					//                    System.out.println(rs.getString("NOM_CONTACTO"));
					//					System.out.println("******17******");
					//                    System.out.println(rs.getInt("ID_BANCO_BENEF"));
					//					System.out.println("******18******");
					//                    System.out.println(rs.getString("ID_CHEQUERA_BENEF"));
					//					System.out.println("******19******");
					//                    System.out.println(rs.getString("RUBRO_CGO"));
					//					System.out.println("******20******");
					//                    System.out.println(rs.getString("SUBRUBRO_CGO"));
					//					System.out.println("******21******");
					//                    System.out.println(rs.getString("RUBRO_ABN"));
					//					System.out.println("******22******");
					//                    System.out.println(rs.getString("SUBRUBRO_ABN"));
					//					System.out.println("******23******");
					//                    System.out.println(rs.getString("ESTATUS_MOV"));
					//					System.out.println("******24******");
					//                    System.out.println(rs.getString("ESTATUS_IMP"));
					//					System.out.println("******25******");
					//                    System.out.println(rs.getString("FIRMANTE_1"));
					//					System.out.println("******26******");
					//                    System.out.println(rs.getString("FIRMANTE_2"));
					//					System.out.println("******27******");
					//                    System.out.println(rs.getString("NO_DOCTO"));
					//					System.out.println("******28******");
					//                    System.out.println(rs.getFloat("SPOT"));
					//					System.out.println("******29******");
					//                    System.out.println(rs.getFloat("PUNTOS_FORWARD"));



					campos.put("NO_FOLIO",Integer.toString(rs.getInt("NO_FOLIO")));
					campos.put("NO_EMPRESA",Integer.toString(rs.getInt("NO_EMPRESA")));
					campos.put("ID_DIVISA_VENTA",rs.getString("ID_DIVISA_VENTA"));
					campos.put("ID_BANCO_CARGO",Integer.toString(rs.getInt("ID_BANCO_CARGO")));
					campos.put("ID_CHEQUERA_CARGO",rs.getString("ID_CHEQUERA_CARGO"));
					campos.put("ID_DIVISA_COMPRA",rs.getString("ID_DIVISA_COMPRA"));
					campos.put("ID_BANCO_ABONO",Integer.toString(rs.getInt("ID_BANCO_ABONO")));
					campos.put("ID_CHEQUERA_ABONO",rs.getString("ID_CHEQUERA_ABONO"));
					campos.put("ID_FORMA_PAGO",Integer.toString(rs.getInt("ID_FORMA_PAGO")));
					campos.put("IMPORTE_PAGO",Double.toString(rs.getDouble("IMPORTE_PAGO")));
					campos.put("IMPORTE_FWS",Double.toString(rs.getDouble("IMPORTE_FWS")));
					campos.put("TIPO_CAMBIO",Double.toString(rs.getDouble("TIPO_CAMBIO")));
					campos.put("FEC_ALTA",rs.getString("FEC_ALTA"));
					campos.put("FEC_VENC",rs.getString("FEC_VENC"));
					campos.put("NO_INSTITUCION",Integer.toString(rs.getInt("NO_INSTITUCION")));
					campos.put("NOM_CONTACTO",rs.getString("NOM_CONTACTO"));
					campos.put("ID_BANCO_BENEF",Integer.toString(rs.getInt("ID_BANCO_BENEF")));
					campos.put("ID_CHEQUERA_BENEF",rs.getString("ID_CHEQUERA_BENEF"));
					campos.put("RUBRO_CGO",rs.getString("RUBRO_CGO"));
					campos.put("SUBRUBRO_CGO",rs.getString("SUBRUBRO_CGO"));
					campos.put("RUBRO_ABN",rs.getString("RUBRO_ABN"));
					campos.put("SUBRUBRO_ABN",rs.getString("SUBRUBRO_ABN"));
					campos.put("ESTATUS_MOV",rs.getString("ESTATUS_MOV"));
					campos.put("ESTATUS_IMP",rs.getString("ESTATUS_IMP"));
					campos.put("FIRMANTE_1",rs.getString("FIRMANTE_1"));
					campos.put("FIRMANTE_2",rs.getString("FIRMANTE_2"));
					campos.put("NO_DOCTO",rs.getString("NO_DOCTO"));
					campos.put("SPOT",Double.toString(rs.getDouble("SPOT")));
					campos.put("PUNTOS_FORWARD",Double.toString(rs.getDouble("PUNTOS_FORWARD")));
					campos.put("REFERENCIA",rs.getString("REFERENCIA"));
					campos.put("CONCEPTO",rs.getString("CONCEPTO"));
					return campos;
				}
			});
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasDaoImpl, M: llenaGrid");
		}return listaResultado;	
	}

	@Override
	public List<Map<String, String>> reportePersonas2(String tipoPersona, String foco, String tipo, String fecA,
			String fecV) {
		List<Map<String, String>> listaResultado = new ArrayList<Map<String, String>>();

		StringBuffer sbSql = new StringBuffer();
		@SuppressWarnings("unused")
		List<LlenarConsultaFws>listDisp= new ArrayList<LlenarConsultaFws>();

		int noE=Integer.parseInt(tipoPersona);
		int tip=Integer.parseInt(tipo);

		//    System.out.println("------------y2--------------------------");
//		System.out.println(tipoPersona);
//		System.out.println(foco);
//		System.out.println(tipo);
//		System.out.println(fecA);
//		System.out.println(fecV);

		//	System.out.println("-----------y2----------------------------");

		try{
			if((foco!="false")&&(tip==1)){

				sbSql.append("\n select * from CONTROL_FORWARDS ");
				sbSql.append("\n where  FEC_ALTA>='"+fecA+"' and FEC_ALTA<= '"+fecV+"' ");
				sbSql.append("\n order by 1");
				System.out.println("querys1111"+sbSql.toString());
			} else{
				if((foco!="false")&&(tip==2)){

					sbSql.append("\n select * from CONTROL_FORWARDS ");
					sbSql.append("\n  where FEC_VENC>='"+fecA+"' and FEC_VENC<= '"+fecV+"'   ");
					sbSql.append("\n order by 1");
					System.out.println("querys2222"+sbSql.toString());


				}else{
					if(tip==1){
						sbSql.append("\n select * from CONTROL_FORWARDS where FEC_ALTA>='"+fecA+"' and FEC_ALTA<= '"+fecV+"' ");
						sbSql.append("\n and NO_EMPRESA="+noE+" ");
						sbSql.append("\n order by 1");

						System.out.println("querys3333"+sbSql.toString());
					}else{
						sbSql.append("\n select * from CONTROL_FORWARDS where FEC_VENC>='"+fecA+"' and FEC_VENC<= '"+fecV+"'  ");
						sbSql.append("\n and NO_EMPRESA="+noE+" ");
						sbSql.append("\n order by 1");


						System.out.println("querys44444"+sbSql.toString());
					}

				}


			}

			System.out.println("querys"+sbSql.toString());

			//				System.out.println("-----------------");
			//			System.out.println("imprime query");
			//			System.out.println("-----------------");

			listaResultado = jdbcTemplate.query(sbSql.toString(), new RowMapper<Map<String, String>>(){
				public Map<String, String> mapRow(ResultSet rs, int idx)throws SQLException{
					Map<String, String> campos = new HashMap<String, String>();

					campos.put("NO_FOLIO",Integer.toString(rs.getInt("NO_FOLIO")));
					campos.put("NO_EMPRESA",Integer.toString(rs.getInt("NO_EMPRESA")));
					campos.put("ID_DIVISA_VENTA",rs.getString("ID_DIVISA_VENTA"));
					campos.put("ID_BANCO_CARGO",Integer.toString(rs.getInt("ID_BANCO_CARGO")));
					campos.put("ID_CHEQUERA_CARGO",rs.getString("ID_CHEQUERA_CARGO"));
					campos.put("ID_DIVISA_COMPRA",rs.getString("ID_DIVISA_COMPRA"));
					campos.put("ID_BANCO_ABONO",Integer.toString(rs.getInt("ID_BANCO_ABONO")));
					campos.put("ID_CHEQUERA_ABONO",rs.getString("ID_CHEQUERA_ABONO"));
					campos.put("ID_FORMA_PAGO",Integer.toString(rs.getInt("ID_FORMA_PAGO")));
					campos.put("IMPORTE_PAGO",Double.toString(rs.getDouble("IMPORTE_PAGO")));
					campos.put("IMPORTE_FWS",Double.toString(rs.getDouble("IMPORTE_FWS")));
					campos.put("TIPO_CAMBIO",Double.toString(rs.getDouble("TIPO_CAMBIO")));
					campos.put("FEC_ALTA",rs.getString("FEC_ALTA"));
					campos.put("FEC_VENC",rs.getString("FEC_VENC"));
					campos.put("NO_INSTITUCION",Integer.toString(rs.getInt("NO_INSTITUCION")));
					campos.put("NOM_CONTACTO",rs.getString("NOM_CONTACTO"));
					campos.put("ID_BANCO_BENEF",Integer.toString(rs.getInt("ID_BANCO_BENEF")));
					campos.put("ID_CHEQUERA_BENEF",rs.getString("ID_CHEQUERA_BENEF"));
					campos.put("RUBRO_CGO",rs.getString("RUBRO_CGO"));
					campos.put("SUBRUBRO_CGO",rs.getString("SUBRUBRO_CGO"));
					campos.put("RUBRO_ABN",rs.getString("RUBRO_ABN"));
					campos.put("SUBRUBRO_ABN",rs.getString("SUBRUBRO_ABN"));
					campos.put("ESTATUS_MOV",rs.getString("ESTATUS_MOV"));
					campos.put("ESTATUS_IMP",rs.getString("ESTATUS_IMP"));
					campos.put("FIRMANTE_1",rs.getString("FIRMANTE_1"));
					campos.put("FIRMANTE_2",rs.getString("FIRMANTE_2"));
					campos.put("NO_DOCTO",rs.getString("NO_DOCTO"));
					campos.put("SPOT",Double.toString(rs.getDouble("SPOT")));
					campos.put("PUNTOS_FORWARD",Double.toString(rs.getDouble("PUNTOS_FORWARD")));
					campos.put("REFERENCIA",rs.getString("REFERENCIA"));
					campos.put("CONCEPTO",rs.getString("CONCEPTO"));
					return campos;
				}
			});
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasDaoImpl, M: llenaGrid");
		}return listaResultado;
	}

	@Override
	public int obtenerCaja(int usuario) {
		int id_caja  = 0;
		try{
			StringBuffer sb = new StringBuffer();
			sb = new StringBuffer();
			sb.append("SELECT id_caja from SEG_USUARIO where id_usuario = '" + usuario + "'");
			System.out.println(sb.toString());
			id_caja = this.getJdbcTemplate().queryForInt(sb.toString());
		}catch(Exception e){
			System.out.println(e);
			logger.error(e.toString());
		}
		return id_caja;
	}
	
	@Override
	public Map<String, Object> ejecutarGenerador(GeneradorDto generadorDto)
	{
		System.out.println(" si entre al generador"+generadorDto);
		consultasGenerales = new ConsultasGenerales(jdbcTemplate);
		return consultasGenerales.ejecutarGenerador(generadorDto);

	}

	@Override
	public String consultaBanco(int id_banco) {		
		String banco  = "";
		try{
			StringBuffer sb = new StringBuffer();
			sb = new StringBuffer();
			sb.append("select desc_banco from cat_banco where id_banco = '" + id_banco + "'");
			System.out.println(sb.toString());
			banco = this.getJdbcTemplate().queryForObject(sb.toString(), String.class);
			
		}catch(Exception e){
			System.out.println(e);
			logger.error(e.toString());
		}
		return banco;
	}

	@Override
	public void eliminarForward(int folio, String unidad_negocio, String chequera_cargo, String chequera_abono,
			Double importe_pago, Double importe_compra, Double tc, String fec_compra, String fec_vto) {


		StringBuffer sbSql = new StringBuffer();
		try{

			sbSql.append("delete from CONTROL_FORWARDS WHERE \n");
			sbSql.append("NO_FOLIO = " + folio + "\n");
			sbSql.append(" and NO_EMPRESA = " + unidad_negocio + "\n");
			sbSql.append(" and ID_CHEQUERA_CARGO = " + chequera_cargo + "\n");
			sbSql.append(" and ID_CHEQUERA_ABONO = " + chequera_abono + "\n");
			sbSql.append(" and IMPORTE_PAGO = " + importe_pago + "\n");
			sbSql.append(" and IMPORTE_FWS = " + importe_compra + "\n");
			sbSql.append(" and TIPO_CAMBIO = " + tc + "\n");
			sbSql.append(" and FEC_ALTA = '" + fec_compra + "'\n");
			sbSql.append(" and FEC_VENC = '" + fec_vto + "'");

			System.out.println("query: "+sbSql.toString());
			jdbcTemplate.update(sbSql.toString());	
		}
		catch(Exception e){
			logger.error(e.toString());
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Derivados, C:ForwardsDaoImpl, M:eliminarForward");
		}

	}

	@Override
	public void modificaForward(int folio, String unidad_negocio, String chequera_cargo, String chequera_abono,
			Double spot, Double pts_forward, Double importe_pago, Double importe_compra, Double tc, String fec_compra,
			String fec_vto) {


		StringBuffer sbSql = new StringBuffer();
		try{

			sbSql.append("UPDATE CONTROL_FORWARDS  \n");
			sbSql.append(" SET SPOT = " + spot + ",\n");
			sbSql.append(" PUNTOS_FORWARD = " + pts_forward + ",\n");
			sbSql.append(" TIPO_CAMBIO = " + tc + ",\n");
			sbSql.append(" IMPORTE_FWS = " + importe_compra + ",\n");
			sbSql.append(" IMPORTE_PAGO = " + importe_pago + "\n");
			sbSql.append(" WHERE NO_FOLIO = " + folio + "\n");
			sbSql.append(" and NO_EMPRESA = " + unidad_negocio + "\n");
			sbSql.append(" and ID_CHEQUERA_CARGO = " + chequera_cargo + "\n");
			sbSql.append(" and ID_CHEQUERA_ABONO = " + chequera_abono + "\n");
			sbSql.append(" and FEC_ALTA = '" + fec_compra + "'\n");
			sbSql.append(" and FEC_VENC = '" + fec_vto + "'");

			System.out.println("query: "+sbSql.toString());
			jdbcTemplate.update(sbSql.toString());	
		}
		catch(Exception e){
			logger.error(e.toString());
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Derivados, C:ForwardsDaoImpl, M:modificaForward");
		}

	}

	@Override
	public void swapForward(int folio, String unidad_negocio, String chequera_cargo, String chequera_abono, Double spot,
			Double puntos_forward, Double importe_pago, Double importe_compra, String fec_compra,
			String fec_vto) {


		StringBuffer sbSql = new StringBuffer();
		try{

			sbSql.append("UPDATE CONTROL_FORWARDS  \n");
			sbSql.append(" SET ESTATUS_SWAP = 'S'\n");
			sbSql.append(" WHERE NO_FOLIO = " + folio + "\n");
			sbSql.append(" and NO_EMPRESA = " + unidad_negocio + "\n");
			sbSql.append(" and ID_CHEQUERA_CARGO = " + chequera_cargo + "\n");
			sbSql.append(" and ID_CHEQUERA_ABONO = " + chequera_abono + "\n");
			sbSql.append(" and FEC_ALTA = '" + fec_compra + "'\n");
			sbSql.append(" and FEC_VENC = '" + fec_vto + "'");

			System.out.println("query: "+sbSql.toString());
			jdbcTemplate.update(sbSql.toString());	
		}
		catch(Exception e){
			logger.error(e.toString());
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Derivados, C:ForwardsDaoImpl, M:swapForward");
		}

	}

}

