package org.nmdp.gfe;

import org.nmdp.HLAGene.HLAGene;
import org.nmdp.databaseAccess.DatabaseUtil;
import org.nmdp.util.FileSystem;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by wwang on 7/24/16.
 */
public class GFE {
     Scanner featureSn;
     Scanner inputSn;
    PrintWriter pw;
     List<GeneInfo> map = new ArrayList<>();
    HLAGene gene;

    public class GeneInfo{
        public String id;
        public String gls;
        public String phase;
        public HLAGene gene;
        public String gfe;
        int count;

        public GeneInfo(String id, String gls, String phase, HLAGene gene, int count){
            this.id = id;
            this.gls = gls;
            this.phase = phase;
            this.count = count;
            this.gene = gene;


        }
        public String toString(){
            return id + "," + gls + ","+phase;
        }

        public void setGFE(String gfe){
            this.gfe = gfe;
        }

    }

    public void process(HLAGene geneType) throws FileNotFoundException {
        featureSn = new Scanner(FileSystem.getFeatureFile(geneType));
        //inputSn = new Scanner(FileSystem.getReformatFile(geneType,));
        pw = new PrintWriter(FileSystem.getGfeOutput(geneType));
        gene = geneType;
        //Process the reformat file
        processOneGene(inputSn.nextLine());


        //Process the feature file
        for(GeneInfo data : map){
            String[] exon = new String[geneType.getExonNumber()];
            String[] intron = new String[geneType.getIntronNumber()];
            String five = "";
            String three = "";
            for(int i = 0; i < data.count; i++){
                String line = featureSn.nextLine();
                String[] lines = line.split("\\s+");
                if(lines[1].equals("exon")){
                    exon[Integer.parseInt(lines[2])-1] = lines[3];
                }else if (lines[1].equals("intron")){
                    intron[Integer.parseInt(lines[2])-1] = lines[3];
                }else if(lines[1].startsWith("five")){
                    five = lines[2];
                }else {
                    three = lines[3];
                }
            }
            StringBuilder sb = new StringBuilder();
            sb.append(geneType.toString());
            sb.append(five);
            int extonLoop = 0;
            int intronLoop = 0;
            while(extonLoop < geneType.getExonNumber()){
                sb.append("-");
                sb.append(exon[extonLoop]);
                sb.append("-");
                if(intronLoop < geneType.getIntronNumber()){
                    sb.append(intron[intronLoop]);
                }else {
                    sb.append(three);
                    break;
                }

                extonLoop++;
                intronLoop++;
            }
            pw.print(data.toString());
            pw.print(",");
            data.setGFE(sb.toString());
            pw.println(sb.toString());
            DatabaseUtil.insertGFEData(data);

        }

        //close resource
        featureSn.close();
        inputSn.close();
        pw.close();

    }

    private  void processOneGene(String firstLine) {
        String id;
        String gls;
        String phase;
        int count = 1;
        String[] data = firstLine.split(",");
        id = data[0];
        gls = data[1];
        phase = data[2];

        String line = "";
        while(inputSn.hasNext()){
            line = inputSn.nextLine();
            if(line.startsWith(id)){
                count++;
            }else{
                map.add(new GeneInfo(id, gls, phase, gene, count));
                System.out.println(id);
                break;
            }
        }
        if(line.startsWith(id)){
            map.add(new GeneInfo(id, gls, phase,gene, count));
            System.out.println(id);
        }else {
            processOneGene(line);
        }

    }
}
