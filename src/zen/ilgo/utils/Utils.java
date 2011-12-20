package zen.ilgo.utils;

public class Utils {

    public static String getUtf16(String aboveFFFF) {
        
        String result;
        int v = Integer.parseInt(aboveFFFF, 16);
        
        if (v > 0xFFFF) {            
            int v2 = v - 0x10000;
            
            int vh = (v2 & 0xFFC00) >> 10;
            int vl = v2 & 0x3FF;
            
            int w1 = 0xD800;
            int w2 = 0xDC00;
            
            w1 = w1 | vh;
            w2 = w2 | vl;
            
            result = String.format("\\u%s\\u%s", Integer.toHexString(w1), Integer.toHexString(w2));
       
        } else {
            result = "\\u" + aboveFFFF.toLowerCase();
        }        
        return result;
    }
}
