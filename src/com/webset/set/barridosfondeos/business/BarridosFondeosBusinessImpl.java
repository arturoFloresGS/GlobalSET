package com.webset.set.barridosfondeos.business;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRMapArrayDataSource;

import com.webset.set.barridosfondeos.dao.BarridosFondeosDao;
import com.webset.set.barridosfondeos.dto.BusquedaFondeoDto;
import com.webset.set.barridosfondeos.dto.FilialDto;
import com.webset.set.barridosfondeos.dto.FondeoAutomaticoDto;
import com.webset.set.barridosfondeos.dto.ParametroDto;
import com.webset.set.barridosfondeos.dto.TmpTraspasoFondeoDto;
import com.webset.set.barridosfondeos.service.BarridosFondeosService;
import com.webset.set.coinversion.dto.ArbolEmpresaDto;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utilerias.GlobalSingleton;
import com.webset.set.utilerias.dto.GeneradorDto;
import com.webset.set.utilerias.dto.LlenaComboEmpresasDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.set.utilerias.dto.MovimientoDto;
 
public class BarridosFondeosBusinessImpl implements BarridosFondeosService{ 
	private BarridosFondeosDao barridosFondeosDao;
	Funciones funciones= new Funciones();
	Bitacora bitacora = new Bitacora();
	private GlobalSingleton globalSingleton;
	
 
	public List<LlenaComboEmpresasDto>obtenerEmpresas(int idUsuario, boolean bMantenimiento){
		return barridosFondeosDao.obtenerEmpresas(idUsuario, bMantenimiento);
	}

	@Override
	public List<FilialDto> obtenerEmpresasFiliales(int noEmpresa, int idUsuario) {
		return barridosFondeosDao.obtenerEmpresasFiliales(noEmpresa, idUsuario);
	}

	@Override
	public List<LlenaComboGralDto> obtenerTipoArbol(boolean bExistentes, String tipoOperacion) {
		return barridosFondeosDao.obtenerTipoArbol(bExistentes, tipoOperacion);
	}

	/* (non-Javadoc)
	 * @see com.webset.set.barridosfondeos.service.BarridosFondeosService#obtenerEmpresasArbolFondeo(int)
	 */
	@Override
	public List<LlenaComboGralDto> obtenerEmpresasArbolFondeo(int noEmpresaRaiz) {
		return barridosFondeosDao.consultarEmpresaArbolFondeo(noEmpresaRaiz);
	}

	@Override
	public String obtenerIgualDiferente(int tipoArbol) {
		return barridosFondeosDao.obtenerIgualDif(tipoArbol);
	}
	
	public List<LlenaComboGralDto> consultarArboles(boolean bExistentes){
		List<LlenaComboGralDto> listEmpRaiz = new ArrayList<LlenaComboGralDto>();
		
		try{
			listEmpRaiz = barridosFondeosDao.consultarArboles(bExistentes);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: BarridosFondeos C: BarridosFondeosBusinessImpl M: consultarArboles");
		}
		return listEmpRaiz;
	}
	
	
	
	public List<LlenaComboGralDto> consultarArbolesInterempresas(boolean bExistentes){
		List<LlenaComboGralDto> listEmpRaiz = new ArrayList<LlenaComboGralDto>();
		
		try{
			listEmpRaiz = barridosFondeosDao.consultarArbolesInterempresas(bExistentes);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: BarridosFondeos C: BarridosFondeosBusinessImpl M: consultarArbolesInterempresas");
		}
		return listEmpRaiz;
	}
	

	public List<LlenaComboGralDto> consultarArbolesHijos(boolean bExistentes, int idRaiz){
		List<LlenaComboGralDto> listEmpRaiz = new ArrayList<LlenaComboGralDto>();
		
		try{
			listEmpRaiz = barridosFondeosDao.consultarArbolesHijos(bExistentes, idRaiz);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: BarridosFondeos C: BarridosFondeosBusinessImpl M: consultarArbolesHijos");
		}
		return listEmpRaiz;
	}
	
	
	 
	
	
	public List<LlenaComboGralDto> consultarArbolesHijosInterempresas(boolean bExistentes, int idRaiz){
		List<LlenaComboGralDto> listEmpRaiz = new ArrayList<LlenaComboGralDto>();
		
		try{ 
			listEmpRaiz = barridosFondeosDao.consultarArbolesHijosInterempresas(bExistentes, idRaiz);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: BarridosFondeos C: BarridosFondeosBusinessImpl M: consultarArbolesHijosInterempresas");
		}
		return listEmpRaiz;
	}
	
	
	public List<LlenaComboGralDto> obtenerEmpresasRaiz(boolean bExistentes, int idArbol){
		List<LlenaComboGralDto> listEmpRaiz = new ArrayList<LlenaComboGralDto>();
		
		try{
			listEmpRaiz = barridosFondeosDao.consultarEmpresasRaiz(bExistentes, idArbol);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: BarridosFondeos C: BarridosFondeosBusinessImpl M: obtenerEmpresasRaiz");
		}
		return listEmpRaiz;
	}

	
	public List<LlenaComboGralDto> obtenerEmpresasRaizInterempresas(boolean bExistentes){
	

	
	List<LlenaComboGralDto> listEmpRaiz = new ArrayList<LlenaComboGralDto>();
		
		try{
			listEmpRaiz = barridosFondeosDao.consultarEmpresasRaizInterempresas(bExistentes);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: BarridosFondeos C: BarridosFondeosBusinessImpl M: obtenerEmpresasRaizInterempresas");
		}
		return listEmpRaiz;
		
	}

	
	
	
	public List<LlenaComboGralDto> obtenerEmpresasHijo(int iEmpresaRaiz){
		List<LlenaComboGralDto> listEmpHijo = new ArrayList<LlenaComboGralDto>();
		try
		{
			listEmpHijo = barridosFondeosDao.consultarEmpresasHijo(iEmpresaRaiz);
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: BarridosFondeos C: PrestamosInterempresasBusinessImpl M: obtenerEmpresasHijo");
		}
		return listEmpHijo;
	}
	
	
	
	
	
	public List<LlenaComboGralDto> obtenerEmpresasHijoIn(int iEmpresaRaiz){
		List<LlenaComboGralDto> listEmpHijo = new ArrayList<LlenaComboGralDto>();
		try
		{
			listEmpHijo = barridosFondeosDao.consultarEmpresasHijoIn(iEmpresaRaiz);
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: BarridosFondeos C: PrestamosInterempresasBusinessImpl M: obtenerEmpresasHijoIn");
		}
		return listEmpHijo;
	}
	
	
	
	
	
	
	public String obtenerArbolEmpresa(int iEmpresaRaiz){
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
			
			barridosFondeosDao.eliminarTmpArbol(iIdUsuario);
			
			listEmpPadre = barridosFondeosDao.consultarEmpresaPadre(iEmpresaRaiz);
			if(listEmpPadre.size() > 0)
			{
				//Inserta padre
				iSecuencia ++;
				barridosFondeosDao.insertarTmpArbol(iIdUsuario, iSecuencia, listEmpPadre.get(0).getDescripcion(), "padre");
				//inicia estructura padre --
				sbstruc.append("[{id:" + listEmpPadre.get(0).getId() + "," + "text:'" + listEmpPadre.get(0).getDescripcion() + "'," + "singleClickExpand: " + false + ",allowDrag: "+ false);//+",qtipCfg: '"+iEmpresaRaiz+"'");
				//inicia estructura hijo
				listEmpHijo = barridosFondeosDao.consultarEmpresasHijos(iEmpresaRaiz, listEmpPadre.get(0).getId());
				if(listEmpHijo.size() <= 0)
					sbstruc.append(",leaf: " + true);
				for(a = 0; a < listEmpHijo.size(); a ++)
				{	
					//Inserta Hijo
					iSecuencia ++;
					barridosFondeosDao.insertarTmpArbol(iIdUsuario, iSecuencia, listEmpHijo.get(a).getDescripcion(), "hijo");
					
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
					listEmpNieto = barridosFondeosDao.consultarEmpresasNietos(iEmpresaRaiz, listEmpHijo.get(a).getId());
					if(listEmpNieto.size() <= 0)
						sbstruc.append(",leaf: " + true);
					for(b = 0; b < listEmpNieto.size(); b ++)
					{
						//Inserta Nieto
						iSecuencia ++;
						barridosFondeosDao.insertarTmpArbol(iIdUsuario, iSecuencia, listEmpNieto.get(b).getDescripcion(), "nieto");
						
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
						listEmpBisnieto = barridosFondeosDao.consultarEmpresasBisnietos(iEmpresaRaiz, listEmpNieto.get(b).getId());
						if(listEmpBisnieto.size() <= 0)
							sbstruc.append(",leaf: " + true);
						for(c = 0; c < listEmpBisnieto.size(); c ++)
						{
							
							//Inserta Bisnieto
							iSecuencia ++;
							barridosFondeosDao.insertarTmpArbol(iIdUsuario, iSecuencia, listEmpBisnieto.get(c).getDescripcion(), "bisnieto");

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
							listEmpTatara = barridosFondeosDao.consultarEmpresasTataranietos(iEmpresaRaiz, listEmpBisnieto.get(c).getId());
							if(listEmpTatara.size() <= 0)
								sbstruc.append(",leaf: " + true);
							for(d = 0; d < listEmpTatara.size(); d ++)
							{
								//Inserta tataranieto
								iSecuencia ++;
								barridosFondeosDao.insertarTmpArbol(iIdUsuario, iSecuencia, listEmpTatara.get(d).getDescripcion(), "tatara");

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
								listEmpChos1 = barridosFondeosDao.consultarEmpresasChosnoUno(iEmpresaRaiz, listEmpTatara.get(d).getId());
								if(listEmpChos1.size() <= 0)
									sbstruc.append(",leaf: " + true);
								for(f = 0; f < listEmpChos1.size(); f ++)
								{
									//Inserta Chosno 1
									iSecuencia ++;
									barridosFondeosDao.insertarTmpArbol(iIdUsuario, iSecuencia, listEmpChos1.get(f).getDescripcion(), "chosno1");

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
									listEmpChos2 = barridosFondeosDao.consultarEmpresasChosnoDos(iEmpresaRaiz, listEmpChos1.get(f).getId());
									if(listEmpChos2.size() <= 0)
										sbstruc.append(",leaf: " + true);
									for(g = 0; g < listEmpChos2.size(); g ++)
									{
										//Inserta Chosno 2
										iSecuencia ++;
										barridosFondeosDao.insertarTmpArbol(iIdUsuario, iSecuencia, listEmpChos2.get(g).getDescripcion(), "chosno2");

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
										listEmpChos3 = barridosFondeosDao.consultarEmpresasChosnoTres(iEmpresaRaiz, listEmpChos2.get(g).getId());
										if(listEmpChos3.size() <= 0)
											sbstruc.append(",leaf: " + true);
										for(h = 0; h < listEmpChos3.size(); h ++)
										{
											//Inserta Chosno 3
											iSecuencia ++;
											barridosFondeosDao.insertarTmpArbol(iIdUsuario, iSecuencia, listEmpChos3.get(h).getDescripcion(), "chosno3");
											
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
											listEmpChos4 = barridosFondeosDao.consultarEmpresasChosnoCuatro(iEmpresaRaiz, listEmpChos3.get(h).getId());
											if(listEmpChos4.size() <= 0)
												sbstruc.append(",leaf: " + true);
											for(i = 0; i < listEmpChos4.size(); i ++)
											{
												//Inserta Chosno 4
												iSecuencia ++;
												barridosFondeosDao.insertarTmpArbol(iIdUsuario, iSecuencia, listEmpChos4.get(i).getDescripcion(), "chosno4");
												
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
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + e.getMessage()
					+ "P: BarridosFondeos C: PrestamosInterempresasBusinessImpl M: obtenerArbolEmpresa");
		}
		bitacora.insertarRegistro("PAEENODES"+sbMenu.toString());
		return sbMenu.toString();
	}
	
	
	
	
	
	public String obtenerArbolEmpresaInterempresas(int iEmpresaRaiz){
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
			
			barridosFondeosDao.eliminarTmpArbol(iIdUsuario);
			
			listEmpPadre = barridosFondeosDao.consultarEmpresaPadreIn(iEmpresaRaiz);
			if(listEmpPadre.size() > 0)
			{
				//Inserta padre
				iSecuencia ++;
				barridosFondeosDao.insertarTmpArbol(iIdUsuario, iSecuencia, listEmpPadre.get(0).getDescripcion(), "padre");
				//inicia estructura padre --
				sbstruc.append("[{id:" + listEmpPadre.get(0).getId() + "," + "text:'" + listEmpPadre.get(0).getDescripcion() + "'," + "singleClickExpand: " + false + ",allowDrag: "+ false);//+",qtipCfg: '"+iEmpresaRaiz+"'");
				//inicia estructura hijo
				listEmpHijo = barridosFondeosDao.consultarEmpresasHijosIn(iEmpresaRaiz, listEmpPadre.get(0).getId());
				if(listEmpHijo.size() <= 0)
					sbstruc.append(",leaf: " + true);
				for(a = 0; a < listEmpHijo.size(); a ++)
				{	
					//Inserta Hijo
					iSecuencia ++;
					barridosFondeosDao.insertarTmpArbol(iIdUsuario, iSecuencia, listEmpHijo.get(a).getDescripcion(), "hijo");
					
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
					listEmpNieto = barridosFondeosDao.consultarEmpresasNietosIn(iEmpresaRaiz, listEmpHijo.get(a).getId());
					if(listEmpNieto.size() <= 0)
						sbstruc.append(",leaf: " + true);
					for(b = 0; b < listEmpNieto.size(); b ++)
					{
						//Inserta Nieto
						iSecuencia ++;
						barridosFondeosDao.insertarTmpArbol(iIdUsuario, iSecuencia, listEmpNieto.get(b).getDescripcion(), "nieto");
						
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
						listEmpBisnieto = barridosFondeosDao.consultarEmpresasBisnietosIn(iEmpresaRaiz, listEmpNieto.get(b).getId());
						if(listEmpBisnieto.size() <= 0)
							sbstruc.append(",leaf: " + true);
						for(c = 0; c < listEmpBisnieto.size(); c ++)
						{
							
							//Inserta Bisnieto
							iSecuencia ++;
							barridosFondeosDao.insertarTmpArbol(iIdUsuario, iSecuencia, listEmpBisnieto.get(c).getDescripcion(), "bisnieto");

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
							listEmpTatara = barridosFondeosDao.consultarEmpresasTataranietosIn(iEmpresaRaiz, listEmpBisnieto.get(c).getId());
							if(listEmpTatara.size() <= 0)
								sbstruc.append(",leaf: " + true);
							for(d = 0; d < listEmpTatara.size(); d ++)
							{
								//Inserta tataranieto
								iSecuencia ++;
								barridosFondeosDao.insertarTmpArbol(iIdUsuario, iSecuencia, listEmpTatara.get(d).getDescripcion(), "tatara");

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
								listEmpChos1 = barridosFondeosDao.consultarEmpresasChosnoUnoIn(iEmpresaRaiz, listEmpTatara.get(d).getId());
								if(listEmpChos1.size() <= 0)
									sbstruc.append(",leaf: " + true);
								for(f = 0; f < listEmpChos1.size(); f ++)
								{
									//Inserta Chosno 1
									iSecuencia ++;
									barridosFondeosDao.insertarTmpArbol(iIdUsuario, iSecuencia, listEmpChos1.get(f).getDescripcion(), "chosno1");

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
									listEmpChos2 = barridosFondeosDao.consultarEmpresasChosnoDosIn(iEmpresaRaiz, listEmpChos1.get(f).getId());
									if(listEmpChos2.size() <= 0)
										sbstruc.append(",leaf: " + true);
									for(g = 0; g < listEmpChos2.size(); g ++)
									{
										//Inserta Chosno 2
										iSecuencia ++;
										barridosFondeosDao.insertarTmpArbol(iIdUsuario, iSecuencia, listEmpChos2.get(g).getDescripcion(), "chosno2");

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
										listEmpChos3 = barridosFondeosDao.consultarEmpresasChosnoTresIn(iEmpresaRaiz, listEmpChos2.get(g).getId());
										if(listEmpChos3.size() <= 0)
											sbstruc.append(",leaf: " + true);
										for(h = 0; h < listEmpChos3.size(); h ++)
										{
											//Inserta Chosno 3
											iSecuencia ++;
											barridosFondeosDao.insertarTmpArbol(iIdUsuario, iSecuencia, listEmpChos3.get(h).getDescripcion(), "chosno3");
											
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
											listEmpChos4 = barridosFondeosDao.consultarEmpresasChosnoCuatroIn(iEmpresaRaiz, listEmpChos3.get(h).getId());
											if(listEmpChos4.size() <= 0)
												sbstruc.append(",leaf: " + true);
											for(i = 0; i < listEmpChos4.size(); i ++)
											{
												//Inserta Chosno 4
												iSecuencia ++;
												barridosFondeosDao.insertarTmpArbol(iIdUsuario, iSecuencia, listEmpChos4.get(i).getDescripcion(), "chosno4");
												
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
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + e.getMessage()
					+ "P: BarridosFondeos C: PrestamosInterempresasBusinessImpl M: obtenerArbolEmpresaIn");
		}
		bitacora.insertarRegistro("PAEENODES"+sbMenu.toString());
		return sbMenu.toString();
	}
	
	
	
	
	
	
	//angel
	
	public String agregarNodosArbolIn(boolean bNvoHijo,  //angel bnt nuevo arbol ->ejecutar
									String sRuta, 
									int iIdEmpresaRaiz,
									int iIdEmpresa,
									double uMonto, 
									String nombreArbol,
									//String tipoValor,
									int tipoOperacion,
									int iIdEmpresaPadre){
		int iNumValores = 0;
		String sMsgUsuario = "";
		String sCampos = "";
		String sValoresCampos = "";
		String sUltEmpresa = "";
		System.out.println("La ruta es "+ sRuta);
		boolean bExisteEmp = false;
		try
		{
			if(bNvoHijo)
			{
				bExisteEmp = barridosFondeosDao.consultarRutaEmpArbolExisIn(iIdEmpresaRaiz, iIdEmpresaPadre, iIdEmpresa)>0; //metodo 1 
				
				if(bExisteEmp){
					sMsgUsuario = "Ya existe la ruta de la empresa " + iIdEmpresa + " en el arbol de la empresa: " + iIdEmpresaRaiz;
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
						sValoresCampos += "," + iIdEmpresaPadre + "," + iIdEmpresa + ", 'N', " + uMonto;//Se agrega la nueva empresa
					//	System.out.println("********\n"+"\n"+"\n"+"\n"+sValoresCampos+"\n"+"angel vC 111111111111111");
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
				barridosFondeosDao.actualizarArbEmpresaEstatusPadreIn(iIdEmpresaRaiz, Integer.parseInt(sUltEmpresa), "S");//metodo2 btn nuevoarbol ->ejecutar
				
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
				}//angel aqui metodo del boton NuevoArbol
		//Aqui inserta la empresa		 

		        //Call gobjSQL.FunSQLInsert1(ps_campos, pi_EmpresaRaiz & "," & ps_valores)
			//empresa_raiz,padre,no_empresa,b_padre,monto_maximo"
 				             sValoresCampos = iIdEmpresaRaiz + "," + iIdEmpresa + "," + iIdEmpresa + ",'N'," + uMonto;
 				             System.out.println("aqui:"+sValoresCampos+"xoxoxoxoxox");
				barridosFondeosDao.insertarArbolEmpresaIn(sCampos, iIdEmpresaRaiz + "," + sValoresCampos, "", false);//Metodo3 insertar en arbol bnt NuevoArbol-> ejec
															//raiz, origen, destino
				
				sMsgUsuario = "Nodo agregado correctamente al Arbol ";
			}
			else{ //Crea nuevo arbol
				bExisteEmp = barridosFondeosDao.consultarEmpArbolExisIn(iIdEmpresaRaiz, iIdEmpresaRaiz).size() > 0;///metodo4 NvoArbol->ejec
				if(bExisteEmp){
					sMsgUsuario = "Ya existe la ruta de la empresa " + iIdEmpresa + " en el arbol de la empresa: " + iIdEmpresaRaiz;
					return sMsgUsuario;
				}
				
				sCampos = "empresa_raiz,padre,no_empresa,b_padre, monto_maximo";
				sValoresCampos = iIdEmpresaRaiz + "," + iIdEmpresaRaiz + "," + iIdEmpresaRaiz + ",'S'," + uMonto;
				barridosFondeosDao.insertarArbolEmpresaIn(sCampos, sValoresCampos,  nombreArbol , true);//metodo5 btn NvoArbol->Ejecutar hace el insert de un nuevo arbol
				

				sMsgUsuario = "Arbol creado correctamente con clave: [" +iIdEmpresaRaiz + "]";
			}
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: BarridosFondeos C: PrestamosInterempresasBusinessImpl M: agregarNodosArbolIn");
		}
		return sMsgUsuario;
	}
	
	
	
	
	public String agregarNodosArbol(boolean bNvoHijo, 
									String sRuta, 
									int iIdEmpresaRaiz,
									int iIdEmpresa,
									double uMonto, 
									String nombreArbol,
									String tipoValor,
									int tipoOperacion,
									int iIdEmpresaPadre){
		int iNumValores = 0;
		String sMsgUsuario = "";
		String sCampos = "";
		String sValoresCampos = "";
		String sUltEmpresa = "";
		System.out.println("La ruta es "+ sRuta);
		boolean bExisteEmp = false;
		try
		{
			if(bNvoHijo)
			{
				bExisteEmp = barridosFondeosDao.consultarRutaEmpArbolExis(iIdEmpresaRaiz, iIdEmpresaPadre, iIdEmpresa) > 0;
				
				if(bExisteEmp){
					sMsgUsuario = "Ya existe la ruta de la empresa " + iIdEmpresa + " en el arbol de la empresa: " + iIdEmpresaRaiz;
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
				barridosFondeosDao.actualizarArbEmpresaEstatusPadre(iIdEmpresaRaiz, Integer.parseInt(sUltEmpresa), "S");
				
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
		//Aqui inserta la empresa		
			/*	barridosFondeosDao.insertarArbolEmpresa(sCampos, iIdEmpresaRaiz + "," + sValoresCampos, "", false);*/
															//raiz, origen, destino
				barridosFondeosDao.insertarArbolEmpresaFondeo(iIdEmpresaRaiz, iIdEmpresaPadre, iIdEmpresa, 
						tipoOperacion, tipoValor, uMonto, "S", 1, nombreArbol);
				
				sMsgUsuario = "Nodo agregado correctamente al ï¿½rbol ";
			}
			else{ //Crea nuevo arbol
				bExisteEmp = barridosFondeosDao.consultarEmpArbolExis(iIdEmpresaRaiz, iIdEmpresaRaiz).size() > 0;
				if(bExisteEmp){
					sMsgUsuario = "Ya existe la ruta de la empresa " + iIdEmpresa + " en el arbol de la empresa: " + iIdEmpresaRaiz;
					return sMsgUsuario;
				}
				
				sCampos = "empresa_raiz,padre,no_empresa,b_padre, monto_maximo";
				sValoresCampos = iIdEmpresaRaiz + "," + iIdEmpresaRaiz + "," + iIdEmpresaRaiz + ",'N'," + uMonto;
				barridosFondeosDao.insertarArbolEmpresa(sCampos, sValoresCampos,  nombreArbol , true);
				
				barridosFondeosDao.insertarArbolEmpresaFondeo(iIdEmpresaRaiz, iIdEmpresaRaiz, iIdEmpresa, 
						tipoOperacion, tipoValor, uMonto, "S", 0, nombreArbol);

				sMsgUsuario = "ï¿½rbol creado correctamente con clave: [" +iIdEmpresaRaiz + "]";
			}
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: BarridosFondeos C: PrestamosInterempresasBusinessImpl M: agregarNodosArbol");
		}
		return sMsgUsuario;
	}
	
	public List<LlenaComboGralDto> consultarTipoOperacion(){
		List<LlenaComboGralDto> operaciones = new ArrayList<LlenaComboGralDto>();
		try{
			operaciones = barridosFondeosDao.consultarTipoOperacion();
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: BarridosFondeos C: PrestamosInterempresasBusinessImpl M: consultarTipoOperacion");
		}
		return operaciones;
	}
	
	public String eliminarNodosArbol(int iIdEmpresaRaiz, int iIdEmpresaActual, int iIdEmpresaPadre)
	{
		String sMsgUsuario = "";
		int iCuentaNodos = 0;
		double uSaldoEmpresa = 0;
		List<ArbolEmpresaDto> listValidaPadre = new ArrayList<ArbolEmpresaDto>();
		List<String> listCampo = new ArrayList<String>();
		try
		{
			listValidaPadre = barridosFondeosDao.consultarValidaPadre(iIdEmpresaRaiz, iIdEmpresaActual);
			
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
					uSaldoEmpresa = barridosFondeosDao.consultarSaldoEmpresaArbol(iIdEmpresaActual);
					if(uSaldoEmpresa <= 0)
					{
						barridosFondeosDao.eliminarNodoArbolEmpresa(iIdEmpresaRaiz, iIdEmpresaActual);
					 
						listCampo = barridosFondeosDao.consultarCampoArbolEmpresa(iIdEmpresaRaiz, iIdEmpresaPadre);
						
						if(listCampo.size() > 0)
						{
							iCuentaNodos = barridosFondeosDao.consultarNumeroEmpresasArbol(iIdEmpresaRaiz, iIdEmpresaPadre);
							if(iCuentaNodos == 1)
							{
								barridosFondeosDao.actualizarArbEmpresaEstatusPadre(iIdEmpresaRaiz, iIdEmpresaPadre, "N");
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
				sMsgUsuario = "No se encontr la empresa en arbol_empresa";
				return sMsgUsuario;
			}
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: BarridosFondeos C: PrestamosInterempresasBusinessImpl M: eliminarNodosArbol");
		}
		return sMsgUsuario;
	}
	
	
	
	
	
	
	
	
	
	
	public String eliminarNodosArbolIn(int iIdEmpresaRaiz, int iIdEmpresaActual, int iIdEmpresaPadre){
		String sMsgUsuario = "";
		int iCuentaNodos = 0;
		double uSaldoEmpresa = 0;
		List<ArbolEmpresaDto> listValidaPadre = new ArrayList<ArbolEmpresaDto>();
		List<String> listCampo = new ArrayList<String>();
		try
		{
			listValidaPadre = barridosFondeosDao.consultarValidaPadreIn(iIdEmpresaRaiz, iIdEmpresaActual);
			
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
					uSaldoEmpresa = barridosFondeosDao.consultarSaldoEmpresaArbolIn(iIdEmpresaActual);
					if(uSaldoEmpresa <= 0)
					{
						barridosFondeosDao.eliminarNodoArbolEmpresaIn(iIdEmpresaRaiz, iIdEmpresaActual);
					 
						listCampo = barridosFondeosDao.consultarCampoArbolEmpresaIn(iIdEmpresaRaiz, iIdEmpresaPadre);
						
						if(listCampo.size() > 0)
						{
							iCuentaNodos = barridosFondeosDao.consultarNumeroEmpresasArbolIn(iIdEmpresaRaiz, iIdEmpresaPadre);
							if(iCuentaNodos == 1)
							{
								barridosFondeosDao.actualizarArbEmpresaEstatusPadreIn(iIdEmpresaRaiz, iIdEmpresaPadre, "S");
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
				sMsgUsuario = "No se encontr la empresa en arbol_empresa";
				return sMsgUsuario;
			}
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: BarridosFondeos C: PrestamosInterempresasBusinessImpl M: eliminarNodosArbol");
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
			listMap = barridosFondeosDao.consultarTmpArbol(iIdUsuario);
			
			jrDataSource = new JRMapArrayDataSource(listMap.toArray());
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: BarridosFondeos C: PrestamosInterempresasBusinessImpl M: obtenerReporteArbolEmpresa");
		}
		return jrDataSource;
	}
/*********************************************************************************************/


	@Override
	public List<LlenaComboGralDto> obtenerEmpresaConcentradora() {
		int iIdUsuario = 0;
		List<LlenaComboGralDto> listEmpCon = new ArrayList<LlenaComboGralDto>();
		try
		{
			globalSingleton = GlobalSingleton.getInstancia();
			iIdUsuario = globalSingleton.getUsuarioLoginDto().getIdUsuario();
			listEmpCon = barridosFondeosDao.consultarEmpresaConcentradora(iIdUsuario);
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: BarridosFondeos C: PrestamosInterempresasBusinessImpl M: obtenerEmpresaConcentradora");
		}
		return listEmpCon;
	}


	@Override
	public List<LlenaComboGralDto> obtenerEmpresasArbol() {
		List<LlenaComboGralDto> listEmp = new ArrayList <LlenaComboGralDto>();
		try
		{
			listEmp = barridosFondeosDao.consultarEmpresasArbol();
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: BarridosFondeos C: PrestamosInterempresasBusinessImpl M: obtenerEmpresasArbol");
		}
		return listEmp;
	}

	/* (non-Javadoc)
	 * @see com.webset.set.barridosfondeos.service.BarridosFondeosService#consultarBancosRaiz()
	 */
	@Override
	public List<LlenaComboGralDto> consultarBancosRaiz(int idEmpresa, String idDivisa) {
		return this.getBarridosFondeosDao().consultarBancosRaiz(idEmpresa, idDivisa);
	}
	

	/* (non-Javadoc)
	 * @see com.webset.set.barridosfondeos.service.BarridosFondeosService#consultarChequerasRaiz(int, int, java.lang.String)
	 */
	@Override
	public List<LlenaComboGralDto> consultarChequerasRaiz(int idEmpresa, int idBanco, String idDivisa) {
		return this.getBarridosFondeosDao().consultarChequeraRaiz(idEmpresa, idBanco, idDivisa);
	}
	
	public List<LlenaComboGralDto> llenarComboChequeraFondeo(int iIdEmpresa, String sIdDivisa, int iIdBanco){
		List<LlenaComboGralDto> listDatos = new ArrayList<LlenaComboGralDto>();
		try{
			listDatos = barridosFondeosDao.consultarChequeraFondeo(iIdEmpresa, sIdDivisa, iIdBanco);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
					+ "P:Coinversion, C:CoinversionBusinessImpl, M:consultarChequeraFondeo");
		}
		return listDatos;
	}
	/* (non-Javadoc)
	 * @see com.webset.set.barridosfondeos.service.BarridosFondeosService#consultarFondeoAutomatico(com.webset.set.barridosfondeos.dto.BusquedaFondeoDto)
	 */
	@Override
	public List<FondeoAutomaticoDto> consultarFondeoAutomatico(
			BusquedaFondeoDto busquedaFondeoDto) {
		double lfImporte = new Double(0);
		double lfDiferencia = new Double(0);
		String lsChequera = "";
		
		List<FondeoAutomaticoDto> lista = null;
		List<FondeoAutomaticoDto> listaRetorno = new ArrayList<FondeoAutomaticoDto>();
		FondeoAutomaticoDto dato = new FondeoAutomaticoDto();
		
		try{
			lista = this.getBarridosFondeosDao().consultarFondeoArbol(busquedaFondeoDto);
			
			
			if (lista != null){
				for (int i = 0; i < lista.size(); i++){
					dato = new FondeoAutomaticoDto();
					dato = lista.get(i);
					dato.setConcepto("FONDEO");
					if (dato.getSaldoChequera() < 0){
						//System.out.println("entro a Saldochequera <0");
						lfImporte = dato.getPm() + dato.getSaldoMinimoChequera() - dato.getImporteF();
						if (dato.getIngresos() > 0){
							lfDiferencia = lfImporte - dato.getIngresos();
							if (lfDiferencia > 0)
								lfImporte = lfDiferencia;
						}
						
						dato.setImporteTraspaso(lfImporte);
						//System.out.println(lfImporte);
					}else{  //saldo chequera < 0
						
						lfImporte = dato.getPm() - dato.getImporteF();
						if (dato.getIngresos() != 0){
							lfDiferencia = lfImporte - dato.getIngresos();
							if (lfDiferencia > 0)
								lfImporte = lfDiferencia;
						}
						if (lfImporte < 0)
							lfImporte = 0;
						dato.setImporteTraspaso(lfImporte);
					}
					lfImporte = 0;
					if (dato.getIngresos() > 0 && dato.getPm() > 0){
						lfImporte = dato.getIngresos() - dato.getPm() - dato.getImporteB();						
					}else if (dato.getIngresos() > 0 && dato.getPm() == 0){
						lfImporte = dato.getIngresos() - dato.getImporteB();
					}
					lfImporte = (lfImporte > 0)? lfImporte: 0;
					dato.setImporteBarrido(lfImporte);
					
					/*--- se asume que siempre se usara arboles de fondeo ---*/
					try{
						if (busquedaFondeoDto.getIdChequera() != null && !busquedaFondeoDto.getIdChequera().equals(""))
							lsChequera = busquedaFondeoDto.getIdChequera();
						else
							lsChequera = this.getBarridosFondeosDao().consultarChequera(dato.getNoEmpresaOrigen(), busquedaFondeoDto.getIdBanco(), 
																						busquedaFondeoDto.getIdDivisa());
						dato.setIdChequeraPadre(lsChequera);
					}catch (Exception e){
						//dato.setMensaje("La empresa " + dato.getNoEmpresaOrigen() + " no tiene chequera predeterminada de traspaso o del banco seleccionado y divisa seleccionada");
						bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
								+ "P: BarridosFondeos C: BarridosFondeosBusinessImpl M: consultarFondeoAutomatico");
					}
					
					lista.set(i, dato);
					//System.out.println("dato "+dato.getIdChequeraPadre());
					if(dato.getPm() != 0)
						listaRetorno.add(lista.get(i));
				}
			}
		}catch (Exception ef){
			dato.setMensaje("Error en consulta de Fondeo Automatico");
			bitacora.insertarRegistro(new Date().toString() + " " + ef.toString()
					+ "P: BarridosFondeos C: BarridosFondeosBusinessImpl M: consultarFondeoAutomatico");
		}
		System.out.println("listaRetorno "+listaRetorno);
		return listaRetorno;
	}
	@Override
	public String prepararFondeoAutomatico(List<FondeoAutomaticoDto> fondeos, BusquedaFondeoDto generales) {
		FondeoAutomaticoDto fondeo = null;
		//BusquedaFondeoDto general = null;
		TmpTraspasoFondeoDto tmpTraspaso = new TmpTraspasoFondeoDto();
		Map<String, Object> valorPadre = new HashMap<String, Object>();
		int liNoFondeo = 0;
		int lsBanco = 0;
		String lsChequera;
		//String idChequeraPadre = "";
		String salida = "";
		
		try{
			if (generales.getIdChequera().equals("Selecciona una Chequera")){
				lsChequera = this.getBarridosFondeosDao().consultarChequera(generales.getIdEmpresaRaiz(),generales.getIdBanco(), 
						generales.getIdDivisa());
				
			} 
			else {
				lsChequera = generales.getIdChequera();
			}
			
           if (generales.getIdBanco() == 0){
        	   lsBanco = this.getBarridosFondeosDao().consultarBanco(generales.getIdEmpresaRaiz(), generales.getIdBanco(),
        			   generales.getIdDivisa());
        	   
        	   
        	   generales.setIdBanco(lsBanco);
        	   
           }
           else{
        	   lsBanco = generales.getIdBanco();
           }
           
			this.barridosFondeosDao.borrarTmpTraspasoFondeo();
			for (int i = 0; i < fondeos.size(); i++){
				fondeo = new FondeoAutomaticoDto();
				
				fondeo = fondeos.get(i);
				fondeo.setIdChequeraPadre(lsChequera.toString());
				tmpTraspaso.setIdEmpresaOrigen(fondeo.getNoEmpresaOrigen());
				tmpTraspaso.setIdEmpresaDestino(fondeo.getNoEmpresaDestino());
				tmpTraspaso.setTipoArbol(generales.getIdEmpresaRaiz());
				tmpTraspaso.setNivel(fondeo.getOrden());
				tmpTraspaso.setIdBanco(lsBanco);
				tmpTraspaso.setIdChequeraPadre(barridosFondeosDao.getIdChequeraHijo(fondeo.getNoEmpresaOrigen(), fondeo.getIdDivisa(), lsBanco, fondeo.getOrden()));
				tmpTraspaso.setIdChequeraHijo(fondeo.getIdChequera());
				tmpTraspaso.setIdUsuario(generales.getIdUsuario());
				if (generales.getSTipoBusqueda().trim().equals("FONDEO")){
					tmpTraspaso.setTipoOperacion(3806);
					tmpTraspaso.setConcepto(fondeo.getConcepto());
					tmpTraspaso.setImporteTraspaso(fondeo.getImporteTraspaso());
				}
				else{
					tmpTraspaso.setTipoOperacion(3805);
					tmpTraspaso.setConcepto("BARRIDO");
					tmpTraspaso.setImporteTraspaso(fondeo.getImporteBarrido());
				}
				fondeo.setIdChequeraPadre(tmpTraspaso.getIdChequeraPadre());
				do{
					
					valorPadre = this.getBarridosFondeosDao().obtenerDatosPadre(tmpTraspaso.getIdEmpresaDestino(), tmpTraspaso.getIdBanco(), 
							tmpTraspaso.getIdChequeraHijo(), generales.getIdEmpresaRaiz());
				//	idChequeraPadre = valorPadre.get("id_chequera").toString();
				//	tmpTraspaso.setIdChequeraPadre(idChequeraPadre);
					
					/*liNoFondeo = barridosFondeosDao.registrarControlFondeo(fondeo, generales);
					tmpTraspaso.setNoFondeo(liNoFondeo);
					this.getBarridosFondeosDao().insertarTmpTraspaso(tmpTraspaso);
					*/
					
					if (generales.getSTipoBusqueda().trim().equals("FONDEO"))
						liNoFondeo = barridosFondeosDao.registrarControlFondeo(fondeo, generales);
					else
						liNoFondeo = 0;
					
					tmpTraspaso.setNoFondeo(liNoFondeo);
					this.getBarridosFondeosDao().insertarTmpTraspaso(tmpTraspaso);
					tmpTraspaso.setNivel( ((BigDecimal) valorPadre.get("orden")).intValue() );
					tmpTraspaso.setIdEmpresaOrigen( ((BigDecimal) valorPadre.get("padre")).intValue() );
					tmpTraspaso.setIdEmpresaDestino( ((BigDecimal) valorPadre.get("no_empresa")).intValue() );
					tmpTraspaso.setIdChequeraHijo(tmpTraspaso.getIdChequeraPadre());
		    		tmpTraspaso.setIdChequeraPadre(barridosFondeosDao.getIdChequeraHijo(tmpTraspaso.getIdEmpresaOrigen(), fondeo.getIdDivisa(), lsBanco, tmpTraspaso.getNivel()));
		    		//(idChequeraPadre);
				//	tmpTraspaso.setIdBanco( ((BigDecimal) valorPadre.get("id_banco")).intValue());
				}while(tmpTraspaso.getNivel() > 0);
				System.out.println("Que Pasa");
			}
		} catch (Exception e){
			salida = "No se pudo preparar el fondeo";
			e.printStackTrace();
		}
		return salida;
	}

	/* (non-Javadoc)
	 * @see com.webset.set.barridosfondeos.service.BarridosFondeosService#ejecutarFondeoAutomatico(java.util.List, com.webset.set.barridosfondeos.dto.BusquedaFondeoDto)
	 */
	@Override
	public String ejecutarFondeoAutomatico(List<FondeoAutomaticoDto> fondeos, BusquedaFondeoDto generales) {
		//FondeoAutomaticoDto fondeo = null;
		//TmpTraspasoFondeoDto tmpTraspaso = new TmpTraspasoFondeoDto();
		List<Map<String, Object>> listaArbol = null;
		Map<String, Object> valorArbol = new HashMap<String, Object>();
		//Map<String, Object> valorChequera = new HashMap<String, Object>();
		ParametroDto parametro = new ParametroDto();
		Long llFolio = new Long(0);
		Long llNoDoctoMov = new Long(0);
		String lsChequera;
		String lsChequeraBenef;
		//int liBancoEntrada = 0;
		int liNoCuenta = 0;
		//int liNoFondeo = 0;
		int lsBanco = 0;
		//String lsChequeraEntrada = "";
		String lsDescTipoOperacion = "";
		//String chequeraRaiz = "";
		int tipo_sal =0;
		
		
		String salida = "";
		try{
			
			if (generales.getIdChequera() != null && !generales.getIdChequera().equals("")){
				lsChequera = generales.getIdChequera();
			} else{
				lsChequera = this.getBarridosFondeosDao().consultarChequera(generales.getIdEmpresaRaiz(),generales.getIdBanco(), 
																			generales.getIdDivisa());
			}
			
			//chequeraRaiz = lsChequera;
			globalSingleton = GlobalSingleton.getInstancia();
			String empresa = globalSingleton.obtenerValorConfiguraSet(1);
			
			System.out.println(empresa.equalsIgnoreCase("DALTON"));
			
			
			if (lsChequera == null  ){
				salida = "La empresa " + generales.getIdEmpresaRaiz() + " no tiene chequera predeterminada de traspaso o del banco seleccionado y divisa seleccionada";
				return salida;
			}
			if (lsChequera.length() == 0){
				salida = "La empresa " + generales.getIdEmpresaRaiz() + " no tiene chequera predeterminada de traspaso o del banco seleccionado y divisa seleccionada";
				return salida;
			}
		
			if (generales.getIdBanco() == 0){
	        	   lsBanco = this.getBarridosFondeosDao().consultarBanco(generales.getIdEmpresaRaiz(), generales.getIdBanco(),
	        			   generales.getIdDivisa());
	        	   
	        	   
	        	   generales.setIdBanco(lsBanco);
	        	   
	           }
	           else{
	        	   lsBanco = generales.getIdBanco();
	           }
		/*	this.barridosFondeosDao.borrarTmpTraspasoFondeo();
			for (int i = 0; i < fondeos.size(); i++){
				fondeo = new FondeoAutomaticoDto();
				fondeo = fondeos.get(i);
				
				tmpTraspaso.setIdEmpresaOrigen(fondeo.getNoEmpresaOrigen());
				tmpTraspaso.setIdEmpresaDestino(fondeo.getNoEmpresaDestino());
				tmpTraspaso.setImporteTraspaso(fondeo.getImporteTraspaso());
				tmpTraspaso.setTipoArbol(generales.getIdEmpresaRaiz());
				tmpTraspaso.setNivel(fondeo.getOrden());
				tmpTraspaso.setIdBanco(fondeo.getIdBanco());
				tmpTraspaso.setIdChequeraHijo(fondeo.getIdChequera());
				tmpTraspaso.setIdChequeraPadre(lsChequera);
				tmpTraspaso.setIdUsuario(generales.getIdUsuario());
				tmpTraspaso.setTipoOperacion(3806);
				tmpTraspaso.setConcepto(fondeo.getConcepto());
				liNoFondeo = barridosFondeosDao.registrarControlFondeo(fondeo, generales);
				tmpTraspaso.setNoFondeo(liNoFondeo);
				this.getBarridosFondeosDao().insertarTmpTraspaso(tmpTraspaso);
				
			}
			*/
			listaArbol = this.getBarridosFondeosDao().consultarTmpArbolFondeo();
			
			Map<Integer, Double> saldos = new HashMap<Integer, Double>();
			
			Double saldo = 0D;
			Double solicitado = 0D;
			Double importe = 0D;
			Double diferencia = 0D;
			boolean retiro = false;
			
			int iFolMovi = 0;
			int iFolDeta = 0;
			GeneradorDto generadorDto;
			Map<String, Object> resGenerador = new HashMap<String, Object>();
			

			//	salida = "La empresa " + generales.getIdEmpresaRaiz() + " no tiene chequera predeterminada de traspaso o del banco seleccionado y divisa seleccionada";
			if (listaArbol != null){
				for (int i = 0; i < listaArbol.size(); i++){
					valorArbol = listaArbol.get(i); 
					
					if (valorArbol.get("col7").toString().equals("0")){
						//valorChequera = this.barridosFondeosDao.consultarDatosChequera(((BigDecimal) valorArbol.get("col2")).intValue(), generales.getIdDivisa());
						//liBancoEntrada = (Integer)valorChequera.get("id_banco");
						//lsChequeraEntrada = (String) valorChequera.get("id_chequera");
					}
					else{
						//liBancoEntrada = (Short) valorArbol.get("col7");
						//lsChequeraEntrada = (String) valorArbol.get("col8");
					}
					lsDescTipoOperacion = (String)valorArbol.get("col10");
					liNoCuenta = (Integer) valorArbol.get("col2");
					
					llNoDoctoMov = this.barridosFondeosDao.obtenerFolioReal("no_docto");
					
					
//					for (int a = 0; a < fondeos.size(); a++){
						llFolio = this.barridosFondeosDao.obtenerFolioReal("no_folio_param");
//						fondeo = new FondeoAutomaticoDto();					
//						fondeo = fondeos.get(a);

						Integer empresaRegreso = (Integer) valorArbol.get("col2");
						
						if(!saldos.containsKey(empresaRegreso)){
							Double d = this.barridosFondeosDao.getSaldoEmpresa(empresaRegreso, 91);
							saldos.put(((Integer) valorArbol.get("col2")),d);
						}
						
						saldo = saldos.get(empresaRegreso);
						//globalSingleton = GlobalSingleton.getInstancia();
						//String empresa = globalSingleton.obtenerValorConfiguraSet(1);
						
						if(saldo == null)
							saldo = 0D;
						
						solicitado = ((BigDecimal)valorArbol.get("col0")).doubleValue();
						System.out.println(empresa);
						
						if(solicitado <= saldo ||  empresa.equals("DALTON")){
							saldo -= solicitado;
							
							importe = solicitado;
							retiro = true;
							
							diferencia = 0D;
						}else{
							importe = saldo;
							diferencia = solicitado - importe;
							retiro = false;
							
							if(importe > 0)
							{
								retiro = true;
							}
							/*else if(importe <= 0 )
							{
								retiro = true;
							}*/
							saldo = 0D;
							
						}
						
						saldos.put(empresaRegreso, saldo);
						
						if(retiro){
							
							 
							 if (generales.getTipoSaldo() == 0)
								{
									tipo_sal = generales.getTipoSaldo();
								}
								else
								{
									tipo_sal=barridosFondeosDao.buscaAbono(generales.getTipoSaldo());
									
								}

							lsChequera = this.getBarridosFondeosDao().consultarChequeraTraspaso(((Integer) valorArbol.get("col1")),generales.getIdDivisa());
							
							if(listaArbol.size()-1 < i)
								lsChequeraBenef = this.getBarridosFondeosDao().consultarChequeraTraspaso(((Integer) valorArbol.get("col2")),generales.getIdDivisa());
							else
								lsChequeraBenef = (String) valorArbol.get("col8");
								
							parametro.setNoEmpresa(((Integer) valorArbol.get("col1")));
							parametro.setNoFolioParam(llFolio.intValue());
							parametro.setAplica(1);
							parametro.setSecuencia(1);
							parametro.setIdTipoOperacion(3806);
							parametro.setCuenta(liNoCuenta);
							parametro.setIdEstatusMov("L");
							parametro.setIdChequera(lsChequera/*(String)valorArbol.get("col12")*/);
							parametro.setIdBanco(barridosFondeosDao.getIdBanco(lsChequera));
							parametro.setImporte(importe);
							parametro.setbSBC("S");
							parametro.setIdEstatusReg("P");
							parametro.setNoUsuario(generales.getIdUsuario());
							parametro.setIdDivisa(generales.getIdDivisa());
							parametro.setIdFormaPago(3);
							parametro.setIdBancoBenef(barridosFondeosDao.getIdBanco(lsChequeraBenef));
							parametro.setIdChequeraBenef(lsChequeraBenef);
							parametro.setOrigenMov("SET");
							String lsAux = valorArbol.get("col4").toString();
							parametro.setConcepto ( ((lsAux.length() < 2)? "0" + lsAux: lsAux) + ":" + lsDescTipoOperacion);
//							parametro.setIdCaja(0); //TODO: corregir
							parametro.setIdCaja(fondeos.get(0).getIdCaja()); //TODO: corregir
							parametro.setNoFolioMov(0);
							parametro.setFolioRef(0);
							parametro.setIdGrupo(llFolio.intValue());
							parametro.setNoCliente(((Integer) valorArbol.get("col2")));
							parametro.setBeneficiario((String) valorArbol.get("col6"));
							parametro.setNoDocto(llNoDoctoMov.intValue());
						    parametro.setTipo_saldo(tipo_sal);
							
							this.barridosFondeosDao.insertarParametro(parametro);
							
							this.barridosFondeosDao.actualizarControlFondeos(generales.getIdEmpresaRaiz(), parametro.getNoEmpresa(), parametro.getNoCliente(), 
									parametro.getIdChequera(), parametro.getIdChequeraBenef(), ((Integer)valorArbol.get("col11")), llNoDoctoMov.intValue());
							
							Long llFolio2 = this.barridosFondeosDao.obtenerFolioReal("no_folio_param");
							liNoCuenta = this.barridosFondeosDao.consultarCuentaEmpresa(((Integer) valorArbol.get("col2")));	
							
							
							 tipo_sal = 0;
							
							parametro.setNoEmpresa(((Integer) valorArbol.get("col2")));
							parametro.setNoFolioParam(llFolio2.intValue());
							parametro.setAplica(1);
							parametro.setSecuencia(2);
							parametro.setIdTipoOperacion(3806);
							parametro.setCuenta(liNoCuenta);
							parametro.setIdEstatusMov("L");
							parametro.setIdChequera(lsChequeraBenef);
							parametro.setIdBanco(barridosFondeosDao.getIdBanco(lsChequeraBenef));
							parametro.setImporte(importe);
							parametro.setbSBC("S");
							parametro.setIdEstatusReg("P");
							parametro.setNoUsuario(generales.getIdUsuario());
							parametro.setIdDivisa("");
							parametro.setIdDivisa(generales.getIdDivisa());
							parametro.setIdFormaPago(3);
//							lsChequera = parametro.getIdChequera();
							parametro.setIdBancoBenef(barridosFondeosDao.getIdBanco(lsChequera));
							parametro.setIdChequeraBenef(lsChequera);
							parametro.setOrigenMov("SET");
							lsAux = valorArbol.get("col4").toString();
							parametro.setConcepto ( ((lsAux.length() < 2)? "0" + lsAux: lsAux) + ":" + lsDescTipoOperacion);
//							parametro.setIdCaja(0); //TODO: corregir
							parametro.setIdCaja(fondeos.get(0).getIdCaja()); //TODO: corregir
							parametro.setNoFolioMov(1);
							parametro.setFolioRef(1);
							parametro.setIdGrupo(llFolio.intValue());
							parametro.setNoCliente(((Integer) valorArbol.get("col1")));
							parametro.setBeneficiario((String) valorArbol.get("col5"));
							parametro.setNoDocto(llNoDoctoMov.intValue());
							parametro.setTipo_saldo(tipo_sal);
							
							this.barridosFondeosDao.insertarParametro(parametro);
							
							/*--- GENERADOR ---*/
							generadorDto = new GeneradorDto();
							generadorDto.setIdUsuario(generales.getIdUsuario());
							generadorDto.setNomForma("FondeoAutomatico"); 
							generadorDto.setEmpresa(((Integer)valorArbol.get("col1")));
							generadorDto.setFolParam(llFolio.intValue());
							generadorDto.setFolMovi(iFolMovi);
							generadorDto.setFolDeta(iFolDeta);
							generadorDto.setResult(0);
							generadorDto.setMensajeResp("inicia generador");
							resGenerador = new HashMap<String, Object>();
							resGenerador = barridosFondeosDao.ejecutarGenerador(generadorDto);

						}
						//else{
							System.out.println("Diferencia:"+diferencia);
							

						if(diferencia > 0){// Prï¿½stamo
							lsChequera = this.getBarridosFondeosDao().consultarChequeraTraspaso(((Integer) valorArbol.get("col1")),generales.getIdDivisa());
							
							if(listaArbol.size()-1 < i)
								lsChequeraBenef = this.getBarridosFondeosDao().consultarChequeraTraspaso(((Integer) valorArbol.get("col2")),generales.getIdDivisa());
							else
								lsChequeraBenef = (String) valorArbol.get("col8");

							liNoCuenta = this.barridosFondeosDao.consultarCuentaEmpresa(((Integer) valorArbol.get("col2")));	
							llFolio = this.barridosFondeosDao.obtenerFolioReal("no_folio_param");
							
							tipo_sal=0;
							parametro.setNoEmpresa(((Integer) valorArbol.get("col1")));
							parametro.setNoFolioParam(llFolio.intValue());
							parametro.setAplica(1);
							parametro.setSecuencia(1);
							parametro.setIdTipoOperacion(3808);
							parametro.setCuenta(liNoCuenta);
							parametro.setIdEstatusMov("L");
							parametro.setIdChequera(lsChequera/*(String)valorArbol.get("col12")*/);
							parametro.setIdBanco(barridosFondeosDao.getIdBanco(lsChequera));
							parametro.setImporte(diferencia);
							parametro.setbSBC("S");
							parametro.setIdEstatusReg("P");
							parametro.setNoUsuario(generales.getIdUsuario());
							parametro.setIdDivisa(generales.getIdDivisa());
							parametro.setIdFormaPago(3);
							parametro.setIdBancoBenef(barridosFondeosDao.getIdBanco(lsChequeraBenef));
							parametro.setIdChequeraBenef(lsChequeraBenef);
							parametro.setOrigenMov("SET");
							String lsAux = valorArbol.get("col4").toString();
							parametro.setConcepto ( ((lsAux.length() < 2)? "0" + lsAux: lsAux) + ":" + "PRÉSTAMO");
							parametro.setIdCaja(0); //TODO: corregir
							parametro.setNoFolioMov(0);
							parametro.setFolioRef(0);
							parametro.setIdGrupo(llFolio.intValue());
							parametro.setNoCliente(((Integer) valorArbol.get("col2")));
							parametro.setBeneficiario((String) valorArbol.get("col6"));
							parametro.setNoDocto(llNoDoctoMov.intValue());
							parametro.setTipo_saldo(tipo_sal);
							
							this.barridosFondeosDao.insertarParametro(parametro);
							
							this.barridosFondeosDao.actualizarControlFondeos(generales.getIdEmpresaRaiz(), parametro.getNoEmpresa(), parametro.getNoCliente(), 
									parametro.getIdChequera(), parametro.getIdChequeraBenef(), ((Integer)valorArbol.get("col11")), llNoDoctoMov.intValue());
							
							Long llFolio2 = this.barridosFondeosDao.obtenerFolioReal("no_folio_param");
//							liNoCuenta = this.barridosFondeosDao.buscarCuentaEmpresa(((Integer) valorArbol.get("col2")));	
							liNoCuenta = this.barridosFondeosDao.consultarCuentaEmpresa(((Integer) valorArbol.get("col2")));
							int tipo_saldo = 0;
							
//							if (generales.getTipoSaldo() == 0)
//							{
//								tipo_saldo = generales.getTipoSaldo();
//							}
//							else
//							{
//								tipo_saldo=barridosFondeosDao.buscaAbono(generales.getTipoSaldo());
//								
//							}
							
							parametro.setNoEmpresa(((Integer) valorArbol.get("col2")));
							parametro.setNoFolioParam(llFolio2.intValue());
							parametro.setAplica(1);
							parametro.setSecuencia(2);
							parametro.setIdTipoOperacion(3808);
							parametro.setCuenta(liNoCuenta);
							parametro.setIdEstatusMov("L");
							parametro.setIdChequera(lsChequeraBenef);
							parametro.setIdBanco(barridosFondeosDao.getIdBanco(lsChequeraBenef));
							parametro.setImporte(diferencia);
							parametro.setbSBC("S");
							parametro.setIdEstatusReg("P");
							parametro.setNoUsuario(generales.getIdUsuario());
							parametro.setIdDivisa("");
							parametro.setIdDivisa(generales.getIdDivisa());
							parametro.setIdFormaPago(3);
//							lsChequera = parametro.getIdChequera();
							parametro.setIdBancoBenef(barridosFondeosDao.getIdBanco(lsChequera));
							parametro.setIdChequeraBenef(lsChequera);
							parametro.setOrigenMov("SET");
							lsAux = valorArbol.get("col4").toString();
							parametro.setConcepto ( ((lsAux.length() < 2)? "0" + lsAux: lsAux) + ":" + "PRÉSTAMO");
							parametro.setIdCaja(0); //TODO: corregir
							parametro.setNoFolioMov(1);
							parametro.setFolioRef(1);
							parametro.setIdGrupo(llFolio.intValue());
							parametro.setNoCliente(((Integer) valorArbol.get("col1")));
							parametro.setBeneficiario((String) valorArbol.get("col5"));
							parametro.setNoDocto(llNoDoctoMov.intValue());
							parametro.setTipo_saldo(tipo_saldo);
							
							this.barridosFondeosDao.insertarParametro(parametro);
							
							/*--- GENERADOR ---*/
							generadorDto = new GeneradorDto();
							generadorDto.setIdUsuario(generales.getIdUsuario());
							generadorDto.setNomForma("FondeoAutomatico"); 
							generadorDto.setEmpresa(((Integer)valorArbol.get("col1")));
							generadorDto.setFolParam(llFolio.intValue());
							generadorDto.setFolMovi(iFolMovi);
							generadorDto.setFolDeta(iFolDeta);
							generadorDto.setResult(0);
							generadorDto.setMensajeResp("inicia generador");
							resGenerador = new HashMap<String, Object>();
							resGenerador = barridosFondeosDao.ejecutarGenerador(generadorDto);

						}
					}
//					}
//					this.barridosFondeosDao.insertarParametro(parametro);
					
//					/*--- GENERADOR ---*/
//					generadorDto = new GeneradorDto();
//					generadorDto.setIdUsuario(generales.getIdUsuario());
//					generadorDto.setNomForma("FondeoAutomatico"); 
//					generadorDto.setEmpresa(((Integer)valorArbol.get("col1")));
//					generadorDto.setFolParam(llFolio.intValue());
//					generadorDto.setFolMovi(iFolMovi);
//					generadorDto.setFolDeta(iFolDeta);
//					generadorDto.setResult(0);
//					generadorDto.setMensajeResp("inicia generador");
//					resGenerador = new HashMap<String, Object>();
//					resGenerador = barridosFondeosDao.ejecutarGenerador(generadorDto);
					
					if (resGenerador.get("result").toString().equals("0"))
					{
						salida = "Fondeo realizado con exito";
					}
					else{
						salida = "Fondeo realizado con exito";
					}
				//}
			
			}
		
		}catch (Exception e){
			e.printStackTrace();
			salida = "La empresa " + generales.getIdEmpresaRaiz() + " no tiene chequera predeterminada de traspaso o del banco seleccionado y divisa seleccionada";
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: BarridosFondeos C: BarridosFondeosBusinessImpl M: ejecutarFondeoAutomatico");
			return salida;
		}
		
		return salida;
	}
	
	

	/* (non-Javadoc)
	 * @see com.webset.set.barridosfondeos.service.BarridosFondeosService#ejecutarBarridoAutomatico(java.util.List, com.webset.set.barridosfondeos.dto.BusquedaFondeoDto)
	 */
	@Override
	public String ejecutarBarridoAutomatico(List<FondeoAutomaticoDto> barridos, BusquedaFondeoDto generales) {
		FondeoAutomaticoDto barrido = null;
		ParametroDto parametro = new ParametroDto();
		Long llFolio = new Long(0);
		Long llNoDocto = new Long(0);
		String lsChequera;
		String lsChequeraBenef = "";
		int liBancoBenef = 0;
		int liNoCuenta = 0;
		
		
		String salida = "";
		try{
			if (generales.getIdChequera() != null && !generales.getIdChequera().equals(""))
				lsChequeraBenef = generales.getIdChequera();
			else
				lsChequeraBenef = this.getBarridosFondeosDao().consultarChequera(generales.getIdEmpresaRaiz(), generales.getIdBanco(), 
																				generales.getIdDivisa());
			
			if (lsChequeraBenef == null){
				salida = "La empresa " + generales.getIdEmpresaRaiz() + " no tiene chequera predeterminada de traspaso o del banco seleccionado y divisa seleccionada";
				return salida;
			}
			if (lsChequeraBenef.length() == 0){
				salida = "La empresa " + generales.getIdEmpresaRaiz() + " no tiene chequera predeterminada de traspaso o del banco seleccionado y divisa seleccionada";
				return salida;
			}
			
			for (int i = 0; i < barridos.size(); i++){
				barrido = new FondeoAutomaticoDto();
				barrido = barridos.get(i);

				lsChequera = barrido.getIdChequera();
				liBancoBenef = generales.getIdBanco();
				lsChequeraBenef = (generales.getIdChequera() != null && generales.getIdChequera().length() > 0)?generales.getIdChequera(): lsChequeraBenef;
				
				llFolio = this.barridosFondeosDao.obtenerFolioReal("no_folio_param");
				liNoCuenta = this.barridosFondeosDao.buscarCuentaEmpresa(barrido.getNoEmpresaDestino());
				llNoDocto = this.barridosFondeosDao.obtenerFolioReal("no_docto");
				
				parametro.setNoEmpresa(barrido.getNoEmpresaDestino());
				parametro.setNoFolioParam(llFolio.intValue());
				parametro.setAplica(1);
				parametro.setSecuencia(1);
				parametro.setIdTipoOperacion(3805);
				parametro.setCuenta(liNoCuenta);
				parametro.setIdEstatusMov("L");
				parametro.setIdChequera(lsChequera);
				parametro.setIdBanco(barrido.getIdBanco());
				parametro.setImporte(barrido.getImporteBarrido());
				parametro.setbSBC("S");
				parametro.setIdEstatusReg("P");
				parametro.setNoUsuario(generales.getIdUsuario());
				parametro.setIdDivisa(generales.getIdDivisa());
				parametro.setIdFormaPago(3);
				parametro.setIdBancoBenef(liBancoBenef);
				parametro.setIdChequeraBenef(lsChequeraBenef);
				parametro.setOrigenMov("SET"); /*----- AQUI -----*/
				String lsAux = "BARRIDO";
				parametro.setConcepto (lsAux);
				parametro.setIdCaja(0); //TODO: corregir
				parametro.setNoFolioMov(0);
				parametro.setFolioRef(0);
				parametro.setIdGrupo(llFolio.intValue());
				parametro.setNoCliente(generales.getIdEmpresaRaiz());
				parametro.setBeneficiario(barrido.getNomEmpresaOrigen());
				parametro.setNoDocto(llNoDocto.intValue());
				
				this.barridosFondeosDao.insertarParametro(parametro);
				
				
				/*lAfectados = gobjSQL.FunSQLInsertaParametro(val(msfgrid.TextMatrix(i, LI_C_NO_EMPRESA_PADRE)), pl_FolioParametroA, 1, _
                        2, pl_tipoOperacion, val(msfgrid.TextMatrix(i, LI_C_NO_EMPRESA)), "L", Trim$(ls_Chequera), val(LI_BANCO), _
                        ld_importe, "S", Format(GT_FECHA_HOY, "dd/mm/yyyy"), "P", GI_USUARIO, _
                        Trim$(Txtid_divisa.Text), 3, val(li_banco_benef), ls_chequera_benef, "SET", "APORTACION ", _
                        GI_ID_CAJA, 1, 1, pl_folioparametro, val(msfgrid.TextMatrix(i, LI_C_NO_EMPRESA)), _
                        Trim(msfgrid.TextMatrix(i, LI_C_DESC_EMPRESA)), psNoDocto)*/
                        
                Long llFolio2 = this.barridosFondeosDao.obtenerFolioReal("no_folio_param");
                        
				parametro.setNoEmpresa(barrido.getNoEmpresaOrigen());
				parametro.setNoFolioParam(llFolio2.intValue());
				parametro.setAplica(1);
				parametro.setSecuencia(2);
				parametro.setIdTipoOperacion(3805);
				parametro.setCuenta(liNoCuenta);
				parametro.setIdEstatusMov("L");
				parametro.setIdChequera(lsChequeraBenef);
				parametro.setIdBanco(liBancoBenef);
				parametro.setImporte(barrido.getImporteBarrido());
				parametro.setbSBC("S");
				parametro.setIdEstatusReg("P");
				parametro.setNoUsuario(generales.getIdUsuario());
				parametro.setIdDivisa(generales.getIdDivisa());
				parametro.setIdFormaPago(3);
				parametro.setIdBancoBenef(generales.getIdBanco());
				parametro.setIdChequeraBenef(lsChequera);
				parametro.setOrigenMov("SET"); /*----- AQUI -----*/
				lsAux = "BARRIDO";
				parametro.setConcepto (lsAux);
				parametro.setIdCaja(0); //TODO: corregir
				parametro.setNoFolioMov(1);
				parametro.setFolioRef(1);
				parametro.setIdGrupo(llFolio.intValue());
				parametro.setNoCliente(barrido.getNoEmpresaDestino());
				parametro.setBeneficiario(barrido.getNomEmpresaDestino());
				parametro.setNoDocto(llNoDocto.intValue());
				
				this.barridosFondeosDao.insertarParametro(parametro);
				
				/*--- GENERADOR ---*/
				/*
				gobjVarGlobal.generador("", "", GI_USUARIO, Me.Name, rstDatos!COL1, pl_folio, pd_FolioMov, _pd_FolioDet, LI_C_NO_EMPRESA) fondeo
				gobjVarGlobal.generador("", "", GI_USUARIO, Me.Name, CLng(val(msfgrid.TextMatrix(i, LI_C_NO_EMPRESA))), pl_folioparametro, a1, a2, 1)
				*/
				int iFolMovi = 0;
				int iFolDeta = 0;
				
				GeneradorDto generadorDto;
				generadorDto = new GeneradorDto();
				generadorDto.setIdUsuario(generales.getIdUsuario());
				generadorDto.setNomForma("FondeoAutomatico"); 
				generadorDto.setEmpresa(barrido.getNoEmpresaDestino());
				generadorDto.setFolParam(llFolio.intValue());
				generadorDto.setFolMovi(iFolMovi);
				generadorDto.setFolDeta(iFolDeta);
				generadorDto.setResult(1);
				generadorDto.setMensajeResp("inicia generador");
				Map<String, Object> resGenerador = new HashMap<String, Object>();
				resGenerador = barridosFondeosDao.ejecutarGenerador(generadorDto);
				
				if (((Integer) resGenerador.get("result")).intValue() == 0){
					salida = "Barrido realizado exitosamente";
				}
			}
			
		}catch (Exception e){
			e.printStackTrace();
			salida = "La empresa " + generales.getIdEmpresaRaiz() + " no tiene chequera predeterminada de traspaso o del banco seleccionado y divisa seleccionada";
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: BarridosFondeos C: BarridosFondeosBusinessImpl M: ejecutarFondeoAutomatico");
			return salida;
		}
		
		return salida;
	}

	/* (non-Javadoc)
	 * @see com.webset.set.barridosfondeos.service.BarridosFondeosService#obtenerPagos(com.webset.set.barridosfondeos.dto.BusquedaFondeoDto)
	 */
	@Override
	public List<MovimientoDto> obtenerPagos(BusquedaFondeoDto dtoBus) {
		List<MovimientoDto> listCons = new ArrayList<MovimientoDto>();
		try{
			listCons = this.barridosFondeosDao.consultarPagos(dtoBus);
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
					+ "P: BarridosFondeos, C: BarridosFondeosBusinessImpl, M:obtenerPagos");
		}
		return listCons;
	}

	public BarridosFondeosDao getBarridosFondeosDao() {
		return barridosFondeosDao;
	}
	public void setBarridosFondeosDao(
			BarridosFondeosDao barridosFondeosDao) {
		this.barridosFondeosDao = barridosFondeosDao;
	}

	@Override
	public List<LlenaComboGralDto> consultarArbolesInterempresas1(boolean bExistentes) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String obtenerArbolEmpresaIn(int iEmpresaRaiz) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String actualizarMonto(int monto, int idEmpresa, int idRaiz) {
		// TODO Auto-generated method stub
		
		try
		{
		barridosFondeosDao.consultarMontoAct( monto,  idEmpresa,  idRaiz);
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: BarridosFondeos C: PrestamosInterempresasBusinessImpl M: obtenerEmpresasHijoIn");
		}
		
		
		return null;
	}










}
