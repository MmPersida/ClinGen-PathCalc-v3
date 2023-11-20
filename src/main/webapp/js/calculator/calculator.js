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
                    var formatEvidenceDoc = formatEvidenceDocForCspecCall();
                    processCSpecRuleSet(cspecRuleSetObj, formatEvidenceDoc.cSpecCallObj.evidence);               
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

            let evidValArray = null;
            let evidName = null;
            let evidModifier = '0';
            var theseEvidenceTags  = cellEvidenceData.evidenceTags;
            for(let eIndx in theseEvidenceTags){
                let evidVal = theseEvidenceTags[eIndx];
                evidValArray = evidVal.split("-");
                
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
                allspecificEvidences.push(evObj);
            }
        }
    }
    return {
        "cSpecCallObj":cSpecCallObj,
        "allspecificEvidences":allspecificEvidences
       };
}

function updateFinallCall(){
    let formatEvidenceDoc = formatEvidenceDocForCspecCall();
    var cSpecCallPostData = formatEvidenceDoc.cSpecCallObj
    var newEvidenceSet = cSpecCallPostData.evidence;

    cSpecCallPostData = JSON.stringify(cSpecCallPostData);

    var xhr = new XMLHttpRequest();
    var url = "/rest/calculator/cspecEngineCaller";
    xhr.onload = function() {
        if (xhr.status === 200 && xhr.readyState == 4) {
            if(xhr.responseText != null && xhr.responseText  != ''){
                var jsonObj = JSON.parse(xhr.responseText);
                var finalCallVal = jsonObj.data.finalCall;
                if(finalCallVal != null || finalCallVal != ''){
                    updateFinalCallHTMLEleme(finalCallVal);
                    saveNewEvidences(finalCallVal, formatEvidenceDoc.allspecificEvidences);
                    editGuidlinesTable(newEvidenceSet);
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
    xhr.send(cSpecCallPostData);
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

/*
function enableEditInterpDescriptionDivBtn(){
    document.getElementById("editInterpDescriptionDivBtn").style.display = "flex";
}*/

function deleteEvidences(){
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
    var url = "/rest/interpretation/deleteEvidence";
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
        alert("Error: Unknown varint CaID!")
        return;
    }

    if(variantInterpretationID == null || variantInterpretationID == ''){
        alert("Error: Unknown varint interpretation ID!")
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
    var url = "/rest/interpretation/saveNewEvidence";
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
