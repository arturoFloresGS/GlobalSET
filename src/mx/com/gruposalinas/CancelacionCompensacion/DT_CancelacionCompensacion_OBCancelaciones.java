/**
 * DT_CancelacionCompensacion_OBCancelaciones.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package mx.com.gruposalinas.CancelacionCompensacion;

public class DT_CancelacionCompensacion_OBCancelaciones  implements java.io.Serializable {
    private java.lang.String NO_EMPRESA;

    private java.lang.String NO_DOC_SAP;

    private java.lang.String EJERCICIO;

    public DT_CancelacionCompensacion_OBCancelaciones() {
    }

    public DT_CancelacionCompensacion_OBCancelaciones(
           java.lang.String NO_EMPRESA,
           java.lang.String NO_DOC_SAP,
           java.lang.String EJERCICIO) {
           this.NO_EMPRESA = NO_EMPRESA;
           this.NO_DOC_SAP = NO_DOC_SAP;
           this.EJERCICIO = EJERCICIO;
    }


    /**
     * Gets the NO_EMPRESA value for this DT_CancelacionCompensacion_OBCancelaciones.
     * 
     * @return NO_EMPRESA
     */
    public java.lang.String getNO_EMPRESA() {
        return NO_EMPRESA;
    }


    /**
     * Sets the NO_EMPRESA value for this DT_CancelacionCompensacion_OBCancelaciones.
     * 
     * @param NO_EMPRESA
     */
    public void setNO_EMPRESA(java.lang.String NO_EMPRESA) {
        this.NO_EMPRESA = NO_EMPRESA;
    }


    /**
     * Gets the NO_DOC_SAP value for this DT_CancelacionCompensacion_OBCancelaciones.
     * 
     * @return NO_DOC_SAP
     */
    public java.lang.String getNO_DOC_SAP() {
        return NO_DOC_SAP;
    }


    /**
     * Sets the NO_DOC_SAP value for this DT_CancelacionCompensacion_OBCancelaciones.
     * 
     * @param NO_DOC_SAP
     */
    public void setNO_DOC_SAP(java.lang.String NO_DOC_SAP) {
        this.NO_DOC_SAP = NO_DOC_SAP;
    }


    /**
     * Gets the EJERCICIO value for this DT_CancelacionCompensacion_OBCancelaciones.
     * 
     * @return EJERCICIO
     */
    public java.lang.String getEJERCICIO() {
        return EJERCICIO;
    }


    /**
     * Sets the EJERCICIO value for this DT_CancelacionCompensacion_OBCancelaciones.
     * 
     * @param EJERCICIO
     */
    public void setEJERCICIO(java.lang.String EJERCICIO) {
        this.EJERCICIO = EJERCICIO;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof DT_CancelacionCompensacion_OBCancelaciones)) return false;
        DT_CancelacionCompensacion_OBCancelaciones other = (DT_CancelacionCompensacion_OBCancelaciones) obj;
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
            ((this.EJERCICIO==null && other.getEJERCICIO()==null) || 
             (this.EJERCICIO!=null &&
              this.EJERCICIO.equals(other.getEJERCICIO())));
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
        if (getEJERCICIO() != null) {
            _hashCode += getEJERCICIO().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(DT_CancelacionCompensacion_OBCancelaciones.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://gruposalinas.com.mx/CancelacionCompensacion", ">DT_CancelacionCompensacion_OB>Cancelaciones"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("NO_EMPRESA");
        elemField.setXmlName(new javax.xml.namespace.QName("", "NO_EMPRESA"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("NO_DOC_SAP");
        elemField.setXmlName(new javax.xml.namespace.QName("", "NO_DOC_SAP"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("EJERCICIO");
        elemField.setXmlName(new javax.xml.namespace.QName("", "EJERCICIO"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
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
