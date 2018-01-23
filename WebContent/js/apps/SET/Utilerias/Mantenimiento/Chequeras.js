Ext.onReady(function()
{
	var NS = Ext.namespace('apps.SET.Utilerias.Mantenimiento.Chequeras');
	NS.tabContId = apps.SET.tabContainerId;
	NS.idUsuario = apps.SET.iUserId;
	NS.noEmpresa = apps.SET.NO_EMPRESA;
	//NS.fecHoy = BFwrk.Util.changeStringToDate(apps.SET.FEC_HOY);
	NS.fecHoy = apps.SET.FEC_HOY;
	var PF = apps.SET.tabID + '.';
	Ext.Direct.addProvider(Ext.setapp.REMOTING_API);
	Ext.QuickTips.init();
	NS.noEmpresaCombo = 0;
	NS.idBanco = 0;
	NS.tipoBanco = 0;
	NS.modificar = false;
	NS.localizacion = "";
	NS.facultadModificar = false;7
	NS.bancoM=0;
	
	//LABELS PANTALLA INICIAL
	NS.lblEmpresa = new Ext.form.Label({
		text: 'Empresa',
		x: 10, 
		y: 0
	});
	
	NS.lblBanco = new Ext.form.Label({
		text: 'Banco',
		x: 410, 
		y: 0
	});
	
	NS.lblTipoChequera = new Ext.form.Label({
		text: 'Tipo Chequera',
		x: 0,
		y: 0
	});
	
	NS.lblGrupo = new Ext.form.Label({
		text: 'Grupo',
		x: 160,
		y: 0
	});
	
	//LABEL CREAR NUEVO	
	NS.lblBancoNuevo = new Ext.form.Label({
		text: 'Banco',
		x: 210, 
		y:0
	});
	
	NS.lblChequeraNuevo = new Ext.form.Label({
		text: 'Chequera',
		x: 530,
		y: 0
	});
	
	NS.lblDivisionNuevo = new Ext.form.Label({
		text: 'Division',
		x: 710,
		y: 0,
		hidden:true
	});
	
	NS.lblDivisaNuevo = new Ext.form.Label({
		text: 'Divisa',
		x: 10,
		y: 70
	});
	
	NS.lblTipoChequeraNuevo = new Ext.form.Label({
		text: 'Tipo Chequera',
		x: 300,
		y: 70
	});
	
	NS.lblDescChequeraNuevo = new Ext.form.Label({
		text: 'Descripción De Chequera',
		x: 10,
		y: 120
	});
	
	NS.lblPlazaNuevo = new Ext.form.Label({
		text: 'Plaza',
		x: 350,
		y: 120
	});
	
	NS.lblSucursalNuevo = new Ext.form.Label({
		text: 'Sucursal',
		x: 530,
		y: 120
	});
	
	NS.lblSaldoInicial = new Ext.form.Label({
		text: 'Saldo Inicial',
		x: 10, 
		y: 170
	});
	
	NS.lblSaldoMinimo = new Ext.form.Label({
		text: 'Saldo Minimo',
		x: 180,
		y: 170
	});
	
	NS.lblSaldoInicialBanco = new Ext.form.Label({
		text: 'Saldo Inicial Del Banco',
		x: 10,
		y: 220
	});
	
	NS.lblUltimoChequeImpreso = new Ext.form.Label({
		text: 'Ultimo Cheque Impreso',
		x: 180,
		y: 220
	});
	
	NS.lblSaldoInicialContabilidad = new Ext.form.Label({
		text: 'Saldo Inicial Contabilidad',
		x: 10,
		y: 270 
	});
	
	NS.lblAbonoSBC = new Ext.form.Label({
		text: 'Abono Salvo Buen Cobro',
		x: 180,
		y: 270
	});
	
	NS.lblPeriodoConciliacion = new Ext.form.Label({
		text: 'Periodo de Conciliación',
		x: 10,
		y: 320		
	});
	
	NS.lblMapeoERP = new Ext.form.Label({
		text: 'Mapeo ERP',
		x: 180,
		y: 320,
		hidden:true
	});
	
	NS.lblClabe = new Ext.form.Label({
		text: 'Clabe',
		x: 10, 
		y: 370
	});
	
	NS.lblChequeraContrato = new Ext.form.Label({
		text: 'Chequera Contrato',
		x: 200,
		y: 370,
		hidden:true
	});	
	
	NS.lblSobregiro = new Ext.form.Label({
		text: 'Sobregiro',
		x: 350,
		y: 170
	});
	
	//LABEL PARA LA ASIGNACION DE CHEQUES
	NS.lblBancoCheques = new Ext.form.Label({
		text: 'Banco',
		x: 10,
		y: 0
	});
	
	NS.lblChequeraCheques = new Ext.form.Label({
		text: 'Chequera',
		x: 10, 
		y: 50
	});
	
	NS.lblCajaCheques = new Ext.form.Label({
		text: 'Caja',
		x: 10, 
		y: 100
	});
	
	NS.lblLote = new Ext.form.Label({
		text: 'Lote',
		x: 0,
		y: 0
	});
	
	NS.lblFolioInicial = new Ext.form.Label({
		text: 'Folio Inicial',
		x: 0,
		y: 50
	});
	
	NS.lblFolioFinal = new Ext.form.Label({
		text: 'Folio Final',
		x: 0,
		y: 100
	});
	
	NS.lblUltimoChequeImp = new Ext.form.Label({
		text: 'Ultimo Cheque Impreso',
		x: 0,
		y: 150
	});
	
	//LABEL PARA ASIGNACION DE DIVISIONES POR EMPRESA
	NS.lblEmpresaDivisiones = new Ext.form.Label({
		text: 'Empresa',
		x: 10,
		y: 15
	});
	
	NS.lblBancoDivisiones = new Ext.form.Label({
		text: 'Banco',
		x: 10,
		y: 65
	});
	
	NS.lblChequeraDivisiones = new Ext.form.Label({
		text: 'Chequera',
		x: 250,
		y: 65		
	});
	
	NS.lblDivisionesPorAsociar = new Ext.form.Label({
		text: 'Divisiones por asociar',
		x: 10,
		y: 115
	});
	
	NS.lblDivisionesAsociadas = new Ext.form.Label({
		text: 'Divisiones Asociadas',
		x: 500,
		y: 115
	});
	
	//TEXTFIELD PANTALLA INICIAL
	NS.txtIdEmpresa = new Ext.form.TextField({
		id: PF + 'txtIdEmpresa',
		name: PF + 'txtIdEmpresa',
		x: 10,
		y: 15,
		width: 50, 
		tabIndex: 0,
		listeners:
		{
			change:
			{
				fn: function(caja, valor)
				{
					if (caja.getValue() !== null && caja.getValue() !== undefined && caja.getValue() !== '')
					{
						var valueCombo = BFwrk.Util.updateTextFieldToCombo(PF + 'txtIdEmpresa', NS.cmbEmpresa.getId());
						if (valueCombo != null && valueCombo != undefined && valueCombo != '')
							NS.cambiaBanco(valueCombo);
						else
							NS.cambiaBanco(0);
					}
					else
					{
						NS.cambiaBanco(0);
						BFwrk.Util.updateTextFieldToCombo(PF + 'txtIdEmpresa', NS.cmbEmpresa.getId());
					}
				}
			}
		}		
	});
	
	NS.txtIdBanco = new Ext.form.TextField({
		id: PF + 'txtIdBanco',
		name: PF + 'txtIdBanco',
		x: 410,
		y: 15,
		width: 50,
		tabIndex: 2,
		maxLength: 6,
		enableKeyEvents:true,
		listeners:
		{
			keydown:{
	 			fn:function(caja, e) {
	 				if(!caja.isValid()){
	 					caja.setValue(caja.getValue().substring(0, 6));
	 				}
	 			}
	 		},
	 		keyup:{
	 			fn:function(caja, e) {	 				
	 				if(!caja.isValid()){
	 					caja.setValue(caja.getValue().substring(0, 6));
	 				}
	 				
	 			}
	 		},
			change:
			{
				fn: function(caja, valor)
				{
					if(caja.getValue() != null && caja.getValue() != undefined && caja.getValue() != '')
					{
						BFwrk.Util.updateTextFieldToCombo(PF + 'txtIdBanco', NS.cmbBanco.getId());
					}
				}
			}	
		}
	});
	
	NS.txtBancoP = new Ext.form.TextField({
		id: PF + 'txtBancoP',
		name: PF + 'txtBancoP',
		x: 270,
		y: 15,
		width: 185,
		hidden:true,
		listeners:{
	 			fn:function(caja, e) {
	 				
	 			}
		}
	});
	
	NS.txtBancoI = new Ext.form.TextField({
		id: PF + 'txtBancoI',
		name: PF + 'txtBancoI',
		x: 210,
		y: 15,
		width: 50,
		hidden:true,
		listeners:{
	 			fn:function(caja, e) {
	 				
	 			}
		}
	});
	
	
	
	//TEXTFIELD CREAR NUEVA
	NS.txtIdBancoNuevo = new Ext.form.TextField({
		id: PF + 'txtIdBancoNuevo',
		name: PF + 'txtIdBancoNuevo',
		x: 210,
		y: 15,
		width: 50,
		tabIndex: 2,
		maxLength: 6,
		enableKeyEvents:true,
		listeners:{
	 		keydown:{
	 			fn:function(caja, e) {
	 				if(!caja.isValid()){
	 					caja.setValue(caja.getValue().substring(0, 6));
	 				}
	 			}
	 		},
	 		keyup:{
	 			fn:function(caja, e) {	 				
	 				if(!caja.isValid()){
	 					caja.setValue(caja.getValue().substring(0, 6));
	 				}
	 				
	 			}
	 		},
			change:	{
				fn: function(caja, valor){
					if (caja.getValue() != null && caja.getValue != undefined && caja.getValue() != '')
					{
						BFwrk.Util.updateTextFieldToCombo(PF + 'txtIdBancoNuevo', NS.cmbBancoNuevo.getId());						
					}
				}
			}
		}	
	});
	
	NS.txtChequeraNueva = new Ext.form.TextField({
		id: PF + 'txtChequeraNueva',
		name: PF + 'txtChequeraNueva',
		x:530,
		y: 15,
		width: 150,
		tabIndex: 4	,
		maxLength: 25,
		enableKeyEvents:true,
		listeners:
		{
			keydown:{
	 			fn:function(caja, e) {
	 				if(!caja.isValid()){
	 					caja.setValue(caja.getValue().substring(0, 25));
	 				}
	 			}
	 		},
	 		keyup:{
	 			fn:function(caja, e) {	 				
	 				if(!caja.isValid()){
	 					caja.setValue(caja.getValue().substring(0, 25));
	 				}
	 				
	 			}
	 		},
		}
	});
	
	NS.txtIdDivision = new Ext.form.TextField({
		id: PF + 'txtIdDivision',
		name: PF + 'txtIdDivision',
		x: 710,
		y: 15,
		width: 50,
		tabIndex: 5,
		hidden:true,
		listeners: {
			change:	{
				fn: function(caja, valor){
					BFwrk.Util.updateTextFieldToCombo(PF + 'txtIdDivision', NS.cmbDivision.getId());					
				}
			}
		}		
	});
		
	NS.txtDescChequera = {
		xtype: 'uppertextfield',
		id: PF + 'txtDescChequera',
		name: PF + 'txtDescChequera',
		x: 10,
		y: 135,
		width: 300,
		maxLength: 20,
		enableKeyEvents:true,
		listeners:{
	 		keydown:{
	 			fn:function(caja, e) {
	 				if(!caja.isValid()){
	 					caja.setValue(caja.getValue().substring(0, 20));
	 				}
	 			}
	 		},
	 		keyup:{
	 			fn:function(caja, e) {	 				
	 				if(!caja.isValid()){
	 					caja.setValue(caja.getValue().substring(0, 20));
	 				}
	 				
	 			}
	 		},
			change:	{
				fn: function(caja, valor){
		
				}
			}
		}		
	};
	
	NS.txtPlaza ={
		xtype: 'uppertextfield',
		id: PF + 'txtPlaza',
		name: PF + 'txtPlaza',
		x: 350,
		y: 135,
		width: 150,
		tabIndex: 15,
		maxLength: 30,
		enableKeyEvents:true,
		listeners:{
	 		keydown:{
	 			fn:function(caja, e) {
	 				if(!caja.isValid()){
	 					caja.setValue(caja.getValue().substring(0, 30));
	 				}
	 			}
	 		},
	 		keyup:{
	 			fn:function(caja, e) {	 				
	 				if(!caja.isValid()){
	 					caja.setValue(caja.getValue().substring(0, 30));
	 				}
	 				
	 			}
	 		},
			change:	{
				fn: function(caja, valor){
		
				}
			}
		}	
	};
	
	NS.txtSucursal = {
		xtype: 'uppertextfield',
		id: PF + 'txtSucursal',
		name: PF + 'txtSucursal',
		x: 530,
		y: 135,
		width: 150,
		tabIndex: 16,
		maxLength: 30,
		enableKeyEvents:true,
		listeners:{
	 		keydown:{
	 			fn:function(caja, e) {
	 				if(!caja.isValid()){
	 					caja.setValue(caja.getValue().substring(0, 30));
	 				}
	 			}
	 		},
	 		keyup:{
	 			fn:function(caja, e) {	 				
	 				if(!caja.isValid()){
	 					caja.setValue(caja.getValue().substring(0, 30));
	 				}
	 				
	 			}
	 		},
			change:	{
				fn: function(caja, valor){
		
				}
			}
		}			
	};
	
	NS.txtSaldoInicial = new Ext.form.NumberField({
		id: PF + 'txtSaldoInicial',
		name: PF + 'txtSaldoInicial',
		x: 10,
		y: 185,
		width: 130,
		tabIndex: 17,
		maxLength: 38,
		enableKeyEvents:true,
		listeners:
		{
			keydown:{
	 			fn:function(caja, e) {
	 				if(!caja.isValid()){
	 					var texto=caja.getRawValue()+'';
	 					caja.setRawValue(texto.substring(0, 38));
	 				}
	 			}
	 		},
	 		keyup:{
	 			fn:function(caja, e) {	 				
	 				if(!caja.isValid()){
	 					var texto=caja.getRawValue()+'';
	 					caja.setRawValue(texto.substring(0, 38));
	 				}
	 				
	 			}
	 		},
		}
	});
	
	NS.txtSaldoMinimo = new Ext.form.NumberField({
		id: PF + 'txtSaldoMinimo',
		name: PF + 'txtSaldoMinimo',
		x: 180,
		y: 185,
		width: 130,
		tabIndex: 18,
		maxLength: 38,
		enableKeyEvents:true,
		listeners:
		{
			keydown:{
	 			fn:function(caja, e) {
	 				if(!caja.isValid()){
	 					var texto=caja.getRawValue()+'';
	 					caja.setRawValue(texto.substring(0, 38));
	 				}
	 			}
	 		},
	 		keyup:{
	 			fn:function(caja, e) {	 				
	 				if(!caja.isValid()){
	 					var texto=caja.getRawValue()+'';
	 					caja.setRawValue(texto.substring(0, 38));
	 				}
	 				
	 			}
	 		},
		}
	});
	
	NS.txtSaldoInicialBanco = new Ext.form.NumberField({
		id: PF + 'txtSaldoInicialBanco',
		name: PF + 'txtSaldoInicialBanco',
		x: 10,
		y: 235,
		width: 130,
		tabIndex: 20,
		maxLength: 38,
		enableKeyEvents:true,
		listeners:
		{
			keydown:{
	 			fn:function(caja, e) {
	 				if(!caja.isValid()){
	 					var texto=caja.getRawValue()+'';
	 					caja.setRawValue(texto.substring(0, 38));
	 				}
	 			}
	 		},
	 		keyup:{
	 			fn:function(caja, e) {	 				
	 				if(!caja.isValid()){
	 					var texto=caja.getRawValue()+'';
	 					caja.setRawValue(texto.substring(0, 38));
	 				}
	 				
	 			}
	 		},
		}
	});
	
	NS.txtUltimoChequeImpreso = new Ext.form.NumberField({
		id: PF + 'txtUltimoChequeImpreso',
		name: PF + 'txtUltimoChequeImpreso',
		x: 180, 
		y: 235,
		width: 130,
		tabIndex: 21,
		maxLength: 11,
		enableKeyEvents:true,
		listeners:
		{
			keydown:{
	 			fn:function(caja, e) {
	 				if(!caja.isValid()){
	 					var texto=caja.getRawValue()+'';
	 					caja.setRawValue(texto.substring(0, 11));
	 				}
	 			}
	 		},
	 		keyup:{
	 			fn:function(caja, e) {	 				
	 				if(!caja.isValid()){
	 					var texto=caja.getRawValue()+'';
	 					caja.setRawValue(texto.substring(0, 11));
	 				}
	 				
	 			}
	 		},
		}
	});
	
	NS.txtSaldoInicialContabilidad = new Ext.form.NumberField({
		id: PF + 'txtSaldoInicialContabilidad',
		name: PF + 'txtSaldoInicialContabilidad',
		x: 10, 
		y: 285,
		width: 130,
		tabIndex: 22,
		maxLength: 38,
		enableKeyEvents:true,
		listeners:
		{
			keydown:{
	 			fn:function(caja, e) {
	 				if(!caja.isValid()){
	 					var texto=caja.getRawValue()+'';
	 					caja.setRawValue(texto.substring(0, 38));
	 				}
	 			}
	 		},
	 		keyup:{
	 			fn:function(caja, e) {	 				
	 				if(!caja.isValid()){
	 					var texto=caja.getRawValue()+'';
	 					caja.setRawValue(texto.substring(0, 38));
	 				}
	 				
	 			}
	 		},
		}
	});
	
	NS.txtAbonoSBC = new Ext.form.NumberField({
		id: PF + 'txtAbonoSBC',
		name: PF + 'txtAbonoSBC',
		x: 180, 
		y: 285,
		width: 130,
		tabIndex: 23,
		maxLength: 38,
		enableKeyEvents:true,
		listeners:
		{
			keydown:{
	 			fn:function(caja, e) {
	 				if(!caja.isValid()){
	 					var texto=caja.getRawValue()+'';
	 					caja.setRawValue(texto.substring(0, 38));
	 				}
	 			}
	 		},
	 		keyup:{
	 			fn:function(caja, e) {	 				
	 				if(!caja.isValid()){
	 					var texto=caja.getRawValue()+'';
	 					caja.setRawValue(texto.substring(0, 38));
	 				}
	 				
	 			}
	 		},
		}
	});
	
	NS.txtPeriodoConciliacion = new Ext.form.TextField({
		id: PF + 'txtPeriodoConciliacion',
		name: PF + 'txtPeriodoConciliacion',
		x: 10,
		y: 335,
		width: 70, 
		tabIndex: 24,
		maxLength: 6,
		enableKeyEvents:true,
		listeners:
		{
			keydown:{
	 			fn:function(caja, e) {
	 				if(!caja.isValid()){
	 					var texto=caja.getRawValue()+'';
	 					caja.setRawValue(texto.substring(0, 6));
	 				}
	 			}
	 		},
	 		keyup:{
	 			fn:function(caja, e) {	 				
	 				if(!caja.isValid()){
	 					var texto=caja.getRawValue()+'';
	 					caja.setRawValue(texto.substring(0, 6));
	 				}
	 				
	 			}
	 		},
		}
	});
	
	NS.txtMapeoERP = new Ext.form.TextField({
		id: PF + 'txtMapeoERP',
		name: PF + 'txtMapeoERP',
		x: 180,
		y: 335,
		width: 100,
		tabIndex: 25,
		hidden:true
	});
	
	NS.txtClabe = new Ext.form.TextField({
		id: PF + 'txtClabe',
		name: PF + 'txtClabe',
		x: 10,
		y: 385,
		width: 150,
		autoCreate: {tag: 'input', type: 'text', size: '20', maxlength: '18'},
		tabIndex: 26,
		maxLength: 18,
		enableKeyEvents:true,
		listeners:{
	 		keydown:{
	 			fn:function(caja, e) {
	 				if(!caja.isValid()){
	 					caja.setValue(caja.getValue().substring(0, 18));
	 				}
	 			}
	 		},
	 		keyup:{
	 			fn:function(caja, e) {	 				
	 				if(!caja.isValid()){
	 					caja.setValue(caja.getValue().substring(0, 18));
	 				}
	 				
	 			}
	 		},
			change:	{
				fn: function(caja, valor){
		
				}
			}
		}	
	});
	
	NS.txtChequeraContrato = new Ext.form.TextField({
		id: PF + 'txtChequeraContrato',
		name: PF + 'txtChequeraContrato',
		x: 200,
		y: 385,
		width: 150,
		tabIndex: 27,
		hidden:true
	});
	
	NS.txtAbaSwift = new Ext.form.TextField({
		id: PF + 'txtAbaSwift',
		name: PF + 'txtAbaSwift',
		x: 70,
		y: 12,
		width: 150, 
		tabIndex: 30,
		maxLength: 15,
		enableKeyEvents:true,
		listeners:{
	 		keydown:{
	 			fn:function(caja, e) {
	 				if(!caja.isValid()){
	 					caja.setValue(caja.getValue().substring(0, 15));
	 				}
	 			}
	 		},
	 		keyup:{
	 			fn:function(caja, e) {	 				
	 				if(!caja.isValid()){
	 					caja.setValue(caja.getValue().substring(0, 15));
	 				}
	 				
	 			}
	 		},
			change:	{
				fn: function(caja, valor){
		
				}
			}
		}		
	});
	
	
	NS.txtSobregiro = new Ext.form.TextField({
		id: PF + 'txtSobregiro',
		name: PF + 'txtSobregiro',
		x: 350,
		y: 185,
		width: 150,
		tabIndex: 19,
		maxLength: 38,
		enableKeyEvents:true,
		listeners:
		{
			keydown:{
	 			fn:function(caja, e) {
	 				if(!caja.isValid()){
	 					var texto=caja.getRawValue()+'';
	 					caja.setRawValue(texto.substring(0, 38));
	 				}
	 			}
	 		},
	 		keyup:{
	 			fn:function(caja, e) {	 				
	 				if(!caja.isValid()){
	 					var texto=caja.getRawValue()+'';
	 					caja.setRawValue(texto.substring(0, 38));
	 				}
	 				
	 			}
	 		},
		}
	});
	
	//TEXTFIELD ASIGNACION DE FOLIOS DE CHEQUE
	NS.txtBancoCheques = new Ext.form.TextField({
		id: PF + 'txtBancoCheques',
		name: PF + 'txtBancoCheques',
		x: 10,
		y: 15,
		width: 150
	});
	
	NS.txtChequeraCheques = new Ext.form.TextField({
		id: PF + 'txtChequeraCheques',
		name: PF + 'txtChequeraCheques',
		x: 10,
		y: 65,
		width: 150
	});
	
	NS.txtIdCajaCheques = new Ext.form.TextField({
		id: PF + 'txtIdCajaCheques',
		name: PF + 'txtIdCajaCheques',
		x: 10, 
		y: 115,
		width: 50,
		listeners: {
			change: {
					fn: function(caja, valor){
						BFwrk.Util.updateTextFieldToCombo(PF + 'txtIdCajaCheques', NS.cmbCajaCheques.getId());						
					}
			}
		}	
	});
	
	NS.txtLoteCheque = new Ext.form.TextField({
		id: PF + 'txtLoteCheque',
		name: PF + 'txtLoteCheque',
		x: 0,
		y: 15,
		width: 100		
	});
	
	NS.txtFolioInicial = new Ext.form.TextField({
		id: PF + 'txtFolioInicial',
		name: PF + 'txtFolioInicial',
		x: 0,
		y: 65,
		width: 100
	});
	
	NS.txtFolioFinal = new Ext.form.TextField({
		id: PF + 'txtFolioFinal',
		name: PF + 'txtFolioFinal',
		x: 0,
		y: 115,
		width: 100		
	});
	
	NS.txtUltimoChequeImp = new Ext.form.TextField({
		id: PF + 'txtUltimoChequeImp',
		name: PF + 'txtUltimoChequeImp',
		x: 0,
		y: 165,
		width: 100
	});
	
	//TEXTFIELD DE LA ASIGNACION DE DIVISIONES
	NS.txtEmpresaDivisiones = new Ext.form.TextField({
		id: PF + 'txtEmpresaDivisiones',
		name: PF + 'txtEmpresaDivisiones',
		x: 10,
		y: 30,
		width: 300
	});
	
	NS.txtBancoDivisiones = new Ext.form.TextField({
		id: PF + 'txtBancoDivisiones',
		name: PF + 'txtBancoDivisiones',
		x: 10,
		y: 80,
		width: 50
	});
	
	NS.txtDescBancoDivisiones = new Ext.form.TextField({
		id: PF + 'txtDescBancoDivisiones',
		name: PF + 'txtDescBancoDivisiones',
		x: 70, 
		y: 80, 
		width: 150
	});
	
	NS.txtChequeraDivisiones = new Ext.form.TextField({
		id: PF + 'txtChequeraDivisiones',
		name: PF + 'txtChequeraDivisiones',
		x: 250,
		y: 80,
		width: 150
	});
	
	
	
	//FUNCIONES
	//Busca las chequeras ya insertadas dependiendo la empresa y el banco seleccionados
	NS.buscar = function(){
		NS.noEmpresaCombo = Ext.getCmp(PF + 'txtIdEmpresa').getValue();
		NS.idBanco = Ext.getCmp(PF + 'txtIdBanco').getValue();
				
		if(NS.noEmpresaCombo == ""){					
			Ext.Msg.alert('SET', 'Debes de seleccionar una empresa');
		}
		
		if(NS.idBanco == "" || NS.idBanco == 0){				
			NS.idBanco = 0;
		}
		
		NS.storeLlenaGrid.baseParams.noEmpresa = parseInt(NS.noEmpresaCombo);
		NS.storeLlenaGrid.baseParams.idBanco = parseInt(NS.idBanco);
		NS.storeLlenaGrid.load();
		
	};
	
	//Cambia el combo del banco de la consulta dependiendo la empresa
	NS.cambiaBanco = function(valueCombo){
		Ext.getCmp(PF + 'txtIdBanco').reset();
		NS.cmbBanco.reset();
		NS.storeBancos.removeAll();
		
		if ((valueCombo != null && valueCombo != undefined && valueCombo != '') || valueCombo == 0){
			if (valueCombo == 0)
				Ext.getCmp(PF + 'txtIdEmpresa').setValue(valueCombo);
			
			NS.storeBancos.baseParams.noEmpresa = parseInt(valueCombo);
			NS.storeBancos.load();
			//NS.noEmpresa = valueCombo;
		}
	};
	
	//limpia la pantalla
	NS.limpiar = function(){			
		Ext.getCmp(PF + 'txtIdBanco').setValue('');
		NS.cmbBanco.reset();
		Ext.getCmp(PF + 'txtIdEmpresa').setValue('');
		NS.cmbEmpresa.reset();
		NS.cmbTipoChequeras.reset();
		NS.cmbGrupo.reset();
		NS.gridDatos.store.removeAll();
		NS.gridDatos.getView().refresh();		
	};
	
	NS.llenaDatos = function(){
		var registroSeleccionado = NS.gridDatos.getSelectionModel().getSelections();
		
		NS.storeBancosTodos.baseParams.tipoBanco=registroSeleccionado[0].get('nacionalidad');
		NS.storeBancosTodos.load();
		
		//Valor de los TextField
		Ext.getCmp(PF + 'panelBancos').setDisabled(true);
		Ext.getCmp(PF + 'panelCrearNuevo').setTitle('Modificar Chequera');
		
		Ext.getCmp(PF + 'txtIdBancoNuevo').setDisabled(true);
		Ext.getCmp(PF + 'txtChequeraNueva').setValue(registroSeleccionado[0].get('idChequera'));
		Ext.getCmp(PF + 'txtChequeraNueva').setDisabled(true);
		
		Ext.getCmp(PF + 'cmbBancoNuevo').setValue(registroSeleccionado[0].get('descripcion'));
		Ext.getCmp(PF + 'txtIdDivision').setValue(registroSeleccionado[0].get('idDivision'));
		Ext.getCmp(PF + 'txtIdDivisa').setValue(registroSeleccionado[0].get('idDivisa'));
		Ext.getCmp(PF + 'txtIdDivisa').setDisabled(true);
		Ext.getCmp(PF + 'cmbDivisa').setValue(registroSeleccionado[0].get('descDivisa'));
		Ext.getCmp(PF + 'txtIdTipoChequerasNuevo').setValue(registroSeleccionado[0].get('tipoChequera'));
		Ext.getCmp(PF + 'txtIdTipoChequerasNuevo').setDisabled(true);
		Ext.getCmp(PF + 'txtDescChequera').setValue(registroSeleccionado[0].get('descChequera'));
		Ext.getCmp(PF + 'txtPlaza').setValue(registroSeleccionado[0].get('plaza'));
		Ext.getCmp(PF + 'txtSucursal').setValue(registroSeleccionado[0].get('sucursal'));
		Ext.getCmp(PF + 'txtSaldoInicial').setValue(registroSeleccionado[0].get('saldoInicial'));
		Ext.getCmp(PF + 'txtSaldoMinimo').setValue(registroSeleccionado[0].get('saldoMinimo'));
		Ext.getCmp(PF + 'txtSaldoInicialBanco').setValue(registroSeleccionado[0].get('saldoBcoInicial'));
		Ext.getCmp(PF + 'txtUltimoChequeImpreso').setValue(registroSeleccionado[0].get('ultimoCheque'));
		Ext.getCmp(PF + 'txtSaldoInicialContabilidad').setValue(registroSeleccionado[0].get('saldoConta'));
		Ext.getCmp(PF + 'txtAbonoSBC').setValue(registroSeleccionado[0].get('abonoSBC'));
		Ext.getCmp(PF + 'txtPeriodoConciliacion').setValue(registroSeleccionado[0].get('periodoConciliacion'));
		Ext.getCmp(PF + 'txtMapeoERP').setValue(registroSeleccionado[0].get('houseBank'));
		Ext.getCmp(PF + 'txtClabe').setValue(registroSeleccionado[0].get('clabe'));
		Ext.getCmp(PF + 'txtChequeraContrato').setValue(registroSeleccionado[0].get('chequeraContrato'));
		Ext.getCmp(PF + 'txtIdBancoNuevo').setValue(registroSeleccionado[0].get('idBanco'));
		
		
		if (registroSeleccionado[0].get('aba') != '')
			Ext.getCmp(PF + 'txtAbaSwift').setValue(registroSeleccionado[0].get('aba'));
		else
			Ext.getCmp(PF + 'txtAbaSwift').setValue(registroSeleccionado[0].get('swift'));
		
		Ext.getCmp(PF + 'txtSobregiro').setValue(registroSeleccionado[0].get('sobregiro'));
		
		//Valor de los combos

		Ext.getCmp(PF + 'cmbBancoNuevo').setValue(registroSeleccionado[0].get('descripcion'));
		Ext.getCmp(PF + 'cmbBancoNuevo').setDisabled(true);
		Ext.getCmp(PF + 'cmbDivision').setValue(registroSeleccionado[0].get('descDivision'));
		
		Ext.getCmp(PF + 'cmbTipoChequerasNuevo').setValue(registroSeleccionado[0].get('descTipoChequera'));
		
		//Valor de los checkBox
		//Cheques
		if (registroSeleccionado[0].get('bCheques') == 'S')
			Ext.getCmp(PF + 'chkCheques').setValue(true);
		else
			Ext.getCmp(PF + 'chkCheques').setValue(false);
		
		//Transferencias
		if (registroSeleccionado[0].get('bTransferencias') == 'S')
			Ext.getCmp(PF + 'chkTransferencias').setValue(true);
		else
			Ext.getCmp(PF + 'chkTransferencias').setValue(false);
		
		//Cheque Ocurre
		if (registroSeleccionado[0].get('chequeOcurre') == 'S')
			Ext.getCmp(PF + 'chkChequeOcurre').setValue(true);
		else
			Ext.getCmp(PF + 'chkChequeOcurre').setValue(false);
		
		
		if (registroSeleccionado[0].get('cargoEnCuenta') == 'S')
			Ext.getCmp(PF + 'chkCargoEnCuenta').setValue(true);
		else
			Ext.getCmp(PF + 'chkCargoEnCuenta').setValue(false);
		
		
		if(registroSeleccionado[0].get('bTraspaso') == 'S')
			Ext.getCmp(PF + 'chkChequeraTraspasos').setValue(true);
		else
			Ext.getCmp(PF + 'chkChequeraTraspasos').setValue(false);
		
		if (registroSeleccionado[0].get('bConcentradora') == 'S' && registroSeleccionado[0].get('tipoChequera') == 'C')
			Ext.getCmp(PF + 'chkDepositos').setValue(true);
		else{
			Ext.getCmp(PF + 'chkDepositos').setValue(false);
			Ext.getCmp(PF + 'chkDepositos').setDisabled(true);
		}
		
		if (registroSeleccionado[0].get('tipoChequera') == 'C')
			Ext.getCmp(PF + 'chkDepositos').setDisabled(false);
		
		if (registroSeleccionado[0].get('massPayment') == 'S')
			Ext.getCmp(PF + 'chkMassPayment').setValue(true);
		else
			Ext.getCmp(PF + 'chkMassPayment').setValue(false);
		
		
		if (registroSeleccionado[0].get('idBanco') == '14')
			Ext.getCmp(PF + 'chkMassPayment').setDisabled(false);
		else
			Ext.getCmp(PF + 'chkMassPayment').setDisabled(true);
		
		
		
		if (registroSeleccionado[0].get('idDivisa') == 'MN')
			Ext.getCmp(PF + 'optTipoBanco').setValue(0);
		else		
			Ext.getCmp(PF + 'optTipoBanco').setValue(1);
		
		
		
		if (registroSeleccionado[0].get('nacionalidad') == 'N' && registroSeleccionado[0].get('idDivisa') == 'DLS')
		{		
			NS.panelAbaSwift.setVisible(true);
			Ext.getCmp(PF + 'optAba').setValue(1);	
			Ext.getCmp(PF + 'optTipoBanco').setValue(1);
		}				
		else if(registroSeleccionado[0].get('nacionalidad') == 'E'){
			NS.panelAbaSwift.setVisible(true);
			Ext.getCmp(PF + 'optAba').setValue(0);
			Ext.getCmp(PF + 'optTipoBanco').setValue(1);
			
		}
		else{			
			NS.panelAbaSwift.setVisible(true);
		}
		
		if(registroSeleccionado[0].get('nacionalidad') == 'N'){
			NS.optTipoBancos.setValue(0);
		}else if(registroSeleccionado[0].get('nacionalidad') == 'E'){
			NS.optTipoBancos.setValue(1);
		}
		

		//Para habilitar el boton de asociacion de divisiones
		MantenimientoChequerasAction.configuraSet(243, function(resultado, e)
		{
			if (resultado != '' && resultado != null)
			{
				if (resultado == 'SI'){			
					Ext.getCmp(PF + 'asociarDivisiones').setDisabled(false);
				}
				else{				
					Ext.getCmp(PF + 'asociarDivisiones').setDisabled(true);
				}
			}
		});
		
	};

	NS.activaPanelNuevo = function(){		
		//NS.facultadModificaChequeras(136);
		NS.panelBusqueda.setVisible(false);
		NS.panelGrid.setVisible(false);
		NS.panelReporte.setVisible(false);
		
		NS.storeBancosTodos.baseParams.tipoBanco = NS.tipoBanco;
		NS.storeBancosTodos.load();	
	 	
	 	//Llena el combo de la divisa
	 	NS.storeLlenaComboDivisa.load();
	 	
	 	//Llena el combo del tipo de chequeras		 				 	
	 	NS.storeTipoChequerasNuevo.load();
 		 	
	 	if (NS.modificar){	
	 		NS.facultadModificar = false;
	 		
	 		NS.facultadModificaChequeras('136');	 		
	 		
	 		Ext.getCmp(PF + 'btnLimpiar').setVisible(false);
	 		//Ext.getCmp(PF + '...').setVisible(true);
	 		NS.llenaDatos();	 		
	 	}
	 	else
	 	{	 	
	 		//Ext.getCmp(PF + '...').setVisible(false);
	 		
	 		Ext.getCmp(PF + 'panelBancos').setDisabled(false);
	 		Ext.getCmp(PF + 'btnLimpiar').setVisible(false);
	 		Ext.getCmp(PF + 'txtIdBancoNuevo').setValue('');
			Ext.getCmp(PF + 'txtIdBancoNuevo').setDisabled(false);
			Ext.getCmp(PF + 'cmbBancoNuevo').setDisabled(false);
	 		NS.cmbBancoNuevo.reset();
	 		Ext.getCmp(PF + 'txtChequeraNueva').setValue('');
	 		Ext.getCmp(PF + 'txtChequeraNueva').setDisabled(false);
	 		Ext.getCmp(PF + 'txtIdDivision').setValue('');
	 		NS.cmbDivision.reset();
	 		Ext.getCmp(PF + 'txtIdDivisa').setValue('');
	 		NS.cmbDivisa.reset();
	 		Ext.getCmp(PF + 'txtIdTipoChequerasNuevo').setValue('');
	 		NS.cmbTipoChequerasNuevo.reset();
	 		Ext.getCmp(PF + 'txtDescChequera').setValue('');
	 		Ext.getCmp(PF + 'txtPlaza').setValue('');
	 		Ext.getCmp(PF + 'txtSucursal').setValue('');
	 		Ext.getCmp(PF + 'txtSaldoInicial').setValue('0.00');
	 		Ext.getCmp(PF + 'txtSaldoMinimo').setValue('0.00');
	 		Ext.getCmp(PF + 'txtSaldoInicialBanco').setValue('0.00');
	 		Ext.getCmp(PF + 'txtUltimoChequeImpreso').setValue('');
	 		Ext.getCmp(PF + 'txtSaldoInicialContabilidad').setValue('0.00');
	 		Ext.getCmp(PF + 'txtAbonoSBC').setValue('0.00');
	 		Ext.getCmp(PF + 'txtPeriodoConciliacion').setValue('');
	 		Ext.getCmp(PF + 'txtMapeoERP').setValue('');
	 		Ext.getCmp(PF + 'txtClabe').setValue('');
	 		Ext.getCmp(PF + 'txtChequeraContrato').setValue('');
	 		Ext.getCmp(PF + 'txtAbaSwift').setValue('');
	 		Ext.getCmp(PF + 'txtSobregiro').setValue('0.00');
	 		Ext.getCmp(PF + 'chkCheques').setValue(false);
	 		Ext.getCmp(PF + 'chkTransferencias').setValue(false);
	 		Ext.getCmp(PF + 'chkChequeOcurre').setValue(false);	
	 		Ext.getCmp(PF + 'chkCargoEnCuenta').setValue(false);
	 		Ext.getCmp(PF + 'chkChequeraTraspasos').setValue(false);
	 		Ext.getCmp(PF + 'chkDepositos').setValue(false);
	 		Ext.getCmp(PF + 'chkDepositos').setDisabled(false);
	 		Ext.getCmp(PF + 'chkMassPayment').setValue(false);
	 		Ext.getCmp(PF + 'chkMassPayment').setDisabled(false);
	 		Ext.getCmp(PF + 'asociarDivisiones').setDisabled(true);
	 	}
	 	
	 	MantenimientoChequerasAction.configuraSet(1, function(resultado, e)
		{
	 		if (resultado != null && resultado != undefined && resultado != '')
	 		{
	 			if (resultado == 'CIE')
	 			{
	 				NS.localizacion = resultado;
	 				//Llena el combo de las divisiones si la empresa es CIE
	 				if (NS.noEmpresaCombo == '' || NS.noEmpresaCombo == 0 || NS.noEmpresaCombo == undefined)	 					
	 					NS.storeLlenaComboDivision.baseParams.noEmpresa = parseInt(NS.noEmpresa); //Se asigna la empresa global
	 				else
	 					NS.storeLlenaComboDivision.baseParams.noEmpresa = parseInt(NS.noEmpresaCombo);
	 				
	 			 	NS.storeLlenaComboDivision.load();

	 				Ext.getCmp(PF + 'txtIdDivision').setDisabled(false);
	 				Ext.getCmp(PF + 'cmbDivision').setDisabled(false);
	 			}
	 			else
	 			{
	 				Ext.getCmp(PF + 'txtIdDivision').setDisabled(true);
	 				Ext.getCmp(PF + 'cmbDivision').setDisabled(true);	 			
	 			}
	 		}	 			
		});
	 	NS.panelCrearNuevo.setVisible(true);
	};
	
	NS.eliminar = function(){
		var registro = NS.gridDatos.getSelectionModel().getSelections();
		
		if (registro.length <= 0)
			Ext.Msg.alert('SET','Debes de seleccionar algun registro para eliminar!');
		else{	
			Ext.Msg.confirm('SET', '¿está seguro de eliminar esta chequera: ' + registro[0].get('idChequera') + '?', function(btn){
				if (btn === 'yes')
					if (registro[0].get('idBanco') != '' && (registro[0].get('saldoFinal') == 0 && registro[0].get('abonoSBC') == 0) && 
						registro[0].get('cargo') == 0 && registro[0].get('abono') == 0){

						//Se elimina chequera						
						MantenimientoChequerasAction.eliminaChequeras(parseInt(registro[0].get('noEmpresa')), parseInt(registro[0].get('idBanco')), 
								registro[0].get('idChequera'), NS.idUsuario, function(resultado, e){
							
							if (resultado == 100)
								Ext.Msg.alert('SET', 'Chequera con pagos pendientes de confirmar!!');
							else if (resultado > 0){
								Ext.Msg.alert('SET', 'Chequera eliminada correctamente!!');
								NS.buscar();
							}else
								Ext.Msg.alert('SET', 'Ocurrio un problema durante la eliminacion!!');								
						});
					}
					else{
						Ext.Msg.alert('SET', 'El registro no se puede eliminar. Los saldos de la chequera debe estar en 0.00');						   
					}				
			});							
		}			
	};
			
	NS.insertaOModificaDatos = function(){	
		
		var registro = NS.gridDatos.getSelectionModel().getSelections();
		var vector = {};
		var matriz = new Array();
		var nacionalidad = Ext.getCmp(PF + 'optTipoBanco').getValue();
		NS.nacionalidad = parseInt(nacionalidad.getGroupValue());
		
		var tipoAbaSwift = Ext.getCmp(PF + 'optAba').getValue();
		NS.tipoAbaSwift = parseInt(tipoAbaSwift.getGroupValue());
		
		NS.noEmpresaCombo = Ext.getCmp(PF + 'txtIdEmpresa').getValue();		
		if (NS.noEmpresaCombo == '' || NS.noEmpresaCombo == 0 || NS.noEmpresaCombo == undefined){			
			vector.noEmpresa = NS.noEmpresa;
			}
		else{		
			vector.noEmpresa = NS.noEmpresaCombo;
		}
		
		vector.idBanco = Ext.getCmp(PF + 'txtIdBancoNuevo').getValue();
		vector.descripcion = Ext.getCmp(PF + 'cmbBancoNuevo').getValue();		
		vector.idChequera = Ext.getCmp(PF + 'txtChequeraNueva').getValue();
		if (NS.modificar)
			vector.idChequeraWhere = registro[0].get('idChequera');
		
		vector.descChequera = Ext.getCmp(PF + 'txtDescChequera').getValue();
		vector.plaza = Ext.getCmp(PF + 'txtPlaza').getValue();
		vector.sucursal = Ext.getCmp(PF + 'txtSucursal').getValue();
		vector.saldoInicial = Ext.getCmp(PF + 'txtSaldoInicial').getValue() == '' ? 0 : Ext.getCmp(PF + 'txtSaldoInicial').getValue();		
		//vector.saldoFinal = Ext.getCmp(PF + 'txtSaldoFinal').getValue();
		vector.ultimoCheque = Ext.getCmp(PF + 'txtUltimoChequeImpreso').getValue() == '' ? 0 : Ext.getCmp(PF + 'txtUltimoChequeImpreso').getValue();
		vector.abonoSBC = Ext.getCmp(PF + 'txtAbonoSBC').getValue();	
		vector.nacionalidad = NS.nacionalidad == 0 ? 'N' : 'E';		
		vector.idDivisa = Ext.getCmp(PF + 'txtIdDivisa').getValue();
		vector.descDivisa = Ext.getCmp(PF + 'cmbDivisa').getValue();
		vector.tipoChequera = Ext.getCmp(PF + 'txtIdTipoChequerasNuevo').getValue();
		vector.descTipoChequera = Ext.getCmp(PF + 'cmbTipoChequerasNuevo').getValue();
		vector.saldoMinimo = Ext.getCmp(PF + 'txtSaldoMinimo').getValue() == '' ? 0 : Ext.getCmp(PF + 'txtSaldoMinimo').getValue();
		vector.saldoBcoInicial = Ext.getCmp(PF + 'txtSaldoInicialBanco').getValue() == '' ? 0 : Ext.getCmp(PF + 'txtSaldoInicialBanco').getValue();
		vector.periodoConciliacion = Ext.getCmp(PF + 'txtPeriodoConciliacion').getValue();
		vector.saldoConta = Ext.getCmp(PF + 'txtSaldoInicialContabilidad').getValue() == '' ? 0 : Ext.getCmp(PF + 'txtSaldoInicialContabilidad').getValue();
		vector.bConcentradora = Ext.getCmp(PF + 'chkDepositos').getValue() == true ? '1' : '0';
		vector.bTraspaso = Ext.getCmp(PF + 'chkChequeraTraspasos').getValue() == true ? '1' : '0';
		vector.bCheques = Ext.getCmp(PF + 'chkCheques').getValue() == true ? '1' : '0';
		vector.bTransferencias = Ext.getCmp(PF + 'chkTransferencias').getValue() == true ? '1' : '0';
		vector.chequeOcurre = Ext.getCmp(PF + 'chkChequeOcurre').getValue() == true ? '1' : '0';
		vector.cargoEnCuenta = Ext.getCmp(PF + 'chkCargoEnCuenta').getValue() == true ? '1' : '0'
		vector.clabe = Ext.getCmp(PF + 'txtClabe').getValue();
		vector.impresionContinua = NS.optImpCon.getValue().inputValue;
		vector.tipoAbaSwift = NS.tipoAbaSwift == 0 ? 'Aba' : 'Swift';
		vector.aba = Ext.getCmp(PF + 'txtAbaSwift').getValue();
		vector.idDivision = Ext.getCmp(PF + 'txtIdDivision').getValue();
		vector.descDivision = Ext.getCmp(PF + 'cmbDivision').getValue();
		vector.sobregiro = Ext.getCmp(PF + 'txtSobregiro').getValue();
		vector.massPayment = Ext.getCmp(PF + 'chkMassPayment').getValue() == true ? '1' : '0';
		vector.houseBank = Ext.getCmp(PF + 'txtMapeoERP').getValue();
		//OjooCambio Edgar Lunes por la tarde.
		vector.chequeraContrato = Ext.getCmp(PF + 'txtChequeraContrato').getValue();
		vector.idUsuario = NS.idUsuario;
		//vector.noEmpresa = Ext.getCmp(PF + 'txtIdEmpresa').getValue();
		vector.localizacion = NS.localizacion;
		vector.fecHoy = NS.fecHoy;
		
		if (NS.modificar)
			vector.tipoOperacion = 'modificar';
		else
			vector.tipoOperacion = 'insertar';		
		
		matriz[0] = vector;
		var jSonString = Ext.util.JSON.encode(matriz);
		
		if (NS.modificar){
			Ext.Msg.confirm('SET', '¿Esta seguro de aplicar los cambios a la chequera?', function(btn){
				if (btn === 'yes'){
					MantenimientoChequerasAction.aceptar(jSonString, function(resultado, e){
						
						if (resultado != 0 && resultado != '' && resultado != undefined)
						{
							
							if (resultado=="Datos actualizados con exito") {
								Ext.Msg.alert('SET', resultado);
								NS.panelCrearNuevo.setVisible(false);
			 					NS.panelBusqueda.setVisible(true);
			 					NS.panelGrid.setVisible(true);
			 					NS.panelReporte.setVisible(true);
								NS.buscar();
							}else{
								Ext.Msg.alert('SET', resultado);
								return;
							}
							
							
						}
					});
				}					
			});
		}
		else{			
			MantenimientoChequerasAction.aceptar(jSonString, function(resultado, e){
				if (resultado != 0 && resultado != '' && resultado != undefined)
				{
					if(Ext.getCmp(PF + 'txtIdBanco').getValue() == '') {
						NS.cambiaBanco(Ext.getCmp(PF + 'txtIdEmpresa').getValue());
						var nuevoBanco = Ext.getCmp(PF + 'txtIdBancoNuevo').getValue();
						
						if(nuevoBanco != '') {
							Ext.getCmp(PF + 'txtIdBanco').setValue(nuevoBanco);
							//BFwrk.Util.updateTextFieldToCombo(PF + 'txtIdBanco', NS.cmbBanco.getId());
							Ext.getCmp(PF + 'cmbBanco').setValue(NS.storeBancosTodos.getById(nuevoBanco).get('descripcion'));
							
						}
					}
//					Ext.Msg.alert('SET', resultado);
//					NS.panelCrearNuevo.setVisible(false);
//					//Edgar Modifico
// 					NS.panelBusqueda.setVisible(true);
// 					NS.panelGrid.setVisible(true);
// 					NS.panelReporte.setVisible(true);
//					
// 					NS.buscar();
					
					if (resultado=="Datos insertados con exito") {
						Ext.Msg.alert('SET', resultado);
						NS.panelCrearNuevo.setVisible(false);
	 					NS.panelBusqueda.setVisible(true);
	 					NS.panelGrid.setVisible(true);
	 					NS.panelReporte.setVisible(true);
	 					
						NS.buscar();
					}else{
						Ext.Msg.alert('SET', resultado);
						return;
					}
 					
				}else{
					MantenimientoChequerasAction.guardarChequera(jSonString, function(resultado, e){
						if (resultado != null && resultado != '' && resultado != undefined){
							if (resultado.Status) {
								Ext.Msg.alert('SET', "Exito al guardar.");
							} else {
								Ext.Msg.alert('SET', resultado.msg);
							}
							
						}else{
							
							Ext.Msg.alert('SET', "Error Desconocido");
						}
					});				
					
				}
			});	
		}
	};
	
	NS.validaDatosCheque = function(){
		var datosCorrectos = true;
		
		if (Ext.getCmp(PF + 'txtIdBancoNuevo') == ''){
			Ext.Msg.alert('SET', 'No hay banco seleccionado');
			datosCorrectos = false;
		}
		
		if (Ext.getCmp(PF + 'txtChequeraNueva') == ''){
			Ext.Msg.alert('SET', 'Falta la chequera');
			datosCorrectos = false;
		}
		
		if (datosCorrectos){
			NS.panelCrearNuevo.setVisible(false);
			NS.storeCajas.load();
			NS.panelCheque.setVisible(true);
		}		
	};
	
	NS.cierraPanelCheques = function(){
		NS.panelCheque.setVisible(false);
		NS.panelCrearNuevo.setVisible(true);
	};
	
	//STORE
	//STORE DE PANTALLA PRINCIPAL
	//Llena el combo de la empresa de la pantalla de consultas
	NS.storeEmpresas = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams: {idUsuario: NS.idUsuario},
		paramOrder: ['idUsuario'],
		directFn: MantenimientoChequerasAction.obtenerEmpresas,
		idProperty: 'noEmpresa',
		fields:[
		 	{name: 'noEmpresa'},
		 	{name: 'nomEmpresa'}
		],
		listeners:{
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeEmpresas, msg: "Cargando..."});
				if (records.length == null || records.length <= 0){
					Ext.Msg.alert('SET', 'No tiene empresas asignadas');
				}
				//Se agrega la opcion de todas las empresas
				/*var recordsStoreEmpresas = NS.storeEmpresas.recordType;
				var todas = new recordsStoreEmpresas({
					noEmpresa: 0,
					nomEmpresa: '***************TODAS***************'
				});
				NS.storeEmpresas.insert(0, todas);*/
			}
		}
	});
	NS.storeEmpresas.load();

	//Llena el combo del banco de la pantalla de consultas
	NS.storeBancos = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams: {noEmpresa: 0},
		paramOrder: ['noEmpresa'],
		directFn: MantenimientoChequerasAction.obtenerBancos,
		idProperty: 'idBanco',
		fields:[
		 	{name: 'idBanco'},
		 	{name: 'descripcion'}
		],
		listeners:{
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeBancos, msg: "Cargando..."});
				if (records.length == null || records.length <= 0)
				{
					Ext.Msg.alert('SET', 'No existen bancos para esta empresa');
				}				
			}
		}
		
	});	
		
	//Llena el grid con los registros de las chequeras encontradas
	NS.storeLlenaGrid = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams: {},
		root: '',
		paramOrder: ['noEmpresa', 'idBanco'],
		directFn:  MantenimientoChequerasAction.llenaGrid, 
		fields: [
		    {name: 'noEmpresa'},     
		    {name: 'idBanco'},
		    {name: 'descripcion'},
		 	{name: 'idChequera'},
		 	{name: 'plaza'},
		 	{name: 'sucursal'},
		 	{name: 'descChequera'},
		 	{name: 'saldoInicial'},
		 	{name: 'cargo'},
		 	{name: 'abono'},		 	
		 	{name: 'saldoFinal'},
		 	{name: 'ultimoCheque'},
		 	{name: 'abonoSBC'},
		 	{name: 'nacionalidad'},
		 	{name: 'idDivisa'},
		 	{name: 'descDivisa'},
		 	{name: 'tipoChequera'},
		 	{name: 'descTipoChequera'},
		 	{name: 'saldoMinimo'},
		 	{name: 'saldoBcoInicial'},
		 	{name: 'periodoConciliacion'},
		 	{name: 'saldoConta'},
		 	{name: 'bConcentradora'},
		 	{name: 'bTraspaso'},
		 	{name: 'bCheques'},
		 	{name: 'bTransferencias'},
		 	{name: 'chequeOcurre'},
		 	{name: 'cargoEnCuenta'},
		 	{name: 'clabe'},
		 	{name: 'impresionContinua'},
		 	{name: 'aba'},
		 	{name: 'swift'},
		 	{name: 'idDivision'},
		 	{name: 'descDivision'},
		 	{name: 'sobregiro'},
		 	{name: 'massPayment'},
		 	{name: 'houseBank'}	,
		 	{name: 'chequeraContrato'}
		    ],
			listeners: {
				load: function(s, records) {
					var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeLlenaGrid, msg: "Buscando..."});
					
					if(records.length == null || records.length <= 0)
						Ext.Msg.alert('SET', 'No hay chequeras dadas de alta!!');
				}
			}
	});	
	//NS.storeLlenaGrid.load();

	//Store tipo de chequeras
    NS.storeTipoChequeras = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		directFn: MantenimientoChequerasAction.obtieneTipoChequeras, 
		idProperty: 'idString', 
		fields: [
			 {name: 'idString'},
			 {name: 'descChequera'}
		],
		listeners: {
			load: function(s, records) {
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeTipoChequeras, msg:"Cargando..."});
				
				if(records.length == null || records.length <= 0)
					Ext.Msg.alert('SET','No tiene tipos de chaqueras dadas de alta');
				
				//Se agrega la opcion todas a storeGrupoEmpresas
	 			var recordsStoreGruEmp = NS.storeTipoChequeras.recordType;	
				var todas = new recordsStoreGruEmp({
					idString : 'T',
					descChequera : '***TODAS***'
		       	});
				NS.storeTipoChequeras.insert('T',todas);
			}
		}
	}); 
	NS.storeTipoChequeras.load();
	
	//Store que llena combo de grupo
	NS.storeLlenaGrupos = new Ext.data.DirectStore ({
		paramsAsHash: false,
		root: '',
		directFn: MantenimientoChequerasAction.llenaComboGrupo,
		idProperty: 'idEntero',
		fields: [
		 	{name: 'idEntero'},
		 	{name: 'descripcion'}
		],
		listeners: {
			load: function(s, records) {
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeLlenaGrupos, msg: "Cargando..."});
				if (records.length == null || records.length <= 0)
					Ext.Msg.alert('SET', 'No hay chequeras.');
			}
		}	
	});
	NS.storeLlenaGrupos.load();
	
	//STORE DE PANEL CREAR NUEVO
	NS.storeBancosTodos = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams: {},
		paramOrder: ['tipoBanco'],
		directFn: MantenimientoChequerasAction.obtenerBancosTodos,
		idProperty: 'idBanco',
		fields:[
		 	{name: 'idBanco'},
		 	{name: 'descripcion'}
		],
		listeners:{
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeBancosTodos, msg: "Cargando..."});
				if (records.length == null || records.length <= 0)
				{
					Ext.Msg.alert('SET', 'No existen bancos para este tipo de nacionalidad');
				}	else{
					if (NS.bancoM!=0) {
						NS.cmbBancoNuevo.setValue(NS.bancoM);
						NS.txtIdBancoNuevo.setValue(NS.bancoM);
						
					} 
				}			
			}
		}
		
	});
	
	NS.storeLlenaComboDivision = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams: {},
		paramOrder: ['noEmpresa'],
		directFn: MantenimientoChequerasAction.llenaComboDivision,
		idProperty: 'idDivision',
		fields:[
		        {name: 'idDivision'},
		        {name: 'descripcion'}
		],
		listeners:{
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeLlenaComboDivision, msg: "Cargando..."});
				if (records.length == null || records.length <= 0)
					Ext.Msg.alert('SET', 'No existen divisiones dadas de alta para la empresa');				
			}
			
		}
	});

	NS.storeLlenaComboDivisa = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams: {},
		paramOrder: [],
		directFn: MantenimientoChequerasAction.llenaComboDivisa,
		idProperty: 'idDivisa',
		fields:[
		        {name: 'idDivisa'},
		        {name: 'descripcion'}
		],
		listeners:{
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeLlenaComboDivisa, msg: "Cargando..."});
				if(records.length == null || records.length <= 0)
					Ext.Msg.alert("No existen divisas dadas de alta");
			}
		}
			
	});
	
	//Store tipo de chequeras
	
    NS.storeTipoChequerasNuevo = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		directFn: MantenimientoChequerasAction.obtieneTipoChequeras, 
		idProperty: 'idString', 
		fields: [
			 {name: 'idString'},
			 {name: 'descChequera'}
		],
		listeners: {
			load: function(s, records) {
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeTipoChequerasNuevo, msg:"Cargando..."});
				
				if(records.length == null || records.length <= 0)
					Ext.Msg.alert('SET','No tiene tipos de chaqueras dadas de alta');
			}
		}
	});
	
    //STORE PARA LA CAJA EN ASIGNACION DE CHEQUES
    NS.storeCajas = new Ext.data.DirectStore({
    	paramsAsHash: false,
    	root: '',
    	directFn: MantenimientoChequerasAction.obtieneCajas,
    	idProperty: 'idCaja',
    	fields:[
    	        {name: 'idCaja'},
    	        {name: 'descCaja'}
    	],
    	listeners: {
    		load: function(s, records){
    			var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeCajas, msg: "Cargando..."});
    			if (records.length == null || records.length <= 0)
    				Ext.Msg.alert('SET', 'No existen cajas registradas');
    		}
    	}
    });
    
    //STORE PARA ASIGNACION DE DIVISIONES POR CHEQUERA
    NS.storeDivisionesPorAsignar = new Ext.data.DirectStore({
    	paramsAsHash: false,
    	root: '',
    	directFn: MantenimientoChequerasAction.divisionesPorAsignar,
    	idProperty: 'idDivision',
    	fields:[
    	        {name: 'idDivision'},
    	        {name: 'descDivision'}
    	],
    	listeners:{
    		load: function(s, records)
    		{
    			var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeDivisionesPorAsignar, msg: "Cargando..."});
    			if (records.length == null || records.length <= 0)
    				Ext.Msg.alert('SET', 'No existen divisiones pendientes de asignar');
    		}
    	}
    });
    
    
	//COMBOBOX
	//COMBOS DE LA PANTALLA PRINCIPAL
	NS.cmbEmpresa = new Ext.form.ComboBox ({
		store: NS.storeEmpresas,
		id: PF + 'cmbEmpresa',
		name: PF + 'cmbEmpresa',
		x: 65, 
		y: 15,
		width: 300,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		tabIndex: 1,
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
					BFwrk.Util.updateComboToTextField(PF + 'txtIdEmpresa', NS.cmbEmpresa.getId());
					NS.cambiaBanco(combo.getValue());					
				}
			}
		}
	});

	NS.cmbBanco = new Ext.form.ComboBox ({
		store: NS.storeBancos,
		id: PF + 'cmbBanco',
		name: PF + 'cmbBanco',
		x: 465,
		y: 15,
		width: 185,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		tabIndex: 3,
		valueField: 'idBanco',
		displayField: 'descripcion',
		autocomplete: true,
		emptyText: 'Seleccione un banco',
		triggerAction: 'all',
		value: '',
		visible: false,
		listeners: {
			select: {
				fn: function(combo, valor) {
					BFwrk.Util.updateComboToTextField(PF + 'txtIdBanco', NS.cmbBanco.getId());		
				}
			}
		}		
	});
	
	//Combo empresas	
	NS.cmbTipoChequeras = new Ext.form.ComboBox ({
		store: NS.storeTipoChequeras,
		id: PF + 'cmbTipoChequera',
		name: PF + 'cmbTipoChequera',
		x: 0,
		y: 15,
		width: 150,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		valueField: 'idString',
		displayField: 'descChequera',
		autocomplete: true,
		emptyText: 'Tipo Chequera',
		triggerAction: 'all',
		value: '',
		visible: false,
		listeners: {
			select: {
				fn: function(combo, valor) {
				
				}
			}
		}
	});	
	
	NS.cmbGrupo = new Ext.form.ComboBox ({
		store: NS.storeLlenaGrupos,
		id: PF + 'cmbGrupo',
		name: PF + 'cmbGrupo',
		x: 160,
		y: 15,
		width: 150,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		valueField: 'idEntero',
		displayField: 'descripcion',
		autocomplete: true,
		emptyText: 'Grupo',
		triggerAction: 'all',
		value: '',
		visible: false,
		listeners: {
			select: {
				fn: function(combo, valor) {
		
				}
			}
		}
	});
	
	
	//COMBOS DE LA PANTALLA PARA CREAR NUEVO
	NS.cmbBancoNuevo = new Ext.form.ComboBox ({
		store: NS.storeBancosTodos,
		id: PF + 'cmbBancoNuevo',
		name: PF + 'cmbBancoNuevo',
		x: 270,
		y: 15,
		width: 185,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		tabIndex: 3,
		valueField: 'idBanco',
		displayField: 'descripcion',
		autocomplete: true,
		emptyText: 'Seleccione un banco',
		triggerAction: 'all',
		value: '',
		visible: false,
		listeners: {
			select: {
				fn: function(combo, valor) {
					BFwrk.Util.updateComboToTextField(PF + 'txtIdBancoNuevo', NS.cmbBancoNuevo.getId());
					if (Ext.getCmp(PF + 'txtIdBancoNuevo').getValue() == '14'){					
						Ext.getCmp(PF + 'chkMassPayment').setDisabled(false);
					}
					else{						
						Ext.getCmp(PF + 'chkMassPayment').setDisabled(true);
					}
				}
			}
		}		
	});
	
	NS.cmbDivision = new Ext.form.ComboBox({
		store: NS.storeLlenaComboDivision,
		id: PF + 'cmbDivision',
		name: PF + 'cmbDivision',
		x: 770,
		y: 15,
		width: 185,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		tabIndex: 6,
		valueField: 'idDivision',
		displayField: 'descripcion',
		autocomplete: true,
		emptyText: 'Selecciona una division',
		triggerAction: 'all',
		value: '',
		hidden:true,
		visible: false,
		listeners:{
			select: {
				fn: function(combo, valor){
					BFwrk.Util.updateComboToTextField(PF + 'txtIdDivision', NS.cmbDivision.getId());					
				}				
			}
		}			
	});
	
	NS.cmbDivisa = new Ext.form.ComboBox({
		store: NS.storeLlenaComboDivisa,
		id: PF + 'cmbDivisa',
		name: PF + 'cmbDivisa',
		x: 70,
		y: 85,
		width: 185,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		tabIndex: 8,
		valueField: 'idDivisa',
		displayField: 'descripcion',
		autocomplete: true,
		emptyText: 'Seleccione una divisa',
		triggerAction: 'all',
		value: '',
		visible: false,
		listeners:{
			select:{
				fn: function(combo, valor){
					BFwrk.Util.updateComboToTextField(PF + 'txtIdDivisa', NS.cmbDivisa.getId());
				}
			}
		}		
	});
	
	NS.cmbTipoChequerasNuevo = new Ext.form.ComboBox ({
		store: NS.storeTipoChequerasNuevo,
		id: PF + 'cmbTipoChequerasNuevo',
		name: PF + 'cmbTipoChequerasNuevo',
		x: 360,
		y: 85,
		width: 150,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		tabIndex: 10,
		valueField: 'idString',
		displayField: 'descChequera',
		autocomplete: true,
		emptyText: 'Tipo Chequera',
		triggerAction: 'all',
		value: '',
		visible: false,
		listeners: {
			select: {
				fn: function(combo, valor) {
					BFwrk.Util.updateComboToTextField(PF + 'txtIdTipoChequerasNuevo', NS.cmbTipoChequerasNuevo.getId());
				}
			}
		}
	});	
	
	NS.cmbCajaCheques = new Ext.form.ComboBox({
		store: NS.storeCajas,
		id: PF + 'cmbCajasCheques',
		name: PF + 'cmbCajasCheques',
		x: 70,
		y: 115,
		width: 150,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		valueField: 'idCaja',
		displayField: 'descCaja',
		autocomplete: true,
		emptyText: 'Caja',
		triggerAction: 'all',
		value: '',
		visible: false,
		listeners:{
			select: {
				fn: function(combo, valor){
					BFwrk.Util.updateComboToTextField(PF + 'txtIdCajaCheques', NS.cmbCajaCheques.getId());
				}
			}
	}
	});
	
	//GRID DE CONSULTA
	//Columna de seleccion en el grid
	NS.columnaSeleccion = new Ext.grid.CheckboxSelectionModel ({
		singleSelect: false
	});

	//Columnas del grid
	NS.columnasGrid = new Ext.grid.ColumnModel ([
	  	//NS.columnaSeleccion,
	  	{header: 'Empresa', width: 70, dataIndex: 'noEmpresa', sortable: true},
	  	{header: 'Banco', width: 50, dataIndex: 'idBanco', sortable: true, hidden: true},
	  	{header: 'DescBanco', width: 120, dataIndex: 'descripcion', sortable: true},
	  	{header: 'Chequera', width: 90, dataIndex: 'idChequera', sortable : true},	  	
	  	{header: 'Plaza', width: 50, dataIndex: 'plaza', sortable: true},
	  	{header: 'Sucursal', width: 70, dataIndex: 'sucursal', sortable: true},	  	
	  	{header: 'Desc Chequera', width: 120, dataIndex: 'descChequera', sortable: true},
	  	{header: 'Saldo Inicial', width: 100, dataIndex: 'saldoInicial', sortable: true, css: 'text-align:right;', renderer: BFwrk.Util.rendererMoney},
	  	{header: 'Cargo', width: 90, dataIndex: 'cargo', sortable: true, css: 'text-align:right;', renderer: BFwrk.Util.rendererMoney},
	  	{header: 'Abono', width: 90, dataIndex: 'abono', sortable: true, css: 'text-align:right;', renderer: BFwrk.Util.rendererMoney},	  	
	  	{header: 'Saldo Final', width: 100, dataIndex: 'saldoFinal', sortable: true, css: 'text-align:right;', renderer: BFwrk.Util.rendererMoney},	  	
	  	{header: 'Ultimo Cheque', width: 90, dataIndex: 'ultimoCheque', sortable: true},
	  	{header: 'Abono SBC', width: 70, dataIndex: 'abonoSBC', sortable: true},
	  	{header: 'Nacionalidad', width: 90, dataIndex: 'nacionalidad', sortable: true},
	  	{header: 'Divisa', width: 50, dataIndex: 'idDivisa', sortable: true},
	  	{header: 'Desc. Divisa', width: 50, dataIndex: 'descDivisa', sortable: true, hidden: true},
	  	{header: 'Tipo Chequera', width: 100, dataIndex: 'tipoChequera', sortable: true},
	  	{header: 'Desc. Tipo Cheq', width: 50, dataIndex: 'descTipoChequera', sortable: true, hidden: true},
	  	{header: 'Saldo Minimo', width: 100, dataIndex: 'saldoMinimo', sortable: true, css: 'text-align:right;', renderer: BFwrk.Util.rendererMoney},
	  	{header: 'Saldo Bco Inicial', width: 120, dataIndex: 'saldoBcoInicial', sortable: true, css: 'text-align:right;', renderer: BFwrk.Util.rendererMoney},
	  	{header: 'Periodo Conciliacion', width: 120, dataIndex: 'periodoConciliacion', sortable: true},
	  	{header: 'Saldo Conta. Ini.', width: 120, dataIndex: 'saldoConta', sortable: true, css: 'text-align:right;', renderer: BFwrk.Util.rendererMoney},
	  	{header: 'Concentradora', width: 80, dataIndex: 'bConcentradora', sortable: true, hidden: true},
	  	{header: 'Aplica Traspasos', width: 50, dataIndex: 'bTraspaso', sortable: true, hidden: true},
	  	{header: 'Maneja Cheques', width: 100, dataIndex: 'bCheques', sortable: true},
	  	{header: 'Maneja Transferencias', width: 120, dataIndex: 'bTransferencias', sortable: true},
	  	{header: 'Aplica Cheque Ocurre', width: 120, dataIndex: 'chequeOcurre', sortable: true},
	  	{header: 'Cargo En Cuenta', width: 100, dataIndex: 'cargoEnCuenta', sortable: true},
	  	{header: 'Clabe', width: 120, dataIndex: 'clabe', sortable: true},
	  	{header: 'Impresion Continua', width: 120, dataIndex: 'impresionContinua', sortable: true},
	  	{header: 'ABA', width: 100, dataIndex: 'aba', sortable: true},
	  	{header: 'SWIFT', width: 100, dataIndex: 'swift', sortable: true},
	  	{header: 'Division', width: 50, dataIndex: 'IdDivision', sortable: true, hidden: true},
	  	{header: 'Desc Division', width: 50, dataIndex: 'descDivision', sortable: true, hidden: true},
	  	{header: 'Sobregiro', width: 50, dataIndex: 'sobregiro', sortable: true, hidden: true},
	  	{header: 'Mass Payment', width: 100, dataIndex: 'massPayment', sortable: true},
	  	{header: 'MapeoERP', width: 100, dataIndex: 'houseBank', sortable: true}		
	]);
	
	//Se agregan componentes (columnas) a grid
	NS.gridDatos = new Ext.grid.GridPanel ({
		store: NS.storeLlenaGrid,
		id: PF + 'gridDatos',
		name: PF + 'gridDatos',
		cm: NS.columnasGrid,
		sm: NS.columnaSeleccion,
		width: 960,
		height: 220,
		stripeRows: true,
		columnLines: true,
		listeners: {
			click: {
				fn: function(grid) {
					
				}
			}
		}
	});
	
	//GRID PARA LA ASIGNACION DE DIVISIONES POR CHEQUERA
	NS.columnaDivisionPorAsignar = new Ext.grid.CheckboxSelectionModel ({
		singleSelect: false
	});
	
	NS.columnasDivisionesPorAsignar = new Ext.grid.ColumnModel
	([
	  	{header: 'Id Division', width: 50, dataIndex: 'idDivision', sortable: true},
	  	{header: 'Desc. Division', width: 100, dataIndex: 'descDivision', sortable: true}
	]);
	
	NS.gridDivisionesPorAsignar = new Ext.grid.GridPanel({
		store: NS.storeDivisionesPorAsignar,
		id: PF + 'gridDivisionesPorAsignar',
		name: PF + 'gridDivisionesPorAsignar',
		cm: NS.columnasDivisionesPorAsignar,
		sm: NS.columnaDivisionPorAsignar,
		width: 400,
		height: 100,
		stripeRows: true,
		columnLines: true,
		listeners:{
			click: {
				fn: function(grid){}
			}
		}
	});
	                                     	 
	//Radio button
	//Radio button para seleccionar el tipo de reporte de las chequeras
	NS.optRadios = new Ext.form.RadioGroup ({
		id: PF + 'optGlobal',
		name: PF + 'optGlobal',
		x: 0,
		y: 0,
		columns: 1, //muestra los radiobuttons en dos columnas
		items: [
	          {boxLabel: 'Empresa Actual', name: 'optActual', inputValue: 0, checked: true},  
	          {boxLabel: 'Global', name: 'optActual', inputValue: 1}
	    ]
	});
	
	//Radio button para seleccionar el tipo de nacionalidad de los bancos
	NS.optTipoBancos = new Ext.form.RadioGroup({
		id: PF + 'optTipoBanco',
		name: PF + 'optTipoBanco',				
		columns: 2,
		items:[
		       {boxLabel: 'Nacional', name: 'optSeleccion', inputValue: 0, checked: true, 
		    	   listeners:{
              			check:{
          					fn:function(opt, valor)
          					{		    	   				
		          				if(valor==true)
		          				{
		          					Ext.getCmp(PF + 'txtIdBancoNuevo').setValue('');
		        					NS.cmbBancoNuevo.reset();
		        					NS.tipoBanco = 0;
		        					NS.storeBancosTodos;
		        					NS.storeBancosTodos.baseParams.tipoBanco = NS.tipoBanco;
		        					NS.storeBancosTodos.load();
		        					NS.panelAbaSwift.setVisible(true);
		          				}
          					}
          				}
		       	}  
          	},
		       {boxLabel: 'Extranjero', name: 'optSeleccion', inputValue: 1,
	          		listeners:{
		      			check:{
		  					fn:function(opt, valor)
		  					{		    	   				
		          				if(valor==true)
		          				{
		          					Ext.getCmp(PF + 'txtIdBancoNuevo').setValue('');
		        					NS.cmbBancoNuevo.reset();		        					
		        					NS.tipoBanco = 1;
		        					NS.storeBancosTodos;
		        					NS.storeBancosTodos.baseParams.tipoBanco = NS.tipoBanco;
		        					NS.storeBancosTodos.load();
		        					NS.panelAbaSwift.setVisible(true);
		          				}
		  					}
		  				}
	          		}  
		       }
		]		
	});	

	NS.optAbaSwift = new Ext.form.RadioGroup({
		id: PF + 'optAba',
		name: PF + 'opAba',
		x: 0,
		y: 0,
		columns: 1,
		tabIndex: 29,
		items:[
		       {boxLabel: 'ABA', name: 'optAba', inputValue: 0, checked: true},
		       {boxLabel: 'SWIFT', name: 'optAba', inputValue: 1}
		]
	});
		
	//PANEL
	NS.panelBusqueda = new Ext.form.FieldSet ({
		title: 'Busqueda',
		x: 0,
		y: 0,
		width: 985,
		height: 80,
		layout: 'absolute',
		items: [
		 	NS.lblEmpresa,
		 	NS.lblBanco,
		 	NS.txtIdEmpresa,
		 	NS.txtIdBanco,
		 	NS.cmbEmpresa,
		 	NS.cmbBanco,
		 	{
		 		xtype: 'button',
		 		text: 'Buscar',
		 		x: 870,
		 		y: 10,
		 		width: 80, 
		 		height: 22,
		 		listeners: {
		 			click: {
		 				fn: function(e) {
		 					NS.buscar();
		 				}
		 			}
		 		}		 		
		 	}
		]
	});
	
	//Panel que contiene los radio button del tipo de nacionalidad de los bancos
	NS.panelRadioButtonBancos = new Ext.form.FieldSet ({
		id: PF + 'panelBancos',
		title: 'Nacionalidad',		
		x: 10,
		y: 0,
		width: 170,
		height: 45,
		layout: 'absolute',
		items: [
		 	NS.optTipoBancos
		]
	});
	
	NS.panelCheckBoxTipoPagos = new Ext.form.FieldSet({
		title: 'Tipo De Pagos',
		x: 750,
		y: 160,
		width: 130,
		height: 130,
		layout: 'absolute',
		items: 
		[
		 	{
		 		xtype: 'checkbox',
		 		id: PF + 'chkCheques',
		 		name: PF + 'chkCheques',
		 		x: 0,
		 		y: 0,
		 		tabIndex: 31,
		 		boxLabel: 'Cheques',
		 		listeners:
		 		{
		 			check:
		 			{
		 				fn: function(checkBox, valor){
		 					//if (valor)
		 								 					
		 				}
		 			}
		 		}
		 	},
		 	{
		 		xtype: 'checkbox',
		 		id: PF + 'chkTransferencias',
		 		name: PF + 'chkTransferencias',
		 		x: 0,
		 		y: 25,
		 		tabIndex: 32,
		 		boxLabel: 'Transferencia',
		 		listeners:
		 		{
		 			check:
		 			{
		 				fn: function(checkBox, valor)
		 				{
		 					//if (valor)
		 					
		 				}
		 			}
		 		}
		 	},
		 	{
		 		xtype: 'checkbox',
		 		id: PF + 'chkChequeOcurre',
		 		name: PF + 'chkChequeOcurre',
		 		x: 0,
		 		y: 50,
		 		tabIndex: 33,
		 		boxLabel: 'Cheque Ocurre',
		 		listeners:
		 		{
		 			check:
		 			{
		 				fn: function(checkBox, valor)
		 				{
		 					//if (valor)
		 					
		 				}
		 			}
		 		}
		 	},
		 	{
		 		xtype: 'checkbox',
		 		id: PF + 'chkCargoEnCuenta',
		 		name: PF + 'chkCargoEnCuenta',
		 		x: 0,
		 		y: 75,
		 		tabIndex: 34,
		 		boxLabel: 'Cargo En Cuenta',
		 		listeners:
		 		{
		 			check:
		 			{
		 				fn: function(checkBox, valor)
		 				{
		 					//if (valor)
		 					
		 				}
		 			}
		 		}
		 	}
    	]
	});
	
	NS.panelAbaSwift = new Ext.form.FieldSet({
		title: 'Aba / Swift',
		x: 350,
		y: 280,
		width: 250,
		height: 70,
		layout: 'absolute',
		items:
		[
		 	NS.optAbaSwift,
		 	NS.txtAbaSwift
		]
	});
	
	NS.optImpCon = new Ext.form.RadioGroup ({
		id: PF + 'optImpCon',
		name: PF + 'optImpCon',
		x: 0,
		y: 0,
		width: 200 ,
		columns: 2, //muestra los radiobuttons en dos columnas
		horizontal: true,
		items: [
	          {boxLabel: 'Si', name: 'optIMP', inputValue: 'S', checked: true},  
	          {boxLabel: 'No', name: 'optIMP', inputValue: 'N'}
	    ]
	});
	
	NS.panelImpCon = new Ext.form.FieldSet({
		title: 'Impresion Continua',
		x: 530,
		y: 180,
		width: 170,
		height: 50,
		layout: 'absolute',
		items:
		[
		 	
		 	NS.optImpCon
		 	
//		 	NS.optAbaSwift,
//		 	NS.txtAbaSwift
		]
	}); 
	
	//PANEL PARA LA ASIGNACION DE LOS FOLIOS DE CHEQUES
	NS.panelLote = new Ext.form.FieldSet({
		id: PF + 'panelLote',
		name: PF + 'panelLote',
		title: 'Lote De Cheque',
		x: 10,
		y: 155,
		width: 200,
		height: 210,
		layout: 'absolute',
		items:
		[
		 	NS.lblLote,
		 	NS.lblFolioInicial,
		 	NS.lblFolioFinal,
		 	NS.lblUltimoChequeImp,
		 	NS.txtLoteCheque,
		 	NS.txtFolioInicial,
		 	NS.txtFolioFinal,
		 	NS.txtUltimoChequeImp
		]
	});
	
	NS.panelOrigenMov = new Ext.form.FieldSet({
		id: PF + 'panelOrigenMov',
		name: PF + 'panelOrigenMov',
		title: 'Origen De Movimientos',
		x: 10, 
		y: 380,
		width: 200,
		height: 40,
		layout: 'absolute',
		items:[		       
		]
	});
	
	NS.panelCheque = new Ext.form.FieldSet({
		id: PF + 'panelCheque',
		name: PF + 'panelCheque',
		title: 'Asignación de folios',
		x: 0,
		y: 0,
		width: 985,
		height: 465,		
		layout: 'absolute',
		hidden: true,
		items:[
		       NS.lblBancoCheques,
		       NS.lblChequeraCheques,
		       NS.lblCajaCheques,
		       NS.txtBancoCheques,
		       NS.txtChequeraCheques,
		       NS.txtIdCajaCheques,
		       NS.cmbCajaCheques,
		       NS.panelLote,
		       NS.panelOrigenMov,		       
		       {
		    	   xtype: 'button',
		    	   text: 'Crear Nuevo',
		    	   x: 550,
		    	   y: 400,
		    	   width: 80,
		    	   height: 22,
		    	   listeners: {
		    	   		click: {
		    	   			fn: function(e){
		    	   				
		       				}
		       			}
		       	   }    	   
		       },
		       {
		    	   xtype: 'button',
		    	   text: 'Aceptar',
		    	   x: 650, 
		    	   y: 400,
		    	   width: 80,
		    	   height: 22,
		    	   listeners:{
		    	   		click: {
		    	   			fn: function(e)
		    	   			{
		    	   				
		    	   			}
		       			}
		    	   }	       		
		       },
		       {
		    	   xtype: 'button',
		    	   text: 'Eliminar',
		    	   x: 750, 
		    	   y: 400,
		    	   width: 80, 
		    	   height: 22,
		    	   listeners:{
		    	   		click: {
		    	   			fn: function(e)
		    	   			{
		    	   
		    	   			}
		       			}
		       	   }
		       },
		       {
		    	   xtype: 'button',
		    	   text: 'Cerrar',
		    	   x: 850,
		    	   y: 400, 
		    	   width: 80,
		    	   height: 22,
		    	   listeners:
		    	   {
		    	   		click:
		    	   		{
		    	   			fn: function (e)
		    	   			{
		    	   				NS.cierraPanelCheques();
		    	   			}
		    	   		}
		    	   }
		       }
		]
	});
	
	//PANEL PARA ASIGNACION DE DIVISIONES
	NS.panelAsignacionDivisiones = new Ext.form.FieldSet({
		id: PF + 'panelCheque',
		name: PF + 'panelCheque',
		title: 'Asignación de folios',
		x: 0,
		y: 0,
		width: 985,
		height: 465,		
		layout: 'absolute',
		hidden: true,
		items: [
		        NS.lblEmpresaDivisiones,
		        NS.lblBancoDivisiones,
		        NS.lblChequeraDivisiones,
		        NS.lblDivisionesPorAsociar,
		        NS.lblDivisionesAsociadas,
		        NS.txtEmpresaDivisiones,
		        NS.txtBancoDivisiones,
		        NS.txtDescBancoDivisiones,
		        NS.txtChequeraDivisiones,
		        NS.gridDivisionesPorAsignar
		]	
	});
	
	NS.panelCrearNuevo = new Ext.form.FieldSet ({
		id: PF + 'panelCrearNuevo',
		name: PF + 'panelCrearNuevo',
		title: 'Creacion De Chequeras',
		x: 0,
		y: 0,
		width: 985,
		height: 465,
		layout: 'absolute',
		hidden: true,
		items: [
		 	NS.lblBancoNuevo,
		 	NS.lblChequeraNuevo,
		 	NS.lblDivisionNuevo,
		 	NS.lblDivisaNuevo,
		 	NS.lblTipoChequeraNuevo,
		 	NS.lblDescChequeraNuevo,
		 	NS.lblPlazaNuevo,
		 	NS.lblSucursalNuevo,
		 	NS.lblSaldoInicial,
		 	NS.lblSaldoMinimo,
		 	NS.lblSaldoInicialBanco,
		 	NS.lblUltimoChequeImpreso,
		 	NS.lblSaldoInicialContabilidad,
		 	NS.lblAbonoSBC,
		 	NS.lblPeriodoConciliacion,
		 	NS.lblMapeoERP,
		 	NS.lblClabe,
		 	NS.lblChequeraContrato,
		 	NS.lblSobregiro,		 	
		 	NS.txtIdBancoNuevo,
		 	NS.txtChequeraNueva,
		 	NS.txtIdDivision,
//		 	NS.txtBancoP,
//		 	NS.txtBancoI,
		 	//NS.txtIdDivisa,
		 	//NS.txtIdTipoChequerasNuevo,
		 	NS.txtDescChequera,
		 	NS.txtPlaza,
		 	NS.txtSucursal,
		 	//NS.optImpContinua,
		 	NS.txtSaldoInicial,
		 	NS.txtSaldoMinimo,
		 	NS.txtSaldoInicialBanco,
		 	NS.txtUltimoChequeImpreso,
		 	NS.txtSaldoInicialContabilidad,
		 	NS.txtAbonoSBC,
		 	NS.txtPeriodoConciliacion,
		 	NS.txtMapeoERP,
		 	NS.txtClabe,
		 	NS.txtChequeraContrato,
		 	NS.txtSobregiro,
		 	{
                xtype: 'uppertextfield',
                x: 10,
                y: 85,
                width: 50,
                tabIndex: 7,
                id: PF + 'txtIdDivisa',
                name: PF + 'txtIdDivisa',
                listeners:{
                	change:{
                		fn:function(caja){
            				if(caja.getValue() != null && caja.getValue() != undefined && caja.getValue() != '') {
                    			var valueCombo = BFwrk.Util.updateTextFieldToCombo(PF + 'txtIdDivisa', NS.cmbDivisa.getId());                    		                        	
            				}else
            					NS.cmbDivisa.reset();
                		}
                	}
                }	 	
            },
            {
            	xtype: 'uppertextfield',
                x: 300,
                y: 85,
                width: 50,
                tabIndex: 9,
                id: PF + 'txtIdTipoChequerasNuevo',
                name: PF + 'txtIdTipoChequerasNuevo',
                listeners:{
                	change:{
                		fn:function(caja){
            				if(caja.getValue() != null && caja.getValue() != undefined && caja.getValue() != '') {
                    			var valueCombo = BFwrk.Util.updateTextFieldToCombo(PF + 'txtIdTipoChequerasNuevo', NS.cmbTipoChequerasNuevo.getId());                    		                        	
            				}else
            					NS.cmbTipoChequerasNuevo.reset();
                		}
                	}
                }            	
            },
		 	NS.cmbBancoNuevo,
		 	NS.cmbDivision,
		 	NS.cmbDivisa,
		 	NS.cmbTipoChequerasNuevo,
		 	NS.panelRadioButtonBancos,		 	
		 	{
		 		xtype: 'checkbox',
		 		id: PF + 'chkChequeraTraspasos',
		 		name: PF + 'chkChequeraTraspasos',
		 		x: 550,
		 		y: 70,
		 		tabIndex: 11,
		 		boxLabel: 'Chequera para traspasos'
		 	},
		 	{
		 		xtype: 'checkbox',
		 		id: PF + 'chkDepositos',
		 		name: PF + 'chkDepositos',
		 		x: 550,
		 		y: 90,
		 		tabIndex: 12,
		 		hidden:true,
		 		boxLabel: 'Usar para depositos por omisión'
		 	},
		 	{
		 		xtype: 'checkbox',
		 		id: PF + 'chkMassPayment',
		 		name: PF + 'chkMassPayment',
		 		x: 750,
		 		y: 70,
		 		tabIndex: 13,
		 		hidden:true,
		 		boxLabel: 'Mass Payment'
		 	},
		 	NS.panelCheckBoxTipoPagos,
		 	NS.panelAbaSwift,	
		 	NS.panelImpCon,
		 	{
		 		xtype: 'button',
		 		id: PF + 'asociarDivisiones',
		 		name: PF + 'asociarDivisiones',
		 		text: 'Asociar Divisiones',
		 		hidden:true,
		 		x: 500, 
		 		y: 385,
		 		width: 80,
		 		height: 22,
		 		disabled: true,
		 		tabIndex: 35,
		 		listeners:
		 		{
		 			click: {
		 				fn: function(e)
		 				{
		 					NS.panelCrearNuevo.setVisible(false);
		 					NS.panelAsignacionDivisiones.setVisible(true);
		 				}
		 			}	 	
		 		}
		 
		 	},
		 	{
		 		xtype: 'button',
		 		text: 'Aceptar',
		 		x: 620, 
		 		y: 385,
		 		width: 80,
		 		height: 22,
		 		tabIndex: 36,
		 		listeners:{
		 			click:{
		 				fn: function(e){
		 					NS.insertaOModificaDatos();
		 				}		 				
		 			}
		 		}	 		
		 	}, 
		 	{
		 		xtype: 'button',
		 		text: 'Cancelar',
		 		x: 720,
		 		y: 385,
		 		width: 80,
		 		height: 22,
		 		tabIndex: 37,
		 		listeners:{
		 			click:{
		 				fn: function(e){
		 					NS.panelCrearNuevo.setVisible(false);
		 					NS.panelBusqueda.setVisible(true);
		 					NS.panelGrid.setVisible(true);
		 					NS.panelReporte.setVisible(true);
		 					
		 					if (NS.modificar)
		 						NS.buscar();
		 				}
		 			}
		 		}
		 	}/*,
		 	//Se deja pendiente este boton 
		 	{
		 		id: PF + '...',
		 		xtype: 'button',
		 		text: '...',
		 		x: 315,
		 		y: 235,
		 		width: 20,
		 		height: 22,		 		
		 		listeners:{
		 			click:{
		 				fn: function(e){		 					
		 					NS.validaDatosCheque();		 					
		 				}
		 			}
		 		}
		 	}*/
		]
	});
	
	NS.btnNuevo = apps.SET.btnFacultativo(new Ext.Button({
 		id: 'umchNuevo',
 		name: 'umchNuevo',
 		text: 'Crear Nuevo',
 		x: 600, 
 		y: 225,
 		width: 80,
 		height: 22,
 		listeners: {
 			click: {
 				fn: function(e) {
 					NS.modificar = false;
 					NS.activaPanelNuevo();	
 					NS.bancoM=0;
 				}
 			}
 		}
 	}));
	NS.btnModificar = apps.SET.btnFacultativo(new Ext.Button({
			id: 'umchModificar',
			name: 'umchModificar',
			text: 'Modificar SI',
			x: 700, 
			y: 225,
			width: 80,
			height: 22,
			listeners: {
				click: {
					fn: function(e) {
						NS.modificar = true;
						
						NS.noEmpresaCombo = Ext.getCmp(PF + 'txtIdEmpresa').getValue();			
 				
 						var registroSeleccionado = NS.gridDatos.getSelectionModel().getSelections();
 						NS.bancoM=registroSeleccionado[0].get("idBanco");
 						if (registroSeleccionado.length <= 0)
 							Ext.Msg.alert('SET', 'Debes de seleccionar un registro para modificar');
 						else //Abre el panel para modificar
 						{		 							
 							NS.activaPanelNuevo();		 							
 						}
 				}
				}
			}
	}));
	
	NS.btnEliminar = apps.SET.btnFacultativo(new Ext.Button({
 			id: 'umchEliminar',
 			name: 'umchEliminar',
 			text: 'Eliminar',
 			x: 800,
 			y: 225,
 			width: 80,
 			height: 22,
 			listeners: {
 				click: {
 					fn: function(e) {
 						NS.eliminar();
 					}
 				}
 			}	 		
 		}));
	
	NS.panelGrid = new Ext.form.FieldSet ({
		title: '',
		x: 0,
		y: 90,
		width: 985,
		height: 270,
		layout: 'absolute',
		items: [
		 	NS.gridDatos,		 	
		 	NS.btnNuevo,
		 	NS.btnModificar,
		 	NS.btnEliminar
		]
	});
	
	//PANEL GRID PARA LA ASIGNACION DE DIVISIONES POR CHEQUERA
	/*
	 * NS.panelGrid = new Ext.form.FieldSet ({
		title: '',
		x: 0,
		y: 90,
		width: 985,
		height: 270,
		layout: 'absolute',
		items: [
		 	NS.gridDatos
	 *//*
	NS.panelGridDivisiones = new Ext.form.FieldSet({
		title: '',
		x: 15,
		y: 100,
		width: 400,
		height: 200,
		layout:
	});
	*/
	NS.panelRadioButton = new Ext.form.FieldSet ({
		title: '',
		x: 160,
		y: 0,
		width: 140,
		height: 65,
		layout: 'absolute',
		items: [
		 	NS.optRadios
		]
	});
	
	
	
	NS.panelReporte = new Ext.form.FieldSet ({
		title: 'Reporte',
		x: 0,
		y: 365,
		width: 600,
		height: 100,
		layout: 'absolute',		
		items: [
		 	NS.lblTipoChequera,
//		 	NS.lblGrupo,
		 	NS.cmbTipoChequeras,
//		 	NS.cmbGrupo,
		 	NS.panelRadioButton,
		 	{
		 		xtype: 'button',
		 		text: 'Imprimir',
		 		x: 320,
		 		y: 15,
		 		width: 80,
		 		height: 22,
		 		hidden: false,
		 		listeners: {
		 			click: {
		 				fn: function(e) {
		 					if (NS.cmbTipoChequeras.getValue() != '') {
								if ((NS.optRadios.getValue().inputValue == 0 && NS.txtIdEmpresa.getValue() != '') || 
										NS.optRadios.getValue().inputValue == 1) {
									
									parametros='?nomReporte=excelChequeras';
		 							parametros+='&nomParam1=tipoChequera';
		 							parametros+='&valParam1=' + NS.cmbTipoChequeras.getValue();
		 							parametros+='&nomParam2=empresa';
		 							parametros+='&valParam2=' + (NS.optRadios.getValue().inputValue == 0 ? NS.txtIdEmpresa.getValue() : 0);
		 							window.open("/SET/jsp/bfrmwrk/gnrators/abreExcel.jsp"+parametros);
								} else {
									Ext.Msg.alert('SET', 'Seleccione una empresa.');
								}
							} else {
								Ext.Msg.alert('SET', 'Seleccione el Tipo de chequera.');
							}
		 					
		 				}
		 			}
		 		}		 		
		 	}	
		 	
		]
	});
	
	
	NS.global = new Ext.form.FieldSet ({
		title: '',
		x: 20,
		y: 5,
		width: 1010,		
		height: 490,
		layout: 'absolute',
		items:
		[
		 	NS.panelBusqueda,
		 	NS.panelGrid,
		 	//NS.panelModificar,
		 	NS.panelReporte,		 	
		 	NS.panelCrearNuevo,
		 	NS.panelCheque,
		 	NS.panelAsignacionDivisiones,
		 	{
		 		xtype: 'button',
		 		text: 'Limpiar',
		 		id: PF + 'btnLimpiar',
		 		x: 830,
		 		y: 408,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				fn: function(e) {
		 					NS.limpiar();		 
		 				}
		 			}
		 		}		 		
		 	}		 			 	
	    ]
	});
	
	NS.chequeras = new Ext.FormPanel ({
		title: 'Mantenimiento De Chequeras',
		width: 1300,
		height: 706,
		frame: true,
		padding: 10,
		autoScroll: true,
		layout: 'absolute',
		id: PF + 'mantenimientoChequeras',
		name: PF + 'mantenimientoChequeras',
		renderTo: NS.tabContId,
		items: [
		 	NS.global
		]
	});	
	
	//FACULTADES
	NS.facultadModificaChequeras = function(indice){
		MantenimientoChequerasAction.facultadDeModificarChequera(NS.idUsuario, indice, function(resultado, e) {
			if (resultado !== null && resultado !== undefined && resultado !== '' && resultado == "true") {				
				NS.facultadModificar = true;
				Ext.getCmp(PF + 'btnNuevo').setDisabled(false);
				Ext.getCmp(PF + 'btnModificar').setDisabled(false);
				Ext.getCmp(PF + 'btnEliminar').setDisabled(false);
				
				if (indice == '136'){
					//Ext.getCmp(PF + 'txtSaldoInicial').setDisabled(false);
		 			//Ext.getCmp(PF + 'txtSaldoInicialContabilidad').setDisabled(false);
		 		//	Ext.getCmp(PF + 'txtSaldoInicialBanco').setDisabled(false);
		 			Ext.getCmp(PF + 'txtSaldoMinimo').setDisabled(false);
		 			Ext.getCmp(PF + 'txtUltimoChequeImpreso').setDisabled(false);
		 			Ext.getCmp(PF + 'txtAbonoSBC').setDisabled(false);
				}
			}
			else{
////				Ext.getCmp(PF + 'btnNuevo').setDisabled(true);
//				Ext.getCmp(PF + 'btnModificar').setDisabled(true);
//				Ext.getCmp(PF + 'btnEliminar').setDisabled(true);
				
				if (indice == '136'){
				//	Ext.getCmp(PF + 'txtSaldoInicial').setDisabled(true);
		 		//	Ext.getCmp(PF + 'txtSaldoInicialContabilidad').setDisabled(true);
		 		//	Ext.getCmp(PF + 'txtSaldoInicialBanco').setDisabled(true);
		 			Ext.getCmp(PF + 'txtSaldoMinimo').setDisabled(true);
		 			Ext.getCmp(PF + 'txtUltimoChequeImpreso').setDisabled(true);
		 			Ext.getCmp(PF + 'txtAbonoSBC').setDisabled(true);
				}
			}			
			
		});
	};		
	
	//NS.facultadModificaChequeras('136, 137');
	

	NS.chequeras.setSize(Ext.get(NS.tabContId).getWidth(), Ext.get(NS.tabContId).getHeight());	
});
 