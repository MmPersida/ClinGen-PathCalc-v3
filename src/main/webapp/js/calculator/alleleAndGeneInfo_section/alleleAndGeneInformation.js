async function displayAlleleAndGeneInformation(variantCAID, alleleDataObj){
    //Allele
    let externalRecordsNameAndLink = null;
    if(alleleDataObj != null){
        let externalRecords_1 = document.getElementById("externalRecords_1");
        externalRecordsNameAndLink = extractAlleleExtRecordsNameAndLink(variantCAID, alleleDataObj);
        if(externalRecordsNameAndLink != null){
            createPCExternalLinks(externalRecordsNameAndLink, externalRecords_1, "alleleGeneLinksCalc");

            let predictorScoreDivBtn = document.getElementById("predictorScoreDivBtn");
            let alleleFerquencyDivBtn = document.getElementById("alleleFerquencyDivBtn");

            if(externalRecordsNameAndLink.MyVariantInfo_hg19 != null){
                let hgvsValue = externalRecordsNameAndLink.MyVariantInfo_hg19.id;
                predictorScoreDivBtn.setAttribute('data-value', hgvsValue);
                alleleFerquencyDivBtn.setAttribute('data-value', hgvsValue);
            }else{
                predictorScoreDivBtn.style.display = "none";
                alleleFerquencyDivBtn.style.display = "none";
            }
        }
    }

    //HGVS external records
    let varGoogleLink = document.getElementById("varGoogleLink");
    varGoogleLink.href = "https://www.google.com/search?q="+variantCID;

    /*
    let varBeaconseLink = document.getElementById("varBeaconseLink");
    varBeaconseLink.href = "https://beacon-network.org/";

    let varVarsomeLink = document.getElementById("varVarsomeLink");
    varVarsomeLink.href = "https://varsome.com/variant/";
    */
   
    /*
    href="https://www.google.com/#q=NDUFS8+AND+%22c.64C%3ET%22+OR+%22c.56-632C%3ET%22+OR+%22c.-67%2B1558C%3ET%22+OR+%22c.119C%3ET%22+OR+%22c.-244C%3ET%22"   
    href="https://beacon-network.org//#/search?pos=67799758&amp;chrom=11&amp;allele=T&amp;ref=C&amp;rs=GRCh37"   
    href="https://varsome.com/variant/hg19/11-67799758-C-T" 
    */            

    var hgvsLinkString = "";
    var hgvsLinksForGenomicAllelesDiv = null;

    //HGVS - Genomic Alleles
    if(alleleDataObj.genomicAlleles != null && alleleDataObj.genomicAlleles.length > 0){
        if(hgvsLinksForGenomicAllelesDiv == null){
            hgvsLinksForGenomicAllelesDiv = document.getElementById("hgvsLinksForGenomicAlleles");
        }

        var genomicAlleles = alleleDataObj.genomicAlleles;
        var n = genomicAlleles.length;
        for (var i = 0; i < n; i++){
            var obj = genomicAlleles[i];
            var hgvsLinks = obj.hgvs;
            var j = hgvsLinks.length;
            for (var k = 0; k < j; k++){
                var hgvsLink =hgvsLinks[k];
                hgvsLinkString = hgvsLinkString+hgvsLink+", ";
            }
        }
    }

    //HGVS - Transcript Alleles
    if(alleleDataObj.transcriptAlleles != null && alleleDataObj.transcriptAlleles.length > 0){
        if(hgvsLinksForGenomicAllelesDiv == null){
            hgvsLinksForGenomicAllelesDiv = document.getElementById("hgvsLinksForGenomicAlleles");
        }

        var transcriptAlleles = alleleDataObj.transcriptAlleles;
        var n = transcriptAlleles.length;
        for (var i = 0; i < n; i++){
            var obj = transcriptAlleles[i];
            if(obj.hgvs != null && obj.hgvs.length > 0){

                var strValue = '';
                if(obj.proteinEffect != null && obj.proteinEffect.hgvs != null){
                    var strValue = obj.proteinEffect.hgvs;
                    let indx = strValue.lastIndexOf(':');
                    strValue = strValue.substring((indx+1),strValue.length);
                }

                var hgvsLinks = obj.hgvs;
                var j = hgvsLinks.length;
                for (var k = 0; k < j; k++){
                    var hgvsLink =hgvsLinks[k];
                    if(strValue != ''){
                        hgvsLink = hgvsLink+" ("+strValue+")";
                    }
                    hgvsLinkString = hgvsLinkString+hgvsLink+", ";
                }
            }
        }
    }

    if(hgvsLinkString != "" && hgvsLinksForGenomicAllelesDiv != null){
        hgvsLinksForGenomicAllelesDiv.innerHTML = hgvsLinkString;
    }

    //realated genes table
     if(externalRecordsNameAndLink != null){
        createRelatedGeneTable(externalRecordsNameAndLink, alleleDataObj.communityStandardTitle[0]);
    }
}

async function createRelatedGeneTable(externalRecordsNameAndLink, communityStandardTitle){
    let associatedGeensMap = null;
    
    if(externalRecordsNameAndLink.MyVariantInfo_hg38 != null){
        let erMyVariantInfoHG38 = externalRecordsNameAndLink.MyVariantInfo_hg38;
        if(erMyVariantInfoHG38.id != null){
            let myVariantInfoHG38Data = await getMyVariantInfoHG38Data(encodeURIComponent(erMyVariantInfoHG38.id));        
            if (myVariantInfoHG38Data != null){
                associatedGeensMap = processHG38ExternalRecordsResponse(myVariantInfoHG38Data);
            }
        }        
    }else{
        let cstGeneName = getGeneNameFromAlleleRegResponse(communityStandardTitle);
        if(cstGeneName != null && cstGeneName != ''){
            associatedGeensMap = {};
            associatedGeensMap[cstGeneName] = {};
        }
    }
    
    if(associatedGeensMap == null){
        return;
    }

    var geneList = Object.keys(associatedGeensMap)

    let engineDataForGenes = await getEngineDataForGeneList(geneList);

    let td = null;
    let tr = null;
    let radioBtn = null;
    let aLink = null;
    let relatedGenesTable = document.getElementById("relatedGenesTable");
    if(relatedGenesTable == null){
        return;
    }

    let imageSrc = null;
    let title = null;
    let message = null;
    let engineId = null

    var n = geneList.length;
    for (var i = 0; i < n; i++){
        let geneName = geneList[i];
        tr = document.createElement('tr');
            td = document.createElement('td');
                let imgElem = document.createElement('img'); 
                if(engineDataForGenes != null && engineDataForGenes[geneName] != null && engineDataForGenes[geneName].enabled){
                    imageSrc ="../images/green_check_button.png";
                    title = "This gene has an available specification (rule set)!"
                    message = "Specification available";
                    engineId = engineDataForGenes[geneName].engineId;
                }else if(engineDataForGenes != null && engineDataForGenes[geneName] != null && !engineDataForGenes[geneName].enabled){
                    imageSrc ="../images/yellow_warning_button.png";
                    title = "The specification for this gene is available but not yet validated for use!"
                    message = "Specification is available but not yet validated";
                    engineId = engineDataForGenes[geneName].engineId;
                }else{
                    imageSrc ="../images/warning_button.png";
                    title = "This gene has no currently available specification (rule set)!"
                    message = "No specification available";
                    engineId = "GN001";
                }
                imgElem.src= imageSrc;
                imgElem.title = title;
                imgElem.style.width='15px';
                imgElem.style.height='15px';
                imgElem.style.marginLeft='5px';
            td.appendChild(imgElem);
        tr.appendChild(td);
            td = document.createElement('td');
            td.className = "calculatorBasicTableLeftTD";
                radioBtn = document.createElement('input');
                radioBtn.type = "radio";
                radioBtn.name = "mainGeneSelectRadioGroup";
                if(i == 0){
                    radioBtn.checked = true;
                }
                radioBtn.value = geneName;
            td.appendChild(radioBtn);
                aLink = document.createElement('a');
                aLink.className = "alleleGeneLinksCalc";
                aLink.href = "https://genboree.org/cfde-gene-dev/Gene/id/"+geneName;
                aLink.target = "_blank"
                    let img = document.createElement('img');
                    img.src = "../images/got_link_icon.png";
                aLink.appendChild(img);
                    let  p = document.createElement('p');
                    p.style.margin = "0px";
                    p.innerHTML = geneName;
                aLink.appendChild(p);
            td.appendChild(aLink);
        tr.appendChild(td);
            td = document.createElement('td');
                let messageSpan = document.createElement("span");
                messageSpan.innerText = message;
            td.appendChild(messageSpan); 
        tr.appendChild(td);    
            td = document.createElement('td');
                let engineInfoBtn = document.createElement("button");
                engineInfoBtn.className = "calcMainMenuBtns"; 
                engineInfoBtn.innerText = "Specification Info";
                engineInfoBtn.value = engineId;
                engineInfoBtn.addEventListener("click", function(){ displayEngineInfoFromBtn(this) });          
            td.appendChild(engineInfoBtn);
        tr.appendChild(td);
            td = document.createElement('td');

            let hgncLink = "https://www.genenames.org/tools/search/#!/?query="+geneName;
            let ncbiLink = "https://www.ncbi.nlm.nih.gov/search/all/?term="+geneName;
            let hgncAndNCBIgeneIdentifiers = await getHGNCandNCBIgeneIdentifiers(geneName);
        
            if(hgncAndNCBIgeneIdentifiers != null && Array.isArray(hgncAndNCBIgeneIdentifiers)){
                if(hgncAndNCBIgeneIdentifiers[0] != ''){
                    hgncLink = 'http://www.genenames.org/data/gene-symbol-report/#!/hgnc_id/'+hgncAndNCBIgeneIdentifiers[0];
                }
                if(hgncAndNCBIgeneIdentifiers[1] != ''){
                    ncbiLink = 'http://www.ncbi.nlm.nih.gov/gene/'+hgncAndNCBIgeneIdentifiers[1];
                }        
            }
        
            let externalGeneSourcesNameAndLinks = {
                'UCSC':{'link':'http://genome.ucsc.edu/cgi-bin/hgGene?org=human&db=hg38&hgg_gene='+geneName},
                'HGNC':{'link':hgncLink},
                'NCBI':{'link':ncbiLink},
                // this link is broken for now! 'ExAC':{'link':'http://exac.broadinstitute.org/awesome?query='+geneName},
                'gnomAD':{'link':'http://gnomad.broadinstitute.org/awesome?query='+geneName},
                'GTR':{'link':'https://www.ncbi.nlm.nih.gov/gtr/all/genes/?term='+geneName},
                'OMIM':{'link':'https://www.omim.org/search?index=entry&start=1&limit=10&sort=score+desc%2C+prefix_sort+desc&search=%22'+geneName+'+gene%22'}
            }        
            createPCExternalLinks(externalGeneSourcesNameAndLinks, td, "alleleGeneLinksCalc");
            
        tr.appendChild(td);
        relatedGenesTable.appendChild(tr);
    }
}

function processHG38ExternalRecordsResponse(apiResponse){
    let geneID = "";
    var associatedGeensMap = {};
    var associatedGeens = apiResponse.snpeff.ann;
    if(Array.isArray(associatedGeens)){
        for(let agIdx in associatedGeens){
            let associatedGeenObj = associatedGeens[agIdx];
            geneID = associatedGeenObj.gene_id+"";
      
            if(!associatedGeensMap.hasOwnProperty(geneID) && geneID != ""){
              associatedGeensMap[geneID] = {};
            }
        }
    }else{
        associatedGeensMap[associatedGeens.gene_id+""] = {};
    }

    return associatedGeensMap;
  }

function getMyVariantInfoHG38Data(myVariantInfoHG38DataIdentifier){
    if(myVariantInfoHG38DataIdentifier == null || myVariantInfoHG38DataIdentifier == ''){
        return null;
    }
    return new Promise(function (resolve, reject) {
        var xhr = new XMLHttpRequest();	
        var url = "/pcalc/rest/calculator/getMyVariantInfoHG38Data/"+myVariantInfoHG38DataIdentifier;
        xhr.onload = function() {
            if (xhr.status === 200 && xhr.readyState == 4) {		
                if(xhr.responseText != null && xhr.responseText != ""){
                    let dataObj = JSON.parse(xhr.responseText);
                    resolve(dataObj);
                }
                resolve(null);                                   
            } else if (xhr.status !== 200) {
                resolve(null);
            }
        };
        xhr.open('GET', url, true);
        xhr.setRequestHeader('Content-Type', 'application/json');
        xhr.send();
    });
}

function getHGNCandNCBIgeneIdentifiers(geneNameID){
    if(geneNameID == null || geneNameID == ''){
        return null;
    }
    return new Promise(function (resolve, reject) {
        var xhr = new XMLHttpRequest();	
        var url = "/pcalc/rest/genes/geneData/"+geneNameID;
        xhr.onload = function() {
            if (xhr.status === 200 && xhr.readyState == 4) {		
                if(xhr.responseText != null && xhr.responseText != ""){
                    let dataObj = JSON.parse(xhr.responseText);
                    if(dataObj.externalRecords != null){
                        let externalRecordsObj = dataObj.externalRecords;
                        let hgncID = "";
                        if(externalRecordsObj.HGNC != null){
                            hgncID = externalRecordsObj.HGNC.id;
                        }
                        let ncbiID = "";
                        if(externalRecordsObj.NCBI != null){
                            ncbiID = externalRecordsObj.NCBI.id;
                        }
                        resolve(new Array(hgncID, ncbiID));  
                    }
                    resolve(null);
                }else{
                    resolve(null);                   
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

function getEngineDataForGeneList(geneList){ 
    return new Promise(function (resolve, reject) {
            var postData = {
              'genes': geneList,
            } 
          
            postData = JSON.stringify(postData);
          
            var xhr = new XMLHttpRequest();
            let url = "/pcalc/rest/genes/engineDataForGenes";
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
