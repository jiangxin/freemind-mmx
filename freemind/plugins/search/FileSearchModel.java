package plugins.search;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

/**
 * This terminal application creates an Apache Lucene index in a folder and adds
 * files into this index based on the input of the user.
 */
public class FileSearchModel {

	public static final String FREEMIND_FILENAME_SUFFIX = ".mm";

	private enum FileAttribute {
		filename, path, contents
	}

	private static StandardAnalyzer analyzer = new StandardAnalyzer(
			Version.LUCENE_46);

	private ArrayList<File> queue = new ArrayList<File>();

	/**
	 * 
	 * @param querystring
	 * @param hitsPerPage
	 * @param searcher
	 * @param q
	 * @param index
	 * @return
	 * @throws ParseException
	 * @throws IOException
	 */
	public TopDocs doSearch(Query q, int hitsPerPage, IndexSearcher searcher)
			throws ParseException, IOException {
		TopScoreDocCollector collector = TopScoreDocCollector.create(
				hitsPerPage, true);
		searcher.search(q, collector);
		TopDocs topDocs = collector.topDocs();
		return topDocs;
	}

	protected Query getQuery(String querystring) throws ParseException {
		// the "title" arg specifies the default field to use
		// when no field is explicitly specified in the query.
		Query q = new QueryParser(Version.LUCENE_46,
				FileAttribute.contents.name(), analyzer).parse(querystring);
		return q;
	}

	protected IndexSearcher getSearcher(Directory index) throws IOException {
		DirectoryReader reader = DirectoryReader.open(index);
		IndexSearcher searcher = new IndexSearcher(reader);
		return searcher;
	}

	/**
	 * Constructor
	 * 
	 */
	FileSearchModel(Logger logger) {
		this._logger = logger;
	}

	private Logger _logger = null;

	/**
	 * Indexes a file or directory
	 * 
	 * @param fileName
	 *            the name of a text file or a folder we wish to add to the
	 *            index
	 * @return
	 * @throws java.io.IOException
	 *             when exception
	 */
	public Directory indexFileOrDirectory(String... fileName)
			throws IOException {
		Directory index = new RAMDirectory();
		IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_46,
				analyzer);
		IndexWriter writer = new IndexWriter(index, config);

		for (int i = 0; i < fileName.length; i++) {
			indexFileOrDirectoryCore(fileName[i], writer);
		}
		// ===================================================
		// after adding, we always have to call the
		// closeIndex, otherwise the index is not created
		// ===================================================
		writer.close();

		return index;
	}

	public Directory indexFileOrDirectory(File... fileName) throws IOException {
		Directory index = new RAMDirectory();
		IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_46,
				analyzer);
		IndexWriter writer = new IndexWriter(index, config);

		for (int i = 0; i < fileName.length; i++) {
			indexFileOrDirectoryCore(fileName[i], writer);
		}
		// ===================================================
		// after adding, we always have to call the
		// closeIndex, otherwise the index is not created
		// ===================================================
		writer.close();

		return index;
	}

	/**
	 * Indexes a file or directory
	 * 
	 * @param fileName
	 *            the name of a text file or a folder we wish to add to the
	 *            index
	 * @return
	 * @throws java.io.IOException
	 *             when exception
	 */
	public Directory indexFileOrDirectory(String fileName) throws IOException {
		return indexFileOrDirectory(new String[] { fileName });
	}

	public Directory indexFileOrDirectory(File fileName) throws IOException {
		return indexFileOrDirectory(new File[] { fileName });
	}

	/**
	 * 
	 * @param fileName
	 * @param writer
	 * @return
	 * @throws IOException
	 */
	private int indexFileOrDirectoryCore(String fileName, IndexWriter writer)
			throws IOException {
		// ===================================================
		// gets the list of files in a folder (if user has submitted
		// the name of a folder) or gets a single file name (is user
		// has submitted only the file name)
		// ===================================================
		File file = new File(fileName);
		if(!file.exists()) {
			throw new FileNotFoundException("Can't open " + fileName);
		}
		addFiles(file);

		return indexFiles(writer);
	}

	private int indexFileOrDirectoryCore(File fileName, IndexWriter writer)
			throws IOException {
		// ===================================================
		// gets the list of files in a folder (if user has submitted
		// the name of a folder) or gets a single file name (is user
		// has submitted only the file name)
		// ===================================================
		addFiles(fileName);

		return indexFiles(writer);
	}

	public int indexFiles(IndexWriter writer) throws IOException {
		int originalNumDocs = writer.numDocs();
		for (File f : queue) {
			FileReader fr = null;
			try {
				Document doc = new Document();

				// ===================================================
				// add contents of file
				// ===================================================
				fr = new FileReader(f);
				doc.add(new TextField(FileAttribute.contents.name(), fr));
				doc.add(new StringField(FileAttribute.path.name(), f.getPath(),
						Field.Store.YES));
				doc.add(new StringField(FileAttribute.filename.name(), f
						.getName(), Field.Store.YES));

				writer.addDocument(doc);
				_logger.info("Added: " + f);
			} catch (Exception e) {
				_logger.warning("Could not add: " + f);
			} finally {
				fr.close();
			}
		}

		int newNumDocs = writer.numDocs();
		int added = newNumDocs - originalNumDocs;
		_logger.info(added + " documents added.");
		queue.clear();
		return added;
	}

	public String getPath(Document d) {
		return d.get(FileAttribute.path.name());
	}

	public String getFilename(Document d) {
		return d.get(FileAttribute.filename.name());
	}

	public String[] getFilepathsFromSearchResults(IndexSearcher searcher,
			TopDocs results) throws IOException {
		ScoreDoc[] scoreDocs = results.scoreDocs;
		String[] fileNames = new String[scoreDocs.length];
		for (int i = 0; i < scoreDocs.length; i++) {
			ScoreDoc scoreDoc = scoreDocs[i];
			Document d = searcher.doc(scoreDoc.doc);
			fileNames[i] = getPath(d);
		}
		return fileNames;
	}

	/**
	 * 
	 * @param file
	 */
	private void addFiles(File file) {

		if (!file.exists()) {
			_logger.warning(file + " does not exist.");
		}
		if (file.isDirectory()) {
			for (File f : file.listFiles()) {
				addFiles(f);
			}
		} else {
			String filename = file.getName().toLowerCase();
			// ===================================================
			// Only index Mind map files
			// ===================================================
			if (filename.endsWith(FREEMIND_FILENAME_SUFFIX)) {
				queue.add(file);
			} else {
				_logger.fine("Skipped " + filename);
			}
		}
	}

}