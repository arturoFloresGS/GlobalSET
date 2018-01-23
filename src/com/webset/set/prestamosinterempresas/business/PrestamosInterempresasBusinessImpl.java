package com.webset.set.prestamosinterempresas.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRMapArrayDataSource;

import com.webset.set.prestamosinterempresas.dao.PrestamosInterempresasDao;
import com.webset.set.prestamosinterempresas.dto.ArbolEmpresaDto;
import com.webset.set.prestamosinterempresas.dto.CuentaDto;
import com.webset.set.prestamosinterempresas.dto.HistSaldoDto;
import com.webset.set.prestamosinterempresas.dto.InteresPrestamoDto;
import com.webset.set.prestamosinterempresas.dto.ParamComunDto;
import com.webset.set.prestamosinterempresas.dto.ParamInteresPresNoDoc;
import com.webset.set.prestamosinterempresas.dto.ParamRepIntNetoDto;
import com.webset.set.prestamosinterempresas.dto.PersonaDto;
import com.webset.set.prestamosinterempresas.dto.RetInteresPresNoDoc;
import com.webset.set.prestamosinterempresas.dto.SolTraspasoCreditoDto;
import com.webset.set.prestamosinterempresas.service.PrestamosInterempresasService;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utilerias.GlobalSingleton;
import com.webset.set.utilerias.dto.GeneradorDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.set.utilerias.dto.ParametroDto;
import com.webset.set.utilerias.dto.TmpEdoCuentaDto;
import com.webset.set.utilerias.dto.UsuarioLoginDto;

public class PrestamosInterempresasBusinessImpl implements PrestamosInterempresasService{

	private PrestamosInterempresasDao prestamosInterempresasDao;
	private Bitacora bitacora = new Bitacora();
	private GlobalSingleton globalSingleton;
	private Funciones funciones = new Funciones();
	private int iCanDec = 2;
	
	public List<LlenaComboGralDto> obtenerEmpresasRaiz(boolean bExistentes)
	{
		List<LlenaComboGralDto> listEmpRaiz = new ArrayList<LlenaComboGralDto>();
		try
		{
			listEmpRaiz = prestamosInterempresasDao.consultarEmpresasRaiz(bExistentes);
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas C: PrestamosInterempresasBusinessImpl M: obtenerEmpresasRaiz");
		}
		return listEmpRaiz;
	}
	
	public List<LlenaComboGralDto> obtenerEmpresasHijo(int iEmpresaRaiz)
	{
		List<LlenaComboGralDto> listEmpHijo = new ArrayList<LlenaComboGralDto>();
		try
		{
			listEmpHijo = prestamosInterempresasDao.consultarEmpresasHijo(iEmpresaRaiz);
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas C: PrestamosInterempresasBusinessImpl M: obtenerEmpresasHijo");
		}
		return listEmpHijo;
	}
	
	public String obtenerArbolEmpresa(int iEmpresaRaiz)
	{
		int a = 0, b = 0, c = 0, d = 0, f = 0, g = 0, h = 0, i = 0;
		int iSecuencia = 0;
		int iIdUsuario = 0;
		StringBuffer sbstruc = new StringBuffer();
		StringBuffer sbMenu = new StringBuffer();
		List<LlenaComboGralDto> listEmpPadre = new ArrayList<LlenaComboGralDto>();
		List<LlenaComboGralDto> listEmpHijo = new ArrayList<LlenaComboGralDto>();
		List<LlenaComboGralDto> listEmpNieto = new ArrayList<LlenaComboGralDto>();
		List<LlenaComboGralDto> listEmpBisnieto = new ArrayList<LlenaComboGralDto>();
		List<LlenaComboGralDto> listEmpTatara = new ArrayList<LlenaComboGralDto>();
		List<LlenaComboGralDto> listEmpChos1 = new ArrayList<LlenaComboGralDto>();
		List<LlenaComboGralDto> listEmpChos2 = new ArrayList<LlenaComboGralDto>();
		List<LlenaComboGralDto> listEmpChos3 = new ArrayList<LlenaComboGralDto>();
		List<LlenaComboGralDto> listEmpChos4 = new ArrayList<LlenaComboGralDto>();
		try
		{
			globalSingleton = GlobalSingleton.getInstancia();
			iIdUsuario = globalSingleton.getUsuarioLoginDto().getIdUsuario();
			
			prestamosInterempresasDao.eliminarTmpArbol(iIdUsuario);
			
			listEmpPadre = prestamosInterempresasDao.consultarEmpresaPadre(iEmpresaRaiz);
			if(listEmpPadre.size() > 0)
			{
				//Inserta padre
				iSecuencia ++;
				prestamosInterempresasDao.insertarTmpArbol(iIdUsuario, iSecuencia, listEmpPadre.get(0).getDescripcion(), "padre");
				//inicia estructura padre --
				sbstruc.append("[{id:" + listEmpPadre.get(0).getId() + "," + "text:'" + listEmpPadre.get(0).getDescripcion() + "'," + "singleClickExpand: " + false + ",allowDrag: "+ false);//+",qtipCfg: '"+iEmpresaRaiz+"'");
				//inicia estructura hijo
				listEmpHijo = prestamosInterempresasDao.consultarEmpresasHijos(iEmpresaRaiz, listEmpPadre.get(0).getId());
				if(listEmpHijo.size() <= 0)
					sbstruc.append(",leaf: " + true);
				for(a = 0; a < listEmpHijo.size(); a ++)
				{	
					//Inserta Hijo
					iSecuencia ++;
					prestamosInterempresasDao.insertarTmpArbol(iIdUsuario, iSecuencia, listEmpHijo.get(a).getDescripcion(), "hijo");
					
					if(a == 0)
						sbstruc.append(",children: [");
					else
						sbstruc.append(",");
					
					sbstruc.append("{");
					sbstruc.append("id:" + listEmpHijo.get(a).getId() + ",");  
					sbstruc.append("text:'" + listEmpHijo.get(a).getDescripcion() + "',");
					sbstruc.append("singleClickExpand: " + false );
					sbstruc.append(", allowDrag: " + false);
					//sbstruc.append(", qtipCfg: '" + iEmpresaRaiz +"-"+listEmpHijo.get(a).getId()+"'");
					//qtipCfg
					//inicia estructura nieto
					listEmpNieto = prestamosInterempresasDao.consultarEmpresasNietos(iEmpresaRaiz, listEmpHijo.get(a).getId());
					if(listEmpNieto.size() <= 0)
						sbstruc.append(",leaf: " + true);
					for(b = 0; b < listEmpNieto.size(); b ++)
					{
						//Inserta Nieto
						iSecuencia ++;
						prestamosInterempresasDao.insertarTmpArbol(iIdUsuario, iSecuencia, listEmpNieto.get(b).getDescripcion(), "nieto");
						
						if(b == 0)
							sbstruc.append(",children: [");
						else
							sbstruc.append(",");
						
						sbstruc.append("{");
						sbstruc.append("id:" + listEmpNieto.get(b).getId() + ",");  
						sbstruc.append("text:'" + listEmpNieto.get(b).getDescripcion() + "',");
						sbstruc.append("singleClickExpand: " + false);
						sbstruc.append(", allowDrag: " + false);
						//sbstruc.append(", qtipCfg: '" + iEmpresaRaiz +"-"+listEmpHijo.get(a).getId()+"-"+listEmpNieto.get(b).getId()+"'");
						//inicia estructura bisnieto
						listEmpBisnieto = prestamosInterempresasDao.consultarEmpresasBisnietos(iEmpresaRaiz, listEmpNieto.get(b).getId());
						if(listEmpBisnieto.size() <= 0)
							sbstruc.append(",leaf: " + true);
						for(c = 0; c < listEmpBisnieto.size(); c ++)
						{
							
							//Inserta Bisnieto
							iSecuencia ++;
							prestamosInterempresasDao.insertarTmpArbol(iIdUsuario, iSecuencia, listEmpBisnieto.get(c).getDescripcion(), "bisnieto");

							if(c == 0)
								sbstruc.append(",children: [");
							else
								sbstruc.append(",");
							
							sbstruc.append("{");
							sbstruc.append("id:" + listEmpBisnieto.get(c).getId() + ",");  
							sbstruc.append("text:'" + listEmpBisnieto.get(c).getDescripcion() + "',");
							sbstruc.append("singleClickExpand: " + false);
							sbstruc.append(", allowDrag: " + false);
							//sbstruc.append(", qtipCfg: '" + iEmpresaRaiz +"-"+listEmpHijo.get(a).getId()+"-"+listEmpNieto.get(b).getId()+"-"+listEmpBisnieto.get(c).getId()+"'");
							//inicia estructura tataranieto
							listEmpTatara = prestamosInterempresasDao.consultarEmpresasTataranietos(iEmpresaRaiz, listEmpBisnieto.get(c).getId());
							if(listEmpTatara.size() <= 0)
								sbstruc.append(",leaf: " + true);
							for(d = 0; d < listEmpTatara.size(); d ++)
							{
								//Inserta tataranieto
								iSecuencia ++;
								prestamosInterempresasDao.insertarTmpArbol(iIdUsuario, iSecuencia, listEmpTatara.get(d).getDescripcion(), "tatara");

								if(d == 0)
									sbstruc.append(",children: [");
								else
									sbstruc.append(",");
								
								sbstruc.append("{");
								sbstruc.append("id:" + listEmpTatara.get(d).getId() + ",");  
								sbstruc.append("text:'" + listEmpTatara.get(d).getDescripcion() + "',");
								sbstruc.append("singleClickExpand: " + false);
								sbstruc.append(", allowDrag: " + false);
								sbstruc.append(", qtipCfg: '" + iEmpresaRaiz +"-"+listEmpHijo.get(a).getId()+"-"+listEmpNieto.get(b).getId()+"-"+listEmpBisnieto.get(c).getId()+"-"+listEmpTatara.get(d).getId()+"'");
								//inicia estructura chosno 1
								listEmpChos1 = prestamosInterempresasDao.consultarEmpresasChosnoUno(iEmpresaRaiz, listEmpTatara.get(d).getId());
								if(listEmpChos1.size() <= 0)
									sbstruc.append(",leaf: " + true);
								for(f = 0; f < listEmpChos1.size(); f ++)
								{
									//Inserta Chosno 1
									iSecuencia ++;
									prestamosInterempresasDao.insertarTmpArbol(iIdUsuario, iSecuencia, listEmpChos1.get(f).getDescripcion(), "chosno1");

									if(f == 0)
										sbstruc.append(",children: [");
									else
										sbstruc.append(",");
									
									sbstruc.append("{");
									sbstruc.append("id:" + listEmpChos1.get(f).getId() + ",");  
									sbstruc.append("text:'" + listEmpChos1.get(f).getDescripcion() + "',");
									sbstruc.append("singleClickExpand: " + false);
									sbstruc.append(", allowDrag: " + false);
									sbstruc.append(", qtipCfg: '" + iEmpresaRaiz +"-"+listEmpHijo.get(a).getId()+"-"+listEmpNieto.get(b).getId()+"-"+listEmpBisnieto.get(c).getId()+"-"+listEmpTatara.get(d).getId()+"-"+listEmpChos1.get(f).getId()+"'");
									//inicia estructura chosno 2
									listEmpChos2 = prestamosInterempresasDao.consultarEmpresasChosnoDos(iEmpresaRaiz, listEmpChos1.get(f).getId());
									if(listEmpChos2.size() <= 0)
										sbstruc.append(",leaf: " + true);
									for(g = 0; g < listEmpChos2.size(); g ++)
									{
										//Inserta Chosno 2
										iSecuencia ++;
										prestamosInterempresasDao.insertarTmpArbol(iIdUsuario, iSecuencia, listEmpChos2.get(g).getDescripcion(), "chosno2");

										if(g == 0)
											sbstruc.append(",children: [");
										else
											sbstruc.append(",");
										
										sbstruc.append("{");
										sbstruc.append("id:" + listEmpChos2.get(g).getId() + ",");  
										sbstruc.append("text:'" + listEmpChos2.get(g).getDescripcion() + "',");
										sbstruc.append("singleClickExpand: " + false);
										sbstruc.append(", allowDrag: " + false);
										sbstruc.append(", qtipCfg: '" + iEmpresaRaiz +"-"+listEmpHijo.get(a).getId()+"-"+listEmpNieto.get(b).getId()+"-"+listEmpBisnieto.get(c).getId()+"-"+listEmpTatara.get(d).getId()+"-"+listEmpChos1.get(f).getId()+"-"+listEmpChos2.get(g).getId()+"'");
										//inicia estructura chosno 3
										listEmpChos3 = prestamosInterempresasDao.consultarEmpresasChosnoTres(iEmpresaRaiz, listEmpChos2.get(g).getId());
										if(listEmpChos3.size() <= 0)
											sbstruc.append(",leaf: " + true);
										for(h = 0; h < listEmpChos3.size(); h ++)
										{
											//Inserta Chosno 3
											iSecuencia ++;
											prestamosInterempresasDao.insertarTmpArbol(iIdUsuario, iSecuencia, listEmpChos3.get(h).getDescripcion(), "chosno3");
											
											if(h == 0)
												sbstruc.append(",children: [");
											else
												sbstruc.append(",");
											
											sbstruc.append("{");
											sbstruc.append("id:" + listEmpChos3.get(h).getId() + ",");  
											sbstruc.append("text:'" + listEmpChos3.get(h).getDescripcion() + "',");
											sbstruc.append("singleClickExpand: " + false);
											sbstruc.append(", allowDrag: " + false);
											sbstruc.append(", qtipCfg: '" + iEmpresaRaiz +"-"+listEmpHijo.get(a).getId()+"-"+listEmpNieto.get(b).getId()+"-"+listEmpBisnieto.get(c).getId()+"-"+listEmpTatara.get(d).getId()+"-"+listEmpChos1.get(f).getId()+"-"+listEmpChos2.get(g).getId()+"-"+listEmpChos3.get(h).getId()+"'");
											//inicia estructura chosno 4
											listEmpChos4 = prestamosInterempresasDao.consultarEmpresasChosnoCuatro(iEmpresaRaiz, listEmpChos3.get(h).getId());
											if(listEmpChos4.size() <= 0)
												sbstruc.append(",leaf: " + true);
											for(i = 0; i < listEmpChos4.size(); i ++)
											{
												//Inserta Chosno 4
												iSecuencia ++;
												prestamosInterempresasDao.insertarTmpArbol(iIdUsuario, iSecuencia, listEmpChos4.get(i).getDescripcion(), "chosno4");
												
												if(i == 0)
													sbstruc.append(",children: [");
												else
													sbstruc.append(",");
												
												sbstruc.append("{");
												sbstruc.append("id:" + listEmpChos4.get(i).getId() + ",");  
												sbstruc.append("text:'" + listEmpChos4.get(i).getDescripcion() + "',");
												sbstruc.append("singleClickExpand: " + false);
												sbstruc.append(", allowDrag: " + false);
												sbstruc.append(",leaf: " + true);
												sbstruc.append(", qtipCfg: '" + iEmpresaRaiz +"-"+listEmpHijo.get(a).getId()+"-"+listEmpNieto.get(b).getId()+"-"+listEmpBisnieto.get(c).getId()+"-"+listEmpTatara.get(d).getId()+"-"+listEmpChos1.get(f).getId()+"-"+listEmpChos2.get(g).getId()+"-"+listEmpChos3.get(h).getId()+"'");
												sbstruc.append("}");
												
												if(i + 1 == listEmpChos4.size())//Con. para cerrar ultimo bisnieto
													sbstruc.append("]");
											}
											//termina estructura chosno 4
											sbstruc.append("}");
											
											if(h + 1 == listEmpChos3.size())//Con. para cerrar ultimo bisnieto
												sbstruc.append("]");
										}
										//termina estructura chosno 3
										sbstruc.append("}");
										
										if(g + 1 == listEmpChos2.size())//Con. para cerrar ultimo bisnieto
											sbstruc.append("]");
									}
									//termina estructura chosno 2
									sbstruc.append("}");
									
									if(f + 1 == listEmpChos1.size())//Con. para cerrar ultimo bisnieto
										sbstruc.append("]");
								}
								//termina estructura chosno 1
								sbstruc.append("}");
								
								if(d + 1 == listEmpTatara.size())//Con. para cerrar ultimo bisnieto
									sbstruc.append("]");
							}
							//termina estructura tataranieto
							sbstruc.append("}");
							
							if(c + 1 == listEmpBisnieto.size())//Con. para cerrar ultimo bisnieto
								sbstruc.append("]");
							
						}
						//termina estructura bisnieto
						sbstruc.append("}");
						
						if(b + 1 == listEmpNieto.size())//Con. para cerrar ultimo nieto
							sbstruc.append("]");
						
					}
					//termina estructura nieto
					
					sbstruc.append("}");
					
					if(a + 1 == listEmpHijo.size())//Con. para cerrar ultimo hijo
						sbstruc.append("]");
				}
				//termina estructura hijo
				sbstruc.append("}]");
				//fin estructura nodo padre
				
				sbMenu.append("{id: " + iEmpresaRaiz + ",descripcion: " + sbstruc + "}");
			}
			else
				sbMenu.append("{}");
			
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas C: PrestamosInterempresasBusinessImpl M: obtenerArbolEmpresa");
		}
		//bitacora.insertarRegistro("PAEENODES"+sbMenu.toString());
		return sbMenu.toString();
	}
	
	public String agregarNodosArbol(boolean bNvoHijo, String sRuta, int iIdEmpresaRaiz, int iIdEmpresa, double uMonto)
	{
		int iNumValores = 0;
		String sMsgUsuario = "";
		String sCampos = "";
		String sValoresCampos = "";
		String sUltEmpresa = "";
		
		boolean bExisteEmp = false;
		try
		{
			if(bNvoHijo)
			{
				bExisteEmp = prestamosInterempresasDao.consultarEmpArbolExis(iIdEmpresaRaiz, iIdEmpresa).size() > 0;
				if(bExisteEmp)
				{
					sMsgUsuario = "Ya existe la ruta de la empresa " + iIdEmpresa + " en el �rbol de la empresa: " + iIdEmpresaRaiz;
					return sMsgUsuario;
				}
				
				sValoresCampos = sRuta.replace("/", ",");
				while(!sRuta.equals(""))
				{
					if(sRuta.indexOf("/") > 0)
					{
						iNumValores ++;
						sRuta = sRuta.substring(sRuta.indexOf("/") + 1);
					}
					else
					{
						iNumValores = iNumValores + 2;
						sValoresCampos += "," + iIdEmpresa + "," + iIdEmpresa + ", 'N', " + uMonto;//Se agrega la nueva empresa
						sUltEmpresa = sRuta;//Se obtiene la ultima empresa de la ruta (la que fue seleccionada)
						sRuta = "";
					}
				}
				
				if(iNumValores > 9)
				{
					sMsgUsuario = "Solo existen 10 niveles de descendencia";
					return sMsgUsuario;
				}
				
				//Actualiza el estatus b_padre en 'S'
				prestamosInterempresasDao.actualizarArbEmpresaEstatusPadre(iIdEmpresaRaiz, Integer.parseInt(sUltEmpresa), "S");
				
				switch (iNumValores)
				{
					case 1:
						sCampos += "empresa_raiz,padre,no_empresa,b_padre,monto_maximo";
					break;
					case 2:
						sCampos += "empresa_raiz,padre,hijo,no_empresa,b_padre,monto_maximo";
					break;
					case 3:
						sCampos += "empresa_raiz,padre,hijo,nieto,no_empresa,b_padre,monto_maximo";
					break;
					case 4:
						sCampos += "empresa_raiz,padre,hijo,nieto,bisnieto,no_empresa,b_padre,monto_maximo";
					break;
					case 5:
						sCampos += "empresa_raiz,padre,hijo,nieto,bisnieto,tataranieto,no_empresa,b_padre,monto_maximo";
					break;
					case 6:
						sCampos += "empresa_raiz,padre,hijo,nieto,bisnieto,tataranieto,chosno,no_empresa,b_padre,monto_maximo";
					break;
					case 7:
						sCampos += "empresa_raiz,padre,hijo,nieto,bisnieto,tataranieto,chosno,chosno2,no_empresa,b_padre,monto_maximo";
					break;
					case 8:
						sCampos += "empresa_raiz,padre,hijo,nieto,bisnieto,tataranieto,chosno,chosno2,chosno3,no_empresa,b_padre,monto_maximo";
					break;
					case 9:
						sCampos += "empresa_raiz,padre,hijo,nieto,bisnieto,tataranieto,chosno,chosno2,chosno3,chosno4,no_empresa,b_padre,monto_maximo";
					break;
				}
				
				prestamosInterempresasDao.insertarArbolEmpresa(sCampos, iIdEmpresaRaiz + "," + sValoresCampos);
				
				sMsgUsuario = "Nodo agregado correctamente";
			}
			else
			{
				bExisteEmp = prestamosInterempresasDao.consultarEmpArbolExis(iIdEmpresaRaiz, iIdEmpresaRaiz).size() > 0;
				if(bExisteEmp)
				{
					sMsgUsuario = "Ya existe la ruta de la empresa " + iIdEmpresa + " en el �rbol de la empresa: " + iIdEmpresaRaiz;
					return sMsgUsuario;
				}
				
				sCampos = "empresa_raiz,padre,no_empresa,b_padre, monto_maximo";
				sValoresCampos = iIdEmpresaRaiz + "," + iIdEmpresaRaiz + "," + iIdEmpresaRaiz + ",'N'," + uMonto;
				prestamosInterempresasDao.insertarArbolEmpresa(sCampos, sValoresCampos);
				
				sMsgUsuario = "Nodo agregado correctamente";
			}
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas C: PrestamosInterempresasBusinessImpl M: agregarNodosArbol");
		}
		return sMsgUsuario;
	}
	
	public String eliminarNodosArbol(int iIdEmpresaRaiz, int iIdEmpresaActual, int iIdEmpresaPadre)
	{
		String sMsgUsuario = "";
		int iRegElim = 0;
		int iCuentaNodos = 0;
		double uSaldoEmpresa = 0;
		List<ArbolEmpresaDto> listValidaPadre = new ArrayList<ArbolEmpresaDto>();
		List<String> listCampo = new ArrayList<String>();
		try
		{
			listValidaPadre = prestamosInterempresasDao.consultarValidaPadre(iIdEmpresaRaiz, iIdEmpresaActual);
			
			if(listValidaPadre.size() > 0)
			{
				if(listValidaPadre.get(0).getBPadre().trim().equals("S"))
				{
					sMsgUsuario = "La empresa " + iIdEmpresaActual  + " no puede eliminarse";
					return sMsgUsuario;
				}
				else if(listValidaPadre.get(0).getBPadre().trim().equals("N"))
				{
					//Proceso para el borrado del nodo
					uSaldoEmpresa = prestamosInterempresasDao.consultarSaldoEmpresaArbol(iIdEmpresaActual);
					if(uSaldoEmpresa <= 0)
					{
						iRegElim = prestamosInterempresasDao.eliminarNodoArbolEmpresa(iIdEmpresaRaiz, iIdEmpresaActual);
					 
						listCampo = prestamosInterempresasDao.consultarCampoArbolEmpresa(iIdEmpresaRaiz, iIdEmpresaPadre);
						
						if(listCampo.size() > 0)
						{
							iCuentaNodos = prestamosInterempresasDao.consultarNumeroEmpresasArbol(iIdEmpresaRaiz, iIdEmpresaPadre);
							if(iCuentaNodos == 1)
							{
								prestamosInterempresasDao.actualizarArbEmpresaEstatusPadre(iIdEmpresaRaiz, iIdEmpresaPadre, "N");
							}
						}
						
					}
					else
					{
						sMsgUsuario = "No se puede eliminar la empresa debido a que tiene un saldo pendiente...";
						return sMsgUsuario;
					}
					
					sMsgUsuario = "Baja realizada correctamente";
					return sMsgUsuario;
				}
			}
			else
			{
				sMsgUsuario = "No se encontr� la empresa en arbol_empresa";
				return sMsgUsuario;
			}
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas C: PrestamosInterempresasBusinessImpl M: eliminarNodosArbol");
		}
		return sMsgUsuario;
	}
	
	public JRDataSource obtenerReporteArbolEmpresa()
	{
		JRMapArrayDataSource jrDataSource = null;
		List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();
		int iIdUsuario = 0;
		try
		{
			globalSingleton = GlobalSingleton.getInstancia();
			//iIdUsuario = globalSingleton.getUsuarioLoginDto().getIdUsuario();
			iIdUsuario = 2;//Cabiar por la linea de arriba
			listMap = prestamosInterempresasDao.consultarTmpArbol(iIdUsuario);
			
			jrDataSource = new JRMapArrayDataSource(listMap.toArray());
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas C: PrestamosInterempresasBusinessImpl M: obtenerReporteArbolEmpresa");
		}
		return jrDataSource;
	}
	
	public List<LlenaComboGralDto> obtenerEmpresaConcentradora()
	{
		int iIdUsuario = 0;
		List<LlenaComboGralDto> listEmpCon = new ArrayList<LlenaComboGralDto>();
		try
		{
			globalSingleton = GlobalSingleton.getInstancia();
			iIdUsuario = globalSingleton.getUsuarioLoginDto().getIdUsuario();
			listEmpCon = prestamosInterempresasDao.consultarEmpresaConcentradora(iIdUsuario);
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas C: PrestamosInterempresasBusinessImpl M: obtenerEmpresaConcentradora");
		}
		return listEmpCon;
	}
	
	public List<LlenaComboGralDto> obtenerBancosEmpresa(int iIdEmpresa, int iIdBanco ,String sIdCheqD)
	{
		String sIdDivisa = "";
		List<LlenaComboGralDto> listBanc = new ArrayList<LlenaComboGralDto>();
		try
		{
			if(!sIdCheqD.equals("") && iIdBanco > 0)
				sIdDivisa = this.verificarDivisas(iIdBanco, sIdCheqD);
			
			listBanc = prestamosInterempresasDao.consultarBancosEmpresa(iIdEmpresa, sIdDivisa);
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas C: PrestamosInterempresasBusinessImpl M: obtenerBancosEmpresa");
		}
		return listBanc;
	}
	
	
	public List<LlenaComboGralDto> obtenerEmpresasDestino(int iIdEmpresa)
	{
		String sParentesco = "";
		List<LlenaComboGralDto> listEmp = new ArrayList<LlenaComboGralDto>();
		try
		{
			sParentesco = "";
			listEmp = prestamosInterempresasDao.consultarEmpresasArbol(iIdEmpresa, sParentesco);
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas C: PrestamosInterempresasBusinessImpl M: obtenerEmpresasDestino");
		}
		return listEmp;
	}
	
	/**
	 * Este m�todo obtiene las chequeras origen y destino para el traspaso,
	 * si se quiere la de origen solo se envian empresa y banco,
	 * pero si se desea la de destino se requiere ademas la chequera origen para mostrar
	 * las chequeras destino con la misma divisa.
	 * @param iIdEmpresa
	 * @param iIdBanco
	 * @param sIdCheqD
	 * @return
	 */
	public List<LlenaComboGralDto> obtenerChequeras(int iIdEmpresa, int iIdBancoD, String sIdCheqD, int iIdBancoA)
	{
		String sIdDivisa = "";
		List<LlenaComboGralDto> listChe = new ArrayList<LlenaComboGralDto>();
		try
		{
			if(!sIdCheqD.equals("") && iIdBancoD > 0)
			{
				sIdDivisa = this.verificarDivisas(iIdBancoD, sIdCheqD);
				listChe = prestamosInterempresasDao.consultarChequeras(iIdEmpresa, iIdBancoA, sIdDivisa);
			}
			else
				listChe = prestamosInterempresasDao.consultarChequeras(iIdEmpresa, iIdBancoD, sIdDivisa);
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas C: PrestamosInterempresasBusinessImpl M: obtenerChequeras");
		}
		return listChe;
	}
	
	/**
	 * M�todo para obtener la divisa de la chequera, 
	 * para asi mostrar las chequeras de tipo de divisa igual
	 * para los traspasos.
	 * @param iIdBanco
	 * @param sIdChequera
	 * @return
	 */
	public String verificarDivisas(int iIdBanco, String sIdChequera)
	{
		String sIdDivisa = "";
		try
		{
			sIdDivisa = prestamosInterempresasDao.consultarTipoDivisa(iIdBanco, sIdChequera);
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas C: PrestamosInterempresasBusinessImpl M: verificarDivisas");
		}
		return sIdDivisa;
	}
	
	/**
	 * M�todo para obtener el saldo final de una chequera, utilizado
	 * en la forma SolicitudDeTrapasosDeCredito.js
	 * @param iIdEmpresa
	 * @param iIdBanco
	 * @param sIdChequera
	 * @return
	 */
	public double obtenerSaldoFinal(int iIdEmpresa, int iIdBanco, String sIdChequera)
	{
		double uSaldoFinal = 0;
		double uImporteCheqXEntregar = 0;
		try
		{
			uImporteCheqXEntregar = this.obtenerImporteChequesXEntregar(iIdBanco, sIdChequera);
			uSaldoFinal = prestamosInterempresasDao.consultarSaldoFinal(iIdEmpresa, iIdBanco, sIdChequera) + uImporteCheqXEntregar;
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas C: PrestamosInterempresasBusinessImpl M: obtenerSaldoFinal");
		}
		return uSaldoFinal;
	}
	
	public double obtenerImporteChequesXEntregar(int iIdBanco, String sIdChequera)
	{
		String sIdDivisa = "";
		int iIdEmpresa = 0;
		double uImporte = 0;
		try
		{
			globalSingleton = GlobalSingleton.getInstancia();
			iIdEmpresa = globalSingleton.getUsuarioLoginDto().getNumeroEmpresa();
			sIdDivisa = this.verificarDivisas(iIdBanco, sIdChequera);
			uImporte = prestamosInterempresasDao.consultarImporteChequesXEntregar(iIdEmpresa, iIdBanco, sIdChequera, sIdDivisa);
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas C: PrestamosInterempresasBusinessImpl M: obtenerImporteChequesXEntregar");
		}
		return uImporte;
	}
	
	public Map<String, Object> obtenerSaldoFinalYCoinversion(int iEmpresaOrigen, int iEmpresaDes,
																	int iIdBancoA, String sIdChequeraA)
	{
		String sIdDivisa = "";
		List<Map<String, Object>> listSaldos = new ArrayList<Map<String, Object>>();
		Map<String, Object> mapSaldos = new HashMap<String, Object>();
		try
		{
			sIdDivisa = this.verificarDivisas(iIdBancoA, sIdChequeraA);
			
			listSaldos = prestamosInterempresasDao
						.consultarSaldoFinalYCoinversion(iIdBancoA, sIdChequeraA, iEmpresaDes, iEmpresaOrigen, sIdDivisa);
			
			if(listSaldos.size() > 0)
			{
				mapSaldos.put("saldoFinal", listSaldos.get(0).get("saldoFinal"));
				mapSaldos.put("saldoCoinversion", listSaldos.get(0).get("saldoCoinversion"));
			}
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas C: PrestamosInterempresasBusinessImpl M: obtenerSaldoFinalYCoinversion");
		}
		return mapSaldos;
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> realizarSolTraspCred(SolTraspasoCreditoDto dto)
	{
		Map<String, Object> mapRet = new HashMap<String, Object>();
		try
		{
			dto.setIdTipoOperacion(3808);
			mapRet = this.realizarTraspasos(dto);
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas C: PrestamosInterempresasBusinessImpl M: realizarSolTraspCred");
		}
		return mapRet;
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> realizarPagoTraspCred(SolTraspasoCreditoDto dto)
	{
		Map<String, Object> mapRet = new HashMap<String, Object>();
		try
		{
			dto.setIdTipoOperacion(3809);
			mapRet = this.realizarTraspasos(dto);
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas C: PrestamosInterempresasBusinessImpl M: realizarPagoTraspCred");
		}
		return mapRet;
	}
	
	public Map<String, Object> realizarTraspasos(SolTraspasoCreditoDto dto)
	{
		Map<String, Object> mapRet = new HashMap<String, Object>();
		Map<String, Object> mapGen = new HashMap<String, Object>();
		List<LlenaComboGralDto> listBanChe = null;
		int parentesco[] = new int[9];
		int iEmpOrigen = 0;
		int iEmpDestino = 0;
		int iBancoOrigen = 0;
		int iBancoDestino = 0;
		int iTipoOperacion = 0;
		int iInsertCtaCred = 0;
		int iFolioParametro = 0;
		int iFolioParametroDos = 0;
		int iFolioGrupo = 0;
		int iRegAfecParam;
		int iIdUsuario = 0;
		int iIdCaja = 0;
		
		double uImporte = 0;
	
		String sCheqOrigen = "";
		String sCheqDestino = "";
		String sDivisaOrigen = "";
		String sDivisaDestino = "";
		String sNoDocto = "";
		Date dFecha = null;
		
		try
		{   
			globalSingleton = GlobalSingleton.getInstancia();
			if(dto.getIdTipoOperacion() == 3809)
				parentesco = this.obtenerParentesco(dto.getEmpresaOrigen());
			else
				parentesco = this.obtenerParentesco(dto.getEmpresaDestino());
			
			iTipoOperacion = dto.getIdTipoOperacion();
			for(int i = 0; i < 2; i ++)
			{
				if(i == 0)
				{
					if((parentesco != null) && (parentesco[0] == dto.getEmpresaOrigen()))
					{
						iEmpOrigen = parentesco[0];
						iBancoOrigen = dto.getBancoOrigen();
						sCheqOrigen = dto.getCheqOrigen();
					}
					else
					{
						//Duda buscar: 'Aqui se obtienen los mismos datos pero de la empresa que pidio el prestamo
						listBanChe = new ArrayList<LlenaComboGralDto>();
						listBanChe = prestamosInterempresasDao.consultarBancoChequera(dto.getEmpresaDestino());
						if(listBanChe.size() > 0)
						{
							iEmpOrigen = parentesco[0];
							iBancoOrigen = listBanChe.get(0).getId();
							sCheqOrigen = listBanChe.get(0).getDescripcion();
						}
						else
						{
							mapRet.put("msgUsuario", "Falta la chequera de trapaso para la empresa: " + dto.getEmpresaDestino());
							return mapRet;
						}
					}
				}
				else
				{
					if((parentesco != null) && (parentesco[1] == dto.getEmpresaDestino()))
					{
						iEmpDestino = parentesco[1];
						iBancoDestino = dto.getBancoDestino();
						sCheqDestino = dto.getCheqDestino();
					}
					else
					{
						//La duda de arriba es por que aqui tambn se busca banco y chequera de la empresa destino
						listBanChe = new ArrayList<LlenaComboGralDto>();
						listBanChe = prestamosInterempresasDao.consultarBancoChequera(dto.getEmpresaDestino());
						if(listBanChe.size() > 0)
						{
							iEmpDestino = parentesco[1];
							iBancoDestino = listBanChe.get(0).getId();
							sCheqDestino = listBanChe.get(0).getDescripcion();
						}
						else
						{
							mapRet.put("msgUsuario", "Falta la chequera de trapaso para la empresa: " + dto.getEmpresaDestino());
							return mapRet;
						}
					}
				}
			}
			sDivisaOrigen = this.verificarDivisas(iBancoOrigen, sCheqOrigen);
			sDivisaDestino = this.verificarDivisas(iBancoDestino, sCheqDestino);
			
			if(!sDivisaOrigen.equals(sDivisaDestino))
			{
				mapRet.put("msgUsuario", "El traspaso no se puede realizar porque las Divisas son diferentes");
				return mapRet;
			}
			
			iInsertCtaCred = this.verificarCtaCredInter(iEmpDestino, sDivisaDestino);
			sNoDocto = "" + this.obtenerFolioReal("docto_cred");
			iFolioParametro = this.obtenerFolioReal("no_folio_param");
			iIdUsuario = globalSingleton.getUsuarioLoginDto().getIdUsuario();
			iIdCaja = globalSingleton.getUsuarioLoginDto().getIdCaja();
			
			if(iFolioGrupo <= 0)
				iFolioGrupo = iFolioParametro;
			
			uImporte = dto.getMonto();
			dFecha = globalSingleton.getFechaHoy();
			
			ParametroDto dtoParam = new ParametroDto();
				dtoParam.setNoEmpresa(iEmpOrigen);dtoParam.setNoFolioParam(iFolioParametro);  
				dtoParam.setAplica(1);			  dtoParam.setSecuencia(1);
			    dtoParam.setIdTipoOperacion(iTipoOperacion);
			    dtoParam.setNoCuenta(prestamosInterempresasDao.consultarNumCtaPagadora(iEmpOrigen)); 
			    if(dtoParam.getNoCuenta() <= 0)
				{
					mapRet.put("msgUsuario", "El traspaso no se puede realizar, debe existir una cuenta");
					return mapRet;
				}
			    dtoParam.setIdEstatusMov("L"); 	  dtoParam.setIdChequera(sCheqOrigen); 
			    dtoParam.setIdBanco(iBancoOrigen);dtoParam.setImporte(uImporte);
			    dtoParam.setBSalvoBuenCobro("S"); dtoParam.setFecValor(dFecha); 
			    dtoParam.setFecValorOriginal(dFecha);dtoParam.setIdEstatusReg("P");
			    dtoParam.setUsuarioAlta(iIdUsuario); dtoParam.setFecAlta(dFecha);
			    dtoParam.setIdDivisa(sDivisaOrigen); dtoParam.setIdFormaPago(3);
			    dtoParam.setImporteOriginal(uImporte);dtoParam.setFecOperacion(dFecha);
			    dtoParam.setIdBancoBenef(iBancoDestino);dtoParam.setIdChequeraBenef(sCheqDestino);  
			    dtoParam.setOrigenMov("SET");     dtoParam.setConcepto(dto.getConcepto());
			    dtoParam.setIdCaja(iIdCaja);      dtoParam.setNoFolioMov(0);  
			    dtoParam.setFolioRef(0);          dtoParam.setGrupo(iFolioGrupo); 
			    dtoParam.setNoDocto(sNoDocto);    dtoParam.setNoCliente("" + iEmpDestino);
			    dtoParam.setBeneficiario(prestamosInterempresasDao.consultarNomEmpresa(iEmpDestino));       
			    dtoParam.setValorTasa(0);  
			    dtoParam.setDiasInv(0);			
			    
		    iRegAfecParam = prestamosInterempresasDao.insertarParametroTrasCred(dtoParam);
			
		    if(iRegAfecParam <= 0)
			{
				mapRet.put("msgUsuario", "Error en parametro uno");
				return mapRet;
			}
		    iFolioParametroDos = this.obtenerFolioReal("no_folio_param");
			    
		    ParametroDto dtoParam2 = new ParametroDto();
				dtoParam2.setNoEmpresa(iEmpDestino);dtoParam2.setNoFolioParam(iFolioParametroDos);  
				dtoParam2.setAplica(1);dtoParam2.setSecuencia(2);
			    dtoParam2.setIdTipoOperacion(iTipoOperacion);
			    dtoParam2.setNoCuenta(prestamosInterempresasDao.consultarNumCtaPagadora(iEmpDestino));
			    if(dtoParam2.getNoCuenta() <= 0)
				{
					mapRet.put("msgUsuario", "El traspaso no se puede realizar, debe existir una cuenta");
					return mapRet;
				}
			    dtoParam2.setIdEstatusMov("L");dtoParam2.setIdChequera(sCheqDestino); 
			    dtoParam2.setIdBanco(iBancoDestino);dtoParam2.setImporte(uImporte);
			    dtoParam2.setBSalvoBuenCobro("S"); dtoParam2.setFecValor(dFecha); 
			    dtoParam2.setFecValorOriginal(dFecha);dtoParam2.setIdEstatusReg("P");
			    dtoParam2.setUsuarioAlta(iIdUsuario); dtoParam2.setFecAlta(dFecha);
			    dtoParam2.setIdDivisa(sDivisaDestino); dtoParam2.setIdFormaPago(3);
			    dtoParam2.setImporteOriginal(uImporte);dtoParam2.setFecOperacion(dFecha);
			    dtoParam2.setIdBancoBenef(iBancoOrigen);dtoParam2.setIdChequeraBenef(sCheqOrigen);  
			    dtoParam2.setOrigenMov("SET");dtoParam2.setConcepto(dto.getConcepto());
			    dtoParam2.setIdCaja(iIdCaja);dtoParam2.setNoFolioMov(1);  
			    dtoParam2.setFolioRef(1);dtoParam2.setGrupo(iFolioGrupo); 
			    dtoParam2.setNoDocto(sNoDocto);dtoParam2.setNoCliente("" + iEmpOrigen);
			    dtoParam2.setBeneficiario(prestamosInterempresasDao.consultarNomEmpresa(iEmpOrigen));       
			    dtoParam2.setValorTasa(0);  
			    dtoParam2.setDiasInv(0);			
		    
	    	iRegAfecParam = prestamosInterempresasDao.insertarParametroTrasCred(dtoParam2);
	    	if(iRegAfecParam <= 0)
			{
				mapRet.put("msgUsuario", "Error en parametro dos");
				return mapRet;
			}
			
			GeneradorDto dtoGen = new GeneradorDto();
				dtoGen.setEmpresa(iEmpOrigen);
				dtoGen.setFolParam(iFolioParametro);
				dtoGen.setFolMovi(0);
				dtoGen.setFolDeta(0);
				dtoGen.setResult(1);
				dtoGen.setMensajeResp("inicia generador");
				dtoGen.setNomForma("SolicitudDeTraspasosDeCredito.js");
				dtoGen.setIdUsuario(globalSingleton.getUsuarioLoginDto().getIdUsuario());
			mapGen =  prestamosInterempresasDao.ejecutarGenerador(dtoGen);
			
			if(Integer.parseInt(mapGen.get("result").toString()) != 0)
			{
				mapRet.put("msgUsuario", "Error en generador #" + mapGen.get("result").toString());
				return mapRet;
			}
			
			mapRet.put("msgUsuario", "Datos registrados correctamente con N�mero de Solicitud: " + sNoDocto);
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas C: PrestamosInterempresasBusinessImpl M: realizarTraspasos");
		}
		return mapRet;
	}
	
	public int[] obtenerParentesco(int iIdEmpHijo)
	{
		int i = 0;
		int arrayParentesco[] = null;
		List<ArbolEmpresaDto> listParen = new ArrayList<ArbolEmpresaDto>();
		try
		{
			listParen = prestamosInterempresasDao.consultarParentesco(iIdEmpHijo);
			if(listParen.size() > 0)
			{
				arrayParentesco = new int[9];//Tama�o maximo posible
				if(listParen.get(0).getPadre() > 0)
				{
					arrayParentesco[i] = listParen.get(0).getPadre();
					i ++;
				}
				if(listParen.get(0).getHijo() > 0)
				{
					arrayParentesco[i] = listParen.get(0).getHijo();
					i ++;
				}
				if(listParen.get(0).getNieto() > 0)
				{
					arrayParentesco[i] = listParen.get(0).getNieto();
					i ++;
				}
				if(listParen.get(0).getBisnieto() > 0)
				{
					arrayParentesco[i] = listParen.get(0).getBisnieto();
					i ++;
				}
				if(listParen.get(0).getTataranieto() > 0)
				{
					arrayParentesco[i] = listParen.get(0).getTataranieto();
					i ++;
				}
				if(listParen.get(0).getChosno() > 0)
				{
					arrayParentesco[i] = listParen.get(i).getChosno();
					i ++;
				}
				if(listParen.get(0).getChosno2() > 0)
				{
					arrayParentesco[i] = listParen.get(0).getChosno2();
					i ++;
				}
				if(listParen.get(0).getChosno3() > 0)
				{
					arrayParentesco[i] = listParen.get(0).getChosno3();
					i ++;
				}
				if(listParen.get(0).getChosno4() > 0)
				{
					arrayParentesco[i] = listParen.get(0).getChosno4();
					i ++;
				}
			}
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas C: PrestamosInterempresasBusinessImpl M: obtenerParentesco");
			e.printStackTrace();
		}
		return arrayParentesco;
	}
	
	
	public int verificarCtaCredInter(int iIdEmpresa, String sIdDivisa)
	{
		int iRegAfec = 0;
		int iNoCuenta = 0;
		int iNoPersona = 0;
		int iNoLinea = 0;
		int iCtaCred = 0;
		try
		{
			globalSingleton = GlobalSingleton.getInstancia();
			iNoCuenta = prestamosInterempresasDao.consultarNumCta(iIdEmpresa);
			iNoPersona = prestamosInterempresasDao.consultarNumPersona(iIdEmpresa);
			iNoLinea = prestamosInterempresasDao.consultarDivisaSoin(sIdDivisa);
			System.out.println("Verificar : " + "Emp: " + iIdEmpresa + "cuenta: " + iNoCuenta + "iNoPersona: " + iNoPersona + "noLnea: " +iNoLinea);
			iCtaCred = prestamosInterempresasDao.consultarCtaCredInter(iIdEmpresa, iNoLinea, iNoPersona, iNoCuenta);
			
			if(iCtaCred <= 0)
			{
				CuentaDto dtoInsert = new CuentaDto();
					dtoInsert.setNoEmpresa(iIdEmpresa);
					dtoInsert.setNoCuenta(iNoCuenta);
					dtoInsert.setNoPersona(iNoPersona);
					dtoInsert.setNoLinea(iNoLinea);
					dtoInsert.setFecAlta(globalSingleton.getFechaHoy());
					dtoInsert.setUsuarioAlta(globalSingleton.getUsuarioLoginDto().getIdUsuario());
					
					iRegAfec = prestamosInterempresasDao.insertarCtaCredInter(dtoInsert);
			}
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas C: PrestamosInterempresasBusinessImpl M: verificarCtaCredInter");
		}
		return iRegAfec;
	}
	
	
	public int obtenerFolioReal(String sTipo){
		int iFolio = 0;
		try{
			iFolio = prestamosInterempresasDao.seleccionarFolioReal(sTipo);
			prestamosInterempresasDao.actualizarFolioReal(sTipo);
			return iFolio;
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:PrestamosInterempresas, C:PrestamosInterempresasBusinessImpl, M:obtenerFolioReal");
			return -1;
		}
	}
	
	/**
	 * M�todo para obtener empresas de arbol_empresa,
	 * excepto la empresa padre.
	 * @return
	 */
	public List<LlenaComboGralDto> obtenerEmpresasArbol()
	{
		List<LlenaComboGralDto> listEmp = new ArrayList<LlenaComboGralDto>();
		try
		{
			listEmp = prestamosInterempresasDao.consultarEmpresasArbol();
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas C: PrestamosInterempresasBusinessImpl M: obtenerEmpresasArbol");
		}
		return listEmp;
	}
	
	/**
	 * Inician m�todos para la forma CalculoDeinteres
	 */
	public List<RetInteresPresNoDoc> obtenerConsultaInteresPresNoDoc(ParamInteresPresNoDoc dto)
	{
		int iIdUsuario = 0;
		double uFormula = 0;
		double uInteresPorPagar = 0;
		double uIva = 0;
		List<RetInteresPresNoDoc> listCons = new ArrayList<RetInteresPresNoDoc>();
		try
		{
			globalSingleton = GlobalSingleton.getInstancia();
			iIdUsuario = globalSingleton.getUsuarioLoginDto().getIdUsuario();
			dto.setIdUsuario(iIdUsuario);
			listCons = prestamosInterempresasDao.consultarInteresPresNoDoc(dto);
			
			//se realizanlos calculos
			uFormula = ((dto.getTasa() / 100) / 360) * dto.getPlazo();
			for(int i = 0; i < listCons.size(); i ++)
			{
				uInteresPorPagar = listCons.get(i).getInteresesPorPagar() * uFormula; 
				listCons.get(i).setInteresesPorPagar(funciones.redondearCantidades(uInteresPorPagar, iCanDec));
				uIva = listCons.get(i).getIva() / 100 * uFormula;
				listCons.get(i).setIva(funciones.redondearCantidades(uIva, iCanDec));
				listCons.get(i).setSaldoPromedio(funciones.redondearCantidades(listCons.get(i).getSaldoPromedio(), iCanDec));
			}
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas C: PrestamosInterempresasBusinessImpl M: obtenerConsultaInteresPresNoDoc");
		}
		return listCons;
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList<String> ejecutarCalculoInteres(List<ParametroDto> listParam, ParamComunDto dtoComun)
	{
		int iIdUsuario = 0;
		int iIdCaja = 0;
		int iInsParametro = 0;
		int iFolioParametro = 0;
		int iInsIntPrestamo = 0;
		String sFecIni = "";
		String sFecFin = "";
		ArrayList<String> sMsgUsuario = new ArrayList<String>();
		Map<String, Object> mapGen = new HashMap<String, Object>();
		List<LlenaComboGralDto> listCheqTrasp = new ArrayList<LlenaComboGralDto>();
		List<LlenaComboGralDto> listCheqMadreMisBan = new ArrayList<LlenaComboGralDto>();
		//variables utilizadas cuando configura 267 = NO
		int iIdBanco = 0;
		int iBancoMadre = 0;
		double uSaldoCoinversion = 0;
		String sIdChequera = "";
		String sIdCheqMadre = "";
		
		try
		{
			globalSingleton = GlobalSingleton.getInstancia();
			iIdUsuario = globalSingleton.getUsuarioLoginDto().getIdUsuario();
			iIdCaja = globalSingleton.getUsuarioLoginDto().getIdCaja();
			sFecIni = funciones.ponerFechaSola(dtoComun.getFecIni());
			sFecFin = funciones.ponerFechaSola(dtoComun.getFecFin());
			for(int i = 0; i < listParam.size(); i ++)
			{
				if(listParam.get(i).getIdChequeraBenef() != null 
						&& !listParam.get(i).getIdChequeraBenef().equals(""))
				{
					if(globalSingleton.obtenerValorConfiguraSet(267).equals("SI"))
					{
						iFolioParametro = this.obtenerFolioReal("no_folio_param");
						
						ParametroDto dtoIns = new ParametroDto();
							dtoIns.setNoEmpresa(listParam.get(i).getNoEmpresa());
							dtoIns.setNoFolioParam(iFolioParametro);
							dtoIns.setAplica(1);
							dtoIns.setSecuencia(1);
							dtoIns.setIdTipoOperacion(8100);
							dtoIns.setNoCuenta(listParam.get(i).getNoCuenta());
							dtoIns.setIdEstatusMov("A");
							dtoIns.setIdChequera(listParam.get(i).getIdChequeraBenef());
							dtoIns.setIdBanco(listParam.get(i).getIdBancoBenef());
							dtoIns.setImporte(listParam.get(i).getImporte() + listParam.get(i).getIva());
							dtoIns.setBSalvoBuenCobro("S");
							dtoIns.setFecValor(globalSingleton.getFechaHoy());
							dtoIns.setFecValorOriginal(globalSingleton.getFechaHoy());
							dtoIns.setIdEstatusReg("P");
							dtoIns.setUsuarioAlta(iIdUsuario);
							dtoIns.setFecAlta(globalSingleton.getFechaHoy());
							dtoIns.setIdDivisa(listParam.get(i).getIdDivisa());
							dtoIns.setIdFormaPago(0);
							dtoIns.setImporteOriginal(listParam.get(i).getImporte());
							dtoIns.setFecOperacion(globalSingleton.getFechaHoy());
							dtoIns.setIdBancoBenef(0);
							dtoIns.setIdChequeraBenef("");
							dtoIns.setOrigenMov("SET");
							dtoIns.setConcepto("INT. PREST. NO DOCUM." + sFecIni + "-" + sFecFin);//Falta fec ini + fec fin concatenar
							dtoIns.setIdCaja(iIdCaja);
							dtoIns.setNoFolioMov(0);
							dtoIns.setFolioRef(0);
							dtoIns.setGrupo(iFolioParametro);
							dtoIns.setNoDocto("0");
							dtoIns.setBeneficiario("''");
							dtoIns.setNoCliente(listParam.get(i).getNoCliente());
							dtoIns.setNoRecibo(0);
							dtoIns.setValorTasa(dtoComun.getTasa());
							dtoIns.setDiasInv(listParam.get(i).getDiasInv());
						iInsParametro = prestamosInterempresasDao.insertarParametroCalInt(dtoIns);
						
						if(iInsParametro <= 0)
						{
							sMsgUsuario.add("No se inserto en parametro");
							return sMsgUsuario;
						}
						GeneradorDto dtoGen = new GeneradorDto();
							dtoGen.setEmpresa(listParam.get(i).getNoEmpresa());
							dtoGen.setFolParam(iFolioParametro);
							dtoGen.setFolMovi(0);
							dtoGen.setFolDeta(0);
							dtoGen.setResult(1);
							dtoGen.setMensajeResp("inicia generador");
							dtoGen.setNomForma("CalculoDeInteres.js");
							dtoGen.setIdUsuario(iIdUsuario);
						mapGen =  prestamosInterempresasDao.ejecutarGenerador(dtoGen);
						if(Integer.parseInt(mapGen.get("result").toString()) != 0)
						{
							sMsgUsuario.add("Error en generador #" + mapGen.get("result").toString());
							return sMsgUsuario;
						}
					}//Termina if configura = 267("SI")
					else if(globalSingleton.obtenerValorConfiguraSet(267).equals("NO"))
					{
						listCheqTrasp = prestamosInterempresasDao.consultarCheqTraspaso(listParam.get(i).getNoEmpresa(), dtoComun.getDivisa());
						if(listCheqTrasp.size() > 0)
						{
							iIdBanco = listCheqTrasp.get(i).getId();
							sIdChequera = listCheqTrasp.get(i).getDescripcion();
						}
						else
						{
							iIdBanco = 0;
							sIdChequera = "";
						}
						
						if(iIdBanco <= 0 && sIdChequera.equals(""))
						{
							sMsgUsuario.add("La empresa " + listParam.get(i).getNoEmpresa() + " no tiene una chequera");
						}
						else
						{
							iBancoMadre = listParam.get(i).getIdBancoBenef();
							sIdCheqMadre = listParam.get(i).getIdChequeraBenef();
							
							if(iIdBanco != iBancoMadre)
							{
								listCheqMadreMisBan = prestamosInterempresasDao
														.consultarCheqMadreMismoBan(listParam.get(i).getNoEmpresa(), dtoComun.getDivisa(), iIdBanco);
							
								if(listCheqMadreMisBan.size() > 0)
								{
									iBancoMadre = listCheqMadreMisBan.get(i).getId();
									sIdCheqMadre = listCheqMadreMisBan.get(i).getDescripcion();
								}
							}
							
							iFolioParametro = this.obtenerFolioReal("no_folio_param");
							
							ParametroDto dtoIns = new ParametroDto();
								dtoIns.setNoEmpresa(listParam.get(i).getNoEmpresa());
								dtoIns.setNoFolioParam(iFolioParametro);
								dtoIns.setAplica(1);
								dtoIns.setSecuencia(1);
								dtoIns.setIdTipoOperacion(3801);
								dtoIns.setNoCuenta(listParam.get(i).getNoCuenta());
								dtoIns.setIdEstatusMov("L");
								dtoIns.setIdChequera(sIdChequera);
								dtoIns.setIdBanco(iIdBanco);
								dtoIns.setImporte(listParam.get(i).getImporte() + listParam.get(i).getIva());
								dtoIns.setBSalvoBuenCobro("S");
								dtoIns.setFecValor(globalSingleton.getFechaHoy());
								dtoIns.setFecValorOriginal(globalSingleton.getFechaHoy());
								dtoIns.setIdEstatusReg("P");
								dtoIns.setUsuarioAlta(iIdUsuario);
								dtoIns.setFecAlta(globalSingleton.getFechaHoy());
								dtoIns.setIdDivisa(listParam.get(i).getIdDivisa());
								dtoIns.setIdFormaPago(0);
								dtoIns.setImporteOriginal(listParam.get(i).getImporte() + listParam.get(i).getIva());
								dtoIns.setFecOperacion(globalSingleton.getFechaHoy());
								dtoIns.setIdBancoBenef(iBancoMadre);
								dtoIns.setIdChequeraBenef(sIdCheqMadre);
								dtoIns.setOrigenMov("PRE");
								dtoIns.setConcepto("3.INTERESES PRESTAMO, PAGO" + sFecIni + "-" + sFecFin);
								dtoIns.setIdCaja(iIdCaja);
								dtoIns.setNoFolioMov(0);
								dtoIns.setFolioRef(0);
								dtoIns.setGrupo(iFolioParametro);
								dtoIns.setNoDocto("0");
								dtoIns.setBeneficiario(listParam.get(i).getNomEmpresaBenef());
								dtoIns.setNoCliente(listParam.get(i).getNoCliente());
								dtoIns.setNoRecibo(0);
								dtoIns.setValorTasa(dtoComun.getTasa());
								dtoIns.setDiasInv(0);
							iInsParametro = prestamosInterempresasDao.insertarParametroCalInt(dtoIns);
							
							iInsParametro = -1;
								dtoIns.setNoEmpresa(Integer.parseInt(listParam.get(i).getNoCliente()));
								dtoIns.setNoFolioParam(this.obtenerFolioReal("no_folio_param"));
								dtoIns.setAplica(1);
								dtoIns.setSecuencia(2);
								dtoIns.setIdTipoOperacion(3801);
								dtoIns.setNoCuenta(prestamosInterempresasDao
													.consultarCtaCoinversora(Integer.parseInt(listParam.get(i).getNoCliente())));
								dtoIns.setIdEstatusMov("L");
								dtoIns.setIdChequera(sIdCheqMadre);
								dtoIns.setIdBanco(iBancoMadre);
								dtoIns.setImporte(listParam.get(i).getImporte() + listParam.get(i).getIva());
								dtoIns.setBSalvoBuenCobro("S");
								dtoIns.setFecValor(globalSingleton.getFechaHoy());
								dtoIns.setFecValorOriginal(globalSingleton.getFechaHoy());
								dtoIns.setIdEstatusReg("P");
								dtoIns.setUsuarioAlta(iIdUsuario);
								dtoIns.setFecAlta(globalSingleton.getFechaHoy());
								dtoIns.setIdDivisa(listParam.get(i).getIdDivisa());
								dtoIns.setIdFormaPago(0);
								dtoIns.setImporteOriginal(listParam.get(i).getImporte() + listParam.get(i).getIva());
								dtoIns.setFecOperacion(globalSingleton.getFechaHoy());
								dtoIns.setIdBancoBenef(iIdBanco);
								dtoIns.setIdChequeraBenef(sIdChequera);
								dtoIns.setOrigenMov("PRE");
								dtoIns.setConcepto("3.INTERESES PRESTAMO, PAGO");
								dtoIns.setIdCaja(iIdCaja);
								dtoIns.setNoFolioMov(1);
								dtoIns.setFolioRef(1);
								dtoIns.setGrupo(iFolioParametro);
								dtoIns.setNoDocto("0");
								dtoIns.setBeneficiario(listParam.get(i).getNomEmpresa());
								dtoIns.setNoCliente(""+listParam.get(i).getNoEmpresa());
								dtoIns.setNoRecibo(0);
								dtoIns.setValorTasa(dtoComun.getTasa());
								dtoIns.setDiasInv(0);
							iInsParametro = prestamosInterempresasDao.insertarParametroCalInt(dtoIns);
							
							uSaldoCoinversion = prestamosInterempresasDao.consultarSaldoCoinversion(
														Integer.parseInt(listParam.get(i).getNoCliente()), 
														listParam.get(i).getNoEmpresa(), dtoComun.getDivisa());
							
							if(uSaldoCoinversion > 0)
							{
								
								if(uSaldoCoinversion >= (listParam.get(i).getImporte() + listParam.get(i).getIva()))
								{
									ParametroDto dtoIns2 = new ParametroDto();
										dtoIns2.setNoEmpresa(Integer.parseInt(listParam.get(i).getNoCliente()));
										dtoIns2.setNoFolioParam(this.obtenerFolioReal("no_folio_param"));
										dtoIns2.setAplica(1);
										dtoIns2.setSecuencia(1);
										dtoIns2.setIdTipoOperacion(3806);
										dtoIns2.setNoCuenta(listParam.get(i).getNoEmpresa());
										dtoIns2.setIdEstatusMov("L");
										dtoIns2.setIdChequera(sIdCheqMadre);
										dtoIns2.setIdBanco(iBancoMadre);
										dtoIns2.setImporte(listParam.get(i).getImporte() + listParam.get(i).getIva());
										dtoIns2.setBSalvoBuenCobro("S");
										dtoIns2.setFecValor(globalSingleton.getFechaHoy());
										dtoIns2.setFecValorOriginal(globalSingleton.getFechaHoy());
										dtoIns2.setIdEstatusReg("P");
										dtoIns2.setUsuarioAlta(iIdUsuario);
										dtoIns2.setFecAlta(globalSingleton.getFechaHoy());
										dtoIns2.setIdDivisa(listParam.get(i).getIdDivisa());
										dtoIns2.setIdFormaPago(3);
										dtoIns2.setImporteOriginal(listParam.get(i).getImporte() + listParam.get(i).getIva());
										dtoIns2.setFecOperacion(globalSingleton.getFechaHoy());
										dtoIns2.setIdBancoBenef(iIdBanco);
										dtoIns2.setIdChequeraBenef(sIdChequera);
										dtoIns2.setOrigenMov("PRE");
										dtoIns2.setConcepto("1.INTERESES PRESTAMO, FONDEO" + sFecIni + "-" + sFecFin);
										dtoIns2.setIdCaja(iIdCaja);
										dtoIns2.setNoFolioMov(0);
										dtoIns2.setFolioRef(0);
										dtoIns2.setGrupo(iFolioParametro);
										dtoIns2.setNoDocto("0");
										dtoIns2.setBeneficiario(listParam.get(i).getNomEmpresa());
										dtoIns2.setNoCliente(""+listParam.get(i).getNoEmpresa());
										dtoIns2.setNoRecibo(0);
										dtoIns2.setValorTasa(dtoComun.getTasa());
										dtoIns2.setDiasInv(0);
										
									iInsParametro = -1;
									iInsParametro = prestamosInterempresasDao.insertarParametroCalInt(dtoIns2);
									
									iInsParametro = -1;
										dtoIns2.setNoEmpresa(listParam.get(i).getNoEmpresa());
										dtoIns2.setNoFolioParam(this.obtenerFolioReal("no_folio_param"));
										dtoIns2.setAplica(1);
										dtoIns2.setSecuencia(2);
										dtoIns2.setIdTipoOperacion(3806);
										dtoIns2.setNoCuenta(listParam.get(i).getNoCuenta());
										dtoIns2.setIdEstatusMov("L");
										dtoIns2.setIdChequera(sIdChequera);
										dtoIns2.setIdBanco(iIdBanco);
										dtoIns2.setImporte(listParam.get(i).getImporte() + listParam.get(i).getIva());
										dtoIns2.setBSalvoBuenCobro("S");
										dtoIns2.setFecValor(globalSingleton.getFechaHoy());
										dtoIns2.setFecValorOriginal(globalSingleton.getFechaHoy());
										dtoIns2.setIdEstatusReg("P");
										dtoIns2.setUsuarioAlta(iIdUsuario);
										dtoIns2.setFecAlta(globalSingleton.getFechaHoy());
										dtoIns2.setIdDivisa(listParam.get(i).getIdDivisa());
										dtoIns2.setIdFormaPago(3);
										dtoIns2.setImporteOriginal(listParam.get(i).getImporte() + listParam.get(i).getIva());
										dtoIns2.setFecOperacion(globalSingleton.getFechaHoy());
										dtoIns2.setIdBancoBenef(iBancoMadre);
										dtoIns2.setIdChequeraBenef(sIdCheqMadre);
										dtoIns2.setOrigenMov("PRE");
										dtoIns2.setConcepto("1.INTERESES PRESTAMO, FONDEO");
										dtoIns2.setIdCaja(iIdCaja);
										dtoIns2.setNoFolioMov(1);
										dtoIns2.setFolioRef(1);
										dtoIns2.setGrupo(iFolioParametro);
										dtoIns2.setNoDocto("0");
										dtoIns2.setBeneficiario(listParam.get(i).getNomEmpresaBenef());
										dtoIns2.setNoCliente(listParam.get(i).getNoCliente());
										dtoIns2.setNoRecibo(0);
										dtoIns2.setValorTasa(dtoComun.getTasa());
										dtoIns2.setDiasInv(0);
									iInsParametro = prestamosInterempresasDao.insertarParametroCalInt(dtoIns2);
								}
								else
								{
									ParametroDto dtoIns3 = new ParametroDto();
										dtoIns3.setNoEmpresa(Integer.parseInt(listParam.get(i).getNoCliente()));
										dtoIns3.setNoFolioParam(this.obtenerFolioReal("no_folio_param"));
										dtoIns3.setAplica(1);
										dtoIns3.setSecuencia(1);
										dtoIns3.setIdTipoOperacion(3806);
										dtoIns3.setNoCuenta(listParam.get(i).getNoEmpresa());
										dtoIns3.setIdEstatusMov("L");
										dtoIns3.setIdChequera(sIdCheqMadre);
										dtoIns3.setIdBanco(iBancoMadre);
										dtoIns3.setImporte(listParam.get(i).getImporte() + listParam.get(i).getIva());
										dtoIns3.setBSalvoBuenCobro("S");
										dtoIns3.setFecValor(globalSingleton.getFechaHoy());
										dtoIns3.setFecValorOriginal(globalSingleton.getFechaHoy());
										dtoIns3.setIdEstatusReg("P");
										dtoIns3.setUsuarioAlta(iIdUsuario);
										dtoIns3.setFecAlta(globalSingleton.getFechaHoy());
										dtoIns3.setIdDivisa(listParam.get(i).getIdDivisa());
										dtoIns3.setIdFormaPago(3);
										dtoIns3.setImporteOriginal(listParam.get(i).getImporte() + listParam.get(i).getIva());
										dtoIns3.setFecOperacion(globalSingleton.getFechaHoy());
										dtoIns3.setIdBancoBenef(iIdBanco);
										dtoIns3.setIdChequeraBenef(sIdChequera);
										dtoIns3.setOrigenMov("PRE");
										dtoIns3.setConcepto("1.INTERESES PRESTAMO, FONDEO" + sFecIni + "-" + sFecFin);
										dtoIns3.setIdCaja(iIdCaja);
										dtoIns3.setNoFolioMov(0);
										dtoIns3.setFolioRef(0);
										dtoIns3.setGrupo(iFolioParametro);
										dtoIns3.setNoDocto("0");
										dtoIns3.setBeneficiario(listParam.get(i).getNomEmpresa());
										dtoIns3.setNoCliente(""+listParam.get(i).getNoEmpresa());
										dtoIns3.setNoRecibo(0);
										dtoIns3.setValorTasa(dtoComun.getTasa());
										dtoIns3.setDiasInv(0);
										
									iInsParametro = -1;
									iInsParametro = prestamosInterempresasDao.insertarParametroCalInt(dtoIns3);
									
									iInsParametro = -1;
										dtoIns3.setNoEmpresa(listParam.get(i).getNoEmpresa());
										dtoIns3.setNoFolioParam(this.obtenerFolioReal("no_folio_param"));
										dtoIns3.setAplica(1);
										dtoIns3.setSecuencia(2);
										dtoIns3.setIdTipoOperacion(3806);
										dtoIns3.setNoCuenta(listParam.get(i).getNoCuenta());
										dtoIns3.setIdEstatusMov("L");
										dtoIns3.setIdChequera(sIdChequera);
										dtoIns3.setIdBanco(iIdBanco);
										dtoIns3.setImporte(listParam.get(i).getImporte() + listParam.get(i).getIva());
										dtoIns3.setBSalvoBuenCobro("S");
										dtoIns3.setFecValor(globalSingleton.getFechaHoy());
										dtoIns3.setFecValorOriginal(globalSingleton.getFechaHoy());
										dtoIns3.setIdEstatusReg("P");
										dtoIns3.setUsuarioAlta(iIdUsuario);
										dtoIns3.setFecAlta(globalSingleton.getFechaHoy());
										dtoIns3.setIdDivisa(listParam.get(i).getIdDivisa());
										dtoIns3.setIdFormaPago(3);
										dtoIns3.setImporteOriginal(listParam.get(i).getImporte() + listParam.get(i).getIva());
										dtoIns3.setFecOperacion(globalSingleton.getFechaHoy());
										dtoIns3.setIdBancoBenef(iBancoMadre);
										dtoIns3.setIdChequeraBenef(sIdCheqMadre);
										dtoIns3.setOrigenMov("PRE");
										dtoIns3.setConcepto("1.INTERESES PRESTAMO, FONDEO");
										dtoIns3.setIdCaja(iIdCaja);
										dtoIns3.setNoFolioMov(1);
										dtoIns3.setFolioRef(1);
										dtoIns3.setGrupo(iFolioParametro);
										dtoIns3.setNoDocto("0");
										dtoIns3.setBeneficiario(listParam.get(i).getNomEmpresaBenef());
										dtoIns3.setNoCliente(listParam.get(i).getNoCliente());
										dtoIns3.setNoRecibo(0);
										dtoIns3.setValorTasa(dtoComun.getTasa());
										dtoIns3.setDiasInv(0);
									iInsParametro = prestamosInterempresasDao.insertarParametroCalInt(dtoIns3);
									
									
									ParametroDto dtoIns4 = new ParametroDto();
										dtoIns4.setNoEmpresa(Integer.parseInt(listParam.get(i).getNoCliente()));
										dtoIns4.setNoFolioParam(this.obtenerFolioReal("no_folio_param"));
										dtoIns4.setAplica(1);
										dtoIns4.setSecuencia(1);
										dtoIns4.setIdTipoOperacion(3806);
										dtoIns4.setNoCuenta(listParam.get(i).getNoEmpresa());
										dtoIns4.setIdEstatusMov("L");
										dtoIns4.setIdChequera(sIdCheqMadre);
										dtoIns4.setIdBanco(iBancoMadre);
										dtoIns4.setImporte(uSaldoCoinversion);
										dtoIns4.setBSalvoBuenCobro("S");
										dtoIns4.setFecValor(globalSingleton.getFechaHoy());
										dtoIns4.setFecValorOriginal(globalSingleton.getFechaHoy());
										dtoIns4.setIdEstatusReg("P");
										dtoIns4.setUsuarioAlta(iIdUsuario);
										dtoIns4.setFecAlta(globalSingleton.getFechaHoy());
										dtoIns4.setIdDivisa(listParam.get(i).getIdDivisa());
										dtoIns4.setIdFormaPago(3);
										dtoIns4.setImporteOriginal(uSaldoCoinversion);
										dtoIns4.setFecOperacion(globalSingleton.getFechaHoy());
										dtoIns4.setIdBancoBenef(iIdBanco);
										dtoIns4.setIdChequeraBenef(sIdChequera);
										dtoIns4.setOrigenMov("PRE");
										dtoIns4.setConcepto("1.INTERESES PRESTAMO, FONDEO" + sFecIni + "-" + sFecFin);
										dtoIns4.setIdCaja(iIdCaja);
										dtoIns4.setNoFolioMov(0);
										dtoIns4.setFolioRef(0);
										dtoIns4.setGrupo(iFolioParametro);
										dtoIns4.setNoDocto("0");
										dtoIns4.setBeneficiario(listParam.get(i).getNomEmpresa());
										dtoIns4.setNoCliente(""+listParam.get(i).getNoEmpresa());
										dtoIns4.setNoRecibo(0);
										dtoIns4.setValorTasa(dtoComun.getTasa());
										dtoIns4.setDiasInv(0);
										
									iInsParametro = -1;
									iInsParametro = prestamosInterempresasDao.insertarParametroCalInt(dtoIns4);
									
									iInsParametro = -1;
										dtoIns4.setNoEmpresa(listParam.get(i).getNoEmpresa());
										dtoIns4.setNoFolioParam(this.obtenerFolioReal("no_folio_param"));
										dtoIns4.setAplica(1);
										dtoIns4.setSecuencia(2);
										dtoIns4.setIdTipoOperacion(3806);
										dtoIns4.setNoCuenta(listParam.get(i).getNoCuenta());
										dtoIns4.setIdEstatusMov("L");
										dtoIns4.setIdChequera(sIdChequera);
										dtoIns4.setIdBanco(iIdBanco);
										dtoIns4.setImporte(uSaldoCoinversion);
										dtoIns4.setBSalvoBuenCobro("S");
										dtoIns4.setFecValor(globalSingleton.getFechaHoy());
										dtoIns4.setFecValorOriginal(globalSingleton.getFechaHoy());
										dtoIns4.setIdEstatusReg("P");
										dtoIns4.setUsuarioAlta(iIdUsuario);
										dtoIns4.setFecAlta(globalSingleton.getFechaHoy());
										dtoIns4.setIdDivisa(listParam.get(i).getIdDivisa());
										dtoIns4.setIdFormaPago(3);
										dtoIns4.setImporteOriginal(uSaldoCoinversion);
										dtoIns4.setFecOperacion(globalSingleton.getFechaHoy());
										dtoIns4.setIdBancoBenef(iBancoMadre);
										dtoIns4.setIdChequeraBenef(sIdCheqMadre);
										dtoIns4.setOrigenMov("PRE");
										dtoIns4.setConcepto("1.INTERESES PRESTAMO, FONDEO");
										dtoIns4.setIdCaja(iIdCaja);
										dtoIns4.setNoFolioMov(1);
										dtoIns4.setFolioRef(1);
										dtoIns4.setGrupo(iFolioParametro);
										dtoIns4.setNoDocto("0");
										dtoIns4.setBeneficiario(listParam.get(i).getNomEmpresaBenef());
										dtoIns4.setNoCliente(listParam.get(i).getNoCliente());
										dtoIns4.setNoRecibo(0);
										dtoIns4.setValorTasa(dtoComun.getTasa());
										dtoIns4.setDiasInv(0);
									iInsParametro = prestamosInterempresasDao.insertarParametroCalInt(dtoIns4);
									
									ParametroDto dtoIns5 = new ParametroDto();
										dtoIns5.setNoEmpresa(Integer.parseInt(listParam.get(i).getNoCliente()));
										dtoIns5.setNoFolioParam(this.obtenerFolioReal("no_folio_param"));
										dtoIns5.setAplica(1);
										dtoIns5.setSecuencia(1);
										dtoIns5.setIdTipoOperacion(3808);
										dtoIns5.setNoCuenta(prestamosInterempresasDao.consultarCtaCoinversora(
																			Integer.parseInt(listParam.get(i).getNoCliente())));
										dtoIns5.setIdEstatusMov("L");
										dtoIns5.setIdChequera(sIdCheqMadre);
										dtoIns5.setIdBanco(iBancoMadre);
										dtoIns5.setImporte(listParam.get(i).getImporte() + listParam.get(i).getIva() - uSaldoCoinversion);
										dtoIns5.setBSalvoBuenCobro("S");
										dtoIns5.setFecValor(globalSingleton.getFechaHoy());
										dtoIns5.setFecValorOriginal(globalSingleton.getFechaHoy());
										dtoIns5.setIdEstatusReg("P");
										dtoIns5.setUsuarioAlta(iIdUsuario);
										dtoIns5.setFecAlta(globalSingleton.getFechaHoy());
										dtoIns5.setIdDivisa(listParam.get(i).getIdDivisa());
										dtoIns5.setIdFormaPago(0);
										dtoIns5.setImporteOriginal(listParam.get(i).getImporte() + listParam.get(i).getIva());
										dtoIns5.setFecOperacion(globalSingleton.getFechaHoy());
										dtoIns5.setIdBancoBenef(iIdBanco);
										dtoIns5.setIdChequeraBenef(sIdChequera);
										dtoIns5.setOrigenMov("PRE");
										dtoIns5.setConcepto("2.INTERESES PRESTAMO, CREDITO FONDEO" + sFecIni + "-" + sFecFin);
										dtoIns5.setIdCaja(iIdCaja);
										dtoIns5.setNoFolioMov(0);
										dtoIns5.setFolioRef(0);
										dtoIns5.setGrupo(iFolioParametro);
										dtoIns5.setNoDocto("0");
										dtoIns5.setBeneficiario(listParam.get(i).getNomEmpresa());
										dtoIns5.setNoCliente(""+listParam.get(i).getNoEmpresa());
										dtoIns5.setNoRecibo(0);
										dtoIns5.setValorTasa(dtoComun.getTasa());
										dtoIns5.setDiasInv(0);
										
									iInsParametro = -1;
									iInsParametro = prestamosInterempresasDao.insertarParametroCalInt(dtoIns5);
									
									iInsParametro = -1;
										dtoIns5.setNoEmpresa(listParam.get(i).getNoEmpresa());
										dtoIns5.setNoFolioParam(this.obtenerFolioReal("no_folio_param"));
										dtoIns5.setAplica(1);
										dtoIns5.setSecuencia(2);
										dtoIns5.setIdTipoOperacion(3808);
										dtoIns5.setNoCuenta(listParam.get(i).getNoCuenta());
										dtoIns5.setIdEstatusMov("L");
										dtoIns5.setIdChequera(sIdChequera);
										dtoIns5.setIdBanco(iIdBanco);
										dtoIns5.setImporte(listParam.get(i).getImporte() + listParam.get(i).getIva() - uSaldoCoinversion);
										dtoIns5.setBSalvoBuenCobro("S");
										dtoIns5.setFecValor(globalSingleton.getFechaHoy());
										dtoIns5.setFecValorOriginal(globalSingleton.getFechaHoy());
										dtoIns5.setIdEstatusReg("P");
										dtoIns5.setUsuarioAlta(iIdUsuario);
										dtoIns5.setFecAlta(globalSingleton.getFechaHoy());
										dtoIns5.setIdDivisa(listParam.get(i).getIdDivisa());
										dtoIns5.setIdFormaPago(0);
										dtoIns5.setImporteOriginal(listParam.get(i).getImporte() + listParam.get(i).getIva());
										dtoIns5.setFecOperacion(globalSingleton.getFechaHoy());
										dtoIns5.setIdBancoBenef(iBancoMadre);
										dtoIns5.setIdChequeraBenef(sIdCheqMadre);
										dtoIns5.setOrigenMov("PRE");
										dtoIns5.setConcepto("2.INTERESES PRESTAMO, CREDITO FONDEO");
										dtoIns5.setIdCaja(iIdCaja);
										dtoIns5.setNoFolioMov(1);
										dtoIns5.setFolioRef(1);
										dtoIns5.setGrupo(iFolioParametro);
										dtoIns5.setNoDocto("0");
										dtoIns5.setBeneficiario(listParam.get(i).getNomEmpresaBenef());
										dtoIns5.setNoCliente(listParam.get(i).getNoCliente());
										dtoIns5.setNoRecibo(0);
										dtoIns5.setValorTasa(dtoComun.getTasa());
										dtoIns5.setDiasInv(0);
									iInsParametro = prestamosInterempresasDao.insertarParametroCalInt(dtoIns5);
								}
							}
							else
							{
								ParametroDto dtoIns6 = new ParametroDto();
									dtoIns6.setNoEmpresa(Integer.parseInt(listParam.get(i).getNoCliente()));
									dtoIns6.setNoFolioParam(this.obtenerFolioReal("no_folio_param"));
									dtoIns6.setAplica(1);
									dtoIns6.setSecuencia(1);
									dtoIns6.setIdTipoOperacion(3808);
									dtoIns6.setNoCuenta(prestamosInterempresasDao.consultarCtaCoinversora(
																		Integer.parseInt(listParam.get(i).getNoCliente())));
									dtoIns6.setIdEstatusMov("L");
									dtoIns6.setIdChequera(sIdCheqMadre);
									dtoIns6.setIdBanco(iBancoMadre);
									dtoIns6.setImporte(listParam.get(i).getImporte() + listParam.get(i).getIva());
									dtoIns6.setBSalvoBuenCobro("S");
									dtoIns6.setFecValor(globalSingleton.getFechaHoy());
									dtoIns6.setFecValorOriginal(globalSingleton.getFechaHoy());
									dtoIns6.setIdEstatusReg("P");
									dtoIns6.setUsuarioAlta(iIdUsuario);
									dtoIns6.setFecAlta(globalSingleton.getFechaHoy());
									dtoIns6.setIdDivisa(listParam.get(i).getIdDivisa());
									dtoIns6.setIdFormaPago(0);
									dtoIns6.setImporteOriginal(listParam.get(i).getImporte());
									dtoIns6.setFecOperacion(globalSingleton.getFechaHoy());
									dtoIns6.setIdBancoBenef(iIdBanco);
									dtoIns6.setIdChequeraBenef(sIdChequera);
									dtoIns6.setOrigenMov("PRE");
									dtoIns6.setConcepto("2.INTERESES PRESTAMO, CREDITO FONDEO");
									dtoIns6.setIdCaja(iIdCaja);
									dtoIns6.setNoFolioMov(0);
									dtoIns6.setFolioRef(0);
									dtoIns6.setGrupo(iFolioParametro);
									dtoIns6.setNoDocto("0");
									dtoIns6.setBeneficiario(listParam.get(i).getNomEmpresa());
									dtoIns6.setNoCliente(""+listParam.get(i).getNoEmpresa());
									dtoIns6.setNoRecibo(0);
									dtoIns6.setValorTasa(dtoComun.getTasa());
									dtoIns6.setDiasInv(0);
									
								iInsParametro = -1;
								iInsParametro = prestamosInterempresasDao.insertarParametroCalInt(dtoIns6);
								
								iInsParametro = -1;
									dtoIns6.setNoEmpresa(listParam.get(i).getNoEmpresa());
									dtoIns6.setNoFolioParam(this.obtenerFolioReal("no_folio_param"));
									dtoIns6.setAplica(1);
									dtoIns6.setSecuencia(2);
									dtoIns6.setIdTipoOperacion(3808);
									dtoIns6.setNoCuenta(listParam.get(i).getNoCuenta());
									dtoIns6.setIdEstatusMov("L");
									dtoIns6.setIdChequera(sIdChequera);
									dtoIns6.setIdBanco(iIdBanco);
									dtoIns6.setImporte(listParam.get(i).getImporte() + listParam.get(i).getIva());
									dtoIns6.setBSalvoBuenCobro("S");
									dtoIns6.setFecValor(globalSingleton.getFechaHoy());
									dtoIns6.setFecValorOriginal(globalSingleton.getFechaHoy());
									dtoIns6.setIdEstatusReg("P");
									dtoIns6.setUsuarioAlta(iIdUsuario);
									dtoIns6.setFecAlta(globalSingleton.getFechaHoy());
									dtoIns6.setIdDivisa(listParam.get(i).getIdDivisa());
									dtoIns6.setIdFormaPago(0);
									dtoIns6.setImporteOriginal(listParam.get(i).getImporte() + listParam.get(i).getIva());
									dtoIns6.setFecOperacion(globalSingleton.getFechaHoy());
									dtoIns6.setIdBancoBenef(iBancoMadre);
									dtoIns6.setIdChequeraBenef(sIdCheqMadre);
									dtoIns6.setOrigenMov("PRE");
									dtoIns6.setConcepto("2.INTERESES PRESTAMO, CREDITO FONDEO");
									dtoIns6.setIdCaja(iIdCaja);
									dtoIns6.setNoFolioMov(1);
									dtoIns6.setFolioRef(1);
									dtoIns6.setGrupo(iFolioParametro);
									dtoIns6.setNoDocto("0");
									dtoIns6.setBeneficiario(listParam.get(i).getNomEmpresaBenef());
									dtoIns6.setNoCliente(listParam.get(i).getNoCliente());
									dtoIns6.setNoRecibo(0);
									dtoIns6.setValorTasa(dtoComun.getTasa());
									dtoIns6.setDiasInv(0);
								iInsParametro = prestamosInterempresasDao.insertarParametroCalInt(dtoIns6);
							}
							
							GeneradorDto dtoGen = new GeneradorDto();
								dtoGen.setEmpresa(listParam.get(i).getNoEmpresa());
								dtoGen.setFolParam(iFolioParametro);
								dtoGen.setFolMovi(0);
								dtoGen.setFolDeta(0);
								dtoGen.setResult(1);
								dtoGen.setMensajeResp("inicia generador");
								dtoGen.setNomForma("CalculoDeInteres.js");
								dtoGen.setIdUsuario(iIdUsuario);
							mapGen =  prestamosInterempresasDao.ejecutarGenerador(dtoGen);
							if(Integer.parseInt(mapGen.get("result").toString()) != 0)
							{
								sMsgUsuario.add("Error en generador #" + mapGen.get("result").toString());
								return sMsgUsuario;
							}
						}
					}
					
					InteresPrestamoDto dtoIns = new InteresPrestamoDto();
						dtoIns.setNoEmpresa(listParam.get(i).getNoEmpresa());
						dtoIns.setFecInicioCalculo(dtoComun.getFecIni());
						dtoIns.setFecFinCalculo(dtoComun.getFecFin());
						dtoIns.setTasa(dtoComun.getTasa());
						dtoIns.setIdDivisa(dtoComun.getDivisa());
						dtoIns.setSaldoPromedio(listParam.get(i).getSaldoPromedio());
						dtoIns.setInteres(listParam.get(i).getImporte());
						dtoIns.setIva(listParam.get(i).getIva());
					iInsIntPrestamo = prestamosInterempresasDao.insertarInteresPrestamo(dtoIns);
				}
			}
			sMsgUsuario.add("Los movimientos han sido procesados con exito");
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas C: PrestamosInterempresasBusinessImpl M: ejecutarCalculoInteres");
		}
		return sMsgUsuario;
	}
	
	/**M�todo para obtener los periodos de prestamos,
	 * utilizado en CalculoDeInteres
	 *  
	 * @return
	 */
	public List<LlenaComboGralDto> obtenerPeriodosPrestamos()
	{
		List<LlenaComboGralDto> listPer = new ArrayList<LlenaComboGralDto>();
		try
		{
			listPer = prestamosInterempresasDao.consultarPeriodosPrestamos();
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas C: PrestamosInterempresasBusinessImpl M: obtenerPeriodosPrestamos");
		}
		return listPer;
	}
	
	@SuppressWarnings("unchecked")
	public JRDataSource obtenerReportePresNoDoc(Map parametros)
	{
		JRMapArrayDataSource jrDataSource = null;
		List<Map<String, Object>> listReport = new ArrayList<Map<String, Object>>();
		ParamComunDto dtoParam = new ParamComunDto();
		try
		{
			dtoParam.setFecIni(funciones.ponerFechaDate(funciones.validarCadena(parametros.get("sFecIni").toString())));
			dtoParam.setFecFin(funciones.ponerFechaDate(funciones.validarCadena(parametros.get("sFecFin").toString())));
			dtoParam.setDivisa(funciones.validarCadena(funciones.validarCadena(parametros.get("sDivisa").toString())));
			
			listReport = prestamosInterempresasDao.consultarReportePresNoDoc(dtoParam);
			jrDataSource = new JRMapArrayDataSource(listReport.toArray());
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas C: PrestamosInterempresasBusinessImpl M: obtenerReportePresNoDoc");
		}
		return jrDataSource;
	}
	
	/**
	 * Inician m�todos de ReporteDeInteresesNeto
	 * @return
	 */
	public List<LlenaComboGralDto> obtenerAnios()
	{
		List<LlenaComboGralDto> listAnios = new ArrayList<LlenaComboGralDto>();
		try
		{
			listAnios = prestamosInterempresasDao.consultarAnioInteresPres();
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas C: PrestamosInterempresasBusinessImpl M: obtenerAnios");
		}
		return listAnios;
	}
	
	public List<LlenaComboGralDto> obtenerMes(int iAnio)
	{
		List<LlenaComboGralDto> listMes = new ArrayList<LlenaComboGralDto>();
		try
		{
			listMes = prestamosInterempresasDao.consultarMes(iAnio);
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas C: PrestamosInterempresasBusinessImpl M: obtenerMes");
		}
		return listMes;
	}
	
	@SuppressWarnings("unchecked")
	public JRDataSource obtenerRepInteresNeto(Map params)
	{
		JRMapArrayDataSource jrDataSource = null;
		List<Map<String, Object>> listCons = new ArrayList<Map<String, Object>>();
		int iIdUsuario = 0;
		double uTasaPres = 0;
		double uTasaCoin = 0;
		
		try
		{
			//globalSingleton = GlobalSingleton.getInstancia();
			//iIdUsuario = globalSingleton.getUsuarioLoginDto().getIdUsuario();
			ParamRepIntNetoDto dto = new ParamRepIntNetoDto();
				dto.setEmpresaCon(funciones.convertirCadenaInteger(params.get("noEmpresa").toString()));
				dto.setIdDivisa(funciones.validarCadena(params.get("idDivisa").toString()));
				dto.setAnio(funciones.convertirCadenaInteger(params.get("anio").toString()));
				dto.setMes(funciones.convertirCadenaInteger(params.get("mes").toString()));
			
			dto.setIdUsuario(2);//HardCode Cambiar por la propiedad cuando se recupere el contexto
			dto.setFacTodas(false);//Validar Facultad
			listCons = prestamosInterempresasDao.consultarRepInteresNeto(dto);
			
			for(int i = 0; i < listCons.size(); i ++)
			{
				if(Double.parseDouble(listCons.get(i).get("ipTasa").toString()) > 0)
					uTasaPres = Double.parseDouble(listCons.get(i).get("ipTasa").toString());
				if(Double.parseDouble(listCons.get(i).get("cTasa").toString()) > 0)
					uTasaCoin = Double.parseDouble(listCons.get(i).get("cTasa").toString());
			}
			
			params.put("tasaPres", ""+uTasaPres);
			params.put("tasaCoin", ""+uTasaCoin);
			
			jrDataSource = new JRMapArrayDataSource(listCons.toArray());
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas C: PrestamosInterempresasBusinessImpl M: obtenerRepInteresNeto");
		}
		return jrDataSource;
	}
	
	//Inician M�todos para FlujoNeto
	/**
	 * 
	 * @return
	 */
	public List<LlenaComboGralDto> obtenerSectores()
	{
		List<LlenaComboGralDto> listSec = new ArrayList<LlenaComboGralDto>();
		try
		{
			listSec = prestamosInterempresasDao.consultarSectores();
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas C: PrestamosInterempresasBusinessImpl M: obtenerSectores");
		}
		return listSec;
	}
	
	//Inician m�todos para EstadoDeCuentaDeCredito
	
	public List<LlenaComboGralDto> obtenerEmpresasArbolUsuario()
	{
		int iIdUsuario = 0;
		List<LlenaComboGralDto> listEmp = new ArrayList<LlenaComboGralDto>();
		try
		{
			globalSingleton = GlobalSingleton.getInstancia();
			iIdUsuario = globalSingleton.getUsuarioLoginDto().getIdUsuario();
			listEmp = prestamosInterempresasDao.consultarArbolEmpresaUsuario(iIdUsuario);
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas C: PrestamosInterempresasBusinessImpl M: obtenerEmpresasArbolUsuario");
		}
		return listEmp;
	}
	
	/**
	 * M�todo para obtener el reporte de EstadoDeCuentaDeCredito
	 * @param params, se pasa un mapa como datos de entrada, cabe mencionar
	 * que en el mismo mapa se agregan parametros que seran mostrados en el reporte.
	 * (uSaldoInicial, uSaldoFinal, etc.)
	 * @return retorna un JRDataSource con los datos de la consulta
	 */
	
	@SuppressWarnings("unchecked")
	public JRDataSource obtenerReporteEstadoDeCuentDeCredito(Map params)
	{
		JRMapArrayDataSource jrDataSource = null;
		List<InteresPrestamoDto> listValCredito = new ArrayList<InteresPrestamoDto>();
		List<Map<String, Object>> listRep = new ArrayList<Map<String, Object>>();
		double uSaldoInicial = 0;
		double uSaldoFinal = 0;
		
		int iIdUsuario = 0;
		int iCuenta = 0;
		int iLinea = 0;
		Date dFecIni = null;
		Date dFecFin = null;
		
		ArrayList<String> msgUsuario = new ArrayList<String>();
		List<PersonaDto> listDatEmp = new ArrayList<PersonaDto>();
		try
		{
			globalSingleton = GlobalSingleton.getInstancia();
			//iIdUsuario = globalSingleton.getUsuarioLoginDto().getIdUsuario();
			iIdUsuario = 2;//Cambiar por la linea de arriba
			
			ParamComunDto dtoComun = new ParamComunDto();
				dtoComun.setIdEmpresa(funciones.convertirCadenaInteger(params.get("iIdEmpresa").toString()));
				dtoComun.setDivisa(funciones.validarCadena(params.get("sIdDivisa").toString()));
				dtoComun.setFecIni(funciones.ponerFechaDate(params.get("sFecIni").toString()));
				dtoComun.setFecFin(funciones.ponerFechaDate(params.get("sFecFin").toString()));
				dtoComun.setFecHoy(globalSingleton.getFechaHoy());
			
			if(params.get("sConcentrado").toString().trim().equals("true"))
			{
				uSaldoFinal = prestamosInterempresasDao.consultarSaldoFinal(dtoComun);
				uSaldoInicial = prestamosInterempresasDao.consultarSaldoInicial(dtoComun);
				
				//Se agregan valores al mapa para el reporte
				params.put("uSaldoInicial", uSaldoInicial);
				params.put("uSaldoFinal", uSaldoFinal);
				
				listValCredito = prestamosInterempresasDao.consultarValoresDeCredito(dtoComun.getIdEmpresa(), funciones.obtenerMes(dtoComun.getFecIni()), 
																					 funciones.obtenerAnio(dtoComun.getFecFin()), dtoComun.getDivisa());
				
				if(listValCredito.size() > 0)
				{
					params.put("uSaldoPromedio", listValCredito.get(0).getSaldoPromedio());
					params.put("uInteres", listValCredito.get(0).getInteres());
					params.put("uIva", listValCredito.get(0).getIva());
					params.put("uTasa", listValCredito.get(0).getTasa());
				}
				else
				{
					params.put("uSaldoPromedio", 0.0);
					params.put("uInteres", 0.0);
					params.put("uIva", 0.0);
					params.put("uTasa", 0.0);
				}
				
				listRep = prestamosInterempresasDao.consultarReporteEstadoDeCuenta(dtoComun);
			}
			else
			{
				iCuenta = prestamosInterempresasDao.consultarNumCta(dtoComun.getIdEmpresa());
				iLinea = prestamosInterempresasDao.consultarDivisaSoin(dtoComun.getDivisa());
				
				dFecIni = prestamosInterempresasDao.consultarFechaMinMax("MIN", dtoComun.getIdEmpresa(), iCuenta, 
																			iLinea, dtoComun.getFecIni(), dtoComun.getFecFin());
				
				if(dFecIni == null)
					dFecIni = dtoComun.getFecIni();
				
				dFecFin = dtoComun.getFecFin();
				
				if(dFecFin != globalSingleton.getFechaHoy())
				{
					dFecFin = prestamosInterempresasDao.consultarFechaMinMax("MAX", dtoComun.getIdEmpresa(), iCuenta, 
																				iLinea, dtoComun.getFecIni(), dtoComun.getFecFin());
				}
				
				if(dFecFin == null)
					dFecFin = dtoComun.getFecFin();
				/*
				if(dFecIni.toString().equals("12:00:00 a.m.") || dFecIni.toString().equals("00:00:00"))
					msgUsuario.add("No existe la fecha inicial");
				else if(dFecFin.toString().equals("12:00:00 a.m.") || dFecFin.toString().equals("00:00:00"))
					msgUsuario.add("No existe la fecha final");
				*/	
				
				prestamosInterempresasDao.borrarTmpEdoCuenta(iIdUsuario);
				
				this.prepararReporteEdoCuentaDetallado(dtoComun.getIdEmpresa(), iCuenta, dFecIni, iLinea, 
															dtoComun.getDivisa(), dFecIni, dFecFin);
				
				listDatEmp = prestamosInterempresasDao.consultarDatosEmpresa(dtoComun.getIdEmpresa());
				
				if(listDatEmp.size() > 0)
				{
					params.put("sRfc", listDatEmp.get(0).getRfc());
					params.put("sDireccion", listDatEmp.get(0).getDireccion());
				}
				
				listValCredito = prestamosInterempresasDao.consultarValoresDeCredito(dtoComun.getIdEmpresa(), funciones.obtenerMes(dtoComun.getFecIni()), 
						 funciones.obtenerAnio(dtoComun.getFecFin()), dtoComun.getDivisa());
				
				if(listValCredito.size() > 0)
				{
					params.put("uSaldoPromedio", listValCredito.get(0).getSaldoPromedio());
					params.put("uInteres", listValCredito.get(0).getInteres());
					params.put("uInteresIva", listValCredito.get(0).getInteres() + listValCredito.get(0).getIva());
					params.put("uIva", listValCredito.get(0).getIva());
					params.put("uTasa", listValCredito.get(0).getTasa());
					params.put("uSaldoFinRes", listValCredito.get(0).getInteres() + listValCredito.get(0).getIva());
				}
				else
				{
					params.put("uSaldoPromedio", 0.0);
					params.put("uInteres", 0.0);
					params.put("uIva", 0.0);
					params.put("uTasa", 0.0);
					params.put("uSaldoFinRes", 0.0);
					params.put("uInteresIva", 0.0);
				}
				
				params.put("dFecHoy", globalSingleton.getFechaHoy());
				params.put("sHora", funciones.obtenerHoraActual(true));
				
				listRep = prestamosInterempresasDao.consultarReporteEdoCuentaDetallado(iIdUsuario);
			}
			jrDataSource = new JRMapArrayDataSource(listRep.toArray());
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas C: PrestamosInterempresasBusinessImpl M:obtenerReporteEstadoDeCuentDeCredito");
		}
		return jrDataSource;
	}
	
	public boolean prepararReporteEdoCuentaDetallado(int iNoEmpresa, int iNoCuenta, Date dFecValor, 
									int iNoLinea, String sIdDivisa, Date dFecIni, Date dFecFin)
	{
		boolean bSuccess = false;
		List<HistSaldoDto> listConsSaldo = new ArrayList<HistSaldoDto>();
		List<Map<String, Object>> listConsSaldoDet = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> listSaldoFin = new ArrayList<Map<String, Object>>();
		
		int iSecuencia = 0;
		int iEtiqueta = 0;
	    double uSaldoIni = 0;
	    double uSaldoFin = 0;
	    double uDepositos = 0;
	    double uRetiros = 0; 
	    String sDetalle = "";
	    String sConcepto = "";
	    Date dFecha = null;
	    
		globalSingleton = GlobalSingleton.getInstancia();
		
		//UsuarioLoginDto u =  globalSingleton.getUsuarioLoginDto();
	    
		try{
			Integer iIdUsuario = globalSingleton.getUsuarioLoginDto(false).getIdUsuario(); // globalSingleton.getUsuarioLoginDto().getIdUsuario();

			listConsSaldo = prestamosInterempresasDao.consultarSaldoDepRet(iNoEmpresa, iNoCuenta, iNoLinea, dFecIni);
			
			if(listConsSaldo.size() > 0)
			{
				iSecuencia = 1;
				dFecha = listConsSaldo.get(0).getFecValor();
				sConcepto = listConsSaldo.get(0).getConcepto();
				uSaldoIni = listConsSaldo.get(0).getSdoIni();
				uDepositos = listConsSaldo.get(0).getDepositos();
				uRetiros = listConsSaldo.get(0).getRetiros();
				uSaldoFin = listConsSaldo.get(0).getSdoFin();//Ojo en vb obtiene sdo_ini
				iEtiqueta = listConsSaldo.get(0).getEtiqueta();
			}
			else
			{
				iSecuencia = 1;
				dFecha = dFecValor;
				sConcepto = "SALDO INICIAL";
				uSaldoIni = 0;
				uDepositos = 0;
				uRetiros = 0;
				uSaldoFin = 0;
				iEtiqueta = 1;
			}
			
			TmpEdoCuentaDto dtoIns = new TmpEdoCuentaDto();
				dtoIns.setSecuencia(iSecuencia);
				dtoIns.setFecha(dFecha);
				dtoIns.setConcepto(sConcepto);
				dtoIns.setSaldoInicial(uSaldoIni);
				dtoIns.setDepositos(uDepositos);
				dtoIns.setRetiros(uRetiros);
				dtoIns.setSaldoFinal(uSaldoFin);
				dtoIns.setEtiqueta(iEtiqueta);
				dtoIns.setUsuarioAlta(iIdUsuario);
			
			prestamosInterempresasDao.insertarTmpEdoCuenta(dtoIns);
			
			listConsSaldoDet = prestamosInterempresasDao
									.consultarSaldoDepRetDetalle(dFecFin == globalSingleton.getFechaHoy(), 
											iNoEmpresa, iNoCuenta, sIdDivisa, dFecIni, dFecFin);
			
			if(listConsSaldoDet.size() > 0)
			{
				for(int i = 0; i < listConsSaldoDet.size(); i ++)
				{
					iSecuencia ++;
					dFecha = (Date)listConsSaldoDet.get(0).get("fecValor"); //funciones.ponerFechaDate(listConsSaldoDet.get(0).get("fecValor").toString());
					sConcepto = listConsSaldoDet.get(0).get("concepto").toString();
					uSaldoIni = uSaldoFin;
					uDepositos = funciones.convertirCadenaDouble(listConsSaldoDet.get(0).get("depositos").toString());
					uRetiros = funciones.convertirCadenaDouble(listConsSaldoDet.get(0).get("retiros").toString());
					uSaldoFin = uSaldoIni + uDepositos - uRetiros;
					iEtiqueta = funciones.convertirCadenaInteger(listConsSaldoDet.get(0).get("etiqueta").toString());
					
					TmpEdoCuentaDto dtoIns2 = new TmpEdoCuentaDto();
						dtoIns2.setSecuencia(iSecuencia);
						dtoIns2.setFecha(dFecha);
						dtoIns2.setConcepto(sConcepto);
						dtoIns2.setSaldoInicial(uSaldoIni);
						dtoIns2.setDepositos(uDepositos);
						dtoIns2.setRetiros(uRetiros);
						dtoIns2.setSaldoFinal(uSaldoFin);
						dtoIns2.setEtiqueta(iEtiqueta);
						dtoIns2.setUsuarioAlta(iIdUsuario);
					
					prestamosInterempresasDao.insertarTmpEdoCuenta(dtoIns2);
				}
				iSecuencia ++;
			}
			else
			{
				iSecuencia ++;
				dFecha = dFecValor;
				sConcepto = "";
				//uSaldoIni = uSaldoIni;
				uDepositos = 0;
				uRetiros = 0;
				//uSaldoFin = uSaldoFin;
				iEtiqueta = 2;
			}
			
			listSaldoFin = prestamosInterempresasDao.consultarSaldoFin(dFecFin == globalSingleton.getFechaHoy(), iNoEmpresa, iNoLinea, 
																		iNoCuenta, globalSingleton.getFechaHoy(), dFecFin);
			
			if(listSaldoFin.size() > 0)
			{
				iSecuencia ++;
				dFecha = funciones.ponerFechaDate(listSaldoFin.get(0).get("fecValor").toString());
				sConcepto = listSaldoFin.get(0).get("concepto").toString();
				uSaldoIni = funciones.convertirCadenaDouble(listSaldoFin.get(0).get("saldoFin").toString());
				uDepositos = funciones.convertirCadenaDouble(listSaldoFin.get(0).get("depositos").toString());
				uRetiros = funciones.convertirCadenaDouble(listSaldoFin.get(0).get("retiros").toString());
				uSaldoFin = funciones.convertirCadenaDouble(listSaldoFin.get(0).get("saldoFin").toString());
				iEtiqueta = funciones.convertirCadenaInteger(listSaldoFin.get(0).get("etiqueta").toString());
			}
			else
			{
				iSecuencia ++;
				dFecha = dFecFin;
				sConcepto = "SALDO FINAL ANTES DE INTERESES";
				uSaldoIni = uSaldoFin;
				uDepositos = 0;
				uRetiros = 0;
				//uSaldoFin = uSaldoFin;
				iEtiqueta = 3;
			}
			
			TmpEdoCuentaDto dtoIns2 = new TmpEdoCuentaDto();
				dtoIns2.setSecuencia(iSecuencia);
				dtoIns2.setFecha(dFecha);
				dtoIns2.setConcepto(sConcepto);
				dtoIns2.setSaldoInicial(uSaldoIni);
				dtoIns2.setDepositos(uDepositos);
				dtoIns2.setRetiros(uRetiros);
				dtoIns2.setSaldoFinal(uSaldoFin);
				dtoIns2.setEtiqueta(iEtiqueta);
				dtoIns2.setUsuarioAlta(iIdUsuario);
			
			prestamosInterempresasDao.insertarTmpEdoCuenta(dtoIns2);
		
			bSuccess = true;
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas C: PrestamosInterempresasBusinessImpl M: prepararReporteEdoCuentaDetallado");
			bSuccess = false;
		}
		return bSuccess;
	}
	

	@SuppressWarnings("unchecked")
	public JRDataSource obtenerReporteSolicitudesDeCredito(Map params)
	{
		JRMapArrayDataSource jrDataSource = null;
		List<Map<String, Object>> listSolCre = new ArrayList<Map<String, Object>>();
		int iEmpresaInf = 0;
		int iEmpresaSup = 0;
		String sIdDivisa = "";
		boolean bCredito = false;
		
		try
		{
			globalSingleton = GlobalSingleton.getInstancia();
			if(params.get("optActual").toString().equals("true"))
			{
				iEmpresaInf = funciones.convertirCadenaInteger(params.get("noEmpresa").toString());
				iEmpresaSup = iEmpresaInf;
			}
			else
			{
				iEmpresaInf = 0;
				iEmpresaSup = 30000;
			}
			sIdDivisa = funciones.validarCadena(params.get("idDivisa").toString());
			
			if(params.get("optCredito").toString().trim().equals("true"))
				bCredito = true;
			
			listSolCre = prestamosInterempresasDao.consultarReporteSolicitudesDeCredito(iEmpresaInf, iEmpresaSup, sIdDivisa, bCredito);
			jrDataSource = new JRMapArrayDataSource(listSolCre.toArray());
			
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas C: PrestamosInterempresasBusinessImpl M: obtenerReporteSolicitudesDeCredito");
		}
		return jrDataSource;
	}
	
	//M�todos de FlujoNeto
	/**
	 * 
	 * @param dto
	 * @return
	 */
	public List<Map<String, Object>> obtenerFlujoNeto(ParamComunDto dto)
	{
		List<Map<String, Object>> listMapFlujo = new ArrayList<Map<String, Object>>();
		int iIdUsuario = 0;
		try
		{
			globalSingleton = GlobalSingleton.getInstancia();
			iIdUsuario = globalSingleton.getUsuarioLoginDto().getIdUsuario();
			dto.setIdUsuario(iIdUsuario);
			dto.setFecHoy(globalSingleton.getFechaHoy());
			
			if(dto.isBSector())
				listMapFlujo = prestamosInterempresasDao.consultarFlujoNetoSectores(dto);
			else
				listMapFlujo = prestamosInterempresasDao.consultarFlujoNetoSinSectores(dto);
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas C: PrestamosInterempresasBusinessImpl M: obtenerFlujoNeto");
		}
		return listMapFlujo;
	}
	
	public JRDataSource obtenerReporteFlujoNeto(ParamComunDto dto)
	{
		JRMapArrayDataSource jrDataSource = null;
		List<Map<String, Object>> listMapFlujo = new ArrayList<Map<String, Object>>();
		int iIdUsuario = 0;
		try
		{
			globalSingleton = GlobalSingleton.getInstancia();
			//iIdUsuario = globalSingleton.getUsuarioLoginDto().getIdUsuario();
			iIdUsuario = 2;//Cambiar
			dto.setIdUsuario(iIdUsuario);
			dto.setFecHoy(globalSingleton.getFechaHoy());
			
			if(dto.isBSector())
				listMapFlujo = prestamosInterempresasDao.consultarFlujoNetoSectores(dto);
			else
				listMapFlujo = prestamosInterempresasDao.consultarFlujoNetoSinSectores(dto);
			
			jrDataSource = new JRMapArrayDataSource(listMapFlujo.toArray());
			
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas C: PrestamosInterempresasBusinessImpl M: obtenerReporteFlujoNeto");
		}
		return jrDataSource;
	}
	
	public PrestamosInterempresasDao getPrestamosInterempresasDao(){
		return prestamosInterempresasDao;
	}

	public void setPrestamosInterempresasDao(
			PrestamosInterempresasDao prestamosInterempresasDao) {
		this.prestamosInterempresasDao = prestamosInterempresasDao;
	}
}
