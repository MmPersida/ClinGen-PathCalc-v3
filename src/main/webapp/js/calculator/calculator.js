let cspecRuleSetObj = null;

//get the basic cspec engine rules for the table
function getCSpecRuleSet(){
    var xhr = new XMLHttpRequest();	
    let url = "/rest/calculator/cspecRuleSet";

    xhr.onload = function() {
        if (xhr.status === 200 && xhr.readyState == 4) {		
            if(xhr.responseText != null){
                cspecRuleSetObj = JSON.parse(xhr.responseText);
                if(cspecRuleSetObj != null){
                    var formatEvidenceDoc = formatEvidenceDocForCspecCall(); //the pathogenicityEvidencesDoc will be used in this step and it need to be ready by now
                    processCSpecRuleSet(cspecRuleSetObj, formatEvidenceDoc.cSpecCallObj.evidence); //now we edit the Assertions table in Guidlines Conclusions section               
                    compareFinaleCallValues(formatEvidenceDoc);
                }
            }else{
                console.log("Value of response text is null!")
            }
        } else if (xhr.status !== 200) {
            alert('Request failed, returned status of ' + xhr.status);
        }
    };
    xhr.open('GET', url, true);
    xhr.setRequestHeader('Content-Type', 'application/json');
    xhr.send();
}

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
        if(jsonObj.interpretationId != null && Number(jsonObj.interpretationId) > 0){
            variantInterpretationID = jsonObj.interpretationId;
        }
        if(jsonObj.finalCall != null){
            updateFinalCallHTMLEleme(jsonObj.finalCall);
        }
        if(jsonObj.viDescription != null && jsonObj.viDescription != ""){
            setVIDescriptionHTMLEleme(jsonObj.viDescription);
        }
        if(jsonObj.evidenceList != null){
            let loadedEvidenceSetReformated = reformatedEvidenceSet(jsonObj.evidenceList);
            renderEvidenceTable(loadedEvidenceSetReformated);  
        }
        if(jsonObj.condition != null && jsonObj.inheritance != null){
            setNewEvidenceDocValues(jsonObj.condition, jsonObj.inheritance);
        }
        enableDeleteInterpretationBtn();
        enableVICommentsBtn();
    }
}

function reformatedEvidenceSet(loadedEvidenceList){
    let newEvidenceSet = [];
    for(let i in loadedEvidenceList){
        let evidence = loadedEvidenceList[i];
        let evidenceType = evidence.name;
        var evidenceModifier = evidence.modifier;

        if(evidenceModifier == 1){
            evidenceModifier = "";
        }else if(evidenceModifier == 'P'){
            evidenceModifier = "Supporting";
        }else if(evidenceModifier == 'M'){    
            evidenceModifier = "Moderate";   
        }else if(evidenceModifier == 'S'){
            evidenceModifier = "Strong";
        }else if(evidenceModifier == 'V'){
            evidenceModifier = "Very Strong";
        }else{
            continue;
        }

        if(evidenceModifier == ""){
            newEvidenceSet.push(evidenceType.toUpperCase());
        }else{
            newEvidenceSet.push(evidenceType.toUpperCase() +" - "+ evidenceModifier);
        }  
    }
    return newEvidenceSet;
}

function formatEvidenceDocForCspecCall(){
    var cSpecCallObj = {"cspecRuleSetUrl": cspecRuleSetUrl,
            "evidence": {}
        }

    var allspecificEvidences = [];

    let keys = Object.keys(pathogenicityEvidencesDoc);
    for(let i in keys){
        let key = keys[i];
        var cellEvidenceData = pathogenicityEvidencesDoc[key];
        if(cellEvidenceData.evidenceTags.length > 0){
            if(cSpecCallObj.evidence[cellEvidenceData.name] == null){
                cSpecCallObj.evidence[cellEvidenceData.name] = cellEvidenceData.evidenceTags.length  
            }else{
                cSpecCallObj.evidence[cellEvidenceData.name] = Number(cSpecCallObj.evidence[cellEvidenceData.name]) + cellEvidenceData.evidenceTags.length ;
            }    

            let evidModifier = '0';
            var theseEvidenceTags  = cellEvidenceData.evidenceTags;
            for(let eIndx in theseEvidenceTags){
                let evidVal = theseEvidenceTags[eIndx];
                let evObj = formatIndividualEvdTag(evidVal);
                allspecificEvidences.push(evObj);
            }
        }
    }
    return {
        "cSpecCallObj":cSpecCallObj,
        "allspecificEvidences":allspecificEvidences
       };
}

function formatIndividualEvdTag(evidVal){
    let evidValArray = evidVal.split("-");
    let evidName = null; 
    let evidModifier = '0';

    if(evidValArray.length == 1){
        evidName = evidValArray[0].trim();
        evidModifier = '1';
    }else if(evidValArray.length == 2){
        evidName = evidValArray[0].trim();
        
        let modifierTemp = evidValArray[1].trim();
        if(modifierTemp == "Supporting"){
            evidModifier =  'P';
        }else if(modifierTemp == "Strong"){
            evidModifier =  'S';
        }else if(modifierTemp == "Moderate"){
            evidModifier =  'M';
        }else if(modifierTemp == "Very Strong"){
            evidModifier =  'V';
        } 
    }

    let evObj = {
        "name": evidName,
        "modifier": evidModifier
    }

    return evObj;
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

function updateFinalCallHTMLEleme(finalCallVal){
    document.getElementById("finalCallValue").innerHTML = finalCallVal;
}

function setVIDescriptionHTMLEleme(viDescription){
    document.getElementById("viDescriptionP").innerHTML = (viDescription.substring(0, 50))+"...";
}

function enableDeleteInterpretationBtn(){
    document.getElementById("deleteInterpretationDivBtn").style.display = "flex";
}

function enableVICommentsBtn(){
    document.getElementById("editInterpDescriptionDivBtn").style.display = "flex";
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

function deleteThisInterpretation(){
    let confirmed = confirm("Are you sure you want to procced with this action. Once executed it canot be revoked.");
    if(!confirmed){
        return;
    }

    if(variantInterpretationID == null || variantInterpretationID == ''){
        alert("Error: Unknown varint interpretation ID!")
        return;
    }

    var postData = {
        "interpretationId": variantInterpretationID
    }
    postData = JSON.stringify(postData);

    var xhr = new XMLHttpRequest();
    var url = "/rest/interpretation/deleteInterpretation";
    xhr.onload = function() {
        if (xhr.status === 200 && xhr.readyState == 4) {
            if(xhr.responseText != null && xhr.responseText  != ''){
                var jsonObj = JSON.parse(xhr.responseText);
                if(jsonObj.message != null && jsonObj.message != ''){
                    openNotificationPopUp(jsonObj.message);
                }else{
                    backToMainPage();
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
        processCSpecRuleSet(cspecRuleSetObj, newEvidenceSet);
    }
}

function getEditEvdcTagsTablePopupIds(tableRowIndex, evidenceTag){
    return 'evt_'+tableRowIndex+'_'+evidenceTag
}

function getEvidenceCellDiv(cellID){
    return "evdCellDiv_"+cellID;
}

function getTagValueIds(evidenceCellId){
    return "tagValues_"+evidenceCellId;
}
