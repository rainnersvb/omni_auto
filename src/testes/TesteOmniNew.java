package testes;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

public class TesteOmniNew extends TesteBase{
    public static void main(String[] args){
        setupDriver();
        init();
        try{
            System.out.println("*** Teste Exploratório de Acesso e navegação entre as páginas do Omni New ***");
            TesteBase.driver.manage().window().setSize(new Dimension(1300, 1020));
            //TesteBase.driver.get("https://code7.mozaik.cloud/");
            TesteBase.driver.get("https://homologcode7.mozaik.cloud/"); // Testando no Homolog com o ajuste do William

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

            System.out.println("- Acessando tela de Atendimentos..");
            clicaBtnContainerSuperior(1);

            TesteBase.espere(By.cssSelector("div.load-more")).click();

            System.out.println("- Acessando página da Boteria para iniciar o chamado..");
            TesteBase.executeJavaScript("window.open('about:blank','_blank');"); // Abre nova guia
            abas = new ArrayList<> (TesteBase.driver.getWindowHandles());
            TesteBase.driver.switchTo().window(abas.get(1));
            // Link para Apresentação RCA - Patty falou que está okay usar
            TesteBase.driver.get("https://app.boteria.com.br/webchat/60a6ba99f0ba2300111724f6");
            TesteBase.aguardaThread(5000); // Tempo para receber o atendimento
            TesteBase.driver.switchTo().window(abas.get(0));

            print("- Identificando novo atendimento recebido..");
            aguardaThread(5000);
            int cont = 0;
            List<WebElement> listaAtendimentos = driver.findElements(By.cssSelector("attendance"));
            while (listaAtendimentos.size() == 0 || cont < 3){
                aguardaThread(5000);
                cont++;
                listaAtendimentos = driver.findElements(By.cssSelector("attendance"));
            }
            listaAtendimentos.get(0).click();
            waitForHide("div.loading", 10);

            print("- Protocolo de atendimento atual: " + retornaProtocoloAtendimentoAtual());
            if ($("contact .info").getText().contains("O contato selecionado ainda não foi identificado." +
                    " Você pode associá-lo a um contato existente")){
                print("- Contato não identificado..");
            }
            print("- Testando série de mensagens, shortcuts e emojis..");
            comandoEnvio("Testes automatizados: Olá, mensagem enviada manualmente!");
            clickAcoes(0);

            enviaShortcut(0);
            enviaShortcut(2);

            insereEmoji(0, 0);
            insereEmoji(0,8);
            clickAcoes(0);

            comandoEnvio("Estou no aguardo da resposta!");
            clickAcoes(0);

            driver.switchTo().window(abas.get(1));

            if (!validaRespostaBoteria("Testes automatizados: Olá, mensagem enviada manualmente!") ||
                    !validaRespostaBoteria("Olá, seja bem vindo a integração do Code7 Omni com SalesForce,"
                            + " com essa integração sua equipe ficará com uma ótima ferramenta para trabalho!") ||
                    !validaRespostaBoteria("Estamos conversando em uma integração do Code7 Omni" +
                            " e SALES FORCE mas se preferir, posso te ligar também.") ||
                    !validaRespostaBoteria("\uD83D\uDE00") ||
                    !validaRespostaBoteria("\uD83D\uDE42") ||
                    !validaRespostaBoteria("Estou no aguardo da resposta!")){
                print("- Mensagens recebidas na Boteria não foram validadas corretamente!");
            }else print("- Mensagens recebidas na Boteria validadas!\n- Testando resposta via cliente..");
            insereMensagemBoteria("Olá, obrigado por me responder, gostaria de saber como testar os testes!");
            clicaEnviarMensagemBoteria();
            aguardaThread(3000);

            driver.switchTo().window(abas.get(0));

            if (!validaRespostaOmni("Olá, obrigado por me responder," +
                    " gostaria de saber como testar os testes!")){
                print("- Mensagem recebida no Omni não foi validada corretamente!");
            }else print("- Mensagem recebida no Omni validada!");
            print("- Testando tag para finalizar atendimento..");
            clickAcoes(3);
            selecionaTagFinalizaAtend(0);
            sairOmni();
            finalizaDriver();
            print("- Teste finalizado!");
        }catch (Exception e){
            finalizaDriver();
            e.printStackTrace();
            print("- O teste falhou!");
        }
    }
}
