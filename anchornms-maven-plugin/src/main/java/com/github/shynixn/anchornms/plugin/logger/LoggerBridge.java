package com.github.shynixn.anchornms.plugin.logger;

import org.apache.maven.plugin.logging.Log;
import org.slf4j.Logger;
import org.slf4j.Marker;

/**
 * Created by Shynixn 2018.
 * <p>
 * Version 1.2
 * <p>
 * MIT License
 * <p>
 * Copyright (c) 2018 by Shynixn
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
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
 */
public class LoggerBridge implements Logger {

    private final Log log;

    public LoggerBridge(Log log) {
        this.log = log;
    }

    @Override
    public String getName() {
        return this.log.toString();
    }

    @Override
    public boolean isTraceEnabled() {
        return false;
    }

    @Override
    public void trace(String s) {
        this.log.info(s);
    }

    @Override
    public void trace(String s, Object o) {
        this.log.info(s);
    }

    @Override
    public void trace(String s, Object o, Object o1) {
        this.log.info(s);
    }

    @Override
    public void trace(String s, Object... objects) {
        this.log.info(s);
    }

    @Override
    public void trace(String s, Throwable throwable) {
        this.log.info(s);
    }

    @Override
    public boolean isTraceEnabled(Marker marker) {
        return false;
    }

    @Override
    public void trace(Marker marker, String s) {
        this.log.info(s);
    }

    @Override
    public void trace(Marker marker, String s, Object o) {
        this.log.info(s);
    }

    @Override
    public void trace(Marker marker, String s, Object o, Object o1) {
        this.log.info(s);
    }

    @Override
    public void trace(Marker marker, String s, Object... objects) {
        this.log.info(s);
    }

    @Override
    public void trace(Marker marker, String s, Throwable throwable) {
        this.log.info(s);
    }

    @Override
    public boolean isDebugEnabled() {
        return true;
    }

    @Override
    public void debug(String s) {
        this.log.debug(s);
    }

    @Override
    public void debug(String s, Object o) {
        this.log.debug(s);
    }

    @Override
    public void debug(String s, Object o, Object o1) {
        this.log.debug(s);
    }

    @Override
    public void debug(String s, Object... objects) {
        this.log.debug(s);
    }

    @Override
    public void debug(String s, Throwable throwable) {
        this.log.debug(s, throwable);
    }

    @Override
    public boolean isDebugEnabled(Marker marker) {
        return true;
    }

    @Override
    public void debug(Marker marker, String s) {
        this.log.debug(s);
    }

    @Override
    public void debug(Marker marker, String s, Object o) {
        this.log.debug(s);
    }

    @Override
    public void debug(Marker marker, String s, Object o, Object o1) {
        this.log.debug(s);
    }

    @Override
    public void debug(Marker marker, String s, Object... objects) {
        this.log.debug(s);
    }

    @Override
    public void debug(Marker marker, String s, Throwable throwable) {
        this.log.debug(s, throwable);
    }

    @Override
    public boolean isInfoEnabled() {
        return true;
    }

    @Override
    public void info(String s) {
        this.log.info(s);
    }

    @Override
    public void info(String s, Object o) {
        this.log.info(s);
    }

    @Override
    public void info(String s, Object o, Object o1) {
        this.log.info(s);
    }

    @Override
    public void info(String s, Object... objects) {
        this.log.info(s);
    }

    @Override
    public void info(String s, Throwable throwable) {
        this.log.info(s, throwable);
    }

    @Override
    public boolean isInfoEnabled(Marker marker) {
        return true;
    }

    @Override
    public void info(Marker marker, String s) {
        this.log.info(s);
    }

    @Override
    public void info(Marker marker, String s, Object o) {
        this.log.info(s);
    }

    @Override
    public void info(Marker marker, String s, Object o, Object o1) {
        this.log.info(s);
    }

    @Override
    public void info(Marker marker, String s, Object... objects) {
        this.log.info(s);
    }

    @Override
    public void info(Marker marker, String s, Throwable throwable) {
        this.log.info(s, throwable);
    }

    @Override
    public boolean isWarnEnabled() {
        return true;
    }

    @Override
    public void warn(String s) {
        this.log.warn(s);
    }

    @Override
    public void warn(String s, Object o) {
        this.log.warn(s);
    }

    @Override
    public void warn(String s, Object... objects) {
        this.log.warn(s);
    }

    @Override
    public void warn(String s, Object o, Object o1) {
        this.log.warn(s);
    }

    @Override
    public void warn(String s, Throwable throwable) {
        this.log.warn(s,throwable);
    }

    @Override
    public boolean isWarnEnabled(Marker marker) {
        return true;
    }

    @Override
    public void warn(Marker marker, String s) {
        this.log.warn(s);
    }

    @Override
    public void warn(Marker marker, String s, Object o) {
        this.log.warn(s);
    }

    @Override
    public void warn(Marker marker, String s, Object o, Object o1) {
        this.log.warn(s);
    }

    @Override
    public void warn(Marker marker, String s, Object... objects) {
        this.log.warn(s);
    }

    @Override
    public void warn(Marker marker, String s, Throwable throwable) {
        this.log.warn(s,throwable);
    }

    @Override
    public boolean isErrorEnabled() {
        return true;
    }

    @Override
    public void error(String s) {
        this.log.error(s);
    }

    @Override
    public void error(String s, Object o) {
        this.log.error(s);
    }

    @Override
    public void error(String s, Object o, Object o1) {
        this.log.error(s);
    }

    @Override
    public void error(String s, Object... objects) {
        this.log.error(s);
    }

    @Override
    public void error(String s, Throwable throwable) {
        this.log.error(s, throwable);
    }

    @Override
    public boolean isErrorEnabled(Marker marker) {
        return true;
    }

    @Override
    public void error(Marker marker, String s) {
        this.log.error(s);
    }

    @Override
    public void error(Marker marker, String s, Object o) {
        this.log.error(s);
    }

    @Override
    public void error(Marker marker, String s, Object o, Object o1) {
        this.log.error(s);
    }

    @Override
    public void error(Marker marker, String s, Object... objects) {
        this.log.error(s);
    }

    @Override
    public void error(Marker marker, String s, Throwable throwable) {
        this.log.error(s, throwable);
    }
}
