package com.example.demo.service;

import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;

@Service
public class AssinaturaService {
    public String calcularHash(String caminhoArquivo) throws Exception {
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

    public void assinarArquivo(String caminhoArquivo, String caminhoArquivoAssinado, String hash) throws Exception {
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

    public boolean verificarAssinatura(String caminhoArquivoAssinado, String hashAComparar) throws Exception {
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
        byte[] hashCalculadoBytes = hexStringToByteArray(hashAComparar);

        // Compara os hashes
        return MessageDigest.isEqual(hashNoArquivo, hashCalculadoBytes);
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
}
