package recipe_pkg;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.common.base.Splitter;

import io.github.bonigarcia.wdm.WebDriverManager;

public class RecipeScrapping {

	static WebDriver driver;

	@BeforeTest
	public void firstStep() {

		WebDriverManager.chromedriver().setup();
		driver = new ChromeDriver();
		String url = "https://www.tarladalal.com/";
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
		driver.manage().timeouts().pageLoadTimeout(3600, TimeUnit.SECONDS);
		driver.manage().window().maximize();
		driver.get(url);
	}

	@AfterTest
	public void quitBrowser() {
		driver.quit();
	}

	public static Map<String, RecipeVO> validateRecipe(String url) throws IOException {

		// Get page count
		List<WebElement> pageList = driver.findElements(By.xpath("//a[@class='respglink']"));
		int pageSize = pageList.size();
		int pageCount = Integer.parseInt(pageList.get(pageSize - 1).getText());
		System.out.println("Page Count: " + pageCount);

		// Prepare list for excel rows
		ArrayList<ArrayList> rowList = new ArrayList<ArrayList>();
		List<RecipeVO> recpList = new ArrayList<RecipeVO>();
		Map<String, RecipeVO> recipeMap = new HashMap<String, RecipeVO>();
		
		for (int currentPage = 0; currentPage < pageCount; currentPage++) {
			System.out.println("*** for loop - page ***");
			// Construct URL for page
			int pageIndex = currentPage + 1;
			String redirect_URL = url + "?pageindex=" + pageIndex;
			System.out.println("Page no : " + pageIndex);

			driver.get(redirect_URL);

			List<WebElement> recipeNameList = driver.findElements(By.xpath("//a[@itemprop='url']"));

			int recipeCount = recipeNameList.size();
			System.out.println("Recipe recipeCount: " + recipeCount);

			for (int i = 0; i < recipeCount; i++) {
				System.out.println("*** for loop - recipe ***");
				RecipeVO recipe = new RecipeVO();
				List<WebElement> recipeIdList = driver.findElements(By.xpath("//div[@class='rcc_rcpno']/span"));
				if(recipeIdList.size() > 0)
				{
					String recipe_a[] = recipeIdList.get(i).getText().split("\\r?\\n");
					String recipe_b[] = recipe_a[0].split("# ");
					recipe.id = recipe_b[1];
					System.out.println("***Recipe Id :" +recipe.id);
				}
				
				List<WebElement> recipeNameList2 = driver.findElements(By.xpath("//a[@itemprop='url']"));
				if(recipeNameList2.size() > 0)
				{
					recipe.name = recipeNameList2.get(i).getText();
					recipeNameList2.get(i).click();
				}

				List<WebElement> ingredients = driver.findElements(By.xpath("//span[@itemprop='recipeIngredient']"));
				if(ingredients.size()>0)
				{
					List<String> ingredientList = new ArrayList<String>();
					for (int j = 0; j < ingredients.size(); j++) {
						ingredientList.add(ingredients.get(j).getText());
					}
					recipe.ingredients = ingredientList;
				}
				
				List<WebElement> prepTime = driver.findElements(By.xpath("//time[@itemprop='prepTime']"));
				String preTime =" ";
				if (prepTime.size() == 0) {
					preTime = "NIL";					
				} else {
					preTime = prepTime.get(0).getText();					
				}
				recipe.preparationTime = preTime;
				
				List<WebElement> cookTime = driver.findElements(By.xpath("//time[@itemprop='cookTime']"));
				String cookTiming =" ";
				if (cookTime.size() == 0) {
					cookTiming = "NIL";					
				} else {
					cookTiming = cookTime.get(0).getText();					
				}
				recipe.cookingTime = cookTiming;

				List<WebElement> prepmethod = driver.findElements(By.xpath("//div[@id='recipe_small_steps']"));
				String method =" ";
				if (prepmethod.size() == 0) {
					method = "NIL";					
				} else {
					method = prepmethod.get(0).getText();					
				}
				recipe.preparationMethod = method;

				List<WebElement> nutrientValue = driver.findElements(By.xpath("//table[@id='rcpnutrients']"));
				String nutVal = "";
				if (nutrientValue.size() == 0) {
					nutVal = "NIL";					
				} else {
					nutVal = nutrientValue.get(0).getText();					
				}
				recipe.nutrientValues = nutVal;

				recipe.url = driver.getCurrentUrl();

				recipeMap.put(recipe.id, recipe);

				if (i != recipeCount - 1) {
					driver.get(redirect_URL);
				}
			}

		}

		driver.get("https://tarladalal.com/RecipeCategories.aspx");

		return recipeMap;
	}

	@Test

	public static void recipeCard() throws IOException {

		WebElement recipes = driver.findElement(By.xpath("//div[text() = 'RECIPES']"));
		recipes.click();

		Map<String, Map<String, RecipeVO>> allRecipeMap = new HashMap<String, Map<String, RecipeVO>>();

		Map<String, List<String>> eliminationListMap = new HashMap<String, List<String>>();

		List<ERecipeVO> eRecipeList = ExcelHandler.excelRead();
		int erCount = eRecipeList.size();
		for (int i = 0; i < erCount; i++) {
			System.out.println("*** for loop - eRecipe ***");
			Map<String, RecipeVO> recipeMap = new HashMap<String, RecipeVO>();

			WebElement recipe = driver.findElement(By.id(eRecipeList.get(i).xPath));
			recipe.click();
			

			// Get current page URL
			String temp_URL = driver.getCurrentUrl();

			List<String> eliminationList = Splitter.on(",").trimResults().splitToList(eRecipeList.get(i).eIngredients);
			
			recipeMap = validateRecipe(temp_URL);
			System.out.println("*** recipeMap :" + recipeMap);
			String recipeTitle = eRecipeList.get(i).rName;
			System.out.println("*** recipeTitle :" + recipeTitle);
			allRecipeMap.put(recipeTitle, recipeMap);
			eliminationListMap.put(recipeTitle, eliminationList);
		}

		for (Map.Entry<String, Map<String, RecipeVO>> entry : allRecipeMap.entrySet()) {
			String key = entry.getKey();
			Map<String, RecipeVO> recipeMap = entry.getValue();

		}
		
		// Prepare list for excel header
		ArrayList<String> headerList = new ArrayList<String>(Arrays.asList("Recipe ID", "Recipe Name", "Ingredients",
				"Preparation Time", "Cooking Time", "Preparation method", "Nutrient values", "Recipe URL"));

		// To create excel with all recipes
		ExcelHandler.excelCreate(headerList, allRecipeMap);
		
		// To create excel with all recipes with out elimination ingredients
		ExcelHandler.excelWithElimination(headerList, allRecipeMap, eliminationListMap);
	}
}
