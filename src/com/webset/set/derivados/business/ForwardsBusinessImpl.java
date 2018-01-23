
package com.webset.set.derivados.business;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.webset.set.derivados.dao.ForwardsDao;
import com.webset.set.derivados.dao.impl.ForwardsDaoImpl;
import com.webset.set.derivados.dto.DatosExcelFwsDto;
import com.webset.set.derivados.service.ForwardsService;
import com.webset.set.egresos.dao.ConfirmacionCargoCtaDao;
import com.webset.set.egresos.dto.ResultadoDto;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.ConstantesSet;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utilerias.dto.GeneradorDto;
import com.webset.set.utilerias.dto.LlenaComboEmpresasDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.set.utilerias.dto.LlenarConsultaFws;
import com.webset.utils.tools.Utilerias;
import com.webset.set.utilerias.GlobalSingleton;


public class ForwardsBusinessImpl implements ForwardsService{
	private ForwardsDao forwardsDao;
	Funciones funciones = new Funciones();
	Bitacora bitacora   = new Bitacora();
	private static Logger logger = Logger.getLogger(ForwardsDaoImpl.class);
	private GlobalSingleton globalSingleton = new GlobalSingleton();
	SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
	

	public List<LlenaComboEmpresasDto> obtenerEmpresas(int iEmpresaRaiz){
		List<LlenaComboEmpresasDto> listEmp = new ArrayList<LlenaComboEmpresasDto>();
		try
		{
			listEmp = forwardsDao.consultarEmpresas(iEmpresaRaiz);
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P: BarridosFondeos C: PrestamosInterempresasBusinessImpl M: obtenerEmpresasHijo");
		}
		return listEmp;
	}
	
	@Override
	public String consultaBanco(int id_banco) {
		String mensaje = "";
		List<DatosExcelFwsDto> datosExcelDtos = new ArrayList<DatosExcelFwsDto>();
		try {
			mensaje = forwardsDao.consultaBanco(id_banco);
		}catch (Exception e) {
			logger.error(e.toString());	
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P: derivados C: ForwardsBusinessImpl M:consultaBanco");
		}
		return mensaje;
	}


	@Override
	public List<DatosExcelFwsDto> validarDatosExcel(String datos) {
		List<DatosExcelFwsDto> datosExcelDtos = new ArrayList<DatosExcelFwsDto>();
		try {
			datosExcelDtos = forwardsDao.validarDatosExcel(datos);
		}catch (Exception e) {
			logger.error(e.toString());	
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P: derivados C: ForwardsBusinessImpl M: validarDatosExcel");
		}
		return datosExcelDtos;
	}


	@Override
	public String insertaForward(String matrizDatos, int usuario) {
		String mensaje = "";
		List<DatosExcelFwsDto> datosExcelDtos = new ArrayList<DatosExcelFwsDto>();
		try {
			datosExcelDtos = forwardsDao.datosFwsFaltantes(matrizDatos, usuario);

			for (int i = 0; i < datosExcelDtos.size(); i++) {
				// INSERTAR EN LA TABLA DE CONTROL FORWARDS
				forwardsDao.insertarFwdCarga(datosExcelDtos.get(i).getFolio(), datosExcelDtos.get(i).getUnidad_negocio(), datosExcelDtos.get(i).getDivisa_venta(), 
						datosExcelDtos.get(i).getBanco_cargo(), datosExcelDtos.get(i).getChequera_cargo(), datosExcelDtos.get(i).getDivisa_compra(),
						datosExcelDtos.get(i).getBanco_abono(), datosExcelDtos.get(i).getChequera_abono(), datosExcelDtos.get(i).getForma_pago(), 
						datosExcelDtos.get(i).getImporte_pago(), datosExcelDtos.get(i).getImporte_compra(), datosExcelDtos.get(i).getTc(), 
						datosExcelDtos.get(i).getFec_compra(), datosExcelDtos.get(i).getFec_vto(), datosExcelDtos.get(i).getInstitucion(), 
						datosExcelDtos.get(i).getNom_contacto(), datosExcelDtos.get(i).getBanco_benef(), datosExcelDtos.get(i).getChequera_benef(),
						datosExcelDtos.get(i).getRubro_cargo(), datosExcelDtos.get(i).getSubrubro_cargo(), datosExcelDtos.get(i).getRubro_abono(), 
						datosExcelDtos.get(i).getSubrubro_abono(), datosExcelDtos.get(i).getEstatus_mov(), datosExcelDtos.get(i).getEstatus_imp(), 
						datosExcelDtos.get(i).getFirmante1(), datosExcelDtos.get(i).getFirmante2(), datosExcelDtos.get(i).getNo_docto(), 
						datosExcelDtos.get(i).getSpot(), datosExcelDtos.get(i).getPuntos_forward(),
						datosExcelDtos.get(i).getReferencia(),datosExcelDtos.get(i).getConcepto());



				globalSingleton = GlobalSingleton.getInstancia();
				String fechaHoy = formato.format(globalSingleton.getFechaHoy());

				int id_caja  = datosExcelDtos.get(i).getCaja();

				//				System.out.println("crear solicitudes 3000");
				//				System.out.println(fechaHoy + "fecha de hoy");
				int id_tipo_docto  = 41;
				int no_docto = 0;
				int no_folio_param = 0; 
				int no_folio_param1 = 0;
				int id_tipo_operacion = 3000;
				String fec_valor  =  datosExcelDtos.get(i).getFec_vto();
				String fec_operacion = fechaHoy ; 
				int no_cuenta = Integer.parseInt(datosExcelDtos.get(i).getUnidad_negocio());
				String no_factura = "NULL"; // es el folio nuevo en vb (no se como lo sacA)
				String id_divisa = datosExcelDtos.get(i).getDivisa_venta();
				String fec_alta  =  fechaHoy;  
				Double importe = datosExcelDtos.get(i).getImporte_pago();
				Double importe_original = datosExcelDtos.get(i).getImporte_pago();
				Double tipo_cambio = datosExcelDtos.get(i).getTc();		
				String beneficiario = datosExcelDtos.get(i).getDesc_institucion();
				String concepto = "COMPRA VENTA DE DIVISAS";
				String id_divisa_original = datosExcelDtos.get(i).getDivisa_venta();
				int id_banco_benef = datosExcelDtos.get(i).getBanco_benef();
				String id_chequera_benef = datosExcelDtos.get(i).getChequera_benef();
				String id_leyenda = "0";
				String id_chequera = datosExcelDtos.get(i).getChequera_cargo();
				String observacion = "NULL";
				int no_cliente  = datosExcelDtos.get(i).getInstitucion();
				String solicita = "";
				String autoriza = "";
				int plaza = 0;
				int sucursal = 0;
				int agrupa1 = 0;
				int agrupa2 = 0;
				int agrupa3 = 0;
				Boolean ADU = false; 
				int no_pedido = 0;
				String id_rubro = datosExcelDtos.get(i).getSubrubro_cargo();
				Boolean CVDivisa = true;
				String fec_valor_original  = fechaHoy;  
				int id_area = -1;
				String referencia = "";
				String id_grupo = datosExcelDtos.get(i).getRubro_cargo();
				int id_banco = datosExcelDtos.get(i).getBanco_cargo();
				String no_cheque = "";


				forwardsDao.actualizarFolioReal("no_folio_param");
				no_folio_param = forwardsDao.seleccionarFolioReal("no_folio_param");

				forwardsDao.actualizarFolioReal("no_folio_docto");
				no_docto = forwardsDao.seleccionarFolioReal("no_folio_docto");



				//				System.out.println("Folio PAram : " + no_folio_param);
				//				System.out.println("Folio docto : " + no_docto);

				System.out.println("crear solicitud 244: 3000");
				forwardsDao.funSQLInsert244(datosExcelDtos.get(i).getUnidad_negocio(),String.valueOf(no_docto), no_folio_param, id_tipo_docto,
						datosExcelDtos.get(i).getForma_pago(), usuario, id_tipo_operacion, no_cuenta, no_factura,
						fec_valor,fec_valor_original,fec_operacion,id_divisa,
						fec_alta,importe,importe_original,tipo_cambio,
						id_caja,id_divisa_original,referencia,beneficiario,
						concepto,id_banco_benef,id_chequera_benef,id_leyenda,
						id_chequera,id_banco,observacion,ADU, no_cliente,
						solicita, autoriza, plaza, sucursal,
						no_folio_param, agrupa1, agrupa2,
						agrupa3, id_rubro, CVDivisa, no_pedido ,id_area ,id_grupo,no_cheque);

				no_folio_param1 = no_folio_param;

				forwardsDao.actualizarFolioReal("no_folio_param");
				no_folio_param = forwardsDao.seleccionarFolioReal("no_folio_param");

//				System.out.println("Folio PAram : " + no_folio_param);
//				System.out.println("Folio PAram : " + no_folio_param1);
//				System.out.println("Folio docto : " + no_docto);

				id_tipo_operacion = 3001;
				id_area = 0;

				forwardsDao.funSQLInsert245(datosExcelDtos.get(i).getUnidad_negocio(),String.valueOf(no_docto), no_folio_param, id_tipo_docto,
						datosExcelDtos.get(i).getForma_pago(), usuario, id_tipo_operacion, no_cuenta, no_factura,
						fec_valor,fec_valor_original,fec_operacion,id_divisa,
						fec_alta,importe,importe_original,tipo_cambio,
						id_caja,id_divisa_original,referencia,beneficiario,
						concepto,id_banco_benef,id_chequera_benef,id_leyenda,
						id_chequera,id_banco,observacion,ADU, no_cliente,
						solicita, autoriza, plaza, sucursal,
						no_folio_param1, agrupa1, agrupa2,
						agrupa3, id_rubro, CVDivisa, no_pedido ,id_area ,id_grupo,no_cheque);

				forwardsDao.actualizarFolioReal("no_folio_param");
				no_folio_param = forwardsDao.seleccionarFolioReal("no_folio_param");

//				System.out.println("Folio PAram : " + no_folio_param);
//				System.out.println("Folio docto : " + no_docto);

				id_tipo_operacion = 1000;
				id_divisa = datosExcelDtos.get(i).getDivisa_compra();
				importe = datosExcelDtos.get(i).getImporte_compra();
				id_divisa_original = datosExcelDtos.get(i).getDivisa_compra();
				id_banco_benef = 0;
				id_chequera_benef = "";
				id_chequera = datosExcelDtos.get(i).getChequera_abono();
				id_rubro= datosExcelDtos.get(i).getSubrubro_abono();
				id_banco = datosExcelDtos.get(i).getBanco_abono();
				id_grupo = datosExcelDtos.get(i).getRubro_abono();

				forwardsDao.funSQLInsert247(datosExcelDtos.get(i).getUnidad_negocio(),String.valueOf(no_docto), no_folio_param, id_tipo_docto,
						datosExcelDtos.get(i).getForma_pago(), usuario, id_tipo_operacion, no_cuenta, no_factura,
						fec_valor,fec_valor_original,fec_operacion,id_divisa,
						fec_alta,importe,importe_original,tipo_cambio,
						id_caja,id_divisa_original,referencia,beneficiario,
						concepto,id_banco_benef,id_chequera_benef,id_leyenda,
						id_chequera,id_banco,observacion,ADU, no_cliente,
						solicita, autoriza, plaza, sucursal,
						no_folio_param1, agrupa1, agrupa2,
						agrupa3, id_rubro, CVDivisa, no_pedido ,id_area ,id_grupo,no_cheque);


				int id_cuadrante  = 6;
				id_grupo =  datosExcelDtos.get(i).getRubro_cargo();
				id_rubro = datosExcelDtos.get(i).getSubrubro_cargo();
				id_divisa = datosExcelDtos.get(i).getDivisa_venta();
				Double importePago = datosExcelDtos.get(i).getImporte_pago();
				int rows = forwardsDao.funSqlGetFlujoProgramado(id_grupo, id_cuadrante, id_rubro, id_divisa , 
						fec_valor, datosExcelDtos.get(i).getUnidad_negocio());

				if(rows > 0){
					forwardsDao.funSQLUpdateFlujoProgramado( id_grupo,  id_cuadrante,id_rubro, id_divisa, fec_valor,agrupa1,
							agrupa2, agrupa3, datosExcelDtos.get(i).getUnidad_negocio(), importePago);
				}else{
					forwardsDao.funSQLInsertFlujoProgramado( id_grupo,  id_cuadrante,id_rubro, id_divisa, fec_valor,agrupa1,
							agrupa2, agrupa3, datosExcelDtos.get(i).getUnidad_negocio(), importePago);
				}	
				
				Map respGenerador = new HashMap();
				
				GeneradorDto generadorDto = new GeneradorDto();
				
				generadorDto.setEmpresa(Integer.parseInt(datosExcelDtos.get(i).getUnidad_negocio()));
				generadorDto.setFolParam( no_folio_param );
				generadorDto.setFolMovi(0);
				generadorDto.setFolDeta(0);
				generadorDto.setIdUsuario(1);
				generadorDto.setMensajeResp("");
				
				//respGenerador = confirmacionCargoCtaDao.ejecutarGenerador(dto.getNoEmpresa(), noFolioParam1, 0, 0, 1, " ");
//				forwardsDao.ejecutarGenerador(generadorDto);
						
				if(!respGenerador.isEmpty() && Integer.parseInt(respGenerador.get("result").toString())!= 0) {
					
//					return new ResultadoDto(false,"Error, en Generador " + respGenerador.get("result"),null);
					mensaje = "Error, en Generador " + respGenerador.get("result");
				}
				else{
					
//					return new ResultadoDto(true,"Se realizo correctamente la compra/venta de divisas", dto.getNoDocto());
					mensaje = "Ejecucion realizada con éxito";
				}

			}
			
			
			
		}catch (Exception e) {
			logger.error(e.toString());
			mensaje = "Ocurrio un error en la ejecución";
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P: derivados C: ForwardsBusinessImpl M:insertaForward");
		}
		return mensaje;
	}



	/**********Excel***************/			 
	public String exportaExcel(String datos,String op) {
		String respuesta = "";
		Gson gson = new Gson();
		List<Map<String, String>> parameters = gson.fromJson(datos, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		String[] titulos = null, registros = null;
		String encabezado = "Forwards ";
		try {	    	
			switch (op) {
			case "forwardsImportados":

				titulos= new String[]{"FOLIO","UNIDAD_DE_NEGOCIO","CHEQUERA_CARGO","CHEQUERA_ABONO","FORMA_PAGO","IMPORTE_PAGO","IMPORTE_COMPRA","T_C","FEC_VTO","INSTITUCION","RUBRO_ABONO","SUBRUBRO_ABONO","RUBRO_CARGO","SUBRUBRO_CARGO","FEC_COMPRA","SPOT","PUNTOS_FORWARD"};
				registros=new String[]{"folio","unidad_negocio","chequera_cargo","chequera_abono","forma_pago","importe_pago","importe_compra","tc","fec_vto","institucion","rubro_abono","subrubro_abono","rubro_cargo","subrubro_cargo","fec_compra","spot","puntos_forward"};
				encabezado+=" Importados. ";
				break;
			}
			System.out.println("-------------------Antes de formar el excel----------------------");
			System.out.println(titulos.length + "Tamaño titulos");
			System.out.println(registros.length + "tamaño registros");
			System.out.println(encabezado + " Encabezado");

			HSSFWorkbook wb = Utilerias.generarExcel(titulos, parameters, registros,encabezado);			

			respuesta = ConstantesSet.RUTA_EXCEL + "forwardsImportados " + Utilerias.indicadorFecha() +".xls";
			File outputFile = new File(respuesta);

			FileOutputStream outs = new FileOutputStream(outputFile);
			wb.write(outs);
			outs.close();            
		} catch (IOException e) {
			logger.error(e.toString());
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:derivados, C:ForwardsBusinessImpl, M:exportaExcel");
			respuesta = "";
		} catch(Exception e){
			logger.error(e.toString());
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:derivados, C:ForwardsBusinessImpl M:exportaExcel");
			respuesta = "";
		}

		return respuesta;
	}




	public ForwardsDao getForwardsDao() {
		return forwardsDao;
	}
	public void setForwardsDao(
			ForwardsDao forwardsDao) {
		this.forwardsDao = forwardsDao;
	}















	@Override
	public List<LlenaComboGralDto> obtenerdivisas(int bExistentes, int idDiv) {
		List<LlenaComboGralDto> listDiv = new ArrayList<LlenaComboGralDto>();

		try{
			listDiv= forwardsDao.consultarDivisas(bExistentes, idDiv);

		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P: Financiamiento C: FinanciamientoModificacionCImpl M: consultarLineas");
		}
		return listDiv;
	}





	@Override
	public List<LlenaComboGralDto> obtenerBanco(int idEmp, String idDiv) {
		List<LlenaComboGralDto> listDiv = new ArrayList<LlenaComboGralDto>();

		try{
			listDiv= forwardsDao.consultarBancos(idEmp, idDiv);

		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P: Financiamiento C: FinanciamientoModificacionCImpl M: consultarLineas");
		}
		return listDiv;
	}

	@Override
	public List<LlenaComboGralDto> obtenerChequeraBanco( int noEmpresa, String idDivisa,
			int idBanco) {
		List<LlenaComboGralDto> listDiv = new ArrayList<LlenaComboGralDto>();

		try{
			listDiv= forwardsDao.obtenerChequeraAbo(noEmpresa, idDivisa, idBanco);
//			System.out.println(listDiv.size()+"hhhhhhhhh");
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P: Financiamiento C: FinanciamientoModificacionCImpl M: consultarLineas");
		}
		return listDiv;
	}



	@Override
	public String agregarFwd(int noEmpresa, String idDivVenta, int bancoCargo, String id_chequera, String idDivCompra,
			int bancoAbono, String cheAbono, int forma_pago, double importePago, double importeFwd, double tipo_cambio,
			String fecAlta, String fecVencimiento, int noInstitucion, String nomContacto, int id_banco_benef,
			String id_chequera_benef, String rubroCarg, String subRubroCarg, String rubroAbono, String subRubroAbono,
			String estatusMov, String estatusImporte, String firmante1, String firmante2, String noDocto, double spot,
			double pntsFwd, String desc_institucion,int usuario) {
		String sMsgUsuario =""; 

		try
		{

			forwardsDao.insertarFwd(noEmpresa,  idDivVenta, bancoCargo, id_chequera, idDivCompra, bancoAbono, cheAbono,
					forma_pago, importePago,  importeFwd,  tipo_cambio,  fecAlta,  fecVencimiento,
					noInstitucion, nomContacto, id_banco_benef,  id_chequera_benef, rubroCarg, subRubroCarg,  rubroAbono,
					subRubroAbono, estatusMov, estatusImporte, firmante1, firmante2, noDocto,   spot,  pntsFwd );


			globalSingleton = GlobalSingleton.getInstancia();
			String fechaHoy = formato.format(globalSingleton.getFechaHoy());

			//			int id_caja  = datosExcelDtos.get(i).getCaja();

			int id_caja = forwardsDao.obtenerCaja(usuario);

//			System.out.println("crear solicitudes 3000");
//			System.out.println(fechaHoy + "fecha de hoy");
			int id_tipo_docto  = 41;
			int no_docto = 0;
			int no_folio_param = 0; 
			int no_folio_param1 = 0;  //se inserta em el grupo
			int id_tipo_operacion = 3000;
			String fec_valor  =  fecVencimiento;
			String fec_operacion = fechaHoy ; 
			int no_cuenta = noEmpresa;
			String no_factura = "NULL"; // es el folio nuevo en vb (no se como lo sacA)
			String id_divisa = idDivVenta;
			String fec_alta  =  fechaHoy;  
			Double importe = importePago;
			Double importe_original = importePago;	
			String beneficiario = desc_institucion; 
			String concepto = "COMPRA VENTA DE DIVISAS";
			String id_divisa_original = idDivVenta;
			String id_leyenda = "0";
			String observacion = "NULL";
			int no_cliente  = noInstitucion;
			String solicita = "";
			String autoriza = "";
			int plaza = 0;
			int sucursal = 0;
			int agrupa1 = 0;
			int agrupa2 = 0;
			int agrupa3 = 0;
			Boolean ADU = false; 
			int no_pedido = 0;
			String id_rubro = subRubroCarg;
			Boolean CVDivisa = true;
			String fec_valor_original  = fechaHoy;  
			int id_area = -1;
			String referencia = "";
			String id_grupo = rubroCarg;
			int id_banco = bancoCargo;
			String no_cheque = "";


			forwardsDao.actualizarFolioReal("no_folio_param");
			no_folio_param = forwardsDao.seleccionarFolioReal("no_folio_param");

			forwardsDao.actualizarFolioReal("no_folio_docto");
			no_docto = forwardsDao.seleccionarFolioReal("no_folio_docto");




//			System.out.println("crear solicitud 244");
			forwardsDao.funSQLInsert244(String.valueOf(noEmpresa),String.valueOf(no_docto), no_folio_param, id_tipo_docto,
					String.valueOf(forma_pago), usuario, id_tipo_operacion, no_cuenta, no_factura,
					fec_valor,fec_valor_original,fec_operacion,id_divisa,
					fec_alta,importe,importe_original,tipo_cambio,
					id_caja,id_divisa_original,referencia,beneficiario,
					concepto,id_banco_benef,id_chequera_benef,id_leyenda,
					id_chequera,id_banco,observacion,ADU, no_cliente,
					solicita, autoriza, plaza, sucursal,
					no_folio_param, agrupa1, agrupa2,
					agrupa3, id_rubro, CVDivisa, no_pedido ,id_area ,id_grupo,no_cheque);

			no_folio_param1 = no_folio_param;

			forwardsDao.actualizarFolioReal("no_folio_param");
			no_folio_param = forwardsDao.seleccionarFolioReal("no_folio_param");

			id_tipo_operacion = 3001;
			id_area = 0;

			forwardsDao.funSQLInsert245(String.valueOf(noEmpresa),String.valueOf(no_docto), no_folio_param, id_tipo_docto,
					String.valueOf(forma_pago), usuario, id_tipo_operacion, no_cuenta, no_factura,
					fec_valor,fec_valor_original,fec_operacion,id_divisa,
					fec_alta,importe,importe_original,tipo_cambio,
					id_caja,id_divisa_original,referencia,beneficiario,
					concepto,id_banco_benef,id_chequera_benef,id_leyenda,
					id_chequera,id_banco,observacion,ADU, no_cliente,
					solicita, autoriza, plaza, sucursal,
					no_folio_param1, agrupa1, agrupa2,
					agrupa3, id_rubro, CVDivisa, no_pedido ,id_area ,id_grupo,no_cheque);

			forwardsDao.actualizarFolioReal("no_folio_param");
			no_folio_param = forwardsDao.seleccionarFolioReal("no_folio_param");

			id_tipo_operacion = 1000;
			id_divisa = idDivCompra;
			importe = importeFwd;
			id_divisa_original = idDivCompra;
			id_banco_benef = 0;
			id_chequera_benef = "";
			id_chequera = cheAbono;
			id_rubro= subRubroAbono;
			id_banco = bancoAbono;
			id_grupo = rubroAbono;

			forwardsDao.funSQLInsert247(String.valueOf(noEmpresa),String.valueOf(no_docto), no_folio_param, id_tipo_docto,
					String.valueOf(forma_pago), usuario, id_tipo_operacion, no_cuenta, no_factura,
					fec_valor,fec_valor_original,fec_operacion,id_divisa,
					fec_alta,importe,importe_original,tipo_cambio,
					id_caja,id_divisa_original,referencia,beneficiario,
					concepto,id_banco_benef,id_chequera_benef,id_leyenda,
					id_chequera,id_banco,observacion,ADU, no_cliente,
					solicita, autoriza, plaza, sucursal,
					no_folio_param, agrupa1, agrupa2,
					agrupa3, id_rubro, CVDivisa, no_pedido ,id_area ,id_grupo,no_cheque);


			int id_cuadrante  = 6;
			id_grupo = rubroCarg;
			id_rubro = subRubroCarg;
			id_divisa = idDivVenta;
			int rows = forwardsDao.funSqlGetFlujoProgramado(id_grupo, id_cuadrante, id_rubro, id_divisa , 
					fec_valor, String.valueOf(noEmpresa));

			if(rows > 0){
				forwardsDao.funSQLUpdateFlujoProgramado( id_grupo,  id_cuadrante,id_rubro, id_divisa, fec_valor,agrupa1,
						agrupa2, agrupa3,  String.valueOf(noEmpresa), importePago);
			}else{
				forwardsDao.funSQLInsertFlujoProgramado( id_grupo,  id_cuadrante,id_rubro, id_divisa, fec_valor,agrupa1,
						agrupa2, agrupa3,  String.valueOf(noEmpresa), importePago);
			}	
			
			sMsgUsuario = "Alta exitosarealizada con exito";


		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P: Financiamiento C: FinanciamientoModificacionCImpl M: consultarLineas");

		}
		return sMsgUsuario;
	}


	@Override
	public List<LlenarConsultaFws> llenarGrid(boolean foco, int tipo, String fecA, String fecV,int noEm) {
		List<LlenarConsultaFws> listDisp = new ArrayList<LlenarConsultaFws>();

		try{
			listDisp= forwardsDao.consultarGrid(foco,tipo,  fecA, fecV, noEm);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P: Financiamiento C: FinanciamientoModificacionCImpl M: consultarLineas");
		}
		return listDisp;
	}

	@Override
	public HSSFWorkbook reportePersonas2(String tipoPersona, String foco, String tipo, String fecA, String fecV) {
		HSSFWorkbook hb=null;
		try {
			hb=Utilerias.generarExcel(new String[]{
					"NO_FOLIO",
					"NO_EMPRESA",
					"ID_DIVISA_VENTA",
					"ID_BANCO_CARGO",
					"ID_CHEQUERA_CARGO",
					"ID_DIVISA_COMPRA",
					"ID_BANCO_ABONO",
					"ID_CHEQUERA_ABONO",
					"ID_FORMA_PAGO",
					"IMPORTE_PAGO",
					"IMPORTE_FWS",
					"TIPO_CAMBIO",
					"FEC_ALTA",
					"FEC_VENC",
					"NO_INSTITUCION",
					"NOM_CONTACTO",
					"ID_BANCO_BENEF",
					"ID_CHEQUERA_BENEF",
					"RUBRO_CGO",
					"SUBRUBRO_CGO",
					"RUBRO_ABN",
					"SUBRUBRO_ABN",
					"ESTATUS_MOV",
					"ESTATUS_IMP",
					"FIRMANTE_1",
					"FIRMANTE_2",
					"NO_DOCTO",
					"SPOT",
					"PUNTOS_FORWARD"

			}, 
					forwardsDao.reportePersonas2(tipoPersona ,foco,tipo, fecA, fecV), 


					new String[]{
							"NO_FOLIO",
							"NO_EMPRESA",
							"ID_DIVISA_VENTA",
							"ID_BANCO_CARGO",
							"ID_CHEQUERA_CARGO",
							"ID_DIVISA_COMPRA",
							"ID_BANCO_ABONO",
							"ID_CHEQUERA_ABONO",
							"ID_FORMA_PAGO",
							"IMPORTE_PAGO",
							"IMPORTE_FWS",
							"TIPO_CAMBIO",
							"FEC_ALTA",
							"FEC_VENC",
							"NO_INSTITUCION",
							"NOM_CONTACTO",
							"ID_BANCO_BENEF",
							"ID_CHEQUERA_BENEF",
							"RUBRO_CGO",
							"SUBRUBRO_CGO",
							"RUBRO_ABN",
							"SUBRUBRO_ABN",
							"ESTATUS_MOV",
							"ESTATUS_IMP",
							"FIRMANTE_1",
							"FIRMANTE_2",
							"NO_DOCTO",
							"SPOT",
							"PUNTOS_FORWARD"

			});

			//			System.out.println("------------y1--------------------------");
			//			System.out.println(tipoPersona);
			//			System.out.println(foco);
			//			System.out.println(tipo);
			//			System.out.println(fecA);
			//			System.out.println(fecV);

			//			System.out.println("-----------y1----------------------------");
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasBusinessImpl, M: reportePersonas");
		}return hb;
	}


	@Override
	public HSSFWorkbook reportFwd(String fwd) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String eliminaForward(String matrizDatos, int usuario) {
		String mensaje = "";
		Gson gson = new Gson();
		List<Map<String, String>> datos = gson.fromJson(matrizDatos, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		System.out.println(datos);
		try {
			for (int i = 0; i < datos.size(); i++) {
				// ELIMINA EN LA TABLA DE CONTROL FORWARDS
				forwardsDao.eliminarForward(Integer.parseInt(datos.get(i).get("folio")),datos.get(i).get("unidad_negocio"),  
						datos.get(i).get("chequera_cargo"),  datos.get(i).get("chequera_abono"),
						Double.parseDouble(datos.get(i).get("importe_pago").replaceAll("\\$", "")), Double.parseDouble(datos.get(i).get("importe_compra").replaceAll("\\$", "")),
						Double.parseDouble(datos.get(i).get("tc").replaceAll("\\$", "")), 
						datos.get(i).get("fec_compra"), datos.get(i).get("fec_vto"));
				mensaje = "Eliminación realizada con éxito";
			}		
	}catch (Exception e) {
		logger.error(e.toString());
		mensaje = "Ocurrio un error en la ejecución";
		bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
		+ "P: derivados C: ForwardsBusinessImpl M:eliminaForward");
	}
	return mensaje;
	}

	@Override
	public String modificaForward(String matrizDatos, int usuario) {

		String mensaje = "";
		Gson gson = new Gson();
		List<Map<String, String>> datos = gson.fromJson(matrizDatos, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		System.out.println(datos);
		try {
			for (int i = 0; i < datos.size(); i++) {
				// MODIFICA EN LA TABLA DE CONTROL FORWARDS
				forwardsDao.modificaForward(Integer.parseInt(datos.get(i).get("folio")),datos.get(i).get("unidad_negocio"),  
						datos.get(i).get("chequera_cargo"),  datos.get(i).get("chequera_abono"),
						Double.parseDouble(datos.get(i).get("spot")),Double.parseDouble(datos.get(i).get("pts_forward")),
						Double.parseDouble(datos.get(i).get("importe_pago").replaceAll("\\$", "")), Double.parseDouble(datos.get(i).get("importe_compra").replaceAll("\\$", "")),
						Double.parseDouble(datos.get(i).get("tc").replaceAll("\\$", "")), 
						datos.get(i).get("fec_compra"), datos.get(i).get("fec_vto"));	
			}		
			mensaje = "Modificación realizada con éxito";
		
		}catch (Exception e) {
			logger.error(e.toString());
			mensaje = "Ocurrio un error en la ejecución";
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P: derivados C: ForwardsBusinessImpl M:eliminaForward");
		}
		return mensaje;
	
	}

	@Override
	public String swapForward(String matrizDatos, int usuario) {
		
		String mensaje = "";
		Gson gson = new Gson();
		List<Map<String, String>> datos = gson.fromJson(matrizDatos, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		System.out.println(datos);
		try {
			for (int i = 0; i < datos.size(); i++) {
				// MODIFICA COLUMNA SWAP EN LA TABLA DE CONTROL FORWARDS
				forwardsDao.swapForward(Integer.parseInt(datos.get(i).get("folio")),datos.get(i).get("unidad_negocio"),  
						datos.get(i).get("chequera_cargo"),  datos.get(i).get("chequera_abono"),
						Double.parseDouble(datos.get(i).get("spot")),Double.parseDouble(datos.get(i).get("puntos_forward")),
						Double.parseDouble(datos.get(i).get("importe_pago").replaceAll("\\$", "")), Double.parseDouble(datos.get(i).get("importe_compra").replaceAll("\\$", "")),
						datos.get(i).get("fec_compra"), datos.get(i).get("fec_vto"));	
			}		
			mensaje = "Modificación realizada con éxito";
		
		}catch (Exception e) {
			logger.error(e.toString());
			mensaje = "Ocurrio un error en la ejecución";
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P: derivados C: ForwardsBusinessImpl M:eliminaForward");
		}
		return mensaje;
	
	}


	















}
