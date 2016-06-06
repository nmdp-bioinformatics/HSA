package org.nmdp.parseExon;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.nmdp.HLAGene.*;
import org.nmdp.config.Configuration;
import org.nmdp.scheduler.Task;
import org.nmdp.util.FileSystem;

public class    ParseExon{
	private Scanner scannerAlign;
	private Scanner scannerFreq;
	//cut the first 100 positions because of the low coverage.
	private int looper = 50;
	private String refSeq;
	private ArrayList<Integer> indexIntron = new ArrayList<Integer>();
	private ArrayList<Integer> indexExon = new ArrayList<Integer>();
	private ArrayList<ExonIntronData> seqList = new ArrayList<ExonIntronData> ();
	private ArrayList<BaseFreq> freqList = new ArrayList<BaseFreq>();
	private File inputAlign;
	private File inputFreq;
	private File output;
	private PrintWriter pw;
	private static final char DIVIDER = '-';

	public void process(Task task){
		File align = FileSystem.getCluFile(task.getGene(), task.getFileName());
		output = FileSystem.getExonFile(task.getGene(), task.getFileName());
		try {
			List<SectionName> sectionNames = Configuration.getSection(task.getGene());
			run(align, sectionNames.get(0), sectionNames.get(1));
		} catch (Exception e) {
			System.out.println("Can't find section setting for "+ task.getGene().toString());
			e.printStackTrace();
			return;
		}


	}

	/**
	 * Input files contains one alignment and frequency table and genetype.
	 * @param align alignment result from Clustal_Omega output in .Clu format.
	 * @param start
	 * @param end
	 */
	public void run(File align, SectionName start, SectionName end){
		inputAlign = align;
		setPrinter();
		HLAGeneData.setType(start, end);
		countExonIndex();
		extratExons();

	}


/**
 * Input files contains one alignment and frequency table and genetype.
 * @param align alignment result from Clustal_Omega output in .Clu format.
 * @param freq 
 * @param start
 * @param end
 * @throws Exception
 */
	public void run(File align, File freq, SectionName start, SectionName end) throws Exception{
		inputAlign = align;
		inputFreq = freq;
		
		setPrinter();
		HLAGeneData.setType(start, end);
		countExonIndex();
		extratExons();
		//extraFreq();
		//PolymorphStaticsProessor.processPolyMoph(freqList, seqList, indexExon, indexIntron, pw);
	}
	private void extraFreq() {
		try {
			scannerFreq = new Scanner(inputFreq);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//skip title: 8 lines
		for(int i = 0; i < 8; i++){
			scannerFreq.nextLine();
		}
		while(scannerFreq.hasNextLine()){
			String line = scannerFreq.nextLine();
			Scanner lineSc = new Scanner(line);
			if(!lineSc.hasNextInt()){
				lineSc.close();
				return;
			}
			lineSc.nextInt();	
			freqList.add(new BaseFreq(lineSc.nextInt(), lineSc.nextInt(), lineSc.nextInt(), lineSc.nextInt()));
			lineSc.close();
		}
		scannerFreq.close();
		
	}
	private void setPrinter() {
		try {
			pw = new PrintWriter(output);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * extra the sequences of exons from alignment results which might contains gaps.
	 */
	private void extratExons()  {
		try {
			scannerAlign = new Scanner(inputAlign);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//Skip four lines to first row of geneData
		scannerAlign.nextLine();
		scannerAlign.nextLine();
		scannerAlign.nextLine();
		scannerAlign.nextLine();
		while(scannerAlign.hasNext()){
			try {
				processSample(scannerAlign.nextLine());
			} catch (Exception e) {
				e.printStackTrace();
				break;
			}
		}
		scannerAlign.close();
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
		ei.setFullLength(data);
		seqList.add(ei);
		//TODO: enable insert database
		//DatabaseUtil.insertExonData(ei);
		pw.println(ei.toFasta());
	}
		
	/**
	 * find the index of all exon and intron
	 */
	private void countExonIndex() {
		try {
			scannerAlign = new Scanner(inputAlign);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//Skip three lines to reference
		while (scannerAlign.hasNextLine()){
			String line = scannerAlign.nextLine();
			if(line.length() <12){
				continue;
			}
			String header = line.substring(0,12);
			if(header.contains("RefSeq")){
				refSeq = line;
				break;
			}

		}

		//Find the first divider
		while(refSeq.charAt(looper) != DIVIDER){
			looper ++;
		}
		while(looper < refSeq.length()){
			findIntron();
			findExon();
		}
		scannerAlign.close();
		
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
		System.out.println("intron: "+refSeq.substring(start, end+1));
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
		System.out.println("extron: "+refSeq.substring(start, end+1));
	}
	

}
