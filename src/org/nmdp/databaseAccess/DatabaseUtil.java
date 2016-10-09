package org.nmdp.databaseAccess;
import java.sql.*;

import org.nmdp.HLAGene.ExonIntronData;
import org.nmdp.HLAGene.SectionName;
import org.nmdp.HLAGene.SequenceData;
import org.nmdp.gfe.GFE;


public class DatabaseUtil
{
	static final String DATA_BASE_NAME = "HLAsequence";
	static final String HLA_TABLE_NAME = "HLAseqTable";
	static final String EXON_TABLE_NAME = "ExonTable";
	static final String GFE_TABLE_NAME = "GfeTable";
	static final String ID = "ID";
	static final String FILE_SOURCE = "FILE_SOURCE";
	static final String SAMPLE_ID = "SAMPLE_ID";
	static final String LOCUS = "LOCUS";
	static final String TYPE = "TYPE";
	static final String GLS = "GLS";
	static final String PHASE_SET = "PHASE_SET";
	static final String SEQUENCE = "SEQ";
	static final String FIVE_NS = "UTR5";
	static final String EXON1 = "EXON1";
	static final String EXON2 = "EXON2";
	static final String EXON3 = "EXON3";
	static final String EXON4 = "EXON4";
	static final String EXON5 = "EXON5";
	static final String EXON6 = "EXON6";
	static final String EXON7 = "EXON7";
	static final String EXON8 = "EXON8";
	static final String EXON1_PL = "EXON1_PL";
	static final String EXON2_PL = "EXON2_PL";
	static final String EXON3_PL = "EXON3_PL";
	static final String EXON4_PL = "EXON4_PL";
	static final String EXON5_PL = "EXON5_PL";
	static final String EXON6_PL = "EXON6_PL";
	static final String EXON7_PL = "EXON7_PL";
	static final String EXON8_PL = "EXON8_PL";
	static final String INTRON1 = "INTRON1";
	static final String INTRON2 = "INTRON2";
	static final String INTRON3 = "INTRON3";
	static final String INTRON4 = "INTRON4";
	static final String INTRON5 = "INTRON5";
	static final String INTRON6 = "INTRON6";
	static final String INTRON7 = "INTRON7";
	static final String THREE_NS = "UTR3";
	static final String PROTIEN = "PROTIEN";



	static Connection connection;
  
  public static void connectDatabase(){
	  connection = null;
	    try {
	      Class.forName("org.sqlite.JDBC");
	      connection= DriverManager.getConnection("jdbc:sqlite:"+DATA_BASE_NAME+".db");
	    } catch ( Exception e ) {
	      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	      System.exit(0);
	    }
	    System.out.println("Opened database successfully");
  }
  
  public static void createSeqTable(){
	  try {
			Statement stmt = connection.createStatement();
			String sql = "CREATE TABLE IF NOT EXISTS " + HLA_TABLE_NAME + "("
					+ ID +" INTEGER PRIMARY KEY AUTOINCREMENT,"
					+ FILE_SOURCE + " CHAR(100) NOT NULL,"
					+ SAMPLE_ID+" CHAR(50) NOT NULL,"
					+LOCUS+" CHAR(20) NOT NULL,"
					+TYPE+" CHAR(20) NOT NULL,"
					+GLS+" TEXT NOT NULL,"
					+PHASE_SET+" CHAR(10) NOT NULL,"
					+SEQUENCE+" TEXT NOT NULL,"
					+"UNIQUE ("+SAMPLE_ID+ ","+GLS+","+ PHASE_SET+" )"
					+");";
			stmt.executeUpdate(sql);
			stmt.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 System.out.println("Create seq table successfully");
  }

	public static void createGfeTable(){
		try {
			Statement stmt = connection.createStatement();
			String sql = "CREATE TABLE IF NOT EXISTS " + GFE_TABLE_NAME + "("
					+ ID +" INTEGER PRIMARY KEY AUTOINCREMENT,"
					+ SAMPLE_ID+" CHAR(50) NOT NULL,"
					+LOCUS+" CHAR(20) NOT NULL,"
					+GLS+" TEXT NOT NULL,"
					+PHASE_SET+" CHAR(10) NOT NULL,"
					+SEQUENCE+" TEXT NOT NULL,"
					+"UNIQUE ("+SAMPLE_ID+ ","+GLS+","+ PHASE_SET+" )"
					+");";
			stmt.executeUpdate(sql);
			stmt.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Create gfe table successfully");
	}

	public static void creatExonTable(){
		try {
			Statement stmt = connection.createStatement();
			String sql = "CREATE TABLE IF NOT EXISTS " + EXON_TABLE_NAME + "("
					+ ID +" INTEGER PRIMARY KEY AUTOINCREMENT,"
					+ FILE_SOURCE + " CHAR(100) NOT NULL,"
					+ SAMPLE_ID+" CHAR(50) NOT NULL,"
					+GLS+" TEXT NOT NULL,"
					+PHASE_SET+" CHAR(10) NOT NULL,"
					+FIVE_NS+" TEXT NOT NULL,"
					+EXON1+ " TEXT NOT NULL,"
					+INTRON1+ " TEXT NOT NULL,"
					+EXON2+ " TEXT NOT NULL,"
					+INTRON2+ " TEXT NOT NULL,"
					+EXON3+ " TEXT NOT NULL,"
					+INTRON3+ " TEXT NOT NULL,"
					+EXON4+ " TEXT NOT NULL,"
					+INTRON4+ " TEXT NOT NULL,"
					+EXON5+ " TEXT NOT NULL,"
					+INTRON5+ " TEXT NOT NULL,"
					+EXON6+ " TEXT NOT NULL,"
					+INTRON6+ " TEXT NOT NULL,"
					+EXON7+ " TEXT NOT NULL,"
					+INTRON7+ " TEXT NOT NULL,"
					+EXON8+ " TEXT NOT NULL,"
					+THREE_NS+ " TEXT NOT NULL,"
					+EXON1_PL+ " TEXT NOT NULL,"
					+EXON2_PL+ " TEXT NOT NULL,"
					+EXON3_PL+ " TEXT NOT NULL,"
					+EXON4_PL+ " TEXT NOT NULL,"
					+EXON5_PL+ " TEXT NOT NULL,"
					+EXON6_PL+ " TEXT NOT NULL,"
					+EXON7_PL+ " TEXT NOT NULL,"
					+EXON8_PL+ " TEXT NOT NULL,"
					+PROTIEN+" TEXT NOT NULL,"
					+" UNIQUE ("+SAMPLE_ID+ ","+GLS+","+ PHASE_SET+" )"
					+");";
			System.out.println(sql);
			stmt.executeUpdate(sql);
			stmt.close();
			System.out.println("Create exon table successfully");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	  
  
  
  public static void insertSeqData(SequenceData data, String fileName)  {
	  Statement stmt = null;
	  try {
		  stmt = connection.createStatement();
	  } catch (SQLException e) {
		  System.out.println("connection is broken. fail to insert " + data.toString());
	  }
	  StringBuilder sb = new StringBuilder();
		sb.append("INSERT INTO "+ HLA_TABLE_NAME);
		sb.append("(");
	  	sb.append(FILE_SOURCE + ",");
		sb.append(SAMPLE_ID + ",");
		sb.append(LOCUS + ",");
		sb.append(TYPE + ",");
		sb.append(GLS + ",");
		sb.append(PHASE_SET + ",");
		sb.append(SEQUENCE + ")");
		sb.append("VALUES (");
	  	sb.append(wrapString(fileName)+ ",");
		sb.append(data.getSampleId() + ",");
		sb.append(data.getLocus() + ",");
		sb.append(data.getType() + ",");
		sb.append(data.getGls() + ",");
		sb.append(data.getPhaseSet() + ",");
		sb.append(data.getSequence());
		sb.append(");");
	  try {
		  System.out.println(sb.toString());
		  stmt.executeUpdate(sb.toString());
		  stmt.close();
	  } catch (SQLException e) {
		  System.out.println("fail to insert duplicate data" + data.toString());
	  }

  }

	public static void insertGFEData(GFE.GeneInfo data){
		Statement stmt = null;
		try {
			stmt = connection.createStatement();
		} catch (SQLException e) {
			System.out.println("connection is broken. fail to insert " + data.toString());
		}
		StringBuilder sb = new StringBuilder();
		sb.append("INSERT INTO "+ GFE_TABLE_NAME);
		sb.append("(");
		sb.append(SAMPLE_ID + ",");
		sb.append(LOCUS + ",");
		sb.append(GLS + ",");
		sb.append(PHASE_SET + ",");
		sb.append(SEQUENCE + ")");
		sb.append("VALUES (");
		sb.append(data.id+ ",");
		sb.append(data.gene.toString() + ",");
		sb.append(data.gls + ",");
		sb.append(data.phase + ",");
		sb.append(data.gfe);
		sb.append(");");
		try {
			System.out.println(sb.toString());
			stmt.executeUpdate(sb.toString());
			stmt.close();
		} catch (SQLException e) {
			System.out.println("fail to insert duplicate  GFE data" + data.toString());
		}
	}
  
  public static void insertExonData(ExonIntronData data, String fileName) {
	  Statement stmt = null;
	  try {
		  stmt = connection.createStatement();
	  } catch (SQLException e) {
		  System.out.println("connection is broken. fail to insert " + data.toString());
	  }
		StringBuilder sb = new StringBuilder();
		sb.append("INSERT INTO "+ EXON_TABLE_NAME);
		sb.append("(");
	    sb.append(FILE_SOURCE + ",");
		sb.append(SAMPLE_ID + ",");
		sb.append(GLS + ",");
		sb.append(PHASE_SET + ",");
		sb.append(FIVE_NS + ",");
		sb.append(EXON1 + ",");
		sb.append(INTRON1 + ",");
		sb.append(EXON2 + ",");
		sb.append(INTRON2 + ",");
		sb.append(EXON3 + ",");
		sb.append(INTRON3 + ",");
		sb.append(EXON4 + ",");
		sb.append(INTRON4 + ",");
		sb.append(EXON5 + ",");
		sb.append(INTRON5 + ",");
		sb.append(EXON6 + ",");
		sb.append(INTRON6 + ",");
		sb.append(EXON7 + ",");
		sb.append(INTRON7 + ",");
		sb.append(EXON8 + ",");
		sb.append(THREE_NS + ",");
		sb.append(EXON1_PL + ",");
		sb.append(EXON2_PL + ",");
		sb.append(EXON3_PL + ",");
		sb.append(EXON4_PL + ",");
		sb.append(EXON5_PL + ",");
		sb.append(EXON6_PL + ",");
		sb.append(EXON7_PL + ",");
	    sb.append(EXON8_PL + ",");
		sb.append(PROTIEN + ")");
		
		sb.append("VALUES (");
	    sb.append(wrapString(fileName)+ ",");
		sb.append(wrapString(data.getSampleID()) + ",");
		sb.append(wrapString(data.getGls()) + ",");
		sb.append(wrapString(data.getPhase()) + ",");
		sb.append(wrapString(data.getIntron(SectionName.US)) + ",");
		sb.append(wrapString(data.getExon(SectionName.e1)) + ",");
		sb.append(wrapString(data.getIntron(SectionName.i1)) + ",");
		sb.append(wrapString(data.getExon(SectionName.e2)) + ",");
		sb.append( wrapString(data.getIntron(SectionName.i2)) + ",");
		sb.append(wrapString(data.getExon(SectionName.e3)) + ",");
		sb.append(wrapString(data.getIntron(SectionName.i3)) + ",");
		sb.append(wrapString(data.getExon(SectionName.e4)) + ",");
		sb.append(wrapString(data.getIntron(SectionName.i4)) + ",");
		sb.append(wrapString(data.getExon(SectionName.e5)) + ",");
		sb.append(wrapString(data.getIntron(SectionName.i5)) + ",");
		sb.append(wrapString(data.getExon(SectionName.e6)) + ",");
		sb.append(wrapString(data.getIntron(SectionName.i6)) + ",");
		sb.append(wrapString(data.getExon(SectionName.e7)) + ",");
		sb.append(wrapString(data.getIntron(SectionName.i7)) + ",");
		sb.append(wrapString(data.getExon(SectionName.e8)) + ",");
		sb.append(wrapString(data.getIntron(SectionName.DS))+ ",");
		sb.append(wrapString(data.getExon_pl(SectionName.e1))+ ",");
		sb.append(wrapString(data.getExon_pl(SectionName.e2))+ ",");
		sb.append(wrapString(data.getExon_pl(SectionName.e3))+ ",");
		sb.append(wrapString(data.getExon_pl(SectionName.e4))+ ",");
		sb.append(wrapString(data.getExon_pl(SectionName.e5))+ ",");
		sb.append(wrapString(data.getExon_pl(SectionName.e6))+ ",");
		sb.append(wrapString(data.getExon_pl(SectionName.e7))+ ",");
	    sb.append(wrapString(data.getExon_pl(SectionName.e8))+ ",");
		sb.append(wrapString(data.getProtein()));
		sb.append(");");
	  try {
		  System.out.println(sb.toString());
		  stmt.executeUpdate(sb.toString());
		  stmt.close();
	  } catch (SQLException e) {
		  System.out.println("Fail to insert duplicate data." + data.toString());
	  }

	  
}
  
  public static void cleanUp(){
	  try {
		connection.close();
	} catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	System.out.println("Connection closed.");  
  }
  
  private static  String wrapString(String str){
		return "'" +str+ "'";
	}
}
