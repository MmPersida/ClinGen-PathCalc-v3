function displayAlleleAndGeneInformation(alleleDataObj){
    //Allele
    if(alleleDataObj != null && alleleDataObj.externalRecords != null){
        let externalRecords_1 = document.getElementById("externalRecords_1");
        let externalRecordsNameAndLink = getAlleleExtRecordsNameAndLink(alleleDataObj.externalRecords);
        createPCExternalLinks(externalRecordsNameAndLink, externalRecords_1);
    }

    //var externalRecordsDiv_2 = document.getElementById("externalRecords_2");

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
                hgvsLinkString = hgvsLinkString+hgvsLink+",";
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
                    hgvsLinkString = hgvsLinkString+hgvsLink+",";
                }
            }
        }
    }

    if(hgvsLinkString != "" && hgvsLinksForGenomicAllelesDiv != null){
        hgvsLinksForGenomicAllelesDiv.innerHTML = hgvsLinkString;
    }

    //sethte gene name (id)
    let geneNameID = getGeneNameFromAlleleRegResponse(alleleDataObj.communityStandardTitle[0]);
    document.getElementById("mainGeneName").innerHTML = geneNameID;
    var geneNameAllelePage = document.getElementById("geneNameAllelePage");
    geneNameAllelePage.href = "https://genboree.org/cfde-gene-dev/Gene/id/"+geneNameID;
    
    let externalGeneSourcesNameAndLinks = [
        {'name':'UCSC', 'link':'http://genome.ucsc.edu/cgi-bin/hgGene?org=human&db=hg38&hgg_gene='+geneNameID},
        {'name':'HGNC', 'link':'http://www.genenames.org/cgi-bin/gene_symbol_report?hgnc_id=HGNC:7715'},
        {'name':'NCBI', 'link':'http://www.ncbi.nlm.nih.gov/gene/4728'},
        {'name':'ExAC', 'link':'http://exac.broadinstitute.org/awesome?query='+geneNameID},
        {'name':'gnomAD', 'link':'http://gnomad.broadinstitute.org/awesome?query='+geneNameID},
        {'name':'GTR', 'link':'https://www.ncbi.nlm.nih.gov/gtr/all/genes/?term='+geneNameID},
        {'name':'OMIM', 'link':'http://www.omim.org/search/?search=gene_name='+geneNameID}
    ]

    let geneExternalLinksTD = document.getElementById("geneExternalLinksTD");
    createPCExternalLinks(externalGeneSourcesNameAndLinks, geneExternalLinksTD);
}

function createPCExternalLinks(externalSourceNameAndLinks, containerDiv){
    let a = null;
    let img = null;
    let p = null;
    for(let esIndx in externalSourceNameAndLinks){
        let esObj = externalSourceNameAndLinks[esIndx];
        a = document.createElement("a");
        a.className = "alleleGeneLinksCalc";
        a.href = esObj.link;
        a.target="_blank";
            img = document.createElement("img");
            img.src = "../images/got_link_icon.png";
        a.appendChild(img);
            p = document.createElement("p");        
            p.innerText = esObj.name;
        a.appendChild(p);  
        containerDiv.appendChild(a);   
    }
}
