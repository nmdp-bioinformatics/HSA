package org.nmdp.HLAGene;

/**
 * Created by Will on 6/4/16.
 */
public enum HLAGene {
    HLA_A(0, 8, 7),
    HLA_B(0, 7, 6),
    HLA_C(0, 8, 7),
    HLA_DRB1(2, 5,4),
    HLA_DPB1(2, 5,4),
    HLA_DQB1(2, 6, 5),
    PB_DRB1(2, 5, 4),
    PB_DPB1(2, 5, 4),
    PB_DQB1(2, 6, 5);

    private int frame;
    private int exonNum;
    private int intronNum;

    HLAGene(int frame, int exon, int intron){
        this.frame = frame;
        exonNum = exon;
        intronNum = intron;
    }

    public int getFrame(){
        return frame;
    }

    public int getExonNumber(){
        return this.exonNum;
    }
    public int getIntronNumber(){
        return  this.intronNum;
    }
}
