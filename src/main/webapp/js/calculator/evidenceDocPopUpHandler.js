function openEvidenceDocInputPopUp(){
    document.getElementById("openEvidenceDocInpModal").click();
    let conditionTermInp = document.getElementById("conditionTermInp");

    var evidenceDocValueDiv = document.getElementById("evidenceDocValue");
    var inheritanceValueDiv =  document.getElementById("inheritanceValue");
    if(evidenceDocValueDiv.style.display == "block" && inheritanceValueDiv.style.display == "block"){
        conditionTermInp.value = evidenceDocValueDiv.innerHTML.trim();
        document.getElementById("modeOfInheritanceInp").value = inheritanceValueDiv.innerHTML.trim();
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
}

async function saveNewEvidenceDoc(){
  let condition = document.getElementById("conditionTermInp").value.trim();
  let modeOfInheritance = document.getElementById("modeOfInheritanceInp").value.trim();
  if(condition == null || condition == '' || modeOfInheritance == null || modeOfInheritance == ''){            
    alert("Error: No values are set!");
    return;
  }

  closeEvidenceDocInputPopUp();
  if(variantInterpretationID > 0){
    //this can be only done on an existing VI
    updateEvidenceDoc(condition, modeOfInheritance);
  }else{
    let viBasicDataList = await checkTheSelectedConditionAndInheritanceForThisCAID(condition, modeOfInheritance);
    if(viBasicDataList != null && viBasicDataList.length > 0){
      //VI's with this CAID, condition and mode of inheritance already exists in the DB
      openNewInterpretationPopUp(viBasicDataList, condition, modeOfInheritance);
      return;
    }

    createNewInterpretationNoEvidences(condition, modeOfInheritance);
    //now that the Vi exists in the DB we can display the evidence table and allow new evidences to be saved
    let array = []
    renderEvidenceTable(array);
  }
  setNewEvidenceDocValues(condition, modeOfInheritance);
}

function createNewInterpretation(divElem){
  var condAndModeOfInher = divElem.getAttribute("data-value");
  closeNewInterpretationPopUp();
  if(condAndModeOfInher != null){
      let condAndModeOfInherArray = condAndModeOfInher.split("_");
      
      createNewInterpretationNoEvidences(condAndModeOfInherArray[0], condAndModeOfInherArray[1]);
      setNewEvidenceDocValues(condAndModeOfInherArray[0], condAndModeOfInherArray[1]);
      let array = []
      renderEvidenceTable(array);
  }
}

function setNewEvidenceDocValues(evidenceType, modeOfInheritance){
    var evidenceDocValueDiv = document.getElementById("evidenceDocValue");
    var inheritanceValueDiv =  document.getElementById("inheritanceValue");
    evidenceDocValueDiv.style.display = "block";
    evidenceDocValueDiv.innerHTML = evidenceType;
    inheritanceValueDiv.style.display = "block";
    inheritanceValueDiv.innerHTML = modeOfInheritance;   
} 

function updateEvidenceDoc(condition, modeOfInheritance){
  var postData = {
    'caid': variantCID,
    'interpretationId': variantInterpretationID,
    'conditionId': null,
    'condition': condition,
    'inheritanceId': null,
    'inheritance': modeOfInheritance
  } 

  postData = JSON.stringify(postData);

  var xhr = new XMLHttpRequest();
  var url = "/rest/interpretation/updateEvidenceDoc";
  xhr.onload = function() {
      if (xhr.status === 200 && xhr.readyState == 4) {
          if(xhr.responseText != null && xhr.responseText  != ''){
              var jsonObj = JSON.parse(xhr.responseText);    
              if(jsonObj.message != null && jsonObj.message != ''){
                openNotificationPopUp(jsonObj.message);
              }                                               
          }
      }else if (xhr.status !== 200) {
          alert('Request failed, returned status of ' + xhr.status);
      }
  };
  xhr.open("POST", url, true);
  xhr.setRequestHeader('Content-Type', 'application/json');
  xhr.send(postData);
}


function checkTheSelectedConditionAndInheritanceForThisCAID(condition, modeOfInheritance){  
  return new Promise(function (resolve, reject) {
          var postData = {
            'caid': variantCID,
            'conditionId': null,
            'condition': condition,
            'inheritanceId': null,
            'inheritance': modeOfInheritance
          } 
        
          postData = JSON.stringify(postData);
        
          var xhr = new XMLHttpRequest();
          var url = "/rest/interpretation/searchInterpByCaidEvidenceDoc";
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
          xhr.send(postData);
  });
}

function createNewInterpretationNoEvidences(condition, modeOfInheritance){
  var postData = {
    'caid': variantCID,
    'conditionId': null,
    'condition': condition,
    'inheritanceId': null,
    'inheritance': modeOfInheritance
  } 

  postData = JSON.stringify(postData);

  var xhr = new XMLHttpRequest();
  var url = "/rest/interpretation/saveNewInterpretation";
  xhr.onload = function() {
      if (xhr.status === 200 && xhr.readyState == 4) {
          if(xhr.responseText != null && xhr.responseText  != ''){
              var jsonObj = JSON.parse(xhr.responseText);
              if(jsonObj.interpretationId != null && jsonObj.interpretationId != ''){
                variantInterpretationID = Number(jsonObj.interpretationId); 
                setPageURLToIncludeNewViId(variantInterpretationID)
              }else if(jsonObj.message != null && jsonObj.message != ''){
                openNotificationPopUp(jsonObj.message);
              }                                                               
          }
      }else if (xhr.status !== 200) {
          alert('Request failed, returned status of ' + xhr.status);
      }
  };
  xhr.open("POST", url, true);
  xhr.setRequestHeader('Content-Type', 'application/json');
  xhr.send(postData);
}

function setPageURLToIncludeNewViId(variantInterpretationID){
  let url = new URL(window.location.href);
  url.searchParams.set("viId", variantInterpretationID);
  history.pushState({}, "", url);
}

function loadModesOfInheritance(){
    var xhr = new XMLHttpRequest();	
    let url = "/rest/calculator/getInheritanceModes";

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
    /*                       
    <option value="" disabled selected style="color:lightgrey;">Choose a value...</option>
    <option value="Autosomal Dominant">Autosomal Dominant</option>
    <option value="Autosomal Recessive">Autosomal Recessive</option>
    <option value="X-linked Dominant">X-linked Dominant</option>
    <option value="X-linked Recessive">X-linked Recessive</option>
    <option value="Mitochondrial">Mitochondrial</option>
    <option value="Multifactoral">Multifactoral</option>
    <option value="Other">Other</option>
    <option value="Unknown">Unknown</option>*/
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
      let url = "/rest/conditions/getConditionsLike/"+conditionTerm;
  
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
                    /*insert a input field that will hold the current array item's value:*/
                    b.innerHTML += "<input type='hidden' value='" + cond.term + "'>";
                    /*execute a function when someone clicks on the item value (DIV element):*/
                    b.addEventListener("click", function(e) {
                        /*insert the value for the autocomplete text field:*/
                        thisInpElem.value = this.getElementsByTagName("input")[0].value;
                        /*close the list of autocompleted values,
                        (or any other open lists of autocompleted values:*/
                        closeAllLists();
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
