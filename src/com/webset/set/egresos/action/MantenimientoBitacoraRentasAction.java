package com.webset.set.egresos.action;
import java.util.List;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.softwarementors.extjs.djn.config.annotations.DirectMethod;
import com.softwarementors.extjs.djn.servlet.ssm.WebContextManager;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.Contexto;
import com.webset.set.utilerias.GlobalSingleton;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.utils.tools.Utilerias;
import com.webset.set.egresos.service.MantenimientoBitacoraRentasService;
import com.webset.set.egresos.dto.MantenimientoBitacoraRentasDto;

public class MantenimientoBitacoraRentasAction {
	Contexto contexto = new Contexto();
	Bitacora bitacora = new Bitacora();
	MantenimientoBitacoraRentasService objMantenimientoBitacoraRentasService;
		
	@DirectMethod
	public List<MantenimientoBitacoraRentasDto> llenaGridBitacoraRentas(String noEmpresa, String noBeneficiario,String estatus){
		List<MantenimientoBitacoraRentasDto> listaResultado = new ArrayList<MantenimientoBitacoraRentasDto>();
		if(!Utilerias.haveSession(WebContextManager.get()) || !Utilerias.tienePermiso(WebContextManager.get(), 190))
			return listaResultado;
		try{
			objMantenimientoBitacoraRentasService = (MantenimientoBitacoraRentasService)contexto.obtenerBean("objMantenimientoBitacoraRentasBusinessImpl");
			listaResultado = objMantenimientoBitacoraRentasService.llenaGridBitacoraRentas(noEmpresa,noBeneficiario, estatus);
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Egresos, C:MantenimientoBitacoraRentasAction, M: llenaGridBitacoraRentas");
		} return listaResultado;
	}
		
	@DirectMethod
	public String updateMantenimientoBitacoraRentas(String sJson){
		String resultado="";
		if(!Utilerias.haveSession(WebContextManager.get()) || !Utilerias.tienePermiso(WebContextManager.get(), 190))
			return resultado;
		Gson gson = new Gson();
		List<MantenimientoBitacoraRentasDto> listaResultado = new ArrayList<MantenimientoBitacoraRentasDto>();		
		List<Map<String, String>> objDatos = gson.fromJson(sJson, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		int t;
		try{
			objMantenimientoBitacoraRentasService = (MantenimientoBitacoraRentasService)contexto.obtenerBean("objMantenimientoBitacoraRentasBusinessImpl");
			System.out.println(objDatos.size());
			for(t=0; t<objDatos.size(); t++) {
				MantenimientoBitacoraRentasDto dto=new MantenimientoBitacoraRentasDto();
				dto.setNoEmpresa(objDatos.get(t).get("noEmpresa")!=null ? objDatos.get(t).get("noEmpresa"): "");
				dto.setNoBeneficiario(objDatos.get(t).get("noBeneficiario")!=null ? objDatos.get(t).get("noBeneficiario"):"" );
				dto.setNoDocumento(objDatos.get(t).get("noDocumento")!=null ? objDatos.get(t).get("noDocumento"):"" );
				dto.setFechaRechazo(objDatos.get(t).get("fechaRechazo")!=null ? objDatos.get(t).get("fechaRechazo"):"" );
				dto.setIdTipoGasto(objDatos.get(t).get("idTipoGasto")!=null ? objDatos.get(t).get("idTipoGasto"):"" );
				dto.setEstatus(objDatos.get(t).get("estatus")!=null ? objDatos.get(t).get("estatus"):"" );
				dto.setImporte(objDatos.get(t).get("importe")!=null ? objDatos.get(t).get("importe"):"" );
				dto.setNoFolioDetalle(objDatos.get(t).get("noFolioDetalle")!=null ? objDatos.get(t).get("noFolioDetalle"):"" );
				listaResultado.add(dto);
			}
			resultado = objMantenimientoBitacoraRentasService.updateMantenimientoBitacoraRentas(listaResultado);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Egresos, C:MantenimientoBitacoraRentasAction, M: updateMantenimientoBitacoraRentas");
		} return resultado;	
	}
	
	@DirectMethod
	public List<LlenaComboGralDto> llenaComboEmpresas(){
		List<LlenaComboGralDto> listaResultado = new ArrayList<LlenaComboGralDto>();
		if(!Utilerias.haveSession(WebContextManager.get()) || !Utilerias.tienePermiso(WebContextManager.get(), 190))
			return listaResultado;
		try{
			objMantenimientoBitacoraRentasService = (MantenimientoBitacoraRentasService)contexto.obtenerBean("objMantenimientoBitacoraRentasBusinessImpl");
			String usuario= GlobalSingleton.getInstancia().getUsuarioLoginDto().getIdUsuario()+"";
			if(usuario == null || usuario.equals(""))
				return null;
			listaResultado = objMantenimientoBitacoraRentasService.llenaComboEmpresa(usuario);
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Egresos, C:MantenimientoBitacoraRentasAction, M: llenaComboEmpresas");
		} return listaResultado;
	}
	
	@DirectMethod
	public List<LlenaComboGralDto> llenaComboProveedor(String nombre , String noProv){
		List<LlenaComboGralDto> listaResultado = new ArrayList<LlenaComboGralDto>();
		if(!Utilerias.haveSession(WebContextManager.get()) || !Utilerias.tienePermiso(WebContextManager.get(), 190))
			return listaResultado;
		try{
			nombre= nombre!=null ? nombre:"";
			noProv = noProv!=null ? noProv: "0";
			objMantenimientoBitacoraRentasService = (MantenimientoBitacoraRentasService)contexto.obtenerBean("objMantenimientoBitacoraRentasBusinessImpl");
			listaResultado = objMantenimientoBitacoraRentasService.llenaComboProveedor(nombre, noProv);
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Egresos, C:MantenimientoBitacoraRentasAction, M: llenaComboProveedor");
		} return listaResultado;
	}
	
	@DirectMethod
	public HSSFWorkbook obtenerExcel(String nombre) {
		FileInputStream file = null;
		HSSFWorkbook workbook = null;
		File arch = new File(nombre);
		try {
			file = new FileInputStream(arch);
			workbook = new HSSFWorkbook(file);
			file.close();
		} catch (FileNotFoundException e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:MantenimientoBitacoraRentasAction, M:obtenerExcel");
		} catch (IOException e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:MantenimientoBitacoraRentasAction, M:obtenerExcel");
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:MantenimientoBitacoraRentasAction, M:obtenerExcel");
		}
		arch.delete();
		return workbook;
	}
	
	@DirectMethod
	public String exportaExcel(String datos) {
		String resultado = "";
		if(!Utilerias.haveSession(WebContextManager.get()) || !Utilerias.tienePermiso(WebContextManager.get(), 190))
			return resultado;
		try {
			objMantenimientoBitacoraRentasService = (MantenimientoBitacoraRentasService)contexto.obtenerBean("objMantenimientoBitacoraRentasBusinessImpl");
			resultado = objMantenimientoBitacoraRentasService.exportaExcel(datos);
		}catch(Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Egresos, C:MantenimientoBitacoraRentasAction, M: exportaExcel");
		}
		return resultado;
	}
}
