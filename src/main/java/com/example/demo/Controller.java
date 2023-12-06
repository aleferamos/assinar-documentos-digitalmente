package com.example.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@RestController
@RequestMapping("/test")
public class Controller {

    @GetMapping
    public void test() {
        try {
            // Caminho para o arquivo a ser assinado
            String arquivoParaAssinar = "C:/Users/Alefe Patrick/Downloads/nota fiscal 11-2023.pdf";

            // Caminho para o arquivo assinado
            String arquivoAssinado = "C:/Users/Alefe Patrick/Downloads/nota fiscal 11-2023_assinado.pdf";

            // Calcule o hash do arquivo
            String hash = calcularHash(arquivoParaAssinar);

            // Grave o hash no arquivo assinado
            assinarArquivo(arquivoParaAssinar, arquivoAssinado, hash);

            System.out.println("Assinatura simples concluída com sucesso.");
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    private static void assinarArquivo(String caminhoArquivo, String caminhoArquivoAssinado, String hash) throws Exception {
        // Leitura do conteúdo do arquivo original
        byte[] arquivoOriginal = lerArquivo(caminhoArquivo);

        // Concatene o conteúdo original com o hash convertido para bytes
        byte[] arquivoAssinado = concatenarBytes(arquivoOriginal, hexStringToByteArray(hash));

        // Grave o conteúdo assinado no arquivo
        gravarArquivo(caminhoArquivoAssinado, arquivoAssinado);
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

    private static void gravarArquivo(String caminhoArquivo, byte[] conteudo) throws IOException {
        FileOutputStream fos = new FileOutputStream(caminhoArquivo);
        fos.write(conteudo);
        fos.close();
    }

    private static byte[] concatenarBytes(byte[] array1, byte[] array2) {
        byte[] resultado = new byte[array1.length + array2.length];
        System.arraycopy(array1, 0, resultado, 0, array1.length);
        System.arraycopy(array2, 0, resultado, array1.length, array2.length);
        return resultado;
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
