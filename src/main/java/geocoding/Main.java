package geocoding;

import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;

public class Main {
	private static final String API_KEY = System.getenv("API_KEY");
	private static final String[] HEADERS = new String[] { "level", "name",
			"phone", "zipcode", "address", "v1", "v2" };

	public static void main(String[] args) throws Exception {
		CSVParser parser = new CSVParser(new FileReader("all.csv"),
				CSVFormat.DEFAULT.withHeader(HEADERS));
		CSVPrinter printer = new CSVPrinter(new FileWriter("convert.csv"),
				CSVFormat.DEFAULT.withHeader(HEADERS).withRecordSeparator('\n'));

		for (CSVRecord record : parser.getRecords()) {
			String level = record.get("level").trim();
			String name = record.get("name").trim();
			String phone = record.get("phone").trim();
			String zipcode = record.get("zipcode").trim();
			String address = record.get("address").trim();
			String v1 = record.get("v1").trim();
			String v2 = record.get("v2").trim();

			List<String> columns = new ArrayList<String>();

			columns.add(level);
			columns.add(name);
			columns.add(phone);
			columns.add(zipcode);
			columns.add(address);
			columns.add(v1);
			columns.add(v2);

			GeoApiContext context = new GeoApiContext().setApiKey(API_KEY);
			GeocodingResult[] results;

			results = GeocodingApi.geocode(context, address).await();

			if (results.length != 0) {
				LatLng location = results[0].geometry.location;

				columns.add(String.valueOf(location.lat));
				columns.add(String.valueOf(location.lng));

				printer.printRecord(columns);
			}

			System.out.println(address);
		}

		printer.flush();
		printer.close();

		parser.close();

	}
}