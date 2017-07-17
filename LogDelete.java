import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class LogDelete {

	final static String TARGET_DIRECTORY = "TargetDirectory";
	final static String SUB_DIRECTORY_TYPE = "SubDirectoryType";
	final static String TARGET_SUB_DIRECTORY_LEVEL = "TargetSubDirectoryLevel";
	final static String REMOVE_BLANK_DIRECTORY = "RemoveBlankDirectory";
	final static String DENY_DIRECTORY = "DenyDirectory";
	final static String FILE_DATE_EXPRESSION = "FileDateExpression";
	final static String TARGET_EXTENTION = "TargetExtention";
	final static String LOG_RETENTION_PERIOD_DAY = "LogRetentionPeriodDay";
	final static String VALUE = "value";

	static List<String> denyDirectoryList = null;
	static List<String> targetExtentionList = null;
	static List<String> fileDateExpressionList = null;
	static String logRetentionPeriodDay = null;

    final static boolean isDebug = false;
	
	/**
	 * @param args
	 * @throws ParserConfigurationException 
	 * @throws IOException 
	 * @throws SAXException 
	 * @throws XPathExpressionException 
	 */
	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
        System.out.println("Start Log Delete..");
        
        // 설정파일을 읽어 들인다.
        File config = new File("bin/config.xml");
 
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(config);
        XPath xpath = XPathFactory.newInstance().newXPath();
 
        NodeList targetDirList = (NodeList) xpath.compile("//Config/TargetDirectory").evaluate(document, XPathConstants.NODESET);
        for (int idx = 0; idx < targetDirList.getLength(); idx++){
        	String value = checkNull(targetDirList.item(idx).getAttributes().getNamedItem(VALUE).getTextContent());
        	String subDirectoryType = checkNull(targetDirList.item(idx).getAttributes().getNamedItem(SUB_DIRECTORY_TYPE).getTextContent());
        	
        	if (isDebug) System.out.println(TARGET_DIRECTORY + ", value : " + value + ", subDirectoryType : " + subDirectoryType);

        	// TargetDirectory Element
        	Element element = (Element) targetDirList.item(idx);
        	
        	// 재사용 객체
        	NodeList nodeList = null;

        	// TargetSubDirectoryLevel
        	String targetSubDirectoryLevel = "";
        	nodeList = element.getElementsByTagName(TARGET_SUB_DIRECTORY_LEVEL);
        	for (int k = 0; k < nodeList.getLength(); k++) {
        		targetSubDirectoryLevel = checkNull(nodeList.item(k).getAttributes().getNamedItem(VALUE).getTextContent());
            	if (isDebug) System.out.println(TARGET_SUB_DIRECTORY_LEVEL + ", value : " + targetSubDirectoryLevel);
        	}
        	
        	// RemoveBlankDirectory
        	String removeBlankDirectory = "";
        	nodeList = element.getElementsByTagName(REMOVE_BLANK_DIRECTORY);
        	for (int k = 0; k < nodeList.getLength(); k++) {
            	removeBlankDirectory = checkNull(nodeList.item(k).getAttributes().getNamedItem(VALUE).getTextContent());
            	if (isDebug) System.out.println(REMOVE_BLANK_DIRECTORY + ", value : " + removeBlankDirectory);
        	}
        	
        	// DenyDirectory
        	denyDirectoryList = new ArrayList<String>();
        	nodeList = element.getElementsByTagName(DENY_DIRECTORY);
        	for (int k = 0; k < nodeList.getLength(); k++) {

            	Element subElement = (Element) nodeList.item(k);

            	// DenyDirectory/value
            	NodeList subNodeList = subElement.getElementsByTagName(VALUE);
            	for (int m = 0; m < subNodeList.getLength(); m++) {
            		String childValue = checkNull(subNodeList.item(m).getTextContent());
            		denyDirectoryList.add(childValue);
            		if (isDebug) System.out.println(DENY_DIRECTORY + "/" + VALUE + ", value : " + childValue);
            	}
        	}
        	
        	// FileDateExpression
        	fileDateExpressionList = new ArrayList<String>();
        	nodeList = element.getElementsByTagName(FILE_DATE_EXPRESSION);
        	for (int k = 0; k < nodeList.getLength(); k++) {

            	Element subElement = (Element) nodeList.item(k);

            	// FileDateExpression/value
            	NodeList subNodeList = subElement.getElementsByTagName(VALUE);
            	for (int m = 0; m < subNodeList.getLength(); m++) {
            		String childValue = checkNull(subNodeList.item(m).getTextContent());
            		fileDateExpressionList.add(childValue);
            		if (isDebug) System.out.println(FILE_DATE_EXPRESSION + "/" + VALUE + ", value : " + childValue);
            	}
        	}

        	// TargetExtention
        	targetExtentionList = new ArrayList<String>();
        	nodeList = element.getElementsByTagName(TARGET_EXTENTION);
        	for (int k = 0; k < nodeList.getLength(); k++) {

            	Element subElement = (Element) nodeList.item(k);

            	// TargetExtention/value
            	NodeList subNodeList = subElement.getElementsByTagName(VALUE);
            	for (int m = 0; m < subNodeList.getLength(); m++) {
            		String childValue = checkNull(subNodeList.item(m).getTextContent());
            		targetExtentionList.add(childValue);
            		if (isDebug) System.out.println(TARGET_EXTENTION + "/" + VALUE + ", value : " + childValue);
            	}
        	}

        	// LogRetentionPeriodDay
        	logRetentionPeriodDay = "";
        	nodeList = element.getElementsByTagName(LOG_RETENTION_PERIOD_DAY);
        	for (int k = 0; k < nodeList.getLength(); k++) {
        		logRetentionPeriodDay = checkNull(nodeList.item(k).getAttributes().getNamedItem(VALUE).getTextContent());
            	if (isDebug) System.out.println(LOG_RETENTION_PERIOD_DAY + ", value : " + logRetentionPeriodDay);
        	}
        	
        	// 파일삭제 처리
        	File targetDir = new File(value);
        	
        	File[] flist = targetDir.listFiles();
        	
        	if (flist == null) {
        		System.err.println("존재하지 않는 디렉토리 입니다. [" + value + "]");
        		continue;
        	}
        	
        	int targetLevel = Integer.parseInt(targetSubDirectoryLevel);
        	
        	for (File f : flist) {
        		if (isDebug) System.out.println(f.getAbsolutePath());
        		
        		// 서브 디렉토리 타입인 경우에는 해당 수만큼 하위 디렉토리를 뒤지면서 재귀호출하면서 파일들을 삭제처리한다.
        		if (subDirectoryType.equals("y")) {
        			int level = 1;
        			findSubDirectory(f, targetLevel, level);
        		} else {
        			// 삭제대상 파일을 지정한다.
        			if (f.isFile()
        					&& (isDenyDirectory(f.getAbsolutePath()) == false && isTargetExtention(f.getAbsolutePath()) == true && isDeleteConditionFile(f.getAbsolutePath()) == true)) {
        				System.out.println("삭제 대상 파일: " + f.getAbsolutePath() + ", result: " + f.delete());
        			}
        		}
        	}
        }
        
        System.out.println("End Log Delete..");
	}

	private static void findSubDirectory(File file, int targetLevel, int level) {
		if (isDebug) System.out.println("targetLevel : " + targetLevel + ", cnt : " + level);
		int subLevel = level + 1;
		File[] flist = file.listFiles();
		if (flist == null) return;
		for (File f : flist) {
			if (f.isDirectory()) {
				// 설정파일의 대상 타겟 레벨을 넘는 경우에는 건너뛴다.
				if (targetLevel < subLevel) {
					continue;
				} else {
					findSubDirectory(f, targetLevel, subLevel);
				}
			} else if (f.isFile()
					&& (isDenyDirectory(f.getAbsolutePath()) == false && isTargetExtention(f.getAbsolutePath()) == true && isDeleteConditionFile(f.getAbsolutePath()) == true)) {
				// 삭제대상 파일을 지정한다.
				System.out.println("삭제 대상 파일: " + f.getAbsolutePath() + ", result: " + f.delete());
    		}
		}
	}
	
	private static boolean isDenyDirectory(String absoluteFilePath) {
		if (absoluteFilePath == null) return false;
		if (denyDirectoryList == null) return false;
		for (String denyDirectory : denyDirectoryList) {
			if (absoluteFilePath.indexOf(System.getProperty("file.separator") + denyDirectory + System.getProperty("file.separator")) < 0) {
				return false;
			} else {
				return true;
			}
		}
		return false;
	}

	private static boolean isTargetExtention(String absoluteFilePath) {
		if (absoluteFilePath == null) return false;
		if (targetExtentionList == null) return false;
		for (String targetExtention : targetExtentionList) {
			if (absoluteFilePath.endsWith(targetExtention)) {
				return true;
			} else {
				return false;
			}
		}
		return false;
	}
	
	private static boolean isDeleteConditionFile(String absoluteFilePath) {
		if (absoluteFilePath == null) return false;
		if (fileDateExpressionList == null) return false;
		if (logRetentionPeriodDay == null) return false;
		
		int logRetentionPeriodDayNum = 0;
		try {
			logRetentionPeriodDayNum = Integer.parseInt(logRetentionPeriodDay);
		} catch (NumberFormatException e) { 
			return false;
		}
		
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, logRetentionPeriodDayNum * -1);
		Date targetDay = cal.getTime();

		Date fileDate = null;

		// 파일명에서 맨끝을 잘라서 저장한다.
		String fileName = absoluteFilePath.substring(absoluteFilePath.lastIndexOf(System.getProperty("file.separator")));
		if (fileName == null) {
			fileName = "";
		} else {
			fileName = fileName.replace(System.getProperty("file.separator"), "");
		}
		
		for (String fileDateExpression : fileDateExpressionList) {
			// 파일 날짜를 매핑시켜 본다.
			String tmpFileName = fileName.substring(0, fileDateExpression.length());
			
			SimpleDateFormat formatter = new SimpleDateFormat(fileDateExpression, Locale.getDefault());
			
			try {
				fileDate = formatter.parse(tmpFileName);
				break; // 에러 없이 잘 변경 되었으면 매칭된 것으로 본다.
			} catch (ParseException e) { }
		}
		
		// 해당 파일이 TargetDay 보다 큰 날짜인 경우에는 삭제한다.
		int compare = targetDay.compareTo(fileDate);
		
		if (compare > 0) {
			return true;
		} else {
			return false;
		}
	}
	
    private static String checkNull(String str) {
 
        if (str == null || str.trim().equals("")) {
            return "";
        } else {
            return str.trim();
        }
    }
}
