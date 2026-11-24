/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany;

import com.mycompany.model.DocumentData;
import com.mycompany.search.TfIdf;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * @author Will DeJs
 */
public class SequentialSearch {

    public static final String BOOKS_DIRECTORY = "./resource/books";
    public static final String SEARCH_QUERY_1 = "The best detective that catches many criminals using his deductive methods";
    public static final String SEARCH_QUERY_2 = "The girl that falls through a rabbit hole into a fantasy worderland";
    public static final String SEARCH_QUERY_3 = "A war between Russia and France in the cold winter";

    public static void Main(String[] args) throws IOException {
        File documentsDirectory = new File(BOOKS_DIRECTORY);

        List<String> documents = Arrays.asList(documentsDirectory.list()).stream().map(documentName -> BOOKS_DIRECTORY + "/" + documentName).collect(Collectors.toList());
        List<String> terms = TfIdf.getWordsFromLine(SEARCH_QUERY_1);
        findMostRelevantDocuments(documents, terms);
        
        List<String> terms2 = TfIdf.getWordsFromLine(SEARCH_QUERY_2);
        findMostRelevantDocuments(documents, terms2);
        
        List<String> terms3 = TfIdf.getWordsFromLine(SEARCH_QUERY_3);
        findMostRelevantDocuments(documents, terms3);
    }

    private static void findMostRelevantDocuments(List<String> documents, List<String> terms) throws FileNotFoundException, IOException {
        Map<String, DocumentData> documentResults = new HashMap<>();
        for (String document : documents) {
            try (BufferedReader bufferedREader = new BufferedReader(new FileReader(document))) {
                List<String> lines = bufferedREader.readAllLines();
                List<String> words = TfIdf.getWordsFromLines(lines);
                DocumentData documentData = TfIdf.createDocumentData(words, terms);
                documentResults.put(document, documentData);
            }
        }
        Map<Double, List<String>> documentByScore = TfIdf.getDcoumentSortedByScore(terms, documentResults);
        printResults(documentByScore);
    }

    private static void printResults(Map<Double, List<String>> documentsByScore) {
        for (Map.Entry<Double, List<String>> docScorePair : documentsByScore.entrySet()) {
            double score = docScorePair.getKey();
            for (String document : docScorePair.getValue()) {
                System.out.println("Book: " + document + " - score: " + score);
            }
        }
    }
}
