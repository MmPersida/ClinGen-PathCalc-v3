function openEvidenceDocInputPopUp(){
    document.getElementById("openEvidenceDocInpModal").click();
    let conditionTermInp = document.getElementById("conditionTermInp");

    var conditionName = null;
    let engineId = null;
    let engineName = null;
    var geneName = null;

    var evidenceDocValueDiv = document.getElementById("evidenceDocValue");
    var inheritanceValueDiv =  document.getElementById("inheritanceValue");
    var engineIdValueDiv =  document.getElementById("engineIdValue");
    var engineNameValueDiv =  document.getElementById("engineNameValue");
    if(evidenceDocValueDiv.style.display == "block" && inheritanceValueDiv.style.display == "block" && engineNameValueDiv.style.display == "block"){
        conditionName = evidenceDocValueDiv.innerHTML.trim();
        conditionTermInp.value = conditionName;
        document.getElementById("modeOfInheritanceInp").value = inheritanceValueDiv.innerHTML.trim();

        engineId = engineIdValueDiv.innerHTML.trim();
        document.getElementById("cspecengineIdP").innerHTML = engineId;
        engineName = engineNameValueDiv.innerHTML.trim();
        document.getElementById("cspecengineName").innerHTML = engineName;
    }

    geneName = getSelectedIdentifierType(document.getElementsByName('mainGeneSelectRadioGroup'));
    if(geneName == null){
      geneName = "N/A"; //this is to make sure that the sql query wil work no mater what
    }
    if(conditionName != null && geneName != null && engineId != null){
      displaySortedCSpecEnginesList(conditionName, geneName, engineId);
    }
    conditionsInpAutocompleteHandler(conditionTermInp);
} 

function closeEvidenceDocInputPopUp(){
  resetEvidenceDocInpFields()
  document.getElementById("openEvidenceDocInpModal").click(); 
}

function resetEvidenceDocInpFields(){
    document.getElementById("conditionTermInp").value = "";
    document.getElementById("modeOfInheritanceInp").value = "";
    document.getElementById("cspecengineIdP").innerHTML = "";
    document.getElementById("cspecengineName").innerHTML = "";
}

async function saveNewEvidenceDoc(){
  let condition = document.getElementById("conditionTermInp").value.trim();
  let modeOfInheritance = document.getElementById("modeOfInheritanceInp").value.trim();
  let cspecengineId = document.getElementById("cspecengineIdP").innerHTML.trim();
  let cspecengineName = document.getElementById("cspecengineName").innerHTML.trim();

  if(condition == null || condition == '' || modeOfInheritance == null || modeOfInheritance == '' || cspecengineId == null || cspecengineId == ''){            
    alert("Error: No values are set!");
    return;
  }
 
  closeEvidenceDocInputPopUp();
  if(variantInterpretationID > 0){
    //this can be only done on an existing VI
    updateEvidenceDoc(condition, modeOfInheritance, cspecengineId);
  }else{
    //user is attempting to create a new VI
    let viBasicDataList = await checkTheSelectedConditionAndInheritanceForThisCAID(condition, modeOfInheritance, cspecengineId);
    if(viBasicDataList != null && viBasicDataList.length > 0){
      //VI's with this CAID, condition and mode of inheritance already exists in the DB
      openNewInterpretationPopUp(viBasicDataList, condition, modeOfInheritance, cspecengineId, cspecengineName);
      return;
    }

    createNewInterpretationNoEvidences(condition, modeOfInheritance, cspecengineId);
    //now that the Vi exists in the DB we can display the evidence table and allow new evidences to be saved
    renderEvidenceTable(new Array());
    enableDeleteInterpretationBtn();
    enableVICommentsBtn();
  }
  setNewEvidenceDocValues(condition, modeOfInheritance, cspecengineName, cspecengineId);


  var formatEvidenceDoc = formatEvidenceDocForCspecCall(); //the pathogenicityEvidencesDoc will be used in the next step and it need to be ready by now
  if(formatEvidenceDoc.evidence != null){
      determineRuleSetAssertions(cspecengineId, formatEvidenceDoc.evidence);
      updateSummariesInEvidenceTagDataObj(cspecengineId);
  }  
}

function createNewInterpretation(divElem){
  var condAndModeOfInher = divElem.getAttribute("data-value");
  closeNewInterpretationPopUp();
  if(condAndModeOfInher != null){
      let condAndModeOfInherEngineIdArray = condAndModeOfInher.split("_");
      
      createNewInterpretationNoEvidences(condAndModeOfInherEngineIdArray[0], condAndModeOfInherEngineIdArray[1], condAndModeOfInherEngineIdArray[3]);
      setNewEvidenceDocValues(condAndModeOfInherEngineIdArray[0], condAndModeOfInherEngineIdArray[1], condAndModeOfInherEngineIdArray[2], condAndModeOfInherEngineIdArray[3]);
      renderEvidenceTable(new Array());
      enableDeleteInterpretationBtn();
      enableVICommentsBtn();

      var formatEvidenceDoc = formatEvidenceDocForCspecCall(); //the pathogenicityEvidencesDoc will be used in the next step and it need to be ready by now
      if(formatEvidenceDoc.evidence != null){
          determineRuleSetAssertions(condAndModeOfInherEngineIdArray[3], formatEvidenceDoc.evidence);
          updateSummariesInEvidenceTagDataObj(condAndModeOfInherEngineIdArray[3]);
      }else{
          alert("Error: Unable to get current evidence list!")
      } 
  }
}

function setNewEvidenceDocValues(condition, modeOfInheritance, engineName, engineId){
    var evidenceDocValueDiv = document.getElementById("evidenceDocValue");
    var inheritanceValueDiv =  document.getElementById("inheritanceValue");
    var engineIdValue =  document.getElementById("engineIdValue");
    var engineNameValue =  document.getElementById("engineNameValue");
    evidenceDocValueDiv.style.display = "block";
    evidenceDocValueDiv.innerHTML = condition;
    inheritanceValueDiv.style.display = "block";
    inheritanceValueDiv.innerHTML = modeOfInheritance;
    engineNameValue.style.display = "block";
    engineNameValue.innerHTML = engineName;
    engineNameValue.setAttribute('data-value', engineId);
    engineIdValue.innerHTML = engineId;
} 

function updateEvidenceDoc(condition, modeOfInheritance, cspecengineId){
  var postData = {
    'caid': variantCID,
    'interpretationId': variantInterpretationID,
    'conditionId': null,
    'condition': condition,
    'inheritanceId': null,
    'inheritance': modeOfInheritance,
    'cspecengineId': cspecengineId
  } 

  postData = JSON.stringify(postData);

  var xhr = new XMLHttpRequest();
  var url = "/pcalc/rest/interpretation/updateEvidenceDocAndEngine";
  xhr.onload = function() {
      if (xhr.status === 200 && xhr.readyState == 4) {
          if(xhr.responseText != null && xhr.responseText  != ''){
              var jsonObj = JSON.parse(xhr.responseText);               
              if(jsonObj.message != null && jsonObj.message != ''){
                openNotificationPopUp(jsonObj.message, null);
              }else{
                cspecEngineID = jsonObj.cspecengineId;
                cspecRuleSetID = jsonObj.rulesetId;
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


function checkTheSelectedConditionAndInheritanceForThisCAID(condition, modeOfInheritance, cspecengineId){  
  return new Promise(function (resolve, reject) {
          var postData = {
            'caid': variantCID,
            'conditionId': null,
            'condition': condition,
            'inheritanceId': null,
            'inheritance': modeOfInheritance,
            'cspecengineId': cspecengineId
          } 
        
          postData = JSON.stringify(postData);
        
          var xhr = new XMLHttpRequest();
          var url = "/pcalc/rest/interpretation/searchInterpByCaidEvdcDocEngine";
          xhr.onload = function() {
              if (xhr.status === 200 && xhr.readyState == 4) {
                  if(xhr.responseText != null && xhr.responseText  != ''){
                      var jsonObj = JSON.parse(xhr.responseText);   
                      resolve(jsonObj);                                                                  
                  }else{
                    resolve(null);
                  }
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

function createNewInterpretationNoEvidences(condition, modeOfInheritance, cspecengineId){
  var postData = {
    'caid': variantCID,
    'conditionId': null,
    'condition': condition,
    'inheritanceId': null,
    'inheritance': modeOfInheritance,
    'cspecengineId': cspecengineId
  } 

  let geneName = getSelectedIdentifierType(document.getElementsByName('mainGeneSelectRadioGroup'));
  if(geneName != null && geneName != ''){
    postData.geneName = geneName;
  }

  postData = JSON.stringify(postData);

  var xhr = new XMLHttpRequest();
  var url = "/pcalc/rest/interpretation/saveNewInterpretation";
  xhr.onload = function() {
      if (xhr.status === 200 && xhr.readyState == 4) {
          if(xhr.responseText != null && xhr.responseText  != ''){
              var jsonObj = JSON.parse(xhr.responseText);
              if(jsonObj.interpretationId != null && jsonObj.interpretationId != ''){
                variantInterpretationID = Number(jsonObj.interpretationId); 
                cspecEngineID = jsonObj.cspecengineId;
                cspecRuleSetID = jsonObj.rulesetId;

                setPageURLToIncludeNewViId(variantInterpretationID)
              }else if(jsonObj.message != null && jsonObj.message != ''){
                openNotificationPopUp(jsonObj.message, null);
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

function setPageURLToIncludeNewViId(variantInterpretationID){
  let url = new URL(window.location.href);
  url.searchParams.set("viId", variantInterpretationID);
  history.pushState({}, "", url);
}

function loadModesOfInheritance(){
    var xhr = new XMLHttpRequest();	
    let url = "/pcalc/rest/calculator/getInheritanceModes";

    xhr.onload = function() {
        if (xhr.status === 200 && xhr.readyState == 4) {		
            if(xhr.responseText != null){
                let modesOfInheritanceList = JSON.parse(xhr.responseText);
                addModesOfInheritanceAsOptions(modesOfInheritanceList)
            }
        } else if (xhr.status !== 200) {
            alert('Request failed, returned status of ' + xhr.status);
        }
    };
    xhr.open('GET', url, true);
    xhr.setRequestHeader('Content-Type', 'application/json');
    xhr.send();
}

function addModesOfInheritanceAsOptions(modesOfInheritanceList){
    var modeOfInheritanceInpSelect = document.getElementById("modeOfInheritanceInp");

    let option = null;

    option = document.createElement("option");
    option.value = "";
    option.innerHTML = "Choose a value...";
    option.disabled = true;
    option.selected = true;
    option.style.color = "lightgrey";
    modeOfInheritanceInpSelect.appendChild(option);
    
    for(let i in modesOfInheritanceList){
        let iObj = modesOfInheritanceList[i];
        option = document.createElement("option");
        option.value = iObj.term; //iObj.id
        option.innerHTML = iObj.term;
        modeOfInheritanceInpSelect.appendChild(option);
    }
}

function resortCSpecEngineList(newConditionValue){
  //resort engines, a new condition was selected
  let engineId =  document.getElementById("engineIdValue").innerHTML.trim();
  let geneName = getSelectedIdentifierType(document.getElementsByName('mainGeneSelectRadioGroup'));
  if(geneName == null){
    geneName = "N/A"; //this is to make sure that the sql query wil work no mater what
  }
  if(engineId == null || geneName == null){
    return;
  }
  displaySortedCSpecEnginesList(newConditionValue, geneName, engineId);
}

async function displaySortedCSpecEnginesList(conditionName, geneName, engineId){
  let cSpecEngineListContainer = document.getElementById("cSpecEngineListContainer");
  disableElement(cSpecEngineListContainer);

  var cSpecEnginesLists = await loadCSpecEngineInfoList(conditionName, geneName); 

  if(cSpecEnginesLists == null){
    alert("Error: Engines list in null or empty!");
    return;
  }
  clearSelectChooser(cSpecEngineListContainer);

  let engineGroupDiv = null;
  if(cSpecEnginesLists.geneAndConditionList != null && cSpecEnginesLists.geneAndConditionList.length > 0){
    engineGroupDiv = createGroupEnginesHTMLObj(conditionName+" & "+geneName);
    createEngineHTMLList(engineGroupDiv, cSpecEnginesLists.geneAndConditionList, engineId);
    cSpecEngineListContainer.appendChild(engineGroupDiv);
  }

  if(cSpecEnginesLists.geneList != null && cSpecEnginesLists.geneList.length > 0){
    engineGroupDiv = createGroupEnginesHTMLObj(geneName);
    createEngineHTMLList(engineGroupDiv, cSpecEnginesLists.geneList, engineId);
    cSpecEngineListContainer.appendChild(engineGroupDiv);
  }

  if(cSpecEnginesLists.conditionList != null && cSpecEnginesLists.conditionList.length > 0){
    engineGroupDiv = createGroupEnginesHTMLObj(conditionName);
    createEngineHTMLList(engineGroupDiv, cSpecEnginesLists.conditionList, engineId);
    cSpecEngineListContainer.appendChild(engineGroupDiv);
  }

  if(cSpecEnginesLists.othersList != null && cSpecEnginesLists.othersList.length > 0){
    engineGroupDiv = createGroupEnginesHTMLObj("Other Criteria Specifications");
    createEngineHTMLList(engineGroupDiv, cSpecEnginesLists.othersList, engineId);
    cSpecEngineListContainer.appendChild(engineGroupDiv);
  }

  enableElement(cSpecEngineListContainer);
}

function loadCSpecEngineInfoList(conditionName, geneName){
  return new Promise(function (resolve, reject) {
    var postData = {
      'condition': conditionName,
      'gene': geneName
    };
    postData = JSON.stringify(postData);
  
    var xhr = new XMLHttpRequest();
    let url = "/pcalc/rest/cspecengines/getSortedCSpecEngines";
    xhr.onload = function() {
        if (xhr.status === 200 && xhr.readyState == 4) {
            if(xhr.responseText != null && xhr.responseText  != ''){
              resolve(JSON.parse(xhr.responseText))                                                                   
            }else{
              resolve(null);
            }
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

function setEngineAndRuleSetID(divElement){
  let cspecengineIdP = document.getElementById("cspecengineIdP");

  replaceClassInElement(divElement, 'engineInfoDivUnselected', 'engineInfoDivSelected'); //mark (select) the one clicked on
  let currentEngineId = cspecengineIdP.innerHTML;
  if(currentEngineId != null && currentEngineId != ''){
    //if any other is previously selected, deselect it
    replaceClassInElement(document.getElementById(currentEngineId), 'engineInfoDivSelected', 'engineInfoDivUnselected'); 
  }

  let dataValue = divElement.getAttribute("data-value");
  let dataValueArray = dataValue.split("|");

  cspecengineIdP.innerHTML = dataValueArray[0].trim(); //engine id
  document.getElementById("cspecengineName").innerHTML = dataValueArray[1].trim(); //engine name (organization)
}

function conditionsInpAutocompleteHandler(inpElem) {
    /*the autocomplete function takes two arguments,
    the text field element and an array of possible autocompleted values:*/
    var currentFocus;
    /*execute a function when someone writes in the text field:*/
    inpElem.addEventListener("input", function(){ processInputtedTerm(this); });

    /*execute a function presses a key on the keyboard:*/
    inpElem.addEventListener("keydown", function(e) {
        var x = document.getElementById(this.id + "autocomplete-list");
        if (x) x = x.getElementsByTagName("div");
        if (e.keyCode == 40) {
          /*If the arrow DOWN key is pressed,
          increase the currentFocus variable:*/
          currentFocus++;
          /*and and make the current item more visible:*/
          addActive(x);
        } else if (e.keyCode == 38) { //up
          /*If the arrow UP key is pressed,
          decrease the currentFocus variable:*/
          currentFocus--;
          /*and and make the current item more visible:*/
          addActive(x);
        } else if (e.keyCode == 13) {
          /*If the ENTER key is pressed, prevent the form from being submitted,*/
          e.preventDefault();
          if (currentFocus > -1) {
            /*and simulate a click on the "active" item:*/
            if (x) x[currentFocus].click();
          }
        }
    });

    function processInputtedTerm(thisInpElem){
      var a, b, i, val = thisInpElem.value;
      /*close any already open lists of autocompleted values*/
      closeAllLists();
      if (!val || val.length < 3) { return false;}
      currentFocus = -1;
      /*create a DIV element that will contain the items (values):*/
      a = document.createElement("DIV");
      a.setAttribute("id", thisInpElem.id + "autocomplete-list");
      a.setAttribute("class", "autocomplete-items");
      /*append the DIV element as a child of the autocomplete container:*/
      thisInpElem.parentNode.appendChild(a);
      getConditionsLike(thisInpElem,a, b, i, val);
    }

    function getConditionsLike(thisInpElem, a, b, i, conditionTerm){
      var xhr = new XMLHttpRequest();	
      let url = "/pcalc/rest/conditions/getConditionsLike/"+conditionTerm;
  
      xhr.onload = function() {
          if (xhr.status === 200 && xhr.readyState == 4) {		
              if(xhr.responseText != null){
                let condArray = JSON.parse(xhr.responseText);
                if(condArray == null || condArray.length == 0){
                  return;
                }

                let cond = null;
                let n = condArray.length;
                for (i = 0; i < n; i++) {
                  cond = condArray[i];
                  cond.term;
                  /*check if the item starts with the same letters as the text field value:*/
                  if (cond.term.substr(0, conditionTerm.length).toUpperCase() == conditionTerm.toUpperCase()) {
                    /*create a DIV element for each matching element:*/
                    b = document.createElement("DIV");
                    /*make the matching letters bold:*/
                    b.innerHTML = "<strong>" + cond.term.substr(0, conditionTerm.length) + "</strong>";
                    b.innerHTML += cond.term.substr(conditionTerm.length);

                    let encodedText = encodeHtmlEntities(cond.term);

                    /*insert a input field that will hold the current array item's value:*/
                    b.innerHTML += "<input type='hidden' value='" +encodedText+ "'>";
                    /*execute a function when someone clicks on the item value (DIV element):*/
                    b.addEventListener("click", function(e) {
                        /*insert the value for the autocomplete text field:*/
                        let newValue = this.getElementsByTagName("input")[0].value.trim();  
                        thisInpElem.value = newValue;                  
                        /*close the list of autocompleted values,
                        (or any other open lists of autocompleted values:*/
                        closeAllLists();
                        if(newValue != null && newValue != '' && newValue != undefined){
                          resortCSpecEngineList(newValue);
                        }
                    });
                    a.appendChild(b);
                  }
                }
              }else{
                return new [];
              }
          } else if (xhr.status !== 200) {
              alert('Request failed, returned status of ' + xhr.status);
          }
      };
      xhr.open('GET', url, true);
      xhr.setRequestHeader('Content-Type', 'application/json');
      xhr.send();
    }

    function addActive(x) {
      /*a function to classify an item as "active":*/
      if (!x) return false;
      /*start by removing the "active" class on all items:*/
      removeActive(x);
      if (currentFocus >= x.length) currentFocus = 0;
      if (currentFocus < 0) currentFocus = (x.length - 1);
      /*add class "autocomplete-active":*/
      x[currentFocus].classList.add("autocomplete-active");
    }

    function removeActive(x) {
      /*a function to remove the "active" class from all autocomplete items:*/
      for (var i = 0; i < x.length; i++) {
        x[i].classList.remove("autocomplete-active");
      }
    }

    function closeAllLists(elmnt) {
      /*close all autocomplete lists in the document,
      except the one passed as an argument:*/
      var x = document.getElementsByClassName("autocomplete-items");
      for (var i = 0; i < x.length; i++) {
        if (elmnt != x[i] && elmnt != inpElem) {
          x[i].parentNode.removeChild(x[i]);
        }
      }
    }
    /*execute a function when someone clicks in the document:*/
    document.addEventListener("click", function (e) {
        closeAllLists(e.target);
    });
}
