package org.nmdp.parseHML;

import org.nmdp.HLAGene.HLAGene;
import org.nmdp.HLAGene.SequenceData;
import org.nmdp.scheduler.Scheduler;
import org.nmdp.scheduler.Task;
import org.nmdp.util.FileSystem;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class FastaGenerator {
    public static final String TAG = "FastaGenerator ";
    // The input file
    private File input;
    // The output file
    private File output;
    // The print writer to generate fasta file
    private PrintWriter pr;
    private HashMap<HLAGene, PrintWriter> prMap;
    private String sampleID;
    private GLSConverter glsConverter;
    private Mode mode;
    private boolean expand;
    private String fileName;
    private  Scheduler scheduler;

    public FastaGenerator(Mode mode, Scheduler scheduler){
        this.scheduler = scheduler;
        this.mode = mode;
    }

    public FastaGenerator(Mode mode, boolean expand, Scheduler scheduler){
        this.mode = mode;
        this.expand = expand;
        this.scheduler = scheduler;
        System.out.println("fasta Generator "+ mode.toString());
    }

    /**
     * The method to parse the input file from HML to fasta.
     *
     * @param input The input file.
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     */
    public void run(File input)
            throws ParserConfigurationException, SAXException, IOException {
        // Setup input and output file
        this.input = input;

        fileName = getFileName(this.input.getName());
        output = FileSystem.getFastaFile(fileName);

        glsConverter = new GLSConverter();
        setupPrinter();

        // Initialize doc
        Document doc = getDoc();

        // Get all sample nodes
        NodeList sampleList = doc.getElementsByTagName("sample");
        for (int i = 0; i < sampleList.getLength(); i++) {
            parseSample(sampleList.item(i));
        }

        //close printer
        closeAllPrint();

    }

    private String getFileName(String name) {
        String[] list = name.split("\\.");
        return list[0];
    }

    private void closeAllPrint() {
        pr.close();
        pr = null;
        for(HLAGene gene : prMap.keySet()){
            if(prMap.get(gene) != null){
                PrintWriter pr = prMap.get(gene);
                pr.close();
                scheduler.addTask(new Task(gene, fileName));
                pr = null;
            }
        }
        prMap.clear();
    }

    /**
     * Get doc from input file.
     *
     * @return A document.
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     */
    private Document getDoc() throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(input);
        doc.getDocumentElement().normalize();
        return doc;
    }

    /**
     * Setup the print writer.
     */
    private void setupPrinter() {
        try {
            pr = new PrintWriter(output);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        prMap = new HashMap<>();
    }

    /**
     * Get print writer base on gene type.
     * @param gene
     * @return
     */
    private PrintWriter getPrintWriter(HLAGene gene){
        if(prMap.containsKey(gene)){
            return prMap.get(gene);
        }else{
            PrintWriter pr = null;
            try {
                File file = FileSystem.getFastaFile(gene, fileName);
                System.out.println("output is "+ file.getAbsolutePath());
                pr = new PrintWriter(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            prMap.put(gene, pr);
            return pr;

        }
    }

    /**
     * Parse the sample node.
     *
     * @param node The sample node.
     */
    private void parseSample(Node node) {

        setSampleID(node);
        Element sample = (Element) node;
        // Get all typing nodes
        NodeList typingList = sample.getElementsByTagName("typing");
        for (int j = 0; j < typingList.getLength(); j++) {
            parseTyping(typingList.item(j));
        }

        //Add line divider
        pr.println();
    }

    private void setSampleID(Node smapleNode){
        Element sample = (Element) smapleNode;
        sampleID = sample.getAttribute("id");
    }

    private void printSampleID(List<PrintWriter> prList){
        for(PrintWriter pr : prList){
            pr.print(">id|" + sampleID + "|");
        }

    }

    /**
     * Parse the typing node.
     *
     * @param hla The typing node.
     */
    public void parseTyping(Node hla) {
        Element element = (Element) hla;
        NodeList haploids = element.getElementsByTagName("haploid");
        NodeList  sequenceList = element.getElementsByTagName("consensus-sequence-block");

        List<String> Gls = getGls(element);

        List<PrintWriter> prList = new ArrayList<>();
        prList.add(pr);

        // Print haploid 1
        SequenceData seqData1 = new SequenceData(sampleID);
        Element haplod1 = (Element) haploids.item(0);
        HLAGene gene = getGeneType(haplod1);
        prList.add(getPrintWriter(gene));
        printSampleID(prList);

        printAttribute(prList, haplod1, "locus");
        seqData1.setLocus(haplod1.getAttribute("locus"));

        printAttribute(prList, haplod1,"type");
        seqData1.setType(haplod1.getAttribute("type"));

        switch(mode){
            case NONE:
                printAttributeLast(prList, "gls", Gls.get(0));
                seqData1.setGls(Gls.get(0));
                break;
            case DECODE:
                printAttribute(prList, "gls", Gls.get(0));
                try {
                    String decodeGls = glsConverter.decode(Gls.get(0), expand);
                    print(prList, decodeGls);
                    seqData1.setGls(decodeGls);
                } catch (Exception e) {
                    seqData1.setGls(Gls.get(0));
                    System.out.println(TAG + "Fail to decode");
                }
                break;
            case ENCODE:
                printAttribute(prList, "gls", Gls.get(0));
                try {
                    String encodeGls = glsConverter.encode(Gls.get(0));
                    print(prList, encodeGls);
                    seqData1.setGls(encodeGls);
                } catch (Exception e) {
                    seqData1.setGls(Gls.get(0));
                    System.out.println(TAG + "Fail to encode");
                }
                break;
        }
        String sequence1 = getPs1(sequenceList,gene);
        printSeq(prList, "|PS1", sequence1);
        seqData1.setPhaseSet("PS1");
        seqData1.setSequence(sequence1);


        //Print haploid 2
        printSampleID(prList);
        SequenceData seqData2 = new SequenceData(sampleID);
        Element haplod2 = (Element) haploids.item(1);

        printAttribute(prList, haplod2, "locus");
        seqData2.setLocus(haplod2.getAttribute("locus"));

        printAttribute(prList, haplod2,"type");
        seqData2.setType(haplod2.getAttribute("type"));

        switch(mode){
            case NONE:
                printAttributeLast(prList, "gls", Gls.get(1));
                seqData2.setGls(Gls.get(1));
                break;
            case DECODE:
                printAttribute(prList, "gls", Gls.get(1));
                try {
                    String decodeGls = glsConverter.decode(Gls.get(1),expand);
                    print(prList, decodeGls);
                } catch (Exception e) {
                    seqData2.setGls(Gls.get(1));
                    System.out.println(TAG + "Fail to decode");
                }
                break;
            case ENCODE:
                printAttribute(prList, "gls", Gls.get(1));
                try {
                    String encodeGls = glsConverter.encode(Gls.get(1));
                    print(prList, encodeGls);
                    seqData2.setGls(encodeGls);
                } catch (Exception e) {
                    seqData2.setGls(Gls.get(1));
                    System.out.println(TAG + "Fail to encode");
                }
                break;
        }

        String sequence2 = getPs2(sequenceList, gene);
        printSeq(prList, "|PS2", sequence2);
        seqData2.setPhaseSet("PS2");
        seqData2.setSequence(sequence2);

        //Print a new line as divider
        println(prList);

    }

    /**
     * Return gene type base on name. Exp  HLA-A will return HLA)A;
     * @param e
     * @return
     */
    private HLAGene getGeneType(Element e){
        String gene = e.getAttribute("locus");
        gene = gene.replace('-','_');
        if(input.getName().contains("PAC")){
            switch (HLAGene.valueOf(gene)){
                case HLA_DPB1:
                    return HLAGene.PB_DPB1;
                case HLA_DQB1:
                    return HLAGene.PB_DQB1;
                case HLA_DRB1:
                     return HLAGene.PB_DRB1;
                default:
                    return HLAGene.valueOf(gene);
            }
        }else {
            return HLAGene.valueOf(gene);
        }

    }

    private void print(List<PrintWriter> prList, String s){
        for(PrintWriter pr: prList) {
            pr.println(s);
//            System.out.println(s);
        }
    }

    private void println(List<PrintWriter> prList){
        for(PrintWriter pr: prList) {
            pr.println();
//            System.out.println();
        }
    }

    private void printSeq(List<PrintWriter> prList, String ps, String seq) {
        for(PrintWriter pr: prList) {
            pr.println(ps);
//            System.out.println(ps);
            pr.println(seq);
//            System.out.println(seq);
        }
    }

    private void printAttribute(List<PrintWriter> prList, String atrrName, String value) {
        for(PrintWriter pr: prList){
            pr.print(atrrName + "|");
//            System.out.print("|");
            pr.print(value + "|");
//            System.out.print(value + "|");
        }

    }

    private void printAttributeLast(List<PrintWriter> prList, String atrrName, String value) {
        for(PrintWriter pr: prList){
            pr.print(atrrName + "|");
            pr.print(value);
//            System.out.print(atrrName + "|");
//            System.out.print(value);
        }
    }

    private void printAttribute(List<PrintWriter> prList, Element element, String atrrName) {
        for(PrintWriter pr: prList){
            pr.print(atrrName + "|");
            pr.print(element.getAttribute(atrrName) + "|");
//            System.out.print(atrrName + "|");
//            System.out.print(element.getAttribute(atrrName) + "|");
        }
    }

    /**
     * Get gls strings.
     *
     * @param e The element to abstract gls string.
     * @return The array of gls string.
     */

    public List<String> getGls(Element e) {
        if(e.getElementsByTagName("glstring").item(0) == null){
            ArrayList<String> result = new ArrayList<String>();
            result.add("null");
            result.add("null");
            return result;
        }
        String myString = e.getElementsByTagName("glstring").item(0).getTextContent();
        if(myString.contains("|")){
            return parseGls(myString);
        }else{
            return parseSimpleGls(myString);
        }

    }

    /**
     * Get gls strings.
     *
     * @param source The element to abstract gls string.
     * @return The array of gls string.
     */
    public List<String> parseGls(String source) {
        String [] ambigus = source.split("\\|");
        List<List<String>> glsList = new ArrayList<List<String>>();
        for(int i = 0 ; i < ambigus.length; i++){
            glsList.add(parseSimpleGls(ambigus[i]));
        }
        StringBuilder sbGls1 = new StringBuilder();
        StringBuilder sbGls2 = new StringBuilder();
        for(List<String> glsPair : glsList){
            sbGls1.append(glsPair.get(0));
            sbGls1.append("/");
            sbGls2.append(glsPair.get(1));
            sbGls2.append("/");

        }
        sbGls1.deleteCharAt(sbGls1.length()-1);
        sbGls2.deleteCharAt(sbGls2.length()-1);

        ArrayList<String> result = new ArrayList<String>();
        result.add(sbGls1.toString());
        result.add(sbGls2.toString());
        return result;

    }

    /**
     * Simple gls string contains two gls which is seperated by +
     * @param source The source gls string
     * @return The array of gls strings
     */
    public List<String> parseSimpleGls(String source){
        String[] gls = source.split("\\+");
        List<String> result = new ArrayList<String>();
        for(int i = 0 ; i < gls.length; i++){
            result.add(gls[i].trim());
        }
        return result;

    }

    //Connect multiple phase sets by '-' indicates a gap of sequence
    private String getPs1(NodeList list, HLAGene gene){
        if(list.getLength() == 0){
            return "null";
        }else if(list.getLength() == 1){
            Element seq1 = (Element) list.item(0);
            return seq1.getElementsByTagName("sequence").item(0).getTextContent();
        }
        else if(list.getLength() == 2){
            if(gene == HLAGene.PB_DRB1 |gene == HLAGene.PB_DPB1|gene == HLAGene.PB_DQB1 ){
                Element seq1 = (Element) list.item(0);
                Element seq2 = (Element) list.item(1);
                return seq1.getElementsByTagName("sequence").item(0).getTextContent() + seq2.getElementsByTagName("sequence").item(0).getTextContent();
            }else {
                Element seq1 = (Element) list.item(0);
                return seq1.getElementsByTagName("sequence").item(0).getTextContent();
            }

        }else{
            StringBuilder sb = new StringBuilder();
            for(int i = 0; i< list.getLength(); i++){
                Element temp = (Element) list.item(i);
                if(temp.getAttribute("phase-set").equals("1")){
                    if(sb.length() > 0){
                        sb.append("-");
                        sb.append(temp.getElementsByTagName("sequence").item(0).getTextContent());
                    }else{
                        sb.append(temp.getElementsByTagName("sequence").item(0).getTextContent());
                    }

                }
            }
            return sb.toString();
        }
    }

    private String getPs2(NodeList list, HLAGene gene){
        if(list.getLength() == 0){
            return "null";
        }else if(list.getLength() == 1){
            Element seq1 = (Element) list.item(0);
            return seq1.getElementsByTagName("sequence").item(0).getTextContent();
        }
        else if(list.getLength() == 2){
            if(gene == HLAGene.PB_DRB1 |gene == HLAGene.PB_DPB1|gene == HLAGene.PB_DQB1 ){
                Element seq1 = (Element) list.item(0);
                Element seq2 = (Element) list.item(1);
                return seq1.getElementsByTagName("sequence").item(0).getTextContent() + seq2.getElementsByTagName("sequence").item(0).getTextContent();
            }
            else{
                Element seq1 = (Element) list.item(1);
                return seq1.getElementsByTagName("sequence").item(0).getTextContent();
            }

        }else{
            StringBuilder sb = new StringBuilder();
            for(int i = 0; i< list.getLength(); i++){
                Element temp = (Element) list.item(i);
                if(temp.getAttribute("phase-set").equals("2")){
                    if(sb.length() > 0){
                        sb.append("-");
                        sb.append(temp.getElementsByTagName("sequence").item(0).getTextContent());
                    }else{
                        sb.append(temp.getElementsByTagName("sequence").item(0).getTextContent());
                    }

                }
            }
            return sb.toString();
        }
    }

}

