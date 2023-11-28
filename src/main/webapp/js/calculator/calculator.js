function loadInterpretedVarinatEvidence(viID){
    var postData = {
        "interpretationId": viID
    }
    postData = JSON.stringify(postData);

    var xhr = new XMLHttpRequest();
    var url = "/rest/interpretation/loadInterpretation";
    xhr.onload = function() {
        if (xhr.status === 200 && xhr.readyState == 4) {
            if(xhr.responseText != null && xhr.responseText  != ''){
                var jsonObj = JSON.parse(xhr.responseText);
                if(jsonObj.message != null && jsonObj.message != ''){
                    openNotificationPopUp(jsonObj.message);
                }else{
                    displayInterpretedVariantEvidence(jsonObj); 
                }                                                                      
            }
        }else if (xhr.status !== 200) {
            alert('Request failed, returned status of ' + xhr.status);
        }
    };
    xhr.open("POST", url, true);
    xhr.setRequestHeader('Content-Type', 'application/json');
    xhr.send(postData);
}

function displayInterpretedVariantEvidence(jsonObj){
    if(jsonObj != null){
        /*
        if(jsonObj.interpretationId != null && Number(jsonObj.interpretationId) > 0){
            variantInterpretationID = jsonObj.interpretationId;
        }*/
        if(jsonObj.finalCall != null){
            updateFinalCallHTMLEleme(jsonObj.finalCall);
        }
        if(jsonObj.viDescription != null && jsonObj.viDescription != ""){
            setVIDescriptionHTMLEleme(jsonObj.viDescription);
        }
        if(jsonObj.evidenceList != null){
            let loadedEvidenceSetReformated = formatedEvidenceSetForUserDisplay(jsonObj.evidenceList);
            renderEvidenceTable(loadedEvidenceSetReformated);  
        }
        if(jsonObj.condition != null && jsonObj.inheritance != null){
            setNewEvidenceDocValues(jsonObj.condition, jsonObj.inheritance);
        }
        if(jsonObj.cspecEngineLdhId != null){
            getCSpecRuleSet(jsonObj.cspecEngineLdhId); 
        }
        enableDeleteInterpretationBtn();
        enableVICommentsBtn();
    }
}

function updateFinallCall(formatEvidenceDoc){
    var cSpecCallPostData = formatEvidenceDoc.cSpecCallObj;
    var newEvidenceSet = cSpecCallPostData.evidence;

	return new Promise(function (resolve, reject) {
		cSpecCallPostData = JSON.stringify(cSpecCallPostData);

		var xhr = new XMLHttpRequest();
		var url = "/rest/calculator/cspecEngineCaller";
		xhr.onload = function() {
			if (xhr.status === 200 && xhr.readyState == 4) {
				if(xhr.responseText != null && xhr.responseText  != ''){
					var jsonObj = JSON.parse(xhr.responseText);
					var finalCallVal = jsonObj.data.finalCall;
					if(finalCallVal != null || finalCallVal != ''){               
						editGuidlinesTable(newEvidenceSet);
						resolve(finalCallVal);
					}						
				}
				resolve(null);				
			}else if (xhr.status !== 200) {
				resolve(null);
			}
		};
		xhr.open("POST", url, true);
		xhr.setRequestHeader('Content-Type', 'application/json');
		xhr.send(cSpecCallPostData);
	});
}

function deleteEvidences(finalCallVal, allspecificEvidences){
    if(variantCID == null || variantCID == ''){
        alert("Error: Unknown varint CaID, unable to delete evidence!")
        return;
    }

    if(variantInterpretationID == null || variantInterpretationID == ''){
        alert("Error: Unknown varint interpretation ID, unable to delete evidence!!")
        return;
    }

    var evidenceDoc = document.getElementById("evidenceDocValue").innerHTML.trim();
    var inheritance =  document.getElementById("inheritanceValue").innerHTML.trim();

    var postData = {
        "interpretationId":variantInterpretationID,
        "caid": variantCID,
        "condition": evidenceDoc,
        "inheritance": inheritance,
        "evidenceList": allspecificEvidences,
        "finalCall": finalCallVal
    }

    postData = JSON.stringify(postData);

    var xhr = new XMLHttpRequest();
    var url = "/rest/evidence/deleteEvidence";
    xhr.onload = function() {
        if (xhr.status === 200 && xhr.readyState == 4) {
            if(xhr.responseText != null && xhr.responseText  != ''){
                var jsonObj = JSON.parse(xhr.responseText);
                if(jsonObj.message != null && jsonObj.message != ''){
                    openNotificationPopUp(jsonObj.message);
                }                           
            }else{
                console.log("Value of response text is null or empty!");
            }
        }else if (xhr.status !== 200) {
            alert('Request failed, returned status of ' + xhr.status);
        }
    };
    xhr.open("POST", url, true);
    xhr.setRequestHeader('Content-Type', 'application/json');
    xhr.send(postData);
}

function saveNewEvidences(finalCallVal, allspecificEvidences){
    if(variantCID == null || variantCID == ''){
        alert("Error: Unknown varint CaID, unable to save new evidence!")
        return;
    }

    if(variantInterpretationID == null || variantInterpretationID == ''){
        alert("Error: Unknown varint interpretation ID, unable to save new evidence!")
        return;
    }

    var evidenceDoc = document.getElementById("evidenceDocValue").innerHTML.trim();
    var inheritance =  document.getElementById("inheritanceValue").innerHTML.trim();

    if(evidenceDoc == null || inheritance == null){
        alert("Error: Unbale to save evidences, Condition and Mode Of Inheritance are missing!")
        return;
    }

    var postData = {
        "interpretationId":variantInterpretationID,
        "caid": variantCID,
        "condition": evidenceDoc,
        "inheritance": inheritance,
        "evidenceList": allspecificEvidences,
        "finalCall": finalCallVal
    }

    postData = JSON.stringify(postData);

    var xhr = new XMLHttpRequest();
    var url = "/rest/evidence/saveNewEvidence";
    xhr.onload = function() {
        if (xhr.status === 200 && xhr.readyState == 4) {
            if(xhr.responseText != null && xhr.responseText  != ''){
                var jsonObj = JSON.parse(xhr.responseText);
                if(jsonObj.message != null && jsonObj.message != ''){
                    openNotificationPopUp(jsonObj.message);
                }                            
            }else{
                console.log("Value of response text is null or empty!");
            }
        }else if (xhr.status !== 200) {
            alert('Request failed, returned status of ' + xhr.status);
        }
    };
    xhr.open("POST", url, true);
    xhr.setRequestHeader('Content-Type', 'application/json');
    xhr.send(postData);
}

async function compareFinaleCallValues(formatEvidenceDoc){
    let currentFinalCallValue = document.getElementById("finalCallValue").innerHTML.trim();
    let newFinalCallValue = await updateFinallCall(formatEvidenceDoc);
    if(currentFinalCallValue != newFinalCallValue){
        let htmlContentMessage = '<b>Warning</b>: The value of Final Call for this Varinat Interpretation as stored in the Data Base with it\'s evidence set previously defined,'+
                                 'no longer mathces the Final Call returned from the most recent querying of the CSpecEngine.</br></br>'+
                                 '<b>Current Final Call value</b>: <span style="color:rgba(50, 110, 150);">'+currentFinalCallValue+'</span></br>'+
                                 '<b>New Final Call value</b>: <span style="color:rgba(50, 110, 150);">'+newFinalCallValue+'</span></br></br>'+
                                 'If you continue working on this Varinat Interpretation, any future work on it\'s Evidence Tags will use and save the new value of Final Call.'+
                                 ' Main actions that can be pereformed with the current value of Final Call are the following:</br>'+
                                 '&#9;*Editing Interpretation comments</br>'+
                                 '&#9;*Editing Evidence summaries</br>'+
                                 '&#9;*Editing Evidence Links</br>'+
                                 '&#9;*Deleting the Interpretation</br>'+
                                 '&#9;*Creating Reports</br></br>'+
                                 '<div class="calcMainMenuBtns" onclick="updateFinalCallValue(\''+newFinalCallValue+'\')">Update Final Call value</div>';
        openNotificationPopUp(htmlContentMessage);
    }
}

function updateFinalCallValue(newFCValue){
    closeNotificationPopUp();
    var postData = {
        "interpretationId": variantInterpretationID,
        "finalCall": newFCValue
    }
    postData = JSON.stringify(postData);

    var xhr = new XMLHttpRequest();
    var url = "/rest/interpretation/updateFinalCall";
    xhr.onload = function() {
        if (xhr.status === 200 && xhr.readyState == 4) {
            if(xhr.responseText != null && xhr.responseText  != ''){
                var jsonObj = JSON.parse(xhr.responseText);
                if(jsonObj.message != null && jsonObj.message != ''){
                    openNotificationPopUp(jsonObj.message);
                }else{
                    updateFinalCallHTMLEleme(newFCValue);
                }                                                              
            }
        }else if (xhr.status !== 200) {
            alert('Request failed, returned status of ' + xhr.status);
        }
    };
    xhr.open("POST", url, true);
    xhr.setRequestHeader('Content-Type', 'application/json');
    xhr.send(postData);
}

async function forceCallCSpecWithCurretEvidnece(){
    let formatEvidenceDoc = formatEvidenceDocForCspecCall();
    let finalCallValue = await updateFinallCall(formatEvidenceDoc);
    updateFinalCallHTMLEleme(finalCallValue);  
}

function editGuidlinesTable(newEvidenceSet){
    if(cspecRuleSetObj != null){
        processCSpecRuleSetForGuidlinesTable(cspecRuleSetObj, newEvidenceSet);
    }
}
