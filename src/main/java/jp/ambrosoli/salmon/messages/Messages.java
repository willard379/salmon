package jp.ambrosoli.salmon.messages;

import java.text.MessageFormat;
import java.util.ResourceBundle;

public class Messages {

    private static final ResourceBundle resource = ResourceBundle.getBundle(getBaseName());

    public static String getString(final String key) {
        return resource.getString(key);
    }

    public static String getString(final String key, final Object... args) {
        String message = resource.getString(key);
        return MessageFormat.format(message, args);
    }

    private static String getBaseName() {
        return Messages.class.getPackage().getName() + ".messages"; //$NON-NLS-1$
    }
}
