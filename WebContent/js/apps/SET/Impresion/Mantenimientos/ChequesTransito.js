/*
 * Autor : Yoseline Espino C.
 * 04-01-2016
 */
	Ext.onReady(function(){
	
	var NS = Ext.namespace('apps.SET.impresion.mantenimientos.chequesTransito');
	NS.tabContId = apps.SET.tabContainerId;
	NS.idUsuario = apps.SET.iUserId;
	NS.noEmpresa = apps.SET.NO_EMPRESA;
	NS.fecHoy = apps.SET.FEC_HOY;
	var PF = apps.SET.tabID + '.';
	Ext.Direct.addProvider(Ext.setapp.REMOTING_API);
	Ext.QuickTips.init();
	NS.GI_ID_EMPRESA = apps.SET.NO_EMPRESA;
	NS.GS_DESC_EMPRESA = apps.SET.NOM_EMPRESA;
	
	NS.Excel= false;
	NS.fechaIni='';
	NS.fechaFin=NS.fecHoy;
	
	NS.myMask = new Ext.LoadMask(Ext.getBody(), {msg:"Buscando ..."});
	
	/*****Combos con su caja y su store  EMPRESAS,BANCOS,CONCEPTOS,CHEQUERAS ***/

	/************************************************************************
	 * 							COMBO EMPRESAS
	 ************************************************************************/
		
    NS.storeEmpresas = new Ext.data.DirectStore({
		paramsAsHash: true,
		root: '',
		directFn: GlobalAction.llenarComboEmpresasUsuario,
		fields:
		[
		    
		    {name: 'id'},
		 	{name: 'descripcion'}
		 	
		],
		idProperty: 'id',
		listeners:
		{
			load: function(s, records){
				if(records.length == null || records.length <= 0){
					Ext.Msg.alert('SET', 'No hay empresas que mostrar');
				}else{
					var recordsStoreGruEmp = NS.storeEmpresas.recordType;	
					var todas = new recordsStoreGruEmp({
				       	id : '0',
				       	descripcion : '***********TODAS***********'
			       	});
					NS.storeEmpresas.insert(0,todas);
				}
				NS.myMask.hide();
					
			},
			exception:function(misc) {
				NS.myMask.hide();
				Ext.Msg.alert('SET', 'Error al cargar los datos, verificar la conexión');
			}
		}
	}); 
    NS.myMask.show();
	NS.storeEmpresas.load();
	
	NS.txtNoEmpresa = new Ext.form.TextField({
		id: PF + 'txtNoEmpresa',
        name:PF + 'txtNoEmpresa',
		x: 50,
        y: 0,
        width: 50,
        tabIndex: 0,
        value:'0',
        listeners: {
			change: {
				fn: function(caja,valor) {
					var err=1;
					if(caja.getValue() != null && caja.getValue() != undefined && caja.getValue() != ''){
							valueCombo = BFwrk.Util.updateTextFieldToCombo(PF + 'txtNoEmpresa', NS.cmbEmpresas.getId());
							if(valueCombo!='' && valueCombo !='undefined' && valueCombo != null)
								err=0;
					}
					
					if(err==1){
						Ext.getCmp(PF + 'txtNoEmpresa').setValue('0');
						NS.cmbEmpresas.reset();
						BFwrk.Util.updateTextFieldToCombo(PF + 'txtNoEmpresa', NS.cmbEmpresas.getId());
					}
                }
			}
    	}
    });

	NS.cmbEmpresas = new Ext.form.ComboBox({
		store: NS.storeEmpresas,
		id: PF + 'cmbEmpresas',
		name: PF + 'cmbEmpresas',
		x: 120,
        y: 0,
        width: 310,
		typeAhead: true,
		mode: 'local',
		forceSelection: true,
		valueField: 'id',
		displayField: 'descripcion',
		triggerAction: 'all',
		value:'',
		visible: false,
		autocomplete:true,
		selectOnFocus:true,
		listeners:{
			select:{
				fn:function(combo, valor) {
					NS.idEmpresa=combo.getValue();
					BFwrk.Util.updateComboToTextField(PF + 'txtNoEmpresa', NS.cmbEmpresas.getId());					
					NS.storeBancos.removeAll();
					NS.storeChequeras.removeAll();
					Ext.getCmp(PF+'cmbChequeras').setValue('');
					Ext.getCmp(PF+'txtIdBanco').setValue('');
					Ext.getCmp(PF+'cmbBancos').setValue('');
					NS.storeBancos.baseParams.noEmpresa = parseInt(Ext.getCmp(PF+'txtNoEmpresa').getValue());
					NS.storeBancos.load();
					
				}
			}
		}
	});

	/************************************************************************
	 * 							COMBO BANCOS 								*
	 ************************************************************************/
	
	
	//store bancos
	NS.storeBancos = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{noEmpresa:0},
		root: '',
		paramOrder:['noEmpresa'],
		root: '',
		directFn: ChequesTransitoAction.llenarComboBancos, 
		idProperty: 'id', 
		fields: [
			 {name: 'id'},
			 {name: 'descripcion'},
		],
		listeners: {
			load: function(s, records){				
				if(records.length==null || records.length<=0){
					Ext.Msg.alert('SET','No hay bancos disponibles para la empresa');
				}/*else{
					var recordsStoreBan =NS.storeBancos.recordType;	
					var todas = new recordsStoreBan({
				       	id : '0',
				       	descripcion : '******TODOS*****'
			       	});
					NS.storeBancos.insert(0,todas);
					
					Ext.getCmp(PF + 'txtIdBanco').setValue('0');
					NS.cmbBancos.reset();
					BFwrk.Util.updateTextFieldToCombo(PF + 'txtIdBanco', NS.cmbBancos.getId());
					NS.storeChequeras.baseParams.noEmpresa=NS.txtNoEmpresa.getValue;
					NS.storeChequeras.baseParams.noBanco=0;
					NS.storeChequeras.load();
				}*/
				
				
			},
			exception:function(misc) {
				Ext.Msg.alert('SET', 'Error al cargar los datos, verificar la conexión');
			}
		}
	});
	//NS.storeBancos.load();

	NS.txtIdBanco = new Ext.form.TextField({
		id: PF+'txtIdBanco',
        name: PF+'txtIdBanco',
        x: 490,
        y: 0,
        width: 50,
        listeners:{
        	change:{
       	         fn:function(caja, valor){
           			var err=1;
					if(caja.getValue() != null && caja.getValue() != undefined && caja.getValue() != ''){
							var comboValue = BFwrk.Util.updateTextFieldToCombo(PF+'txtIdBanco',NS.cmbBancos.getId());
							if(valueCombo!='' && valueCombo !='undefined' && valueCombo != null)
								err=0;
					}
					
					if(err==1){
						Ext.getCmp(PF + 'txtIdBanco').setValue('');
						NS.cmbBancos.reset();
						NS.storeChequeras.reset();
					}
					
           			NS.accionarCmbBancos(comboValue);
           			NS.noBanco=caja.getValue();
       	         }
       		}
   		}
    });
	
	
	NS.accionarCmbBancos = function(comboValor){
		Ext.getCmp(PF+'cmbChequeras').setValue('');
		NS.storeChequeras.baseParams.noBanco = comboValor;		
		NS.storeChequeras.baseParams.noEmpresa = parseInt(Ext.getCmp(PF+'txtNoEmpresa').getValue());
		NS.storeChequeras.load();
		//NS.descBanco = NS.storeBancos.getById(comboValor).get('descripcion');
	}
	
	//combo bancos
	NS.cmbBancos = new Ext.form.ComboBox({
		store: NS.storeBancos
		,name: PF+'cmbBancos'
		,id: PF+'cmbBancos'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
        ,x: 550
        ,y: 0
        ,width: 170
		,valueField:'id'
		,displayField:'descripcion'
		,emptyText: 'Seleccione un banco'
		,triggerAction: 'all'
		,value: ''
		,visible: false
		,autocomplete:true
		,listeners:{
			select:{
				fn:function(combo, valor) {
					NS.noBanco=combo.getValue();
					BFwrk.Util.updateComboToTextField(PF+'txtIdBanco',NS.cmbBancos.getId());
					NS.accionarCmbBancos(combo.getValue());			
				}
			},
			change:{
				fn:function(combo, valor) {
					if(combo.getValue()==''){
						Ext.getCmp(PF + 'txtIdBanco').setValue('');
						NS.cmbBancos.reset();
						NS.storeChequeras.removeAll();
						NS.cmbChequeras.reset();
					}
				}
			}
		}
	});

	

	/************************************************************************
	 * 							COMBO CHEQUERAS								*
	 ************************************************************************/
	
	NS.storeChequeras = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{
			noBanco: 0,
			noEmpresa:0
		},
		root: '',
		paramOrder:['noBanco','noEmpresa'],
		root: '',
		directFn: ChequesTransitoAction.llenarComboChequeras, 
		idProperty: 'descripcion', 
		fields: [
			 {name: 'descripcion'},
			 {name: 'descripcion'}
		],
		listeners: {
			load: function(s, records){
				if(records.length==null || records.length<=0){
					Ext.Msg.alert('SET','No hay chequeras disponibles para el banco');
				}/*else{
					var recordsStoreCheq =NS.storeChequeras.recordType;	
					var todas = new recordsStoreCheq({
				       	id : '0',
				       	descripcion : '***TODAS***'
			       	});
					NS.storeChequeras.insert(0,todas);
					
					NS.cmbChequeras.setValue('***TODAS***');
					
				}*/
			},
			exception:function(misc) {
				Ext.Msg.alert('SET', 'Error al cargar los datos, verificar la conexión');
			}
		}
	});
	
	//combo chequeras

	NS.cmbChequeras = new Ext.form.ComboBox({
		store: NS.storeChequeras
		,name: PF+'cmbChequeras'
		,id: PF+'cmbChequeras'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
        ,x: 790
        ,y: 0
        ,width: 150
		,valueField:'descripcion'
		,displayField:'descripcion'
		,autocomplete: true
		,emptyText: 'Seleccione una chequera'
		,triggerAction: 'all'
		,value: ''
		,visible: false
		,editable:false
		,listeners:{
			select:{
				fn:function(combo, valor) {
						NS.chequera=combo.getValue();
				}
			}
		}
	});
	
	/********************************************************
	 * 					Combo Motivos						*
	 *******************************************************/
	
	NS.storeMotivos = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{},
		root: '',
		paramOrder:[],
		root: '',
		directFn: ChequesTransitoAction.llenarComboMotivos, 
		idProperty: 'id', 
		fields: [
			 {name: 'id'},
			 {name: 'descripcion'},
		],
		listeners: {
			load: function(s, records){				
				if(records.length==null || records.length<=0){
					Ext.Msg.alert('SET','No existen motivos');
				}
			},
			exception:function(misc) {
				Ext.Msg.alert('SET', 'Error al cargar los datos, verificar la conexión');
			}
		}
	});
	NS.storeMotivos.load();
	
	NS.cmbMotivos = new Ext.form.ComboBox({
		store: NS.storeMotivos
		,name: PF+'cmbMotivos'
		,id: PF+'cmbMotivos'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
        ,x: 120
        ,y: 0
        ,width: 170
        ,valueField:'id'
    	,displayField:'descripcion'
    	,emptyText: 'Seleccione un motivo'
		,triggerAction: 'all'
		,value: ''
		,visible: false
		,autocomplete:true
	});

	/***COMPONENTES DEL FORMULARIO ***/

	NS.lbEmpresa = new Ext.form.Label({
		text: 'Empresa:',
        x: 0,
        y: 5	
	});

	NS.lbBanco = new Ext.form.Label({
		text: 'Banco:',
        x: 440,
        y: 	5	
	});

	NS.lbChequera = new Ext.form.Label({
		text: 'Chequera:',
        x: 730,
        y: 5		
	});

	NS.lbNoCheque = new Ext.form.Label({
		text: 'No. Cheque:',
        x: 10,
        y: 45	
	});

	NS.lbFechas = new Ext.form.Label({
		text: 'Rango de fechas:',
        x: 240,
        y: 45		
	});

	NS.lbDiasAntiguedad = new Ext.form.Label({
		text: 'Dias de antiguedad:',
        x: 550,
        y: 45		
	});
	
	NS.lbMotivo = new Ext.form.Label({
		text: 'Motivo de cancelación:',
        x: 0,
        y: 5		
	});

	//FECHAS INICIAL Y FINAL

	NS.txtFechaIni = new Ext.form.DateField({
		name: PF+'txtFechaIni',
        id: PF+'txtFechaIni',
        x: 330,
        y: 40,
        width: 95,
		value: NS.fecHoy,
        format: 'd/m/Y',
        enableKeyEvents:true,
		listeners:{
	 		change:{
	 			fn:function(caja, e) {	
	 				NS.fechaIni=cambiarFecha(caja.getValue()+'')+'';
	 				var fechaFin = Ext.getCmp(PF + 'txtFechaFin').getValue();
        			if(fechaFin < caja.getValue()&& NS.fechaFin!=''){
        				Ext.Msg.alert('SET', 'La fecha inicial no puede ser mayor a la fecha final!!');
        				Ext.getCmp(PF + 'txtFechaFin').setValue('');
        			}
	 			}
	 		}
		}
	});

	NS.txtFechaFin = new Ext.form.DateField({
		format: 'd/m/Y',
        id: PF+'txtFechaFin',
        name: PF+'txtFechaFin',
        value: NS.fecHoy,
        x: 440,
        y: 40,
        width: 95,
        enableKeyEvents:true,
		listeners:{
	 		change:{
	 			fn:function(caja, e) {
	 				NS.fechaFin=cambiarFecha(caja.getValue()+'')+'';
	 				var fechaIni = Ext.getCmp(PF + 'txtFechaIni').getValue();
        			if(fechaIni > caja.getValue()&& NS.fechaIni!=''){
        				Ext.Msg.alert('SET', 'La fecha inicial no puede ser mayor a la fecha final!!');
        				Ext.getCmp(PF + 'txtFechaIni').setValue('');
        			}
	 			}
	 		}
	 	}
	});

	NS.txtNoCheque = new Ext.form.TextField({
		id: PF+'txtNoCheque',
        name: PF+'txtNoCheque',
        x: 80,
        y: 40,
    });

    NS.txtDiasAntiguedad = new Ext.form.TextField({
		id: PF+'txtDiasAntiguedad',
        name: PF+'txtDiasAntiguedad',
        x: 650,
        y: 40,
        width: 100,
    });
    
    /******************************
     * GRID
     */
    
  //Store's
	NS.storeLlenaGrid = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams: {noEmpresa:'',noBanco:'',idChequera:'',noCheque:'',fechaIni:'',fechaFin:'',dias:''},
		paramOrder: ['noEmpresa','noBanco','idChequera','noCheque','fechaIni','fechaFin','dias'],
		directFn: ChequesTransitoAction.llenaGrid,
		fields: [
		    {name: 'noDocumento'},
		 	{name: 'noCheque'},
		 	{name: 'noProveedor'},
		 	{name: 'beneficiario'},
		 	{name: 'descripcion'},
		 	{name: 'concepto'},
		 	{name: 'importe'},
		 	{name: 'divisa'},
		 	{name: 'fechaCheque'},
		 	{name: 'noEmpresa'},
		 	{name: 'nomEmpresa'},
		 	{name: 'idBanco'},
		 	{name: 'descBanco'},
		 	{name: 'idChequera'},
		 	{name: 'dias'},
		 	{name: 'noFolioDetalle'},
		 	{name: 'origenMovimiento'},
		 	{name: 'tipoOperacion'},
		 	{name: 'tipoMovimiento'},
		 	{name: 'formaPago'},
		 	{name: 'usuarioAlta'},
		 	{name: 'loteE'},
		 	{name: 'loteS'},
		 	{name: 'bEntregado'},
		 	{name: 'bsbc'},
		 	{name: 'idEstatus'},
		 	{name: 'noCuenta'},
		 	{name: 'fecTransferencia'},
		 	{name: 'division'},
		 	{name: 'idPoliza'},
		 	{name: 'fecOperacion'},
		 	{name: 'fecAlta'},
		 	{name: 'fecValorOriginal'},
		 	{name: 'importeOriginal'},
		 	{name: 'tipoCambio'},
		 	{name: 'idCaja'},
		 	{name: 'idDivisaOriginal'},
		 	{name: 'idBancoBenef'},
		 	{name: 'idLeyenda'},
		 	{name: 'clabe'},
		 	{name: 'solicita'},
		 	{name: 'autoriza'},
		 	{name: 'plaza'},
		 	{name: 'sucursal'},
		 	{name: 'noFolioMov'},
		 	{name: 'idRubro'},
		 	{name: 'idGrupo'},
		 	{name: 'noCliente'},
		 	{name: 'idChequeraBenef'},
		 	{name: 'referencia'},
		 	{name: 'ejercicio'},
		 	{name: 'poHeders'}
		 	
		],
		listeners: {
			load: function(s, records) {
				if (records.length == null || records.length <= 0){Ext.Msg.alert('SET', 'No existen cheques por cobrar');}
				NS.myMask.hide();
			},
			exception:function(misc) {
				NS.myMask.hide();
				Ext.Msg.alert('SET', 'Error al cargar los datos, verificar la conexión');
			}
			
		}	
	})
	//NS.storeLlenaGrid.load();
	
	NS.columnaSeleccion= new Ext.grid.CheckboxSelectionModel({
		singleSelect: true
	});
	
	NS.columnas = new Ext.grid.ColumnModel([
	  NS.columnaSeleccion,
	  {header: 'No. Docto', width: 100, dataIndex: 'noDocumento', sortable: true},
	  {header: 'No. Cheque', width: 100, dataIndex: 'noCheque', sortable: true},
	  {header: 'No. Proveedor', width: 100, dataIndex: 'noCliente', sortable: true},
	  {header: 'Beneficiario', width: 200, dataIndex: 'beneficiario', sortable: true},
	  {header: 'Concepto', width: 200, dataIndex: 'concepto', sortable: true},
	  {header: 'Importe', width: 100, dataIndex: 'importe', sortable: true,
		  renderer: function (value, meta, record) {
	            return '$' + NS.formatNumber(Math.round((value)*100)/100 );
		  }
	  },
	  {header: 'Divisa', width: 40, dataIndex: 'divisa', sortable: true},
	  {header: 'Féc. Cheque', width: 100, dataIndex: 'fechaCheque', sortable: true},
	  {header: 'Empresa', width: 200, dataIndex: 'nomEmpresa', sortable: true},
	  {header: 'Banco', width: 100, dataIndex: 'descBanco', sortable: true},
	  {header: 'Chequera', width: 100, dataIndex: 'idChequera', sortable: true},
	  {header: 'Días de Antigüedad', width: 70, dataIndex: 'dias', sortable: true}
	]);
					
	NS.panelGridDetalle = new Ext.grid.GridPanel({
		store: NS.storeLlenaGrid,
		id: PF + 'panelGridDetalle',
		name: PF + 'panelGridDetalle',
		cm: NS.columnas,
		sm: NS.columnaSeleccion,
		x: 0,
		y: 0,
		width: 1000,
		height: 260,
		stripRows: true,
		columnLines: true,
	});
    
    
    /************************************************************
     *						 Funciones 							*
     ************************************************************/
    
    NS.limpiarTodo=function(){
    	NS.txtIdBanco.setValue("");
    	NS.txtFechaIni.setValue(NS.fecHoy);
    	NS.txtFechaFin.setValue(NS.fecHoy);
    	NS.txtNoCheque.setValue("");
    	NS.txtDiasAntiguedad.setValue("");
    	NS.cmbBancos.reset();
    	NS.cmbChequeras.reset();
    	NS.cmbMotivos.reset();
    	NS.storeBancos.removeAll();
    	NS.storeChequeras.removeAll();
		NS.storeLlenaGrid.removeAll();
		NS.fechaIni='';
		NS.fechaFin=NS.fecHoy;
		
		Ext.getCmp(PF + 'txtNoEmpresa').setValue('0');
		NS.cmbEmpresas.reset();
		BFwrk.Util.updateTextFieldToCombo(PF + 'txtNoEmpresa', NS.cmbEmpresas.getId());
	} 
    
    NS.buscar=function(){
    	NS.storeLlenaGrid.removeAll();
		NS.storeLlenaGrid.baseParams.noEmpresa=NS.txtNoEmpresa.getValue();
    	NS.storeLlenaGrid.baseParams.noBanco=NS.txtIdBanco.getValue();
    	NS.storeLlenaGrid.baseParams.idChequera=NS.cmbChequeras.getValue();
    	NS.storeLlenaGrid.baseParams.noCheque=NS.txtNoCheque.getValue();
    	NS.storeLlenaGrid.baseParams.fechaIni=NS.fechaIni;
    	NS.storeLlenaGrid.baseParams.fechaFin=NS.fechaFin;
    	NS.storeLlenaGrid.baseParams.dias=NS.txtDiasAntiguedad.getValue();
    	NS.myMask.show();
    	NS.storeLlenaGrid.load();
    }
    
    NS.cancelar=function(pwd){
    	var registroSeleccionado = NS.panelGridDetalle.getSelectionModel().getSelections();
		if(registroSeleccionado.length == 0) {
			Ext.Msg.alert('SET', 'Debe seleccionar un registro para realizar la cancelación');
			winLogin.hide();
		}else{
			var motivo=NS.cmbMotivos.getRawValue();
	    	if(motivo!='' && motivo != null && motivo!=' '){
	    		var matriz = new Array();
	    		var vector = {};
	    		vector.noFolioDetalle=registroSeleccionado[0].get('noFolioDetalle');
	    		vector.tipoOperacion=registroSeleccionado[0].get('tipoOperacion');
	    		vector.idEstatus=registroSeleccionado[0].get('idEstatus');
	    		vector.origenMovimiento=registroSeleccionado[0].get('origenMovimiento');
	    		vector.formaPago=registroSeleccionado[0].get('formaPago');
	    		vector.bEntregado=registroSeleccionado[0].get('bEntregado');
	    		vector.tipoMovimiento=registroSeleccionado[0].get('tipoMovimiento');
	    		vector.importe=NS.unformatNumber(registroSeleccionado[0].get('importe'));
	    		vector.noEmpresa=registroSeleccionado[0].get('noEmpresa');
	    		vector.noCuenta=registroSeleccionado[0].get('noCuenta');
	    		vector.idChequera=registroSeleccionado[0].get('idChequera');
	    		vector.idBanco=registroSeleccionado[0].get('idBanco');
	    		vector.noCheque=registroSeleccionado[0].get('noCheque');
	    		vector.usuarioAlta=registroSeleccionado[0].get('usuarioAlta');
	    		vector.noDocumento=registroSeleccionado[0].get('noDocumento');
	    		vector.loteE=registroSeleccionado[0].get('loteE');
	    		vector.bsbc=registroSeleccionado[0].get('bsbc');
	    		vector.fecTransferencia=registroSeleccionado[0].get('fecTransferencia');
	    		vector.divisa=registroSeleccionado[0].get('divisa');
	    		vector.dias=registroSeleccionado[0].get('dias');
	    		vector.division = registroSeleccionado[0].get('division');
	    		vector.idPoliza = registroSeleccionado[0].get('idPoliza');
	    		vector.fecOperacion = registroSeleccionado[0].get('fecOperacion');
	    		vector.fecAlta = registroSeleccionado[0].get('fecAlta');
	    		vector.fecValorOriginal = registroSeleccionado[0].get('fecValorOriginal');
	    		vector.importeOriginal = registroSeleccionado[0].get('importeOriginal');
	    		vector.tipoCambio = registroSeleccionado[0].get('tipoCambio');
	    		vector.idCaja = registroSeleccionado[0].get('idCaja');
	    		vector.idDivisaOriginal = registroSeleccionado[0].get('idDivisaOriginal');
	    		vector.idBancoBenef = registroSeleccionado[0].get('idBancoBenef');
	    		vector.idLeyenda = registroSeleccionado[0].get('idLeyenda');
	    		vector.clabe = registroSeleccionado[0].get('clabe');
	    		vector.solicita = registroSeleccionado[0].get('solicita');
	    		vector.autoriza = registroSeleccionado[0].get('autoriza');
	    		vector.plaza = registroSeleccionado[0].get('plaza');
	    		vector.sucursal = registroSeleccionado[0].get('sucursal');
	    		vector.noFolioMov = registroSeleccionado[0].get('noFolioMov');
	    		vector.idRubro = registroSeleccionado[0].get('idRubro');
	    		vector.idGrupo = registroSeleccionado[0].get('idGrupo');
	    		vector.noCliente= registroSeleccionado[0].get('noCliente');
	    		vector.idChequeraBenef=registroSeleccionado[0].get('idChequeraBenef');
	    		vector.referencia=registroSeleccionado[0].get('referencia');
	    		vector.observacion=motivo;
	    		vector.beneficiario=registroSeleccionado[0].get('beneficiario');
	    		vector.concepto=registroSeleccionado[0].get('concepto');
	    		vector.pwd=pwd;
	    		vector.ejercicio=registroSeleccionado[0].get('ejercicio');
	    		vector.poHeders=registroSeleccionado[0].get('poHeders');
	    		
	    		matriz[0] = vector;
	    		var jSonString = Ext.util.JSON.encode(matriz);
	    		ChequesTransitoAction.cancelarCheque(jSonString, function(resultado, e) {
	    			if (resultado != '' && resultado != null && resultado != undefined) {
	    				Ext.Msg.alert('SET', resultado);
	    			}else{
	    				Ext.Msg.alert('SET', 'Error en el proceso');
	    			}
	    			winLogin.hide();
	    		});	  
	    	}else{
	    		Ext.Msg.alert('SET', 'Debe seleccionar un motivo para realizar la cancelación');
	    		winLogin.hide();
	    	}
		}
    	
    }

	//Filed set para radios
	NS.panelBuscar = new Ext.form.FieldSet ({
		title: 'Buscar',
        x:0,
        y: 0,
        width: 1035,
        height: 100,
        layout: 'absolute',
		items: [
		    NS.txtNoEmpresa,
			NS.cmbEmpresas ,
			NS.txtIdBanco ,
			NS.cmbBancos ,
			NS.cmbChequeras,
			NS.lbEmpresa ,
			NS.lbBanco,
			NS.lbChequera,
			NS.lbNoCheque,
			NS.lbFechas,
			NS.lbDiasAntiguedad,
			NS.txtFechaIni,
			NS.txtFechaFin,
			NS.txtNoCheque,
		    NS.txtDiasAntiguedad,
		    {
		 		xtype: 'button',
		 		text: 'Buscar',
		 		x: 800,
		 		y: 40,
		 		width: 80,
		 		height: 22,
		 		tabIndex:3,
		 		listeners: {
		 			click:{
		 				fn: function(e){
		 					NS.buscar();
		 				}
		 			}
		 		}
		 	},
		]
	});

	NS.panelGrid = new Ext.form.FieldSet ({
		title: 'Detalle',
        id: PF+'panelGrid',
        name: PF+'panelGrid',
        x: 0,
        y: 100,
        width: 1035,
        height: 300,
        layout: 'absolute',
        items: [
                NS.panelGridDetalle
         ]
	});
	
	NS.panelMotivo = new Ext.form.FieldSet ({
		title: '',
        id: PF+'panelMotivo',
        name: PF+'panelMotivo',
        x: 0,
        y: 410,
        width: 1035,
        height: 45,
        layout: 'absolute',
        items: [
                NS.lbMotivo,
                NS.cmbMotivos,
                {
    		 		xtype: 'button',
    		 		text: 'Cancelar',
    		 		x: 700,
    		 		y: 0,
    		 		width: 80,
    		 		height: 22,
    		 		tabIndex:3,
    		 		listeners: {
    		 			click:{
    		 				fn: function(e){
    		 					Ext.getCmp(PF+'txtPsw').setValue("");
    		 					var registroSeleccionado = NS.panelGridDetalle.getSelectionModel().getSelections();
    		 					if(registroSeleccionado.length == 0) {
    		 						Ext.Msg.alert('SET', 'Debe seleccionar un registro para realizar la cancelación');
    		 					}else{
    		 						var motivo=NS.cmbMotivos.getRawValue();
    		 				    	if(motivo!='' && motivo != null && motivo!=' '){
    		 				    		winLogin.show();
    		 				    	}else{
    		 				    		Ext.Msg.alert('SET', 'Debe seleccionar un motivo para realizar la cancelación');
    		 				    	}
    		 						
    		 					}
    		 				}
    		 			}
    		 		}
    		 	},
    		 	  {
    		 		xtype: 'button',
    		 		text: 'Excel',
    		 		x: 800,
    		 		y: 0,
    		 		width: 80,
    		 		height: 22,
    		 		tabIndex:3,
    		 		listeners: {
    		 			click:{
    		 				fn: function(e){
    		 					NS.Excel = true;
    				    		NS.validaDatosExcel();
    		 				}
    		 			}
    		 		}
    		 	},
    		 	{
    		 		xtype: 'button',
    		 		text: 'Limpiar',
    		 		x: 900,
    		 		y: 0,
    		 		width: 80,
    		 		height: 22,
    		 		tabIndex:3,
    		 		listeners: {
    		 			click:{
    		 				fn: function(e){
    		 					NS.limpiarTodo();
    		 				}
    		 			}
    		 		}
    		 	},


         ]
	});
	
	NS.panelComponentes = new Ext.form.FieldSet ({
		title: '',
	    x: 0,
	    y: 0,
	    width: 1060,
	    height: 480,
	    layout: 'absolute',
		items: [
		 	NS.panelBuscar,
		 	NS.panelGrid,
		 	NS.panelMotivo
		]
	});

	//panel principal

	NS.contenedorMovimientos =new Ext.FormPanel({
    title: 'Movimientos de Banca Electrónica',
    width: 1200,
    height: 600,
    padding: 10,
    frame: true,
    autoScroll: true,
    layout: 'absolute',
    renderTo: NS.tabContId,
    items : [
			NS.panelComponentes,
   		 ]   
    });

    /***Funciones generales **/

    NS.btnLimpiar=function(){
    	NS.contenedorMovimientos.getForm().reset();
		//NS.obtenerFechaHoy();
		NS.empresaActual = true;
		NS.empresaTodas = false;
		NS.movimientoCapturado = true;
		NS.movimientoBanca = false;
		NS.tipoIngreso = false;
		NS.tipoEgreso = false;
		NS.tipoTodos = true;
		NS.tipoConceptos = false;
		NS.tipoMovimientos = false;
		NS.descBanco = '';
		NS.descConcepto = '';
		NS.chkTEF = false;
		NS.chkDia = false;
		NS.chkExcel = false;
		NS.chkDetalle = false;
    }

    /**********************Generar el excel *********************************/
	 NS.validaDatosExcel = function() {	
		var registroSeleccionado = NS.panelGridDetalle.getSelectionModel().getSelections();
		//Primero valida si se seleccionaron propuestas para el excel, sino toma todas
		if(registroSeleccionado.length == 0) {
			registroSeleccionado = NS.storeLlenaGrid.data.items;
			if(registroSeleccionado.length == 0) {
				Ext.Msg.alert('SET','No hay registros para el reporte !!');
				return;
			}
		}
		
		if(NS.Excel) {
			
			var matriz = new Array();
			
			for(var i=0;i<registroSeleccionado.length;i++){
				var vector = {};
				vector.noDocumento=registroSeleccionado[i].get('noDocumento');
				vector.noCheque=registroSeleccionado[i].get('noCheque');
				vector.noProveedor=registroSeleccionado[i].get('noProveedor');
				vector.beneficiario=registroSeleccionado[i].get('beneficiario');
				vector.descripcion=registroSeleccionado[i].get('descripcion');
				vector.concepto=registroSeleccionado[i].get('concepto');
				vector.importe=registroSeleccionado[i].get('importe');
				vector.divisa=registroSeleccionado[i].get('divisa');
				vector.fechaCheque=registroSeleccionado[i].get('fechaCheque');
				vector.nomEmpresa=registroSeleccionado[i].get('nomEmpresa');
				vector.descBanco=registroSeleccionado[i].get('descBanco');
				vector.dias=registroSeleccionado[i].get('dias');
				matriz[i] = vector;		
			}
			var jSonString = Ext.util.JSON.encode(matriz);
		}
		NS.exportaExcel(jSonString);
		NS.excel = false;
	
		return;
	};
		
	NS.exportaExcel = function(jsonCadena) {
		ChequesTransitoAction.exportaExcel(jsonCadena, function(res, e){
			if (res != null && res != undefined && res == "") {
				Ext.Msg.alert('SET', "Error al generar el archivo");
			} else {
				strParams = '?nomReporte=chequesTransito';
				strParams += '&'+'nomParam1=nomArchivo';
				strParams += '&'+'valParam1='+res;
				window.open("/SET/jsp/bfrmwrk/gnrators/abreExcel.jsp"+strParams);
			}
		});
	};
		/****************Fin de generar excel ***********************/
   	
    function cambiarFecha(fecha){	
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
    
/***********************Login *************************/
 	
 	function verificaComponentesCreados(){
 		var compt; 
	 	compt = Ext.getCmp(PF + 'txtPsw');
		if(compt != null || compt != undefined ){ compt.destroy(); }
	}
 	
 	var winLogin = new Ext.Window({
		title: 'Confirmación de contraseña'
		,modal:true
		,shadow:true
		,width: 200
	   	,height:160
	   	,layout: 'fit'
	   	,plain:true
	    ,bodyStyle:'padding:10px;'
	   	,buttonAlign:'center'
	   	,closable: false
	   	,resizable: false
	   	,draggable: false
	   	,items: [
	   	{
	   	xtype: 'fieldset',
        title: 'Introduce tu contraseña',
        id:PF+'framePendiente',
        name:PF+'framePendientes',
       	x: 0,
        y: 0,
        width: 100,
        height: 30,
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
	       		autocomplete: 'off',
	       		style: {
	       			autocomplete: 'off',
	            }
	   		}
		]
        }
		]
	   	,buttons: [
		   	{
			 	text: 'Aceptar',
				handler: function(e) {
    				var pwd=Ext.getCmp(PF+'txtPsw').getValue();
    				if(pwd!='' && pwd != null && pwd!=' '){
    					 NS.cancelar(pwd);
    				     NS.buscar;
    				}else
    					Ext.Msg.alert('SET', 'Debe ingresar su contraseña');
				 }
		     },{
			 	text: 'Cancelar',
				handler: function(e) {
					Ext.getCmp(PF+'txtPsw').setValue("");
				 	winLogin.hide();
				 }
		     }
	 	]
	});
 	
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
	
	//Quitar formato a las cantidades
	NS.unformatNumber=function(num) {
		return num.replace(/(,)/g,''); //num.replace(/([^0-9\.\-])/g,''*1);
	};

 	/*************************************************************/
 	NS.contenedorMovimientos.setSize(Ext.get(NS.tabContId).getWidth(), Ext.get(NS.tabContId).getHeight());
	
});