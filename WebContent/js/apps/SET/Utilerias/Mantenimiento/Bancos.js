Ext.onReady(function()
{
	var NS = Ext.namespace('apps.SET.Utilerias.Mantenimiento.Bancos');
	NS.tabContId = apps.SET.tabContainerId;
	NS.idUsuario = apps.SET.iUserId;
	NS.noEmpresa = apps.SET.noEmpresa;
	NS.fecHoy = apps.SET.FEC_HOY ;//BFwrk.Util.changeStringToDate(apps.SET.FEC_HOY);
	var PF = apps.SET.tabID + '.';
	Ext.Direct.addProvider(Ext.setapp.REMOTING_API);
	Ext.QuickTips.init();
	NS.tipoBanco = 0;
	NS.idBanco = 0;
	NS.aba = 0;
	NS.descBanco = 0;
	NS.modificar = false;
	NS.indicador = 'N';
	/* FALTANTES
	 * ********Falta facultad en el form_load
	 * If (Not (gobjSeguridad.ValidaFacultad("CRELBAN", sMensaje))) Then
        CmdNuevo.Visible = False
        CmdBorrar.Visible = False
    *  End If
	 * 
	 */

	//LABEL
	// LABEL PARA LA BUSQUEDA
	//Label para el banco de busqueda
	NS.lblBanco = new Ext.form.Label({
		text: 'Banco',
		x: 200,
		y: 0
	});

	//Label para campo de busqueda de ABA
	NS.lblAba = new Ext.form.Label({
		id: PF + 'lblAba',
		name: PF + 'lblAba',
		text: 'ABA',
		x: 500,
		y: 0,
		hidden: true
	});
	
	//LABEL PARA EL INSERT
	NS.lblBancoInserta = new Ext.form.Label({
		text: 'Banco',
		x: 0,
		y: 0
	});
	
	NS.lblIdBancoAMB = new Ext.form.Label({
		id: PF + 'lblIdBancoAMB',
		name: PF + 'lblIdBancoAMB',
		text: 'No. Banco AMB',
		x: 0,
		y: 45
	});
	
	NS.lblPrefijoInstitucion = new Ext.form.Label({
		id: PF + 'lblPrefijo',
		name: PF + 'lblPrefijo',
		text: 'Prefijo Institución',
		x: 100,
		y: 45
	});

	NS.lblBancaElectronica = new Ext.form.Label({
		text: 'Banca Electronica',
		x: 0,
		y: 90
	});
	
	NS.lblDescripcionInbursa = new Ext.form.Label({
		id: PF + 'lblDescripcionInbursa',
		name: PF + 'lblDescripcionInbursa',
		text: 'Descripción',
		x: 0,
		y: 135
	});
	
	NS.lblClavePlaza = new Ext.form.Label({
		id: PF + 'lblClavePlaza',
		name: PF + 'lblClavePlaza',
		text: 'Clave Plaza',		
		x: 240,
		y: 45,
		disabled: true
	});
	
	NS.lblHoraSBC = new Ext.form.Label({
		id: PF + 'lblHora',
		name: PF + 'lblHora',
		text: 'Hora SBC T + 2',
		x: 240,
		y: 135,
		disabled: true
	});
	
	NS.lblSaldoBanco = new Ext.form.Label({
		text: 'Saldo Banco',
		x: 340,
		y: 135
	});	

	
	
	NS.lblPathArchivo = new Ext.form.Label({
		id: PF + 'lblPathArchivo',
		name: PF + 'lblPathArchivo',
		text: 'Path de Archivo del Cheque Protegido',
		x: 520,
		y: 45,
		disabled: true
	});
	
	NS.lblCodigoSeguridad = new Ext.form.Label({
		id: PF + 'lblCodigoSeguridad',
		name: PF + 'lblCodigoSeguridad',
		text: 'Codigo de Seguridad',
		x: 520,
		y: 90,
		disabled: true
	});
	
	NS.lblClaveArchivo = new Ext.form.Label({
		id: PF + 'lblClaveArchivo',
		name: PF + 'lblClaveArchivo',
		text: 'Clave del Archivo',
		x: 650,
		y: 90,
		disabled: true
	});
	
	NS.lblFechaArchivo = new Ext.form.Label({
		id: PF + 'lblFechaArchivo',
		name: PF + 'lblFechaArchivo',
		text: 'Dia y Mes del Archivo',
		x: 520,
		y: 135,
		disabled: true
	});
	
	NS.lblConsecutivoArchivo = new Ext.form.Label({
		id: PF + 'lblConsecutivoArchivo',
		name: PF + 'lblConsecutivoArchivo',
		text: 'Consecutivo del Archivo',
		x: 670,
		y: 135,
		disabled: true
	});
	
	NS.lblPlazaExt = new Ext.form.Label({
		id: PF + 'lblPlazaExt',
		name: PF + 'lblPlazaExt',
		text: 'Direccion',
		x: 240, 
		y: 45,
		hidden: true
	});
	
	//TEXTFIELD
	NS.txtBanco = {
		xtype: 'uppertextfield',
		id: PF + 'txtBanco',
		name: PF + 'txtBanco',
		x: 200,
		y: 15,
		width: 150,
		tabIndex: 0
		
	};
	
	
	NS.txtAba = new Ext.form.TextField({
		id: PF + 'txtAba',
		name: PF + 'txtAba',
		x: 500,
		y: 15,
		hidden: true,
		width: 150
	});
	
	//TextField para el insert
	NS.txtIdBancoInserta = {
		xtype: 'uppertextfield',
		id: PF + 'txtIdBancoInserta',
		name: PF + 'txtIdBancoInserta',
		x: 0,
		y: 15,
		width: 50, 
		tabIndex: 2
	};
	
	NS.txtBancoInserta = {
		xtype: 'uppertextfield',
		id: PF + 'txtBancoInserta',
		name: PF + 'txtBancoInserta',
		x: 60,
		y: 15,
		width: 150,
		tabIndex: 3
	};
	
	NS.txtBancoAMB = {
		xtype: 'uppertextfield',
		id: PF + 'txtBancoAMB',
		name: PF + 'txtBancoAMB',
		x: 0,
		y: 60,
		width: 80, 
		tabIndex: 4
	};
	
	NS.txtPrefijo = {
		xtype: 'uppertextfield',
		id: PF + 'txtPrefijo',
		name: PF + 'txtPrefijo',
		x: 100,
		y: 60,
		width: 80,
		tabIndex: 5
	};
	

	NS.txtDescripcionInbursa = {
		xtype: 'uppertextfield',
		id: PF + 'txtDescripcionInbursa',
		name: PF + 'txtDescripcionInbursa',
		x: 0,
		y: 150,
		width: 210,
		tabIndex: 7
	};
	
	NS.txtClavePlaza = {
		xtype: 'uppertextfield',
		id: PF + 'txtClavePlaza',
		name: PF + 'txtClavePlaza',
		x: 240,
		y: 60,
		width: 210,
		disabled: true,
		tabIndex: 9
	};
	
	NS.txtHora = new Ext.form.TextField({
		id: PF + 'txtHora',
		name: PF + 'txtHora',
		x: 240,
		y: 150,
		width: 80,
		disabled: true,
		tabIndex: 11
	});
	
	NS.txtPathChequeProtegido = new Ext.form.TextField({
		id: PF + 'txtPathChequeProtegido',
		name: PF + 'txtPathChequeProtegido',
		x: 520,
		y: 60,
		width: 210, 
		disabled: true,
		tabIndex: 14
	});
	
	NS.txtCodigoSeguridad = new Ext.form.TextField({
		id: PF + 'txtCodigoSeguridad',
		name: PF + 'txtCodigoSeguridad',
		x: 520,
		y: 105,
		width: 50, 
		disabled: true,
		tabIndex: 15
	});
	
	NS.txtClaveArchivo = new Ext.form.TextField({
		id: PF + 'txtClaveArchivo',
		name: PF + 'txtClaveArchivo',
		x: 650,
		y: 105,
		width: 80, 
		disabled: true,
		tabIndex: 16
	});
	
	NS.txtFechaArchivo = new Ext.form.TextField({
		id: PF + 'txtFechaArchivo',
		name: PF + 'txtFechaArchivo',
		x: 520,
		y: 150, 
		disabled: true,
		tabIndex: 17
	});
	
	NS.txtConsecutivo = new Ext.form.TextField({
		id: PF + 'txtConsecutivo',
		name: PF + 'txtConsecutivo',
		x: 670,
		y: 150,
		disabled: true,
		tabIndex: 18
	});
	
	NS.txtPlazaExt = {
		xtype: 'uppertextfield',
		id: PF + 'txtPlazaExt',
		name: PF + 'txtPlazaExt',
		x: 240,
		y: 60,
		width: 210,
		hidden: true
	};
	
	NS.txtAbaSwift = new Ext.form.TextField({
		id: PF + 'txtAbaSwift',
		name: PF + 'txtAbaSwift',
		x: 70,
		y: 12,
		width: 150		
	});
	
	
	//FUNCIONES
	//Hace la consulta de los bancos y hace la llamada para llenar el grid
	NS.buscar = function(){
		NS.descBanco = Ext.getCmp(PF + 'txtBanco').getValue();
		NS.aba = Ext.getCmp(PF + 'txtAba').getValue();
				
		NS.storeLlenaGrid.baseParams.descBanco = NS.descBanco;
		NS.storeLlenaGrid.baseParams.aba = NS.aba;
		NS.storeLlenaGrid.baseParams.tipoBanco = NS.tipoBanco;
		NS.storeLlenaGrid.load();	
		
		
	};
	
	//Cuando selecciona un registro del grid llena los campos
	NS.llenaDatos = function(){
		var registroSeleccionado = NS.gridDatos.getSelectionModel().getSelections();
		
		if (registroSeleccionado <= 0)
			Ext.Msg.alert('SET', 'Debe de seleccionar al menos un registro');
		else
		{
			Ext.getCmp(PF + 'txtIdBancoInserta').setValue(registroSeleccionado[0].get('idBanco'));
			Ext.getCmp(PF + 'txtBancoInserta').setValue(registroSeleccionado[0].get('descripcion'));
			Ext.getCmp(PF + 'txtBancoAMB').setValue(registroSeleccionado[0].get('idBancoCecoban'));
			Ext.getCmp(PF + 'txtPrefijo').setValue(registroSeleccionado[0].get('prefInstitucion'));
						
			if (registroSeleccionado[0].get('bancaElect') == ('I'))
				Ext.getCmp(PF + 'cmbBancaElectronica').setValue("IMPORTACION");
			else if (registroSeleccionado[0].get('bancaElect') == ('E'))
				Ext.getCmp(PF + 'cmbBancaElectronica').setValue("EXPORTACION");
			else if (registroSeleccionado[0].get('bancaElect') == ('N'))
				Ext.getCmp(PF + 'cmbBancaElectronica').setValue("NINGUNA");
			else if (registroSeleccionado[0].get('bancaElect') == ('A'))
				Ext.getCmp(PF + 'cmbBancaElectronica').setValue("AMBAS");
	
			
			Ext.getCmp(PF + 'txtClavePlaza').setValue(registroSeleccionado[0].get('clavePlaza'));
			Ext.getCmp(PF + 'txtHora').setValue(registroSeleccionado[0].get('horaLiberacion'));
			Ext.getCmp(PF + 'cmbSaldoBanco').setValue(registroSeleccionado[0].get('saldoBanco'));
			Ext.getCmp(PF + 'txtDescripcionInbursa').setValue(registroSeleccionado[0].get('descripcionInbursa'));
			Ext.getCmp(PF + 'txtPathChequeProtegido').setValue(registroSeleccionado[0].get('pathArchivo'));
			Ext.getCmp(PF + 'txtCodigoSeguridad').setValue(registroSeleccionado[0].get('codigoSeguridad'));
			Ext.getCmp(PF + 'txtClaveArchivo').setValue(registroSeleccionado[0].get('archivoProtegido'));
			Ext.getCmp(PF + 'txtFechaArchivo').setValue(registroSeleccionado[0].get('fechaProtegido'));
			Ext.getCmp(PF + 'txtConsecutivo').setValue(registroSeleccionado[0].get('noProtegido'));
			Ext.getCmp(PF + 'txtPlazaExt').setValue(registroSeleccionado[0].get('plazaExtranjera'));
			
			if (registroSeleccionado[0].get('liberacionAutomatica') == 'S')
				Ext.getCmp(PF + 'chkLiberacionAutomatica').setValue(true);
			else
				Ext.getCmp(PF + 'chkLiberacionAutomatica').setValue(false);
			
			if (registroSeleccionado[0].get('layoutComerica') == 'S')
				Ext.getCmp(PF + 'chkLayoutComerica').setValue(true);
			else
				Ext.getCmp(PF + 'chkLayoutComerica').setValue(false);
			
			if (registroSeleccionado[0].get('validacionClabe') == 'S')
				Ext.getCmp(PF + 'chkValidaClabe').setValue(true);
			else
				Ext.getCmp(PF + 'chkValidaClabe').setValue(false);
			
			if (registroSeleccionado[0].get('bChequeElect') == 'S')
				Ext.getCmp(PF + 'chkChequeElectronico').setValue(true);
			else
				Ext.getCmp(PF + 'chkChequeElectronico').setValue(false);
			
			
			if (registroSeleccionado[0].get('bProtegido') == 'S')
				Ext.getCmp(PF + 'chkChequeProtegido').setValue(true);
			else
				Ext.getCmp(PF + 'chkChequeProtegido').setValue(false);
			
			
			if (registroSeleccionado[0].get('chequeOcurre') == 'S')
				Ext.getCmp(PF + 'chkChequeOcurre').setValue(true);
			else
				Ext.getCmp(PF + 'chkChequeOcurre').setValue(false);
			
			if (registroSeleccionado[0].get('transferPorBanco') == 'S')
				Ext.getCmp(PF + 'chkTratarPorBanco').setValue(true);
			else
				Ext.getCmp(PF + 'chkTratarPorBanco').setValue(false);			
		}			
	};	
	
	//Limpia la pantalla
	NS.limpiar = function(){
		Ext.getCmp(PF + 'txtIdBancoInserta').setValue('');
		Ext.getCmp(PF + 'txtBancoInserta').setValue('');
		Ext.getCmp(PF + 'txtBancoAMB').setValue('');
		Ext.getCmp(PF + 'txtPrefijo').setValue('');					
		NS.cmbBancaElectronica.reset();		
		Ext.getCmp(PF + 'txtClavePlaza').setValue('');
		Ext.getCmp(PF + 'txtHora').setValue('00:00');
		NS.cmbSaldoBanco.reset();		
		Ext.getCmp(PF + 'txtDescripcionInbursa').setValue('');
		Ext.getCmp(PF + 'txtPathChequeProtegido').setValue('');
		Ext.getCmp(PF + 'txtCodigoSeguridad').setValue('');
		Ext.getCmp(PF + 'txtClaveArchivo').setValue('');
		Ext.getCmp(PF + 'txtFechaArchivo').setValue('');
		Ext.getCmp(PF + 'txtConsecutivo').setValue('');
		Ext.getCmp(PF + 'chkLiberacionAutomatica').setValue(false);
		Ext.getCmp(PF + 'chkLayoutComerica').setValue(false);
		Ext.getCmp(PF + 'chkValidaClabe').setValue(false);
		Ext.getCmp(PF + 'chkChequeElectronico').setValue(false);
		Ext.getCmp(PF + 'chkChequeProtegido').setValue(false);
		Ext.getCmp(PF + 'chkChequeOcurre').setValue(false);	
		Ext.getCmp(PF + 'chkTratarPorBanco').setValue(false);
		Ext.getCmp(PF + 'txtPlazaExt').setValue('');
		NS.gridDatos.store.removeAll();
		NS.gridDatos.getView().refresh();	
		Ext.getCmp(PF + 'txtAba').setValue('');
		Ext.getCmp(PF + 'txtBanco').setValue('');
		
	};
	
	//Funcion para insertar y modificar un banco
	NS.insertaModifica = function(){	
		var registro = NS.gridDatos.getSelectionModel().getSelections();		
		var vector = {};
		var matriz = new Array();
		
		vector.idBancoInserta = Ext.getCmp(PF + 'txtIdBancoInserta').getValue();
		vector.descBancoInserta = Ext.getCmp(PF + 'txtBancoInserta').getValue();
		vector.idBancoAMB = Ext.getCmp(PF + 'txtBancoAMB').getValue();
		vector.prefijo = Ext.getCmp(PF + 'txtPrefijo').getValue();		
		vector.bancaElectronica = Ext.getCmp(PF + 'cmbBancaElectronica').getValue();
		vector.clavePlaza = Ext.getCmp(PF + 'txtClavePlaza').getValue();		
		vector.hora = Ext.getCmp(PF + 'txtHora').getValue();
		vector.saldoBanco = Ext.getCmp(PF + 'cmbSaldoBanco').getValue();		
		vector.descripcionInbursa = Ext.getCmp(PF + 'txtDescripcionInbursa').getValue();
		vector.pathArchivo = Ext.getCmp(PF + 'txtPathChequeProtegido').getValue();
		vector.codigoSeguridad = Ext.getCmp(PF + 'txtCodigoSeguridad').getValue();
		vector.claveArchivo = Ext.getCmp(PF + 'txtClaveArchivo').getValue();
		vector.fechaArchivo = Ext.getCmp(PF + 'txtFechaArchivo').getValue();
		vector.consecutivo = Ext.getCmp(PF + 'txtConsecutivo').getValue();
		vector.plazaExtranjera = Ext.getCmp(PF + 'txtPlazaExt').getValue();
		
		if (Ext.getCmp(PF + 'chkLiberacionAutomatica').getValue(true))
			vector.liberacionAutomatica = 'S';
		else
			vector.liberacionAutomatica = 'N';
		
		if (Ext.getCmp(PF + 'chkLayoutComerica').getValue(true))
			vector.layoutComerica = 'S';
		else
			vector.layoutComerica = 'N';
		
		if (Ext.getCmp(PF + 'chkValidaClabe').getValue(true)) 
			vector.validaClabe = 'S';
		else
			vector.validaClabe = 'N';
			
		if (Ext.getCmp(PF + 'chkChequeElectronico').getValue(true))
			vector.chequeElectronico = 'S';
		else
			vector.chequeElectronico = 'N';
		
		if (Ext.getCmp(PF + 'chkChequeProtegido').getValue(true))
			vector.chequeProtegido = 'S';
		else
			vector.chequeProtegido = 'N';
		
		if (Ext.getCmp(PF + 'chkChequeOcurre').getValue(true))
			vector.chequeOcurre = 'S';
		else
			vector.chequeOcurre = 'N';
		
		if (Ext.getCmp(PF + 'chkTratarPorBanco').getValue(true))
			vector.tratarPorBanco = 'S';
		else
			vector.tratarPorBanco = 'N';
		
		vector.tipoBanco = NS.tipoBanco;
		
		if (NS.modificar)
			vector.modificar = 'modificar';
		else
			vector.modificar = 'insertar';
			
		vector.fecHoy = NS.fecHoy;
		vector.idUsuario = NS.idUsuario;
				
		matriz[0] = vector;
		
		var jSonString = Ext.util.JSON.encode(matriz);
		
		if (vector.saldoBanco==0) {
			Ext.Msg.confirm('SET', 'Seleccione el saldo del banco por favor');
		} else {

		
		
		if (NS.modificar==true){ //Si esto se cumple es para modificar si no es para insertar nuevo registro
			Ext.Msg.confirm('SET', '¿Esta seguro de aplicar los cambios al banco seleccionado?', function(btn){
				if (btn === 'yes'){
						MantenimientoBancosAction.aceptar(jSonString, function(resultado, e){
						if (resultado != 0 && resultado != '' && resultado != undefined)
						{
							Ext.Msg.alert('SET', resultado);
							
							NS.buscar();
						}
					});
				}					
			});
		}
		else{
			MantenimientoBancosAction.aceptar(jSonString, function(resultado, e){
				if (resultado != 0 && resultado != '' && resultado != undefined)
				{
					Ext.Msg.alert('SET', resultado);
					//NS.limpiar();    				
					NS.buscar();
				}
			});		
		}
		
		}
	
			
	};
	
	//Elimina el banco seleccionado
	NS.eliminar = function(){
		var registroSeleccionado = NS.gridDatos.getSelectionModel().getSelections();
		
		if (registroSeleccionado <= 0)
			Ext.Msg.alert('SET', 'Debe de seleccionar un registro');
		else{					    		
			MantenimientoBancosAction.validaCuentasAsignadas(parseInt(registroSeleccionado[0].get('idBanco')), function(resultado, e){
				if (resultado != 0 && resultado != '' && resultado != undefined){
					Ext.Msg.alert('SET', 'El banco no se puede eliminar por que tiene chequeras asignadas');
				}
				else{			        						
					Ext.Msg.confirm('SET', '¿está seguro de eliminar el banco: ' + registroSeleccionado[0].get('idBanco') +' '+ registroSeleccionado[0].get('descripcion') + '?', function(btn){
    					if (btn === 'yes'){    						
    						//Se elimina el banco
    						MantenimientoBancosAction.eliminaBanco(parseInt(registroSeleccionado[0].get('idBanco')), function(resultado, e){
    							if (resultado > 0){
    								Ext.Msg.alert('SET', 'EL registro fue eliminado con exito');
    								NS.limpiar();
    		        				NS.buscar();
    							}
    						});
    					}
    				});			        					
				}					
			});			
		}
	};
	 
	NS.inhabilita = function(opcion){
		switch (opcion){
		case 1:
			//cuando seleccionas el tipo de banco nacional
			NS.tipoBanco = 0;
			Ext.getCmp(PF + 'lblAba').setVisible(false);
			Ext.getCmp(PF + 'txtAba').setVisible(false);
			
			Ext.getCmp(PF + 'lblIdBancoAMB').setDisabled(true);
			Ext.getCmp(PF + 'lblPrefijo').setDisabled(false);
			Ext.getCmp(PF + 'lblDescripcionInbursa').setDisabled(false);
			Ext.getCmp(PF + 'lblClavePlaza').setVisible(true);
			Ext.getCmp(PF + 'lblPlazaExt').setVisible(false);
			Ext.getCmp(PF + 'txtDescripcionInbursa').setDisabled(false);
			Ext.getCmp(PF + 'txtBancoAMB').setDisabled(true);
			Ext.getCmp(PF + 'txtPrefijo').setDisabled(false);
			Ext.getCmp(PF + 'txtClavePlaza').setVisible(true);
			Ext.getCmp(PF + 'txtPlazaExt').setVisible(false);
			
			Ext.getCmp(PF + 'chkLayoutComerica').setDisabled(true);
			Ext.getCmp(PF + 'chkChequeProtegido').setDisabled(false);
			Ext.getCmp(PF + 'chkValidaClabe').setDisabled(false);
			Ext.getCmp(PF + 'chkChequeOcurre').setDisabled(false);
			Ext.getCmp(PF + 'chkChequeElectronico').setDisabled(false);
			break;
		case 2:
			//cuando seleccionas el tipo de banco extranjero
			NS.tipoBanco = 1;
			Ext.getCmp(PF + 'lblAba').setVisible(true);
			Ext.getCmp(PF + 'txtAba').setVisible(true);
			
			Ext.getCmp(PF + 'lblIdBancoAMB').setDisabled(true);
			Ext.getCmp(PF + 'lblPrefijo').setDisabled(true);
			Ext.getCmp(PF + 'lblDescripcionInbursa').setDisabled(true);
			Ext.getCmp(PF + 'lblClavePlaza').setVisible(false);
			Ext.getCmp(PF + 'lblPlazaExt').setVisible(true);
			Ext.getCmp(PF + 'txtDescripcionInbursa').setDisabled(true);
			Ext.getCmp(PF + 'txtBancoAMB').setDisabled(true);
			Ext.getCmp(PF + 'txtPrefijo').setDisabled(true);
			Ext.getCmp(PF + 'txtClavePlaza').setVisible(false);
			Ext.getCmp(PF + 'txtPlazaExt').setVisible(true);
			Ext.getCmp(PF + 'chkLayoutComerica').setDisabled(false);
			Ext.getCmp(PF + 'chkChequeProtegido').setDisabled(true);
			Ext.getCmp(PF + 'chkValidaClabe').setDisabled(true);
			Ext.getCmp(PF + 'chkChequeOcurre').setDisabled(true);
			Ext.getCmp(PF + 'chkChequeElectronico').setDisabled(true);
			
			break;
		case 3:			
			
			break;
		}
	};

	//Radio Button para el tipo de bancos Nacional / Extranjero
	NS.optTipoBanco = new Ext.form.RadioGroup({
		id: PF + 'optTipoBanco',
		name: PF + 'optTipoBanco',
		columns: 2,
		items: [
		        {boxLabel: 'Nacional', name: 'optSeleccion', inputValue: 0, checked: true,		        	
		        	listeners:{
	          			check:{
	      					fn:function(opt, valor)
	      					{		    	   				
		          				if(valor==true)
		          				{
		          					NS.indicador='N';
		          					NS.inhabilita(1);
		          					NS.limpiar()
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
		          					NS.indicador='E';
		          					NS.inhabilita(2);
		          					NS.limpiar()
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
		items:[
		       {boxLabel: 'ABA', name: 'optAba', inputValue: 0, checked: true},
		       {boxLabel: 'SWIFT', name: 'optAba', inputValue: 1}
		]
	});
	
	
	//STORE Y COMBOS		
	//store que llena el grid de los bancos
	NS.storeLlenaGrid = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams: {descBanco: NS.descBanco, aba: NS.aba, tipoBanco: NS.tipoBanco},
		paramOrder: ['descBanco', 'aba', 'tipoBanco'],
		directFn: MantenimientoBancosAction.llenaGridBancos,
		idProperty: 'idBanco',
		fields: [
		         {name: 'idBanco'},
		         {name: 'descripcion'},
		         {name: 'idBancoCecoban'},		         
		         {name: 'bChequeElect'},
		         {name: 'bProtegido'},
		         {name: 'clavePlaza'},
		         {name: 'codigoSeguridad'},
		         {name: 'pathArchivo'},
		         {name: 'archivoProtegido'},
		         {name: 'fechaProtegido'},
		         {name: 'noProtegido'},
		         {name: 'nacionalidad'},
		         {name: 'bancaElect'},
		         {name: 'prefInstitucion'},
		         {name: 'liberacionAutomatica'},
		         {name: 'horaLiberacion'},
		         {name: 'saldoBanco'},
		         {name: 'plazaExtranjera'},
		         {name: 'layoutComerica'},
		         {name: 'descripcionInbursa'},
		         {name: 'validacionClabe'},
		         {name: 'chequeOcurre'},
		         {name: 'transferPorBanco'}
		         
		],
		listeners:{
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeLlenaGrid, msg: "Cargando..."});
				
				if (records.length == null || records.length <= 0)
					Ext.Msg.alert('SET', 'No existen Bancos para mostrar');
			}		
		}		
	});
		
	//Datos fijos para el combo de Banca Electronica
	NS.datosCombo = [
		['I', 'IMPORTACION'],
		['E', 'EXPORTACION'],
		['N', 'NINGUNA'],
		['A', 'AMBAS']
	];	      		
	 
	NS.storeBancaElectronica = new Ext.data.SimpleStore({
   		idProperty: 'idBanca',
   		fields: [
   					{name: 'idBanca'},
   					{name: 'descripcion'}
   				]
   	});
   	NS.storeBancaElectronica.loadData(NS.datosCombo);
	
	//COMBOBOX
	//Combo del tipo de Banca Electronica (Se llena de forma manual)
	NS.cmbBancaElectronica = new Ext.form.ComboBox({
		store: NS.storeBancaElectronica,
		id: PF + 'cmbBancaElectronica',
		name: PF + 'cmbBancaElectronica',
		x: 0,
		y: 105,
		width: 150,
		typeAhead: true,
		tabIndex: 6,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		valueField: 'idBanca',
		displayField: 'descripcion',
		autocomplete: true,
		emptyText: 'Seleccione Tipo Banca',
		triggerAction: 'all',
		value: '',
		visible: false,
		listeners:{
			select: 
			{
				fn: function (combo, valor)
				{
					
				}
			}
		}		
	});
	
	//Esto es para el combo de saldo_banco
	NS.datosComboSaldoBanco = [
		['C', 'COLUMNA'],
		['R', 'RENGLON'],
		['N', 'NINGUNA']		       			
	];	       		
	 
	NS.storeSaldoBanco = new Ext.data.SimpleStore({
   		idProperty: 'idSaldo',
   		fields: [
   					{name: 'idSaldo'},
   					{name: 'descripcion'}
   				]
   	});
   	NS.storeSaldoBanco.loadData(NS.datosComboSaldoBanco);
	
	//COMBOBOX
	//Combo del tipo de Banca Electronica (Se llena de forma manual)
	NS.cmbSaldoBanco = new Ext.form.ComboBox({
		store: NS.storeSaldoBanco,
		id: PF + 'cmbSaldoBanco',
		name: PF + 'cmbSaldoBanco',
		x: 330,
		y: 150,
		width: 150,
		typeAhead: true,
		mode: 'local',
		tabIndex: 12,
		selecOnFocus: true,
		forceSelection: true,
		valueField: 'idSaldo',
		displayField: 'descripcion',
		autocomplete: true,
		emptyText: 'Seleccione Tipo Saldo',
		triggerAction: 'all',
		value: '',
		visible: false,
		listeners:{
			select: 
			{
				fn: function (combo, valor)
				{
					
				}
			}
		}		
	});
	
	//GRID
	NS.columnaSeleccion = new Ext.grid.CheckboxSelectionModel({
		singleSelect: false
	});
	
	//Columnas del grid
	NS.columnasGrid = new Ext.grid.ColumnModel([
		{header: 'Id Banco', width: 50, dataIndex: 'idBanco', sortable: true},
		{header: 'Desc Banco', width: 80, dataIndex: 'descripcion', sortable: true},
		{header: 'Id Banco CECOBAN', width: 50, dataIndex: 'idBancoCecoban', sortable: true, hidden: true},		
		{header: 'Imprime BE', width: 50, dataIndex: 'bChequeElect', sortable: true},
		{header: 'Cheq. Protegido', width: 50, dataIndex: 'bProtegido', sortable: true},
		{header: 'Clave Plaza', width: 80, dataIndex: 'clavePlaza', sortable: true},
		{header: 'Codigo Seguridad', width: 50, dataIndex: 'codigoSeguridad', sortable: true},
		{header: 'Path De Archivo', width: 100, dataIndex: 'pathArchivo', sortable: true},
		{header: 'Clave De Archivo', width: 70, dataIndex: 'archivoProtegido', sortable: true},
		{header: 'Fecha De Archivo', width: 70, dataIndex: 'fechaProtegido', sortable: true},
		{header: 'Consecutivo', width: 70, dataIndex: 'noProtegido', sortable: true},
		{header: 'Nacionalidad', width: 50, dataIndex: 'nacionalidad', sortable: true},
		{header: 'Banca Elect', width: 50, dataIndex: 'bancaElect', sortable: true},
		{header: 'Pref. Intitución', width: 60, dataIndex: 'prefInstitucion', sortable: true},
		{header: 'Liberación Automática', width: 50, dataIndex: 'liberacionAutomatica', sortable: true, hidden: true},
		{header: 'Hora Liberación', width: 70, dataIndex: 'horaLiberacion', sortable: true, hidden: true},
		{header: 'Saldo Banco', width: 80, dataIndex: 'saldoBanco', sortable: true},
		{header: 'Dirección Extranjera', width: 100, dataIndex: 'plazaExtranjera', sortable: true},
		{header: 'Layout Comerica', width: 100, dataIndex: 'layoutComerica', sortable: true},
		{header: 'Descripción Inbursa', width: 100, dataIndex: 'descripcionInbursa', sortable: true},
		{header: 'Valida Clabe', width: 50, dataIndex: 'validacionClabe', sortable: true, hidden: true},
		{header: 'Cheque Ocurre', width: 50, dataIndex: 'chequeOcurre', sortable: true, hidden: true},
		{header: 'Transfer Por Banco', width: 60, dataIndex: 'transferPorBanco', sortable: true, hidden: true}
		 
	]);
	
	NS.gridDatos = new Ext.grid.GridPanel({
		store: NS.storeLlenaGrid,
		id: PF + 'gridDatos',
		name: PF + 'gridDatos',
		cm: NS.columnasGrid,
		sm: NS.columnaSeleccion,
		width: 960,
		height: 120,
		stripeRows: true,
		columnsLine: true,
		listeners: {
			click: 
			{
				fn: function(e){
					NS.modificar = true;
					NS.llenaDatos();
			}
			}
		}
	});
	 
	
	//Panel para radio button de bancos nacionales o extranjeros	
	NS.panelTipoBanco = new Ext.form.FieldSet({
		title: '',
		x: 0,
		y: 0,
		width: 170,
		height: 40,
		layout: 'absolute',
		items:
		[
		 	NS.optTipoBanco
		]
	});
		
	//Panel de busqueda
	NS.panelBusqueda = new Ext.form.FieldSet({
		title: 'Busqueda',
		x: 0,
		y: 0,
		width: 985,
		height: 75,
		layout: 'absolute',
		items: [
		        NS.panelTipoBanco,
		        NS.lblBanco,
		        NS.txtBanco,
		        //NS.cmbBanco,
		        NS.lblAba,
		        NS.txtAba,
		        {
		        	xtype: 'button',
		        	text: 'Buscar',
		        	x: 870,
		        	y: 10,
		        	width: 80,
		        	height: 22,
		        	tabIndex: 1,
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
	
	
	
	//Panel que contiene grid
	NS.panelGrid = new Ext.form.FieldSet({
		title: '',
		x: 0,
		y: 80,
		width: 985,
		height: 140,
		layout: 'absolute',
		items: 
		[
		 	NS.gridDatos
		]
	});
	
	NS.panelAbaSwift = new Ext.form.FieldSet({
		title: 'Aba / Swift',
		x: 240,
		y: 0,
		width: 250,
		height: 80,
		layout: 'absolute',
		hidden: true,
		items:
		[
		 	NS.optAbaSwift,
		 	NS.txtAbaSwift
		]
	});
	
	NS.panelCampos = new Ext.form.FieldSet({
		title: '',
		x: 0,
		y: 225,
		width: 985,
		height: 240,
		layout: 'absolute',
		items: [
		        NS.lblBancoInserta,
		        NS.lblIdBancoAMB,
		        NS.lblPrefijoInstitucion,
		        NS.lblBancaElectronica,	
		        NS.lblClavePlaza,		
		        NS.lblHoraSBC,		
		        NS.lblSaldoBanco,
		        NS.lblDescripcionInbursa,
		        NS.lblPathArchivo,
		        NS.lblCodigoSeguridad,
		        NS.lblClaveArchivo,
		        NS.lblFechaArchivo,
		        NS.lblConsecutivoArchivo,
		        NS.lblPlazaExt,
		        NS.txtIdBancoInserta,
		        NS.txtBancoInserta,
		        NS.txtBancoAMB,
		        NS.txtPrefijo,
		        NS.txtClavePlaza,
		        NS.txtHora,
		        NS.txtDescripcionInbursa,
		        NS.txtPathChequeProtegido,
		        NS.txtCodigoSeguridad,
		        NS.txtClaveArchivo,
		        NS.txtFechaArchivo,
		        NS.txtConsecutivo,
		        NS.txtPlazaExt,
		        NS.panelAbaSwift,
		        NS.cmbBancaElectronica,	
		        NS.cmbSaldoBanco,		        
		        {
		        	xtype: 'checkbox',
		        	id: PF + 'chkChequeElectronico',
		        	name: PF + 'chkChequeElectronico',
		        	x: 240,
		        	y: 10,
		        	tabIndex: 8,
		        	boxLabel: 'Imprime Cheque Electronico',
		        	listeners: {
		        		check:
		        		{
		        			fn: function(checkBox, valor){
		        				if (valor){
		        					Ext.getCmp(PF + 'lblClavePlaza').setDisabled(false);
		        					Ext.getCmp(PF + 'txtClavePlaza').setDisabled(false);
		        				}
		        				else
		        				{
		        					Ext.getCmp(PF + 'lblClavePlaza').setDisabled(true);
		        					Ext.getCmp(PF + 'txtClavePlaza').setDisabled(true);
		        				}		        					
		        			}
		        		}
		        	}
		        },
		        {
		        	xtype: 'checkbox',
		        	id: PF + 'chkLiberacionAutomatica',
		        	name: PF + 'chkLiberacionAutomatica',
		        	x: 240,
		        	y: 95,
		        	tabIndex: 10,
		        	boxLabel: 'Maneja Liberación Automatica',
		        	listeners: 
		        	{
		        		check:
		        		{
		        			fn: function(checkBox, valor){
		        				if (valor)
		        				{
		        					Ext.getCmp(PF + 'lblHora').setDisabled(false);
		        					Ext.getCmp(PF + 'txtHora').setDisabled(false);
		        				}
		        				else
		        				{
		        					Ext.getCmp(PF + 'lblHora').setDisabled(true);
		        					Ext.getCmp(PF + 'txtHora').setDisabled(true);
		        				}
		        					
		        			}
		        		}
		        	}		        	
		        },
		        {
		        	xtype: 'checkbox',
		        	id: PF + 'chkLayoutComerica',
		        	name: PF + 'chkLayoutComerica',
		        	x: 770,
		        	y: 10,
		        	tabIndex: 19,
		        	boxLabel: 'Maneja Layout Comerica',
		        	disabled: true,
		        	listeners:
		        	{
		        		check:
		        		{
		        			fn: function(checkBox, valor){}
		        		}
		        	}
		        },
		        {
		        	xtype: 'checkbox',
		        	id: PF + 'chkValidaClabe',
		        	name: PF + 'chkValidaClabe',
		        	x: 770,
		        	y: 35,
		        	tabIndex: 20,
		        	boxLabel: 'Valida CLABE',
		        	listeners:
		        	{
		        		check: 
		        		{
		        			fn: function(checkBox, valor){}
		        	    }
		        	}
		        },
		        {
		        	xtype: 'checkbox',
		        	id: PF + 'chkChequeOcurre',
		        	name: PF + 'chkChequeOcurre',
		        	x: 770,
		        	y: 60,
		        	tabIndex: 21,
		        	boxLabel: 'Cheque Ocurre',
		        	listeners:
		        	{
		        		check:
		        		{
		        			fn: function(checkBox, valor) {}
		        		}
		        	}
		        },
		        {
		        	xtype: 'checkbox',
		        	id: PF + 'chkTratarPorBanco',
		        	name: PF + 'chkTratarPorBanco',
		        	x: 770,
		        	y: 85,
		        	tabIndex: 22,
		        	boxLabel: 'Tratar por Banco la CV Transfer',
		        	listeners:
		        	{
		        		check:
		        		{
		        			fn: function(checkBox, valor){}
		        		}
		        	}
		        },
		        {
		        	xtype: 'checkbox',
		        	id: PF + 'chkChequeProtegido',
		        	name: PF + 'chkChequeProtegido',
		        	x: 520,
		        	y: 10,
		        	tabIndex: 13,
		        	boxLabel: 'Imprime Cheque Protegido',
		        	listeners:
		        	{
		        		check:
		        		{
		        			fn: function(checkBox, valor){
		        				if (valor){
		        					Ext.getCmp(PF + 'lblPathArchivo').setDisabled(false);
		        					Ext.getCmp(PF + 'lblCodigoSeguridad').setDisabled(false);
		        					Ext.getCmp(PF + 'lblClaveArchivo').setDisabled(false);
		        					Ext.getCmp(PF + 'lblFechaArchivo').setDisabled(false);
		        					Ext.getCmp(PF + 'lblConsecutivoArchivo').setDisabled(false);
		        					
		        					Ext.getCmp(PF + 'txtPathChequeProtegido').setDisabled(false);
		        					Ext.getCmp(PF + 'txtCodigoSeguridad').setDisabled(false);
		        					Ext.getCmp(PF + 'txtClaveArchivo').setDisabled(false);
		        					Ext.getCmp(PF + 'txtFechaArchivo').setDisabled(false);
		        					Ext.getCmp(PF + 'txtConsecutivo').setDisabled(false);		        					
		        				}		        					
		        				else{
		        					Ext.getCmp(PF + 'lblPathArchivo').setDisabled(true);
		        					Ext.getCmp(PF + 'lblCodigoSeguridad').setDisabled(true);
		        					Ext.getCmp(PF + 'lblClaveArchivo').setDisabled(true);
		        					Ext.getCmp(PF + 'lblFechaArchivo').setDisabled(true);
		        					Ext.getCmp(PF + 'lblConsecutivoArchivo').setDisabled(true);
		        					
		        					Ext.getCmp(PF + 'txtPathChequeProtegido').setDisabled(true);
		        					Ext.getCmp(PF + 'txtCodigoSeguridad').setDisabled(true);
		        					Ext.getCmp(PF + 'txtClaveArchivo').setDisabled(true);
		        					Ext.getCmp(PF + 'txtFechaArchivo').setDisabled(true);
		        					Ext.getCmp(PF + 'txtConsecutivo').setDisabled(true);
		        				}
		        			}		        
		        		}
		        	}		        	
		        },		        
		        {
		        	xtype: 'button',
		        	text: 'Carga Bancos',
		        	x: 350,
		        	y: 190,
		        	width: 80, 
		        	height: 22,
		        	tabIndex: 23,
		        	hidden: true,
		        	listeners: {
		        		click: {
		        			fn: function(e){}
		        		}
		        	}
		        },
		        {
		        	xtype: 'button',
		        	text: 'Imprimir',
		        	x: 450,
		        	y: 190,
		        	width: 80,
		        	height: 22,
		        	tabIndex: 28,
		        	listeners: {
		        		click: {
			 				fn: function(e)
			 				{
//			 					
			 					
			 						parametros='?nomReporte=excelBancos';
		 							parametros+='&nomParam1=tipoBanco';
		 							parametros+='&valParam1='+NS.indicador;
		 							window.open("/SET/jsp/bfrmwrk/gnrators/abreExcel.jsp"+parametros);
			 					
			 				}
			 			}
			 		}
		        },
		        {
		        	xtype: 'button',
		        	text: 'Crear Nuevo',
		        	x: 550,
		        	y: 190,
		        	width: 80,
		        	height: 22,
		        	tabIndex: 24,
		        	listeners: {
		        		click: {
		        			fn: function(e){
		        				NS.modificar = false;
		        				NS.limpiar();		        				
		        				if (NS.tipoBanco == 1){
		        					//Aqui se incrementa en automatico el numero de banco ya que es para Extranjero
		        					Ext.getCmp(PF + 'txtIdBancoInserta').setDisabled(true);
		        					
		        					MantenimientoBancosAction.obtieneIdBancoMax(function (resultado, e){
		        						if (resultado != '' || resultado != 0 || resultado != undefined){
		        							Ext.getCmp(PF + 'txtIdBancoInserta').setValue(resultado);		        					
		        						}						
		        						else{
		        							Ext.getCmp(PF + 'txtIdBancoInserta').setValue(1000);		        					
		        						}
		        					});
		        				}
		        				else{
		        					Ext.getCmp(PF + 'txtIdBancoInserta').setValue('');
		        					Ext.getCmp(PF + 'txtIdBancoInserta').setDisabled(false);
		        				}
		        			}
		        		}
		        	}
		        },
		        {
		        	xtype: 'button',
		        	text: 'Eliminar',
		        	x: 650,
		        	y: 190,
		        	width: 80,
		        	height: 22,
		        	tabIndex: 25,
		        	listeners: {
		        		click: {
		        			fn: function(e){
		        				NS.eliminar();		        				
		        			}
		        		}
		        	}		        
		        },
		        {
		        	xtype: 'button',
		        	text: 'Aceptar',
		        	x: 750,
		        	y: 190,
		        	width: 80,
		        	height: 22,
		        	tabIndex: 26,
		        	listeners: {
		        		click: {
		        			fn: function(e){
		        				NS.insertaModifica();		        				
		        			}
		        		}
		        	}
		        },
		        {
		        	xtype: 'button',
		        	text: 'Limpiar',
		        	x: 850,
		        	y: 190,
		        	width: 80,
		        	height: 22,
		        	tabIndex: 27,
		        	listeners: {
		        		click: {
		        			fn: function(e){
		        				NS.limpiar();
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
		 	NS.panelCampos
	    ]
	});
	
	NS.bancos = new Ext.FormPanel ({
		title: 'Mantenimiento De Bancos',
		width: 1300,
		height: 706,
		frame: true,
		padding: 10,
		autoScroll: true,
		layout: 'absolute',
		id: PF + 'bancos',
		name: PF + 'bancos',
		renderTo: NS.tabContId,
		items: [
		 	NS.global
		]
	});
	
	NS.bancos.setSize(Ext.get(NS.tabContId).getWidth(), Ext.get(NS.tabContId).getHeight());	
});
 