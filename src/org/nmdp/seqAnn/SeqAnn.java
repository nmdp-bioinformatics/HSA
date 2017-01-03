package org.nmdp.seqAnn;

import org.nmdp.HLAGene.ExonIntronData;
import org.nmdp.HLAGene.HLAGene;
import org.nmdp.HLAGene.HLAGeneData;
import org.nmdp.HLAGene.SectionName;
import org.nmdp.config.Configuration;
import org.nmdp.databaseAccess.DatabaseUtil;
import org.nmdp.translate.Translator;
import org.nmdp.util.FileSystem;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

/**
 * Created by wwang on 7/25/16.
 */
public class SeqAnn {

    private int looper = 50;
    private String refSeq;
    private ArrayList<Integer> indexIntron = new ArrayList<Integer>();
    private ArrayList<Integer> indexExon = new ArrayList<Integer>();
    private Scanner inputSn;
    private String TAG = "SeqAnn";
    private static final char DIVIDER = '-';
    private PrintWriter pw;
    private ExonIntronData refData;
    private Translator translator = new Translator();
    private HLAGene gene;

    public static void main(String[] args){

    }

    public void process(){
        process(HLAGene.HLA_A);
        process(HLAGene.HLA_B);
        process(HLAGene.HLA_C);
        process(HLAGene.PB_DPB1);
        process(HLAGene.HLA_DQB1);
        process(HLAGene.HLA_DRB1);
    }

    private  void process(HLAGene gene){
        //process HLA_A
        List<SectionName> sectionNames = null;
        try {
            sectionNames = Configuration.getSection(gene);
        } catch (Exception e) {
            e.printStackTrace();
        }
        HLAGeneData.setType(gene, sectionNames.get(0), sectionNames.get(1));
        processFolder(FileSystem.getCluFolder(gene));
    }

    public void processFolder(File folder){
        File[] inputList = folder.listFiles();
        for (int i = 0; i < inputList.length; i++) {
                processFile(inputList[i]);
        }

    }

    public void processFile(File input){
        String fileName = input.getName();
        try {
            pw = new PrintWriter(new File(input.getParent() + fileName + ".ann"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        countExonIndex(input);
        refData = new HLAGeneData();
        refData.setExonIntron(refSeq, indexExon, indexIntron);
        refData.setProtein(translator.translate(refData.getCDNA(), gene.getFrame()));
        extratExons(input);

    }

    /**
     * find the index of all exon and intron
     */
    private void countExonIndex(File input) {
        indexExon.clear();
        indexIntron.clear();
        try {
            inputSn = new Scanner(input);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        //Skip three lines to reference
        while (inputSn.hasNextLine()){
            String line = inputSn.nextLine();
            if(line.length() <12){
                continue;
            }
            String header = line.substring(0,16);
            if(header.contains("RefSeq")){
                refSeq = line;
                break;
            }

        }

        //Find the first divider
        while(!Character.isLetter(refSeq.charAt(looper))){
            looper ++;
        }
        while(looper < refSeq.length()){
            findIntron();
            findExon();
        }
        //Reset the looper to start position after processing one gene.
        looper = 70;
        inputSn.close();

    }
    private void findIntron() {
        if(looper >= refSeq.length()){
            return;
        }
        while(looper < refSeq.length() && !Character.isLowerCase(refSeq.charAt(looper))){
            looper++;
        }
        int start = looper;
        while(looper < refSeq.length() && (Character.isLowerCase(refSeq.charAt(looper)) || refSeq.charAt(looper) == DIVIDER) ){
            looper++;
        }
        int end = looper-1;
        indexIntron.add(start);
        indexIntron.add(end);
        System.out.println(TAG + "intron: "+refSeq.substring(start, end+1));
    }

    private void findExon(){
        if(looper >= refSeq.length()){
            return;
        }
        while(looper < refSeq.length() && Character.isLowerCase(refSeq.charAt(looper)) ){
            looper++;
        }
        int start = looper;
        while(looper < refSeq.length() && (!Character.isLowerCase(refSeq.charAt(looper)) || refSeq.charAt(looper) == DIVIDER)){
            looper++;

        }
        int end = looper-1;
        indexExon.add(start);
        indexExon.add(end);
        System.out.println(TAG + "exon: "+refSeq.substring(start, end+1));
    }

    /**
     * extra the sequences of exons from alignment results which might contains gaps.
     */
    private void extratExons(File input)  {
        try {
            inputSn = new Scanner(input);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        //Skip four lines (header of alignment) to first row of geneData
        inputSn.nextLine();
        inputSn.nextLine();
        inputSn.nextLine();
        while(inputSn.hasNext()){
            try {
                String data = inputSn.nextLine();
                if(data.length() > 16 && data.substring(0,16).contains("RefSeq")){
                    break;
                }
                processSample(data);
                pw.println();
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
        }
        inputSn.close();
        pw.close();
    }
    private void processSample(String data) throws Exception {
        if(data.charAt(0) == ' '){
            //If the geneData is the last line, do not process
            return;
        }
        //split the geneData by white space or |
        String[] split = data.split(" |\\|");
        ExonIntronData ei = new HLAGeneData();
        ei.setSampleId(split[1]);
        ei.setGeneType(split[3]);
        ei.setType(split[5]);
        ei.setGls(split[7]);
        ei.setPhase(split[8]);
        ei.setExonIntron(data, indexExon, indexIntron);
        ei.setProtein(translator.translate(ei.getCDNA(), gene.getFrame()));
        //print sample id, gl string and phasing set.
        pw.print(split[1]);
        pw.print("|");
        pw.print(split[7]);
        pw.print("|");
        pw.print(split[8]);
        pw.print("\t");

        List<SectionName> geneSections = HLAGeneData.geneSections;
        SectionName pre = null;
        HashMap<SectionName, String> frameMap = new HashMap<>();
        for(SectionName sn : geneSections){
            if(sn == SectionName.DS || sn == SectionName.US){
                continue;
            }
            if(sn.isExon()){
                checkFrame(ei, frameMap, sn, pre);
                pre = sn;
            }
        }

        for(SectionName sn : geneSections){
            if(sn == SectionName.DS || sn == SectionName.US){
                continue;
            }
            // pw.println();
            // pw.println("Now is processing " + sn.toString());
            // separate the regions by tab
            // pw.println("\t");
            if(sn.isExon()){
                compareExon(frameMap, sn, ei);
            }else {
                compareIntron(sn, ei);
            }
            pw.print("\t");
        }


    }

    private void checkFrame(ExonIntronData ei, HashMap<SectionName, String> map, SectionName sn, SectionName pre) {
        FrameGenerater  generater;
        if(pre == null){
            generater = new FrameGenerater(0);
        }else {
            String frameData = map.get(pre);
            generater = new FrameGenerater(frameData.charAt(frameData.length()-1) - '0');
        }
        StringBuilder sb = new StringBuilder();
        String input = ei.getExon(sn);
        //3 is invalid frame number. Valid number are 0,1,2
        int preFrame = 3;
        for(int i = 0; i< input.length(); i++){
            if(input.charAt(i) == '-'){
                sb.append(preFrame);
            }else {
                sb.append(generater.getFrame());
            }
        }
        map.put(sn, sb.toString());

    }

    private class FrameGenerater{
        private int seed = 0;

        public FrameGenerater(int seed){
            this.seed = seed;
        }

        public int getFrame(){
            int oldSeed = seed;
            seed = (seed + 1)%3;
            return oldSeed;
        }

    }

    private void compareIntron(SectionName sn, ExonIntronData ei) {
        String ref = refData.getIntron(sn);
        String data = ei.getIntron(sn);
        for(int i = 0; i< ref.length(); i++){
            if(i >= data.length()){
                pw.print(sn.toString());
                pw.print(":");
                pw.print(i+1);
                pw.print(ref.charAt(i));
                pw.print(">");
                pw.print("-");

            }else{
                if(ref.charAt(i) == data.charAt(i)){
                    continue;
                }else{
                    pw.print(sn.toString());
                    pw.print(":");
                    pw.print(i+1);
                    pw.print(ref.charAt(i));
                    pw.print(">");
                    pw.print(data.charAt(i));
                    pw.print(" ");
                }
            }

        }
    }

    private void compareExon(HashMap<SectionName, String> map, SectionName sn, ExonIntronData ei) {
        String ref = refData.getExon(sn);
        String data = ei.getExon(sn);
        Translator translator = new Translator();
        for(int i = 0; i< ref.length(); i++){
            if(i < data.length() && ref.charAt(i) == data.charAt(i)){
                continue;
            }else{
                int protienPos = (i+1)/3 +1;
                pw.print(sn.toString());
                pw.print(":");
                pw.print(i+1);
                pw.print("(");
                pw.print(protienPos);
                pw.print(")");
                //pw.print(ref.charAt(i));
                String beforeDNA = getRefAmio(sn,i);
                pw.print(beforeDNA);
                pw.print("(");
                pw.print(translator.dna2aa(beforeDNA));
                pw.print(")");
                pw.print(">");
                //pw.print(data.charAt(i));
                String afterDNA = getAmio(map,sn,ei, i);
                pw.print(afterDNA);
                pw.print("(");
                pw.print(translator.dna2aa(afterDNA));
                pw.print(")");
                pw.print(" ");

            }
        }
    }

    private String getAmio(HashMap<SectionName, String> map, SectionName sn, ExonIntronData ei, int index){
        String exon = ei.getExon(sn);
        String frameDate = map.get(sn);
        if(frameDate.charAt(index) == 0){
            if(exon.charAt(index) != '-'){
                return findThree(exon, index);
            }else {
                String one = findOneBackward(exon, index-1);
                String two = findTwoForward(exon, index+1);
                return one + two;
            }
        }else if(frameDate.charAt(index) == 1){
            if(exon.charAt(index) != '-'){
                String one = findOneBackward(exon, index-1);
                String lastOne = findOneForward(exon, index+1);
                return one + exon.charAt(index) + lastOne;
            }else{
                String two = findTwoBackward(exon, index-1);
                String one = findOneForward(exon, index+1);
                return two+one;
            }
        }else if(frameDate.charAt(index) == 2){
            if(exon.charAt(index) != '-'){
                String two = findTwoBackward(exon, index-1);
                return two + exon.charAt(index);
            }else{
                String two = findTwoBackward(exon, index-1);
                String one = findOneForward(exon, index+1);
                return two+one;
            }
        }else {
            //frame is 3 which is not valid
            return findThreeNoinclude(exon,index);
        }
    }

    private String findOneForward(String exon, int index) {
        String one = "";
        while(index < exon.length() && exon.charAt(index) == '-'){
            index++;
        }
        if(index < exon.length()){
            one  += exon.charAt(index);
        }
        return one;
    }

    private String findTwoForward(String exon, int index) {
        String two = "";
        while(index < exon.length() && exon.charAt(index) == '-'){
            index++;
        }
        if(index < exon.length()){
            two  += exon.charAt(index);
        }
        index++;
        while(index < exon.length() && exon.charAt(index) == '-'){
            index++;
        }
        if(index < exon.length()){
            two  += exon.charAt(index);
        }
        return two;

    }

    private  String findTwoBackward(String exon, int index){
        String two = "";
        while(index >= 0 && exon.charAt(index) == '-'){
            index--;
        }
        if(index >= 0){
            two  += exon.charAt(index);
        }
        index--;
        while(index >= 0 && exon.charAt(index) == '-'){
            index--;
        }
        if(index < exon.length()){
            two  += exon.charAt(index);
        }
        return two;
    }

    private String findThree(String exon, int index){
        String three = "";
        three  += exon.charAt(index);
        index ++;
        while(index < exon.length() && exon.charAt(index) == '-'){
            index++;
        }
        if(index < exon.length()){
            three  += exon.charAt(index);
        }
        index ++;
        while(index < exon.length() && exon.charAt(index) == '-'){
            index++;
        }

        if(index < exon.length()){
            three  += exon.charAt(index);
        }
        return three;

    }

    private String findThreeNoinclude(String exon, int index){
        String three = "";
        while(index < exon.length() && exon.charAt(index) == '-'){
            index++;
        }
        if(index < exon.length()){
            three  += exon.charAt(index);
        }
        index ++;
        while(index < exon.length() && exon.charAt(index) == '-'){
            index++;
        }

        if(index < exon.length()){
            three  += exon.charAt(index);
        }
        index ++;
        while(index < exon.length() && exon.charAt(index) == '-'){
            index++;
        }

        if(index < exon.length()){
            three  += exon.charAt(index);
        }
        return three;

    }

    private String findOneBackward(String exon, int index){
        String one = "";
        while(index >=0 && exon.charAt(index)== '-'){
            index--;
        }
        if(index >= 0){
            one = one + exon.charAt(index);
        }
        return one;
    }

    private String getRefAmio(SectionName sn, int index){
        String exon = refData.getExon(sn);
        if(index %3 == 0){
            return exon.substring(index,index + 3);
        }else if(index % 3 == 1){
            return exon.substring(index-1, index +2);
        }else {
            return exon.substring(index-2, index+1);
        }
    }


}
