package org.nmdp.HLAGene;

/**
 * Created by Will on 6/4/16.
 */
// expanded to KIR, see readme and order_number

public enum HLAGene {
    HLA_A(0, 8, 7),
    HLA_B(0, 7, 6),
    HLA_C(0, 8, 7),
    HLA_DRB1(2, 5,4),
    HLA_DPB1(2, 5,4),
    HLA_DQB1(2, 6, 5),
    PB_DRB1(2, 5, 4),
    PB_DPB1(2, 5, 4),
    PB_DQB1(2, 6, 5),
    KIR3DP1(0, 5,4),
    KIR2DL4(0, 8, 7),
    KIR2DL5A(0, 8, 7),
    KIR2DL5B(0, 8, 7),
    KIR2DS1(0, 8, 7),
    KIR2DS2(0, 8, 7),
    KIR2DS3(0, 8, 7),
    KIR2DS4(0, 8, 7),
    KIR2DS5(0, 8, 7),
    KIR3DL3(0, 8, 7),
    KIR3DL1(0,9,8),
    KIR3DL2(0,9,8),
    KIR2DP1(0,9,8),
    KIR3DS1(0,9,8),
    KIR2DL1(0, 8, 7),
    KIR2DL2(0, 8, 7),
    KIR2DL3(0, 8, 7);
    //addNewCase3
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
