package org.nmdp.util;

import org.nmdp.HLAGene.HLAGene;

import java.io.File;

/**
 * Created by Will on 6/4/16.
 */
public class FileSystem {
    public static final String ROOT = "./output";
    public static final String FASTA = "/fasta/";
    public static final String CLU = "/clu/";
    public static final String EXON = "/exon/";
    public static final String PROTEIN = "/protein/";
    public static final String REF = "./ref/";
    public static final String HLA_A_REF = "HLA-A_Ref.clu";
    public static final String HLA_B_REF = "HLA-B_Ref.clu";
    public static final String HLA_C_REF = "HLA-C_Ref.clu";


    public static File getFastaFile(HLAGene gene, String fileName){
        String folder = ROOT +FASTA+ gene.toString();
        File directory = new File(folder);
        if(!directory.exists()){
            directory.mkdirs();
        }
        return new File(folder+"/"+fileName+".fasta");
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
        return new File(folder+"/"+fileName+".txt");

    }
}
