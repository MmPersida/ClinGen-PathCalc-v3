
let caIdRegexObj = new RegExp("^(CA|ca)[0-9]");
let rsIdRegexObj = new RegExp("^(RS|rs)[0-9]");
let hgvsRegexObj = new RegExp("^NM_|XM_[0-9]\.[0-9]:$");
let clinvarRegexObj = new RegExp("^[0-9]+$");
let clinvarRCVRegexObj = new RegExp("^(RCV|rcv)[0-9]");
let gnomADRegexObj = new RegExp("[0-9]*-[0-9]*-[A-Za-z]*-[A-Za-z]*");
let myVariantInfoHG38RegexObj = new RegExp("chr[0-9]*:[a-z]\.[0-9]*[A-Za-z]*");

async function searchNewVariant(){
    let warrningMessageInput = document.getElementById("warrningMessageInput");
    if(warrningMessageInput.style.display == 'flex'){
        emptyAndHideInputWarningMessageChannel(warrningMessageInput);
    }

    let variantIdentifierInp =  document.getElementById('newVarinatInp').value.trim();
    variantIdentifierInp = (variantIdentifierInp.replace(/\(.+?\)/g, '')).trim(); //remove parentheses and their content from indentifier

    let selectedIdentifierType = getSelectedIdentifierType(document.getElementsByName('variantIdentifierRG'));

    if(variantIdentifierInp == '' || variantIdentifierInp == null ||
        selectedIdentifierType == '' || selectedIdentifierType == null){
            openWarringDiv("Unable to process the inputed variant identifier "+variantIdentifierInp+", of type "+selectedIdentifierType+", some input data is missing!"); 
            return;
    }

    let selectedIdentifierTypeTemp = null;
    let i = selectedIdentifierType.indexOf("_");
    if(i > 0){
        selectedIdentifierTypeTemp = selectedIdentifierType.substring(0,i);
    }else{
        selectedIdentifierTypeTemp = selectedIdentifierType;
    }

    let determinedVarIdentifier = checkTypeOfVariantIdentifier(variantIdentifierInp);
    if(determinedVarIdentifier != selectedIdentifierTypeTemp){
        let b = confirm("Are you sure that the inputed variant identifier "+variantIdentifierInp+" is of type "+selectedIdentifierType+". If this is incorrect it might not give any meaningful response data!");
        if(!b){
            return;
        }
    }

    if(selectedIdentifierType != 'caid'){
        //get the CAID based on the non CAID used varint identifier
        variantIdentifierInp = await determineCIADFromOtherVariantIdentifier(variantIdentifierInp, selectedIdentifierType);
    }

    if(variantIdentifierInp == null){
        openWarringDiv("Unable to determine the Variant CAID based on the variant identifier of type "+selectedIdentifierType+"!</br>"+ 
            "<b>Please try the following</b>: Check the used Variant Identifier value or selected type, you may have a typo error, and please try again or refresh the page!");
        return;
    }else{
        searchNewVariantSetID(variantIdentifierInp);
    }
}

function openNewVariantPopUp(){
    document.getElementById("openInputNewInterpretationModal").click();
}

function closeNewVariantPopUp(){
    document.getElementById('openInputNewInterpretationModal').click(); 
}

function checkTypeOfVariantIdentifier(variantIdTerm){
    if(caIdRegexObj.test(variantIdTerm)){
        return 'caid';
    }else if(rsIdRegexObj.test(variantIdTerm)){  
        return 'rsid'; 
    }else if(hgvsRegexObj.test(variantIdTerm)){
        return 'hgvs';
    }else if(clinvarRegexObj.test(variantIdTerm)){
        return 'clinvar';
    }else if(clinvarRCVRegexObj.test(variantIdTerm)){
        return 'clinvarRCV';
    }else if(gnomADRegexObj.test(variantIdTerm)){
        return 'gnomad';
    }else if(myVariantInfoHG38RegexObj.test(variantIdTerm)){
        return 'myvariantinfohg38';
    }else{
        return '';
    }
}

function determineCIADFromOtherVariantIdentifier(variantIdentifierInp, selectedIdentifierType){
    var postData = {
        "identifierValue": variantIdentifierInp,
        "identifierType": selectedIdentifierType
    }
    postData = JSON.stringify(postData);

    return new Promise(function (resolve, reject) {
        var xhr = new XMLHttpRequest();	
        let url = "/pcalc/rest/intro/determineCIAD";

        xhr.onload = function() {
            if (xhr.status === 200 && xhr.readyState == 4) {		
                if(xhr.responseText != null && xhr.responseText != ""){
                    resolve(xhr.responseText);  
                }else{
                    resolve(null);                   
                }
            } else if (xhr.status !== 200) {
                resolve("Error: Request failed, unbale to get response. Returned status: " + xhr.status);
            }
        };
        xhr.open("POST", url, true);
        xhr.setRequestHeader('Content-Type', 'application/json');
        xhr.withCredentials = true;
        xhr.send(postData);
    });
}
