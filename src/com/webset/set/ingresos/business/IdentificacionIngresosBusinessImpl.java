package com.webset.set.ingresos.business;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.webset.set.conciliacionbancoset.dto.ConciliaBancoDto;
import com.webset.set.conciliacionbancoset.dto.CriteriosBusquedaDto;
import com.webset.set.ingresos.dao.IdentificacionIngresosDao;
import com.webset.set.ingresos.dto.CuentaContableDto;
import com.webset.set.ingresos.service.IdentificacionIngresosService;
import com.webset.set.seguridad.dto.PersonaDto;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.ConstantesSet;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utilerias.GlobalSingleton;
import com.webset.set.utilerias.dto.LlenaComboChequeraDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.set.utilerias.dto.MovimientoDto;
import com.webset.utils.tools.Utilerias;

public class IdentificacionIngresosBusinessImpl implements IdentificacionIngresosService {

	Funciones funciones= new Funciones();
	Bitacora bitacora = new Bitacora(); 
	GlobalSingleton globalSingleton;
	IdentificacionIngresosDao identificacionIngresosDao;
	
	
	public IdentificacionIngresosDao getIdentificacionIngresosDao() {
		return identificacionIngresosDao;
	}
	
	public void setIdentificacionIngresosDao(IdentificacionIngresosDao identificacionIngresosDao) {
		this.identificacionIngresosDao = identificacionIngresosDao;
	}

	@Override
	public List<LlenaComboGralDto> llenarComboBancos(int noEmpresa) {
		
		List<LlenaComboGralDto> listDatos = new ArrayList<LlenaComboGralDto>();
		try{
			listDatos = identificacionIngresosDao.llenarComboBancos(noEmpresa);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
					+ "P:Ingresos, C:IdentificacionIngresosBusinessImpl, M:llenarComboBancos");
		}
		return listDatos;
		
	}
	
	@Override
	public List<CuentaContableDto> getCuentaContable(int noEmpresa, String idGrupo,String idRubro) {
		List<CuentaContableDto> listDatos = new ArrayList<CuentaContableDto>();
		try{
			listDatos = identificacionIngresosDao.getCuentaContable(noEmpresa, idGrupo,idRubro);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Ingresos, C:IdentificacionIngresosBusinessImpl, M:getCuentaContable");
		}
		
		return listDatos;
	}

	@Override
	public List<LlenaComboChequeraDto> llenarCmbChequeras(int iBanco, int iEmpresa, int iOpc) {
		
		List<LlenaComboChequeraDto> listDatos = new ArrayList<LlenaComboChequeraDto>();
		try{
			listDatos = identificacionIngresosDao.llenarCmbChequeras(iBanco, iEmpresa, iOpc);
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
					+ "P:Ingresos, C:IdentificacionIngresosBusinessImpl, M:llenarCmbChequeras");
		}
		return listDatos;
		
	}

	@Override
	public List<ConciliaBancoDto> llenarMovsEmpresa(CriteriosBusquedaDto dto){
		List<ConciliaBancoDto> movsBanco = new ArrayList<ConciliaBancoDto>();
		try{
			movsBanco = identificacionIngresosDao.llenarMovsEmpresa(dto);
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Ingresos, C:IdentificacionIngresosBusinessImpl, M:llenarMovsEmpresa");
		}
		return movsBanco;
	}
	
	@Override
	public List<MovimientoDto> llenarMovsBancos(CriteriosBusquedaDto dto){
		List<MovimientoDto> movsEmpresa = new ArrayList<MovimientoDto>();
		try{
			movsEmpresa = identificacionIngresosDao.llenarMovsBancos(dto);

		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
				+ "P:Ingresos, C:IdentificacionIngresosBusinessImpl, M:llenarMovsBancos");
		}
		return movsEmpresa;
	}
	
	@Override
	public List<MovimientoDto> llenarMovsBancos2(CriteriosBusquedaDto dto){
		System.out.println("llenarMovsBancos2-----");
		List<MovimientoDto> movsEmpresa = new ArrayList<MovimientoDto>();
		try{
			movsEmpresa = identificacionIngresosDao.llenarMovsBancos2(dto);

		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
				+ "P:Ingresos, C:IdentificacionIngresosBusinessImpl, M:llenarMovsBancos2");
		}
		return movsEmpresa;
	}
	
	@Override
	public List<LlenaComboGralDto> llenarComboDivisa() {
		
		List<LlenaComboGralDto> listDatos = new ArrayList<LlenaComboGralDto>();
		try{
			listDatos = identificacionIngresosDao.llenarComboDivisa();
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
					+ "P:Ingresos, C:IdentificacionIngresosBusinessImpl, M:llenarComboDivisa");
		}
		return listDatos;
		
	}
	@Override
	public List<MovimientoDto> llenaComboCuentaContable() {
		
		List<MovimientoDto> listDatos = new ArrayList<MovimientoDto>();
		try{
			listDatos = identificacionIngresosDao.llenaComboCuentaContable();
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
					+ "P:Ingresos, C:IdentificacionIngresosBusinessImpl, M:llenaComboCuentaContable");
		}
		return listDatos;
		
	}
	
	@Override
	public List<ConciliaBancoDto> llenaComboCuentaContable2() {
		
		List<ConciliaBancoDto> listDatos = new ArrayList<ConciliaBancoDto>();
		try{
			listDatos = identificacionIngresosDao.llenaComboCuentaContable2();
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
					+ "P:Ingresos, C:IdentificacionIngresosBusinessImpl, M:llenaComboCuentaContable2");
		}
		return listDatos;
		
	}
	
	@Override
	public List<ConciliaBancoDto> llenaComboOrigen() {
		
		List<ConciliaBancoDto> listDatos = new ArrayList<ConciliaBancoDto>();
		try{
			listDatos = identificacionIngresosDao.llenaComboOrigen();
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
					+ "P:Ingresos, C:IdentificacionIngresosBusinessImpl, M:llenaComboOrigen");
		}
		return listDatos;
		
	}
	
	
	@Override
	public Map<String, String> conciliaBancosEmpresa(List<ConciliaBancoDto> listCobranza, List<MovimientoDto> listBanco) {
		
		Map<String, String> result = new HashMap<>();
		result.put("error", "");
		result.put("mensaje", "");
		
		try{
			
			result = identificacionIngresosDao.conciliaBancosEmpresa(listCobranza, listBanco);
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
					+ "P:Ingresos, C:IdentificacionIngresosBusinessImpl, M:conciliaBancosEmpresa");
		}
		return result;
		
	}
	

	@Override
	public Map<String, String> conciliaVirtualCobranza(List<ConciliaBancoDto> listCobranza, List<MovimientoDto> listBanco , String causa, String origen, String cuenta) {
		
		Map<String, String> result = new HashMap<>();
		result.put("error", "");
		result.put("mensaje", "");
		
		try{
			
			result = identificacionIngresosDao.conciliaVirtualCobranza(listCobranza, listBanco, causa, origen,cuenta);
			
		}catch(Exception e){
			
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
					+ "P:Ingresos, C:IdentificacionIngresosBusinessImpl, M:conciliaVirtualCobranza");
			result.put("error", "Error al convertir datos Bussines.");
		}
		return result;
		
	}
	

	@Override
	public Map<String, String> conciliaVirtualBanco(List<ConciliaBancoDto> listCobranza, List<MovimientoDto> listBanco , String causa, String origen, String cuenta) {
		
		Map<String, String> result = new HashMap<>();
		result.put("error", "");
		result.put("mensaje", "");
		
		try{
			
			result = identificacionIngresosDao.conciliaVirtualBanco(listCobranza, listBanco, causa, origen,cuenta);
			
		}catch(Exception e){
			
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
					+ "P:Ingresos, C:IdentificacionIngresosBusinessImpl, M:conciliaVirtualBanco");
			result.put("error", "Error al convertir datos Bussines.");
		}
		return result;
		
	}
	
public Map<String, String> parcializarBancos2(List<MovimientoDto> listBanco,double diferencia) {
		
		Map<String, String> result2 = new HashMap<>();
		result2.put("error", "");
		result2.put("mensaje", "");
		
		if(diferencia <= 0){
			result2.put("error", "Error al obtener la diferencia.");
			return result2;
		}
		
		try{
			result2 = identificacionIngresosDao.parcializarBancos2(listBanco, diferencia);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
					+ "P:Ingresos, C:IdentificacionIngresosBusinessImpl, M:parcializarBancosEmpresa2");
		}
		return result2;
		
	}
	
	
	@Override
	public Map<String, String> parcializarBancos(List<ConciliaBancoDto> listCobranza,  double diferencia) {
		
		Map<String, String> result = new HashMap<>();
		result.put("error", "");
		result.put("mensaje", "");
		
		if(diferencia <= 0){
			result.put("error", "Error al obtener la diferencia.");
			return result;
		}
		
		try{
			result = identificacionIngresosDao.parcializarBancos(listCobranza, diferencia);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
					+ "P:Ingresos, C:IdentificacionIngresosBusinessImpl, M:parcializarBancosEmpresa");
		}
		return result;
		
	}


public Map<String, String> concilVirSBanco(List<MovimientoDto> listBanco,String causa,String cuenta) {
		
		Map<String, String> result2 = new HashMap<>();
		result2.put("error", "");
		result2.put("mensaje", "");
		
		
		
		try{
			result2 = identificacionIngresosDao.concilVirSBanco(listBanco, causa,cuenta);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
					+ "P:Ingresos, C:IdentificacionIngresosBusinessImpl, M:concilVirSBanco");
		}
		return result2;
		
	}

@Override
public Map<String, String> concilVirSCobranza(List<ConciliaBancoDto> listCobranza, String causa,String cuenta) {
	
	Map<String, String> result = new HashMap<>();
	result.put("error", "");
	result.put("mensaje", "");
	
	
	
	try{
		result = identificacionIngresosDao.concilVirSCobranza(listCobranza, causa,cuenta);
	}catch(Exception e){
		bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
				+ "P:Ingresos, C:IdentificacionIngresosBusinessImpl, M:concilVirSCobranza");
	}
	return result;
	
}

	public List<ConciliaBancoDto> datosExcel(int empresa, String fechaIni, String fechaFin) {
		List<ConciliaBancoDto> result = new ArrayList<ConciliaBancoDto>();
		
		try{
			result = identificacionIngresosDao.datosExcel(empresa, fechaIni, fechaFin);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
					+ "P:Ingresos, C:IdentificacionIngresosBusinessImpl, M:Datos Excel");
		}
		return result;
	}

	@Override
	public String exportaExcel(int empresa, int caso, String fecIni, String fecFin) {
		String respuesta = "";
		List<Map<String, String>> parameters = null;
		int tam = 0;
	    try {
	    	if (caso == 0) tam = 9;
	    	else if (caso == 1) tam = 8;
	    	else tam = 15 ;
	    	
	    	String encabezados [] = new String[tam];
	    	String nombreParametros [] = new String[tam];
	    	String nombreReporte = "";
	    	
	    	switch (caso) {
				case 0:
					parameters = identificacionIngresosDao.obtieneFacturas(empresa, fecIni, fecFin);
					nombreReporte = "Facturas[" + empresa + "][" + fecIni.substring(0,10) + "_" + fecFin.substring(0,10) + "]";
			    	
					encabezados[0] = "Empresa";
					encabezados[1] = "Factura";
					encabezados[2] = "Fecha";
					encabezados[3] = "No. Cliente";
					encabezados[4] = "Cliente";
					encabezados[5] = "Importe";
					encabezados[6] = "Saldo";
					encabezados[7] = "Divisa";
					encabezados[8] = "Estatus";
			    	
			    	nombreParametros[0] = "empresa";
			    	nombreParametros[1] ="factura";
			    	nombreParametros[2]="fecha";
			    	nombreParametros[3]="noCliente";
			    	nombreParametros[4]="cliente";
			    	nombreParametros[5]="importe";
			    	nombreParametros[6]="saldo";
			    	nombreParametros[7]="idDivisa";
			    	nombreParametros[8]="estatus";
					
					break;
				case 1:
					parameters = identificacionIngresosDao.obtieneDepositos(empresa, fecIni, fecFin);
					nombreReporte = "Depositos[" + empresa + "][" + fecIni.substring(0,10) + "_" + fecFin.substring(0,10) + "]";
			    	
			    	encabezados[0]="Empresa";
			    	encabezados[1]="Banco";
			    	encabezados[2]="Cta. Bancaria";
			    	encabezados[3]="Fecha dep�sito";
			    	encabezados[4]="Importe";
			    	encabezados[5]="Moneda";
			    	encabezados[6]="Concepto";
		    		encabezados[7]="Estatus";
			    	
			    	nombreParametros[0]="empresa";
			    	nombreParametros[1]="banco";
			    	nombreParametros[2]="cuenta";
			    	nombreParametros[3]="fecha";
			    	nombreParametros[4]="importe";
			    	nombreParametros[5]="divisa";
			    	nombreParametros[6]="concepto";
			    	nombreParametros[7]="estatus";
					
					break;
				case 2:
					parameters = identificacionIngresosDao.obtieneConciliados(empresa, fecIni, fecFin);
					nombreReporte = "Conciliados[" + empresa + "][" + fecIni.substring(0,10) + "_" + fecFin.substring(0,10) + "]";
			    	
			    	encabezados[0]="Empresa";
			    	encabezados[1]="Fecha conciliaci�n";
			    	encabezados[2]="Cuenta";
			    	encabezados[3]="Folio set";
			    	encabezados[4]="Factura";
			    	encabezados[5]="Fecha factura";
			    	encabezados[6]="No. cliente";
			    	encabezados[7]="Cliente";
			    	encabezados[8]="Importe factura";
			    	encabezados[9]="Fecha deposito";
			    	encabezados[10]="Deposito bancario";
			    	encabezados[11]="Divisa";
			    	encabezados[12]="Banco";
			    	encabezados[13]="Concepto";
			    	encabezados[14]="Estatus";
			    	
			    	nombreParametros[0]="empresa";
			    	nombreParametros[1]="fechaConcilia";
			    	nombreParametros[2]="cuenta";
			    	nombreParametros[3]="folioSet";
			    	nombreParametros[4]="factura";
			    	nombreParametros[5]="fechaFactura";
			    	nombreParametros[6]="noCliente";
			    	nombreParametros[7]="cliente";
			    	nombreParametros[8]="importe";
			    	nombreParametros[9]="fechaDeposito";
			    	nombreParametros[10]="deposito";
			    	nombreParametros[11]="idDivisa";
			    	nombreParametros[12]="banco";
			    	nombreParametros[13]="concepto";
			    	nombreParametros[14]="estatus";
					
					break;
				default:
					break;
			}	    	
			HSSFWorkbook wb = Utilerias.generarExcel(encabezados, parameters, nombreParametros);			
            
            respuesta = ConstantesSet.RUTA_EXCEL + nombreReporte + ".xls";
            File outputFile = new File(respuesta);
          
			FileOutputStream outs = new FileOutputStream(outputFile);
			wb.write(outs);
			outs.close();            
        } catch (IOException e) {
        	respuesta = "";
        	bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) +"P:Bancaelectronica, C:MovimientosBEBusiness, M:exportaExcel");
    		
        } catch(Exception e){
        	bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) +"P:Bancaelectronica, C:MovimientosBEBusiness, M:exportaExcel");    		
            respuesta = "";
        }
        return respuesta;
	}

	@Override
	public List<PersonaDto> obtenerClientes(String cliente) {
		List<PersonaDto> personas = identificacionIngresosDao.obtenerClientes(cliente);
		return personas;
	}

	@Override
	public String enviarAnticipo(int folioDet, String cliente, double importe, int noEmpresa, String divisa) {
		String res = identificacionIngresosDao.enviarAnticipo(folioDet, cliente, importe, noEmpresa, divisa);
		return res;
	}
	
}
