package org.nmdp.HLAGene;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public abstract class ExonIntronData {

    private static final char DIVIDER = '-';
    private String sampleID;
    private String gls;
    private String phaseSet;
    private String type;
    private String geneType;
    protected Map<SectionName, String> geneData = new HashMap<>();
    protected Map<SectionName, String> plData = new HashMap<>();

    private String fullLength = "";
    private String protien = "";



    public void setType(String type){
        this.type = type;

    }
    public String getType(){
        return type;
    }

    public void setGeneType(String gene){
        geneType = gene;
    }

    public String getGeneType(){
        return geneType;
    }
    public String getFullLength() {
        return fullLength;
    }

    public void setFullLength(String fullLength) {
        this.fullLength = fullLength;
    }

    public void setSampleId(String sID){
        sampleID = sID;
    }

    public String getSampleID(){
        return sampleID;
    }

    public void setGls(String gls){
        this.gls = gls;
    }

    public String getGls(){
        return gls;
    }


    public void setPhase(String phase){
        this.phaseSet = phase;
    }

    public String getPhase(){
        return phaseSet;
    }

    public String getFive_NS() {
        return geneData.get(SectionName.DS);
    }

    public void setFive_UTR(String five_NS) {
        geneData.put(SectionName.DS, five_NS);
    }

    public String getExon(SectionName sn) {
        if(geneData.containsKey(sn)){
            if(geneData.get(sn).length() == 0){
                return "null";
            }else {
                return geneData.get(sn);
            }
        } else
            return "null";
    }

    public String getIntron(SectionName sn) {
        if(geneData.containsKey(sn))
            if(geneData.get(sn).length() == 0){
                return "null";
            }else {
                return geneData.get(sn).toLowerCase();
            }

        else
            return "null";
    }





    public String getExon_pl(SectionName sn) {
        if(plData.containsKey(sn)){
            return plData.get(sn);
        }
        else {
            return "null";
        }
    }

    public void setExon_pl(SectionName sn, String pl){
        plData.put(sn, pl);
    }



    protected String filterDivider(String seq){
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < seq.length(); i++){
            if(seq.charAt(i) != DIVIDER){
                sb.append(seq.charAt(i));
            }
        }
        return sb.toString();
    }
    /**
     * Separate exon and intron sequence from original geneData.
     * @param data One line text from alignment file.
     * @param exon List of index of exon start and end position.
     * @param intron List of index of intron start and end position.
     */
    abstract public void setExonIntron(String data, List<Integer> exon, List<Integer> intron);
    abstract public void setExonIntronNoFilter(String data, List<Integer> exon, List<Integer> intron);

    abstract public String getCDS();

    public String toString(){
        return sampleID + " "+ gls + " "+ phaseSet;
    }

    abstract  public String toFasta();

    abstract public  String getCDNA();

    public void setProtein(String protien){
        this.protien = protien;
    }

    public String getProtein() {
        return this.protien;

    }
    abstract public String toCVS();
}

