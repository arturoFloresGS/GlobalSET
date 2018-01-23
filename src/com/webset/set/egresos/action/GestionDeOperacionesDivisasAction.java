package com.webset.set.egresos.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.softwarementors.extjs.djn.config.annotations.DirectMethod;
import com.webset.set.egresos.business.GestionDeOperacionesDivisasBo;
import com.webset.set.egresos.dto.ConfirmacionCargoCtaDto;
import com.webset.set.egresos.dto.OperacionDivisaDTO;
import com.webset.set.egresos.dto.ResultadoDto;
import com.webset.set.egresos.service.CapturaSolicitudesPagoService;
import com.webset.set.egresos.service.ConfirmacionCargoCtaService;
import com.webset.set.utilerias.Contexto;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utilerias.dto.LlenaComboDivisaDto;
import com.webset.set.utilerias.dto.LlenaComboEmpresasDto;

public class GestionDeOperacionesDivisasAction {
	
	private static Logger logger = Logger.getLogger(GestionDeOperacionesDivisasAction.class);	
	
	private Contexto contexto = new  Contexto();	
	private Funciones funciones = new Funciones();
	
	
	private GestionDeOperacionesDivisasBo gestionDeOperacionesDivisasBo = (GestionDeOperacionesDivisasBo)contexto.obtenerBean("gestionDeOperacionesDivisasBo");
	
	/*
	 ********************************************************************************* 
	 *							COMBOS GENERALES 
	 *********************************************************************************
	 */
	
	@DirectMethod
	public List<LlenaComboEmpresasDto> obtenerEmpresas(int idUsuario)
	{		
		return gestionDeOperacionesDivisasBo.obtenerEmpresas(idUsuario);		
	}
	
	@DirectMethod
	public List<ConfirmacionCargoCtaDto> obtenerDivisaVta (int idUsuario, int noEmpresa) {
			
		return gestionDeOperacionesDivisasBo.obtenerDivisa(idUsuario, noEmpresa);
		
	}//END METHOD: obtenerDivisaVta
	
	@DirectMethod
	public List<ConfirmacionCargoCtaDto> obtenerBanco(int noEmpresa ,String idDivisa) {
		
		return gestionDeOperacionesDivisasBo.obtenerBanco(noEmpresa,idDivisa);
		
	}//END METHOD: obtenerBanco
	
	@DirectMethod
	public List<ConfirmacionCargoCtaDto> obtenerChequera( int noEmpresa, String idDivisa , int idBanco) {
		
		return gestionDeOperacionesDivisasBo.obtenerChequera(noEmpresa, idDivisa, idBanco); 
		
	}//END METHOD:obtenerChequera	
	
	@DirectMethod
	public List<ConfirmacionCargoCtaDto> obtenerCasaCambio() {

		return gestionDeOperacionesDivisasBo.obtenerCasaCambio();
		
	}//END METHOD: obtenerCasaCambio

	@DirectMethod
	public List<ConfirmacionCargoCtaDto> obtenerBancoTerceros (int noCliente ,String idDivisa) {
	
		return  gestionDeOperacionesDivisasBo.obtenerBancoTerceros(noCliente, idDivisa);
	
	}//END METHOD: obtenerBancoTerceros

	@DirectMethod
	public List<ConfirmacionCargoCtaDto> obtenerChequeraTerceros(int noCliente, String idDivisa , int idBanco) {

			return gestionDeOperacionesDivisasBo.obtenerChequeraTerceros(noCliente,idDivisa,idBanco);
			
	}//END METHOD: obtenerChequeraTotal
	
	@DirectMethod
	public List<ConfirmacionCargoCtaDto> obtenerOperador(int noCliente) {

			return gestionDeOperacionesDivisasBo.obtenerOperador(noCliente);

	}//END METHOD: obtenerOperador

	@DirectMethod
	public List<ConfirmacionCargoCtaDto> obtenerGrupos( String  idTipoMovto ) {
			
		return gestionDeOperacionesDivisasBo.obtenerGrupos( idTipoMovto );
			
	}//END METHOD:obtenerGrupos

	@DirectMethod
	public List<ConfirmacionCargoCtaDto> obtenerRubro( int idGrupo ) {
		
		return gestionDeOperacionesDivisasBo.obtenerRubro(idGrupo);
		
	}//END METHOD: obtenerRubro 
	
	@DirectMethod
	public List<ConfirmacionCargoCtaDto> llenaComboFirmas( ) {
			
		return gestionDeOperacionesDivisasBo.llenaComboFirmas();
		
	}//END METHOD:llenaComboFirmas 
	
	@DirectMethod
	public ResultadoDto validaCampos(int Cliente				, int usuario			 	, String folio, 
									 int noEmpresa				, String idDivisaVenta	 	, String idBancoCargo,
								 	 String chequeraCargo		, String idDivisaCompra 	, String idBancoAbono, 
								 	 String chequeraAbono		, String tipoDeCambio	 	, String idCasaDeCambio, 
								 	 String nomCasaDeCambio		, String idOperador	 		, String nomOperador,
								 	 String idGrupoEgreso		, String idBancoCasaCambio	, String fechaValor,
								 	 String idRubroEgreso		, String importeCompra		, String importeVenta, 
								 	 String chequeraCasaCambio	, String fechaLiquidacion   , int custodia, 
								 	 int idFormaPago			, String concepto			, String referencia, 
								 	 String idGrupoIngreso		, String idRubroIngreso		, String firma1, 
								 	 String firma2,
								 	 String descDivisaVenta		,String descDivisaCompra	, String descBancoCargo,	
								 	 String descBancoAbono		,String descBancoCasa		, String descGrupoEgreso,	
								 	 String descRubroEgreso){ 
	
		
		String msg 			= null;
		String fecHoy 		= null;
		String datosBoton 	= null;
		
		ConfirmacionCargoCtaDto dto = new ConfirmacionCargoCtaDto();
		
		fechaLiquidacion = fechaLiquidacion.substring(0,4) + "/" + fechaLiquidacion.substring(5,7) + "/" + fechaLiquidacion.substring(8,10);
		fechaValor       = fechaValor.substring(0,4)       + "/" + fechaValor.substring(5,7)       + "/" + fechaValor.substring(8,10);
		
		ConfirmacionCargoCtaService confirmacionCargoCtaService = (ConfirmacionCargoCtaService)contexto.obtenerBean("confirmacionCargoCtaBusinessImpl");
		fecHoy = confirmacionCargoCtaService.fecHoy();
		
		fecHoy = fecHoy.substring(0,4) +"/"+fecHoy.substring(5,7)+ "/"+ fecHoy.substring(8,10); 
		
		try {
			
			if( noEmpresa == 0 ){
				return  new ResultadoDto(false, "No ha seleccionado una Empresa", null);
			}
			
			if (idDivisaVenta.equals("")){  
				return  new ResultadoDto(false, "No ha seleccionado una Divisa de Venta", null);
		   	}
			
			if (idBancoCargo.equals("")){ 
				return  new ResultadoDto(false, "No ha seleccionado un Banco de Cargo", null);
		    }  
			
			if (chequeraCargo.equals("")){  
				return  new ResultadoDto(false, "No ha seleccionado una chequera de Cargo", null);
		    }
			
			if (idDivisaCompra.equals("")){  
				return  new ResultadoDto(false, "No ha seleccionado una Divisa de Compra", null);
            }
			
			if (idDivisaVenta == idDivisaCompra){  
				return  new ResultadoDto(false, "Divisa de Compra y Divisa de Venta son iguales", null);
		   	}
			
			if (idBancoAbono.equals("")){  
				return  new ResultadoDto(false, "No ha seleccionado un Banco de Abono", null);
	        }
			
			if (chequeraAbono.equals("")){  
				return  new ResultadoDto(false,  "No ha seleccionado una Chequera de Abono", null);
		    }		
			
			if (tipoDeCambio.equals( "" ) ){
				return  new ResultadoDto(false,  "No ha digitado el Tipo de Cambio", null);
			}
			
			if (importeCompra.equals("")){  
				return  new ResultadoDto(false,  "No ha digitado el Importe de Compra", null);
			}
			
			if ( importeVenta.equals("") ){
				return  new ResultadoDto(false,  "No ha digitado el Importe de Venta", null);
			}
			
			if (idCasaDeCambio.equals("")){  
				return  new ResultadoDto(false,  "No ha seleccionado la Casa de Cambio", null);
		    }
			
			if (idBancoCasaCambio.equals("")){
				return  new ResultadoDto(false,   "No ha seleccionado el Banco de la Casa de Cambio", null);    
		    } 
			
			if (chequeraCasaCambio.equals("")){  
				return  new ResultadoDto(false,  "No ha seleccionado la Chequera de la Casa de Cambio", null);
		    }
			
			if (idOperador.equals("")){
				return  new ResultadoDto(false,   "No ha seleccionado un Operador", null);    
		    } 
			
			if (fechaValor.equals("")){  
				return  new ResultadoDto(false, "No ha seleccionado la Fecha Valor", null);
			}
			
			if(fechaValor.compareTo(fecHoy) < 0){
				return  new ResultadoDto(false,  "Fecha Valor no valida, es anterior al dia de hoy", null);
			}

			if (fechaLiquidacion.equals("")){ 
				return  new ResultadoDto(false,   "No ha seleccionado la fecha de liquidación", null);
	        }
			
			if(fechaLiquidacion.compareTo(fecHoy) < 0){
				return  new ResultadoDto(false,  "Fecha Liquidación no valida, es anterior al dia de hoy", null);
			}
			
			if (fechaLiquidacion.compareTo(fechaValor) < 0){     
				return  new ResultadoDto(false,   "La Fecha de Liquidación debe ser mayor o igual a la Fecha Valor", null);
			}
			
			if (idGrupoEgreso.equals("")){  
				return  new ResultadoDto(false,   "No ha seleccionado un Grupo de Egreso.", null);
			}
			
			if (idRubroEgreso.equals("")){  
				return  new ResultadoDto(false,   "No ha seleccionado un Rubro de Egreso.", null);
			}
				
			if (idGrupoIngreso.equals("")){  
				return  new ResultadoDto(false,   "No ha seleccionado un Grupo de Ingreso.", null);
			}
			
			if (idRubroIngreso.equals("")){  
				return  new ResultadoDto(false,   "No ha seleccionado un Rubro de Ingreso.", null);
			}
			
			if (referencia.equals("")){  
				return  new ResultadoDto(false,   "No ha capturado la referencia.", null);
			}			
			if (firma1.equals("")){  
				return  new ResultadoDto(false,   "No ha seleccionado el Firmante 1.", null);
			}
			
			if (firma2.equals("")){  
				return  new ResultadoDto(false,   "No ha seleccionado el Firmante 2.", null);
			}
			
			dto.setUsuario(usuario);
	        dto.setSFolio(folio.toString());
	    	dto.setNoEmpresa(noEmpresa);
	    		    	
	    	
	    	dto.setDivisaVenta(idDivisaVenta.toString());	    	
	    	dto.setBancoAbono(funciones.convertirCadenaInteger(idBancoAbono));
	    	dto.setChequeraAbono(chequeraAbono.toString());
	    	
	    	dto.setDivisaCompra(idDivisaCompra.toString());
	    	dto.setBancoCargo(funciones.convertirCadenaInteger(idBancoCargo));
	    	dto.setChequeraCargo(chequeraCargo.toString());
	    	
	    	dto.setFechaValor(fechaValor.toString());
	    	dto.setFechaLiquidacion(fechaLiquidacion.toString());
	    	dto.setNomOperador(nomOperador.toString());
	    	  
	    	dto.setTipoCambio(funciones.convertirCadenaDouble(tipoDeCambio));
	    	dto.setImporteCompra(funciones.convertirCadenaDouble(importeCompra.replace(",", "")));
	    	dto.setImporteOriginal(funciones.convertirCadenaDouble(importeCompra.replace(",", "")));
	    	dto.setImporteVenta(funciones.convertirCadenaDouble(importeVenta.replace(",", "")));
	    	
	    	dto.setCustodia(custodia);
	    	dto.setIdFormaPago(idFormaPago);
	    	dto.setIdBanco(funciones.convertirCadenaInteger(idBancoCasaCambio));
	    	dto.setIdChequera(chequeraCasaCambio.toString());
	    	dto.setIdBancoBenef(funciones.convertirCadenaInteger(idBancoCasaCambio));
	    	dto.setIdChequeraBenef(chequeraCasaCambio.toString());	    	
	    	
	    	dto.setCliente(Cliente);
	    	dto.setIdCasaCambio(funciones.convertirCadenaInteger(idCasaDeCambio));
	    	dto.setDescCasaCambio(nomCasaDeCambio);
	    	
	    	dto.setIdOperador(Integer.parseInt(idOperador));
	    	
	    	dto.setIdGrupo(funciones.convertirCadenaInteger(idGrupoEgreso));	    	
	    	dto.setIdRubro(funciones.convertirCadenaInteger(idRubroEgreso));
	    	
	    	dto.setGrupoIngreso( funciones.convertirCadenaInteger( idGrupoIngreso) );
	    	dto.setRubroIngreso( funciones.convertirCadenaInteger( idRubroIngreso ) );
	    	
	    	dto.setReferencia( referencia );
	    	dto.setConcepto(concepto);	    	
	    	dto.setSolicita( firma1 ); 
	    	dto.setAutoriza( firma2 ); 	 
	    	
	    	dto.setDescDivisaVenta(descDivisaVenta);		
	    	dto.setDescDivisaCompra(descDivisaCompra);	
	    	dto.setDescBancoCargo(descBancoCargo);
		 	dto.setDescBancoAbono(descBancoAbono); 
		 	dto.setDescBancoCasa(descBancoCasa); 
		 	dto.setDescGrupoEgreso(descGrupoEgreso); 
		 	dto.setDescRubroEgreso(descRubroEgreso); 		
	    	
	    	return gestionDeOperacionesDivisasBo.crearSolicitudesCVD(dto); 
		    	
		    
		}catch(Exception e) {
			e.printStackTrace();
			return  new ResultadoDto(false,    "Ocurrio un error en el Sistema. Favor de Reportarlo", null);
		}
		
	}//END METHOD: validaCampos
	
	
	/*
	 ********************************************************************************* 
	 *					AUTORIZACION Y EJECUCION DE COMPRA DE TRANSFER 
	 *********************************************************************************
	 */
	
	@DirectMethod
	public List<OperacionDivisaDTO> traerOperacionesCompraDeTransferParaPago() {
		
		return gestionDeOperacionesDivisasBo.traerOperacionesCompraDeTransferParaPago();
		
	}//END METHOD: traerOperacionesCompraDeTransferParaPago
	
	/*
	 ********************************************************************************* 
	 *					AUTORIZACION Y EJECUCION DE OPERACIONES DE DIVISAS
	 *********************************************************************************
	 */
	
	@DirectMethod
	public List<OperacionDivisaDTO> traerOperacionesCompraVentaDeDivisasParaPago() {
		
		return gestionDeOperacionesDivisasBo.traerOperacionesCompraVentaDeDivisasParaPago();
		
	}//END METHOD:traerOperacionesCompraVentaDeDivisasParaPago
	
	@DirectMethod
	public ResultadoDto autorizaOperacionesDivisas( String credencial, int noUsuario, String folios ) {
		
		return gestionDeOperacionesDivisasBo.autorizaOperacionesDivisas( credencial,noUsuario, folios ); 
		
	}//END METHOD: traerOperacionesCompraVentaDeDivisasParaPago
	
	@DirectMethod
	public ResultadoDto autorizaOperacionesTransfer( String credencial, int noUsuario, String folios ) {
		
		return gestionDeOperacionesDivisasBo.autorizaOperacionesTransfer( credencial,noUsuario, folios ); 
		
	}//END METHOD: traerOperacionesCompraVentaDeDivisasParaPago
	
	
	
	@DirectMethod
	public ResultadoDto pagarOperacionesDivisas( String registros ) {
		
		Gson gson = new Gson();
		
		List< Map< String, String> > registrosList = gson.fromJson( registros, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		
		ResultadoDto resultado = null; 
		
		for( Map< String, String> registro: registrosList ){
			
			
			
			resultado = gestionDeOperacionesDivisasBo.pagarOperacionesDivisas( registro.get( "bandera" ),
																			   Integer.parseInt(registro.get( "noUsuario" )),
																			   Integer.parseInt(registro.get( "idBancoCargo" )) ,
																			   registro.get( "chequeraCargo" ),
																			   registro.get( "folio" )
																			  ); 
			
		}
		
		return resultado;   
		
	}//END METHOD: traerOperacionesCompraVentaDeDivisasParaPago
	
	@DirectMethod
	public ResultadoDto pagarOperacionesTransferAnt( String registros ) {
		
		Gson gson = new Gson();
		
		List< Map< String, String> > registrosList = gson.fromJson( registros, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		
		ResultadoDto resultado = null; 
		
		for( Map< String, String> registro: registrosList ){
			
			
			
			resultado = gestionDeOperacionesDivisasBo.pagarOperacionesTransfer( registro.get( "bandera" ),
																			   Integer.parseInt(registro.get( "noUsuario" )),
																			   Integer.parseInt(registro.get( "idBancoCargo" )) ,
																			   registro.get( "chequeraCargo" ),
																			   registro.get( "folio" )
																			  ); 
			
		}
		
		return resultado;   
		
	}//END METHOD: traerOperacionesCompraVentaDeDivisasParaPago
	
	@DirectMethod
	public Map<String,Object> pagarOperacionesTransferOriginal( String registros ) {
		
		Gson gson = new Gson();
		
		List< Map< String, String> > registrosList = gson.fromJson( registros, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		
		return gestionDeOperacionesDivisasBo.pagarOperacionesTransfer(registrosList);   
		
	}//END METHOD: traerOperacionesCompraVentaDeDivisasParaPago
	
	@DirectMethod
	public Map<String,Object> pagarOperacionesTransfer( String registros ) {
		
		return gestionDeOperacionesDivisasBo.pagarOperacionesTransfer(registros);   
		
	}//END METHOD: traerOperacionesCompraVentaDeDivisasParaPago
	
	@DirectMethod
	public ResultadoDto eliminarOperacionesDivisas( int noUsuario, String folios ) {
		
		try{
			
		
			folios = folios.substring(0, folios.length() -1 );		
			
			String[] folio = folios.split(",");
			
			ResultadoDto resultadoDto = null; 
			
			for( int i = 0; i < folio.length; i++ ){
				
				resultadoDto = gestionDeOperacionesDivisasBo.eliminarOperacionesDivisas( noUsuario, folio[i] );
				
			}
			
			return resultadoDto;
		
		}catch( Exception e ){
			return new ResultadoDto(false, "ERROR:En la eliminación. Reportelo a su Administrador.", null);
		}
		
				
	}//END METHOD: eliminarOperacionesDivisas
	
	@DirectMethod
	public ResultadoDto eliminarOperacionesTransfer( int noUsuario, String folios ) {
		
		try{
			
		
			folios = folios.substring(0, folios.length() -1 );		
			
			String[] folio = folios.split(",");
			
			ResultadoDto resultadoDto = null; 
			
			for( int i = 0; i < folio.length; i++ ){
				
				resultadoDto = gestionDeOperacionesDivisasBo.eliminarOperacionesTransfer( noUsuario, folio[i] );
				
			}
			
			return resultadoDto;
		
		}catch( Exception e ){
			return new ResultadoDto(false, "ERROR:En la eliminación. Reportelo a su Administrador.", null);
		}
		
				
	}//END METHOD: traerOperacionesCompraVentaDeDivisasParaPago
	
}//END CLASS: GestionDeOperacionesDivisasAction
