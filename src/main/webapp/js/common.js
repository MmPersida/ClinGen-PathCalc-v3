function clearSelectChooser(elem){
    if(elem.firstChild != null){
        while (elem.firstChild) {
            elem.removeChild(elem.firstChild);
        }
    }
}

function isObjectEmpty(obj) {
    // null and undefined are "empty"
    if (obj == null) return true;

    // Assume if it has a length property with a non-zero value
    // that that property is correct.
    if (obj.length > 0)    return false;
    if (obj.length === 0)  return true;

    // If it isn't an object at this point
    // it is empty, but it can't be anything *but* empty
    // Is it empty?  Depends on your application.
    if (typeof obj !== "object") return true;

    // Otherwise, does it have any properties of its own?
    // Note that this doesn't handle
    // toString and valueOf enumeration bugs in IE < 9
    for (var key in obj) {
        if (hasOwnProperty.call(obj, key)) return false;
    }

    return true;
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

function checkVCEPNameFormat(vcepPartialVal){
    return true;
}

function extractAlleleExtRecordsNameAndLink(externalRecords){
    if(externalRecords == null || externalRecords.length == 0){
        return null;
    }
    var externalRecordsNameAndLink = {};
    var myKeys = Object.keys(externalRecords)
    var n = myKeys.length;
    for (var i = 0; i < n; i++){
        var iter = myKeys[i];
        var obj = externalRecords[iter][0];

        if(obj['@id'] != null){
                var link = obj["@id"];
                var erObj = {
                    'link':link,
                }
                if(obj.id != null){
                    erObj.id = obj.id;
                }
                externalRecordsNameAndLink[iter] = erObj;
        }
    } 
    return externalRecordsNameAndLink;
}

function createPCExternalLinks(externalSourceNameAndLinks, containerDiv, linkClassName){
    let a = null;
    let img = null;
    let p = null;

    var myKeys = Object.keys(externalSourceNameAndLinks)
    var n = myKeys.length;
    for (var i = 0; i < n; i++){
        var iter = myKeys[i];
        var obj = externalSourceNameAndLinks[iter];
        if(obj == null){
            continue;
        }
        a = document.createElement("a");
        a.className = linkClassName;
        a.href = obj.link;
        a.target="_blank";
            img = document.createElement("img");
            img.src = "../images/got_link_icon.png";
        a.appendChild(img);
            p = document.createElement("p");        
            p.innerText = iter;
        a.appendChild(p);  
        containerDiv.appendChild(a); 
    } 
}

function getAlleleRegistryDataForVariant(variantCaIdInp){  
    return new Promise(function (resolve, reject) {
        var xhr = new XMLHttpRequest();	
        let url = "/rest/calculator/alleleRepository/"+variantCaIdInp;

        xhr.onload = function() {
            if (xhr.status === 200 && xhr.readyState == 4) {		
                if(xhr.responseText != null && xhr.responseText != ""){
                    let alleleDataObj = JSON.parse(xhr.responseText);
                    resolve(alleleDataObj);  
                }else{
                    resolve("Unable to get response value from Allele Registry for variant with identifier: <b>"+variantCaIdInp+"</b>."+
                            "<br><b>Please try the following</b>: Check the used Variant Identifier value or selected type, you may have a typo error, and please try again or refresh the page!");                   
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

function getCSpecEngineInfo(cspecengineId){
    return new Promise(function (resolve, reject) {
        var xhr = new XMLHttpRequest();	
        let url = "/rest/cspecengines/getCSpecEngineInfo/"+cspecengineId;

        xhr.onload = function() {
            if (xhr.status === 200 && xhr.readyState == 4) {		
                if(xhr.responseText != null && xhr.responseText != ""){
                    let dataObj = JSON.parse(xhr.responseText);
                    resolve(dataObj);  
                }else{
                    resolve("Unable to get data for CSpecEngine with ID: "+cspecengineId);                   
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

function displayRecentlyInterpretedVariants(recentVariantsContainer, variantIdsList){
    if(variantIdsList.length == 0){
        return;
    }
    clearSelectChooser(recentVariantsContainer);

    let varInfoDiv = null;
    let pInfo = null;
    let patIdDiv = null;
    let patTypeDiv = null;

    for(let i in variantIdsList){
        let rVarObj = variantIdsList[i];
        varInfoDiv = document.createElement("div");
        varInfoDiv.className = "recentVariantInfo";

            patIdDiv = document.createElement("div");
            patIdDiv.className = "varPatogenicityIdDiv";
                pInfo = document.createElement("p");
                pInfo.style.margin = "0px";
                pInfo.innerText = (Number(i)+1)+". "+rVarObj.caid;
                patIdDiv.appendChild(pInfo);

                let calculateDivBtn = document.createElement("div");
                calculateDivBtn.className ="calculateDivBtn";
                calculateDivBtn.setAttribute('data-value', rVarObj.caid+"_"+rVarObj.interpretationId);
                calculateDivBtn.addEventListener("click", function(){ goToCalculatorPage(this) });
                patIdDiv.appendChild(calculateDivBtn);
            varInfoDiv.appendChild(patIdDiv);

            pInfo = document.createElement("p");
            pInfo.innerText = rVarObj.condition
            //pInfo.title = "Condition type"
            varInfoDiv.appendChild(pInfo);

            pInfo = document.createElement("p");
            pInfo.innerText = rVarObj.inheritance
            //pInfo.title = "Mode of Inheritance"
            varInfoDiv.appendChild(pInfo);

            patTypeDiv = document.createElement("div");
            patTypeDiv.className = "varTypeDiv varTypeDivYellow";
            patTypeDiv.innerText = rVarObj.finalCall;
            varInfoDiv.appendChild(patTypeDiv);

        recentVariantsContainer.appendChild(varInfoDiv);
    }
}

function goToCalculatorPage(someElemOrValue){
    let variantCaidAndViId = null;
    if(typeof someElemOrValue == 'object'){
        if(someElemOrValue.getAttribute("data-value") != null){
            variantCaidAndViId = someElemOrValue.getAttribute("data-value");
        }else if(someElemOrValue.getAttribute("id") != null){
            variantCaidAndViId = someElemOrValue.getAttribute("id");
        }
    }else if(typeof someElemOrValue == "string"){
        variantCaidAndViId = someElemOrValue;
    }

    if(variantCaidAndViId == null){
        openWarringDiv("Error: Unable to get the variant CAID for the calculator page.");  
        return;            
    }   
    
    let variantCaidAndViIdArray = variantCaidAndViId.split("_");
    window.location="calculator.html?caid="+variantCaidAndViIdArray[0]+"&viId="+variantCaidAndViIdArray[1];
}

function getFullTimeAndDate(dateString){
    //var d = Date.parse(dateString);
    var d = new Date(Number(dateString));
    return getDayLabel(d)+" "+d.toLocaleDateString()+" "+d.toLocaleTimeString()
}

function getDayLabel(date){
    var day = null;
    switch(date.getDay()){
        case 0:
            day = 'Sunday';
            break;
        case 1:
            day = 'Monday';
            break;
        case 2:
            day = 'Tuesday';
            break;
        case 3:
            day = 'Wednesday';
            break;
        case 4:
            day = 'Thursday';
            break;
        case 5:
            day = 'Friday';
            break;
        case 6:
            day = 'Saturday';
            break
        default:
            "None";
    }
    return day;
}

function addClassToElement(div, newClassName){
    div.classList.add(newClassName);
}

function removeClassFromElement(div, classToRemove){
    div.classList.remove(classToRemove);
}

function replaceClassInElement(div, oldClass, newClass){
    div.classList.replace(oldClass, newClass);
}

function disableElement(elem){
    elem.disabled = true;
}
  
function enableElement(elem){
    elem.disabled = false;
}

function createGroupEnginesHTMLObj(label){
    let engineGroupDiv = document.createElement("div");
    engineGroupDiv.className = "cspecEngineGroupDiv";
      let engineGroupP = document.createElement("p");
      engineGroupP.style.margin = "0px";
      engineGroupP.style.fontWeight = "600";
      engineGroupP.style.fontSize = "15px";
      engineGroupP.style.color = "rgba(50, 110, 150)";
      engineGroupP.innerText = label;
    engineGroupDiv.appendChild(engineGroupP);
    return engineGroupDiv;
  }

function createEngineHTMLList(cSpecEngineListContainer, cSpecEnginesInfoList, engineId){
    if(cSpecEnginesInfoList == null || cSpecEnginesInfoList.length == 0){
      return null;
    }
  
    let div;
    let p;
    let a;
    let num;
    for(let i in cSpecEnginesInfoList){
      let cSpecEngineInfo = cSpecEnginesInfoList[i];
      div  = document.createElement("div");
      div.id = cSpecEngineInfo.engineId;
      div.className = "cspecEngineInfoDiv";
      //if engineId is null than this is a new interpretation
      if(engineId != null && cSpecEngineInfo.engineId == engineId){
        addClassToElement(div, 'engineInfoDivSelected');
      }else{
        addClassToElement(div, 'engineInfoDivUnselected');
      }
        p = document.createElement("p");
        num = Number(i)+1;
        p.innerHTML = num+": <b>"+cSpecEngineInfo.engineId + " - "+cSpecEngineInfo.organizationName+"</b>"; 
      div.appendChild(p);
      if(cSpecEngineInfo.engineId != 'GN001'){
            a = document.createElement("a");
            a.style.display = "inline-block";
            a.style.color = "white";
            a.href = 'https://cspec.genome.network/cspec/ui/svi/doc/'+cSpecEngineInfo.engineId;
            a.target = 'blank';
                p = document.createElement("p");
                p.innerText = 'Specification Details';
            a.appendChild(p);
        div.appendChild(a);
      }

        

      /* engine summary
        p = document.createElement("p");
        p.innerHTML = "Summary: "+cSpecEngineInfo.engineSummary; 
      div.appendChild(p);
      */
      div.setAttribute('data-value', cSpecEngineInfo.engineId+"|"+cSpecEngineInfo.organizationName);
      div.addEventListener("click", function(){ setEngineAndRuleSetID(this) });
      cSpecEngineListContainer.appendChild(div);
    }
  }

  function getSelectedIdentifierType(radioGroup){
    let radioValue = null;
    let n = radioGroup.length;
    for(let i = 0; n > i; i++){
        let radioBtn = radioGroup[i];
        if(radioBtn != null && radioBtn.checked){
            radioValue = radioBtn.value.trim();
            break;
        }
    }
    if(radioValue != null){
        return radioValue;
    }
}
