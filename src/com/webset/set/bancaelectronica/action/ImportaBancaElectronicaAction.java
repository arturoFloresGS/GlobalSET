package com.webset.set.bancaelectronica.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.softwarementors.extjs.djn.config.annotations.DirectMethod;
import com.softwarementors.extjs.djn.servlet.ssm.WebContextManager;
import com.webset.set.bancaelectronica.business.ImportaBancaElectronicaBusiness;
import com.webset.set.bancaelectronica.dto.CatBancoDto;
import com.webset.set.bancaelectronica.dto.CatCtaBancoDto;
import com.webset.set.bancaelectronica.dto.EquivaleConceptoDto;
import com.webset.set.bancaelectronica.dto.ParamBusImpBancaDto;
import com.webset.set.bancaelectronica.dto.ParamRetImpBancaDto;
import com.webset.set.seguridad.dto.EmpresaDto;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.Contexto;
import com.webset.set.utilerias.Funciones;
import com.webset.utils.tools.Utilerias;
/**
 * @author Cristian Garcï¿½a Garcï¿½a
 * @since 07/12/2010
 */
public class ImportaBancaElectronicaAction {
	Contexto contexto=new Contexto();
	Bitacora bitacora=new Bitacora();
	Funciones funciones = new Funciones();
	private static Logger logger = Logger.getLogger(ImportaBancaElectronicaAction.class);
	//dto de dos parametros para retornar el id del banco y su descripciï¿½n
	private static class ComboBancos {
		@SuppressWarnings("unused") 
		public int idBanco;
		@SuppressWarnings("unused") 
		public String descBanco;
		private ComboBancos(int idBanco,String descBanco) {
			this.idBanco = idBanco;
			this.descBanco= descBanco;
			 
		}
	}
	//dto de dos parametros para retornar el id de la chequera y la descripciï¿½n de la chequera
	private static class ComboChequeras {
		@SuppressWarnings("unused") 
		public String idChequera;
		@SuppressWarnings("unused") 
		public String descChequera;
		private ComboChequeras(String idChequera,String descChequera) {
			this.idChequera = idChequera;
			this.descChequera= descChequera;
			 
		}
	}
	
	/**
	 * Mï¿½todo para obtener los bancos segun el parametro siguiente
	 * @param pbInversion contiene la bandera de inversiones
	 * @return cmbBancos una lista de tipo ComboBancos
	 */
	@DirectMethod
	public List<ComboBancos> obtenerBancos(String pbInversion) {
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		boolean pbInversionBoolean=false;
		if (pbInversion.equals("true"))
			pbInversionBoolean=true;
		else 
			pbInversionBoolean=false;
		
		List<CatBancoDto> bancosList = new ArrayList<CatBancoDto>();
		List<ComboBancos> cmbBancos = new ArrayList<ComboBancos>();
		try{
			if (Utilerias.haveSession(WebContextManager.get())) {
			ImportaBancaElectronicaBusiness importaBancaElectronicaBusiness = (ImportaBancaElectronicaBusiness)contexto.obtenerBean("importaBancaElectronicaBusiness");
			bancosList = importaBancaElectronicaBusiness.obtenerBancosCombo(pbInversionBoolean);
			ListIterator<CatBancoDto> itr = bancosList.listIterator();
			
			while(itr.hasNext()){
				CatBancoDto bancoTmp = itr.next();
			    cmbBancos.add(new ComboBancos(bancoTmp.getIdBanco(),bancoTmp.getDescBanco()));
			}
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:BancaElectronica, C:ImportaBancaElectronicaAction, M:obtenerBancos");
		}
		return cmbBancos;
	}
	/**
	 * Metodo para obtener las chequeras segun los parametros siguientes
	 * @param noEmpresa
	 * @param idBanco
	 * @param pbTipoChequera
	 * @param pbInversion
	 * @return
	 */
	@DirectMethod
	public List<ComboChequeras> obtenerChequeras(String noEmpresa, String idBanco,String pbTipoChequera, String pbInversion) {
		List<ComboChequeras> cmbChequeras = new ArrayList<ComboChequeras>();
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		boolean pbInversionBoolean=false;
		boolean pbTipoChequeraBoolean=false;
		try{	
			if (Utilerias.haveSession(WebContextManager.get())) {
			if (pbInversion.substring(0).equals("true"))
				pbInversionBoolean=true;
			if (pbTipoChequera.substring(0).equals("true"))
				pbTipoChequeraBoolean=true;
			
			List<CatCtaBancoDto> cheqList = new ArrayList<CatCtaBancoDto>();
			CatCtaBancoDto dto=new CatCtaBancoDto();
			
			dto.setNoEmpresa(Integer.parseInt(noEmpresa));
			dto.setIdBanco(Integer.parseInt(idBanco));
			ImportaBancaElectronicaBusiness importaBancaElectronicaBusiness = (ImportaBancaElectronicaBusiness)contexto.obtenerBean("importaBancaElectronicaBusiness");
			cheqList = importaBancaElectronicaBusiness.obtenerChequeras(dto,pbTipoChequeraBoolean,pbInversionBoolean);
			ListIterator<CatCtaBancoDto> itr = cheqList.listIterator();
			
			while(itr.hasNext()){
				CatCtaBancoDto cheqTmp = itr.next();
				cmbChequeras.add(new ComboChequeras(cheqTmp.getIdChequera(),cheqTmp.getDescChequera()));
			}
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:BancaElectronica, C:ImportaBancaElectronicaAction, M:obtenerChequeras");
		}
		return cmbChequeras;
	}
	/**
	 * Mï¿½todo que obtiene las empresas por ahora es un combo pero debe ser segun el usuario 
	 * @return empresaList lista de tipo EmpresaDto
	 */
	@DirectMethod
	public List<EmpresaDto> obtenerEmpresas()
	{
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		List<EmpresaDto>empresaList=new ArrayList<EmpresaDto>();
		try{
			if (Utilerias.haveSession(WebContextManager.get())) {
			ImportaBancaElectronicaBusiness importaBancaElectronicaBusiness = (ImportaBancaElectronicaBusiness)contexto.obtenerBean("importaBancaElectronicaBusiness");
			empresaList= importaBancaElectronicaBusiness.obtenerEmpresas();
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:BancaElectronica, C:ImportaBancaElectronicaAction, M:obtenerEmpresas");
		}
		return empresaList;
	}
	/**
	 * Mï¿½todo para obtener los conceptos 
	 * @param bancoUno 
	 * @param banco 
	 * @param psTipoMovto
	 * @return
	 */
	@DirectMethod
	public List<EquivaleConceptoDto>obtenerConcepto(int bancoUno, int bancoDos, String psTipoMovto)
	{
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		List<EquivaleConceptoDto>equivaleList=new ArrayList<EquivaleConceptoDto>();
		try{
			if (Utilerias.haveSession(WebContextManager.get())) {
			ImportaBancaElectronicaBusiness importaBancaElectronicaBusiness = (ImportaBancaElectronicaBusiness)contexto.obtenerBean("importaBancaElectronicaBusiness");
			equivaleList=importaBancaElectronicaBusiness.obtenerConcepto(bancoUno, bancoDos, psTipoMovto);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:BancaElectronica, C:ImportaBancaElectronicaAction, M:obtenerConcepto");
		}
		return equivaleList;
	}
	@DirectMethod
	public List<ParamRetImpBancaDto> llenarGridImportaBancaE(String optCapturados, int noEmpresa, String tipoMovto, int idUsuario, String idBanco,
	        												 String psChequera, String pdMontoIni, String pdMontoFin, String psFechaValorIni,
	        												 String psFechaValorFin,String psConcepto,String lbTipoCh,String chkInversion) {
		ParamBusImpBancaDto dtoParamBus= new ParamBusImpBancaDto();
		List<ParamRetImpBancaDto> listRetImpBanca=new ArrayList<ParamRetImpBancaDto>();
		boolean optionCapturados=false;
		boolean checkInversion=false;
		boolean pbTipoChequera=false;
//		if (!Utilerias.haveSession(WebContextManager.get())) 
//			return null;
		
		try {
			System.out.println("llenarGridImportaBancaE action");
//			if (Utilerias.haveSession(WebContextManager.get())) {
			ImportaBancaElectronicaBusiness importaBancaElectronicaBusiness = (ImportaBancaElectronicaBusiness)contexto.obtenerBean("importaBancaElectronicaBusiness");
			
			if(optCapturados.equals("true"))
				optionCapturados=true;
			else
				optionCapturados=false;
			dtoParamBus.setOptCapturados(optionCapturados);
			dtoParamBus.setPlNoUsuario(idUsuario>0?idUsuario:0);
			dtoParamBus.setNoEmpresa(noEmpresa != 0 ? noEmpresa : 0);
			dtoParamBus.setPbTodasEmp(noEmpresa != 0 ? false : true);
			dtoParamBus.setPlBanco(idBanco.trim()!=null && !idBanco.trim().equals("")?Integer.parseInt(idBanco):0);
			
			if(lbTipoCh.equals("true"))
				pbTipoChequera=true;
			else
				pbTipoChequera=false;
			dtoParamBus.setPbTipoCheq(pbTipoChequera);			
			dtoParamBus.setPdMontoIni(pdMontoIni!=null && !pdMontoIni.equals("")?Double.parseDouble(pdMontoIni):0);
			dtoParamBus.setPdMontoFin(pdMontoFin!=null && !pdMontoFin.equals("")?Double.parseDouble(pdMontoFin):0);
			
			if(chkInversion!=null && !chkInversion.equals("")&& chkInversion.equals("true"))
				checkInversion=true;
			else
				checkInversion=false;
			dtoParamBus.setPbInversion(checkInversion);
			dtoParamBus.setPsChequera(psChequera!=null && !psChequera.equals("")?psChequera:"");
			dtoParamBus.setPsConcepto(psConcepto!=null && !psConcepto.equals("")?psConcepto:"");
			dtoParamBus.setPsTipoMovto(tipoMovto!=null && !tipoMovto.trim().equals("")?tipoMovto:"");
			dtoParamBus.setPsFechaValorIni(psFechaValorIni!=null && !psFechaValorIni.equals("")?psFechaValorIni:"");
			dtoParamBus.setPsFechaValorFin(psFechaValorFin!=null && !psFechaValorFin.equals("")?psFechaValorFin:"");
			
			listRetImpBanca = importaBancaElectronicaBusiness.consultarMovimientoBancaE(dtoParamBus);
			
			logger.info( "optCapturados:"+optionCapturados+"noEmpresa:"+dtoParamBus.getNoEmpresa()+"tipoMovto: "+tipoMovto+
					"usuario: "+ idUsuario+"idBanco: "+ dtoParamBus.getPlBanco()+"chequera: "+psChequera+"monto ini: "+ dtoParamBus.getPdMontoIni()+
					"monto fin: "+ dtoParamBus.getPdMontoFin()+"fecha ini: "+  psFechaValorIni+"fecha fin: "+
	         psFechaValorFin+"concepto: "+ psConcepto+"tipo check: "+ lbTipoCh+"chkInversion: "+ checkInversion);
//			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:BancaElectronica, C:ImportaBancaElectronicaAction, M:llenarGridImportaBancaE");
		}
		return listRetImpBanca;
	}
	/***
	 * 
	 * @param data este es una lista de todo el grid de consulta en un jSon
	 * @param tipoMovto este parametro trae el valor del option para saber el tipo de movimiento
	 * Se pasan los datos del grid a la clase ImportaBancaElectronicaBusiness
	 * @return retrona un mapa el cual puede traer los registros importados o
	 * un mensaje al usuario como: "no existe la chequera" o errores del generador o
	 * revividor
	 */
	@DirectMethod
	public Map<String, Object> obtenerBarridoGrid(String data, String tipoMovto) {
		Gson gson = new Gson();
		List<Map<String, String>> objParams = gson.fromJson(data, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		List<ParamRetImpBancaDto>listGrid=	new ArrayList<ParamRetImpBancaDto>();
		Map<String,Object> res= new HashMap<String,Object>();

		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		try{
		//	System.out.println("tamaño del json "+objParams.size());
			//if (Utilerias.haveSession(WebContextManager.get())) {
			ImportaBancaElectronicaBusiness importaBancaElectronicaBusiness = (ImportaBancaElectronicaBusiness)contexto.obtenerBean("importaBancaElectronicaBusiness");
			for(int i=0;i<objParams.size();i++)
			{
				ParamRetImpBancaDto dtoGrid=new ParamRetImpBancaDto();
				dtoGrid.setImporte(Double.parseDouble(objParams.get(i).get("importe")));
				dtoGrid.setReferencia(objParams.get(i).get("referencia"));
				dtoGrid.setSucursal(objParams.get(i).get("sucursal"));
				dtoGrid.setFolioBanco(objParams.get(i).get("folioBanco"));
				dtoGrid.setIdChequera(objParams.get(i).get("idChequera"));
				dtoGrid.setDescBanco(objParams.get(i).get("descBanco"));
				dtoGrid.setConcepto(objParams.get(i).get("concepto"));
				dtoGrid.setNoEmpresa(Integer.parseInt(objParams.get(i).get("noEmpresa")));
				dtoGrid.setNomEmpresa(objParams.get(i).get("nomEmpresa"));
				dtoGrid.setDescConcepto(objParams.get(i).get("descConcepto"));
				dtoGrid.setBBancaElect(objParams.get(i).get("bBancaElect"));
				dtoGrid.setTranspasoDeposito(objParams.get(i).get("transpasoDeposito"));
				dtoGrid.setFecValorString(objParams.get(i).get("fecValorString"));
				dtoGrid.setDescCorresponde(objParams.get(i).get("descCorresponde"));
				dtoGrid.setCorresponde(objParams.get(i).get("corresponde"));
				dtoGrid.setSIdentificador(objParams.get(i).get("sIdentificador"));
				dtoGrid.setSValores(objParams.get(i).get("sValores"));
				dtoGrid.setNoCliente(objParams.get(i).get("noCliente"));
				dtoGrid.setBeneficiario(objParams.get(i).get("beneficiario"));
			 	dtoGrid.setIdDivision(objParams.get(i).get("idDivision")!=null?Integer.parseInt(objParams.get(i).get("idDivision")):0);
			 	dtoGrid.setImporta(objParams.get(i).get("importa"));
			 	dtoGrid.setBRechazo(objParams.get(i).get("rechazo"));
			 	dtoGrid.setLiberaAut(objParams.get(i).get("liberaAut"));
				dtoGrid.setIdRubro(objParams.get(i).get("idRubro")!=""?Integer.parseInt(objParams.get(i).get("idRubro")):0);
				dtoGrid.setRestoReferencia(objParams.get(i).get("restoReferencia"));
				dtoGrid.setIdBanco(objParams.get(i).get("idBanco")!=""?Integer.parseInt(objParams.get(i).get("idBanco")):0);
				dtoGrid.setIdUsuario(Integer.parseInt(objParams.get(i).get("idUsuario")));
				dtoGrid.setNoCuentaEmp(Integer.parseInt(objParams.get(i).get("noCuentaEmp")));
				dtoGrid.setTipoMovto(tipoMovto);
				dtoGrid.setSecuencia(Integer.parseInt(objParams.get(i).get("secuencia")));
				//System.out.println("descrip "+objParams.get(i).get("descripcion"));
				dtoGrid.setDescripcion(objParams.get(i).get("descripcion"));
				dtoGrid.setIdCveConcepto(objParams.get(i).get("idCveConcepto"));
				dtoGrid.setCargoAbono(objParams.get(i).get("cargoAbono"));
				listGrid.add(dtoGrid);
			}
		//	System.out.println("tamaño de listGrid al pasar a business "+listGrid.size());
			res=importaBancaElectronicaBusiness.ejecutarImportaBancaElectronica(listGrid);
			//}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:BancaElectronica, C:ImportaBancaElectronicaAction, M:obtenerBarridoGrid");
			e.printStackTrace();
		}
		return res;
	}
	
	@DirectMethod
	public int cancelarMovimientos(String data) {
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return -99999;
		Gson gson = new Gson();
		List<Map<String, String>> objParams = gson.fromJson(data, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		int[] secuencia= new int[objParams.size()];
		int res=0;
		try{
			if (Utilerias.haveSession(WebContextManager.get())) {
			ImportaBancaElectronicaBusiness importaBancaElectronicaBusiness = (ImportaBancaElectronicaBusiness)contexto.obtenerBean("importaBancaElectronicaBusiness");
			for(int i=0;i<objParams.size();i++)
				secuencia[i]=Integer.parseInt(objParams.get(i).get("secuencia"));				
			
			res=importaBancaElectronicaBusiness.cancelarMovimientos(secuencia);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:BancaElectronica, C:ImportaBancaElectronicaAction, M:cancelarMovimientos");
			e.printStackTrace();
		}
		return res;
	}
}
