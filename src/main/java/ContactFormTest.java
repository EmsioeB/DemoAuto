import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestResult;
import org.testng.annotations.*;
import org.testng.Assert;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;


public class ContactFormTest {

    private static WebDriver driver;
    private String baseURL = "https://DEHA-soft.com";

    @BeforeClass
    @Parameters("browser")
    public void setUp(String browser) {
        // Lựa chọn trình duyệt
        switch (browser.toLowerCase()) {
            case "chrome":
                System.setProperty("webdriver.chrome.driver", "path/to/chromedriver");
                driver = new ChromeDriver();
                break;
            case "edge":
                System.setProperty("webdriver.edge.driver", "path/to/msedgedriver");
                driver = new EdgeDriver();
                break;
            case "firefox":
                System.setProperty("webdriver.gecko.driver", "path/to/geckodriver");
                driver = new FirefoxDriver();
                break;
            default:
                throw new IllegalArgumentException("Unsupported browser: " + browser);
        }
        //path/to/driver: Copy đường dẫn đến WebDriver theo máy
        // Mở trình duyệt full màn hình
        driver.manage().window().maximize();
    }

    @Test (priority = 1)
    public void verifyHomePage() {
        // Mở trang web
        driver.get(baseURL);

        // Xác minh đã vào trang chủ thông qua Text ở trang chủ
        Assert.assertEquals(driver.findElement(By.xpath("/html/body/div[2]/div[1]/div/div/div/div/ul/li[1]/div/div[1]/h1")).getText(), "Nhà tư vấn chiến lược số\n" +
                "hàng đầu Việt Nam");
    }

    @Test (priority = 2, dataProvider = "contactFormData")
    public void testContactForm(String name, String businessName, String businessEmail, String phone, String message) {
        driver.findElement(By.xpath("/html/body/div[1]/header[1]/div[3]/div/nav/div/div/div[7]/a/span")).click();
        // Điền thông tin vào các trường của form liên hệ
        fillContactForm(name, businessName, businessEmail, phone, message);

        // Ấn nút Gửi
        WebElement submitButton = driver.findElement(By.xpath("/html/body/main/div/div[1]/div[2]/div/div/div/div/form/div[1]/div[10]/button"));
        submitButton.click();

        String successMessageLocator = "//div[contains(@class,'elementor-message-success') and contains(text(),'Your submission was successful')]";
        WebDriverWait wait = new WebDriverWait(driver, 10);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(successMessageLocator)));
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(successMessageLocator)));
            // Kiểm tra xem thông báo thành công có hiển thị không
        } catch (Exception e) {
            handleErrorMessage();
        }
    }


    @DataProvider(name = "contactFormData")
    public Object[][] provideContactFormData() {
        // Cung cấp dữ liệu để kiểm thử liên hệ
        return new Object[][]{
                {"Nguyễn Văn A", "Công ty cổ phần B", " a@example.com ", "0934567123", "Đây là nội dung test."},
                {"Lê Văn A@", "Công ty TNHH C", "b@example.com", "","Đây là nội dung test."},
                // Thêm dữ liệu khác nếu cần
        };
    }    private void fillContactForm(String name, String businessName, String businessEmail, String phone, String message) {
        // Điền thông tin vào các trường của form liên hệ
        driver.findElement(By.xpath("//*[@id=\"form-field-Fist_name\"]")).sendKeys(name);
        driver.findElement(By.xpath("//*[@id=\"form-field-last_name\"]")).sendKeys(businessName);
        driver.findElement(By.xpath("//*[@id=\"form-field-email\"]")).sendKeys(businessEmail);
        driver.findElement(By.xpath("//*[@id=\"form-field-phone_number\"]")).sendKeys(phone);


        // Điền thông tin vào ô yêu cầu
        driver.findElement(By.xpath("//*[@id=\"form-field-field_f2bec3c\"]")).sendKeys(message);

        // Tích vào ô checkbox
        WebElement checkbox = driver.findElement(By.xpath("//*[@id=\"form-field-Nhan_tin-0\"]"));
        if (!checkbox.isSelected()) {
            checkbox.click();
        }
    }

    private void handleErrorMessage() {
        // Kiểm tra và xử lý thông báo lỗi nếu có
        String requiredFieldErrorMessageLocator = "//div[contains(@class,'elementor-message-danger') and contains(text(),'Your submission failed because of an error.')]";

        try {
            WebDriverWait wait = new WebDriverWait(driver, 5);
            wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(requiredFieldErrorMessageLocator)));

        } catch (Exception e) {
            // Không có thông báo lỗi về việc thiếu trường bắt buộc, kiểm tra các trường hợp khác nếu cần
        }
    }
    @AfterClass
    public static void tearDown() throws InterruptedException {
        // Đóng trình duyệt sau khi test
        Thread.sleep(5000);
        if (driver != null) {
            driver.quit();
        }
    }
}
