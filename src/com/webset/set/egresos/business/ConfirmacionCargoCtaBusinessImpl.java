package com.webset.set.egresos.business;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.webset.set.egresos.dao.ConfirmacionCargoCtaDao;
import com.webset.set.egresos.dto.ConfirmacionCargoCtaDto;
import com.webset.set.egresos.dto.ResultadoDto;
import com.webset.set.egresos.service.ConfirmacionCargoCtaService;
import com.webset.set.financiamiento.dto.AmortizacionCreditoDto;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utilerias.GlobalSingleton;
import com.webset.set.utilerias.dto.GeneradorDto;
import com.webset.set.utilerias.dto.LlenaComboChequeraDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.set.utilerias.dto.MovimientoDto;
import com.webset.set.utilerias.dto.StoreParamsComunDto;

public class ConfirmacionCargoCtaBusinessImpl implements ConfirmacionCargoCtaService {
	private ConfirmacionCargoCtaDao confirmacionCargoCtaDao;
	Bitacora bitacora;
	Funciones funciones;
	GlobalSingleton globalSingleton;
	SimpleDateFormat formato = new SimpleDateFormat("ddMMyyyy");

	public List<LlenaComboGralDto> obtenerBancos(int noEmpresa) {
		return confirmacionCargoCtaDao.obtenerBancos(noEmpresa);
	}
	public List<AmortizacionCreditoDto> obtenerImportes(int folio) {
		return confirmacionCargoCtaDao.obtenerImportes(folio);
	}

	public List<LlenaComboChequeraDto> llenaComboChequera(int idBanco, int noEmpresa, String idDivisa) {
		return confirmacionCargoCtaDao.llenaComboChequera(idBanco, noEmpresa, idDivisa);
	}

	public List<MovimientoDto> llenarMovtosPagos(ConfirmacionCargoCtaDto dtoGrid) {
		return confirmacionCargoCtaDao.llenarMovtosPagos(dtoGrid);	
	}

	public List<ConfirmacionCargoCtaDto> llenarMovtosCargos(ConfirmacionCargoCtaDto dtoGrid) {
		return confirmacionCargoCtaDao.llenarMovtosCargos(dtoGrid);	
	}

	public Map<String, Object> ejecutarConfirmacionCargoCta(List<MovimientoDto> listPagos, List<ConfirmacionCargoCtaDto> listCargos, 
			double diferencia, double permisible) {
		Map<String, Object> mapRetornoMsg = new HashMap<String, Object>();
		Map<String, Object> respConfirmador = new HashMap<String, Object>();
		String sFolioEncontrados = "";
		int iFolio = 0;
		String sSecuencia = "";
		boolean bEjecuto = false;

		try {
			if((diferencia > permisible) || listPagos.size() <= 0 || listCargos.size() <= 0) {
				mapRetornoMsg.put("msgUsuario", "No se pueden confirmar los movimientos seleccionados," +
						" ya que la diferencia no es 0, no esta dentro del rango permisible de confirmación" +
						" o bien no hay registros que procesar");
				return mapRetornoMsg;
			}
			for(int i=0; i<listCargos.size(); i++) {
				if(listCargos.get(i).getNoFolioDet() != 0) {
					for(int x=0; x<listPagos.size(); x++) {
						if(listPagos.get(x).getNoFolioDet() == listCargos.get(i).getNoFolioDet() && sFolioEncontrados.indexOf(listPagos.get(x).getNoFolioDet()) < 0) {
							iFolio = listCargos.get(i).getNoFolioDet();
							sSecuencia = listCargos.get(i).getSecuencia();
							sFolioEncontrados = sFolioEncontrados + listCargos.get(i).getNoFolioDet() + ",";
							break;
						}
					}
				}else {
					for(int y=0; y<listPagos.size(); y++) {
						if(listPagos.get(y).getImporte() == listCargos.get(i).getImporte() && sFolioEncontrados.indexOf(listPagos.get(y).getNoFolioDet()) < 0) {
							iFolio = listPagos.get(y).getNoFolioDet();
							sSecuencia = listCargos.get(i).getSecuencia();
							sFolioEncontrados = sFolioEncontrados + listPagos.get(i).getNoFolioDet() + ",";
							break;
						}
					}
				}
				if(iFolio != 0 && !sSecuencia.equals("")) {
					bEjecuto = true;
					respConfirmador = confirmacionCargoCtaDao.ejecutarConfirmador(iFolio, "0", Integer.parseInt(sSecuencia),
							listPagos.get(i).getFecValorOriginal(), 0, "0");
					iFolio = 0;
					sSecuencia = "";
				}
				if (!respConfirmador.isEmpty() && Integer.parseInt(respConfirmador.get("result").toString())!= 0) {
					mapRetornoMsg.put("msgUsuario", "Error, en confirmador " + respConfirmador.get("result") + ":");
					return mapRetornoMsg;
				}
			}
			if(bEjecuto)
				mapRetornoMsg.put("msgUsuario", "Se realizo la confirmación correctamente!!");
		}catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionCargoCtaBusinessImpl, M:ejecutarConfirmacion");
			e.printStackTrace();
		}
		return mapRetornoMsg;
	}


	//Para la pantalla de Consulta de mantenimiento de cupos
	public List<ConfirmacionCargoCtaDto> llenaComboGpoEmpresas() {
		return confirmacionCargoCtaDao.llenaComboGpoEmpresas();
	}

	public List<ConfirmacionCargoCtaDto> llenaComboDivision() {
		return confirmacionCargoCtaDao.llenaComboDivision();
	}
	public List<ConfirmacionCargoCtaDto> llenaComboGpoCupo() {
		return confirmacionCargoCtaDao.llenaComboGpoCupo();
	}
	public List<ConfirmacionCargoCtaDto> buscarCupos(int optCupos, int grupoEmpresa, String fecIni, String fecFin, String idDivision) {
		return confirmacionCargoCtaDao.buscarCupos(optCupos, grupoEmpresa, fecIni, fecFin, idDivision);
	}

	public Map<String, Object> ejecutarAltaRegistro(List<ConfirmacionCargoCtaDto> listDatos, boolean insertar, boolean esDivision) {
		Map<String, Object> mapRetornoMsg = new HashMap<String, Object>();
		globalSingleton = GlobalSingleton.getInstancia();
		int iFolio = 0;
		int iRes = 0;
		String sCveControl = "";

		try {
			iRes = confirmacionCargoCtaDao.actualizarFolioCupos();
			iFolio = confirmacionCargoCtaDao.seleccionarFolioCupos();

			if(iFolio == -1) {
				mapRetornoMsg.put("msgUsuario", "En cat_usuario el folio para la Clave Control es NULO");
				return mapRetornoMsg;
			}
			if(insertar) {
				sCveControl = iFolio + formato.format(confirmacionCargoCtaDao.obtenerFechaHoy()) + globalSingleton.getUsuarioLoginDto().getIdUsuario();
				iRes = confirmacionCargoCtaDao.ejecutarAltaRegistro(listDatos, esDivision, sCveControl);
			}else {
				for(int i=0; i<listDatos.size(); i++) {
					iRes = confirmacionCargoCtaDao.ejecutarUpdateRegistro(listDatos, esDivision, i);
				}
			}
			if(iRes > 0)
				mapRetornoMsg.put("msgUsuario", "Datos Registrados");
			else
				mapRetornoMsg.put("msgUsuario", "Error al resgistrar los datos");
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionCargoCtaBusinessImpl, M:ejecutarAltaRegistro");
		}
		return mapRetornoMsg;
	}

	public Map<String, Object> eliminarRegistro(List<ConfirmacionCargoCtaDto> listDatos) {
		Map<String, Object> mapRetornoMsg = new HashMap<String, Object>();
		int movtos = 0;

		try {
			for(int i=0; i<listDatos.size(); i++) {
				movtos = confirmacionCargoCtaDao.existenMovtosSelAutoGrupo(listDatos, i, "movimiento");

				if(movtos > 0) {
					mapRetornoMsg.put("msgUsuario", "No se puede eliminar porque hay movimientos con esta Cve. de Propuesta asignada " + listDatos.get(i).getCveControl());
					return mapRetornoMsg;
				}
				movtos = confirmacionCargoCtaDao.existenMovtosSelAutoGrupo(listDatos, i, "hist_movimiento");

				if(movtos > 0) {
					mapRetornoMsg.put("msgUsuario", "No se puede eliminar porque hay movimientos con esta Cve. de Propuesta asignada " + listDatos.get(i).getCveControl());
					return mapRetornoMsg;
				}
				movtos = confirmacionCargoCtaDao.deleteMovtosSelAutoGrupo(listDatos, i);
			}
			mapRetornoMsg.put("msgUsuario", "Registros eliminados");
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionCargoCtaBusinessImpl, M:eliminarRegistro");
		}
		return mapRetornoMsg;
	}


	//Para la pantalla de pago de propuestas automatico
	public List<ConfirmacionCargoCtaDto> llenaComboCveControl(int gpoEmpresa, int grupo, int idDivision) {
		return confirmacionCargoCtaDao.comboCveControl(gpoEmpresa, grupo, idDivision);
	}

	public List<ConfirmacionCargoCtaDto> llenaComboValor(int tipoCombo, int gpoEmpresa, int idBcoPag) {
		List<ConfirmacionCargoCtaDto> query = new ArrayList<ConfirmacionCargoCtaDto>();

		try {
			switch(tipoCombo) {
			case 4:
				return confirmacionCargoCtaDao.comboBancoReceptor();
			case 7:
				return confirmacionCargoCtaDao.comboDivision(gpoEmpresa);
			case 8:
				return confirmacionCargoCtaDao.comboFormaPago();
			case 9:
				return confirmacionCargoCtaDao.comboDivisa();
			case 10:
				return confirmacionCargoCtaDao.comboClaveOperacion();
			case 16:
				return confirmacionCargoCtaDao.comboOrigenMovto();
			case 17:
				return confirmacionCargoCtaDao.comboRubroMovto(gpoEmpresa);
			case 20:
				return confirmacionCargoCtaDao.comboBancoPagador(gpoEmpresa);
			case 21:
				return confirmacionCargoCtaDao.comboChequeraPagadora(gpoEmpresa, idBcoPag);
			case 23:
				return confirmacionCargoCtaDao.comboCaja();
			}
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionCargoCtaBusinessImpl, M:llenaComboValor");
		}
		return query;
	}

	public List<ConfirmacionCargoCtaDto> selectCupos(int gpoEmpresa, String idDivisa, int grupo, String cveControl, boolean bCambioMMF, int idDivision) {
		List<ConfirmacionCargoCtaDto> list = new ArrayList<ConfirmacionCargoCtaDto>();
		ConfirmacionCargoCtaDto dto = new ConfirmacionCargoCtaDto();

		try {
			list = confirmacionCargoCtaDao.selectAutomaticaGpo(gpoEmpresa, grupo, cveControl, idDivision, idDivisa);

			if(list.size() > 0) {
				if(list.get(0).getFechaPropuesta().equals("")) {
					dto.setMensaje("No tiene fecha propuesta asignada");
					list.add(dto);
					return list;
				}
			}else {
				dto.setMensaje("No tiene cupo asignado para esta empresa");
				list.add(dto);
				return list;
			}
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionCargoCtaBusinessImpl, M:buscarCupos");
		}
		return list;
	}

	public List<ConfirmacionCargoCtaDto> selectPagosAutomaticos(ConfirmacionCargoCtaDto dto, List<Map<String, String>> paramsGrid) {
		List<ConfirmacionCargoCtaDto> list = new ArrayList<ConfirmacionCargoCtaDto>();
		List<ConfirmacionCargoCtaDto> listAutProp = new ArrayList<ConfirmacionCargoCtaDto>();
		boolean bAgregar = true;
		double dTotPropuesto = 0;

		try {
			if(confirmacionCargoCtaDao.consultarConfiguraSet(240).equals("SI")) {
				listAutProp = confirmacionCargoCtaDao.selectAutorizacionProp(dto);

				if(listAutProp.size() > 0) {
					switch (listAutProp.get(0).getNivelAutorizacion()) {
					case 1:
						if(listAutProp.get(0).getUsuarioUno() != 0) bAgregar = false;
						break;
					case 2:
						if(listAutProp.get(0).getUsuarioUno() != 0 && listAutProp.get(0).getUsuarioDos() != 0) bAgregar = false;
						break;
					case 3:
						if(listAutProp.get(0).getUsuarioUno() != 0 && listAutProp.get(0).getUsuarioDos() != 0 && listAutProp.get(0).getUsuarioTres() != 0) bAgregar = false;
						break;
					}
				}
			}
			listAutProp = confirmacionCargoCtaDao.selectPagosAutomaticos(dto, paramsGrid);

			if(listAutProp.size() > 0) {
				list = confirmacionCargoCtaDao.selectAutomaticaGpo(dto.getIdGrupoFlujo(), dto.getIdGrupo(), 
						dto.getCveControl(), Integer.parseInt(dto.getIdDivision()), dto.getIdDivisa());

				for(int i=0; i<listAutProp.size(); i++) {
					if(listAutProp.get(i).getImporte() > 0) {
						/*If rstDatos!fec_valor < GT_FECHA_HOY Then
		                    sElemento = sElemento & (GT_FECHA_HOY - rstDatos!fec_valor) & Chr(9)
		                Else
		                    sElemento = sElemento & "0" & Chr(9)
		                End If
						 */
						//listEquivaleP  = confirmacionCargoCtaDao.selectEquivaleP(listAutProp.get(i).getNoCliente());
						if(!dto.isChkProvServ()) {
							if((((dto.getIdDivisa().equals("MN") ? listAutProp.get(i).getImporteMn() : listAutProp.get(i).getImporte()) <= list.get(0).getMontoMaximo()) 
									&& (((dto.getTotalPropuesto() != 0 ? dto.getTotalPropuesto() : dTotPropuesto) + (dto.getIdDivisa().equals("MN") ? listAutProp.get(i).getImporteMn() : listAutProp.get(i).getImporte())) < list.get(0).getCupoAutomatico()))
									|| dto.isChkPropuestos()) {

								if(bAgregar || dto.isChkPropuestos()) {
									dTotPropuesto = dTotPropuesto + (dto.getIdDivisa().equals("MN") ? listAutProp.get(i).getImporteMn() : listAutProp.get(i).getImporte());

									if(dto.getTipoGrid().equals("pendientes")) {
										listAutProp.remove(i);
										i--;
									}
								}else if(dto.getTipoGrid().equals("propuestos")) {
									listAutProp.remove(i);
									i--;
								}
							}else if(dto.getTipoGrid().equals("propuestos")) {
								listAutProp.remove(i);
								i--;
							}
						}else {
							if(((dto.getTotalPropuesto() + (dto.getIdDivisa().equals("MN") ? listAutProp.get(i).getImporteMn() : listAutProp.get(i).getImporte())) < list.get(0).getCupoAutomatico()) 
									|| dto.isChkPropuestos()) {

								if(bAgregar || dto.isChkPropuestos()) {
									dTotPropuesto = dTotPropuesto + (dto.getIdDivisa().equals("MN") ? listAutProp.get(i).getImporteMn() : listAutProp.get(i).getImporte());

									if(dto.getTipoGrid().equals("pendientes")) {
										listAutProp.remove(i);
										i--;
									}
								}else if(dto.getTipoGrid().equals("propuestos")) {
									listAutProp.remove(i);
									i--;
								}
							}else if(dto.getTipoGrid().equals("propuestos")) {
								listAutProp.remove(i);
								i--;
							}
						}
					}
				}
				/*if(dto.getTipoGrid().equals("propuestos")) {
					dto.setTotalPropuesto(dTotPropuesto);
					listAutProp.add(dto);
				}*/
			}
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionCargoCtaBusinessImpl, M:buscarCupos");
		}
		return listAutProp;
	}

	public Map<String, Object> ejecutarDatos(List<Map<String, String>> parametros, String fechaPago, String cveControl, double cupoAuto, 
			double totPropuesto, String idDivisa, int idGrupo) {
		Map<String, Object> mapRetornoMsg = new HashMap<String, Object>();
		globalSingleton = GlobalSingleton.getInstancia();
		//		Map<String, Object> respCuadrante = new HashMap<String, Object>();
		String sFolios = "";
		//		int iAfectados = 0;
		double dImporteManual = 0;
		//		double dTotalmn = 0;
		double tipoCambio = 0;
		int iResp = 0;

		try {
			//if(!fechaPago.equals("")) fechaPago = fechaPago.substring(8,10) + "/" + fechaPago.substring(5,7) + "/" + fechaPago.substring(0,4);

			for(int i=0; i<parametros.size(); i++) {
				sFolios = parametros.get(i).get("noFolioDet") + ",";
			}

			/*if(!sFolios.equals("")) {
				//"", "", Me.Name, GI_USUARIO, iFormaPago, tFechaPropuesta, "", "", 0, sFolios
				respCuadrante = confirmacionCargoCtaDao.ejecutarCuadrante("" + globalSingleton.getUsuarioLoginDto().getIdUsuario(), fechaPago, sFolios);
			}
			if (!respCuadrante.isEmpty() && Integer.parseInt(respCuadrante.get("result").toString())!= 0) {
				mapRetornoMsg.put("msgUsuario", "Error, en afectar cuadrantes " + respCuadrante.get("result") + ":");
				return mapRetornoMsg;
			}*/

			sFolios = sFolios.substring(0, sFolios.length() - 1);
			confirmacionCargoCtaDao.updateFecPropuesta(sFolios, fechaPago, cveControl);
			/*Call BitacoraCambiosDocumentos(sFolios & ",", "AGREGA A PROPUESTA: " & matCveControl(cmbCveControl.ItemData(cmbCveControl.ListIndex)))*/

			if(totPropuesto > cupoAuto)
				dImporteManual = totPropuesto - cupoAuto;
			else
				cupoAuto = totPropuesto;

			if(idDivisa.equals("") || idDivisa.equals("MN")) {
				/*Esto esta comentado por que ya en la pantalla de visual tampoco se necesita
				  for(int x=0; x<parametros.size(); x++) {
					dTotalmn = dTotalmn + Double.parseDouble(parametros.get(x).get("importeMn"));
				}*/
				tipoCambio = confirmacionCargoCtaDao.tipoCambio(idDivisa.equals("") ? "MN" : idDivisa);
				dImporteManual = dImporteManual * tipoCambio;
				cupoAuto = cupoAuto * tipoCambio;

				System.out.println(dImporteManual);
				System.out.println(cupoAuto);
			}
			iResp = confirmacionCargoCtaDao.updateCupoManualGrupo(false, cupoAuto, dImporteManual, idGrupo, fechaPago, cveControl);

			if(iResp > 0) mapRetornoMsg.put("msgUsuario", "Pagos Propuestos");
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionCargoCtaBusinessImpl, M:ejecutarDatos");
		}
		return mapRetornoMsg;
	}

	public String validaAgregarQuitar(ConfirmacionCargoCtaDto dto) {
		String sMensa = "";
		boolean bAgregar = true;
		List<ConfirmacionCargoCtaDto> listAutProp = new ArrayList<ConfirmacionCargoCtaDto>();

		try {
			if(confirmacionCargoCtaDao.consultarConfiguraSet(240).equals("SI")) {
				listAutProp = confirmacionCargoCtaDao.selectAutorizacionProp(dto);

				if(listAutProp.size() > 0) {
					switch (listAutProp.get(0).getNivelAutorizacion()) {
					case 1:
						if(listAutProp.get(0).getUsuarioUno() != 0) bAgregar = false;
						break;
					case 2:
						if(listAutProp.get(0).getUsuarioUno() != 0 && listAutProp.get(0).getUsuarioDos() != 0) bAgregar = false;
						break;
					case 3:
						if(listAutProp.get(0).getUsuarioUno() != 0 && listAutProp.get(0).getUsuarioDos() != 0 && listAutProp.get(0).getUsuarioTres() != 0) bAgregar = false;
						break;
					}
				}
				if(!bAgregar) sMensa = "Propuesta en proceso de autorizaciï¿½n, para cambios se requiere su des-autorizaciï¿½n";
			}
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionCargoCtaBusinessImpl, M:validaAgregarQuitar");
		}
		return sMensa;
	}


	//Para la pantalla de compra venta de divisas
	public List<ConfirmacionCargoCtaDto> obtenerDivisaVta (int idUsuario) {
		return confirmacionCargoCtaDao.obtenerDivisaVta(idUsuario);	
	}

	//Para la pantalla de compra venta de divisas
	public List<ConfirmacionCargoCtaDto> obtenerDivisaVta (int idUsuario, int noEmpresa) {
		return confirmacionCargoCtaDao.obtenerDivisaVta( idUsuario, noEmpresa );	
	}

	public List<ConfirmacionCargoCtaDto> obtenerBancoVta (int noEmpresa,String idDivisa) {
		return confirmacionCargoCtaDao.obtenerBancoVta (noEmpresa, idDivisa);	
	}

	public List<ConfirmacionCargoCtaDto> obtenerChequeraVta (int noEmpresa,String idDivisa, int idBanco ) {
		return confirmacionCargoCtaDao.obtenerChequeraVta (noEmpresa,idDivisa,idBanco);	
	}

	public List<ConfirmacionCargoCtaDto> obtenerBancoAbo(int noEmpresa, String idDivisa,int radio,int custodia) { 
		return confirmacionCargoCtaDao.obtenerBancoAbo(noEmpresa,idDivisa, radio, custodia);   
	}

	public List<ConfirmacionCargoCtaDto> obtenerChequeraAbo (int custodia, int radio,int noEmpresa,String idDivisa, int idBanco ) {
		return confirmacionCargoCtaDao.obtenerChequeraAbo (custodia,radio,noEmpresa,idDivisa,idBanco);	
	}

	public List<ConfirmacionCargoCtaDto> obtenerCasaCambioVta (int idUsuario) {
		return confirmacionCargoCtaDao.obtenerCasaCambioVta (idUsuario);	
	}

	public List<ConfirmacionCargoCtaDto> obtenerOperadorVta (int noCliente) {
		return confirmacionCargoCtaDao.obtenerOperadorVta (noCliente);	
	}

	public List<ConfirmacionCargoCtaDto> obtenerGrupoVta (int idUsuario) {
		return confirmacionCargoCtaDao.obtenerGrupoVta (idUsuario);	
	}

	public List<ConfirmacionCargoCtaDto> obtenerBanco (int noCliente,String idDivisa) {
		return confirmacionCargoCtaDao.obtenerBanco (noCliente, idDivisa);	
	}

	public List<ConfirmacionCargoCtaDto> obtenerRubroVta(int idGrupo) {
		return confirmacionCargoCtaDao.obtenerRubroVta (idGrupo);	
	}

	public List<ConfirmacionCargoCtaDto> obtenerChequeraTotal (int noCliente,String idDivisa, int idBanco ) {
		return confirmacionCargoCtaDao.obtenerChequeraTotal (noCliente,idDivisa,idBanco);	
	}

	public String fecHoy(){
		return confirmacionCargoCtaDao.fecHoy();
	}


	//Para la pantalla de compra venta de transferencias
	public List<ConfirmacionCargoCtaDto> claveControl(int idUsuario) { 
		return confirmacionCargoCtaDao.claveControl();
	}

	public List<ConfirmacionCargoCtaDto> DivisaLlenado(String noClaveControl) { 
		return confirmacionCargoCtaDao.DivisaLlenado(noClaveControl);
	}

	public List<ConfirmacionCargoCtaDto> llenaGridMovimientos(ConfirmacionCargoCtaDto dtoGrid) {
		boolean pbCpaVtaTransfer = true;
		return confirmacionCargoCtaDao.llenaGridMovimientos(dtoGrid, pbCpaVtaTransfer);	
	}

	public List<ConfirmacionCargoCtaDto> obtenerDivisaEmpresa (int idUsuario) {
		return confirmacionCargoCtaDao.obtenerDivisaEmpresa(idUsuario);
	}

	public List<ConfirmacionCargoCtaDto> obtenerBancoEmpresa (String nomEmpresa, String idDivisa) {
		return confirmacionCargoCtaDao.obtenerBancoEmpresa(nomEmpresa, idDivisa);
	}

	public List<ConfirmacionCargoCtaDto> obtenerChequeraEmp (ConfirmacionCargoCtaDto dtoGrid) {
		return confirmacionCargoCtaDao.obtenerChequeraEmp(dtoGrid);
	}

	public List<ConfirmacionCargoCtaDto> obtenerDivisaPago (int noEmpresa) {
		return confirmacionCargoCtaDao.obtenerDivisaPago(noEmpresa);
	}

	public List<ConfirmacionCargoCtaDto> obtenerEmpresa(int idUsuario) { 
		return confirmacionCargoCtaDao.obtenerEmpresa(idUsuario); 
	}

	public List<ConfirmacionCargoCtaDto> BancoLlenado (int noEmpresa, String idDivisa) {
		return confirmacionCargoCtaDao.BancoLlenado(noEmpresa, idDivisa); 
	}

	public List<ConfirmacionCargoCtaDto> obtenerChequera (int noEmpresa, String idDivisa, int idBanco) {
		return confirmacionCargoCtaDao.obtenerChequera(noEmpresa, idDivisa, idBanco);
	}

	public List<ConfirmacionCargoCtaDto> CasaCambio (int idUsuario) {
		return confirmacionCargoCtaDao.CasaCambio(idUsuario);
	}

	public List<ConfirmacionCargoCtaDto> BancoCasaCambio (String idDivisa, int idCasaCambio) {
		return confirmacionCargoCtaDao.BancoCasaCambio(idDivisa, idCasaCambio);
	}

	public List<ConfirmacionCargoCtaDto> obtenerChequeraCasa (int idCasaCambio, String idDivisa, int idBancoCasa) {
		return confirmacionCargoCtaDao.obtenerChequeraCasa( idCasaCambio,  idDivisa, idBancoCasa);
	}

	public List<ConfirmacionCargoCtaDto> obtenerOperador (int idCasaCambio) {
		return confirmacionCargoCtaDao.obtenerOperador( idCasaCambio);
	}

	public int Update(int psFoliosCli, String idCasaCambio, String idBancoCasa, String idChequera, String idDivisa, double tipoCambio, double importe) {
		return confirmacionCargoCtaDao.Update(psFoliosCli, idCasaCambio, idBancoCasa, idChequera, idDivisa, tipoCambio, importe);
	}

	public Map<String, Object> ejecutaPagador(List<ConfirmacionCargoCtaDto> listGridFon, String sBandera, String usuario, String pi_Banco, String chequera, 
			int pri, String ter, int seg, String ps_folios, String pi_folios_rech, String ps_folios_rech,
			int cua){

		Map<String, Object> mapRetFon = new HashMap<String, Object>();

		List<ConfirmacionCargoCtaDto> listRevisaPagos = new ArrayList<ConfirmacionCargoCtaDto>();
		Map<String, Object> resPagador = new HashMap<String, Object>();
		StoreParamsComunDto dtoPagador = new StoreParamsComunDto();
		Map<String, Object> resGenerador = new  HashMap<String, Object>();

		try {

			String sNoFolioDet = "";
			for(int c = 0; c < listGridFon.size(); c++)
			{
				listRevisaPagos = new ArrayList<ConfirmacionCargoCtaDto>();
				//listRevisaPagos = confirmacionCargoCtaDao.ejecutaPagador(listGridFon.get(c).getIdBanco()
				//		, listGridFon.get(c).getIdChequera());
				if(listRevisaPagos.size() > 0)
				{
					for(int d = 0 ; d < listRevisaPagos.size() ; d++)
					{
						resPagador = new HashMap<String, Object>();
						dtoPagador = new StoreParamsComunDto();   
						sNoFolioDet += listRevisaPagos.get(d).getNoFolioDet() + ",#"; 
						dtoPagador.setParametro(sBandera + "," + 0 + ","   
								+ globalSingleton.getUsuarioLoginDto().getIdUsuario() + "," 
								+ listGridFon.get(d).getIdBanco() + "," + listGridFon.get(d).getIdChequera() + ","+
								sNoFolioDet);
						dtoPagador.setMensaje("iniciar pagador");
						dtoPagador.setResult(0);
						//ejecuta pagador
						resPagador = confirmacionCargoCtaDao.ejecutaPagador(dtoPagador);
						if(funciones.convertirCadenaInteger(resGenerador.get("result").toString()) != 0)
							mapRetFon.put("mensajes", "Error en Pagador" + resPagador.get("result"));
						/*else
							confirmacionCargoCtaDao.eliminarPagosCheques(listRevisaPagos.get(d).getNoFolioDet(), 
								listGridFon.get(d).getIdBanco(), listGridFon.get(d).getIdChequera());*/      
					}
				}
			}

		}catch(Exception e){

		}
		return mapRetFon;
	}

	public String ejecutar(ConfirmacionCargoCtaDto dto) {
		int noFolioParam = 0;
		int noFolioParam1 = 0;
		int noFolioDocto = 0;
		//		int num = 0;
		//		int res = 0;
		int cuentaEmp = 0;
		int idCaja = 0;

		//		String resultado = "";   

		Map<String, Object> respGenerador = new HashMap<String, Object>();
		List<ConfirmacionCargoCtaDto> datosEmpresa = new ArrayList<ConfirmacionCargoCtaDto>();

		confirmacionCargoCtaDao.actualizarFolioReal("no_folio_param");
		noFolioParam = confirmacionCargoCtaDao.seleccionarFolioReal("no_folio_param");
		confirmacionCargoCtaDao.actualizarFolioReal("no_folio_docto");
		noFolioDocto = confirmacionCargoCtaDao.seleccionarFolioReal("no_folio_docto");
		datosEmpresa = confirmacionCargoCtaDao.obtenerCajaCuenta(dto.getNoEmpresa());

		if(datosEmpresa.size() > 0) {
			cuentaEmp = Integer.parseInt(datosEmpresa.get(0).getNoCuenta());
			idCaja = datosEmpresa.get(0).getIdCaja();
		}

		confirmacionCargoCtaDao.InsertAceptado(244, dto, noFolioParam, 3000, cuentaEmp, noFolioDocto, idCaja,
				"COMPRA VENTA DE DIVISAS", "0", "NULL", "", "", 0, 0, noFolioParam, 0, 0, 0, false, 0, true, -1, "", "", 0, "", false, false);   

		noFolioParam1 = noFolioParam;

		confirmacionCargoCtaDao.actualizarFolioReal("no_folio_param");
		noFolioParam = confirmacionCargoCtaDao.seleccionarFolioReal("no_folio_param");

		confirmacionCargoCtaDao.InsertAceptado(245, dto, noFolioParam, 3001, cuentaEmp, noFolioDocto, idCaja,
				"COMPRA VENTA DE DIVISAS", "0", "NULL", "", "", 0, 0, noFolioParam1, 0, 0, 0, false, 0, true, -1, "", "", 0, "", false, false);   

		confirmacionCargoCtaDao.actualizarFolioReal("no_folio_param");
		noFolioParam = confirmacionCargoCtaDao.seleccionarFolioReal("no_folio_param");

		confirmacionCargoCtaDao.InsertAceptado(247, dto, noFolioParam, 1000, cuentaEmp, noFolioDocto, idCaja,
				"COMPRA VENTA DE DIVISAS", "0", "NULL", "", "", 0, 0, noFolioParam1, 0, 0, 0, false, 0, true, -1, "", "", 0, "", false, false);   


		/*'Insercion en la tabla Parametro del Papï¿½
        lAfectados = gobjSQL.FunSQLInsert244(Val(GI_ID_EMPRESA), Val(pi_folioParametro), _
                                             3, Val(GI_USUARIO), GI_CUENTAEMP, folionuevo, _
                                             Format$(txtFechaLiq.Text, "dd/mm/yyyy"), _
                                             Format$(GT_FECHA_HOY, "dd/mm/yyyy"), psDivisa, _
                                             Format$(GT_FECHA_HOY, "dd/mm/yyyy"), _
                                             Format(txtImpVenta, "#########0.00"), _
                                             Format(txtImpVenta, "#########0.00"), _
                                             Format(txtTipoCambio, "#########0.#####0"), GI_ID_CAJA, _
                                             psDivisa, Trim$(cmbCasaCambio), "COMPRA VENTA DE DIVISAS", _
                                             Val(txtIdBanco), _
                                             Trim(cmbChequera), "0", _
                                             cmbCheqCargo, "NULL", psNoCliente, "", _
                                             "", 0, 0, pi_folioParametro, _
                                             0, 0, 0, False, 0, Trim$(txtIdRubro.Text), True, _
                                             Format$(txtFechaValor.Text, "dd/mm/yyyy"), , lblReferencia.Caption)


        pl_folioParametro1 = pi_folioParametro
        pi_folioParametro = gobjVarGlobal.Folio_Real("no_folio_param")

        If pi_folioParametro <= 0 Then
            Exit Sub
        End If

        li_tipo = 3001

        'Insercion del hijo
        lAfectados = gobjSQL.FunSQLInsert245(Val(GI_ID_EMPRESA), Val(pi_folioParametro), _
                                             3, Val(GI_USUARIO), li_tipo, GI_CUENTAEMP, _
                                             folionuevo, _
                                             Format$(txtFechaLiq.Text, "dd/mm/yyyy"), _
                                             Format$(GT_FECHA_HOY, "dd/mm/yyyy"), psDivisa, _
                                             Format$(GT_FECHA_HOY, "dd/mm/yyyy"), _
                                             Format(txtImpVenta, "#########0.00"), _
                                             Format(txtTipoCambio, "#########0.####"), GI_ID_CAJA, _
                                             psDivisa, Trim(cmbCasaCambio), "COMPRA VENTA DE DIVISAS", _
                                             Val(txtIdBanco), cmbChequera, "0", cmbCheqCargo, "", _
                                             psNoCliente, "", "", 0, 0, pl_folioParametro1, _
                                             Trim$(txtIdRubro.Text), 0, 0, 0, False, 0, True, _
                                             Format$(txtFechaValor.Text, "dd/mm/yyyy"))

        li_tipo = 1000


    End If


    pi_folioParametro = gobjVarGlobal.Folio_Real("no_folio_param")
    'Insercion del 1000
    lAfectados = gobjSQL.FunSQLInsert247(Val(GI_ID_EMPRESA), Val(pi_folioParametro), _
                                         liFormaPago, Val(GI_USUARIO), li_tipo, GI_CUENTAEMP, _
                                         folionuevo, _
                                         Format$(txtFechaLiq.Text, "dd/mm/yyyy"), _
                                         Format$(GT_FECHA_HOY, "dd/mm/yyyy"), txtDivisaCompra, _
                                         Format$(GT_FECHA_HOY, "dd/mm/yyyy"), _
                                         Format(txtImpCompra, "#########0.00"), _
                                         Format(txtTipoCambio, "#########0.####"), GI_ID_CAJA, _
                                         txtDivisaCompra, Trim(cmbCasaCambio), "COMPRA VENTA DE DIVISAS", _
                                         0, "", "0", _
                                         mat_CheqAbono(cmbCheqAbono.ItemData(cmbCheqAbono.ListIndex)), _
                                         "", psNoCliente, "", "", 0, 0, pl_folioParametro1, _
                                         Trim$(txtIdRubro.Text), 0, 0, 0, False, 0, True, _
                                         txtBcoAbono, "L", _
                                         Format$(txtFechaValor.Text, "dd/mm/yyyy"))


    'Inicia llamada al generador por grupo
    ld_resp = gobjVarGlobal.Generador("", "", GI_USUARIO, "frmVarias", GI_ID_EMPRESA, CDbl(pl_folioParametro1), a1, a2, 1)
		 */


		//cuentaEmp = confirmacionCargoCtaDao.obtenerCuenta(dto.getNoEmpresa());
		//idCaja = confirmacionCargoCtaDao.obtenerCaja(dto.getNoEmpresa());


		/*  
		 * En el metodo InsertAceptado los parametros que le mandamos son:
		 * 
		 * int inst
		 * String configSet
		 * int liTipo
		 * objeto dto (referencia a clase ConfirmacionCargoCtaDto)
		 * int num (es el noFolioParam o un 0)
		 * String noFolioDocto
		 * String concepto
		 * int pago
		 * String divisa
		 * String leyeda
		 * String obserbacion
		 * String solicita
		 * String autoriza
		 * int plaza
		 * int sucursal
		 * int pedido
		 * String referencia
		 * boolean ADU
		 * Stirng Division 
		 * int plArea
		 * boolean pbNomina
		 * int cuentaEmp 
		 * String partida
		 * int agrupa1
		 * int agrupa2
		 * int agrupa3
		 * boolean pbProvAc
		 * String idCaja
		 * boolean CVDivisa
		 * int piLote
		 */

		/// ................

		///................
		/*
		res = confirmacionCargoCtaDao.InsertAceptado(245,configSet,3001,dto,noFolioParam,noFolioDocto,
				"COMPRA VENTA DE DIVISAS",3,divisa,"O","","","",0,0,0,"",false,"",
				-1,false,cuentaEmp,"",0,0,0,false,idCaja,true,0,noFolioParam1);


		confirmacionCargoCtaDao.actualizarFolioReal("no_folio_param");
		noFolioParam = confirmacionCargoCtaDao.seleccionarFolioReal("no_folio_param");




		res = confirmacionCargoCtaDao.InsertAceptado(247,configSet,1000,dto,noFolioParam,noFolioDocto,
				"COMPRA VENTA DE DIVISAS",dto.getCustodia(),divisa,"O","","","",0,0,0,"",false,"",
				-1,false,cuentaEmp,"",0,0,0,false,idCaja,true,0,noFolioParam1);

		 */
		GeneradorDto generadorDto = new GeneradorDto();
		generadorDto.setEmpresa(dto.getNoEmpresa());
		generadorDto.setFolParam(noFolioParam1);
		generadorDto.setFolMovi(0);
		generadorDto.setFolDeta(0);
		respGenerador = confirmacionCargoCtaDao.ejecutarGenerador(generadorDto);


		if(!respGenerador.isEmpty() && Integer.parseInt(respGenerador.get("result").toString())!= 0) {
			return "Error, en Generador " + respGenerador.get("result");
		}
		else
			return "Se realizo correctamente la compra/venta de divisas";
	}




	public ConfirmacionCargoCtaDao getConfirmacionCargoCtaDao() {
		return confirmacionCargoCtaDao;
	}

	public void setConfirmacionCargoCtaDao(
			ConfirmacionCargoCtaDao confirmacionCargoCtaDao) {
		this.confirmacionCargoCtaDao = confirmacionCargoCtaDao;
	}

	public List<ConfirmacionCargoCtaDto> obtenerBancoAbo(int noEmpresa, String idDivisa) { 
		return confirmacionCargoCtaDao.obtenerBancoAbo(noEmpresa,idDivisa);   
	}

	public List<ConfirmacionCargoCtaDto> obtenerChequeraAbo (int noEmpresa, String idDivisa,int idBanco ) {
		return confirmacionCargoCtaDao.obtenerChequeraAbo (noEmpresa,idDivisa,idBanco);	
	}

	@Override
	public List<ConfirmacionCargoCtaDto> obtenerGruposPorTipoMovto( String idTipoMovto ) {

		return confirmacionCargoCtaDao.obtenerGruposPorTipoMovto( idTipoMovto );

	}//END METHOD: obtenerGruposPorTipoMovto

	@Override
	public List<ConfirmacionCargoCtaDto> llenaComboFirmas() {
		return confirmacionCargoCtaDao.llenaComboFirmas();
	}

	public ResultadoDto ejecutar2(ConfirmacionCargoCtaDto dto) {

		int noFolioParam = 0;
		int noFolioParam1 = 0;
		int noFolioDocto = 0;
		int num = 0;
		int res = 0;
		int cuentaEmp = 0;
		int idCaja = 0;

		String resultado = "";   

		Map respGenerador = new HashMap();
		List<ConfirmacionCargoCtaDto> datosEmpresa = new ArrayList<ConfirmacionCargoCtaDto>();

		confirmacionCargoCtaDao.actualizarFolioReal("no_folio_param");
		noFolioParam = confirmacionCargoCtaDao.seleccionarFolioReal("no_folio_param");
		confirmacionCargoCtaDao.actualizarFolioReal("no_folio_docto");
		noFolioDocto = confirmacionCargoCtaDao.seleccionarFolioReal("no_folio_docto");
		datosEmpresa = confirmacionCargoCtaDao.obtenerCajaCuenta(dto.getNoEmpresa());

		dto.setNoDocto(String.valueOf(noFolioDocto ) );

		if(datosEmpresa.size() > 0) {
			cuentaEmp = Integer.parseInt(datosEmpresa.get(0).getNoCuenta());
			idCaja = datosEmpresa.get(0).getIdCaja();
		}

		if( dto.getConcepto().trim().equals( "" ) ){
			dto.setConcepto( "COMPRA VENTA DE DIVISAS" );
		}

		res = confirmacionCargoCtaDao.InsertAceptado2(244, dto, noFolioParam, 3000, cuentaEmp, noFolioDocto, idCaja,
				dto.getConcepto().trim(), "0", "NULL", dto.getSolicita(), dto.getAutoriza(), 0, 0, noFolioParam, 0, 0, 0, false, 0, true, -1, dto.getReferencia(), "", 0, "", false, false);   

		noFolioParam1 = noFolioParam;

		confirmacionCargoCtaDao.actualizarFolioReal("no_folio_param");
		noFolioParam = confirmacionCargoCtaDao.seleccionarFolioReal("no_folio_param");

		res = confirmacionCargoCtaDao.InsertAceptado2(245, dto, noFolioParam, 3001, cuentaEmp, noFolioDocto, idCaja,
				dto.getConcepto().trim(), "0", "NULL", dto.getSolicita(), dto.getAutoriza(), 0, 0, noFolioParam1, 0, 0, 0, false, 0, true, -1, dto.getReferencia(), "", 0, "", false, false);   

		res = confirmacionCargoCtaDao.guardarCartaCVD(dto); 

		confirmacionCargoCtaDao.actualizarFolioReal("no_folio_param");
		noFolioParam = confirmacionCargoCtaDao.seleccionarFolioReal("no_folio_param");

		dto.setIdGrupo( dto.getGrupoIngreso() );
		dto.setIdRubro( dto.getRubroIngreso() );

		dto.setIdBanco( dto.getBancoAbono()); 
		dto.setIdChequera( dto.getChequeraAbono() );

		dto.setIdBancoBenef(0);
		dto.setIdChequeraBenef(" "); 

		res = confirmacionCargoCtaDao.InsertAceptado2(247, dto, noFolioParam, 1000, cuentaEmp, noFolioDocto, idCaja,
				dto.getConcepto().trim(), "0", "NULL", dto.getSolicita(), dto.getAutoriza(), 0, 0, noFolioParam1, 0, 0, 0, false, 0, true, -1, dto.getReferencia(), "", 0, "", false, false);   

		GeneradorDto generadorDto = new GeneradorDto();

		generadorDto.setEmpresa( dto.getNoEmpresa());
		generadorDto.setFolParam( noFolioParam );
		generadorDto.setFolMovi(0);
		generadorDto.setFolDeta(0);
		generadorDto.setIdUsuario(1);
		generadorDto.setMensajeResp("");

		//respGenerador = confirmacionCargoCtaDao.ejecutarGenerador(dto.getNoEmpresa(), noFolioParam1, 0, 0, 1, " ");
		confirmacionCargoCtaDao.ejecutarGenerador(generadorDto);

		if(!respGenerador.isEmpty() && Integer.parseInt(respGenerador.get("result").toString())!= 0) {

			return new ResultadoDto(false,"Error, en Generador " + respGenerador.get("result"),null);

		}
		else{

			return new ResultadoDto(true,"Se realizo correctamente la compra/venta de divisas", dto.getNoDocto());

		}

	}//END METHOD: ejecutar

	@Override
	public Map<String, Object> reporteContratoCompraVentaDeDivisas( String folioRepCVD ) {

		try {

			return confirmacionCargoCtaDao.getDatosReporteCVD(folioRepCVD);

		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Consultas, C:ConsultasBusiness, M:reporteCuentasPersonales");
			return null; 
		}

	}

	public int traerNoPersona(String equivalePersona){
		return confirmacionCargoCtaDao.traerNoPersona(equivalePersona); 
	}

	@Override
	public ResultadoDto ejecutarCompraDeTransfer( String folioPadre 		  , String foliosHijo 		 	 , String numRegistros 		 	, 	    
			String noProveedor 		  , String nomProveedor 		 , String idDivisaTransfer     	,
			String idBancoProveedor     , String descBancoProveedor    , String idChequeraProveedor   ,
			String descDivisaTransfer   , String totalPagoTrans 	 	 , String idCasaDeCambio 		,
			String nomCasaDeCambio      , String idBancoCasaDeCambio   , String nomBancoCasaDeCambio  , 				
			String chequeraCadaDeCambio , String tipoDeCambio          , String idBancoPagador 		,
			String nomBancoPagador      , String idChequeraPagadora    , String idDivisaDePago        ,
			String descDivisaPago       , int noUsuario ){

		//1.- CREAR LA CADENA PARA EL AJUSTE DE DIVISAS 

		StringBuilder sb = new StringBuilder();
		String cadena    = null;
		ResultadoDto resultadoDto = null;
		int respActualizacion = 0; 


		//2.- REALIZAR EL AJUSTE DE DIVISA
		respActualizacion = confirmacionCargoCtaDao.cambioDeDivisa(noUsuario, idDivisaDePago, idBancoPagador,idChequeraPagadora, tipoDeCambio, folioPadre); 

		if( respActualizacion == 0 ){
			return resultadoDto;
		}

		//3.- REALIZAR ACTUALIZACION DE LA TRASFER CON DATOS DE LA CASA

		String foliosHijoTmp = foliosHijo.substring(0, foliosHijo.length()- 1 );

		resultadoDto = confirmacionCargoCtaDao.updateTransfer(foliosHijoTmp,idCasaDeCambio,idBancoCasaDeCambio, chequeraCadaDeCambio );

		if( ! resultadoDto.isResultado() ){
			return resultadoDto; 
		}

		//4.- EJECUTAR PAGADOR

		String sBandera = "N";

		if( Integer.parseInt(numRegistros) > 1 ){
			sBandera = "A";
		}

		StoreParamsComunDto dtoPagador= new StoreParamsComunDto();	

		dtoPagador.setParametro(sBandera + "," + 0 + "," + noUsuario + ","	+ idBancoPagador + "," + idChequeraPagadora + ","+ foliosHijoTmp + "," + 1 );
		dtoPagador.setMensaje("");
		dtoPagador.setResult(0);
		try{

			confirmacionCargoCtaDao.ejecutarPagador(dtoPagador);			

		}catch(Exception e){
			return new ResultadoDto(false, "Error: En ejecucion de SP PAGADOR. Reportarlo a su Administrador.", null);
		}


		//GUARDAR DATOS DE CVT
		ConfirmacionCargoCtaDto dto = new ConfirmacionCargoCtaDto();

		ConfirmacionCargoCtaDto dtoTemp = confirmacionCargoCtaDao.obtenerEmpresa(idChequeraPagadora);

		if( dtoTemp != null ){
			dto.setNoEmpresa( dtoTemp.getNoEmpresa()  );
			dto.setNomEmpresa( dtoTemp.getNomEmpresa() );
		}

		dto.setNoDocto(folioPadre); 
		dto.setDivisaVenta(idDivisaDePago);
		dto.setDescDivisaVenta(descDivisaPago); 
		dto.setBancoCargo(Integer.parseInt(idBancoPagador)); 
		dto.setDescBancoCargo(nomBancoPagador);
		dto.setChequeraCargo(idChequeraPagadora); 
		dto.setImporteVenta( Double.parseDouble(totalPagoTrans) * Double.parseDouble(tipoDeCambio) );
		dto.setDivisaCompra(idDivisaTransfer);
		dto.setDescDivisaCompra(descDivisaTransfer);
		dto.setBancoAbono(Integer.parseInt(idBancoProveedor)); 
		dto.setDescBancoAbono(descBancoProveedor);
		dto.setChequeraAbono(idChequeraProveedor);
		dto.setTipoCambio(Double.parseDouble(tipoDeCambio));
		dto.setImporteCompra(Double.parseDouble(totalPagoTrans));
		dto.setIdCasaCambio(Integer.parseInt(idCasaDeCambio));
		dto.setDescCasaCambio(nomCasaDeCambio);
		dto.setIdBanco(Integer.parseInt(idBancoCasaDeCambio) );
		dto.setDescBancoCasa(nomBancoCasaDeCambio);
		dto.setIdChequera(chequeraCadaDeCambio);
		dto.setConcepto(nomProveedor); 

		try{
			confirmacionCargoCtaDao.guardarCartaCVT( dto );
			return new ResultadoDto(true, "Se ejecuto correctamente la Compra de Transfer", folioPadre);
		}catch(Exception e){
			return new ResultadoDto(false, "Error: En el registro final de la Operacion. Reportarlo a su Administrador.", null);
		}

	}//END METHOD: ejecutarCompraDeTransfer

	@Override
	public Map<String, Object> reporteContratoCompraTransfer( String folioRepCVD ) {

		try {

			return confirmacionCargoCtaDao.getDatosReporteCVT(folioRepCVD);

		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Consultas, C:ConsultasBusiness, M:reporteCuentasPersonales");
			return null; 
		}

	}
	@Override
	public Map<String, Object> pagoParcial(double importeTotal, int folio, int piFolioRech, int psFolioRech, double saldoPendiente, String origenMov,double interes, double iva ) {
		Map<String, Object> mapGenerador = new HashMap<String, Object>();
		Map<String, Object> mapResult = new HashMap<String, Object>();
		List<String> mensajes = new ArrayList<String>();
		DecimalFormat formato = new DecimalFormat("###,###,###,##0.00");
		System.out.println("business");
		try {
			System.out.println("importeTotal:"+importeTotal);
			System.out.println("folio:"+folio);
			globalSingleton = GlobalSingleton.getInstancia();
			String parametro=globalSingleton.getUsuarioLoginDto().getIdUsuario()+","+folio+","+importeTotal;
			mapGenerador = new HashMap<String, Object>();
			mapGenerador = confirmacionCargoCtaDao.generadorPagoParcial(parametro,interes, iva);
			piFolioRech=Integer.parseInt(mapGenerador.get("result").toString());
			System.out.println("piFolioRech"+piFolioRech);
			if (piFolioRech!=0||psFolioRech!=0) {
				mensajes.add("Error en el pagador.");
				mapResult.put("msgUsuario", mensajes);
				mapResult.put("result", 0);
			}else{
				if(!origenMov.equals("CRD")){
				mensajes.add("Solicitud Parcializada Correctamente!"+
						" Se cambió la solicitud por $" +formato.format(importeTotal)+
						" queda un saldo a pagar de $"+formato.format(saldoPendiente));
				mapResult.put("msgUsuario", mensajes);
				mapResult.put("result", 1);
				}
				else{
					mensajes.add("Solicitud Parcializada Correctamente!.\n Se cambió la solicitud por $" +formato.format(importeTotal));
					mapResult.put("msgUsuario", mensajes);
					mapResult.put("result", 1);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionTransferenciasAction, M: pagoParcial");
		}
		return mapResult;

	}
	@Override
	public Map<String, Object> crearInteres(int folio, double interes,int secuencia,String cveControl) {
		Map<String, Object> mapGenerador=new HashMap<String,Object>();
		Map<String, Object> mapResult = new HashMap<String, Object>();
		List<String> mensajes = new ArrayList<String>();
		System.out.println("business");
		try {
			System.out.println("interes:"+interes);
			System.out.println("folio:"+folio);
			globalSingleton = GlobalSingleton.getInstancia();
			mapGenerador = confirmacionCargoCtaDao.crearInteres(folio,interes,secuencia,cveControl);
			mensajes.add("Pago de Interes creado con exito");
			mapResult.put("msgUsuario", mensajes);
			mapResult.put("result", 1);
				
			
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionTransferenciasAction, M: pagoParcial");
		}
		return mapResult;

	}


}
