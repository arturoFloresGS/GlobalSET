package mx.com.gruposalinas.Poliza;

public class SOS_PolizasProxy implements mx.com.gruposalinas.Poliza.SOS_Polizas {
  private String _endpoint = null;
  private mx.com.gruposalinas.Poliza.SOS_Polizas sOS_Polizas = null;
  
  public SOS_PolizasProxy() {
    _initSOS_PolizasProxy();
  }
  
  public SOS_PolizasProxy(String endpoint) {
    _endpoint = endpoint;
    _initSOS_PolizasProxy();
  }
  
  private void _initSOS_PolizasProxy() {
    try {
      sOS_Polizas = (new mx.com.gruposalinas.Poliza.SOS_PolizasServiceLocator()).getHTTPS_Port();
      if (sOS_Polizas != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)sOS_Polizas)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)sOS_Polizas)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (sOS_Polizas != null)
      ((javax.xml.rpc.Stub)sOS_Polizas)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public mx.com.gruposalinas.Poliza.SOS_Polizas getSOS_Polizas() {
    if (sOS_Polizas == null)
      _initSOS_PolizasProxy();
    return sOS_Polizas;
  }
  
  public mx.com.gruposalinas.Poliza.DT_Polizas_ResponseResponse[] SOS_Polizas(mx.com.gruposalinas.Poliza.DT_Polizas_OBPolizas[] MT_Polizas_OB) throws java.rmi.RemoteException{
    if (sOS_Polizas == null)
      _initSOS_PolizasProxy();
    return sOS_Polizas.SOS_Polizas(MT_Polizas_OB);
  }
  
  
}