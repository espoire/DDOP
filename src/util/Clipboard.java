package util;

import java.awt.*;
import java.awt.datatransfer.StringSelection;

public class Clipboard {
    /** Copies the .toString() of the provided object to the system keyboard. */
    public static void copy(Object toCopy) {
        String text = toCopy.toString();

        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(
                new StringSelection(text),
                null
        );

        System.out.println("Copied text to clipboard! Length: " + text.length());
    }
}
