package cse.oop2.crms;

//import cse.oop2.crms.ui.MainFrame;

import cse.oop2.crms.view.MainFrame;

public class CRMS {

    public static void main(String[] args) {

        javax.swing.SwingUtilities.invokeLater(() -> {
            new MainFrame();
        });
    }
}