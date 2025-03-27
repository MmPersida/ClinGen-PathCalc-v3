/*
function openAlleleFrequencyChartPopUp(thisDivElem){
    var variantHGVS = thisDivElem.getAttribute("data-value").trim();
    if(variantHGVS == null || variantHGVS == ''){
        return;
    }    
    loadHighChartForAF(variantHGVS);
}

async function loadHighChartForAF(inputHGVS){
    // static url of myvariant
    var linkProtocol = window.location.protocol ;
    var mvidBase = linkProtocol+'//myvariant.info/v1/variant/' ;
  
    // Generate full URL
    var mvAPI = mvidBase+inputHGVS+"?fields=gnomad_exome.af+gnomad_genome.af" ;
  
    let respObj = await getResponseFromMyVariant(mvAPI);
  
    if(respObj != null){      
      var yCategories = ['Total', 'African', 'Latino', 'Ashkenazi Jewish' ,'East Asian', 'European (Finish)', 'European (Non Finish)' , 'South Asian'] ;
      var xCategories = ['Absent', '0 - 1 %', '1 - 5 %', '> 5%'] ;
      var hcDataObj = [] ;
      var afType = null  ;
      var alleleFreq ;
        
      if(respObj.gnomad_exome){
        afType = "Exome" ;
        alleleFreq = respObj.gnomad_exome.af ;
      }else if(respObj.gnomad_genome){
        afType = "Genome" ;
        alleleFreq = respObj.gnomad_genome.af ;
      }else{
        afType = null ;
      }
  
      var afLabelMap = {
          "Total": "af",
          "African": "af_afr",
          "Latino": "af_amr" ,
          "East Asian": "af_eas",
          "Ashkenazi Jewish": "af_asj" ,
          "European (Finish)": "af_fin" ,
          "European (Non Finish)": "af_nfe" ,
          "South Asian": "af_sas"
      } ;
        
      if(afType !== null) {
        var ii ;
        for(ii=0; ii<yCategories.length; ii++){
          var yCat = yCategories[ii] ;
          var af = alleleFreq[afLabelMap[yCat]] ;
          // AF missing/absent
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
        }
        makeAFChartWindow(inputHGVS) ;
  
        $('#afChartContainer').highcharts({
                chart: {
                    type: 'heatmap',
                    marginTop: 70,
                    marginBottom: 40
                },  
                title: {
                    text: 'Allele Frequency For gnomAD ('+afType+') for '+inputHGVS
                },    
                xAxis: {
                    categories: ['Absent', '0 - 1 %', '1 - 5 %', '> 5%']
                },    
                yAxis: {
                    categories: ['Total', 'African', 'Latino', 'Ashkenazi Jewish', 'East Asian', 'European (Finish)', 'European (Non Finish)', 'South Asian'],
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
      }else{
        var message = "Both exome and genome fields missing in response from myVariant.info. Cannot render chart.";
        openNotificationPopUp(message, null);
      }
    }else{
      var message = "Failed to get the allele data.<br> Either this variant does not exist in myvariant.info or the specific data is missing.";
      openNotificationPopUp(message, null);
    }
}

function getResponseFromMyVariant(url){
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

function makeAFChartWindow(allele){
    let html = '<div class="scorePiechart af-chart-container" id="afChartContainer" ></div>';
    openNotificationPopUp(html, "Allele Frequency");
}*/


/*
 .scorePiechart{
    width: 100%;
    height: fit-content;
    padding: 0px;
    margin: 0px;
 }
*/