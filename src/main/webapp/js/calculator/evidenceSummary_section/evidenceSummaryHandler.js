function updateCalculatedFinalCallHTML(finalCallObj){
    document.getElementById("finalCallValue").innerHTML = finalCallObj.term;
    document.getElementById("finalCallId").innerHTML = finalCallObj.id;
}

function setVIDescriptionHTMLEleme(viDescription){
    document.getElementById("viDescriptionDiv").innerHTML = (viDescription.substring(0, 95))+"...";
}

function enableDeleteInterpretationBtn(){
    document.getElementById("deleteInterpretationDivBtn").style.display = "flex";
}

function enableVICommentsBtn(){
    document.getElementById("editInterpDescriptionDivBtn").style.display = "flex";
}

function deleteThisInterpretation(){
    if(variantInterpretationID == null || variantInterpretationID == ''){
        alert("Error: Unknown Variant Classification ID!")
        return;
    }

    var postData = {
        "interpretationId": variantInterpretationID
    }
    postData = JSON.stringify(postData);

    var xhr = new XMLHttpRequest();
    var url = "/pcalc/rest/interpretation/deleteInterpretation";
    xhr.onload = function() {
        if (xhr.status === 200 && xhr.readyState == 4) {
            if(xhr.responseText != null && xhr.responseText  != ''){
                var jsonObj = JSON.parse(xhr.responseText);
                if(jsonObj.message != null && jsonObj.message != ''){
                    openNotificationPopUp(jsonObj.message, null);
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
    xhr.withCredentials = true;
    xhr.send(postData);
}
