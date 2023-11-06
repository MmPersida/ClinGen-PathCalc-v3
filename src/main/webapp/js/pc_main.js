
function searchNewVariant(){
    let variantCaIdInp =  document.getElementById('newVarinatInp').value.trim();
    if(!checkVariantCaidFormat(variantCaIdInp)){
        alert("Error: CaID variant format is not correct!");
        return;
    }
    closeNewInterpretationPopUp()   
    searchNewVariantSetID(variantCaIdInp)
}

function openInterpretedVariant(btnElem){
    resetInterpretedVariantsContainner()
    closeUserVariantCollectionPopUp();
    searchNewVariantSetID(btnElem.value)
}

async function searchNewVariantSetID(variantCaIdInp){
    let alleleRegResponse = await getAlleleRegistryDataForVariant(variantCaIdInp);
    if(alleleRegResponse != null && isObject(alleleRegResponse)){
        displayVariantAlleleRegistryResponse(variantCaIdInp, alleleRegResponse);
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
        openWarringDiv("There are no Interpretations created for a Variant wits CAID: "+variantCaIdInp); 
        return;
    }

    var tr = null;
    var td = null;
    var div = null;
    var p = null;
    var a = null;

    //set table header
    var headerColumnNames = ['Variants','Gene','Description','Assertion(s)/Tags','Quick Links'];
    tr = document.createElement('tr');
    for(let i in headerColumnNames){
        td = document.createElement('td');
        td.innerHTML = headerColumnNames[i];
        tr.appendChild(td);
    }

    //set data for each var. interpretation made for this variant caid
    for(let elemIndx in viBasicDataList){
        let viBasicDataObj = viBasicDataList[elemIndx];

        tr = document.createElement('tr');
            td = document.createElement('td');
                a = document.createElement('a');
                a.href = alleleRegResponse['@id'];
                a.style.color = 'black';
                a.target="_blank";
                    p = document.createElement("p");        
                    p.innerText = variantCaIdInp;
                a.appendChild(p);  
            td.appendChild(a);
        tr.appendChild(td);
            td = document.createElement('td');
            td.style.width = "6%";
                let geneNameID = getGeneNameFromAlleleRegResponse(alleleRegResponse.communityStandardTitle[0]);
                a = document.createElement('a');
                a.href = "https://genboree.org/cfde-gene-dev/Gene/id/"+geneNameID;
                a.style.color = 'black';
                a.target="_blank";
                    p = document.createElement("p");        
                    p.innerText = geneNameID;
                a.appendChild(p);  
            td.appendChild(a);
        tr.appendChild(td);
            td = document.createElement('td');
            td.style.width = "10%";
            td.innerHTML = alleleRegResponse.communityStandardTitle[0];
        tr.appendChild(td);
            td = document.createElement('td');
            td.style.width = "25%";
                div = document.createElement('div');
                div.className = "quickLinksContainer";
                    let divFinalCall = document.createElement('div');
                    divFinalCall.className ="varTypeDiv";
                    divFinalCall.title = "Final call"
                    divFinalCall.innerHTML = viBasicDataObj.finalCall;         
                div.appendChild(divFinalCall);
                    let divPCLink = document.createElement('div');
                    divPCLink.id = "gotToCalculatorIcon";
                    divPCLink.className ="calculateDivBtn";
                    divPCLink.title = "Edit interpretation!"
                    divPCLink.setAttribute('data-value', viBasicDataObj.caid+"_"+viBasicDataObj.interpretationId);
                    divPCLink.addEventListener("click", function(){ goToCalculatorPage(this) });
                div.appendChild(divPCLink);
                    p = document.createElement('p');
                div.appendChild(p);
            td.appendChild(div);
        tr.appendChild(td);
            td = document.createElement('td');
            td.style.width = "53%";
                div = document.createElement('div');
                div.className = "quickLinksContainer";
                    if(alleleRegResponse.externalRecords != null){
                        displayAlleleExternalRecordsLinks(getAlleleExtRecordsNameAndLink(alleleRegResponse.externalRecords), div);
                    }
            td.appendChild(div);
        tr.appendChild(td);
        variantListingTable.appendChild(tr);
    }
}

function displayAlleleExternalRecordsLinks(externalRecordsNameAndLink, div){
    if(externalRecordsNameAndLink == null || externalRecordsNameAndLink.length == 0){
        return;
    }   

    for(let erIndx in externalRecordsNameAndLink){
        let er = externalRecordsNameAndLink[erIndx];
        
        var a = document.createElement("a");
        a.className = "alleleGeneLinksIntro";
        a.href = er.link;
        a.target="_blank";         
            var p = document.createElement("p");        
            p.innerText = er.name;
        a.appendChild(p);  
        div.appendChild(a);
    }
}

function searchInterpretedCAIDs(caIDpartialVal){
    if(caIDpartialVal != null && caIDpartialVal.length >= 3){	
        if(!checkVariantCaidFormat(caIDpartialVal)){
            alert("Error: CaID variant format is not correct!");
            return;
        }

        url = "/rest/intro/getInterpretedCaIDs/"+caIDpartialVal.toUpperCase();  

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
    if(variantCaIdsList == null || variantCaIdsList.length == 0){
        let p = document.createElement("p");
        p.innerText = "You have no variants that match the inputted value!";
        interpretedVariantContainner.appendChild(p);
        return;
    }

    clearSelectChooser(interpretedVariantContainner);

    var btn = null;
    for(let i in variantCaIdsList){
        btn = document.createElement("button");
            btn.className = "mainVariantBtns";
            btn.value = variantCaIdsList[i];
            btn.addEventListener("click", function(){ openInterpretedVariant(this) });
            btn.innerText = variantCaIdsList[i];
            interpretedVariantContainner.appendChild(btn);
    }
}

function openVariantAlleleRegistryDataDiv(responseObj){
    let variantsListing = document.getElementById("variantsListing");
    variantsListing.style.display = "block";     
}

function getVIBasicDataForCaid(variantCaId){  
    return new Promise(function (resolve, reject) {
        var xhr = new XMLHttpRequest();	
        let url = "/rest/interpretation/getVIBasicDataForCaid/"+variantCaId;

        xhr.onload = function() {
            if (xhr.status === 200 && xhr.readyState == 4) {		
                if(xhr.responseText != null && xhr.responseText != ''){
                    resolve(JSON.parse(xhr.responseText));
                }else{
                    resolve("Error: Unbale to get response value from this call!");    
                }
            } else if (xhr.status !== 200) {
                resolve("Request failed, returned status of " + xhr.status);
            }
        };
        xhr.open('GET', url, true);
        xhr.setRequestHeader('Content-Type', 'application/json');
        xhr.send();
    });
}
