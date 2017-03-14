/**
 * CTrans - A constraint translator
 * <p>
 * The MIT License (MIT)
 * <p>
 * Copyright (c) 2017 Julian Thome <julian.thome.de@gmail.com>
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 **/

import com.julianthome.ctrans.CTrans;
import com.julianthome.ctrans.TranslationTarget;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snt.inmemantlr.tree.Ast;

public class TestTranslation {


    final static Logger LOGGER = LoggerFactory.getLogger(TestTranslation.class);


    String gfile = getClass().getResource("Logic.g4")
            .getFile();


	@Test
    public void testTranslation0() {

        String formula = "(((X1 and X2) or (X3 and X4)) or X5)(((X6 and X7) or (X8 " +
                "and X9)" +
                ") or X10)";

        CTrans d = new CTrans(gfile, TranslationTarget.DNF);

        d.translate(formula);
        System.out.println();
    }


	@Test
    public void GhanemOneClause() {

        String formula = "XXXXXXXXXXXXX1";

        CTrans d = new CTrans(gfile, TranslationTarget.DNF);

        d.translate(formula);
        System.out.println();
    }
	
	@Test
    public void JulianNegatedClause() {

        String formula = "not XXXXXXXXXXXXX1";

        CTrans d = new CTrans(gfile, TranslationTarget.DNF);

        d.translate(formula);
        System.out.println();
    }
	
    @Test
    public void testConj0() {
        String formula = "(A or B) and (C and D)"; //"A and C or B and C"
        CTrans d = new CTrans(gfile, TranslationTarget.DNF);
        d.translate(formula);
        System.out.println();
    }

    @Test
    public void testImplication() {
        String formula = "(a implies d)";
        CTrans d = new CTrans(gfile, TranslationTarget.DNF);
        d.translate(formula);
        System.out.println();
    }

    @Test
    public void testNegation0() {
        String formula = "not not not not a";
        CTrans d = new CTrans(gfile, TranslationTarget.DNF);
        d.translate(formula);
        System.out.println();
    }

    @Test
    public void testNegation1() {
        String formula = "not (a and not b)";
        CTrans d = new CTrans(gfile, TranslationTarget.DNF);
        d.translate(formula);
        System.out.println();
    }

    @Test
    public void testNegation2() {
        String formula = "not (a or b)";
        CTrans d = new CTrans(gfile, TranslationTarget.DNF);
        d.translate(formula);
        System.out.println();
    }

    @Test
    public void testXor() {
        String formula = "a xor b";
        CTrans d = new CTrans(gfile, TranslationTarget.DNF);
        Ast out = d.translate(formula);
        System.out.println();
    }
    
    @Test
    public void testImpl() {
        String formula = "(a and c) implies b";
        CTrans d = new CTrans(gfile, TranslationTarget.DNF);
        Ast out = d.translate(formula);
        System.out.println();
    }

    @Test
    public void testBig(){
        String s = "(( q ) and (( XXXXXXXXXXXXXXXXXXXXXX130  and  XXXXXXXXXXXXXXXXXXXXXX131 ) or " +
                "( XXXXXXXXXXXXXXXXXXXXXX132  and  XXXXXXXXXXXXXXXXXXXXXX133 ) or ( XXXXXXXXXXXXXXXXXXXXXX134  and  XXXXXXXXXXXXXXXXXXXXXX135 ) or " +
                "( XXXXXXXXXXXXXXXXXXXXXX143  and  XXXXXXXXXXXXXXXXXXXXXX144 ) or ( XXXXXXXXXXXXXXXXXXXXXX145  and  XXXXXXXXXXXXXXXXXXXXXX146 ) or " +
                "( XXXXXXXXXXXXXXXXXXXXXX147  and  XXXXXXXXXXXXXXXXXXXXXX148 )) or ((not  XXXXXXXXXXXXXXXXXXXXXXXX142 ) and (( XXXXXXXXXXXXXXXXXXXXXX143  and  XXXXXXXXXXXXXXXXXXXXXX144 ) " +
                "or ( XXXXXXXXXXXXXXXXXXXXXX145  and  XXXXXXXXXXXXXXXXXXXXXX146 ) or ( XXXXXXXXXXXXXXXXXXXXXX147  and  XXXXXXXXXXXXXXXXXXXXXX148 ) or " +
                "( XXXXXXXXXXXXXXXXXXXXXX149  and  XXXXXXXXXXXXXXXXXXXXXX150 ) or ( XXXXXXXXXXXXXXXXXXXXXX151  and  XXXXXXXXXXXXXXXXXXXXXX152 ) or " +
                "( XXXXXXXXXXXXXXXXXXXXXX153  and  XXXXXXXXXXXXXXXXXXXXXX154 ) or ( XXXXXXXXXXXXXXXXXXXXXX155  and  XXXXXXXXXXXXXXXXXXXXXX156 ) or " +
                "( XXXXXXXXXXXXXXXXXXXXXX157  and  XXXXXXXXXXXXXXXXXXXXXX158 ) or ( XXXXXXXXXXXXXXXXXXXXXX159  and  XXXXXXXXXXXXXXXXXXXXXX160 ) or " +
                "( XXXXXXXXXXXXXXXXXXXXXX161  and  XXXXXXXXXXXXXXXXXXXXXX162 ) or ( XXXXXXXXXXXXXXXXXXXXXX163  and  XXXXXXXXXXXXXXXXXXXXXX164 ) or " +
                "( XXXXXXXXXXXXXXXXXXXXXX165  and  XXXXXXXXXXXXXXXXXXXXXX166 ) or ( XXXXXXXXXXXXXXXXXXXXXX167  and  XXXXXXXXXXXXXXXXXXXXXX168 ) or " +
                "( XXXXXXXXXXXXXXXXXXXXXX169  and  XXXXXXXXXXXXXXXXXXXXXX170 " +
                "))))";

        CTrans d = new CTrans(gfile, TranslationTarget.DNF);
        Ast out = d.translate(s);
        System.out.println();
    }
    
    public void testNegation3(){
        String s = "(not X14) and ( X35  or  X36 )";
        CTrans d = new CTrans(gfile, TranslationTarget.DNF);
        Ast out = d.translate(s);
        System.out.println();
    }

    @Test
    public void testNegation4(){
        String s = "( a1  and  a2 ) or ((not  a3 ) and ( a4  or  a5 ))";

        CTrans d = new CTrans(gfile, TranslationTarget.DNF);
        Ast out = d.translate(s);
        System.out.println();
    }




}
