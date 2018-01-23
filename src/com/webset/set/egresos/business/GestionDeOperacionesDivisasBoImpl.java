package com.webset.set.egresos.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

import com.webset.set.bancaelectronica.dto.ParametroDto;
import com.webset.set.egresos.dao.impl.GestionDeOperacionesDivisasDAO;
import com.webset.set.egresos.dto.ConfirmacionCargoCtaDto;
import com.webset.set.egresos.dto.OperacionDivisaDTO;
import com.webset.set.egresos.dto.ResultadoDto;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utilerias.GlobalSingleton;
import com.webset.set.utilerias.dto.ComunEgresosDto;
import com.webset.set.utilerias.dto.GeneradorDto;
import com.webset.set.utilerias.dto.LlenaComboEmpresasDto;
import com.webset.set.utilerias.dto.StoreParamsComunDto;

public class GestionDeOperacionesDivisasBoImpl implements GestionDeOperacionesDivisasBo {
	
	private GlobalSingleton globalSingleton = new GlobalSingleton();
	private GestionDeOperacionesDivisasDAO gestionDeOperacionesDivisasDAO;
	private Funciones funciones = new Funciones();
	
	private PagosEnSAPBusiness pagosEnSAPBusiness;
	
	public PagosEnSAPBusiness getPagosEnSAPBusiness() {
		return pagosEnSAPBusiness;
	}



	public void setPagosEnSAPBusiness(PagosEnSAPBusiness pagosEnSAPBusiness) {
		this.pagosEnSAPBusiness = pagosEnSAPBusiness;
	}
	
	public GestionDeOperacionesDivisasDAO getGestionDeOperacionesDivisasDAO() {
		return gestionDeOperacionesDivisasDAO;
	}

	public void setGestionDeOperacionesDivisasDAO( GestionDeOperacionesDivisasDAO gestionDeOperacionesDivisasDAO) {
		this.gestionDeOperacionesDivisasDAO = gestionDeOperacionesDivisasDAO;
	}
		
	@Override
	public List<LlenaComboEmpresasDto> obtenerEmpresas(int idUsuario) {
		
		return gestionDeOperacionesDivisasDAO.obtenerEmpresas(idUsuario);
		
	}//END METHOD: obtenerEmpresas

	@Override
	public List<ConfirmacionCargoCtaDto> obtenerDivisa(int idUsuario, int noEmpresa) {
		
		return gestionDeOperacionesDivisasDAO.obtenerDivisa(idUsuario, noEmpresa);
		
	}//END METHOD:obtenerDivisa
	
	@Override
	public List<ConfirmacionCargoCtaDto> obtenerBanco(int noEmpresa,String idDivisa) {
		
		return gestionDeOperacionesDivisasDAO.obtenerBanco(noEmpresa, idDivisa);
		
	}//END METHOD: obtenerBancoVta
	
	@Override
	public List<ConfirmacionCargoCtaDto> obtenerChequera(int noEmpresa,String idDivisa, int idBanco ) {
		
		return gestionDeOperacionesDivisasDAO.obtenerChequera(noEmpresa,idDivisa,idBanco);
		
	}//END METHOD:obtenerBanco
	
	public List<ConfirmacionCargoCtaDto> obtenerCasaCambio () {
		
		return gestionDeOperacionesDivisasDAO.obtenerCasaCambio();
		
	}//END METHOD: obtenerCasaCambio
	
	@Override
	public List<ConfirmacionCargoCtaDto> obtenerBancoTerceros(int noCliente,String idDivisa) {
		
		return gestionDeOperacionesDivisasDAO.obtenerBancoTerceros(noCliente, idDivisa);
		
	}//END METHOD:obtenerBancoTerceros 
	
	@Override
	public List<ConfirmacionCargoCtaDto> obtenerChequeraTerceros (int noCliente,String idDivisa, int idBanco ) {
		
			return gestionDeOperacionesDivisasDAO.obtenerChequeraTerceros(noCliente, idDivisa,idBanco );
			
	}//END METHOD: obtenerChequeraTerceros
	
	@Override
	public List<ConfirmacionCargoCtaDto> obtenerGrupos( String idTipoMovto ) {
		
		return gestionDeOperacionesDivisasDAO.obtenerGrupos( idTipoMovto );
		
	}//END METHOD: obtenerGrupos
	
	@Override
	public List<ConfirmacionCargoCtaDto> obtenerRubro(int idGrupo) {
		
		return gestionDeOperacionesDivisasDAO.obtenerRubros(idGrupo);
		
	}//END METHOD:obtenerRubro 
	
	@Override
	public List<ConfirmacionCargoCtaDto> llenaComboFirmas() {
		
		return gestionDeOperacionesDivisasDAO.llenaComboFirmas();
		
	}//END METHOD: llenaComboFirmas
	
	@Override
	public List<ConfirmacionCargoCtaDto> obtenerOperador(int noCliente) {
		
		return gestionDeOperacionesDivisasDAO.obtenerOperador(noCliente);
		
	}//END METHOD: obtenerOperador
	
	@Override
	public ResultadoDto crearSolicitudesCVD(ConfirmacionCargoCtaDto dto) {
		
		int noFolioParam = 0;
		int noFolioParam1 = 0;
		int noFolioDocto = 0;
		//int num = 0;
		//int res = 0;
		int cuentaEmp = 0;
		int idCaja = 0;
		
		String resultado = "";   
	 	
	 	Map respGenerador = new HashMap();
	 	List<ConfirmacionCargoCtaDto> datosEmpresa = new ArrayList<ConfirmacionCargoCtaDto>();
	 	
	 	gestionDeOperacionesDivisasDAO.actualizarFolioReal("no_folio_param");
	 	noFolioParam = gestionDeOperacionesDivisasDAO.seleccionarFolioReal("no_folio_param");
	 	gestionDeOperacionesDivisasDAO.actualizarFolioReal("no_folio_docto");
		noFolioDocto = gestionDeOperacionesDivisasDAO.seleccionarFolioReal("no_folio_docto");
		datosEmpresa = gestionDeOperacionesDivisasDAO.obtenerCajaCuenta(dto.getNoEmpresa());
		
		dto.setNoDocto(String.valueOf(noFolioDocto ) );
		
		if(datosEmpresa.size() > 0) {
			cuentaEmp = Integer.parseInt(datosEmpresa.get(0).getNoCuenta());
			idCaja = datosEmpresa.get(0).getIdCaja();
		}
		
		if( dto.getConcepto().trim().equals( "" ) ){
			dto.setConcepto( "COMPRA VENTA DE DIVISAS" );
		}
		
		gestionDeOperacionesDivisasDAO.InsertAceptado(244, dto, noFolioParam, 3000, cuentaEmp, noFolioDocto, idCaja,
				dto.getConcepto().trim(), "0", "NULL", dto.getSolicita(), dto.getAutoriza(), 0, 0, noFolioParam, 0, 0, 0, false, 0, true, -1, dto.getReferencia(), "", 0, "", false, false);   
		
		noFolioParam1 = noFolioParam;
		
		gestionDeOperacionesDivisasDAO.actualizarFolioReal("no_folio_param");
		noFolioParam = gestionDeOperacionesDivisasDAO.seleccionarFolioReal("no_folio_param");
		
		gestionDeOperacionesDivisasDAO.InsertAceptado(245, dto, noFolioParam, 3001, cuentaEmp, noFolioDocto, idCaja,
				dto.getConcepto().trim(), "0", "NULL", dto.getSolicita(), dto.getAutoriza(), 0, 0, noFolioParam1, 0, 0, 0, false, 0, true, -1, dto.getReferencia(), "", 0, "", false, false);   
		
		gestionDeOperacionesDivisasDAO.guardarCartaCVD(dto); 
		
		gestionDeOperacionesDivisasDAO.actualizarFolioReal("no_folio_param");
		noFolioParam = gestionDeOperacionesDivisasDAO.seleccionarFolioReal("no_folio_param");
		
		dto.setIdGrupo( dto.getGrupoIngreso() );
		dto.setIdRubro( dto.getRubroIngreso() );
		
		dto.setIdBanco( dto.getBancoAbono()); 
		dto.setIdChequera( dto.getChequeraAbono() );
		
		dto.setIdBancoBenef(0);
		dto.setIdChequeraBenef(" "); 
		
		gestionDeOperacionesDivisasDAO.InsertAceptado(247, dto, noFolioParam, 1000, cuentaEmp, noFolioDocto, idCaja,
				dto.getConcepto().trim(), "0", "NULL", dto.getSolicita(), dto.getAutoriza(), 0, 0, noFolioParam1, 0, 0, 0, false, 0, true, -1, dto.getReferencia(), "", 0, "", false, false);   
		
		respGenerador = gestionDeOperacionesDivisasDAO.ejecutarGenerador(dto.getNoEmpresa(), noFolioParam1, 0, 0, 1, " ");
				
		if(!respGenerador.isEmpty() && Integer.parseInt(respGenerador.get("result").toString())!= 0) {
			
			return new ResultadoDto(false,"Error, en Generador " + respGenerador.get("result"),null);
			
		}
		else{
			
			return new ResultadoDto(true,"Se realizo correctamente la compra/venta de divisas", dto.getNoDocto());
			
		}
		
	}//END METHOD: ejecutar
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	@Override
	public List<OperacionDivisaDTO> traerOperacionesCompraDeTransferParaPago() {
		gestionDeOperacionesDivisasDAO.updateSesionOperacionesCompraDeTransferParaPago();
		return gestionDeOperacionesDivisasDAO.traerOperacionesCompraDeTransferParaPago();
		
	}//END METHOD: traerOperacionesCompraDeTransferParaPago

	@Override
	public List<OperacionDivisaDTO> traerOperacionesCompraVentaDeDivisasParaPago() {
		return gestionDeOperacionesDivisasDAO.traerOperacionesCompraVentaDeDivisasParaPago();
	}//END METHOD:traerOperacionesCompraVentaDeDivisasParaPago
	
	@Override
	public ResultadoDto autorizaOperacionesDivisas( String credencial, int noUsuario, String folios ){
		
		int resp = 0;
		
		boolean bUsuarioAutenticado = false;
		
		bUsuarioAutenticado = gestionDeOperacionesDivisasDAO.validarUsuarioAutenticado(noUsuario, funciones.encriptador(credencial) );

		if(!bUsuarioAutenticado) {
			return new ResultadoDto(false,"Error: La contraseña no es valida.", null );
		}
		
		folios = folios.substring(0, folios.length() -1 );
		
		try{
		
			resp = gestionDeOperacionesDivisasDAO.autorizaOperacionesDivisas(noUsuario, folios);
			
		}catch( Exception e ){
			e.printStackTrace();
			return new ResultadoDto(false,"Excepcion: En la autorización de operaciones. Reportarlo a su administrador.", null );			
		}
		
		if( resp == 0 ){
			return new ResultadoDto(false,"Error: En la autorización de operaciones. Reportarlo a su administrador.", null ); 
		}else{
			return new ResultadoDto(true,"Las operaciones fueron actualizadas correctamente.", null );
		}
		
	}//END METHOD: autorizaOperacionesDivisas

	
	@Override
	public ResultadoDto autorizaOperacionesTransfer( String credencial, int noUsuario, String folios ){
		
		int resp = 0;
		
		boolean bUsuarioAutenticado = false;
		
		bUsuarioAutenticado = gestionDeOperacionesDivisasDAO.validarUsuarioAutenticado(noUsuario, funciones.encriptador(credencial) );

		if(!bUsuarioAutenticado) {
			return new ResultadoDto(false,"Error: La contraseña no es valida.", null );
		}
		
		folios = folios.substring(0, folios.length() -1 );
		
		try{
		
			resp = gestionDeOperacionesDivisasDAO.autorizaOperacionesDivisas(noUsuario, folios);
			
		}catch( Exception e ){
			e.printStackTrace();
			return new ResultadoDto(false,"Excepcion: En la autorización de operaciones. Reportarlo a su administrador.", null );			
		}
		
		if( resp == 0 ){
			return new ResultadoDto(false,"Error: En la autorización de operaciones. Reportarlo a su administrador.", null ); 
		}else{
			return new ResultadoDto(true,"Las operaciones fueron actualizadas correctamente.", null );
		}
		
	}//END METHOD: autorizaOperacionesDivisas

	
	@Override
	public ResultadoDto pagarOperacionesDivisas(String bandera, int noUsuario, int noBanco, String idChequera, String folios ) {
		
		int noFolioDet = 0; 
		String foliosTmp = null; 
		
		StoreParamsComunDto dtoPagador= new StoreParamsComunDto();
		
		noFolioDet = gestionDeOperacionesDivisasDAO.traerFolioDetDivisas(folios, 3000);
		
		foliosTmp = noFolioDet + ",";
		
		dtoPagador.setParametro(bandera + "," + 0 + "," + noUsuario + ","	+ noBanco + "," + idChequera + ","+ foliosTmp + "#" );
		dtoPagador.setMensaje("iniciar pagador");
		dtoPagador.setResult(0);

		try
		{
			
			gestionDeOperacionesDivisasDAO.ejecutarPagador(dtoPagador);			
			gestionDeOperacionesDivisasDAO.actualizaEjecucionOperacionesDivisas(noUsuario, folios);
			
		}catch( Exception e ){
			e.printStackTrace(); 
			return new ResultadoDto(false, "Exception: Ocurrio un Error al pagar el folio " + folios +" Reportarlo a su administrador", null);
		}
		
		ParametroDto paramDto = null;
		
		try
		{
			
			paramDto = gestionDeOperacionesDivisasDAO.obtenerIngresoOperacionDeDivisa("0",folios);
			
			globalSingleton = GlobalSingleton.getInstancia();
			int iUsuario = globalSingleton.getUsuarioLoginDto().getIdUsuario();
			int iCaja = globalSingleton.getUsuarioLoginDto().getIdCaja();
			Date dFechaHoy = globalSingleton.getFechaHoy();
			
			gestionDeOperacionesDivisasDAO.actualizarFolioReal("no_folio_param");
			int iFolio = gestionDeOperacionesDivisasDAO.seleccionarFolioReal("no_folio_param");
			
		    paramDto.setNoFolioParam(iFolio);
            paramDto.setUsuarioAlta(iUsuario);
            paramDto.setFecValor(dFechaHoy);	            
            paramDto.setFecValorOriginal(dFechaHoy);	            
            paramDto.setFecOperacion(dFechaHoy);	            
            paramDto.setFecAlta(dFechaHoy);
            paramDto.setIdCaja(iCaja);
	            
            int iAfectados = 0;
            iAfectados = gestionDeOperacionesDivisasDAO.insertarParametro(paramDto);
            
            Map<String, Object> resGenerador = new  HashMap<String, Object>();
	            
            	GeneradorDto generadorDto = new GeneradorDto();
				generadorDto.setIdUsuario(iUsuario);
				generadorDto.setNomForma("GestionDeDivisas"); 
				generadorDto.setEmpresa(paramDto.getNoEmpresa());
				generadorDto.setFolParam(iFolio);
				generadorDto.setFolMovi(0);
				generadorDto.setFolDeta(0);				
				generadorDto.setMensajeResp("inicia generador");
				
				resGenerador = gestionDeOperacionesDivisasDAO.ejecutarGenerador2(generadorDto);
				Map<String, Object> mapRet = new HashMap<String, Object>();
	            
				if(funciones.convertirCadenaInteger(resGenerador.get("result").toString()) != 0)
				{
					if(funciones.convertirCadenaInteger(resGenerador.get("result").toString()) != 1053)
					{
						mapRet.put("msgUsuario","Error " + resGenerador.get("result") + " en Generador " + "no_folio_param = " + iFolio);
					}
					
				}

		}catch( Exception e ){
			e.printStackTrace(); 
			return new ResultadoDto(false, "Exception: Ocurrio un Error al pagar el folio " + folios +" Reportarlo a su administrador", null); 
		}
		
		return new ResultadoDto(true, "Se ejecuto correctamente la operacion", null); 
		
		
	}//END METHOD: pagarOperacionesDivisas
	
	
	@Override
	public ResultadoDto pagarOperacionesTransfer(String bandera, int noUsuario, int noBanco, String idChequera, String folios ) {
		
		int noFolioDet = 0; 
		
		StoreParamsComunDto dtoPagador= new StoreParamsComunDto();
			
		
		dtoPagador.setParametro(bandera + "," + 0 + "," + noUsuario + ","	+ noBanco + "," + idChequera + ","+ folios + "#" );
		dtoPagador.setMensaje("iniciar pagador");
		dtoPagador.setResult(0);

		try
		{
			
			gestionDeOperacionesDivisasDAO.ejecutarPagador(dtoPagador);			
			gestionDeOperacionesDivisasDAO.actualizaEjecucionOperacionesDivisas(noUsuario, folios);
			
		}catch( Exception e ){
			e.printStackTrace(); 
			return new ResultadoDto(false, "Exception: Ocurrio un Error al pagar el folio " + folios +" Reportarlo a su administrador", null);
		}		
		
		return new ResultadoDto(true, "Se ejecuto correctamente la operacion", null);
		
	}//END METHOD: pagarOperacionesDivisas
	
	@Override
	public Map<String,Object> pagarOperacionesTransfer( List< Map< String, String> > registrosList ) {
			
		List<ComunEgresosDto> listaPropuestas = null;
		String folios = getListaFoliosDetDePropuestas(registrosList);
		Map mapRetorno = null;
			
		try{
			
			int resultado = gestionDeOperacionesDivisasDAO.updateCancelSesionOperacionesCompraDeTransferParaPago(folios);
			
			listaPropuestas=getListaCvesControlDePropuesta(registrosList);			
			
			Map<String, Object> datosUsuario = globalSingleton.obtenerPropiedadesUsuario();
			
			gestionDeOperacionesDivisasDAO.actualizaEjecucionOperacionesDivisas((Integer)datosUsuario.get("idUsuario"), folios);
			
			String fecha = gestionDeOperacionesDivisasDAO.obtenerFechaHoyV2().toString();			
			mapRetorno = pagosEnSAPBusiness.compensaPropuestasSAP("", fecha , (Integer)datosUsuario.get("idUsuario"), true, false,listaPropuestas, false, 0);
			
		}catch( Exception e ){
			e.printStackTrace();
		}
		
		//return new ResultadoDto(true, "Se ejecuto correctamente la operacion", null);
		return mapRetorno;
		
	}//END METHOD: pagarOperacionesDivisas
	
	
	@Override
	public Map<String,Object> pagarOperacionesTransfer( String registros ) {
		
		Map mapRetorno = null;
		List<ComunEgresosDto> listaPropuestas = null;
		
		String folios = pagosEnSAPBusiness.getListaFoliosDetDePropuestas(registros);

		
		try{
			
			int resultado = gestionDeOperacionesDivisasDAO.updateCancelSesionOperacionesCompraDeTransferParaPago(folios);
			
			
			listaPropuestas=pagosEnSAPBusiness.getListaCvesControlDePropuesta(registros);
			
			
			Map<String, Object> datosUsuario = globalSingleton.obtenerPropiedadesUsuario();
			
			
			//gestionDeOperacionesDivisasDAO.actualizaEjecucionOperacionesDivisas((Integer)datosUsuario.get("idUsuario"), folios);
			
			String fechaHoy = gestionDeOperacionesDivisasDAO.obtenerFechaHoyV2();
			int noUsuario = (Integer)datosUsuario.get("idUsuario");
			
			mapRetorno = pagosEnSAPBusiness.compensaPropuestasSAP("",fechaHoy ,noUsuario , true, false, listaPropuestas, false, 0 ) ;
		
			
		}catch( Exception e ){
			e.printStackTrace();
		}
		
		return mapRetorno;
		
	}//END METHOD: pagarOperacionesDivisas
	
	private List<ComunEgresosDto> getListaCvesControlDePropuesta(List< Map< String, String> > registrosList ){
		
		Map<String, String> nuevoMapa = new HashMap<String,String>(); 
		
		for(Map< String, String> registro: registrosList ){
			
			nuevoMapa.put(registro.get("cveControl"), registro.get("cveControl"));		
		}	
		
		Set<String> nuevaLista = nuevoMapa.keySet();
		List<ComunEgresosDto> listaPropuestas = new ArrayList<ComunEgresosDto>();
		
		for( String cveControl : nuevaLista){
			ComunEgresosDto ced = new ComunEgresosDto();
			ced.setCveControl(cveControl);
			listaPropuestas.add( ced ) ;
		}
		
		return listaPropuestas;
	}

	
	private String getListaFoliosDetDePropuestas(List< Map< String, String> > registrosList ){
		
		StringBuilder sb = new StringBuilder();
		String folios = null;
		
		for(Map< String, String> registro: registrosList ){
			
			 sb.append( registro.get("folio") ).append(",");		
		}	
		
		folios = sb.toString();
		folios = folios.substring(0, folios.length()-1);
		
		return folios;
	}


	@Override
	public JRBeanCollectionDataSource operacionesVentaDeDivisasParaImprimir( String folios ) {
		
		folios = folios.substring( 0, folios.length()-1);
		
		return  new JRBeanCollectionDataSource(gestionDeOperacionesDivisasDAO.operacionesVentaDeDivisasParaImprimir(folios));
			
	}//END CLASS:
	
	@Override
	public JRBeanCollectionDataSource operacionesVentaDeTransferParaImprimir( String folios ) {
		
		folios = folios.substring( 0, folios.length()-1);
		
		return  new JRBeanCollectionDataSource(gestionDeOperacionesDivisasDAO.operacionesVentaDeTransferParaImprimir(folios));
			
	}//END CLASS: 
	
	@Override
	public ResultadoDto eliminarOperacionesDivisas(int noUsuario, String folios) {
		
		int    noFolioDet = 0; 
		String foliosTmp  = null; 
		
		noFolioDet = gestionDeOperacionesDivisasDAO.traerFolioDetDivisas(folios, 3000);
				
		gestionDeOperacionesDivisasDAO.eliminaOperacionesDivisas(noUsuario, noFolioDet);
		
		noFolioDet = gestionDeOperacionesDivisasDAO.traerFolioDetDivisasIngreso(folios, 1000);
		
		gestionDeOperacionesDivisasDAO.eliminaOperacionesDivisas(noUsuario, noFolioDet);
		
		gestionDeOperacionesDivisasDAO.eliminaEjecucionOperacionesDivisas(noUsuario, folios);
		
		return new ResultadoDto(true, "Se ejecuto correctamente la eliminacion.", null); 
		
	}//END METHOD: pagarOperacionesDivisas
	
	@Override
	public ResultadoDto eliminarOperacionesTransfer(int noUsuario, String folios) {
		
		int    noFolioDet = 0; 
		String foliosTmp  = null; 
		
		gestionDeOperacionesDivisasDAO.eliminaOperacionesTransfer( noUsuario, noFolioDet );
		
		gestionDeOperacionesDivisasDAO.eliminaEjecucionOperacionesDivisas( noUsuario, folios );
		
		return new ResultadoDto(true, "Se ejecuto correctamente la eliminacion.", null); 
		
	}//END METHOD: pagarOperacionesDivisas
	

	


	

}//END CLASS: 
