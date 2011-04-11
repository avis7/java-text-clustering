package test.by.bsu.rfe.clustering.app.cli;

import java.io.File;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import test.by.bsu.rfe.clustering.app.util.CSVDataSetExporter;

import by.bsu.rfe.clustering.algorithm.ClusteringAlgorithm;
import by.bsu.rfe.clustering.algorithm.cluster.Cluster;
import by.bsu.rfe.clustering.math.DistanseMeasure;
import by.bsu.rfe.clustering.math.EuclideanDistanceMeasure;
import by.bsu.rfe.clustering.nlp.WordList;
import by.bsu.rfe.clustering.text.algorithm.TextKMeansAlgorithm;
import by.bsu.rfe.clustering.text.document.DocumentCollection;
import by.bsu.rfe.clustering.text.document.DocumentDataElement;
import by.bsu.rfe.clustering.text.document.DocumentDataSet;
import by.bsu.rfe.clustering.text.ir.DocumentCollectionReader;
import by.bsu.rfe.clustering.text.ir.FileSystemDocumentCollectionReader;
import by.bsu.rfe.clustering.text.vsm.DocumentVSMGenerator;
import by.bsu.rfe.clustering.text.vsm.NormalizedTFIDF;
import by.bsu.rfe.clustering.text.vsm.TFIDF;

public class CLI {

    private static transient Log _log = LogFactory.getLog(CLI.class);

    public static void main(String[] args) throws Exception {
        File stopWords = new File("dictionary\\stopwords.txt");
        WordList stopWordList = WordList.load(stopWords);

        DocumentCollectionReader reader = new FileSystemDocumentCollectionReader(new File("samples"), stopWordList);

        DocumentCollection docCollection = reader.readDocuments();

        /*
         * for (String term : new TreeSet<String>(docCollection.getAllTerms()))
         * { System.out.println(term); }
         */

        System.out.println("\r\n\r\n\r\n");

        DocumentVSMGenerator vsmGen = new NormalizedTFIDF();
        DocumentDataSet dataSet = vsmGen.createVSM(docCollection);

        CSVDataSetExporter.export(dataSet, new File("tmp\\dataset.csv"));

        DistanseMeasure distanse = new EuclideanDistanceMeasure();

        final int numberOfClusters = 100;

        ClusteringAlgorithm<DocumentDataElement, Cluster<DocumentDataElement>, DocumentDataSet> clustering = new TextKMeansAlgorithm(
                distanse, numberOfClusters);

        List<Cluster<DocumentDataElement>> clusters = clustering.cluster(dataSet);

        for (Cluster<DocumentDataElement> cluster : clusters) {
            System.out.printf("%n(%d) %s:%n%n",cluster.getDataElements().size(), cluster.getLabel());

            for (DocumentDataElement elem : cluster.getDataElements()) {
                System.out.printf("\t%s%n", elem.getDocument().getTitle());
            }
        }

    }

}