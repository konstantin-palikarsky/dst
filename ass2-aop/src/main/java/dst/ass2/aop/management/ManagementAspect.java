package dst.ass2.aop.management;

import dst.ass2.aop.IPluginExecutable;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;

import java.util.Timer;
import java.util.TimerTask;

@Aspect
public class ManagementAspect {

    @Around("execution(* dst.ass2.aop.IPluginExecutable.execute(..))")
    public Object around(ProceedingJoinPoint point) {
        IPluginExecutable advisedPlugin = (IPluginExecutable) point.getTarget();


        var myAnnotation = ((MethodSignature) point.getSignature()).getMethod()
                .getAnnotation(Timeout.class);

        if (myAnnotation != null) {

            var timeout = myAnnotation.value();
            TimerTask task = new TimerTask() {
                public void run() {
                    advisedPlugin.interrupted();
                }
            };
            Timer timer = new Timer("Timer");

            timer.schedule(task, timeout);
        }

        Object result;
        try {
            result = point.proceed();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }

        return result;
    }

}
