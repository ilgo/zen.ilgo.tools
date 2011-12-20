package zen.ilgo.utils;

import java.util.regex.Pattern;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import zen.ilgo.utils.MyStringBuilder;

public class MyStringBuilderTest extends TestCase {

    static MyStringBuilder instance;
    static MyStringBuilder instance2;
    static String text = "asd_1_qwa_1_mnb_1_zxc_1_poi";
    static String newLineText = "asd_1_qwa_1_m\nnb_1_zxc_1_poi";

    @Before
    public void setUp() throws Exception {
        instance = new MyStringBuilder(text);
        instance2 = new MyStringBuilder(newLineText);
    }

    @Test
    public void testReplaceFirst() {
        System.out.println(instance.toString());
        instance.replaceFirst("_1_", "_XXX_");
        System.out.println(instance.toString());
        System.out.println("-----");
        assertEquals(instance.toString(), "asd_XXX_qwa_1_mnb_1_zxc_1_poi");
    }

    @Test
    public void testReplaceLast() {
        System.out.println(instance.toString());
        instance.replaceLast("_1_", "_XXX_");
        System.out.println(instance.toString());
        System.out.println("-----");
        assertEquals(instance.toString(), "asd_1_qwa_1_mnb_1_zxc_XXX_poi");
    }

    @Test
    public void testReplaceAll() {
        System.out.println(instance.toString());
        instance.replaceAll("_1_", "_XXX_");
        System.out.println(instance.toString());
        System.out.println("-----");
        assertEquals(instance.toString(), "asd_XXX_qwa_XXX_mnb_XXX_zxc_XXX_poi");
    }

    @Test
    public void testReplaceFirstRegex() {
        System.out.println(instance.toString());
        instance.replaceFirstRegex("_\\d_", "_XXX_");
        System.out.println(instance.toString());
        System.out.println("-----");
        assertEquals(instance.toString(), "asd_XXX_qwa_1_mnb_1_zxc_1_poi");
    }

    @Test
    public void testReplaceLastRegex() {
        System.out.println(instance.toString());
        instance.replaceLastRegex("_\\d_", "_XXX_");
        System.out.println(instance.toString());
        System.out.println("-----");
        assertEquals(instance.toString(), "asd_1_qwa_1_mnb_1_zxc_XXX_poi");
    }

    @Test
    public void testReplaceAllRegex() {
        System.out.println(instance.toString());
        instance.replaceAllRegex("_\\d_", "_XXX_");
        System.out.println(instance.toString());
        System.out.println("-----");
        assertEquals(instance.toString(), "asd_XXX_qwa_XXX_mnb_XXX_zxc_XXX_poi");
    }
    
    @Test
    public void testReplaceFirstRegexFlags() {
        System.out.println(instance2.toString());
        instance2.replaceFirstRegex("_\\d_", "_XXX_", Pattern.MULTILINE);
        System.out.println(instance2.toString());
        System.out.println("-----");
        assertEquals(instance2.toString(), "asd_XXX_qwa_1_m\nnb_1_zxc_1_poi");
    }

    @Test
    public void testReplaceLastRegexFlags() {
        System.out.println(instance2.toString());
        instance2.replaceLastRegex("_\\d_", "_XXX_", Pattern.MULTILINE);
        System.out.println(instance2.toString());
        System.out.println("-----");
        assertEquals(instance2.toString(), "asd_1_qwa_1_m\nnb_1_zxc_XXX_poi");
    }

    @Test
    public void testReplaceAllRegexFlags() {
        System.out.println(instance2.toString());
        instance2.replaceAllRegex("_\\d_", "_XXX_", Pattern.MULTILINE);
        System.out.println(instance2.toString());
        System.out.println("-----");
        assertEquals(instance2.toString(), "asd_XXX_qwa_XXX_m\nnb_XXX_zxc_XXX_poi");
    }

}
