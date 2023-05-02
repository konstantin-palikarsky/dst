package dst.ass2.aop.logging;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import java.io.IOException;
import java.util.Arrays;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Aspect
public class LoggingAspect {

    // TODO
    @Around("execution(* dst.ass2.aop.IPluginExecutable.execute(..)) && !@annotation(Invisible)")
    public Object around(ProceedingJoinPoint point) {
        var advisedObject = point.getThis();



        try {
            printBeforeOutput(advisedObject);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        Object returnValue;
        try {
            returnValue = point.proceed();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }

        return returnValue;
    }

    Function<Void, Void> outputLog(Object advisedObject, String output) {
        var loggerFields =
                Arrays.stream(advisedObject.getClass().getDeclaredFields())
                        .filter(x -> java.util.logging.Logger.class.isAssignableFrom(x.getClass()))
                        .collect(Collectors.toList());

        if (loggerFields.isEmpty()) {
            return (VOID) -> {
                System.out.print(output);
                return VOID;
            };
        }
        var loggerField = loggerFields.get(0);
        loggerField.setAccessible(true);
        Logger logger;
        try {
            logger = (Logger) loggerField.get(advisedObject);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }


        return (VOID) -> {
            logger.info(output);
            return VOID;
        };
    }

    private void printBeforeOutput(Object advisedObject) throws IOException {
        String output = "Plugin " + advisedObject.getClass() + " started to execute\n";
        outputLog(advisedObject, output);


    }
}
