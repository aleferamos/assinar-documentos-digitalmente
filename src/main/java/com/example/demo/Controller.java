package com.example.demo;

import com.example.demo.service.AssinaturaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/assinatura")
public class Controller {

    @Autowired
    private AssinaturaService assinaturaService;

    @PostMapping("/assinar")
    ResponseEntity<Void> assinar() {
        try {
            // Caminho para o arquivo a ser assinado
            String arquivoParaAssinar = "C:/Users/Alefe Patrick/Downloads/nota fiscal 11-2023.pdf";

            // Caminho para o arquivo assinado
            String arquivoAssinado = "C:/Users/Alefe Patrick/Downloads/nota fiscal 11-2023_assinado.pdf";

            // Calcule o hash do arquivo
            String hash = assinaturaService.calcularHash(arquivoParaAssinar);

            // Grave o hash no arquivo assinado
            assinaturaService.assinarArquivo(arquivoParaAssinar, arquivoAssinado, hash);

            System.out.println("Assinatura simples concluída com sucesso.");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/validar")
    ResponseEntity<Void> validarAssinatura() throws Exception {
        // Caminho para o arquivo assinado
        String arquivoAssinado = "C:/Users/Alefe Patrick/Downloads/nota fiscal 11-2023_assinado.pdf";

        // Realize a verificação da assinatura
        if (assinaturaService.verificarAssinatura(arquivoAssinado,
                "dcc4baa12bc122523b12c5018ec1ecb7e4434de36ffe8b11a563e343fe9f7c1e")) {
            System.out.println("Assinatura válida. O arquivo não foi modificado.");
        } else {
            System.out.println("Assinatura inválida. O arquivo foi modificado ou não foi assinado.");
        }

        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }
}
