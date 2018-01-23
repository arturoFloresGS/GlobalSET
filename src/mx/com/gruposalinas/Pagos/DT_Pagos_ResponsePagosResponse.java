/**
 * DT_Pagos_ResponsePagosResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package mx.com.gruposalinas.Pagos;

public class DT_Pagos_ResponsePagosResponse  implements java.io.Serializable {
    private java.lang.String NO_EMPRESA;

    private java.lang.String NO_DOC_SAP;

    private java.lang.String SECUENCIA;

    private java.lang.String mensaje;

    private java.lang.String DOC_POLIZA_SAP;

    public DT_Pagos_ResponsePagosResponse() {
    }

    public DT_Pagos_ResponsePagosResponse(
           java.lang.String NO_EMPRESA,
           java.lang.String NO_DOC_SAP,
           java.lang.String SECUENCIA,
           java.lang.String mensaje,
           java.lang.String DOC_POLIZA_SAP) {
           this.NO_EMPRESA = NO_EMPRESA;
           this.NO_DOC_SAP = NO_DOC_SAP;
           this.SECUENCIA = SECUENCIA;
           this.mensaje = mensaje;
           this.DOC_POLIZA_SAP = DOC_POLIZA_SAP;
    }


    /**
     * Gets the NO_EMPRESA value for this DT_Pagos_ResponsePagosResponse.
     * 
     * @return NO_EMPRESA
     */
    public java.lang.String getNO_EMPRESA() {
        return NO_EMPRESA;
    }


    /**
     * Sets the NO_EMPRESA value for this DT_Pagos_ResponsePagosResponse.
     * 
     * @param NO_EMPRESA
     */
    public void setNO_EMPRESA(java.lang.String NO_EMPRESA) {
        this.NO_EMPRESA = NO_EMPRESA;
    }


    /**
     * Gets the NO_DOC_SAP value for this DT_Pagos_ResponsePagosResponse.
     * 
     * @return NO_DOC_SAP
     */
    public java.lang.String getNO_DOC_SAP() {
        return NO_DOC_SAP;
    }


    /**
     * Sets the NO_DOC_SAP value for this DT_Pagos_ResponsePagosResponse.
     * 
     * @param NO_DOC_SAP
     */
    public void setNO_DOC_SAP(java.lang.String NO_DOC_SAP) {
        this.NO_DOC_SAP = NO_DOC_SAP;
    }


    /**
     * Gets the SECUENCIA value for this DT_Pagos_ResponsePagosResponse.
     * 
     * @return SECUENCIA
     */
    public java.lang.String getSECUENCIA() {
        return SECUENCIA;
    }


    /**
     * Sets the SECUENCIA value for this DT_Pagos_ResponsePagosResponse.
     * 
     * @param SECUENCIA
     */
    public void setSECUENCIA(java.lang.String SECUENCIA) {
        this.SECUENCIA = SECUENCIA;
    }


    /**
     * Gets the mensaje value for this DT_Pagos_ResponsePagosResponse.
     * 
     * @return mensaje
     */
    public java.lang.String getMensaje() {
        return mensaje;
    }


    /**
     * Sets the mensaje value for this DT_Pagos_ResponsePagosResponse.
     * 
     * @param mensaje
     */
    public void setMensaje(java.lang.String mensaje) {
        this.mensaje = mensaje;
    }


    /**
     * Gets the DOC_POLIZA_SAP value for this DT_Pagos_ResponsePagosResponse.
     * 
     * @return DOC_POLIZA_SAP
     */
    public java.lang.String getDOC_POLIZA_SAP() {
        return DOC_POLIZA_SAP;
    }


    /**
     * Sets the DOC_POLIZA_SAP value for this DT_Pagos_ResponsePagosResponse.
     * 
     * @param DOC_POLIZA_SAP
     */
    public void setDOC_POLIZA_SAP(java.lang.String DOC_POLIZA_SAP) {
        this.DOC_POLIZA_SAP = DOC_POLIZA_SAP;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof DT_Pagos_ResponsePagosResponse)) return false;
        DT_Pagos_ResponsePagosResponse other = (DT_Pagos_ResponsePagosResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.NO_EMPRESA==null && other.getNO_EMPRESA()==null) || 
             (this.NO_EMPRESA!=null &&
              this.NO_EMPRESA.equals(other.getNO_EMPRESA()))) &&
            ((this.NO_DOC_SAP==null && other.getNO_DOC_SAP()==null) || 
             (this.NO_DOC_SAP!=null &&
              this.NO_DOC_SAP.equals(other.getNO_DOC_SAP()))) &&
            ((this.SECUENCIA==null && other.getSECUENCIA()==null) || 
             (this.SECUENCIA!=null &&
              this.SECUENCIA.equals(other.getSECUENCIA()))) &&
            ((this.mensaje==null && other.getMensaje()==null) || 
             (this.mensaje!=null &&
              this.mensaje.equals(other.getMensaje()))) &&
            ((this.DOC_POLIZA_SAP==null && other.getDOC_POLIZA_SAP()==null) || 
             (this.DOC_POLIZA_SAP!=null &&
              this.DOC_POLIZA_SAP.equals(other.getDOC_POLIZA_SAP())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getNO_EMPRESA() != null) {
            _hashCode += getNO_EMPRESA().hashCode();
        }
        if (getNO_DOC_SAP() != null) {
            _hashCode += getNO_DOC_SAP().hashCode();
        }
        if (getSECUENCIA() != null) {
            _hashCode += getSECUENCIA().hashCode();
        }
        if (getMensaje() != null) {
            _hashCode += getMensaje().hashCode();
        }
        if (getDOC_POLIZA_SAP() != null) {
            _hashCode += getDOC_POLIZA_SAP().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(DT_Pagos_ResponsePagosResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://gruposalinas.com.mx/Pagos", ">DT_Pagos_Response>PagosResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("NO_EMPRESA");
        elemField.setXmlName(new javax.xml.namespace.QName("", "NO_EMPRESA"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("NO_DOC_SAP");
        elemField.setXmlName(new javax.xml.namespace.QName("", "NO_DOC_SAP"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("SECUENCIA");
        elemField.setXmlName(new javax.xml.namespace.QName("", "SECUENCIA"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("mensaje");
        elemField.setXmlName(new javax.xml.namespace.QName("", "Mensaje"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("DOC_POLIZA_SAP");
        elemField.setXmlName(new javax.xml.namespace.QName("", "DOC_POLIZA_SAP"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
