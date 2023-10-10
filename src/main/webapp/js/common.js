function clearSelectChooser(elem){
    if(elem.firstChild != null){
        while (elem.firstChild) {
            elem.removeChild(elem.firstChild);
        }
    }
}

function removeWhiteSpace(strValue){
    return strValue.replace(/\s/g,'');
}

function checkVariantCaidFormat(variantCaid){
    let caIdRegex = "^CA[0-9]+$";  //  let hgvsRegex = "^NM_|XM_[0-9]\.[0-9]:$";
    let caIdRegexObj = new RegExp(caIdRegex);

    if(caIdRegexObj.test(variantCaid)){
        return true;
    }
    return false;
}

function getAlleleExtRecordsNameAndLink(externalRecords){
    var externalRecordsNameAndLink = [];
    var myKeys = Object.keys(externalRecords)
    var n = myKeys.length;
    for (var i = 0; i < n; i++){
        var iter = myKeys[i];
        var obj = externalRecords[iter][0];

        if(obj['@id'] != null){
                var link = obj["@id"];
                var erObj = {
                    'name':iter,
                    'link':link
                }
                externalRecordsNameAndLink.push(erObj);
        }
    } 
    return externalRecordsNameAndLink;
}

function getAlleleRegistryDataForVariant(variantCaIdInp){  
    return new Promise(function (resolve, reject) {
        var xhr = new XMLHttpRequest();	
        let url = "/rest/calculator/alleleAndGeneData/"+variantCaIdInp;

        xhr.onload = function() {
            if (xhr.status === 200 && xhr.readyState == 4) {		
                if(xhr.responseText != null && xhr.responseText != ""){
                    let alleleDataObj = JSON.parse(xhr.responseText);
                    resolve(alleleDataObj);  
                }else{
                    resolve("Unable to get response value from Allele Registry for variant CAID: <b>"+variantCaIdInp+"</b>.<br><b>Please try the following</b>: Check the used CAID value and please try again or refresh the page!");                   
                }
            } else if (xhr.status !== 200) {
                resolve("Error: Request failed, unbale to get response from Allele Registry API, returned status: " + xhr.status);
            }
        };
        xhr.open('GET', url, true);
        xhr.setRequestHeader('Content-Type', 'application/json');
        xhr.send();
    });
}

function getGeneNameFromAlleleRegResponse(communityStandardTitle){
    let startIndx = communityStandardTitle.indexOf("(");
    let endIndx = communityStandardTitle.indexOf(")");
    return communityStandardTitle.substring(startIndx+1, endIndx).trim();
}

function isObject(value) {
    return (
        typeof value === 'object' &&
        value !== null &&
        !Array.isArray(value)
    );
}


    /*
    if(inheritance == "Autosomal Dominant"){
        inheritance = "AUTOSOMAL_DOMINANT";
    }else if(inheritance == "Autosomal Recessive"){
        inheritance = "AUTOSOMAL_RECESSIVE";
    }else if(inheritance =="X-linked Dominant"){
        inheritance = "X_LINKED_DOMINANT";
    }else if(inheritance =="X-linked Recessive"){
        inheritance = "X_LINKED_RECESSIVE";
    }else if(inheritance =="Mitochondrial"){
        inheritance = "MITOCHONDRIAL";
    }else if(inheritance =="Multifactoral"){
        inheritance = "MULTIFACTORIAL";
    }else if(inheritance =="Other"){
        inheritance = "OTHER";
    }else{
        inheritance = "UNKNOWN";
    }*/

    /*
    if(finalCallVal == "Uncertain Significance - Insufficient Evidence"){
        finalCallVal = "INSUFFICIENT";
    }else if(finalCallVal == "Likely Benign"){
        finalCallVal = "LIKELY_BENIGN";
    }else if(finalCallVal == "Benign"){
        finalCallVal = "BENIGN";
    }else if(finalCallVal == "Uncertain Significance"){
        finalCallVal = "UNCERTAIN";
    }else if(finalCallVal == "Likely Pathogenic"){
        finalCallVal = "LIKELY_PATHOGENIC";
    }else if(finalCallVal == "Pathogenic"){
        finalCallVal = "PATHOGENIC";
    }else if(finalCallVal == "Uncertain Significance - Conflicting Evidence"){
        finalCallVal = "CONFLICTING";
    }*/
