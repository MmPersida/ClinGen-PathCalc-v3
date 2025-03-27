function openAlleleFrequencyChartPopUp(thisDivElem){
    var caid = thisDivElem.getAttribute("data-value").trim();
    if(caid == null || caid == ''){
        return;
    }    
    loadHighChartForAF(caid);
}

async function loadHighChartForAF(caid){
    let exomeData = await getResponseFromLDH("https://ldh.clinicalgenome.org/ldh-dss/cg/ns/gnomad_v4_1/set/exome/id/"+caid+"/data");
    let genomeData = await getResponseFromLDH("https://ldh.clinicalgenome.org/ldh-dss/cg/ns/gnomad_v4_1/set/genome/id/"+caid+"/data");
    
    if(exomeData == null && genomeData == null){
        openNotificationPopUp( "Both exome and genome fields missing in response from ldh.clinicalgenome.org. Cannot render chart!", null);
        return;
    }
    
    makeAFChartWindow();

    if(exomeData.gks_va_freq != null && exomeData.gks_va_freq.subcohortFrequency != null){
        renderAlleleFreqChart(exomeData.gks_va_freq,"afChartExome", "Exome", caid);
    }
    if(genomeData.gks_va_freq != null && genomeData.gks_va_freq.subcohortFrequency != null){
        renderAlleleFreqChart(genomeData.gks_va_freq, "afChartGenome", "Genome", caid);
    }
}

function renderAlleleFreqChart(alleleFreqData, divContainerId, afType, caid){
    if(!Array.isArray(alleleFreqData.subcohortFrequency) || alleleFreqData.subcohortFrequency.length == 0){
        return;
    }      

    var hcDataObj = [] ;
    var alleleSubcohortFreq = alleleFreqData.subcohortFrequency;
    var afLabels= [];

    //inicate and object for "Total"
    var totalSubcohort = {
        "alleleFrequency": alleleFreqData.alleleFrequency,
        "cohort":{
            "id": "ALL",
            "characteristics":[
                {
                    "value": "Total"
                }
            ]
        }
    }
    alleleSubcohortFreq.push(totalSubcohort);    

    if(afType !== null) {
        var ii = 0;
        for(let i in alleleSubcohortFreq){
            var subcohortFreq = alleleSubcohortFreq[i];
            var af = subcohortFreq.alleleFrequency;

            var label = "Unknown"
            if(subcohortFreq.cohort != null){
                if(subcohortFreq.cohort.characteristics != null && subcohortFreq.cohort.characteristics.length > 0){
                    label = (subcohortFreq.cohort.characteristics[0]).value;
                    afLabels.push(label);
                } 
            }

            if(af === null || af === undefined || af === ""){
                hcDataObj.push([0, ii, 1]) ;
                hcDataObj.push([1, ii, 0]) ;
                hcDataObj.push([2, ii, 0]) ;
                hcDataObj.push([3, ii, 0]) ;
            }else{
                af = af * 100 ;
                if(af >= 0 && af <= 1){
                    hcDataObj.push([1, ii, 1]) ;
                    hcDataObj.push([0, ii, 0]) ;
                    hcDataObj.push([2, ii, 0]) ;
                    hcDataObj.push([3, ii, 0]) ;
                }else if(af > 1 && af <= 5){
                    hcDataObj.push([2, ii, 1]) ;
                    hcDataObj.push([0, ii, 0]) ;
                    hcDataObj.push([1, ii, 0]) ;
                    hcDataObj.push([3, ii, 0]) ;
                }else{
                    hcDataObj.push([3, ii, 1]) ;
                    hcDataObj.push([0, ii, 0]) ;
                    hcDataObj.push([1, ii, 0]) ;
                    hcDataObj.push([2, ii, 0]) ;
                }
            }
            ii++;
        }

        var chartDiv = document.getElementById(divContainerId);
        chartDiv.style.display = "block";

        $('#'+divContainerId).highcharts({
                chart: {
                    type: 'heatmap',
                    marginTop: 70,
                    marginBottom: 40
                },  
                title: {
                    text: 'Allele Frequency Of Type '+afType+' For '+alleleFreqData.id
                },    
                xAxis: {
                    categories: ['Absent', '0 - 1 %', '1 - 5 %', '> 5%']
                },    
                yAxis: {
                    categories: afLabels,
                    title: null
                },    
                colorAxis: {
                    min: 0,
                    minColor: '#FFFFFF',
                    maxColor: '#F434G2',
                    max: 1
                },    
                legend: {
                    enabled: false,
                    align: 'right',
                    layout: 'vertical',
                    margin: 0,
                    verticalAlign: 'top',
                    y: 25,
                    symbolHeight: 320
                },
                tooltip: {
                    formatter: function() {
                    if (this.point.value === 1) {
                        if (this.point.x !== 0) {
                        return '<b> Allele frequency is in the range of ' + this.series.xAxis.categories[this.point.x] + '</b> for <br><b>' +
                        this.series.yAxis.categories[this.point.y] + '</b>';
                            
                        }
                        else {
                        return '<b>This variant is absent in ' + this.series.yAxis.categories[this.point.y] + '</b>';
                        }
                    }
                    else {
                        return false ;
                    }
                    }
                },
                series: [{
                    name: 'Categories',
                    borderWidth: 0,
                    pointPadding: 10,
                    borderRadius: 3,
                    width: 0,
                    data: hcDataObj,
                    dataLabels: {
                    enabled: false,
                    color: 'black',
                    style: {
                        textShadow: 'none'
                    }
                    }
                }]
        });
    }
}

function getResponseFromLDH(url){
    if(url == null || url == ''){
        return null;
    }
    return new Promise(function (resolve, reject) {
        var xhr = new XMLHttpRequest();	
        xhr.onload = function() {
            if (xhr.status === 200 && xhr.readyState == 4) {		
                if(xhr.responseText != null && xhr.responseText != ""){
                    let dataObj = JSON.parse(xhr.responseText);
                    if(dataObj != null){
                        resolve(dataObj);
                    }
                }                   
            }
            resolve(null);
        };
        xhr.open('GET', url, true);
        xhr.send();
    });
}

function makeAFChartWindow(){
    let html = '<div class="chartContainer">'+
                        '<div class="alleleFreqChartDiv af-chart-container" style="display:none;" id="afChartExome"></div>'+
                        '<div class="alleleFreqChartDiv af-chart-container" style="display:none;" id="afChartGenome"></div>'+
                '</div>';
    openNotificationPopUp(html, "Allele Frequency");
}
