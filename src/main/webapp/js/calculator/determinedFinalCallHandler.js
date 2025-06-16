function loadFinalCalls(){
    var xhr = new XMLHttpRequest();	
    let url = "/pcalc/rest/calculator/getFinalCalls";

    return new Promise(function (resolve, reject) {
        xhr.onload = function() {
            if (xhr.status === 200 && xhr.readyState == 4) {		
                if(xhr.responseText != null){
                    let finalCallsListList = JSON.parse(xhr.responseText);
                    addFinalCallsAsOptions(finalCallsListList)
                    resolve('ok');
                }
            } else if (xhr.status !== 200) {
                console.log('Request failed, returned status of ' + xhr.status);
            }
            resolve(null);
        };
        xhr.open('GET', url, true);
        xhr.setRequestHeader('Content-Type', 'application/json');
        xhr.send();
    });
}

function addFinalCallsAsOptions(finalCallsListList){
    var determinedFinalCallSelect = document.getElementById("determinedFinalCallSelect");
    if(determinedFinalCallSelect == null){
        return;
    }
    
    let option = null;

    option = document.createElement("option");
    option.value = "";
    option.innerHTML = "Choose a value...";
    option.disabled = true;
    option.selected = true;
    option.style.color = "lightgrey";
    determinedFinalCallSelect.appendChild(option);
    
    for(let i in finalCallsListList){
        let fcObj = finalCallsListList[i];
        option = document.createElement("option");
        option.value = fcObj.id;
        option.innerHTML = fcObj.term;
        determinedFinalCallSelect.appendChild(option);
    }
}

function getSelectedDeterminedFC(selectElem){
    let determinedFCid = selectElem.value;
    if(determinedFCid == null || determinedFCid == ''){
        return;
    }

    document.getElementById("saveDeterminedFCBtn").style.display = "block";
}

function saveDeterminedFinalCall(){
    let determinedFinalCallId = document.getElementById("determinedFinalCallSelect").value;
    if(determinedFinalCallId == null){
        return;
    }

    var postData = {
        "interpretationId": variantInterpretationID,
        "finalCallId": determinedFinalCallId
    }
    postData = JSON.stringify(postData);

    var xhr = new XMLHttpRequest();
    var url = "/pcalc/rest/interpretation/saveDeterminedFC";
    xhr.onload = function() {
        if (xhr.status === 200 && xhr.readyState == 4) {
            if(xhr.responseText != null && xhr.responseText  != ''){
                var jsonObj = JSON.parse(xhr.responseText);
                if(jsonObj.message != null && jsonObj.message != ''){
                    openNotificationPopUp(jsonObj.message, null);
                }else{
                    document.getElementById("saveDeterminedFCBtn").style.display = "none";
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

function updateDeterminedFinalCallHTML(determinedFCObj){
    if(determinedFCObj == null){
        return;
    }
    document.getElementById("determinedFinalCallSelect").value = determinedFCObj.id;
}

