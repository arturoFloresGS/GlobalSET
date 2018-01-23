package mx.com.gruposalinas.ImpresionCheques;

public class SOS_ImpresionChequesProxy implements mx.com.gruposalinas.ImpresionCheques.SOS_ImpresionCheques {
  private String _endpoint = null;
  private mx.com.gruposalinas.ImpresionCheques.SOS_ImpresionCheques sOS_ImpresionCheques = null;
  
  public SOS_ImpresionChequesProxy() {
    _initSOS_ImpresionChequesProxy();
  }
  
  public SOS_ImpresionChequesProxy(String endpoint) {
    _endpoint = endpoint;
    _initSOS_ImpresionChequesProxy();
  }
  
  private void _initSOS_ImpresionChequesProxy() {
    try {
      sOS_ImpresionCheques = (new mx.com.gruposalinas.ImpresionCheques.SOS_ImpresionChequesServiceLocator()).getHTTPS_Port();
      if (sOS_ImpresionCheques != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)sOS_ImpresionCheques)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)sOS_ImpresionCheques)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (sOS_ImpresionCheques != null)
      ((javax.xml.rpc.Stub)sOS_ImpresionCheques)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public mx.com.gruposalinas.ImpresionCheques.SOS_ImpresionCheques getSOS_ImpresionCheques() {
    if (sOS_ImpresionCheques == null)
      _initSOS_ImpresionChequesProxy();
    return sOS_ImpresionCheques;
  }
  
  public mx.com.gruposalinas.ImpresionCheques.DT_ImpresionCheques_ResponseCheques[] SOS_ImpresionCheques(mx.com.gruposalinas.ImpresionCheques.DT_ImpresionCheques_OBCheques[] MT_ImpresionCheques_OB) throws java.rmi.RemoteException{
    if (sOS_ImpresionCheques == null)
      _initSOS_ImpresionChequesProxy();
    return sOS_ImpresionCheques.SOS_ImpresionCheques(MT_ImpresionCheques_OB);
  }
  
  
}