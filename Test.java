import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class Test {

	public static void main(String[] args) {

		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, 10 * -1);
		Date targetDay = cal.getTime();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
		System.out.println(formatter.format(targetDay));
		
		String absoluteFilePath = "C:\\wisenut\\sf-1v5.3\\log\\isc\\2015\\09\\20150915_error.log";
		
		String fileName = absoluteFilePath.substring(absoluteFilePath.lastIndexOf(System.getProperty("file.separator")));
		if (fileName == null) {
			fileName = "";
		} else {
			fileName = fileName.replace(System.getProperty("file.separator"), "");
		}
		System.out.println(fileName);
		
	}
}
