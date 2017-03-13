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

public class Expression {

    private int id = 0;

    public static int i = 0;

    public enum Kind {
        ATOM(""),
        AND("and"),
        OR("or"),
        XOR("xor"),
        IMPLIES("implies"),
        NEGATION("not");

        private String lbl = "";
        Kind(String lbl) {
            this.lbl = lbl;
        }
    }


    private boolean negated = false;
    private Kind kind = Kind.ATOM;

    private String lbl = "";

    public Expression(Kind kind) {
        this.kind = kind;
        this.id = i++;
        this.lbl = kind.lbl;
    }


    public Expression(String lbl) {
        this.kind = Kind.ATOM;
        this.id = i++;
        this.lbl = lbl;
    }


    public int getId() {
        return this.id;
    }

    @Override
    public String toString() {
        if(this.getKind() == Kind.ATOM)
            return this.lbl;
        return this.kind.lbl;
    }

    public Kind getKind() {
        return this.kind;
    }

    public void setKind(Kind kind) {
        this.kind = kind;
    }

    @Override
    public int hashCode() {
        return this.id;

    }
}
