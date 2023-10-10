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

function loadInterpretedVarinatEvidence(variantCID){
    var postData = {
        "caid": variantCID
    }
    postData = JSON.stringify(postData);

    var xhr = new XMLHttpRequest();
    var url = "/rest/interpretation/loadInterpretation";
    xhr.onload = function() {
        if (xhr.status === 200 && xhr.readyState == 4) {
            if(xhr.responseText != null && xhr.responseText  != ''){
                var jsonObj = JSON.parse(xhr.responseText);
                displayInterpretedVariantEvidence(jsonObj);                                                        
            }else{
                openEvidenceDocInputPopUp();
                let array = []
                renderEvidenceTable(array);  
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
        if(jsonObj.evidenceSet != null){
            let loadedEvidenceSetReformated = reformatedEvidenceSet(jsonObj.evidenceSet);
            renderEvidenceTable(loadedEvidenceSetReformated);  
        }
        if(jsonObj.condition != null && jsonObj.inheritance != null){
            saveNewEvidenceDocWithSetValues(jsonObj.condition, jsonObj.inheritance);
        }
    }
}

function reformatedEvidenceSet(loadedEvidenceSet){
    let newEvidenceSet = [];
    let keys = Object.keys(loadedEvidenceSet);
    for(let i in keys){
        let evidenceType = keys[i];
        var evidenceModifier = loadedEvidenceSet[evidenceType];

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

function saveNewEvidences(finalCallVal, allspecificEvidences){
    if(variantCID == null || variantCID == ''){
        alert("Error: Unknown varint CaID!")
        return;
    }
    var evidenceDoc = document.getElementById("evidenceDocValue").innerHTML.trim();
    var inheritance =  document.getElementById("inheritanceValue").innerHTML.trim();

    var postData = {
        "caid": variantCID,
        "condition": evidenceDoc,
        "inheritance": inheritance,
        "evidenceSet": {
            "bp1": '0',
            "bp2": '0',
            "bp3": '0',
            "bp4": '0',
            "bp5": '0',
            "bp6": '0',
            "bp7": '0',
            "bs1": '0',
            "bs2": '0',
            "bs3": '0',
            "bs4": '0',
            "ba1": false,
            "pp1": '0',
            "pp2": '0',
            "pp3": '0',
            "pp4": '0',
            "pp5": '0',
            "pm1": '0',
            "pm2": '0',
            "pm3": '0',
            "pm4": '0',
            "pm5": '0',
            "pm6": '0',
            "ps1": '0',
            "ps2": '0',
            "ps3": '0',
            "ps4": '0',
            "pvs1": '0'
        },
        "finalCall": finalCallVal
    }

    if(variantInterpretationID != null && variantInterpretationID > 0){
        postData.interpretationId = variantInterpretationID;
    }

    for(let eIndx in allspecificEvidences){
        let currntEvid = allspecificEvidences[eIndx];
        let evdNameLowerCase = currntEvid.name.toLowerCase();
        if(postData.evidenceSet[evdNameLowerCase] != null){
            if(evdNameLowerCase != 'ba1'){
                postData.evidenceSet[evdNameLowerCase] = currntEvid.modifier+'';
            }else{
                postData.evidenceSet[evdNameLowerCase] = true;
            }       
        }
    }

    postData = JSON.stringify(postData);

    var xhr = new XMLHttpRequest();
    var url = "/rest/interpretation/saveNewInterpretation";
    xhr.onload = function() {
        if (xhr.status === 200 && xhr.readyState == 4) {
            if(xhr.responseText != null && xhr.responseText  != ''){
                var jsonObj = JSON.parse(xhr.responseText);
                if(jsonObj.id == null){
                    alert("Error: Something went wrong while saving the variant interpretation!");
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
