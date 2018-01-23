package com.webset.set.utilerias.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.softwarementors.extjs.djn.config.annotations.DirectMethod;
import com.softwarementors.extjs.djn.servlet.ssm.WebContextManager;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.Contexto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.set.utilerias.service.MantenimientoCatalogosService;
import com.webset.set.utileriasmod.dto.MantenimientoCatalogosDefColCatDto;
import com.webset.set.utileriasmod.dto.MantenimientoCatalogosDto;
import com.webset.utils.tools.Utilerias;

public class MantenimientoCatalogosAction {
	Bitacora bitacora = new Bitacora();
	Contexto contexto = new Contexto();
	MantenimientoCatalogosService objMantenimientoCatalogosService;
	MantenimientoCatalogosDto filas;
	
	@DirectMethod
	public List<MantenimientoCatalogosDto> llenaComboCatalogos(){
		List<MantenimientoCatalogosDto> listaResultado = new ArrayList<MantenimientoCatalogosDto>();
		try{	
			if(Utilerias.haveSession(WebContextManager.get())){
				objMantenimientoCatalogosService = (MantenimientoCatalogosService)contexto.obtenerBean("objMantenimientoCatalogosBusinessImpl");
				listaResultado = objMantenimientoCatalogosService.llenaComboCatalogos();
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: MantenimientoCatalogosAction, M: llenaComboCatalogos");
		}return listaResultado;
	}
	
	@DirectMethod
	public List<LlenaComboGralDto>llenarComboBancos(int noEmpresa){
		List<LlenaComboGralDto> list= new ArrayList<LlenaComboGralDto>();
		if(!Utilerias.haveSession(WebContextManager.get()) || !Utilerias.tienePermiso(WebContextManager.get(), 197))
			return list;
		try{
			if (Utilerias.haveSession(WebContextManager.get())) {
				objMantenimientoCatalogosService = (MantenimientoCatalogosService)contexto.obtenerBean("objMantenimientoCatalogosBusinessImpl");
			list=objMantenimientoCatalogosService.llenarComboBancos(noEmpresa);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: MantenimientoCatalogosAction, M: llenaComboBancos");
		}return list;
	}
	
	@DirectMethod
	public List<LlenaComboGralDto>llenarComboChequeras(int noBanco, int noEmpresa){
		List<LlenaComboGralDto> list= new ArrayList<LlenaComboGralDto>();
		if(!Utilerias.haveSession(WebContextManager.get()) || !(Utilerias.tienePermiso(WebContextManager.get(), 197)||Utilerias.tienePermiso(WebContextManager.get(), 77)))
			return list;

		try{
			if (Utilerias.haveSession(WebContextManager.get())) {
				objMantenimientoCatalogosService = (MantenimientoCatalogosService)contexto.obtenerBean("objMantenimientoCatalogosBusinessImpl");
			list=objMantenimientoCatalogosService.llenarComboChequeras(noBanco, noEmpresa);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) +"P: Utilerias, C: MantenimientoCatalogosAction, M:llenarComboChequeras");	
		}
		return list;
	}
	
	@DirectMethod
	public List<Map<String, Object>> llenaGridCatalogos(String nombreCatalogo,String noEmpresa,String noBanco,String idChequera){
		List<Map<String, Object>> listaResultado = new ArrayList<Map<String, Object>>();
		try{
			if(Utilerias.haveSession(WebContextManager.get())){
				objMantenimientoCatalogosService = (MantenimientoCatalogosService)contexto.obtenerBean("objMantenimientoCatalogosBusinessImpl");
				listaResultado = objMantenimientoCatalogosService.llenaGridCatalogos(nombreCatalogo,noEmpresa,noBanco,idChequera);
				System.out.println(listaResultado.size());
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: MantenimientoCatalogosAction, M: llenaGridCatalogos");
		}
		return listaResultado;
		
	}
	@DirectMethod
	public int deleteRecord(String catalogo,String key,String valor){
		System.out.println(catalogo+" - "+key+" - "+valor);
		int res=0;
		try{
			if(Utilerias.haveSession(WebContextManager.get())){
				objMantenimientoCatalogosService = (MantenimientoCatalogosService)contexto.obtenerBean("objMantenimientoCatalogosBusinessImpl");
				res = objMantenimientoCatalogosService.deleteRecord(catalogo, key,valor);
			}
			System.out.println("res="+res);
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: MantenimientoCatalogosAction, M: deleteRecord");
		}return res;
	}
	@DirectMethod
	public String addRecord(String catalogo,String key,String tipo){
		String id=null;
		try{
			if(Utilerias.haveSession(WebContextManager.get())){
				objMantenimientoCatalogosService = (MantenimientoCatalogosService)contexto.obtenerBean("objMantenimientoCatalogosBusinessImpl");
				id = objMantenimientoCatalogosService.addRecord(catalogo, key, tipo);
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: MantenimientoCatalogosAction, M: addRecord");
		}return id;
	}
	@DirectMethod
	public String saveRecords(String catalogo,String records,String fields){
	    System.out.println(records+" Action records");
		System.out.println(fields+" Action fields");
		Gson gson = new Gson();
		String message="";
		StringBuffer r=new StringBuffer();
		List<Map<String, String>> matRegistros = 
				gson.fromJson(records, 
						new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		List<Map<String, String>> defCols = 
				gson.fromJson(fields, 
						new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		int res=0;
		try{
			if(Utilerias.haveSession(WebContextManager.get())){
				for(int j=0;j<matRegistros.size();j++){
					for(int i=0;i<matRegistros.get(j).size();i++){
						if(defCols.get(i).get("type").equals("string") ||
								defCols.get(i).get("type").equals("date")){
							if(matRegistros.get(j).get(defCols.get(i).get("name")).trim().length()<=0 || matRegistros.get(j).get(defCols.get(i).get("name")).equals("undefined")){
								message="Error en "+defCols.get(i).get("name");
								res=-1;
								break;
							}
						}else{
							if(defCols.get(i).get("type").equals("int")){
								try{
									Integer.parseInt(matRegistros.get(j).get(defCols.get(i).get("name")));
								}catch(Exception e){
									message="Error en "+defCols.get(i).get("name");
									res=-1;
									break;
								}
							}
						}
					}	
				}
				r.append("{");
				if(res==0){
					objMantenimientoCatalogosService = (MantenimientoCatalogosService)contexto.obtenerBean("objMantenimientoCatalogosBusinessImpl");
					res = objMantenimientoCatalogosService.saveRecord(catalogo,matRegistros,defCols);
					if(res>0){
						r.append("success: true,");
						r.append("message: '"+res+" registro(s) guardado(s) con exito'");
						
					}else{
						r.append("success: false,");
						r.append("message: 'Error al guardar los cambios'");
					}
				}else{
					r.append("success: false,");
					r.append("message:'"+message+"'");
				}
				r.append("}");
				System.out.println(r.toString());
			}
		}catch(Exception e){
			r=new StringBuffer();
			r.append("{");
			r.append("success: false,");
			r.append("message:'Error al guardar los cambios'");
			r.append("}");
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: MantenimientoCatalogosAction, M: saveRecords");
		}
		return r.toString();
	}
	@DirectMethod
	public List<MantenimientoCatalogosDefColCatDto> obtenHeads(String nombreCatalogo,String titulos,String col){
		System.out.println(titulos);
		Matcher mat = null;
		String titulo="";
		Pattern pat= Pattern.compile("(\\S?\\(+(\\S+)\\)+)");
		List<MantenimientoCatalogosDefColCatDto> ls=new ArrayList<MantenimientoCatalogosDefColCatDto>();
		MantenimientoCatalogosDefColCatDto defCol=new MantenimientoCatalogosDefColCatDto();
		
		try{
			if(Utilerias.haveSession(WebContextManager.get())){
				titulos=titulos.replace("^","");
				String []t=titulos.split("\\|");
				
				String cols[]=col.split("\\|");
				StringBuffer columns= new StringBuffer();
				StringBuffer fields= new StringBuffer();
				String catalogo=null;
				columns.append("[");
				fields.append("[");
				for(int i=1,j=0;i<cols.length;i+=2,j++){
					catalogo=cols[i].toUpperCase();
					columns.append("{");
					fields.append("{");
					switch(cols[i-1].toString().charAt(0)){
						case 'f':
							//columns.append("renderer: Ext.util.Format.dateRenderer('d/m/Y'),");
							columns.append("editor: new Ext.form.DateField({format:'d/m/Y'}),");
							//columns.append("xtype: 'datecolumn',");
							//columns.append("format: 'd/m/Y',");
							
							fields.append("dateFormat: 'd/m/Y',");
							fields.append("isKey: false,");
							fields.append("type: 'date',");
							break;
						case 's':
							fields.append("type:'string',");
							if(catalogo.indexOf("ID_")<0 && catalogo.indexOf("NO_")<0 || (i-1)>0 || editarTodos(nombreCatalogo)){
								columns.append("editor: new Ext.form.TextField({}),");
							}
							break;
						case 'n':
							fields.append("type:'int',");
							if(catalogo.indexOf("ID_")<0 && catalogo.indexOf("NO_")<0 || (i-1)>0 || editarTodos(nombreCatalogo)){
								columns.append("editor: new Ext.form.TextField({}),");
								if((i-1)==0)
									fields.append("isKey: true,");
								else
									fields.append("isKey: false,");
							}else
								fields.append("isKey: true,");
							break;
						default:
							columns.append("editor: new Ext.form.TextField({}),");
							break;
					}
					mat = pat.matcher(t[j]);
					if(mat.find()){
						//System.out.println(mat.group(1));
						//System.out.println(mat.group(2));
						fields.append("length: '"+mat.group(2)+"',");
						titulo=mat.replaceAll("").trim().toUpperCase();
						columns.append("header: '"+titulo+"',");
						columns.append("width: "+titulo.length()*10+",");
						//System.out.println(mat.replaceAll(""));
					}else{
						columns.append("header: '"+cols[i].toUpperCase()+"',");
					}
					columns.append("dataIndex: '"+cols[i]+"',");
					columns.append("sortable: true}");
	
					fields.append("allowBlank : false,");				//no permitir valores nulos
					//fields.append("header: '"+titulo+"',");
					fields.append("name:'"+cols[i]+"'}");
					if(i!=cols.length-1){
						columns.append(",");
						fields.append(",");
					}
				}
				columns.append("]");
				fields.append("]");
				
				System.out.println(columns.toString());
				System.out.println(fields.toString());
				
				defCol.setCatalogo(nombreCatalogo);
				defCol.setColumns(columns.toString());
				defCol.setFields(fields.toString());
				ls.add(defCol);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: MantenimientoCatalogosAction, M: obtenHeads");
		}
		return ls;
	}
	public boolean editarTodos(String nombreCatalogo){
		boolean existe=false;
		try{
		//CatalogosTotalmenteEditables cat= CatalogosTotalmenteEditables.valueOf(nombreCatalogo);
		existe=true;
		}catch(Exception e){
			existe=false;
		}
		return existe;
	}
//	private enum CatalogosTotalmenteEditables{
//		//cat_act_economica,
//		//cat_act_generica,
//		//cat_caja,
//		//cat_calidad_empresa,
//		cat_centro_costo,
//		//cat_cobrador,
//		//cat_concepto_trasp,
//		cat_divisa,
//		cat_edo_civil,
//		cat_estado,
//		//cat_giro,
//		//cat_grupo_cupo,
//		//cat_grupo_flujo,
//		cat_pais,
//		//cat_riesgo_empresa,
//		//cat_sucursal,
//		//cat_tamano,
//		cat_tasas,
//		cat_tipo_direccion,
//		//cat_tipo_firma,
//		cat_tipo_inmueble,
//		cat_tipo_medio,
//		cat_tipo_saldo,
//		dia_inhabil,
//		muestra_concepto;
//	}
	@DirectMethod
	public MantenimientoCatalogosDto getBodyReportService()
	{
					
		MantenimientoCatalogosDto filasReporte=new MantenimientoCatalogosDto();
		 
		
		try{		
			if(Utilerias.haveSession(WebContextManager.get())){
				objMantenimientoCatalogosService = (MantenimientoCatalogosService)contexto.obtenerBean("objMantenimientoCatalogosBusinessImpl");
				filasReporte = objMantenimientoCatalogosService.getBodyReport();			
				System.out.println(filasReporte + " filas del reporte");
				//System.out.println(filasReporte.getRow() + " contenido de renglon");
			}
			
		}catch(Exception e){
			/*logger.error("Error: P:CASH FLOW, C:CashFlowActionImplements, M:getBodyReportServicePAEE");
			e.printStackTrace();*/
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:fdsaf, C:adfasd, M:sdfasd");					
		}	
		
		return filasReporte;
		
	}//END METHOD getBodyReportService

	@DirectMethod
	public HSSFWorkbook reporteCatalogo(String catalogo, ServletContext context){
		HSSFWorkbook wb=null;
		System.out.println(catalogo);
		try {
			objMantenimientoCatalogosService = (MantenimientoCatalogosService)contexto.obtenerBean("objMantenimientoCatalogosBusinessImpl",context);
			wb=objMantenimientoCatalogosService.reporteCatalogo(catalogo);
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasAction, M: reporteCatalogo");
		}return wb;
	}

}
