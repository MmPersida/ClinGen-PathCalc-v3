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
    xhr.withCredentials = true;
    xhr.send(postData);
}

function displayInterpretedVariantEvidence(jsonObj){
    if(jsonObj != null){
        if(jsonObj.finalCall != null){
            updateFinalCallHTMLEleme(jsonObj.finalCall);
        }
        if(jsonObj.viDescription != null && jsonObj.viDescription != ""){
            setVIDescriptionHTMLEleme(jsonObj.viDescription);
        }
        if(jsonObj.condition != null && jsonObj.inheritance != null && jsonObj.cspecEngineDTO != null){
            //set this before the evidence table is created!
            setNewEvidenceDocValues(jsonObj.condition, jsonObj.inheritance, jsonObj.cspecEngineDTO.organizationName, jsonObj.cspecEngineDTO.engineId);
        }
        if(jsonObj.evidenceList == null){        
            jsonObj.evidenceList = new Array(); //must not be null!
        }
        renderEvidenceTable(jsonObj.evidenceList);         

        if(jsonObj.cspecEngineDTO != null){          
            cspecEngineID = jsonObj.cspecEngineDTO.engineId;
            cspecRuleSetID = jsonObj.cspecEngineDTO.ruleSetId;
            var formatEvidenceDoc = formatEvidenceDocForCspecCall(); //the pathogenicityEvidencesDoc will be used in the next step and it need to be ready by now
            if(formatEvidenceDoc.evidence == null){
                alert("Error: Unable to get current evidence list!")
            }   
            determineRuleSetAssertions(jsonObj.cspecEngineDTO.engineId, formatEvidenceDoc.evidence); 
            compareFinalCallValues(formatEvidenceDoc);            
        }
        enableDeleteInterpretationBtn();
        enableVICommentsBtn();       
    }
}

async function updateFinallCallAndProcessRuleSets(formatEvidenceDoc){
    let newFinalCall = await getFinallCallForEvidences(formatEvidenceDoc);
    if(newFinalCall == null || newFinalCall == ''){
        alert('Errro: Unable to get FinalCall from API response!')
    }

    if(formatEvidenceDoc.evidence != null){
        determineRuleSetAssertions(cspecEngineID, formatEvidenceDoc.evidence);
    }else{
        alert("Error: Unable to get current evidence list!")
    }   
    return newFinalCall;
}

function getFinallCallForEvidences(formatEvidenceDoc){
    var postData = {
        "rulesetId": cspecRuleSetID,
        "cspecengineId": cspecEngineID,
        "evidenceMap":  formatEvidenceDoc.evidence
    }

	return new Promise(function (resolve, reject) {
		postData = JSON.stringify(postData);

		var xhr = new XMLHttpRequest();
		var url = "/rest/cspecengines/cspecEngineCaller";
		xhr.onload = function() {
			if (xhr.status === 200 && xhr.readyState == 4) {
				if(xhr.responseText != null && xhr.responseText  != ''){
					var jsonObj = JSON.parse(xhr.responseText);
					var finalCallVal = jsonObj.data.finalCall;
					if(finalCallVal != null || finalCallVal != ''){					
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
        xhr.withCredentials = true;
		xhr.send(postData);
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
    xhr.withCredentials = true;
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
    xhr.withCredentials = true;
    xhr.send(postData);
}

async function compareFinalCallValues(formatEvidenceDoc){
    let currentFinalCallValue = document.getElementById("finalCallValue").innerHTML.trim();
    let newFinalCallValue = await getFinallCallForEvidences(formatEvidenceDoc);
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
    xhr.withCredentials = true;
    xhr.send(postData);
}

async function forceCallCSpecWithCurretEvidnece(){
    let formatEvidenceDoc = formatEvidenceDocForCspecCall();
    let finalCallValue = await updateFinallCallAndProcessRuleSets(formatEvidenceDoc);
    updateFinalCallHTMLEleme(finalCallValue);  
}

async function displayEngineInfo(divElem){
    let cspecengineId = divElem.innerHTML.trim();
    if(cspecengineId == null || cspecengineId == ''){
        return;
    }
    openNotificationPopUp(await createCSpecEngineInfoContent(cspecengineId));
}
