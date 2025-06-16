function loadRecentVariants(){
    url = "/pcalc/rest/intro/getRecentlyInterVariants";  
    var xhr = new XMLHttpRequest();
    xhr.onload = function() {
        if (xhr.status === 200 && xhr.readyState == 4) {
            let variantIdsList = JSON.parse(xhr.responseText);    

            let recentVariantsContainer = document.getElementById("recentVariantsContainer");           
            displayRecentlyInterpretedVariants(recentVariantsContainer, variantIdsList); 
        }else if (xhr.status !== 200) {
            alert('Request failed, returned status of ' + xhr.status);
        }
    };
    xhr.open('GET', url, true);
    xhr.setRequestHeader('Content-Type', 'application/json');
    xhr.send();
}
