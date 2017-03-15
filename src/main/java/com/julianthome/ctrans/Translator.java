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

import com.julianthome.ctrans.translator.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashSet;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;


public class Translator {

    final static Logger LOGGER = LoggerFactory.getLogger(Translator.class);

    static Set<TranslationHandler> first = new LinkedHashSet<>();
    static Set<TranslationHandler> second = new LinkedHashSet<>();

    private static Translator dnf;
    private static Translator cnf;


    public static Translator getInstance(TranslationTarget target) {
        if(target == TranslationTarget.DNF) {
            if(dnf == null)
                dnf = new Translator(TranslationTarget.DNF);

            return dnf;
        } else {
            assert target == TranslationTarget.CNF;

            if(cnf == null)
                cnf = new Translator(TranslationTarget.CNF);

            return cnf;
        }
    }



    private Translator(TranslationTarget target) {
        first.add(new XorTranslator());
        first.add(new ImplicationTranslator());
        second.add(new NegationTranslator());

        if(target == TranslationTarget.DNF) {
            second.add(new ConjunctionTranslator());
        } else {
            assert target == TranslationTarget.CNF;
            second.add(new DisjunctionTranslator());
        }
    }

    private boolean hasActiveHandler(Set<TranslationHandler> s,
                                                ExpressionGraph eg, Expression e) {
        return s.stream().filter(t -> t.isActive(eg, e))
                .count() > 0;
    }

    private Pair<Expression, TranslationHandler> getActiveHandler
            (Set<TranslationHandler> s,
                                               ExpressionGraph eg, Expression e) {

        //LOGGER.debug("get active handler");
        Set<TranslationHandler> ret = s.stream().filter(t -> t.isActive(eg, e))
                .collect(Collectors
                        .toSet());

        if (ret.size() >= 1) {
            return new Pair(e, ret.iterator().next());
        } else {
            return null;
        }
    }

    private Pair<Expression, TranslationHandler> getNext
            (ExpressionGraph eg, Set<TranslationHandler> s) {

        try {
            return eg.vertexSet().stream().filter(
                    e -> hasActiveHandler(s, eg, e)
            ).map(e -> getActiveHandler(s, eg, e)).findFirst().get();
        } catch(NoSuchElementException e) {
            return null;
        }

    }


    public void translate(ExpressionGraph eg) {
        loop(eg, first);
        // CN contains only disjunction, conjunction or negation
        loop(eg, second);
    }



    private void loop(ExpressionGraph eg, Set<TranslationHandler> s) {

        LOGGER.debug("Translate");

        Pair<Expression, TranslationHandler> nxt = null;

        while((nxt = getNext(eg, s)) != null) {
            nxt.getSecond().translate(eg,nxt.getFirst());
        }

    }


}
