function openVIDescriptionPoPup(){
    document.getElementById("openVIDescriptionModal").click();
    loadVIDescriptionText();
}

function closeVIDescriptionPoPup(){
  document.getElementById("viDescriptionFullTA").value = '';
  document.getElementById("openVIDescriptionModal").click();
}

function loadVIDescriptionText(){
    if(variantInterpretationID == null || variantInterpretationID == ''){
        alert("Error: Unknown Variant Classification ID!")
        return;
    }

    var postData = {
        "interpretationId": variantInterpretationID,
    }
    postData = JSON.stringify(postData);

    var xhr = new XMLHttpRequest();
    var url = "/pcalc/rest/interpretation/loadViDescription";
    xhr.onload = function() {
        if (xhr.status === 200 && xhr.readyState == 4) {
            if(xhr.responseText != null && xhr.responseText  != ''){
                document.getElementById("viDescriptionFullTA").value = xhr.responseText;                                                              
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

function saveChangedOrNewVIComment(){
    if(variantInterpretationID == null || variantInterpretationID == ''){
        alert("Error: Unknown Variant Classification ID!")
        return;
    }
    let description  = document.getElementById("viDescriptionFullTA").value;

    var postData = {
        "interpretationId": variantInterpretationID,
        "viDescription": description
    }
    postData = JSON.stringify(postData);
    
    var xhr = new XMLHttpRequest();
    var url = "/pcalc/rest/interpretation/saveEditVIDescription";
    xhr.onload = function() {
        if (xhr.status === 200 && xhr.readyState == 4) {
            if(xhr.responseText != null && xhr.responseText  != ''){
                setVIDescriptionHTMLEleme(description);
                closeVIDescriptionPoPup();                                                             
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
