package org.nmdp.HLAGene;



import org.nmdp.HLAGene.SectionName;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by wwang on 5/30/16.
 */
public class HLAGeneData  extends ExonIntronData{
    public static final String TAG = " HLAGeneData ";
    static SectionName start;
    static SectionName end;
    public static List<SectionName> geneSections = new ArrayList<>();
    static String cDNA;

    public static void setType(SectionName start, SectionName end){
        List<SectionName> dic = Arrays.asList(SectionName.values());
        geneSections.clear();
        HLAGeneData.start = start;
        HLAGeneData.end = end;

        boolean add = false;
        for(int i = 0; i< dic.size(); i++){
            if(dic.get(i) == start){
                add = true;
            }
            if(dic.get(i) == end){
                geneSections.add(dic.get(i));
                add = false;
            }
            if(add){
                geneSections.add(dic.get(i));
            }

        }
    }

    /**
     * Return the sorted section list. Exon 5DS Intron 3DS
     * @return he sorted section list.
     */
    public static List<SectionName> sortSectionList(){
        boolean hasFive = false;
        boolean hasThree = false;
        List<SectionName> exons = new ArrayList<SectionName>();
        List<SectionName> intron = new ArrayList<SectionName>();
        for(SectionName sectionName : geneSections){
            if(sectionName.isExon()){
                exons.add(sectionName);
            }else if(sectionName == SectionName.US){
                hasFive = true;
            }else if(sectionName == SectionName.DS){
                hasThree = true;
            }else {
                intron.add(sectionName);
            }
        }
        if(hasFive){
            exons.add(SectionName.US);
        }
        for(SectionName sectionName : intron){
            exons.add(sectionName);
        }
        if(hasThree){
            exons.add(SectionName.DS);
        }
        return exons;

    }


    @Override
    public void setExonIntron(String data, List<Integer> extron, List<Integer> intron) {
        int extornIndex = 0;
        int intronIndex = 0;
        StringBuilder sb = new StringBuilder();
        for(SectionName sn : geneSections){
            try{
                if(sn.isExon()){
                    geneData.put(sn, filterDivider(data.substring(extron.get(extornIndex), extron.get(extornIndex+1)+1)));
                    sb.append(geneData.get(sn));
                    extornIndex+=2;
                }else {
                    geneData.put(sn, filterDivider(data.substring(intron.get(intronIndex), intron.get(intronIndex+1)+1)));
                    intronIndex+=2;
                }
            }catch (IndexOutOfBoundsException e){
                System.out.println(TAG + "The input format is wrong. It contains more sections than setting.");
            }

        }
        cDNA = sb.toString();
        sb = null;

    }

    @Override
    public void setExonIntronNoFilter(String data, List<Integer> extron, List<Integer> intron) {
        int extornIndex = 0;
        int intronIndex = 0;
        StringBuilder sb = new StringBuilder();
        for(SectionName sn : geneSections){
            try{
                if(sn.isExon()){
                    geneData.put(sn, data.substring(extron.get(extornIndex), extron.get(extornIndex+1)+1));
                    sb.append(geneData.get(sn));
                    extornIndex+=2;
                }else {
                    geneData.put(sn, data.substring(intron.get(intronIndex), intron.get(intronIndex+1)+1));
                    intronIndex+=2;
                }
            }catch (IndexOutOfBoundsException e){
                System.out.println(TAG + "The input format is wrong. It contains more sections than setting.");
            }

        }
        cDNA = sb.toString();
        sb = null;
    }

    @Override
    public String toFasta() {
        StringBuilder sb = new StringBuilder();
        sb.append(">id|");
        sb.append(getSampleID());
        sb.append("|locus|");
        sb.append(getGeneType());
        sb.append("|type|");
        sb.append(getType());
        sb.append("|gls|");
        sb.append(getGls());
        sb.append(getPhase());
        return sb.toString();
    }

    @Override
    public String getCDNA() {
        return cDNA;
    }

    @Override
    public String getCDS() {
        return "";
    }


}
