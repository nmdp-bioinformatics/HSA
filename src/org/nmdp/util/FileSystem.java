package org.nmdp.util;

import org.nmdp.HLAGene.HLAGene;

import java.io.File;

/**
 * Created by Will on 6/4/16.
 */
public class FileSystem {
    public static String ROOT = "./output";
    public static final String FASTA = "/fasta/";
    public static final String CLU = "/clu/";
    public static final String EXON = "/exon/";
    public static final String PROTEIN = "/protein/";
    public static final String GFE = "./GFE/parsed-local/";
    public static final String REF = "./ref/";
    public static final String HLA_A_REF = "HLA-A_Ref.clu";
    public static final String HLA_B_REF = "HLA-B_Ref.clu";
    public static final String HLA_C_REF = "HLA-C_Ref.clu";
    public static final String HLA_DPB1_REF = "HLA-DPB1_Ref.clu";
    public static final String HLA_DQB1_REF = "HLA-DQB1_Ref.clu";
    public static final String HLA_DRB1_REF = "HLA-DRB1_Ref.clu";


    public static File getFastaFile(HLAGene gene, String fileName){
        String folder = ROOT +FASTA+ gene.toString();
        File directory = new File(folder);
        if(!directory.exists()){
            directory.mkdirs();
        }
        return new File(folder+"/"+fileName+".sta");
    }

    public static File getFastaFile(String fileName){
        String folder = ROOT +FASTA;
        File directory = new File(folder);
        if(!directory.exists()) {
            directory.mkdirs();
        }

        return new File( folder + fileName+".fasta");
    }

    public static File getRefFile(HLAGene gene){
        switch (gene){
            case HLA_A:
                return new File(REF+HLA_A_REF);
            case HLA_B:
                return new File(REF+HLA_B_REF);
            case HLA_C:
                return new File(REF+HLA_C_REF);
            case HLA_DPB1:
                return new File(REF+HLA_DPB1_REF);
            case HLA_DQB1:
                return new File(REF+HLA_DQB1_REF);
            case HLA_DRB1:
                return new File(REF+HLA_DRB1_REF);
            case PB_DRB1:
                return new File(REF+HLA_DRB1_REF);
            case PB_DPB1:
                return new File(REF+HLA_DPB1_REF);
            case PB_DQB1:
                return new File(REF+HLA_DQB1_REF);
            default:
                return null;
        }
    }

    public static File getCluFile(HLAGene gene, String fileName){
        String folder = ROOT +CLU+ gene.toString();
        File directory = new File(folder);
        if(!directory.exists()){
            directory.mkdirs();
        }
        return new File(folder+"/"+fileName+".clu");
    }

    public static File getExonFile(HLAGene gene, String fileName){
        String folder = ROOT + EXON + gene.toString();
        File directory = new File(folder);
        if(!directory.exists()){
            directory.mkdirs();
        }
        return new File(folder+"/"+fileName+".txt");

    }

    public static File getProteinFile(HLAGene gene, String fileName){
        String folder = ROOT + PROTEIN + gene.toString();
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
        return new File(folder + fileName + gene.toString()+"_reformat" + ".csv");
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
        return new File(folder + fileName);
    }

    public static File getGfeOutput(HLAGene gene){
        String folder = GFE ;
        String fileName = gene.toString() + "_gfe.txt";
        return new File(folder + fileName);

    }
    public static void deleteFeatureFile(HLAGene gene){
        File file = getFeatureFile(gene);
        file.delete();
    }
}
