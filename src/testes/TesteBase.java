package testes;

import com.google.common.base.Function;
import io.github.bonigarcia.wdm.WebDriverManager;
import io.github.bonigarcia.wdm.config.WebDriverManagerException;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.*;

public class TesteBase {
    public static WebDriver driver = null;

    // Organiza as preferências de inicialização do ChromeDriver, coloquei algumas genéricas.
    // Os testes podem ser realizados sem interface gráfica (ideal) caso preferir, a linha foi comentada
    public static void init() {
        Map<String, Object> prefs = new HashMap<String, Object>();
        prefs.put("profile.default_content_setting_values.notifications", 2);
        prefs.put("credentials_enable_service", false);
        prefs.put("password_manager_enabled", false);
        ChromeOptions options = new ChromeOptions();
        options.setExperimentalOption("prefs", prefs);
        options.setExperimentalOption("excludeSwitches", Collections.singletonList("enable-automation"));
        options.addArguments("--disable-notifications", "--disable-infobars","--no-sandbox");
        options.addArguments("allow-file-access-from-files");
        options.addArguments("use-fake-device-for-media-stream");
        options.addArguments("use-fake-ui-for-media-stream");
        //options.addArguments("headless"); // Descomentar caso prefira os testes sem interface gráfica
        TesteBase.driver = new ChromeDriver(options);
    }

    public static void finalizaDriver() {
        driver.quit();
        // Report, finaliza etc adicionar aqui
    }

    // Utiliza método para organizar o WebDriver utilizado. Serve para todos os navegadores.
    public static void setupDriver() {
        try{
            WebDriverManager.globalConfig().setTimeout(5);
            WebDriverManager.chromedriver().setup();
        }catch (WebDriverManagerException e){
            System.out.println("Ocorreu um problema ao gerenciar o ChromeDriver!");
        }
    }

    // Função para que o WebDriver fique buscando pela visibilidade de um seletor.
    public static WebElement esperaVisibilidade(String seletor){
        boolean elementoVisivel = false;
        while (!elementoVisivel){
            try{
                if (driver.findElement(By.cssSelector(seletor)).isDisplayed()){
                    elementoVisivel = true;
                }
            }catch (Exception e){} // Faça nada com a exceção, continaur buscando no laço
        }
        return driver.findElement(By.cssSelector(seletor));
    }
    public static WebElement $(String selector) {
        WebElement elemento = null;
        if (selector.contains("#") && !selector.contains(" ") && !selector.contains(".") && !selector.contains(">")) {
            selector = selector.replace("#", "");
            elemento = driver.findElement(By.id(selector));
        } else {
            elemento = driver.findElement(By.cssSelector(selector));
        }
        return elemento;
    }

    // Função para filtrar elementos de uma lista por visibilidade
    public static List<WebElement> $(String seletor, WebElement pattern, boolean visivel) {  //
        List<WebElement> retorno = new ArrayList<WebElement>();
        List<WebElement> list = $(seletor, pattern);
        if (visivel) {
            for (WebElement element : list) {
                if ((!element.getCssValue("display").equals("none")
                        && !element.getAttribute("class").contains("oculto")
                        && element.isDisplayed())) {
                    retorno.add(element);
                }
            }
        }
        return (visivel) ? retorno : list;
    }

    // Função para selecionar elementos numa lista
    public static List<WebElement> $(String seletor, WebElement patern) {
        if (patern != null) {
            return patern.findElements(By.cssSelector(seletor));
        }
        return driver.findElements(By.cssSelector(seletor));
    }

    // Função para aguardar dinamicamente pelo carregamento da página
    public static WebElement espere(final By locator) {
        Wait<WebDriver> wait = new FluentWait<>(driver)
                .withTimeout(java.time.Duration.ofSeconds(15)) // 15 s limite

                .pollingEvery(java.time.Duration.ofSeconds(1)) // tentando a cada 1s

                .ignoring(org.openqa.selenium.ElementNotVisibleException.class) // mesmo se não estiver visível

                .ignoring(org.openqa.selenium.NoSuchElementException.class); // mesmo se não estiver listado
        WebElement foo = wait.until(
                new Function<WebDriver, WebElement>() {
                    public WebElement apply(WebDriver driver) {
                        return driver.findElement(locator);
                    }
                }
        );
        return foo;
    }

    public static void aguardaThread(int ms) {
        try{
            Thread.sleep(ms);
        }catch (Exception e){}
    }

    public static Object executeJavaScript(String comando) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        return js.executeScript(comando);
    }

    public static boolean waitForClick(String element, int tempo) {
        boolean retorno;
        try{
            WebDriverWait wait = new WebDriverWait(driver, java.time.Duration.ofSeconds(tempo));
            wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(element)));
            retorno = true;
        }catch (Exception e){
            retorno = false;
        }
        return retorno;
    }

    public static boolean waitForHide(String element, int tempo) {
        boolean retorno = true, wait = true;
        int cont = 0;
        while (wait && cont <= tempo) {
            try {
                if ($("" + element + "").isDisplayed()) {
                    wait = true;
                    if (cont % 10 == 1) { // a cada 10s
                        System.out.println("Checagem por elemento seletor " + element + ", esperando parar de ser exibido na tela...");
                    }
                    Thread.sleep(1000);
                    cont++;
                } else{
                    retorno = false;
                    break;
                }
            } catch (Exception e) {
                retorno = false;
                wait = false;
            }
        }
        return retorno;
    }

    public static void print(String mensagem){
        System.out.println(mensagem);
    }

    public static String retornaProtocoloAtendimentoAtual(){
        String retorno = null;
        WebElement elementProt;
        try{
            elementProt = driver.findElement(By.cssSelector("chat div.contact-chat p b"));
        }catch (Exception e){
            return retorno;
        }
        return retorno;
    }

    // 0 a 3, Enviar, Anexo, Emoji, Tag
    public static void clickAcoes(int acao){
        WebElement btnAcao = $("div.smart-box li a", null).get(acao);
        btnAcao.click();
        aguardaThread(1000);
    }

    public static void comandoEnvio(String comando){
        WebElement campoMensagem = $("#message");
        campoMensagem.click();
        campoMensagem.sendKeys(comando);
    }

    public static void limpaEnvio(){
        WebElement campoMensagem = $("#message");
        campoMensagem.clear();
    }

    // Ordem do atalho.
    public static void enviaShortcut(int shortcut){
        comandoEnvio("/");
        aguardaThread(5000); // Melhorar espera
        List<WebElement> mensagens = $("div.list-box ul li", null);
        mensagens.get(shortcut).click();
        clickAcoes(0);
    }

    // Não feito ainda
    public static void enviaAnexo(){

    }

    // Categoria - Sorrisos, pessoas, animais etc
    public static void insereEmoji(int categoria, int emoji){
        clickAcoes(2);
        espere(By.cssSelector("div.emojis"));
        List<WebElement> listaCategoria = $("ul.emoji-categories a", null);
        listaCategoria.get(categoria).click();
        aguardaThread(1000);
        List<WebElement> listaEmoji = $("ul.emoji-list a", null);
        listaEmoji.get(emoji).click();
        clickAcoes(2);
    }

    public static void selecionaTagFinalizaAtend(int tag){
        espere(By.cssSelector("div.tags"));
        List<WebElement> listaTags = $("div.tags ul li", null);
        listaTags.get(tag).click();
        waitForHide("div.end-attendance", 10);
        aguardaThread(5000);
    }

    public static void clicaBtnContainerSuperior(int btn){
        WebDriverWait wait = new WebDriverWait(driver, java.time.Duration.ofSeconds(10));
        wait.until(ExpectedConditions.
                elementToBeClickable(driver.findElements(By.cssSelector("div.container ul li.nav-item")).get(btn)));
        List<WebElement> btnsContainer = TesteBase.$("div.container ul li.nav-item", null);
        btnsContainer.get(btn).click();
    }

    public static void sairOmni(){
        clicaBtnContainerSuperior(3);
        espere(By.cssSelector("menu-profile"));
        List<WebElement> opcoesMenuProfile = $("menu-profile div.info ul li a", null);
        opcoesMenuProfile.get(1).click();
        aguardaThread(2000);
        espere(By.cssSelector("re-captcha")); // Aguarda pela tela de login
    }

    public static boolean validaRespostaOmni(String respostaEsperada){
        boolean msgValidada = false;
        List<WebElement> listaMensagens = $("message-box div.message", null);
        for (int i = 0; i < listaMensagens.size(); i++){
            if (listaMensagens.get(i).getText().contains(respostaEsperada)){
                msgValidada = true;
                break;
            }
        }
        return msgValidada;
    }

    public static boolean validaRespostaBoteria(String respostaEsperada){
        boolean msgValidada = false;
        List<WebElement> listaMensagens = $("#webchat-body-chat-messages ul li div p", null);
        for (int i = 0; i < listaMensagens.size(); i++){
            if (listaMensagens.get(i).getText().contains(respostaEsperada)){
                msgValidada = true;
                break;
            }
        }
        return msgValidada;
    }

    public static void insereMensagemBoteria(String mensagem){
        espere(By.cssSelector("input.webchat-input-quickaccess-disable"));
        $("input.webchat-input-quickaccess-disable").sendKeys(mensagem);
    }

    public static void clicaEnviarMensagemBoteria(){
        espere(By.cssSelector(".webchat-body-chat-footer-img-send"));
        $(".webchat-body-chat-footer-img-send").click();
    }
}