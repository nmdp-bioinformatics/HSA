package org.nmdp.config;

import org.nmdp.HLAGene.HLAGene;
import org.nmdp.HLAGene.SectionName;
import org.nmdp.parseHML.Mode;
import org.nmdp.util.FileSystem;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * Created by Will on 6/5/16.
 */
public class Configuration {
    public static Mode mode;
    public static boolean expand;
    public static String gfeLoadOutput;
    private static Map<HLAGene, List<SectionName>> sectionMap = new HashMap<>();
    public static void loadSetting(String path) throws FileNotFoundException {
        System.out.println("before to load setting");
        System.out.println(path);
        Scanner sn = new Scanner(new File(path));
        System.out.println("after access file config.txt");
        String[] modeSetting = sn.next().split(",");
        mode = Mode.valueOf(modeSetting[1].toUpperCase());

        //No error checking. user has to make sure to enter "false" or "true" to this option (It was changed to -g option for annotating fasta input only).
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
            //add New Case based on the number of exon
            switch (gene){
                case HLA_A:
                case HLA_C:
                    temp = new SectionName[]{SectionName.US, SectionName.i8};
                    return Arrays.asList(temp);
                case HLA_B:
                case ABO
                    temp = new SectionName[]{SectionName.US, SectionName.i7};
                    return Arrays.asList(temp);
                case KIR3DL2:
                case KIR2DP1:
                case KIR3DS1:
                case KIR3DL1:
                    temp = new SectionName[]{SectionName.US, SectionName.DS};
                    return Arrays.asList(temp);
                case KIR2DL4:
                case KIR2DL5A:
                case KIR2DL5B:
                case KIR2DS1:
                case KIR2DS2:
                case KIR2DS3:
                case KIR2DS4:
                case KIR2DS5:
                case KIR3DL3:
                case KIR2DL1:
                case KIR2DL2:
                case KIR2DL3:
                    temp = new SectionName[]{SectionName.US, SectionName.i8};
                    return Arrays.asList(temp);
                case KIR3DP1:
                    temp = new SectionName[]{SectionName.US, SectionName.i5};
                    return Arrays.asList(temp);
                default:
                    throw new Exception("The section data is not set for " + gene.toString());
            }
        }
    }
}
