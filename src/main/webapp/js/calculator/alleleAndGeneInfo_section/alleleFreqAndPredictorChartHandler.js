function openPredictorChartPopUp(thisDivElem){
    var variantHGVS = thisDivElem.getAttribute("data-value").trim();
    if(variantHGVS == null || variantHGVS == ''){
        return;
    }
    loadPredictorChart(variantHGVS);
}

function openAlleleFrequencyChartPopUp(thisDivElem){
    var variantHGVS = thisDivElem.getAttribute("data-value").trim();
    if(variantHGVS == null || variantHGVS == ''){
        return;
    }    
    loadHighChartForAF(variantHGVS);
}

/*   Methods written by Ronak and Sameer
 * 
 *
 */

// function to help with mapping of reference sequence to chromosome 
function refSeqMapping(inSeq){
    return {
      "NC_000001.10":  "chr1" , "NC_000002.11":  "chr2" , "NC_000003.11":  "chr3" ,
      "NC_000004.11":  "chr4" , "NC_000005.9" :  "chr5" , "NC_000006.11":  "chr6" ,
      "NC_000007.13":  "chr7" , "NC_000008.10":  "chr8" , "NC_000009.11":  "chr9" ,
      "NC_000010.10":  "chr10", "NC_000011.9" :  "chr11", "NC_000012.11":  "chr12",
      "NC_000013.10":  "chr13", "NC_000014.8" :  "chr14", "NC_000015.9" :  "chr15",
      "NC_000016.9" :  "chr16", "NC_000017.10":  "chr17", "NC_000018.9" :  "chr18",
      "NC_000019.9" :  "chr19", "NC_000020.10":  "chr20", "NC_000021.8" :  "chr21",
      "NC_000022.10":  "chr22", "NC_000023.10":  "chrX" , "NC_000024.9" :  "chrY"
    }[inSeq] ;
  }
  
async function loadPredictorChart(inputHGVS) {
    // static url of myvariant
    var linkProtocol = window.location.protocol ;
    var mvidBase = linkProtocol+'//myvariant.info/v1/variant/' ;
  
    // Change the input hgvs to what requried by myvariant
    //var transformedHGVS = refSeqMapping(inputHGVS.split(':')[0])+':'+inputHGVS.split(':')[1] ;
  
    // Generate full URL
    //var mvAPI = mvidBase+transformedHGVS ;

    var mvAPI = mvidBase+inputHGVS;

    let response = await getResponseFromMyVariant(mvAPI);
    if( response.dbnsfp !== undefined ){
        var predictors = ["sift","provean","lrt","metalr","metasvm","ma","mt","hdiv","hvar","fathmm"] ;
        // Function to convert predictors abbreviation to displayable name
        var predictorsFullName =function (inSoftware){
            return {
                 sift: 'SIFT', provean: 'PROVEAN', lrt: 'LRT', metalr: 'MetaLR',
                 metasvm: 'MetaSVM', hdiv: 'PolyPhen-2 HDIV', hvar: 'PolyPhen-2 HVAR', fathmm: 'FATHMM',
                 ma: 'Mutation Assessor', mt: 'MutationTaster' }[inSoftware] 
            } ;
         // This will handle when the JSON response does not have information about the predictions
         jQuery.each(predictors,function(element){
           if(response.dbnsfp[predictors[element]] === undefined){
             response.dbnsfp[predictors[element]] = {} ;
           }
         });
         // Some of the software that might be left out in the previous loop
         if(response.dbnsfp['polyphen2'] === undefined) {
           response.dbnsfp['polyphen2'] = {hdiv: {}, hvar: {}} ;
         }
         if(response.dbnsfp['mutationassessor'] === undefined) {
           response.dbnsfp['mutationassessor'] = {} ;
         }
         if(response.dbnsfp['mutationtaster'] === undefined) {
           response.dbnsfp['mutationtaster'] = {} ;
         }


         // Key prediction hash that stores actual predictions
         var prediction = {
           sift    : response.dbnsfp.sift.pred ,
           provean : response.dbnsfp.provean.pred ,
           fathmm : response.dbnsfp.fathmm.pred,
           lrt : response.dbnsfp.lrt.pred ,
           metalr : response.dbnsfp.metalr.pred ,
           metasvm : response.dbnsfp.metasvm.pred ,
           hdiv : response.dbnsfp.polyphen2.hdiv.pred ,
           hvar : response.dbnsfp.polyphen2.hvar.pred ,
           mt : response.dbnsfp.mutationtaster.pred ,
           ma : response.dbnsfp.mutationassessor.pred  
         } ;// end prediction


         // The scores are cryptic and difficult to understand, however we have mapping
         // This hash stores that mapping
         var score_mapping = {
           sift   : { D: 'Deleterious', T: 'Tolerated' , '.':'Unknown', 'U':'Unknown'},
           fathmm :  { D: 'Deleterious', T: 'Tolerated' ,'.':'Unknown', 'U':'Unknown'},
           ma     : {H: 'High Impact', M: 'Medium Impact', 
             L: 'Low Impact', N: 'Neutral', '.':'Unknown', 'U':'Unknown'},
           hdiv   : {D: 'Probably damaging', P:'Possibly damaging', B: 'Benign', '.':'Unknown', 'U':'Unknown'},
           hvar   : {D: 'Probably damaging', P:'Possibly damaging', B: 'Benign', '.':'Unknown', 'U':'Unknown'},
           mt     : {A: 'Disease Causing Automatic', D: 'Disease Causing', 
             N: 'Polymorphic', P: 'Polymorphic' , '.':'Unknown', 'U':'Unknown'},
           provean :  { D: 'Deleterious', N: 'Neutral' , '.':'Unknown' , 'U':'Unknown'},
           lrt     :  { D: 'Deleterious', N: 'Neutral', T: 'Tolerated' , '.':'Unknown' , 'U':'Unknown'},
           metasvm :  { D: 'Deleterious', N: 'Neutral', T: 'Tolerated' , '.':'Unknown' , 'U':'Unknown'},
           metalr  :  { D: 'Deleterious', N: 'Neutral', T: 'Tolerated' , '.':'Unknown' , 'U':'Unknown'}
         } ;// end score mapping

         var totalcount = {} ;
         var software   = {} ;

         // The bening predictions should be displayed green and pathogenic red.
         var customColors = {
           'Deleterious'               : '#ff7f7f',
           'High Impact'               : '#ff9999',
           'Possibly damaging'         : '#ffb2b2',
           'Disease Causing'           : '#ffcccc',
           'Probably damaging'         : '#f3b877',
           'Disease Causing Automatic' : '#f5c48e',
           'Medium Impact'             : '#f7cfa4',
           'Tolerated'                 : '#66ff66',
           'Low Impact'                : '#ccffcc',
           'Neutral'                   : '#b2ffb2',
           'Benign'                    : '#7fff7f',
           'Polymorphic'               : '#99ff99',
       'Unknown'                   : '#d3d3d3'
         } ;// end customColors

         // All mumble jumble to get information out
         var finalColors = [];
           for(var j = 0 ; j < predictors.length ; j++){
             if( prediction[predictors[j]] !== undefined ){
               if(prediction[predictors[j]].constructor !== Array){
                 if( totalcount[score_mapping[predictors[j]][prediction[predictors[j]]]] == undefined ){ totalcount[score_mapping[predictors[j]][prediction[predictors[j]]]] = 1 ;} 
                 else {totalcount[score_mapping[predictors[j]][prediction[predictors[j]]]] += 1 ;}
                 if(software[score_mapping[predictors[j]][prediction[predictors[j]]]] == undefined ){ software[score_mapping[predictors[j]][prediction[predictors[j]]]] = [predictors[j]] ;}
                 else {software[score_mapping[predictors[j]][prediction[predictors[j]]]].push(predictors[j]) ;}
               }
               if(prediction[predictors[j]].constructor === Array){
                 for(var k = 0 ; k < prediction[predictors[j]].length ; k++ ){
                   if(totalcount[score_mapping[predictors[j]][prediction[predictors[j]][k]]] == undefined ){ totalcount[score_mapping[predictors[j]][prediction[predictors[j]][k]]] = 1 ;} 
                   else {totalcount[score_mapping[predictors[j]][prediction[predictors[j]][k]]] += 1 ;}
                   if(software[score_mapping[predictors[j]][prediction[predictors[j]][k]]]  == undefined ) { software[score_mapping[predictors[j]][prediction[predictors[j]][k]]] = [predictors[j]] ;}
                   else {software[score_mapping[predictors[j]][prediction[predictors[j]][k]]].push(predictors[j]) ;}

                 }// for for k
               } // end if array
             }// end of outer if when not undefined 
           } // end out for loop

         jQuery.each(totalcount,function(name,value){
           finalColors.push(customColors[name]);
         });

         jQuery.each(software,function(name,value){
           jQuery.each(value,function(j){
             software[name][j] =  predictorsFullName(value[j]) ;
           });
         });

         google.charts.load('current', {'packages':['corechart']});
         google.charts.setOnLoadCallback(drawChart);

         function drawChart() {
           var data = new google.visualization.DataTable();
           data.addColumn('string','Predictor');
           data.addColumn('number','Percentage');
           jQuery.each(totalcount, function(name,value){
             data.addRow([name+' ( '+[].concat(jQuery.unique(software[name])).join(", ")+' )',value]) ;
           });

           makePredictorChartWindow();
           var options = {
            colors: finalColors, 
            pieSliceTextStyle: { color: 'black'} , 
            legend: {position: 'right', textStyle: {color: 'black', fontSize: 12}}, 
            pieHole: 0.4, 
            title: 'Predictor Scores' , 
            width:750,
            height:300,
            chartArea: {  width: 700, height: 300 }
             /*height: chartHeight*/
          } ;

           var chart = new google.visualization.PieChart(document.getElementById('piechart'));
           chart.draw(data,options);
        } // end draw chart
    }else{
      var message = "Failed to get the allele data.<br> Either this variant does not exist in myvariant.info or dbNSFP annotations are missing.";
      openNotificationPopUp(message, null);
    }
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

function makePredictorChartWindow(){
    let html = '<div class="scorePiechart" id="piechart"></div>'; 
    openNotificationPopUp(html, "Predictor Chart");
}
    
function makeAFChartWindow(allele){
    let html = '<div class="scorePiechart af-chart-container" id="afChartContainer" ></div>';
    openNotificationPopUp(html, "Allele Frequency");
}
