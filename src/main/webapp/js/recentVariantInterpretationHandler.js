function loadRecentVariants(){
    url = "/rest/intro/getRecentlyInterVariants";  
    var xhr = new XMLHttpRequest();
    xhr.onload = function() {
        if (xhr.status === 200 && xhr.readyState == 4) {
            let variantIdsList = JSON.parse(xhr.responseText);               
            displayRecentlyInterpretedVariants(variantIdsList); 
        }else if (xhr.status !== 200) {
            alert('Request failed, returned status of ' + xhr.status);
        }
    };
    xhr.open('GET', url, true);
    xhr.setRequestHeader('Content-Type', 'application/json');
    xhr.send();
}

function displayRecentlyInterpretedVariants(variantIdsList){
    if(variantIdsList.length == 0){
        return;
    }
    let recentVariantsContainer = document.getElementById("recentVariantsContainer");
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
                calculateDivBtn.setAttribute("data-value", rVarObj.caid);
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
            patTypeDiv.className = "varTypeDiv";
            patTypeDiv.innerText = rVarObj.finalCall;
            varInfoDiv.appendChild(patTypeDiv);

        recentVariantsContainer.appendChild(varInfoDiv);
    }
}
