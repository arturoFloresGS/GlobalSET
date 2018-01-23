package com.webset.set.personas.dto;

public class ConsultaPersonasDto {
	int noEmpresa;
	String nomEmpresa;
	String idTipoPersona;
	String descTipoPersona;
	String equivalePersona;
	String nombre;
	String tipoPersona;
	int noPersona;
	String fisicaMoral;
	String idEstadoCivil;
	String descEstadoCivil;
	int persona;
	String razonSocialDI;
	int idActividadEconomica;
	String descActividadEconomica;
	int idGrupo;
	String descGrupo;
	int idActividadGenerica;
	String descActividadGenerica;
	String idDireccion;
	String descDireccion;
	int idGiro;
	String descGiro;
	int idCaja;
	String descCaja;
	int idRiesgo;
	String descRiesgo;
	String idTamano;
	String descTamano;
	String idCalidad;
	String descCalidad;
	String idInmueble;
	String descInmueble;
	String idEstado;
	String descEstado;
	String idPais;
	String descPais;
	int idFormaPagoProv;
	String descFormaPagoProv;
	int idContrato;
	double descContrato;
	int idContratoPayment;
	String descContratoPayment;
	String paterno;
	String materno;
	String razonSocial;
	String nombreCorto;
	String rfc;
	String puesto;
	String bAsociacion;
	String bProveedor;
	String bContratoInversion;
	String fechaIngreso;
	String bServicios;
	String objetoSocial;
	double ventasAnuales;
	int noEmpleados;
	int diaLimite;
	int diaRecepcion;
	int accionA;
	int accionB;
	int accionC;
	int idRubro;
	String exporta;
	String concentradora;
	String division;
	String pagoCruzado;
	String estatus;
	int noDivisiones;
	String sexo;
	String pagoReferenciado;
	String fechaHoy;
	int usuarioModif;
	String idEstatus;
	String referencia;
	String multiempresa;
	String noBenef;
	String descBenef;
	int noLinea;
	int noCuenta;
	int noCuentaEmp;
	private String descMedio;
	private String contactoMedio;
	private String especial;
	
	
	
	
public String getEspecial() {
		return especial;
	}
	public void setEspecial(String especial) {
		this.especial = especial;
	}
public String getDescMedio() {
		return descMedio;
	}
	public void setDescMedio(String descMedio) {
		this.descMedio = descMedio;
	}
	public String getContactoMedio() {
		return contactoMedio;
	}
	public void setContactoMedio(String contactoMedio) {
		this.contactoMedio = contactoMedio;
	}

	//****************************** MANTENIMIENTO CUENTAS PROVEEDOR ***********************************************************************
	int idBanco;
	String descBanco;
	String chequera;
	String descChequera;
	String divisa;
	int sucursal;
	int plaza;
	String clabe;
	String chequeraAnt;
	String bancoI;
	////////////////////////////////Direcciones//////////////////////////////////
	
	String idTipoD;
	String calle;
	String colonia;
	String cp;
	String delegacion;
	String ciudad;
	String estado;
	String pais;
	String fecha;
	String cuenta;
	int tipoRelacion;
	
	

	//////////////////////////////////////////////////////////////////////////////
	public String getCuenta() {
		return cuenta;
	}
	public String getBancoI() {
		return bancoI;
	}
	public void setBancoI(String bancoI) {
		this.bancoI = bancoI;
	}
	public void setCuenta(String cuenta) {
		this.cuenta = cuenta;
	}
	
	public int getTipoRelacion() {
		return tipoRelacion;
	}
	public void setTipoRelacion(int tipoRelacion) {
		this.tipoRelacion = tipoRelacion;
	}
	
	public String getFecha() {
		return fecha;
	}
	public void setFecha(String fecha) {
		this.fecha = fecha;
	}
	///////////////////////////////////////////

	//////////////////////////////////////////7
	
	int idBancoAnt;
	//int usuarioModif; (esta ya declarado)
	String fecModif;
	//int noEmpresa; (ya declarado)
	//int noPersona; (ya declarado)
	String chequeraBenef;
	String swift;
	String aba;
	int bankTrue;
	int bankCorresponding;
	String nacionalidad;
	String chequeraTrue;
	String bancoAnterior;
	String chequeraAnterior;
	String abaInter;
	String swiftInter;
	String abaCorresp;
	String swiftCorresp;	
	String grabado;
	String idDivisa;	
	String noDocto;
	int usuarioAlta;
	int folioDet;
	int folioReal;
	String concepto;
	String actualizaChequeraProv;
	
	private int sGrupoRubro;
	private int sRubro;
	private String sDescGrupoRubro;
	private String sDescRubro;
	
	private String descTipoRelacion;
	private String fecRegistro;
	private int noPersonaRel;
	
	public String getActualizaChequeraProv() {
		return actualizaChequeraProv;
	}
	public void setActualizaChequeraProv(String actualizaChequeraProv) {
		this.actualizaChequeraProv = actualizaChequeraProv;
	}
	public String getConcepto() {
		return concepto;
	}
	public void setConcepto(String concepto) {
		this.concepto = concepto;
	}
	public int getFolioReal() {
		return folioReal;
	}
	public void setFolioReal(int folioReal) {
		this.folioReal = folioReal;
	}
	public int getFolioDet() {
		return folioDet;
	}
	public void setFolioDet(int folioDet) {
		this.folioDet = folioDet;
	}
	public int getUsuarioAlta() {
		return usuarioAlta;
	}
	public void setUsuarioAlta(int usuarioAlta) {
		this.usuarioAlta = usuarioAlta;
	}
	public String getNoDocto() {
		return noDocto;
	}
	public void setNoDocto(String noDocto) {
		this.noDocto = noDocto;
	}
	public String getIdDivisa() {
		return idDivisa;
	}
	public void setIdDivisa(String idDivisa) {
		this.idDivisa = idDivisa;
	}
	public String getGrabado() {
		return grabado;
	}
	public void setGrabado(String grabado) {
		this.grabado = grabado;
	}
	public int getIdBanco() {
		return idBanco;
	}
	public void setIdBanco(int idBanco) {
		this.idBanco = idBanco;
	}
	public String getDescBanco() {
		return descBanco;
	}
	public void setDescBanco(String descBanco) {
		this.descBanco = descBanco;
	}
	public String getChequera() {
		return chequera;
	}
	public void setChequera(String chequera) {
		this.chequera = chequera;
	}
	public String getDescChequera() {
		return descChequera;
	}
	public void setDescChequera(String descChequera) {
		this.descChequera = descChequera;
	}
	public String getDivisa() {
		return divisa;
	}
	public void setDivisa(String divisa) {
		this.divisa = divisa;
	}
	public int getSucursal() {
		return sucursal;
	}
	public void setSucursal(int sucursal) {
		this.sucursal = sucursal;
	}
	public int getPlaza() {
		return plaza;
	}
	public void setPlaza(int plaza) {
		this.plaza = plaza;
	}
	public String getClabe() {
		return clabe;
	}
	public void setClabe(String clabe) {
		this.clabe = clabe;
	}
	public String getChequeraAnt() {
		return chequeraAnt;
	}
	public void setChequeraAnt(String chequeraAnt) {
		this.chequeraAnt = chequeraAnt;
	}
	public int getIdBancoAnt() {
		return idBancoAnt;
	}
	public void setIdBancoAnt(int idBancoAnt) {
		this.idBancoAnt = idBancoAnt;
	}
	public String getFecModif() {
		return fecModif;
	}
	public void setFecModif(String fecModif) {
		this.fecModif = fecModif;
	}
	public String getChequeraBenef() {
		return chequeraBenef;
	}
	public void setChequeraBenef(String chequeraBenef) {
		this.chequeraBenef = chequeraBenef;
	}
	public String getSwift() {
		return swift;
	}
	public void setSwift(String swift) {
		this.swift = swift;
	}
	public String getAba() {
		return aba;
	}
	public void setAba(String aba) {
		this.aba = aba;
	}
	public int getBankTrue() {
		return bankTrue;
	}
	public void setBankTrue(int bankTrue) {
		this.bankTrue = bankTrue;
	}
	public int getBankCorresponding() {
		return bankCorresponding;
	}
	public void setBankCorresponding(int bankCorresponding) {
		this.bankCorresponding = bankCorresponding;
	}
	public String getNacionalidad() {
		return nacionalidad;
	}
	public void setNacionalidad(String nacionalidad) {
		this.nacionalidad = nacionalidad;
	}
	public String getChequeraTrue() {
		return chequeraTrue;
	}
	public void setChequeraTrue(String chequeraTrue) {
		this.chequeraTrue = chequeraTrue;
	}
	public String getBancoAnterior() {
		return bancoAnterior;
	}
	public void setBancoAnterior(String bancoAnterior) {
		this.bancoAnterior = bancoAnterior;
	}
	public String getChequeraAnterior() {
		return chequeraAnterior;
	}
	public void setChequeraAnterior(String chequeraAnterior) {
		this.chequeraAnterior = chequeraAnterior;
	}
	public String getAbaInter() {
		return abaInter;
	}
	public void setAbaInter(String abaInter) {
		this.abaInter = abaInter;
	}
	public String getSwiftInter() {
		return swiftInter;
	}
	public void setSwiftInter(String swiftInter) {
		this.swiftInter = swiftInter;
	}
	public String getAbaCorresp() {
		return abaCorresp;
	}
	public void setAbaCorresp(String abaCorresp) {
		this.abaCorresp = abaCorresp;
	}
	public String getSwiftCorresp() {
		return swiftCorresp;
	}
	public void setSwiftCorresp(String swiftCorresp) {
		this.swiftCorresp = swiftCorresp;
	}
	public int getNoCuentaEmp() {
		return noCuentaEmp;
	}
	public void setNoCuentaEmp(int noCuentaEmp) {
		this.noCuentaEmp = noCuentaEmp;
	}
	public int getNoLinea() {
		return noLinea;
	}
	public void setNoLinea(int noLinea) {
		this.noLinea = noLinea;
	}
	public int getNoCuenta() {
		return noCuenta;
	}
	public void setNoCuenta(int noCuenta) {
		this.noCuenta = noCuenta;
	}
	public String getNoBenef() {
		return noBenef;
	}
	public void setNoBenef(String noBenef) {
		this.noBenef = noBenef;
	}
	public String getDescBenef() {
		return descBenef;
	}
	public void setDescBenef(String descBenef) {
		this.descBenef = descBenef;
	}
	public String getMultiempresa() {
		return multiempresa;
	}
	public void setMultiempresa(String multiempresa) {
		this.multiempresa = multiempresa;
	}
	public String getReferencia() {
		return referencia;
	}
	public void setReferencia(String referencia) {
		this.referencia = referencia;
	}
	public String getIdEstatus() {
		return idEstatus;
	}
	public void setIdEstatus(String idEstatus) {
		this.idEstatus = idEstatus;
	}		
	public int getUsuarioModif() {
		return usuarioModif;
	}
	public void setUsuarioModif(int usuarioModif) {
		this.usuarioModif = usuarioModif;
	}
	public String getFechaHoy() {
		return fechaHoy;
	}
	public void setFechaHoy(String fechaHoy) {
		this.fechaHoy = fechaHoy;
	}
	
	public String getPagoReferenciado() {
		return pagoReferenciado;
	}
	public void setPagoReferenciado(String pagoReferenciado) {
		this.pagoReferenciado = pagoReferenciado;
	}
	public String getSexo() {
		return sexo;
	}
	public void setSexo(String sexo) {
		this.sexo = sexo;
	}
	public int getNoDivisiones() {
		return noDivisiones;
	}
	public void setNoDivisiones(int noDivisiones) {
		this.noDivisiones = noDivisiones;
	}
	public String getEstatus() {
		return estatus;
	}
	public void setEstatus(String estatus) {
		this.estatus = estatus;
	}
	public String getExporta() {
		return exporta;
	}
	public void setExporta(String exporta) {
		this.exporta = exporta;
	}
	public String getConcentradora() {
		return concentradora;
	}
	public void setConcentradora(String concentradora) {
		this.concentradora = concentradora;
	}
	public String getDivision() {
		return division;
	}
	public void setDivision(String division) {
		this.division = division;
	}
	public String getPagoCruzado() {
		return pagoCruzado;
	}
	public void setPagoCruzado(String pagoCruzado) {
		this.pagoCruzado = pagoCruzado;
	}
	public int getAccionA() {
		return accionA;
	}
	public void setAccionA(int accionA) {
		this.accionA = accionA;
	}
	public int getAccionB() {
		return accionB;
	}
	public void setAccionB(int accionB) {
		this.accionB = accionB;
	}
	public int getAccionC() {
		return accionC;
	}
	public void setAccionC(int accionC) {
		this.accionC = accionC;
	}
	public int getDiaRecepcion() {
		return diaRecepcion;
	}
	public void setDiaRecepcion(int diaRecepcion) {
		this.diaRecepcion = diaRecepcion;
	}
	public int getDiaLimite() {
		return diaLimite;
	}
	public void setDiaLimite(int diaLimite) {
		this.diaLimite = diaLimite;
	}
	public int getNoEmpleados() {
		return noEmpleados;
	}
	public void setNoEmpleados(int noEmpleados) {
		this.noEmpleados = noEmpleados;
	}
	public double getVentasAnuales() {
		return ventasAnuales;
	}
	public void setVentasAnuales(double ventasAnuales) {
		this.ventasAnuales = ventasAnuales;
	}
	public String getObjetoSocial() {
		return objetoSocial;
	}
	public void setObjetoSocial(String objetoSocial) {
		this.objetoSocial = objetoSocial;
	}
	public String getBServicios() {
		return bServicios;
	}
	public void setBServicios(String servicios) {
		bServicios = servicios;
	}
	public String getFechaIngreso() {
		return fechaIngreso;
	}
	public void setFechaIngreso(String fechaIngreso) {
		this.fechaIngreso = fechaIngreso;
	}
	public String getBAsociacion() {
		return bAsociacion;
	}
	public void setBAsociacion(String asociacion) {
		bAsociacion = asociacion;
	}
	public String getBProveedor() {
		return bProveedor;
	}
	public void setBProveedor(String proveedor) {
		bProveedor = proveedor;
	}
	public String getBContratoInversion() {
		return bContratoInversion;
	}
	public void setBContratoInversion(String contratoInversion) {
		bContratoInversion = contratoInversion;
	}
	public String getRfc() {
		return rfc;
	}
	public void setRfc(String rfc) {
		this.rfc = rfc;
	}
	public String getPuesto() {
		return puesto;
	}
	public void setPuesto(String puesto) {
		this.puesto = puesto;
	}
	public String getNombreCorto() {
		return nombreCorto;
	}
	public void setNombreCorto(String nombreCorto) {
		this.nombreCorto = nombreCorto;
	}
	public String getPaterno() {
		return paterno;
	}
	public void setPaterno(String paterno) {
		this.paterno = paterno;
	}
	public String getMaterno() {
		return materno;
	}
	public void setMaterno(String materno) {
		this.materno = materno;
	}
	public String getRazonSocialDI() {
		return razonSocialDI;
	}
	public void setRazonSocialDI(String razonSocialDI) {
		this.razonSocialDI = razonSocialDI;
	}
	public int getIdContratoPayment() {
		return idContratoPayment;
	}
	public void setIdContratoPayment(int idContratoPayment) {
		this.idContratoPayment = idContratoPayment;
	}
	public String getDescContratoPayment() {
		return descContratoPayment;
	}
	public void setDescContratoPayment(String descContratoPayment) {
		this.descContratoPayment = descContratoPayment;
	}
	public int getIdContrato() {
		return idContrato;
	}
	public void setIdContrato(int idContrato) {
		this.idContrato = idContrato;
	}
	public double getDescContrato() {
		return descContrato;
	}
	public void setDescContrato(double descContrato) {
		this.descContrato = descContrato;
	}
	public int getIdFormaPagoProv() {
		return idFormaPagoProv;
	}
	public void setIdFormaPagoProv(int idFormaPagoProv) {
		this.idFormaPagoProv = idFormaPagoProv;
	}
	public String getDescFormaPagoProv() {
		return descFormaPagoProv;
	}
	public void setDescFormaPagoProv(String descFormaPagoProv) {
		this.descFormaPagoProv = descFormaPagoProv;
	}
	public String getIdInmueble() {
		return idInmueble;
	}
	public void setIdInmueble(String idInmueble) {
		this.idInmueble = idInmueble;
	}
	public String getDescInmueble() {
		return descInmueble;
	}
	public void setDescInmueble(String descInmueble) {
		this.descInmueble = descInmueble;
	}
	public String getIdCalidad() {
		return idCalidad;
	}
	public void setIdCalidad(String idCalidad) {
		this.idCalidad = idCalidad;
	}
	public String getDescCalidad() {
		return descCalidad;
	}
	public void setDescCalidad(String descCalidad) {
		this.descCalidad = descCalidad;
	}
	public String getIdTamano() {
		return idTamano;
	}
	public void setIdTamano(String idTamano) {
		this.idTamano = idTamano;
	}
	public String getDescTamano() {
		return descTamano;
	}
	public void setDescTamano(String descTamano) {
		this.descTamano = descTamano;
	}
	public int getIdRiesgo() {
		return idRiesgo;
	}
	public void setIdRiesgo(int idRiesgo) {
		this.idRiesgo = idRiesgo;
	}
	public String getDescRiesgo() {
		return descRiesgo;
	}
	public void setDescRiesgo(String descRiesgo) {
		this.descRiesgo = descRiesgo;
	}
	public int getIdCaja() {
		return idCaja;
	}
	public void setIdCaja(int idCaja) {
		this.idCaja = idCaja;
	}
	public String getDescCaja() {
		return descCaja;
	}
	public void setDescCaja(String descCaja) {
		this.descCaja = descCaja;
	}
	
	/////////////////////////////
	public String getIdDireccion() {
		return idDireccion;
	}
	public void setIdDireccion(String idDireccion) {
		this.idDireccion = idDireccion;
	}
	public String getDescDireccion() {
		return descDireccion;
	}
	public void setDescDireccion(String descDireccion) {
		this.descDireccion = descDireccion;
	}
	
	////////////////////////
	
	public String getIdPais() {
		return idPais;
	}
	public void setIdPais(String idPais) {
		this.idPais = idPais;
	}
	public String getDescPais() {
		return descPais;
	}
	public void setDescPais(String descPais) {
		this.descPais = descPais;
	}
	
	
	public String getIdEstado() {
		return idEstado;
	}
	public void setIdEstado(String idEstado) {
		this.idEstado = idEstado;
	}
	public String getDescEstado() {
		return descEstado;
	}
	public void setDescEstado(String descEstado) {
		this.descEstado = descEstado;
	}
	
	
	public int getIdGiro() {
		return idGiro;
	}
	public void setIdGiro(int idGiro) {
		this.idGiro = idGiro;
	}
	public String getDescGiro() {
		return descGiro;
	}
	public void setDescGiro(String descGiro) {
		this.descGiro = descGiro;
	}
	public int getIdActividadGenerica() {
		return idActividadGenerica;
	}
	public void setIdActividadGenerica(int idActividadGenerica) {
		this.idActividadGenerica = idActividadGenerica;
	}
	public String getDescActividadGenerica() {
		return descActividadGenerica;
	}
	public void setDescActividadGenerica(String descActividadGenerica) {
		this.descActividadGenerica = descActividadGenerica;
	}
	public int getIdActividadEconomica() {
		return idActividadEconomica;
	}
	public void setIdActividadEconomica(int idActividadEconomica) {
		this.idActividadEconomica = idActividadEconomica;
	}
	public int getIdGrupo() {
		return idGrupo;
	}
	public void setIdGrupo(int idGrupo) {
		this.idGrupo = idGrupo;
	}
	public String getDescGrupo() {
		return descGrupo;
	}
	public void setDescGrupo(String descGrupo) {
		this.descGrupo = descGrupo;
	}
	
	public String getDescActividadEconomica() {
		return descActividadEconomica;
	}
	public void setDescActividadEconomica(String descActividadEconomica) {
		this.descActividadEconomica = descActividadEconomica;
	}
	public String getIdEstadoCivil() {
		return idEstadoCivil;
	}
	public void setIdEstadoCivil(String idEstadoCivil) {
		this.idEstadoCivil = idEstadoCivil;
	}
	public String getDescEstadoCivil() {
		return descEstadoCivil;
	}
	public void setDescEstadoCivil(String descEstadoCivil) {
		this.descEstadoCivil = descEstadoCivil;
	}
	public String getFisicaMoral() {
		return fisicaMoral;
	}
	public void setFisicaMoral(String fisicaMoral) {
		this.fisicaMoral = fisicaMoral;
	}
	public String getEquivalePersona() {
		return equivalePersona;
	}
	public void setEquivalePersona(String equivalePersona) {
		this.equivalePersona = equivalePersona;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getTipoPersona() {
		return tipoPersona;
	}
	public void setTipoPersona(String tipoPersona) {
		this.tipoPersona = tipoPersona;
	}
	public int getNoPersona() {
		return noPersona;
	}
	public void setNoPersona(int noPersona) {
		this.noPersona = noPersona;
	}
	public String getIdTipoPersona() {
		return idTipoPersona;
	}
	public void setIdTipoPersona(String idTipoPersona) {
		this.idTipoPersona = idTipoPersona;
	}
	public String getDescTipoPersona() {
		return descTipoPersona;
	}
	public void setDescTipoPersona(String descTipoPersona) {
		this.descTipoPersona = descTipoPersona;
	}
	public int getNoEmpresa() {
		return noEmpresa;
	}
	public void setNoEmpresa(int noEmpresa) {
		this.noEmpresa = noEmpresa;
	}
	public String getNomEmpresa() {
		return nomEmpresa;
	}
	public void setNomEmpresa(String nomEmpresa) {
		this.nomEmpresa = nomEmpresa;
	}
	public int getSGrupoRubro() {
		return sGrupoRubro;
	}
	public void setSGrupoRubro(int grupoRubro) {
		sGrupoRubro = grupoRubro;
	}
	public int getSRubro() {
		return sRubro;
	}
	public void setSRubro(int rubro) {
		sRubro = rubro;
	}
	public String getSDescGrupoRubro() {
		return sDescGrupoRubro;
	}
	public void setSDescGrupoRubro(String descGrupoRubro) {
		sDescGrupoRubro = descGrupoRubro;
	}
	public String getSDescRubro() {
		return sDescRubro;
	}
	public void setSDescRubro(String descRubro) {
		sDescRubro = descRubro;
	}
	public String getDescTipoRelacion() {
		return descTipoRelacion;
	}
	public void setDescTipoRelacion(String descTipoRelacion) {
		this.descTipoRelacion = descTipoRelacion;
	}
	public String getFecRegistro() {
		return fecRegistro;
	}
	public void setFecRegistro(String fecRegistro) {
		this.fecRegistro = fecRegistro;
	}
	public int getNoPersonaRel() {
		return noPersonaRel;
	}
	public void setNoPersonaRel(int noPersonaRel) {
		this.noPersonaRel = noPersonaRel;
	}
	public int getIdRubro() {
		return idRubro;
	}
	public void setIdRubro(int idRubro) {
		this.idRubro = idRubro;
	}
	//////////////////////////////////Direcciones////////////////////////////////
	public int getPersona() {
		return persona;
	}
	public void setPersona(int persona) {
		this.persona = persona;
	}
	
	public String getRazonSocial() {
		return razonSocial;
	}
	public void setRazonSocial(String razonSocial) {
		this.razonSocial = razonSocial;
	}
	
	
	public String getIdTipoD() {
		return idTipoD;
	}
	public void setIdTipoD(String idTipoD) {
		this.idTipoD = idTipoD;
	}

	public String getCalle() {
		return calle;
	}
	public void setCalle(String calle) {
		this.calle = calle;
	}
	
	public String getColonia() {
		return colonia;
	}
	public void setColonia(String colonia) {
		this.colonia = colonia;
	}
	
	public String getCp() {
		return cp;
	}
	public void setCp(String cp) {
		this.cp = cp;
	}
	
	public String getDelegacion() {
		return delegacion;
	}
	public void setDelegacion(String delegacion) {
		this.delegacion = delegacion;
	}
	
	public String getCiudad() {
		return ciudad;
	}
	public void setCiudad(String ciudad) {
		this.ciudad = ciudad;
	}
	
	public String getEstado() {
		return estado;
	}
	public void setEstado(String estado) {
		this.estado = estado;
	}
	
	public String getPais() {
		return pais;
	}
	public void setPais(String pais) {
		this.pais = pais;
	}
	
	
	//////////////////////////////////////////////////////////////////////////
}