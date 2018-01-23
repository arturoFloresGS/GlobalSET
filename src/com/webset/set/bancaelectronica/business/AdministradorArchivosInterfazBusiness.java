package com.webset.set.bancaelectronica.business;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;

//Apps


import com.webset.set.bancaelectronica.dao.AdministradorArchivosInterfazDao;
import com.webset.set.bancaelectronica.dto.ParametroBuscarEmpresaDto;
import com.webset.set.bancaelectronica.dto.ParametroLayoutDto;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.BusquedaArchivos;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utilerias.GlobalSingleton;

/**
 * @author Sergio Vaca
 * @since 10/Novimebre/2010
 */
public class AdministradorArchivosInterfazBusiness {
	public final int BANAMEX = 2;
	public final int BANCOMER = 12;
	public final int SANTANDER = 14;
	public final int HSBC = 21;
	// public final int INBURSA = 36;
	public final int INVERLAT = 44;
	public final int BANORTE = 72;
	// public final int AMROBANK = 102;
	// public final int AZTECA = 127;
	public final int CITIBANK = 1026;
	// public final int HSBCDLS = 1229;

	private static Logger logger = Logger.getLogger(AdministradorArchivosInterfazBusiness.class);
	
	private Bitacora bitacora = new Bitacora();
	
	public boolean lbReferenciaPorChequera;
	public boolean lbReferenciaNumerica;
	public int liLongitudReferencia;
	public int idUsuario;
	private JdbcTemplate jdbcTemplate;
//	private administradorArchivosInterfazDao administradorArchivosInterfazDao;
	private AdministradorArchivosInterfazDao administradorArchivosInterfazDao;
	private BusquedaArchivos busquedaArchivos;
	private ParametroBuscarEmpresaDto bEmpresa;
	private ParametroLayoutDto parametro;
	private ParametroLayoutDto insercion;
	private Funciones funciones;
	
	public ArrayList<String> mensajes = new ArrayList<String>();
	public ArrayList<String> mensajesUsuario = new ArrayList<String>();
	
	private boolean control;
	private boolean bExisteChequera;
	private boolean bActualizarSaldo;
	private boolean pbHSBCMismoDia;
	private boolean bEmpresaUsuario;
	private boolean archivoOk;

	private int i;
	private int z;
	private int renglon;
	private int piNoEmpresa;
	private int plFolio;
	private int iContador;
	private int piEncontrados;
	private int piEmpresaAnt;
	private int plRegLeidos;
	private int plRegSinChequera;
	private int piPosicion;

	private long plFolioBanco;

	private double dSaldoBanco;
	private double pdImporte;

	private String psArchivo;
	private String psArchivos;
	private String psFecha;
	private String psSBCobro;
	private String psRegistro;
	private String psChequera;
	private String psConcepto;
	private String psHora;
	private String psReferencia;
	private String sCargoAbono;
	private String sDescripcion;
	private String sSucursal;
	private String psHSBC;
	private String psCveConcepto;
	private String psMensaje;
	private String rutaActual;
	private String psChequerasInexistentes;
	private String psChequeraNueva;
	private String psSucursal;
	private String psCargoAbono;
	private String psSantander;

	private File[] nombres;
	private Scanner archivo = null;
	
	GlobalSingleton globalSingleton;
	
	/**
	 * 
	 * @param activar
	 * 
	 * inicializar variables
	 */
	public void inicializar() {
		busquedaArchivos = null;
		funciones = null;
		bEmpresa = null;
		parametro = null;
		insercion = null;

		control = false;
		bExisteChequera = false;
		bActualizarSaldo = false;
		pbHSBCMismoDia = false;
		bEmpresaUsuario = false;
		archivoOk = false;

		i = 0;
		z = 0;
		renglon = 0;
		piNoEmpresa = 0;
		plFolio = 0;
		iContador = 0;
		piEncontrados = 0;
		piEmpresaAnt = 0;
		plRegLeidos = 0;
		plRegSinChequera = 0;
		plFolioBanco = 0;
		piPosicion = 0;

		dSaldoBanco = 0;
		pdImporte = 0;

		psArchivo = null;
		psArchivos = null;
		psFecha = null;
		psSBCobro = null;
		psRegistro = null;
		psChequera = null;
		psConcepto = null;
		psHora = null;
		psReferencia = null;
		sCargoAbono = null;
		sDescripcion = null;
		sSucursal = null;
		psHSBC = null;
		psCveConcepto = null;
		psMensaje = null;
		rutaActual = null;
		psChequerasInexistentes = null;
		psChequeraNueva = null;
		psSucursal = null;
		psCargoAbono = null;
		psSantander = null;

		archivo = null;
		
		psChequera = "";
		psArchivos = "";
		psChequeraNueva = "";
		busquedaArchivos = new BusquedaArchivos();
		funciones = new Funciones();
		bEmpresaUsuario = true;
		rutaActual = System.getProperty("user.dir");
	}

	/**
	 * 
	 * @param dto
	 * @return boolean
	 * 
	 * 

	 */
	/**
	 * 
	 * @param param
	 * @return int Retorna el folio real de la BD
	 */

	/**
	 * 
	 * @param psArchivos
	 * @param piBanco
	 * @return int
	 * 
	 */
	/**
	 * 
	 * @return Date
	 * 
	 */
	/**
	 * 
	 * @param piBanco
	 * @param piArchivos
	 * @return int
	 * 
	
	
	 */
	

	@SuppressWarnings("unused")
	public void datosMantenimientoMovimiento(){
		//administradorArchivosInterfazDao=new AdministradorArchivosInterfazDao();
		int res = administradorArchivosInterfazDao.DatosMantenimientoMovimiento();
		String result = "";
		
		System.out.println("hola");
		
		if(res==1)
			
			result = "Procedimiento ejecutado correctamente";
		else if(res==-1)
			result = "Ocurrio un error al ejecutar el procedimiento";		
	}
	

	public AdministradorArchivosInterfazDao getAdministradorArchivosInterfazDao() {
		return administradorArchivosInterfazDao;
	}

	public void setAdministradorArchivosInterfazDao(AdministradorArchivosInterfazDao administradorArchivosInterfazDao) {
		this.administradorArchivosInterfazDao = administradorArchivosInterfazDao;
	}

	public boolean isLbReferenciaPorChequera() {
		return lbReferenciaPorChequera;
	}

	public void setLbReferenciaPorChequera(boolean lbReferenciaPorChequera) {
		this.lbReferenciaPorChequera = lbReferenciaPorChequera;
	}

	public int getLiLongitudReferencia() {
		return liLongitudReferencia;
	}

	public void setLiLongitudReferencia(int liLongitudReferencia) {
		this.liLongitudReferencia = liLongitudReferencia;
	}

	public int getIdUsuario() {
		return idUsuario;
	}

	public void setIdUsuario(int idUsuario) {
		this.idUsuario = idUsuario;
	}

	public boolean isLbReferenciaNumerica() {
		return lbReferenciaNumerica;
	}

	public void setLbReferenciaNumerica(boolean lbReferenciaNumerica) {
		this.lbReferenciaNumerica = lbReferenciaNumerica;
	}
}

