package com.webset.set.interfaz.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;

import com.webset.set.interfaz.dao.ExportacionDao;
import com.webset.set.interfaz.dto.InterfazDto;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.ConsultasGenerales;
import com.webset.set.utilerias.Funciones;

public class ExportadorAgrupado {
	Bitacora bitacora;
	Funciones funciones;
	ConsultasGenerales consultas;
	ExportacionDao objExportacionDao;
	private JdbcTemplate jdbcTemplate;
	
	double importeTotal = 0;
	int folioDetConta;
	int tipoOp = 0;
	int tipoO = 0;
	
	public String exportador(String foliosDet, int noEmpresa) {
		String mensaje = "";
		List<InterfazDto> listMovtos = new ArrayList<InterfazDto>();
		List<InterfazDto> ctasConta = new ArrayList<InterfazDto>();
		List<InterfazDto> listDepCCPro = new ArrayList<InterfazDto>();
		consultas = new ConsultasGenerales(jdbcTemplate);
		double tipoCambio;
		int noFolioDet;
		int res;
		
		try {
			tipoCambio = objExportacionDao.obtenTipoCambio();
			
			mensaje = agrupaComisiones(foliosDet, tipoCambio);
			
			if(!mensaje.equals("")) return mensaje;
			
			listMovtos = objExportacionDao.buscaMovtos(foliosDet, false);
			
			if(listMovtos.size() > 0) {
				for(int i=0; i<listMovtos.size(); i++) {
					ctasConta = objExportacionDao.ctasContables(Integer.parseInt(listMovtos.get(i).getNoFolioDet()), false);
					
					if(Integer.parseInt(listMovtos.get(i).getNoFolioDet()) == 49226)
						System.out.print("llego");
					
					listDepCCPro = objExportacionDao.buscaDeptoCCProyect(listMovtos, i);
					
					if(Integer.parseInt(listMovtos.get(i).getIdTipoOperacion()) != 3201) {
						res = objExportacionDao.insertZexpFact(listMovtos, i, ctasConta, tipoCambio, listDepCCPro);
						
						if(res == 0) return "Error, al insertar en zexp_fact con folio set: " + listMovtos.get(i).getNoFolioDet();
						
						if(listMovtos.get(i).getGrupoPago() != 0) {
							res = objExportacionDao.insertZexpFactDet(listMovtos, i, tipoCambio, 0, ctasConta, false, true, listDepCCPro);
							
							if(res == 0) return "Error, al insertar en zexp_fact_det con folio set: " + listMovtos.get(i).getNoFolioDet();
						}
					}else {
						res = objExportacionDao.insertZexpFactDet(listMovtos, i, tipoCambio, 0, ctasConta, false, false, listDepCCPro);
						
						if(res == 0) return "Error, al insertar en zexp_fact_det con folio set: " + listMovtos.get(i).getNoFolioDet();
						
//						res = objExportacionDao.colocaCtaCargo(listMovtos, i, ctasConta);
					}
					
					if(!listMovtos.get(i).getIdDivisa().equals("MN")) {
						res = objExportacionDao.insertZexpFactDet(listMovtos, i, tipoCambio, 0, ctasConta, false, false, listDepCCPro);
						
						if(res == 0) return "Error, al insertar en zexp_fact_det dolares con folio set: " + listMovtos.get(i).getNoFolioDet();
						
						noFolioDet = objExportacionDao.seleccionarFolioReal("folio_ref_conta");
						objExportacionDao.actualizarFolioReal("folio_ref_conta");
						
						ctasConta = objExportacionDao.ctasContables(Integer.parseInt(listMovtos.get(i).getNoFolioDet()), true);
						
						res = objExportacionDao.insertZexpFactDet(listMovtos, i, tipoCambio, noFolioDet, ctasConta, true, false, listDepCCPro);
						if(res == 0) return "Error, al insertar en zexp_fact_det dolares complementaria con folio set: " + noFolioDet;
						
						noFolioDet = objExportacionDao.seleccionarFolioReal("folio_ref_conta");
						objExportacionDao.actualizarFolioReal("folio_ref_conta");
						
						res = objExportacionDao.insertZexpFactDet(listMovtos, i, tipoCambio, noFolioDet, ctasConta, false, true, listDepCCPro);
						if(res == 0) return "Error, al insertar en zexp_fact_det dolares a pesos total con folio set: " + noFolioDet;
						
						
					}
					res = objExportacionDao.updateMovimiento(listMovtos, i);
					if(res == 0) return "Error, al actualizar los movimientos que se exportaron!!";
				}
				mensaje = "Registros exportados con exito!!";
			}else return "No se encontraron registros para exportar!!";
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Interfaz, C: ExportacionBusinessImpl, M: exportaRegistros");
		}return mensaje;
	}
	
	public String agrupaComisiones(String foliosDet, double tipoCambio) {
		List<InterfazDto> listMovtos = new ArrayList<InterfazDto>();
		int folioComple = 0;
		int res = 0;
		int i=0;
		int x=0;
		String resp = "";
		List<InterfazDto> ctasConta = new ArrayList<InterfazDto>();
		
		try {
			listMovtos = objExportacionDao.buscaMovtos(foliosDet, true);
			
			if(listMovtos.size() > 0) {
				tipoOp = Integer.parseInt(listMovtos.get(0).getIdTipoOperacion().toString());
				
				for(x=0; x<listMovtos.size(); x++) {
					if(tipoOp == Integer.parseInt(listMovtos.get(x).getIdTipoOperacion().toString()))
						tipoO++;
				}
				
				if(tipoO > 1) {
					folioDetConta = objExportacionDao.seleccionarFolioReal("folio_ref_conta");
					objExportacionDao.actualizarFolioReal("folio_ref_conta");
				}
				
				for(i=0; i<listMovtos.size(); i++) {
					if(tipoO == 0) {
						for(x=0; x<listMovtos.size(); x++) {
							if(tipoOp == Integer.parseInt(listMovtos.get(x).getIdTipoOperacion().toString()))
								tipoO++;
						}
					}
					if(tipoOp == Integer.parseInt(listMovtos.get(i).getIdTipoOperacion().toString()) && tipoO > 1) {
						if(!listMovtos.get(i).getIdDivisa().equals("MN"))
							importeTotal += (listMovtos.get(i).getImporte() * tipoCambio);
						else
							importeTotal += listMovtos.get(i).getImporte();
						
						ctasConta = objExportacionDao.ctasContables(Integer.parseInt(listMovtos.get(i).getNoFolioDet()), false);
						
						res = objExportacionDao.insertComisionesAgrupDet(listMovtos, i, tipoCambio, folioDetConta, false, 0, ctasConta, false, importeTotal);
						
						if(res == 0) return "Error al insertar a zexp_fact_det MN";
						
						objExportacionDao.updateMovComisiones(Integer.parseInt(listMovtos.get(i).getNoFolioDet()));
						
						if(!listMovtos.get(i).getIdDivisa().equals("MN")) {
							folioComple = objExportacionDao.seleccionarFolioReal("folio_ref_conta");
							objExportacionDao.actualizarFolioReal("folio_ref_conta");
							
							ctasConta = new ArrayList<InterfazDto>();
							ctasConta = objExportacionDao.ctasContables(Integer.parseInt(listMovtos.get(i).getNoFolioDet()), true);
							
							res = objExportacionDao.insertComisionesAgrupDet(listMovtos, i, tipoCambio, folioDetConta, true, folioComple, ctasConta, false, importeTotal);
							
							if(res == 0) return "Error al insertar a zexp_fact_det DLS";
						}
					}else {
						if(tipoO > 1) i--;
						
						resp = insertaAgrupados(listMovtos, i, tipoCambio, folioComple, ctasConta, false);
						if(!resp.equals("")) return resp;
						tipoO = 0;
					}
				}
				if(tipoO > 1) {
					i--;
					resp = insertaAgrupados(listMovtos, i, tipoCambio, folioComple, ctasConta, true);
					tipoO = 0;
				}
			}
		}
		catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Interfaz, C:ExportacionBusinessImpl, M:agrupaComisiones");
			return e.toString();
		}
		return resp;
	}
	
	public String insertaAgrupados(List<InterfazDto> listMovtos, int i, double tipoCambio, int folioComple, List<InterfazDto> ctasConta, boolean fin) {
		int res = 0;
		String resp = "";
		
		try {
			
			if(objExportacionDao.buscaComisionesAgrup(Integer.parseInt(listMovtos.get(i).getNoFolioDet())) == 0) {
				res = objExportacionDao.insertComisionesAgrup(listMovtos, i, importeTotal, folioDetConta, tipoO);
				
				if(res == 0) return "Error al insertar a zexp_fact Agrupado";
				else
					objExportacionDao.updateMovComisiones(Integer.parseInt(listMovtos.get(i).getNoFolioDet()));
				if(tipoO > 1)
					res = objExportacionDao.insertComisionesAgrupDet(listMovtos, i, tipoCambio, folioDetConta, false, folioComple, ctasConta, true, importeTotal);
				
				if(res == 0) return "Error al insertar a zexp_fact_det el agrupado";
				
				if(!fin) {
					if(tipoO > 1) {
						folioDetConta = objExportacionDao.seleccionarFolioReal("folio_ref_conta");
						objExportacionDao.actualizarFolioReal("folio_ref_conta");
					}
					if(i < listMovtos.size()-1)
						tipoOp = Integer.parseInt(listMovtos.get(i+1).getIdTipoOperacion().toString());
					importeTotal = 0;
				}
			}else
				objExportacionDao.updateMovComisiones(Integer.parseInt(listMovtos.get(i).getNoFolioDet()));
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Interfaz, C:ExportacionBusinessImpl, M:insertaAgrupados");
			return e.toString();
		}
		return resp;
	}

	public ExportacionDao getObjExportacionDao() {
		return objExportacionDao;
	}

	public void setObjExportacionDao(ExportacionDao objExportacionDao) {
		this.objExportacionDao = objExportacionDao;
	}
	
}
