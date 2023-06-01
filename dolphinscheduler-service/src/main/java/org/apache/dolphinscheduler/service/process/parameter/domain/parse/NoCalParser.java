package org.apache.dolphinscheduler.service.process.parameter.domain.parse;


import org.apache.dolphinscheduler.service.process.parameter.infrastructure.utils.ExpressionUtils;

import java.text.ParseException;
import java.util.Date;
import java.util.Objects;

public class NoCalParser extends Parser {

    @Override
    public String parseExpression(String expression, Date executeTime) throws NoSuchMethodException, ParseException {
        expression = nowParser(expression, executeTime);
        expression = getDayParser(expression, executeTime);
        expression = getMonthParser(expression, executeTime);
        expression = getYearParser(expression, executeTime);
        return getNextParser().parseExpression(expression, executeTime);
    }

    private String nowParser(String expression, Date executeTime) {
        if (expression.contains("#now()")) {
            String nowStr = ExpressionUtils.now();
            if (Objects.nonNull(executeTime)) {
                nowStr = ExpressionUtils.now(executeTime);
            }
            return expression.replace("#now()", "'" + nowStr + "'");
        }
        return expression;
    }

    private String getYearParser(String expression, Date executeTime) {
        if (expression.contains("#getYear()")) {
            String result = ExpressionUtils.getYear();
            if (Objects.nonNull(executeTime)) {
                result = ExpressionUtils.getYear(executeTime);
            }
            return expression.replace("#getYear()", "'" + result + "'");
        }
        return expression;
    }

    private String getMonthParser(String expression, Date executeTime) {
        if (expression.contains("#getMonth()")) {
            String result = ExpressionUtils.getMonth();
            if (Objects.nonNull(executeTime)) {
                result = ExpressionUtils.getMonth(executeTime);
            }

            return expression.replace("#getMonth()", "'" + result + "'");
        }
        return expression;
    }

    private String getDayParser(String expression, Date executeTime) {
        if (expression.contains("#getDay()")) {
            String result = ExpressionUtils.getYear();
            if (Objects.nonNull(executeTime)) {
                result = ExpressionUtils.getDay(executeTime);
            }
            return expression.replace("#getDay()", "'" + result + "'");
        }
        return expression;
    }
}
