package com.webset.set.egresos.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.webset.set.egresos.dao.SeguimientoCPDao;
import com.webset.set.egresos.dto.SeguimientoCPDto;
import com.webset.set.egresos.service.SeguimientoCPService;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.Funciones;

public class SeguimientoCPBusinessImpl <objSeguimientoCPDao> implements SeguimientoCPService{
	

	private SeguimientoCPDao objSeguimientoCPDao;
	Bitacora bitacora;
	Funciones funciones = new Funciones();
	SeguimientoCPDto objSeguimientoCPDto = new SeguimientoCPDto();

	
	
	public SeguimientoCPDao getObjSeguimientoCPDao() {
		return objSeguimientoCPDao;
	}



	public void setObjSeguimientoCPDao(SeguimientoCPDao objSeguimientoCPDao) {
		this.objSeguimientoCPDao = objSeguimientoCPDao;
	}



	@Override
	public List<SeguimientoCPDto> llenaGrid(String idEmpresa, String idBeneficiario, String noDocumento, String periodo) {
		List<SeguimientoCPDto> resultado = new ArrayList<SeguimientoCPDto>();
		try {
			resultado = objSeguimientoCPDao.llenaGrid(idEmpresa, idBeneficiario, noDocumento, periodo);
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Egresos, C: SeguimientoCPBusinessImpl, M: llenaGrid");
		}return resultado;
	}



	@Override
	public String cadenaDeInformacion(String idEmpresa, String idBeneficiario, String noDocumento, String periodo) {
		List<SeguimientoCPDto> renglon= new ArrayList<SeguimientoCPDto>();
		String cadenaDeInformacion ="No se han podido obtener los datos";
		try {
			
			 renglon = objSeguimientoCPDao.cadenaDeInformacion(idEmpresa, idBeneficiario, noDocumento, periodo);
			 
			if (renglon!=null && !renglon.isEmpty()) {
				SeguimientoCPDto seguimiento= renglon.get(0);
				cadenaDeInformacion="\n Movimiento encontrado como "+ seguimiento.getDescFormaPago();
				System.out.println(seguimiento.getOrigenMov());
				System.out.println(seguimiento.getIdTipoOperacion());
				if (seguimiento.getOrigenMov().equals("CVT")&& !seguimiento.getIdTipoOperacion().equals("3000")) {
					cadenaDeInformacion+=" de forma pago compra venta de transferencia ";
				} else {
					cadenaDeInformacion+=" con forma de pago "+seguimiento.getDescFormaPago();
				}
				

				if (seguimiento.getIdTipoOperacion().equals("3200") || seguimiento.getIdTipoOperacion().equals("3701")
						&& seguimiento.getDescFormaPago().equals("TRANSFERENCIA") || seguimiento.getDescFormaPago().equals("TRASPASO")
						|| seguimiento.getDescFormaPago().equals("CHEQUE") && !seguimiento.getDescEstatus().equals("CANCELADO")) {
						
					cadenaDeInformacion+=" esta ";
					if (seguimiento.getDescEstatus().equals("APLICADO")) {
						cadenaDeInformacion+=" PAGADO ";
					}else if (seguimiento.getDescEstatus().equals("PENDIENTE")) {
						if (seguimiento.getDescFormaPago().equals("CHEQUE")) {
							cadenaDeInformacion+=" PENDIENTE POR IMPRIMIR ";
						}else if (seguimiento.getDescFormaPago().equals("TRANSFERENCIA")) {
							cadenaDeInformacion+=" PENDIENTE POR ENVIAR A LA BANCA ELECTR�NICA ";
						}
					}else{
						cadenaDeInformacion+=seguimiento.getDescEstatus();
					}
					
					cadenaDeInformacion+=" EL DIA "+seguimiento.getFechaV();
					cadenaDeInformacion+=", ESTA EN LA CAJA "+seguimiento.getDescCaja();
					
					
					
				} else if (!seguimiento.getGrupo().equals("0") && seguimiento.getDescEstatus().equals("APLICADO")) {
					
				}
				
				if (seguimiento.getIdTipoOperacion().equals("3000")) {
					cadenaDeInformacion+=" CON UN IMPORTE DE $"+seguimiento.getImporte()+ " " +seguimiento.getId_divisa();
					System.out.println(seguimiento.getDescUsuario());
					if (seguimiento.getDescUsuario()==null) {
						cadenaDeInformacion+=" ";
					} else {
						cadenaDeInformacion+=" FUE DADO DE ALTA POR "+seguimiento.getDescUsuario();
					}
					
				} else {
					cadenaDeInformacion+=" CON UN IMPORTE DE $"+seguimiento.getImporte()+ " " +seguimiento.getId_divisa();
				}
				
				if (seguimiento.getIdTipoOperacion().equals("3200") || seguimiento.getIdTipoOperacion().equals("3701")
						&& seguimiento.getDescFormaPago().equals("TRANSFERENCIA ") || seguimiento.getDescFormaPago().equals("TRASPASO")
						|| seguimiento.getDescFormaPago().equals("CHEQUE ") && !seguimiento.getDescEstatus().equals("CANCELADO")) {
					
					if (seguimiento.getDescUsuario()!=null) {
						cadenaDeInformacion+=", FUE PAGADO POR EL USUARIO "+ seguimiento.getDescUsuario();

					}
					
					if (seguimiento.getBancoBenef()!=null) {
    
						cadenaDeInformacion+=" a la cuenta ";
						if (seguimiento.getBancoBenef()!=null) {
							cadenaDeInformacion+= seguimiento.getBancoBenef() ;
						}
						if (seguimiento.getIdChequeraBenef()!=null) {
							cadenaDeInformacion+= seguimiento.getIdChequeraBenef() ;
						}
					
						
						
					}
					
					if (seguimiento.getBanco()!=null) {
						cadenaDeInformacion+=" Y SE PAGO CON LA CUENTA ";
						if (!seguimiento.getBanco().equals(null)) {
							cadenaDeInformacion+= seguimiento.getBanco() ;
						}
						if (seguimiento.getIdChequera()!=null) {
							cadenaDeInformacion+= seguimiento.getIdChequera() ;
						}
					}
					if (seguimiento.getDescUsuarioMod()!=null) {
						cadenaDeInformacion+=" FUE MODIFICADO POR EL USUARIO"; 
		                
						if (seguimiento.getDescUsuarioMod()!=null) {
							cadenaDeInformacion+= seguimiento.getDescUsuarioMod() ;
						}
			
					}
					
					
				} 
				
				if (seguimiento.getDescEstatus().equals("CANCELADO")) {
					cadenaDeInformacion+=" fue cancelado por el usuario ";
					if (!seguimiento.getDescUsuario().equals(null)) {
						cadenaDeInformacion+= seguimiento.getDescUsuario();
					}else{
						cadenaDeInformacion+= " desconocido ";
					}
				}
				if (seguimiento.getCveControl()!=null && !seguimiento.getCveControl().equals("") ) {
					cadenaDeInformacion+= " Este movimiento se encuentra en la propuesta ";
					cadenaDeInformacion+= seguimiento.getCveControl();
					
					if(seguimiento.getUsuarioUno()!=null && !seguimiento.getUsuarioUno().equals("")  ){
						cadenaDeInformacion+= ", esta autorizada por el usuario ";
						cadenaDeInformacion+= seguimiento.getUsuarioUno();
						if (seguimiento.getFechaPropuesta()!=null && !seguimiento.getFechaPropuesta().equals("") ) {
							cadenaDeInformacion+= ", con fecha de autorizaci�n ";
							cadenaDeInformacion+= seguimiento.getFechaPropuesta();
						} else {
							cadenaDeInformacion+= ", no tiene fecha de autorizaci�n ";
						}
					}else{
						cadenaDeInformacion+= ", no esta autoizada por ningun usuario ";
					}
					
					if ((seguimiento.getIdTipoOperacion().equals("3200") || seguimiento.getIdTipoOperacion().equals("3701"))
					&& (seguimiento.getDescFormaPago().equals("TRANSFERENCIA") || seguimiento.getDescFormaPago().equals("TRASPASO")
					|| seguimiento.getDescFormaPago().equals("CHEQUE")) && !seguimiento.getEstatus().equals("CANCELADO")){
						
						if (seguimiento.getDescUsuario()!=null && !seguimiento.getDescUsuario().equals("")) {
							cadenaDeInformacion+= ", fue pagado por el usuario ";
							cadenaDeInformacion+= seguimiento.getDescUsuario();
						}
						
						if (seguimiento.getBancoBenef()!=null && !seguimiento.getBancoBenef().equals("")) {
							cadenaDeInformacion+= ", a la cuenta ";
							cadenaDeInformacion+= seguimiento.getBancoBenef() ;
							cadenaDeInformacion+= seguimiento.getIdChequeraBenef() ;
						}
						
						if (seguimiento.getBanco()!=null && !seguimiento.getBanco().equals("")) {
							cadenaDeInformacion+= ", y se pago con la cuenta ";
							cadenaDeInformacion+= seguimiento.getBanco() ;
							cadenaDeInformacion+= seguimiento.getIdChequera() ;
						}
						
						if (seguimiento.getDescUsuarioMod()!=null && !seguimiento.getDescUsuarioMod().equals("")) {
							cadenaDeInformacion+= ", Fue modificado por el usuario ";
							cadenaDeInformacion+= seguimiento.getDescUsuarioMod() ;
							
						}
						
						
						
					} 
					
					if (seguimiento.getDescEstatus().equals("CANCELADO")) {
						if (seguimiento.getDescUsuario()!=null && !seguimiento.getDescUsuario().equals("")) {
							cadenaDeInformacion+= ", Fue cancelado por el usuario ";
							cadenaDeInformacion+= seguimiento.getDescUsuario() ;
						} else {
							cadenaDeInformacion+= ", Fue cancelado por el usuario ";
							cadenaDeInformacion+= " desconocido" ;
						}
					}
						
					
				} else {
					cadenaDeInformacion+= "\n Este movimiento no se encuentra en ninguna propuesta ";
				}
				
			} 

			
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Egresos, C: SeguimientoCPBusinessImpl, M: cadenaDeInformacion");
		}return cadenaDeInformacion;
	}
	
	
	

}
