/*
let expandTableBtn = document.getElementsByClassName("expandTableBtn");
for(const btn of expandTableBtn){
    btn.addEventListener("click", function(){ displayEditEvidencetagSubTable(this) });
}*/

/*
                <tr>
                    <td class="lightblueTD"><div class="evidenceTableBtnDiv"><img src="../images/data_button.png"><p>POPULATION DATA</p></div></td>
                    <td id="ed_11" class="whiteTD benignSupport" onclick="openEvidenceCellPopUp(this)">
                        <div id="evidencesNum_11"></div>
                        <div class="dropdownEvidenceCodes">
                            Evidence codes:
                            <ul>
                                <li>BS1-Supporting</li>
                                <li>BS2-Supporting</li>
                            </ul>  
                        </div>
                    </td>
                    <td id="ed_12" class="whiteTD benignStrong" onclick="openEvidenceCellPopUp(this)">
                        <div id="evidencesNum_12"></div>
                        <div class="dropdownEvidenceCodes">
                            Evidence codes:
                            <ul>
                                <li>BS1</li>
                                <li>BS2</li>
                            </ul>  
                        </div>
                    </td>
                    <td id="ed_13" class="whiteTD benignSA" onclick="openEvidenceCellPopUp(this)">
                        <div id="evidencesNum_13"></div>
                        <div class="dropdownEvidenceCodes">
                            Evidence codes:
                            <ul>
                                <li>BA1</li>
                            </ul>  
                        </div>
                    </td>
                    <td id="ed_14" class="whiteTD pathogSupport" onclick="openEvidenceCellPopUp(this)">
                        <div id="evidencesNum_14"></div>
                        <div class="dropdownEvidenceCodes">
                            Evidence codes:
                            <ul>
                                <li>PM2-Supporting</li>
                                <li>PS4-Supporting</li>
                            </ul>  
                        </div>
                    </td>
                    <td id="ed_15" class="whiteTD pathogModerate" onclick="openEvidenceCellPopUp(this)">
                        <div id="evidencesNum_15"></div>
                        <div class="dropdownEvidenceCodes">
                            Evidence codes:
                            <ul>
                                <li>PM2</li>
                                <li>PS4-Moderate</li>
                            </ul>  
                        </div>
                    </td>
                    <td id="ed_16" class="whiteTD pathogStrong" onclick="openEvidenceCellPopUp(this)">
                        <div id="evidencesNum_16"></div>
                        <div class="dropdownEvidenceCodes">
                            Evidence codes:
                            <ul>
                                <li>PS4</li>
                                <li>PM2-Strong</li>
                            </ul>  
                        </div>
                    </td>
                    <td id="ed_17" class="whiteTD pathogVS" onclick="openEvidenceCellPopUp(this)">
                        <div id="evidencesNum_17"></div>
                        <div class="dropdownEvidenceCodes">
                            Evidence codes:
                            <ul>
                                <li>PM2-Very Strong</li>
                                <li>PS4-Very Strong</li>
                            </ul>  
                        </div>
                    </td>
                </tr>
                <tr>
                    <td class="lightblueTD"><div class="evidenceTableBtnDiv"><img src="../images/data_button.png"><p>COMPUTATIONAL AND PREDICTIVE DATA</p></div></td>
                    <td id="ed_21" class="whiteTD benignSupport" onclick="openEvidenceCellPopUp(this)">
                        <div id="evidencesNum_21"></div>
                        <div class="dropdownEvidenceCodes">
                            Evidence codes:
                            <ul>
                                <li>BP1</li>
                                <li>BP3</li>
                                <li>BP4</li>
                                <li>BP7</li>
                            </ul>  
                        </div>
                    </td>
                    <td id="ed_22" class="whiteTD benignStrong" onclick="openEvidenceCellPopUp(this)">
                        <div id="evidencesNum_22"></div>
                        <div class="dropdownEvidenceCodes">
                            Evidence codes:
                            <ul>
                                <li>BP1-Strong</li>
                                <li>BP3-Strong</li>
                                <li>BP4-Strong</li>
                                <li>BP7-Strong</li>
                            </ul>  
                        </div>
                    </td>
                    <td id="ed_23" class="lightGrayTD benignSA">
                    </td>
                    <td id="ed_24" class="whiteTD pathogSupport" onclick="openEvidenceCellPopUp(this)">
                        <div id="evidencesNum_24"></div>
                        <div class="dropdownEvidenceCodes">
                            Evidence codes:
                            <ul>
                                <li>PP3</li>
                                <li>PM4-Suporting</li>
                                <li>PM5-Suporting</li>
                                <li>PS1-Suporting</li>
                                <li>PVS1-Suporting</li>
                            </ul>  
                        </div>
                    </td>
                    <td id="ed_25" class="whiteTD pathogModerate" onclick="openEvidenceCellPopUp(this)">
                        <div id="evidencesNum_25"></div>
                        <div class="dropdownEvidenceCodes">
                            Evidence codes:
                            <ul>
                                <li>PM4</li>
                                <li>PM5</li>
                                <li>PP3-Moderate</li>
                                <li>PS1-Moderate</li>
                                <li>PVS1-Moderate</li>
                            </ul>  
                        </div>
                    </td>
                    <td id="ed_26" class="whiteTD pathogStrong" onclick="openEvidenceCellPopUp(this)">
                        <div id="evidencesNum_26"></div>
                        <div class="dropdownEvidenceCodes">
                            Evidence codes:
                            <ul>
                                <li>PS1</li>
                                <li>PP3-Strong</li>
                                <li>PM4-Strong</li>
                                <li>PM5-Strong</li>
                                <li>PVS1-Strong</li>
                            </ul>  
                        </div>
                    </td>
                    <td id="ed_27" class="whiteTD pathogVS" onclick="openEvidenceCellPopUp(this)">
                        <div id="evidencesNum_27"></div>
                        <div class="dropdownEvidenceCodes">
                            Evidence codes:
                            <ul>
                                <li>PVS1</li>
                                <li>PS1-Very Strong</li>
                                <li>PP3-Very Strong</li>
                                <li>PM4-Very Strong</li>
                                <li>PM5-Very Strong</li>                              
                            </ul>  
                        </div>
                    </td>
                </tr>
                <tr>
                    <td class="lightblueTD"><div class="evidenceTableBtnDiv"><img src="../images/data_button.png"><p>FUNCTIONAL DATA</p></div></td>  
                    <td id="ed_31" class="whiteTD benignSupport" onclick="openEvidenceCellPopUp(this)">
                        <div id="evidencesNum_31"></div>
                        <div class="dropdownEvidenceCodes">
                            Evidence codes:
                            <ul>
                                <li>BS3-Supporting</li>
                            </ul>  
                        </div>
                    </td>
                    <td id="ed_32" class="whiteTD benignStrong" onclick="openEvidenceCellPopUp(this)">
                        <div id="evidencesNum_32"></div>
                        <div class="dropdownEvidenceCodes">
                            Evidence codes:
                            <ul>
                                <li>BS3</li>
                            </ul>  
                        </div>
                    </td>
                    <td id="ed_33" class="lightGrayTD benignSA">
                    </td>
                    <td id="ed_34" class="whiteTD pathogSupport" onclick="openEvidenceCellPopUp(this)">
                        <div id="evidencesNum_34"></div>
                        <div class="dropdownEvidenceCodes">
                            Evidence codes:
                            <ul>
                                <li>PP2</li>
                                <li>PM1-Supporting</li>
                                <li>PS3-Supporting</li>
                            </ul>  
                        </div>
                    </td>
                    <td id="ed_35" class="whiteTD pathogModerate" onclick="openEvidenceCellPopUp(this)">
                        <div id="evidencesNum_35"></div>
                        <div class="dropdownEvidenceCodes">
                            Evidence codes:
                            <ul>                              
                                <li>PM1</li>
                                <li>PP2-Moderate</li>
                                <li>PS3-Moderate</li>
                            </ul>  
                        </div>
                    </td>
                    <td id="ed_36" class="whiteTD pathogStrong" onclick="openEvidenceCellPopUp(this)">
                        <div id="evidencesNum_36"></div>
                        <div class="dropdownEvidenceCodes">
                            Evidence codes:
                            <ul>
                                <li>PS3</li>
                                <li>PP2-Strong</li>
                                <li>PM1-Strong</li>                               
                            </ul>  
                        </div>
                    </td>
                    <td id="ed_37" class="whiteTD pathogVS" onclick="openEvidenceCellPopUp(this)">
                        <div id="evidencesNum_37"></div>
                        <div class="dropdownEvidenceCodes">
                            Evidence codes:
                            <ul>
                                <li>PP2-Very Strong</li>
                                <li>PM1-Very Strong</li>
                                <li>PS3-Very Strong</li>
                            </ul>  
                        </div>
                    </td>                
                </tr>
                <tr>
                    <td class="lightblueTD"><div class="evidenceTableBtnDiv"><img src="../images/data_button.png"><p>SEGREGATION DATA</p></div></td> 
                    <td id="ed_41" class="whiteTD benignSupport" onclick="openEvidenceCellPopUp(this)">
                        <div id="evidencesNum_41"></div>
                        <div class="dropdownEvidenceCodes">
                            Evidence codes:
                            <ul>
                                <li>BS4-Supporting</li>
                            </ul>  
                        </div>
                    </td>
                    <td id="ed_42" class="whiteTD benignStrong" onclick="openEvidenceCellPopUp(this)">
                        <div id="evidencesNum_42"></div>
                        <div class="dropdownEvidenceCodes">
                            Evidence codes:
                            <ul>
                                <li>BS4</li>
                            </ul>  
                        </div>
                    </td>
                    <td id="ed_43" class="lightGrayTD benignSA">
                    </td>
                    <td id="ed_44" class="whiteTD pathogSupport" onclick="openEvidenceCellPopUp(this)">
                        <div id="evidencesNum_44"></div>
                        <div class="dropdownEvidenceCodes">
                            Evidence codes:
                            <ul>
                                <li>PP1</li>
                            </ul>  
                        </div>
                    </td>
                    <td id="ed_45" class="whiteTD pathogModerate" onclick="openEvidenceCellPopUp(this)">
                        <div id="evidencesNum_45"></div>
                        <div class="dropdownEvidenceCodes">
                            Evidence codes:
                            <ul>
                                <li>PP1-Moderate</li>
                            </ul>  
                        </div>
                    </td>
                    <td id="ed_46" class="whiteTD pathogStrong" onclick="openEvidenceCellPopUp(this)">
                        <div id="evidencesNum_46"></div>
                        <div class="dropdownEvidenceCodes">
                            Evidence codes:
                            <ul>
                                <li>PP1-Strong</li>
                            </ul>  
                        </div>
                    </td>
                    <td id="ed_47" class="whiteTD pathogVS" onclick="openEvidenceCellPopUp(this)">
                        <div id="evidencesNum_47"></div>
                        <div class="dropdownEvidenceCodes">
                            Evidence codes:
                            <ul>
                                <li>PP1-Very Strong</li>
                            </ul>  
                        </div>
                    </td>                
                </tr>
                <tr>
                    <td class="lightblueTD"><div class="evidenceTableBtnDiv"><img src="../images/data_button.png"><p>DE NOVO DATA</p></div></td>
                    <td id="ed_51" class="lightGrayTD benignSupport">
                    </td>
                    <td id="ed_52" class="lightGrayTD benignStrong">
                    </td>
                    <td id="ed_53" class="lightGrayTD benignSA">
                    </td>
                    <td id="ed_54" class="whiteTD pathogSupport" onclick="openEvidenceCellPopUp(this)">
                        <div id="evidencesNum_54"></div>
                        <div class="dropdownEvidenceCodes">
                            Evidence codes:
                            <ul>
                                <li>PM6-Supporting</li>
                                <li>PS2-Supporting</li>
                            </ul>  
                        </div>
                    </td>
                    <td id="ed_55" class="whiteTD pathogModerate" onclick="openEvidenceCellPopUp(this)">
                        <div id="evidencesNum_55"></div>
                        <div class="dropdownEvidenceCodes">
                            Evidence codes:
                            <ul>
                                <li>PM6</li>
                                <li>PS2-Moderate</li>
                            </ul>  
                        </div>
                    </td>
                    <td id="ed_56" class="whiteTD pathogStrong" onclick="openEvidenceCellPopUp(this)">
                        <div id="evidencesNum_56"></div>
                        <div class="dropdownEvidenceCodes">
                            Evidence codes:
                            <ul>
                                <li>PS2</li>
                                <li>PM6-Strong</li>
                            </ul>  
                        </div>
                    </td>
                    <td id="ed_57" class="whiteTD pathogVS" onclick="openEvidenceCellPopUp(this)">
                        <div id="evidencesNum_57"></div>
                        <div class="dropdownEvidenceCodes">
                            Evidence codes:
                            <ul>
                                <li>PS2-Very Strong</li>
                                <li>PM6-Very Strong</li>
                            </ul>  
                        </div> 
                    </td>                  
                </tr>
                <tr>
                    <td class="lightblueTD"><div class="evidenceTableBtnDiv"><img src="../images/data_button.png"><p>ALLELIC DATA</p></div></td>
                    <td id="ed_61" class="whiteTD benignSupport" onclick="openEvidenceCellPopUp(this)">
                        <div id="evidencesNum_61"></div>
                        <div class="dropdownEvidenceCodes">
                            Evidence codes:
                            <ul>
                                <li>BP2</li>
                            </ul>  
                        </div>
                    </td>
                    <td id="ed_62" class="whiteTD benignStrong" onclick="openEvidenceCellPopUp(this)">
                        <div id="evidencesNum_62"></div>
                        <div class="dropdownEvidenceCodes">
                            Evidence codes:
                            <ul>
                                <li>BP2-Strong</li>
                            </ul>  
                        </div>
                    </td>
                    <td id="ed_63" class="lightGrayTD benignSA">
                    </td>
                    <td id="ed_64" class="whiteTD pathogSupport" onclick="openEvidenceCellPopUp(this)">
                        <div id="evidencesNum_64"></div>
                        <div class="dropdownEvidenceCodes">
                            Evidence codes:
                            <ul>
                                <li>PM3-Supporting</li>
                            </ul>  
                        </div>
                    </td>
                    <td id="ed_65" class="whiteTD pathogModerate" onclick="openEvidenceCellPopUp(this)">
                        <div id="evidencesNum_65"></div>
                        <div class="dropdownEvidenceCodes">
                            Evidence codes:
                            <ul>
                                <li>PM3</li>
                            </ul>  
                        </div>
                    </td>
                    <td id="ed_66" class="whiteTD pathogStrong" onclick="openEvidenceCellPopUp(this)">\
                        <div id="evidencesNum_66"></div>
                        <div class="dropdownEvidenceCodes">
                            Evidence codes:
                            <ul>
                                <li>PM3-Strong</li>
                            </ul>  
                        </div>
                    </td>
                    <td id="ed_67" class="whiteTD pathogVS" onclick="openEvidenceCellPopUp(this)">
                        <div id="evidencesNum_67"></div>
                        <div class="dropdownEvidenceCodes">
                            Evidence codes:
                            <ul>
                                <li>PM3-Very Strong</li>
                            </ul>  
                        </div>   
                    </td>                  
                </tr>
                <tr>
                    <td class="lightblueTD"><div class="evidenceTableBtnDiv"><img src="../images/data_button.png"><p>OTHER DATABASE</p></div></td>
                    <td id="ed_71" class="whiteTD benignSupport" onclick="openEvidenceCellPopUp(this)">
                        <div id="evidencesNum_71"></div>
                        <div class="dropdownEvidenceCodes">
                            Evidence codes:
                            <ul>
                                <li>BP6</li>
                            </ul>  
                        </div>
                    </td>
                    <td id="ed_72" class="whiteTD benignStrong" onclick="openEvidenceCellPopUp(this)">
                        <div id="evidencesNum_72"></div>
                        <div class="dropdownEvidenceCodes">
                            Evidence codes:
                            <ul>
                                <li>BP6-Strong</li>
                            </ul>  
                        </div>
                    </td>
                    <td id="ed_73" class="lightGrayTD benignSA">
                    </td>
                    <td id="ed_74" class="whiteTD pathogSupport" onclick="openEvidenceCellPopUp(this)">
                        <div id="evidencesNum_74"></div>
                        <div class="dropdownEvidenceCodes">
                            Evidence codes:
                            <ul>
                                <li>PP5</li>
                            </ul>  
                        </div>
                    </td>
                    <td id="ed_75" class="whiteTD pathogModerate" onclick="openEvidenceCellPopUp(this)">
                        <div id="evidencesNum_75"></div>
                        <div class="dropdownEvidenceCodes">
                            Evidence codes:
                            <ul>
                                <li>PP5-Moderate</li>
                            </ul>  
                        </div>
                    </td>
                    <td id="ed_76" class="whiteTD pathogStrong" onclick="openEvidenceCellPopUp(this)">
                        <div id="evidencesNum_76"></div>
                        <div class="dropdownEvidenceCodes">
                            Evidence codes:
                            <ul>
                                <li>PP5-Strong</li>
                            </ul>  
                        </div>
                    </td>
                    <td id="ed_77" class="whiteTD pathogVS" onclick="openEvidenceCellPopUp(this)">
                        <div id="evidencesNum_77"></div>
                        <div class="dropdownEvidenceCodes">
                            Evidence codes:
                            <ul>
                                <li>PP5-Very Strong</li>
                            </ul>  
                        </div>                        
                    </td>                  
                </tr>
                <tr>
                    <td class="lightblueTD"><div class="evidenceTableBtnDiv"><img src="../images/data_button.png"><p>OTHER DATA</p></div></td>
                    <td id="ed_81" class="whiteTD benignSupport" onclick="openEvidenceCellPopUp(this)">
                        <div id="evidencesNum_81"></div>
                        <div class="dropdownEvidenceCodes">
                            Evidence codes:
                            <ul>
                                <li>BP5</li>
                            </ul>  
                        </div>
                    </td>
                    <td id="ed_82" class="whiteTD benignStrong" onclick="openEvidenceCellPopUp(this)">
                        <div id="evidencesNum_82"></div>
                        <div class="dropdownEvidenceCodes">
                            Evidence codes:
                            <ul>
                                <li>BP5-Strong</li>
                            </ul>  
                        </div>
                    </td>
                    <td id="ed_83" class="lightGrayTD benignSA">
                    </td>
                    <td id="ed_84" class="whiteTD pathogSupport" onclick="openEvidenceCellPopUp(this)">
                        <div id="evidencesNum_84"></div>
                        <div class="dropdownEvidenceCodes">
                            Evidence codes:
                            <ul>
                                <li>PP4</li>
                            </ul>  
                        </div>
                    </td>
                    <td id="ed_85" class="whiteTD pathogModerate" onclick="openEvidenceCellPopUp(this)">
                        <div id="evidencesNum_85"></div>
                        <div class="dropdownEvidenceCodes">
                            Evidence codes:
                            <ul>
                                <li>PP4-Modearte</li>
                            </ul>  
                        </div>
                    </td>
                    <td id="ed_86" class="whiteTD pathogStrong" onclick="openEvidenceCellPopUp(this)">
                        <div id="evidencesNum_86"></div>
                        <div class="dropdownEvidenceCodes">
                            Evidence codes:
                            <ul>
                                <li>PP4-Strong</li>
                            </ul>  
                        </div>
                    </td>
                    <td id="ed_87" class="whiteTD pathogVS" onclick="openEvidenceCellPopUp(this)">
                        <div id="evidencesNum_87"></div>
                        <div class="dropdownEvidenceCodes">
                            Evidence codes:
                            <ul>
                                <li>PP4-Very Strong</li>
                            </ul>  
                        </div>                        
                    </td>                  
                </tr>

*/