package org.nmdp.HLAGene;



import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by wwang on 5/30/16.
 */
public class HLAGeneData  extends ExonIntronData{
    static SectionName start;
    static SectionName end;
    static List<SectionName> geneSections = new ArrayList<>();
    static String cDNA;

    public static void setType(SectionName start, SectionName end){
        List<SectionName> dic = Arrays.asList(SectionName.values());
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


    @Override
    public void setExonIntron(String data, List<Integer> extron, List<Integer> intron) {
        int extornIndex = 0;
        int intronIndex = 0;
        StringBuilder sb = new StringBuilder();
        for(SectionName sn : geneSections){
            try{
                if(sn.isExon()){
                    geneData.put(sn, filterDivider(data.substring(extron.get(extornIndex), extron.get(extornIndex+1))));
                    sb.append(geneData.get(sn));
                    extornIndex++;
                }else {
                    geneData.put(sn, filterDivider(data.substring(intron.get(intronIndex), intron.get(intronIndex+1))));
                    intronIndex++;
                }
            }catch (IndexOutOfBoundsException e){
                System.out.println("The input format is wrong. It contains more sections than setting.");
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
        sb.append("\n");
        sb.append(cDNA);
        return sb.toString();
    }
    @Override
    public String getCDS() {
        return "";
    }


}
