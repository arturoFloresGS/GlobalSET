package com.webset.set.inversiones.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRMapArrayDataSource;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.webset.set.inversiones.dao.BarridoInversionDao;
import com.webset.set.inversiones.dto.CanastaInversionesDto;
import com.webset.set.inversiones.middleware.service.BarridoInversionService;
import com.webset.set.layout.business.CreacionArchivosBusiness;
import com.webset.set.layout.business.LayoutBancomerBusiness;
import com.webset.set.layout.dto.ParametrosLayoutDto;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.ConstantesSet;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utilerias.dto.MovimientoDto;
import com.webset.set.utilerias.dto.StoreParamsComunDto;

public class BarridoInversionBusinessImpl implements BarridoInversionService {
	BarridoInversionDao barridoInversionDao;
	Funciones funciones = new Funciones();
	private Bitacora bitacora = new Bitacora();
	private LayoutBancomerBusiness layoutBancomerBusiness;
	private CreacionArchivosBusiness creacionArchivosBusiness;
	
	public BarridoInversionDao getBarridoInversionDao() {
		return barridoInversionDao;
	}

	public void setBarridoInversionDao(BarridoInversionDao barridoInversionDao) {
		this.barridoInversionDao = barridoInversionDao;
	}

	public LayoutBancomerBusiness getLayoutBancomerBusiness() {
		return layoutBancomerBusiness;
	}

	public void setLayoutBancomerBusiness(
			LayoutBancomerBusiness layoutBancomerBusiness) {
		this.layoutBancomerBusiness = layoutBancomerBusiness;
	}

	public CreacionArchivosBusiness getCreacionArchivosBusiness() {
		return creacionArchivosBusiness;
	}

	public void setCreacionArchivosBusiness(
			CreacionArchivosBusiness creacionArchivosBusiness) {
		this.creacionArchivosBusiness = creacionArchivosBusiness;
	}

	@Override
	public List<CanastaInversionesDto> empresasConcentradoras() {
		return barridoInversionDao.empresasConcentradoras();
	}

	@Override
	public List<CanastaInversionesDto> todosLosBancos(String idDivisa) {
		return barridoInversionDao.todosLosBancos(idDivisa);
	}

	@Override
	public List<CanastaInversionesDto> obtenerRegistros(String idDivisa, String noBanco) {
		return barridoInversionDao.obtenerRegistros(idDivisa, noBanco);
	}

	@Override
	public String insertarBarridos(String params) {
		String msg = "";
		int result = 0;
		Gson gson = new Gson();
		List<Map<String, String>> datosGrid = gson.fromJson(params, new TypeToken<ArrayList<Map<String, String>>> () {}.getType());
		Date fecHoy;
		
		try {
			fecHoy = barridoInversionDao.obtenerFechaHoy();
			
			for(int i=0; i<datosGrid.size(); i++) {
				result = barridoInversionDao.buscaRegistro(datosGrid, i, fecHoy);
				
				if(result == 0) {
					result = barridoInversionDao.insertarBarridos(datosGrid, i, fecHoy);
					
					if(result == 0) break;
				}
			}
			
			if(result != 0)
				msg = "Registros almacenados con exito";
			else
				msg = "Error en almacenar los registros, consulte con su administrador de sistemas";
			
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " "+ Bitacora.getStackTrace(e) + 
			"P:Inversiones, C:BarridoInversionBusinessImpl, M:insertarBarridos");
		}
		return msg;
	}
	
	
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	//++++++++++++++++++++++++++ PROCESO DE EJECUCION DE BARRIDO INVERSION +++++++++++++++++++++++++++
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	
	
	@Override
	public List<CanastaInversionesDto> obtenerSolicitudesBarrido(String idDivisa, String noBanco) {
		return barridoInversionDao.obtenerSolicitudesBarrido(idDivisa, noBanco);
	}

	@Override
	public String confirmaBarrido(String noSolicitudes, String opcion, String clave) {
		String msg = "";
		Gson gson = new Gson();
		List<Map<String, String>> datosGrid = gson.fromJson(noSolicitudes, new TypeToken<ArrayList<Map<String, String>>> () {}.getType());
		
		try {
			if(opcion.equals("AUT"))
				msg = this.autorizar(datosGrid, clave);
			else if(opcion.equals("DES"))
				msg = this.desAutorizar(datosGrid, clave);
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " "+ Bitacora.getStackTrace(e) + 
			"P:Inversiones, C:BarridoInversionBusinessImpl, M:confirmaBarrido");
		}
		return msg;
	}
	
	public String autorizar(List<Map<String, String>> datosGrid, String clave) {
		String msg = "";
		int result = 0;
		
		try {
			result = barridoInversionDao.validaFacultad(174);
			
			if(result != 1) return "No cuenta con la facultad para autorizar";
			
			if(!barridoInversionDao.validaPassword(funciones.encriptador(clave)))
				return "Contraseña incorrecta";
			
			for(int i=0; i<datosGrid.size(); i++) {
				result = barridoInversionDao.verificaBarridoAut(datosGrid, i);
				
				if(result != 0) return "El traspaso ya fue autorizado";
				
				result = barridoInversionDao.autorizaBarrido(datosGrid, i);
				if(result == 0) break;
			}
			
			if(result != 0)
				msg = "Registros autorizados con exito";
			else
				msg = "Error al autorizar un barrido, consulte con su administrador de sistemas";
			
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " "+ Bitacora.getStackTrace(e) + 
			"P:Inversiones, C:BarridoInversionBusinessImpl, M:autorizar");
		}
		return msg;
	}
	
	public String desAutorizar(List<Map<String, String>> datosGrid, String clave) {
		String msg = "";
		int result = 0;
		
		try {
			if(!barridoInversionDao.validaPassword(funciones.encriptador(clave)))
				return "Contraseña incorrecta";
			
			for(int i=0; i<datosGrid.size(); i++) {
				result = barridoInversionDao.verificaBarridoDes(datosGrid, i);
				
				if(result == 0) return "El traspaso no fue autorizado por usted";
				
				result = barridoInversionDao.desAutorizaBarrido(datosGrid, i);
				if(result == 0) break;
			}
			
			if(result != 0)
				msg = "Registros desautorizados con exito";
			else
				msg = "Error al desautorizar un barrido, consulte con su administrador de sistemas";
			
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " "+ Bitacora.getStackTrace(e) + 
			"P:Inversiones, C:BarridoInversionBusinessImpl, M:desAutorizar");
		}
		return msg;
	}

	@Override
	public String regresarBarrido(String noSolicitudes) {
		String msg = "";
		int result = 0;
		Gson gson = new Gson();
		List<Map<String, String>> datosGrid = gson.fromJson(noSolicitudes, new TypeToken<ArrayList<Map<String, String>>> () {}.getType());
		
		try {
			for(int i=0; i<datosGrid.size(); i++) {
				result = barridoInversionDao.verificaBarridoAut(datosGrid, i);
				
				if(result != 0) return "Primero tiene que desautorizar el traspaso";
				
				result = barridoInversionDao.regresarBarrido(datosGrid, i);
				if(result == 0) break;
			}
			
			if(result != 0)
				msg = "Registros cancelado con exito";
			else
				msg = "Error al cancelar un barrido, consulte con su administrador de sistemas";
			
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " "+ Bitacora.getStackTrace(e) + 
			"P:Inversiones, C:BarridoInversionBusinessImpl, M:regresarBarrido");
		}
		return msg;
	}
	
	@Override
	public String ejecutarBarridos(String noSolicitudes) {
		String msg = "";
		Map<String, Object> resp = new HashMap<String, Object>();
		Gson gson = new Gson();
		List<Map<String, String>> datosGrid = gson.fromJson(noSolicitudes, new TypeToken<ArrayList<Map<String, String>>> () {}.getType());
		StringBuffer noSolic = new StringBuffer();
		
		try {
			for(int i=0; i<datosGrid.size(); i++) {
				noSolic.append(datosGrid.get(i).get("noSolicitud"));
				if(i+1 != datosGrid.size()) noSolic.append(","); 
			}
			
			resp = barridoInversionDao.ejecutarBarridos(noSolic.toString());
			
			if(resp.get("result").toString().equals("0")) {
				generaArchivo();
				barridoInversionDao.cambiaEstCtrlBarrido(noSolic.toString());
			}
			if(resp.get("result").toString().equals("0"))
				resp = barridoInversionDao.armaCanastas();
			
			if(resp.get("result").toString().equals("0"))
				msg = "Registros procesados con exito";
			else
				msg = resp.get("mensaje").toString() + ", consulte con su administrador de sistemas";
			
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " "+ Bitacora.getStackTrace(e) + 
			"P:Inversiones, C:BarridoInversionBusinessImpl, M:ejecutarBarridos");
		}
		return msg;
	}
	
	public String generaArchivo(){
		String result = "";
		Map<String,Object> resultArchivo = new HashMap<String,Object>();
		Map pdResp= new HashMap();
		StoreParamsComunDto dtoTraspaso= new StoreParamsComunDto();
		
		try{
		
		/** variables*/
		int piTipo_Traspaso = 0;
		int pi_banco = 0;
		int liSecuencia;
		int pl_operacion = 0;
		int plEmpresa = 0;
		int plLoteEntrada = 0;
		int piNumBanco = 0;
		int ll_empresa = 0;
		int importeParcial = -1;
		int liNoDocto = 0;
		double ps_total;
		double suma =0;
		boolean internacional = false;
		boolean optCitibankFlatFile = false;
		boolean pbEncontre3809 = false;
		boolean lbEnvioBitalH2H = false; //su valor viene del js
		boolean cambio_divisa_pago = false; //esta variable es en realidad una funcion(pendiente)
		boolean pb_layout_comerica = false;
		boolean lb_abierto_banamex = false;
		boolean lb_abierto_inbursa = false;
		boolean lb_abierto_santander = false;
		boolean lb_abierto_bancomer = false;
		boolean lb_abierto_bital = false;
		boolean lb_abierto_COMERICA = false;
		boolean lb_abierto_citibank_dls = false;
		boolean lb_abierto_bankboston = false;
		boolean lb_abierto_azteca = false; //'ACM 15/06/06
		boolean lb_abierto_banorte = false; //'FZV 13/08/2007
		boolean lb_abierto_inverlat = false; //'FZV 17/08/2007
		boolean lb_abierto_archivo = false;
		boolean lbBanca = false;
		boolean lbBanamex_MassPayment = false;
		boolean pbEscribi = false;
		boolean pb_mensaje_fondeo;
		boolean pb_negativo = false;
		boolean pbMismoBanco = false;
		boolean bCambioDivisa = false;
		boolean pbAgrupa;
		boolean pbPagoParcial = false;
		boolean pbBE_Bital = false;
		String psDirectorio = "";
		String ps_Archivo = "";
		String psFoliosRep = "";
		String psFoliosNeg = "";
//		String ps_total;
		String pl_docto = "";
		String Ubicacion = "IUSA";
		String lsInstancia = "";
		String psFolios = "";
		String psRegistro = "";
		String sPropu1 = "";
	    String sPropu2 = "";
	    String psFoliosTrasp = "";
	    String psFolioGroup = "";
		List<MovimientoDto> list = new ArrayList<MovimientoDto>();
		Map<String, Object> mapRespExportaReg = new HashMap<String, Object>();
		
		boolean mismoBanco = false;
		
		String chequera = "";
		
		String dirPrincipal = barridoInversionDao.configuraSet(3012);
		
		if(dirPrincipal.equals(""))
			return "No se encontró la ruta para el envio en configura_set(3012)";
	
		List<MovimientoDto> movimientos = new ArrayList<MovimientoDto>();
		
		movimientos = barridoInversionDao.buscaMovimientosTraspasos();
		
		if(movimientos.size() != 0) {
			for(int i=0; i<movimientos.size(); i++) {
				if(pi_banco != movimientos.get(i).getIdBanco()) {
					if(movimientos.get(i).getIdBanco() == movimientos.get(i).getIdBancoBenef()) {
						mismoBanco = true;
						piTipo_Traspaso = 2;
					}else {
						mismoBanco = false;
						piTipo_Traspaso = 1;
					}
					
					switch(movimientos.get(i).getIdBanco()) {
		                case ConstantesSet.SANTANDER:     //'SANTANDER
		                	resultArchivo = creacionArchivosBusiness.generarNombreArchivo("santander", "arch_santander", "st",
		                			mismoBanco, false, false, 0,2);
							
		                	if(!resultArchivo.isEmpty()) {
								if(resultArchivo.get("msgUsuario") != null) {
									return resultArchivo.get("msgUsuario").toString();
								}
							}
		            	break;
		                case ConstantesSet.BANCOMER:
		                    if(mismoBanco) {
		                    	resultArchivo = creacionArchivosBusiness.generarNombreArchivo("bancomer", "arch_bancomer", "bc",
		                    			mismoBanco, false, false, 0,2);
		    					if(!resultArchivo.isEmpty()) {
		    						if(resultArchivo.get("msgUsuario") != null) {
		    							return resultArchivo.get("msgUsuario").toString();
		    						}
		    					}
		                    }else if(false) {
		                    	resultArchivo = creacionArchivosBusiness.generarNombreArchivo("bancomer_internacional", "arch_bancomer", "bc",
		                    			mismoBanco, false, false, 0,2);
		    					if(!resultArchivo.isEmpty()) {
		    						if(resultArchivo.get("msgUsuario") != null) {
		    							return resultArchivo.get("msgUsuario").toString();
		    						}
		    					}
		                    }else {
		                    	resultArchivo = creacionArchivosBusiness.generarNombreArchivo("bancomer", "arch_bancomer", "bc",
		                    			mismoBanco, false, false, 0,2);
		    					if(!resultArchivo.isEmpty()) {
		    						if(resultArchivo.get("msgUsuario") != null) {
		    							return resultArchivo.get("msgUsuario").toString();
		    						}
		    					}
		                    }
		                    break;
		                default:
		                    lbBanca = false;
		                	return "Este banco no tiene Layout de Banca Electronica";
		        	}
				}
		        lb_abierto_banamex = false;
		        lb_abierto_inbursa = false;
		        lb_abierto_santander = false;
		        lb_abierto_bancomer = false;
		        lb_abierto_bital = false;
		        lb_abierto_COMERICA = false;
		        lb_abierto_citibank_dls = false;
		        lb_abierto_bankboston = false;
		        lb_abierto_azteca = false; 
		        lb_abierto_banorte = false; 
		        lb_abierto_inverlat = false; 
		        lb_abierto_archivo = false;
		        
		        ps_total = 0;
		        pbEscribi = false;
		        pb_mensaje_fondeo = false;
		        psFoliosRep = "";
		        liSecuencia = 0;
	            liSecuencia = liSecuencia + 1;
        		   
            	plEmpresa = movimientos.get(i).getNoEmpresa();
				plLoteEntrada = movimientos.get(i).getLoteEntrada() < 0 ? 0 : movimientos.get(0).getLoteEntrada();
                
				psFolios = psFolios + movimientos.get(i).getNoFolioDet() + ",";
				
				ps_total = ps_total + Double.parseDouble(funciones.ponerFormatoCeros(movimientos.get(i).getImporte(), 4));
				
				switch (movimientos.get(i).getIdBanco()) {
					case ConstantesSet.SANTANDER:
						mapRespExportaReg = layoutBancomerBusiness.beExportacion(movimientos, i, piTipo_Traspaso,
								resultArchivo.get("nomArchivo").toString(), dirPrincipal, movimientos.get(i).getIdBanco(), "TRANSFERENCIA");
						
						if(!mapRespExportaReg.get("msgRegistro").equals("")) {
//	        				if(!creacionArchivosBusiness.escribirArchivoLayout(dirPrincipal, resultArchivo.get("psBanco").toString(),
//	        						resultArchivo.get("nomArchivo").toString(), mapRespExportaReg.get("msgRegistro").toString())) {
//	        					
//	        					return "Error al Generar Archivo " + resultArchivo.get("psBanco").toString();
//							}
						}else {
							result = mapRespExportaReg.get("msgUsuario").toString();
						}
						pbEscribi = true;
					break;
					case ConstantesSet.BANCOMER:
						if(mismoBanco) {
							/*for(int y=0; y<traspasosIguales.size(); y++) {
								if(traspasosIguales.get(y).get("chequera").equals(parametroDto.get(i).getIdChequera()) &&
										traspasosIguales.get(y).get("chequeraB").equals(parametroDto.get(i).getIdChequeraBenef()) &&
										!parametroDto.get(i).isBSumImp()) {
									double imp = Double.parseDouble(traspasosIguales.get(y).get("importeTot"));
									parametroDto.get(i).setImporte(imp);
									break;
								}
							}*/
							
							//Esta llamada es para el archivo de Net CashC43
        					psRegistro = layoutBancomerBusiness.beArmaBancomerC43(obtenerParametrosLayout(movimientos, i, mismoBanco));//Arma la cadena
        					
							if(psRegistro!=null && !psRegistro.equals("")) {
								
								//if(!parametroDto.get(i).isBSumImp()) {
								
//            						if(!creacionArchivosBusiness.escribirArchivoLayout(dirPrincipal,resultArchivo.get("psBanco").toString(),
//            								resultArchivo.get("nomArchivo").toString(),psRegistro)) {
//            							result = "Error al generar archivo de Bancomer!";
//            						}else {
//            							//result.put("msgUsuario","Los Registros han sido Guardados en el archivo "+ dirPrincipal+resultArchivo.get("psBanco").toString()+resultArchivo.get("nomArchivo").toString());
//            							lb_abierto_bancomer = true;
//            						}
								pbEscribi = true;
        					}
						}
						else {
							//Este es para net cash
                            psRegistro = layoutBancomerBusiness.beArmaBancomerC43(obtenerParametrosLayout(movimientos, i, mismoBanco));//Arma la cadena
                            
                            if(psRegistro!=null && !psRegistro.equals("")) {
//        						if(!creacionArchivosBusiness.escribirArchivoLayout(dirPrincipal,resultArchivo.get("psBanco").toString(),
//        								resultArchivo.get("nomArchivo").toString(),psRegistro)) {
//        							result = "Error al generar archivo de Bancomer!";
//        						}else {
//        							lb_abierto_bancomer = true;
//        						}
        						pbEscribi = true;	
        					}
						}
					break;
				}
				pi_banco = movimientos.get(i).getIdBanco();
			}
		}
//	continuar_proceso:

	        if(!psFolios.equals(""))
	        {
	            psFolios = psFolios.substring(0, psFolios.length() - 1);
	        }

	        if(!psFoliosRep.equals(""))
	        {
	        	psFoliosRep = psFoliosRep.substring(0, psFoliosRep.length() - 1);
	        	result = "Partidas duplicadas en los folios: " + psFoliosRep;
	        }

	            
//	            Call funCerrar_archivo 'cierra el archivo
	        	if(pbEscribi == false) //'si no escribió ningun registro se sale
	        	{
	        		result = "No se escribio ningun arhivo";
	        	}
	        	if(psFolios.trim().length() > 0)
	        	{
//	                'PROCEDIMIENTO PARA GUARDAR EN LAS TABLAS DE CONTROL DE ARCHIVOS
	        		if(!psFolioGroup.equals(""))
	        		{
	                    psFolios = psFolioGroup;
	        		}
	        		//System.out.println(resultArchivo.get("nomArchivo").toString() + " nombre del archivo");
	        		result = resultArchivo.get("nomArchivo").toString();
	        		if(guardarArchivo(resultArchivo.get("nomArchivo").toString(),psFolios, pbPagoParcial, 0,
	        				2, pi_banco, false,
	        				mismoBanco, false))
	        		{
	        			//System.out.println("llego hasta antes del archivo");
	        			result = "Los Registros han sido Guardados en el archivo " + dirPrincipal +resultArchivo.get("psBanco").toString() + resultArchivo.get("nomArchivo").toString();
	        		}
	        	}
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Traspasos, C:TraspaosBusiness, M:aceptar-ejecutarSolicitudTraspaso");
		}
		return result;
	}

	public boolean guardarArchivo(String archivo, String folios, boolean pagoParcial, double total, 
			int usuario, int banco, boolean opCiti, boolean mBanco, boolean pbMasspayment){
		boolean guardaArchivo = true;
		int lAfectados = 0;
		int piRegistros = 0;
		int actualizado = 0;
		double pdImporte = 0;
		String psNomArchivo = "";
		String psTipoEnvio = "";
		List<MovimientoDto>listReg = new ArrayList<MovimientoDto>();
		MovimientoDto regDto = new MovimientoDto();
		
		listReg = barridoInversionDao.seleccionarDetalleArchivo(folios);
		
		if(listReg.size() > 0){
			for(int i = 0; i < listReg.size(); i++) {
				pdImporte = listReg.get(i).getImporte();
	            
//	            ' Agrega la funcionalidad de Traspaso Parcial
	            archivo = archivo.substring(0, 8);
	            regDto.setNoFolioDet(listReg.get(i).getNoFolioDet());
	            regDto.setNoDocto(listReg.get(i).getNoDocto());
	            regDto.setFecValor(barridoInversionDao.obtenerFechaHoy());
	            regDto.setIdChequera(listReg.get(i).getIdChequera());
	            regDto.setIdBanco(listReg.get(i).getIdBanco());
	            regDto.setIdBancoBenef(listReg.get(i).getIdBancoBenef());
	            regDto.setIdChequeraBenef(listReg.get(i).getIdChequeraBenef());
	            regDto.setInstFinan(listReg.get(i).getInstFinan());
	            regDto.setImporte(pdImporte);
	            regDto.setBeneficiario(listReg.get(i).getBeneficiario());
	            regDto.setSucursal(listReg.get(i).getSucursal() <= 0 ? 0 : listReg.get(i).getPlaza());
	            regDto.setPlaza(01001);
	            regDto.setConcepto(listReg.get(i).getConcepto());
	            lAfectados = barridoInversionDao.insertarDetArchTransfer(regDto, archivo);
	            piRegistros = piRegistros + 1;
			}
	        lAfectados = barridoInversionDao.insertarArchTransfer(archivo, banco, barridoInversionDao.obtenerFechaHoy(), total, piRegistros, usuario);
		}else {
			guardaArchivo = false;
		}
		
//	    Insertar en movimiento  el tipo de envio
		if(banco == ConstantesSet.CITIBANK_DLS && opCiti == true)
	        psTipoEnvio = "ACH";
	    else if(mBanco == true)
	        psTipoEnvio = "MB";
	    else
	        psTipoEnvio = "IB";
	   
	    psNomArchivo = archivo.substring(0, 8);
	    actualizado = barridoInversionDao.actualizarMovimientoTipoEnvio(psTipoEnvio, psNomArchivo, pbMasspayment);
	    
	    return guardaArchivo;
	}

	public ParametrosLayoutDto obtenerParametrosLayout(List<MovimientoDto> list, int i, boolean mismoBanco)
	{
		String pBenef = list.get(i).getPlazaBenef() != null ? list.get(i).getPlazaBenef() : "0";
		int plazaBenef = !pBenef.equals("") ? Integer.parseInt(pBenef) : 0;
		
		try{
			ParametrosLayoutDto paramLayDto = new ParametrosLayoutDto();
			
			paramLayDto.setMismoBanco(mismoBanco);
			paramLayDto.setIdChequeraBenef(list.get(i).getIdChequeraBenef());
			paramLayDto.setPiPlaza(list.get(i).getPlaza());
			paramLayDto.setPiPlazaBenef(plazaBenef);
			paramLayDto.setIdChequera(list.get(i).getIdChequera());
			paramLayDto.setImporte(list.get(i).getImporte());
			paramLayDto.setBeneficiario(list.get(i).getBeneficiario());
			paramLayDto.setIdBancoBenef(list.get(i).getIdBancoBenef()>0 ? ""+list.get(i).getIdBancoBenef() : "");
			paramLayDto.setNoFolioDet(list.get(i).getNoFolioDet());
			paramLayDto.setPsSucursal("");
			paramLayDto.setPsDivisa(list.get(i).getIdDivisa());
			paramLayDto.setPsReferenciaTraspaso("");
			paramLayDto.setNoCliente(list.get(i).getNoCliente()!=null?Integer.parseInt(list.get(i).getNoCliente()):0);
			paramLayDto.setPsClabe(list.get(i).getClabeBenef());
			paramLayDto.setConcepto(list.get(i).getConcepto());
			
			return paramLayDto;
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+"P:Traspasos, C:TraspasosBusiness, M:parametrosLayout");
			return null;
		}
		
	}
	
	
	
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	//++++++++++++++++++++++++++++++        MONITOR DE INVERSION        ++++++++++++++++++++++++++++++
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	
	
	@Override
	public List<CanastaInversionesDto> obtenerCanastasInv(String noEmpresa, String idDivisa) {
		return barridoInversionDao.obtenerCanastasInv(noEmpresa, idDivisa);
	}

	@Override
	public String crearNodosArbol(String noEmpresa, String idDivisa) {
		StringBuffer nodos = new StringBuffer();
		StringBuffer resNodos = new StringBuffer();
		List<CanastaInversionesDto> listNodosRaiz = new ArrayList<CanastaInversionesDto>();
		List<CanastaInversionesDto> listNodos = new ArrayList<CanastaInversionesDto>();
		List<CanastaInversionesDto> listNodosHijos = new ArrayList<CanastaInversionesDto>();
		String raiz;
		
		try {
			listNodosRaiz = barridoInversionDao.crearNodosRaizArbol(noEmpresa, idDivisa);
			
			if(listNodosRaiz.size() == 0) return "";
			
			raiz = "'" + listNodosRaiz.get(0).getNomEmpresa() + "  |  " + listNodosRaiz.get(0).getImporte() + "  |  " + 
					listNodosRaiz.get(0).getInteres() + "  |  " + listNodosRaiz.get(0).getIsr() + "'";
			
			listNodos = barridoInversionDao.crearNodosArbol(noEmpresa, idDivisa);
			
			nodos.append("[");
			
			for(int i=0; i<listNodos.size(); i++) {
				if(i != 0) nodos.append(", ");
				
				nodos.append("{id:" + "'" + listNodos.get(i).getNoCanasta() + "C'");
				nodos.append(", text:" + "'" + listNodos.get(i).getCanasta() + "  |  " + listNodos.get(i).getImporte() + "  |  " + 
										listNodos.get(i).getInteres() + "  |  " + listNodos.get(i).getIsr() + "  |  " + 
										listNodos.get(i).getFechaVence() + "'");
				nodos.append(", singleClickExpand: true");
				
				listNodosHijos = barridoInversionDao.crearNodosHijosArbol(listNodos.get(i).getNoCanasta());
				
				nodos.append(", children: [");
				
				for(int x=0; x<listNodosHijos.size(); x++) {
					if(x != 0) nodos.append(", ");
					
					nodos.append("{id:" + "'" + listNodosHijos.get(x).getNoOrden() + "O'");
					nodos.append(", text: " + "'" + listNodosHijos.get(x).getNomEmpresa() + "'");
					nodos.append(", singleClickExpand: true");
					nodos.append(", children: [");
					
					nodos.append("{id:" + "'" + listNodosHijos.get(x).getNoOrden() + "OB'");
					nodos.append(", text: " + "'" + listNodosHijos.get(x).getNomBanco() + "  |  " + listNodosHijos.get(x).getIdChequera() + "  |  " + 
							listNodosHijos.get(x).getImporte() + "  |  " + listNodosHijos.get(x).getInteres() + "  |  " + listNodosHijos.get(x).getIsr() + "'");
					nodos.append(", singleClickExpand: false");
					nodos.append(", leaf: true}]}");
				}
				nodos.append(" ]");
				nodos.append(" }");
			}
			nodos.append(" ]");
			resNodos.append("{raiz: " + raiz + ",nodos: " + nodos + "}");
			
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " "+ Bitacora.getStackTrace(e) + 
			"P:Inversiones, C:BarridoInversionBusinessImpl, M:crearNodosArbol");
		}
		return resNodos.toString();
	}

	@Override
	public String vencimientoCanastas(String param, int noEmpresa, String tasaReal) {
		String msg = "";
		Map<String, Object> resp = new HashMap<String, Object>();
		Gson gson = new Gson();
		List<Map<String, String>> datosGrid = gson.fromJson(param, new TypeToken<ArrayList<Map<String, String>>> () {}.getType());
		int noCanasta = Integer.parseInt(datosGrid.get(0).get("noCanasta"));
		
		try {
			//Actualiza tasa, interes e isr en tabla inversion
			barridoInversionDao.actualizaTasaReal(noCanasta, tasaReal);
			
			//Actualiza interes e isr en tabla canasta_inversiones
			barridoInversionDao.actualizaCanastaIntIsr(noCanasta);
			
			resp = barridoInversionDao.vencimientoCanastas(noCanasta, noEmpresa);
			
			if(resp.get("result").toString().equals("0"))
				resp = barridoInversionDao.regresoCanastas(noCanasta, noEmpresa);
			
			if(resp.get("result").toString().equals("0"))
				msg = "Registros procesados con exito";
			else
				msg = resp.get("mensaje").toString() + ", consulte con su administrador de sistemas";
			
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " "+ Bitacora.getStackTrace(e) + 
			"P:Inversiones, C:BarridoInversionBusinessImpl, M:ejecutarBarridos");
		}
		return msg;
	}

	@Override
	public String clasificacionCanastas(int noCanasta, int noEmpresa, String codigo) {
		String msg = "";
		int result = 0;
		List<CanastaInversionesDto> listDet = new ArrayList<CanastaInversionesDto>();
		String codigoDet = "";
		
		try {
			//Actualiza la canasta de inversion con el codigo de confirmacion del banco
			result = barridoInversionDao.actualizaCanastaCodigo(noCanasta, codigo.trim());
			
			if(result == 0) return "Error al actualizar el código a la canasta";
			
			listDet = barridoInversionDao.detalleEmpresas(noCanasta);
			
			if(listDet.size() > 0) {
				for(int i=0; i<listDet.size(); i++) {
					codigoDet = codigo.trim() + "-" + listDet.get(i).getNoEmpresa() + "-" + listDet.get(i).getNoOrden();
					
					//Actualiza el detalle que conforma la canasta con el codigo del banco mas un consecutivo
					result = barridoInversionDao.actualizaDetalleCanastacodigo(listDet.get(i).getNoOrden(), codigoDet);
				}
				
				if(result != 0)
					msg = "Registros clasificados con exito";
				else
					msg = "Error al actualizar el detalle, consulte con su administrador de sistemas";
			}
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " "+ Bitacora.getStackTrace(e) + 
			"P:Inversiones, C:BarridoInversionBusinessImpl, M:clasificacionCanastas");
		}
		return msg;
	}

	@Override
	public List<CanastaInversionesDto> consultarDetalleCanasta(int canasta) {
		return barridoInversionDao.consultarDetalleCanasta(canasta);
	}

	@SuppressWarnings("unchecked")
	@Override
	public JRDataSource reportesCanasta(Map parametros) {
		JRMapArrayDataSource jrDataSource = null;
		List<Map<String, Object>> listReport = new ArrayList<Map<String, Object>>();
		String nomReporte;
		
		try {
			nomReporte = parametros.get("nomReporte").toString();
			
			if(nomReporte.equals("Barridos"))
				listReport = barridoInversionDao.reportesBarridos(parametros);
			else if(nomReporte.equals("CanastasPendientes"))
				listReport = barridoInversionDao.reporteCanastasPendientes(parametros);			
			else if(nomReporte.equals("CanastasVencimientos"))
				listReport = barridoInversionDao.reporteCanastasVencimientos(parametros);			
			else if(nomReporte.equals("InversionesDetalladas"))
				listReport = barridoInversionDao.InversionesDetalladas(parametros);
			else if(nomReporte.equals("CanastasVencenHoy"))
				listReport = barridoInversionDao.reporteCanastasVencenHoy(parametros);
			
			jrDataSource = new JRMapArrayDataSource(listReport.toArray());
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " "+ Bitacora.getStackTrace(e) + 
			"P:Inversiones, C:BarridoInversionBusinessImpl, M:reportesCanasta");
		}
		return jrDataSource;
	}
	
	
	
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	//++++++++++++++++++++++++++       TASAS Y COMISION DE INVERSION        ++++++++++++++++++++++++++
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	
	
	
	@Override
	public List<CanastaInversionesDto> obtenerTasaComision() {
		return barridoInversionDao.obtenerTasaComision();
	}

	@Override
	public String insertarTasaComision(String param, String comision) {
		String result = "";
		Gson gson = new Gson();
		List<Map<String, String>> datosGrid = gson.fromJson(param, new TypeToken<ArrayList<Map<String, String>>> () {}.getType());
		Date fecHoy;
		
		try {
			for(int i=0; i<datosGrid.size(); i++) {
				if(barridoInversionDao.existeTasa(Integer.parseInt(datosGrid.get(i).get("dias").toString())) == 0) {
					barridoInversionDao.insertarTasa(Integer.parseInt(datosGrid.get(i).get("dias").toString()), 
							Double.parseDouble(datosGrid.get(i).get("tasa").toString()));
				}else {
					barridoInversionDao.modificarTasa(Integer.parseInt(datosGrid.get(i).get("dias").toString()), 
							Double.parseDouble(datosGrid.get(i).get("tasa").toString()));
				}
			}
			fecHoy = barridoInversionDao.obtenerFechaHoy();
			
			if(barridoInversionDao.existeComision(Double.parseDouble(comision), fecHoy) == 0) {
				barridoInversionDao.insertarComision(Double.parseDouble(comision), fecHoy);
			}
			return "Se almacenaron correctamente los datos";
			
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " "+ Bitacora.getStackTrace(e) + 
			"P:Inversiones, C:BarridoInversionBusinessImpl, M:insertarTasaComision");
		}
		return result;
	}

	@Override
	public List<CanastaInversionesDto> obtenerCanastasVencidasHoy(
			String noEmpresa, String idDivisa) {
		return barridoInversionDao.obtenerCanastasVencidasHoy(noEmpresa, idDivisa);
	}
}