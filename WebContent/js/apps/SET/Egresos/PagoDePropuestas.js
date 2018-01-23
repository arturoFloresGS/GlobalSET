/**
 * 
 */

Ext.onReady(function(){
	var NS = Ext.namespace('apps.SET.Egresos.PagoDePropuestas');
	//NS.tabContId = 'contenedorPagoPropuestas';
	NS.tabContId = apps.SET.tabContainerId;
	var PF = apps.SET.tabID+'.'; // Generar prefijo para id html
    Ext.Direct.addProvider(Ext.setapp.REMOTING_API);	
	Ext.QuickTips.init(); 
	NS.idUsuario = apps.SET.iUserId;
	NS.idGrupoEmpresa = -1;
	NS.idDivision = 0;	
	NS.montoTotalMN = 0;
	NS.montoTotalDLS = 0;
	NS.chkLocales = 'false';
	NS.chkCheques = false;
	NS.chkTransfers = false;	
	
	NS.sm = new Ext.grid.CheckboxSelectionModel
	({
		singleSelect: false
	});
	
	//funcion para obtener la fecha de HOY
	/*NS.obtenerFechaHoy = function(){
		ConsultaPropuestasAction.obtenerFechaHoy(function(result, e){
			NS.fechaHoy = ''+result;
			Ext.getCmp(PF+'fecha1').setValue(NS.fechaHoy);
			Ext.getCmp(PF+'fecha2').setValue(NS.fechaHoy);
		});
	}
	NS.obtenerFechaHoy();*/
	
	
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
	
	
	function cambiarFecha(fecha)
	{
		var mesArreglo=new Array(11);
		mesArreglo[0]="Jan";mesArreglo[1]="Feb";mesArreglo[2]="Mar";mesArreglo[3]="Apr";
		mesArreglo[4]="May";mesArreglo[5]="Jun";mesArreglo[6]="Jul";mesArreglo[7]="Aug";mesArreglo[8]="Sep";
		mesArreglo[9]="Oct";mesArreglo[10]="Nov";mesArreglo[11]="Dec";
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
	
	function cambiarFechaGrid(fecha)
	{
		return fecha.substring(0,11);
	}
	
	//store  GrupoEmpresas
    NS.storeGrupoEmpresas = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{
			idUsuario:NS.idUsuario
		},
		root: '',
		paramOrder:['idUsuario'],
		root: '',
		directFn: PagoDePropuestasSetAction.llenarComboGrupoEmpresas, 
		idProperty: 'id', 
		fields: [
			 {name: 'id'},
			 {name: 'descripcion'},
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeGrupoEmpresas, msg:"Cargando..."});
				if(records.length == null || records.length<=0)
				{
					Ext.Msg.alert('SET','No Existen empresas asignadas al usuario: '+NS.idUsuario);
					return;
				}
		 		//Se agrega la opcion todas a storeGrupoEmpresas
	 			var recordsStoreGruEmp = NS.storeGrupoEmpresas.recordType;	
				var todas = new recordsStoreGruEmp({
			       	id : 0,
			       	descripcion : '***************TODAS***************'
		       	});
		   		NS.storeGrupoEmpresas.insert(0,todas);
			}
		}
	}); 
	
	NS.storeGrupoEmpresas.load();
	//combo Grupo
	NS.cmbGrupoEmpresas = new Ext.form.ComboBox({
		store: NS.storeGrupoEmpresas
		,name: PF+'cmbGrupoEmpresas'
		,id: PF+'cmbGrupoEmpresas'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
        ,x: 10
        ,y: 15
        ,width: 290
		,valueField:'id'
		,displayField:'descripcion'
		,autocomplete: true
		,emptyText: 'Seleccione un grupo empresa'
		,triggerAction: 'all'
		,value: ''
		,visible: false
		,listeners:{
			select:{
				fn:function(combo, valor) {
					NS.idGrupoEmpresa=combo.getValue();
				}
			}
		}
	});
	
	//store  Division por empresas
	//Revisado no se ultiliza esta en la pantalla Alberto A.G.
	/*   NS.storeDivision = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{
			idUsuario:NS.idUsuario
		},
		root: '',
		paramOrder:['idUsuario'],
		root: '',
		directFn: PagoDePropuestasSetAction.llenarComboDivXEmp, 
		idProperty: 'id', 
		fields: [
			 {name: 'id'},
			 {name: 'descripcion'},
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeDivision, msg:"Cargando..."});
				if(records.length==null || records.length<=0)
				{
					Ext.Msg.alert('SET','No Existen divisiones asignadas al usuario: '+NS.idUsuario);
				}
			}
		}
	});
	
	//NS.storeDivision.load();
	//combo Division por empresas 
    //Revisado no se ultiliza esta en la pantalla Alberto A.G.
	NS.cmbDivision = new Ext.form.ComboBox({
		store: NS.storeDivision
		,name: PF+'cmbDivision'
		,id: PF+'cmbDivision'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
        ,x: 10
        ,y: 65
        ,width: 190
		,valueField:'id'
		,displayField:'descripcion'
		,autocomplete: true
		,emptyText: 'Seleccione una division'
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
	*/
    //Store para llenar el grid principal
  	 NS.storePagoPropuestas = new Ext.data.DirectStore({
  	 	paramsAsHash: false,
  	 	baseParams: {
			idGrupoEmpresa:-1,
			idDivision: NS.idDivision,
			fecIni:'',
			fecFin:'',
			chkLocal:'false',
			idUsuario:NS.idUsuario
		},
		root: '',
		paramOrder:['idGrupoEmpresa','idDivision','fecIni','fecFin','chkLocal','idUsuario'],
		directFn: PagoDePropuestasSetAction.consultarPropuestas,
		fields: [
			{name: 'empChe'},
			{name: 'cveControl'},
			{name: 'fecPropuesta'/*,
				type: 'dateField',
				dateFormat: 'Y-m-d H: i: s'*/},
			{name: 'descGrupoFlujo'},
			{name: 'descGrupoCupo'},
			{name: 'importeMn'},
			{name: 'importeDls'},
			{name: 'totalDls'},
			{name: 'idGrupoCupo'},
			{name: 'idGrupoFlujo'},
			{name: 'descBancoPago'},
			{name: 'idChequera'},
			{name: 'concepto'},
			{name: 'idBancoBenef'},
			{name: 'fecRecalculo'},
			{name: 'numIntercos'},
			{name: 'totalIntercos'},
			{name: 'color'}
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storePagoPropuestas, msg:"Buscando..."});
				Ext.MessageBox.hide();
				
				if(records.length==null || records.length<=0)
				{
					Ext.getCmp(PF + 'cmdGrafica').setDisabled(true);
					Ext.Msg.alert('Información  SET','No existen datos con los parametros de búsqueda');
				}
				else
				{
          			var sumMn=0;
          			var sumDls=0;
          			//se comenta esta linea para que el usuario NO ejecute todas las propuestas por error
          			//NS.sm.selectRange(0,records.length-1);
          			//var regSelec = NS.gridPagoPropuestas.getSelectionModel().getSelections();
          			
          			for(var k=0; k<records.length; k++) {
          				sumMn = sumMn + records[k].get('importeMn');
          				sumDls = sumDls + records[k].get('importeDls');
          			}
          			NS.montoTotalMN=sumMn;
          			NS.montoTotalDLS=sumDls;
          			
          			Ext.getCmp(PF+'montoPagPenMn').setValue(NS.formatNumber(sumMn.toFixed(2)));
          			Ext.getCmp(PF+'totalPagoMn').setValue(NS.formatNumber(0));
          			Ext.getCmp(PF+'diferenciaMn').setValue(NS.formatNumber(sumMn.toFixed(2)));
          			
          			Ext.getCmp(PF+'montoPagPenDls').setValue(NS.formatNumber(sumDls.toFixed(2)));
          			Ext.getCmp(PF+'totalPagoDls').setValue(NS.formatNumber(0));
          			Ext.getCmp(PF+'diferenciaDls').setValue(NS.formatNumber(sumDls.toFixed(2)));
          			
          			//Graficas
		            Ext.getCmp(PF + 'cmdGrafica').setDisabled(false);
				}
			}
		}
	});
	//grid principal
	NS.gridPagoPropuestas = new Ext.grid.GridPanel({
        store : NS.storePagoPropuestas,
        id : 'gridPagoPropuestas',
        cm: new Ext.grid.ColumnModel({
            defaults: {
                width: 120,
                value:true,
                sortable: true
            },
            columns: [
                NS.sm,
                {header :'Agrupa Emp Che', width :80, sortable :true, dataIndex :'empChe', hidden: true},
                {header :'Cve Control', width :90, sortable :true, dataIndex :'cveControl',
		        	renderer: function (value, meta, record) {
			            meta.attr = 'style=' + record.get('color');
			            return value;
			        },	
                },
                {header :'Fecha Propuesta', width :110, sortable :true/*,renderer: Ext.util.Format.dateRenderer('Y-m-d')*/, dataIndex :'fecPropuesta',
                	renderer: function (value, meta, record) {
			            meta.attr = 'style=' + record.get('color');
			            return value;
			        },
                },
                {header :'Empresa', width :180, sortable :true, dataIndex :'descGrupoFlujo',
                	renderer: function (value, meta, record) {
			            meta.attr = 'style=' + record.get('color');
			            return value;
			        },	
                },
                {header :'Grupo Rubros', width :88, sortable :true, dataIndex :'descGrupoCupo',
                	renderer: function (value, meta, record) {
			            meta.attr = 'style=' + record.get('color');
			            return value;
			        },
		        },
                {header :'Importe MN', width :100, sortable :true, dataIndex :'importeMn', css: 'text-align:right;', renderer: BFwrk.Util.rendererMoney
		        },
                {header :'Importe DLS', width :100, sortable :true, dataIndex :'importeDls', css: 'text-align:right;', renderer: BFwrk.Util.rendererMoney
                },
                {header :'Total DLS', width :80, sortable :true, dataIndex :'totalDls', hidden:true, renderer: BFwrk.Util.rendererMoney,
                	renderer: function (value, meta, record) {
			            meta.attr = 'style=' + record.get('color');
			            return value;
			        },	
                },
                {header :'Id Grupo Rubros', width :80, sortable :true, dataIndex :'idGrupoCupo', hidden: true,
                	renderer: function (value, meta, record) {
			            meta.attr = 'style=' + record.get('color');
			            return value;
			        },	
                },
                {header :'Id Grupo Empresas', width :80, sortable: true, dataIndex :'idGrupoFlujo', hidden: true,
                	renderer: function (value, meta, record) {
			            meta.attr = 'style=' + record.get('color');
			            return value;
			        },
		        },
                {header :'Banco Pago', width :80, sortable :true, dataIndex :'descBancoPago', hidden: true,
		        	renderer: function (value, meta, record) {
			            meta.attr = 'style=' + record.get('color');
			            return value;
			        },	
                },
                {header :'Chequera', width :80, sortable :true, dataIndex :'idChequera', hidden: true,
                	renderer: function (value, meta, record) {
			            meta.attr = 'style=' + record.get('color');
			            return value;
			        },	
                },
                {header :'Concepto', width :200, sortable :true, dataIndex :'concepto',
                	renderer: function (value, meta, record) {
			            meta.attr = 'style=' + record.get('color');
			            return value;
			        },	
                },
                {header :'Id Banco Benef', width :80, sortable :true, dataIndex :'idBancoBenef', hidden: true,
                	renderer: function (value, meta, record) {
			            meta.attr = 'style=' + record.get('color');
			            return value;
			        },
                },
                {header :'Fec Recalculo', width :80, sortable :true, dataIndex :'fecRecalculo', hidden: true},
                {header :'Intercos', width :70, sortable :true, dataIndex :'numIntercos', hidden:true},
                {header :'Total Intercos', width :80, sortable :true, dataIndex :'totalIntercos', hidden:true}
            ]
        }),
        sm: NS.sm,
        columnLines: true,
        x:0,
        y:0,
        width:960,
        height:175,
        frame:true,
        listeners:{
        	rowdblClick:{
        		fn:function(e) {
        			var regSelec = NS.gridPagoPropuestas.getSelectionModel().getSelections();
          			if(regSelec.length > 0) {
	          			Ext.Msg.confirm('SET', '¿Desea visualizar el detalle?', function(btn) {
	          				if(btn == 'yes') {
	          					NS.storeDetaPro.baseParams.idGrupoEmpresa = parseInt(regSelec[0].get('idGrupoFlujo'));
	    		        		NS.storeDetaPro.baseParams.idGrupoRubro = parseInt(regSelec[0].get('idGrupoCupo'));
	    		        		NS.storeDetaPro.baseParams.cveControl = regSelec[0].get('cveControl');
	    		        		NS.storeDetaPro.baseParams.idUsuario1 = 0;
	    		        		NS.storeDetaPro.baseParams.idUsuario2 = 0;
	    		        		NS.storeDetaPro.baseParams.idUsuario3 = 0;
	    		        		NS.storeDetaPro.load();
	          					
	    		        		winDetalle.show();
	          				}
	          			});
          			}
        		}
        	},
        	click: {
        		fn:function(e) {
          			var sumMn=0;
          			var sumDls=0;
          			var totalMn=0;
          			var totalDls=0;
          			var regSelec = NS.gridPagoPropuestas.getSelectionModel().getSelections();
          			
          			for(var k=0;k<regSelec.length;k=k+1)
          			{
          				sumMn=sumMn+regSelec[k].get('importeMn');
          				sumDls=sumDls+regSelec[k].get('importeDls');
          				totalMn=totalMn+regSelec[k].get('importeMn');
          				totalDls=totalDls+regSelec[k].get('importeDls');
          			}
          			//Ext.getCmp(PF+'totalPagoMn').setValue(NS.formatNumber(totalMn));
          			Ext.getCmp(PF + 'totalPagoMn').setValue(NS.formatNumber(Math.round((totalMn)*100)/100));
          			Ext.getCmp(PF+'diferenciaMn').setValue(NS.formatNumber((NS.montoTotalMN-totalMn).toFixed(2)));
          			
          			Ext.getCmp(PF+'totalPagoDls').setValue(NS.formatNumber(totalDls.toFixed(2)));
          			Ext.getCmp(PF+'diferenciaDls').setValue(NS.formatNumber((NS.montoTotalDLS-totalDls).toFixed(2)));
        		}
        	}
        }
    });
	
	//******************************************************** Este codigo es para el detalle de las propuestas *****************************************//
	//Store detalle de las propuestas a pagar
	NS.storeDetaPro = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams: {idGrupoEmpresa: 0, idGrupoRubro: 0, cveControl: '', idUsuario1: 0, idUsuario2: 0, idUsuario3: 0},
		root: '',
		paramOrder:['idGrupoEmpresa','idGrupoRubro','cveControl','idUsuario1','idUsuario2','idUsuario3'],
		directFn: PagoDePropuestasSetAction.consultarDetalle, 
		fields: [
				{name: 'nomEmpresa'},//Grupo
				{name: 'noEmpresa'},//Nom. Empresa
				{name: 'poHeaders'},
				{name: 'noDocto'},//No. Docto
				{name: 'nomProveedor'},//Proveedor
				{name: 'importe'},//importe
				{name: 'fecValorOriginal'},//fecha de vencimiento
				{name: 'idDivisa'},//Divisa
				{name: 'importeMn'},//ImporteMN
				{name: 'descFormaPago'},//Forma Pago
				{name: 'fecPropuesta'},//Fec Prop
				{name: 'bancoPago'},//Banco de Pago
				{name: 'idChequera'},//Chequera de Pago
				{name: 'descGrupoCupo'},//Grupo de Rubros
				{name: 'idRubro'},//Rubro
				{name: 'importeOriginal'},//Importe Original
				{name: 'idDivisaOriginal'},//DivisaOriginal
				{name: 'beneficiario'},//Beneficiario
				{name: 'noFolioDet'},//No. Folio Det
				{name: 'concepto'},//Concepto
				{name: 'idBancoBenef'},//Id Banco Benef
				{name: 'idChequeraBenef'},//Id Chequera Benef
				{name: 'noFactura'},//No. Factura
				{name: 'diasInv'},//Dias Vto
				{name: 'origenMov'},//Origen
				{name: 'noCliente'},//No. Persona
				{name: 'noCheque'},//No. Cheque
				{name: 'idBanco'}//No. Banco pagador
		],
		listeners: {
			load: function(s, records) {
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeDetaPro, msg: "Buscando..."});
				
				if(records.length == null || records.length <= 0)
					Ext.Msg.alert('SET', 'No hay detalle de las propuestas!!');
			}
		}
	});
	
	//Para indicar el modo de seleccion de los registros en el Grid
	NS.selecDeta = new Ext.grid.CheckboxSelectionModel({
		singleSelect: false
	});
	
	//Columna de Seleccion  
	NS.columDeta = new Ext.grid.ColumnModel([
	    //NS.selecDeta,
	    {header :'Empresa', width :180, sortable :true, dataIndex :'nomEmpresa'},
	    {header :'No.Docto', width :60, sortable :true, dataIndex :'noDocto'},
	    {header :'Folio Compensacion', width :60, sortable :true, dataIndex :'poHeaders'},
	    {header :'Beneficiario', width :200, sortable :true, dataIndex :'beneficiario'},
	    {header :'Importe MN', width :95, sortable :true, dataIndex :'importeMn', renderer: BFwrk.Util.rendererMoney},
	    {header :'Concepto', width :250, sortable :true, dataIndex :'concepto'},
	    {header :'No.Empresa', width :90, sortable :true, dataIndex :'noEmpresa'},
	    {header :'Forma Pago', width :120, sortable :true, dataIndex :'descFormaPago'},
	    {header :'Fecha Venc.', width :100, sortable :true, dataIndex :'fecValorOriginal'},
	    {header :'Fec Prop', width :100, sortable :true, dataIndex :'fecPropuesta'},
	    {header :'Banco Pago', width :90, sortable :true, dataIndex :'bancoPago'},
	    {header :'Chequera de Pago', width :100, sortable :true, dataIndex :'idChequera'},
	    {header :'Proveedor', width :80, sortable :true, dataIndex :'nomProveedor', hidden: true},
	    {header :'Importe', width :90, sortable :true, dataIndex :'importe', renderer: BFwrk.Util.rendererMoney},
	    {header :'Divisa', width :80, sortable :true, dataIndex :'idDivisa'},
	    {
			header :'Grupo de Rubros',
			width :80,
			sortable :true,
			dataIndex :'descGrupoCupo'
		},{
			header :'Rubro',
			width :80,
			sortable :true,
			dataIndex :'idRubro'
		},{
			header :'Importe Original',
			width :90,
			sortable :true,
			dataIndex :'importeOriginal',
			renderer: BFwrk.Util.rendererMoney
		},{
			header :'Divisa Original',
			width :80,
			sortable :true,
			dataIndex :'idDivisaOriginal'
		},{
			header :'No.Folio Det',
			width :80,
			sortable :true,
			dataIndex :'noFolioDet'
		},{
			header :'Id Banco Benef',
			width :80,
			sortable :true,
			dataIndex :'idBancoBenef'
		},{
			header :'Id Chequera Benef',
			width :80,
			sortable :true,
			dataIndex :'idChequeraBenef'
		},{
			header :'No. Factura',
			width :80,
			sortable :true,
			dataIndex :'noFactura'
		},{
			header :'Dias Vto',
			width :80,
			sortable :true,
			dataIndex :'diasInv'
		},{
			header :'Origen',
			width :80,
			sortable :true,
			dataIndex :'origenMov'
		},{
			header :'No. Persona',
			width :80,
			sortable :true,
			dataIndex :'noCliente'
		},{
			header :'No. Cheque',
			width :80,
			sortable :true,
			dataIndex :'noCheque'
		}
	]);
	
	//Grid para mostrar los datos seleccionados
	NS.gridDetaProp = new Ext.grid.GridPanel({
		store: NS.storeDetaPro,
		id: 'gridDetaProp',
		x: 5,
		y: 5,
		cm: NS.columDeta,
		//sm: NS.selecDeta,
		width: 680,
	    height: 310,
	    stripeRows: true,
	    columnLines: true,
	    title: ''
	});
	
	//Contenedor de detalle de las propuestas
	NS.contDetalle = new Ext.form.FieldSet({
		title: 'Movimientos',
		layout: 'absolute',
	    items: [
	           NS.gridDetaProp,
	           {
	        	    xtype: 'button',
	        	   	text: 'Cerrar',
	        	   	x: 580,
					y: 330,
					width: 80,
					height: 22,
					listeners: {
						click:{
							fn:function(e){
								winDetalle.hide();
							}
						}
					}
	           }
	           ]
	});
	//ventana para el detalle de las propuestas a pagar
	var winDetalle = new Ext.Window({
		title: 'Detalle de Pago de Propuestas',
		modal: true,
		shadow: true,
		width: 750,
	   	height: 450,
	   	layout: 'fit',
	   	plain: true,
	    bodyStyle: 'padding:10px;',
	    buttonAlign: 'center',
	    closable: false,
	   	items: [
	   	        NS.contDetalle
	   	        ]
	});//Termina ventana

	//**************************************************** Termina el código para el detalle de las propuestas *****************************************//	
	//funcion para buscar en el grid
	NS.buscar = function() {
		//if(NS.idGrupoEmpresa<0 && NS.idDivision==0) {
		if(NS.idGrupoEmpresa<0) {
			Ext.Msg.alert('Información SET','Seleccione un Grupos de empresas.');
			return;
		}
		
		//llamada para los saldos de las chequeras
		PagoDePropuestasSetAction.obtenerSaldosChequeras(NS.idGrupoEmpresa,NS.idDivision,NS.idUsuario, function(result, e){
			var aux=result.saldoInicialMn;
			Ext.getCmp(PF+'txtSaldoInicialMN').setValue(NS.formatNumber(aux));
			Ext.getCmp(PF+'txtInversionMN').setValue(NS.formatNumber(result.inversionesMn));
			Ext.getCmp(PF+'txtCargoMN').setValue(NS.formatNumber(result.cargoMn));
			Ext.getCmp(PF+'txtAbonoMN').setValue(NS.formatNumber(result.abonoMn));
			Ext.getCmp(PF+'txtSaldoFinalMN').setValue(NS.formatNumber(result.saldoFinalMn));
			
			Ext.getCmp(PF+'txtSaldoInicialDLS').setValue(NS.formatNumber(result.saldoInicialDls));
			Ext.getCmp(PF+'txtInversionDLS').setValue(NS.formatNumber(result.inversionesDls));
			Ext.getCmp(PF+'txtCargoDLS').setValue(NS.formatNumber(result.cargoDls));
			Ext.getCmp(PF+'txtAbonoDLS').setValue(NS.formatNumber(result.abonoDls));
			Ext.getCmp(PF+'txtSaldoFinalDLS').setValue(NS.formatNumber(result.saldoFinalDls));
			
			//proceso para el llenado del grid
			NS.storePagoPropuestas.baseParams.fecIni = cambiarFecha('' + Ext.getCmp(PF + 'fechaInicial').getValue());
			NS.storePagoPropuestas.baseParams.fecFin = cambiarFecha('' + Ext.getCmp(PF + 'fechaFinal').getValue());
			NS.storePagoPropuestas.baseParams.idGrupoEmpresa = NS.idGrupoEmpresa;
			var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storePagoPropuestas, msg:"Buscando..."});
			NS.storePagoPropuestas.load();
		});
	};
	
	//Función para la llamada al reporte de pago de propuestas
	NS.imprimirReporte = function(tipoReporte, cveControl){
		var regGrid = NS.storePagoPropuestas.data.items;
		
		if(regGrid.length > 0) {
			if(tipoReporte == 1)
				strParams = '?nomReporte=PagoPropuestas';
			else if(tipoReporte == 2) {
				strParams = '?nomReporte=PagoPropuestasDetalle';
				
				var regSelec = NS.gridPagoPropuestas.getSelectionModel().getSelections();
				
				for(var i=0; i<regSelec.length; i++) {
					if(cveControl == '')
						cveControl = "'" + regSelec[i].get('cveControl') + "'";
					else
						cveControl += ',' + "'" + regSelec[i].get('cveControl') + "'";
				}
			}else if(tipoReporte == 3) {
				strParams = '?nomReporte=PagoPropuestasDetalle';
			}
			strParams += '&'+'nomParam1=idGrupoEmpresa';
			strParams += '&'+'valParam1=' + NS.idGrupoEmpresa;
			strParams += '&'+'nomParam2=idDivision';
			strParams += '&'+'valParam2=' + NS.idDivision;
			strParams += '&'+'nomParam3=fecIni';
			strParams += '&'+'valParam3=' + cambiarFecha('' + Ext.getCmp(PF + 'fechaInicial').getValue());
			strParams += '&'+'nomParam4=fecFin';
			strParams += '&'+'valParam4=' + cambiarFecha('' + Ext.getCmp(PF + 'fechaFinal').getValue());
			strParams += '&'+'nomParam5=chkLocales';
			strParams += '&'+'valParam5=' + NS.chkLocales;
			strParams += '&'+'nomParam6=idUsuario';
			strParams += '&'+'valParam6=' + NS.idUsuario;
			strParams += '&'+'nomParam7=fecHoy';
			strParams += '&'+'valParam7=' + apps.SET.FEC_HOY;
			strParams += '&'+'nomParam8=cveControl';
			strParams += '&'+'valParam8=' + cveControl;
			strParams += '&'+'nomParam9=tipoReporte';
			strParams += '&'+'valParam9=' + tipoReporte;
			
			window.open("/SET/jsp/bfrmwrk/gnrators/genJasperPDF.jsp" + strParams);
		}else
			Ext.Msg.alert('SET', 'No existe información para imprimir!!');
		return;
	};
	//inicio ventana
NS.PagoPropuestas =new Ext.form.FormPanel({
    title: 'Pago De Propuestas',
    width: 1020,
    height: 600,
    padding: 10,
    frame: true,
    autoScroll: true,
    layout: 'absolute',
    id: PF+'contenedorPagoPropuestas',
    name: PF+'contenedorPagoPropuestas',
    renderTo: NS.tabContId,
       items :[
            {
                xtype: 'fieldset',
                title: '',
                width: 1010,
                height: 530,
                x: 20,
                y: 5,
                layout: 'absolute',
                id: 'contPrincipal',
	            items: [
					{
					    xtype: 'fieldset',
					    title: 'Agrupar:',
					    x:0,
					    y: 445,
					    hidden: true, 
					    width: 200,
					    height: 60,
					    layout: 'absolute',
					    id: 'fSetAgrupación',
					    items: [
					        {
					            xtype: 'checkbox',
					            x: 0,
					            y: 0,
					            boxLabel: 'Cheques',
					            name: 'chkCheques',
					            id: 'chkCheques',
					            listeners : 
					            {
					            	check : {
					            		fn : function(opt, valor){
					            			if(valor)
					            				NS.chkCheques = true;
					            			else
					            				NS.chkCheques = false;
					            		}
					            	}
					            } 
					        },
					        {
					            xtype: 'checkbox',
					            x: 80,
					            y: 0,
					            boxLabel: 'Transferencias',
					            name: 'chkTransfers',
					            id: 'chkTransfers',
					            listeners : 
					            {
					            	check : {
					            		fn : function(opt, valor)
					            		{
					            			if(valor)
					            				NS.chkTransfers = true;
					            			else
					            				NS.chkTransfers = false;
					            		}
					            	}
					            } 
					        }					        
					    ]
					},	             
                {
                    xtype: 'button',
                    text: 'Limpiar',
                    x: 800,
                    y: 480,
                    width: 80,
                    id: 'btnLimpiar',
                    listeners:{
                        	click:{
                        	fn:function(e){
                        	
                       				NS.idUsuario=2;
									NS.idGrupoEmpresa=0;
									NS.idDivision=0;	
									NS.montoTotalMN=0;
									NS.montoTotalDLS=0;
									
                        			NS.cmbGrupoEmpresas.reset();
                        			//NS.cmbDivision.reset();
                        			 
                        	
                        			NS.gridPagoPropuestas.store.removeAll();
									NS.gridPagoPropuestas.getView().refresh();
									
									NS.PagoPropuestas.getForm().reset();
                        		}
                        	}
                        }
                },
                ////
                {
                    xtype: 'button',
                    text: 'Nueva Prupuesta',
                    x: 490,
                    y: 480,
                    width: 110,
                    hidden: true,
                    id: 'btnNuevaPropuesta',
                    listeners:{
                    	click:{
                        	fn:function(e){
                        		NS.generarSubpropuestas();
       						}
                    	}
                    }
                },
                
                {
                    xtype: 'button',
                    text: 'Excel',
                    x: 615,
                    y: 480,
                    width: 80,
                    id: 'btnGenerarExcel',
                    listeners:{
                    	click:{
                        	fn:function(e){
                        		NS.generarExcel();
       						}//fin de la funcion
                    	}
                    }
                },
                
                ////
                {
                    xtype: 'fieldset',
                    title: 'Propuestas',
                    x: 0,
                    y: 180,
                    width: 985,
                    height: 210,
                    layout: 'absolute',
                    id: 'contPropuestas',
                    items:[
                    	 NS.gridPagoPropuestas
                    ]
                },
                {
                    xtype: 'fieldset',
                    title: 'Resumen de Todas las Chequeras',
                    layout: 'absolute',
                    width: 485,
                    height: 175,
                    hidden: true,
                    x: 500,
                    y: 0,
                    id: 'resumenChequeras',
                    items: [
                        {
                            xtype: 'textfield',
                            x: 130,
                            y: 15,
                            name: PF+'txtSaldoInicialMN',
                            id: PF+'txtSaldoInicialMN'
                        },
                        {
                            xtype: 'textfield',
                            x: 280,
                            y: 15,
                            name: PF+'txtSaldoInicialDLS',
                            id: PF+'txtSaldoInicialDLS'
                        },
                        {
                            xtype: 'textfield',
                            x: 130,
                            y: 40,
                            name: PF+'txtInversionMN',
                            id: PF+'txtInversionMN'
                        },
                        {
                            xtype: 'textfield',
                            x: 280,
                            y: 40,
                            name: PF+'txtInversionDLS',
                            id: PF+'txtInversionDLS'
                        },
                        {
                            xtype: 'textfield',
                            x: 130,
                            y: 65,
                            name: PF+'txtCargoMN',
                            id: PF+'txtCargoMN'
                        },
                        {
                            xtype: 'textfield',
                            x: 280,
                            y: 65,
                            name: PF+'txtCargoDLS',
                            id: PF+'txtCargoDLS'
                        },
                        {
                            xtype: 'textfield',
                            x: 130,
                            y: 90,
                            name: PF+'txtAbonoMN',
                            id: PF+'txtAbonoMN'
                        },
                        {
                            xtype: 'textfield',
                            x: 280,
                            y: 90,
                            name: PF+'txtAbonoDLS',
                            id: PF+'txtAbonoDLS'
                        },
                        {
                            xtype: 'textfield',
                            x: 130,
                            y: 115,
                            name: PF+'txtSaldoFinalMN',
                            id: PF+'txtSaldoFinalMN'
                        },
                        {
                            xtype: 'textfield',
                            x: 280,
                            y: 115,
                            name: PF+'txtSaldoFinalDLS',
                            id: PF+'txtSaldoFinalDLS'
                        },
                        {
                            xtype: 'label',
                            text: 'MN',
                            x: 190,
                            y: 0
                        },
                        {
                            xtype: 'label',
                            text: 'DLS',
                            x: 335,
                            y: 0
                        },
                        {
                            xtype: 'label',
                            text: 'Saldo Inicial',
                            x: 40,
                            y: 20
                        },
                        {
                            xtype: 'label',
                            text: 'Inversiones',
                            x: 40,
                            y: 45
                        },
                        {
                            xtype: 'label',
                            text: 'Cargos',
                            x: 40,
                            y: 70
                        },
                        {
                            xtype: 'label',
                            text: 'Abonos',
                            x: 40,
                            y: 95
                        },
                        {
                            xtype: 'label',
                            text: 'Saldo Final',
                            x: 40,
                            y: 120
                        }
                    ]
                },
                //Panel de busqueda inicia.
                {
                    xtype: 'fieldset',
                    title: 'Búsqueda',
                    layout: 'absolute',
                    width: 340,
                    height: 175,
                    //id: 'conBusqueda',
                    id: PF+'conBusqueda',
                    name: PF+'conBusqueda',
                    items: [
                        {
                            xtype: 'checkbox',
                            x: 340,
                            y: 15,
                            boxLabel: 'Sólo locales',
                            hidden: true,
                            listeners:{
	                        check:{
	                        		fn:function(checkBox,valor)
	                        		{
	                        		if(valor==true) {
	                     				NS.storePagoPropuestas.baseParams.chkLocal=''+valor;
	                     				NS.chkLocales = 'true';
	                        		}else{
	                     			  	NS.storePagoPropuestas.baseParams.chkLocal='false';
	                     			  	NS.chkLocales = 'false';
		                        	}
	                        		}
	                        }
	                      }
                        },
                       NS.cmbGrupoEmpresas,
                       /*,NS.cmbDivision,*/
                       {
                           xtype: 'button',
                           text: 'Grafica',
                           x: 40,
                           y: 115,
                           width: 80,
                           height: 22,
                           disabled: true,
                           hidden: true,
                           id: PF+'cmdGrafica',
                           name: PF+'cmdGrafica',
                           listeners:{
                        	click:{
	        			   		fn:function(e){
		                        	var sName = NS.idGrupoEmpresa+""+NS.idDivision+""+NS.storePagoPropuestas.baseParams.chkLocal;
		            				
			    		            Ext.getCmp('panel1').update('<img src="'+window.location.protocol+"//"+window.location.host+"/graficaSet/"+apps.SET.iUserId+'PagoPropuestas'+sName+'PC.jpg" border="0"/>');
			    					Ext.getCmp('panel2').update('<img src="'+window.location.protocol+"//"+window.location.host+"/graficaSet/"+apps.SET.iUserId+'PagoPropuestas'+sName+'BG.jpg" border="0"/>');
			    					Ext.getCmp('panel3').update('<img src="'+window.location.protocol+"//"+window.location.host+"/graficaSet/"+apps.SET.iUserId+'PagoPropuestas'+sName+'LG.jpg" border="0"/>');
			    					Ext.Msg.alert('SET', 'Graficas listas');
	        		   			}
        		   		 	}
                           }
                       },
                        {
                            xtype: 'button',
                            text: 'Buscar',
                            x: 130,
                            y: 115,
                            width: 80,
                            height: 22,
                            id: PF+'btnBuscar',
                            name: PF+'btnBuscar',
                            listeners:{
                            	click:{
                            		fn:function(e){
                            			NS.buscar();
                            		}
                            	}
                            }
                        },
                        {
                            xtype: 'label',
                            text: 'Empresas',
                            x: 10,
                            y: 0
                        },
                        /*{
                            xtype: 'label',
                            text: 'Division',
                            x: 10,
                            y: 50
                        },*/
                        {
                            xtype: 'label',
                            text: 'Rango de Fechas',
                            x: 10,
                            y: 50
                        },
                      {
                   		xtype:'datefield',
                   		format:'d/m/Y',
                   		id:PF+'fechaInicial',
                   		name:PF+'fechaInicial',
                   		value: apps.SET.FEC_HOY,
                   		x: 10,
                   		y: 65,
                   		width:100,
                   		allowBlank: false,
                   		blankText:'La fecha inicial es requerida',
                   		listeners:{
                   			change:{
                   				fn:function(caja,valor){
									NS.storePagoPropuestas.baseParams.fecIni=cambiarFecha(''+caja.getValue());
									//alert('fechaIni: '+cambiarFecha(''+caja.getValue()));
                   				}
                   			}
                   		}
                   	},
                      {
                   		xtype:'datefield',
                   		format:'d/m/Y',
                   		id:PF+'fechaFinal',
                   		name:PF+'fechaFinal',
                   		value: apps.SET.FEC_HOY,
                   		x:130,
                   		y:65,
                   		width:100,
                   		allowBlank: false,
                   		blankText:'La fecha final es requerida',
                   		listeners:{
                   			change:{
                   				fn:function(caja,valor){
                   					if(Ext.getCmp(PF+'fechaInicial').getValue()>valor)
									{
										Ext.Msg.alert('información SET','La fecha inicial debe ser menor a la final');
										return;
									}
									NS.storePagoPropuestas.baseParams.fecFin=cambiarFecha(''+caja.getValue());	
								
                   				}
                   			}
                   		}
                   	},
                        {
                            xtype: 'button',
                            text: 'Imprimir.',
                            x: 220,
                            y: 115,
                            width: 80,
                            id: PF + 'btnImprimir1',
                            listeners: {
		                   		click: {
			                   		fn:function(e) {
				                   		NS.imprimirReporte(1, '');
                   					}
			                   	}
		                   	}
                        }
                    ]
                },
                {
                    xtype: 'button',
                    text: 'Ejecutar',
                    x: 710,
                    y: 480,
                    width: 80,
                    name: PF+'btnEjecutar',
                    id: PF+'btnEjecutar',
                    listeners: {
                    	click: {
                    		fn : function(e) {
                    			var pru = 0;
                    			var matrizValidar = new Array ();//matriz para validar los mensajes de usuario 
                    			var matrizPagProp = new Array (); //para mandar una lista con solo los reg a pagar
                				var registrosSelec = NS.gridPagoPropuestas.getSelectionModel().getSelections();
                				var cveControlReport = "";
                				
                				if(registrosSelec.length===0) {
                					Ext.Msg.alert('Información SET','Es necesario seleccionar algun registro');
                					return;
                				}
                				Ext.MessageBox.show({
							       title : 'Información SET',
							       msg : 'Pagando propuestas, espere por favor...',
							       width : 300,
							       wait : true,
							       progress:true,
							       waitConfig : {interval:200}//,
							       //icon :'lupita'
						           //icon :'forma_alta', //custom class in msg-box.html
						           ///animateTarget : 'mb7'
						   		});
                				for(var i=0; i<registrosSelec.length; i++) {
									var regValidar = {};
	               					regValidar.agrupaCheEmp = registrosSelec[i].get('empChe'); //alberto
	               					regValidar.cveControl = registrosSelec[i].get('cveControl');
	               					regValidar.fecPropuesta =registrosSelec[i].get('fecPropuesta');
	               					regValidar.idGrupoEmpresa = registrosSelec[i].get('idGrupoFlujo');
	               					regValidar.idGrupoRubro = registrosSelec[i].get('idGrupoCupo');
	               					matrizValidar[i] = regValidar;
	               					
	               					if(cveControlReport == "")
	               						cveControlReport = "'" + registrosSelec[i].get('cveControl') + "'";
	               					else
	               						cveControlReport += ',' + "'" + registrosSelec[i].get('cveControl') + "'";
                				}
                				
                				
                				var jsonString = Ext.util.JSON.encode(matrizValidar);	
									PagoDePropuestasSetAction.validarCpaVtaTransfer(jsonString, function(result, e){
										if(result!=null && result!=undefined && result!='')
										{
											for(var c=0;c<result.length;c=c+1)
											{
												var regPagProp = {};
												if((result[c].mensaje!=null && result[c].mensaje!='') 
												&& (result[c].mensaje2==null || result[c].mensaje2=='' || result[c].mensaje2==undefined))
												{
											 
													if(confirm(result[c].cveControl+' '+result[c].mensaje+', ¿Desea continuar?'))
													{
														regPagProp.agrupaCheEmp = result[c].agrupaEmpChe;
					                					regPagProp.cveControl = result[c].cveControl;
					                					regPagProp.fecPropuesta = result[c].fechaPago;
					                					regPagProp.idGrupoEmpresa = result[c].idGrupoEmpresas;
					                					regPagProp.idGrupoRubro = result[c].idGrupoRubros;					                					
					                				
					                					matrizPagProp[c] = regPagProp;
													}
													else{
														c=result.length;//para salir solo del for
													}
												}
												else if((result[c].mensaje!=null && result[c].mensaje!='') 
												&& (result[c].mensaje2!=null || result[c].mensaje2!=''))//Valida los dos mensajes 
												{
											 
													if((confirm(result[c].cveControl+' '+result[c].mensaje+', ¿Desea continuar?'))
														&& (confirm(result[c].cveControl+' '+result[c].mensaje2+', ¿Desea continuar?')))
														{
															regPagProp.agrupaCheEmp = result[c].agrupaEmpChe;
						                					regPagProp.cveControl = result[c].cveControl;
						                					regPagProp.fecPropuesta = result[c].fechaPago;
						                					regPagProp.idGrupoEmpresa = result[c].idGrupoEmpresas;
						                					regPagProp.idGrupoRubro = result[c].idGrupoRubros;
						                				
						                					matrizPagProp[c] = regPagProp;
														}
														else{
															c=result.length;//para salir solo del for
														}
												}
												else{
													regPagProp.agrupaCheEmp = result[c].agrupaEmpChe;
				                					regPagProp.cveControl = result[c].cveControl;
				                					regPagProp.fecPropuesta = result[c].fechaPago;
				                					regPagProp.idGrupoEmpresa = result[c].idGrupoEmpresas;
				                					regPagProp.idGrupoRubro = result[c].idGrupoRubros;
				                					 
				                					//Valido agrupación de cheques...
				                					if (NS.chkCheques == true)				                					
				                						regPagProp.agrupaCheques = "SI";
				                					else
				                						regPagProp.agrupaCheques = "NO";
				                					
				                					//Valido agrupación de transfers...
				                					if (NS.chkTransfers == true)				                					
				                						regPagProp.agrupaTransfers = "SI";
				                					else
				                						regPagProp.agrupaTransfers = "NO";
				                			
				                					matrizPagProp[c] = regPagProp;
												}
											}
										}

		                				var jsonString4 = Ext.util.JSON.encode(matrizPagProp);
		                				PagoDePropuestasSetAction.insetarZexpFact(jsonString4, apps.SET.FEC_HOY, NS.idUsuario, function(result, e){
		                					
		                					if(result!==null && result!==undefined && result!==''){
		                						
		                						if(result.estatus==true){
		                									                							/*Ext.Msg.alert('Información SET', ''+result.salida, function(btn){
			                						    if (btn == 'ok'){
			                						    	
			                						        // process text value...
			                						    	NS.buscar();
			                						    	
			                						    }
			                						});*/
		                							Ext.Msg.alert('Información SET', 'Los datos se pagaron exitosamente', function(btn){
			                						    if (btn == 'ok'){
			                						    	NS.buscar();
			                						    }
			                						});
		                							
		                						}else{
		                							Ext.Msg.alert('Información SET', ''+result.msgError, function(btn){
			                						    if (btn == 'ok'){
			                						    	NS.buscar();
			                						    }
			                						});
		                						}
		                					

		                					}
		                					
		                				});
		                				
									});
                    		}
                    	}
                    }
                },
                {
                    xtype: 'button',
                    text: 'Imprimir',
                    x: 890,
                    y: 480,
                    width: 80,
                    id: PF + 'btnImprimir',
                    listeners: {
                   		click: {
	                   		fn:function(e) {
		                   		NS.imprimirReporte(2, '');
		                   	}
	                   	}
                   	}
                },
                {
                    xtype: 'textfield',
                    x: 830,
                    y: 415,
                    width: 140,
                    name: PF+'diferenciaDls',
                    id: PF+'diferenciaDls',
                    readOnly: true
                },
                {
                    xtype: 'textfield',
                    x: 670,
                    y: 415,
                    width: 140,
                    name: PF+'totalPagoDls',
                    id: PF+'totalPagoDls',
                    readOnly: true
                },
                {
                    xtype: 'textfield',
                    x: 510,
                    y: 415,
                    width: 140,
                    name: PF+'montoPagPenDls',
                    id: PF+'montoPagPenDls',
                    readOnly: true
                },
                {
                    xtype: 'textfield',
                    x: 340,
                    y: 415,
                    width: 140,
                    name: PF+'diferenciaMn',
                    id: PF+'diferenciaMn',
                    readOnly: true
                },
                {
                    xtype: 'textfield',
                    x: 180,
                    y: 415,
                    width: 140,
                    name: PF+'totalPagoMn',
                    id: PF+'totalPagoMn',
                    readOnly: true
                },
                {
                    xtype: 'textfield',
                    x: 20,
                    y: 415,
                    width: 140,
                    name: PF+'montoPagPenMn',
                    id: PF+'montoPagPenMn',
                    readOnly: true
                },
                {
                    xtype: 'label',
                    text: 'T. de Pagos Pendientes M.N.',
                    x: 20,
                    y: 400,
                    width: 170
                },
                {
                    xtype: 'label',
                    text: 'Total del Pago M.N.',
                    x: 180,
                    y: 400
                },
                {
                    xtype: 'label',
                    text: 'Diferencia M.N.',
                    x: 340,
                    y: 400
                },
                {
                    xtype: 'label',
                    text: 'T. de Pagos Pendientes Dlls',
                    x: 510,
                    y: 400,
                    width: 140
                },
                {
                    xtype: 'label',
                    text: 'Total del Pago Dlls',
                    x: 670,
                    y: 400
                },
                {
                    xtype: 'label',
                    text: 'Diferencia Dlls',
                    x: 830,
                    y: 400
                }/*,
                {
                    xtype: 'progress',
                    value: 0.4,
                    x: 490,
                    y: 510,
                    width: 230,
                    text: 'Proceso de la tarea',
                    id : PF+'barProceso',
                    name : PF+'barProceso'
                }*/
            ]
       	 }
        ]
	});
NS.generarExcel = function(){
	var regSelec = NS.gridPagoPropuestas.getSelectionModel().getSelections();
	if(regSelec.length > 0) {
		var cveControl = '';
		for(var k=0;k<regSelec.length;k=k+1){
			cveControl = cveControl +"'"+ regSelec[k].get('cveControl')+"',";
		}
		parametros='?nomReporte=excelPagoPropuestas';
		parametros+='&nomParam1=clave';
		parametros+='&valParam1=' + cveControl;
		window.open("/SET/jsp/bfrmwrk/gnrators/abreExcel.jsp"+parametros);
	}else{
		Ext.Msg.alert('SET','Seleccionar una propuesta.');
		return;
	}// fin 
}

	//////generar subpropuestas//////
NS.generarSubpropuestas = function(num,prefix){
	
	
	var regSelec = NS.gridPagoPropuestas.getSelectionModel().getSelections();
	 
	if(regSelec.length > 0) {	
		for(var k=0;k<regSelec.length && regSelec[k].get('color')== 'color:#090DFA' ;k=k+1){
			
			if(regSelec[k].get('color')!= '')
			var cveControl = regSelec[k].get('cveControl');
			var idGrupoFlujo = parseInt(regSelec[k].get('idGrupoFlujo'));
			var idGrupoCupo = parseInt(regSelec[k].get('idGrupoCupo'));
			NS.generarSubPropuesta(cveControl, idGrupoFlujo,idGrupoCupo);			
			
			
		}
	}else{
		Ext.Msg.alert('SET','Seleccionar una propuesta.');
		return;
	}// fin el if de valida seleccionadas
	//Ext.MessageBox.hide();
}// fin de generarSubpropuestas

NS.generarSubPropuesta = function(cveControlK, idGrupoFlujoK,idGrupoCupoK){
	Ext.MessageBox.show({
	       title : 'Información SET',
	       msg : 'Generando propuestas, espere por favor...',
	       width : 300,
	       wait : true,
	       progress:true,
	       waitConfig : {interval:200}//,
		});
	
	var idUsuario1 = 0;
	var idUsuario2 = 0;
	var idUsuario3 = 0;
	//alert(cveControlK);
	PagoDePropuestasSetAction.consultarDetallePropuestasNoPagadas(
			idGrupoFlujoK,idGrupoCupoK,cveControlK,idUsuario1,idUsuario2,idUsuario3, function(result, e){
				if(result!=null && result!=undefined && result!=''){
					
					var matrizPagProp = new Array ();
					var folios = '', noDoctos = '', empresa = '';
					var montoMaximo = 0;
					
					for(var c=0;c<result.length;c=c+1){
						//alert(result[c].noFolioDet);
						folios = folios + result[c].noFolioDet + ",";
						noDoctos = noDoctos + result[c].noDocto + ",";
						montoMaximo = montoMaximo + result[c].importe + ",";
					
					}
					//alert('folios '+folios+' doctos'+noDoctos+' monto maximo '+ '' + montoMaximo);
					 var stencero = "";
					 stencero = "CUPO EN CERO:";
					  
					 if(cveControlK != '' || cveControlK != null || cveControlK != undefined){
						 
						 var nvaProp = cveControlK + '-1';
						 
						 if(numCaracter(nvaProp)>1){
							 nvaProp = cveControlK;
						 }
						 
					 }else{
						 Ext.Msg.alert('SET','La propuesta seleccionada no tiene Id.');
						 return; 
					 }
					 ////////////////////////////////////////////////////////////////
					 
					 
					// alert(nvaProp);
					//Verificamos que no exista la nueva propuesta, si existe se va incrementando el consecutivo del final
					 PagoDePropuestasSetAction.validaCvePropuesta(nvaProp, function(result, e){
						 
						 if(e.message=="Unable to connect to the server."){
								Ext.Msg.alert('SET','Error de conexión al servidor');
								return;
							}
						 
						 if(result != null && result != '' && result != undefined) {								
								
							 if(result.error != ''){
								 Ext.Msg.alert('SET',result.error);
								 return;
							 }else if(result.mensaje != ''){
								 //result.mensaje tiene la nueva clave de la propuesta.
								 //inicia actualizacion
								 
								 nvaProp = result.mensaje;
								 //alert(apps.SET.FEC_HOY);
								 var fecha = apps.SET.FEC_HOY;
								 
								// alert('cve nueaaa '+nvaProp+'12 '+montoMaximo+' cve vieja'+cveControlK+' usuario '+NS.idUsuario+' fecha '+fecha);
								 
								 PagoDePropuestasSetAction.insertaSubPropuesta(parseFloat(montoMaximo), ''+nvaProp, ''+cveControlK, 
										 										parseInt(NS.idUsuario), ''+fecha, 
										 										function(result, e){
									
									 if(e.message=="Unable to connect to the server."){
											Ext.Msg.alert('SET','Error de conexión al servidor');
											return;
									}
									 
									 if(result != null && result != '' && result != undefined) {								
											
										 if(result.error != ''){
											 Ext.Msg.alert('SET',result.error);
											 
											
											 return;
										 }else if(result.mensaje != ''){
											 
											 //var sDescModificado = "Se Creo Sub Propuesta " + nvaProp + " a partir de propuesta " + NS.seleccionPropuestaGlobal.get('cveControl');
											 
											 noDoctos = noDoctos.substring(0, noDoctos.length -1);
											// alert('  1'+noDoctos);
											// alert(nvaProp+' nueve '+noDoctos+' ck'+cveControlK);
											 PagoDePropuestasSetAction.actualizaPropuesta(nvaProp, noDoctos, 
										 									cveControlK,fecha, function(result, e){
													
												 if(e.message=="Unable to connect to the server."){
														Ext.Msg.alert('SET','Error de conexión al servidor');
														return;
													}
												 
												if(result != null && result != '' && result != undefined) {								
													 if(result.error != ''){
														 Ext.Msg.alert('SET',result.error);
														 
														 return;
													 }else if(result.mensaje != ''){
														 //sDescModificado = "Se Actualizan fecha propuesta,valor y clave del pago para " +nvaProp;
														 
														 //Actualiza montos de la propuesta original
														 PagoDePropuestasSetAction.actualizaMontoPropuesta(parseFloat(montoMaximo), cveControlK, 
																 										stencero, function(result, e){
															 if(e.message=="Unable to connect to the server."){
																	Ext.Msg.alert('SET','Error de conexión al servidor');
																	return;
																}
															 
															if(result != null && result != '' && result != undefined) {								
																	
																 if(result.error != ''){
																	 Ext.Msg.alert('SET',result.error);
																	
																	//Inserta en bitacora propuesta
																	 
																	 return;
																 }else if(result.mensaje != ''){
																	
																	 //sDescModificado = "SE Actualizo Monto Maximo de propuesta por " + montoMaximo;
																	 //Inserta bitacora_propuesta
																	 Ext.Msg.alert('SET','Proceso terminado con éxito.');
																	 NS.buscar();
																	 //NS.limpiarTodo();
																 }
															}
															}); //Fin actualiza montos.
														 
													 }
												}
											}); //Fin actualiza propuesta
											 
										 }
									 }
								 }); //Fin inserta propuesta
							        
							 }
							
						 }
						 
					 }); //Fin validaCvePropiuesta
					
					 
					 
					 ///////////////////////////////////////////////////////////////
				}else{
					Ext.Msg.alert('SET','No existen datos para la prupuesta');
					return;
				}
				
				
	 });
		 
	
	

};

function numCaracter(cveControl){
	var numGuiones = 0;
	var posIni = cveControl.indexOf('-');
	
    if(posIni > 0){
    	numGuiones = numGuiones + 1
    }
        
    for(var i = posIni+1; i <cveControl.length; i++){
    	if(cveControl[i] == '-'){
    		numGuiones = numGuiones + 1;
    	}
    }
    return numGuiones;
}


	
	NS.PagoPropuestas.setSize(Ext.get(NS.tabContId).getWidth(), Ext.get(NS.tabContId).getHeight());
///		staticCheck("#gridPagoPropuestas","#gridPagoPropuestas",8,".x-grid3-scroller",false);
	
});//Termina Onready