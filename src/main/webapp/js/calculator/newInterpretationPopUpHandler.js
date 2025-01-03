function openNewInterpretationPopUp(viBasicDataList, condition, modeOfInheritance, cspecengineId, cspecengineName){
    document.getElementById("openNewInterpretationModal").click();
    displayAlreadyInterpretedVariants(viBasicDataList, condition, modeOfInheritance, cspecengineId, cspecengineName); 
} 
  
function closeNewInterpretationPopUp(){
  clearSelectChooser(document.getElementById("alreadyInterpretedVariantsDiv"));
  document.getElementById("openNewInterpretationModal").click(); 
}

function displayAlreadyInterpretedVariants(viBasicDataList, condition, modeOfInheritance, cspecengineId, cspecengineName){
    var startNewInterpretationBtn = document.getElementById("startNewInterpretationBtn");
    startNewInterpretationBtn.setAttribute('data-value', condition+"_"+modeOfInheritance+"_"+cspecengineName+"_"+cspecengineId);

    displayRecentlyInterpretedVariants(document.getElementById("alreadyInterpretedVariantsDiv"), viBasicDataList);
}

