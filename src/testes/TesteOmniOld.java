package testes;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.util.ArrayList;
import java.util.List;

public class TesteOmniOld {
    public static void main(String[] args){
        TesteBase.setupDriver();
        TesteBase.init();

        System.out.println("*** Teste Exploratório de Acesso e navegação entre as páginas do Omni Old ***");
        TesteBase.driver.get("https://omni.mozaik.cloud/#/");
        TesteBase.driver.findElement(By.cssSelector("button.btn-microsoft-content")).click();

        TesteBase.aguardaThread(5000);
        ArrayList<String> abas = new ArrayList<> (TesteBase.driver.getWindowHandles());
        TesteBase.driver.switchTo().window(abas.get(1));

        System.out.println("- Realizando acesso por conta Microsoft (não ter que lidar com o ReCaptcha)..");
        WebElement campoEmail = TesteBase.driver.findElement(By.cssSelector("#i0116"));
        WebElement btnAvancar = TesteBase.driver.findElement(By.cssSelector("#idSIButton9"));
        campoEmail.sendKeys("testesomni@outlook.com");
        btnAvancar.click();
        TesteBase.aguardaThread(3000);
        WebElement campoSenha = TesteBase.driver.findElement(By.cssSelector("#i0118"));
        btnAvancar = TesteBase.driver.findElement(By.cssSelector("#idSIButton9"));
        campoSenha.sendKeys("OmniTest1!");
        btnAvancar.click();

        TesteBase.aguardaThread(5000);
        TesteBase.driver.switchTo().window(abas.get(0));

        System.out.println("- No menu Atendimentos, verificando atendimentos..");
        List<WebElement> menus = TesteBase.driver.findElements(By.cssSelector("div.side-menu ul li"));
        menus.get(0).click();
        WebElement abaAtendidos = TesteBase.driver.findElement(By.cssSelector("div.control-tab div.past"));
        abaAtendidos.click();
        TesteBase.aguardaThread(2000);
        List<WebElement> listaContatos =
                TesteBase.driver.findElements(By.cssSelector("div.contacts-list li.grid-list"));
        System.out.println("- Ao clicar nos atendimentos concluídos," +
                " foram exibidos " + listaContatos.size() + " contatos..");

        abaAtendidos.click();
        TesteBase.aguardaThread(2000);
        listaContatos = TesteBase.driver.findElements(By.cssSelector("div.contacts-list li.grid-list"));
        System.out.println("- Ao clicar novamente nos atendimentos concluídos," +
                " foram exibidos " + listaContatos.size() + " contatos..");

        menus = TesteBase.driver.findElements(By.cssSelector("div.side-menu ul li"));
        menus.get(1).click();
        System.out.println("- No menu Omni, verificando intervalo entre 01/01/2021 a 31/01/2021...");
        TesteBase.aguardaThread(2000);
        WebElement calendario = TesteBase.driver.findElement(By.cssSelector("div.info-picker"));
        calendario.click();
        TesteBase.aguardaThread(1000);

        Select selecionaMes = new Select(TesteBase.driver.findElement(By.cssSelector("select.custom-select")));
        selecionaMes.selectByIndex(0); // Janeiro
        TesteBase.aguardaThread(2000);

        List<WebElement> diasMes = TesteBase.$("div.ngb-dp-day", null, true);
        diasMes.get(4).click(); // 1° de janeiro
        diasMes.get(34).click(); // 31° de janeiro

        TesteBase.driver.findElement(By.cssSelector("div.title")).click(); // Placeholder para clicar no titulo
        TesteBase.aguardaThread(5000); // Sem validação.. apenas observa
        System.out.println("- Sem validação!");

        menus.get(2).click();
        System.out.println("- No menu Dash, ir para aba de Atendimentos..");
        TesteBase.aguardaThread(500);
        List<WebElement> itensSubMenu = TesteBase.$("div.floating-menu ul li", null, true);
        itensSubMenu.get(0).click();
        TesteBase.aguardaThread(7000);

        System.out.println("- Saindo do sistema pelo menuzinho do Perfil..");
        WebElement avatarLogado = TesteBase.$("div.profile-menu");
        avatarLogado.click();
        TesteBase.aguardaThread(1000);

        WebElement btnDeslogar = TesteBase.$("div.signout i.fa");
        btnDeslogar.click();
        TesteBase.aguardaThread(4000);

        TesteBase.finalizaDriver();
    }
}
