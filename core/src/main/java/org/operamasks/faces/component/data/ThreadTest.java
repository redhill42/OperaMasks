/*
 * $Id: ThreadTest.java,v 1.2 2008/04/20 11:40:56 jacky Exp $
 *
 * Copyright (C) 2006 Operamasks Community.
 * Copyright (C) 2000-2006 Apusic Systems, Inc.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see http://www.gnu.org/licenses.
 */
package org.operamasks.faces.component.data;

import java.util.ArrayList;

public class ThreadTest {
    public static void main(String[] args) throws Throwable {
        ArrayList<String> list = new ArrayList<String>();
        for (int i = 0; i < 500; i++ ) {
        list.add(i+"");
        }
        list.add(99, "aaa");
        list.trimToSize();
        String[] t = list.toArray(new String[list.size()]);
            for (String s : t) {
                System.out.println("item: \t" + s);
            }
        //new ThreadTest().exec();
    }
    private void exec() throws Throwable {
        final MyBiz t1 = new MyBiz(-1);
        new Thread() {
            @Override
            public void run() {
                t1.doSomeThing();
            }
        }.start();
        new Thread() {
            @Override
            public void run() {
                t1.doSomeThing();
            }
        }.start();
        Thread.sleep(2000);
        t1.setI(1);
        t1.doSomeThing();
    }
    
    static class MyBiz {
        private int i;

        public MyBiz(int i) {
            this.i = i;
        }

        public void doSomeThing() {
            if (i < 0) {
                synchronized (this) {
                    try {
                        wait();
                        System.out.println(System.currentTimeMillis());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            synchronized (this) {
                notifyAll();
            }
        }

        public void setI(int i) {
            this.i = i;
        }
    }
}
