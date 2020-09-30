package util;

import settings.Setting;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.QueryBuilder;

import java.io.IOException;
import java.nio.file.Paths;

public class Simple {
    private static String path = Setting.PATH;
    public static void main(String[] args) throws IOException {
        testIndex(path);
    }

    /**
     * test index a doc
     * @param path
     * @throws IOException
     */
    public static void testIndex(String path) throws IOException {
        Directory directory = FSDirectory.open(Paths.get(path));
        IndexWriter indexWriter = new IndexWriter(directory,new IndexWriterConfig(new StandardAnalyzer()).setUseCompoundFile(false));
        Document document = new Document();
        document.add(new TextField("name","chenxin", Field.Store.YES));
        document.add(new StoredField("age",23));
        indexWriter.addDocument(document);
        indexWriter.commit();
        indexWriter.close();
    }

    public static void testSearch(String path) throws IOException {
        Directory directory = FSDirectory.open(Paths.get(path));
        IndexReader reader = DirectoryReader.open(directory);
        IndexSearcher searcher = new IndexSearcher((reader));
        QueryBuilder builder = new QueryBuilder(new StandardAnalyzer());
        Query query = builder.createBooleanQuery("name","chenxin");
        TopDocs topDocs = searcher.search(query, 10);
        ScoreDoc[] docs = topDocs.scoreDocs;
        for (ScoreDoc doc : docs){
            Document document = searcher.doc(doc.doc);
            System.out.println(document.get("name"));
            System.out.println(document.get("age"));
        }
    }
}
