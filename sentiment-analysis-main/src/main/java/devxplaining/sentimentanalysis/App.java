
package devxplaining.sentimentanalysis;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations.SentimentAnnotatedTree;
import edu.stanford.nlp.util.CoreMap;

import javax.swing.*;

public class App implements ActionListener {

    JFrame frame;
    JButton button;
    JPanel panel;
    JLabel label1;
    JLabel label2;
    JTextField inputTaker;
    int clicks=0;
    App(){
        frame= new JFrame();
        button = new JButton("Analyse");
        button.addActionListener(this);

        panel= new JPanel();
        label1= new JLabel("Enter the text you want to analyze:");
        label2= new JLabel("");
        inputTaker=new JTextField();

        panel.setBorder(BorderFactory.createEmptyBorder(150,150,150,150));
        //panel.setSize(300,300);
        panel.setLayout(new GridLayout(0,1));
        panel.add(label1);
        panel.add(inputTaker);
        panel.add(button);
        panel.add(label2);
        button.addActionListener(this);

        frame.add(panel,BorderLayout.CENTER);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("Sentiment Analysis");
        frame.pack();
        frame.setVisible(true);

    }
    @Override
    public void actionPerformed(ActionEvent e) {
        String content = inputTaker.getText();
        var props = new Properties();

        // tokenizer, sentence splitting, consistuency parsing, sentiment analysis
        props.setProperty("annotators", "tokenize, ssplit, parse, sentiment");
        var pipeline = new StanfordCoreNLP(props);
        var annotation = pipeline.process(content);
        annotation.get(CoreAnnotations.SentencesAnnotation.class).forEach(sentence -> {
            var tree = sentence.get(SentimentAnnotatedTree.class);
            var sentimentInt = RNNCoreAnnotations.getPredictedClass(tree);
            var sentimentName = sentence.get(SentimentCoreAnnotations.SentimentClass.class);
            label2.setText(sentimentName);
            System.out.println(sentimentName + "\t" + sentimentInt + "\t" + sentence);
        });

    }

    public static void main(String[] args) throws Exception {
        new App();
        var text = loadResourceFromClasspath();
        //tokenize(text);
        analyze(text);
        //System.out.println(analyzeAndReturn(text));
    }


/**
     * Let's tokenize the text using simple API
     * Simple API also stays fast as long as you call fast operations
     *
     */

    public static void tokenize(String content) {
        var document = new edu.stanford.nlp.simple.Document(content);
        document.sentences().forEach(s -> System.out.println(s.words()));
    }



















    public static void analyze(String content) {
        String temp="";
        var props = new Properties();
        boolean bool=false;
        // tokenizer, sentence splitting, consistuency parsing, sentiment analysis
        props.setProperty("annotators", "tokenize, ssplit, parse, sentiment");
        var pipeline = new StanfordCoreNLP(props);
        for(int i=0;i<content.length();i++){
            if(content.charAt(i)==','){
                bool=true;
            }
            else{
                temp=temp+content.charAt(i);
            }
            if(bool){
                var annotation = pipeline.process(temp);
                annotation.get(CoreAnnotations.SentencesAnnotation.class).forEach(sentence -> {
                    var tree = sentence.get(SentimentAnnotatedTree.class);
                    var sentimentInt = RNNCoreAnnotations.getPredictedClass(tree);
                    var sentimentName = sentence.get(SentimentCoreAnnotations.SentimentClass.class);
                    System.out.println(sentimentName + " \t" + sentimentInt + "\t" + sentence);
                });temp="";bool=false;}}
    }

//    public static void analyze(String content) {
//        var props = new Properties();
//        // tokenizer, sentence splitting, consistuency parsing, sentiment analysis
//        props.setProperty("annotators", "tokenize, ssplit, parse, sentiment");
//        var pipeline = new StanfordCoreNLP(props);
//
//        String temp="";
//        for(int i=0;i<content.length();i++){
//            boolean bool=false;
//            if(content.charAt(i)==','){
//                bool=true;
//            }
//            else
//            if(bool){
//        var annotation = pipeline.process(content);
//        annotation.get(CoreAnnotations.SentencesAnnotation.class).forEach(sentence -> {
//            var tree = sentence.get(SentimentAnnotatedTree.class);
//            var sentimentInt = RNNCoreAnnotations.getPredictedClass(tree);
//            var sentimentName = sentence.get(SentimentCoreAnnotations.SentimentClass.class);
//            System.out.println(sentimentName + "\t" + sentimentInt + "\t" + sentence + "\n");
//
//        });bool=false;}
//    }

    record SentimentRecord(String name, int value, String sentence) {
    }

    public static SentimentRecord convertToSentimentRecord(CoreMap sentence) {
        var tree = sentence.get(SentimentAnnotatedTree.class);
        return new SentimentRecord(
                sentence.get(SentimentCoreAnnotations.SentimentClass.class),
                RNNCoreAnnotations.getPredictedClass(tree),
                sentence.toString());
    }

    public static boolean negativeComments(SentimentRecord sentimentRecord) {
        return sentimentRecord.value < 2;
    }

    public static boolean positiveComments(SentimentRecord sentimentRecord) {
        return sentimentRecord.value > 2;
    }

    public static List<SentimentRecord> analyzeAndReturn(String content) {
        var props = new Properties();
        // tokenizer, sentence splitting, consistuency parsing, sentiment analysis
        props.setProperty("annotators", "tokenize, ssplit, parse, sentiment");
        var pipeline = new StanfordCoreNLP(props);
        var annotation = pipeline.process(content);
        return annotation.get(CoreAnnotations.SentencesAnnotation.class).stream()
                .map(App::convertToSentimentRecord)
                .filter(App::negativeComments)
                //.filter(App::positiveComments)
                .collect(Collectors.toList());
    }

/**
     * A tiny util that loads a resource from classpath using getResourceAsStream -
     * that works also in .jar packaged format
     *
     * @return
     * @throws IOException
     */

private static String loadResourceFromClasspath() throws IOException {
    var inputStream = App.class.getClassLoader().getResourceAsStream("comments.txt");
    return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
}

    private static String loadResourceFromClasspath2() throws IOException {
        var inputStream = App.class.getClassLoader().getResourceAsStream("comments.json");
        return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
    }

}

    /*public static void analyze(String content) {
        var props = new Properties();
        // tokenizer, sentence splitting, consistuency parsing, sentiment analysis
        props.setProperty("annotators", "tokenize, ssplit, parse, sentiment");
        var pipeline = new StanfordCoreNLP(props);
        var annotation = pipeline.process(content);
        annotation.get(CoreAnnotations.SentencesAnnotation.class).forEach(sentence -> {
            var tree = sentence.get(SentimentAnnotatedTree.class);
            var sentimentInt = RNNCoreAnnotations.getPredictedClass(tree);
            var sentimentName = sentence.get(SentimentCoreAnnotations.SentimentClass.class);
            System.out.println(sentimentName + "\t" + sentimentInt + "\t" + sentence);
        });
    }*/


/*
-----------------------
package devxplaining.sentimentanalysis;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations.SentimentAnnotatedTree;
import edu.stanford.nlp.util.CoreMap;

public class App {


    public static void main(String[] args) throws Exception {
        var text = loadResourceFromClasspath();
        //tokenize(text);
        analyze(text);
        //System.out.println(analyzeAndReturn(text));
    }

    */
/**
     * Let's tokenize the text using simple API
     * Simple API also stays fast as long as you call fast operations
     *
     *//*

    public static void tokenize(String content) {
        var document = new edu.stanford.nlp.simple.Document(content);
        document.sentences().forEach(s -> System.out.println(s.words()));
    }





    public static void analyze(String content) {
        String temp="";
        var props = new Properties();
        boolean bool=false;
        // tokenizer, sentence splitting, consistuency parsing, sentiment analysis
        props.setProperty("annotators", "tokenize, ssplit, parse, sentiment");
        var pipeline = new StanfordCoreNLP(props);
        for(int i=0;i<content.length();i++){
            if(content.charAt(i)==','){
                bool=true;
            }
            else{
                temp=temp+content.charAt(i);
            }
            if(bool){
        var annotation = pipeline.process(temp);
        annotation.get(CoreAnnotations.SentencesAnnotation.class).forEach(sentence -> {
            var tree = sentence.get(SentimentAnnotatedTree.class);
            var sentimentInt = RNNCoreAnnotations.getPredictedClass(tree);
            var sentimentName = sentence.get(SentimentCoreAnnotations.SentimentClass.class);
            System.out.println(sentimentName + " \t" + sentimentInt + "\t" + sentence);
        });temp="";bool=false;}}
    }
//    public static void analyze(String content) {
//        var props = new Properties();
//        // tokenizer, sentence splitting, consistuency parsing, sentiment analysis
//        props.setProperty("annotators", "tokenize, ssplit, parse, sentiment");
//        var pipeline = new StanfordCoreNLP(props);
//
//        String temp="";
//        for(int i=0;i<content.length();i++){
//            boolean bool=false;
//            if(content.charAt(i)==','){
//                bool=true;
//            }
//            else
//            if(bool){
//        var annotation = pipeline.process(content);
//        annotation.get(CoreAnnotations.SentencesAnnotation.class).forEach(sentence -> {
//            var tree = sentence.get(SentimentAnnotatedTree.class);
//            var sentimentInt = RNNCoreAnnotations.getPredictedClass(tree);
//            var sentimentName = sentence.get(SentimentCoreAnnotations.SentimentClass.class);
//            System.out.println(sentimentName + "\t" + sentimentInt + "\t" + sentence + "\n");
//
//        });bool=false;}
//    }

    record SentimentRecord(String name, int value, String sentence) {
    }

    public static SentimentRecord convertToSentimentRecord(CoreMap sentence) {
        var tree = sentence.get(SentimentAnnotatedTree.class);
        return new SentimentRecord(
                sentence.get(SentimentCoreAnnotations.SentimentClass.class),
                RNNCoreAnnotations.getPredictedClass(tree),
                sentence.toString());
    }

    public static boolean negativeComments(SentimentRecord sentimentRecord) {
        return sentimentRecord.value < 2;
    }

    public static boolean positiveComments(SentimentRecord sentimentRecord) {
        return sentimentRecord.value > 2;
    }

    public static List<SentimentRecord> analyzeAndReturn(String content) {
        var props = new Properties();
        // tokenizer, sentence splitting, consistuency parsing, sentiment analysis
        props.setProperty("annotators", "tokenize, ssplit, parse, sentiment");
        var pipeline = new StanfordCoreNLP(props);
        var annotation = pipeline.process(content);
        return annotation.get(CoreAnnotations.SentencesAnnotation.class).stream()
                .map(App::convertToSentimentRecord)
                .filter(App::negativeComments)
                //.filter(App::positiveComments)
                .collect(Collectors.toList());
    }

    */
/**
     * A tiny util that loads a resource from classpath using getResourceAsStream -
     * that works also in .jar packaged format
     *
     * @return
     * @throws IOException
     *//*

    private static String loadResourceFromClasspath() throws IOException {
        var inputStream = App.class.getClassLoader().getResourceAsStream("comments.txt");
        return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
    }
}
    */
/*public static void analyze(String content) {
        var props = new Properties();
        // tokenizer, sentence splitting, consistuency parsing, sentiment analysis
        props.setProperty("annotators", "tokenize, ssplit, parse, sentiment");
        var pipeline = new StanfordCoreNLP(props);
        var annotation = pipeline.process(content);
        annotation.get(CoreAnnotations.SentencesAnnotation.class).forEach(sentence -> {
            var tree = sentence.get(SentimentAnnotatedTree.class);
            var sentimentInt = RNNCoreAnnotations.getPredictedClass(tree);
            var sentimentName = sentence.get(SentimentCoreAnnotations.SentimentClass.class);
            System.out.println(sentimentName + "\t" + sentimentInt + "\t" + sentence);
        });
   }*/
//above this dont remove the starthat
