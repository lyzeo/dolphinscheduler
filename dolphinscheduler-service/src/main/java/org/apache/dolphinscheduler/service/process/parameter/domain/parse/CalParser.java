package org.apache.dolphinscheduler.service.process.parameter.domain.parse;

import org.apache.dolphinscheduler.service.process.parameter.infrastructure.utils.ExpressionUtils;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.SimpleEvaluationContext;

import java.util.Date;

public class CalParser extends Parser{

    @Override
    public String parseExpression(String expression, Date executeTime) throws NoSuchMethodException {
        ExpressionParser parser = new SpelExpressionParser();
        EvaluationContext context = SimpleEvaluationContext.forReadOnlyDataBinding().build();
        context.setVariable("getFirstDayOfMonth", ExpressionUtils.class.getDeclaredMethod("getFirstDayOfMonth", String.class));
        context.setVariable("getLastDayOfMonth", ExpressionUtils.class.getDeclaredMethod("getLastDayOfMonth", String.class));
        context.setVariable("addMonths", ExpressionUtils.class.getDeclaredMethod("addMonths", String.class, int.class));
        context.setVariable("addDays", ExpressionUtils.class.getDeclaredMethod("addDays", String.class, int.class));
        context.setVariable("addYears", ExpressionUtils.class.getDeclaredMethod("addYears", String.class, int.class));
        context.setVariable("addWeeks", ExpressionUtils.class.getDeclaredMethod("addWeeks", String.class, int.class));
        context.setVariable("formatToInt", ExpressionUtils.class.getDeclaredMethod("formatToInt", String.class));
        return parser.parseExpression(expression).getValue(context, String.class);
    }
}
