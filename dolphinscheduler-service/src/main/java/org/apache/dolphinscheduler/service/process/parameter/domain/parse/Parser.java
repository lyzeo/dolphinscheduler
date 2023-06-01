package org.apache.dolphinscheduler.service.process.parameter.domain.parse;

import java.text.ParseException;
import java.util.Date;

public abstract class Parser {
    protected Parser nextParser = null;

    public Parser getNextParser() {
        return nextParser;
    }

    public void setNextParser(Parser nextParser) {
        this.nextParser = nextParser;
    }

    public abstract String parseExpression(String expression, Date executeTime) throws NoSuchMethodException, ParseException;
}
