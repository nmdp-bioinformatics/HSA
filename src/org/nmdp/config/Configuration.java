package org.nmdp.config;

import org.nmdp.HLAGene.HLAGene;
import org.nmdp.HLAGene.SectionName;
import org.nmdp.parseHML.Mode;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * Created by Will on 6/5/16.
 */
public class Configuration {
    public static Mode mode;
    public static boolean expand;
    public static String CLUSTALO;
    private static Map<HLAGene, List<SectionName>> sectionMap = new HashMap<>();
    public static void loadSetting() throws FileNotFoundException {
        Scanner sn = new Scanner(new File("config.txt"));
        CLUSTALO = sn.nextLine();
        String[] modeSetting = sn.next().split(",");
        mode = Mode.valueOf(modeSetting[1].toUpperCase());

        //No error checking. user has to make sure to enter "false" or "true" to this option.
        String[] expandSetting = sn.next().split(",");
        expand = "true".equals(expandSetting[1].toLowerCase());

        while (sn.hasNext()){
            String[] geneSetting = sn.next().split(",");
            HLAGene gene = HLAGene.valueOf(geneSetting[0]);
            SectionName start = SectionName.valueOf(geneSetting[1]);
            SectionName end = SectionName.valueOf(geneSetting[2]);
            List<SectionName> section = new ArrayList<>(2);
            section.add(start);
            section.add(end);
            sectionMap.put(gene, section);
        }
    }

    public static List<SectionName> getSection(HLAGene gene) throws Exception {
        if(sectionMap.containsKey(gene)){
            return sectionMap.get(gene);
        }else{
            SectionName[] temp;
            switch (gene){
                case HLA_A:
                case HLA_C:
                    temp = new SectionName[]{SectionName.US, SectionName.DS};
                    return Arrays.asList(temp);
                case HLA_B:
                    temp = new SectionName[]{SectionName.US, SectionName.e7};
                    return Arrays.asList(temp);
                default:
                    throw new Exception("The section data is not set for " + gene.toString());
            }
        }
    }
}
