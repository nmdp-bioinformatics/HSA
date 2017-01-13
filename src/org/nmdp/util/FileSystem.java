package org.nmdp.util;

import org.nmdp.HLAGene.HLAGene;
import org.nmdp.config.Configuration;

import java.io.File;

/**
 * Created by Will on 6/4/16.
 */
public class FileSystem {
    public static String ROOT = "/output";
    public static String FOLDER = "/output/";
    public static final String FASTA = "/fasta/";
    public static final String CLU = "/clu/";
    public static final String EXON = "/exon/";
    public static final String PROTEIN = "/protein/";
    public static final String GFE = "/GFE/parsed-local/";
    public static final String REF = "/ref/";
    public static final String HLA_A_REF = "HLA-A_Ref.clu";
    public static final String HLA_B_REF = "HLA-B_Ref.clu";
    public static final String HLA_C_REF = "HLA-C_Ref.clu";
    public static final String HLA_DPB1_REF = "HLA-DPB1_Ref.clu";
    public static final String HLA_DQB1_REF = "HLA-DQB1_Ref.clu";
    public static final String HLA_DRB1_REF = "HLA-DRB1_Ref.clu";
    public static final String KIR3DL1 = "KIR3DL1_Ref.clu";
    public static final String KIR3DP1 = "KIR3DP1_Ref.clu";
    public static final String KIR2DL4 ="KIR2DL4_Ref.clu";
    public static final String KIR2DL5A ="KIR2DL5A_Ref.clu";
    public static final String KIR2DL5B ="KIR2DL5B_Ref.clu";
    public static final String KIR2DS1 ="KIR2DS1_Ref.clu";
    public static final String KIR2DS2 ="KIR2DS2_Ref.clu";
    public static final String KIR2DS3 ="KIR2DS3_Ref.clu";
    public static final String KIR2DS4 ="KIR2DS4_Ref.clu";
    public static final String KIR2DS5 ="KIR2DS5_Ref.clu";
    public static final String KIR3DL3 ="KIR3DL3_Ref.clu";
    public static final String KIR3DL2 ="KIR3DL2_Ref.clu";
    public static final String KIR2DP1 ="KIR2DP1_Ref.clu";
    public static final String KIR3DS1 ="KIR3DS1_Ref.clu";
    public static final String KIR2DL1 ="KIR2DL1_Ref.clu";
    public static final String KIR2DL2 ="KIR2DL2_Ref.clu";
    public static final String KIR2DL3 ="KIR2DL3_Ref.clu";


    //add the new string for new types and the new pre-alignments
    //public static final String ABO ="ABO_Ref.clu";




    public static File getFastaFile(HLAGene gene, String fileName){
        String folder = FOLDER +FASTA+ gene.toString();
        File directory = new File(folder);
        if(!directory.exists()){
            directory.mkdirs();
        }
        return new File(folder+"/"+fileName+".fasta");
    }

    public static File getFastaFile(String fileName){
        String folder = FOLDER +FASTA;
        File directory = new File(folder);
        if(!directory.exists()) {
            directory.mkdirs();
        }

        return new File( folder + fileName+".fasta");
    }

    public static File getCvsFile(String fileName){
        return new File(ROOT + "/" + fileName + "_ann.csv");
    }

    public static File getRefFile(HLAGene gene){
        switch (gene){
            //addNewCase2
            case HLA_A:
                return new File(ROOT+REF+HLA_A_REF);
            case HLA_B:
                return new File(ROOT+REF+HLA_B_REF);
            case HLA_C:
                return new File(ROOT+REF+HLA_C_REF);
            case HLA_DPB1:
                return new File(ROOT+REF+HLA_DPB1_REF);
            case HLA_DQB1:
                return new File(ROOT+REF+HLA_DQB1_REF);
            case HLA_DRB1:
                return new File(ROOT+REF+HLA_DRB1_REF);
            case PB_DRB1:
                return new File(ROOT+REF+HLA_DRB1_REF);
            case PB_DPB1:
                return new File(ROOT+REF+HLA_DPB1_REF);
            case PB_DQB1:
                return new File(ROOT+REF+HLA_DQB1_REF);
            case KIR3DL1:
                return new File(ROOT+REF+KIR3DL1);
            case KIR3DP1:
                return new File(ROOT+REF+KIR3DP1);
            case KIR2DL4:
            return new File(ROOT+REF+KIR2DL4);
            case KIR2DL5A:
            return new File(ROOT+REF+KIR2DL5A);
            case KIR2DL5B:
            return new File(ROOT+REF+KIR2DL5B);
            case KIR2DS1:
            return new File(ROOT+REF+KIR2DS1);
            case KIR2DS2:
            return new File(ROOT+REF+KIR2DS2);
            case KIR2DS3:
            return new File(ROOT+REF+KIR2DS3);
            case KIR2DS4:
            return new File(ROOT+REF+KIR2DS4);
            case KIR2DS5:
            return new File(ROOT+REF+KIR2DS5);
            case KIR3DL3:
            return new File(ROOT+REF+KIR3DL3);
            case KIR3DL2:
            return new File(ROOT+REF+KIR3DL2);
            case KIR2DP1:
            return new File(ROOT+REF+KIR2DP1);
            case KIR3DS1:
            return new File(ROOT+REF+KIR3DS1);
            case KIR2DL1:
                return new File(ROOT+REF+KIR2DL1);
            case KIR2DL2:
                return new File(ROOT+REF+KIR2DL2);
            case KIR2DL3:
                return new File(ROOT+REF+KIR2DL3);
            case ABO:
            return new File(ROOT+REF+ABO);
            default:
                return null;
        }
    }

    public static File getCluFile(HLAGene gene, String fileName){
        String folder = FOLDER +CLU+ gene.toString();
        File directory = new File(folder);
        if(!directory.exists()){
            directory.mkdirs();
        }
        return new File(folder+"/"+fileName+".clu");
    }

    public static File getExonFile(HLAGene gene, String fileName){
        String folder = FOLDER + EXON + gene.toString();
        File directory = new File(folder);
        if(!directory.exists()){
            directory.mkdirs();
        }
        return new File(folder+"/"+fileName+".txt");

    }

    public static File getProteinFile(HLAGene gene, String fileName){
        String folder = FOLDER + PROTEIN + gene.toString();
        File directory = new File(folder);
        if(!directory.exists()){
            directory.mkdirs();
        }
        return new File(folder+"/"+fileName+".fasta");

    }

    public static File getCluFolder(HLAGene gene){
        return new File(ROOT + CLU + gene.toString());
    }

    public static File getReformatFile(HLAGene gene, String fileName){
        String folder = GFE;
        File directory = new File(folder);
        if(!directory.exists()){
            directory.mkdirs();
        }
        return new File(Configuration.gfeLoadOutput +"/" + fileName+"_reformat" + ".csv");
    }

    public static File getFeatureFile(HLAGene gene){
        String folder = GFE ;
        String fileName = "";
        switch (gene){
            case HLA_A:
                fileName = "A.features.txt";
                break;
            case HLA_B:
                fileName = "B.features.txt";
                break;
            case HLA_C:
                fileName = "C.features.txt";
                break;
            case HLA_DPB1:
                fileName = "DPB1.features.txt";
                break;
            case HLA_DQB1:
                fileName = "DQB1.features.txt";
                break;
            case HLA_DRB1:
                fileName = "DRB1.features.txt";
                break;
        }
        return new File(ROOT + folder + fileName);
    }

    public static File getGfeOutput(HLAGene gene){
        String folder = GFE ;
        String fileName = gene.toString() + "_gfe.txt";
        return new File(ROOT + folder + fileName);

    }
    public static void deleteFeatureFile(HLAGene gene){
        File file = getFeatureFile(gene);
        file.delete();
    }

    public static String getFileName(File input) {
        String name = input.getName();
        String[] data = name.split("\\.");
        return data[0];

    }
}
