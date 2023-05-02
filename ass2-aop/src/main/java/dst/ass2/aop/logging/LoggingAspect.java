package dst.ass2.aop.logging;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Aspect
public class LoggingAspect {

    @Around("execution(* dst.ass2.aop.IPluginExecutable.execute(..)) && !@annotation(Invisible)")
    public Object around(ProceedingJoinPoint point) {
        var advisedObject = point.getTarget();


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
        try {
            printAfterOutput(advisedObject);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return returnValue;
    }

    public void logOutput(Object advisedObject, String output) {
        var loggerFields =
                Arrays.stream(advisedObject.getClass().getDeclaredFields())
                        .filter(x -> Logger.class.isAssignableFrom(x.getType()))
                        .collect(Collectors.toList());

        if (loggerFields.isEmpty()) {
            System.out.print(output);
            return;
        }

        var loggerField = loggerFields.get(0);
        loggerField.setAccessible(true);
        Logger logger;
        try {
            logger = (java.util.logging.Logger) loggerField.get(advisedObject);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }


        logger.info(output);
        System.err.println(logger);
    }

    private void printBeforeOutput(Object advisedObject) throws IOException {
        String output = "Plugin " + advisedObject.getClass() + " started to execute\n";

        logOutput(advisedObject, output);
    }

    private void printAfterOutput(Object advisedObject) throws IOException {
        String output = "Plugin " + advisedObject.getClass() + " completed execution\n";

        logOutput(advisedObject, output);
    }
}
