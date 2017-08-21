# HSA

HistoSeqAnn             INSTALLATION INSTRUCTIONS


The release version hap_1.0 software produces annotation of HLA  exon/intron boundaries by
multiple sequence alignment.

The role of alignment is align each exonic and intronic regions to HLA 
reference sequences 

DOWNLOAD:
   hap 1.0 is available at:
   https://github.com/wwang-nmdp/HSA/tree/SeqAnn

PRE-INSTALLATION

   hap 1.0  will run on the most common platforms by running -jar file.
   The package consists of two files:
		hap1.0.tar.gz
		hap1.0.readme


INSTALLATION

   1. Uncompress and untar the package:

      cat hap1.0.tar.gz | uncompress | tar xvf -

      This will produce a directory 'hap1.0'.
   2. In your shell set the environment variable:

      HAP	full path to the 'hap' directory

   3. You might need made clustalo file excutable: 
       run command: chmod u+x ../clustalo

   4. In the test folder, you can test hap1.1.jar 
        run command: java -jar /path/to/hap1.0.jar -i /path/to/testfile(xxxx.hml)
   5. The input file can be a fasta file, but need set a -g option
        e.g java -jar /path/to/hap1.1.jar -i /path/to/XXX.fasta -o /path/to/output -g (0-22)
        See the genotype_order.txt

   6. Check the results in output folder
       fasta: the orignal HLA sequences parsed from hml
       clu: the orignal alignment results
       exon: the annotated exons
       protein: the HLA protein sequences translated from CDS

   7. Enjoy ...

PROBLEMS:
hap1.0 still in testing, for academic users only. 
Please contact wwang@nmdp.org in case of problems.
