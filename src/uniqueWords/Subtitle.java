package uniqueWords;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

public class Subtitle {

	private Subtitle() {

	}

	public static List<String> readFile(String filePath) {

		List<String> list = new ArrayList<>();
		try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
			String line;
			while ((line = reader.readLine()) != null) {
				list.add(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return list;
	}

	public static List<String> leaveOnlyPhrases(List<String> list) {
		List<String> listWithoutCharacter = list.stream().map(Subtitle::removeOtherCharacterFunction).toList();
		List<String> listPhrases = new ArrayList<>(listWithoutCharacter);
		listPhrases.removeIf(String::isBlank);

		return listPhrases;
	}

	public static String removeOtherCharacterFunction(String t) {

		return t.replaceAll("-?\\d+(?:\\.\\d+)?", "").replace(":", "").replace(",", "").replace("-->", "")
				.replace("<i>", "").replace("</i>", "").replace(".", "").replace("?", "").replace("!", "")
				.replace("\"", "").replace("-", "").replace("$", "").toLowerCase();
	}

	public static Set<String> leaveOnlyWords(List<String> listPhrases) {
		Set<String> onlyWords = new HashSet<>();
		for (String phrase : listPhrases) {
			String[] words = phrase.split(" ");
			onlyWords.addAll(Arrays.asList(words));
		}
		onlyWords.removeIf(String::isBlank);
		return onlyWords;
	}

	public static Set<String> getUniqueWordsFromFile(String route) {
		List<String> readFile = Subtitle.readFile(route);
		List<String> phrasesWithoutCharacter = Subtitle.leaveOnlyPhrases(readFile);
		return Subtitle.leaveOnlyWords(phrasesWithoutCharacter);
	}
	
	
	public static List<String> getFilesFromDirectory(String pathName) {
		File directory = new File(pathName);
		if(directory.isDirectory()) {
			List<String> fileExcel = new ArrayList<>(Arrays.asList(directory.list()));
			fileExcel.removeIf(x-> !x.endsWith(".srt"));
			return fileExcel;
		}
		
		throw new RuntimeException("no directory");
	}
	
	
	public static void getAllWords(String pathName) {
		List<String> filesFromDirectory = getFilesFromDirectory(pathName);
		Set<Set<String>> wordsLists = new HashSet<>();
		
		for (String file : filesFromDirectory) {
			Set<String> uniqueWordsFromFile = getUniqueWordsFromFile(pathName.concat("/").concat(file));
			wordsLists.add(uniqueWordsFromFile);
		}
		writeInExcel(pathName.concat("/").concat("file.xlsx"), wordsLists);

	}
	
	public static void writeInExcel(String filePath, Set<Set<String>> wordsLists) {
        try (Workbook workbook = new HSSFWorkbook()) {
            
        	Sheet sheet = workbook.createSheet("Sheet1");

        	
        	Set<String> allWords = new HashSet<>();
            
            int cellNumber = 0;
            for (Set<String> words :wordsLists) {
				
            	allWords.addAll(words);
            	
	            int rowNumber = 0;
	            for (String word : words) {
	            	Row row=null;
            		row = sheet.getRow(rowNumber);
            		if(row==null) {
            			row = sheet.createRow(rowNumber);
            		}
	            	
	            	Cell cell = row.createCell(cellNumber);
	                cell.setCellValue(word);
	                rowNumber++;
				}
	            
	            cellNumber++;
			}

            int rowNumberAll = 0;
            for (String word :allWords) {
            	Row row=null;
        		row = sheet.getRow(rowNumberAll);
        		if(row==null) {
        			row = sheet.createRow(rowNumberAll);
        		}
            	
            	Cell cell = row.createCell(cellNumber);
                cell.setCellValue(word);
                rowNumberAll++;
            }
            
            try (FileOutputStream fileOutputStream = new FileOutputStream(filePath)) {
                workbook.write(fileOutputStream);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
	}
}
