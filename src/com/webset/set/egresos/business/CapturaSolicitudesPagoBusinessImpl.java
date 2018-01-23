package com.webset.set.egresos.business;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.axis.AxisFault;

import com.webset.set.egresos.dao.CapturaSolicitudesPagoDao;
import com.webset.set.egresos.dao.impl.CapturaSolicitudesPagoDaoImpl;
import com.webset.set.egresos.dao.impl.ConsultasGeneralesEgresosDao;
import com.webset.set.egresos.dto.ConfirmacionCargoCtaDto;
import com.webset.set.egresos.dto.PolizaContableDto;
import com.webset.set.egresos.service.CapturaSolicitudesPagoService;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.ConstantesSet;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utilerias.dto.CajaUsuarioDto;
import com.webset.set.utilerias.dto.GeneradorDto;
import com.webset.set.utilerias.dto.LlenaComboDivisaDto;
import com.webset.set.utilerias.dto.LlenaComboEmpresasDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.set.utilerias.dto.LlenaFormaPagoDto;
import com.webset.set.utilerias.dto.MovimientoDto;
import com.webset.set.utilerias.dto.ParametroDto;

import mx.com.gruposalinas.Poliza.DT_Polizas_OBPolizas;
import mx.com.gruposalinas.Poliza.DT_Polizas_ResponseResponse;
import mx.com.gruposalinas.Poliza.SOS_PolizasBindingStub;
import mx.com.gruposalinas.Poliza.SOS_PolizasServiceLocator;

public class CapturaSolicitudesPagoBusinessImpl implements CapturaSolicitudesPagoService{
	private CapturaSolicitudesPagoDao capturaSolicitudesPagoDao;
	private ConsultasGeneralesEgresosDao consultasGeneralesEgresosDao;
	Funciones funciones= new Funciones();
	Bitacora bitacora = new Bitacora();

	public List<LlenaComboEmpresasDto>obtenerEmpresas(int idUsuario){
		return capturaSolicitudesPagoDao.obtenerEmpresas(idUsuario);
	}
	public List<LlenaComboDivisaDto>obtenerDivisas(){
		return capturaSolicitudesPagoDao.obtenerDivisas();
	}
	public List<LlenaFormaPagoDto>obtenerFormaPago(){
		return capturaSolicitudesPagoDao.obtenerFormaPago();
	}
	//
	public List<LlenaFormaPagoDto>llenarComboFormaPagoParametrizado(String poliza, String rubro, String Grupo){
		return capturaSolicitudesPagoDao.llenarComboFormaPagoParametrizado(poliza, rubro, Grupo);
	}
	
	public Date obtenerFechaHoy(){
		return capturaSolicitudesPagoDao.obtenerFechaHoy();
	}
	public List<CajaUsuarioDto>obtenerCajaUsuario(int idUsuario){
		return capturaSolicitudesPagoDao.obtenerCajaUsuario(idUsuario);
	}
	public List<LlenaComboGralDto>LlenarComboGral(LlenaComboGralDto dto){
		return capturaSolicitudesPagoDao.llenarComboGral(dto);
	}
	public List<PolizaContableDto>llenarComboPoliza(PolizaContableDto dto){
		return capturaSolicitudesPagoDao.llenarComboPoliza(dto);
	}
	public String consultarConfiguraSet(int indice){
		return capturaSolicitudesPagoDao.consultarConfiguaraSet(indice);
	}
	public List<LlenaComboGralDto>llenarComboAreaDestino(LlenaComboGralDto dto){
		String condIdUsuario="";
		condIdUsuario=dto.getCondicion();
		dto.setCondicion("id_area in (select id_area from usuario_area where	"+condIdUsuario+")");
		return capturaSolicitudesPagoDao.llenarComboGral(dto);		
	}
	
	public List<LlenaComboGralDto>llenarComboBeneficiario(LlenaComboGralDto dto, int noEmpresa){
		return consultasGeneralesEgresosDao.llenarComboBeneficiario(dto);
	}
	public List<LlenaComboGralDto>llenarComboBeneficiarioParametrizado(LlenaComboGralDto dto, int noEmpresa, String grupo, String rubro){
		//System.out.println("entro a parametrizado biss");
		return consultasGeneralesEgresosDao.llenarComboBeneficiarioParametrizado(dto, grupo, rubro);
		
	}
	
	public List<LlenaComboGralDto>obtenerBancoBenef(Map<String, Object> datos){
		return capturaSolicitudesPagoDao.obtenerBancoBenef(datos);		
	}
	public List<LlenaComboGralDto>obtenerChequeras(Map<String,Object> datos){
		return capturaSolicitudesPagoDao.obtenerChequeras(datos);
	}
	public List<Map<String,String>> obtenerChequerasBancoInterlocutor(Map<String,Object> datos){
		return capturaSolicitudesPagoDao.obtenerChequerasBancoInterlocutor(datos);
	}
	public List<Map<String, Integer>> obtenerSucPlazClabe(Map<String,Object> datos){
		 return capturaSolicitudesPagoDao.obtenerSucPlazClabe(datos);
	}
	public List<MovimientoDto> consultarMovimientos(Map<String, Object> datos){
		return capturaSolicitudesPagoDao.consultarMovimientos(datos);
	}
	public List<Map<String, Object>>obtenerDetalleMovimiento(int noFolioDet){
		return capturaSolicitudesPagoDao.obtenerDetalleMovimiento(noFolioDet);
	}
	//
	public Map<String,Object> registrarPlantilla(Map<String, String> datIn){
		//System.out.println("entra al business");
		Map<String, Object> mapReturn= new HashMap<String, Object>();
		try {
			mapReturn = capturaSolicitudesPagoDao.registrarPlantilla(datIn);
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Egresos, C:CapturaSolicitudesPagoBussines, M:registrarPlantilla");
		}
		return mapReturn;
	}
	@Override
	public List<String> fijaPlantilla(int idPlantilla, int idUsuario) {
		List<String> result = new ArrayList<String>();
		try {
			result = capturaSolicitudesPagoDao.fijaPlantilla(idPlantilla, idUsuario);
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Egresos, C:CapturaSolicitudesPagoBussines, M:fijaPlantilla");
		}
			return result;
		}
	
	
	public Map<String,Object> generarDocumento(Map<String, String> plantilla, Map<String, String> cabecera){
		Map<String, Object> mapReturn= new HashMap<String, Object>();
		mapReturn.put("msgUsuario", "Error desconocido.");
		
		try {
			//inicio fijar dto
			String  idEmpresa=cabecera.get("idEmpresa").toString();
		    int gICuentaEmp = capturaSolicitudesPagoDao.obtenerNoCuenta(Integer.parseInt(idEmpresa));
	        //folioParametro1= folioParametro;
	        ParametroDto insertUno=new ParametroDto();
	      //  insertUno.setIdTipoPoliza(Integer.parseInt((String)cabecera.get("tipoPoliza")));
	        insertUno.setNoEmpresa(Integer.parseInt((String) cabecera.get("idEmpresa")));
	        //insertUno.setNoFolioParam(folioParametro);
	        insertUno.setIdFormaPago(Integer.parseInt((String)cabecera.get("idFormaPago")));
	        insertUno.setUsuarioAlta(Integer.parseInt((String)cabecera.get("idUsuario")));
	        insertUno.setNoCuenta(gICuentaEmp);
	       //insertUno.setNoDocto(String.valueOf(folioNuevo));
        	//insertUno.setNoFactura(String.valueOf(folioNuevo));
        	insertUno.setReferencia(plantilla.get("i7"));
        	insertUno.setDivision(plantilla.get("i11"));//i10
        	insertUno.setCentroCosto(plantilla.get("i10"));//
        	insertUno.setFecValor(funciones.ponerFechaDate(plantilla.get("i1")));
        	insertUno.setFecOperacion(funciones.ponerFechaDate((String)cabecera.get("fechaFactura")));
	        insertUno.setFecAlta(capturaSolicitudesPagoDao.obtenerFechaHoy());
	        insertUno.setFecValorOriginal(funciones.ponerFechaDate((String)cabecera.get("fechaFactura")));//duda?verificar estas cuatro fechas
	        insertUno.setIdDivisa((String)cabecera.get("idDivOriginal"));
	        insertUno.setImporte(Double.parseDouble((String)plantilla.get("i4")));//duda?importes
	        insertUno.setImporteOriginal(Double.parseDouble((String)cabecera.get("importeOriginal")));
	        insertUno.setTipoCambio(Double.parseDouble((String)plantilla.get("i3")));
	        insertUno.setIdCaja(cabecera.get("idCaja")!=null && !cabecera.get("idCaja").equals("") 
	        		? Integer.parseInt((String)cabecera.get("idCaja")):0);
	        insertUno.setIdDivisaOriginal((String)cabecera.get("idDivOriginal"));
	        insertUno.setBeneficiario((String)cabecera.get("nomPersona"));
	        insertUno.setNoCliente(String.valueOf(capturaSolicitudesPagoDao.obtenerPersona(cabecera.get("idPersona"))));
	        insertUno.setConcepto((String)plantilla.get("i0"));
	        insertUno.setIdBancoBenef(Integer.parseInt(String.valueOf(cabecera.get("idBancoBenef"))));
	        insertUno.setIdChequeraBenef(cabecera.get("idChequera")!=null && !cabecera.get("idChequera").equals("")
	        		? (String)cabecera.get("idChequera") : "0");
	        insertUno.setIdLeyenda(cabecera.get("leyenda").equals("true")?"1":"0");
        	insertUno.setIdChequera(cabecera.get("idChequeraP")!=null && !cabecera.get("idChequeraP").equals("")
	        		? (String)cabecera.get("idChequeraP") : "0");
    	
	        insertUno.setObservacion(String.valueOf(plantilla.get("i14")));
	        //insertUno.setGrupo(folioParametro);
	        insertUno.setAplica(1);
	        insertUno.setIdEstatusMov("C");
	        insertUno.setBSalvoBuenCobro("S");
	        insertUno.setIdEstatusReg("P");
	        insertUno.setIdTipoDocto(41);
	        insertUno.setIdTipoOperacion(3000);
	        insertUno.setFolioRef(0);
	        //clabe
	        insertUno.setClabe(cabecera.get("clabe") != null && !cabecera.get("clabe").equals("")
	        		? cabecera.get("clabe") : "0");
	        //psBenef
	        insertUno.setSolicita(String.valueOf(plantilla.get("i9")));
	        insertUno.setAutoriza(String.valueOf(plantilla.get("i13")));
	        insertUno.setPlaza(cabecera.get("plaza")!=null && !cabecera.get("plaza").equals("") 
	        		? Integer.parseInt((String) cabecera.get("plaza")) : 0);
	        insertUno.setSucursal(cabecera.get("sucursal")!=null && !cabecera.get("sucursal").equals("")
	        		? Integer.parseInt((String)cabecera.get("sucursal")) : 0);
	        //pendiente el segundo pi_folioParametro
	        insertUno.setNoFolioMov(1);
	        insertUno.setAgrupa1(0);//los campos agrupa estan fijos ya que en la interfaz por ahora no se muestran
	        insertUno.setAgrupa2(0);
	        insertUno.setAgrupa3(0);
	        insertUno.setAdu(false);
	        insertUno.setLote(2);
	        insertUno.setPartida("0");
	        insertUno.setIdRubroInt(cabecera.get("idRubro")!=null && !cabecera.get("idRubro").equals("0")?cabecera.get("idRubro"):"0");
	        insertUno.setIdGrupo(cabecera.get("idGrupo")!=null && !cabecera.get("idGrupo").equals("0")?cabecera.get("idGrupo"):"0");
	        insertUno.setDivision(plantilla.get("i11")!=null && !plantilla.get("i11").equals("0")?plantilla.get("i11"):"");
	        //insertUno.set
			//fin fijar dto
			//if (cabecera.get("polizaContable").equals("true")) {
				System.out.println("El proceso es para el webset");
				int folioParametro=0;
				int folioParametro1=0;
				int folioNuevo=0;
				
				capturaSolicitudesPagoDao.actualizarFolioReal("no_folio_param");
		        folioParametro= capturaSolicitudesPagoDao.seleccionarFolioReal("no_folio_param");
		        if(folioParametro<=0){
		        	mapReturn.put("msgUsuario", "Error al obtener: no_folio_param");
		            return mapReturn;
		        }
		        
		        capturaSolicitudesPagoDao.actualizarFolioReal("no_folio_docto");
		        folioNuevo = capturaSolicitudesPagoDao.seleccionarFolioReal("no_folio_docto");
		        if(folioParametro<=0){
		        	mapReturn.put("msgUsuario", "Error al obtener: no_folio_param");
		            return mapReturn;
		        }
		        
		        folioParametro1= folioParametro;
		        insertUno.setNoFolioParam(folioParametro);
		        insertUno.setNoDocto(String.valueOf(folioNuevo));
	        	insertUno.setNoFactura(String.valueOf(folioNuevo));
	        	insertUno.setGrupo(folioParametro);
	        	
		        if(capturaSolicitudesPagoDao.insertarParametro(insertUno)>0){
		        	mapReturn.put("msgUsuario", "Se inserto en parametro.");
		        	capturaSolicitudesPagoDao.actualizarFolioReal("no_folio_param");
			        folioParametro= capturaSolicitudesPagoDao.seleccionarFolioReal("no_folio_param");
			        if(folioParametro<=0){
			        	mapReturn.put("msgUsuario", "Error al obtener: no_folio_param");
			            return mapReturn;
			        }
			        
			        insertUno.setGrupo(folioParametro1);
			        insertUno.setNoFolioParam(folioParametro);
			        insertUno.setNoDocto(String.valueOf(folioNuevo));
		        	insertUno.setNoFactura(String.valueOf(folioNuevo));
		        	insertUno.setIdEstatusMov("H");
		        	insertUno.setIdTipoOperacion(3001);
		        	insertUno.setFolioRef(1);
		        	insertUno.setPartida("1");
		        	insertUno.setLote(1);
		        	insertUno.setIdArea(plantilla.get("i16") != null && !plantilla.get("i16").equals("") ? Integer.parseInt(plantilla.get("i16").toString()) : 0);
		        	insertUno.setPartida("1");
		        	
		        	if(capturaSolicitudesPagoDao.insertarParametro(insertUno)>0){
		        		GeneradorDto generadorDto = new GeneradorDto();
		        		
		     	        generadorDto.setEmpresa(Integer.parseInt(cabecera.get("idEmpresa")));
		     	        generadorDto.setFolParam(folioParametro1);
		     	        generadorDto.setFolMovi(0);
		     	        generadorDto.setFolDeta(0);
		     	        mapReturn=capturaSolicitudesPagoDao.ejecutarGenerador(generadorDto);
		     	        
		     	        if(Integer.parseInt(mapReturn.get("result").toString())!=0){
		     	        	System.out.println("\n datos error generador");
		     	        	mapReturn.put("msgUsuario","Error " +mapReturn.get("result")+" enGenerador "+"no_folio_param="+folioParametro1);
		     	            return mapReturn;
		     	            
		     	        }else{
		     	        	System.out.println("\n datos registrados");
		     	        	//capturaSolicitudesPagoDao.actualizarClabe(mapReturn.get("folDeta").toString(), insertUno.getClabe());
	     					mapReturn.put("msgUsuario", "Datos registrados para el docto: "+folioNuevo);
	     					mapReturn.put("terminado", "1");
		     	        }
		        	}else	
		        		mapReturn.put("msgUsuario", "Error desconocido al insertar en parametro dos.");
		        }else
		        	mapReturn.put("msgUsuario", "Error desconocido al insertar en parametro uno.");
	
			/*} else {
				System.out.println("El proceso es para el webservice");
				insertUno.setNoCliente(String.valueOf(cabecera.get("idPersona")));
				//Select Transaccion, Clase_Doc From Configura_Solicitud_Pago Where Id_Poliza = 2 And Id_Grupo = 2000 And Id_Rubro = 2001;
				List<String> claseDocTranscion = new ArrayList<String>();
				String claseDoc = "";
				String formaPago = "T";
				//String tansaccion = "";
				if (claseDocTranscion.size()!= 0) {
					claseDoc = claseDocTranscion.get(1);
					//tansaccion = claseDocTranscion.get(0);
				} else {
					claseDoc = "";
					//tansaccion = "";
				}
				if(insertUno.getIdFormaPago() != 3)
					formaPago = "C";
				
				//agregar la transaccion cuando este lista la definicion.
				claseDocTranscion=capturaSolicitudesPagoDao.obtieneTransacionClasedocumento(insertUno.getIdTipoPoliza()+"",insertUno.getIdGrupo()+"", insertUno.getIdRubroInt()+"");
				System.out.println(claseDocTranscion+"esta es la lista");
				//poner i5 en observacion
				//
				//List<DT_Polizas_OBPolizas> list_dt_Polizas_OBPolizas = new ArrayList<DT_Polizas_OBPolizas>();
				DT_Polizas_OBPolizas dt_Polizas_OBPolizas = new DT_Polizas_OBPolizas(funciones.ajustarLongitudCampo(String.valueOf(idEmpresa), 4, "D", "", "0"), ""/*NO_DOC_SAP, ""/*SECUENCIA
						""/*NO_PEDIDO, ""/*NO_FOLIO_SET, ""/*NO_FACTURA, 
						funciones.ponerFechaSola(insertUno.getFecValor()), String.valueOf(funciones.ponerFechaSola(insertUno.getFecAlta())), String.valueOf(insertUno.getNoCliente()), 
						String.valueOf(insertUno.getImporte()), insertUno.getIdDivisaOriginal(), insertUno.getIdDivisaOriginal(), 
						String.valueOf(insertUno.getTipoCambio()), formaPago, String.valueOf(insertUno.getIdBanco()), 
						""/*ID_CHEQUE, insertUno.getReferencia(), insertUno.getConcepto(),
						""/*IND_MAY_ES, insertUno.getDivision(),
						""/*IND_IVA, 
						funciones.ponerFechaSola(insertUno.getFecValor()), funciones.ponerFechaSola(insertUno.getFecValor()), ""/*ESTATUS, 
						""/*CAUSA_RECH, funciones.ponerFechaSola(insertUno.getFecValor()), String.valueOf(insertUno.getImporte())/*importe usa, 
						""/*TIPO_OPERACION, ""/*TIPO_MOVTO, ""/*CAPITAL, 
						""/*INTERES, ""/*ISR, ""/*CHEQUERA_INV, 
						""/*IMP_DE_IVA ""/*IMP_MES_ANT, ""/*IMP_MES_NVO, 
						""/*NO_CHEQUE, ""/*CVE_CONTROL, ""/*NO_POLIZA_COMPENSA, 
						plantilla.get("i5")!=null && !plantilla.get("i5").equals("0") && !plantilla.get("i5").equals("")?plantilla.get("i5"):"",
						"", "", insertUno.getCentroCosto(), 
						""/*CTA_CONTABLE_IVA, claseDoc,String.valueOf(insertUno.getIdTipoPoliza()),
						insertUno.getAutoriza(),insertUno.getSolicita(),plantilla.get("i6")!=null && !plantilla.get("i6").equals("0") && !plantilla.get("i6").equals("")?plantilla.get("i6"):"");
					//agregar el get dee solicitata sociedad gl
					//banco interlocutor por autoriza
					//
				
				//System.out.println(dt_Polizas_OBPolizas);
				//list_dt_Polizas_OBPolizas.add(dt_Polizas_OBPolizas);//aqui
				SOS_PolizasServiceLocator service = new SOS_PolizasServiceLocator();
				SOS_PolizasBindingStub sos_PolizasBindingStub = new SOS_PolizasBindingStub(new URL(service.getHTTP_PortAddress()), service);
				sos_PolizasBindingStub.setUsername(capturaSolicitudesPagoDao.consultarConfiguaraSet(ConstantesSet.USERNAME_WS_POLIZAS));
				sos_PolizasBindingStub.setPassword(capturaSolicitudesPagoDao.consultarConfiguaraSet(ConstantesSet.PASSWORD_WS_POLIZAS));
				
				DT_Polizas_ResponseResponse[] dt_Polizas_ResponseResponse  = 
						sos_PolizasBindingStub.SOS_Polizas(
								new DT_Polizas_OBPolizas[] {dt_Polizas_OBPolizas});
				
				if (dt_Polizas_ResponseResponse.length>0) {
					mapReturn=capturaSolicitudesPagoDao.insertaBitacoraPoliza(dt_Polizas_ResponseResponse[0]);
					if(!(Boolean)mapReturn.get("bandera")){
						mapReturn.put("msgUsuario",mapReturn.get("msgUsuario"));
						return mapReturn;
					}else{
						mapReturn.put("msgUsuario","El documento fue generado con el numero:  "+mapReturn.get("msgUsuario").toString());
						return mapReturn;
					}
				} else {
					mapReturn.put("msgUsuario","Sin respuesta del web service.");
				}
					
			}
		}catch ( MalformedURLException e1) {
			mapReturn.put("msgUsuario","Error al conectarse al web service.");
			e1.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + e1.toString() 
			+ "P:Egresos, C:CapturaSolicitudesPagoBussines, M:generarDocumento");
		} catch (AxisFault e1) {
			mapReturn.put("msgUsuario","Error al procesar los datos dentro del web service.");
			e1.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + e1.toString() 
			+ "P:Egresos, C:CapturaSolicitudesPagoBussines, M:generarDocumento");*/
			//}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Egresos, C:CapturaSolicitudesPagoBussines, M:generarDocumento");
		}
		
		return mapReturn;
	}
	

	public Map<String,Object> ejecutarSolicitud(Map<String, String> datIn){
		System.out.println("entra al business");
		Map<String, Object> mapReturn= new HashMap<String, Object>();
		
		try{
				int  gICuentaEmp=0; 
				int folioNuevo=0;
				int folioParametro=0;
				int folioParametro1=0;
				
				Map<String, Object> pdResp;
				String  idEmpresa=datIn.get("idEmpresa").toString();
				String idBanco=datIn.get("idBancoBenef").toString();
				
				mapReturn.put("terminado", "0");
				
		        if(datIn.get("chkProvUnico").equals("true") && datIn.get("idFormaPago").equals("3")) 
		        {
		            if(datIn.get("optNac").equals("true"))
		            {
		                if(Integer.parseInt(idBanco) < 1000 && !datIn.get("idDivOriginal").equals("MN"))
		                {
		                     if(datIn.get("chequera").toString().trim().length()>25)
		                     {
		                            mapReturn.put("msgUsuario", "La chequera debe ser menor de 25"); //ok
		                            return mapReturn;
		                     }
		                }
		                else
		                {
		                    if(idBanco.trim().equals("127"))
		                    {
		                        if(datIn.get("chequera").toString().trim().length()!=14) 
		                        {
		                        	mapReturn.put("msgUsuario", " Las chequeras de Banco Azteca deben ser de 14 caracteres");
		                            return mapReturn;
		                        }
		                    }
		                   /* if(idBanco.trim().equals("12") && !(datIn.get("observaciones").equals( "PAGO PARCIAL" )) )
		                    {
		                    	if(datIn.get("chequera").toString().trim().substring(0,2).equals("91"))
		                    	{
		                            if(datIn.get("chequera").toString().trim().length()!=13)
		                            {
		                            	mapReturn.put("msgUsuario", "Las chequeras de Bancomer deben ser de 13 caracteres"); //
			                            return mapReturn;
		                            }
		                    	}
		                        else
		                        {
		                        	if(datIn.get("chequera").toString().trim().length()!=11)
		                        	{
		                        		if(datIn.get("chequera").toString().trim().length()!=18)
		                        		{
		                        			if(datIn.get("chequera").toString().trim().length()!=25)
		                        			{
		                                        mapReturn.put("msgUsuario", "Las chequeras de Bancomer deben ser de 11,18 � 25 caracteres");
					                            return mapReturn;
		                        			}
		                        		}
		                        	}
		                        }
		                    }*/
		                    else
		                    {
		                    	
		                    	
		                    	/*if( ! datIn.get("observaciones").equals( "PAGO PARCIAL" ) ){
			                    	if(datIn.get("chequera").toString().trim().length()!=11 )
		                        	{
		                        		if(datIn.get("chequera").toString().trim().length()!=18)
		                        		{
		                        			if(datIn.get("chequera").toString().trim().length()!=25)
		                        			{
		                                        mapReturn.put("msgUsuario", "Las chequeras de Bancomer deben ser de 11,18 � 25 caracteres");
					                            return mapReturn;
		                        			}
		                        		}
		                        	}
		                    	}*/
		                    }
		                }
		            }
		        }
		    	 
		        if(datIn.get("clabe") != null ){
		        	
			        if(!datIn.get("clabe").equals("")) 
			        {
			            if(datIn.get("clabe").toString().trim().length()!=18)
			            {
			                mapReturn.put("msgUsuario", "El campo CLABE debe ser de 18 Digitos cad"+datIn.get("clabe").toString()+"tam: "+datIn.get("clabe").toString().trim().length());
		                    return mapReturn;
			            }
			        }
			        
				}
		        
		        gICuentaEmp = capturaSolicitudesPagoDao.obtenerNoCuenta(Integer.parseInt(idEmpresa));
		       
		       /* if(!noDocto.equals(""))
		        {
		        	if((datIn.get("cmbOrigenProv")!=null && !datIn.get("cmbOrigenProv").equals("NOMINA")) && capturaSolicitudesPagoDao.consultarConfiguaraSet(1).equals("COSTCO"))
		        	{
			        	if(!capturaSolicitudesPagoDao.verificarDocumento(Integer.parseInt(noDocto), Integer.parseInt(idEmpresa)))
			            {
			            	mapReturn.put("msgUsuario", "Este n�mero de documento ya existe");
		                    return mapReturn;
			            }
		        	}
		        }
		        else{
		        
		        	mapReturn.put("msgUsuario", "Falta el n�mero de Documento");
	                return mapReturn;
		        }
		        */
		      /*  if(!noDocto.equals("") && capturaSolicitudesPagoDao.consultarConfiguaraSet(1).equals("COSTCO")) 
		        {
		        	if(datIn.get("cmbOrigenProv").equals("NOMINA") && capturaSolicitudesPagoDao.consultarConfiguaraSet(1).equals("COSTCO"))
		               folioNuevo = Integer.parseInt(noDocto);
		            else
		               folioNuevo = Integer.parseInt(noDocto);
		        }    
		        else
		        {
		        	capturaSolicitudesPagoDao.actualizarFolioReal("no_folio_docto");
		            folioNuevo = capturaSolicitudesPagoDao.seleccionarFolioReal("no_folio_docto");
		            if((datIn.get("cmbOrigenProv")!=null && !datIn.get("cmbOrigenProv").equals("NOMINA")) && capturaSolicitudesPagoDao.consultarConfiguaraSet(1).equals("COSTCO"))
		        	{
			        	if(!capturaSolicitudesPagoDao.verificarDocumento(Integer.parseInt(noDocto), Integer.parseInt(idEmpresa)))
			            {
			            	mapReturn.put("msgUsuario", "Este n�mero de documento ya existe");
		                    return mapReturn;
			            }
		        	}
		        }*/		
		   /* capturaSolicitudesPagoDao.actualizarFolioReal("no_folio_param");
	        folioParametro= capturaSolicitudesPagoDao.seleccionarFolioReal("no_folio_param");
	        if(folioParametro<=0)
	        {
	        	mapReturn.put("msgUsuario", "Error al obtener: no_folio_param");
	            return mapReturn;
	        }*/
		        
	        String cheqPag="";
	        
	        /*if(datIn.get("idFormaPago").equals("2"))
	        {
	        	cheqPag=capturaSolicitudesPagoDao.obtenerChequeraPagadora((String)datIn.get("idDivOriginal"));
	        }*/
	        
	        /*if(datIn.get("idFormaPago").equals("3") )
	        {
	        	
	        	if( datIn.get("observaciones").equals( "PAGO PARCIAL" ) )
	        	{	        	
	        		cheqPag= (String)datIn.get("idChequera");
	        		
	        	
	        	}	        	
	        	
	        }*/
	        

	        
	    	
	        
	            
	        //Hacer la primer insercion el parametro duda?ValidaError y variable EntraError
	        folioParametro1= folioParametro;
	        ParametroDto insertUno=new ParametroDto();
	        insertUno.setNoEmpresa(Integer.parseInt((String) datIn.get("idEmpresa")));
	        insertUno.setNoFolioParam(folioParametro);
	        insertUno.setIdFormaPago(Integer.parseInt((String)datIn.get("idFormaPago")));
	        insertUno.setUsuarioAlta(Integer.parseInt((String)datIn.get("idUsuario")));
	        insertUno.setNoCuenta(gICuentaEmp);
	        
	        if( datIn.get("observaciones").equals( "PAGO PARCIAL" ) )
        	{
	        	insertUno.setNoDocto( (String)datIn.get("noDocto") );
	        	insertUno.setNoFactura((String)datIn.get("noFactura"));
	        	if ( datIn.get("idBanco").equals( "" ) ){
	        		insertUno.setIdBanco( 0 );
	        		
	        	}else{
	        		insertUno.setIdBanco( Integer.parseInt(  datIn.get("idBanco") ) );
	        	}
        	}
	        else
	        {
	        		
	        	insertUno.setNoDocto( (String)datIn.get(""+folioNuevo) );
	        	insertUno.setNoFactura(""+folioNuevo);
	        		
        	}
	        
	        
	        
	        insertUno.setFecValor(funciones.ponerFechaDate((String)datIn.get("fechaPago")));
	        insertUno.setFecOperacion(funciones.ponerFechaDate((String)datIn.get("fechaFactura")));
	        insertUno.setFecAlta(capturaSolicitudesPagoDao.obtenerFechaHoy());
	        insertUno.setFecValorOriginal(funciones.ponerFechaDate((String)datIn.get("fechaFactura")));//duda?verificar estas cuatro fechas
	        insertUno.setIdDivisa((String)datIn.get("idDivPago"));
	        insertUno.setImporte(Double.parseDouble((String)datIn.get("importePago")));//duda?importes
	        insertUno.setImporteOriginal(Double.parseDouble((String)datIn.get("importeOriginal")));
	        insertUno.setTipoCambio(Double.parseDouble((String)datIn.get("tipoCambio")));
	        insertUno.setIdCaja(datIn.get("idCaja")!=null && !datIn.get("idCaja").equals("") 
	        		? Integer.parseInt((String)datIn.get("idCaja")):0);
	        insertUno.setIdDivisaOriginal((String)datIn.get("idDivOriginal"));
	        insertUno.setBeneficiario((String)datIn.get("nomPersona"));
	        insertUno.setNoCliente(""+datIn.get("idPersona"));
	        insertUno.setConcepto((String)datIn.get("concepto"));
	        insertUno.setIdBancoBenef(Integer.parseInt((String)datIn.get("idBancoBenef")));
	        
	        if( datIn.get("observaciones").equals( "PAGO PARCIAL" ) )
        	{
	        	insertUno.setIdChequeraBenef(datIn.get("chequera")!=null && !datIn.get("chequera").equals("")
		        		? (String)datIn.get("chequera") : "0");
        	}else{
	        insertUno.setIdChequeraBenef(datIn.get("idChequera")!=null && !datIn.get("idChequera").equals("")
	        		? (String)datIn.get("idChequera") : "0");
        	}
	        insertUno.setIdLeyenda(datIn.get("chkLeyenda").equals("true")?"1":"0");
	        
	        if( datIn.get("observaciones").equals( "PAGO PARCIAL" ) )
        	{
	        	insertUno.setIdChequera(datIn.get("idChequera")!=null && !datIn.get("idChequera").equals("")
		        		? (String)datIn.get("idChequera") : "0");
        	}else{
        		insertUno.setIdChequera(cheqPag);
        	}
	        insertUno.setObservacion((String)datIn.get("observaciones"));
	        insertUno.setGrupo(folioParametro1);
	        
	        insertUno.setAplica(1);
	        insertUno.setIdEstatusMov("C");
	        insertUno.setBSalvoBuenCobro("S");
	        insertUno.setIdEstatusReg("P");
	        insertUno.setIdTipoDocto(41);
	        insertUno.setIdTipoOperacion(3000);
	        insertUno.setFolioRef(0);
	        //clabe
	        insertUno.setClabe(datIn.get("clabe") != null && !datIn.get("clabe").equals("")
	        		? datIn.get("clabe") : "0");
	        //psBenef
	        insertUno.setSolicita((String)datIn.get("solicita"));
	        insertUno.setAutoriza((String)datIn.get("autoriza"));
	        insertUno.setPlaza(datIn.get("plaza")!=null && !datIn.get("plaza").equals("") 
	        		? Integer.parseInt((String) datIn.get("plaza")) : 0);
	        insertUno.setSucursal(datIn.get("sucursal")!=null && !datIn.get("sucursal").equals("")
	        		? Integer.parseInt((String)datIn.get("sucursal")) : 0);
	        //pendiente el segundo pi_folioParametro
	        insertUno.setNoFolioMov(1);
	        insertUno.setAgrupa1(0);//los campos agrupa estan fijos ya que en la interfaz por ahora no se muestran
	        insertUno.setAgrupa2(0);
	        insertUno.setAgrupa3(0);
	        insertUno.setAdu(false);
//	        insertUno.setIdArea(detalle.get(0).get("area") != null ? Integer.parseInt(detalle.get(0).get("area").toString()) : 0);
//	        insertUno.setIdRubroInt(detalle.get(0).get("rubro")!=null && !detalle.get(0).get("rubro").equals("0")?Integer.parseInt((String)detalle.get(0).get("rubro")):0);
//	        //insertUno.setDivision(detalle.get(0).get("departamento")!=null && !detalle.get(0).get("departamento").equals("0")?(String)detalle.get(0).get("departamento"):"");
//	        
//	        //if( datIn.get("observaciones").equals( "PAGO PARCIAL" ) )
//        	//{	        
//	        	insertUno.setIdGrupo(detalle.get(0).get("grupo")!=null && !detalle.get(0).get("grupo").equals("0")?Integer.parseInt((String)detalle.get(0).get("grupo")):0);
        	//}
	        	
	        	
	        //Checar el lote
	        insertUno.setLote(2);
	        insertUno.setPartida("0");
	        //insertUno.setchkContabilizar
	        
	       
	        
	        //insercion de los hijos del padre
	        /*for(int i=0;i<detalle.size();i++)
	        {
	        	capturaSolicitudesPagoDao.actualizarFolioReal("no_folio_param");
	        	folioParametro= capturaSolicitudesPagoDao.seleccionarFolioReal("no_folio_param");
	        	insertDet=new ParametroDto();
	            insertDet.setNoEmpresa(Integer.parseInt((String) datIn.get("idEmpresa")));
	            insertDet.setNoFolioParam(folioParametro);
	            insertDet.setIdFormaPago(Integer.parseInt((String)datIn.get("idFormaPago")));
	            insertDet.setUsuarioAlta(Integer.parseInt((String)datIn.get("idUsuario")));
	            insertDet.setNoCuenta(gICuentaEmp);
	            if( datIn.get("observaciones").equals( "PAGO PARCIAL" ) )
	        	{
	            	insertDet.setNoDocto( (String)datIn.get("noDocto") );
	            	insertDet.setNoFactura((String)datIn.get("noFactura"));
	            	if ( datIn.get("idBanco").equals( "" ) ){
	            		insertDet.setIdBanco( 0 );
		        		
		        	}else{
		        		insertDet.setIdBanco( Integer.parseInt(  datIn.get("idBanco") ) );
		        	}
	        	}
		        else
		        {		        		
		        	insertDet.setNoDocto( (String)datIn.get(""+folioNuevo) );	
		        	insertDet.setNoFactura(""+folioNuevo);
	        	}
	            
	            insertDet.setFecValor(funciones.ponerFechaDate((String)datIn.get("fechaPago")));
	            insertDet.setFecOperacion(funciones.ponerFechaDate((String)datIn.get("fechaFactura")));
	            insertDet.setFecAlta(capturaSolicitudesPagoDao.obtenerFechaHoy());
	            insertDet.setFecValorOriginal(funciones.ponerFechaDate((String)datIn.get("fechaFactura")));//duda?verificar estas cuatro fechas
	            insertDet.setIdDivisa((String)datIn.get("idDivPago"));
	            insertDet.setImporte(Double.parseDouble((String)detalle.get(i).get("importe")));//duda?importes
	            
	            insertDet.setImporteOriginal(Double.parseDouble((String)detalle.get(i).get("importe")));
	            insertDet.setTipoCambio(Double.parseDouble((String)datIn.get("tipoCambio")));
	            insertDet.setIdCaja(datIn.get("idCaja")!=null && !datIn.get("idCaja").equals("") 
	            		? Integer.parseInt((String)datIn.get("idCaja")) : 0);
	            insertDet.setIdDivisaOriginal((String)datIn.get("idDivOriginal"));
	            insertDet.setBeneficiario((String)datIn.get("nomPersona"));
	            insertDet.setNoCliente(""+datIn.get("idPersona"));
	            insertDet.setConcepto((String)datIn.get("concepto"));
	            insertDet.setIdBancoBenef(Integer.parseInt((String)datIn.get("idBancoBenef")));
	            insertDet.setIdChequeraBenef(datIn.get("idChequera")!=null && !datIn.get("idChequera").equals("")
	            		? (String)datIn.get("idChequera") : "0");
	            insertDet.setIdLeyenda(datIn.get("chkLeyenda").equals("true")?"1":"0");
	            insertDet.setIdChequera(cheqPag);
	            insertDet.setObservacion((String)datIn.get("observaciones"));
	            insertDet.setGrupo(folioParametro1);
	            
	            insertDet.setAplica(1);
	            insertDet.setIdEstatusMov("H");
	            insertDet.setBSalvoBuenCobro("S");
	            insertDet.setIdEstatusReg("P");
	            insertDet.setIdTipoDocto(41);
	            insertDet.setIdTipoOperacion(3001);
	            if(i>0)
	            	insertDet.setFolioRef(2);
	            else
	            	insertDet.setFolioRef(1);
	            //psBenef
	            insertDet.setSolicita((String)datIn.get("solicita"));
	            insertDet.setAutoriza((String)datIn.get("autoriza"));
	            insertDet.setPlaza(datIn.get("plaza") != null && !datIn.get("plaza").equals("")
	            		? Integer.parseInt((String)datIn.get("plaza")) : 0);
	            insertDet.setSucursal(datIn.get("sucursal") != null && !datIn.get("sucursal").equals("") 
	            		?  Integer.parseInt((String)datIn.get("sucursal")) : 0);
	            insertDet.setNoFolioMov(1);
	            insertDet.setAgrupa1(0);//los campos agrupa estan fijos ya que en la interfaz por ahora no se muestran
	            insertDet.setAgrupa2(0);
	            insertDet.setAgrupa3(0);
	            insertDet.setAdu(false);
	            insertDet.setIdRubroInt(detalle.get(i).get("rubro")!=null && !detalle.get(i).get("rubro").equals("0")?Integer.parseInt((String)detalle.get(i).get("rubro")):0);
	            insertDet.setIdArea(detalle.get(i).get("area") != null ? Integer.parseInt(detalle.get(i).get("area").toString()) : 0);
	            insertDet.setDivision(detalle.get(i).get("departamento")!=null && !detalle.get(i).get("departamento").equals("0")?(String)detalle.get(i).get("departamento"):"99");
	            insertDet.setIdGrupo(detalle.get(i).get("grupo")!=null && !detalle.get(i).get("grupo").equals("0")?Integer.parseInt((String)detalle.get(i).get("grupo")):0);
	            insertDet.setPartida(detalle.get(i).get("partida")!=null && !detalle.get(i).get("partida").equals("0")?(String)detalle.get(i).get("partida"):"");
	            //clabe
	            insertUno.setClabe(datIn.get("clabe") != null && !datIn.get("clabe").equals("")
	            		? datIn.get("clabe") : "0");
	            //Checar el lote
	            insertDet.setLote(1);
	            //insertUno.setchkContabilizar
	           insDet=capturaSolicitudesPagoDao.insertarParametro(insertDet);
	        }*/
	        pdResp=null;
	        GeneradorDto generadorDto = new GeneradorDto();
	        generadorDto.setEmpresa(Integer.parseInt(datIn.get("idEmpresa")));
	        generadorDto.setFolParam(folioParametro1);
	        generadorDto.setFolMovi(0);
	        generadorDto.setFolDeta(0);
	        pdResp=capturaSolicitudesPagoDao.ejecutarGenerador(generadorDto);
	        
	        if(Integer.parseInt(pdResp.get("result").toString())!=0)
	        {
	        	
	        	mapReturn.put("msgUsuario","Error " +pdResp.get("result")+" enGenerador "+"no_folio_param="+folioParametro1);
	            return mapReturn;
	            
	        }
	        else
	        {
	        	
//	        	if( datIn.get("observaciones").equals( "PAGO PARCIAL" ) ){	
	        		
//	        		String strNoFolioDet = "";
//	        		String strImporteParcial = "";
//	        		String strDescripcion = "";
	        		
//	        		strNoFolioDet = ( (String) datIn.get("noFolioDet") ) ;
//	        		strImporteParcial = ( (String) datIn.get("importePago") ) ;
//	        		strDescripcion = "PAGO PARCIALIZADO" ;
	        		
	        		
	        		
//	        	}
	        	
					capturaSolicitudesPagoDao.actualizarClabe(pdResp.get("folDeta").toString(), insertUno.getClabe());
					mapReturn.put("msgUsuario", "Datos registrados para el docto: "+folioNuevo);
					mapReturn.put("terminado", "1");
				
	        }
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Egresos, C:CapturaSolicitudesPagoBussines, M:ejecutarSolicitud");
		}
		
		return mapReturn;
	}

	@Override
	public List<Boolean> habilitaComponetesPerfilEmpresa(String rubro, String poliza, String grupo) {
		List<Boolean> listRetorno = new ArrayList<Boolean>();
		try {
			listRetorno = capturaSolicitudesPagoDao.habilitaComponetesPerfilEmpresa(rubro, poliza, grupo);
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Egresos, C:CapturaSolicitudesPagoBussines, M:habilitaComponetesPerfilEmpresa");
		}
		return listRetorno;
	}
	public Map<String, Double> obtenerCambioDivisa(String idDivisaOriginal, String idDivisaPago, double importeOriginal)
	{
		Map<String, Double> res= new HashMap<String, Double>();
		double valDivOriginal=0,valDivPago=0, importePago;
		try{
			if(idDivisaOriginal.equals(idDivisaPago))
			{
				res.put("importePago", importeOriginal);
				res.put("tipoCambio", 1.0);				
			}
			else
			{
				valDivOriginal=capturaSolicitudesPagoDao.obtenerTipoCambio(idDivisaOriginal, capturaSolicitudesPagoDao.obtenerFechaHoy());
			
				valDivPago=capturaSolicitudesPagoDao.obtenerTipoCambio(idDivisaPago, capturaSolicitudesPagoDao.obtenerFechaHoy());
			
				if(valDivOriginal>valDivPago)
				{
					importePago=importeOriginal*valDivOriginal;
					res.put("importePago", importePago);
					res.put("tipoCambio", valDivOriginal);
				}
				else if(valDivOriginal<valDivPago)
				{
					importePago=importeOriginal/valDivPago;
					res.put("importePago", importePago);
					res.put("tipoCambio", valDivPago);
				}
				else{
					res.put("importePago", importeOriginal);
					res.put("tipoCambio", 1.0);
				}
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:CapturaSolicitudesPagoDao, M:obtenerCambioDivisa");	
		}
		return res;
	}
	
	public String obtenerSolicitante() {
		return capturaSolicitudesPagoDao.obtenerSolicitante();
	}
	public CapturaSolicitudesPagoDao getCapturaSolicitudesPagoDao() {
		return capturaSolicitudesPagoDao;
	}
	public void setCapturaSolicitudesPagoDao(
			CapturaSolicitudesPagoDao capturaSolicitudesPagoDao) {
		this.capturaSolicitudesPagoDao = capturaSolicitudesPagoDao;
		consultasGeneralesEgresosDao = new ConsultasGeneralesEgresosDao(
				((CapturaSolicitudesPagoDaoImpl)capturaSolicitudesPagoDao).getJdbcTemplate());
	}
	@Override
	public List<String> obtenerGrupoDelRubro(String idRubro) {
		List<String> result = new ArrayList<String>();
		try {
			result = capturaSolicitudesPagoDao.obtenerGrupoDelRubro(idRubro);
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Egresos, C:CapturaSolicitudesPagoBussines, M:obtenerGrupoDelRubro");
		}
		return result;
	}
	
	public Map<String,Object> obtieneCheqPagadora(int idEmpresa,String idDivOriginal,int idFormaPago){
		Map<String, Object> mapReturn= new HashMap<String, Object>();
		mapReturn.put("msgUsuario", "Error desconocido.");
		
		try {
		        		GeneradorDto generadorDto = new GeneradorDto();
		        		
		     	        generadorDto.setEmpresa(idEmpresa);
		     	        generadorDto.setDivisa(idDivOriginal);
		     	        generadorDto.setId_forma_pago(idFormaPago);
		     	        mapReturn=capturaSolicitudesPagoDao.obtieneCheqPagadora(generadorDto);
		     	      return   mapReturn;

		        	
		
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Egresos, C:CapturaSolicitudesPagoBussines, M:generarDocumento");
		}
		
		return mapReturn;
	}
	

	@Override
	public List<LlenaComboGralDto> obtenerGrupo() {
		return capturaSolicitudesPagoDao.obtenerGrupo();
	}
	@Override
	public List<LlenaComboGralDto>obtenerRubro(LlenaComboGralDto dto){
		return capturaSolicitudesPagoDao.obtenerRubro(dto);
	}
	
	
    /**
     * Estos metodos get y set es para hecer la conexion con la clase Dao
     * @return
     */
	
	



}
