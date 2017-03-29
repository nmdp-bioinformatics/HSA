package org.nmdp.HLAGene;



import com.sun.tools.internal.xjc.reader.xmlschema.bindinfo.BIConversion;
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
    static HLAGene gene;


    public static void setType(HLAGene g, SectionName start, SectionName end){
        if(g == HLAGene.ABO){
            //set up gene sections for ABO, which contains all exons.
            geneSections.clear();
            geneSections.add(SectionName.e1);
            geneSections.add(SectionName.e2);
            geneSections.add(SectionName.e3);
            geneSections.add(SectionName.e4);
            geneSections.add(SectionName.e5);
            geneSections.add(SectionName.e6);
            geneSections.add(SectionName.e7);

        }else if(g == HLAGene.DPB1 || g == HLAGene.DQB1 || g == HLAGene.DRB1){
            //set up gene sections for ABO, which contains all exons.
            geneSections.clear();
            geneSections.add(SectionName.e1);
            geneSections.add(SectionName.e2);
        }else {
            List<SectionName> dic = Arrays.asList(SectionName.values());
            geneSections.clear();
            HLAGeneData.start = start;
            HLAGeneData.end = end;
            gene = g;
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
    public void setExonIntron(String data, List<Integer> exon, List<Integer> intron) {
        int extornIndex = 0;
        int intronIndex = 0;
        StringBuilder sb = new StringBuilder();
        for(SectionName sn : geneSections){
            try{
                if(sn.isExon()){
                    geneData.put(sn, filterDivider(data.substring(exon.get(extornIndex), exon.get(extornIndex+1)+1)));
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
    public void setExonIntronNoFilter(String data, List<Integer> exon, List<Integer> intron) {
        int extornIndex = 0;
        int intronIndex = 0;
        StringBuilder sb = new StringBuilder();
        for(SectionName sn : geneSections){
            try{
                if(sn.isExon()){
                    geneData.put(sn, data.substring(exon.get(extornIndex), exon.get(extornIndex+1)+1));
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

    public String toCVS(){
        StringBuilder sb = new StringBuilder();
        sb.append(getSampleID() + ",");
        sb.append(getGls()+ ",");
        sb.append(getPhase() + ",");
        sb.append(getIntron(SectionName.US) + ",");
        sb.append(getExon(SectionName.e1) + ",");
        sb.append(getIntron(SectionName.i1) + ",");
        sb.append(getExon(SectionName.e2) + ",");
        sb.append(getIntron(SectionName.i2) + ",");
        sb.append(getExon(SectionName.e3) + ",");
        sb.append(getIntron(SectionName.i3) + ",");
        sb.append(getExon(SectionName.e4) + ",");
        sb.append(getIntron(SectionName.i4) + ",");
        sb.append(getExon(SectionName.e5) + ",");
        sb.append(getIntron(SectionName.i5) + ",");
        sb.append(getExon(SectionName.e6) + ",");
        sb.append(getIntron(SectionName.i6) + ",");
        sb.append(getExon(SectionName.e7) + ",");
        sb.append(getIntron(SectionName.i7) + ",");
        sb.append(getExon(SectionName.e8) + ",");
        sb.append(getIntron(SectionName.i8) + ",");
        sb.append(getExon(SectionName.e9) + ",");
        sb.append(getIntron(SectionName.DS)+ ",");
        sb.append(getExon_pl(SectionName.e1)+ ",");
        sb.append(getExon_pl(SectionName.e2)+ ",");
        sb.append(getExon_pl(SectionName.e3)+ ",");
        sb.append(getExon_pl(SectionName.e4)+ ",");
        sb.append(getExon_pl(SectionName.e5)+ ",");
        sb.append(getExon_pl(SectionName.e6)+ ",");
        sb.append(getExon_pl(SectionName.e7)+ ",");
        sb.append(getExon_pl(SectionName.e8)+ ",");
        return sb.toString();
    }


}
