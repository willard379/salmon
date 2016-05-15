package jp.ambrosoli.salmon.utils;

/**
 * <p>
 * 文字列に関するユーティリティクラス
 * </p>
 *
 * @author willard379
 * @since 0.1.0
 *
 */
public class StringUtil {

    /**
     * <p>
     * 文字列がnullまたは空文字かを判定します。
     * </p>
     *
     * @param value
     *            チェックする文字列
     * @return valueがnullまたは空文字の場合{@code true}、それ以外の場合{@code false}
     */
    public static boolean isEmpty(final String value) {
        return value == null || value.isEmpty();
    }

    /**
     * <p>
     * 文字列がnullでも空文字でもないかを判定します。
     * </p>
     *
     * @param value
     *            チェックする文字列
     * @return valueがnullでも空文字でもない場合{@code true}、それ以外の場合{@code false}
     */
    public static boolean isNotEmpty(final String value) {
        return !isEmpty(value);
    }

    /**
     * <p>
     * 文字列valueの末尾にCR、LF、CRLFがある場合、それを削除した文字列を返します。
     * 文字列valueが連続したCR、LF、CRLFで終わる場合、末尾の1つのみ削除します。
     * </p>
     *
     * @param value
     *            対象の文字列
     * @return 末尾のCR、LF、CRLFを削除した文字列
     */
    public static String chomp(final String value) {
        if (value == null) {
            return value;
        }
        return value.replaceFirst("([\r\n]|\r\n)$", ""); //$NON-NLS-1$ //$NON-NLS-2$
    }

}
