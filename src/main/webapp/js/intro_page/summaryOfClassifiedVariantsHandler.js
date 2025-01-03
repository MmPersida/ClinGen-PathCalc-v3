function loadSummaryOfClassifiedVariants(){
    url = "/pcalc/rest/intro/getSummaryOfClassifiedVariants";  
    var xhr = new XMLHttpRequest();
    xhr.onload = function() {
        if (xhr.status === 200 && xhr.readyState == 4) {
            let summaryList = JSON.parse(xhr.responseText);    
            if(summaryList != null){
                displaySummaryOfClassifiedVariants(summaryList); 
            }
        }else if (xhr.status !== 200) {
            alert('Request failed, returned status of ' + xhr.status);
        }
    };
    xhr.open('GET', url, true);
    xhr.setRequestHeader('Content-Type', 'application/json');
    xhr.send();
}

function displaySummaryOfClassifiedVariants(summaryList){
    let variantSummaryTable = document.getElementById('variantSummaryTable');

    let tr = null;
    let th = null;
    let td = null;
    let btn = null;
    for(let i in summaryList){
        let rowData = summaryList[i];
        tr = document.createElement('tr');

        for(let r in rowData){
            let tdObj = rowData[r];
            if(i == 0){
                th = document.createElement('th');
                if(tdObj.strValue != null){
                    let value = tdObj.strValue;
                    if(value == 'Uncertain Significance - Insufficient Evidence'){
                        value = 'VUS-Insufficient';
                    }else if(value == 'Uncertain Significance - Conflicting Evidence'){
                        value = "VUS-Conflicting";
                    }
                    th.innerHTML = value;
                }else{
                    th.innerHTML = '';
                }
                tr.appendChild(th);                
            }else{
                td = document.createElement('td');
                td.style.textAlign = 'center';
                if(r == 0){
                    td.style.textAlign = 'left';
                    td.innerHTML = tdObj.strValue;
                }else if(r > 0 && tdObj.numberOfCaids == 0){
                    td.innerHTML = tdObj.numberOfCaids;
                }else{    
                        btn = document.createElement("button");
                        btn.value = tdObj.caidsList;
                        btn.innerText = tdObj.numberOfCaids;
                        btn.style.padding = "1px 5px 1px 5px";
                        btn.style.margin = "1px"
                        btn.style.cursor = "pointer";
                        btn.addEventListener("click", function(){ openVariantCollectionPopUpNoInput(this) });
                    td.appendChild(btn);
                }
                tr.appendChild(td);
            }
        }
        variantSummaryTable.appendChild(tr);
    }
}

function openVariantCollectionPopUpNoInput(btnElem){
    if(btnElem.value == null){
        return;
    }
    openUserVariantCollectionPopUpNoInputs();

    let caidsListNonFormated = btnElem.value;
    let caidsList = caidsListNonFormated.split(",");
    if(caidsList == null || caidsList.length == 0){
        return;
    }

    displayVariantCollectionForCurrentUser(caidsList);
}
