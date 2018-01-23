package com.webset.set.utilerias.business.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.dao.MantenimientoChequerasDao;
import com.webset.set.utilerias.dto.LlenaComboEmpresasDto;
import com.webset.set.utilerias.service.MantenimientoChequerasService;
import com.webset.set.utileriasmod.dto.MantenimientoChequerasDto;
import com.webset.utils.tools.Utilerias;

public class MantenimientoChequerasBusinessImpl implements MantenimientoChequerasService
{
	/*
	 *Revisado por LASM 
	 * 
	 */
	//Falta lo de la impresion continua en la pantalla de las chequeras
	
	MantenimientoChequerasDao objMantenimientoChequerasDao;
	Bitacora bitacora = new Bitacora();
	MantenimientoChequerasDto objChequerasDto = new MantenimientoChequerasDto();
	List<MantenimientoChequerasDto> recibeDatos = new ArrayList<MantenimientoChequerasDto>();
			
	public List<LlenaComboEmpresasDto> obtenerEmpresas (int idUsuario){	
		return objMantenimientoChequerasDao.obtenerEmpresas(idUsuario);
	}
		
	public List<MantenimientoChequerasDto> obtenerBancos(int noEmpresa){	
		return objMantenimientoChequerasDao.obtenerBancos(noEmpresa);
	}
	
	public List<MantenimientoChequerasDto> llenaGrid(int noEmpresa, int idBanco){		
		return objMantenimientoChequerasDao.llenaGrid(noEmpresa, idBanco);
	}
	
	public List<MantenimientoChequerasDto> obtieneTipoChequeras(){
		return objMantenimientoChequerasDao.obtieneTipoChequeras();
	}
		
	public List<MantenimientoChequerasDto> llenaComboGrupo(){
		return objMantenimientoChequerasDao.llenaComboGrupo();
	}
	public List<MantenimientoChequerasDto> obtenerBancosTodos(int bancoNacional){			
		return objMantenimientoChequerasDao.obtenerBancosTodos(bancoNacional);
	}
	
	public List<MantenimientoChequerasDto> llenaComboDivision(int noEmpresa){		
		return objMantenimientoChequerasDao.llenaComboDivision(noEmpresa);
	}
	
	public List<MantenimientoChequerasDto> llenaComboDivisa(){
		return objMantenimientoChequerasDao.llenaComboDivisa();
	}
	
	public String configuraSet (int indice){
		return objMantenimientoChequerasDao.configuraSet(indice);
	}
	
	public int eliminaChequeras(int noEmpresa, int idBanco, String idChequera, int noUsuario){
		int resp = 0;
		int respBusqueda = 0;
		
		try {
			respBusqueda = objMantenimientoChequerasDao.buscaRegPendientes(noEmpresa, idBanco, idChequera);
			
			if(respBusqueda == 0)
				resp = objMantenimientoChequerasDao.eliminaChequeras(noEmpresa, idBanco, idChequera, noUsuario);
			else
				resp = 100;
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:MantenimientoChequerasBusinessImpl, M:eliminaChequeras");
		}
		return resp;
	}
	
	public String aceptar (List<Map<String, String>> registro)
	{
		List<MantenimientoChequerasDto> recibeDatos = new ArrayList<MantenimientoChequerasDto>();
		
		String mensaje = "";
		String psBanco = "";
		String bConcentradora = "";
		String bCheques = "";
		String bTransferencias = "";
		String chequeOcurre = "";
		String cargoEnCuenta = "";
		String bTraspasos = "";
		String massPayment = "";
		String psDivision = "";
		String psDivisa = "";
		String psNacExt = "";
		String psChequera = "";
		String psPlaza = "";
		String psSucursal = "";
		String psDescChequera = "";
		String psTipoChequera = "";
		String psAba = "";
		String psSwift = "";
		String psClabe = "";
		String psAbaSwift = "";
		String psEmpresa = "";
		double pdSaldoInicial = 0;
		int resultadoEntero = 0;
		
		try{			
			psEmpresa = registro.get(0).get("noEmpresa");
		
			//Validacion de los datos que contiene el registro
			//Banco
			if (registro.get(0).get("idBanco").equals(0) || registro.get(0).get("idBanco").equals(""))
				mensaje = "Falta seleccionar el banco";
			else
				psBanco = registro.get(0).get("idBanco");
			
			
			//Chequera
			if (registro.get(0).get("idChequera").equals(""))
				mensaje = "Falta la chequera";
			else
				psChequera = registro.get(0).get("idChequera");

			//Plaza
			if (registro.get(0).get("plaza").equals("") && registro.get(0).get("tipoChequera").equals("P") && registro.get(0).get("nacionalidad").equals("N"))
				mensaje = "Falta la plaza del Banco";
			else
				psPlaza = registro.get(0).get("plaza");
			
			//Sucursal
			if ((registro.get(0).get("sucursal").equals("") && registro.get(0).get("tipoChequera").equals("P")) && registro.get(0).get("nacionalidad").equals("N"))
				mensaje = "Falta la Sucursal del Banco";
			else
				psSucursal = registro.get(0).get("sucursal");
			
			//Descripcion de la chequera
			if (registro.get(0).get("descChequera").equals(""))
				mensaje = "Falta la descripcion de la chequera";
			else
				psDescChequera = registro.get(0).get("descChequera");
			
			//Divisa
			if (registro.get(0).get("idDivisa").equals(""))
				mensaje = "Falta la divisa de la chequera";
			else
				psDivisa = registro.get(0).get("idDivisa");
			
			//Tipo de chequera
			if (registro.get(0).get("tipoChequera").equals(""))
				mensaje = "Falta el tipo de la chequera";
			else
				psTipoChequera = registro.get(0).get("tipoChequera");
			
			//Saldo Inicial de la chequera
			if (registro.get(0).get("saldoInicial").equals(""))
				mensaje = "Falta indicar el Saldo Inicial de la Chequera";
			else
				pdSaldoInicial = Double.parseDouble(registro.get(0).get("saldoInicial"));
				
			//Clabe
			if (!registro.get(0).get("clabe").equals("")){
				if (registro.get(0).get("clabe").length() != 18)
					mensaje = "La CLABE debe de ser de 18 digitos";
				else
					psClabe = registro.get(0).get("clabe");
			}
			
			//Aba o Swift
			if (registro.get(0).get("nacionalidad").equals("E")){
				if (registro.get(0).get("aba").equals(""))
					mensaje = "Falta capturar el ABA o SWIFT";
				else
					psAbaSwift = registro.get(0).get("nacionalidad");
					
			}
			
			//Asignacion de valores para los checkBox
			if (registro.get(0).get("bConcentradora").equals("1"))
				bConcentradora = "S";
			else
				bConcentradora = "N";
			
			if(registro.get(0).get("bCheques").equals("1"))
				bCheques = "S";
			else
				bCheques = "N";
			
			if (registro.get(0).get("bTransferencias").equals("1"))
				bTransferencias = "S";
			else
				bTransferencias = "N";
			
			if (registro.get(0).get("chequeOcurre").equals("1"))
				chequeOcurre = "S";
			else
				chequeOcurre = "N";
			
			if (registro.get(0).get("cargoEnCuenta").equals("1"))
				cargoEnCuenta = "S";
			else
				cargoEnCuenta = "N";
			
			if (registro.get(0).get("bTraspaso").equals("1"))
				bTraspasos = "S";
			else
				bTraspasos = "N";
			
			if (registro.get(0).get("massPayment").equals("1"))
				massPayment = "S";
			else
				massPayment = "N";
			/*		
    If Chkb_impre_continua.Value = 1 Then
        ps_b_impre_continua = "S"
    Else
        ps_b_impre_continua = "N"
    End If
      
    'IALA 13/04/2007
    If ps_b_impre_continua <> pvbImpre_Continua Then
        If MsgBox("El ultimo n�mero de cheque impreso del banco: " & Me.cmbbanco2.Text & " de la chequera: " & Me.txtchequera2.Text & _
                  IIf((ps_b_impre_continua = "S"), " continua ", " laser ") & vbCrLf & " es: " & IIf(Trim(Me.txtUltCheImp.Text) = "", "0", Me.txtUltCheImp.Text) & ". " & _
                  "�Es correcto?", vbInformation + vbYesNo, "SET") = vbNo Then
            Screen.MousePointer = 0
            Me.txtUltCheImp.SetFocus
            Exit Sub
        End If
    End If    
			 */
			
			//Divisa			
			if (registro.get(0).get("idDivisa").equals("") || registro.get(0).get("idDivisa").equals("0"))			
				mensaje = "Falta la divisa de la chequera";
				
			else
				psDivisa = registro.get(0).get("idDivisa");
			
			//Division (solo aplica para CIE)
			if (registro.get(0).get("localizacion").equals("CIE") && (!registro.get(0).get("idDivision").equals("") || !registro.get(0).get("idDivision").equals("0")))
				psDivision = registro.get(0).get("idDivision");
			else
				psDivision = "";
				
			if (registro.get(0).get("nacionalidad").equals("N"))
			{
				psNacExt = "N";
				if (Integer.parseInt(registro.get(0).get("idBanco")) < 1000 && !registro.get(0).get("idDivisa").equals("MN"))
				{
					if (registro.get(0).get("idChequera").length() > 25)
						mensaje = "La longitud de la chequera debe de ser menor a 25 caracteres";
				}
				else
				{
					//Se valida que el banco 127 que es Banco Azteca tenga 14 caracteres de longitud en la chequera
					if (registro.get(0).get("idBanco").equals("127")){
						if(registro.get(0).get("idChequera").length() != 14)
						{
							mensaje = "Las chequeras de Banco Azteca deben ser de 14 caracteres";
						}
					}
					else if (registro.get(0).get("idBanco").equals("12"))
					{
						if (registro.get(0).get("idChequera").substring(0, 2).equals("91")){
							if(registro.get(0).get("idChequera").length() != 13)
								mensaje = "Las chequeras de Bancomer deben de ser de 13 caracteres";
						}
						else{
							if (registro.get(0).get("idChequera").length() != 11 && 
								registro.get(0).get("idChequera").length() != 18 && 
								registro.get(0).get("idChequera").length() != 25)
								mensaje = "Las chequeras de Bancomer deben de ser de 11, 18 � 25 caracteres";
						}
					}
					else 
					{
						if (registro.get(0).get("idChequera").length() != 11 && 
								registro.get(0).get("idChequera").length() != 18 && 
								registro.get(0).get("idChequera").length() != 25)
								mensaje = "Las chequeras Nacionales deben de ser de 11, 18 � 25 caracteres";
					}									
				}
			}
			else
				psNacExt = "E";
					
			//Aqui se valida si trae el mensaje vacio todo esta correcto en los datos y entra a insertar o modificar
			if (mensaje.equals(""))
			{
				//Asigna todos los campos al objeto
		
				objChequerasDto.setNoEmpresa(Integer.parseInt(registro.get(0).get("noEmpresa")));		
				objChequerasDto.setIdBanco(Integer.parseInt(psBanco));
				objChequerasDto.setIdChequeraWhere(registro.get(0).get("idChequeraWhere"));
				objChequerasDto.setIdChequera(psChequera);				
				objChequerasDto.setDescChequera(psDescChequera);
				objChequerasDto.setIdDivisa(psDivisa);
				objChequerasDto.setSaldoInicial(pdSaldoInicial);
				objChequerasDto.setSaldoFinal(pdSaldoInicial);
				objChequerasDto.setUltimoCheque(Integer.parseInt(registro.get(0).get("ultimoCheque").equals("") ? "0" : registro.get(0).get("ultimoCheque")));
				objChequerasDto.setAbonoSBC(Double.parseDouble(registro.get(0).get("abonoSBC").equals("") ? "0" : registro.get(0).get("abonoSBC")));
				objChequerasDto.setPlaza(psPlaza);
				objChequerasDto.setSucursal(psSucursal);
				objChequerasDto.setTipoChequera(psTipoChequera);
				objChequerasDto.setNacionalidad(registro.get(0).get("nacionalidad"));
				objChequerasDto.setBConcentradora(bConcentradora);
				objChequerasDto.setSaldoBcoInicial(Double.parseDouble(registro.get(0).get("saldoBcoInicial").equals("") ? "0" : registro.get(0).get("saldoBcoInicial")));						
				objChequerasDto.setFecHoy(registro.get(0).get("fecHoy"));						
				objChequerasDto.setIdUsuario(Integer.parseInt(registro.get(0).get("idUsuario")));						
				objChequerasDto.setSaldoConta(Double.parseDouble(registro.get(0).get("saldoConta")));						
				objChequerasDto.setBTraspaso(bTraspasos);
				objChequerasDto.setSaldoMinimo(Double.parseDouble(registro.get(0).get("saldoMinimo")));
				objChequerasDto.setPeriodoConciliacion(registro.get(0).get("periodoConciliacion"));
				objChequerasDto.setBCheques(bCheques);
				objChequerasDto.setBTransferencias(bTransferencias);
				objChequerasDto.setChequeOcurre(chequeOcurre);
				objChequerasDto.setClabe(registro.get(0).get("clabe"));
				objChequerasDto.setAba(psAba);
				objChequerasDto.setSwift(psSwift);
				objChequerasDto.setDivision(registro.get(0).get("idDivision").equals("") ? "0" : registro.get(0).get("idDivision"));
				objChequerasDto.setSobregiro(Double.parseDouble(registro.get(0).get("sobregiro").equals("") ? "0" : registro.get(0).get("sobregiro")));
				objChequerasDto.setMassPayment(massPayment);
				objChequerasDto.setCargoEnCuenta(cargoEnCuenta);
				objChequerasDto.setHouseBank(registro.get(0).get("houseBank"));
		
				//Inserta chequera
				if (registro.get(0).get("tipoOperacion").equals("insertar")){					
					//Valida que no exista la chequera dada de alta
					recibeDatos = objMantenimientoChequerasDao.buscaChequera(Integer.parseInt(registro.get(0).get("idBanco")), registro.get(0).get("idChequera"));
					
					if (recibeDatos.size() != 0)
					{
						mensaje = "La chequera ya existe!";
					}
					else
					{
						if (registro.get(0).get("nacionalidad").equals("E")){
							if(registro.get(0).get("tipoAbaSwift").equals("Aba")){
								psAba = registro.get(0).get("aba");
								objChequerasDto.setAba(registro.get(0).get("aba"));
							}
							else{
								psSwift = registro.get(0).get("aba");
								objChequerasDto.setSwift(registro.get(0).get("aba"));
							}	
						}					
						
						resultadoEntero = objMantenimientoChequerasDao.insertaChequera(objChequerasDto);
						
						if (resultadoEntero > 0){
							mensaje = "Datos insertados con exito";							
						}						
					}
				}
				else //Modifica la chequera
				{
					if (registro.get(0).get("nacionalidad").equals("E")){
						if(registro.get(0).get("tipoAbaSwift").equals("Aba")){
							psAba = registro.get(0).get("aba");
							objChequerasDto.setAba(registro.get(0).get("aba"));
						}
						else{
							psSwift = registro.get(0).get("aba");
							objChequerasDto.setSwift(registro.get(0).get("aba"));
						}	
					}
					
					resultadoEntero = objMantenimientoChequerasDao.actualizaChequera(objChequerasDto);
					
					if (resultadoEntero > 0)
						mensaje = "Datos actualizados con exito";
					else
						mensaje = "Los datos no se actualizaron correctamente";
					/*
			
                If ps_b_traspaso = "S" Then
                    Call gobjSQL.FunSQLUpdateCheqTraspaso(GI_ID_EMPRESA, _
                                                          txtchequera2.Text, _
                                                          Txtid_divisa, pi_banco)
                End If
                    
                If ps_b_cheque = "S" Then
                    Call gobjSQL.FunSQLUpdateCheqCheque(GI_ID_EMPRESA, _
                                                        txtchequera2.Text, _
                                                        Txtid_divisa, pi_banco)
                End If
                    
                If ps_b_transferencia = "S" Then
                    Call gobjSQL.FunSQLUpdateCheqTransferencia(GI_ID_EMPRESA, _
                                                          txtchequera2.Text, _
                                                          Txtid_divisa, pi_banco)
                End If
                
                If ps_b_cargo_en_cuenta = "S" Then
                    Call gobjSQL.FunSQLUpdateCargoEnCuenta(GI_ID_EMPRESA, _
                                                          txtchequera2.Text, _
                                                          Txtid_divisa, pi_banco)
                End If
                
                If ps_b_cheque_ocurre = "S" Then
                    Call gobjSQL.FunSQLUpdateCheqOcurre(GI_ID_EMPRESA, _
                                                          txtchequera2.Text, _
                                                          Txtid_divisa, pi_banco)
                End If
                        
                    ' Recalcula el Saldo Final
'MLU10
                Call gobjSQL.FunSQLUpdate10(txtidbanco2, ps_chequera)
    
'MLU8    ---- ojo!!! se repite
                                       
                MsgBox "Datos registrados!", vbInformation, "SET"
                       
            End If
                    
            Screen.MousePointer = 0

            Call CmdLimpiar_Click
            Call CmdBuscar_Click
                
        End If
					 */
				}									
			}
			else
				return mensaje;
		}		
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:MantenimientoChequerasBusinessImpl, M:aceptar");			
		}return mensaje;		
	}
		
	public List<MantenimientoChequerasDto> obtieneCajas(){
		return objMantenimientoChequerasDao.obtieneCajas();
	}
	
	public String facultadDeModificarChequera (int noUsuario, String indice){
		String mensaje = "";
		try{
			recibeDatos = objMantenimientoChequerasDao.facultadDeModificarChequera(noUsuario, indice);
			System.out.println(recibeDatos.size() + " tama�o de recibe");
			if (recibeDatos.size() > 0){
				mensaje = "true";				
			}
			else{
				if (indice.equals("136"))
					mensaje = "No tiene la facultad para Modificar Saldos";
				else if (indice.equals("136, 137"))
					mensaje = "No tiene la facultad para Modificar Chequeras";
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: MantenimientoChequerasBusinessImpl, M: facultadDeModificarChequera");
		}return mensaje;
	}
	
	//****************************************************************************************************************************************
	public MantenimientoChequerasDao getObjMantenimientoChequerasDao() 
	{
		return objMantenimientoChequerasDao;
	}

	public void setObjMantenimientoChequerasDao(MantenimientoChequerasDao objMantenimientoChequerasDao) 
	{
		this.objMantenimientoChequerasDao = objMantenimientoChequerasDao;
	}

	@Override
	public Map<String, Object> guardarChequera(String jString) {
		Map<String, Object> resultado= new HashMap<String, Object>();
		resultado.put("Status", false);
		resultado.put("msg", "error desconocido");
		try {
			Gson gson=new Gson();
			List<Map <String, String>> datos = gson.fromJson(jString, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
			MantenimientoChequerasDto mantenimientoChequerasDto = new MantenimientoChequerasDto();
			mantenimientoChequerasDto.setIdChequera(datos.get(0).get("idChequera"));
			mantenimientoChequerasDto.setDescChequera(datos.get(0).get("descChequera"));
			mantenimientoChequerasDto.setPlaza(datos.get(0).get("plaza"));
			mantenimientoChequerasDto.setSucursal(datos.get(0).get("sucursal"));
			mantenimientoChequerasDto.setSaldoInicial(Double.parseDouble(datos.get(0).get("saldoInicial")));
			//System.out.println(datos.get(0).get("saldoFinal")==null);
			//mantenimientoChequerasDto.setSaldoFinal(Double.parseDouble(datos.get(0).get("saldoFinal")));
			mantenimientoChequerasDto.setUltimoCheque(Integer.parseInt(datos.get(0).get("ultimoCheque")));
			mantenimientoChequerasDto.setAbonoSBC(Double.parseDouble(datos.get(0).get("abonoSBC")));
			mantenimientoChequerasDto.setNacionalidad(datos.get(0).get("nacionalidad"));
			mantenimientoChequerasDto.setIdDivisa(datos.get(0).get("idDivisa"));
			//mantenimientoChequerasDto.setDescDivisa(datos.get(0).get("descDivisa"));
			mantenimientoChequerasDto.setTipoChequera(datos.get(0).get("tipoChequera"));
			mantenimientoChequerasDto.setSaldoMinimo(Double.parseDouble(datos.get(0).get("saldoMinimo")));
			mantenimientoChequerasDto.setPeriodoConciliacion(datos.get(0).get("periodoConciliacion"));
			mantenimientoChequerasDto.setSaldoConta(Double.parseDouble(datos.get(0).get("saldoConta")));
			mantenimientoChequerasDto.setBConcentradora(datos.get(0).get("bConcentradora"));
			mantenimientoChequerasDto.setBTraspaso(datos.get(0).get("bTraspaso"));
			mantenimientoChequerasDto.setBCheques(datos.get(0).get("bCheques"));
			mantenimientoChequerasDto.setBTransferencias(datos.get(0).get("bTransferencias"));
			mantenimientoChequerasDto.setChequeOcurre(datos.get(0).get("chequeOcurre"));
			mantenimientoChequerasDto.setCargoEnCuenta(datos.get(0).get("cargoEnCuenta"));
			mantenimientoChequerasDto.setClabe(datos.get(0).get("clabe"));
			mantenimientoChequerasDto.setImpresionContinua(datos.get(0).get("impresionContinua"));
			mantenimientoChequerasDto.setAba(datos.get(0).get("aba"));
			mantenimientoChequerasDto.setIdDivision(datos.get(0).get("idDivision"));
			//mantenimientoChequerasDto.setDescDivision(datos.get(0).get("descDivision"));
			mantenimientoChequerasDto.setSobregiro(Double.parseDouble(datos.get(0).get("sobregiro")));
			mantenimientoChequerasDto.setMassPayment(datos.get(0).get("massPayment"));
			mantenimientoChequerasDto.setHouseBank(datos.get(0).get("houseBank"));
			mantenimientoChequerasDto.setIdUsuario(Integer.parseInt(datos.get(0).get("idUsuario")));
			mantenimientoChequerasDto.setNoEmpresa(Integer.parseInt(datos.get(0).get("noEmpresa")));
			mantenimientoChequerasDto.setFecHoy(datos.get(0).get("fecHoy"));
			mantenimientoChequerasDto.setIdBanco(Integer.parseInt(datos.get(0).get("idBanco")));
			
			int r=objMantenimientoChequerasDao.guardarChequera(mantenimientoChequerasDto);
			if (r==0) {
				resultado.put("msg", "La chequera no pudo guardarse");
			} else if (r>0) {
				resultado.put("Status", true);
				resultado.put("msg", "La chequera fue registrada exitosamente");
				
			}
			
		}catch (NumberFormatException e) {
			e.printStackTrace();
			resultado.put("msg", "El saldo inicial no es correcto");
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: MantenimientoChequerasBusinessImpl, M: guardarChequera");
		}
			
		return resultado;
	}

	@Override
	public HSSFWorkbook reporteChequeras(String tipoChequera, String empresa) {
		HSSFWorkbook hb=null;
		try {
			hb=Utilerias.generarExcel(new String[]{
					"no_empresa",
					"id_banco",					
					"id_chequera",
					"plaza",
					"sucursal",
					"desc_chequera",				
					"b_cheque",
					"b_transferencia",
					"b_cheque_ocurre",
					"b_cargo_en_cuenta",					
					"id_divisa",					
					"desc_divisa",
					"desc_banco",					
					"ult_cheq_impreso",
					"abono_sbc",
					"tipo_chequera",
					"desc__tipo_chequera",					
					"saldo_minimo",
					"saldo_banco_ini",					
					"b_concentradora",
					"nac_ext",
					"b_traspaso",
					"saldo_conta_ini",
					"aba",
					"id_clabe",
					"b_impre_continua",
					"periodo_conciliacion",
					"id_division",
					"desc_division",
					"sobregiro",
					"pago_mass",
					"housebank",
					"saldo_inicial",
					"cargo",
					"abono",
					"saldo_final"	
			}, 
					objMantenimientoChequerasDao.reporteChequeras(tipoChequera, empresa), new String[]{
							"no_empresa",
							"id_banco",					
							"id_chequera",
							"plaza",
							"sucursal",
							"desc_chequera",				
							"b_cheque",
							"b_transferencia",
							"b_cheque_ocurre",
							"b_cargo_en_cuenta",					
							"id_divisa",					
							"desc_divisa",
							"desc_banco",					
							"ult_cheq_impreso",
							"abono_sbc",
							"tipo_chequera",
							"desc__tipo_chequera",					
							"saldo_minimo",
							"saldo_banco_ini",					
							"b_concentradora",
							"nac_ext",
							"b_traspaso",
							"saldo_conta_ini",
							"aba",
							"id_clabe",
							"b_impre_continua",
							"periodo_conciliacion",
							"id_division",
							"desc_division",
							"sobregiro",
							"pago_mass",
							"housebank",
							"saldo_inicial",
							"cargo",
							"abono",
							"saldo_final"		
								
					});
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasBusinessImpl, M: reportePersonas");
		}return hb;
	}	
}
