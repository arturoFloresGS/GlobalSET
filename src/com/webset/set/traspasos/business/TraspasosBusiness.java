/**
 * @author Jessica Arelly Cruz Cruz
 * @since 05/01/2011
 */
package com.webset.set.traspasos.business;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.webset.set.bancaelectronica.dao.EnvioTransferenciasDao;
import com.webset.set.bancaelectronica.dto.PersonasDto;
import com.webset.set.ingresos.dto.CatBancoDto;
import com.webset.set.ingresos.dto.CatCtaBancoDto;
import com.webset.set.ingresos.dto.CatGrupoRubroDto;
import com.webset.set.layout.business.CreacionArchivosBusiness;
import com.webset.set.layout.business.LayoutAztecaBusiness;
import com.webset.set.layout.business.LayoutBancomerBusiness;
import com.webset.set.layout.business.LayoutBanorteBusiness;
import com.webset.set.layout.business.LayoutScotiabankInverlatBusiness;
import com.webset.set.layout.dto.ParametrosLayoutDto;
import com.webset.set.seguridad.dto.EmpresaDto;
import com.webset.set.seguridad.exceptions.BusinessException;
import com.webset.set.traspasos.dao.TraspasosDao;
import com.webset.set.traspasos.dto.BuscarSolicitudesTraspasosDto;
import com.webset.set.traspasos.dto.CatConceptoTraspDto;
import com.webset.set.traspasos.dto.GrupoDTO;
import com.webset.set.traspasos.dto.ParametroTraspasosDto;
import com.webset.set.traspasos.dto.RubroDTO;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.ConstantesSet;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utilerias.GlobalSingleton;
import com.webset.set.utilerias.dto.GeneradorDto;
import com.webset.set.utilerias.dto.LlenaComboDivisaDto;
import com.webset.set.utilerias.dto.LlenaComboEmpresasDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
//import com.webset.set.traspasos.dto.MovimientoDto;
import com.webset.set.utilerias.dto.MovimientoDto;
import com.webset.set.utilerias.dto.StoreParamsComunDto;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRMapArrayDataSource;

public class TraspasosBusiness {
	TraspasosDao traspasosDao = new TraspasosDao();
	private LayoutBancomerBusiness layoutBancomerBusiness;
	private CreacionArchivosBusiness creacionArchivosBusiness;
	private LayoutAztecaBusiness layoutAztecaBusiness;
	private LayoutScotiabankInverlatBusiness layoutScotiabankInverlatBusiness;
	private EnvioTransferenciasDao envioTransferenciasDao;
	LayoutBanorteBusiness layoutBanorteBusiness = new LayoutBanorteBusiness();
	double im;

	Bitacora bitacora = new Bitacora();
	Funciones funciones = new Funciones();
	private static Logger logger = Logger.getLogger(TraspasosBusiness.class);
	
	GlobalSingleton globalSingleton;
	
	/**
	 * llamada al metodo configuraSET
	 * @param indice
	 * @return consulta
	 */
	public String consultarSET(int indice) {
		String consulta = traspasosDao.consultarConfiguraSet(indice);
			return consulta;
	}
	
	/**
	 * llamada al metodo de FechaHoy
	 * */
	public String consultarFechaHoyS(){
		return traspasosDao.obtenerFechaHoyS();
	}
	
	public Date obtenerFechaHoy(){
		return traspasosDao.obtenerFechaHoy();
	}
	public int seleccionarFolioReal(String tipoFolio) {
		return traspasosDao.seleccionarFolioReal(tipoFolio);
	}
	
	public List<CatConceptoTraspDto> llenarComboConcepto() {
		List<CatConceptoTraspDto> concepto= null;
		try {
			concepto = traspasosDao.llenarComboConcepto();
		} catch (Exception e) {}
		return concepto;
	}
	
	public EnvioTransferenciasDao getEnvioTransferenciasDao() {
		return envioTransferenciasDao;
	}
	public void setEnvioTransferenciasDao(EnvioTransferenciasDao envioTransferenciasDao) {
		this.envioTransferenciasDao = envioTransferenciasDao;
	}
	
	/**
	 * consultar empresas
	 * @param piUsuario
	 * @return
	 * @throws Exception 
	 * @throws Exception
	 * @throws BusinessException
	 */
	public List<EmpresaDto> obtenerEmpresa(int piUsuario) throws Exception {
		List<EmpresaDto> empresas = traspasosDao.obtenerEmpresa(piUsuario); 
		return empresas;
	}
/**
 * consultar bancos
 * @param emp
 * @param pbInversion
 * @return
 * @throws Exception
 */
	public List<CatBancoDto> obtenerBanco(int emp, boolean pbInversion) throws Exception{
		List<CatBancoDto> bancos = traspasosDao.obtenerBanco(emp, pbInversion);
		return bancos;
	}

/**
 * consultar chequeras
 * @param ban
 * @param emp
 * @param pbInversion
 * @return bancos
 * @throws Exception
 */
	public List<CatCtaBancoDto> obtenerChequera(int emp, int ban, boolean pbInversion) throws Exception{
		List<CatCtaBancoDto> bancos = traspasosDao.obtenerChequera(emp, ban, pbInversion);
		return bancos;
	}

/**
 * 
 * @param emp
 * @param ban
 * @param chequera
 * @param bAnterior
 * @param fecha
 * @return saldo
 * @throws Exception
 */
	public double consultarSaldoFin(int emp, int ban, String chequera, boolean bAnterior, String fecha) throws Exception{
		return traspasosDao.consultarSaldoFinal(emp, ban, chequera, bAnterior, fecha);
	}
	
	/**
	 * 
	 * @param ban
	 * @param chequera
	 * @param pbInversion
	 * @return
	 * @throws Exception
	 */
	public List<Map<String,String>> verificaDivisa(int ban, String chequera, boolean pbInversion) {
		return traspasosDao.verificarDivisa(ban, chequera, pbInversion);
	}
	
	/**
	 * FunSQLSelectReferencia
	 * @param emp
	 * @param ban
	 * @param cheque
	 * @return
	 * @throws Exception
	 */
	public List<Map<String,String>> obtenerDatosReferencia(int emp, int ban, String cheque) {
		return traspasosDao.obtenerDatosReferencia(emp, ban, cheque);
	}
	
	/**
	 * 
	 * @param emp
	 * @param divisa
	 * @param chequera
	 * @param ban
	 * @return
	 * @throws Exception
	 */
	public double sumarImporte(int emp, String chequera, int ban, boolean pbInversion, boolean bAnterior, String fecha) throws Exception{
			//double suma = traspasosDao.sumarImporte(emp, chequera, ban, pbInversion);
			double saldo = traspasosDao.consultarSaldoFinal(emp, ban, chequera, bAnterior, fecha);
			double importe = saldo;
		return importe;
	}
	
	/**
	 * 
	 * @param emp2
	 * @param divisa
	 * @param pbInversion
	 * @param tipoChequera
	 * @return
	 * @throws Exception
	 */
	public List<CatBancoDto> obtenerBancoA(int emp2, String divisa, boolean pbInversion, String tipoChequera) throws Exception{
		List<CatBancoDto> bancos = traspasosDao.obtenerBancoA(emp2, divisa, pbInversion, tipoChequera);
		return bancos;
	}
	
	/**
	 * 
	 * @param emp
	 * @param ban
	 * @param pbInversion
	 * @param cheqExcluye
	 * @return
	 * @throws Exception
	 */
	public List<CatCtaBancoDto> obtenerChequeraA(int emp, int ban, boolean pbInversion, String cheqExcluye){
		List<CatCtaBancoDto> bancos = traspasosDao.obtenerChequeraA(emp, ban, pbInversion, cheqExcluye);
		return bancos;
	}
	
	/**
	 * 
	 * @param usuario
	 * @return lista de empresas
	 */
	public List<PersonasDto> llenarComboInterEmpresas() {
		globalSingleton = GlobalSingleton.getInstancia();
		List<PersonasDto> empresas = traspasosDao.llenarComboInterEmpresas(globalSingleton.getUsuarioLoginDto().getIdUsuario());
		return empresas;
	}
	
	/**
	 * 
	 * @param empresa
	 * @return lista de bancos
	 */
	public List<CatBancoDto> llenarComboBancoInter(int empresa){
		List<CatBancoDto> bancos = traspasosDao.llenarComboBancoInter(empresa);
		return bancos;
	}
	
	/**
	 * 
	 * @param empresa
	 * @param usuario
	 * @return
	 */
	public List<PersonasDto> llenarComboInterEmpresas2(int empresa) {
		globalSingleton = GlobalSingleton.getInstancia();
		List<PersonasDto> empresas = traspasosDao.llenarComboInterEmpresas2(empresa, globalSingleton.getUsuarioLoginDto().getIdUsuario());
		return empresas;
	}
	
	/**
	 * 
	 * @param empresa2
	 * @param pbInversion
	 * @param bancoDe
	 * @param chequeraDe
	 * @return lista de bancos
	 */
	public List<CatBancoDto> llenarComboBancoInter2(int empresa2, boolean pbInversion, int bancoDe, String chequeraDe){
		List<CatBancoDto> bancos = traspasosDao.llenarComboBancoInter2(empresa2, pbInversion, bancoDe, chequeraDe);
		return bancos;
	}
	
	/**
	 * 
	 * @param empresa
	 * @return
	 */
	public String consultarConcentradora(int empresa){
		String concentradora = traspasosDao.consultarConcentradora(empresa);
		return concentradora;
	}
	
	/**
	 * obtener clabe
	 * @param empresa
	 * @param banco
	 * @param chequera
	 * @return
	 */
	public List<Map<String,String>> obtenerClabe(int empresa, int banco, String chequera) {
		return traspasosDao.obtenerClabe(empresa, banco, chequera);
	}
	
	/**
	 * inserta la solicitud de traspaso en parametro
	 * @param parametro
	 * @return
	 */
	public Map<String,Object> insertarSolicitudTraspaso(ParametroTraspasosDto parametro){
		
		Map<String,Object> result= new HashMap<String,Object>();
		
		int primerInsert = 0;
		//int segundoInsert = 0;
		int banco = parametro.getIdBanco();
		int bancoB = parametro.getIdBancoBenef();
		String chequera = parametro.getIdChequera();
		String chequeraB = parametro.getIdChequeraBenef();
		boolean bandera = parametro.isBandera();
		String tipoFolio = "no_folio_param";
		String divisa = "";
		Map<String, Object> pdResp;
		int plEmpresa = 0;
		int plFolio = 0;
		int pdFolioMov = 0;
		int pdFolioDet = 0;
		int nuevoFolio = 0;
		
		
		int noCuenta = traspasosDao.obtenerNumeroCuenta(parametro.getNoEmpresa());
		
		int folioParam = 0;
		
		traspasosDao.actualizarFolioReal(tipoFolio);
		folioParam = traspasosDao.seleccionarFolioReal(tipoFolio);
		logger.info("folio actualizado:"+folioParam);
		List<Map<String, String>> divisaD = traspasosDao.verificarDivisa(banco, chequera, bandera);
		List<Map<String, String>> divisaA = traspasosDao.verificarDivisa(bancoB, chequeraB, bandera);
		
		if(divisaD.get(0).get("divisa") == null || divisaD.get(0).get("divisa").equals("")||divisaA.get(0).get("divisa") == null || divisaA.get(0).get("divisa").equals(""))
		{
			result.put("msgUsuario","No hay divisa");
		}
		
		if(divisaD.get(0).get("divisa").trim() != divisaA.get(0).get("divisa").trim())
		{	
			result.put("msgUsuario","El traspaso no se puede realizar porque las divisas son distintas");
			//return result;
		}
		
		divisa = divisaD.get(0).get("divisa");
		parametro.setGrupo(folioParam);
		parametro.setNoFolioParam(folioParam);
		parametro.setNoCuenta(noCuenta);
		parametro.setIdDivisa(divisa);
		plFolio = parametro.getNoFolioParam();
		
		// inserta secuencia 1 en parametro (EGRESO)
		primerInsert = traspasosDao.insertarSolicitudTraspaso(parametro);
			if(primerInsert > 0)
			{
				traspasosDao.actualizarFolioReal(tipoFolio);
				nuevoFolio = traspasosDao.seleccionarFolioReal(tipoFolio);
			}
		
		parametro.setNoFolioParam(nuevoFolio);
		parametro.setGrupo(folioParam);
		logger.info("folio actualizado otra vez:"+nuevoFolio);
		// inserta secuencia 2 en parametro (EGRESO)
		traspasosDao.insertarSolicitudTraspaso2(parametro);
		System.out.println("Despues del insert");
		plEmpresa = parametro.getNoEmpresa();
		
		//llamada al generador
		System.out.println("antes del generador");
		
		GeneradorDto dto = new GeneradorDto();
		
		
		dto.setEmpresa(plEmpresa);
		dto.setFolDeta(pdFolioDet);
		dto.setFolMovi(pdFolioMov);
		dto.setFolParam(plFolio);
		//dto.setMensajeResp("0");
		System.out.println(dto.toString());
		pdResp=traspasosDao.ejecutarGenerador(dto);
			logger.info("generador " + pdResp.get("result"));
			if(Integer.parseInt(pdResp.get("result").toString()) == 0)
			{
				//traspasosDao.insertAutorizacionesMov(plFolio);
				result.put("msgUsuario","Solicitud registrada correctamente");
			}
			else 
			{
				result.put("msgUsuario","Error " +pdResp.get("result")+" en Generador "+" no_folio_param = "+plFolio);
			}
		return result;
	}

	public TraspasosDao getTraspasosDao() {
		return traspasosDao;
	}
	
	public void setTraspasosDao(TraspasosDao traspasosDao) {
		this.traspasosDao = traspasosDao;
	}

	/**de aqui en adelante se agregan las llamadas a las consultas referentes a ejecucion de traspasos*/
	
	public List<LlenaComboGralDto>llenarComboGral(LlenaComboGralDto dto){
		return traspasosDao.llenarComboGral(dto);
	}
	
	public List<LlenaComboDivisaDto>llenarComboDivisa(){
		return traspasosDao.llenarComboDivisa();
	}
	
	public List<BuscarSolicitudesTraspasosDto> consultarSolicitudesTraspasos(BuscarSolicitudesTraspasosDto datos) {
		List<BuscarSolicitudesTraspasosDto> listaRes = new ArrayList<BuscarSolicitudesTraspasosDto>();
		
		try {
			listaRes = traspasosDao.consultarSolicitudesTraspasos(datos);
	//		System.out.println("tamaño de consultarSolicitudesTrS"+listaRes.size());
			for(int i=0; i<listaRes.size(); i++) {
				if(listaRes.get(i).getUsr1().equals("") && listaRes.get(i).getUsr2().equals(""))
					listaRes.get(i).setColor("color:#000000"); //Negro
				else if(listaRes.get(i).getUsr1().equals("") || listaRes.get(i).getUsr2().equals("")) {
					listaRes.get(i).setColor("color:#FDA920"); //Naranja
				}else if(!listaRes.get(i).getUsr1().equals("") && !listaRes.get(i).getUsr2().equals(""))
					listaRes.get(i).setColor("color:#04A861"); //Verde
			}
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Traspasos, C: TraspasosBusiness, M: consultarSolicitudesTraspasos");
		}
		return listaRes;
	}
	
	public List<BuscarSolicitudesTraspasosDto> llenarComboClave(BuscarSolicitudesTraspasosDto datos){
		return traspasosDao.llenarComboClave(datos);
	}
	
	/**
	 * metodo que realiza las operaciones para escribir el layout y ejecutar los traspasos
	 * @param parametroDto
	 * @param h2hAfrd 
	 * @return
	 */
	@SuppressWarnings("unused")
	public Map<String,Object> ejecutarSolicitudTraspaso(List<BuscarSolicitudesTraspasosDto> parametroDto, List<Map<String, String>> traspasosIguales, int optEnvioBNMX, int h2hAfrd){
		Map<String,Object> result= new HashMap<String,Object>();
		Map<String,Object> resultArchivo= new HashMap<String,Object>();
		Map<String,Object> pdResp= new HashMap<String,Object>();
		StoreParamsComunDto dtoTraspaso= new StoreParamsComunDto();
		Map<String, Object> mapRespExportaReg = new HashMap<String, Object>();

		try{
		/** variables*/
		int piTipo_Traspaso = 0;
		int pi_banco;
		int liSecuencia;
		int pl_operacion = 0;
		int plEmpresa = 0;
		int plLoteEntrada = 0;
		int piNumBanco = 0;
		int ll_empresa = 0;
		int importeParcial = -1;
		int liNoDocto = 0;
		int LI_BANCO;
		double ps_total;
		double suma =0;
		boolean internacional = false;
		boolean optCitibankFlatFile = false;
		//boolean pbEncontre3809 = false;
		boolean lbEnvioBitalH2H = false; //su valor viene del js
		boolean cambio_divisa_pago = false; //esta variable es en realidad una funcion(pendiente)
		boolean pb_layout_comerica = false;
		boolean lbBanca = false;
		boolean pbEscribi;
		boolean pb_mensaje_fondeo;
		boolean pb_negativo = false;
		boolean pbMismoBanco = false;
		boolean bCambioDivisa = false;
		boolean pbAgrupa;
		boolean pbPagoParcial = false;
		boolean pbBE_Bital = false;
		String sDescBanco = "";
		String psFoliosRep = "";
		String psFoliosNeg = "";
		String Ubicacion = "IUSA";
		String psFolios = "";
		String psRegistro = "";
		String sPropu1 = "";
	    String sPropu2 = "";
	    String psFoliosTrasp = "";
	    String psFolioGroup = "";
		double impTotDet = 0;
	//	int i = 0;
		List<MovimientoDto> listMovimiento = new ArrayList<MovimientoDto>();
		//String dirPrincipal = ConstantesSet.CARPETA_RAIZ_RESPALDO.substring(0,ConstantesSet.CARPETA_RAIZ_RESPALDO.length()-1);
		
		globalSingleton = GlobalSingleton.getInstancia();
		String dirPrincipal = ConstantesSet.CARPETA_RAIZ_RESPALDO;
		dirPrincipal=dirPrincipal.substring(0, dirPrincipal.length()-1);
		//System.out.println("dirPrincipal "+dirPrincipal);
		if(dirPrincipal.equals("")){
			result.put("msgUsuario", "No se encontro la ruta para el envio en configura_set(3006)");
			result.put("msgSalio", "salio");
			return result;
		}
		
		for (Map<String, String> map : traspasosIguales) {
			System.out.println("////inicio map////");
			System.out.println(map);
		}
	
		LI_BANCO = parametroDto.get(0).getIdBanco();
		
		if(parametroDto.get(0).isOpcionInterbancaria()){
	        piTipo_Traspaso = 1;
		}
		
	    else if(parametroDto.get(0).isOpcionMismoBanco()){

	    	if(LI_BANCO == ConstantesSet.BANAMEX && optCitibankFlatFile == false)
	    		piTipo_Traspaso = 3;
	    	else
	    		piTipo_Traspaso = 2;
	    }
	    else if(parametroDto.get(0).isOpcionSpeua()){
	    	piTipo_Traspaso = 4;
	    }
	    else if(parametroDto.get(0).isOpcionInternacional()){
	        internacional = true;
	        piTipo_Traspaso = 3;
	    }
		
	    lbEnvioBitalH2H = false;
	    if(LI_BANCO == ConstantesSet.BITAL && traspasosDao.consultarConfiguraSet(232).equals("SI"))
	        lbEnvioBitalH2H = true;

//	       'PAGO CRUZADO
		   	if(parametroDto.get(0).isCambioDivisa()){
		   		bCambioDivisa = true;
	            if(cambio_divisa_pago == false){//llamado a la funcion
	                result.put("msgUsuario", "No se cambio la Divisa de traspaso en los movimientos");
	                result.put("msgSalio", "salio");
	                return result;
	            }
	   		}
	       
		   	if(parametroDto.get(0).getBLayoutComerica().equals("S")){
	            pb_layout_comerica = true;
	            pi_banco = ConstantesSet.COMERICA;
		   	}else{
	            pb_layout_comerica = false;
	            pi_banco = LI_BANCO;
	        }
	        
	        if(parametroDto.get(0).isCambioDivisa()){
	            if(ConstantesSet.COMERICA != LI_BANCO){
	                pb_layout_comerica = false;
	                pi_banco = LI_BANCO;
	            }else{
	                pb_layout_comerica = true;
	                pi_banco = ConstantesSet.COMERICA;
	            }
	        }
	       
	        String banca = traspasosDao.consultarBancaElect(LI_BANCO);
	        if(banca.equals("E")  || banca.equals("A"))
	        	lbBanca = true;
	        else 
	        	lbBanca = false;
	        
	        ps_total = 0;
	        pbEscribi = false;
	        pb_mensaje_fondeo = false;
	        psFoliosRep = "";
	        
	        if(!parametroDto.get(0).getCmbBenef().equals("")){
	        	for(int i = 0; i < parametroDto.size(); i++){
                    sPropu1 = parametroDto.get(i).getCveControl();
                    System.out.println(parametroDto.get(i));
	        	}
	        	for(int i = 0; i <parametroDto.size(); i++){
                    sPropu2 = parametroDto.get(i).getCveControl();
                    if(sPropu1 != sPropu2){
                        result.put("msgUsuario", "Las propuestas seleccionadas son diferentes");
                        result.put("msgSalio", "salio");
                        return result;
                    }
	        	}
	        }
	        
	        if(parametroDto.get(0).getCmbBenef().equals(""))
	            pbAgrupa = false;
	        else
	            pbAgrupa = true;
	        
	        liSecuencia = 0;
	        
	        //'NUEVO PROCEDIMIENTO EN DONDE EL NOMBRE DEL ARCHIVO ES AUTOMATICO
	        resultArchivo = creacionArchivosBusiness.generarNombreArcivoBanco(pi_banco, parametroDto.get(0).isOpcionMismoBanco(),
	        		parametroDto.get(0).isOpcionInterbancaria(), parametroDto.get(0).isChkH2HSantander(), optEnvioBNMX,h2hAfrd);

	        if(!(Boolean)resultArchivo.get("estatus")){
	        	result.put("msgUsuario", "Este banco no tiene Layout de Banca Electronica");
	        	logger.error("Este banco no tiene Layout de Banca Electronica");
	        	return result;
	        }
	//        System.out.println("tamaño de parametroDto "+parametroDto.size());
	        for(int i=0; i < parametroDto.size(); i++){
	  //      	System.out.println("contador i"+i);
	            int row = i;
	            liSecuencia = liSecuencia + 1;
        		listMovimiento = traspasosDao.subSeleccionSolicitudes(parametroDto.get(row).getNoEmpresa(), parametroDto.get(row).getNoFolioDet(),parametroDto.get(0).isOpcionMismoBanco());
     //   		System.out.println("tamaño de listMovimiento"+listMovimiento.size());
                if(listMovimiento.size() > 0){
                    pb_negativo = false;
	                //'Se inserta el egreso del traspaso
        			if(listMovimiento.get(0).getIdTipoOperacion() == 3800)//'Trap. entre cuentas
                        pl_operacion = 3700;
                    else if(listMovimiento.get(0).getIdTipoOperacion() == 3801) //'Trasp entre empresas
                        pl_operacion = 3701;
                    else if(listMovimiento.get(0).getIdTipoOperacion() == 3805) //'Barrido
                        pl_operacion = 3705;
                    else if(listMovimiento.get(0).getIdTipoOperacion() == 3806) //'Fondeo                    
                        pl_operacion = 3706;
                        /*if(!Ubicacion.equals("CCM")){
	                        List<ComunDto> listMov = new ArrayList<ComunDto>();
	                        listMov = traspasosDao.checarMovimientosNegativos(parametroDto.get(row).getNoEmpresa(), 
	                        		parametroDto.get(row).getNoEmpresaBenef(), parametroDto.get(row).getIdDivisa());
	                        
	                        if(listMov.size() > 0) {
		                        suma = listMov.get(row).getSuma() - parametroDto.get(row).getImporte();
		                        if(suma >= 0)
		                        	pb_negativo = false;
		                        else
		                        	pb_negativo = true;
	                        }
		                    //' Valida si tiene autorizaciï¿½n para sobregiros en la ejecuciï¿½n de traspasos por fondeo
	                        if(pb_negativo){
	                        	result.put("msgUsuario", "La empresa "+parametroDto.get(row).getNoEmpresaBenef()+" no tiene saldo suficiente en coinversion");
	                        	result.put("msgSalio", "salio");
                        	}
                    	}*/
                
                	else if(listMovimiento.get(0).getIdTipoOperacion()== 3807) //'Inversiones
                            pl_operacion = 3707;
                    else if(listMovimiento.get(0).getIdTipoOperacion()== 3808) //'Prestamo
                            pl_operacion = 3708;
                    else if(listMovimiento.get(0).getIdTipoOperacion()== 3809) //'Pago
                            pl_operacion = 3709;
        			
        			if(pb_negativo == false){
                        //'validar que el rubro exista
        				if(listMovimiento.get(0).getIdRubroInt() != 0 && 
        						listMovimiento.get(0).getIdTipoOperacion() != 3800)
        				{
        					List<CatGrupoRubroDto>listRubro = new ArrayList<CatGrupoRubroDto>();
        					listRubro = traspasosDao.validarRubro(listMovimiento.get(0).getIdRubroInt());
        					if(!listRubro.isEmpty())
        						listRubro.clear();
        					else
        					{
        						listRubro.clear();
        						result.put("msgUsuario", "No existe el rubro: " + listMovimiento.get(0).getIdRubroInt());
        						result.put("msgSalio", "salio");
        						return result;
        					}
        				}
        				plEmpresa = listMovimiento.get(0).getNoEmpresa();
        				plLoteEntrada = listMovimiento.get(0).getLoteEntrada() < 0 ? 0 :  listMovimiento.get(0).getLoteEntrada();
                        
        				if(lbBanca){
        					if(listMovimiento.get(0).getInstFinan() != null){
        						if(!listMovimiento.get(0).getInstFinan().equals("")){
                                    piNumBanco = listMovimiento.get(0).getIdBanco();
                                    //lsInstancia = listMovimiento.get(0).getInstFinan();
                                    if(listMovimiento.get(0).getIdBanco() == listMovimiento.get(0).getIdBancoBenef())
                                        pbMismoBanco = true;
                                    else
                                        pbMismoBanco = false;
        						}
        					}
                            else{
                            	result.put("msgUsuario", "No hay clave de banco para el banco:" + 
                            			listMovimiento.get(0).getIdBancoBenef() + "   no se va a crear el archivo");
                                pbEscribi = false;
                                result.put("msgSalio", "salio");
                                return result;
                            }
        				}
        				psFolios = psFolios + parametroDto.get(row).getNoFolioDet() + ",";
        				
        				ps_total = ps_total + Double.parseDouble(funciones.ponerFormatoCeros(parametroDto.get(i).getImporte(), 4));
        				ll_empresa = parametroDto.get(row).getNoEmpresaBenef();
	                        
//                    'NUEVO PROCESO PARA EJECUTAR TRASPASOS
//                    'aqui se ejecuta uno por uno cuando no son agrupados
        				
        				if(bCambioDivisa == false){
        					if(pbAgrupa == false){
        						if(importeParcial == -1){
        							dtoTraspaso.setParametro("N," 
        									+ 0 + "," 
        									+ parametroDto.get(0).getIdUsuario() + "," 
        									+ "," 
        									+ "," 
        									+ "," 
        									+ "," 
        									+ "," 
        									+ "," 
        									+ parametroDto.get(row).getNoFolioDet() + ","
        									);
        							dtoTraspaso.setRegreso("");
        							dtoTraspaso.setMensaje("iniciar traspaso");
        							dtoTraspaso.setResult(0);
        							
        							pdResp = traspasosDao.ejecutarTraspaso(dtoTraspaso, psFolioGroup);
        							
        						}
        						else{
        							dtoTraspaso.setParametro("P," 
        									+ parametroDto.get(row).getImporte() + "," 
        									+ parametroDto.get(0).getIdUsuario() + "," 
        									+ parametroDto.get(row).getIdBanco() + "," 
        									+ parametroDto.get(row).getIdChequera() + "," 
        									+ parametroDto.get(row).getIdBancoBenef() + "," 
        									+ parametroDto.get(row).getIdChequeraBenef() + "," 
        									+ parametroDto.get(row).getIdDivisa() + "," 
        									+ parametroDto.get(0).getTipoCambio() + ","
        									+ parametroDto.get(row).getNoFolioDet() + "," 
        									);
        							dtoTraspaso.setRegreso("");
        							dtoTraspaso.setMensaje("iniciar traspaso");
        							dtoTraspaso.setResult(0);
        							
        							pdResp = traspasosDao.ejecutarTraspaso(dtoTraspaso, psFolioGroup);
                         
        						}
    							if(Integer.parseInt(pdResp.get("result").toString()) != 0){
    								result.put("msgUsuario", "Error en proceso Traspaso " + pdResp.get("result") + 
    										" en el registro " + row);
    								pbPagoParcial = false;
									
    								if(!psFoliosTrasp.equals("")){
		                                  psFolios = psFoliosTrasp;
									}

    							}
    							else{
    								psFoliosTrasp = psFoliosTrasp + psFolioGroup + ",";
    								psFolioGroup = "";
    							}
        					}
        				}
	                        
	                    liNoDocto++;
	                    
	                    if(lbBanca && parametroDto.get(0).getCmbBenef().equals("")) {
	                    	//'LOS LAYOUTS CAMBIAN SI LAS TRANFERENCIAS SON INTERBANCARIAS O AL MISMO BANCO
	                    	
	                    	if(optEnvioBNMX == 4 || optEnvioBNMX == 5)
	                    		piNumBanco = ConstantesSet.CITIBANK_DLS;
	                    	else if(pb_layout_comerica == true)
	                    		piNumBanco = ConstantesSet.COMERICA;
	                    	else
	                    		piNumBanco = pi_banco;
	                       
                        	//piNumBanco=2;
                        	parametroDto.get(row).setOpcionCitiBankPaylinkMN(false);
                        	switch (piNumBanco){
	        						case ConstantesSet.BAJIO:
	        							psRegistro = "L ";
	        							result = creacionArchivosBusiness.escribirArchivoLayout(dirPrincipal, resultArchivo.get("psBanco").toString(),
        		        						resultArchivo.get("nomArchivo").toString(), psRegistro);
        								
        		        				if((Boolean)result.get("resExito")) {
        		        					result.put("msgRegistro", psRegistro);
        		        					pbEscribi = true;
        								}else{
        									result.put("msgUsuario", "Error al Generar Archivo " + resultArchivo.get("psBanco").toString());
        									return result;
        								}
	    							break;
	    							
	        						case ConstantesSet.SANTANDER:
	        							if(parametroDto.get(0).isChkH2HSantander()) {
	                           				impTotDet += parametroDto.get(0).getImporte();
	                           				
	                           				psRegistro = layoutBancomerBusiness.beArmaH2HSantander(obtenerParametrosLayout(parametroDto, i, pbMismoBanco), i, ps_total, parametroDto.size(), false, false);//Arma la cadena
	                    					
	                           				if(psRegistro != null && !psRegistro.equals("")) 
	                            				mapRespExportaReg.put("msgRegistro", psRegistro);
	                           			}else {
	                           				mapRespExportaReg = layoutBancomerBusiness.beExportacion(listMovimiento, 0, piTipo_Traspaso,
	        									resultArchivo.get("nomArchivo").toString(), dirPrincipal, piNumBanco, "TRANSFERENCIA");
	                           			}
	        							
	        							if(!mapRespExportaReg.get("msgRegistro").equals("")) {
	        								result = creacionArchivosBusiness.escribirArchivoLayout(dirPrincipal, resultArchivo.get("psBanco").toString(),
	        		        						resultArchivo.get("nomArchivo").toString(), mapRespExportaReg.get("msgRegistro").toString());
	        								
	        		        				if((Boolean)result.get("resExito")) {
	        		        					result.put("msgRegistro", psRegistro);
	        		        					
	        								}else{
	        									result.put("msgUsuario", "Error al Generar Archivo " + resultArchivo.get("psBanco").toString());
	        									return result;
	        								}
	        							}else {
	        								result.put("msgUsuario", mapRespExportaReg.get("msgUsuario"));
	        							}
	        							pbEscribi = true;
	        							

	    							break;
	        						case ConstantesSet.INVERLAT:
	        							psRegistro = layoutScotiabankInverlatBusiness.beArmaScotiaBankInverlat(listMovimiento.get(i), 0, pbMismoBanco, parametroDto.get(0).isOpcionInternacional());
	                           			if(psRegistro!=null && !psRegistro.equals("")){
	                           				result = creacionArchivosBusiness.escribirArchivoLayout(dirPrincipal,resultArchivo.get("psBanco").toString(),
	                           						resultArchivo.get("nomArchivo").toString(),psRegistro);
	                						if ((Boolean)result.get("resExito")) {
	                							result.put("msgRegistro", psRegistro);
	            								
	        								} else {
	        									result.put("msgUsuario",result.get("msgUsuario").toString());
	                							return result;
	        								}
	                					}else{
	                						result.put("msgUsuario","Error al generar archivo de Banco Azteca.");
	            							return result;
	                					}
	                           			
	    							break;
	        						case ConstantesSet.CITIBANK_DLS:
	        					//		System.out.println("tipo envio bussin "+optEnvioBNMX);
	        					//		System.out.println("H2H bussin "+h2hAfrd);
	        							if ((optEnvioBNMX== 5)&&(h2hAfrd==1)) {
											psRegistro="";
	        								psRegistro += layoutBancomerBusiness.beArmaCitibankPaylinkMN485(listMovimiento, 0, "070005");
	                           				
	        								if(psRegistro != null && !psRegistro.equals("")){ 
	        									result.put("msgRegistro", psRegistro);
	        									result = creacionArchivosBusiness.escribirArchivoLayout(dirPrincipal, resultArchivo.get("psBanco").toString(),
	        											resultArchivo.get("nomArchivo").toString(), result.get("msgRegistro").toString());
	        									parametroDto.get(row).setOpcionCitiBankPaylinkMN(true);
	        								}
	        								else{
	        									result.put("msgUsuario", "No se encontro el folio <folio_paylink>!!");
	        									return result;
	                            			}
	        							}else{
	        								if((optEnvioBNMX==5)&&(h2hAfrd==2)) {			//Opcion de Citibank PayLink MN
	        									//psRegistro="";
	        									System.out.println("entro a citibank sin h2h");
	        									psRegistro = layoutBancomerBusiness.beArmaCitibankPaylinkMN485(listMovimiento, 0, "070005");
		                           			//	System.out.println("registroo... "+psRegistro);
		                           				if(psRegistro != null && !psRegistro.equals("")){ 
		                           					result.put("msgRegistro", psRegistro);
		                           					System.out.println("regristro antes de  escribirArchivo..."+result.get("msgRegistro").toString());
		                           					result = creacionArchivosBusiness.escribirArchivoLayout(dirPrincipal, resultArchivo.get("psBanco").toString(),
		                           							resultArchivo.get("nomArchivo").toString(), result.get("msgRegistro").toString());
		                           					parametroDto.get(row).setOpcionCitiBankPaylinkMN(true);
		                           					result.put("msgRegistro", psRegistro);///agregue esteeee
		                           					System.out.println("registro despues de escribirArchivo..."+result.toString());
		                           					
		                           				}
		                           				else{
		                           					result.put("msgUsuario", "No se encontro el folio <folio_paylink>!!");
		                           					return result;
		                           				}
		        							}else {
		                           				if(optEnvioBNMX == 2)			//Opcion de Citibank WorldLink
		                           					piTipo_Traspaso = 5;
		                           				else if(optEnvioBNMX == 6)	//Opcion de Citibank ACH
		                           					piTipo_Traspaso = 4;
		                           				else if(piTipo_Traspaso == 1) {			//Opcion de Interbancaria
		                           					if(listMovimiento.get(i).getTipoEnvioLayout() != 0)
		                           						piTipo_Traspaso = listMovimiento.get(i).getTipoEnvioLayout();
		                           				//\citibank_FlatFile\cd200030.tx
		                           					//else if(!listMovimiento.get(i).getNomBancoIntermediario().trim().equals(""))
		                           						//piTipo_Traspaso = 3;
		                           					//else
		                           						piTipo_Traspaso = 1;
		                           				}
		                           				System.out.println("entro a oarte falsa de traspasos beexportacion");
		                           				result = layoutBancomerBusiness.beExportacion(listMovimiento, i, piTipo_Traspaso,
		                           						resultArchivo.get("nomArchivo").toString(), dirPrincipal, piNumBanco, "TRANSFERENCIA");
		                           				
		                           				//mapResp.put("msgRegistro", sRegistro);
		                           				
		                           				if(!result.get("msgRegistro").equals("")) {
			        								result = creacionArchivosBusiness.escribirArchivoLayout(dirPrincipal, resultArchivo.get("psBanco").toString(),
			        		        						resultArchivo.get("nomArchivo").toString(), result.get("msgRegistro").toString());
			        								
			        		        				if((Boolean)result.get("resExito")) {
			        		        					result.put("msgRegistro", psRegistro);
			        		        					pbEscribi = true;
			        								}else{
			        									result.put("msgUsuario", "Error al Generar Archivo " + resultArchivo.get("psBanco").toString());
			        									return result;
			        								}
			        							}else {
			        								result.put("msgUsuario", result.get("msgUsuario"));
			        							}
		                           				
		                           				
		                           			}
	        							}
	        							
	        					
	    							break;
	        						case ConstantesSet.INBURSA:

	    							break;
	        						case ConstantesSet.BITAL:
	        							
	        		    					if(pbMismoBanco) {
	        		    						mapRespExportaReg = layoutBancomerBusiness.beArmaHSBCH2HMB(listMovimiento, i, piTipo_Traspaso,
	        		    								resultArchivo.get("nomArchivo").toString(), dirPrincipal, piNumBanco, "TRANSFERENCIA");  //ok
	        		    					}else {
	        		    						mapRespExportaReg = layoutBancomerBusiness.beArmaHSBCH2HIB(listMovimiento, i, piTipo_Traspaso,
	        		    								resultArchivo.get("nomArchivo").toString(), dirPrincipal, piNumBanco, "TRANSFERENCIA");//ok
	        		    						return result;
	        		    					}/*else if(dtoCriBus.getOptTipoEnvio().equals("SPEUA")) {
	        		    					result = layoutBancomerBusiness.beExportacion(listMovimiento, i, piTipo_Traspaso,
	                								mapCreaArch.get("nomArchivo").toString(), dirPrincipal, piBanco, "TRANSFERENCIA");  //ok p
	        		    					}*/
	        		    					if(!mapRespExportaReg.get("msgRegistro").equals("")) {
		        								result = creacionArchivosBusiness.escribirArchivoLayout(dirPrincipal, resultArchivo.get("psBanco").toString(),
		        		        						resultArchivo.get("nomArchivo").toString(), mapRespExportaReg.get("msgRegistro").toString());
		        								
		        		        				if((Boolean)result.get("resExito")) {
		        		        					result.put("msgRegistro", psRegistro);
		        		        					pbEscribi = true;
		        								}else{
		        									result.put("msgUsuario", "Error al Generar Archivo " + resultArchivo.get("psBanco").toString());
		        									return result;
		        								}
		        							}else {
		        								result.put("msgUsuario", mapRespExportaReg.get("msgUsuario"));
		        							}
	        		    					break;
	        						case ConstantesSet.BANAMEX:
	        							System.out.println("Saltamosa dos metodos premero Configura SET y BeArmaBanamex valor del optEvioBnmx:..."+optEnvioBNMX);//Layout - TEF (Transferencia Electronica de Fondos)
	        							//optEnvioBNMX=1;
	        							if(optEnvioBNMX == 1) {
	        								sDescBanco = traspasosDao.consultarConfiguraSet(ConstantesSet.NOMBRE_CARPETA_BANAMEX_TEF);
	        								
	        								 //anterior angelsDescBanco = traspasosDao.consultarConfiguraSet(3109);
	        								 result = layoutBancomerBusiness.beArmaBanamexTEF(pbMismoBanco == false ? true : false,
	        										 listMovimiento, false, "", i, dirPrincipal, sDescBanco);
	        								
	        								return 	result;
	        				   			}else if(optEnvioBNMX == 3) {		//Layout - Mass Payment Citibank

	        				   				parametroDto.get(row).setOpcionCitiBankPaylinkMN(true);
			        						psRegistro += layoutBancomerBusiness.beArmaCitibankPaylinkMN485(listMovimiento, i, "070005");
	        				   				
	        								if(psRegistro != null && !psRegistro.equals("")){ 
	        									result.put("msgRegistro", psRegistro);
	        									result = creacionArchivosBusiness.escribirArchivoLayout(dirPrincipal, resultArchivo.get("psBanco").toString(),
	        											resultArchivo.get("nomArchivo").toString(), result.get("msgRegistro").toString());
	                            				 
	        									return result;
	        								}
	        				   				else{
	                            				result.put("msgUsuario", "No se encontro el folio <folio_paylink>!!");
	                            				logger.error("No se encontrol el folio <folio_paylink>");
	                           					return result;
	                            			}
	        				   				
	        				   				/* estaba antes para masspaymet de momento simular con paylink
	        				   				result = layoutBancomerBusiness.beArmaBanamexMassPay(pbMismoBanco == false ? true : false, 
	        				   						listMovimiento, false, "", i, dirPrincipal);
	        								return result;
	        								*/
	        				   			}
	        				   			else if(parametroDto.get(row).isOpcionMismoBanco()) {
	        								/*ESTE ESTABA ANTES 
	        								 //   mapRespExportaReg = layoutBancomerBusiness.beExportacion(listMovimiento, i, piTipo_Traspaso,
	        										resultArchivo.get("nomArchivo").toString(), dirPrincipal, piNumBanco, "TRANSFERENCIA");
	        								 */
	        								
	        				   					mapRespExportaReg = layoutBancomerBusiness.beExportacion(listMovimiento, i, piTipo_Traspaso,
	        				   							resultArchivo.get("nomArchivo").toString(), dirPrincipal, piNumBanco, "TRASPASO");
	        								
	        				   					if(psRegistro != null && !psRegistro.equals("")) {
	        				   						mapRespExportaReg.put("msgRegistro", psRegistro);
	        				   					}
	        				   					else{
	        				   						logger.error("Error al armar Banamex mismo banco");
	        				   						result.put("msgUsuario", "Error al armar Banamex mismo banco");
	        				   					}
	        				   				}
	        							else{
	                            			psRegistro = layoutBancomerBusiness.beArmaBanamexInterT(parametroDto, i);
	                            			parametroDto.get(row).setOpcionCitiBankPaylinkMN(false);
	        								
	                            			if(psRegistro != null && !psRegistro.equals("")){ 
	                            				mapRespExportaReg.put("msgRegistro", psRegistro);
	                            			
	                            			}else{
	                            				result.put("msgUsuario", "Error al armar Banamex Interbancario");
	                            				mapRespExportaReg.put("msgRegistro", "");
	                            				return result;
	                            			}
	                            		}
	        							
	        							if(!mapRespExportaReg.get("msgRegistro").equals("")) {
	        								result = creacionArchivosBusiness.escribirArchivoLayout(dirPrincipal, resultArchivo.get("psBanco").toString(),
	        		        						resultArchivo.get("nomArchivo").toString(), mapRespExportaReg.get("msgRegistro").toString());
	        								
	        		        				if((Boolean)result.get("resExito")) {
	        		        					result.put("msgRegistro", psRegistro);
	        		        					pbEscribi = true;
	        								}else{
	        									result.put("msgUsuario", "Error al Generar Archivo " + resultArchivo.get("psBanco").toString());
	        									return result;
	        								}
	        							}else {
	        								result.put("msgUsuario", mapRespExportaReg.get("msgUsuario"));
	        							}

	    							break;
	        						case ConstantesSet.BANCOMER:
	        							if(parametroDto.get(row).isOpcionMismoBanco()) {
	        								for(int y=0; y<traspasosIguales.size(); y++) {
        										if(traspasosIguales.get(y).get("chequera").equals(parametroDto.get(i).getIdChequera()) &&
	        											traspasosIguales.get(y).get("chequeraB").equals(parametroDto.get(i).getIdChequeraBenef()) &&
	        											!parametroDto.get(i).isBSumImp()) {
        											double imp = Double.parseDouble(traspasosIguales.get(y).get("importeTot"));
	        										parametroDto.get(i).setImporte(imp);
	        										break;
	        									}
	        								}
	        								
                        					//Esta llamada es para el archivo de Cash Windows
	        								//psRegistro=layoutBancomerBusiness.beArmaBancomer(obtenerParametrosLayout(parametroDto,i, pbMismoBanco));//arma cadena
	                    					
	        								//Esta llamada es para el archivo de Net CashC43
                        					psRegistro = layoutBancomerBusiness.beArmaBancomerC43(obtenerParametrosLayout(parametroDto, i, pbMismoBanco));//Arma la cadena
                        					
	        								if(psRegistro!=null && !psRegistro.equals("")) {
	        									if(!parametroDto.get(i).isBSumImp()) {
//		                    						
	        										mapRespExportaReg.put("msgRegistro", psRegistro);
		                            				result = creacionArchivosBusiness.escribirArchivoLayout(dirPrincipal, resultArchivo.get("psBanco").toString(),
			        		        						resultArchivo.get("nomArchivo").toString(), mapRespExportaReg.get("msgRegistro").toString());
			        								
			        		        				if((Boolean)result.get("resExito")) {
			        		        					result.put("msgRegistro", psRegistro);
			        		        					pbEscribi = true;
			        		        					//lb_abierto_bancomer = true;
			        								}else{
			        									result.put("msgUsuario", "Error al Generar Archivo " + resultArchivo.get("psBanco").toString());
			        									return result;
			        								}
	        										
	        										
	        									}
	                    						pbEscribi = true;
	                    					}
	        							}
	        							else if(parametroDto.get(row).isOpcionInternacional()){
	        								System.out.println("caso internacional");
	        								psRegistro = layoutBancomerBusiness.beArmaBancomerInternacionalT
	        										(obtenerParametrosLayout(parametroDto, i, pbMismoBanco),parametroDto.get(i));//Arma la cadena
                        					
	        								if(psRegistro != null && !psRegistro.equals("")) {
	                            				mapRespExportaReg.put("msgRegistro", psRegistro);
	                            				result = creacionArchivosBusiness.escribirArchivoLayout(dirPrincipal, resultArchivo.get("psBanco").toString(),
		        		        						resultArchivo.get("nomArchivo").toString(), mapRespExportaReg.get("msgRegistro").toString());
		        								
		        		        				if((Boolean)result.get("resExito")) {
		        		        					result.put("msgRegistro", psRegistro);
		        		        					pbEscribi = true;
		        		        					//lb_abierto_bancomer = true;
		        								}else{
		        									result.put("msgUsuario", "Error al Generar Archivo " + resultArchivo.get("psBanco").toString());
		        									return result;
		        								}
				        					}else {
			    								result.put("msgUsuario", "Error al generar Layout Aztaca.");
			    							}
	        								
	        							}
	        							else{
	        								//Este es para net cash
		                                    psRegistro = layoutBancomerBusiness.beArmaBancomerC43(obtenerParametrosLayout(parametroDto, i, parametroDto.get(0).isOpcionMismoBanco()));//Arma la cadena
		                                    
		                                    
		                                    if(psRegistro!=null && !psRegistro.equals("")){
		                                    	
		                                    	mapRespExportaReg.put("msgRegistro", psRegistro);
	                            				result = creacionArchivosBusiness.escribirArchivoLayout(dirPrincipal, resultArchivo.get("psBanco").toString(),
		        		        						resultArchivo.get("nomArchivo").toString(), mapRespExportaReg.get("msgRegistro").toString());
		        								
		        		        				if((Boolean)result.get("resExito")) {
		        		        					result.put("msgRegistro", psRegistro);
		        		        					pbEscribi = true;
		        		        					//lb_abierto_bancomer = true;
		        								}else{
		        									result.put("msgUsuario", "Error al Generar Archivo " + resultArchivo.get("psBanco").toString());
		        									return result;
		        								}
		                                    	
	                    						pbEscribi = true;	
	                    					}
	        							}
	    							break;
	        						
	        						case ConstantesSet.AZTECA:
		        							System.out.println("antes de azteca");
		        							psRegistro = layoutAztecaBusiness.beArmaAztecaT(parametroDto.get(i), i, pbMismoBanco, parametroDto.get(row).isOpcionInternacional());
		        							System.out.println("despues de azteca"+psRegistro);
		        							if(psRegistro != null && !psRegistro.equals("")) {
	                            				mapRespExportaReg.put("msgRegistro", psRegistro);
	                            				result = creacionArchivosBusiness.escribirArchivoLayout(dirPrincipal, resultArchivo.get("psBanco").toString(),
		        		        						resultArchivo.get("nomArchivo").toString(), mapRespExportaReg.get("msgRegistro").toString());
		        								System.out.println("mapa "+result.get("resExito"));
		        		        				if((Boolean)result.get("resExito")) {
		        		        					result.put("msgRegistro", psRegistro);
		        		        					pbEscribi = true;
		        								}else{
		        									result.put("msgUsuario", "Error al Generar Archivo " + resultArchivo.get("psBanco").toString());
		        									return result;
		        								}
				        					}else {
			    								result.put("msgUsuario", "Error al generar Layout Aztaca.");
			    							}
	        								
	    							break;
	        						case ConstantesSet.BANORTE:
	        							psRegistro = layoutBanorteBusiness.beArmaBanorteT(parametroDto.get(i), i, pbMismoBanco, parametroDto.get(row).isOpcionInternacional());
	                           			if(psRegistro!=null && !psRegistro.equals("")){
	                           				result = creacionArchivosBusiness.escribirArchivoLayout(dirPrincipal,resultArchivo.get("psBanco").toString(),
	                           						resultArchivo.get("nomArchivo").toString(),psRegistro);
	                						if ((Boolean)result.get("resExito")) {
	                							result.put("msgRegistro", psRegistro);
	                							pbEscribi = true;
	        								} else {
	        									result.put("msgUsuario",result.get("msgUsuario").toString());
	                							return result;
	        								}
	                					}else{
	                						result.put("msgUsuario","Error al generar archivo de Banorte.");
	            							return result;
	                					}
	    							break;
	        					} /**end switch*/
	                        }
        			} /**if negativos*/
        			else
        				listMovimiento.clear(); /**else de los negativos*/
                }/**listMovimiento.size() > 0*/
                else
                	listMovimiento.clear(); /**else de listMovimiento.size() > 0*/
              im=im+parametroDto.get(i).getImporte();

	        }/**termina for*/// reiniciamos el psp registro para no pegar cosas
	        psRegistro="";
	        System.out.println("registro despues del for "+psRegistro);
	        if(!psFolios.equals("")){
	            psFolios = psFolios.substring(0, psFolios.length() - 1);
	        }
        
	        if(pb_mensaje_fondeo == true){
	            psFoliosNeg = psFoliosNeg.substring(0, psFoliosNeg.length() - 1);
	            result.put("msgUsuario","El saldo de coinversion es negativo para los folios " + psFoliosNeg);
	        }
	        
	        if(!psFoliosRep.equals("")){
	        	psFoliosRep = psFoliosRep.substring(0, psFoliosRep.length() - 1);
	        	result.put("msgUsuario", "Partidas duplicadas en los folios: " + psFoliosRep);
	        }
	        
	        if(piNumBanco == ConstantesSet.COMERICA){ //'Inserta el trailer para el layout de COMERICA
	        }
	        
	        else if(parametroDto.get(0).isOpcionCitiBankPaylinkMN() == true){
        		psRegistro = layoutBancomerBusiness.Trailer_citibank_paylink_MN(parametroDto.size(),im,parametroDto.get(0).isOpcionCitiBankPaylinkMN()); 		
        		if(psRegistro != null && !psRegistro.equals("")){ 
        			result.put("msgRegistro", psRegistro);
        			System.out.println("registroo despues del trailer... "+result.get("msgRegistro").toString());
        			result = creacionArchivosBusiness.escribirArchivoLayout(dirPrincipal, resultArchivo.get("psBanco").toString(),
        					resultArchivo.get("nomArchivo").toString(), result.get("msgRegistro").toString());
					//System.out.println("nombre archivo "+resultArchivo.get("nomArchivo").toString());
					//System.out.println("banco "+resultArchivo.get("psBanco").toString());
					//System.out.println("contenido final archivo"+result.toString());
        			String ruta=dirPrincipal+resultArchivo.get("psBanco").toString()+resultArchivo.get("nomArchivo").toString();
        			
        			System.out.println("ruta "+ruta);
        			System.out.println("H2H ANTES DE RENOMBRAR "+h2hAfrd);
        			if(h2hAfrd==1){
        				try {
        					System.out.println("entro a cambiar el nombre del archivo");
        					String cadenaR=resultArchivo.get("nomArchivo").toString();
        					String cadena2=null;
        					System.out.println("cadenaR "+cadenaR);
        					cadena2=cadenaR.replace("PA", "PAGOSDIARIOS");
   //     					System.out.println("cadena2 "+cadena2);
        					String ruta2=dirPrincipal+resultArchivo.get("psBanco").toString()+cadena2;
     //   					System.out.println("ruta2 "+ruta2);
        					File archivo= new File(ruta);
        					archivo.renameTo(new File(ruta2));
        					ruta="";
        				}
        				catch (Exception e) {
        					logger.error(e);
        				}
        			}
        		}
        		else{
        			result.put("msgUsuario", "No se armo el trailer!!");
        			logger.error("No se armo el trailer");
        			return result;
        		}
        	}
        	else if(lbEnvioBitalH2H == true && parametroDto.get(0).isOpcionBitalH2H() == true && 
        			parametroDto.get(0).isOpcionInterbancaria() == true){

        	}
        	else if(pbBE_Bital == true && parametroDto.get(0).isOpcionSpeua() == false)	{
        		
        	}
        	else if(parametroDto.get(0).isOpcionBanamexMassPayment() == true){

        	}
        	else if(parametroDto.get(0).isOpcionBanamexTEF() == true){

        	}
	            
        	if(pbEscribi == false){ //'si no escribio ningun registro se sale
        		result.put("msgUsuario", "No se escribio ningun arhivo");
        	}
        	
        	if(psFoliosNeg.trim().length() > 0){

        	}
        	
        	if(psFolios.trim().length() > 0){
        		// 'PROCEDIMIENTO PARA GUARDAR EN LAS TABLAS DE CONTROL DE ARCHIVOS
        		if(!psFolioGroup.equals("")){
                    psFolios = psFolioGroup;
        		}
        		
        		result.put("archivo", resultArchivo.get("nomArchivo").toString());
        		
        		if(guardarArchivo(resultArchivo.get("nomArchivo").toString(),psFolios, pbPagoParcial,ps_total,
        				parametroDto.get(0).getIdUsuario(), LI_BANCO, parametroDto.get(0).isOpcionCitiBankACH(),
        				parametroDto.get(0).isOpcionMismoBanco(), parametroDto.get(0).isOpcionBanamexMassPayment(),
        				parametroDto.get(0).isChkH2HSantander()))
        		{
        			result.put("msgUsuario","Los Registros han sido Guardados en el archivo "  +
        					resultArchivo.get("nomArchivo").toString());
        		}
        	}
        	
			String salida="";
			if(pi_banco==14 && parametroDto.get(0).isChkH2HSantander()){
				/////////////////////////////daniel////////////////////////////////////////
				//String psRutaArchivos = ConstantesSet.CARPETA_RAIZ_RESPALDO.substring(0,ConstantesSet.CARPETA_RAIZ_RESPALDO.length()-1) + "\\santander_h2h\\";
				String psRutaArchivos = ConstantesSet.CARPETA_RAIZ_RESPALDO + "\\santander_h2h\\";
				
				//psRutaArchivos = dirPrincipal;
				String psSoloArchivos = resultArchivo.get("nomArchivo").toString();
				//StringBuffer mapFtp = creacionArchivosBusiness.creaArchivoFtp(psSoloArchivos);  
				//creacionArchivosBusiness.escribirArchivoLayout("C:\\apicifrado\\cifradoSalida\\", "", "ftp" +  globalSingleton.getUsuarioLoginDto().getIdUsuario()  + ".bat", mapFtp.toString());
				salida = creacionArchivosBusiness.enviaH2H(psRutaArchivos, psSoloArchivos);	 
			}
			else{ 
				result.put("msgUsuario", "No se realizo ninguna tarea");
			}
		}
		catch(Exception e){
			e.printStackTrace();
			logger.error(e);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Traspasos, C:TraspaosBusiness, M:aceptar-ejecutarSolicitudTraspaso");
		}
		return result;
	}
	
	public ParametrosLayoutDto obtenerParametrosLayout(List<BuscarSolicitudesTraspasosDto> list, int i, boolean mismoBanco){
		
		try{
			
			//String pBenef = list.get(i).getPlazaBenef() != null ? list.get(i).getPlazaBenef() : "0";
			//int plazaBenef = !pBenef.equals("") ? Integer.parseInt(pBenef) : 0;
			
			ParametrosLayoutDto paramLayDto = new ParametrosLayoutDto();
			
			paramLayDto.setMismoBanco(mismoBanco);
			paramLayDto.setIdChequeraBenef(list.get(i).getIdChequeraBenef());
			paramLayDto.setPiPlaza(list.get(i).getPlaza());
			//paramLayDto.setPiPlazaBenef(plazaBenef);
			paramLayDto.setIdChequera(list.get(i).getIdChequera());
			paramLayDto.setImporte(list.get(i).getImporte());
			paramLayDto.setBeneficiario(list.get(i).getBeneficiario());
			paramLayDto.setIdBancoBenef(list.get(i).getIdBancoBenef()>0?""+list.get(i).getIdBancoBenef():"");
			paramLayDto.setNoFolioDet(list.get(i).getNoFolioDet());
			paramLayDto.setPsSucursal(list.get(i).getSucursalDestino());
			paramLayDto.setPsDivisa(list.get(i).getIdDivisa());
			paramLayDto.setPsReferenciaTraspaso("70005");
			paramLayDto.setNoCliente(list.get(i).getNoCliente()!=null?Integer.parseInt(list.get(i).getNoCliente()):0);
			paramLayDto.setPsClabe(list.get(i).getClabeBenef());
			paramLayDto.setConcepto(list.get(i).getConcepto());
			paramLayDto.setRfcBenef(list.get(i).getRfcBenef());
			
			System.out.println("Los parametros paramLayDto:  "  + paramLayDto.getImporte() + " ," + paramLayDto.getNoFolioDet());
			
			return paramLayDto;
			
	
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+"P:Traspasos, C:TraspasosBusiness, M:parametrosLayout");
			return null;
		}
		
	}
	
	public boolean guardarArchivo(String archivo, String folios, boolean pagoParcial, double total, 
			int usuario, int banco, boolean opCiti, boolean mBanco, boolean pbMasspayment, boolean h2hSantander){
		
		boolean guardaArchivo = true;
		int piRegistros = 0;
		double pdImporte = 0;
		String psNomArchivo = "";
		String psTipoEnvio = "";
		List<MovimientoDto>listReg = new ArrayList<MovimientoDto>();
		MovimientoDto regDto = new MovimientoDto();
		try {
			listReg = traspasosDao.seleccionarDetalleArchivo(folios);
			if(listReg.size() > 0){
				for(int i = 0; i < listReg.size(); i++){

	               pdImporte = listReg.get(i).getImporte();
	               if(!h2hSantander)
	            	   archivo = archivo.substring(0, 8);
		    		else
		    			archivo = archivo.replace(".in", "");
			    		
//		            ' Agrega la funcionalidad de Traspaso Parcial
		            regDto.setNoFolioDet(listReg.get(i).getNoFolioDet());
		            regDto.setNoDocto(listReg.get(i).getNoDocto());
		            regDto.setFecValor(traspasosDao.obtenerFechaHoy());
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
		            traspasosDao.insertarDetArchTransfer(regDto, archivo);
		            piRegistros = piRegistros + 1; //'dgs, 22/11/06
				}
		        traspasosDao.insertarArchTransfer(archivo, banco, traspasosDao.obtenerFechaHoy(), total, piRegistros, usuario);
		        
			}
			else{
				guardaArchivo = false;
			}
			
//		    'Insertar en movimiento  el tipo de envio
			if(banco == ConstantesSet.CITIBANK_DLS && opCiti == true)
		        psTipoEnvio = "ACH";
		    else if(mBanco == true)
		        psTipoEnvio = "MB";
		    else
		        psTipoEnvio = "IB";
		   
		    psNomArchivo = archivo.substring(0, 8);
		    traspasosDao.actualizarMovimientoTipoEnvio(psTipoEnvio, psNomArchivo, pbMasspayment);

		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + 
					"P: Traspasos, C:TraspasosBusiness, M:guardarArchivo");
			e.printStackTrace();
		}
		return guardaArchivo;
	}
	
	public JRDataSource reporteDetArchTraspInv(String archivo){
		JRMapArrayDataSource jrDataSource = null;
		List<Map<String, Object>> listReport = new ArrayList<Map<String, Object>>();
		System.out.println("reporte bussines");
		try {
			listReport = traspasosDao.reporteDetArchTraspInv(archivo);
	        jrDataSource = new JRMapArrayDataSource(listReport.toArray());
	        
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + 
		e.toString() + "P: Traspasos, C:TraspasosBusiness, "
				+ "M:reporteDetArchTraspInv");
		}
		return jrDataSource;
	}
	
	public List<LlenaComboEmpresasDto> llenaComboEmpresas (int noUsuario){
		return traspasosDao.llenaComboEmpresas(noUsuario);
	}
	
	public String cancelaMovimiento(String foliosCancelados){
		String mensaje = "";
		int resultado = 0;
		try{
			foliosCancelados = foliosCancelados.substring(0, foliosCancelados.length() - 1);
			resultado = traspasosDao.cancelaMovimientos(foliosCancelados);
			
			if (resultado > 0)
				mensaje = "Traspasos eliminados con exito";
			else
				mensaje = "Ocurrio un error en el proceso";
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Traspasos, C: TraspasosBusiness, M: cancelaMovimiento");
		}return mensaje;
	}
	
	public int validaFacultad(int idFacultad) {
		return traspasosDao.validaFacultad(idFacultad);
	}
	
	public String autoDesAuto(List<Map<String, String>> datos, String autoDesAuto, String pass) {
		String mensaje = "Autorizaciï¿½n correcta";
		String usrModif = "";
		int usr = 0;
		int usrAux = 0;
		int res = 0;
		//boolean existe;
		
		List<MovimientoDto> list = new ArrayList<MovimientoDto>();
		globalSingleton = GlobalSingleton.getInstancia();
		
		try {
			usr = globalSingleton.getUsuarioLoginDto().getIdUsuario();
			res = traspasosDao.validaPassword(usr, funciones.encriptador(pass));
			
			if(res == 0) return "Contraseï¿½a Incorrecta";
			
			for(int i=0; i<datos.size(); i++) {
				usrAux = usr;
				list = traspasosDao.validaAuto(Integer.parseInt(datos.get(i).get("noFolioDet")));
				
				if(list.size() > 0) {
					if(autoDesAuto.equals("AUT")) {
						if(list.get(0).getUsuarioAlta() == usrAux || list.get(0).getUsuarioModif() == usrAux)
							return "Usted ya autorizo este traspaso Folio Det: " + datos.get(i).get("noFolioDet");
						else if(list.get(0).getUsuarioAlta() == 0 && list.get(0).getUsuarioModif() == usrAux)
							return "El traspaso requiere de la autorizaciï¿½n del nivel uno para continuar";
						if(list.get(0).getUsuarioAlta() == 0)
							usrModif = "usuario_uno";
						else
							usrModif = "usuario_dos";
					}else if(autoDesAuto.equals("DAUT")) {
						if(list.get(0).getUsuarioAlta() != usrAux && list.get(0).getUsuarioModif() != usrAux)
							return "Usted no ha autorizado este traspaso, no puede quitar una autorizaciï¿½n que no ha proporcionado";
						else if(list.get(0).getUsuarioAlta() == usrAux && list.get(0).getUsuarioModif() != 0)
							return "No puede quitar la primer autorizaciï¿½n hasta que se elimine la segunda";
						else if(list.get(0).getUsuarioAlta() == usrAux)
							usrModif = "usuario_uno";
						else if(list.get(0).getUsuarioModif() == usrAux)
							usrModif = "usuario_dos";
						usrAux = 0;
						mensaje = "Se ha eliminado la autorizaciï¿½n";
					}
					traspasosDao.autoDesAuto(Integer.parseInt(datos.get(i).get("noFolioDet")), usrModif, usrAux);
				}else
					traspasosDao.insertAutorizacionesMov(Integer.parseInt(datos.get(i).get("noFolioDet")), usrAux);
			}
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Traspasos, C: TraspasosBusiness, M: autoDesAuto");
		}
		return mensaje;
	}
	
	public List<GrupoDTO> llenarComboGrupo(String idTipoGrupo) {
		
		List<GrupoDTO> grupos= null;
		
		try {
			
			grupos = traspasosDao.llenarComboGrupo(idTipoGrupo);
			
		} catch (Exception e) {
			
		}
		
		return grupos;
		
	}//End methodo llenarComboRubro
	
    public List<GrupoDTO> llenarComboGrupoVX(String idTipoGrupo, int noEmpresa) {
		
		List<GrupoDTO> grupos= null;
		
		try {
			
			grupos = traspasosDao.llenarComboGrupoVX(idTipoGrupo, noEmpresa);
			
		} catch (Exception e) {
			
		}
		
		return grupos;
		
	}//End methodo llenarComboRubro
	
	public List<RubroDTO> llenarComboRubro(int idGrupo, int noEmpresa) {
		
		List<RubroDTO> rubros= null;
			
		rubros = traspasosDao.llenarComboRubro(idGrupo, noEmpresa);
		
		return rubros;
		
	}//End methodo llenarComboRubro
	
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
	public LayoutScotiabankInverlatBusiness getLayoutScotiabankInverlatBusiness() {
		return layoutScotiabankInverlatBusiness;
	}
	public void setLayoutScotiabankInverlatBusiness(LayoutScotiabankInverlatBusiness layoutScotiabankInverlatBusiness) {
		this.layoutScotiabankInverlatBusiness = layoutScotiabankInverlatBusiness;
	}
	public LayoutAztecaBusiness getLayoutAztecaBusiness() {
		return layoutAztecaBusiness;
	}

	public void setLayoutAztecaBusiness(LayoutAztecaBusiness layoutAztecaBusiness) {
		this.layoutAztecaBusiness = layoutAztecaBusiness;
	}
	
	
	
	
	
	public String comandosH2H(String psRutaArchivos,String psSoloArchivos) throws IOException{
		 String salida ="";	  
		 String comando = "cmd /c move " + " "+  psRutaArchivos+psSoloArchivos +" " + " C:\\apicifrado\\CifradoEntrada\\" ;
		 //String cmd = envioTransferenciasDao.obtenerComando();
		 String comand = "cmd /k " + traspasosDao.consultarConfiguraSet(500) + " C:\\apicifrado\\CifradoEntrada\\" +psSoloArchivos +" " + " C:\\apicifrado\\CifradoSalida\\" + psSoloArchivos ;
		 String comandr ="cmd /c rename C:\\apicifrado\\CifradoEntrada\\" +psSoloArchivos +" "+ psSoloArchivos.substring(0, psSoloArchivos.length()-3) +".bak" ;
		 String comandoCopiaArch = "cmd /c copy C:\\apicifrado\\CifradoEntrada\\" + psSoloArchivos.substring(0, psSoloArchivos.length()-3) +".bak" +" " + " C:\\apicifrado\\old" ;
		 String comandCambiaExt ="cmd /c rename C:\\apicifrado\\old\\" +psSoloArchivos.substring(0, psSoloArchivos.length()-3) +".bak" +" "+ psSoloArchivos.substring(0, psSoloArchivos.length()-3) +".old" ;
		 String comandoEliminar ="cmd /c del /f /q  C:\\apicifrado\\CifradoEntrada\\"+psSoloArchivos.substring(0, psSoloArchivos.length()-3) +".occ";
		 String comandoFtp="cmd /c C:\\apicifrado\\cifradoSalida\\ftp.bat";
		 if (comando != null){
			 // Ejecucion Basica del Comando
		     Process proceso = Runtime.getRuntime().exec(comando);
		     InputStreamReader entrada = new InputStreamReader(proceso.getInputStream());
		     BufferedReader stdInput = new BufferedReader(entrada);
		     //Si el comando tiene una salida la mostramos
		     if((salida=stdInput.readLine()) != null){
		    	 System.out.println("Movimiento correctamente");
			     while ((salida=stdInput.readLine()) != null){
			     	System.out.println(salida);
			     }
			 }else{
				 salida="Error al Generar Archivo";
			     comando= null;	
			 }
		 }
		 if (comand != null && comando != null ){
			 // Ejecucion Basica del Comando
			 Process proceso = Runtime.getRuntime().exec(comand);
			 InputStreamReader entrada = new InputStreamReader(proceso.getInputStream());
			 BufferedReader stdInput = new BufferedReader(entrada); 
			 //Si el comando tiene una salida la mostramos
			 if((salida=stdInput.readLine()) != null){
				 System.out.println("Comando ejecutado Correctamente para cifrar");
				 while ((salida=stdInput.readLine()) != null){
					 System.out.println(salida);
				 }
			 }else{
				 salida="Error al Generar Archivo";
				 comand= null;	
			 }
		 } 
		 else{
			 comand= null;
		 }
		 if (comando != null && comand != null && comandr != null){
			 // Ejecucion Basica del Comando
			 Process proceso = Runtime.getRuntime().exec(comandr);
			 InputStreamReader entrada = new InputStreamReader(proceso.getInputStream());
			 BufferedReader stdInput = new BufferedReader(entrada);
			 //Si el comando tiene una salida la mostramos
			 if((salida=stdInput.readLine()) == null){
				 System.out.println("Comando ejecutado Correctamente para el bak");
				 while ((salida=stdInput.readLine()) != null){
					 System.out.println(salida);
				 }
			 }else{
				 salida="Error al Generar Archivo";
				 comandr= null;	
			 }
			 //////////////COMANDOS NUEVOS DANIEL//////////////////////////
			 // Ejecucion Basica del Comando
			 Process procesoFtp = Runtime.getRuntime().exec(comandoCopiaArch);
			 InputStreamReader entradaFtp = new InputStreamReader(procesoFtp.getInputStream());
			 BufferedReader stdInputFtp = new BufferedReader(entradaFtp);
			 if((salida=stdInput.readLine()) == null){
				 System.out.println("Copia de archivos a la carpeta OLD Exitosa");
				 while ((salida=stdInput.readLine()) != null){
					 System.out.println(salida);
				 }
			 }else{
				 salida="Error al Generar Archivo";
				 comando= null;	
			 }            
			 procesoFtp = Runtime.getRuntime().exec(comandCambiaExt);
			 entradaFtp = new InputStreamReader(procesoFtp.getInputStream());
			 stdInputFtp = new BufferedReader(entradaFtp);
			 if((salida=stdInput.readLine()) == null){
				 System.out.println("Rename del archivo OLD exitoso");
				 salida="";
				 while ((salida=stdInput.readLine()) != null){
					 System.out.println(salida);
				 }
			 }else{
				 salida="Error al Generar Archivo";
				 comando= null;	
			 }    
			 procesoFtp = Runtime.getRuntime().exec(comandoFtp);
			 entradaFtp = new InputStreamReader(procesoFtp.getInputStream());
			 stdInputFtp = new BufferedReader(entradaFtp);
			 //Si el comando tiene una salida la mostramos
			 if(stdInputFtp.readLine()!= null){
				 System.out.println("Ejecucion del comando BAT exitosa");
			 }else{
				 salida="Error al Generar Archivo";
				 comando= null;	
			 }
			 procesoFtp = Runtime.getRuntime().exec(comandoEliminar);
			 entradaFtp = new InputStreamReader(procesoFtp.getInputStream());
			 stdInputFtp = new BufferedReader(entradaFtp);
			 if((salida=stdInput.readLine()) == null){
				 System.out.println("ELIMINACION DE ARCHIVOS DE CIFRADO ENTRADA");
				 salida="";
				 while ((salida=stdInput.readLine()) != null){
					 System.out.println(salida);
					 salida="";
				 }
			 }else{
				 salida="Error al Generar Archivo";
				 comando= null;	
			 }
		                ///////////////COMANDOS NUEVOS DANIEL////////////////////////
		 }
		 else{
			 comandr= null;
		 }
		 creacionArchivosBusiness.eliminarArchivo("C:\\apicifrado\\", "cifradoSalida\\","ftp.bat");
		 return salida;
	}
	
	
}//End class 

