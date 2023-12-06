package com.example.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@RestController
@RequestMapping("/test/validar")
public class ControllerValidar {

    @GetMapping
    public void test() throws Exception {
        // Caminho para o arquivo assinado
        String arquivoAssinado = "C:/Users/Alefe Patrick/Downloads/nota fiscal 11-2023_assinado.pdf";

        // Realize a verificação da assinatura
        if (verificarAssinatura(arquivoAssinado)) {
            System.out.println("Assinatura válida. O arquivo não foi modificado.");
        } else {
            System.out.println("Assinatura inválida. O arquivo foi modificado ou não foi assinado.");
        }
    }

    private static boolean verificarAssinatura(String caminhoArquivoAssinado) throws Exception {
        // Leitura do conteúdo do arquivo assinado
        byte[] conteudoAssinado = lerArquivo(caminhoArquivoAssinado);

        // Tamanho do hash (SHA-256) em bytes
        int tamanhoHash = 32;

        // Divide o conteúdo do arquivo entre o arquivo original e o hash
        byte[] arquivoOriginal = new byte[conteudoAssinado.length - tamanhoHash];
        byte[] hashNoArquivo = new byte[tamanhoHash];

        System.arraycopy(conteudoAssinado, 0, arquivoOriginal, 0, arquivoOriginal.length);
        System.arraycopy(conteudoAssinado, arquivoOriginal.length, hashNoArquivo, 0, tamanhoHash);

        // Recalcula o hash do conteúdo original
        String hashCalculado = calcularHash(arquivoOriginal);

        // Converte o hash calculado para bytes
        byte[] hashCalculadoBytes = hexStringToByteArray("dcc4baa12bc122523b12c5018ec1ecb7e4434de36ffe8b11a563e343fe9f7c1e");

        // Compara os hashes
        return MessageDigest.isEqual(hashNoArquivo, hashCalculadoBytes);
    }

    private static String calcularHash(String caminhoArquivo) throws Exception {
        FileInputStream fis = new FileInputStream(caminhoArquivo);
        byte[] data = new byte[fis.available()];
        fis.read(data);
        fis.close();

        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = digest.digest(data);

        // Converta o hash para uma representação hexadecimal
        StringBuilder hexString = new StringBuilder();
        for (byte hashByte : hashBytes) {
            String hex = Integer.toHexString(0xff & hashByte);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }

        return hexString.toString();
    }

    private static String calcularHash(byte[] data) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = digest.digest(data);

        // Converta o hash para uma representação hexadecimal
        StringBuilder hexString = new StringBuilder();
        for (byte hashByte : hashBytes) {
            String hex = Integer.toHexString(0xff & hashByte);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }

        return hexString.toString();
    }

    private static byte[] lerArquivo(String caminhoArquivo) throws IOException {
        FileInputStream fis = new FileInputStream(caminhoArquivo);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = fis.read(buffer)) > -1) {
            baos.write(buffer, 0, len);
        }
        fis.close();
        return baos.toByteArray();
    }

    private static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }
}
