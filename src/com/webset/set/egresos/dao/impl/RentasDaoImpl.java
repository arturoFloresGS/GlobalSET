package com.webset.set.egresos.dao.impl;
/* Autor: Luis Alfredo Serrato Montes de Oca
 * 22102015
 */

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.webset.set.egresos.dao.RentasDao;
import com.webset.set.egresos.dto.DatosExcelDto;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.Funciones;
import com.webset.utils.tools.Utilerias;

public class RentasDaoImpl implements RentasDao {
	private JdbcTemplate jdbcTemplate;
	Bitacora bitacora = new Bitacora();
	
	
	
	@Override
	public List<DatosExcelDto> validarDatosExcel(String datos) {
		Gson gson = new Gson();
		List<DatosExcelDto> datosExcel = new ArrayList<DatosExcelDto>();
		List<Map<String, String>> renglon = gson.fromJson(datos, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yy");
		Funciones fn = new Funciones();
		for(int i = 0; i < renglon.size(); i++){
			DatosExcelDto datosExcelDto = new DatosExcelDto();
			datosExcelDto.setCcid(Integer.parseInt(renglon.get(i).get("ccid")));
			try{
				datosExcelDto.setSociedad(Integer.parseInt(renglon.get(i).get("sociedad")));
			}catch(Exception e){
				datosExcelDto.setSociedad(0);
			}
			
			try{
				datosExcelDto.setNumeroAcredor(Integer.parseInt(renglon.get(i).get("numeroAcredor")));
			}catch(Exception e){
				datosExcelDto.setNumeroAcredor(0);
			}
			datosExcelDto.setNombreAcredor(renglon.get(i).get("nombreAcredor"));
			datosExcelDto.setDocSap(renglon.get(i).get("docSap"));
			datosExcelDto.setViaPago(renglon.get(i).get("viaPago"));
			datosExcelDto.setBanco(renglon.get(i).get("banco"));
			datosExcelDto.setCuenta(renglon.get(i).get("cuenta"));
			datosExcelDto.setMoneda(renglon.get(i).get("moneda"));
			datosExcelDto.setReferencia(renglon.get(i).get("referencia"));
			
			try{
				Date fecha = format.parse(renglon.get(i).get("fechaContabilizacion"));
				datosExcelDto.setFechaContabilizacion(fn.ponerFechaSola(fecha));
			}catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
				+"P:Egresos, C:RentasDaoImpl, M:validarDatosExcel");
			}
			
			try{
				String total =  renglon.get(i).get("total").replaceAll("\\$", "");
				datosExcelDto.setTotal(Double.parseDouble(total.replaceAll("\\,", "")));
			}catch(Exception e){
				datosExcelDto.setTotal(0.0);
			}
			datosExcelDto.setObservacion("");
			datosExcelDto.setNoFolioDet("");
			datosExcelDto.setBandera(" - ");
			datosExcel.add(cosultasPorCampoMov(datosExcelDto));
			
		}
		
		return datosExcel;
	}
	

	private DatosExcelDto cosultasPorCampoMov(DatosExcelDto datosExcel){
		StringBuffer sb = new StringBuffer();
		
		try {
			sb.append("SELECT COUNT(*) FROM CAT_TIENDA WHERE CCID = " + datosExcel.getCcid());
			System.out.println(sb.toString());

			if(jdbcTemplate.queryForInt(sb.toString()) <= 0) {
				datosExcel.setObservacion("La tienda no esta registrada");
				return datosExcel;
			}

			sb = new StringBuffer();

			sb.append("SELECT COUNT(*) FROM MOVIMIENTO M ");
			sb.append("WHERE NO_EMPRESA = " + datosExcel.getSociedad() + " \n");
			sb.append(" AND ID_TIPO_OPERACION= 3000");
			
			System.out.println(sb.toString());
			
			if(jdbcTemplate.queryForInt(sb.toString()) <= 0) {
				datosExcel.setObservacion("No coincide sociedad");
				return datosExcel;
			}
			
			sb.append("AND (NO_CLIENTE in (SELECT NO_PERSONA FROM PERSONA P ");
			sb.append("WHERE M.NO_CLIENTE = P.NO_PERSONA AND EQUIVALE_PERSONA = '");
			sb.append(datosExcel.getNumeroAcredor() +"' )) ");				
			System.out.println(sb.toString());
			
			if(jdbcTemplate.queryForInt(sb.toString()) <= 0) {
				datosExcel.setObservacion("No coincide numero acredor");
				return datosExcel;
			}

			sb.append(" AND NO_DOCTO = '"+ datosExcel.getDocSap() +"' \n");
			System.out.println(sb.toString());

			if(jdbcTemplate.queryForInt(sb.toString()) <= 0) {
				datosExcel.setObservacion("No coincide numero de documento");
				return datosExcel;
			}

			sb.append(" AND ID_DIVISA = '"+ datosExcel.getMoneda() +"' \n");
			System.out.println(sb.toString());

			if(jdbcTemplate.queryForInt(sb.toString()) <= 0) {
				datosExcel.setObservacion("No coincide divisa");
				return datosExcel;
			}

			sb.append(" AND IMPORTE between ("+datosExcel.getTotal()+"-0.99) ");
			sb.append("and ("+datosExcel.getTotal()+"+0.99)");
			System.out.println(sb.toString());

			if(jdbcTemplate.queryForInt(sb.toString()) <= 0) {
				datosExcel.setObservacion("No coincide importe");
				return datosExcel;
			}

			if (datosExcel.getFechaContabilizacion() != null && 
				!datosExcel.getFechaContabilizacion().equals("null") )
				sb.append(" AND FECHA_CONTABILIZACION = CONVERT(DATETIME'"+ 
					datosExcel.getFechaContabilizacion() +"', 103) \n");
			else {
				datosExcel.setObservacion("Falta la fecha de contabilizacion");
				return datosExcel;
			}

			if(jdbcTemplate.queryForInt(sb.toString()) <= 0) {
				datosExcel.setObservacion("No coincide la fecha de contabilizacion");
				return datosExcel;
			}

			sb.append(" AND CVE_CONTROL IS NULL OR CVE_CONTROL = '' \n");
			System.out.println(sb.toString());

			if(jdbcTemplate.queryForInt(sb.toString()) <= 0) {
				datosExcel.setObservacion("El registro ya existe en alguna propuesta");
				return datosExcel;
			}

			sb.append(" AND ID_FORMA_PAGO = '"+ datosExcel.getViaPago() +"' \n");
			System.out.println(sb.toString());

			//En caso de que coincida la forma de pago
			if(jdbcTemplate.queryForInt(sb.toString()) > 0) {
				//Cheque o cheque de caja
				if(datosExcel.getViaPago().equals("1") || 
					datosExcel.getViaPago().equals("10")) {
					sb.append(" AND BENEFICIARIO = '"+ datosExcel.getNombreAcredor() +"' \n" );

					System.out.println(sb.toString());

					if(jdbcTemplate.queryForInt(sb.toString()) <= 0) {
						datosExcel.setObservacion("No coincide nombre acredor");
						return datosExcel;
					}

					sb = new StringBuffer();
					sb.append(" SELECT COUNT(*) FROM MOVIMIENTO M \n");
					sb.append(" WHERE NO_EMPRESA = "+ datosExcel.getSociedad() +"  \n");
					sb.append(" AND NO_CLIENTE = (SELECT NO_PERSONA FROM PERSONA P WHERE M.NO_CLIENTE = P.NO_PERSONA AND EQUIVALE_PERSONA = '" + datosExcel.getNumeroAcredor() + "') \n");
					sb.append(" AND BENEFICIARIO = '"+ datosExcel.getNombreAcredor() +"' \n");
					sb.append(" AND ID_TIPO_OPERACION IN ('3000', '3200') \n");
					sb.append(" AND ID_ESTATUS_MOV IN ('N', 'C', 'I', 'R') \n");
					sb.append(" AND ID_FORMA_PAGO = "+ datosExcel.getViaPago() + "\n");
					sb.append(" AND SUCURSAL = '"+datosExcel.getCcid()+"' \n");
				
					System.out.println(sb.toString());

					if(jdbcTemplate.queryForInt(sb.toString()) >= 3){
						datosExcel.setObservacion("Tiene mas de 3 cheques");
						return datosExcel;
					}
				} else if (datosExcel.getViaPago().equals("3")) {
				//Transferencia
					sb.append("AND ID_BANCO_BENEF = '"+ datosExcel.getBanco() +"' \n");
					System.out.println(sb.toString());

					boolean coincidenDM = true;
					
					StringBuffer sbTDM = new StringBuffer();

					if(jdbcTemplate.queryForInt(sb.toString()) <= 0) {
						coincidenDM = false;

						sbTDM = new StringBuffer();

						sbTDM.append("SELECT COUNT(*) FROM CTAS_BANCO");
						sbTDM.append("\n WHERE NO_PERSONA IN (");
						sbTDM.append("SELECT NO_PERSONA FROM PERSONA P WHERE EQUIVALE_PERSONA = '"); 
						sbTDM.append(datosExcel.getNumeroAcredor()+"' )");
						sbTDM.append("\n AND ID_BANCO = '"+datosExcel.getBanco()+"'");

						if(datosExcel.getBanco().equals("127") && 
							datosExcel.getMoneda().equals("MN"))
							sbTDM.append("\n AND ID_CHEQUERA= '"+datosExcel.getCuenta()+"'");
						else
							sbTDM.append("\n AND ID_CLABE= '"+datosExcel.getCuenta()+"'");

						System.out.println(sbTDM.toString());

						if(jdbcTemplate.queryForInt(sbTDM.toString()) <= 0) {
							datosExcel.setObservacion("Datos bancarios incorrectos");
							return datosExcel;
						}
						
						datosExcel.setBandera("B-C- ");
					}

					if (coincidenDM) {
						sbTDM = new StringBuffer();

						sbTDM.append("SELECT COUNT(*) FROM CTAS_BANCO");
						sbTDM.append("\n WHERE NO_PERSONA IN (");
						sbTDM.append("SELECT NO_PERSONA FROM PERSONA P WHERE EQUIVALE_PERSONA = '"); 
						sbTDM.append(datosExcel.getNumeroAcredor()+"' )");
						sbTDM.append("\n AND ID_BANCO = '"+datosExcel.getBanco()+"'");

						if(datosExcel.getBanco().equals("127") && 
							datosExcel.getMoneda().equals("MN"))
							sbTDM.append("\n AND ID_CHEQUERA= '"+datosExcel.getCuenta()+"'");
						else
							sbTDM.append("\n AND ID_CLABE= '"+datosExcel.getCuenta()+"'");

						System.out.println(sbTDM.toString());
						
						if(datosExcel.getBanco().equals("127") && 
							datosExcel.getMoneda().equals("MN"))
							sbTDM.append("\n AND ID_CHEQUERA= '"+datosExcel.getCuenta()+"'");
						else
							sbTDM.append("\n AND ID_CLABE= '"+datosExcel.getCuenta()+"'");

						System.out.println(sbTDM.toString());
						if(jdbcTemplate.queryForInt(sbTDM.toString()) <= 0) {
							datosExcel.setObservacion("No coincide cuenta");
							return datosExcel;
						}
					}

					

					StringBuffer sbFolio = new StringBuffer();
					sbFolio.append("SELECT NO_FOLIO_dET   \n");
					sb.delete(0, 16);
					sbFolio.append(sb.toString());

					System.out.println(sbFolio.toString());
					
					String noFoliodet= String.valueOf(
						jdbcTemplate.queryForLong(sbFolio.toString()));

					System.out.println(noFoliodet);
					
					datosExcel.setNoFolioDet(noFoliodet);
					
					sb.delete(0, sb.length());

					sb.append("select count(*) from bitacora_pago_rechazado \n");
					sb.append("WHERE estatus= 'R' \n");
					sb.append("AND NO_FOLIO_DET = "+noFoliodet+" \n");

					if(jdbcTemplate.queryForInt(sb.toString()) > 0){
						datosExcel.setObservacion("Proveedor con incidencias en la banca.");
						return datosExcel;
					}
				}
			} else {
			//En caso de que no coincida la forma de pago
				//Cheque o cheque de caja
				if(datosExcel.getViaPago().equals("1") || 
					datosExcel.getViaPago().equals("10")) {
					sb.append(" AND BENEFICIARIO = '"+ datosExcel.getNombreAcredor() +"' \n" );

					System.out.println(sb.toString());

					if(jdbcTemplate.queryForInt(sb.toString()) <= 0) {
						datosExcel.setObservacion("No coincide nombre acredor");
						return datosExcel;
					}

					sb = new StringBuffer();
					sb.append(" SELECT COUNT(*) FROM MOVIMIENTO M \n");
					sb.append(" WHERE NO_EMPRESA = "+ datosExcel.getSociedad() +"  \n");
					sb.append(" AND NO_CLIENTE = (SELECT NO_PERSONA FROM PERSONA P WHERE M.NO_CLIENTE = P.NO_PERSONA AND EQUIVALE_PERSONA = '" + datosExcel.getNumeroAcredor() + "') \n");
					sb.append(" AND BENEFICIARIO = '"+ datosExcel.getNombreAcredor() +"' \n");
					sb.append(" AND ID_TIPO_OPERACION IN ('3000', '3200') \n");
					sb.append(" AND ID_ESTATUS_MOV IN ('N', 'C', 'I', 'R') \n");
					sb.append(" AND ID_FORMA_PAGO = "+ datosExcel.getViaPago() + "\n");
					sb.append(" AND SUCURSAL = '"+datosExcel.getCcid()+"' \n");
				
					System.out.println(sb.toString());

					if(jdbcTemplate.queryForInt(sb.toString()) >= 3){
						datosExcel.setObservacion("Tiene mas de 3 cheques");
						return datosExcel;
					}
					
					datosExcel.setBandera(datosExcel.getBandera()+datosExcel.getViaPago());
				} else if (datosExcel.getViaPago().equals("3")) {
				//Transferencia

					sb = new StringBuffer();

					sb.append("SELECT COUNT(*) FROM CTAS_BANCO");
					sb.append("\n WHERE NO_PERSONA IN (");
					sb.append("SELECT NO_PERSONA FROM PERSONA P WHERE EQUIVALE_PERSONA = '"); 
					sb.append(datosExcel.getNumeroAcredor()+"' )");
					sb.append("\n AND ID_BANCO = '"+datosExcel.getBanco()+"'");

					if(datosExcel.getBanco().equals("127") && 
						datosExcel.getMoneda().equals("MN"))
						sb.append("\n AND ID_CHEQUERA= '"+datosExcel.getCuenta()+"'");
					else
						sb.append("\n AND ID_CLABE= '"+datosExcel.getCuenta()+"'");

					System.out.println(sb.toString());

					if(jdbcTemplate.queryForInt(sb.toString()) <= 0) {
						datosExcel.setObservacion("Datos bancarios incorrectos");
						return datosExcel;
					}

					StringBuffer sbFolio = new StringBuffer();
					sbFolio.append("SELECT NO_FOLIO_dET   \n");
					sb.delete(0, 16);
					sbFolio.append(sb.toString());

					System.out.println(sbFolio.toString());
					
					String noFoliodet= String.valueOf(
						jdbcTemplate.queryForLong(sbFolio.toString()));

					System.out.println(noFoliodet);
					
					datosExcel.setNoFolioDet(noFoliodet);
					
					sb.delete(0, sb.length());

					sb.append("select count(*) from bitacora_pago_rechazado \n");
					sb.append("WHERE estatus= 'R' \n");
					sb.append("AND NO_FOLIO_DET = "+noFoliodet+" \n");

					if(jdbcTemplate.queryForInt(sb.toString()) > 0){
						datosExcel.setObservacion("Proveedor con incidencias en la banca.");
						return datosExcel;
					}
					
					datosExcel.setBandera("B-C- " + datosExcel.getViaPago());
				}
			}			
		}catch(Exception e){
			datosExcel.setObservacion("Error de formato");
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:RentasDaoImpl, M:cosultasPorCampoMov");
		}
		
		return datosExcel;
	}
	
	public int obtenerFolioCupos(int claveUsuario){
		StringBuffer sb = new StringBuffer();
		int folio = 0;
		
		try {
			sb.append(" SELECT FOLIO_CONTROL FROM CAT_USUARIO \n");
			sb.append(" WHERE NO_USUARIO = "  + claveUsuario + " \n");
			folio = jdbcTemplate.queryForInt(sb.toString());
			actualizarFolioCupos(claveUsuario);
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:PagosPedientesDaoImpl, M:obtenerFolioCupos");
		}
		
		if(folio < 0){
			folio = 1;
		}
		
		return folio;
	}
	
	public int actualizarFolioCupos(int claveUsuario){
		StringBuffer sb = new StringBuffer();
		int res = -1;
		
		try {
			sb.append(" UPDATE cat_usuario \n");
			sb.append(" SET folio_control = folio_control + 1 \n");
			sb.append(" WHERE no_usuario = " + Utilerias.validarCadenaSQL(claveUsuario));
			
			res = jdbcTemplate.update(sb.toString());
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:PagosPedientesDaoImpl, M:actualizarFolioCupos");
		}
		
		
		return res;
		
	}
	
	@Override
	public int insertarPropuestaPago(String claveControl, int sociedad, double importeSeleccionado, Date fecha, String concepto) {
		
		StringBuffer sb = new StringBuffer();
		SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
		int res = -1;
		try {
			sb.append(" INSERT into seleccion_automatica_grupo ( id_grupo, fecha_propuesta, \n");
			sb.append(" id_grupo_flujo, monto_maximo, cupo_manual, cupo_automatico,  \n");
			sb.append(" cupo_total, fec_limite_selecc, cve_control, id_division, concepto)  \n");
			sb.append(" VALUES( " + 0 + ", '");
			sb.append(formato.format(fecha) + "', ");
			sb.append(Utilerias.validarCadenaSQL(sociedad) + ", ");
			sb.append(Utilerias.validarCadenaSQL(importeSeleccionado) + ", ");
			sb.append(0 + ", ");
			sb.append(Utilerias.validarCadenaSQL(importeSeleccionado) + ", ");
			sb.append(Utilerias.validarCadenaSQL(importeSeleccionado) + ", '");
			sb.append(formato.format(fecha) + "', '");
			sb.append(Utilerias.validarCadenaSQL(claveControl) + "', null, '");
			sb.append(Utilerias.validarCadenaSQL(concepto) + "' )");
		
			res = jdbcTemplate.update(sb.toString());
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:RentasDaoImpl, M:insertarPropuestaPago");
		}
		
		return res;
		
	}
	
	public int actualizaMvimiento(Date fecha, String claveControl, int sociedad, int numeroAcredor, String nombreAcredor, String docSap, String viaPago, String banco, 
			String cuenta, String moneda, double total, String ccid , String noFolioDet,String referencia, String bandera){
		
		StringBuffer sb = new StringBuffer();
		int res = -1;
		SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
		String arBandera[]= bandera.split("-");
		String observacion="";
		String auxiliar ="";
		try {
			
			sb.append(" UPDATE movimiento m \n");
			sb.append(" SET fec_propuesta = '" + formato.format(fecha) + "' ,\n");
			sb.append(" cve_control = '" + Utilerias.validarCadenaSQL(claveControl) + "' \n");
			sb.append(", SUCURSAL = '" + ccid +"'  \n");
			
			if(!arBandera[0].trim().equals("")){
				sb.append( ",id_banco_benef  = '"+ Utilerias.validarCadenaSQL(banco) +"' \n");
				observacion+="Se actualizo banco";
			}
			
			if(!arBandera[1].trim().equals("")){
				if(viaPago.equals("3") && banco.equals("127") && moneda.equals("MN"))	
				   sb.append(" ,ID_CHEQUERA_BENEF = '"+ Utilerias.validarCadenaSQL(cuenta) +"' \n");
				else
					sb.append( ", ID_CHEQUERA_BENEF = (SELECT ID_CHEQUERA FROM CTAS_BANCO C WHERE ID_CLABE = '"+ cuenta +"' AND ID_BANCO = "+banco+" AND M.NO_CLIENTE = C.NO_PERSONA  AND NO_PERSONA = (SELECT NO_PERSONA FROM PERSONA P WHERE M.NO_CLIENTE = P.NO_PERSONA  AND EQUIVALE_PERSONA = '"+ numeroAcredor +"')) \n");
				
				observacion+="/ Se actualizo chequera";
			}
			
			if(arBandera.length==3 && !arBandera[2].trim().equals("")){
					sb.append(" ,ID_FORMA_PAGO = '"+ Utilerias.validarCadenaSQL(viaPago) +"' \n");
					observacion+="/ Se actualizo forma de pago";
			}else
					auxiliar +=" AND ID_FORMA_PAGO = '"+ Utilerias.validarCadenaSQL(viaPago) +"' \n";
			
			if(!referencia.trim().equals("")){
				sb.append(" ,referencia = '"+ Utilerias.validarCadenaSQL(referencia) +"' \n");
				observacion+="/Se actualizo referencia";
			}
			
			if(!observacion.equals(""))
				sb.append(" ,observacion = observacion || '/"+ observacion +"' \n");
				
			//sb.append(" SUCURSAL = '"+ Utilerias.validarCadenaSQL(ccid) +"' \n");
			sb.append(" WHERE NO_EMPRESA = " + sociedad + " \n");
			sb.append(" AND NO_FOLIO_DET = "+ noFolioDet);
			sb.append(" AND ID_TIPO_OPERACION= 3000");
			//sb.append(" AND (NO_CLIENTE = (SELECT NO_PERSONA FROM PERSONA WHERE EQUIVALE_PERSONA = '" + Utilerias.validarCadenaSQL(numeroAcredor) + "' ))  \n");
			//sb.append(" AND BENEFICIARIO = '"+ Utilerias.validarCadenaSQL(nombreAcredor) +"' \n");
			sb.append(" AND NO_DOCTO = '"+ Utilerias.validarCadenaSQL(docSap) +"' \n");
			sb.append(auxiliar);
			if(arBandera[0].equals(""))
				sb.append(" AND ID_BANCO_BENEF = '"+ Utilerias.validarCadenaSQL(banco) +"' \n");
			/*if(banco.equals("127")){
				sb.append(" AND ID_CHEQUERA_BENEF = '"+ Utilerias.validarCadenaSQL(cuenta) +"' \n");
			}else{
				sb.append(" AND ID_CHEQUERA_BENEF = (SELECT ID_CHEQUERA FROM CTAS_BANCO WHERE ID_CLABE = '"+ cuenta +"' AND NO_PERSONA = "+ Utilerias.validarCadenaSQL(numeroAcredor) +") \n");
			}*/
			
			sb.append(" AND ID_DIVISA = '"+ Utilerias.validarCadenaSQL(moneda) +"' \n");
			sb.append(" AND IMPORTE between ("+Utilerias.validarCadenaSQL(total)+"-0.99) and ("+Utilerias.validarCadenaSQL(total)+"+0.99)");
			
			System.out.println(sb.toString());
			
			res = jdbcTemplate.update(sb.toString());
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:RentasDaoImpl, M:actualizaMvimiento");
		}
		
		
		return res;

	}
	
	@Override
	public int obtenerGrupoFlujo(int sociedad) {
		int idGrupoFlujo = 0;
		StringBuffer sb = new StringBuffer();
		
		try{
			sb.append("SELECT ID_GRUPO_FLUJO FROM GRUPO_EMPRESA WHERE NO_EMPRESA = " + Utilerias.validarCadenaSQL(sociedad));
			idGrupoFlujo = jdbcTemplate.queryForInt(sb.toString());
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:RentasDaoImpl, M:obtenerGrupoFlujo");
		}
		
		return idGrupoFlujo;
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
