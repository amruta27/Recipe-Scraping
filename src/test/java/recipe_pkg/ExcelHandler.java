package recipe_pkg;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelHandler {

	public static void excelCreate(ArrayList<String> hList, Map<String, Map<String, RecipeVO>> allRecipeMap)
			throws IOException {

		XSSFWorkbook workbook = new XSSFWorkbook();
		for (Map.Entry<String, Map<String, RecipeVO>> entry : allRecipeMap.entrySet()) {
			String key = entry.getKey();
			Map<String, RecipeVO> recipeMap = entry.getValue();

			XSSFSheet worksheet = workbook.createSheet(key);

			// To create header
			int rowNo = 0;
			Row hRow = worksheet.createRow(rowNo++);
			int hColNo = 0;
			ArrayList<String> headerList = hList;
			int hCount = headerList.size();
			for (int i = 0; i < hCount; i++) {
				Cell cell1 = hRow.createCell(hColNo++);
				cell1.setCellValue(headerList.get(i));
			}

			for (Map.Entry<String, RecipeVO> recEntry : recipeMap.entrySet()) {
				String recKey = recEntry.getKey();
				RecipeVO recVal = recEntry.getValue();

				Row row = worksheet.createRow(rowNo++);
				int colNo = 0;
				Cell cell1 = row.createCell(colNo++);
				cell1.setCellValue((String) recVal.id);
				Cell cell2 = row.createCell(colNo++);
				cell2.setCellValue((String) recVal.name);

				Cell cell3 = row.createCell(colNo++);
				String ingredientList = "";
				for (String ing : recVal.ingredients) {
					ingredientList = ingredientList + ing + "\n";
				}
				cell3.setCellValue(ingredientList);

				Cell cell4 = row.createCell(colNo++);
				cell4.setCellValue((String) recVal.preparationTime);
				Cell cell5 = row.createCell(colNo++);
				cell5.setCellValue((String) recVal.cookingTime);
				Cell cell6 = row.createCell(colNo++);
				cell6.setCellValue((String) recVal.preparationMethod);
				Cell cell7 = row.createCell(colNo++);
				cell7.setCellValue((String) recVal.nutrientValues);
				Cell cell8 = row.createCell(colNo++);
				cell8.setCellValue((String) recVal.url);

			}

		}
		String path = System.getProperty("user.dir") + "/src/test/resources/OutputFile/AllRecipes.xlsx";
		File Excelfile = new File(path);
		FileOutputStream Fos = null;
		try {
			Fos = new FileOutputStream(Excelfile);
			workbook.write(Fos);
			workbook.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			Fos.close();
		}

	}

	public static void excelWithElimination(ArrayList<String> hList, Map<String, Map<String, RecipeVO>> allRecipeMap,
			Map<String, List<String>> eliminationListMap) throws IOException {

		XSSFWorkbook workbook = new XSSFWorkbook();
		for (Map.Entry<String, Map<String, RecipeVO>> entry : allRecipeMap.entrySet()) {
			String key = entry.getKey();
			Map<String, RecipeVO> recipeMap = entry.getValue();

			XSSFSheet worksheet = workbook.createSheet(key);

			// To create header
			int rowNo = 0;
			Row hRow = worksheet.createRow(rowNo++);
			int hColNo = 0;
			ArrayList<String> headerList = hList;
			int hCount = headerList.size();
			for (int i = 0; i < hCount; i++) {
				Cell cell1 = hRow.createCell(hColNo++);
				cell1.setCellValue(headerList.get(i));
			}

			// To eliminate recipe from recipeMap by Ingredients
			for (Map.Entry<String, RecipeVO> recEntry : recipeMap.entrySet()) {
				String recKey = recEntry.getKey();
				RecipeVO recVal = recEntry.getValue();
				for (String ing : recVal.ingredients) {
					for (String eIng : eliminationListMap.get(key)) {
						if (ing.toUpperCase().contains(eIng)) {
							recipeMap.remove(recKey);
							System.out.println("*** Remove Key: " + recKey);
						}
					}

				}

			}
			for (Map.Entry<String, RecipeVO> recEntry : recipeMap.entrySet()) {
				String recKey = recEntry.getKey();
				RecipeVO recVal = recEntry.getValue();

				Row row = worksheet.createRow(rowNo++);
				int colNo = 0;
				Cell cell1 = row.createCell(colNo++);
				cell1.setCellValue((String) recVal.id);
				Cell cell2 = row.createCell(colNo++);
				cell2.setCellValue((String) recVal.name);

				Cell cell3 = row.createCell(colNo++);
				String ingredientList = "";
				for (String ing : recVal.ingredients) {
					ingredientList = ingredientList + ing + "\n";
				}
				cell3.setCellValue(ingredientList);

				Cell cell4 = row.createCell(colNo++);
				cell4.setCellValue((String) recVal.preparationTime);
				Cell cell5 = row.createCell(colNo++);
				cell5.setCellValue((String) recVal.cookingTime);
				Cell cell6 = row.createCell(colNo++);
				cell6.setCellValue((String) recVal.preparationMethod);
				Cell cell7 = row.createCell(colNo++);
				cell7.setCellValue((String) recVal.nutrientValues);
				Cell cell8 = row.createCell(colNo++);
				cell8.setCellValue((String) recVal.url);

			}

		}
		String path = System.getProperty("user.dir") + "/src/test/resources/OutputFile/FinalRecipes.xlsx";
		File Excelfile = new File(path);
		FileOutputStream Fos = null;
		try {
			Fos = new FileOutputStream(Excelfile);
			workbook.write(Fos);
			workbook.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			Fos.close();
		}

	}

	public static List<ERecipeVO> excelRead() throws IOException {

		// obtaining input bytes from a file
		String path = System.getProperty("user.dir") + "/src/test/resources/InputFile/InputRecipeList.xlsx";
		FileInputStream fis = new FileInputStream(path); // obtaining bytes from the file
		// creating Workbook instance that refers to .xlsx file
		XSSFWorkbook wb = new XSSFWorkbook(fis);
		XSSFSheet sheet = wb.getSheetAt(0); // creating a Sheet object to retrieve object
		Iterator<Row> itr = sheet.iterator(); // iterating over excel file
		List<ERecipeVO> eRecipeList = new ArrayList<ERecipeVO>();
		while (itr.hasNext()) {
			ERecipeVO eRec = new ERecipeVO();
			Row row = itr.next();
			Iterator<Cell> cellIterator = row.cellIterator(); // iterating over each column
			int r = 0;
			while (cellIterator.hasNext()) {
				r++;
				Cell cell = cellIterator.next();
				switch (cell.getCellType()) {
				case STRING: // field that represents string cell type
					if (r == 1) {
						eRec.rName = cell.getStringCellValue();
					}
					if (r == 2) {
						eRec.xPath = cell.getStringCellValue();
					}
					if (r == 3) {
						eRec.eIngredients = cell.getStringCellValue();
					}
					break;
				case NUMERIC: // field that represents number cell type
					break;
				default:
				}
			}
			eRecipeList.add(eRec);
			System.out.println("");
		}

		return eRecipeList;
	}
}
