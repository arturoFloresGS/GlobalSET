package com.webset.set.utilerias;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import com.softwarementors.extjs.djn.servlet.ssm.WebContext;
import com.softwarementors.extjs.djn.servlet.ssm.WebContextManager;
import com.webset.set.utilerias.dto.ConfiguraSetDto;
import com.webset.set.utilerias.dto.UsuarioLoginDto;

public final class GlobalSingleton {

	Contexto contexto = new Contexto();
	Bitacora bitacora = new Bitacora();
	static GlobalSingleton instancia;
	private List<ConfiguraSetDto> listConfiguraSet;
	private Date fechaHoy;
	
	private UsuarioLoginDto usuarioLoginDto;
	
	public static GlobalSingleton getInstancia(){
		if(instancia == null) {
			instancia = new GlobalSingleton();
			instancia.usuarioLoginDto = null;
		}
		return instancia;
	}

	public Map<String,Object> obtenerPropiedadesUsuario() {
		Map<String,Object> cons = new HashMap<String,Object>();	
		cons.put("idUsuario",getUsuarioLoginDto().getIdUsuario());
		cons.put("paterno",getUsuarioLoginDto().getApellidoPaterno());
		cons.put("materno",getUsuarioLoginDto().getApellidoMaterno());
		cons.put("nombre",getUsuarioLoginDto().getNombre());
		cons.put("noEmpresa",getUsuarioLoginDto().getNumeroEmpresa());
		cons.put("nomEmpresa",getUsuarioLoginDto().getNombreEmpresa());
		cons.put("idCaja",getUsuarioLoginDto().getIdCaja());
		cons.put("descCaja",getUsuarioLoginDto().getDescripcionCaja());
		cons.put("noCuentaEmp",getUsuarioLoginDto().getNoCuentaEmpresa());
		return cons;
	}
	
	public boolean isBPropiedadesCargadas() {
		return usuarioLoginDto != null;
	}	

	public UsuarioLoginDto getUsuarioLoginDto() {
		UsuarioLoginDto login=new UsuarioLoginDto();
		try{
			login=(UsuarioLoginDto)WebContextManager.get().getSession().getAttribute("usuarioLogin");

		}catch(Exception e){
			e.printStackTrace();
			
		}
		return login;
		
	}
	
	public UsuarioLoginDto getUsuarioLoginDto(boolean refrescar) {
		if(refrescar)
			return (UsuarioLoginDto)WebContextManager.get().getSession().getAttribute("usuarioLogin");
		else
			return this.usuarioLoginDto;
	}


	public void setUsuarioLoginDto(UsuarioLoginDto usuarioLoginDto) {
		this.usuarioLoginDto = usuarioLoginDto;
	}

	public List<ConfiguraSetDto> getListConfiguraSet() {
		return listConfiguraSet;
	}

	public void setListConfiguraSet(List<ConfiguraSetDto> listConfiguraSet) {
		this.listConfiguraSet = listConfiguraSet;
	}
	
	/**
	 * Este m�todo nos sirve para setear nuevos valores a la lista 
	 * que contiene las propiedades del usuario, utilizado para
	 * el cambio de empresa y/o caja
	 * @param iIdEmpresa
	 * @param sNomEmpresa
	 * @param iIdCaja
	 * @param sDescCaja
	 * @return un mapa con las propiedades modificadas del usuario
	 */
	
	public Map<String, Object> cambiarPropiedadUsuario(int iIdEmpresa, String sNomEmpresa, int iIdCaja, String sDescCaja){
		Map<String, Object> mapRet = new HashMap<String, Object>();
		try{
			usuarioLoginDto.setNumeroEmpresa(iIdEmpresa);
			usuarioLoginDto.setNombreEmpresa(sNomEmpresa);
			usuarioLoginDto.setIdCaja(iIdCaja);
			usuarioLoginDto.setDescripcionCaja(sDescCaja);
			mapRet = obtenerPropiedadesUsuario();
				
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Utilerias, C:GlobalSingleton, M:obtenerPropiedadUsuario");
			e.printStackTrace();
		}
		return mapRet;
	}
	
	public String obtenerValorConfiguraSet(int indice){
		int i=0;
		try{
			for(i=0; i<this.getListConfiguraSet().size(); i++)
			{
				if(this.getListConfiguraSet().get(i).getIndice() == indice)
				{
					return this.getListConfiguraSet().get(i).getValor();
				}
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Utilerias, C:GlobalSingleton, M:obtenerValorConfiguraSet");
		}
		return "";
	}

	public Date getFechaHoy() {
		return fechaHoy;
	}

	public void setFechaHoy(Date fechaHoy) {
		this.fechaHoy = fechaHoy;
	}
	
	/**
	 * Este metodo valida el usuario y su c�digo de session que se seteo
	 * en seguridad cuando fue validada su contrase�a
	 * @param iIdUsuario
	 * @param sCodSession
	 * @return retorna true si se encontro la session activa de lo contrario false
	 */
	public boolean obtenerUsuarioActivo(){
		return usuarioLoginDto != null;
	}
	
	/**
	 * Este metodo borra de la lista a los usuarios que salen de
	 * session del sistema
	 * @param iIdUsuario
	 * @param sCodSession
	 */
	public void borrarUsuarioActivo(){
		try{
			WebContext context = WebContextManager.get();
		    HttpSession sesion = context.getSession();
		    
		    usuarioLoginDto = null;
			
			sesion.removeAttribute("id_usuario");
			sesion.removeAttribute("userLogin");
			sesion.removeAttribute("usuarioLogin");
			sesion.removeAttribute("facultades");
			sesion.invalidate();
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Utilerias, C:GlobalSingleton, M:borrarUsuarioActivos");
		}
	}
	
	/**
	 * 
	 * Este m�todo realiza una b�squeda del perfil recibido como
	 * parametro, asociado al usuario en session.
	 * @param iIdPerfil
	 * @return
	 */
	public boolean obtenerPerfilUsuario(int iIdPerfil){
		try{
			return getUsuarioLoginDto() != null && getUsuarioLoginDto().getIdPerfil() == iIdPerfil;
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Utilerias, C:GlobalSingleton, M:obtenerPerfilUsuario");
		}
		return false;
	}
}
