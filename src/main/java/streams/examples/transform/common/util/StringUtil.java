package streams.examples.transform.common.util;

import java.text.Normalizer;
public abstract class StringUtil {
    public static String regex(String src){
        return Normalizer.normalize(src, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
    }
}
