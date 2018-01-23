package com.webset.set.egresos.action;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.softwarementors.extjs.djn.config.annotations.DirectMethod;
import com.softwarementors.extjs.djn.servlet.ssm.WebContextManager;
import com.webset.set.egresos.dto.ConsultaSolicitudPagoSinPolizaDto;
import com.webset.set.egresos.service.ConsultaSolicitudPagoSinPolizaService;
import com.webset.set.seguridad.action.SegPerfilFacultadAction;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.Contexto;
import com.webset.utils.tools.Utilerias;

public class ConsultaSolicitudPagoSinPolizaAction {
	Contexto contexto = new Contexto();
	Bitacora bitacora = new Bitacora();
	ConsultaSolicitudPagoSinPolizaService objConsultaSolicitudPagoSinPolizaService;
		
	@DirectMethod
	public List<ConsultaSolicitudPagoSinPolizaDto> llenaGrid(String noEmpresa, String fechaIni, String fechaFin, String usuario, String usuarioEnCurso){
		List<ConsultaSolicitudPagoSinPolizaDto> listaResultado = new ArrayList<ConsultaSolicitudPagoSinPolizaDto>();
		if(!Utilerias.haveSession(WebContextManager.get()) || !Utilerias.tienePermiso(WebContextManager.get(), 191))
			return listaResultado;
		SegPerfilFacultadAction facultad =new SegPerfilFacultadAction();
		boolean bandera=false;
		try{
			bandera=(Boolean)facultad.verificarFacultad("eccmbUsuarios").get("facultad");
			noEmpresa= (noEmpresa!=null && !noEmpresa.equals(""))? noEmpresa:"";
			fechaIni= (fechaIni!=null && !fechaIni.equals(""))? fechaIni:"";
			fechaFin= (fechaFin!=null && !fechaFin.equals(""))? fechaFin:"";
			usuario= (usuario!=null && !usuario.equals(""))? usuario:"";
			usuarioEnCurso= (usuarioEnCurso!=null && !usuarioEnCurso.equals("") )? usuarioEnCurso:"";
			objConsultaSolicitudPagoSinPolizaService = (ConsultaSolicitudPagoSinPolizaService)contexto.obtenerBean("objConsultaSolicitudPagoSinPolizaBusinessImpl");
			listaResultado = objConsultaSolicitudPagoSinPolizaService.llenaGrid(noEmpresa,fechaIni ,fechaFin, usuario,bandera, usuarioEnCurso);
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConsultaSolicitudPagoSinPolizaAction, M: llenaGrid");
		} return listaResultado;
	}
	
	@DirectMethod
	public List<ConsultaSolicitudPagoSinPolizaDto> llenaComboUsuario(String idUsuario){
		List<ConsultaSolicitudPagoSinPolizaDto> listaResultado = new ArrayList<ConsultaSolicitudPagoSinPolizaDto>();
		if(!Utilerias.haveSession(WebContextManager.get()) || !Utilerias.tienePermiso(WebContextManager.get(), 191))
			return listaResultado;
		SegPerfilFacultadAction facultad =new SegPerfilFacultadAction();
		boolean bandera=false;
		try{
			bandera=(Boolean)facultad.verificarFacultad("eccmbUsuarios").get("facultad");;
			objConsultaSolicitudPagoSinPolizaService = (ConsultaSolicitudPagoSinPolizaService)contexto.obtenerBean("objConsultaSolicitudPagoSinPolizaBusinessImpl");
			listaResultado = objConsultaSolicitudPagoSinPolizaService.llenaComboUsuario(bandera,idUsuario);
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Egresos, C: ConsultaSolicitudPagoSinPolizaAction, M: llenaComboUsuario");
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
			+"P:Egresos, C:ConsultaSolicitudPagoSinPolizaAction, M:obtenerExcel");
		} catch (IOException e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:ConsultaSolicitudPagoSinPolizaAction, M:obtenerExcel");
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:ConsultaSolicitudPagoSinPolizaAction, M:obtenerExcel");
		}
		arch.delete();
		return workbook;
	}
	
	@DirectMethod
	public String exportaExcel(String datos) {
		String resultado = "";
		if(!Utilerias.haveSession(WebContextManager.get()) || !Utilerias.tienePermiso(WebContextManager.get(), 191))
			return resultado;
		try {
			objConsultaSolicitudPagoSinPolizaService = (ConsultaSolicitudPagoSinPolizaService)contexto.obtenerBean("objConsultaSolicitudPagoSinPolizaBusinessImpl");
			resultado = objConsultaSolicitudPagoSinPolizaService.exportaExcel(datos);
		}catch(Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Egresos, C:ConsultaSolicitudPagoSinPolizaAction, M: exportaExcel");
		}
		return resultado;
	}
}
