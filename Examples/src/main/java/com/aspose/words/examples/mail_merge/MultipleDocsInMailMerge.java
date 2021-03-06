package com.aspose.words.examples.mail_merge;

import com.aspose.words.Document;
import com.aspose.words.examples.Utils;

import java.sql.*;
import java.text.MessageFormat;
import java.util.Hashtable;


public class MultipleDocsInMailMerge {
    //ExStart:
    private static Connection mConnection;
    private static final String dataDir = Utils.getSharedDataDir(NestedMailMergeRegions.class) + "MailMerge/";

    public static void main(String[] args) throws Exception {
        produceMultipleDocuments(dataDir, "TestFile.Multiple Pages.doc");
    }

    public static void produceMultipleDocuments(String dataDir, String srcDoc) throws Exception {
        // Create a connection to the database
        createConnection(dataDir);

        // Open the database connection.
        ResultSet rs = executeQuery("SELECT * FROM Customers");

        // Open the template document.
        Document doc = new Document(dataDir + srcDoc);

        // A record of how many documents that have been generated so far.
        int counter = 1;

        // Loop though all records in the data source.
        while (rs.next()) {
            // Clone the template instead of loading it from disk (for speed).
            Document dstDoc = (Document) doc.deepClone(true);

            // Extract the data from the current row of the ResultSet into a Hashtable.
            Hashtable dataMap = getRowData(rs);

            // Execute mail merge.
            dstDoc.getMailMerge().execute(keySetToArray(dataMap), dataMap.values().toArray());

            // Save the document.
            dstDoc.save(MessageFormat.format(dataDir + "TestFile Out {0}.doc", counter++));
        }
    }

    /**
     * Creates a Hashtable from the name and value of each column in the current row of the ResultSet.
     */
    public static Hashtable getRowData(ResultSet rs) throws Exception {
        ResultSetMetaData metaData = rs.getMetaData();
        Hashtable values = new Hashtable();

        for (int i = 1; i <= metaData.getColumnCount(); i++) {
            values.put(metaData.getColumnName(i), rs.getObject(i));
        }

        return values;
    }

    /**
     * Utility function that returns the keys of a Hashtable as an array of Strings.
     */
    public static String[] keySetToArray(Hashtable table) {
        return (String[]) table.keySet().toArray(new String[table.size()]);
    }

    /**
     * Executes a query to the demo database using a new statement and returns
     * the result in a ResultSet.
     */
    protected static ResultSet executeQuery(String query) throws Exception {
        return createStatement().executeQuery(query);
    }

    /**
     * Utility function that creates a connection to the Database.
     */
    public static void createConnection(String dataDir) throws Exception {
        Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
        String connectionString = "jdbc:ucanaccess://" + dataDir + "Customers.mdb";

        // Create a connection to the database.
        mConnection = DriverManager.getConnection(connectionString);
    }

    /**
     * Utility function that creates a statement to the database.
     */
    public static Statement createStatement() throws Exception {
        return mConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
    }
    //ExEnd:
}