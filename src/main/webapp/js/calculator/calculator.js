function disableCalculatorPageForMillis(timeInMillis){
    let calculatorPageBody = document.getElementById("calculatorPageBody");
    calculatorPageBody.style.pointerEvents='none';
    calculatorPageBody.style.opacity= 0.5;
    
    let animationDuration = timeInMillis/1000;

    let calculatorLoaderSpiner = document.getElementById("calculatorLoader");
    calculatorLoaderSpiner.style.opacity = "1.0";
    calculatorLoaderSpiner.style.display = "block";
    calculatorLoaderSpiner.style.animation = "spinner "+animationDuration+"s linear";

    setTimeout(enableMainCalculatorPage, timeInMillis);
}

function enableMainCalculatorPage(){
    let calculatorPageBody = document.getElementById("calculatorPageBody");
    if(calculatorPageBody == null){
        console.log("calculatorPageBody is null!");
    }
    calculatorPageBody.style.pointerEvents='auto';
    calculatorPageBody.style.opacity= 1;

    let calculatorLoaderSpiner = document.getElementById("calculatorLoader");
    calculatorLoaderSpiner.style.display = "none";
    calculatorLoaderSpiner.style.animation = "none";
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
    xhr.withCredentials = true;
    xhr.send(postData);
}

function displayInterpretedVariantEvidence(jsonObj){
    if(jsonObj != null){
        if(jsonObj.calculatedFinalCall != null){
            updateCalculatedFinalCallHTML(jsonObj.calculatedFinalCall);
        }
        if(jsonObj.determinedFinalCall != null){
            updateDeterminedFinalCallHTML(jsonObj.determinedFinalCall);
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
            determineRuleSetAssertions(cspecEngineID, formatEvidenceDoc.evidence); 
            updateSummariesInEvidenceTagDataObj(cspecEngineID); 
            compareFinalCallValues(formatEvidenceDoc);            
        }
        enableDeleteInterpretationBtn();
        enableVICommentsBtn();       
    }
}

async function updateCalculatedFinallCallAndProcessRuleSets(formatEvidenceDoc){
    let newFinalCall = await getFinallCallForEvidences(formatEvidenceDoc);
    if(newFinalCall == null || newFinalCall.term == null){
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
					var finalCallObj = JSON.parse(xhr.responseText);
					if(finalCallObj != null){					
						resolve(finalCallObj);
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

function deleteEvidences(calculatedFCObj, allspecificEvidences){
    if(variantCID == null || variantCID == ''){
        alert("Error: Unknown varint CaID, unable to delete evidence!")
        return;
    }

    if(variantInterpretationID == null || variantInterpretationID == ''){
        alert("Error: Unknown Variant Classification ID, unable to delete evidence!!")
        return;
    }

    var postData = {
        "interpretationId":variantInterpretationID,
        "evidenceList": allspecificEvidences,
        "calculatedFinalCall": calculatedFCObj
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

function saveNewEvidences(calculatedFCObj, allspecificEvidences){
    if(variantCID == null || variantCID == ''){
        alert("Error: Unknown varint CaID, unable to save new evidence!")
        return;
    }

    if(variantInterpretationID == null || variantInterpretationID == ''){
        alert("Error: Unknown Variant Classification ID, unable to save new evidence!")
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
        "calculatedFinalCall": calculatedFCObj
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
    let finalCallValue = document.getElementById("finalCallValue").innerText.trim();
    let finalCallId = Number(document.getElementById("finalCallId").innerText.trim());
    if(finalCallValue == null || finalCallValue == ''){
        return;
    }

    let newFinalCallValue = await getFinallCallForEvidences(formatEvidenceDoc);
    if(finalCallId != newFinalCallValue.id){
        let htmlContentMessage = '<b>Warning</b>: Notice the difference between the previous and newly computed classifications</br></br>'+
                                 '<b>Previously Calculated Classification</b>: <span style="color:rgba(50, 110, 150);">'+finalCallValue+'</span></br>'+
                                 '<b>New Calculated Classification</b>: <span style="color:rgba(50, 110, 150);">'+newFinalCallValue.term+'</span></br></br>'+
                                 'Path Calc will use the Newly Computed Classification for all the subsequent operations including:</br>'+
                                 '&#9;*Editing Classification comments</br>'+
                                 '&#9;*Editing Evidence summaries</br>'+
                                 '&#9;*Editing Evidence Links</br>'+
                                 '&#9;*Deleting the Classification</br>'+
                                 '&#9;*Creating Reports</br></br>'+
                                 '<div class="calcMainMenuBtns" onclick="updateCalculatedFinalCall(\''+newFinalCallValue.id+'\')">Update Calculated Classification value</div>';
        openNotificationPopUp(htmlContentMessage);
    }
}

function updateCalculatedFinalCall(newFCId){
    closeNotificationPopUp();
    var postData = {
        "interpretationId": variantInterpretationID,
        "finalCallId": newFCId
    }
    postData = JSON.stringify(postData);

    var xhr = new XMLHttpRequest();
    var url = "/rest/interpretation/updateCalculatedFinalCall";
    xhr.onload = function() {
        if (xhr.status === 200 && xhr.readyState == 4) {
            if(xhr.responseText != null && xhr.responseText  != ''){
                var jsonObj = JSON.parse(xhr.responseText);
                if(jsonObj.message != null && jsonObj.message != ''){
                    openNotificationPopUp(jsonObj.message);
                }else{
                    updateCalculatedFinalCallHTML(jsonObj.finalCall);
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
    disableCalculatorPageForMillis(2000);
    let formatEvidenceDoc = formatEvidenceDocForCspecCall();
    let finalCallObj = await updateCalculatedFinallCallAndProcessRuleSets(formatEvidenceDoc);
    updateCalculatedFinalCallHTML(finalCallObj);  
}

async function displayEngineInfoFromDivBtn(divElem){
    var cspecengineId = divElem.getAttribute("data-value").trim();
    if(cspecengineId == null || cspecengineId == ''){
        return;
    }
    openSpecificationDetailsPoPup();
    createCSpecEngineInfoContent(cspecengineId);
}

async function displayEngineInfoFromBtn(btnElem){
    var cspecengineId = btnElem.value.trim();
    if(cspecengineId == null || cspecengineId == ''){
        return;
    }
    openSpecificationDetailsPoPup();
    createCSpecEngineInfoContent(cspecengineId);
}

function openExpertFCDescirption(){
    let htmlContentMessage = "Overwrite the computed classification manually in case of conflict. Leaving this empty will imply that you agree with the computed classification.";
    openNotificationPopUp(htmlContentMessage);
}
