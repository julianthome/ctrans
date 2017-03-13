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

package com.julianthome.ctrans;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snt.inmemantlr.GenericParser;
import org.snt.inmemantlr.exceptions.AstProcessorException;
import org.snt.inmemantlr.exceptions.CompilationException;
import org.snt.inmemantlr.exceptions.IllegalWorkflowException;
import org.snt.inmemantlr.listener.DefaultTreeListener;
import org.snt.inmemantlr.tree.Ast;

import java.io.File;
import java.io.FileNotFoundException;


public class CTrans {

    final static Logger LOGGER = LoggerFactory.getLogger(CTrans.class);

    private GenericParser gp = null;
    DefaultTreeListener dlist = null;


    public CTrans(String grammar) {

        LOGGER.debug("load grammar file {}"+ grammar);

        File f = new File(grammar);
        try {
            gp = new GenericParser(f);
        } catch (FileNotFoundException e) {
            LOGGER.debug(e.getMessage());System.out.println(e.getMessage());
            System.exit(-1);
        }

        dlist = new DefaultTreeListener();

        gp.setListener(dlist);
        try {
            gp.compile();
        } catch (CompilationException e) {
        	LOGGER.debug(e.getMessage());System.out.println(e.getMessage());;
            System.exit(-1);
        }


    }


    public Ast translate(String formula) {
        ExpressionGraph eg = null;
        LogicListener l = null;


        try {
            gp.parse(formula);
            l = new LogicListener(dlist.getAst());
        } catch (IllegalWorkflowException e) {
        	System.err.println("DNF transformer- intial parsing error");
        	return null;
        }

        System.out.println("AST inital");
        System.out.println(dlist.getAst().toDot());

        try {
            l.process();
            eg = l.getResult();
        } catch (AstProcessorException e) {
        	System.err.println("DNF transformer- network building error");
        	return dlist.getAst();
        }

   

        Translator.INSTANCE.translate(eg);
        
        System.out.println("Constraint network");
        System.out.println(eg.toDot());

        String s = eg.serialize();

        Ast dnfast = null;
        try {
            gp.parse(s);
            dnfast = dlist.getAst();
        } catch (IllegalWorkflowException e) {
        	System.err.println("DNF transformer- transformation error");
        	return dlist.getAst();

        }
        
        System.out.println("AST DNF");
        System.out.println(dnfast.toDot());
        return dnfast;
    }

}
