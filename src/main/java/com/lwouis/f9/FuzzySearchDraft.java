package com.lwouis.f9;

import java.io.IOException;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

public class FuzzySearchDraft {
  public static void main(String[] args) throws IOException {
    String query = "tete";
    List<String> candidates = new ArrayList<>(
            Arrays.asList("test", "toto", "t1", "testt", "tot", "tit", "tat", "t21", "tst", "tste", "stst", "ttest",
                    "teest", "tsst", "tesst"));
    test(query, candidates);
  }

  private static void test(String query, List<String> candidates) throws IOException {
    // compare with prefix list
    int testLength = query.length();
    List<String> prefixMatches = new ArrayList<>();
    List<String> noPrefixMatches = new ArrayList<>();
    Collator coll = Collator.getInstance();
    coll.setStrength(Collator.PRIMARY);
    for (String ss : candidates) {
      if (coll.compare(query, ss.substring(0, Math.min(testLength, ss.length()))) == 0) {
        prefixMatches.add(ss);
      }
      else {
        noPrefixMatches.add(ss);
      }
    }

    // localized sort
    prefixMatches.sort(coll);

    List<String> fuzzyCandidates = fuzzySearch(query, noPrefixMatches);
    prefixMatches.addAll(fuzzyCandidates);
    for (String rr : prefixMatches) {
      System.out.println(rr);
    }
  }

  private static List<String> fuzzySearch(String test, List<String> sample) throws IOException {
    Directory dir = new RAMDirectory();
    IndexWriterConfig config = new IndexWriterConfig(new StandardAnalyzer());
    IndexWriter writer = new IndexWriter(dir, config);
    Document doc = new Document();
    for (String ss : sample) {
      doc.add(new StringField("name", ss, Field.Store.YES));
    }
    writer.addDocument(doc);
    writer.close();
    IndexReader reader = DirectoryReader.open(dir);
    IndexSearcher searcher = new IndexSearcher(reader);
    Term tt = new Term("name", test);
    Query query = new FuzzyQuery(tt);
    TopDocs docs = searcher.search(query, 1000);
    ScoreDoc[] ee = docs.scoreDocs;
    List<String> result = new ArrayList<>();
    for (ScoreDoc scoreDoc : ee) {
      result.add(searcher.doc(scoreDoc.doc).get("name"));
    }
    return result;
  }
}
