Ext.onReady(function (){
	Ext.Direct.addProvider(Ext.setapp.REMOTING_API);	
	Ext.QuickTips.init(); 
	var NS = Ext.namespace('apps.SET.BarridosFondeos.FondeoAutomatico');
	var PF = apps.SET.tabID+'.';
	NS.idUsuario = apps.SET.iUserId;
	NS.tabContId = apps.SET.tabContainerId;
	
	//variables
	NS.idEmpresa = 0;
	NS.idDivisa = '';
	NS.idBanco = 0;
	NS.idChequera = '';
	NS.chkMismoBanco = false;
	NS.idArbolConcen = 0;
	NS.visibleCmbChequera = true;
	NS.visibleCmbBancoRaiz = false;
	NS.tipoBusqueda = 'D';
	NS.visibleMontoMinFondeo = false;//*
	NS.montoMinFondeo = '0';//*Las varibales marcadas con (*) se deben contener el valor 
							//de un objeto pintado en la pantalla cuando se requiera
						
	NS.nomBancoGrid = '';
	NS.nomEmpresaGrid = '';
	NS.noCuentaChequesGrid = '';
	NS.idBancoGrid = 0;
	NS.tipoOpArbol = '';
	
	NS.idTipoSaldo=0;
	
	var mascara = new Ext.LoadMask(Ext.getBody(), {msg:"Cargando..."});
	
	//Para evitar el desface de los grids
	Ext.override(Ext.grid.GridView, {
	    getColumnWidth : function(col){
	        var w = this.cm.getColumnWidth(col);
	        if(typeof w == 'number'){
	            return (Ext.isBorderBox || Ext.isSafari3 ? w : (w-this.borderWidth > 0 ? w-this.borderWidth:0)) + 'px';
	        }
	        return w;
	    }
	});
	
   	NS.sm = new Ext.grid.CheckboxSelectionModel({
		forceFit: false,
		fixed:true					
	});
	
 	NS.sm2 = new Ext.grid.CheckboxSelectionModel({
		forceFit: false,
		fixed:true					
	});
	
 	NS.sm3 = new Ext.grid.CheckboxSelectionModel({
		forceFit: false,
		fixed:true					
	});
	
	NS.sm4 = new Ext.grid.CheckboxSelectionModel({
		forceFit: false,
		fixed:true					
	});
	
	NS.sm5 = new Ext.grid.CheckboxSelectionModel({
		forceFit: false,
		fixed:true					
	});
	
 
	NS.limpiar = function(){
		Ext.getCmp(PF+'cmbArbol').setVisible(true);
		Ext.getCmp(PF+'cmbArbol').reset();
		Ext.getCmp(PF+'lblArbol').setVisible(true);
		Ext.getCmp(PF+'txtIdArbol').setVisible(true);
		Ext.getCmp(PF+'cmbEmpresasConcentradoras').setVisible(false);
		Ext.getCmp(PF+'cmbEmpresasConcentradoras').reset();
		Ext.getCmp(PF+'txtIdEmpresa').setVisible(false);
	 	Ext.getCmp(PF+'lblEmpresa').setVisible(false);
		Ext.getCmp(PF+'cmbChequera').setVisible(true);
		Ext.getCmp(PF+'cmbChequera').reset();
	 	Ext.getCmp(PF+'lblChequera').setVisible(true);
	 	NS.visibleCmbChequera = true;
	 	Ext.getCmp(PF+'cmbBanco').reset();
	 	Ext.getCmp(PF+'cmbDivisa').reset();
	 	
	 	Ext.getCmp(PF+'cmbBanco').setVisible(true);
		Ext.getCmp(PF+'cmbBancoRaiz').setVisible(false);
		NS.visibleCmbBancoRaiz = false;
		NS.tipoBusqueda = 'D';
		
		NS.gridFondeos.store.removeAll();
		NS.gridFondeos.getView().refresh();
									
	 	NS.FondeoAutomatico.getForm().reset();
	 	Ext.getCmp(PF+'btnImprimir').setVisible(false);
	
	}
	NS.limpiarWinPagPend = function(){
		Ext.getCmp(PF+'txtTotalRegistrosPP').setValue('0');
		Ext.getCmp(PF+'txtImporteTotalPP').setValue('0.0');
		Ext.getCmp(PF+'txtTotalRegistrosDP').setValue('0');
		Ext.getCmp(PF+'txtImporteTotalDP').setValue('0.0');
		//winPagosPendientes.restore();
		NS.gridPagosP.store.removeAll();
		NS.gridPagosP.getView().refresh();
		NS.gridPagosPDetalle.store.removeAll();
		NS.gridPagosPDetalle.getView().refresh();
	}
	
	NS.limpiarWinPagosCheques = function(){
		NS.gridFonVencHoy.store.removeAll();
		NS.gridFonVencHoy.getView().refresh();
		NS.gridFonVencAnt.store.removeAll();
		NS.gridFonVencAnt.getView().refresh();
		//winFondeoCheques.restore();
		Ext.getCmp(PF +'lblPagFonHoy').setText('0');
        Ext.getCmp(PF + 'lblFonPenHoy').setText('0');
        Ext.getCmp(PF+'lblMontosFondear').setText('0.0');
        Ext.getCmp(PF+'lblMontosDia').setText('0.0');
        Ext.getCmp(PF+'lblPagFonAnt').setText('0');
        Ext.getCmp(PF+'lblFonPenAnt').setText('0');
        Ext.getCmp(PF+'lblMontosAnteriores').setText('0.0');
        Ext.getCmp(PF+'lblMontosFondearAnt').setText('0.0');
		Ext.getCmp(PF+'txtNomEmpresaFC').setValue('');
		Ext.getCmp(PF+'txtCtaChequesFC').setValue('');
	}
	
	NS.cambiarFormato = function(num,prefix){
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
	
//store tipos_saldo
	
	NS.storeTipoSaldo = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{
		},
		root: '',
		paramOrder:[],
		directFn: CoinversionAction.llenarTipoSaldo, 
		idProperty: 'id', 
		fields: [
			 {name: 'id'},
			 {name: 'descripcion'}
		],
		listeners: {
			load: function(s, records){
				if(records.length==null || records.length<=0)
				{
					BFwrk.Util.msgShow('No hay tipos de saldos disponibles', 'INFO');
				//	Ext.getCmp(PF+'txtBanco').setValue('');
					//Ext.getCmp(PF+'cmbBanco').setValue('');
					return;
				}
				
				//console.log("tipo de saldo "+records[0].get('descripcion'));
			}
		}
	}); 
	//NS.storeTipoSaldo.load();
	NS.cmbTipoSaldo = new Ext.form.ComboBox({
		store: NS.storeTipoSaldo
		,name: PF+'cmbTipoSaldo'
		,id: PF+'cmbTipoSaldo'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
		,x: 140
        , y: 100
        ,width: 212
		,valueField:'id'
		,displayField:'descripcion'
		,autocomplete: true
		,emptyText: 'Tipo de Saldo'
		,triggerAction: 'all'
		,value: ''
		,listeners:{
			select:{
				fn: 
				function(combo, valor) {
					var valor=BFwrk.Util.updateComboToTextField( PF+'cmbTipoSaldo',NS.cmbTipoSaldo.getId());
					//NS.accionarcmbTipoSaldo(combo.getValue());
					NS.idTipoSaldo=combo.getValue();
					console.log("id tipo saldo "+combo.getValue());
				}
			}
		}
	});
	
	//store del combo cmbEmpresasConcentradoras
	NS.storeEmpresasConcentradoras = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{
			bMantenimiento : false
		},
		root: '',
		paramOrder:['bMantenimiento'],
		root: '',
		directFn: GlobalAction.llenarComboEmpresasConcentradoras, 
		idProperty: 'id', 
		fields: [
			 {name: 'id'},
			 {name: 'descripcion'},
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeEmpresasConcentradoras, msg:"Cargando..."});
				if(records.length == null || records.length<=0)
				{
				//	Ext.Msg.alert('SET','No Existen empresas asignadas al usuario: '+NS.idUsuario);
					return;
				}
			}
		}
	}); 
	var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeEmpresasConcentradoras, msg:"Cargando..."});
	NS.storeEmpresasConcentradoras.load();
	
	NS.accionarEmpresasConcentradoras = function(valueCombo){
		NS.idEmpresa = valueCombo;
	}
	//combo cmbEmpresasConcentradoras
	NS.cmbEmpresasConcentradoras = new Ext.form.ComboBox({
		store: NS.storeEmpresasConcentradoras
		,name: PF+'cmbEmpresasConcentradoras'
		,id: PF+'cmbEmpresasConcentradoras'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
        ,x: 140
	    ,y: 0
	    ,width: 250
		,valueField:'id'
		,displayField:'descripcion'
		,autocomplete: true
		,emptyText: 'Seleccione una empresa concentradora'
		,triggerAction: 'all'
		,value: ''
		,hidden: true
		,listeners:{
			select:{
				fn:function(combo, valor) {
				 	BFwrk.Util.updateComboToTextField(PF+'txtIdEmpresa',NS.cmbEmpresasConcentradoras.getId());
				 	NS.accionarEmpresasConcentradoras(combo.getValue());

				}
			}
		}
	});
	
	//store del combo cmbDivisa
	NS.storeDivisa = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{
			condicion : 'id_divisa_soin is not null'
		},
		root: '',
		paramOrder:['condicion'],
		root: '',
		directFn: GlobalAction.llenarComboDivisas, 
		idProperty: 'idDivisa', 
		fields: [
			 {name: 'idDivisa'},
			 {name: 'descDivisa'},
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeDivisa, msg:"Cargando..."});
				if(records.length == null || records.length<=0)
				{
					Ext.Msg.alert('SET','No Existen divisas');
					return;
				}
			}
		}
	}); 
	var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeDivisa, msg:"Cargando..."});
	NS.storeDivisa.load();
	
	//store del combo cmbBancoRaiz
	NS.storeBancoRaiz = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{
			idRaiz: 0,
			idDivisa:''
		},
		root: '',
		paramOrder:['idRaiz','idDivisa'],
		directFn: CoinversionAction.llenarComboBancosRaiz, 
		idProperty: 'id', 
		fields: [
			 {name: 'id'},
			 {name: 'descripcion'}
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeBancoRaiz, msg:"Cargando..."});
				if(records.length == null || records.length<=0)
				{
					Ext.Msg.alert('SET','No existen bancos del árbol');
					return;
				}
			}
		}
	}); 
	
	NS.accionarArbol = function(valueCombo){
		NS.idArbolConcen = valueCombo;
		NS.idRaiz = valueCombo;
		NS.storeBancoRaiz.baseParams.idRaiz = parseInt(valueCombo);
	}
	
	NS.accionarDivisa = function(valueCombo){
		NS.idDivisa = valueCombo;
		if(NS.idEmpresa > 0)
			NS.storeBanco.baseParams.bConcentradora = true;
		else
			NS.storeBanco.baseParams.bConcentradora = false;
			
		if(NS.visibleCmbBancoRaiz)
		{
			NS.storeBancoRaiz.baseParams.idDivisa = ''+NS.idDivisa;
			var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeBancoRaiz, msg:"Cargando..."});
			NS.storeBancoRaiz.load();
			Ext.getCmp(PF + 'cmbBancoRaiz').setVisible(true);
			Ext.getCmp(PF + 'cmbBanco').setVisible(false);
		}
		else
		{
			NS.storeBanco.baseParams.iIdEmpresa = NS.idEmpresa;
			NS.storeBanco.baseParams.iIdDivisa = ''+NS.idDivisa;
			var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeBanco, msg:"Cargando..."});
			NS.storeBanco.load();
			
			
		}
	}
	
	//combo cmbDivisa
	NS.cmbDivisa = new Ext.form.ComboBox({
		store: NS.storeDivisa
		,name: PF+'cmbDivisa'
		,id: PF+'cmbDivisa'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
		,x: 520
	    ,y: 20
	    ,width: 180
		,valueField:'idDivisa'
		,displayField:'descDivisa'
		,autocomplete: true
		,emptyText: 'Seleccione una divisa'
		,triggerAction: 'all'
		,value: ''
		,visible: false
		,listeners:{
			select:{
				fn:function(combo, valor) {
				 	BFwrk.Util.updateComboToTextField(PF+'txtIdDivisa',NS.cmbDivisa.getId());
				 	NS.accionarDivisa(combo.getValue());
				 	Ext.getCmp(PF+'')
				}
			}
		}
	});
	
	//store del combo cmbArbol
	NS.storeArbol = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{
			bExistentes : true
		},
		root: '',
		paramOrder:['bExistentes'],
		directFn: CoinversionAction.llenarComboEmpresaRaiz, 
		idProperty: 'campoUno', 
		fields: [
			 {name: 'id'},
			 {name: 'descripcion'},
			 {name: 'campoUno'}
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeArbol, msg:"Cargando..."});
				if(records.length == null || records.length<=0)
				{
					Ext.Msg.alert('SET','No existen empresas raíz');
					return;
				}
			}
		}
	}); 
	var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeArbol, msg:"Cargando..."});
	NS.storeArbol.load();

	//combo cmbArbol
	NS.cmbArbol = new Ext.form.ComboBox({
		store: NS.storeArbol
		,name: PF+'cmbArbol'
		,id: PF+'cmbArbol'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
		,x: 140
        ,y: 20
        ,width: 210
		,valueField:'campoUno'
		,displayField:'descripcion'
		,autocomplete: true
		,emptyText: 'Seleccione Árbol'
		,triggerAction: 'all'
		,value: ''
		,visible: false
		,listeners:{
			select:{
				fn:function(combo, valor) {
					var id = combo.getValue();
				 	//BFwrk.Util.updateComboToTextField(PF+'txtIdArbol',NS.storeArbol.getById(id).get('id'));
				 	Ext.getCmp(PF+'txtIdArbol').setValue(NS.storeArbol.getById(id).get('id'));
				 	NS.accionarArbol(combo.getValue());
				 	Ext.getCmp(PF+'cmbEmpresasConcentradoras').setVisible(false);
				 	Ext.getCmp(PF+'txtIdEmpresa').setVisible(false);
				 	Ext.getCmp(PF+'lblEmpresa').setVisible(false);
				// 	Ext.getCmp(PF+'lblChequera').setVisible(false);
				// 	Ext.getCmp(PF+'cmbChequera').setVisible(false);
				 	Ext.getCmp(PF+'cmbBanco').setVisible(false);
				 	Ext.getCmp(PF+'cmbBancoRaiz').setVisible(true);
				 	NS.visibleCmbBancoRaiz = true;
				 //	NS.visibleCmbChequera = false;
				}
			}
		}
	});
	
	
	//store del combo cmbBanco
	NS.storeBanco = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{
			bConcentradora : false,
			iIdEmpresa: NS.idEmpresa,
			iIdDivisa: NS.idDivisa
		},
		root: '',
		paramOrder:['bConcentradora','iIdEmpresa','iIdDivisa'],
		root: '',
		directFn: CoinversionAction.llenarComboBancoConcentradora, 
		idProperty: 'id', 
		fields: [
			 {name: 'id'},
			 {name: 'descripcion'},
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeBanco, msg:"Cargando..."});
				if(records.length == null || records.length<=0)
				{
					Ext.Msg.alert('SET','No existen bancos');
					return;
				}
			}
		}
	}); 
	
	
	NS.accionarBanco = function(valueCombo){
		NS.idBanco = valueCombo;
		if(NS.idEmpresa > 0 && NS.idDivisa != '' && NS.idBanco > 0)
		{
			NS.storeChequera.baseParams.iIdEmpresa = NS.idEmpresa;
			NS.storeChequera.baseParams.sIdDivisa = NS.idDivisa;
			NS.storeChequera.baseParams.iIdBanco = NS.idBanco;
			NS.storeChequera.load();
		}
	}
	
	//combo cmbBanco
	NS.cmbBanco = new Ext.form.ComboBox({
		store: NS.storeBanco
		,name: PF+'cmbBanco'
		,id: PF+'cmbBanco'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
        ,x: 140
	    ,y: 60
	    ,width: 140
		,valueField:'id'
		,displayField:'descripcion'
		,autocomplete: true
		,emptyText: 'Selecciona un banco'
		,triggerAction: 'all'
		,value: ''
		,disabled: true
		,listeners:{
			select:{
				fn:function(combo, valor) {
				 	BFwrk.Util.updateComboToTextField(PF+'txtIdBanco',NS.cmbBanco.getId());
				 	NS.accionarBanco(combo.getValue());
				}
			}
		}
	});
	
	
	NS.accionarBancoRaiz= function(valueCombo){
		NS.idBanco = valueCombo;
		if(NS.idArbolConcen > 0 && NS.idDivisa != '' && NS.idBanco > 0)
		{
			NS.storeChequera.baseParams.iIdEmpresa = parseInt(NS.idArbolConcen);
			NS.storeChequera.baseParams.sIdDivisa = ''+NS.idDivisa;
			NS.storeChequera.baseParams.iIdBanco = parseInt(NS.idBanco);
			var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeChequera, msg:"Cargando..."});
			NS.storeChequera.load();
		}
		
	}
	
	//combo cmbBancoRaiz
	NS.cmbBancoRaiz = new Ext.form.ComboBox({
		store: NS.storeBancoRaiz
		,name: PF+'cmbBancoRaiz'
		,id: PF+'cmbBancoRaiz'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
        ,x: 140
	    ,y: 60
	    ,width: 160
		,valueField:'id'
		,displayField:'descripcion'
		,autocomplete: true
		,emptyText: 'Selecciona un banco'
		,triggerAction: 'all'
		,value: ''
		,visible: false
		,listeners:{
			select:{
				fn:function(combo, valor) {
				 	BFwrk.Util.updateComboToTextField(PF+'txtIdBanco',NS.cmbBancoRaiz.getId());
				 	NS.accionarBancoRaiz(combo.getValue());
				}
			}
		}
	});
	
	
		//store del combo cmbChequera
	NS.storeChequera = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{
			iIdEmpresa : NS.idEmpresa,
			sIdDivisa: NS.idDivisa,
			iIdBanco: NS.idBanco
		},
		root: '',
		paramOrder:['iIdEmpresa','sIdDivisa','iIdBanco'],
		root: '',
		directFn: CoinversionAction.consultarChequeraFondeo, 
		idProperty: 'descripcion', 
		fields: [
			 {name: 'descripcion'},
			 {name: 'descripcion'},
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeChequera, msg:"Cargando..."});
				if(records.length == null || records.length<=0)
				{
					Ext.Msg.alert('SET','No existen chequeras');
					return;
				}
			}
		}
	}); 
	
	
	NS.accionarChequera = function(valueCombo){
		 
	}
	
	//combo cmbChequera
	NS.cmbChequera = new Ext.form.ComboBox({
		store: NS.storeChequera
		,name: PF+'cmbChequera'
		,id: PF+'cmbChequera'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
  		,x: 520
	    ,y: 60
	    ,width: 180
		,valueField:'descripcion'
		,displayField:'descripcion'
		,autocomplete: true
		,emptyText: 'Selecciona una Chequera'
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
	
	
	NS.storeGridFondeos = new Ext.data.DirectStore({
  	 	paramsAsHash: false,
  	 	baseParams: {
			idEmpresa:0,
			idEmpresaRaiz:0,
			idBanco:0,
			idDivisa:'',
	        idChequera:'',
	        chkMismoBanco: false,
	        tipoBusqueda:'',
	        idUsuario: NS.idUsuario,	
		},
		root: '',
		paramOrder:['idEmpresa','idEmpresaRaiz','idBanco','idDivisa','idChequera','chkMismoBanco','tipoBusqueda','idUsuario'],
		directFn: BarridosFondeosAction.consultarFondeoAutomatico,
		//idProperty: 'marca',
		fields: [
			{name: 'noEmpresaOrigen'},
			{name: 'nomEmpresaOrigen'},
			{name: 'noEmpresaDestino'},
			{name: 'nomEmpresaDestino'},
			{name: 'orden'},
			{name: 'pm'},
			{name: 'importeF'},
			{name: 'importeB'},
			{name: 'descBanco '},		
			{name: 'idChequera'},
			{name: 'ingresos'},
			{name: 'saldoChequera'},
			{name: 'saldoMinimoChequera'},
			{name: 'descBanco'},
			{name: 'idBanco'},
			{name: 'idDivisa'},
			{name: 'concepto'},
			{name: 'importeBarrido'},
			{name: 'importeTraspaso'},
			{name: 'idChequeraPadre'},
			{name: 'saldoCoinversion'},
			{name: 'idCaja'},
			{name: 'mensaje'}
		],
		listeners: {
			load: function(s, records){
			//	var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeGridFondeos, msg:"Buscando..."});
				/*for(var i=0, j=0;i<records.length;i++, j++){
					if(parseInt(records[i].get('pm'))==0){
						NS.storeGridFondeos.data.removeAt(j);
						NS.gridFondeos.getView().refresh();
						j--;
					}
				}*/
				
			    mascara.hide();
				if(NS.storeGridFondeos.data.items==null || NS.storeGridFondeos.data.items<=0)
				{
					Ext.Msg.alert('Información  SET','No existen datos con los parametros de búsqueda');
				}
				if (records[0].get('mensaje') != null && records[0].get('mensaje') != '')
					Ext,Msg.alert('Información SET',records[0].get('Mensaje'));
			}
		}
	}); 
	
	NS.gridFondeos = new Ext.grid.EditorGridPanel({
        store : NS.storeGridFondeos,
        autoScroll:true,
        id:PF+'gridFondeos',
        cm: new Ext.grid.ColumnModel({
            defaults: {
                width: 120,
                value:true,
                sortable: true
            },
            columns: [
                NS.sm,
                {
					header :'No',
					width :80,
					sortable :true,
					dataIndex :'noEmpresaDestino',
					hidden: false
				},{
					
					header :'Empresa',
					width :90,
					sortable :true,
					dataIndex :'nomEmpresaDestino'
				},{
					
					header :'Pagos Pendientes',
					width :90,
					sortable :true,
					dataIndex :'pm',
					renderer: BFwrk.Util.rendererMoney,
					listeners:{
						click:{
						fn: function(e){
							var regSelec = NS.gridFondeos.getSelectionModel().getSelections();
							for(var k=0;k<regSelec.length;k=k+1)
		          			{
		          				 
		 						NS.nomBancoGrid = regSelec[k].get('descBanco');
		 						NS.nomEmpresaGrid = regSelec[k].get('nomEmpresaDestino');
		 						NS.noCuentaChequesGrid = regSelec[k].get('idChequera');
		 						
		 						NS.storeGridPagosP.baseParams.iIdEmpresa = parseInt(regSelec[k].get('noEmpresaDestino'));
								NS.storeGridPagosP.baseParams.iIdBanco = parseInt(regSelec[k].get('idBanco'));
				       			NS.storeGridPagosP.baseParams.sIdChequera = ''+regSelec[k].get('idChequera');
				       			
				       			//Parametros para el store detalle de pagos pendientes
				       			NS.storeGridPagosPDetalle.baseParams.iIdEmpresa = parseInt(regSelec[k].get('noEmpresaDestino'));
				       			NS.storeGridPagosPDetalle.baseParams.iIdBanco = parseInt(regSelec[k].get('idBanco'));
				       			NS.storeGridPagosPDetalle.baseParams.sIdChequera = ''+regSelec[k].get('idChequera');
		          			}
		          			
		          			NS.storeGridPagosP.baseParams.iIdEmpresaRaiz = 1;
		          			NS.storeGridPagosP.baseParams.sIdDivisa = ''+Ext.getCmp(PF+'cmbDivisa').getValue();
		          			NS.storeGridPagosP.baseParams.sTipoBusqueda = ''+NS.tipoBusqueda;
		          			var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeGridPagosP, msg:"Buscando..."});
		          			NS.storeGridPagosP.load();
		          			
		          			//Parametros para el store detalle de pagos pendientes
		          			NS.storeGridPagosPDetalle.baseParams.iIdEmpresaRaiz = 1;
		          			NS.storeGridPagosPDetalle.baseParams.sIdDivisa = ''+Ext.getCmp(PF+'cmbDivisa').getValue();
		          			NS.storeGridPagosPDetalle.baseParams.sTipoBusqueda = ''+NS.tipoBusqueda;
		          			//el store se carga en el click del grid padre
		          			
			        		Ext.getCmp(PF+'txtNomBanco').setValue(NS.nomBancoGrid); 
		           			Ext.getCmp(PF+'txtNomEmpresa').setValue(NS.nomEmpresaGrid);
		           			Ext.getCmp(PF+'txtCtaCheques').setValue(NS.noCuentaChequesGrid);
		           			winFondeoCheques.hide();
		           			winPagosPendientes.show();
							}
						}
					}
				},{
					header :'Cheques Pendientes',
					width :80,
					sortable :true,
					dataIndex :'pmCheques',
					renderer: BFwrk.Util.rendererMoney,
					listeners:{
						click:{
						fn: function(e){
							var regSelec = NS.gridFondeos.getSelectionModel().getSelections();
							for(var k=0;k<regSelec.length;k=k+1)
		          			{
		 						NS.nomBancoGrid = ''+regSelec[k].get('descBanco');
								NS.idBancoGrid = parseInt(regSelec[k].get('idBanco'));
		 						NS.nomEmpresaGrid = ''+regSelec[k].get('nomEmpresaDestino');
		 						NS.noCuentaChequesGrid = ''+regSelec[k].get('idChequera');
		 						
		 						NS.storeFonVencHoy.baseParams.sIdChequera = ''+regSelec[k].get('idChequera');
								NS.storeFonVencHoy.baseParams.iIdBanco = parseInt(regSelec[k].get('idBanco'));
								
								NS.storeFonVencAnt.baseParams.sIdChequera = ''+regSelec[k].get('idChequera');
								NS.storeFonVencAnt.baseParams.iIdBanco = parseInt(regSelec[k].get('idBanco'));
		          			}
		          			NS.storeFonVencHoy.load();
		          			NS.storeFonVencAnt.load();
		          			
		           			Ext.getCmp(PF+'txtNomEmpresaFC').setValue(NS.nomEmpresaGrid);
		           			Ext.getCmp(PF+'txtCtaChequesFC').setValue(NS.noCuentaChequesGrid);
		           			winPagosPendientes.hide();
							winFondeoCheques.show();
							}
						}
					}
				},{
					header :'Importe a Fondear',
					width :140,
					sortable :true,
					editor:{xtype: 'numberfield',
						selectOnFocus:true,
						listeners:{
							change:{
								fn:function(e){
									var importeTraspaso = 0;
									var regSelec = NS.gridFondeos.getSelectionModel().getSelections();
									
									for(var k=0;k<regSelec.length;k=k+1){
										importeTraspaso = importeTraspaso + regSelec[k].get('importeTraspaso');
									}
									
									Ext.getCmp(PF+'txtTotalRegistros').setValue(regSelec.length);
									Ext.getCmp(PF+'txtTotalTraspaso').setValue(BFwrk.Util.formatNumber(importeTraspaso));
								}
							}
						}
				},
					dataIndex :'importeTraspaso',
					renderer: BFwrk.Util.rendererMoneyBlue
				},{
					header :'Fondeado',
					width :80,
					sortable :true,
					dataIndex :'importeF',
					renderer: BFwrk.Util.rendererMoney
				},{
					header :'Saldo en Chequera',
					width :80,
					sortable :true,
					dataIndex :'saldoChequera',
					renderer: BFwrk.Util.rendererMoney
				},{
					header :'Saldo mínimo',
					width :80,
					sortable :true,
					dataIndex :'saldoMinimoChequera',
					renderer: BFwrk.Util.rendererMoney
				},{
					header :'Prestamos Autorizar',
					width :80,
					sortable :true,
					dataIndex :'prestamos'
				},{
					header :'Chequera Benef',
					width :80,
					sortable :true,
					dataIndex :'idChequera'
				},{
					header :'Banco Benef',
					width :80,
					sortable :true,
					dataIndex :'descBanco'
				},{
					header :'Divisa',
					width :80,
					sortable :true,
					dataIndex :'idDivisa'
				},{
					header :'Concepto',
					width :80,
					sortable :true,
					dataIndex :'concepto'
				},{
					header :'Saldo Coinversión',
					width :100,
					sortable :true,
					dataIndex :'saldoCoinversion',
					renderer: BFwrk.Util.rendererMoney
				},{
					header :'Saldo Coinversión Otra Divisa',
					width :100,
					sortable :true,
					dataIndex :'saldoCoinversionOtraDivisa',
					renderer: BFwrk.Util.rendererMoney,
					hidden: true
				}
				,{
					header :'Id caja',
					width :100,
					sortable :true,
					dataIndex :'idCaja',
					//renderer: BFwrk.Util.rendererMoney,
					hidden: true
				}
            ]
        }),
        sm: NS.sm,
        columnLines: true,
        x:0,
        y:0,
        width:950,
        height:200,
        frame:true,
        listeners:{
        	click:{
        		fn:function(e){
          			var importeTraspaso = 0; 
          			var regSelec = NS.gridFondeos.getSelectionModel().getSelections();
          			
          			for(var k=0;k<regSelec.length;k=k+1)
          				importeTraspaso = importeTraspaso + regSelec[k].get('importeTraspaso');
          			
          		 	Ext.getCmp(PF+'txtTotalRegistros').setValue(regSelec.length);
          		 	var totalF=Math.round((importeTraspaso)*100)/100;
          		 	Ext.getCmp(PF+'txtTotalTraspaso').setValue(NS.cambiarFormato(totalF, '$'));
          		 	//Ext.getCmp(PF+'txtTotalTraspaso').setValue(BFwrk.Util.formatNumber(Math.round((importeTraspaso * 100)/100)));
          		 	//Ext.getCmp(PF+'txtTotalTraspaso').setValue(BFwrk.Util.formatNumber(importeTraspaso));
        		}
        	}
        }
    });

	//store para el grid de la ventana de pagos pendientes
	NS.storeGridPagosP = new Ext.data.DirectStore({
  	 	paramsAsHash: false,
  	 	baseParams: {
			iIdEmpresa:0,
			iIdEmpresaRaiz:0,
			iIdBanco:0,
			sIdDivisa:'',
	        sIdChequera:'',
	        sTipoBusqueda:''
		},
		root: '',
		paramOrder:['iIdEmpresa','iIdEmpresaRaiz','iIdBanco','sIdDivisa','sIdChequera','sTipoBusqueda'],
		directFn: CoinversionAction.obtenerPagos,
		fields: [
			{name: 'cveControl'},
			{name: 'importe'},
			{name: 'importeOriginal'}
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeGridPagosP, msg:"Buscando..."});
				if(records.length==null || records.length<=0)
				{
					Ext.Msg.alert('Informaci&oacute;n  SET','No existen pagos para fondear');
				}
			}
		}
	}); 
	
	//Grid detalle pagos pendientes
	NS.gridPagosP = new Ext.grid.GridPanel({
        store : NS.storeGridPagosP,
        cm: new Ext.grid.ColumnModel({
            defaults: {
                width: 120,
                value:true,
                sortable: true
            },
            columns: [
                NS.sm2,
                {
					header :'Clave Control',
					width :120,
					sortable :true,
					dataIndex :'cveControl'
				},{
					
					header :'Importe',
					width :100,
					sortable :true,
					dataIndex :'importe',
					renderer: BFwrk.Util.rendererMoney
				},{
					
					header :'Importe Original',
					width :100,
					sortable :true,
					dataIndex :'importeOriginal',
					renderer: BFwrk.Util.rendererMoney
				}
            ]
        }),
        sm: NS.sm2,
        columnLines: true,
        x:0,
        y:0,
        width:700,
        height:180,
        frame:true,
        listeners:{
        	click:{
        		fn:function(e){
          			var importe = 0; 
          			var regSelec = NS.gridPagosP.getSelectionModel().getSelections();
          			
          			for(var k = 0;k < regSelec.length;k = k+1)
          				importe = importe + regSelec[k].get('importe');
          			
          		 	Ext.getCmp(PF+'txtTotalRegistrosPP').setValue(regSelec.length);
          		 	//Ext.getCmp(PF+'txtImporteTotalPP').setValue(BFwrk.Util.formatNumber(Math.round((importe * 100)/100)));
          		 	Ext.getCmp(PF+'txtImporteTotalPP').setValue(BFwrk.Util.formatNumber(importe));
        		}
        	},
        	dblclick:{
        		fn:function(e){
        		    var cveControl = '';
          			var regSelec = NS.gridPagosP.getSelectionModel().getSelections();
          			
          			for(var k = 0;k < regSelec.length;k = k+1)
          				cveControl = regSelec[k].get('cveControl');
          		 	
          		 	NS.storeGridPagosPDetalle.baseParams.sCveControl = ''+cveControl;
          		 	var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeGridPagosP, msg:"Buscando..."});
          			NS.storeGridPagosPDetalle.load();
          			Ext.getCmp(PF+'txtTotalRegistrosDP').setValue('0');
					Ext.getCmp(PF+'txtImporteTotalDP').setValue('0.0');
        		}
        	}
        }
    }); 
   
   //store para el grid detalle de la ventana de pagos pendientes
	NS.storeGridPagosPDetalle = new Ext.data.DirectStore({
  	 	paramsAsHash: false,
  	 	baseParams: {
			iIdEmpresa:0,
			iIdEmpresaRaiz:0,
			iIdBanco:0,
			sIdDivisa:'',
	        sIdChequera:'',
	        sTipoBusqueda:'',
	        sCveControl:''
		},
		root: '',
		paramOrder:['iIdEmpresa','iIdEmpresaRaiz','iIdBanco','sIdDivisa','sIdChequera','sTipoBusqueda','sCveControl'],
		directFn: CoinversionAction.obtenerDesglosePagos,
		fields: [
			{name: 'cveControl'},
			{name: 'importe'},
			{name: 'descFormaPago'},
			{name: 'beneficiario'},
			{name: 'concepto'},
			{name: 'noFolioDet'}
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeGridPagosP, msg:"Buscando..."});
				if(records.length==null || records.length<=0)
				{
					Ext.Msg.alert('Informaci&oacute;n  SET','No existen pagos para fondear');
				}
			}
		}
	}); 
	
	NS.gridPagosPDetalle = new Ext.grid.GridPanel({
        store : NS.storeGridPagosPDetalle,
        cm: new Ext.grid.ColumnModel({
            defaults: {
                width: 120,
                value:true,
                sortable: true
            },
            columns: [
                NS.sm3,
                {
					header :'Clave Control',
					width :120,
					sortable :true,
					dataIndex :'cveControl'
				},{
					
					header :'Importe',
					width :100,
					sortable :true,
					dataIndex :'importe',
					renderer: BFwrk.Util.rendererMoney
				},{
					
					header :'Forma Pago',
					width :100,
					sortable :true,
					dataIndex :'descFormaPago'
				},{
					
					header :'Beneficiario',
					width :100,
					sortable :true,
					dataIndex :'beneficiario'
				},{
					
					header :'Concepto',
					width :100,
					sortable :true,
					dataIndex :'concepto'
				},{
					header :'Folio Det',
					width :100,
					sortable :true,
					dataIndex :'noFolioDet'
				}
            ]
        }),
        sm: NS.sm3,
        columnLines: true,
        x:0,
        y:0,
        width:700,
        height:180,
        frame:true,
        listeners:{
        	click:{
        		fn:function(e){
          			var importe = 0; 
          			var regSelec = NS.gridPagosPDetalle.getSelectionModel().getSelections();
          			
          			for(var k=0;k<regSelec.length;k=k+1)
          				importe = importe + regSelec[k].get('importe');
          				
          		 	Ext.getCmp(PF+'txtTotalRegistrosDP').setValue(regSelec.length);
          		 	Ext.getCmp(PF+'txtImporteTotalDP').setValue(BFwrk.Util.formatNumber(importe));
          		 	//Ext.getCmp(PF+'txtImporteTotalDP').setValue(BFwrk.Util.formatNumber(Math.round((importe * 100)/100)));
        		}
        	}
        }
    }); 
    
    //store para el grid FonVencHoy de la ventana de Fondeo de Cheques
	NS.storeFonVencHoy = new Ext.data.DirectStore({
  	 	paramsAsHash: false,
  	 	baseParams: {
			sIdChequera:'',
			iIdBanco:0,
			sVencimiento: 'H'
		},
		root: '',
		paramOrder:['sIdChequera','iIdBanco','sVencimiento'],
		directFn: CoinversionAction.obtenerFondeoCheques,
		fields: [
			{name: 'folioFondeo'},
			{name: 'noFolioDet'},
			{name: 'fecValor'},
			{name: 'fecEntregado'},
			{name: 'beneficiario'},
			{name: 'importe'}
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeGridPagosP, msg:"Buscando..."});
				if(records.length==null || records.length<=0)
				{
					Ext.Msg.alert('Información  SET','No existen vencimientos para el día de hoy');
				}
			}
		}
	}); 
	
	NS.gridFonVencHoy = new Ext.grid.GridPanel({
        store : NS.storeFonVencHoy,
        cm: new Ext.grid.ColumnModel({
            defaults: {
                width: 120,
                value:true,
                sortable: true
            },
            columns: [
                NS.sm4,
                {
					header :'Folio Fondeo',
					width :120,
					sortable :true,
					dataIndex :'folioFondeo'
				},{
					
					header :'No Folio Det',
					width :100,
					sortable :true,
					dataIndex :'noFolioDet'
				},{
					
					header :'Fec Valor',
					width :100,
					sortable :true,
					dataIndex :'fecValor'
				},{
					
					header :'Beneficiario',
					width :150,
					sortable :true,
					dataIndex :'beneficiario'
				},{
					
					header :'Fec Entregado',
					width :100,
					sortable :true,
					dataIndex :'fecEntregado'
				},{
					header :'Importe',
					width :100,
					sortable :true,
					dataIndex :'importe',
					renderer: BFwrk.Util.rendererMoney
				}
            ]
        }),
        sm: NS.sm4,
        columnLines: true,
        x:0,
        y:0,
        width:700,
        height:180,
        frame:true,
        listeners:{
        	click:{
        		fn:function(e){
          			var fondeosPendientes = 0; 
          			var pagosFondear = 0;
          			var montosDia = 0;
          			var montosFondear = 0;
          			var regSelec = NS.gridFonVencHoy.getSelectionModel().getSelections();
          			var totalReg = NS.storeFonVencHoy.data.items; 
          			
          			for(var i = 0; i < regSelec.length; i = i + 1)
          			{
          				montosFondear = montosFondear + regSelec[i].get('importe');
          			}
          			for(var inc = 0; inc < totalReg.length; inc = inc + 1)
          			{
          				montosDia = montosDia + totalReg[inc].get('importe');
          			}
          			
          		 	Ext.getCmp(PF+'lblFonPenHoy').setText(totalReg.length - regSelec.length);
          		 	Ext.getCmp(PF+'lblPagFonHoy').setText(regSelec.length);
          		 	//Ext.getCmp(PF+'lblMontosDia').setText(BFwrk.Util.formatNumber(Math.round(((montosDia - montosFondear) * 100)/100)));
          		 	//Ext.getCmp(PF+'lblMontosFondear').setText(BFwrk.Util.formatNumber(Math.round((montosFondear * 100)/100)));
          		 	Ext.getCmp(PF+'lblMontosDia').setText(BFwrk.Util.formatNumber(montosDia - montosFondear));
          		 	Ext.getCmp(PF+'lblMontosFondear').setText(BFwrk.Util.formatNumber(montosFondear));
          		 	 
        		}
        	}
        }
    }); 
    
    
    //store para el grid FonVencAnt de la ventana de Fondeo de Cheques
	NS.storeFonVencAnt = new Ext.data.DirectStore({
  	 	paramsAsHash: false,
  	 	baseParams: {
			sIdChequera:'',
			iIdBanco:0,
			sVencimiento: 'A'
		},
		root: '',
		paramOrder:['sIdChequera','iIdBanco','sVencimiento'],
		directFn: CoinversionAction.obtenerFondeoCheques,
		fields: [
			{name: 'folioFondeo'},
			{name: 'noFolioDet'},
			{name: 'fecValor'},
			{name: 'fecEntregado'},
			{name: 'beneficiario'},
			{name: 'importe'}
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeGridPagosP, msg:"Buscando..."});
				if(records.length==null || records.length<=0)
				{
					Ext.Msg.alert('Información  SET','No existen vencimientos anteriores');
				}
			}
		}
	}); 
	
	NS.gridFonVencAnt = new Ext.grid.GridPanel({
        store : NS.storeFonVencAnt,
        cm: new Ext.grid.ColumnModel({
            defaults: {
                width: 120,
                value:true,
                sortable: true
            },
            columns: [
                NS.sm5,
                {
					header :'Folio Fondeo',
					width :120,
					sortable :true,
					dataIndex :'folioFondeo'
				},{
					
					header :'No Folio Det',
					width :100,
					sortable :true,
					dataIndex :'noFolioDet'
				},{
					
					header :'Fec Valor',
					width :100,
					sortable :true,
					dataIndex :'fecValor'
				},{
					
					header :'Beneficiario',
					width :150,
					sortable :true,
					dataIndex :'beneficiario'
				},{
					
					header :'Fec Entregado',
					width :100,
					sortable :true,
					dataIndex :'fecEntregado'
				},{
					header :'Importe',
					width :100,
					sortable :true,
					dataIndex :'importe',
					renderer: BFwrk.Util.rendererMoney
				}
            ]
        }),
        sm: NS.sm5,
        columnLines: true,
        x:0,
        y:0,
        width:700,
        height:180,
        frame:true,
        listeners:{
        	click:{
        		fn:function(e){
					var fondeosPendientes = 0; 
          			var pagosFondear = 0;
          			var montosDia = 0;
          			var montosFondear = 0;
          			var regSelec = NS.gridFonVencAnt.getSelectionModel().getSelections();
          			var totalReg = NS.storeFonVencAnt.data.items; 
          			
          			for(var i = 0; i < regSelec.length; i = i + 1)
          			{
          				montosFondear = montosFondear + regSelec[i].get('importe');
          			}
          			for(var inc = 0; inc < totalReg.length; inc = inc + 1)
          			{
          				montosDia = montosDia + totalReg[inc].get('importe');
          			}
          			
          		 	Ext.getCmp(PF+'lblFonPenAnt').setText(totalReg.length - regSelec.length);
          		 	Ext.getCmp(PF+'lblPagFonAnt').setText(regSelec.length);
          		 	//Ext.getCmp(PF+'lblMontosFondearAnt').setText(BFwrk.Util.formatNumber(Math.round(((montosDia - montosFondear) * 100)/100)));
          		 	//Ext.getCmp(PF+'lblMontosAnteriores').setText(BFwrk.Util.formatNumber(Math.round((montosFondear * 100)/100)));     
          		 	Ext.getCmp(PF+'lblMontosFondearAnt').setText(BFwrk.Util.formatNumber(montosDia - montosFondear));
          		 	Ext.getCmp(PF+'lblMontosAnteriores').setText(BFwrk.Util.formatNumber(montosFondear));    		

        		}
        	}
        }
    }); 
    
    NS.buscar = function(){//Funcion para realizar la búsqueda
    
    //	NS.idEmpresa = Ext.getCmp(PF+'cmbEmpresasConcentradoras').getValue() != null 
	//    	&& Ext.getCmp(PF+'cmbEmpresasConcentradoras').getValue() != ''? Ext.getCmp(PF+'cmbEmpresasConcentradoras').getValue() : 0;
    	
			
    	
		NS.idChequera = Ext.getCmp(PF+'cmbChequera').getValue();
		NS.idArbolConcen = Ext.getCmp(PF+'cmbArbol').getValue()!= null 
			&& Ext.getCmp(PF+'cmbArbol').getValue() != ''? Ext.getCmp(PF+'cmbArbol').getValue() : 0;
		NS.idDivisa = Ext.getCmp(PF+'cmbDivisa').getValue();
		
		
		if(NS.visibleCmbBancoRaiz)
			NS.idBanco = Ext.getCmp(PF+'txtIdBanco').getValue();
		if(NS.idBanco == ''){
			NS.idBanco = 0;
		//	NS.idBanco = Ext.getCmp(PF+'cmbBancoRaiz').getValue() != null && Ext.getCmp(PF+'cmbBancoRaiz').getValue() != ''? Ext.getCmp(PF+'cmbBancoRaiz').getValue() : 0;
    }else
	//	NS.idBanco = Ext.getCmp(PF+'cmbBanco').getValue() != null && Ext.getCmp(PF+'cmbBanco').getValue() != ''? Ext.getCmp(PF+'cmbBanco').getValue() : 0;
    	
    	
    	NS.chkMismoBanco = Ext.getCmp(PF+'checkMismoBanco').getValue();
                          			
		if(NS.idArbolConcen <= 0 && NS.idEmpresa <= 0)	
		{                                		
			Ext.Msg.alert('Información SET','Debe seleccionar la Empresa Raiz');
			return;
		}
		if(NS.idDivisa == '')
		{	                                		
			Ext.Msg.alert('Información SET','Debe seleccionar una divisa');
			return;
		}
		if(NS.chkMismoBanco == true && NS.idBanco <= 0)
		{	                                		
			Ext.Msg.alert('Información SET','Debe seleccionar el Banco cuando se busca por mismo banco');
			return;
		}
		/*if(NS.visibleCmbChequera && NS.idChequera == '')
		{	                                		
			Ext.Msg.alert('Informaci&oacute;n SET','Debe seleccionar una chequera');
			return;	
		}*/
     //   if(Ext.getCmp(PF+'txtIdEmpresasArbol').getValue() != 0)
     //   	NS.storeGridFondeos.baseParams.idEmpresa = parseInt(Ext,getCmp(PF+'txtIdEmpresasArbol').getValue());
     //   else
		
        NS.storeGridFondeos.baseParams.idEmpresa = 0;
		NS.storeGridFondeos.baseParams.idEmpresaRaiz = parseInt(NS.idArbolConcen);
		NS.storeGridFondeos.baseParams.idBanco = parseInt(NS.idBanco);
		NS.storeGridFondeos.baseParams.idDivisa = ''+NS.idDivisa;
		NS.storeGridFondeos.baseParams.idChequera = ''+NS.idChequera;
		NS.storeGridFondeos.baseParams.chkMismoBanco = Ext.getCmp(PF+'checkMismoBanco').getValue();
		NS.storeGridFondeos.baseParams.tipoBusqueda = NS.tipoBusqueda;
		NS.idEmpresa = 0;
		mascara.show();
		NS.storeGridFondeos.load();
		NS.gridFondeos.getView().refresh();
    }
    
    
    NS.ejecutar = function(){//Funcion para realizar la accion de ejecutar
        
    	NS.idEmpresa = 0;
		NS.idChequera = Ext.getCmp(PF+'cmbChequera').getValue();
		NS.idArbolConcen = Ext.getCmp(PF+'cmbArbol').getValue()!= null 
			&& Ext.getCmp(PF+'cmbArbol').getValue() != ''? Ext.getCmp(PF+'cmbArbol').getValue() : 0;
		NS.idDivisa = Ext.getCmp(PF+'cmbDivisa').getValue();
		var totalFondeo = Ext.getCmp(PF+'txtTotalTraspaso').getValue();
		
		
		if(totalFondeo == '0' || totalFondeo == '0.0'){
			Ext.Msg.alert('Información SET','El Total del Fondeo es CERO');
			return;
		}
		if(NS.visibleCmbBancoRaiz)
		{
			NS.idBanco = Ext.getCmp(PF+'cmbBancoRaiz').getValue() != null && Ext.getCmp(PF+'cmbBancoRaiz').getValue() != ''? Ext.getCmp(PF+'cmbBancoRaiz').getValue() : 0;
		}
		else
		{
			NS.idBanco = Ext.getCmp(PF+'cmbBanco').getValue() != null && Ext.getCmp(PF+'cmbBanco').getValue() != ''? Ext.getCmp(PF+'cmbBanco').getValue() : 0;
		}
			
		NS.chkMismoBanco = Ext.getCmp(PF+'checkMismoBanco').getValue();
                       
		if(NS.idArbolConcen <= 0 && NS.idEmpresa <= 0)	
		{                                		
			Ext.Msg.alert('Información SET','Hace falta la concentradora');
			return;
		}
		/*if(NS.idBanco <= 0 && NS.idArbolConcen <= 0)
		{
			Ext.Msg.alert('Informaci&oacute;n SET','Hace falta el banco');
			return;
		}*/
		if(NS.idDivisa == '')
		{	                                		
			Ext.Msg.alert('Información SET','Debe seleccionar una divisa');
			return;
		}
		if(NS.chkMismoBanco == true && NS.idBanco <= 0)
		{	                                		
			Ext.Msg.alert('Información SET','Debe seleccionar el Banco cuando se busca por mismo banco');
			return;
		}
		if(NS.visibleCmbChequera)
		{	                                		
	//		Ext.Msg.alert('Información SET','Debe seleccionar una chequera');
		}
		else if (NS.visibleCmbChequera && NS.idChequera == '')
		{
			Ext.Msg.alert('Información SET','Debe seleccionar una chequera');
			return;	
		}
		
		
		var matDatFondeo = new Array();
		var regSelecFondeo = "";
		regSelecFondeo = NS.gridFondeos.getSelectionModel().getSelections();
		
		if(regSelecFondeo.length === 0)
		{
			Ext.Msg.alert('Información SET','Es necesario seleccionar algun registro');
			return;
		}
	//	NS.prepararFondeo();
		NS.imprimirTodo();
		
		Ext.Msg.confirm('SET', 'Se generó reporte de validación, ¿Desea enviar el fondeo?', function(btn){
			if(btn == 'yes'){
		BFwrk.Util.msgWait('Procesando fondeo, espere por favor...', true);
		for(var i=0;i<regSelecFondeo.length;i=i+1)
        {
			var regGridFondeo = {};
	        regGridFondeo.orden = regSelecFondeo[i].get('orden');
	     	regGridFondeo.prestamos = regSelecFondeo[i].get('prestamos');
	     	regGridFondeo.noEmpresaOrigen = regSelecFondeo[i].get('noEmpresaOrigen');
	     	regGridFondeo.nomEmpresaOrigen = regSelecFondeo[i].get('nomEmpresaOrigen');
	     	regGridFondeo.noEmpresaDestino = regSelecFondeo[i].get('noEmpresaDestino');
	     	regGridFondeo.nomEmpresaDestino = regSelecFondeo[i].get('nomEmpresaDestino');
	     	regGridFondeo.descBanco = regSelecFondeo[i].get('descBanco');
	     	regGridFondeo.idBanco = regSelecFondeo[i].get('idBanco');
	     	regGridFondeo.idChequera = regSelecFondeo[i].get('idChequera');
	     	regGridFondeo.saldoChequera = regSelecFondeo[i].get('saldoChequera');
	     	regGridFondeo.saldoMinimoChequera = regSelecFondeo[i].get('saldoMinimoChequera');
	     	regGridFondeo.importeF = regSelecFondeo[i].get('importeF');
	     	regGridFondeo.tipoCambio = regSelecFondeo[i].get('tipoCambio');
	     	regGridFondeo.idDivisa = regSelecFondeo[i].get('idDivisa');
	     	regGridFondeo.pm = regSelecFondeo[i].get('pm');
	     	regGridFondeo.pmCheques = regSelecFondeo[i].get('pmCheques');
	     	regGridFondeo.concepto = regSelecFondeo[i].get('concepto');
	     	regGridFondeo.importeTraspaso = regSelecFondeo[i].get('importeTraspaso');
	     	regGridFondeo.saldoCoinversion = regSelecFondeo[i].get('saldoCoinversion');
	        regGridFondeo.idChequeraPadre = NS.idChequera;
	        regGridFondeo.idCajaE = regSelecFondeo[i].get('idCaja');
	        regGridFondeo.idSaldo = NS.idTipoSaldo;
	        
	        matDatFondeo[i] = regGridFondeo; 
        }

   	var jsonGridFondeo = Ext.util.JSON.encode(matDatFondeo);
    	BarridosFondeosAction.ejecutarFondeoAutomatico(jsonGridFondeo, parseInt(NS.idEmpresa), parseInt(NS.idArbolConcen),
			parseInt(NS.idBanco),	''+NS.idDivisa, ''+NS.idChequera, Ext.getCmp(PF+'checkMismoBanco').getValue(),
			NS.tipoBusqueda, NS.visibleMontoMinFondeo, ''+NS.montoMinFondeo,parseInt(NS.idUsuario), ''+NS.cmbArbol.getRawValue(),function(result, e){
			BFwrk.Util.msgWait('Terminando...', false);
			if(result != null && result != '')
			{
				alert(result);
				Ext.Msg.alert('Información SET', result);
				console.log('Llegó respuesta success.' + result);
				NS.buscar();
			}	
	//		Ext.Msg.alert('Información SET', result.msgUsuario);
			
    	});
    
			}
		});
    }
    NS.prepararFondeo = function()
    {
    	var matDatFondeo = new Array();
		var regSelecFondeo = "";
		regSelecFondeo = NS.gridFondeos.getSelectionModel().getSelections();
		
    	for(var i=0;i<regSelecFondeo.length;i=i+1)
        {
			var regGridFondeo = {};
	        regGridFondeo.orden = regSelecFondeo[i].get('orden');
	     	regGridFondeo.prestamos = regSelecFondeo[i].get('prestamos');
	     	regGridFondeo.noEmpresaOrigen = regSelecFondeo[i].get('noEmpresaOrigen');
	     	regGridFondeo.nomEmpresaOrigen = regSelecFondeo[i].get('nomEmpresaOrigen');
	     	regGridFondeo.noEmpresaDestino = regSelecFondeo[i].get('noEmpresaDestino');
	     	regGridFondeo.nomEmpresaDestino = regSelecFondeo[i].get('nomEmpresaDestino');
	     	regGridFondeo.descBanco = regSelecFondeo[i].get('descBanco');
	     	regGridFondeo.idBanco = regSelecFondeo[i].get('idBanco');
	     	regGridFondeo.idChequera = regSelecFondeo[i].get('idChequera');
	     	regGridFondeo.saldoChequera = regSelecFondeo[i].get('saldoChequera');
	     	regGridFondeo.saldoMinimoChequera = regSelecFondeo[i].get('saldoMinimoChequera');
	     	regGridFondeo.importeF = regSelecFondeo[i].get('importeF');
	     	regGridFondeo.tipoCambio = regSelecFondeo[i].get('tipoCambio');
	     	regGridFondeo.idDivisa = regSelecFondeo[i].get('idDivisa');
	     	regGridFondeo.pm = regSelecFondeo[i].get('pm');
	     	regGridFondeo.pmCheques = regSelecFondeo[i].get('pmCheques');
	     	regGridFondeo.concepto = regSelecFondeo[i].get('concepto');
	     	regGridFondeo.importeTraspaso = regSelecFondeo[i].get('importeTraspaso');
	     	regGridFondeo.saldoCoinversion = regSelecFondeo[i].get('saldoCoinversion');
	        regGridFondeo.idChequeraPadre = NS.idChequera;
	        
	        matDatFondeo[i] = regGridFondeo; 
        }
    	var jsonGridFondeo = Ext.util.JSON.encode(matDatFondeo);
    	BarridosFondeosAction.prepararFondeoAutomatico(jsonGridFondeo, NS.idEmpresa, NS.idArbolConcen,
			NS.idBanco,	NS.idDivisa, NS.idChequera, Ext.getCmp(PF+'checkMismoBanco').getValue(),
			NS.tipoBusqueda, NS.visibleMontoMinFondeo, NS.montoMinFondeo,NS.idUsuario, NS.cmbArbol.getRawValue(),function(result, e){
		//	BFwrk.Util.msgWait('Terminando...', false);
			if(result != null && result != '')
			{
				Ext.Msg.alert('Información SET', result);
				console.log('Llegó respuesta success.' + result);
			//	NS.buscar();
			}
    });
    }

    NS.imprimirArbol =  function()
    {
    	var strParams = '';
    	strParams = '?nomReporte=ReporteArbolEmpresaFondeo';
    	strParams += '&'+'noEmpresa=' + Ext.getCmp(PF+'txtIdArbol').getValue();
		strParams += '&'+'idUsuario=' + NS.idUsuario;
		strParams += '&' + 'EMPRESA=' + Ext.getCmp(PF+'cmbArbol').getRawValue();
		strParams += '&' + 'NOM_ARBOL=' + Ext.getCmp(PF+'cmbArbol').getRawValue();
	//	strParams += '&' + 'opera=' + Ext.getCmp(PF+'cmbTipoOperacion').getRawValue();
		
		window.open("/SET/jsp/Reportes.jsp" + strParams);
    };
    
    NS.imprimir = function()
    {
        var strParams = '';
		
		strParams = '?nomReporte=ReporteFondeoBarrido';	
		
		strParams += '&'+'noEmpresa=' + Ext.getCmp(PF+'txtIdArbol').getValue();
		strParams += '&'+'idUsuario=' + NS.idUsuario;
		strParams += '&'+'EMPRESA=' +  NS.cmbArbol.getRawValue();
		window.open("/SET/jsp/Reportes.jsp" + strParams);
    }
    NS.imprimir = function()
	{
		var strParams = '';
		
		strParams = '?nomReporte=ReporteFondeoBarrido';	
		
		strParams += '&'+'noEmpresa=' + Ext.getCmp(PF+'txtIdArbol').getValue();
		strParams += '&'+'idUsuario=' + NS.idUsuario;
		strParams += '&'+'EMPRESA=' +  NS.cmbArbol.getRawValue();
		window.open("/SET/jsp/Reportes.jsp" + strParams);
	};
	
	NS.imprimirTodo = function()
	{
		var matDatFondeo = new Array();
		var regSelecFondeo = "";
		regSelecFondeo = NS.gridFondeos.getSelectionModel().getSelections();
		
		if(regSelecFondeo.length === 0)
		{
			Ext.Msg.alert('Información SET','Es necesario seleccionar algun registro');
			return;
		}
		
		var j = 0;
		for(var i=0;i<regSelecFondeo.length;i++)
        {
			var regGridFondeo = {};
	        regGridFondeo.orden = regSelecFondeo[i].get('orden');
	     	regGridFondeo.prestamos = regSelecFondeo[i].get('prestamos');
	     	regGridFondeo.importeTraspaso = regSelecFondeo[i].get('importeTraspaso');
	     	regGridFondeo.importeBarrido  = regSelecFondeo[i].get('importeBarrido');

	     //	if (regGridFondeo.importeTraspaso > 0){ // es un fondeo
		     	regGridFondeo.noEmpresaOrigen = regSelecFondeo[i].get('noEmpresaOrigen');
		     	regGridFondeo.nomEmpresaOrigen = regSelecFondeo[i].get('nomEmpresaOrigen');
		     	regGridFondeo.noEmpresaDestino = regSelecFondeo[i].get('noEmpresaDestino');
		     	regGridFondeo.nomEmpresaDestino = regSelecFondeo[i].get('nomEmpresaDestino');
		     	regGridFondeo.idChequeraPadre = NS.idChequera;
		     	regGridFondeo.idChequera = regSelecFondeo[i].get('idChequera');
	     	/*}else{
	     		regGridFondeo.noEmpresaOrigen = regSelecFondeo[i].get('noEmpresaDestino');
		     	regGridFondeo.nomEmpresaOrigen = regSelecFondeo[i].get('nomEmpresaDestino');
		     	regGridFondeo.noEmpresaDestino = regSelecFondeo[i].get('noEmpresaOrigen');
		     	regGridFondeo.nomEmpresaDestino = regSelecFondeo[i].get('nomEmpresaOrigen');
		     	regGridFondeo.idChequera = NS.idChequera;
		     	regGridFondeo.idChequeraPadre = regSelecFondeo[i].get('idChequera');
	     	}*/
	     	regGridFondeo.descBanco = regSelecFondeo[i].get('descBanco');
	     	regGridFondeo.idBanco = regSelecFondeo[i].get('idBanco');
	     	
	     	regGridFondeo.saldoChequera = regSelecFondeo[i].get('saldoChequera');
	     	regGridFondeo.saldoMinimoChequera = regSelecFondeo[i].get('saldoMinimoChequera');
	     	regGridFondeo.importeF = regSelecFondeo[i].get('importeF');
	     	regGridFondeo.tipoCambio = regSelecFondeo[i].get('tipoCambio');
	     	regGridFondeo.idDivisa = regSelecFondeo[i].get('idDivisa');
	     	regGridFondeo.pm = regSelecFondeo[i].get('pm');
	     	regGridFondeo.pmCheques = regSelecFondeo[i].get('pmCheques');
	     	regGridFondeo.concepto = regSelecFondeo[i].get('concepto');
	     	regGridFondeo.saldoCoinversion = regSelecFondeo[i].get('saldoCoinversion');
	     	if (regGridFondeo.importeTraspaso > 0 || regGridFondeo.importeBarrido > 0){
		     	matDatFondeo[j] = regGridFondeo;
		        j++;
	     	}
        }

    	var jsonGridFondeo = Ext.util.JSON.encode(matDatFondeo);
    	var forma = NS.FondeoAutomatico.getForm();
    	Ext.getCmp('hdJasonBF').setValue(jsonGridFondeo);
    	Ext.getCmp('noEmpresa').setValue(NS.cmbArbol.getValue());
    	Ext.getCmp('EMPRESA').setValue(NS.cmbArbol.getRawValue());
    	Ext.getCmp('nomReporte').setValue("ReporteFondeoBarridoAuto");
    	forma.url = '/SET/jsp/Reportes.jsp';
	    forma.standardSubmit = true;
	    forma.method = 'POST';
	    forma.target = '_blank';
	    var el = forma.getEl().dom;
	    var target = document.createAttribute("target");
	    target.nodeValue = "_blank";
	    el.setAttributeNode(target);
	    forma.submit({target: '_blank'});
	};
	
	 /*--- Ejecutar barrido ---*/
    NS.ejecutarBarrido = function(){//Funcion para realizar la accion de ejecutar
        
    	NS.idEmpresa = 0;
		NS.idChequera = Ext.getCmp(PF+'cmbChequera').getValue();
		NS.idArbolConcen = Ext.getCmp(PF+'cmbArbol').getValue()!= null 
			&& Ext.getCmp(PF+'cmbArbol').getValue() != ''? Ext.getCmp(PF+'cmbArbol').getValue() : 0;
		NS.idDivisa = Ext.getCmp(PF+'cmbDivisa').getValue();
		var totalFondeo =  Ext.getCmp(PF+'txtTotalBarrido').getValue();
		if (totalFondeo == '0' || totalFondeo == '0.0'){
			Ext.Msg.alert('Información SET','El total del barrido es CERO');
			return;
		}
		
		if(NS.visibleCmbBancoRaiz)
		{
			NS.idBanco = Ext.getCmp(PF+'cmbBancoRaiz').getValue() != null && Ext.getCmp(PF+'cmbBancoRaiz').getValue() != ''? Ext.getCmp(PF+'cmbBancoRaiz').getValue() : 0;
		}
		else
		{
			NS.idBanco = Ext.getCmp(PF+'cmbBanco').getValue() != null && Ext.getCmp(PF+'cmbBanco').getValue() != ''? Ext.getCmp(PF+'cmbBanco').getValue() : 0;
		}
			
		NS.chkMismoBanco = Ext.getCmp(PF+'checkMismoBanco').getValue();
                       
		if(NS.idArbolConcen <= 0 && NS.idEmpresa <= 0)	
		{                                		
			Ext.Msg.alert('Información SET','Hace falta la concentradora');
			return;
		}
		/*if(NS.idBanco <= 0 && NS.idArbolConcen <= 0)
		{
			Ext.Msg.alert('Información SET','Hace falta el banco');
			return;
		}*/
		if(NS.idDivisa == '')
		{	                                		
			Ext.Msg.alert('Información SET','Debe seleccionar una divisa');
			return;
		}
		if(NS.chkMismoBanco == true && NS.idBanco <= 0)
		{	                                		
			Ext.Msg.alert('Información SET','Debe seleccionar el Banco cuando se busca por mismo banco');
			return;
		}
		if(NS.visibleCmbChequera && NS.idChequera == '')
		{	                                		
			Ext.Msg.alert('Información SET','Debe seleccionar una chequera');
			return;	
		}
		
		var matDatFondeo = new Array();
		var regSelecFondeo = "";
		regSelecFondeo = NS.gridFondeos.getSelectionModel().getSelections();
		
		if(regSelecFondeo.length === 0)
		{
			Ext.Msg.alert('Información SET','Es necesario seleccionar algun registro');
			return;
		}
		
		NS.imprimirTodo();
		Ext.Msg.confirm('SET', 'Se generó reporte de validación, ¿Desea enviar el barrido?', function(btn){
		if (btn == 'yes'){
			BFwrk.Util.msgWait('Procesando barrido, espere por favor...', true);
			for(var i=0;i<regSelecFondeo.length;i=i+1)
	        {
				var regGridFondeo = {};
		        regGridFondeo.orden = regSelecFondeo[i].get('orden');
		     	regGridFondeo.prestamos = regSelecFondeo[i].get('prestamos');
		     	regGridFondeo.noEmpresaOrigen = regSelecFondeo[i].get('noEmpresaOrigen');
		     	regGridFondeo.nomEmpresaOrigen = regSelecFondeo[i].get('nomEmpresaOrigen');
		     	regGridFondeo.noEmpresaDestino = regSelecFondeo[i].get('noEmpresaDestino');
		     	regGridFondeo.nomEmpresaDestino = regSelecFondeo[i].get('nomEmpresaDestino');
		     	regGridFondeo.descBanco = regSelecFondeo[i].get('descBanco');
		     	regGridFondeo.idBanco = regSelecFondeo[i].get('idBanco');
		     	regGridFondeo.idChequera = regSelecFondeo[i].get('idChequera');
		     	regGridFondeo.idChequeraPadre = NS.idChequera;
		     	regGridFondeo.saldoChequera = regSelecFondeo[i].get('saldoChequera');
		     	regGridFondeo.saldoMinimoChequera = regSelecFondeo[i].get('saldoMinimoChequera');
		     	regGridFondeo.importeF = regSelecFondeo[i].get('importeF');
		     	regGridFondeo.tipoCambio = regSelecFondeo[i].get('tipoCambio');
		     	regGridFondeo.idDivisa = regSelecFondeo[i].get('idDivisa');
		     	regGridFondeo.pm = regSelecFondeo[i].get('pm');
		     	regGridFondeo.pmCheques = regSelecFondeo[i].get('pmCheques');
		     	regGridFondeo.concepto = regSelecFondeo[i].get('concepto');
		     	regGridFondeo.importeTraspaso = regSelecFondeo[i].get('importeTraspaso');
		     	regGridFondeo.importeBarrido  = regSelecFondeo[i].get('importeBarrido');
		     	regGridFondeo.saldoCoinversion = regSelecFondeo[i].get('saldoCoinversion');
		        
		        matDatFondeo[i] = regGridFondeo; 
	        }
	
	    	var jsonGridFondeo = Ext.util.JSON.encode(matDatFondeo);
	    	
	    	BarridosFondeosAction.ejecutarBarridoAutomatico(jsonGridFondeo, NS.idEmpresa, NS.idArbolConcen,
				NS.idBanco,	NS.idDivisa, NS.idChequera, Ext.getCmp(PF+'checkMismoBanco').getValue(),
				NS.tipoBusqueda, NS.visibleMontoMinFondeo, NS.montoMinFondeo, NS.idUsuario, NS.cmbArbol.getRawValue(),function(result, e){
				BFwrk.Util.msgWait('Terminando...', false);
				if(result != null && result != '')
				{
					Ext.Msg.alert('Información SET', result);
					
					NS.buscar();
				}	
	    	});
		}
 
    });
    }
    /*--- termina ejecutar barrido ---*/
    
/*	NS.ejecutarFondeoCheques = function(){//Funcion para realizar la accion de ejecutar de la pantalla fondeo de cheques 
    
		var matVencHoy = new Array();
		var regSelecGrigVencHoy = NS.gridFonVencHoy.getSelectionModel().getSelections();
		var allRegVencHoy = NS.storeFonVencHoy.data.items; 

		//Variables para los datos del grid de vencimientos anteriores
		var matVencAnt = new Array();
		var regSelecGrigVencAnt = NS.gridFonVencAnt.getSelectionModel().getSelections();
		var allRegVencAnt = NS.storeFonVencAnt.data.items; 
		
		if(regSelecGrigVencHoy.length == 0 && regSelecGrigVencAnt.length == 0)
		{
			Ext.Msg.alert('Información SET','Es necesario seleccionar algun registro');
			return;
		}
		BFwrk.Util.msgWait('Procesando fondeo, espere por favor...', true);
		for(var i = 0; i < allRegVencHoy.length; i = i+1)
        {
        	var regGridVencHoy = {};
        	for(var inc = 0; inc < regSelecGrigVencHoy.length; inc = inc + 1)
        	{
        		if(allRegVencHoy[i].get('noFolioDet') == regSelecGrigVencHoy[inc].get('noFolioDet'))
        		{
        			regGridVencHoy.seleccionado = true; 
        			break;
        		}
				else
				{
		    		regGridVencHoy.seleccionado = false;
		    	}
        	}
			      	
        	regGridVencHoy.folioFondeo = allRegVencHoy[i].get('folioFondeo');
	     	regGridVencHoy.noFolioDet = allRegVencHoy[i].get('noFolioDet');
	     	regGridVencHoy.fecValor = allRegVencHoy[i].get('fecValor');
	     	regGridVencHoy.fecEntregado = allRegVencHoy[i].get('fecEntregado');
	     	regGridVencHoy.beneficiario = allRegVencHoy[i].get('beneficiario');
	     	regGridVencHoy.importe = allRegVencHoy[i].get('importe');
	       	matVencHoy[i] = regGridVencHoy; 
        }

    	var jsonGridVencHoy = Ext.util.JSON.encode(matVencHoy);
    	
		for(var c = 0; c < allRegVencAnt.length; c = c+1)
        {
        	var regGridVencAnt = {};
        	for(var a = 0; a < regSelecGrigVencAnt.length; a = a + 1)
        	{
        		if(allRegVencAnt[c].get('noFolioDet') == regSelecGrigVencAnt[a].get('noFolioDet'))
        		{
        			regGridVencAnt.seleccionado = true; 
        			break;
        		}
				else
				{
		    		regGridVencAnt.seleccionado = false;
		    	}
        	} 
			      	
        	regGridVencAnt.folioFondeo = allRegVencAnt[i].get('folioFondeo');
	     	regGridVencAnt.noFolioDet = allRegVencAnt[i].get('noFolioDet');
	     	regGridVencAnt.fecValor = allRegVencAnt[i].get('fecValor');
	     	regGridVencAnt.fecEntregado = allRegVencAnt[i].get('fecEntregado');
	     	regGridVencAnt.beneficiario = allRegVencAnt[i].get('beneficiario');
	     	regGridVencAnt.importe = allRegVencAnt[i].get('importe');
	       	matVencAnt[i] = regGridVencAnt; 
        }

    	var jsonGridVencAnt = Ext.util.JSON.encode(matVencAnt);
			
    	CoinversionAction.ejecutarFondeoCheques(jsonGridVencHoy, jsonGridVencAnt
    		,NS.idBancoGrid, NS.noCuentaChequesGrid, function(result, e){
				BFwrk.Util.msgWait('Terminando...', false);
				if(result != null && result != '')
				{
					BFwrk.Util.msgShow('' + result.msgUsuario, 'INFO');
					NS.storeFonVencHoy.load();
					NS.storeFonVencAnt.load();
				}	
    	});
    }
    
    
	//Ventana que contiene el formulario de pagos pendientes de fondeo
	var winPagosPendientes = new Ext.Window({
		title: 'Pagos Pendientes'
		,modal:true
		,shadow:true
		,width: 790
	   	,height: 700
	   	,minWidth: 400
	   	,minHeight: 580
	   	,layout: 'fit'
	   	,plain:true
	    ,bodyStyle:'padding:10px;'
	   	,buttonAlign:'center'
	   	,items: [
		   	{
                xtype: 'fieldset',
                title: '',
                x: 10,
                y: 10,
                width: 760,
                height: 650,
                layout: 'absolute',
                id: 'fieldPrincipal',
                items: [
                    {
                        xtype: 'fieldset',
                        title: 'Pagos Pendientes',
                        x: 0,
                        y: 80,
                        height: 250,
                        layout: 'absolute',
                        width: 730,
                        id: 'fieldPagosPendientes',
                        items: [
                        	NS.gridPagosP,
                            {
                                xtype: 'label',
                                text: 'Total de Registros:',
                                x: 30,
                                y: 190
                            },
                            {
                                xtype: 'label',
                                text: 'Importe Total:',
                                x: 400,
                                y: 190
                            },
                            {
                                xtype: 'textfield',
                                x: 150,
                                y: 190,
                                width: 110,
                                name: PF + 'txtTotalRegistrosPP',
                                id: PF + 'txtTotalRegistrosPP',
                                value: '0'
                            },
                            {
                                xtype: 'textfield',
                                x: 500,
                                y: 190,
                                width: 180,
                                name: PF + 'txtImporteTotalPP',
                                id: PF + 'txtImporteTotalPP',
                                value: '0.0'
                            }
                        ]
                    },
                    {
                        xtype: 'fieldset',
                        title: 'Desglose de Pago',
                        x: 0,
                        y: 340,
                        width: 730,
                        height: 250,
                        layout: 'absolute',
                        id: 'fieldDesglosePago',
                        items: [
                        	NS.gridPagosPDetalle,
                            {
                                xtype: 'label',
                                text: 'Total de Registros:',
                                x: 30,
                                y: 190,
                                width: 120
                            },
                            {
                                xtype: 'label',
                                text: 'Importe Total:',
                                x: 400,
                                y: 190,
                                width: 110
                            },
                            {
                                xtype: 'textfield',
                                x: 150,
                                y: 190,
                                width: 110,
                                readOnly: true,
                                name: PF + 'txtTotalRegistrosDP',
                                id: PF + 'txtTotalRegistrosDP',
                                value: '0'
                            },
                            {
                                xtype: 'textfield',
                                x: 500,
                                y: 190,
                                width: 180,
                                readOnly: true,
                                name: PF + 'txtImporteTotalDP',
                                id: PF + 'txtImporteTotalDP',
                                value: '0.0'
                            }
                        ]
                    },
                    {
                        xtype: 'button',
                        text: 'Imprimir',
                        x: 530,
                        y: 600,
                        width: 80,
                        id: 'btnImprimir'
                    },
                    {
                        xtype: 'button',
                        text: 'Cerrar',
                        x: 640,
                        y: 600,
                        width: 80,
                        id: PF+'btnCerrar',
                        name: PF+'btnCerrar',
						listeners:{
							click:{
								fn:function(e){
									NS.limpiarWinPagPend();
									NS.buscar();
									winPagosPendientes.hide();
								}
							}
						}                        
                    },
                    {
                        xtype: 'label',
                        text: 'Empresa:',
                        x: 30,
                        y: 10
                    },
                    {
                        xtype: 'label',
                        text: 'Banco:',
                        x: 430,
                        y: 10
                    },
                    {
                        xtype: 'label',
                        text: 'Cuenta de Cheques:',
                        x: 430,
                        y: 50
                    },
                    {
                        xtype: 'textfield',
                        x: 100,
                        y: 10,
                        width: 250,
                        name: PF+'txtNomEmpresa',
                        id: PF+'txtNomEmpresa',
                        value: NS.nomEmpresaGrid,
                        readOnly : true
                    },
                    {
                        xtype: 'textfield',
                        x: 490,
                        y: 10,
                        width: 210,
                        name: PF+'txtNomBanco',
                        id: PF+'txtNomBanco',
                        value: NS.nomBancoGrid,
                        readOnly : true
                    },
                    {
                        xtype: 'textfield',
                        x: 560,
                        y: 50,
                        width: 140,
                        name: PF+'txtCtaCheques',
                        id: PF+'txtCtaCheques',
                        value: NS.noCuentaChequesGrid,
                        readOnly : true
                    }
                ]
		    }
		]
	});*/
	
/*	NS.ejecutarFondeoCheques = function(){//Funcion para realizar la accion de ejecutar de la pantalla fondeo de cheques 
    
		var matVencHoy = new Array();
		var regSelecGrigVencHoy = NS.gridFonVencHoy.getSelectionModel().getSelections();
		var allRegVencHoy = NS.storeFonVencHoy.data.items; 

		//Variables para los datos del grid de vencimientos anteriores
		var matVencAnt = new Array();
		var regSelecGrigVencAnt = NS.gridFonVencAnt.getSelectionModel().getSelections();
		var allRegVencAnt = NS.storeFonVencAnt.data.items; 
		
		if(regSelecGrigVencHoy.length == 0 && regSelecGrigVencAnt.length == 0)
		{
			Ext.Msg.alert('Informaci&oacute;n SET','Es necesario seleccionar algun registro');
			return;
		}
		BFwrk.Util.msgWait('Procesando fondeo, espere por favor...', true);
		for(var i = 0; i < allRegVencHoy.length; i = i+1)
        {
        	var regGridVencHoy = {};
        	for(var inc = 0; inc < regSelecGrigVencHoy.length; inc = inc + 1)
        	{
        		if(allRegVencHoy[i].get('noFolioDet') == regSelecGrigVencHoy[inc].get('noFolioDet'))
        		{
        			regGridVencHoy.seleccionado = true; 
        			break;
        		}
				else
				{
		    		regGridVencHoy.seleccionado = false;
		    	}
        	}
			      	
        	regGridVencHoy.folioFondeo = allRegVencHoy[i].get('folioFondeo');
	     	regGridVencHoy.noFolioDet = allRegVencHoy[i].get('noFolioDet');
	     	regGridVencHoy.fecValor = allRegVencHoy[i].get('fecValor');
	     	regGridVencHoy.fecEntregado = allRegVencHoy[i].get('fecEntregado');
	     	regGridVencHoy.beneficiario = allRegVencHoy[i].get('beneficiario');
	     	regGridVencHoy.importe = allRegVencHoy[i].get('importe');
	       	matVencHoy[i] = regGridVencHoy; 
        }

    	var jsonGridVencHoy = Ext.util.JSON.encode(matVencHoy);
    	
		for(var c = 0; c < allRegVencAnt.length; c = c+1)
        {
        	var regGridVencAnt = {};
        	for(var a = 0; a < regSelecGrigVencAnt.length; a = a + 1)
        	{
        		if(allRegVencAnt[c].get('noFolioDet') == regSelecGrigVencAnt[a].get('noFolioDet'))
        		{
        			regGridVencAnt.seleccionado = true; 
        			break;
        		}
				else
				{
		    		regGridVencAnt.seleccionado = false;
		    	}
        	} 
			      	
        	regGridVencAnt.folioFondeo = allRegVencAnt[i].get('folioFondeo');
	     	regGridVencAnt.noFolioDet = allRegVencAnt[i].get('noFolioDet');
	     	regGridVencAnt.fecValor = allRegVencAnt[i].get('fecValor');
	     	regGridVencAnt.fecEntregado = allRegVencAnt[i].get('fecEntregado');
	     	regGridVencAnt.beneficiario = allRegVencAnt[i].get('beneficiario');
	     	regGridVencAnt.importe = allRegVencAnt[i].get('importe');
	       	matVencAnt[i] = regGridVencAnt; 
        }

    	var jsonGridVencAnt = Ext.util.JSON.encode(matVencAnt);
			
    	CoinversionAction.ejecutarFondeoCheques(jsonGridVencHoy, jsonGridVencAnt
    		,NS.idBancoGrid, NS.noCuentaChequesGrid, function(result, e){
				BFwrk.Util.msgWait('Terminando...', false);
				if(result != null && result != '')
				{
					BFwrk.Util.msgShow('' + result.msgUsuario, 'INFO');
					NS.storeFonVencHoy.load();
					NS.storeFonVencAnt.load();
				}	
    	});
    }
    */
    
	//Ventana que contiene el formulario de pagos pendientes de fondeo
	var winPagosPendientes = new Ext.Window({
		title: 'Pagos Pendientes'
		,modal:true
		,shadow:true
		,width: 790
	   	,height: 700
	   	,minWidth: 400
	   	,minHeight: 580
	   	,layout: 'fit'
	   	,plain:true
	    ,bodyStyle:'padding:10px;'
	   	,buttonAlign:'center'
	   	,items: [
		   	{
                xtype: 'fieldset',
                title: '',
                x: 10,
                y: 10,
                width: 760,
                height: 650,
                layout: 'absolute',
                id: 'fieldPrincipalPP',
                items: [
                    {
                        xtype: 'fieldset',
                        title: 'Pagos Pendientes',
                        x: 0,
                        y: 80,
                        height: 250,
                        layout: 'absolute',
                        width: 730,
                        id: 'fieldPagosPendientes',
                        items: [
                        	NS.gridPagosP,
                            {
                                xtype: 'label',
                                text: 'Total de Registros:',
                                x: 30,
                                y: 190
                            },
                            {
                                xtype: 'label',
                                text: 'Importe Total:',
                                x: 400,
                                y: 190
                            },
                            {
                                xtype: 'textfield',
                                x: 150,
                                y: 190,
                                width: 110,
                                readOnly: true,
                                name: PF + 'txtTotalRegistrosPP',
                                id: PF + 'txtTotalRegistrosPP',
                                value: '0'
                            },
                            {
                                xtype: 'textfield',
                                x: 500,
                                y: 190,
                                width: 180,
                                readOnly: true,
                                name: PF + 'txtImporteTotalPP',
                                id: PF + 'txtImporteTotalPP',
                                value: '0.0'
                            }
                        ]
                    },
                    {
                        xtype: 'fieldset',
                        title: 'Desglose de Pago',
                        x: 0,
                        y: 340,
                        width: 730,
                        height: 250,
                        layout: 'absolute',
                        id: 'fieldDesglosePago',
                        items: [
                        	NS.gridPagosPDetalle,
                            {
                                xtype: 'label',
                                text: 'Total de Registros:',
                                x: 30,
                                y: 190,
                                width: 120
                            },
                            {
                                xtype: 'label',
                                text: 'Importe Total:',
                                x: 400,
                                y: 190,
                                width: 110
                            },
                            {
                                xtype: 'textfield',
                                x: 150,
                                y: 190,
                                width: 110,
                                readOnly: true,
                                name: PF + 'txtTotalRegistrosDP',
                                id: PF + 'txtTotalRegistrosDP',
                                value: '0'
                            },
                            {
                                xtype: 'textfield',
                                x: 500,
                                y: 190,
                                width: 180,
                                readOnly: true,
                                name: PF + 'txtImporteTotalDP',
                                id: PF + 'txtImporteTotalDP',
                                value: '0.0'
                            }
                        ]
                    },
                    {
                        xtype: 'button',
                        text: 'Imprimir',
                        disabled: true,
                        hidden: true,
                        x: 520,
                        y: 600,
                        width: 80,
                        id: PF+'btnImprimir'
                    },
                    {
                        xtype: 'button',
                        text: 'Cerrar',
                        x: 640,
                        y: 600,
                        width: 80,
                        id: PF+'btnCerrarPP',
                        name: PF+'btnCerrarPP',
						listeners:{
							click:{
								fn:function(e){
									NS.limpiarWinPagPend();
									NS.buscar();
									winPagosPendientes.hide();
								}
							}
						}                        
                    },
                    {
                        xtype: 'label',
                        text: 'Empresa:',
                        x: 30,
                        y: 10
                    },
                    {
                        xtype: 'label',
                        text: 'Banco:',
                 //       id: PF+'lblBanco',
                        x: 430,
                        y: 10
                    },
                    {
                        xtype: 'label',
                        text: 'Cuenta de Cheques:',
                        x: 430,
                        y: 50
                    },
                    {
                        xtype: 'textfield',
                        x: 100,
                        y: 10,
                        width: 250,
                        name: PF+'txtNomEmpresa',
                        id: PF+'txtNomEmpresa',
                        value: NS.nomEmpresaGrid,
                        readOnly : true
                    },
                    {
                        xtype: 'textfield',
                        x: 490,
                        y: 10,
                        width: 210,
                        name: PF+'txtNomBanco',
                        id: PF+'txtNomBanco',
                        value: NS.nomBancoGrid,
                        readOnly : true
                    },
                    {
                        xtype: 'textfield',
                        x: 560,
                        y: 50,
                        width: 140,
                        name: PF+'txtCtaCheques',
                        id: PF+'txtCtaCheques',
                        value: NS.noCuentaChequesGrid,
                        readOnly : true
                    }
                ]
		    }
		]
	});
	
	//Ventana que contiene el formulario de Fondeo Cheques
	var winFondeoCheques = new Ext.Window({
		title: 'Fondeo de Cheques'
		,modal:true
		,shadow:true
		,width: 790
	   	,height: 700
	   	,minWidth: 400
	   	,minHeight: 580
	   	,layout: 'fit'
	   	,plain:true
	    ,bodyStyle:'padding:10px;'
	   	,buttonAlign:'center'
	   	,items: [
		   	{
                xtype: 'fieldset',
                title: '',
                x: 10,
                y: 10,
                width: 760,
                height: 630,
                layout: 'absolute',
                id: 'fieldPrincipalCP',
                items: [
                    {
                        xtype: 'fieldset',
                        title: 'Vencimientos Hoy',
                        x: 0,
                        y: 60,
                        height: 250,
                        layout: 'absolute',
                        width: 730,
                        id: 'fieldVencimientosHoy',
                        items: [
                        	NS.gridFonVencHoy,
                            {
                                xtype: 'label',
                                text: 'Fondeos Pendientes',
                                x: 170,
                                y: 180
                            },
                            {
                                xtype: 'label',
                                text: 'Pagos a Fondear',
                                x: 170,
                                y: 200
                            },
                            {
                                xtype: 'label',
                                text: 'Montos de Día',
                                x: 420,
                                y: 180
                            },
                            {
                                xtype: 'label',
                                text: '0.0',
                                x: 520,
                                y: 180,
                                id: PF+'lblMontosDia',
                                name: PF+'lblMontosDia'
                                
                            },
                            {
                                xtype: 'label',
                                text: 'Montos a Fondear',
                                x: 420,
                                y: 200
                            },
                            {
                                xtype: 'label',
                                text: '0.0',
                                x: 520,
                                y: 200,
                                id: PF+'lblMontosFondear',
                                name: PF+'lblMontosFondear'
                            },
                            {
                                xtype: 'label',
                                text: '0',
                                x: 130,
                                y: 180,
                                id: PF + 'lblFonPenHoy',
                                name : PF + 'lblFonPenHoy'
                            },
                            {
                                xtype: 'label',
                                text: '0',
                                x: 130,
                                y: 200,
                                width: 10,
                                id: PF +'lblPagFonHoy',
                                name: PF +'lblPagFonHoy'
                            }
                        ]
                    },
                    {
                        xtype: 'fieldset',
                        title: 'Vencimientos Anteriores',
                        x: 0,
                        y: 320,
                        width: 730,
                        height: 250,
                        layout: 'absolute',
                        id: 'fieldVencimientosAnteriores',
                        items: [
                        	NS.gridFonVencAnt,
                            {
                                xtype: 'label',
                                text: 'Pagos a Fondear',
                                x: 170,
                                y: 200,
                                width: 110,
                                id: ''
                            },
                            {
                                xtype: 'label',
                                text: 'Fondeos Pendientes',
                                x: 170,
                                y: 180
                            },
                            {
                                xtype: 'label',
                                text: 'Montos a Fondear',
                                x: 420,
                                y: 200
                            },
                            {
                                xtype: 'label',
                                text: '0.0',
                                x: 520,
                                y: 180,
                                id: PF+'lblMontosFondearAnt',
                                name: PF+'lblMontosFondearAnt'
                            },
                            {
                                xtype: 'label',
                                text: 'Montos Anteriores',
                                x: 420,
                                y: 180
                            },
                            {
                                xtype: 'label',
                                text: '0.0',
                                x: 520,
                                y: 200,
                                id: PF+'lblMontosAnteriores',
                                name: PF+'lblMontosAnteriores'
                            },
                            {
                                xtype: 'label',
                                text: '0',
                                x: 130,
                                y: 180,
                                id: PF+'lblFonPenAnt',
                                name: PF+'lblFonPenAnt'
                            },
                            {
                                xtype: 'label',
                                text: '0',
                                x: 130,
                                y: 200,
                                id: PF+'lblPagFonAnt',
                                name: PF+'lblPagFonAnt'
                            }
                        ]
                    },
                    {
                        xtype: 'button',
                        text: 'Ejecutar',
                        x: 530,
                        y: 580,
                        width: 80,
                        id: PF+'btnEjecutar',
                        name: PF+'btnEjecutar',
                        listeners:{
	                        click:{
	                         fn: function(e){
	                         	NS.ejecutarFondeoCheques();
	                         }
	                      }
                        }
                    },
                    {
                        xtype: 'button',
                        text: 'Cerrar',
                        x: 640,
                        y: 580,
                        width: 80,
                        id: PF+'btnCerrarCP',
                        name: PF+'btnCerrarCP',
                        listeners:{
	                        click:{
	                         fn: function(e){
	                         	NS.limpiarWinPagosCheques();
	                         	NS.buscar();
	                         	winFondeoCheques.hide();
	                         }
	                      }
                        }
                    },
                    {
                        xtype: 'label',
                        text: 'Empresa:',
                        x: 30,
                        y: 30
                    },
                    {
                        xtype: 'label',
                        text: 'Cuenta de Cheques:',
                        x: 430,
                        y: 30
                    },
                    {
                        xtype: 'textfield',
                        x: 100,
                        y: 30,
                        width: 250,
                        name: PF+'txtNomEmpresaFC',
                        id: PF+'txtNomEmpresaFC'
                    },
                    {
                        xtype: 'textfield',
                        x: 560,
                        y: 30,
                        width: 140,
                        name: PF+'txtCtaChequesFC',
                        id: PF+'txtCtaChequesFC'
                    },
                    {
                        xtype: 'label',
                        text: 'Consulta/Fondeo de Obligaciones de Pago  (Cheques)',
                        x: 30,
                        y: -1
                    }
                ]
		    }
		]
	});
	NS.mostrarTipoSaldo = function(){
		GlobalAction.obtenerValorConfiguraSet(1, function(valor, e){
	              			   			
			if(valor!==null  &&  valor!= undefined)
			{
				if(valor === 'DALTON')
				{
					Ext.getCmp(PF+'cmbTipoSaldo').show();
					Ext.getCmp(PF+'tiposaldo').show();
					NS.storeTipoSaldo.load();
					//Ext.getCmp(PF+'chkFondear').show();
					//NS.mostrarFondeo = true;
				}
				else
				{
					Ext.getCmp(PF+'cmbTipoSaldo').hide();
					Ext.getCmp(PF+'tiposaldo').hide();
					//Ext.getCmp(PF+'chkFondear').hide();
					//NS.mostrarFondeo = false;
				}
			}
		});
	}
	NS.mostrarTipoSaldo();
	

	NS.FondeoAutomatico = new Ext.form.FormPanel ({
	    title: 'Fondeo Automático',
	    width: 1050,
	    height: 630,
	    padding: 10,
	    frame: true,
	    autoScroll: true,
	    layout: 'absolute',
	    id: PF+'contenedorFondeoAutomatico',
	    name: PF+'contenedorFondeoAutomatico',
	    renderTo: NS.tabContId,
	        items : [
	            {
	                xtype: 'fieldset',
	                title: '',
	                x: 10,
	                y: 10,
	                width: 1040,
	                height: 580,
	                layout: 'absolute',
	                id: 'contPrincipal',
	                items: [
	                    {
	                        xtype: 'fieldset',
	                        title: 'Concentradora',
	                        x: 0,
	                        y: 0,
	                        height: 160,
	                        layout: 'absolute',
	                        width: 1000,
	                        id: 'contBusqueda',
	                        items: [
		                        	{
	                                xtype: 'fieldset',
	                                title: '',
	                                x: -20,
	                                y: -20,
	                             //   layout: 'absolute',
	                                width: 0,
	                                height: 0,
	                                items: [
	                               	{
		                                xtype: 'radiogroup',
							            columns: 2,
							            
							            items: [
		                                    {
		                                        xtype: 'radio',
		                                        name: PF+'optTipoBusqueda',
		                                        id:PF+'optTipoBusqueda1',
		                        				inputValue: 0,
		                        				hidden: true,
		                                        x: 9,
		                                        y: -15,
		                                        boxLabel: 'Clase Igual',
		                                        listeners:{
													check:{
														fn:function(opt, valor)
														{
															if(valor==true){
																NS.tipoBusqueda = 'I'
															}
														}
													}
												}
		                                    },
		                                    {
		                                        xtype: 'radio',
		                                        name: PF+'optTipoBusqueda',
		                                        id:PF+'optTipoBusqueda2',
		                        				inputValue: 1,
		                                        x: 80,
		                                        y: -10,
		                                        boxLabel: 'Clase Diferente',
		                                        hidden: true,
		                                      //checked: true,
		                                        listeners:{
													check:{
														fn:function(opt, valor)
														{
															if(valor==true){
																NS.tipoBusqueda = 'D'
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
	                                xtype: 'label',
	                                text: 'Empresa:',
	                                x: 20,
	                                y: 0,
	                                id: PF+'lblEmpresa',
	                                name: PF+'lblEmpresa',
	                                hidden : true
	                            },
	                            {
	                                xtype: 'label',
	                                text: 'Divisa:',
	                                x: 430,
	                                y: 20
	                            },
	                            {
	                                xtype: 'label',
	                                text: 'Banco:',
	                                id: PF+'lblBanco',
	                                x: 40,
	                                y: 60
	                            },
	                            {
	                                xtype: 'label',
	                                text: 'Árbol:',
	                                x: 40,
	                                y: 20,
	                                id: PF+'lblArbol',
	                                name: PF+'lblArbol'
	                            },
	                            {
	                                xtype: 'textfield',
	                                x: 20,
	                                y: 20,
	                                width: 40,
	                                name: PF+'txtIdEmpresa',
	                                id: PF+'txtIdEmpresa',
	                                hidden: true,
	                                listeners : {
	                                	change : {
	                                		fn: function(caja, valor){
	                                			if(caja.getValue() != null && caja.getValue() != undefined && caja.getValue() != '')
                        							var valueCombo = BFwrk.Util.updateTextFieldToCombo(PF+'txtIdEmpresa',NS.cmbEmpresasConcentradoras.getId());
                        						if(valueCombo != null && valueCombo != undefined && valueCombo != '')
                        							NS.accionarEmpresasConcentradoras(valueCombo);
                        						
                        						
	                                		}
	                                	}
	                                }
	                            },
	                           	NS.cmbEmpresasConcentradoras,
	                            NS.cmbDivisa,
	                            NS.cmbBanco,
	                             NS.cmbBancoRaiz,
	                             
	                            {
	                                xtype: 'textfield',
	                                x: 90,
	                                y: 20,
	                                width: 40,
	                                name: PF+'txtIdArbol',
	                                id: PF+'txtIdArbol',
	                                listeners: {
	                                change:{
	                                		fn:function(caja, valor){
	                                			if(caja.getValue() != null && caja.getValue() != undefined && caja.getValue() != ''){
		                                				for(var i=0; i<NS.storeArbol.data.length; i++){
		                                					if(NS.storeArbol.data.items[i].get('id')==valor){
		                                						NS.cmbArbol.setValue(NS.storeArbol.data.items[i].get('descripcion'))
		                                						NS.accionarArbol(NS.cmbArbol.getValue());
		                                					}
		                                				}
	                                			}

	                                				//var id=NS.storeArbol.getById(valor).get('id');
                        							
	                                				//var valueCombo = BFwrk.Util.updateTextFieldToCombo(PF+'txtIdArbol',NS.cmbArbol.getId());
                        						//if(valueCombo != null && valueCombo != undefined && valueCombo != '')
                        							
	                                		} 
	                                	}
	                                }
	                            },
	                            NS.cmbArbol,
	                            {
	                                xtype: 'button',
	                                text: 'Buscar',
	                                x: 750,
	                                y: 60,
	                                width: 80,
	                                id: PF+'cmdBuscar',
	                                name: PF+'cmdBuscar',
	                                listeners: {
	                                	click:{
	                                		fn : function(e){
	                                			GlobalAction.obtenerValorConfiguraSet(1, function(valor, e){
	                                		    	if(valor!==null  &&  valor!= undefined)
	                                				{
	                                					if(valor === 'DALTON')
	                                					{	
	                                					  if(Ext.getCmp(PF+'cmbTipoSaldo').getValue()=='')
	                                						{
	                                						  Ext.Msg.alert('Información SET','Debe seleccionar el tipo de saldo');
	                                					        return;
	                                						}
	                                					  else
	                                						  {
	                                						   NS.buscar();
	                                						  }
	                                			
	                                					}
	                                					else
	                                						{
	                                						NS.buscar();
	                                						}
	                             
	                                				}
	                                				});
	                                		 
	                                		}
	                                	}
	                                } 
	                            },
	                            {
	                                xtype: 'uppertextfield',
	                                x: 470,
	                                y: 20,
	                                width: 40,
	                                name: PF+'txtIdDivisa',
	                                id: PF+'txtIdDivisa',
	                                listeners : {
	                                	change : {
	                                		fn: function(caja, valor){
	                                			if(caja.getValue() != null && caja.getValue() != undefined && caja.getValue() != '')
                        							var valueCombo = BFwrk.Util.updateTextFieldToCombo(PF+'txtIdDivisa',NS.cmbDivisa.getId());
                        						if(valueCombo != null && valueCombo != undefined && valueCombo != ''){
                        							NS.accionarDivisa(valueCombo);
                        						}else{
                        							Ext.Msg.alert('SET','Id. de Divisa no valido.');  
                        							caja.reset();
                        							NS.cmbDivisa.reset();
                        							NS.cmbBancoRaiz.reset();
                        							NS.cmbChequera.reset();
                        							Ext.getCmp(PF+'txtIdBanco').reset();
                        							return;
                        						}
	                                		}
	                                	}
	                                }
	                            },
	                            {
	                                xtype: 'textfield',
	                                x: 90,
	                                y: 60,
	                                width: 40,
	                                name: PF+'txtIdBanco',
	                                id: PF+'txtIdBanco',
	                                listeners:{
	                                	change:{
	                                		fn:function(caja, valor){
		                                		if(caja.getValue() != null && caja.getValue() != undefined && caja.getValue() != '')
	                       							var valueCombo = BFwrk.Util.updateTextFieldToCombo(PF+'txtIdBanco',NS.cmbBancoRaiz.getId());
	                   							if(valueCombo != null && valueCombo != undefined && valueCombo != ''){
		                                			NS.accionarBancoRaiz(valueCombo);
		                                		}else{
		                                			Ext.Msg.alert('SET','Id. de Banco no valido');
		                                			caja.reset();
		                                			NS.cmbBancoRaiz.reset();
		                                			NS.cmbChequera.reset();
		                                			return;
		                                		}	
	                                		}
	                                	}
	                                }
	                            },
	                            {
	                                xtype: 'label',
	                                text: 'Chequera:',
	                                x: 450,
	                                y: 60,
	                                hidden:true,
	                                id: PF+'lblChequera',
	                                name: PF+'lblChequera'
	                            },
	                            NS.cmbChequera,
	                            {
	                                xtype: 'checkbox',
	                                x: 320,
	                                y: 60,
	                                boxLabel: 'Mismo Banco',
	                                id: PF+'checkMismoBanco',
	                                name:PF+'checkMismoBanco',
	                                listeners: {
	                                	check:{
	                                		fn: function(check){
	                                			if(check.getValue())
	                                				Ext.getCmp(PF+'cmbBanco').setDisabled(false);
	                                			else
	                                				Ext.getCmp(PF+'cmbBanco').setDisabled(true);
	                                		}
	                                	}
	                                }
	                            },
	                            {
	                                xtype: 'button',
	                                text: 'Imprimir',
	                                x: 750,
	                                y: 20,
	                                width: 80,
	                                id: PF+'cmbImprimir',
	                                listeners:{
	                                	click:{
	                                		fn: function(e){
	                                			NS.imprimirArbol();
	                                		}
	                                	}
	                                }
	                            },
	                            {
	                                xtype: 'label',
	                                text: 'Tipo Saldo:',
	                                name: PF+'tiposaldo',
	                        		id: PF+'tiposaldo',
	                                x: 40,
	                                y: 105
	                            },
	                            NS.cmbTipoSaldo
	                            
	                        ]
	                    },
	                    {
	                        xtype: 'fieldset',
	                        title: 'Empresa',
	                        x: 0,
	                        y: 180,
	                        width: 1000,
	                        height: 300,
	                        layout: 'absolute',
	                        id: 'contGrid',
	                        items: [
	                        	NS.gridFondeos,
	                            {
	                                xtype: 'label',
	                                text: 'Total del Fondeo',
	                                x: 30,
	                                y: 210,
	                                width: 80
	                            },
	                            {
	                                xtype: 'label',
	                                text: 'Total de registros   seleccionados',
	                                x: 240,
	                                y: 210,
	                                width: 110
	                            },
	                            {
	                                xtype: 'textfield',
	                                x: 30,
	                                y: 240,
	                                width: 180,
	                                readOnly: true,
	                                id: PF + 'txtTotalTraspaso',
	                                name: PF + 'txtTotalTraspaso',
	                                value:'0.0'
	                            },
	                            {
	                                xtype: 'textfield',
	                                x: 240,
	                                y: 240,
	                                width: 140,
	                                readOnly: true,
	                                id: PF+'txtTotalRegistros',
	                                name: PF+'txtTotalRegistros',
	                                value:'0'
	                            },/*{
	    	                        xtype: 'button',
	    	                        text: 'Imprimir',
	    	                        x: 650,
	    	                        y: 240,
	    	                        width: 80,
	    	                        id: 'cmdImprimirSeg'
	    	                    },*/
	    	                    {
	    	                        xtype: 'button',
	    	                        text: 'Limpiar',
	    	                        x: 850,
	    	                        y: 240,
	    	                        width: 80,
	    	                        id: PF+'cmdLimpiar',
	    	                        name:PF+'cmdLimpiar',
	    	                        listeners:{
	    	                        	click:{
	    	                        		fn: function(){
	    	                        			NS.limpiar();
	    	                        		}
	    	                        	}
	    	                        }
	    	                    },
	    	                    {
	    	                        xtype: 'button',
	    	                        text: 'Ejecutar Fondeo',
	    	                        x: 750,
	    	                        y: 240,
	    	                        width: 80,
	    	                        id: PF + 'cmbEjecutar',
	    	                        name: PF + 'cmbEjecutar',
	    	                        listeners : {
	    		                        	click : {
	    		                        		fn : function(){
	    		                        			NS.ejecutar();
	    		                        		}
	    		                        	}
	    	                        }
	    	                    }
	                        ]
	                    },
	                   /* {
	                        xtype: 'button',
	                        text: 'Imprimir',
	                        x: 700,
	                        y: 480,
	                        width: 80,
	                        id: 'cmdImprimirSeg'
	                    },
	                    {
	                        xtype: 'button',
	                        text: 'Limpiar',
	                        x: 900,
	                        y: 480,
	                        width: 80,
	                        id: PF+'cmdLimpiar',
	                        name:PF+'cmdLimpiar',
	                        listeners:{
	                        	click:{
	                        		fn: function(){
	                        			NS.limpiar();
	                        		}
	                        	}
	                        }
	                    },
	                    {
	                        xtype: 'button',
	                        text: 'Ejecutar Fondeo',
	                        x: 800,
	                        y: 480,
	                        width: 80,
	                        id: PF + 'cmbEjecutar',
	                        name: PF + 'cmbEjecutar',
	                        listeners : {
		                        	click : {
		                        		fn : function(){
		                        			NS.ejecutar();
		                        		}
		                        	}
	                        }
	                    },*/{
	                    	xtype: 'textfield',
	                    	x:0,
	                    	y:0,
	                    	hidden:true,
	                    	name:'hdJasonBF',
	                    	id:'hdJasonBF',
	                    	value: 'value from hidden field'
	                    },
	                    {
	                    	xtype: 'textfield',
	                    	x:0,
	                    	y:0,
	                    	hidden:true,
	                    	name:'noEmpresa',
	                    	id:'noEmpresa',
	                    	value: 'value from hidden field'
	                    },{
	                    	xtype: 'textfield',
	                    	x:0,
	                    	y:0,
	                    	hidden:true,
	                    	name: 'nomReporte',
	                    	id: 'nomReporte',
	                    	value: 'ReporteFondeoBarrido'
	                    },{
	                    	xtype: 'textfield',
	                    	x:0,
	                    	y:0,
	                    	hidden: true,
	                    	name: 'idUsuario',
	                    	id: 'idUsuario',
	                    	value: NS.idUsuario
	                    	
	                    },{
	                    	xtype: 'textfield',
	                    	x:0,
	                    	y:0,
	                    	hidden: true,
	                    	name: 'EMPRESA',
	                    	id: 'EMPRESA',
	                    	value: ''
	                    },{
	                    	xtype: 'textfield',
	                    	x:0,
	                    	y:0,
	                    	hidden: true,
	                    	name: 'PREFIJO',
	                    	id: 'PREFIJO',
	                    	value: PF
	                    }
	                    
	                ]
	            }
	        ] 
	});
	 NS.FondeoAutomatico.setSize(Ext.get(NS.tabContId).getWidth(), Ext.get(NS.tabContId).getHeight());
	 //staticCheck("div[id*='gridFondeos'] div div[class='x-panel-ml']","div[id*='gridFondeos'] div div[class='x-panel-ml']",8,".x-grid3-scroller",false);
	 
});

