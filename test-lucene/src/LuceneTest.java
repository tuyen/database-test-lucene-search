
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

/**
 * This is test example file to explore Lucene API
 * 
 * @author vishal.zanzrukia
 * @version 1.0
 */
public class LuceneTest {
	/**
	 * this is index directory path where all index file will be stored which
	 * lucene uses internally.
	 */
	public static final Path INDEX_DIRECTORY = FileSystems.getDefault()
			.getPath("IndexDirectory");

	/**
	 * to create index on simple database table
	 */
	public void createIndex() {

		System.out.println("-- Indexing --");

		try {
			/** JDBC Section */
			Class.forName("com.mysql.jdbc.Driver").newInstance();

			/** Assuming database solr_test exists */
			Connection conn = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/recsys", "root", "");
			Statement stmt = conn.createStatement();
			String sql = "select JobId,JobName,Location,Tags from job";
			ResultSet rs = stmt.executeQuery(sql);

			/** Lucene Section */
			Directory directory = FSDirectory.open(INDEX_DIRECTORY);

			/** defining Analyzer */
			Analyzer keywordAnalyzer = new StandardAnalyzer();

			/** preparing config for indexWriter */
			IndexWriterConfig writerConfig = new IndexWriterConfig(
					keywordAnalyzer);
			/**
			 * Create a new index in the directory, removing any previously
			 * indexed documents
			 */
			writerConfig.setOpenMode(OpenMode.CREATE);
			/**
			 * Optional: for better indexing performance, if you are indexing
			 * many documents,<BR>
			 * increase the RAM buffer. But if you do this, increase the max
			 * heap size to the JVM (eg add -Xmx512m or -Xmx1g):
			 */
			// writerConfig.setRAMBufferSizeMB(256.0);

			IndexWriter iWriter = new IndexWriter(directory, writerConfig);

			int count = 0;
			Document doc = null;
			Field field = null;

			/** declaring string type */
//			FieldType stringType = new FieldType();
//			stringType.setTokenized(true);

			/** Looping through resultset and adding data to index file */
			while (rs.next()) {
				doc = new Document();

				/** adding id in document */
				field = new IntField("JobId", rs.getInt("JobId"), Field.Store.YES);
				doc.add(field);

				/** adding name in document */
				field = new Field("JobName", rs.getString("JobName"),
						TextField.TYPE_STORED);
				doc.add(field);

				/** adding address in document */
				field = new Field("Location", rs.getString("Location"),
						TextField.TYPE_STORED);
				doc.add(field);

				/** adding details in document */
				field = new StringField("Tags", rs.getString("Tags"),
						Field.Store.YES);
				doc.add(field);

				/** Adding doc to iWriter */
				iWriter.addDocument(doc);
				count++;
			}

			System.out.println(count + " record indexed");

			/** Closing iWriter */
			iWriter.commit();
			iWriter.close();

			/** Closing JDBC connection */
			rs.close();
			stmt.close();
			conn.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * to search the keywords
	 * 
	 * @param keyword
	 */
	public List<String> search(String keyword) {
		List<String> results = new ArrayList<String>();
		System.out.println("-- Seaching --");

		try {
			/** Searching */
			IndexReader directoryReader = DirectoryReader.open(FSDirectory
					.open(INDEX_DIRECTORY));
			IndexSearcher searcher = new IndexSearcher(directoryReader);
			Analyzer keywordAnalyzer = new StandardAnalyzer();

			/** MultiFieldQueryParser is used to search multiple fields */
			String[] filesToSearch = { "JobId", "JobName", "Location", "Tags" };
			MultiFieldQueryParser mqp = new MultiFieldQueryParser(
					filesToSearch, keywordAnalyzer);

			/** search the given keyword */
			Query query = mqp.parse(keyword);
			System.out.println("query >> " + query);

			/** run the query */
			TopDocs hits = searcher.search(query, 100);
			System.out.println("Results found >> " + hits.totalHits);

			Document doc = null;
//			for (int i = 0; i < hits.totalHits; i++) {
			for (int i = 0; i < 99; i++) {
				/** get the next document */
				doc = searcher.doc(hits.scoreDocs[i].doc);
				results.add("id:" + doc.get("JobId") + "--" +"title: " + doc.get("JobName") + "--"+ "Location: " + doc.get("Location"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return results;
	}
}
