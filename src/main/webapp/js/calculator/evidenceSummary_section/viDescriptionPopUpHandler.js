var viDescriptionExpanded = false;

async function openVIDescriptionPoPup(){
    document.getElementById("openVIDescriptionModal").click();

    let  viDescText = null;
    viDescText = await loadVIDescriptionText();

    if(viDescText != null && viDescText != ''){
        let viDescriptionFullTA = document.getElementById("viDescriptionFullTA");                                                              
        viDescriptionFullTA.value = viDescText;
    }
}

function closeVIDescriptionPoPup(){
  document.getElementById("viDescriptionFullTA").value = '';
  document.getElementById("openVIDescriptionModal").click();
}

async function expandVIDescriptionText(expandBtnElem){
    let  viDescText = null;
    let viDescriptionDiv = document.getElementById("viDescriptionDiv");

    if(!viDescriptionExpanded){
        viDescText = await loadVIDescriptionText();        
        if(viDescText != null && viDescText != ''){
            viDescriptionDiv.innerHTML = viDescText;
        }
        expandBtnElem.style.backgroundImage = 'url("../images/up-arrow.png")';
        viDescriptionExpanded = true;
    }else{
        viDescriptionDiv.innerHTML = (viDescriptionDiv.innerHTML).substring(0, 95)+"...";
        expandBtnElem.style.backgroundImage = 'url("../images/down-arrow.png")';
        viDescriptionExpanded = false;
    }
}

function loadVIDescriptionText(){
    if(variantInterpretationID == null || variantInterpretationID == ''){
        alert("Error: Unknown Variant Classification ID!")
        return;
    }
    var postData = {
        "interpretationId": variantInterpretationID,
    }

	return new Promise(function (resolve, reject) {
        postData = JSON.stringify(postData);

		var xhr = new XMLHttpRequest();
		var url = "/pcalc/rest/interpretation/loadViDescription";
		xhr.onload = function() {
			if (xhr.status === 200 && xhr.readyState == 4) {
				if(xhr.responseText != null && xhr.responseText  != ''){				
					resolve(xhr.responseText);					
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
                viDescriptionExpanded = false;
                document.getElementById("expandVIDescDivBtn").style.backgroundImage = 'url("../images/down-arrow.png")';
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
