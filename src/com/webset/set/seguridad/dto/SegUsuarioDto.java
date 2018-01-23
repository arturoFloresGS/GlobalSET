package com.webset.set.seguridad.dto;

/**
 * @Author Sergio Vaca
 * @since 19/Octubre/2010
 * @see <p>Tabla seg_usuario</p>
 **/

/**
 * clase que contiene los metodos get y set para los atributos de la tabla
 */
public class SegUsuarioDto {
		
	private int idUsuario;
	private String claveUsuario;
	private String nombre;
	private String apellidoPaterno;
	private String apellidoMaterno;
	private String contrasena;
	private String estatus;
	private int intentos;
	private String correoElectronico;
	private String fechaAcceso;
	private String fechaVencimiento;
	private int idPerfil;
	private String clavePerfil;
	private int noEmpresa;
	private String nomEmpresa;
	private int idCaja;
	private String nomCaja;
	
	/**
	 * combo en busqueda del usuario
	 */
	private int idUsuarioC;
	private String nombreCompletoC;
	
	
	private int idUsuarioCve;
	private String claveUsuarioCve;
	
	private int idUsuarioU;
	private String nombreU;
	
	public int getIdUsuarioU() {
		return idUsuarioU;
	}
	public void setIdUsuarioU(int idUsuarioU) {
		this.idUsuarioU = idUsuarioU;
	}
	public String getNombreU() {
		return nombreU;
	}
	public void setNombreU(String nombreU) {
		this.nombreU = nombreU;
	}
	public int getIdUsuario() {
		return idUsuario;
	}
	public void setIdUsuario(int idUsuario) {
		this.idUsuario = idUsuario;
	}
	public String getClaveUsuario() {
		return claveUsuario;
	}
	public void setClaveUsuario(String claveUsuario) {
		this.claveUsuario = claveUsuario;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getApellidoPaterno() {
		return apellidoPaterno;
	}
	public void setApellidoPaterno(String apellidoPaterno) {
		this.apellidoPaterno = apellidoPaterno;
	}
	public String getApellidoMaterno() {
		return apellidoMaterno;
	}
	public void setApellidoMaterno(String apellidoMaterno) {
		this.apellidoMaterno = apellidoMaterno;
	}
	public String getContrasena() {
		return contrasena;
	}
	public void setContrasena(String contrasena) {
		this.contrasena = contrasena;
	}
	public String getEstatus() {
		return estatus;
	}
	public void setEstatus(String estatus) {
		this.estatus = estatus;
	}
	public int getIntentos() {
		return intentos;
	}
	public void setIntentos(int intentos) {
		this.intentos = intentos;
	}
	public String getCorreoElectronico() {
		return correoElectronico;
	}
	public void setCorreoElectronico(String correoElectronico) {
		this.correoElectronico = correoElectronico;
	}
	public String getFechaAcceso() {
		return fechaAcceso;
	}
	public void setFechaAcceso(String fechaAcceso) {
		this.fechaAcceso = fechaAcceso;
	}
	public String getFechaVencimiento() {
		return fechaVencimiento;
	}
	public void setFechaVencimiento(String fechaVencimiento) {
		this.fechaVencimiento = fechaVencimiento;
	}
	public int getIdPerfil() {
		return idPerfil;
	}
	public void setIdPerfil(int idPerfil) {
		this.idPerfil = idPerfil;
	}
	public String getClavePerfil() {
		return clavePerfil;
	}
	public void setClavePerfil(String clavePerfil) {
		this.clavePerfil = clavePerfil;
	}
	public int getIdUsuarioC() {
		return idUsuarioC;
	}
	public void setIdUsuarioC(int idUsuarioC) {
		this.idUsuarioC = idUsuarioC;
	}
	public String getNombreCompletoC() {
		return nombreCompletoC;
	}
	public void setNombreCompletoC(String nombreCompletoC) {
		this.nombreCompletoC = nombreCompletoC;
	}
	public int getIdUsuarioCve() {
		return idUsuarioCve;
	}
	public void setIdUsuarioCve(int idUsuarioCve) {
		this.idUsuarioCve = idUsuarioCve;
	}
	public String getClaveUsuarioCve() {
		return claveUsuarioCve;
	}
	public void setClaveUsuarioCve(String claveUsuarioCve) {
		this.claveUsuarioCve = claveUsuarioCve;
	}
	public String getNomEmpresa() {
		return nomEmpresa;
	}
	public void setNomEmpresa(String nomEmpresa) {
		this.nomEmpresa = nomEmpresa;
	}
	public String getNomCaja() {
		return nomCaja;
	}
	public void setNomCaja(String nomCaja) {
		this.nomCaja = nomCaja;
	}
	public int getNoEmpresa() {
		return noEmpresa;
	}
	public void setNoEmpresa(int noEmpresa) {
		this.noEmpresa = noEmpresa;
	}
	public int getIdCaja() {
		return idCaja;
	}
	public void setIdCaja(int idCaja) {
		this.idCaja = idCaja;
	}
}
