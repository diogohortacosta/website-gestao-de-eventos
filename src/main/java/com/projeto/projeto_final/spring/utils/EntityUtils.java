package com.projeto.projeto_final.spring.utils;

import java.io.*;

public class EntityUtils {
    // Método para clonar um objeto usando serialização
    public static <T> T clone(T object) {
        try {
            // Cria um ByteArrayOutputStream para escrever o objeto
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(byteOut);
            out.writeObject(object); // Escreve o objeto no stream

            // Cria um ByteArrayInputStream para ler o objeto
            ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
            ObjectInputStream in = new ObjectInputStream(byteIn);

            @SuppressWarnings("unchecked") // Suprime o aviso de cast não verificado
            T clonedObject = (T) in.readObject(); // Lê e retorna o objeto clonado

            return clonedObject;
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Failed to clone object", e);
        }
    }
}
