package org.nmdp.HLAGene;

/**
 * Created by Will on 6/4/16.
 */
public enum HLAGene {
    HLA_A(0),
    HLA_B(0),
    HLA_C(0),
    HLA_DRB1(2),
    HLA_DPB1(2),
    HLA_DQB1(2),
    PB_DRB1(2),
    PB_DPB1(2),
    PB_DQB1(2);

    private int frame;

    HLAGene(int frame){
        this.frame = frame;
    }

    public int getFrame(){
        return frame;
    }
}
