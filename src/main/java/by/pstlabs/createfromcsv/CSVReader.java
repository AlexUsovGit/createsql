package by.pstlabs.createfromcsv;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class CSVReader {

    private static final String DELIMITER = "\n";
    private static final String CLIENT_FILE_PATH_CSV = "client_insert_template.csv";
    private static final String CLIENT_FILE_PATH_SQL = "insert-into-CLIENT.sql";

    private static final String RISK_STATUS_DICTIONARY_FILE_PATH_CSV = "risk_status_dictionary_insert_template.csv";
    private static final String RISK_STATUS_DICTIONARY_FILE_PATH_SQL = "insert-into-RISK_STATUS_DICTIONARY.sql";


    File fileDir;
    Writer currentWriter;

    @Bean()
    public void createSQL() throws IOException {

        currentWriter = generateUTF8Writer(new File(CLIENT_FILE_PATH_SQL));
        generateClient(readFile(new FileReader(CLIENT_FILE_PATH_CSV)), currentWriter);
        currentWriter.flush();


        currentWriter = generateUTF8Writer(new File(RISK_STATUS_DICTIONARY_FILE_PATH_SQL));
        generateClient(readFile(new FileReader(CLIENT_FILE_PATH_CSV)), currentWriter);
        currentWriter.flush();

        currentWriter.close();

    }

    public Writer generateUTF8Writer(File outputFile) throws FileNotFoundException {
        Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(outputFile), StandardCharsets.UTF_8));

        return writer;
    }

    public List<List<String>> readFile(FileReader inputFile) {
        List<List<String>> records = new ArrayList<>();

        String line;
        try (BufferedReader br = new BufferedReader(inputFile)) {
            while ((line = br.readLine()) != null) {
                String[] values = line.split(DELIMITER);
                records.add(Arrays.asList(values));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return records;
    }

    public void generateClient(List<List<String>> records, Writer writer) throws IOException {
        String insert = "";
        String header = "";
        String body = "";
        String status = "";

        if (records.size() > 0) {
            for (int i = 1; i < records.size(); i++) {
                for (String s : records.get(0)) {
                    header = s.replace(";", ",");
                }
                for (String s2 : records.get(i)) {
                    String[] row;
                    row = s2.split(";");
                    body = "'" + row[0] + "',"
                            + (row[1].equals("") ? "''," : "to_timestamp('" + row[1] + "', 'DD.MM.RR HH24:MI:SSXFF'),")
                            + (row[2].equals("") ? "''," : "to_timestamp('" + row[2] + "', 'DD.MM.RR HH24:MI:SSXFF'),")
                            + "'" + row[3] + "',"
                            + "'" + row[4] + "',"
                            + "'" + row[5] + "',"
                            + "'" + row[6] + "',"
                            + "'" + row[7] + "',"
                            + "'" + row[8] + "',"
                            + "'" + row[9] + "',"
                            + "'" + row[10] + "'";
                }
                insert = "Insert into Client (" + header + ")" + " values (" + body + ")";
                System.out.println(insert);
                writer.write(insert + ";\n");
            }
            status = "\n\n\nOk\n\n";

        } else {
            status = "\n\n\nчто-то пошло не так\n\n\n";
        }

        System.out.println(status);
    }
}