function openInterpretedVariant(btnElem, closeCollectionPopuUp){
    resetInterpretedVariantsContainner()
    if(closeCollectionPopuUp){
        closeUserVariantCollectionPopUp();
    }
    searchNewVariantSetID(btnElem.value)
}

async function searchNewVariantSetID(variantCaIdInp){
    if(variantCaIdInp == null){
        return;
    }

    let alleleRegResponse = await getAlleleRegistryDataForVariant(variantCaIdInp);
    if(alleleRegResponse != null && isObject(alleleRegResponse)){
        displayVariantAlleleRegistryResponse(variantCaIdInp, alleleRegResponse);
        enableNewInterpretationBtn(variantCaIdInp);
    }else{
        openWarringDiv(alleleRegResponse); 
    }
}

async function displayVariantAlleleRegistryResponse(variantCaIdInp, alleleRegResponse){
    openVariantAlleleRegistryDataDiv();    

    let variantListingTable = document.getElementById('variantListingTable');
    clearSelectChooser(variantListingTable);

    let viBasicDataList = await getVIBasicDataForCaid(variantCaIdInp);
    if(viBasicDataList == null || viBasicDataList.length == 0){
        setVarInputPopUpWarning(variantCaIdInp);
        openWarringDiv("There are no Classification created for this Identifier.<br>"+
                        "You can start a new Classification by clicking on the \"Start New Classification Now\" button below!");
        return;
    }

    var tr = null;
    var td = null;
    var div = null;
    var p = null;
    var a = null;

    //set data for each var. interpretation made for this variant caid
    for(let elemIndx in viBasicDataList){
        let viBasicDataObj = viBasicDataList[elemIndx];

        tr = document.createElement('tr');
            //caid
            td = document.createElement('td');
            td.style.width = "4.5%";
                a = document.createElement('a');
                a.href = setVariantAllelePageURL(variantCaIdInp);
                a.style.color = 'black';
                a.title = 'Variant Id';
                a.target="_blank";
                    p = document.createElement("p");        
                    p.innerText = variantCaIdInp;
                a.appendChild(p);  
            td.appendChild(a);
        tr.appendChild(td);
            //Gene name
            td = document.createElement('td');
            td.style.width = "5%";
                let geneNameElem = null;
                let geneNameID = "N/A";
                if(viBasicDataObj.relatedGene != null && viBasicDataObj.relatedGene.geneName != null){
                    geneNameID = viBasicDataObj.relatedGene.geneName;
                    geneNameElem = document.createElement('a');
                    if(viBasicDataObj.relatedGene.hgncId != null){
                        geneNameElem.href = creteHgncLink(viBasicDataObj.relatedGene.hgncId);
                    }else{
                        geneNameElem.href = "https://www.genenames.org/tools/search/#!/?query="+geneNameID;
                    }
                    geneNameElem.style.color = 'black';
                    geneNameElem.target="_blank";
                        p = document.createElement("p");        
                        p.innerText = geneNameID;
                    geneNameElem.appendChild(p);
                }else{
                    geneNameElem = document.createElement("p");       
                    geneNameElem.innerText = geneNameID;
                }
                geneNameElem.title = 'Gene name';
 
            td.appendChild(geneNameElem);
        tr.appendChild(td);
            //HGVS query
            td = document.createElement('td');
            td.style.width = "10%";
            td.title = "HGVS identifier"
            td.innerHTML = alleleRegResponse.communityStandardTitle[0];
        tr.appendChild(td);
            //Final Call
            td = document.createElement('td');
            td.style.width = "18%";
            //td.style.border = "1px solid red";
                let divFinalCall = document.createElement('div');
                divFinalCall.className ="varTypeDiv varTypeDivYellow";
                divFinalCall.title = "Final Classification"
                if(viBasicDataObj.determinedFinalCall != null && viBasicDataObj.determinedFinalCall.id != viBasicDataObj.calculatedFinalCall.id){
                    divFinalCall.innerHTML = viBasicDataObj.determinedFinalCall.term;
                }else{
                    divFinalCall.innerHTML = viBasicDataObj.calculatedFinalCall.term;
                }
                divFinalCall.setAttribute('data-value', viBasicDataObj.cspecengineId);
            td.appendChild(divFinalCall);    
        tr.appendChild(td);
            //Condition
            td = document.createElement('td');
            td.style.width = "5%";
            //td.style.border = "1px solid green";
                a = document.createElement('a');
                a.href = "https://cspec.genome.network/cspec/Disease/id/"+viBasicDataObj.conditionId;
                a.style.color = 'black';
                a.title = "Condition type";
                a.target="_blank";
                    p = document.createElement("p");        
                    p.innerText = viBasicDataObj.condition;
                a.appendChild(p);  
            td.appendChild(a);
        tr.appendChild(td);
            //Mode of inheritance         
            td = document.createElement('td');
            td.style.width = "5%";
            //td.style.border = "1px solid purple";
                let divInheritance = document.createElement('div');
                divInheritance.title = "Mode Of Inheritance"
                divInheritance.innerHTML = viBasicDataObj.inheritance; 
            td.appendChild(divInheritance);
        tr.appendChild(td);
            //PC page link
            td = document.createElement('td');
            td.style.width = "3%";
            //td.style.border = "1px solid green";
                let divPCLink = document.createElement('div');
                divPCLink.id = "gotToCalculatorIcon";
                divPCLink.className ="calculateDivBtn";
                divPCLink.title = "Edit Classification!"
                divPCLink.setAttribute('data-value', viBasicDataObj.caid+"_"+viBasicDataObj.interpretationId);
                divPCLink.addEventListener("click", function(){ goToCalculatorPage(this) });
            td.appendChild(divPCLink);
        tr.appendChild(td);
            //Dates
            td = document.createElement('td');
            //td.style.border = "1px solid orange";
            td.style.width = "10%";
            td.innerHTML = getFullTimeAndDate(viBasicDataObj.createOn);
            td.title = "Date when created (month/day/year)"
        tr.appendChild(td);
            //remote resource links
            td = document.createElement('td');
            td.style.width = "34%";
            //td.style.border = "1px solid purple";
                div = document.createElement('div');
                div.className = "quickLinksContainer";
                    if(alleleRegResponse.externalRecords != null){
                        let externalRecordsNameAndLink = extractAlleleExtRecordsNameAndLink(viBasicDataObj.caid, alleleRegResponse);
                        if(externalRecordsNameAndLink != null){
                            createPCExternalLinks(externalRecordsNameAndLink, div, "alleleGeneLinksIntro");
                        }
                    }
            td.appendChild(div);
        tr.appendChild(td);
        variantListingTable.appendChild(tr);
    }
}

function setVarInputPopUpWarning(variantCaIdInp){
    let warrningMessageInput = document.getElementById("warrningMessageInput");
    warrningMessageInput.style.display = "flex"; 
    document.getElementById("warrningMessInputP").innerHTML = 'There are no Classification created for a Variant with CAID: '+variantCaIdInp+'.<br>'+
                                                            'You can start a new Classification by clicking <span id="newCLassificationPopupBtn" onclick="goToCalculatorPage(\''+variantCaIdInp+'_0\')"><b><u>Here</u><b></span>!';
}

function emptyAndHideInputWarningMessageChannel(warrningMessageInputElem){
    document.getElementById("newVarinatInp").value= "";

    document.getElementById("warrningMessInputP").innerHTML= "";
    if(warrningMessageInputElem != null){
        warrningMessageInputElem.style.display = "none";
    }else{
        document.getElementById("warrningMessageInput").style.display = "none";
    }
}

function openVariantAlleleRegistryDataDiv(responseObj){
    let variantsListing = document.getElementById("variantsListing");
    variantsListing.style.display = "block";     
}

function getVIBasicDataForCaid(variantCaId){  
    return new Promise(function (resolve, reject) {
        var xhr = new XMLHttpRequest();	
        let url = "/pcalc/rest/interpretation/getVIBasicDataForCaid/"+variantCaId;

        xhr.onload = function() {
            if (xhr.status === 200 && xhr.readyState == 4) {		
                if(xhr.responseText != null && xhr.responseText != ''){
                    resolve(JSON.parse(xhr.responseText));
                }else{
                    resolve(null);    
                }
            } else if (xhr.status !== 200) {
                resolve(null);
            }
        };
        xhr.open('GET', url, true);
        xhr.setRequestHeader('Content-Type', 'application/json');
        xhr.send();
    });
}

function searchInterpretedCAIDs(caIDpartialVal){
    if(caIDpartialVal != null && caIDpartialVal.length >= 3){	
        if(!checkVariantCaidFormat(caIDpartialVal)){
            alert("Error: CaID variant format is not correct!");
            return;
        }

        url = "/pcalc/rest/intro/getInterpretedCaIDs/"+caIDpartialVal.toUpperCase();  

        var xhr = new XMLHttpRequest();
        xhr.onload = function() {
            if (xhr.status === 200 && xhr.readyState == 4) {
                if(xhr.responseText != null && xhr.responseText != ""){
                    let variantCAIDsList = JSON.parse(xhr.responseText);               
                    displayVariantCollectionForCurrentUser(variantCAIDsList);  
                }
            }else if (xhr.status !== 200) {
                alert('Request failed, returned status of ' + xhr.status);
            }
        };
        xhr.open('GET', url, true);
        xhr.setRequestHeader('Content-Type', 'application/json');
        xhr.send();
    }
}

function displayVariantCollectionForCurrentUser(variantCaIdsList){
    var interpretedVariantContainner = document.getElementById("interpretedVariantContainner");
    clearSelectChooser(interpretedVariantContainner);

    if(variantCaIdsList == null || variantCaIdsList.length == 0){
        let p = document.createElement("p");
        p.innerText = "You have no variants that match the inputted value!";
        interpretedVariantContainner.appendChild(p);
        return;
    }

    var btn = null;
    for(let i in variantCaIdsList){
        if(variantCaIdsList[i] == null || variantCaIdsList[i] == ''){
            continue;
        }
        btn = document.createElement("button");
            btn.className = "mainVariantBtns";
            btn.value = variantCaIdsList[i];
            btn.addEventListener("click", function(){ openInterpretedVariant(this, true) });
            btn.innerText = variantCaIdsList[i];
            interpretedVariantContainner.appendChild(btn);
    }
}

id="vcepsPartNameInp" 

function searchVCEPsByPartName(vcepNamePartialVal){
    if(vcepNamePartialVal != null && vcepNamePartialVal.length >= 4){	
        if(!checkVCEPNameFormat(vcepNamePartialVal)){
            alert("Error: VCEP (RuseSet) name format is not correct!");
            return;
        }
        getVCEPsInfoByPartialName(vcepNamePartialVal);
    }else if(vcepNamePartialVal != null && vcepNamePartialVal.length == ''){
        getAllVCEPsInfoForPopUp();
    }
}


async function getAllVCEPsInfoForPopUp(){
    getVCEPsInfoByPartialName("all_data");
}

async function getVCEPsInfoByPartialName(vcepNamePartialVal){
    let cspecEnginesInfo = await getVCEPsInfoByName(vcepNamePartialVal);
    let vcepsListContainer = document.getElementById("vcepsListContainer");
    clearSelectChooser(vcepsListContainer);

    if(cspecEnginesInfo == null || cspecEnginesInfo.length == 0){
        return;
    }

    let engineGroupDiv = createGroupEnginesHTMLObj("");
    createEngineHTMLList(engineGroupDiv, cspecEnginesInfo, null);
    vcepsListContainer.appendChild(engineGroupDiv);
}

function getVCEPsInfoByName(vcepNamePartialVal){
    return new Promise(function (resolve, reject) {
        var xhr = new XMLHttpRequest();	
        let url = "/pcalc/rest/cspecengines/getVCEPsInfoByName/"+vcepNamePartialVal;

        xhr.onload = function() {
            if (xhr.status === 200 && xhr.readyState == 4) {		
                if(xhr.responseText != null && xhr.responseText != ""){
                    let dataObj = JSON.parse(xhr.responseText);
                    resolve(dataObj);  
                }else{
                    resolve(null);                   
                }
            } else if (xhr.status !== 200) {
                resolve("Error: Request failed, unbale to get response from cspecengines API, returned status: "+xhr.status);
            }
        };
        xhr.open('GET', url, true);
        xhr.setRequestHeader('Content-Type', 'application/json');
        xhr.send();
    });
}
