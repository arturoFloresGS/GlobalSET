
Ext.onReady(function(){
	Ext.QuickTips.init();
	Ext.Direct.addProvider(Ext.setapp.REMOTING_API);
	var NS = Ext.namespace('apps.SET.Traspasos.EjecucionTraspasos.EjecucionTrapasos');
	NS.tabContId = apps.SET.tabContainerId;	// esta linea sirve para probar la pantalla con la aplicacion completa
	//NS.tabContId = 'contenedorEjecutaTraspasos'; //esta linea sirve para probar la pantalla individual
	var PF = apps.SET.tabID+'.';
	var sName="";
	var banderaEnvio=false;
	
	//variables globales
	NS.GS_DESC_EMPRESA = apps.SET.NOM_EMPRESA;
	NS.GI_ID_EMPRESA = apps.SET.NO_EMPRESA;
	NS.GI_USUARIO = apps.SET.iUserId;
	NS.desc_usuario = apps.SET.NOMBRE+' '+apps.SET.APELLIDO_PATERNO+' '+apps.SET.APELLIDO_MATERNO;
	NS.GS_DESC_CAJA = apps.SET.DESC_CAJA;
	NS.fechaHoy = apps.SET.FEC_HOY;
	NS.BANCOMER = 12;
	NS.CONF_GUARDAR_DCOM = 222;  //indice de configura_set para guardar archivos
	NS.DescOperacion = '';
	NS.optInter = true;
	NS.optMismo = false;
	NS.optSpeua = false;
	NS.optInternacional = false;
	NS.chkInversion = false;
	NS.chkTodos = false;
	NS.optActual = true;
	NS.optTodas = false;
	NS.LI_BANCO = '';
    NS.id_banco = 0;
    NS.iOperacion = 0;
	NS.montoTotal = 0;
	
	NS.autoDesauto = '';
	
	NS.chkH2HSantander = 'false';
	
	
	
	
	//SE AGREGA EL COMBO DE EMPRESA 
	NS.lblNoEmpresaBusqueda = new Ext.form.Label({
		text: 'Empresa:',
		x: 0,
		y: 0
	});
	
	NS.txtNoEmpresaBusqueda = new Ext.form.TextField({
		id: PF + 'txtNoEmpresaBusqueda',
		name: PF + 'txtNoEmpresaBusqueda',
		x: 0,
		y: 15,
		width: 50,
		listeners: {
			change: {
				fn: function(caja, valor) {
					if (caja.getValue() !== null && caja.getValue() !== undefined && caja.getValue() !== '')
						BFwrk.Util.updateTextFieldToCombo(PF + 'txtNoEmpresaBusqueda', NS.cmbEmpresaBusqueda.getId());
					else
						NS.cmbEmpresaBusqueda.reset();					
				}
			}
		}
	});
	
	NS.storeLlenaComboEmpresas = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams: {},
		paramOrder: ['noUsuario'],
		directFn: TraspasosAction.llenaComboEmpresas,
		idProperty: 'noEmpresa',
		fields:
		[
			{name: 'noEmpresa'},
			{name: 'nomEmpresa'}
		],
		listeners: {
			load: function(s, records) {
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeLlenaComboEmpresas, msg: "Cargando..."});
				if (records.length == null || records.length <= 0)
					Ext.Msg.alert('SET', 'No tiene empresas asignadas');
				
				//Se agrega la opcion de todas las empresas
				var recordsStoreEmpresas = NS.storeLlenaComboEmpresas.recordType;
				var todas = new recordsStoreEmpresas({
					noEmpresa: 0,
					nomEmpresa: '********************TODAS********************'
				});
				NS.storeLlenaComboEmpresas.insert(0, todas);
			}
		}		
	});
	
	NS.storeLlenaComboEmpresas.baseParams.noUsuario = NS.GI_USUARIO;
	NS.storeLlenaComboEmpresas.load();
	
	NS.cmbEmpresaBusqueda = new Ext.form.ComboBox({
		store: NS.storeLlenaComboEmpresas,
		id: PF + 'cmbEmpresaBusqueda',
		name: PF + 'cmbEmpresaBusqueda',
		x: 60,
		y: 15,
		width: 375,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		valueField: 'noEmpresa',
		displayField: 'nomEmpresa',
		autocomplete: true,
		emptyText: 'Seleccione una empresa',
		triggerAction: 'all',
		value: '',
		visible: false,
		listeners: {
			select: {
				fn: function(combo, valor) {
					BFwrk.Util.updateComboToTextField(PF + 'txtNoEmpresaBusqueda', NS.cmbEmpresaBusqueda.getId());
				}
			}
		}		
	});
	
	//Se paso el boton de buscar a una funcion
	NS.buscar = function(){
		if (Ext.getCmp(PF + 'txtNoEmpresaBusqueda').getValue() == ''){
			Ext.Msg.alert('SET', 'Debe de seleccionar una empresa valida');
			return;
		}
		
		if (Ext.getCmp(PF + 'txtIdDivisa').getValue() == '' || Ext.getCmp(PF + 'txtIdDivisa').getValue() == 0){
			Ext.Msg.alert('SET', 'Debe de seleccionar una divisa');
			return;
		}
			
        if(NS.chkTodos == false)
        {
	        //Ext.getCmp(PF+'cmdAceptar').setDisabled(false);
	        Ext.getCmp(PF+'cmdPagoParcial').setDisabled(false);
	        //Ext.getCmp(PF+'cmdCancelar').setDisabled(false);
	        Ext.getCmp(PF+'cmdRegresar').setDisabled(false);
	    }
        /*
	    if(cambiarFecha(''+Ext.getCmp(PF+'txtFechaIni').getValue()) > cambiarFecha(''+Ext.getCmp(PF+'txtFechaFin').getValue()))
		{	
			Ext.Msg.alert('SET', 'La segunda fecha debe ser mayor a la primera');
			return;
		} */ 
        if(NS.chkTodos == true)
        {
        	if(Ext.getCmp(PF+'cmbValor').getValue() !== '')
	            NS.LI_BANCO = Ext.getCmp(PF+'cmbValor').getValue()
	        else
	        {
	            NS.LI_BANCO = 0;
	            NS.id_banco = 0;
	        }
		}
	    else
	    {
	        if(Ext.getCmp(PF+'cmbValor').getValue() !== '')
	            NS.LI_BANCO = Ext.getCmp(PF+'cmbValor').getValue()
	        else
	        {
	            Ext.Msg.alert('SET', 'Es necesario seleccionar un Banco');
	            return;
	        }
	  	}
        
        var checkedItem = Ext.getCmp(PF+'groupTipo').getValue();
		
		if(checkedItem.getGroupValue() == 0)
		{
			NS.optInter = true;
			NS.optMismo = false;
			NS.optSpeua = false;
			NS.optInternacional = false;
		}
		if(checkedItem.getGroupValue() == 1)
		{
			NS.optInter = false;
			NS.optMismo = true;
			NS.optSpeua = false;
			NS.optInternacional = false;
		}
		if(checkedItem.getGroupValue() == 2)
		{
			NS.optInter = false;
			NS.optMismo = false;
			NS.optSpeua = true;
			NS.optInternacional = false;
		}
		if(checkedItem.getGroupValue() == 3)
		{
			NS.optInter = false;
			NS.optMismo = false;
			NS.optSpeua = false;
			NS.optInternacional = true;
		}
		var records=NS.storeOperacion.data.items;
		if(Ext.getCmp(PF+'cmbTipoOperacion').getValue() === '')
			NS.iOperacion = 0;
		else
			NS.iOperacion = Ext.getCmp(PF+'cmbTipoOperacion').getValue(); 
		//barrido al grid para obtener la descripcion del combo
		for(j = 0; j< records.length; j++)
		{
			if(records[j].get('idOperacion') == NS.iOperacion)
			{
				NS.DescOperacion = records[j].get('descripcion');
			}
		}
		
		NS.storeClave.removeAll();
        NS.criterios = {};
		NS.matriz = new Array();
		
		//criterios de busqueda
		NS.criterios.DescOperacion = NS.DescOperacion;
		NS.criterios.TipoOperacion = NS.iOperacion;
		NS.criterios.Valor = Ext.getCmp(PF+'cmbValor').getValue();
		NS.criterios.banMismoBanco = NS.optMismo;
		NS.criterios.banEmpresaActual = NS.optActual;
		//NS.criterios.Empresa = NS.GI_ID_EMPRESA;
		NS.criterios.Empresa = Ext.getCmp(PF + 'txtNoEmpresaBusqueda').getValue();
		NS.criterios.Divisa = Ext.getCmp(PF+'cmbDivisa').getValue();
		NS.criterios.banSPEUA = NS.optSpeua;
		NS.criterios.Usuario = NS.GI_USUARIO;
		NS.criterios.fechaIni = cambiarFecha(''+Ext.getCmp(PF+'txtFechaIni').getValue());
		NS.criterios.fechaFin = cambiarFecha(''+Ext.getCmp(PF+'txtFechaFin').getValue());
		NS.criterios.banInternacional = NS.optInternacional;
//		NS.criterios.banco = NS.id_banco;
		NS.criterios.banInversion = NS.chkInversion;
		NS.criterios.optInter = NS.optInter;
		NS.criterios.chkTodos = NS.chkTodos;
		NS.criterios.cmbBenef = Ext.getCmp(PF+'cmbBeneficiarios').getValue();
		
		NS.matriz[0] = NS.criterios;
		var jsonString = Ext.util.JSON.encode(NS.matriz);
		NS.storeGridConsulta.baseParams.criterios = jsonString;
		NS.storeGridConsulta.load();
		
		//If chkTodos.Value = 1 And cmbValor = "" Then
		if(NS.chkTodos == true && Ext.getCmp(PF+'cmbValor').getValue() === '')
		{
			NS.storeClave.baseParams.criterios = jsonString;
			NS.storeClave.load();
		}
		
		
        /*
		    fraBanamex.Visible = False
		    CmbCve.Clear
		    While Not rst_Transfer.EOF
		        ls_Referencia_Traspaso = rst_Transfer!referencia
		    Call listaBeneficiarios
		    CmbCve.Clear
		*/
	}
	
	//Funcion para cancelar los traspasos
	NS.cancelaSolicitud = function(){
		var registroSeleccionado = NS.gridConsulta.getSelectionModel().getSelections();
		var foliosCancelados = "";
		
		for (var i = 0; i < registroSeleccionado.length; i++) {
			if (registroSeleccionado[i].get('origenMov') == 'INV' || registroSeleccionado[i].get('origenMov') == 'FID')
				Ext.Msg.alert('SET', 'cancelación de Traspaso de inversion o al FID no habilitada');
			else if (registroSeleccionado[i].get('usr1') !== null && registroSeleccionado[i].get('usr1') != '' &&
					registroSeleccionado[i].get('usr1') !== undefined && registroSeleccionado[i].get('usr2') !== undefined &&
					registroSeleccionado[i].get('usr2') !== null && registroSeleccionado[i].get('usr2') != '')
				Ext.Msg.alert('SET', 'No puede eliminar un traspaso con autorizaciones');
			else
				foliosCancelados = foliosCancelados + registroSeleccionado[i].get('noFolioMov') + ',';
		}
		
		if (foliosCancelados != ''){
			TraspasosAction.cancelaMovimiento(foliosCancelados, function(resultado, e){
				if (resultado !== null && resultado !== undefined && resultado !== ''){
					Ext.Msg.alert('SET', resultado);
					NS.gridConsulta.store.removeAll();
					NS.gridConsulta.getView().refresh();	
					NS.buscar();
				}
			});
		}
	};
	
	//check
	NS.sm = new Ext.grid.CheckboxSelectionModel({
				
	});
	
//	ls_ruta_envio = gobjVarGlobal.valor_Configura_set(200) 'Ruta_transferencias("paths_envio.txt")
//    ls_ruta_envio_MASSPAYMENT = gobjVarGlobal.valor_Configura_set(196)
//    
//    If ls_ruta_envio = "" Then
//        MsgBox "No se encontró la ruta para el envio en configura_set(200)", vbCritical, "SET"
//        Exit Sub
//    End If
	
	
	//data tipo de operacion
    NS.dataOperacion = [[ '3800', 'TRASPASO ENTRE CUENTAS' ],
						[ '3801', 'TRASPASO INTEREMPRESAS' ],
						[ '3802', 'PAGO INTEREMPRESAS' ],
						[ '3806', 'REGRESO DE APORTACION' ],
						[ '3808', 'CREDITO' ],
						[ '3809', 'PAGO DE CREDITO' ],
						[ '3805', 'APORTACIONES' ],
						[ '3807', 'REGRESO INVERSION' ]
					  ];
					  
	/**store criterio*/
	NS.storeOperacion = new Ext.data.SimpleStore( {
			idProperty: 'idOperacion',  		
			fields : [ 
						{name :'idOperacion'}, 
						{name :'descripcion'}
					 ]
	});
	
	NS.storeOperacion.loadData(NS.dataOperacion);
	
	//combo tipo de operacion
	NS.cmbTipoOperacion = new Ext.form.ComboBox({
		store: NS.storeOperacion
		,name: PF+'cmbTipoOperacion'
		,id: PF+'cmbTipoOperacion'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
        ,x: 230
        ,y: 65
        ,width: 210
		,valueField:'idOperacion'
		,displayField:'descripcion'
		,autocomplete: true
		,emptyText: '--- Tipo de operacion ---'
		,triggerAction: 'all'
		,value: ''
		,visible: false
		,listeners:{
			select:{
				fn:function(combo, valor) {
				
				}
			}
		}
	});
	
	//store combo (banco) valor
	//FunSQLCombo369
	NS.storeValor = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{
			campoUno:'distinct b.id_banco',
			campoDos:'b.desc_banco',
			tabla:'cat_banco b, cat_cta_banco ccb',
			condicion:'b.id_banco = ccb.id_banco and b.nac_ext in (\'N\',\'E\')',
			orden:''
		},
		root: '',
		paramOrder:['campoUno','campoDos','tabla','condicion','orden'],
		root: '',
		directFn: TraspasosAction.llenarCombo, 
		idProperty: 'id', 
		fields: [
			 {name: 'id'},
			 {name: 'descripcion'},
		],
		listeners: {
			load: function(s, records){
				if(records.length==null || records.length<=0)
				{
					Ext.Msg.alert('SET','No hay bancos disponibles');
				}
			}
		}
	}); 
	NS.storeValor.load();
	
	//combo valor
	NS.cmbValor = new Ext.form.ComboBox({
		store: NS.storeValor
		,name: PF+'cmbValor'
		,id: PF+'cmbValor'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
        ,x: 0
        ,y: 65
        ,width: 190
		,valueField:'id'
		,displayField:'descripcion'
		,autocomplete: true
		,emptyText: 'Descripcion del Banco'
		,triggerAction: 'all'
		,value: ''
		,visible: false
		,listeners:{
			select:{
				fn:function(combo, valor) {
					
							
						
//				if(combo.getValue() === 21)
//					Ext.getCmp(PF+'optTipo')[2].setDisabled(false);
//				else
//					Ext.getCmp(PF+'optTipo')[2].setDisabled(true);
				
//				    //optInter.Value = True
//				    NS.optInter = true;
//				    NS.id_banco = combo.getValue();
//				    if(NS.id_banco == NS.BANCOMER && Ext.getCmp(PF+'cmbDivisa').getValue() !== 'MN')
//				    {
//				        //optmismo.Value = True
//				        NS.optMismo;
//				        NS.contenedorEjecutaTraspasos.getForm().Ext.getCmp(PF+'groupTipo').Ext.getCmp(PF+'optTipo')[3].disabled=true;
//				        OptInternacional.Enabled = True
//				        optInter.Enabled = False
//			        }
//			        else if(NS.id_banco == NS.BANCOMER && Ext.getCmp(PF+'cmbDivisa').getValue() === 'MN')
//			        {
//				        //optInter.Value = True
//				        NS.optInter = true;
//				        NS.contenedorEjecutaTraspasos.getForm().Ext.getCmp(PF+'groupTipo').Ext.getCmp(PF+'optTipo')[3].disabled=false;
//				        //optInter.Enabled = True
//			        }
//				    else
//				    {
//				        optInter.Value = True
//				        NS.optInter = true;
//				        OptInternacional.Enabled = False
//				        optInter.Enabled = True
//				    }
//				    Call cmbValor_Validate(True)
					
				}
			}
		}
	});
	
	//store combo divisa
	NS.storeDivisa = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{
		},
		root: '',
		paramOrder:[],
		directFn: TraspasosAction.llenarComboDivisa, 
		idProperty: 'idDivisa', 
		fields: [
			 {name: 'idDivisa'},
			 {name: 'descDivisa'},
		],
		listeners: {
			load: function(s, records){
				if(records.length==null || records.length<=0)
				{
					Ext.Msg.alert('SET','No hay divisas disponibles');
				}
			}
		}
	}); 
	NS.storeDivisa.load();
	
	NS.accionarCmbDivisa = function(comboValue)
	{
		Ext.getCmp(PF+'txtTipoCambio').setValue('1.0000');
		Ext.getCmp(PF+'txtIdDivisaPago').setValue(comboValue);
		Ext.getCmp(PF+'cmbDivisaPago').setValue(comboValue);
	}
	
	//combo divisa
	NS.cmbDivisa = new Ext.form.ComboBox({
		store: NS.storeDivisa
		,name: PF+'cmbDivisa'
		,id: PF+'cmbDivisa'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
        ,x: 50
        ,y: 115
        ,width: 140
		,valueField:'idDivisa'
		,displayField:'descDivisa'
		,autocomplete: true
		,emptyText: 'Seleccione la divisa'
		,triggerAction: 'all'
		,value: ''
		,visible: false
		,listeners:{
			select:{
				fn:function(combo, valor) {
					
					BFwrk.Util.updateComboToTextField(PF+'txtIdDivisa',NS.cmbDivisa.getId());
					NS.accionarCmbDivisa(combo.getValue());
					
					 		
				}
			}
		}
	});
//	Call gobjVarGlobal.LlenaComboRst(cmbDivisaPago, gobjSQL.FunSQLCombo3(), lmat_divisaPago)

	//combo divisaPago
		NS.cmbDivisaPago = new Ext.form.ComboBox({
			store: NS.storeDivisa
			,name: PF+'cmbDivisaPago'
			,id: PF+'cmbDivisaPago'
			,typeAhead: true
			,mode: 'local'
			,minChars: 0
			,selecOnFocus: true
			,forceSelection: true
	        ,x: 50
	        ,y: 210
	        ,width: 140
			,valueField:'idDivisa'
			,displayField:'descDivisa'
			,autocomplete: true
			,emptyText: 'Seleccione la divisa'
			,triggerAction: 'all'
			,value: ''
			,visible: false
			,hidden: true
			,listeners:{
				select:{
					fn:function(combo, valor) {
						Ext.getCmp(PF+'txtIdDivisaPago').setValue(combo.getValue());
					}
				}
			}
		});
	
	//store combo clave
	NS.storeClave = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams: {criterios: NS.criterios},
		paramOrder:['criterios'],
		root: '',
		directFn: TraspasosAction.llenarComboClave, 
		idProperty: 'cveControl', 
		fields: [
			 {name: 'cveControl'},
			 {name: 'cveControl'},
		],
		listeners: {
			load: function(s, records){
				
			}
		}
	}); 
	
	//combo valor
	NS.cmbClave = new Ext.form.ComboBox({
		store: NS.storeClave
		,name: PF+'cmbClave'
		,id: PF+'cmbClave'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
        ,x: 210
        ,y: 160
        ,width: 180
		,valueField:'cveControl'
		,displayField:'cveControl'
		,autocomplete: true
		,emptyText: 'clave control'
		,triggerAction: 'all'
		,value: ''
		,hidden: true
		,disabled: true
		,listeners:{
			select:{
				fn:function(combo, valor) {
				}
			}
		}
	});
	
	//store combo beneficiarios
	NS.storeBeneficiarios = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{
		},
		root: '',
		paramOrder:[],
		directFn: TraspasosAction.llenarComboDivisa, 
		idProperty: 'idDivisa', 
		fields: [
			 {name: 'idDivisa'},
			 {name: 'descDivisa'},
		],
		listeners: {
			load: function(s, records){
				if(records.length==null || records.length<=0)
				{
					Ext.Msg.alert('SET','No hay divisas disponibles');
				}
			}
		}
	}); 
//	NS.storeBeneficiarios.load();
	
	//combo beneficiarios
	NS.cmbBeneficiarios = new Ext.form.ComboBox({
		store: NS.storeBeneficiarios
		,name: PF+'cmbBeneficiarios'
		,id: PF+'cmbBeneficiarios'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
		,hidden: true
        ,x: 0
        ,y: 160
        ,width: 190
		,valueField:'idDivisa'
		,displayField:'descDivisa'
		,autocomplete: true
		,emptyText: '---- Datos no agrupados ----'
		,triggerAction: 'all'
		,value: ''
		,disabled: true
		,listeners:{
			select:{
				fn:function(combo, valor) {
					Ext.getCmp(PF+'cmbClave').setDisabled(true);
				}
			}
		}
	});
	
	//store del grid consulta
	NS.storeGridConsulta = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams: {criterios: NS.criterios},
		paramOrder:['criterios'],
		directFn: TraspasosAction.consultarSolicitudesTraspaso,
		root: '',
		idProperty: 'criterio',
		fields: [
			{name: 'descObservacion'},
			{name: 'nomEmpresa'},
			{name: 'noEmpresa'},
			{name: 'noEmpresaCxp'},
			{name: 'noEmpresaBenef'},
			{name: 'clabeBenef'},
			{name: 'idBanco'},
			{name: 'idBancoBenef'},
			{name: 'secuencia'},
			{name: 'descBanco'},
			{name: 'idChequera'},
			{name: 'fecValor'},
			{name: 'fechaPropuesta'},
			{name: 'sucursalDestino'},
			{name: 'folioBanco'},
			{name: 'referencia'},
			{name: 'idDivisa'},
			{name: 'importe'},
			{name: 'conceptoSet'},
			{name: 'observacion'},
			{name: 'cargoAbono'},
			{name: 'importeCa'},
			{name: 'noFolioDet'},
			{name: 'ejecutado'},
			{name: 'fecAlta'},
			{name: 'bTraspBanco'},
			{name: 'bTraspconta'},
			{name: 'noDocto'},
			{name: 'fecOperacion'},
			{name: 'beneficiario'},
			{name: 'descBancoBenef'},
			{name: 'concepto'},
			{name: 'idChequera'},
			{name: 'idChequeraBenef'},
			{name: 'origenMov'},
			{name: 'bLayoutComerica'},
			{name: 'habilitado'},
			{name: 'idTipoOperacion'},
			{name: 'descUsuarioBital'},
			{name: 'descServicioBital'},
			{name: 'idContratoMass'},
			{name: 'loteEntrada'},
			{name: 'plazaBenef'},
			{name: 'sucursalDestino'},
			{name: 'instFinan'},
			{name: 'noFolioMov'},
			{name: 'usr1'},
			{name: 'usr2'},
			{name: 'color'},
			{name: 'usrUno'},
			{name: 'usrDos'},
			{name: 'rfcBenef'},
			{name: 'abaBenef'},
			{name: 'swiftBenef'}
		],
		listeners: {
			load: function(s, records){
				NS.montoTotal = 0;
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeGridConsulta, msg:"Buscando..."});
				
				if(records==null || records.length==null || records.length<=0)
				{
					Ext.getCmp(PF + 'cmdGrafica').setDisabled(true);
					//Ext.getCmp('panel1').update('<img src="'+window.location.protocol+'//'+window.location.host+'/graficaSet/back.png" border="0"/>');
					//Ext.getCmp('panel2').update('<img src="'+window.location.protocol+'//'+window.location.host+'/graficaSet/back.png" border="0"/>');
					//Ext.getCmp('panel3').update('<img src="'+window.location.protocol+'//'+window.location.host+'/graficaSet/back.png" border="0"/>');
					Ext.Msg.alert('SET','No hay solicitudes de traspaso pendientes!');
				}
				else
				{
          			NS.sm.selectRange(0,records.length-1);
          			var regSelec=NS.gridConsulta.getSelectionModel().getSelections();
          			
          			for(var j = 0; j < records.length; j=j+1)
          			{
          				NS.montoTotal = NS.montoTotal + records[j].get('importe');
          			}
          			Ext.getCmp(PF+'txtRegistrosSeleccionados').setValue(regSelec.length);
          			var totalTras=Math.round((NS.montoTotal)*100)/100;
          			Ext.getCmp(PF+'txtMontoTotal').setValue(NS.formatNumber(totalTras,'$'));
          			
          			//Graficas
		            Ext.getCmp(PF + 'cmdGrafica').setDisabled(false);
		            
		            sName = NS.criterios.Empresa + NS.criterios.Valor + NS.criterios.Divisa;
		            
		            var sAux = NS.criterios.fechaIni;
					sAux = sAux.replace("/", "");
					sName += sAux.replace("/", "");
					sAux = NS.criterios.fechaFin;
					sAux = sAux.replace("/", "");
					sName += sAux.replace("/", "");
				}
		
			}
		}
	}); 
	
		// crear grid
	NS.gridConsulta = new Ext.grid.GridPanel( {
		store : NS.storeGridConsulta,
		id : 'gridConsultaEjecTras'
		,cm: new Ext.grid.ColumnModel({
            defaults: {
                width: 120,
                value:true,
                sortable: true
            }	 
		,columns : [ 
				NS.sm,
				{
					header :'Nivel Uno',	
					width :70,	
					sortable :true,	
					dataIndex :'usr1',
					renderer: function (value, meta, record) {
			            meta.attr = 'style=' + record.get('color');
			            return value;
			        }
				},{
					header :'Nivel Dos',	
					width :70,	
					sortable :true,	
					dataIndex :'usr2',
					renderer: function (value, meta, record) {
			            meta.attr = 'style=' + record.get('color');
			            return value;
			        }
				},{
					header :'Empresa',
					width :120,
					sortable :true,
					hidden: true,
					dataIndex :'noEmpresa',
					renderer: function (value, meta, record) {
			            meta.attr = 'style=' + record.get('color');
			            return value;
			        }
				},{
					header :'Empresa',
					width :120,
					sortable :true,
					dataIndex :'nomEmpresa',
					renderer: function (value, meta, record) {
			            meta.attr = 'style=' + record.get('color');
			            return value;
			        }
				},{
					header :'Banco',
					width :120,
					sortable :true,
					dataIndex :'descBanco',
					renderer: function (value, meta, record) {
			            meta.attr = 'style=' + record.get('color');
			            return value;
			        }
				},{
					header :'Chequera',
					width :120,
					sortable :true,
					dataIndex :'idChequera',
					renderer: function (value, meta, record) {
			            meta.attr = 'style=' + record.get('color');
			            return value;
			        }
				},{
					header :'Importe',
					width :100,
					sortable :true,
					dataIndex :'importe',
					css: 'text-align:right;',
					renderer: function (value, meta, record) {
			            meta.attr = 'style=' + record.get('color');
			            return '$ ' + NS.formatNumber(value);
			        }
				},{
					header :'Fec. Operacion',	
					width :100,	
					sortable :true,	
					dataIndex :'fecOperacion',
					renderer: function (value, meta, record) {
			            meta.attr = 'style=' + record.get('color');
			            return value;
			        }
				},{
					header :'Beneficiario',
					width :150,
					sortable :true,
					dataIndex :'beneficiario',
					renderer: function (value, meta, record) {
			            meta.attr = 'style=' + record.get('color');
			            return value;
			        }
				},{
					header :'Empresa Benef.',
					width :120,
					sortable :true,
					hidden: true,
					dataIndex :'noEmpresaBenef',
					renderer: function (value, meta, record) {
			            meta.attr = 'style=' + record.get('color');
			            return value;
			        }
				},{
					header :'banco benef',
					width :80,
					sortable :true,
					hidden: true,
					dataIndex :'idBancoBenef',
					renderer: function (value, meta, record) {
			            meta.attr = 'style=' + record.get('color');
			            return value;
			        }
				},{
					header :'Banco Benef.',
					width :120,
					sortable :true,
					dataIndex :'descBancoBenef',
					renderer: function (value, meta, record) {
			            meta.attr = 'style=' + record.get('color');
			            return value;
			        }
				},{
					header :'Chequera Benef.',
					width :120,
					sortable :true,
					dataIndex :'idChequeraBenef',
					renderer: function (value, meta, record) {
			            meta.attr = 'style=' + record.get('color');
			            return value;
			        }
				},{
					header :'CLABE Benef.',
					width :120,
					sortable :true,
					dataIndex :'clabeBenef',
					renderer: function (value, meta, record) {
			            meta.attr = 'style=' + record.get('color');
			            return value;
			        }
				},{
					header :'No. Docto',	
					width :70,	
					sortable :true,	
					dataIndex :'noDocto',
					renderer: function (value, meta, record) {
			            meta.attr = 'style=' + record.get('color');
			            return value;
			        },
			        hidden: true
				},
				{
					header :'Divisa',
					width :80,
					sortable :true,
					dataIndex :'idDivisa',
					renderer: function (value, meta, record) {
			            meta.attr = 'style=' + record.get('color');
			            return value;
			        },
			        hidden: true
				},
				{
					header :'Concepto',
					width :150,
					sortable :true,
					dataIndex :'concepto',
					renderer: function (value, meta, record) {
			            meta.attr = 'style=' + record.get('color');
			            return value;
			        }
				},
				{
					header :'Sucursal',
					width :120,
					sortable :true,
					dataIndex :'sucursalDestino',
					renderer: function (value, meta, record) {
			            meta.attr = 'style=' + record.get('color');
			            return value;
			        },
			        hidden: true
				},
				{
					header :'Fec. Valor',
					width :100,
					sortable :true,
					dataIndex :'fecValor',
					renderer: function (value, meta, record) {
			            meta.attr = 'style=' + record.get('color');
			            return value;
			        },
			        hidden: true
				},
				{
					header :'Origen Mov.',
					width :120,
					sortable :true,
					dataIndex :'origenMov',
					renderer: function (value, meta, record) {
			            meta.attr = 'style=' + record.get('color');
			            return value;
			        },
			        hidden: true
				},
				{
					header :'Empresa CXP',
					width :120,
					sortable :true,
					dataIndex :'noEmpresaCXP',
					renderer: function (value, meta, record) {
			            meta.attr = 'style=' + record.get('color');
			            return value;
			        },
			        hidden: true
				},
				{
					header :'Layout comerica',
					width :80,
					sortable :true,
					hidden: true,
					dataIndex :'bLayoutComerica',
					renderer: function (value, meta, record) {
			            meta.attr = 'style=' + record.get('color');
			            return value;
			        }
				},
				{
					header :'Habilitado',
					width :80,
					sortable :true,
					hidden: true,
					dataIndex :'habilitado',
					renderer: function (value, meta, record) {
			            meta.attr = 'style=' + record.get('color');
			            return value;
			        }
				},
				{
					header :'fecha propuesta',
					width :80,
					sortable :true,
					hidden: true,
					dataIndex :'fechaPropuesta',
					renderer: function (value, meta, record) {
			            meta.attr = 'style=' + record.get('color');
			            return value;
			        }
				},
				{
					header :'tipo operacion',
					width :80,
					sortable :true,
					hidden: true,
					dataIndex :'idTipoOperacion',
					renderer: function (value, meta, record) {
			            meta.attr = 'style=' + record.get('color');
			            return value;
			        }
				},
				{
					header :'descUsuarioBital',
					width :80,
					sortable :true,
					hidden: true,
					dataIndex :'descUsuarioBital',
					renderer: function (value, meta, record) {
			            meta.attr = 'style=' + record.get('color');
			            return value;
			        }
				},
				{
					header :'descSerBital',
					width :80,
					sortable :true,
					hidden: true,
					dataIndex :'loteEntrada',
					renderer: function (value, meta, record) {
			            meta.attr = 'style=' + record.get('color');
			            return value;
			        }
				},
				{
					header :'lote entrada',
					width :80,
					sortable :true,
					hidden: true,
					dataIndex :'descServicioBital',
					renderer: function (value, meta, record) {
			            meta.attr = 'style=' + record.get('color');
			            return value;
			        }
				},
				{
					header :'contrato mass',
					width :80,
					sortable :true,
					hidden: true,
					dataIndex :'idContratoMass',
					renderer: function (value, meta, record) {
			            meta.attr = 'style=' + record.get('color');
			            return value;
			        }
				},
				{
					header :'folio det',
					width :80,
					sortable :true,
					hidden: true,
					dataIndex :'noFolioDet',
					renderer: function (value, meta, record) {
			            meta.attr = 'style=' + record.get('color');
			            return value;
			        }
				},
				{
					header :'plaza benef',
					width :80,
					sortable :true,
					hidden: true,
					dataIndex :'plazaBenef',
					renderer: function (value, meta, record) {
			            meta.attr = 'style=' + record.get('color');
			            return value;
			        }
				},
				{
					header :'suc destino',
					width :80,
					sortable :true,
					hidden: true,
					dataIndex :'sucursalDestino',
					renderer: function (value, meta, record) {
			            meta.attr = 'style=' + record.get('color');
			            return value;
			        }
				},
				{
					header :'Folio Mov',
					width :80,
					sortable :true,
					hidden: true,
					dataIndex :'noFolioMov',
					renderer: function (value, meta, record) {
			            meta.attr = 'style=' + record.get('color');
			            return value;
			        }
				},
				{
					header :'Aba',
					width :80,
					sortable :true,
					hidden: true,
					dataIndex :'abaBenef',
					renderer: function (value, meta, record) {
			            meta.attr = 'style=' + record.get('color');
			            return value;
			        }
				},
				{
					header :'Swift',
					width :80,
					sortable :true,
					hidden: true,
					dataIndex :'swiftBenef',
					renderer: function (value, meta, record) {
			            meta.attr = 'style=' + record.get('color');
			            return value;
			        }
				},
				{
					header :'RFC Benef.',
					width :80,
					sortable :true,
					hidden: true,
					dataIndex :'rfcBenef',
					renderer: function (value, meta, record) {
			            meta.attr = 'style=' + record.get('color');
			            return value;
			        }
				}
	  		]
	  	})
      	,sm: NS.sm
      	,columnLines: true
      	,frame:false
		,height :135
		,x :0
		,y :0
		,title :''
	  ,listeners:{	
			click:{
       		   	fn:function(e){	
							var regSeleccionados = NS.gridConsulta.getSelectionModel().getSelections();
		        			var regActuales=Ext.getCmp(PF+'txtRegistrosSeleccionados').getValue();
		        			var totalActual=Ext.getCmp(PF+'txtMontoTotal').getValue();
		        			var contImporte=0;
		        			
			        		Ext.getCmp(PF+'txtRegistrosSeleccionados').setValue(regSeleccionados.length);
			        				
		        			for(var i=0;i<regSeleccionados.length;i++)
		        			{
		        				contImporte=contImporte+regSeleccionados[i].get('importe');
		        			}
		        			var totalTras=Math.round((contImporte)*100)/100;
		          			Ext.getCmp(PF+'txtMontoTotal').setValue(NS.formatNumber(totalTras,'$'));
		        				//Ext.getCmp(PF+'txtMontoTotal').setValue(NS.formatNumber(contImporte));
   				}
			}
		}
	});
	
	NS.imprimeReporte = function(archivo, negativo, banca) {
		if(banca)
			strParams = '?nomReporte=detArchTraspInv';
		else {
			if(negativo)
				strParams = '?nomReporte=traspInvNo';
			else
				strParams = '?nomReporte=traspInv';
		}
		strParams += '&'+'nomParam1=archivo';
		strParams += '&'+'valParam1=' + archivo;
		strParams += '&'+'nomParam2=nomEmpresa';
		strParams += '&'+'valParam2=' + Ext.getCmp(PF + 'cmbEmpresaBusqueda').getValue();
		strParams += '&'+'nomParam3=referencia';
		strParams += '&'+'valParam3=' + "DETALLE DE ARCHIVO DE TRASPASOS";
		strParams += '&'+'nomParam4=fecHoy';
		strParams += '&'+'valParam4=' + apps.SET.FEC_HOY;
		
		window.open("/SET/jsp/bfrmwrk/gnrators/genJasperPDF.jsp" + strParams);
		return;
	};
	/////////////// contenedores de tipos de envios/////////////////////
	/*Radio Button Digitem, TEF (Transferencia Electronica de Fondos), Citibank WorldLink,
	Mass Payment Citibank, Flat File Citibank (Solo DLS), Pay Link Citibank (Solo MN),
	Citibank ACH Credit */
	NS.optEnvioBNMX = new Ext.form.RadioGroup({
		id: PF + 'optEnvioBNMX',
		name: PF + 'optEnvioBNMX',
		x: 10,
		columns: 1, //muestra los radiobuttons en una columna
		items: [
	          {boxLabel: 'Digitem', name: 'optBNM', inputValue: 0, checked: true},  
	          {boxLabel: 'TEF (Transferencia Electronica de Fondos)', name: 'optBNM', inputValue: 1, disabled: true},
	          {boxLabel: 'Citibank WorldLink', name: 'optBNM', inputValue: 2, disabled: true	},  
	          {boxLabel: 'Mass Payment Citibank', name: 'optBNM', hidden: true, inputValue: 3, disabled: true	},
	          {boxLabel: 'Flat File Citibank (Solo DLS)', name: 'optBNM', inputValue: 4, disabled: true	},  
	          {boxLabel: 'Pay Link Citibank (Solo MN)', name: 'optBNM', inputValue: 5, disabled: true	},
	          {boxLabel: 'Citibank ACH Credit', name: 'optBNM', inputValue: 6, disabled: true	}
	     ]
	});
	
	//Radio Button Conexion empresarial y host to host HSBC
	NS.optEnvioHSBC = new Ext.form.RadioGroup({
		id: PF + 'optEnvioHSBC',
		name: PF + 'optEnvioHSBC',
		x: 60,
		y: 30,
		columns: 1, //muestra los radiobuttons en una columna
		items: [
	          {boxLabel: 'Conexion Empresarial', name: 'optHSBC', inputValue: 0, checked: true},  
	          {boxLabel: 'Host to Host', name: 'optHSBC', inputValue: 1}
	     ]
	});
	/////////////////////// fin de contenedores de tipos de envios
	
	//////contenedor de tipo de envio banamex/////////
	NS.contEnvioBancos = new Ext.form.FieldSet({
		title: 'Tipos de Envio Bancos',
		id: PF + 'contEnvioBancos',
		width: 300,
		height: 240,
		x: 350,
		y: 0,
		layout: 'absolute',
		plain: false,
		bodyStyle: 'background:#89ACB9',
		hidden: true,
		items: [
		        NS.optEnvioBNMX,
		        NS.optEnvioHSBC,
		        {
                    xtype: 'button',
                    text: 'Continuar',
                    x: 50,
                    y: 170,
                    width: 80,
                    listeners: {
		        		click: {
		        			fn:function(e) {
		        				NS.ejecutar();
		        			}
		        		}
		        	}
		        },{
                    xtype: 'button',
                    text: 'Cancelar',
                    x: 150,
                    y: 170,
                    width: 80,
                    listeners: {
		        		click: {
		        			fn:function(e) {
		        				Ext.getCmp(PF + 'contEnvioBancos').setVisible(false);
		        			}
		        		}
		        	}
		        }
		        ]
	});
	
	///// fin de contenedor de de envion de banamex  /////////
	//// funcion de ejecutar////////////
	NS.ejecutar = function(){
		
		var regSelec=NS.gridConsulta.getSelectionModel().getSelections();
	   	var recordsGrid=NS.storeGridConsulta.data.items;
		NS.lbDesmarcados = false;
		NS.bCambioDivisa = false;
		var optValorBNMX = 0;
		var optValorHSBC = 0;
		
		if(regSelec.length > 0) {
			if(!banderaEnvio){
				winTipoEnvio.show();
				return;
			}
			else{
			if(NS.LI_BANCO == 2) {
				var optEnvioBNMX = Ext.getCmp(PF + 'optEnvioBNMX').getValue();
				optValorBNMX = parseInt(optEnvioBNMX.getGroupValue());
			}else if(NS.LI_BANCO == 21) {
				var envioHSBC = Ext.getCmp(PF + 'optEnvioHSBC').getValue();
				optValorHSBC = parseInt(envioHSBC.getGroupValue());
			}
			for(var j = 0; j < regSelec.length; j=j+1){
  				if(regSelec[j].get('habilitado') === 'N' && cambiarFecha(''+regSelec[j].get('fechaPropuesta')) != NS.fechaHoy){
	                NS.lbDesmarcados = true;
	            }
  			}
			if(NS.lbDesmarcados == true){
		         if(!confirm('Se desmarcaron Traspasos que no se pueden ejecutar el día de hoy,'
		         +' ¿desea continuar?'))
					return;
	         }

			for(var i = 1; i < regSelec.length; i ++) {
	            if(regSelec[i].get('idTipoOperacion') == 3809 && cambiarFecha(''+ regSelec[i].get('fecOperacion')) > NS.fechaHoy)
	               if(!confirm('Esta seguro de ejecutar el pago interempresa ' +
		                  '  la fecha de vencimiento no es de hoy'))
		            	return;
		    }
		    //validacion para pago cruzado
		    if(recordsGrid.length > 1){
		    	if(Ext.getCmp(PF+'cmbDivisa').getValue() !== Ext.getCmp(PF+'cmbDivisaPago').getValue()){
		            if(confirm('Todos los movimientos seleccionados serán cambiados de divisa'+
		                      '¿Desea Continuar?')){
		                NS.bCambioDivisa = true;
					    //'debe haber un tipo de cambio capturado
		                if(Ext.getCmp(PF+'txtTipoCambio').getValue() === ''){
		                    Ext.Msg.alert('SET','Tipo de Cambio Incorrecto para traspaso cruzado');
		                    return;
	                    }
	                }
		            else
		                return;
	            }
	        }
        //validacion para pago agrupado
	        if(NS.bCambioDivisa == false) {
//							            'pago agrupado
				if(Ext.getCmp(PF+'cmbBeneficiarios').getValue()!= '' && Ext.getCmp(PF+'cmbTipoOperacion').getValue() === '')
				{
	                Ext.Msg.alert('SET','Si hace pagos agrupados la busqueda debe'+
	                       ' ser tambien por Tipo de operación');
	                return;
                }
	            else if(regSelec.length == 1 && Ext.getCmp(PF+'cmbBeneficiarios').getValue() != '')
	            {
	                Ext.Msg.alert('SET','No se puede agrupar un solo registro');
	                return;
                }
	            else if(Ext.getCmp(PF+'cmbBeneficiarios').getValue() != '' &&  Ext.getCmp(PF+'cmbTipoOperacion').getValue() != '')
	            {
//							                ' checa que el movimiento tenga el mismo origen y destino (chequeras)
//							                ' checando chequeras checamos todo (empresa y banco)
	                var sTempChequera = '';
	                for(var i = 1; i < regSelec.length; i++)
	                {
//								                For i = 1 To msfDatos.Rows - 1
                        if(sTempChequera === '')
                            sTempChequera = regSelec[i].get('idChequera');
                        else
                        {
                            if(sTempChequera != regSelec[i].get('idChequera'))
                            {
                                Ext.Msg.alert('SET','Los movimientos no se pueden agrupar por que '+
                                      ' no tienen la misma chequera origen');
                                return;
                            }
                        }
	                }
	                var sTempChequerab = '';
	                for(var i = 1; i < regSelec.length; i++)
	                {
                        if(sTempChequerab = '')
                            sTempChequerab = regSelec[i].get('idChequeraBenef');
                        else
                        {
                            if(sTempChequerab != regSelec[i].get('idChequeraBenef'))
                            {
                                Ext.Msg.alert('SET','Los movimientos no se pueden agrupar por que '+
                                      ' no tienen la misma chequera destino');
                                return;
                            }
                        }
	                }
	            }
        	}
	    	//envio de parametros
	    	BFwrk.Util.msgWait('Ejecutando...', true);
	    	var matriz = new Array ();
	    	for(var i=0;i<regSelec.length;i++)
	       	{
	    		if(regSelec[i].get('idBancoBenef') != Ext.getCmp(PF+'cmbValor').getValue() && 
	    				(regSelec[i].get('clabeBenef') == null || regSelec[i].get('clabeBenef') == '')){
	    			Ext.Msg.alert('SET','No se puede continuar. El registro ' + (i + 1) + ' no tiene cuenta clabe.');
	    		}
	       		var parametro = {};
	       		//criterios de busqueda
	       		parametro.cambioDivisa = NS.bCambioDivisa;
	       		parametro.opcionMismoBanco = NS.optMismo;
				parametro.opcionEmpresaActual = NS.optActual;
				parametro.opcionSpeua = NS.optSpeua;
				parametro.opcionInternacional = NS.optInternacional;
				parametro.opcionInversion = NS.chkInversion;
				parametro.opcionInterbancaria = NS.optInter;
				//opciones del frame tipos de envio HSBC
				parametro.opcionBitalH2H = false;
				parametro.opcionBitalNormal = false;
				//opciones del frame tipos de envio banamex
				parametro.opcionBanamexNormal = false;
				parametro.opcionBanamexTEF = false;
				parametro.opcionBanamexMassPayment = false;
				parametro.opcionCitiBankFlatFile = false;
				parametro.opcionCitiBankPaylinkMN = false;
				parametro.opcionCitiBankACH = false;
				parametro.banco = Ext.getCmp(PF+'cmbValor').getValue();
				parametro.divisa = Ext.getCmp(PF+'cmbDivisa').getValue();
				parametro.idTipoOperacion = NS.iOperacion;
				parametro.DescOperacion = NS.DescOperacion;
				parametro.tipoCambio = '1.0000';
				parametro.usuario = 2;
				//datos del grid
	       		parametro.fechaOperacion = regSelec[i].get('fecOperacion');
	       		parametro.usuarioBital = regSelec[i].get('descUsuarioBital');
	       		parametro.servicioBital = regSelec[i].get('descServicioBital');
	       		parametro.noEmpresa = regSelec[i].get('noEmpresa');
	       		parametro.contratoMass = regSelec[i].get('idContratoMass');
	       		parametro.layoutComerica = regSelec[i].get('bLayoutComerica');
	       		parametro.noEmBenef = regSelec[i].get('noEmpresaBenef');
	       		parametro.loteE = regSelec[i].get('loteEntrada');
	       		parametro.instFinan = regSelec[i].get('instFinan');
	       		parametro.idBanBenef = regSelec[i].get('idBancoBenef');
	       		parametro.noFolioDet = regSelec[i].get('noFolioDet');
	       		parametro.importe = regSelec[i].get('importe');
	       		parametro.documento = regSelec[i].get('noDocto');
	       		parametro.plazaB = regSelec[i].get('plazaBenef');
	       		parametro.chequeraB = regSelec[i].get('idChequeraBenef');
	       		parametro.idChequera = regSelec[i].get('idChequera');
	       		parametro.beneficiario = regSelec[i].get('beneficiario');
	       		parametro.sucDestino = regSelec[i].get('sucursalDestino');
	       		parametro.idDivisa = regSelec[i].get('idDivisa');
	       		parametro.cliente = regSelec[i].get('noEmpresaBenef');
	       		parametro.clabeBenef = regSelec[i].get('clabeBenef');
	       		parametro.concepto = regSelec[i].get('concepto');
	       		parametro.rfcBenef = regSelec[i].get('rfcBenef');
	       		parametro.noDocto = '';
	       		parametro.aba= regSelec[i].get('abaBenef');
	       		parametro.swift= regSelec[i].get('swiftBenef');
	       		parametro.nomEmpresa = regSelec[i].get('nomEmpresa');
	    		if (NS.optTipoEnvioH2H.getValue() != null && NS.optTipoEnvioH2H.getValue().getGroupValue() == 0) {
	    			parametro.chkH2HSantander = true;			
	    			NS.chkH2HSantander = true;
	    		} else {
	    			parametro.chkH2HSantander = false;
	    			NS.chkH2HSantander = false;
	    		}
	       		//parametro.chkH2HSantander = NS.chkH2HSantander;
	       		matriz[i] = parametro;
	   		}
	   		var jsonString = Ext.util.JSON.encode(matriz);	
	   		
	   		TraspasosAction.ejecutarSolicitudes(jsonString,optValorBNMX, function(result, e) {
				BFwrk.Util.msgWait('Ejecutando...', false);
				
				if(result.msgSalio != null && result.msgSalio != '' && result.msgSalio != undefined)
					Ext.Msg.alert('SET', result.msgUsuario + '!!');
				else {
					if(result.msgUsuario != null && result.msgUsuario != '' && result.msgUsuario != undefined) {
						winTipoEnvio.hide();
						banderaEnvio=false;
						if(result.msgUsuario=='No se realizó ninguna tarea'){
							Ext.Msg.alert('SET','Los Registros han sido Guardados en el archivo'+ result.resNombreArchivoLocal + '!!');
						}
						
						NS.storeGridConsulta.removeAll();
	      			   	NS.gridConsulta.getView().refresh();
	      			   	NS.contenedorEjecutaTraspasos.getForm().reset();
	      			   	NS.montoTotal = 0;
	  			   		Ext.getCmp(PF + 'txtNoEmpresaBusqueda').setValue('0');
	      			   	if(result.resExitoLocal == true ){
	      			   		if( NS.LI_BANCO != 30&&result.archivo.charAt(0)!='t'){
		    					strParams = '?nomReporte=layouts';
		    					strParams += '&'+'nomParam1=rutaArchivo';
		    					strParams += '&'+'valParam1='+result.resRutaLocal+result.resNombreArchivoLocal;
		    					strParams += '&'+'nomParam2=nombreArchivo';
		    					strParams += '&'+'valParam2='+result.resNombreArchivoLocal;
		    					window.open("/SET/jsp/bfrmwrk/gnrators/abretxt.jsp"+strParams);
	      			   		}
	    					NS.imprimeReporte(result.archivo, false, true);
	    				}else{
	    					Ext.Msg.alert('SET', 'Error desconocido. <br>'+result.msgUsuario);
	    				}	
					}
				}
			});
			}
		}else {
			Ext.Msg.alert('SET','Debe seleccionar al menos un registro!');
			return;
		}

	}
	//// fin funcion de ejecutar////////////
	NS.btnEjecutar = apps.SET.btnFacultativo(new Ext.Button({
            text: 'Ejecutar',
            id:'trejEjecutar',
            name:'trejEjecutar',
            x: 490,
            y: 210,
            width: 70,
            listeners:{
          		click:{
      			   fn:function(e){
      				  
						/////// contenedor de city grup
						if(NS.LI_BANCO == 2 ||NS.LI_BANCO == 1026) {
							
        					Ext.getCmp(PF + 'contEnvioBancos').setVisible(true);
        					Ext.getCmp(PF + 'contEnvioBancos').setTitle('Tipos de Envio Banamex');
        					Ext.getCmp(PF + 'optEnvioHSBC').setVisible(false);
        					Ext.getCmp(PF + 'optEnvioBNMX').setVisible(true);
        				}else if(NS.LI_BANCO!=14){
        					banderaEnvio=true;
							NS.ejecutar();
        				}
        				else{
        					banderaEnvio=false;
        					NS.ejecutar();
        				}					
      			   }
  			   	}
            }
        }));	
	
	NS.btnEliminar = apps.SET.btnFacultativo(new Ext.Button({
		 
             
             text: 'Eliminar',
             id:'trejEliminar',
             name:'trejEliminar',
             x: 650,
             y: 210,
             width: 70,
             listeners:{
           		click:{
       			   fn:function(e){
         				var registroSeleccionado = NS.gridConsulta.getSelectionModel().getSelections();
         				if (registroSeleccionado.length <= 0)
         					Ext.Msg.alert('SET', 'Debe de seleccionar al menos un registro para eliminar');
         				else{
         					Ext.Msg.confirm('SET', '¿Esta seguro de cancelar los Traspasos?', function(btn){
         						if (btn === 'yes'){
         							NS.cancelaSolicitud();
         						}                            							
         					});
         				}                           				
       			   }
   			   	}
			   	}
        
	}));
		//formulario
	NS.contenedorEjecutaTraspasos =new Ext.FormPanel({
    title: 'Ejecuci&#243;n de Traspasos',
    width: 1300,
    height: 706,
    frame: true,
    autoScroll: true,
    padding: 10,
    layout: 'absolute',
    renderTo: NS.tabContId, 
     	items: [
            {
                xtype: 'fieldset',
                title: '',
                x: 10,
                y: 0,
                width: 1010,
                height: 490,
                layout: 'absolute',                
                items: [
                	{
                        xtype: 'label',
                        text: 'Empresa:',
                        x: 10,
                        y: 10,
                        hidden: true                  
                    },
                    {
                        xtype: 'label',
                        text: 'Usuario:',
                        x: 310,
                        y: 10,
                        hidden: true                        
                    },
                    {
                        xtype: 'label',
                        text: 'Fecha:',
                        x: 600,
                        y: 10,
                        hidden: true
                    },
                    {
                        xtype: 'textfield',
                        value: NS.GS_DESC_EMPRESA,
                        x: 80,
                        y: 10,
                        width: 210,
                        readOnly: true,
                        hidden: true
                    },
                    {
                        xtype: 'textfield',
                        id: PF+'txtFechaHoy',
                        name: PF+'txtFechaHoy',
                        readOnly: true,
                        value: apps.SET.FEC_HOY,
                        x: 660,
                        y: 10,
                        width: 110,
                        hidden: true
                    },
                    {
                        xtype: 'textfield',
                        value: NS.desc_usuario,
                        x: 370,
                        y: 10,
                        width: 210,
                        readOnly: true,
                        hidden: true
                    },
                    {
                        xtype: 'fieldset',
                        title: 'B&#250;squeda',
                        width: 985,
                        height: 180,                        
                        layout: 'absolute',
                        x: 0,
                        y: 0,
                        items: [
                            NS.cmbValor,
                            NS.lblNoEmpresaBusqueda,
                            NS.txtNoEmpresaBusqueda,
                            NS.cmbEmpresaBusqueda,
                            {
                                xtype: 'checkbox',
                                x: 500,
                                y: 0,
                                boxLabel: 'H2H Santander',
                                name: PF+'chkH2HSantander',
                                id: PF+'chkH2HSantander',
                                hidden: true,
                             	listeners: {
                               		check: {
                               			fn:function(opt, valor) {
                               				if(valor == true) {
//                               					NS.storeEnvioTransGrid.baseParams.chkH2HSantander = true;
                               					NS.chkH2HSantander = 'true';
                               				}else {
//                               					NS.storeEnvioTransGrid.baseParams.chkH2HSantander = false;
                               					NS.chkH2HSantander = 'false';
                               				}
                               			}
                               		}
                               	}
                            },
                            {
                                xtype: 'label',
                                text: 'Banco:',
                                x: 0,
                                y: 50
                            },
                            {
                                xtype: 'label',
                                text: 'Divisa:',
                                x: 0,
                                y: 100
                            },
                            {
                                xtype: 'uppertextfield',
                                id: PF+'txtIdDivisa',
                                name: PF+'txtIdDivisa',
                                x: 0,
                                y: 115,
                                width: 40,
                                listeners:{
		                        	change:{
		                        		fn:function(caja, valor){
		                        			var comboValue = BFwrk.Util.updateTextFieldToCombo(PF+'txtIdDivisa',NS.cmbDivisa.getId());
		                        			NS.accionarCmbDivisa(comboValue);
		                        		}
		                        	}
		                        }
                            },
                            NS.cmbDivisa,
                            {
                                xtype: 'checkbox',
                                id: PF+'chkInversion',
                                name: PF+'chkInversion',
                                hidden: false,
                                x: 480,
                                y: 20,
                                boxLabel: 'Traspaso de Inversi&#243;n',
                                hidden: true,
                                listeners:{
									check:{
										fn:function(opt, valor)
										{
											if(valor==true){
												NS.chkInversion = true;
												NS.chkTodos = false;
											}
										}
									}
								}
                            },
                            {
                                xtype: 'checkbox',
                                id: PF+'chkTodos',
                                name: PF+'chkTodos',
                                hidden: true,
                                x: 660,
                                y: 20,
                                boxLabel: 'Todos',
                                listeners:{
                                	check:{
										fn:function(opt, valor)
										{
											if(valor==true){
												NS.chkInversion = false;
												NS.chkTodos = true;
											}
										}
									}
								}
                            },
                            {
                                xtype: 'label',
                                text: 'Fecha:',
                                x: 230,
                                y: 100
                            },
                            {
                                xtype: 'label',
                                text: 'Tipo de operacion:',
                                x: 230,
                                y: 50
                            },
                            NS.cmbTipoOperacion,
                            {
                                xtype: 'datefield',
                                format: 'd/m/Y',
                                id: PF+'txtFechaIni',
                                name: PF+'txtFechaIni',
                                value: apps.SET.FEC_HOY,
                                x: 200,
                                y: 115,
                                width: 100,
                                listeners:{
                                	change:{
                                		fn:function(caja, valor){
                                			var fechaFin = Ext.getCmp(PF + 'txtFechaFin').getValue();
                                			
                                			if(fechaFin < caja.getValue()){
                                				Ext.Msg.alert('SET', 'La fecha inicial no puede ser mayor a la fecha final!!');
                                				Ext.getCmp(PF + 'txtFechaFin').setValue('');
                                			}
                        				}
                        			}
                        		}
                            },
                            {
                                xtype: 'datefield',
                                format: 'd/m/Y',
                                id: PF+'txtFechaFin',
                                name: PF+'txtFechaFin',
                                value: apps.SET.FEC_HOY,
                                x: 340,
                                y: 115,
                                width: 100,
                                listeners:{
                                	change:{
                                		fn:function(caja, valor){
                                			var fechaIni = Ext.getCmp(PF + 'txtFechaIni').getValue();
                                			
                                			if(fechaIni > caja.getValue()){
                                				Ext.Msg.alert('SET', 'La fecha inicial no puede ser mayor');
                                				Ext.getCmp(PF + 'txtFechaIni').setValue('');
                                			}
                        				}
                        			}
                        		}
                            },
                            {
                                xtype: 'fieldset',
                                title: 'Empresa',
                                x: 460,
                                y: 0,
                                layout: 'absolute',
                                width: 190,
                                height: 60,
                                hidden: true,
                                items: [
                               	{
	                                xtype: 'radiogroup',
						            columns: 2,
						            items: [
	                                    {
	                                        xtype: 'radio',
	                                        name: PF+'optEmpresa',
	                        				inputValue: 0,
	                                        x: 10,
	                                        y: 0,
	                                        boxLabel: 'Actual',
	                                        checked: true,
	                                        listeners:{
												check:{
													fn:function(opt, valor)
													{
														if(valor==true){
															NS.optActual = true;
															NS.optTodas = false;
														}
													}
												}
											}
	                                    },
	                                    {
	                                        xtype: 'radio',
	                                        name: PF+'optEmpresa',
	                        				inputValue: 1,
	                                        x: 100,
	                                        y: 0,
	                                        boxLabel: 'Todas',
	                                        listeners:{
												check:{
													fn:function(opt, valor)
													{
														if(valor==true){
															NS.optActual = false;
															NS.optTodas = true;
														}
													}
												}
											}
	                                    }
                                    ]
                                   }
                                ]
                            },
                            {
                                xtype: 'fieldset',
                                title: '',
                                x: 480,
                                y: 50,
                                width: 265,
                                height: 80,
                                layout: 'absolute',
                                items: [
                                {
                                	xtype: 'radiogroup',
                                	id: PF+'groupTipo',
						            columns: 2,
						            items: [
	                                    {
	                                        xtype: 'radio',
	                                        id: PF+'optTipo0',
	                                        name: PF+'optTipo',
	                        				inputValue: 0,
	                                        x: 5,
	                                        y: 0,
	                                        boxLabel: 'Interbancaria',
	                                        checked: true
	                                    },
	                                    {
	                                        xtype: 'radio',
	                                        id: PF+'optTipo1',
	                                        name: PF+'optTipo',
	                        				inputValue: 1,
	                                        x: 130,
	                                        y: 0,
	                                        boxLabel: 'Mismo Banco'
	                                    },
	                                    {
	                                        xtype: 'radio',
	                                        id: PF+'optTipo2',
	                                        name: PF+'optTipo',
	                        				inputValue: 2,
	                                        x: 5,
	                                        y: 30,
	                                        hidden: true,
	                                        boxLabel: 'SPEI',
	                                        disabled: true
	                                    },
	                                    {
	                                        xtype: 'radio',
	                                        id: PF+'optTipo3',
	                                        name: PF+'optTipo',
	                        				inputValue: 3,
	                                        x: 130,
	                                        y: 30,
	                                        boxLabel: 'Internacional',
	                                        hidden: false,
	                                        disabled: false				
	                                    }
                                    ]
                                   }
                                ]
                            },
                            {
                                xtype: 'button',
                                text: 'Grafica',
                                disabled: 'true',
                                hidden: true,
                                x: 775,
                                y: 60,
                                width: 65,
                                name : PF + 'cmdGrafica',
                                id : PF + 'cmdGrafica',
                                listeners : {
                                	click : {
                                		fn : function(e)
                                		{
			                            	Ext.getCmp('panel3').show();
			        						Ext.getCmp('panel2').show();
			        						Ext.getCmp('panel1').show();
			        						Ext.getCmp('panel1').update('<img src="'+window.location.protocol+"//"+window.location.host+"/graficaSet/"+apps.SET.iUserId+'EjecucionTraspasos'+sName+'PC.jpg" border="0"/>');
											Ext.getCmp('panel2').update('<img src="'+window.location.protocol+"//"+window.location.host+"/graficaSet/"+apps.SET.iUserId+'EjecucionTraspasos'+sName+'BG.jpg" border="0"/>');
											Ext.getCmp('panel3').update('<img src="'+window.location.protocol+"//"+window.location.host+"/graficaSet/"+apps.SET.iUserId+'EjecucionTraspasos'+sName+'LG.jpg" border="0"/>');
											Ext.Msg.alert('SET', 'Graficas listas');
                                		}
                                	}
                                }
                            },
                            {
                                xtype: 'button',
                                text: 'Buscar',
                                x: 850,
                                y: 60,
                                width: 65,
                                listeners: {
	                                click:{
		                                fn: function(e){
                            				NS.buscar();
		                                }
	                                }
                                }
                            }
                        ]
                    },
                    {
                        xtype: 'fieldset',
                        title: 'Solicitudes Pendientes',
                        x: 0,
                        y: 195,
                        width: 985,
                        height: 270,
                        layout: 'absolute',
                        items: [
                        NS.gridConsulta,
                            {
                                xtype: 'label',
                                text: 'Clave de propuesta:',
                                hidden: true,
                                x: 210,
                                y: 140
                            },
                            {
                                xtype: 'label',
                                text: 'Beneficiario para grupo:',
                                x: 0,
                                hidden: true,
                                y: 140
                            },
                            NS.cmbBeneficiarios,
                            NS.cmbClave,
                            {
                                xtype: 'label',
                                text: 'Divisa de Pago:',
                                x: 0,
                                hidden: true,
                                y: 190
                            },
                            {
                                xtype: 'textfield',
                                id: PF+'txtIdDivisaPago',
                                name: PF+'txtIdDivisaPago',
                                x: 0,
                                hidden: true,
                                y: 210,
                                width: 40
                            },
                            NS.cmbDivisaPago,
                            {
                                xtype: 'label',
                                text: 'Tipo de Cambio:',
                                x: 210,
                                hidden: true,
                                y: 190
                            },
                            {
                                xtype: 'textfield',
                                id: PF+'txtTipoCambio',
                                name: PF+'txtTipoCambio',
                                x: 210,
                                hidden: true,
                                y: 210,
                                width: 110
                            },
                            {
                                xtype: 'label',
                                text: 'Monto Total:',
                                x: 140,
                                y: 140
                            },
                            {
                                xtype: 'label',
                                text: 'Total de Registros:',
                                x: 0,
                                y: 140
                            },
                            {
                                xtype: 'textfield',
                                id: PF+'txtRegistrosSeleccionados',
                                name: PF+'txtRegistrosSeleccionados',
                                readOnly: true,
                                x: 0,
                                y: 160,
                                width: 100
                            },
                            {
                                xtype: 'textfield',
                                id: PF+'txtMontoTotal',
                                name: PF+'txtMontoTotal',
                                readOnly: true,
                                x: 140,
                                y: 160,
                                width: 100
                            },
                            {
                                xtype: 'button',
                                text: 'Traspaso Parcial',
                                id:PF+'cmdPagoParcial',
                                name:PF+'cmdPagoParcial',
                                x: 380,
                                y: 210,
                                width: 100,
                                disabled: true,
                                hidden: true,
                                listeners:{
			                  		click:{
			              			   fn:function(e){
			              			   //Ext.Msg.alert('SET', 'Boton en construccion');
			              			   }
		              			   	}
	              			   	}
                            },
                            NS.btnEjecutar,
                            {
                                xtype: 'button',
                                text: 'Regresar',
                                id:PF+'cmdRegresar',
                                name:PF+'cmdRegresar',
                                x: 570,
                                y: 210,
                                width: 70,
                                hidden: true,
                                listeners:{
			                  		click:{
			              			   fn:function(e){
			              			   //Ext.Msg.alert('SET', 'Boton en construccion');
			              			   }
		              			   	}
	              			   	}
                            },
                           //eliminar 
                            NS.btnEliminar,
                            {
                                xtype: 'button',
                                text: 'Limpiar',
                                id: PF + 'btnLimpiar',
                                name: PF + 'btnLimpiar',
                                x: 750,
                                y: 210,
                                width: 70,
                                listeners:{
        	                  		click:{
        	              			   fn:function(e){
        	              			   	NS.contenedorEjecutaTraspasos.getForm().reset();
        	              			   	NS.storeGridConsulta.removeAll();
        	              			   	NS.gridConsulta.getView().refresh();
        	              			   	//NS.obtenerFechaHoy();
        	              			   	NS.montoTotal = 0;
        	              			   }
        	           			   }
        	       			   }
                            },{
                                xtype: 'button',
                                text: 'Autorizar',
                                x: 750,
                                y: 160,
                                hidden: true,
                                width: 80,
                                id: PF + 'btnAutorizar',
                                name: PF + 'btnAutorizar',
                                listeners: {
        		                	click: {
        		                		fn:function(e) {
                           					NS.autorizar();
        			                   	}
                           			}
                           		}
                           	},{
                                xtype: 'button',
                                text: 'Quitar Autorización',
                                x: 850,
                                y: 160,
                                hidden: true,
                                width: 80,
                                id: PF+'cmbQuitAut',
                                name: PF+'cmbQuitAut',
                                listeners: {
	        	                	click: {
	        	                		fn:function(e) {
	        	                			NS.desAutorizar();
	        	                		}
	        	                	}
                   			 	}
                            },
                            NS.contEnvioBancos
                        ]
                    }
                ]
            }
        ]
    });
    
    function cambiarFecha(fecha)
	{	
		var mesArreglo=new Array(11);
		mesArreglo[0]="Jan";mesArreglo[1]="Feb";mesArreglo[2]="Mar";mesArreglo[3]="Apr";
		mesArreglo[4]="May";mesArreglo[5]="Jun";mesArreglo[6]="Jul";mesArreglo[7]="Aug";
		mesArreglo[8]="Sep";mesArreglo[9]="Oct";mesArreglo[10]="Nov";mesArreglo[11]="Dec";
		var mesDate=fecha.substring(4,7);
		var dia=fecha.substring(8,10);
		var anio=fecha.substring(11,15);
		for(var i=0;i<12;i=i+1)
		{
			if(mesArreglo[i]===mesDate)
			{
			var mes=i+1;
			if(mes<10)
				var mes='0'+mes;
			}
		}
		var fechaString=''+dia+'/'+mes+'/'+anio;
		return fechaString;
	}
	
	//Formato de un numero a monetario
	NS.formatNumber = function(num,prefix){
	
		num = num.toString();
		if(num.indexOf('.')>-1){
			if(num.substring(num.indexOf('.')).length<3){
				num = num + '0';
			}
		}
		else{
			num = num + '.00';
		}
		prefix = prefix || '';
		var splitStr = num.split('.');
		var splitLeft = splitStr[0];
		var splitRight = splitStr.length > 1 ? '.' + splitStr[1] : '';
		var regx = /(\d+)(\d{3})/;
		while (regx.test(splitLeft)) {
			splitLeft = splitLeft.replace(regx, '$1' + ',' + '$2');
		}
		return prefix + splitLeft + splitRight;
	};
	
//	NS.validaFacultades = function() {
//		//139 EQUIVALE AL INDICE QUE LE CORRESPONDE A LA FACULTAD DE EJECUCION DE TRASPASOS
//		TraspasosAction.validaFacultad(139, function(result, e) {
//			
//			if(result == 1)
//				Ext.getCmp(PF + 'cmdAceptar').setDisabled(false);
//		});
//	};
//	NS.validaFacultades();
	
	NS.autorizar = function() {
		TraspasosAction.validaFacultadt(140, function(result, e) {
			if(result == 0) {
				Ext.Msg.alert('SET', 'No cuenta con la facultad para autorizar traspasos!!');
				return;
			}
			var datoAutorizar = NS.gridConsulta.getSelectionModel().getSelections();
    		
			if(datoAutorizar.length == 0)
				Ext.Msg.alert('SET', 'Es necesario seleccionar un registro');
			else {
				//Ext.Msg.confirm('SET','¿Esta seguro de autorizar '+ datoAutorizar.length +' registros?', function(btn) {
				//	if(btn == 'yes') {
						NS.autoDesauto = 'AUT';
						winLogin.show();
				//	}
				//});
			}
		});
	};
	
	NS.desAutorizar = function() {
		var recordsDesAutorizar = NS.gridConsulta.getSelectionModel().getSelections();
		
		if(recordsDesAutorizar.length == 0)
			Ext.Msg.alert('SET', 'Es necesario seleccionar algun registro');
		else {
			//Ext.Msg.confirm('SET', '¿Esta seguro quitar autorización a '+ recordsDesAutorizar.length +' registros?',function(btn) {
			//	if(btn == 'yes') {
					NS.autoDesauto = 'DAUT';
					winLogin.show();
			//	}
			//});	
		}
	};
	
	NS.funAutoDesAuto = function() {
		var matAuto = new Array();
		var recAuto = NS.gridConsulta.getSelectionModel().getSelections();
		
		for (var i=0; i<recAuto.length; i++) {
			var regAuto = {};
			regAuto.noFolioDet = recAuto[i].get('noFolioDet');
			matAuto[i] = regAuto;
		}
		var jsonString = Ext.util.JSON.encode(matAuto);
		var pass = Ext.getCmp(PF + 'txtPsw').getValue();
		
		TraspasosAction.autoDesAuto(jsonString, NS.autoDesauto, pass, function(result, e) {
			if(result != null && result != '') {
				Ext.Msg.alert('SET', result + '!!');
				NS.storeGridConsulta.removeAll();
				NS.gridConsulta.getView().refresh();
				NS.buscar();
			}
		});
		winLogin.hide();
		Ext.getCmp(PF+'txtPsw').setValue("");
	};
	
	
	
////////////AGREGAR TIPO DE ENVIO A EL TRASPASO//
	NS.optTipoEnvioH2H = new Ext.form.RadioGroup ({
		id: PF + 'gOptTipoEnvio',
		name: PF + 'gOptTipoEnvio',
		x: 5,
		y: 5,
		columns: 1, //muestra los radiobuttons en dos columnas
		items: [
	          {boxLabel: 'Host to host', name: 'optTipoEnvioH2H', inputValue: 0, checked: true},  
	          {boxLabel: 'Normal', name: 'optTipoEnvioH2H', inputValue: 1}
	    ]
	});
	
	var winTipoEnvio = new Ext.Window({
		title: 'Tipo de envio',
		modal: true,
		shadow: true,
		//closable: false,
		closeAction: 'hide',
		width: 200,
	   	height: 160,
	   	layout: 'absolute',
	   	plain: true,
	   	resizable:false,
	   	draggable:false,
	    bodyStyle: 'padding:10px;',
	    buttonAlign: 'center',
	    buttons:[
	             {
	            	 text:'Aceptar',
	            	 handler:function(){
	            		 banderaEnvio=true;
	            		 NS.ejecutar();
	            	 }
			     }					      
	    ],
	   	items: [NS.optTipoEnvioH2H],
   	    listeners:{
   	        	show:{
   	        		fn:function(){
   	        			winTipoEnvio.show();
   	        			banderaEnvio=true;
   	        	    }
   	        	},
   	        	hide:{
   	        		fn:function(){
   	        			banderaEnvio=false;
   	        		}
   	        	}
   	     }	
	});
	
	/////////////AGREGAR TIPO DE ENVIO AL TRASPASO
	
	
	
	
	var winLogin = new Ext.Window({
		title: 'Traspasos'
		,closable: false
		,modal:true
		,shadow:true
		,width: 200
	   	,height:200
	   	,minWidth: 400
	   	,minHeight: 580
	   	,layout: 'fit'
	   	,plain:true
	    ,bodyStyle:'padding:10px;'
	   	,buttonAlign:'center'
	   	,items: [
	   	{
	   	xtype: 'fieldset',
        title: 'Contraseña',
        id:PF+'framePendiente',
        name:PF+'framePendientes',
       	x: 0,
        y: 0,
        width: 80,
        height: 100,
        layout: 'absolute',
        items: [
	   		{
       		inputType: 'password',
			xtype: 'textfield',
       		id:PF+'txtPsw',
       		name:PF+'txtPsw',
       		x:20,
       		y:10,
       		width:100,
       		hidden:false,
       		allowBlank: false,
       		blankText:'Introdusca su Contraseña'
       		}
		          ]
        }
	]
   	,buttons: [
	   	{
		 	text: 'Aceptar',
			handler: function(e) {
	   		NS.funAutoDesAuto();
			 }
	     },{
		 	text: 'Cancelar',
			handler: function(e) {
	    	 	winLogin.hide();
			 }
	     }
 	]
	});

	NS.contenedorEjecutaTraspasos.setSize(Ext.get(NS.tabContId).getWidth(), Ext.get(NS.tabContId).getHeight());
	//staticCheck("#gridConsultaEjecTras","#gridConsultaEjecTras",2,".x-grid3-scroller",false);
	
});