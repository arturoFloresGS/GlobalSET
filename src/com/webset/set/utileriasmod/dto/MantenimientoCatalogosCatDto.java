package com.webset.set.utileriasmod.dto;
import java.math.BigDecimal;

public class MantenimientoCatalogosCatDto {
	//cat_act_economica
	private int id_act_economica;
	private String desc_act_economica;
	//cat_act_generica
	private int id_act_generica;
	private String desc_act_generica;
	//cat_caja
	private int id_caja;
	private String desc_caja;
	private String ubicacion;
	private char b_activa;	
	private int equivale_banco;	
	private BigDecimal fondo_fijo_mn;
	private BigDecimal fondo_fijo_dls;
	private char b_abrir_caja;
	//cat_calidad_empresa
	private char id_calidad_empresa;
	private String desc_calidad_empresa;
	//cat_centro_costo
	private String centro_costo;
	private String desc_centro_costo;
	//cat_cobrador
	private int no_cobrador;
	private String nom_cobrador;
	/*private String centro_costo;*/
	private int no_proveedor;
	private int no_empresa;
	//cat_concepto_trasp
	private int id_concepto;
	private String desc_concepto;
	/*private int no_empresa*/
	//cat_divisa
	private String id_divisa;
	private String desc_divisa;
	private String equivale_divisa;
	private String id_divisa_soin;
	private char clasificacion;
	private char muestra_divisa;
	//cat_edo_civil
	private char id_edo_civil;
	private String desc_edo_civil;
	//cat_estado
	private String id_estado;
	private String desc_estado;
	//cat_giro
	private int id_giro;
	private String desc_giro;
	//cat_grupo_cupo
	private int id_grupo_cupo;
	private String desc_grupo_cupo;
	//cat_grupo_flujo
	private int id_grupo_flujo;
	private String desc_grupo_flujo;
	private String correo_empresa;
	private String remitente_correo;
	private int nivel_autorizacion;
	//cat_pais
	private String id_pais;
	private String desc_pais;
	//cat_riesgo_empresa
	private int id_riesgo_empresa;
	private String desc_riesgo_empresa;
	//cat_sucursal
	private int id_sucursal;
	private String desc_sucursal;
	//cat_tamano
	private char id_tamano;
	private String desc_tamano;
	//cat_tasas
	private String id_tasa;
	private String desc_tasa;
	//cat_tipo_direccion
	private String id_tipo_direccion;
	private String desc_tipo_direccion;
	//cat_tipo_firma
	private char id_tipo_firma;
	private String desc_tipo_firma;
	//cat_tipo_inmueble
	private char id_tipo_inmueble;
	private String desc_tipo_inmueble;
	//cat_tipo_medio
	private String id_tipo_medio;
	private String desc_tipo_medio;
	//cat_tipo_saldo
	private int id_tipo_saldo;
	private String desc_tipo_saldo;
	private String clasif_saldo;
	//dia_inhabil
	private String fec_inhabil;
	private String corresponde_pais;
	//muestra_concepto
	private int no_concepto;
	private int id_banco;
	/*private String desc_concepto;*/
	private String despliega;
	private String importa;
	/* forma pago */
	private int id_forma_pago;
	private String desc_forma_pago;
	/* contables erp */
	private int id_contables;
	private String id_chequera;
	private String libro_mayor;
	private String id_cuenta;
	private String cargo_abono;
	/* zusuarios */
	private String bname;
	/* zgpotesoreria */
	private String grupp;
	private String textl;
	/*zcme*/
	private String koart;
	private String ltext;
	private String sgbkz;
	/*factoraje*/
	private int no_prov_intermediario;
	private String nombre;
	/*causa regreso*/
	private int id_causa;
	private String desc_causa;
	/*fechas*/
	private String fec_hoy;
	private String fec_ayer;
	private String fec_manana;
	private int estatus_sist;
	private String fecha3;
	private String fecha4;
	private String fecha5;
	private int usuario_cierre;
	private int estatus_sist2;
	/*causas_novalidez_cheque*/
	private int causa;
	private String descripcion;
	/*configura_set*/
	private int indice;
	private String valor;
	/*cat_catalogo*/
	private String nombre_catalogo;
	private int id_empresa;
	private String desc_catalogo;
	private String titulo_columnas;
	private String campos;
	private String botones;
	/*cat_colores_bit*/
	private String tipo;
	private String color;
	private String descripcion_uno;
	private String descripcion_dos;
	private String id_color;
	/*cat_casa_cambio*/
	private String no_prov_casa_camb;
	private String contacto;
	
	public int getId_causa() {
		return id_causa;
	}
	public void setId_causa(int id_causa) {
		this.id_causa = id_causa;
	}
	public String getDesc_causa() {
		return desc_causa;
	}
	public void setDesc_causa(String desc_causa) {
		this.desc_causa = desc_causa;
	}
	public int getNo_prov_intermediario() {
		return no_prov_intermediario;
	}
	public void setNo_prov_intermediario(int no_prov_intermediario) {
		this.no_prov_intermediario = no_prov_intermediario;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getKoart() {
		return koart;
	}
	public void setKoart(String koart) {
		this.koart = koart;
	}
	public String getLtext() {
		return ltext;
	}
	public void setLtext(String ltext) {
		this.ltext = ltext;
	}
	public String getSgbkz() {
		return sgbkz;
	}
	public void setSgbkz(String sgbkz) {
		this.sgbkz = sgbkz;
	}
	public String getGrupp() {
		return grupp;
	}
	public void setGrupp(String grupp) {
		this.grupp = grupp;
	}
	public String getTextl() {
		return textl;
	}
	public void setTextl(String textl) {
		this.textl = textl;
	}
	public String getBname() {
		return bname;
	}
	public void setBname(String bname) {
		this.bname = bname;
	}
	public int getId_contables() {
		return id_contables;
	}
	public void setId_contables(int id_contables) {
		this.id_contables = id_contables;
	}
	public String getId_chequera() {
		return id_chequera;
	}
	public void setId_chequera(String id_chequera) {
		this.id_chequera = id_chequera;
	}
	public String getLibro_mayor() {
		return libro_mayor;
	}
	public void setLibro_mayor(String libro_mayor) {
		this.libro_mayor = libro_mayor;
	}
	public String getId_cuenta() {
		return id_cuenta;
	}
	public void setId_cuenta(String id_cuenta) {
		this.id_cuenta = id_cuenta;
	}
	public String getCargo_abono() {
		return cargo_abono;
	}
	public void setCargo_abono(String cargo_abono) {
		this.cargo_abono = cargo_abono;
	}

	public String getCorresponde_pais() {
		return corresponde_pais;
	}
	public void setCorresponde_pais(String correspondePais) {
		this.corresponde_pais = correspondePais;
	}
	public String getEquivale_divisa() {
		return equivale_divisa;
	}
	public void setEquivale_divisa(String equivale_divisa) {
		this.equivale_divisa = equivale_divisa;
	}
	public int getId_forma_pago() {
		return id_forma_pago;
	}
	public void setId_forma_pago(int id_forma_pago) {
		this.id_forma_pago = id_forma_pago;
	}
	public String getDesc_forma_pago() {
		return desc_forma_pago;
	}
	public void setDesc_forma_pago(String desc_forma_pago) {
		this.desc_forma_pago = desc_forma_pago;
	}
	public int getId_act_economica() {
		return id_act_economica;
	}
	public void setId_act_economica(int id_act_economica) {
		this.id_act_economica = id_act_economica;
	}
	public String getDesc_act_economica() {
		return desc_act_economica;
	}
	public void setDesc_act_economica(String desc_act_economica) {
		this.desc_act_economica = desc_act_economica;
	}
	public int getId_act_generica() {
		return id_act_generica;
	}
	public void setId_act_generica(int id_act_generica) {
		this.id_act_generica = id_act_generica;
	}
	public String getDesc_act_generica() {
		return desc_act_generica;
	}
	public void setDesc_act_generica(String desc_act_generica) {
		this.desc_act_generica = desc_act_generica;
	}
	public int getId_caja() {
		return id_caja;
	}
	public void setId_caja(int id_caja) {
		this.id_caja = id_caja;
	}
	public String getDesc_caja() {
		return desc_caja;
	}
	public void setDesc_caja(String desc_caja) {
		this.desc_caja = desc_caja;
	}
	public String getUbicacion() {
		return ubicacion;
	}
	public void setUbicacion(String ubicacion) {
		this.ubicacion = ubicacion;
	}
	public char getB_activa() {
		return b_activa;
	}
	public void setB_activa(char b_activa) {
		this.b_activa = b_activa;
	}
	public int getEquivale_banco() {
		return equivale_banco;
	}
	public void setEquivale_banco(int equivale_banco) {
		this.equivale_banco = equivale_banco;
	}
	public BigDecimal getFondo_fijo_mn() {
		return fondo_fijo_mn;
	}
	public void setFondo_fijo_mn(BigDecimal fondo_fijo_mn) {
		this.fondo_fijo_mn = fondo_fijo_mn;
	}
	public BigDecimal getFondo_fijo_dls() {
		return fondo_fijo_dls;
	}
	public void setFondo_fijo_dls(BigDecimal fondo_fijo_dls) {
		this.fondo_fijo_dls = fondo_fijo_dls;
	}
	public char getB_abrir_caja() {
		return b_abrir_caja;
	}
	public void setB_abrir_caja(char b_abrir_caja) {
		this.b_abrir_caja = b_abrir_caja;
	}
	public char getId_calidad_empresa() {
		return id_calidad_empresa;
	}
	public void setId_calidad_empresa(char id_calidad_empresa) {
		this.id_calidad_empresa = id_calidad_empresa;
	}
	public String getDesc_calidad_empresa() {
		return desc_calidad_empresa;
	}
	public void setDesc_calidad_empresa(String desc_calidad_empresa) {
		this.desc_calidad_empresa = desc_calidad_empresa;
	}
	public String getCentro_costo() {
		return centro_costo;
	}
	public void setCentro_costo(String centro_costo) {
		this.centro_costo = centro_costo;
	}
	public String getDesc_centro_costo() {
		return desc_centro_costo;
	}
	public void setDesc_centro_costo(String desc_centro_costo) {
		this.desc_centro_costo = desc_centro_costo;
	}
	public int getNo_cobrador() {
		return no_cobrador;
	}
	public void setNo_cobrador(int no_cobrador) {
		this.no_cobrador = no_cobrador;
	}
	public String getNom_cobrador() {
		return nom_cobrador;
	}
	public void setNom_cobrador(String nom_cobrador) {
		this.nom_cobrador = nom_cobrador;
	}
	public int getNo_proveedor() {
		return no_proveedor;
	}
	public void setNo_proveedor(int no_proveedor) {
		this.no_proveedor = no_proveedor;
	}
	public int getNo_empresa() {
		return no_empresa;
	}
	public void setNo_empresa(int no_empresa) {
		this.no_empresa = no_empresa;
	}
	public int getId_concepto() {
		return id_concepto;
	}
	public void setId_concepto(int id_concepto) {
		this.id_concepto = id_concepto;
	}
	public String getDesc_concepto() {
		return desc_concepto;
	}
	public void setDesc_concepto(String desc_concepto) {
		this.desc_concepto = desc_concepto;
	}
	public String getId_divisa() {
		return id_divisa;
	}
	public void setId_divisa(String id_divisa) {
		this.id_divisa = id_divisa;
	}
	public String getDesc_divisa() {
		return desc_divisa;
	}
	public void setDesc_divisa(String desc_divisa) {
		this.desc_divisa = desc_divisa;
	}
	public String getId_divisa_soin() {
		return id_divisa_soin;
	}
	public void setId_divisa_soin(String id_divisa_soin) {
		this.id_divisa_soin = id_divisa_soin;
	}
	public char getClasificacion() {
		return clasificacion;
	}
	public void setClasificacion(char clasificacion) {
		this.clasificacion = clasificacion;
	}
	public char getMuestra_divisa() {
		return muestra_divisa;
	}
	public void setMuestra_divisa(char muestra_divisa) {
		this.muestra_divisa = muestra_divisa;
	}
	public char getId_edo_civil() {
		return id_edo_civil;
	}
	public void setId_edo_civil(char id_edo_civil) {
		this.id_edo_civil = id_edo_civil;
	}
	public String getDesc_edo_civil() {
		return desc_edo_civil;
	}
	public void setDesc_edo_civil(String desc_edo_civil) {
		this.desc_edo_civil = desc_edo_civil;
	}
	public String getId_estado() {
		return id_estado;
	}
	public void setId_estado(String id_estado) {
		this.id_estado = id_estado;
	}
	public String getDesc_estado() {
		return desc_estado;
	}
	public void setDesc_estado(String desc_estado) {
		this.desc_estado = desc_estado;
	}
	public int getId_giro() {
		return id_giro;
	}
	public void setId_giro(int id_giro) {
		this.id_giro = id_giro;
	}
	public String getDesc_giro() {
		return desc_giro;
	}
	public void setDesc_giro(String desc_giro) {
		this.desc_giro = desc_giro;
	}
	public int getId_grupo_cupo() {
		return id_grupo_cupo;
	}
	public void setId_grupo_cupo(int id_grupo_cupo) {
		this.id_grupo_cupo = id_grupo_cupo;
	}
	public String getDesc_grupo_cupo() {
		return desc_grupo_cupo;
	}
	public void setDesc_grupo_cupo(String desc_grupo_cupo) {
		this.desc_grupo_cupo = desc_grupo_cupo;
	}
	public int getId_grupo_flujo() {
		return id_grupo_flujo;
	}
	public void setId_grupo_flujo(int id_grupo_flujo) {
		this.id_grupo_flujo = id_grupo_flujo;
	}
	public String getDesc_grupo_flujo() {
		return desc_grupo_flujo;
	}
	public void setDesc_grupo_flujo(String desc_grupo_flujo) {
		this.desc_grupo_flujo = desc_grupo_flujo;
	}
	public String getCorreo_empresa() {
		return correo_empresa;
	}
	public void setCorreo_empresa(String correo_empresa) {
		this.correo_empresa = correo_empresa;
	}
	public String getRemitente_correo() {
		return remitente_correo;
	}
	public void setRemitente_correo(String remitente_correo) {
		this.remitente_correo = remitente_correo;
	}
	public int getNivel_autorizacion() {
		return nivel_autorizacion;
	}
	public void setNivel_autorizacion(int nivel_autorizacion) {
		this.nivel_autorizacion = nivel_autorizacion;
	}
	public String getId_pais() {
		return id_pais;
	}
	public void setId_pais(String id_pais) {
		this.id_pais = id_pais;
	}
	public String getDesc_pais() {
		return desc_pais;
	}
	public void setDesc_pais(String desc_pais) {
		this.desc_pais = desc_pais;
	}
	public int getId_riesgo_empresa() {
		return id_riesgo_empresa;
	}
	public void setId_riesgo_empresa(int id_riesgo_empresa) {
		this.id_riesgo_empresa = id_riesgo_empresa;
	}
	public String getDesc_riesgo_empresa() {
		return desc_riesgo_empresa;
	}
	public void setDesc_riesgo_empresa(String desc_riesgo_empresa) {
		this.desc_riesgo_empresa = desc_riesgo_empresa;
	}
	public int getId_sucursal() {
		return id_sucursal;
	}
	public void setId_sucursal(int id_sucursal) {
		this.id_sucursal = id_sucursal;
	}
	public String getDesc_sucursal() {
		return desc_sucursal;
	}
	public void setDesc_sucursal(String desc_sucursal) {
		this.desc_sucursal = desc_sucursal;
	}
	public char getId_tamano() {
		return id_tamano;
	}
	public void setId_tamano(char id_tamano) {
		this.id_tamano = id_tamano;
	}
	public String getDesc_tamano() {
		return desc_tamano;
	}
	public void setDesc_tamano(String desc_tamano) {
		this.desc_tamano = desc_tamano;
	}
	public String getId_tasa() {
		return id_tasa;
	}
	public void setId_tasa(String id_tasa) {
		this.id_tasa = id_tasa;
	}
	public String getDesc_tasa() {
		return desc_tasa;
	}
	public void setDesc_tasa(String desc_tasa) {
		this.desc_tasa = desc_tasa;
	}
	public String getId_tipo_direccion() {
		return id_tipo_direccion;
	}
	public void setId_tipo_direccion(String id_tipo_direccion) {
		this.id_tipo_direccion = id_tipo_direccion;
	}
	public String getDesc_tipo_direccion() {
		return desc_tipo_direccion;
	}
	public void setDesc_tipo_direccion(String desc_tipo_direccion) {
		this.desc_tipo_direccion = desc_tipo_direccion;
	}
	public char getId_tipo_firma() {
		return id_tipo_firma;
	}
	public void setId_tipo_firma(char id_tipo_firma) {
		this.id_tipo_firma = id_tipo_firma;
	}
	public String getDesc_tipo_firma() {
		return desc_tipo_firma;
	}
	public void setDesc_tipo_firma(String desc_tipo_firma) {
		this.desc_tipo_firma = desc_tipo_firma;
	}
	public char getId_tipo_inmueble() {
		return id_tipo_inmueble;
	}
	public void setId_tipo_inmueble(char id_tipo_inmueble) {
		this.id_tipo_inmueble = id_tipo_inmueble;
	}
	public String getDesc_tipo_inmueble() {
		return desc_tipo_inmueble;
	}
	public void setDesc_tipo_inmueble(String desc_tipo_inmueble) {
		this.desc_tipo_inmueble = desc_tipo_inmueble;
	}
	public String getId_tipo_medio() {
		return id_tipo_medio;
	}
	public void setId_tipo_medio(String id_tipo_medio) {
		this.id_tipo_medio = id_tipo_medio;
	}
	public String getDesc_tipo_medio() {
		return desc_tipo_medio;
	}
	public void setDesc_tipo_medio(String desc_tipo_medio) {
		this.desc_tipo_medio = desc_tipo_medio;
	}
	public int getId_tipo_saldo() {
		return id_tipo_saldo;
	}
	public void setId_tipo_saldo(int id_tipo_saldo) {
		this.id_tipo_saldo = id_tipo_saldo;
	}
	public String getDesc_tipo_saldo() {
		return desc_tipo_saldo;
	}
	public void setDesc_tipo_saldo(String desc_tipo_saldo) {
		this.desc_tipo_saldo = desc_tipo_saldo;
	}
	public String getClasif_saldo() {
		return clasif_saldo;
	}
	public void setClasif_saldo(String clasif_saldo) {
		this.clasif_saldo = clasif_saldo;
	}
	public String getFec_inhabil() {
		return fec_inhabil;
	}
	public void setFec_inhabil(String fec_inhabil) {
		this.fec_inhabil = fec_inhabil;
	}
	public int getNo_concepto() {
		return no_concepto;
	}
	public void setNo_concepto(int no_concepto) {
		this.no_concepto = no_concepto;
	}
	public int getId_banco() {
		return id_banco;
	}
	public void setId_banco(int id_banco) {
		this.id_banco = id_banco;
	}
	public String getDespliega() {
		return despliega;
	}
	public void setDespliega(String despliega) {
		this.despliega = despliega;
	}
	public String getImporta() {
		return importa;
	}
	public void setImporta(String importa) {
		this.importa = importa;
	}
	public String getFec_hoy() {
		return fec_hoy;
	}
	public void setFec_hoy(String fec_hoy) {
		this.fec_hoy = fec_hoy;
	}
	public String getFec_ayer() {
		return fec_ayer;
	}
	public void setFec_ayer(String fec_ayer) {
		this.fec_ayer = fec_ayer;
	}
	public String getFec_manana() {
		return fec_manana;
	}
	public void setFec_manana(String fec_manana) {
		this.fec_manana = fec_manana;
	}
	public int getEstatus_sist() {
		return estatus_sist;
	}
	public void setEstatus_sist(int estatus_sist) {
		this.estatus_sist = estatus_sist;
	}
	public String getFecha3() {
		return fecha3;
	}
	public void setFecha3(String fecha3) {
		this.fecha3 = fecha3;
	}
	public String getFecha4() {
		return fecha4;
	}
	public void setFecha4(String fecha4) {
		this.fecha4 = fecha4;
	}
	public String getFecha5() {
		return fecha5;
	}
	public void setFecha5(String fecha5) {
		this.fecha5 = fecha5;
	}
	public int getUsuario_cierre() {
		return usuario_cierre;
	}
	public void setUsuario_cierre(int usuario_cierre) {
		this.usuario_cierre = usuario_cierre;
	}
	public int getEstatus_sist2() {
		return estatus_sist2;
	}
	public void setEstatus_sist2(int estatus_sist2) {
		this.estatus_sist2 = estatus_sist2;
	}
	public int getCausa() {
		return causa;
	}
	public void setCausa(int causa) {
		this.causa = causa;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	public int getIndice() {
		return indice;
	}
	public void setIndice(int indice) {
		this.indice = indice;
	}
	public String getValor() {
		return valor;
	}
	public void setValor(String valor) {
		this.valor = valor;
	}
	
	public String getNombre_catalogo() {
		return nombre_catalogo;
	}
	public void setNombre_catalogo(String nombre_catalogo) {
		this.nombre_catalogo = nombre_catalogo;
	}
	public int getId_empresa() {
		return id_empresa;
	}
	public void setId_empresa(int id_empresa) {
		this.id_empresa = id_empresa;
	}
	public String getDesc_catalogo() {
		return desc_catalogo;
	}
	public void setDesc_catalogo(String desc_catalogo) {
		this.desc_catalogo = desc_catalogo;
	}
	public String getTitulo_columnas() {
		return titulo_columnas;
	}
	public void setTitulo_columnas(String titulo_columnas) {
		this.titulo_columnas = titulo_columnas;
	}
	public String getCampos() {
		return campos;
	}
	public void setCampos(String campos) {
		this.campos = campos;
	}
	public String getBotones() {
		return botones;
	}
	public void setBotones(String botones) {
		this.botones = botones;
	}
	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	public String getDescripcion_uno() {
		return descripcion_uno;
	}
	public void setDescripcion_uno(String descripcion_uno) {
		this.descripcion_uno = descripcion_uno;
	}
	public String getDescripcion_dos() {
		return descripcion_dos;
	}
	public void setDescripcion_dos(String descripcion_dos) {
		this.descripcion_dos = descripcion_dos;
	}
	public String getId_color() {
		return id_color;
	}
	public void setId_color(String id_color) {
		this.id_color = id_color;
	}
	public String getNo_prov_casa_camb() {
		return no_prov_casa_camb;
	}
	public void setNo_prov_casa_camb(String no_prov_casa_camb) {
		this.no_prov_casa_camb = no_prov_casa_camb;
	}
	public String getContacto() {
		return contacto;
	}
	public void setContacto(String contacto) {
		this.contacto = contacto;
	}
	
	
}
