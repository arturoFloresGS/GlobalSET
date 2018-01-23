Ext.onReady(function()
{
	var NS = Ext.namespace('apps.SET.Impresion.Mantenimientos.ParametrosCarta');
	NS.tabContId = apps.SET.tabContainerId;
	NS.idUsuario = apps.SET.iUserId;
	//NS.noEmpresa = apps.SET.noEmpresa;
	NS.fecHoy = apps.SET.FEC_HOY ;//BFwrk.Util.changeStringToDate(apps.SET.FEC_HOY);
	var PF = apps.SET.tabID + '.';
	Ext.Direct.addProvider(Ext.setapp.REMOTING_API);
	Ext.QuickTips.init();
//	NS.tipoBanco = 0;
//	NS.idBanco = 0;
//	NS.aba = 0;
//	NS.descBanco = 0;
//	NS.modificar = false;
	NS.idSecuencia = 0;
	NS.matrizDatos = new Object();
	NS.modificar=false;
	
	
	NS.storeLlenaGrid = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		directFn: ParametrosCartaAction.llenaGrid,
		fields:
		[
		 	{name: 'idBanco'},
		 	{name: 'descBanco'},
		 	{name: 'tipo'},
		 	{name: 'usuario'}, //,type:'number'
		 	{name: 'nombre'},
		 	{name: 'aPaterno'},
		 	{name: 'aMaterno'},
		 	{name: 'usuario'},
		 	{name: 'fecha'}
		 	
		],
		listeners: {
			load: function (s, records) {
				
				if(records.length == null || records.length <= 0)
					Ext.Msg.alert('SET', 'No existen datos para estos parametros de busqueda');
			}
		}
	});
	var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeLlenaGrid, msg: "Cargando ..."});
	NS.storeLlenaGrid.load();
	
	NS.storeBanco = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams: {},
		paramOrder: [],
		directFn: ParametrosCartaAction.llenaBanco,
		fields:
		[
		 	{name: 'idBanco'},
		 	{name: 'descBanco'}
		],
		listeners: {
			load: function(s, records) {
				if (records.length == null || records.length <= 0)
					Ext.Msg.alert('SET', 'No existen datos de banco');
			}
		}
	});
	var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeBanco, msg: "Cargando..."});
	NS.storeBanco.load();
	
	NS.eliminaCarta = function(){
//		alert("funcion");
 		var registroSeleccionado = NS.gridConsulta.getSelectionModel().getSelections();
 		
 		if (registroSeleccionado.length <= 0)
 			Ext.Msg.alert('SET', 'Debe de seleccionar algun registro');
 		else{
			ParametrosCartaAction.verificaRegistro(registroSeleccionado[0].get('idBanco'), registroSeleccionado[0].get('tipo'), function(resultado, e){
// 				alert(resultado);
				if (resultado != 0 && resultado != undefined && resultado != null){
 					Ext.Msg.confirm('SET', '¿Esta seguro de eliminar los parametros de esta carta?', function(btn){
 						if (btn == 'yes') {
 							ParametrosCartaAction.eliminaCarta(registroSeleccionado[0].get('idBanco'), registroSeleccionado[0].get('tipo'), function(resultado, e){
 								if (resultado != '' && resultado != undefined && resultado != null)
 		 									Ext.Msg.alert('SET', resultado);
 		 								NS.storeLlenaGrid.load();
 		 							});
 									
// 							});
 						}
 					});
 				} 					
 			});
 		} 		
 	};
 	
	
	
	
	NS.lblTipo = new Ext.form.Label({
		text: 'Tipo de Carta',
		x: 410,
		y: 200		
	});
	
	NS.datosComboTipo = [
	                    	   ['ACERT', 'CARTA CERTIFICADO'],
	                    	   ['ACHEQ', 'CARTA CHEQUE DE CAJA'],
	                    	   ['CCERT', 'CANCELACION CARTA CERTIFICADA'],
	                    	   ['CCHEQ', 'CANCELACION CARTA CHEQUE DE CAJA']
	                    	];	       		
	                    	                   	 
	                    	NS.storeTipo = new Ext.data.SimpleStore({
	                    		idProperty: 'idTipo',
	                    	    fields: [
	                    	             {name: 'idTipo'},
	                    	             {name: 'descTipo'}
	                    	    ]
	                    	});
	                    	NS.storeTipo.loadData(NS.datosComboTipo);
	

	NS.cmbTipo = new Ext.form.ComboBox({
		store: NS.storeTipo,
		id: PF + 'cmbTipo',
		name: PF + 'cmbTipo',
		x: 500,
		y: 200,
		width: 250,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		valueField: 'idTipo',
		displayField: 'descTipo',
		autocomplete: true,
		emptyText: 'Tipo',
		triggerAction: 'all',
		value: '',
		listeners: {
			select: {
				fn: function() {
					
				}
			}
		}
		
	});
	
	NS.llenaCamposCarta = function(registro){
		NS.txtBanco.setValue(registro[0].idBanco);
		NS.cmbBanco.setValue(registro[0].descBanco);
		Ext.getCmp(PF + 'cmbTipo').setValue(registro[0].tipo);	
		Ext.getCmp(PF + 'txtB1').setValue(registro[0].b1);
		Ext.getCmp(PF + 'txtB2').setValue(registro[0].b2);
		Ext.getCmp(PF + 'txtB3').setValue(registro[0].b3);
		Ext.getCmp(PF + 'txtB4').setValue(registro[0].b4);
		Ext.getCmp(PF + 'txtC1').setValue(registro[0].c1);
		Ext.getCmp(PF + 'txtC2').setValue(registro[0].c2);
		Ext.getCmp(PF + 'txtC3').setValue(registro[0].c3);
		Ext.getCmp(PF + 'txtC4').setValue(registro[0].c4);
		Ext.getCmp(PF + 'txtC5').setValue(registro[0].c5);				
		Ext.getCmp(PF + 'txtC6').setValue(registro[0].c6);
		Ext.getCmp(PF + 'txtC7').setValue(registro[0].c7);
		Ext.getCmp(PF + 'txtC8').setValue(registro[0].c8);
		Ext.getCmp(PF + 'txtC9').setValue(registro[0].c9);
		Ext.getCmp(PF + 'txtC10').setValue(registro[0].c10);
		Ext.getCmp(PF + 'txtC11').setValue(registro[0].c11);
		Ext.getCmp(PF + 'txtC12').setValue(registro[0].c12);
		Ext.getCmp(PF + 'txtC13').setValue(registro[0].c13);				
		Ext.getCmp(PF + 'txtC14').setValue(registro[0].c14);
		if(NS.cmbTipo.getValue()=='ACERT' || NS.cmbTipo.getValue()=='ACHEQ'){
			Ext.getCmp(PF + 'txtC15').setValue(registro[0].c15);
		}else{
			Ext.getCmp(PF + 'txtCC15').setValue(registro[0].c15);
		}
		
		
	};
 	
 	NS.llenaCarta = function(){
 		ParametrosCartaAction.obtieneCarta(NS.gridConsulta.getSelectionModel().getSelections()[0].get('idBanco'),NS.gridConsulta.getSelectionModel().getSelections()[0].get('tipo'), function(resultado, e){
 			
 			if (resultado !== null && resultado !== '' && resultado !== undefined)
			{
				NS.llenaCamposCarta(resultado);
			}
			else
				Ext.Msg.alert('SET', 'No existe información para esta carta');
		});
		
	};
	
	NS.lblTituloC = new Ext.form.Label({
		text: 'SERVICIOS LABORALES Y EJECUTIVOS SA DE CV',
		x: 280,
		y: 20
		
	});
	
	NS.lblDirC = new Ext.form.Label({
		text: 'Av de los Insurgentes Sur 2795 C.P 61517 Mexico, DF Tel: 17 20 70 00',
		x: 230,
		y: 40
		
	});
	
	NS.txtC15 ={
			xtype: 'uppertextfield',
			id: PF + 'txtC15',
			name: PF + 'txtC15',
			x: 415,
			y: 75,
			width: 150,	
				
		};
	
	NS.txtCC15 ={
			xtype: 'uppertextfield',
			id: PF + 'txtCC15',
			name: PF + 'txtCC15',
			x: 415,
			y: 75,
			width: 150,	
				
		};
	
	NS.lblFechaC = new Ext.form.Label({
		text: ', a 18 de Diciembre de 2015',
		x: 570,
		y: 80
		
	});
		
	NS.lblTitulo = new Ext.form.Label({
		text: 'SERVICIOS LABORALES Y EJECUTIVOS SA DE CV',
		x: 280,
		y: 20
		
	});
	
	NS.lblDir = new Ext.form.Label({
		text: 'Av de los Insurgentes Sur 2795 C.P 61517 Mexico, DF Tel: 17 20 70 00',
		x: 230,
		y: 40
		
	});
	
	NS.lblFecha = new Ext.form.Label({
		text: ', a 18 de Diciembre de 2015',
		x: 570,
		y: 80
		
	});
	
	NS.lblBanco = new Ext.form.Label({
		text: 'Banco',
		x: 140,
		y: 200		
	});
	
	NS.txtBanco = new Ext.form.TextField({
		id: PF + 'txtBanco',
		name: PF + 'txtBanco',
		x: 180,
		y: 200,
		width: 50,		
		listeners: {
 			change: {
 				
 				fn: function(caja, valor) {
 					
 				if (caja.getValue() !== null && caja.getValue() !== '' & caja.getValue() !== undefined)
						BFwrk.Util.updateTextFieldToCombo(PF + 'txtBanco', NS.cmbBanco.getId());
 					
				}
 			}
 		}
		
	});
	
	NS.txtB1 ={
		xtype: 'uppertextfield',
		id: PF + 'txtB1',
		name: PF + 'txtB1',
		x: 15,
		y: 120,
		width: 150,		
		
	};
	
	NS.txtB2 = {
		xtype: 'uppertextfield',
		id: PF + 'txtB2',
		name: PF + 'txtB2',
		x: 15,
		y: 150,
		width: 200,		
		
	};
	
	NS.txtB3 = {
		xtype: 'uppertextfield',
		id: PF + 'txtB3',
		name: PF + 'txtB3',
		x: 15,
		y: 180,
		width: 250,		
		
	};
	
	NS.txtB4 ={
		xtype: 'uppertextfield',
		id: PF + 'txtB4',
		name: PF + 'txtB4',
		x: 15,
		y: 210,
		width: 100,		
		
	};
	
	NS.txtC7 ={
			xtype: 'uppertextfield',
			id: PF + 'txtC7',
			name: PF + 'txtC7',
			x: 120,
			y: 280,
			width: 630,		
			
		};
	
	NS.txtC8 ={
			xtype: 'uppertextfield',
			id: PF + 'txtC8',
			name: PF + 'txtC8',
			x: 80,
			y: 305,
			width: 670,		
			
		};
	
	NS.txtC9 ={
			xtype: 'uppertextfield',
			id: PF + 'txtC9',
			name: PF + 'txtC9',
			x: 15,
			y: 120,
			width: 150,		
			
		};
		
		NS.txtC10 = {
			xtype: 'uppertextfield',
			id: PF + 'txtC10',
			name: PF + 'txtC10',
			x: 15,
			y: 150,
			width: 200,		
			
		};
		
		NS.txtC11 = {
			xtype: 'uppertextfield',
			id: PF + 'txtC11',
			name: PF + 'txtC11',
			x: 15,
			y: 180,
			width: 100,		
			
		};	


		NS.txtC12 ={
			xtype: 'uppertextfield',
			id: PF + 'txtC12',
			name: PF + 'txtC12',
			x: 120,
			y: 440,
			width: 620,		
			
		};
		
		NS.txtC13 ={
			xtype: 'uppertextfield',
			id: PF + 'txtC13',
			name: PF + 'txtC13',
			x: 170,
			y: 465,
			width: 120,		
			
		};

		NS.txtC14 ={
			xtype: 'uppertextfield',
			id: PF + 'txtC14',
			name: PF + 'txtC14',
			x: 120,
			y: 525,
			width: 620,
			height: 80,	
				
		};
		
		
	
	
	NS.txtC1 ={
		xtype: 'uppertextfield',
		id: PF + 'txtC1',
		name: PF + 'txtC1',
		x: 120,
		y: 280,
		width: 550,		
		
	};
	
	NS.lblNombre = new Ext.form.Label({
		text: 'A: GARDUÑO',
		x: 680,
		y: 280
		
	});
	
	NS.lblNombre2 = new Ext.form.Label({
		text: 'TORRES JORGE ADRIAN,',
		x: 80,
		y: 310
		
	});
	
	NS.txtC2 ={
		xtype: 'uppertextfield',
		id: PF + 'txtC2',
		name: PF + 'txtC2',
		x: 215,
		y: 305,
		width: 420,		
		
	};
	
	NS.lblIfe = new Ext.form.Label({
		text: '9568742135764,',
		x: 645,
		y: 310
		
	});
	
	NS.txtC3 ={
		xtype: 'uppertextfield',
		id: PF + 'txtC3',
		name: PF + 'txtC3',
		x: 80,
		y: 330,
		width: 420,		
		
	};
	
	NS.lblBeneficiario = new Ext.form.Label({
		text: 'Beneficiario',
		x: 280,
		y: 380
		
	});
	
	NS.lblMonto = new Ext.form.Label({
		text: 'Monto',
		x: 470,
		y: 380
		
	});
	
	NS.lblB1 = new Ext.form.Label({
		text: 'GODINES MANCHADO MARGARITA YOCELIN',
		x: 200,
		y: 395
		
	});
	
	NS.lblM1 = new Ext.form.Label({
		text: '$ 25,000.00',
		x: 470,
		y: 395
		
	});
	
	NS.lblChequeC = new Ext.form.Label({
		text: 'Cheque',
		x: 90,
		y: 380
		
	});
	
	NS.lblChC = new Ext.form.Label({
		text: '8956',
		x: 100,
		y: 395
		
	});
	
	
	NS.lblBeneficiarioC = new Ext.form.Label({
		text: 'Beneficiario',
		x: 280,
		y: 380
		
	});
	
	NS.lblMontoC = new Ext.form.Label({
		text: 'Monto',
		x: 470,
		y: 380
		
	});
	
	NS.lblB1C = new Ext.form.Label({
		text: 'GODINES MANCHADO MARGARITA YOCELIN',
		x: 200,
		y: 395
		
	});
	
	NS.lblM1C = new Ext.form.Label({
		text: '$ 25,000.00',
		x: 470,
		y: 395
		
	});
	

	NS.lblImporteC = new Ext.form.Label({
		text: 'Importe en letra',
		x: 580,
		y: 380
		
	});
	
	NS.lblImpC = new Ext.form.Label({
		text: 'Veinticinco mil pesos 00/100 M.N',
		x: 550,
		y: 395
		
	});
	
	
	NS.txtC4 ={
		xtype: 'uppertextfield',
		id: PF + 'txtC4',
		name: PF + 'txtC4',
		x: 120,
		y: 440,
		width: 620,		
		
	};
	
	NS.txtC5 ={
		xtype: 'uppertextfield',
		id: PF + 'txtC5',
		name: PF + 'txtC5',
		x: 170,
		y: 465,
		width: 120,		
		
	};
	
	NS.lblCuenta = new Ext.form.Label({
		text: 'No. 137070567',
		x: 80,
		y: 470
		
	});
	
	NS.lblNE = new Ext.form.Label({
		text: 'SERVICIOS LABORALES Y EJECUTIVOS SA DE CV',
		x: 300,
		y: 470
		
	});
	
	
	NS.lblCuentaC = new Ext.form.Label({
		text: 'No. 137070567',
		x: 80,
		y: 470
		
	});
	
	NS.lblNEC = new Ext.form.Label({
		text: 'SERVICIOS LABORALES Y EJECUTIVOS SA DE CV',
		x: 300,
		y: 470
		
	});
	
	
	NS.txtC6 ={
		xtype: 'uppertextfield',
		id: PF + 'txtC6',
		name: PF + 'txtC6',
		x: 120,
		y: 525,
		width: 620,
		height: 80,	
			
	};
	
	
	
	
	NS.lblSolicitanteC = new Ext.form.Label({
		text: 'Solicitante',
		x: 370,
		y: 610
		
	});
	
	NS.lblNSC = new Ext.form.Label({
		text: 'Garduño Torres Jorge Adrian',
		x: 320,
		y: 650
		
	});

	
	NS.lblAttC = new Ext.form.Label({
		text: 'ATENTAMENTE',
		x: 360,
		y: 700
		
	});
	
	NS.lblAu1C = new Ext.form.Label({
		text: 'C.P. Alberto Camargo Ledezma',
		x: 150,
		y: 750
		
	});
	
	NS.lblAu2C = new Ext.form.Label({
		text: 'C.P. Alfonso Hernandez Perez',
		x: 500,
		y: 750
		
	});
	
	NS.lblAu3C = new Ext.form.Label({
		text: 'AUTORIZO',
		x: 210,
		y: 760
		
	});
	
	NS.lblAu4C = new Ext.form.Label({
		text: 'AUTORIZO',
		x: 560,
		y: 760
		
	});
	
	
	
	
	NS.lblSolicitante = new Ext.form.Label({
		text: 'Solicitante',
		x: 370,
		y: 610
		
	});
	
	NS.lblNS = new Ext.form.Label({
		text: 'Garduño Torres Jorge Adrian',
		x: 320,
		y: 650
		
	});

	
	NS.lblAtt = new Ext.form.Label({
		text: 'ATENTAMENTE',
		x: 360,
		y: 700
		
	});
	
	NS.lblAu1 = new Ext.form.Label({
		text: 'C.P. Alberto Camargo Ledezma',
		x: 150,
		y: 750
		
	});
	
	NS.lblAu2 = new Ext.form.Label({
		text: 'C.P. Alfonso Hernandez Perez',
		x: 500,
		y: 750
		
	});
	
	NS.lblAu3 = new Ext.form.Label({
		text: 'AUTORIZO',
		x: 210,
		y: 760
		
	});
	
	NS.lblAu4 = new Ext.form.Label({
		text: 'AUTORIZO',
		x: 560,
		y: 760
		
	});
	
	NS.validaGenerar = function(){
		
		 if(NS.txtBanco.getValue()==""){
			Ext.Msg.alert('SET',"Selecciona un banco");
			return false;
		}else if(NS.cmbTipo.getValue()==""){
			Ext.Msg.alert('SET',"Seleccione un tipo");
			return false;
		}
		else{
			return true;
		}
	};
	
	
	NS.limpiar = function(){
		
		Ext.getCmp(PF + 'txtB1').setValue("");
		Ext.getCmp(PF + 'txtB2').setValue("");
		Ext.getCmp(PF + 'txtB3').setValue("");
		Ext.getCmp(PF + 'txtB4').setValue("");
		Ext.getCmp(PF + 'txtC1').setValue("");
		Ext.getCmp(PF + 'txtC2').setValue("");
		Ext.getCmp(PF + 'txtC3').setValue("");
		Ext.getCmp(PF + 'txtC4').setValue("");
		Ext.getCmp(PF + 'txtC5').setValue("");
		Ext.getCmp(PF + 'txtC6').setValue("");
		Ext.getCmp(PF + 'txtC7').setValue("");
		Ext.getCmp(PF + 'txtC8').setValue("");
		Ext.getCmp(PF + 'txtC9').setValue("");
		Ext.getCmp(PF + 'txtC10').setValue("");
		Ext.getCmp(PF + 'txtC11').setValue("");
		Ext.getCmp(PF + 'txtC12').setValue("");
		Ext.getCmp(PF + 'txtC13').setValue("");
		Ext.getCmp(PF + 'txtC14').setValue("");
		Ext.getCmp(PF + 'txtC15').setValue("");
		Ext.getCmp(PF + 'txtCC15').setValue("");
		 NS.txtBanco.setValue("");
		 NS.cmbBanco.reset();
		 NS.cmbTipo.reset();
		
	};

	NS.cmbBanco = new Ext.form.ComboBox({
		store: NS.storeBanco,
		id: PF + 'cmbBanco',
		name: PF + 'cmbBanco',
		x: 240,
		y: 200,
		width: 150,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		valueField: 'idBanco',
		displayField: 'descBanco',
		autocomplete: true,
		emptyText: 'Banco',
		triggerAction: 'all',
		value: '',
		visible: false,
		listeners: {
			select: {
				
				fn: function(combo, valor) {
					BFwrk.Util.updateComboToTextField(PF + 'txtBanco', NS.cmbBanco.getId());
					
				}
			}
		}
		
	});
	
	
	NS.actualizarCarta = function(){
		
			
			var vector = {};
			var matriz = new Array();
			
			vector.idBanco= NS.txtBanco.getValue();
			vector.usuario= NS.idUsuario;
			vector.fecha=NS.fecHoy;
			vector.tipo = NS.cmbTipo.getValue();
			vector.b1 = Ext.getCmp(PF + 'txtB1').getValue();
			vector.b2 = Ext.getCmp(PF + 'txtB2').getValue();		
			vector.b3 = Ext.getCmp(PF + 'txtB3').getValue();
			vector.b4 = Ext.getCmp(PF + 'txtB4').getValue();
			vector.c1 = Ext.getCmp(PF + 'txtC1').getValue();
			vector.c2 = Ext.getCmp(PF + 'txtC2').getValue();
			vector.c3 = Ext.getCmp(PF + 'txtC3').getValue();
			vector.c4 = Ext.getCmp(PF + 'txtC4').getValue();
			vector.c5 = Ext.getCmp(PF + 'txtC5').getValue();
			vector.c6 = Ext.getCmp(PF + 'txtC6').getValue();
			vector.c7 = Ext.getCmp(PF + 'txtC7').getValue();
			vector.c8 = Ext.getCmp(PF + 'txtC8').getValue();
			vector.c9 = Ext.getCmp(PF + 'txtC9').getValue();
			vector.c10 = Ext.getCmp(PF + 'txtC10').getValue();
			vector.c11 = Ext.getCmp(PF + 'txtC11').getValue();
			vector.c12 = Ext.getCmp(PF + 'txtC12').getValue();
			vector.c13 = Ext.getCmp(PF + 'txtC13').getValue();
			vector.c14 = Ext.getCmp(PF + 'txtC14').getValue();
			if(NS.cmbTipo.getValue()=='ACERT' || NS.cmbTipo.getValue()=='ACHEQ'){
				vector.c15 = Ext.getCmp(PF + 'txtC15').getValue();
			}else{
				vector.c15 = Ext.getCmp(PF + 'txtCC15').getValue();
			}
					
			matriz[0] = vector;
			var jSonString = Ext.util.JSON.encode(matriz);
			console.log(jSonString);
					
			  ParametrosCartaAction.actualizaCarta(jSonString,''+ NS.gridConsulta.getSelectionModel().getSelections()[0].get('idBanco'),NS.gridConsulta.getSelectionModel().getSelections()[0].get('tipo'), function(resultado, e){			  
				if (resultado == ''){
					Ext.Msg.alert('SET', resultado);		
				}
				else if (resultado !== '' && resultado !== undefined && resultado !== null)
				  	Ext.Msg.alert('SET', resultado);
					NS.limpiar();
					NS.panelTextos.setVisible(false);
					NS.panelTextosC.setVisible(false);
					NS.cmbBanco.setVisible(false);
					NS.txtBanco.setVisible(false);
					NS.cmbTipo.setVisible(false);
					NS.lblTipo.setVisible(false);
					NS.lblBanco.setVisible(false);
					NS.storeLlenaGrid.load();
					
					
			  });

 	};
	
	
 	NS.insertarActualizarCarta = function(){
		
			var vector = {};
			var matriz = new Array();
			
			vector.idBanco= NS.txtBanco.getValue();
			vector.usuario= NS.idUsuario;
			vector.fecha=NS.fecHoy;
			vector.tipo = NS.cmbTipo.getValue();
			vector.b1 = Ext.getCmp(PF + 'txtB1').getValue();
			vector.b2 = Ext.getCmp(PF + 'txtB2').getValue();		
			vector.b3 = Ext.getCmp(PF + 'txtB3').getValue();
			vector.b4 = Ext.getCmp(PF + 'txtB4').getValue();
			vector.c1 =	Ext.getCmp(PF + 'txtC1').getValue();
			vector.c2 = Ext.getCmp(PF + 'txtC2').getValue();
			vector.c3 = Ext.getCmp(PF + 'txtC3').getValue();
			vector.c4 = Ext.getCmp(PF + 'txtC4').getValue();
			vector.c5 = Ext.getCmp(PF + 'txtC5').getValue();
			vector.c6 = Ext.getCmp(PF + 'txtC6').getValue();
			vector.c7 = Ext.getCmp(PF + 'txtC7').getValue();
			vector.c8 = Ext.getCmp(PF + 'txtC8').getValue();
			vector.c9 = Ext.getCmp(PF + 'txtC9').getValue();
			vector.c10 = Ext.getCmp(PF + 'txtC10').getValue();
			vector.c11 = Ext.getCmp(PF + 'txtC11').getValue();
			vector.c12 = Ext.getCmp(PF + 'txtC12').getValue();
			vector.c13 = Ext.getCmp(PF + 'txtC13').getValue();
			vector.c14 = Ext.getCmp(PF + 'txtC14').getValue();
			if(NS.cmbTipo.getValue()=='ACERT' || NS.cmbTipo.getValue()=='ACHEQ'){
				vector.c15 = Ext.getCmp(PF + 'txtC15').getValue();
			}else{
				vector.c15 = Ext.getCmp(PF + 'txtCC15').getValue();
			}
			
			matriz[0] = vector;
			var jSonString = Ext.util.JSON.encode(matriz);
		
			  ParametrosCartaAction.insertaCarta(jSonString, function(resultado, e){	
				if (resultado == ''){
					
				}
				else if (resultado !== '' && resultado !== undefined && resultado !== null)
				  	Ext.Msg.alert('SET', resultado);
					NS.limpiar();
					NS.panelTextos.setVisible(false);
					NS.panelTextosC.setVisible(false);
					NS.txtBanco.setDisabled(false);
 					NS.cmbBanco.setDisabled(false);
 					NS.lblTipo.setDisabled(false);
 					NS.cmbTipo.setDisabled(false);
 					Ext.getCmp(PF + 'generar').setVisible(false);
					NS.storeLlenaGrid.load();
					
			  });
 	};
	
 	
	NS.panelTextos = new Ext.form.FieldSet({
		title: '',
		x: 90,
		y: 240,
		width: 810,
		height: 990,
		layout: 'absolute',
		items:
		[
		 NS.lblTitulo,
		 NS.lblDir,
		 NS.txtC15,
		 NS.lblFecha,
		 NS.txtB1,
		 NS.txtB2,
		 NS.txtB3,
		 NS.txtB4,
		 NS.txtC1,
		 NS.lblNombre,
		 NS.lblNombre2,
		 NS.txtC2,
		 NS.lblIfe,
		 NS.txtC3,
		 NS.lblBeneficiario,
		 NS.lblMonto,
		 NS.lblB1,
		 NS.lblM1,
		 NS.txtC4,
		 NS.txtC5,
		 NS.lblNE,
		 NS.lblCuenta,
		 NS.txtC6,
		 NS.lblSolicitante,
		 NS.lblNS,
		 NS.lblAtt,
		 NS.lblAu1,
		 NS.lblAu2,
		 NS.lblAu3,
		 NS.lblAu4,
		 
		 
//		 {
//		 		xtype: 'button',
//		 		text: 'Excel',
//		 		x: 300,
//		 		y: 915,
//		 		width: 80,
//		 		height: 22,
//		 		listeners: {
//		 			click: {
//		 				fn: function(e)
//		 				{
//		 					
//		 				}	
//		 			}
//		 		}
//		 		
//		 	},
		 	
		 	{
		 		xtype: 'button',
		 		text: 'Aceptar',
		 		id: PF + 'aceptar',
		 		name: PF + 'aceptar',
		 		x: 340,
		 		y: 915,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				
		 				fn: function(e)
		 				{
		 					NS.insertarActualizarCarta();
		 					
		 				}
		 			}
		 		}
		 	},
		 	{
		 		xtype: 'button',
		 		text: 'Modificar',
		 		id: PF + 'modificar',
		 		name: PF + 'modificar',
		 		x: 340,
		 		y: 915,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				
		 				fn: function(e)
		 				{
		 					NS.actualizarCarta();
		 					
		 				}
		 			}
		 		}
		 	},
							
		 	{
		 		xtype: 'button',
		 		text: 'Cancelar',
		 		x: 430,
		 		y: 915,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				fn: function(e)
		 				{
		 					NS.panelTextos.setVisible(false);
		 					NS.lblBanco.setVisible(false);
		 					NS.txtBanco.setVisible(false);
		 					NS.cmbBanco.setVisible(false);
		 					NS.lblTipo.setVisible(false);
		 					NS.cmbTipo.setVisible(false);
		 					Ext.getCmp(PF + 'generar').setVisible(false);
		 					NS.limpiar();
		 				}
		 			}
		 		}
		 	},
		 	
		 	
		 ]
	
	});
	
	
	NS.panelTextosC = new Ext.form.FieldSet({
		title: '',
		x: 90,
		y: 240,
		width: 810,
		height: 990,
		layout: 'absolute',
		items:
		[
		 NS.lblImporteC,
		 NS.lblImpC,
		 NS.lblChequeC,
		 NS.lblChC,
		 NS.lblTituloC,
		 NS.lblDirC,
		 NS.txtCC15,
		 NS.lblFechaC,
		 NS.txtC9,
		 NS.txtC10,
		 NS.txtC11,
		 NS.txtC7,
		 NS.txtC8,
		 NS.lblBeneficiarioC,
		 NS.lblMontoC,
		 NS.lblB1C,
		 NS.lblM1C,
		 NS.txtC12,
		 NS.txtC13,
		 NS.lblNEC,
		 NS.lblCuentaC,
		 NS.txtC14,
		 NS.lblSolicitanteC,
		 NS.lblNSC,
		 NS.lblAttC,
		 NS.lblAu1C,
		 NS.lblAu2C,
		 NS.lblAu3C,
		 NS.lblAu4C,
		 
		 
//		 {
//		 		xtype: 'button',
//		 		text: 'Excel',
//		 		x: 300,
//		 		y: 915,
//		 		width: 80,
//		 		height: 22,
//		 		listeners: {
//		 			click: {
//		 				fn: function(e)
//		 				{
//		 					
//		 				}	
//		 			}
//		 		}
//		 		
//		 	},
		 	
		 	{
		 		xtype: 'button',
		 		text: 'Aceptar',
		 		id: PF + 'aceptarC',
		 		name: PF + 'aceptarC',
		 		x: 340,
		 		y: 915,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				
		 				fn: function(e)
		 				{
		 					NS.insertarActualizarCarta();
		 					
		 				}
		 			}
		 		}
		 	},
		 	{
		 		xtype: 'button',
		 		text: 'Modificar',
		 		id: PF + 'modificarC',
		 		name: PF + 'modificarC',
		 		x: 340,
		 		y: 915,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				
		 				fn: function(e)
		 				{
		 					NS.actualizarCarta();
		 					
		 				}
		 			}
		 		}
		 	},
							
		 	{
		 		xtype: 'button',
		 		text: 'Cancelar',
		 		x: 430,
		 		y: 915,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				fn: function(e)
		 				{
		 					NS.panelTextos.setVisible(false);
		 					NS.panelTextosC.setVisible(false);
		 					NS.lblBanco.setVisible(false);
		 					NS.txtBanco.setVisible(false);
		 					NS.cmbBanco.setVisible(false);
		 					NS.lblTipo.setVisible(false);
		 					NS.cmbTipo.setVisible(false);
		 					Ext.getCmp(PF + 'generar').setVisible(false);
		 					NS.limpiar();
		 				}
		 			}
		 		}
		 	},
		 	
		 ]
	
	});
	
	NS.columnaSeleccion = new Ext.grid.CheckboxSelectionModel({
		singleSelect: false
	});
	
	NS.columnasGrid = new Ext.grid.ColumnModel([
	                                            
	            {header: 'Id Banco ', width: 100,  dataIndex: 'idBanco', sortable: true, hidden:true},
	            {header: 'Banco ', width: 250,  dataIndex: 'descBanco', sortable: true},
	            {header: 'Tipo de Carta ', width: 70,  dataIndex: 'tipo', sortable: true},
	            {header: 'Id Usuario', width:100 , dataIndex: 'usuario', sortable: true, hidden:true},
	            {header: 'Nombre Usuario', width:150 , dataIndex: 'nombre', sortable: true},
	            {header: '', width:120 , dataIndex: 'aPaterno', sortable: true},
	            {header: '', width:120 , dataIndex: 'aMaterno', sortable: true},
	            {header: 'Fecha Modificación', width:215 , dataIndex: 'fecha', sortable: true},
	             ]); 
	
	
	NS.gridConsulta = new Ext.grid.GridPanel({
		store: NS.storeLlenaGrid,
		id: PF + 'gridConsulta',
		name: PF + 'gridConsulta',
		cm: NS.columnasGrid,
		sm: NS.columnaSeleccion,
		width: 960,
		height: 120,
		stripeRows : true,
		columnLines: true,
		listeners: {
			click: {
				fn: function(e)
				{

				}
			}
		}
	});
	
	
	NS.panelGrid = new Ext.form.FieldSet({
		title: '',
		x: 0,
		y: 5,
		width: 985,
		height: 180,
		layout: 'absolute',
		items:
		[
		 	
		 	{
		 		xtype: 'button',
		 		text: 'Crear Nuevo',
		 		x: 670,
		 		y: 130,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				fn: function(e)
		 				{
		 					NS.limpiar();
		 					NS.panelTextos.setVisible(false);
		 					NS.lblBanco.setVisible(true);
		 					NS.txtBanco.setVisible(true);
		 					NS.cmbBanco.setVisible(true);
//		 					NS.panelTextos.setVisible(true);
		 					NS.lblTipo.setVisible(true);
		 					NS.cmbTipo.setVisible(true);
		 					
		 					NS.txtBanco.setDisabled(false);
		 					NS.cmbBanco.setDisabled(false);
		 					NS.lblTipo.setDisabled(false);
		 					NS.cmbTipo.setDisabled(false);
		 					Ext.getCmp(PF + 'generar').setVisible(true);
		 					
		 					
		 					}	
		 				}
		 			}
		 		
		 	},
		 	
		 	{
		 		xtype: 'button',
		 		text: 'Eliminar',
		 		x: 760,
		 		y: 130,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				
		 				fn: function(e)
		 				{
		 					NS.eliminaCarta();
		 					NS.panelTextos.setVisible(false);
		 					NS.panelTextosC.setVisible(false);
		 					NS.lblBanco.setVisible(false);
		 					NS.txtBanco.setVisible(false);
		 					NS.cmbBanco.setVisible(false);
		 					NS.lblTipo.setVisible(false);
		 					NS.cmbTipo.setVisible(false);
		 					Ext.getCmp(PF + 'generar').setVisible(false);
		 					NS.limpiar();
		 				}
		 			}
		 		}
		 	},
							
		 	{
		 		xtype: 'button',
		 		text: 'Modificar',
		 		x: 850,
		 		y: 130,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				fn: function(e)
		 				{
		 					
		 					var registroSeleccionado = NS.gridConsulta.getSelectionModel().getSelections();
		 					if (registroSeleccionado.length <= 0)
		 						Ext.Msg.alert('SET', 'Se debe de elegir un registro');
		 					else
		 					{
		 						NS.modificar=true;
		 						NS.lblBanco.setVisible(true);
			 					NS.txtBanco.setVisible(true);
			 					NS.cmbBanco.setVisible(true);
			 					NS.lblTipo.setVisible(true);
			 					NS.cmbTipo.setVisible(true);
			 					
			 					if(NS.gridConsulta.getSelectionModel().getSelections()[0].get('tipo')=='ACERT'||
			 							NS.gridConsulta.getSelectionModel().getSelections()[0].get('tipo')=='ACHEQ'){
			 						NS.panelTextos.setVisible(true);
			 						Ext.getCmp(PF + 'modificar').setVisible(true);
				 					Ext.getCmp(PF + 'aceptar').setVisible(false);
				 					Ext.getCmp(PF + 'generar').setVisible(false);
			 					}else{
			 						NS.panelTextosC.setVisible(true);
			 						Ext.getCmp(PF + 'modificarC').setVisible(true);
				 					Ext.getCmp(PF + 'aceptarC').setVisible(false);
				 					Ext.getCmp(PF + 'generar').setVisible(false);
			 					}
			 					
			 					
			 					NS.txtBanco.setDisabled(true);
			 					NS.cmbBanco.setDisabled(true);
			 					NS.cmbTipo.setDisabled(true);
			 					
		 						NS.llenaCarta();
		 						
			 					
	 					}
		 						
		 				}
		 			}
		 		}
		 	},
		 	
		 	NS.gridConsulta,
		 	
		]
	});

	NS.contenedor = new Ext.form.FieldSet ({
		title: 'Parametros Carta',
		x: 20,
		y: 5,
		width: 1010,
		height: 1300,
		layout: 'absolute',
		items:
		[
		 NS.panelGrid,
		 NS.panelTextos.setVisible(false),
		 NS.panelTextosC.setVisible(false),
		 NS.lblBanco.setVisible(false),
		 NS.txtBanco.setVisible(false),
		 NS.cmbBanco.setVisible(false),
		 NS.lblTipo.setVisible(false),
		 NS.cmbTipo.setVisible(false),
		 {
		 		xtype: 'button',
		 		text: 'Generar',
		 		id: PF + 'generar',
		 		name: PF + 'generar',
		 		hidden:true,
		 		x: 760,
		 		y: 200,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				
		 				fn: function(e)
		 				{
		 					if (NS.validaGenerar()==true) {
								if(NS.cmbTipo.getValue()=='ACERT' || NS.cmbTipo.getValue()=='ACHEQ'){
			 						NS.panelTextos.setVisible(true);
			 						Ext.getCmp(PF + 'modificar').setVisible(false);
				 					Ext.getCmp(PF + 'aceptar').setVisible(true);
				 					Ext.getCmp(PF + 'generar').setVisible(true);
			 					}else{
			 						NS.panelTextosC.setVisible(true);
			 						Ext.getCmp(PF + 'modificarC').setVisible(false);
				 					Ext.getCmp(PF + 'aceptarC').setVisible(true);
				 					Ext.getCmp(PF + 'generar').setVisible(true);
			 					}
								
		 					}
							
		 					
		 					
		 					
		 					
		 					
		 				}
		 			}
		 		}
		 	},

		 ]
	});
	
	
	
	
	NS.Global = new Ext.FormPanel ({
		title: 'Parametros de Carta',
		width: 1300,
		height: 706,
		frame: true,
		padding: 10,
		autoScroll: true,
		layout: 'absolute',
		id: PF + 'global',
		name: PF + 'global',
		renderTo: NS.tabContId,
		items: [
		 	
		NS.contenedor,
		
		 
		]
	});
	NS.Global.setSize(Ext.get(NS.tabContId).getWidth(), Ext.get(NS.tabContId).getHeight());
	

	

	
});