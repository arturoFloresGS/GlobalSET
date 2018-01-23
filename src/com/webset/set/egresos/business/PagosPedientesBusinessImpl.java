package com.webset.set.egresos.business;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.webset.set.egresos.dao.PagosPedientesDao;
import com.webset.set.egresos.dto.PagosPendientesDto;
import com.webset.set.egresos.dto.PlantillaDto;
import com.webset.set.egresos.service.PagosPedientesService;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utilerias.GlobalSingleton;
import com.webset.set.utilerias.dto.GrupoEmpresasDto;
import com.webset.set.utilerias.dto.LlenaComboDivisaDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;

/*
 * Clase Creada por Luis Alfredo Serrato Montes de Oca
 * 11092015
 */


public class PagosPedientesBusinessImpl implements PagosPedientesService {
	
	private PagosPedientesDao pagosPedientesDao; 
	Bitacora bitacora = new Bitacora();
	GlobalSingleton gb = GlobalSingleton.getInstancia();
	SimpleDateFormat formato = new SimpleDateFormat("ddMMyyyy");
	@Override
	public List<PagosPendientesDto> llamarObtenerPagosPendientes(PlantillaDto plantilla, List<Map<String, String>> rangosDocumentos ,List<Map<String, String>> rangosProveedores, boolean proveedores, boolean documentos) {
		List<PagosPendientesDto> pagosPendientes  = new ArrayList<PagosPendientesDto>();
		try{
			if(!plantilla.getNumDocA().equals("")&&!plantilla.getNumDocB().equals("")){
				Map<String, String> map = new HashMap<>();
				map.put("de", plantilla.getNumDocA());
				map.put("a", plantilla.getNumDocB());
				rangosDocumentos.add(map);
			}
			
			if(!plantilla.getEquivalePersonaA().equals("")&&!plantilla.getEquivalePersonaB().equals("")){
				Map<String, String> map = new HashMap<>();
				map.put("de", plantilla.getEquivalePersonaA());
				map.put("a", plantilla.getEquivalePersonaB());
				rangosProveedores.add(map);
			}
			pagosPendientes = pagosPedientesDao.obtenerPagosPendientes(plantilla,rangosDocumentos, rangosProveedores, proveedores, documentos);
			if(pagosPendientes.size() == 0 ){
				bitacora.insertarRegistro(new Date().toString() + " " +  "PagosPnedientesDaoImpl retorno una lista vacia"
						+ "P:Egresos, C:PagosPedientesBusinessImpl, M:llamarConsultaPagosPendientes");
			}
		}catch(Exception e){
			e.getStackTrace();
		}
		return pagosPendientes;
	}

	
	@Override
	public List<PagosPendientesDto> llamarObtenerPagosPendientesNivDos(int numeroEmpresa, PlantillaDto plantilla, List<Map<String, String>> rangosDocumentos ,List<Map<String, String>> rangosProveedores, boolean proveedores, boolean documentos) {
		
		if(!plantilla.getNumDocA().equals("")&&!plantilla.getNumDocB().equals("")){
			Map<String, String> map = new HashMap<>();
			map.put("de", plantilla.getNumDocA());
			map.put("a", plantilla.getNumDocB());
			rangosDocumentos.add(map);
		}
		
		if(!plantilla.getEquivalePersonaA().equals("")&&!plantilla.getEquivalePersonaB().equals("")){
			Map<String, String> map = new HashMap<>();
			map.put("de", plantilla.getEquivalePersonaA());
			map.put("a", plantilla.getEquivalePersonaB());
			rangosProveedores.add(map);
		}
		
		List<PagosPendientesDto> pagosPendientes = pagosPedientesDao.obtenerPagosPendientesNivDos(numeroEmpresa, plantilla, rangosDocumentos , rangosProveedores, proveedores, documentos);
	//	System.out.println("tamaño de pagos business "+pagosPendientes.size());
		if(pagosPendientes.size() == 0 ){
			bitacora.insertarRegistro(new Date().toString() + " " +  "PagosPnedientesDaoImpl retorno una lista vacia"
					+ "P:Egresos, C:PagosPedientesBusinessImpl, M:llamarConsultaPagosPendientesNivDos");
		}
		return pagosPendientes;
	}
	
	
	@Override
	public List<PagosPendientesDto> llamarObtenerPagosPendientesNivTres(int numeroEmpresa, String acreedor, PlantillaDto plantilla, List<Map<String, String>> rangosDocumentos ,List<Map<String, String>> rangosProveedores, boolean documentos, String beneficiario) {
		if(!plantilla.getNumDocA().equals("")&&!plantilla.getNumDocB().equals("")){
			Map<String, String> map = new HashMap<>();
			map.put("de", plantilla.getNumDocA());
			map.put("a", plantilla.getNumDocB());
			rangosDocumentos.add(map);
		}
		List<PagosPendientesDto> pagosPendientes = pagosPedientesDao.obtenerPagosPendientesNivTres(numeroEmpresa, acreedor, plantilla, rangosDocumentos, rangosProveedores, documentos, beneficiario);
		if(pagosPendientes.size() == 0 ){
			bitacora.insertarRegistro(new Date().toString() + " " +  "PagosPnedientesDaoImpl retorno una lista vacia"
					+ "P:Egresos, C:PagosPedientesBusinessImpl, M:llamarObtenerPagosPendientesNivTres");
		}
		return pagosPendientes;
	}
	
	@Override
	public String crearPropuestaPago(String matrizDatosPagos, int sociedad, double importeSeleccionado, String fechaPago, String divisa, String tipoBusqueda) {
		String claveControl = "";
		String concepto;
		String mensaje = "";
		SimpleDateFormat formatoDelTexto = new SimpleDateFormat("dd/MM/yyyy");
		
		try{
			System.out.println("entro crear propuesta pago");
			Date fechaDate = formatoDelTexto.parse(fechaPago);
			int idGrupoFlujo = pagosPedientesDao.obtenerGrupoFlujo(sociedad);
			
			//matrizDatosPagos = matrizDatosPagos.replace("}],[{", "},{");
			matrizDatosPagos = matrizDatosPagos.replace("[[{", "[{");
			matrizDatosPagos = matrizDatosPagos.replace("}]]", "}]");
			
//			String divisa = "MN";
//			
//			if (matrizDatosPagos.indexOf("DLS") > 0) {
//				divisa = "DLS";
//			}
			
			concepto = "M" + "-" + "MANUAL" + "-" + gb.getUsuarioLoginDto().getClaveUsuario() + "-" + idGrupoFlujo + "-" + formato.format(fechaDate) + "-" + divisa;
			
			claveControl = pagosPedientesDao.existeConcepto(concepto);
			if(!claveControl.equals("")){
				if(!pagosPedientesDao.estaDisponible(concepto, claveControl, fechaPago)){
					System.out.println("crearNueva");
					return "crearNueva";
				}
			}else{
				int i= pagosPedientesDao.obtenerFolioCupos(gb.getUsuarioLoginDto().getIdUsuario());
				do{
					claveControl = "M" + i;
					claveControl = claveControl + formato.format(gb.getFechaHoy());
					claveControl = claveControl + gb.getUsuarioLoginDto().getIdUsuario();
					i++;
				}while (pagosPedientesDao.claveControlValida(claveControl)>0);
				pagosPedientesDao.insertarPropuestaPago(claveControl, idGrupoFlujo, 
						fechaPago, concepto);
			}
				

			mensaje = "MN: " + claveControl + " ";
			
			actualizarMovimientos(matrizDatosPagos, claveControl, fechaPago, tipoBusqueda);			
			
			pagosPedientesDao.actualizaImporte(claveControl);
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " +  "PagosPnedientesDaoImpl retorno una lista vacia"
					+ "P:Egresos, C:PagosPedientesBusinessImpl, M:crearPropuestaPago");
		}		

	return mensaje;
	}
	
	private void actualizarMovimientos(String matrizDatosPagos, String claveControl, String fechaPago, String tipoBusqueda) {
		System.out.println("entro actualizar movimientos");
		String[] proveedores = matrizDatosPagos.split("\\]\\,\\[");
		proveedores[0] = proveedores[0].substring(1);
		proveedores[proveedores.length - 1] = 
				proveedores[proveedores.length - 1].substring(
					0,
					proveedores[proveedores.length - 1].length()-1);
		
		for (String proveedor : proveedores) {
			actualizarProveedores(proveedor, claveControl, fechaPago, tipoBusqueda);
			
		}
	}
	
	
	@Override
	public String crearPropuestaPagoSimple(String matrizDatosPagos, int sociedad, double importeSeleccionado, String fechaPago, String divisa, String tipoBusqueda) {
		String claveControl = "";
		String concepto;
		String mensaje = "";
		try {
			System.out.println("Entro a crear propuesta de pago simple");
			int idGrupoFlujo = pagosPedientesDao.obtenerGrupoFlujo(sociedad);
			//matrizDatosPagos = matrizDatosPagos.replace("}],[{", "},{");
			matrizDatosPagos = matrizDatosPagos.replace("[[{", "[{");
			matrizDatosPagos = matrizDatosPagos.replace("}]]", "}]");
			
//			String divisa = "MN";
//			
//			if (matrizDatosPagos.indexOf("DLS") > 0) {
//				divisa = "DLS";
//			}
			
			int i = pagosPedientesDao.obtenerFolioCupos(gb.getUsuarioLoginDto().getIdUsuario());
			do{
				claveControl = "M" + i;
				claveControl = claveControl + formato.format(gb.getFechaHoy());
				claveControl = claveControl + gb.getUsuarioLoginDto().getIdUsuario();
				i++;
			}while (pagosPedientesDao.claveControlValida(claveControl)>0);
			concepto = "M" + "-" + "MANUAL" + "-" + 
						gb.getUsuarioLoginDto().getClaveUsuario() + 
						"-" + idGrupoFlujo + "-" + 
						formato.format(gb.getFechaHoy()) + 
						"-" + divisa;
			
			int r = pagosPedientesDao.insertarPropuestaPago(claveControl, idGrupoFlujo, 
					fechaPago, concepto);
			
			if (r <= 0) {
				return "";
			}
			
			mensaje = divisa + ": " + claveControl + " ";
			
			actualizarMovimientos(matrizDatosPagos, claveControl, fechaPago, tipoBusqueda);			
			
			pagosPedientesDao.actualizaImporte(claveControl);
			
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " +  "PagosPnedientesDaoImpl retorno una lista vacia"
					+ "P:Egresos, C:PagosPedientesBusinessImpl, M:crearPropuestaPago");
			mensaje = "Ocurrio un error al crear la propuesta";
		}		
			
		return mensaje;
	}
	
	
	private void actualizarProveedores(String matrizDatosPagos, String claveControl, String fechaPago, String tipoBusqueda) {
		System.out.println("entro actualizar proveedores");
		String[] renglones = matrizDatosPagos.split("\\}\\,\\{");
		renglones[0] = renglones[0].substring(1);
		renglones[renglones.length - 1] = 
				renglones[renglones.length - 1].substring(
					0,
						renglones[renglones.length - 1].length()-1);
		
		Gson gson = new Gson();
		
		List<String> bloques = new ArrayList<String>();
		
		for(int i = 0; i<renglones.length; i++){
			StringBuffer foliosAux = new StringBuffer();
			for (int c = 0; i < renglones.length && c < 1000; i++) {
				HashMap<String, String> renglon = 
						gson.fromJson("{" + renglones[i] + "}", 
								new TypeToken<HashMap<String, String>>() {}.getType());
				if (renglon.get("seleccionado").equals("X")) {						
					foliosAux.append("'");
					foliosAux.append(renglon.get("factura"));
					foliosAux.append("',");						
					c++;
				}
			}
			
			if (foliosAux != null &&
					foliosAux.length() != 0) {
				foliosAux.delete(foliosAux.length() - 1, foliosAux.length());
				bloques.add(foliosAux.toString());
			}			
			
			i--;
							
		}
		
		HashMap<String, String> renglon = 
				gson.fromJson("{" + renglones[0] + "}", 
						new TypeToken<HashMap<String, String>>() {}.getType());
		
		for (String bloque : bloques) {
			pagosPedientesDao.actualizaMvimiento(
					bloque, 
					renglon.get("chequera"), 
					Integer.parseInt(renglon.get("idBanco")) ,
					renglon.get("referencaBanc"),
					Integer.parseInt(renglon.get("idBancoP")), 
					renglon.get("chequeraP"), 
					fechaPago, claveControl, tipoBusqueda);
		}
	}


	public double calculaTotalPropuesta(String matrizDatosPagos, String divisa){
		Gson gson = new Gson();
		double total = 0.00;
		List<Map<String, String>> datos = gson.fromJson(matrizDatosPagos, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		for(int i = 0; i < datos.size(); i++){	
			if(datos.get(i).get("divisa").equals(divisa)){
				System.out.println(datos.get(i).get("importe"));
				total = (total + Double.parseDouble(datos.get(i).get("importe")));
			}
		}
		return Math.rint(total*100)/100;
	}
	
	@Override
	public String guardarPlantilla(String datosPlantilla, List<Map<String, String>> rangosDocumentos ,List<Map<String, String>> rangosProveedores) {
		String mensaje = "";
		PlantillaDto plantillaDto = new PlantillaDto();
		int bandera=0;
		try{
			Gson gson = new Gson();
			List<Map<String, String>> plantilla = gson.fromJson(datosPlantilla, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
			
			plantillaDto.setIdReglaNegocio(Integer.parseInt(plantilla.get(0).get("reglaNegocio")));
			plantillaDto.setDivisa(plantilla.get(0).get("divisa"));
			plantillaDto.setIndicadores(plantilla.get(0).get("indicadores"));
			plantillaDto.setTipoDocumento(plantilla.get(0).get("tipoDoc"));
			plantillaDto.setNombrePlantilla(plantilla.get(0).get("nombrePlantilla")); 
			plantillaDto.setIdUsuario(gb.getUsuarioLoginDto().getIdUsuario());
			
			plantillaDto.setNumDocA(plantilla.get(0).get("numDocA"));
			plantillaDto.setNumDocB(plantilla.get(0).get("numDocB"));
			plantillaDto.setEquivalePersonaA(plantilla.get(0).get("equivalePersonaA"));
			plantillaDto.setEquivalePersonaB(plantilla.get(0).get("equivalePersonaB"));
			plantillaDto.setGrupoEmpresas(plantilla.get(0).get("grupoEmpresa"));
			
			plantillaDto.setNombreRegla(plantilla.get(0).get("nombreRegla"));
			plantillaDto.setFechaContableInicio(plantilla.get(0).get("fechaConIni"));
			plantillaDto.setFechaContableFin(plantilla.get(0).get("fechaConFin"));
			plantillaDto.setFechaPropuestaPagoInicio(plantilla.get(0).get("fechaPropIni"));
			plantillaDto.setFechaPropuestaPagoFin(plantilla.get(0).get("fechaPropFin"));
			
			if(!plantillaDto.getNumDocA().equals("")&&!plantillaDto.getNumDocB().equals("")){
				Map<String, String> map = new HashMap<>();
				map.put("de", plantillaDto.getNumDocA());
				map.put("a", plantillaDto.getNumDocB());
				rangosDocumentos.add(map);
			}else if(!plantillaDto.getNumDocB().equals("")){
					Map<String, String> map = new HashMap<>();
					map.put("de", plantillaDto.getNumDocB());
					map.put("a", plantillaDto.getNumDocB());
					rangosDocumentos.add(map);	
			}else if(!plantillaDto.getNumDocA().equals("")){
					Map<String, String> map = new HashMap<>();
					map.put("de", plantillaDto.getNumDocA());
					map.put("a", plantillaDto.getNumDocA());
					rangosDocumentos.add(map);
			}
			
			if(!plantillaDto.getEquivalePersonaA().equals("")&&!plantillaDto.getEquivalePersonaB().equals("")){
				Map<String, String> map = new HashMap<>();
				map.put("de", plantillaDto.getEquivalePersonaA());
				map.put("a", plantillaDto.getEquivalePersonaB());
				rangosProveedores.add(map);
			}else if(!plantillaDto.getEquivalePersonaB().equals("")){
					Map<String, String> map = new HashMap<>();
					map.put("de", plantillaDto.getEquivalePersonaB());
					map.put("a", plantillaDto.getEquivalePersonaB());
					rangosProveedores.add(map);	
			}else if (!plantillaDto.getEquivalePersonaA().equals("")){
					Map<String, String> map = new HashMap<>();
					map.put("de", plantillaDto.getEquivalePersonaA());
					map.put("a", plantillaDto.getEquivalePersonaA());
					rangosProveedores.add(map);
			}
			
			
			if(rangosProveedores.size()>0 || rangosDocumentos.size()>0)	{
				bandera=1;
			}
			mensaje = pagosPedientesDao.guardarPlantilla(plantillaDto,bandera);
			
			if(!mensaje.equals("Ocurrio un error al guardar la plantilla") && bandera==1){
				mensaje =pagosPedientesDao.guardarRangos(rangosDocumentos, rangosProveedores);
			}
			
			
		}catch(Exception e){
			e.getStackTrace();
			mensaje = "Ocurrio un error al guardar la plantilla";
			bitacora.insertarRegistro(new Date().toString() + " " +  "PagosPnedientesDaoImpl retorno una lista vacia"
					+ "P:Egresos, C:PagosPedientesBusinessImpl, M:guardarPlantilla");
		}
		
		return mensaje;
		
	}

	@Override
	public List<LlenaComboGralDto> obtenerListaBancos(String proveedor,String tipoBusqueda) {
		return pagosPedientesDao.obtenerListaBancos(proveedor,tipoBusqueda);
	}
	
	@Override
	public List<LlenaComboGralDto> obtenerListaChequeras(String proveedor, int idBanco, String tipoBusqueda) {
		return pagosPedientesDao.obtenerListaChequeras(proveedor, idBanco, tipoBusqueda);
	}
	
	
	@Override
	public List<LlenaComboGralDto> obtenerListaBancosPagador(int noEmpresa, String divisa) {
		return pagosPedientesDao.obtenerListaBancosPagador(noEmpresa, divisa);
	}
	
	@Override
	public List<LlenaComboGralDto> obtenerListaChequerasPagadoras(int noEmpresa, String divisa, int idBanco, String beneficiario, String acreedor) {
		return pagosPedientesDao.obtenerListaChequerasPagadoras(noEmpresa, divisa, idBanco, beneficiario, acreedor);
	}
	
	@Override
	public List<LlenaComboDivisaDto>obtenerDivisas(){
		return pagosPedientesDao.obtenerDivisas();
	}
	
	@Override
	public PlantillaDto obtenerFechasIni() {
		return pagosPedientesDao.obtenerFechasIni();
	}
	
	@Override
	public List<PlantillaDto> obtenerListaPlantillas() {
		List<PlantillaDto> listaPlantillas = new ArrayList<PlantillaDto>();
		try {
			listaPlantillas = pagosPedientesDao.obtenerListaPlantillas();
		} catch (Exception e) {
			e.getStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " +  e.toString()
					+ "P:Egresos, C:PagosPedientesBusinessImpl, M:obtenerListaPlantillas");
		}
		
		return listaPlantillas;
	}


	@Override
	public Map<String, Object> obtenerPlantilla(int idPlantilla) {
		Map<String, Object> resultado = new HashMap<String, Object>();
		PlantillaDto plantilla = new PlantillaDto();
		List<Map<String , String>> documentos = new ArrayList<>();
		List<Map<String , String>> proveedores = new ArrayList<>();
		try{
			plantilla = pagosPedientesDao.obtenerPlantilla(idPlantilla);
			if(plantilla!=null){
				if(plantilla.getbRango()!= null && plantilla.getbRango().equals("1")){
					List<Map<String, String>> rangos =pagosPedientesDao.obtenerPlantillaRangos(idPlantilla);
					for (int i = 0; i < rangos.size(); i++) {
						Map<String , String> aux = new HashMap<String , String>();
						aux.put("de", rangos.get(i).get("de"));
						aux.put("a", rangos.get(i).get("de"));
						if(rangos.get(i).get("tipo").equals("D")){
							documentos.add(aux);
						}else if(rangos.get(i).get("tipo").equals("P")){
							proveedores.add(aux);
						}
					}
				}
			}
			resultado.put("plantilla", plantilla);
			resultado.put("documentos",documentos);
			resultado.put("proveedores",proveedores);
		}catch(Exception e){
			e.getStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " +  e.toString()
			+ "P:Egresos, C:PagosPedientesBusinessImpl, M:obtenerPlantilla");
		}
		
		return resultado;
	}
	
	@Override
	public List<LlenaComboGralDto>llenarComboReglaNegocio(LlenaComboGralDto dto){
		
		List<LlenaComboGralDto> listRet= new ArrayList<LlenaComboGralDto>();
		try{
			listRet = pagosPedientesDao.llenarComboReglaNegocio(dto);
		}catch(Exception e){
			e.getStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConsultaPropuestasBusinessImpl, M:llenaComboPeriodos");
		}
		return listRet;
	}
	
	@Override
	public String controlFechas(String fechaHoy, String origen){
		String fecha = "";
		boolean horaLimite = pagosPedientesDao.horaLimmite();
		try {
			Calendar calendario = Calendar.getInstance();
			SimpleDateFormat formatoDelTexto = new SimpleDateFormat("dd/MM/yyyy");
			Date fechaDate = formatoDelTexto.parse(fechaHoy);
			if(origen.equals("select")){

				calendario.setTime(fechaDate);
	
				while(calendario.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || calendario.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY){
					calendario.add(Calendar.DATE, 1);
				}
				fechaDate = calendario.getTime();
				
				if(pagosPedientesDao.obtenerDiaInhabil(formatoDelTexto.format(fechaDate)) > 0){
					calendario.setTime(fechaDate);
					calendario.add(Calendar.DATE, 1);
					fechaDate = calendario.getTime();
					fecha = controlFechas(formatoDelTexto.format(fechaDate), origen);
				}else{
					fecha = formatoDelTexto.format(fechaDate);
				}
			}else if(origen.equals("load")){
				if(horaLimite){
					fecha = fechaHoy;
				}else{
					calendario.setTime(fechaDate);
					calendario.add(Calendar.DATE, 1);
					
					while(calendario.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || calendario.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY){
						calendario.add(Calendar.DATE, 1);
					}
				
					fechaDate = calendario.getTime();
					if(pagosPedientesDao.obtenerDiaInhabil(formatoDelTexto.format(fechaDate)) > 0){
						fecha = controlFechas(formatoDelTexto.format(fechaDate), origen);
					}else{
						fecha = formatoDelTexto.format(fechaDate);
					}
				
				}

			}
			
		} catch (Exception e) {
			e.getStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " +  e.toString()
			+ "P:Egresos, C:PagosPedientesBusinessImpl, M:controlFechas");
		}
		
		return fecha;
	}

	public String pagosProgramados(String fecha){
		Map<String, String> resultado= new HashMap<>();
		DecimalFormatSymbols symbols = new DecimalFormatSymbols();
		symbols.setDecimalSeparator('.');
		symbols.setGroupingSeparator(',');
		String pattern = "###,##0.00";
		DecimalFormat decimalFormat = new DecimalFormat(pattern, symbols);
		String usuario=GlobalSingleton.getInstancia().getUsuarioLoginDto().getClaveUsuario();
		try {
			if(fecha!=null && !fecha.equals("") && usuario!= null && !usuario.equals(""))
				resultado = pagosPedientesDao.pagosProgramados(fecha, usuario);
			
			if(resultado.size()==0)
				return "0.0%0.0";
			
			double pesos= Double.parseDouble(resultado.get("pesos"));
			double dolares= Double.parseDouble(resultado.get("dolares"));
			
			return "$"+decimalFormat.format(pesos)+"%"+"$"+decimalFormat.format(dolares);
		} catch (Exception e) {
			e.getStackTrace();
			return "$0.0%$0.0";
		}
		
	}
	
	@Override
	public String ejecutarPagoParcial(String fechaPago, float importeTotal, float importePagoParcial, String noFolioDet) {
		String mensaje = "";
		try{
		String parametros = noFolioDet+","+importePagoParcial+","+fechaPago;
		System.out.println(parametros);
			mensaje = pagosPedientesDao.ejecutarPagoParcial(parametros);
			if(mensaje.equals("Terminando sp_Solicitud_parcial")){
				mensaje="Pago parcial realizado con exito";
			}else{
				mensaje="Ocurrio un error durante el proceso de pago parcial";
			}
		}catch(Exception e){
			e.getStackTrace();
			mensaje = "Ocurrio un error al parcializar el pago";
			bitacora.insertarRegistro(new Date().toString() + "P:Egresos, C:PagosPedientesBusinessImpl, M:ejecutarPagoParcial");
		}
		
		return mensaje;
	}
	
	@Override
	public String ejecutarCambioDatosMovimiento(String fechaPago, String claves,  int idBancoBenef, String idChequeraBenef, int idBancoPag, String idChequeraPag, String refBancaria, String tipoBusqueda) {
		String mensaje = "";
		try{
			claves=claves.substring(0,claves.length()-1);
			String[] folioDet=claves.split(",");
			String noCliente = pagosPedientesDao.obtenProveedor(folioDet);
			String valida = pagosPedientesDao.validaReferenciaCte(noCliente);
			String campoRefBancaria="";
			if(!refBancaria.equals("") && tipoBusqueda.equals("P")){
			switch (valida) {
				case "":
					pagosPedientesDao.actualizaProveedor(noCliente);
					campoRefBancaria="concepto";
					break;
				case "concepto y no_factura":
					mensaje="El proveedor requiere de una referencia alfanumerica y una numerica";
					return mensaje;
				case "concepto":
					campoRefBancaria=valida;
					break;
				case "no_docto":
					campoRefBancaria=valida;
					break;
				case "no_factura":
					campoRefBancaria=valida;
					break;
				case "referencia":
					campoRefBancaria=valida;
					break;
				case "referencia_dalton":
					campoRefBancaria=valida;
					break;
				default: 
					mensaje = "Ya tiene una referencia unica";
					return mensaje;
				}
			}
			int res = pagosPedientesDao.ejecutarCambioDatosMovimiento(fechaPago, claves, idBancoBenef, idChequeraBenef, idBancoPag, idChequeraPag, refBancaria, campoRefBancaria, tipoBusqueda);
				if(res>0){
					mensaje="Datos modificados con exito";
				}else{
					mensaje="Ocurrio un error durante el proceso de modificación";
				}
		}catch(Exception e){
			e.getStackTrace();
			mensaje = "Ocurrio un error al cambiar la fecha el pago";
			bitacora.insertarRegistro(new Date().toString() + "P:Egresos, C:PagosPedientesBusinessImpl, M:ejecutarCambioFechaPago");
		}
		
		return mensaje;
	}
	
	public List<LlenaComboGralDto> llenarComboGrupoFlujo(){
		List<LlenaComboGralDto> combo=  new ArrayList<LlenaComboGralDto>();
		try {
			GrupoEmpresasDto dto = new GrupoEmpresasDto();
			dto.setIdEmpresa(0);
			dto.setIdUsuario(GlobalSingleton.getInstancia().getUsuarioLoginDto().getIdUsuario()+"");
			dto.setPagoEmpresa("");
			combo= pagosPedientesDao.llenarComboGrupoFlujo(dto);
		} catch (Exception e) {
			e.getStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:PagosPendientesBusinessImpl, M:llenarComboGrupoFlujo");
		}return combo;
	}
	
	public List<PagosPendientesDto> obtenerTodosPagosPendientesPorEmpresa(int numeroEmpresa, String acredor, PlantillaDto plantilla , String tipo){
		List<PagosPendientesDto> pagosPendientes = pagosPedientesDao.obtenerTodosPagosPendientesPorEmpresa(numeroEmpresa, acredor, plantilla, tipo);
		if(pagosPendientes.size() == 0 ){
			bitacora.insertarRegistro(new Date().toString() + " " +  "PagosPendientesDaoImpl retorno una lista vacia"
					+ "P:Egresos, C:PagosPedientesBusinessImpl, M:obtenerTodosPagosPendientesPorEmpresa");
		}
		return pagosPendientes;
	}
	/****************************Getters and Setters **********************/
	
	public PagosPedientesDao getPagosPedientesDao() {
		return pagosPedientesDao;
	}

	public void setPagosPedientesDao(PagosPedientesDao pagosPedientesDao) {
		this.pagosPedientesDao = pagosPedientesDao;
	}


	@Override
	public String retornarJsonPendientesNivTres(int numeroEmpresa, String acreedor, PlantillaDto plantilla,
			List<Map<String, String>> rangosDocumentos, List<Map<String, String>> rangosProveedores, int marca, boolean documentos) {
		if(!plantilla.getNumDocA().equals("")&&!plantilla.getNumDocB().equals("")){
			Map<String, String> map = new HashMap<>();
			map.put("de", plantilla.getNumDocA());
			map.put("a", plantilla.getNumDocB());
			rangosDocumentos.add(map);
		}
		List<Map<String, String>> pagosPendientes = pagosPedientesDao.obtenerPagosPendientesNivTresJson(
				numeroEmpresa, acreedor, plantilla, 
				rangosDocumentos, rangosProveedores, marca, documentos);
		if(pagosPendientes != null &&
				pagosPendientes.size() > 0 ){
			Gson gson = new Gson();
			String json = gson.toJson(pagosPendientes);
			return json;
		}
		return null;
	}
	
	@Override
	public List<Map<String, String>> leerExcel(HSSFWorkbook workbook, String[] keys, 
			int noUsuario, String tipo) {
		List<Map<String, String>> hojaDatos = new ArrayList<Map<String, String>>();
		
		pagosPedientesDao.limpiarIndividuales(noUsuario, tipo);
		
		HSSFSheet sheet = workbook.getSheetAt(0);
		Iterator<?> rows = sheet.rowIterator();
		rows.next();
		while (rows.hasNext()) {
			HSSFRow row = (HSSFRow) rows.next();
			
			Map<String, String> datosExcel = new HashMap<String, String>();
			
			DataFormatter formatter = new DataFormatter(); //creating formatter using the default locale
			
			boolean b = true;
			
			for (int i = 0; i < keys.length; i++) {
				HSSFCell cell = row.getCell(i);
				System.out.println(cell);
				if (cell != null && 
						formatter.formatCellValue(
								cell).trim().equals("")) {
					b = false;
					break;
				}
				if(cell != null && cell.getCellType() == Cell.CELL_TYPE_NUMERIC && 
						HSSFDateUtil.isCellDateFormatted(cell)){
					Funciones funciones = new Funciones();
					datosExcel.put(keys[i],funciones.ponerFechaSola(cell.getDateCellValue()));
				} else {
					datosExcel.put(keys[i],formatter.formatCellValue(cell));
				}
				System.out.println(formatter.formatCellValue(cell));
				/*if(cell.getCellType() == Cell.CELL_TYPE_NUMERIC){
					datosExcel.put(keys[i], obtenerNumero(cell.getNumericCellValue()));	
				}else if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
					datosExcel.put(keys[i], cell.getRichStringCellValue().toString());
				}*/
			}
			
			if (b) {
				hojaDatos.add(datosExcel);
				pagosPedientesDao.insertaIndividual(datosExcel, noUsuario, tipo);
			}
			
		}
		return hojaDatos;
	}

	
	public List<Map<String, String>> leerExcel(XSSFWorkbook workbook, String[] keys, 
			int noUsuario, String tipo) {
		List<Map<String, String>> hojaDatos = new ArrayList<Map<String, String>>();
		
		pagosPedientesDao.limpiarIndividuales(noUsuario, tipo);
		
		XSSFSheet sheet = workbook.getSheetAt(0);
		Iterator<?> rows = sheet.rowIterator();
		rows.next();
		while (rows.hasNext()) {
			XSSFRow row = (XSSFRow) rows.next();
			
			Map<String, String> datosExcel = new HashMap<String, String>();
			DataFormatter formatter = new DataFormatter(); //creating formatter using the default locale
			 //Cell cell = sheet.getRow(i).getCell(0);
			//String j_username = formatter.formatCellValue(cell); //Returns the formatted value of a cell as a String regardless of the cell type.
			boolean b = true;
			
			for (int i = 0; i < keys.length; i++) {
				XSSFCell cell = row.getCell(i);
				//datosExcel.put(keys[i],formatter.formatCellValue(cell));
				System.out.println(cell);
				if (cell != null && 
						formatter.formatCellValue(
								cell).trim().equals("")) {
					b = false;
					break;
				}
				if(cell != null && cell.getCellType() == Cell.CELL_TYPE_NUMERIC && 
						HSSFDateUtil.isCellDateFormatted(cell)){
					Funciones funciones = new Funciones();
					datosExcel.put(keys[i],funciones.ponerFechaSola(cell.getDateCellValue()));
				}else{
					datosExcel.put(keys[i],formatter.formatCellValue(cell));
				}
				
				/*if(cell.getCellType() == Cell.CELL_TYPE_NUMERIC){
					datosExcel.put(keys[i], obtenerNumero(cell.getNumericCellValue()));	
				}else if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
					datosExcel.put(keys[i], cell.getRichStringCellValue().toString());
				}*/
				System.out.println(formatter.formatCellValue(cell));
			}
			
			if (b) {
				hojaDatos.add(datosExcel);
				pagosPedientesDao.insertaIndividual(datosExcel, noUsuario, tipo);
			}
		}
		return hojaDatos;
	}



}
