package com.webset.set.utilerias.business.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.webset.set.egresos.dto.BloqueoPagoCruzadoDto;
import com.webset.set.egresos.dto.ConsultaPropuestasDto;
import com.webset.set.egresos.dto.PagosPropuestosDto;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.ConstantesSet;
import com.webset.set.utilerias.GlobalSingleton;
import com.webset.set.utilerias.dao.ReglasNegocioDao;
import com.webset.set.utilerias.dto.AcreedorDoctoDto;
import com.webset.set.utilerias.dto.CondicionPagoDto;
import com.webset.set.utilerias.dto.DoctoTesoreriaDto;
import com.webset.set.utilerias.dto.EmpresaRegNegDto;
import com.webset.set.utilerias.dto.MovimientoDto;
import com.webset.set.utilerias.dto.PlanPagosDto;
import com.webset.set.utilerias.dto.ReglasNegocioDto;
import com.webset.set.utilerias.dto.RubroRegNeg;
import com.webset.set.utilerias.dto.SeleccionAutomaticaGrupoDto;
import com.webset.set.utilerias.service.ReglasNegocioService;
import com.webset.utils.tools.Utilerias;

public class ReglasNegocioBusinessImpl implements ReglasNegocioService {

	private ReglasNegocioDao reglasNegocioDao;
	
	private Bitacora bitacora = new Bitacora(); 
	 
	@Override
	public List<ReglasNegocioDto> obtenerRelacion() {
		
		List<ReglasNegocioDto>list= new ArrayList<ReglasNegocioDto>();
		
		try{
			
			list = reglasNegocioDao.obtenerRelacion();
			 
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Utilerias, C:ReglasNegocioBussinesImpl, M:obtenerRelacion");	
		}
		return list;
	}

	/**********************************************************************/
	public ReglasNegocioDao getReglasNegocioDao() {
		return reglasNegocioDao;
	}

	public void setReglasNegocioDao(ReglasNegocioDao reglasNegocioDao) {
		this.reglasNegocioDao = reglasNegocioDao;
	}
	/**********************************************************************/

	@Override
	public List<EmpresaRegNegDto> obtenerEmpresas(int idUsuario, boolean bExiste, int idRegla) {
		
		List<EmpresaRegNegDto> empresas = new ArrayList<EmpresaRegNegDto>();
		
		try{
			empresas = reglasNegocioDao.obtenerEmpresas(idUsuario, bExiste, idRegla);
			
			for(int i=0; i<empresas.size();i++){
				if(empresas.get(i).getHoraLimiteOperacion().length() > 0){
					empresas.get(i).setHoraLimiteOperacion(empresas.get(i).getHoraLimiteOperacion().substring(11,19)); //substring para que muestre solo la hora y no fecha
				}
			}
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Utilerias, C:ReglasNegocioBussinesImpl, M:obtenerEmpresas");	
		}
		
		return empresas;
		
	}

	@Override
	public List<AcreedorDoctoDto> obtenerAcreedorDocumento(boolean bTipoAcre, int idRegla, String clasificacion) {

		List<AcreedorDoctoDto> acreedores = new ArrayList<AcreedorDoctoDto>();
		
		try{
			acreedores = reglasNegocioDao.obtenerAcreedorDocumento(bTipoAcre, idRegla, clasificacion);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Utilerias, C:ReglasNegocioBussinesImpl, M:obtenerAcreedorDocumento");	
		}
		
		return acreedores;
	}

	@Override
	public List<DoctoTesoreriaDto> obtenerDoctoTesoreria(int idRegla, String clasificacion) {

		List<DoctoTesoreriaDto> list = new ArrayList<DoctoTesoreriaDto>();
		
		try{
			list = reglasNegocioDao.obtenerDoctoTesoreria(idRegla, clasificacion);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Utilerias, C:ReglasNegocioBussinesImpl, M:obtenerDoctoTesoreria");	
		}
		
		return list;
	}

	@Override
	public CondicionPagoDto obtenerCondicionesPago(int idRegla) {

		List<CondicionPagoDto> list = new ArrayList<CondicionPagoDto>();
		CondicionPagoDto condicionPago = new CondicionPagoDto();
		
		try{
			list = reglasNegocioDao.obtenerCondicionesPago(idRegla);
			
			if(list.size() > 0){
				condicionPago = list.get(0);
			}
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Utilerias, C:ReglasNegocioBussinesImpl, M:obtenerCondicionesPago");	
		}
		
		return condicionPago;
		
	}

	@Override
	public List<PlanPagosDto> obtenerPlanPago(int idCondicioPago) {
		
		List<PlanPagosDto> list = new ArrayList<PlanPagosDto>();
		
		try{
			
			list = reglasNegocioDao.obtenerPlanPago(idCondicioPago);
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Utilerias, C:ReglasNegocioBussinesImpl, M:obtenerPlanPago");	
		}
		
		return list;
	}

	@Override
	public Map<String, String> insertarActualizarReglaNegocio(ReglasNegocioDto reglaNegocio, List<EmpresaRegNegDto> listEmpresas,
			List<AcreedorDoctoDto> listAcreDocto, List<DoctoTesoreriaDto> listDoctoTes, CondicionPagoDto condicionPago,
			List<PlanPagosDto> listPlanPagos,List<RubroRegNeg> listRubros, String tipoOperacion, List<AcreedorDoctoDto> lEliminarAcreDocto, List<DoctoTesoreriaDto> lEliminarDoctoTes,
			List<PlanPagosDto> lEliminarFechas,
			boolean eliminaDiasContabVenc, boolean eliminaDiasAdicionales,
			boolean eliminaDiasespecificos, boolean eliminaPlanPagos, boolean simulador){
		
		Map<String,String> result = new HashMap<String,String>();
		result.put("error", "");
		result.put("mensaje", "");
		
		try{
			
			if(reglaNegocio.getTipoRegla().equals("")){
				result.put("error", "Selecciona un tipo de regla de negocios");
				return result;
			}
			 
			if(reglaNegocio.getReglaNegocio().equals("")){
				result.put("error", "Escriba un nombre para la regla de negocio");
				return result;
			}
			
			if(listEmpresas.size() <= 0){
				result.put("error", "No hay empresas asignadas");
				return result;
			}
			
			if(reglaNegocio.getLongChequera() > 0 && reglaNegocio.getLongChequera() > 99){
				result.put("error", "Se debe escribir un valor de dos d�gitos en longitud chequera.");
				return result;
			}
			
			List<AcreedorDoctoDto> nvaListaAcreDocto = new ArrayList<>();
			
			if(listAcreDocto.size() <= 0){
				result.put("error", "No hay ningun registro en Acreedores ni documentos");
				return result;
			}else{
				
				for(AcreedorDoctoDto acreDocto: listAcreDocto){
					
					if(!acreDocto.getDeAcre().equals("") && !acreDocto.getaAcre().equals("")){
						nvaListaAcreDocto.add(acreDocto);
					}else if(acreDocto.getDeAcre().equals("") && !acreDocto.getaAcre().equals("")){
						if(acreDocto.getClasificacion().equals("A")){
							if(acreDocto.getTipoAcre().equals("I")){
								result.put("error", "Campo vacio \"DE\" en rango Acreedor Incluir");
							}else{
								result.put("error", "Campo vacio \"DE\" en rango Acreedor Excluir");
							}
							
						}else{
							if(acreDocto.getTipoAcre().equals("I")){
								result.put("error", "Campo vacio \"DE\" en rango Documento Incluir");
							}else{
								result.put("error", "Campo vacio \"DE\" en rango Documento Excluir");
							}
						}
						return result;
					}else if(!acreDocto.getDeAcre().equals("") && acreDocto.getaAcre().equals("")){
						if(acreDocto.getClasificacion().equals("A")){
							if(acreDocto.getTipoAcre().equals("I")){
								result.put("error", "Campo vacio \"A\" en rango Acreedor Incluir");
							}else{
								result.put("error", "Campo vacio \"A\" en rango Acreedor Excluir");
							}
						}else{
							if(acreDocto.getTipoAcre().equals("I")){
								result.put("error", "Campo vacio \"A\" en rango Documento Incluir");
							}else{
								result.put("error", "Campo vacio \"A\" en rango Documento Excluir");
							}
						}
						return result;
					}
				}
			}
			
			//Validamos que no este vacio en acreedores con la nueva lista
			int ca = 0; 
			//int cd = 0;
			for(AcreedorDoctoDto acreDocto: nvaListaAcreDocto){
				if(acreDocto.getClasificacion().equals("A")){ //acreedores
					ca++;
				}
				
				/*if(acreDocto.getClasificacion().equals("D")){	//documentos
					cd++;
				}*/
			}
			
			if(ca==0){
				result.put("error", "No hay ningun registro en Acreedores");
				return result;
			}
			
			/*	SE COMENTA YA QUE CAMBIARON A QUE DOCUMENTOS PUDIERA QUEDAR VACIO: EMS 08/12/2015  
			 if(cd==0){
			 
				result.put("error", "No hay ningun registro en Documentos");
				return result;
			}
			*/
			
			//Se pasar� como par�metro nvaListaDoctoTes
			List<DoctoTesoreriaDto> nvaListaDoctoTes = new ArrayList<>();
			
			if(listDoctoTes.size() <= 0){
				//Se comenta ya que pidieron que no fuera obligatorio clase docto. y grupo tesoreria	EMS: 07/12/15
				//result.put("error", "No hay ningun registro en Clase Documento ni Grupo Tesoreria");
				//return result;
			}else{
				
				for(DoctoTesoreriaDto doctoTes: listDoctoTes){
					
					if(!doctoTes.getIncluir().equals("") || !doctoTes.getExcluir().equals("")){
						nvaListaDoctoTes.add(doctoTes);
					}
					
				}
			}

/*	YA NO ES OBLIGATORIO: EMS 07/12/15
			ca = 0; //clase docto
			cd = 0; //grupo teso
			
			for(DoctoTesoreriaDto doctoTeso: nvaListaDoctoTes){
				if(doctoTeso.getClasificacion().equals("D")){
					ca++;
				}
				
				if(doctoTeso.getClasificacion().equals("T")){
					cd++;
				}
			}
			
			if(ca==0){
				result.put("error", "No hay ningun registro en Clase Documento");
				return result;
			}
			
			if(cd==0){
				result.put("error", "No hay ningun registro en Grupo Tesorer�a");
				return result;
			}
			
	*/		
			 //USUARIOS
			/*	YA NO ES OBLIGATORIO: EMS 07/12/15
			 * if(reglaNegocio.getUsuarios().equals("")){
				result.put("error", "Campo Usuarios vac�o.");
				return result;
			}*/
			
			//INDICADOR
			if(reglaNegocio.getIndicador().length() > 1){
				result.put("error", "Indicador no puede ser mayor a 1 caracter.");
				return result;
			}
			

			//CONDICION PAGO 
			if(condicionPago.getFechaBase().equals("")){
				result.put("error", "Falta seleccionar Fecha Base");
				return result;
			}
			
			if(condicionPago.getClaseDia().equals("")){
				result.put("error", "Falta seleccionar Clase de D�as");
				return result;
			}
			
			if(condicionPago.getAccionDia().equals("")){
				result.put("error", "Falta seleccionar Acci�n Fin de Semana/Destiempo");
				return result;
			}
			
			if(condicionPago.isAplicaDiasAdicionales() && condicionPago.getDiasAdicionales() <= 0){
				result.put("error", "N�mero d�as adicionales invalido");
				return result;
			}
			
			if(condicionPago.getBancoInterlocutor().length() > 4){
				result.put("error", "Banco interlocutor no puede ser mayor a 4 d�gitos.");
				return result;
			}
			
			//DIAS CONTAB VENCIMIENTO
			if(condicionPago.isAplicaContab()){
				if(condicionPago.getDiaPago1().equals("")){
					result.put("error", "Falta asignar d�a de contab vencimiento en LUNES");
					return result;
				}
				
				if(condicionPago.getDiaPago2().equals("")){
					result.put("error", "Falta asignar d�a de contab vencimiento en MARTES");
					return result;
				}
				
				if(condicionPago.getDiaPago3().equals("")){
					result.put("error", "Falta asignar d�a de contab vencimiento en MIERCOLES");
					return result;
				}
				
				if(condicionPago.getDiaPago4().equals("")){
					result.put("error", "Falta asignar d�a de contab vencimiento en JUEVES");
					return result;
				}
				
				if(condicionPago.getDiaPago5().equals("")){
					result.put("error", "Falta asignar d�a de contab vencimiento en VIERNES");
					return result;
				}
				
				if(condicionPago.getDiaPago6().equals("")){
					result.put("error", "Falta asignar d�a de contab vencimiento en SABADO");
					return result;
				}
				
				if(condicionPago.getDiaPago7().equals("")){
					result.put("error", "Falta asignar d�a de contab vencimiento en DOMINGO");
					return result;
				}
				
				if(condicionPago.getSemana1().equals("")){
					result.put("error", "Falta asignar semana de pago en LUNES");
					return result;
				}
				
				if(condicionPago.getSemana2().equals("")){
					result.put("error", "Falta asignar semana de pago en MARTES");
					return result;
				}
				
				if(condicionPago.getSemana3().equals("")){
					result.put("error", "Falta asignar semana de pago en MIERCOLES");
					return result;
				}
				
				if(condicionPago.getSemana4().equals("")){
					result.put("error", "Falta asignar semana de pago en JUEVES");
					return result;
				}
				
				if(condicionPago.getSemana5().equals("")){
					result.put("error", "Falta asignar semana de pago en VIERNES");
					return result;
				}
				
				if(condicionPago.getSemana6().equals("")){
					result.put("error", "Falta asignar semana de pago en S�BADO");
					return result;
				}
				
				if(condicionPago.getSemana7().equals("")){
					result.put("error", "Falta asignar semana de pago en DOMINGO");
					return result;
				}
			}
			
			//Dias excepcion
			if(condicionPago.isAplicaDiasExcep()){
				if(condicionPago.getDiaEspecifico1() <= 0){
					result.put("error", "Digite d�a espec�fico(1)");
					return result;
				}
				/*if(condicionPago.getDiaExcep1().equals("")){
					result.put("error", "Seleccione d�a excepci�n(1)");
					return result;
				}*/
				
				/*if(condicionPago.getDiaPagoExcep1().equals("")){
					result.put("error", "Seleccione d�a de pago por excepci�n(1)");
					return result;
				}*/
				
				/* EMS 06/01/2016 - SE QUITA VALIDACI�N A PETICI�N DE JORGE COLLAZO./ 
				if(condicionPago.getRangoDiaDesde1() <= 0){
					result.put("error", "Digite un n�mero mayor a 0 en rango de d�as Desde(1)");
					return result;
				}
				
				if(condicionPago.getRangoDiaHasta1() <= 0){
					result.put("error", "Digite un n�mero mayor a 0 en rango de d�as hasta(1)");
					return result;
				}
				*/
				/** VALIDACIONES DE VALORES EN RANGOS	**/
				if(condicionPago.getRangoDiaDesde1()  > condicionPago.getRangoDiaHasta1()){
					result.put("error", "El campo rango de d�as Desde(1) debe ser menor que el campo Hasta(1)");
					
					return result;
				}
				
				if(condicionPago.getRangoDiaDesde1()  < 0){
					result.put("error", "El campo rango de d�as Desde(1) no debe estar vacio o ser menor que 1.");
				}
				
				if(condicionPago.getRangoDiaHasta1()  < 0){
					result.put("error", "El campo rango de d�as Hasta(1) no debe estar vacio o ser menor que 1.");
				}
				
				if(condicionPago.getRangoDiaDesde1()  == 0 && condicionPago.getRangoDiaHasta1()  != 0){
					result.put("error", "El campo rango de d�as Desde(1) no debe ser 0.");
				}
				
				if(condicionPago.getRangoDiaDesde1()  != 0 && condicionPago.getRangoDiaHasta1()  == 0){
					result.put("error", "El campo rango de d�as Hasta(1) no debe ser 0.");
				}
				
				//PARA DIA ESPECIFICO 2
				/* EMS 06/01/2016 - SE QUITA VALIDACI�N A PETICI�N DE JORGE COLLAZO./
				if(condicionPago.getDiaEspecifico2() <= 0){
					
					if(condicionPago.getFinMes().equals("N")){
						result.put("error", "Debe seleccionar el check \"Fin Mes\" ");
						return result;
					}
					
				}else{
					//si dia especifico(2) no esta vacio, forsozamente fin mes debe estar deseleccionado
					if(condicionPago.getFinMes().equals("S")){
						result.put("error", "Debe quitar la selecci�n de \"Fin Mes\" ");
						return result;
					}
					
				}
				*/
				
				if(!condicionPago.getDiaExcep2().equals("") && condicionPago.getDiaEspecifico2() <= 0){
					result.put("error", "Debe llenar campo D�a Espec�fico(2)");
					return result;
				}/*else if(condicionPago.getDiaExcep2().equals("") && condicionPago.getDiaEspecifico2() > 0){
					result.put("error","Debe seleccionar D�a Excepci�n(2)");
					return result;
				}*/
				
				if(!condicionPago.getDiaPagoExcep2().equals("") && condicionPago.getDiaEspecifico2() <= 0){
					result.put("error", "Debe llenar campo D�a Espec�fico(2)");
					return result;
				}else if(condicionPago.getDiaPagoExcep2().equals("") && condicionPago.getDiaEspecifico2() > 0){
					result.put("error","Debe seleccionar d�a Pago Excepci�n(2)");
					return result;
				}
				
				if(condicionPago.getRangoDiaDesde2() > 0 && condicionPago.getDiaEspecifico2() <= 0){
					result.put("error", "Debe llenar campo D�a Espec�fico(2)");
					return result;
				}/*else if(condicionPago.getRangoDiaDesde2() <= 0 && condicionPago.getDiaEspecifico2() > 0){
					result.put("error","Debe llenar campo Rango Desde(2)");
					return result;
				}
				*/
				
				if(condicionPago.getRangoDiaHasta2() > 0 && condicionPago.getDiaEspecifico2() <= 0){
					result.put("error", "Debe llenar campo D�a Espec�fico(2)");
					return result;
				}/*else if(condicionPago.getRangoDiaHasta2() <= 0 && condicionPago.getDiaEspecifico2() > 0){
					result.put("error","Debe llenar campo Rango Hasta(2)");
					return result;
				}*/
				
				if(condicionPago.getRangoDiaDesde2()  > condicionPago.getRangoDiaHasta2()){
					result.put("error", "El campo rango de d�as Desde(2) debe ser menor que el campo Hasta(2)");
						
					return result;
				}				
				
				if(condicionPago.getRangoDiaDesde2()  < 0){
					result.put("error", "El campo rango de d�as Desde(2) no debe estar vacio o ser menor que 1.");
				}
				
				if(condicionPago.getRangoDiaHasta2()  < 0){
					result.put("error", "El campo rango de d�as Hasta(2) no debe estar vacio o ser menor que 1.");
				}
				
				if(condicionPago.getRangoDiaDesde2()  == 0 && condicionPago.getRangoDiaHasta2() != 0){
					result.put("error", "El campo rango de d�as Desde(2) no debe ser 0.");
				}
				
				if(condicionPago.getRangoDiaDesde2()  != 0 && condicionPago.getRangoDiaHasta2()  == 0){
					result.put("error", "El campo rango de d�as Hasta(2) no debe ser 0.");
				}
				
			}
			
			if(listPlanPagos.size() > 0){
				
				for(int i=0; i<listPlanPagos.size(); i++){
					if(listPlanPagos.get(i).getFecha().equals("")){
						result.put("error", "Ingrese una fecha en el campo vacio Plan pagos");
						return result;
					}
				}
				
			}
			/* RUBROS - CAMPO SOLO INFORMATIVO, DA IGUAL SI VA VACIO O NO.
			if(listRubros.size() <= 0){
				result.put("error", "No hay rubros asignados");
				return result;
			}
			*/
			
			//Si cumple todas las validaciones guardamos/actualizamos el registro.
			int afec = 0;
			if(tipoOperacion.equals("I") || simulador == true){
				
				afec = reglasNegocioDao.insertarReglaNegocio(reglaNegocio, listEmpresas,
															nvaListaAcreDocto,nvaListaDoctoTes, 
															condicionPago,listPlanPagos, 
															listRubros, tipoOperacion, simulador);
				if(afec > 0){
					result.put("mensaje", "Regla de negocio guardada con �xito.");
				}else{
					
					switch (afec) {
					case -1:
						result.put("error", "Error al insertar en Regla Negocio");
						break;
					case -2:
						result.put("error", "Error al insertar empresas");
						break;
					case -3:
						result.put("error", "Error al insertar Acreedores/Documento");
						break;	
					case -4:
						result.put("error", "Error al insertar Clase Documento/Tesorer�a");
						break;	
					case -5:
						result.put("error", "Error al insertar condici�n pago");
						break;
					case -6:
						result.put("error", "Error al insertar contab vencimiento.");
						break;	
					case -7:
						result.put("error", "Error al insertar d�as espec�ficos/excepcion");
						break;	
					case -8:
						result.put("error", "Error al insertar plan de pagos");
						break;	
					case -9:
						result.put("error", "Error al insertar rubros.");
						break;
					}
					
				}
				return result;
			}else if(tipoOperacion.equals("U")){
				
				afec = reglasNegocioDao.actualizarReglaNegocio(reglaNegocio, listEmpresas, nvaListaAcreDocto,nvaListaDoctoTes, 
																condicionPago,listPlanPagos, listRubros, tipoOperacion,
																lEliminarAcreDocto, lEliminarDoctoTes, lEliminarFechas,
																eliminaDiasContabVenc, eliminaDiasAdicionales,
																eliminaDiasespecificos, eliminaPlanPagos);
				
				if(afec > 0){
					result.put("mensaje", "Regla de negocio actualizada con �xito.");
					return result;
				}else{
					switch (afec) {
					case -1:
						result.put("error", "Error al actualizar en Regla Negocio");
						break;
					case -2:
						result.put("error", "Error al actualizar empresas");
						break;
					case -3:
						result.put("error", "Error al actualizar Acreedores/Documento");
						break;	
					case -4:
						result.put("error", "Error al actualizar Clase Documento/Tesorer�a");
						break;	
					case -5:
						result.put("error", "Error al actualizar condici�n pago");
						break;
					case -6:
						result.put("error", "Error al actualizar contab vencimiento.");
						break;	
					case -7:
						result.put("error", "Error al actualizar d�as espec�ficos/excepcion");
						break;	
					case -8:
						result.put("error", "Error al actualizar plan de pagos");
						break;
					case -9:
						result.put("error", "Error al eliminar acreedores/documentos.");
						break;	
					case -10:
						result.put("error", "Error al eliminar Clase Documento/ Grupo Tesoreria.");
						break;	
					case -11:
						result.put("error", "Error al eliminar fechas de plan de pago.");
						break;	
					case -12:
						result.put("error", "Error al actualizar los rubros.");
						break;	
					case -13:
						result.put("error", "Error al eliminar los d�as contab vencimiento.");
						break;
					case -14:
						result.put("error", "Error al elminar dias adicionales.");
						break;
					case -15:
						result.put("error", "Error al eliminar d�as especificos.");
						break;
					case -16:
						result.put("error", "Error al eliminar plan de pagos.");
						break;
					}
				
				}
				
			}else{
				result.put("error", "Error en tipo de operaci�n");
				return result;
			}
			
			//result = reglasNegocioDao.obtenerPlanPago(idCondicioPago);
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Utilerias, C:ReglasNegocioBussinesImpl, M:InsertarActualizarReglaNegocio");	
		}
		
		return result;
	}

	@Override
	public Map<String, String> eliminarReglaNegocio(int idRegla) {
		
		Map<String,String> resultado = new HashMap<String,String>();
		resultado.put("error", "");
		resultado.put("mensaje", "");
		int afec = 0;
		try{
			
			afec = reglasNegocioDao.eliminarReglaNegocio(idRegla);
			
			if(afec == 0){
				resultado.put("error", "No se elimin� la regla");
			}else{
				resultado.put("mensaje", "Se ha eliminado la regla correctamente");
			}

		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Utilerias, C:ReglasNegocioBussinesImpl, M:eliminarReglaNegocio");
			resultado.put("error", "Error al eliminar");
		}
		
		return resultado;
	}

	@Override
	public Map<String, String> validarDocumento(List<AcreedorDoctoDto> list) {

		Map<String, String> resultado = new HashMap<>();
		resultado.put("error", "");
		resultado.put("mensaje", "");
		
		try{
			resultado = reglasNegocioDao.validarDocumento(list);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Utilerias, C:ReglasNegocioBussinesImpl, M:validarDocumentos");
		}
		
		return resultado;
	}

	@Override
	public Map<String, Object> validarAcreedores(List<AcreedorDoctoDto> list, String tipoOper) {
		
		Map<String, Object> resultado = new HashMap<>();
		resultado.put("error", "");
		resultado.put("mensaje", "");
		resultado.put("existe", list); //Lista de acreedores si existen en otra regla de negocio
		resultado.put("listaOriginal",list);
		resultado.put("listaAcre",list);
		
		try{
			resultado = reglasNegocioDao.validarAcreedores(list, tipoOper);
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Utilerias, C:ReglasNegocioBussinesImpl, M:validarAcreedores");
		}
		
		return resultado;
	}

	@Override
	public Map<String, String> validarEmpresas(List<EmpresaRegNegDto> list, int idRegla, int idUsuario) {
		
		Map<String, String> resultado = new HashMap<>();
		resultado.put("error", "");
		resultado.put("mensaje", "");
		
		try{
			resultado = reglasNegocioDao.validarEmpresas(list, idRegla, idUsuario);
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Utilerias, C:ReglasNegocioBussinesImpl, M:validarEmpresas");
		}
		
		return resultado;
		
	}

	@Override
	public List<RubroRegNeg> obtenerRubros(boolean bAsignados, int idRegla) {

		List<RubroRegNeg> rubros = new ArrayList<RubroRegNeg>();
		
		try{
			rubros = reglasNegocioDao.obtenerRubros(bAsignados, idRegla);			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Utilerias, C:ReglasNegocioBussinesImpl, M:obtenerRubros");	
		}
		
		return rubros;
	}
	
	public String exportaExcel(String datos) {
		String respuesta = "";
		
		if(datos.equals("")){
			return respuesta;
		}
		
	    Gson gson = new Gson();
		List<Map<String, String>> parameters = gson.fromJson(datos, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		
	    try {	    	
			//Se crea el libro Excel
			HSSFWorkbook wb = Utilerias.generarExcel(new String[]{
													"Regla Negocio", //Titulos
										            "Tipo regla",
										            "Fecha Captura",
										            "Usuario capturo"
												}, 
												parameters, //datos
												new String[]{
														"reglaNegocio", //keys
														"tipoRegla",
														"fechaCaptura",
														//"usuarioCaptura"
														"claveUsuarioCaptura"
												});			
            
            respuesta = ConstantesSet.RUTA_EXCEL + "regNeg " + Utilerias.indicadorFecha() +".xls";
            File outputFile = new File(respuesta);
          
			FileOutputStream outs = new FileOutputStream(outputFile);
			wb.write(outs);
			outs.close();            
        } catch (IOException e) {
        	bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
        	+ "P:Egresos, C:ReglasNegocioBusinessImpl, M:exportaExcel");
        	respuesta = "";
        } catch(Exception e){
        	bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
        	+ "P:Egresos, C:ReglasNegocioBusinessImpl, M:exportaExcel");
            respuesta = "";
        }
        return respuesta;
	}

	//idRegla - Parametro obsoleto.
	@Override
	public String procesaSimulador(int idRegla) {
		
		String folios = "";
		
		try{
			//Obtenemos la regla de negocio de la tabla temporal
			//if(idRegla==0){
				idRegla = reglasNegocioDao.obtenerIdReglaSim();	
			//}
			
			folios = reglasNegocioDao.procesaSimulador(idRegla);			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Utilerias, C:ReglasNegocioBussinesImpl, M:procesaSimulador");	
		}
		
		return folios;
	}
	
	//Agregado EMS: 03/02/2016
	@Override
	public List<SeleccionAutomaticaGrupoDto> consultarSeleccionAutomaticaSIM(ConsultaPropuestasDto dtoIn){
		List<SeleccionAutomaticaGrupoDto>list= new ArrayList<SeleccionAutomaticaGrupoDto>();
		int count=0;
		String rech="";
		
		try {
			list=reglasNegocioDao.consultarSeleccionAutomaticaSIM(dtoIn);
			
			if(list.size()>0) {
				for(int i=0; i<list.size();i++) {
			
		            
		            		
            		//*** MOVIMIENTOS QUE VIENEN DE OTRA REGLA DE NEGOCIO
		           
		            count = reglasNegocioDao.buscaMovimientosTmp(list.get(i).getCveControl());
				    
		            if(count>0) {
		            	list.get(i).setEstatus("");
		            	list.get(i).setColor("color:#FDA920");	//Naranja
		            }
		            
				}
			}
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Utilerias, C:ReglasNegocioBusiness, M:consultarSeleccionAutomatica");
		}
		return list;
	}
	
	
	//Modicicado EMS:/04/11/2015
		public List<MovimientoDto>consultarDetalle(SeleccionAutomaticaGrupoDto dtoIn){
			PagosPropuestosDto consDetalleDto= new PagosPropuestosDto();
			List<MovimientoDto> listDetalle= new ArrayList<MovimientoDto>();
			
			try{ 
				if(dtoIn.getCveControl().trim().substring(0,1).equals("M")){
					consDetalleDto.setIdGrupoEmpresa(dtoIn.getIdGrupoFlujo());
					consDetalleDto.setIdGrupo(dtoIn.getIdGrupo());//Checar
					consDetalleDto.setCveControl(dtoIn.getCveControl());
					consDetalleDto.setFecha(GlobalSingleton.getInstancia().getFechaHoy());//Checar
					consDetalleDto.setPsDivision("");
					listDetalle= reglasNegocioDao.consultarDetalle(consDetalleDto);
				}
				
				for(int i=0; i<listDetalle.size(); i++){
					if(listDetalle.get(i).getComentario2() != null && !listDetalle.get(i).getComentario2().equals("")){
						listDetalle.get(i).setColor("color:#FDA920"); //Naranja - Significa que viene de otra regla/ propuesta.
						
						List<SeleccionAutomaticaGrupoDto> lstSAG = 
														reglasNegocioDao.existeSeleccionAutomaticaGrupoSIM(
																			listDetalle.get(i).getComentario2());
						
						//Buscamos la regla de la que viene
						if(lstSAG.size() > 0 ){
							listDetalle.get(i).setComentario1(lstSAG.get(0).getConcepto());
						}

					}
				}

				/*
				int count = 0;
				List<BloqueoPagoCruzadoDto> listBloqueados = new ArrayList<>();

				if(listDetalle.size() > 0) {
					for(int i=0; i<listDetalle.size(); i++) {
						
						//Checa si ya trae el color desde la consulta, hasta el momento solo no_cobrador.
						if(listDetalle.get(i).getColor() != null && listDetalle.get(i).getColor().equals("NARANJA")){
							listDetalle.get(i).setColor("color:#FDA920"); //Naranja
							continue;
						}
						
						//Verifica que no este bloqueado el proveedor
						listBloqueados = consultaPropuestasDao.obtenerBloqueoProveedor(
														""+listDetalle.get(i).getNoEmpresa(),
														""+listDetalle.get(i).getEquivale(),
														dtoIn.getCveControl());
						
						if(listBloqueados.size()>0){
							
							//Hace corrimiento, si hay alg�n registro sin documento, es bloqueo proveedor, se PINTA el registro.
							//Si no, es bloqueo de documento, se compara con el documento de la propuesta, si no son iguales, no se pinta azul.
							for(int j=0; j<listBloqueados.size(); j++){
								if(listBloqueados.get(j).getNoDocumento() == null || listBloqueados.get(j).getNoDocumento().equals("")){
									listDetalle.get(i).setColor("color:#090DFA"); //Azul
									break;
								}else if(listBloqueados.get(j).getNoDocumento() != null && listBloqueados.get(j).getNoDocumento().equals(listDetalle.get(i).getNoDocto())){
									listDetalle.get(i).setColor("color:#090DFA");
									break;
								}
							}
							
							
						}else{
							//Si hay bloqueo de SAP
							listBloqueados = consultaPropuestasDao.obtenerBloqueoSAP(
									""+dtoIn.getCveControl(),
									""+listDetalle.get(i).getNoDocto());
							
							if(listBloqueados.size()>0){
								listDetalle.get(i).setColor("color:#090DFA"); //Azul
							}
							
						}
						
						//Valida que existan las chequeras benef
						count = consultaPropuestasDao.existeChequeraBenef(listDetalle.get(i));
						//Si no existe se pinta de color
						if(count == 0){
							listDetalle.get(i).setColor("color:#7030A0"); //morado
						}else{
							listDetalle.get(i).setColor("color:#000000");
						}
						
						
						
					}
				}
				*/
			}catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
				+ "P:Utilerias, C:ReglasNegocioBusinessImpl, M:consultarDetalle");
			}
			return listDetalle;
		}
		
		//Agregado EMS 10/02/16
		@Override
		public Map<String,Object> actualizarMovtosSimulador(int idRegla) {
			
			Map<String, Object> result = new HashMap<>();
			result.put("error", "");
			result.put("msgUsuario", "");
			
			try{
				result = reglasNegocioDao.actualizarMovtosSimulador(idRegla);			
			}catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
				+"P:Utilerias, C:ReglasNegocioBussinesImpl, M:actualizarMovtosSimulador");	
			}
			
			return result;
		}
		
		@Override
		public Map<String,Object> deshacerCambiosSimulador() {
			
			Map<String, Object> result = new HashMap<>();
			result.put("error", "");
			result.put("msgUsuario", "");
			
			try{
				boolean res = reglasNegocioDao.deshacerCambiosSimulador();
				
				if(!res){
					result.put("error","Error al borrar los datos temporales de simulaci�n.");
				}else{
					result.put("msgUsuario", "Datos cancelados con �xito!");
				}
				
			}catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
				+"P:Utilerias, C:ReglasNegocioBussinesImpl, M:deshacerCambiosSimulador");	
			}
			
			return result;
		}

		@Override
		public Map<String, Object> loginSimulador() {
			Map<String, Object> result = new HashMap<>();
			result.put("error", "");
			
			try{
				result = reglasNegocioDao.loginSimulador();
				
			}catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
				+"P:Utilerias, C:ReglasNegocioBussinesImpl, M:loginSimulador");	
			}
			
			return result;
		}
		
		@Override
		public Map<String, Object> insertarLoginSimulador() {
			Map<String, Object> result = new HashMap<>();
			result.put("error", "");
			
			try{
				result = reglasNegocioDao.insertarLoginSimulador();
				
			}catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
				+"P:Utilerias, C:ReglasNegocioBussinesImpl, M:insertarLoginSimulador");	
			}
			
			return result;
		}
		
		@Override
		public Map<String, Object> eliminaLogueoUsuario() {
			Map<String, Object> result = new HashMap<>();
			result.put("error", "");
			
			try{
				result = reglasNegocioDao.eliminaLogueoUsuario();
				
			}catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
				+"P:Utilerias, C:ReglasNegocioBussinesImpl, M:eliminaLogueoUsuario");	
			}
			
			return result;
		}
		
		
}
