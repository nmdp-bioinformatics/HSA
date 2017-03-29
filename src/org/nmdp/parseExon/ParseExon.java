package org.nmdp.parseExon;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.nmdp.HLAGene.*;
import org.nmdp.config.Configuration;
import org.nmdp.scheduler.Task;
import org.nmdp.translate.Translator;
import org.nmdp.util.FileSystem;

public class    ParseExon{
	public static final String TAG = "ParseExon ";
	private Scanner scannerAlign;
	private Scanner scannerFreq;
	//cut the first 50 positions because of the low coverage.
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
	// The print wirter to generate cvs file
	private PrintWriter cvsWriter;
	private PrintWriter protienWriter;
	private PrintWriter validationWriter;
	private PrintWriter reformatWriter;
	private static final char DIVIDER = '-';
	private String fileName;
	private HLAGene geneType;
	private Translator translator;
	private int sampleNum = 0;
	FileWriter fw;
	BufferedWriter bw;
	private boolean notSplitHeader = false;


	public ParseExon(){
		translator = new Translator();
	}

	public void process(Task task){
		File align = FileSystem.getCluFile(task.getGene(), task.getFileName());
		output = FileSystem.getExonFile(task.getGene(), task.getFileName());
		fileName = task.getFileName();
		geneType = task.getGene();
		try {
			List<SectionName> sectionNames = Configuration.getSection(task.getGene());
			System.out.println(sectionNames.get(0).toString());
			System.out.println(sectionNames.get(1).toString());
			run(align, sectionNames.get(0), sectionNames.get(1));
		} catch (Exception e) {
			System.out.println(TAG + "Can't find section setting for "+ task.getGene().toString());
			e.printStackTrace();
			return;
		}

	}

	public void processFasta(Task task){
		notSplitHeader = true;
		process(task);
	}

	/**
	 *
	 * @param input the clu file
	 * @param output
     * @param gene
     */
	public void process(File input, File output, HLAGene gene){
		this.fileName = FileSystem.getFileName(input);
		this.output = output;
		geneType = gene;
		try {
			List<SectionName> sectionNames = Configuration.getSection(gene);
			run(input, sectionNames.get(0), sectionNames.get(1));
		} catch (Exception e) {
			System.out.println(TAG + "Can't find section setting for "+ gene.toString());
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
		HLAGeneData.setType(geneType,start, end);
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
		HLAGeneData.setType(geneType,start, end);
		countExonIndex();
		extratExons();
		closePrinter();
		//extraFreq();
		//PolymorphStaticsProessor.processPolyMoph(freqList, seqList, indexExon, indexIntron, pw);
	}

	private void closePrinter() {
		pw.close();
		protienWriter.close();
		reformatWriter.close();
		validationWriter.close();
		cvsWriter.close();

	}

	private void extraFreq() {
		try {
			scannerFreq = new Scanner(inputFreq);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		//skip clustalo title: 8 lines
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
			protienWriter = new PrintWriter(FileSystem.getProteinFile(geneType, fileName));
			validationWriter = new PrintWriter(FileSystem.getValidationFile(geneType, fileName));
			FileWriter fw2 = new FileWriter(FileSystem.getReformatFile(geneType, fileName), true);
			BufferedWriter bw2 = new BufferedWriter(fw2);
			reformatWriter = new PrintWriter(bw2);
			fw = new FileWriter(FileSystem.getCvsFile(fileName), true);
			bw = new BufferedWriter(fw);
			cvsWriter = new PrintWriter(bw);
		} catch (IOException e) {
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
			e.printStackTrace();
		}
		//Skip four lines to first row of geneData
		scannerAlign.nextLine();
		scannerAlign.nextLine();
		scannerAlign.nextLine();
		while(scannerAlign.hasNext()){
			try {
				String data = scannerAlign.nextLine();
				if(data.length() > 16 && data.substring(0,16).contains("RefSeq")){
					break;
				}
				processSample(data);
			} catch (Exception e) {
				e.printStackTrace();
				break;
			}
		}
		scannerAlign.close();
		pw.close();
		protienWriter.close();
		reformatWriter.close();
		cvsWriter.close();
	}
	private void processSample(String data) throws Exception {
		if(data.charAt(0) == ' '){
			//If the geneData is the last line, do not process
			return;
		}

		ExonIntronData ei = new HLAGeneData();
		if(notSplitHeader){
			String[] split = data.split("\\s+");
			ei.setSampleId(split[0]);
		}else{
			//split the geneData by white space or |
			String[] split = data.split(" |\\|");
			ei.setSampleId(split[1]);
			ei.setGeneType(split[3]);
			ei.setType(split[5]);
			ei.setGls(split[7]);
			ei.setPhase(split[8]);
		}

		ei.setExonIntron(data, indexExon, indexIntron);
		ei.setFullLength(data);
		ei.setProtein(translator.translate(ei.getCDNA(), geneType.getFrame()));
		ei.earlyEnd = translator.earlyEnd;
		seqList.add(ei);

		cvsWriter.println(ei.toCVS());
		pw.println(ei.toFasta());
		pw.println(ei.getCDNA());

		//write protein
		protienWriter.println(ei.toFasta());
		protienWriter.println(ei.getProtein());

		//write valida data
		if(ei.earlyEnd){
			validationWriter.println(ei.getProtein());
		}

		//write reformat file

		List<SectionName> list = HLAGeneData.sortSectionList();
		for(int i =0; i < list.size(); i++){
			reformatWriter.print(ei.getSampleID());
			reformatWriter.print(",");
			reformatWriter.print(ei.getGls());
			reformatWriter.print(",");
			reformatWriter.print(ei.getPhase());
			reformatWriter.print(",");
			if(list.get(i).isExon()){
				reformatWriter.print("exon");
			}else {
				if(list.get(i) == SectionName.US){
					reformatWriter.print("Five_prime-UTR");
				}else if(list.get(i) == SectionName.DS){
					reformatWriter.print("Three_Prime-UTR");
				}else if((i == list.size()-1) && (list.size() == 2 + geneType.getIntronNumber()+geneType.getExonNumber())){
					reformatWriter.print("Three_Prime-UTR");
				}else {
					reformatWriter.print("intron");
				}
			}
			reformatWriter.print(",");

			reformatWriter.print(list.get(i).getNumber());
			reformatWriter.print(",");
			reformatWriter.println(ei.getExon(list.get(i)));
		}

	}
		
	/**
	 * find the index of all exon and intron
	 */
	private void countExonIndex() {
		indexExon.clear();
		indexIntron.clear();
		//ABO index is predefined
		if(geneType == HLAGene.ABO){
			ArrayList<Integer> exonLengthList = new ArrayList<>();
			exonLengthList.add(28);
			exonLengthList.add(70);
			exonLengthList.add(57);
			exonLengthList.add(48);
			exonLengthList.add(36);
			exonLengthList.add(134);
			exonLengthList.add(691);
			countIndexBasedOnLength(exonLengthList);

		}else if(geneType == HLAGene.DPB1){
			ArrayList<Integer> exonLengthList = new ArrayList<>();
			exonLengthList.add(264);
			exonLengthList.add(281);
			countIndexBasedOnLength(exonLengthList);
		}else if(geneType == HLAGene.DQB1 || geneType == HLAGene.DRB1){
			ArrayList<Integer> exonLengthList = new ArrayList<>();
			exonLengthList.add(270);
			exonLengthList.add(281);
			countIndexBasedOnLength(exonLengthList);
		}else {
			countIndexBasedOnCharacter();
		}
	}

	private void countIndexBasedOnLength(List<Integer> lengthList){
		looper = 18;
		try {
			scannerAlign = new Scanner(inputAlign);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		//Skip three lines to reference
		while (scannerAlign.hasNextLine()){
			String line = scannerAlign.nextLine();
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
		//Find the start char of first exon
		for(int length : lengthList){
			int start = looper;
			int end;
			int count = 0;
			while(count != length && looper < refSeq.length()){
				if(refSeq.charAt(looper) != DIVIDER){
					count++;
				}
				looper++;
			}
			end = looper-1;
			indexExon.add(start);
			indexExon.add(end);
		}
		//Reset the looper to start position after processing one gene.
		looper = 18;
		scannerAlign.close();

	}
	private void countIndexBasedOnCharacter() {
		try {
			scannerAlign = new Scanner(inputAlign);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		//Skip three lines to reference
		while (scannerAlign.hasNextLine()){
			String line = scannerAlign.nextLine();
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
	

}
