/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.model;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Will DeJs
 */
public class DocumentData {
    private Map<String, Double> termToFrequency = new HashMap<>();
    
    public void putTermFrequency(String term, double frequency) { 
        termToFrequency.put(term, frequency);
    }   
    
    public double getFrequency(String term) {
        return termToFrequency.get(term);
    }
}
