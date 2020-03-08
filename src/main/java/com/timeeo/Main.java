package com.timeeo;

import com.timeeo.api.Searcher;

import javax.swing.*;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

public class Main {

    public static void main(String[] args) {
        String username = "";
        String password = "";
        String hashtag = "";

        while (username == null || username.isEmpty())
            username = JOptionPane.showInputDialog(null, "Veuillez saisir votre nom d'utilisateur Instagram ou Facebook.", "Nom d'utilisateur");

        JPasswordField passwordField = new JPasswordField();

        while (password.isEmpty()) {
            JOptionPane.showConfirmDialog(null, passwordField, "Veuillez saisir votre mot de passe.", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
            password = new String(passwordField.getPassword());
        }

        while (hashtag == null || hashtag.isEmpty())
            hashtag = JOptionPane.showInputDialog(null, "Veuillez saisir un hashtag à rechercher.", "Hashtag");

        if (hashtag.startsWith("#")) hashtag = hashtag.substring(1);

        try {
            Searcher s = new Searcher(username, password);
            Map<String, Map<String, Integer>> tagsMap = s.searchTags(hashtag);

            if (tagsMap.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Aucun message trouvé... API saturée, ou aucun tag de ce type.", "Fini !", JOptionPane.ERROR_MESSAGE);
                return;
            }

            PrintWriter printer = new PrintWriter(new FileWriter("output.txt", false));

            for (String tag : tagsMap.keySet()) {
                printer.printf("%s%n", tag);

                Map<String, Integer> submap = tagsMap.get(tag);

                for (String location : submap.keySet())
                    printer.printf("\t%s : %d%n", location, submap.get(location));

                printer.println();
            }

            printer.close();

            JOptionPane.showMessageDialog(null, "Fini ! Données écrite dans output.txt", "Fini !", JOptionPane.INFORMATION_MESSAGE);

        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showConfirmDialog(null, e.getMessage(), "Erreur...", JOptionPane.OK_OPTION, JOptionPane.ERROR_MESSAGE);
        }
    }
}
