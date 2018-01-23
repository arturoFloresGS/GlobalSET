package mx.com.gruposalinas.CancelacionCompensacion;

public class SOS_CancelacionCompensacionProxy implements mx.com.gruposalinas.CancelacionCompensacion.SOS_CancelacionCompensacion {
  private String _endpoint = null;
  private mx.com.gruposalinas.CancelacionCompensacion.SOS_CancelacionCompensacion sOS_CancelacionCompensacion = null;
  
  public SOS_CancelacionCompensacionProxy() {
    _initSOS_CancelacionCompensacionProxy();
  }
  
  public SOS_CancelacionCompensacionProxy(String endpoint) {
    _endpoint = endpoint;
    _initSOS_CancelacionCompensacionProxy();
  }
  
  private void _initSOS_CancelacionCompensacionProxy() {
    try {
      sOS_CancelacionCompensacion = (new mx.com.gruposalinas.CancelacionCompensacion.SOS_CancelacionCompensacionServiceLocator()).getHTTPS_Port();
      if (sOS_CancelacionCompensacion != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)sOS_CancelacionCompensacion)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)sOS_CancelacionCompensacion)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (sOS_CancelacionCompensacion != null)
      ((javax.xml.rpc.Stub)sOS_CancelacionCompensacion)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public mx.com.gruposalinas.CancelacionCompensacion.SOS_CancelacionCompensacion getSOS_CancelacionCompensacion() {
    if (sOS_CancelacionCompensacion == null)
      _initSOS_CancelacionCompensacionProxy();
    return sOS_CancelacionCompensacion;
  }
  
  public mx.com.gruposalinas.CancelacionCompensacion.DT_CancelacionCompensacion_ResponseCancelaciones[] SOS_CancelacionCompensacion(mx.com.gruposalinas.CancelacionCompensacion.DT_CancelacionCompensacion_OBCancelaciones[] MT_CancelacionCompensacion_OB) throws java.rmi.RemoteException{
    if (sOS_CancelacionCompensacion == null)
      _initSOS_CancelacionCompensacionProxy();
    return sOS_CancelacionCompensacion.SOS_CancelacionCompensacion(MT_CancelacionCompensacion_OB);
  }
  
  
}